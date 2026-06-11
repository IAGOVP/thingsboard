#!/usr/bin/env python3
"""
Add class- and public/protected method-level Javadoc to rest-client/ Java sources.
Documents HTTP endpoints (GET/POST/DELETE paths) invoked via Spring RestTemplate.
Includes @param, @return, and @throws tags. Skips package-info.java.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from add_dao_javadoc import (  # noqa: E402
    DECL_LINE_RE,
    class_body_bounds_at,
    dao_insert_text_at,
    detect_kind,
    find_class_methods,
    fix_unindented_method_javadocs,
    format_javadoc,
    format_return,
    humanize,
    insert_text_at,
    javadoc_block_before,
    javadoc_is_complete,
    method_description,
    method_javadoc_needs_fixup,
    method_line_indent,
    param_description,
    split_license_and_body,
    throws_description,
)
from add_service_javadoc import find_insert_position  # noqa: E402

REST_CLIENT_ROOTS = [
    Path("rest-client/src/main/java"),
]

REST_CLIENT_CLASS_DOCS: dict[str, list[str]] = {
    "RestClient": [
        "Java client for the ThingsBoard REST API using Spring {@link org.springframework.web.client.RestTemplate}.",
        "",
        "<p>Each public method maps to one or more HTTP endpoints on tb-node "
        "(see {@code baseURL + \"/api/...\"}). JWT is sent in the "
        "{@value org.thingsboard.rest.client.RestClient#TOKEN_HEADER_PARAM} header; "
        "the request interceptor refreshes tokens automatically.",
        "",
        "<p>Used by tests, monitoring, integrations, and tools. Server-side controllers live in the "
        "{@code application} module; full API catalog: {@code docs/REST_API.md}.",
    ],
    "AuthType": [
        "Authentication mode for the RestTemplate request interceptor.",
        "",
        "<p>{@link #JWT} — {@code X-Authorization: Bearer &lt;token&gt;} with automatic refresh via "
        "{@link RestClient#refreshToken()}. {@link #API_KEY} — static {@code X-Authorization: ApiKey &lt;token&gt;}.",
    ],
    "RestJsonConverter": [
        "Utility that converts REST JSON telemetry and attribute payloads into "
        "{@link org.thingsboard.server.common.data.kv.KvEntry} domain objects.",
        "",
        "<p>Used by {@link org.thingsboard.rest.client.RestClient} when parsing device telemetry "
        "and attribute responses from tb-node.",
    ],
}

REST_CLIENT_METHOD_DOCS: dict[str, str] = {
    "login": "Authenticates with username and password via POST /api/auth/login.",
    "refreshToken": "Refreshes the JWT using POST /api/auth/token.",
    "getToken": "Returns the current main JWT sent in the X-Authorization header.",
    "close": "Shuts down the internal executor service used for async REST calls.",
    "withApiKey": "Factory that creates a client using static ApiKey authentication.",
    "toAttributes": "Maps a REST attribute JSON array to a list of AttributeKvEntry instances.",
    "toTimeseries": "Maps a REST timeseries JSON map (key → value nodes) to TsKvEntry instances.",
    "parseValue": "Parses a JSON value node into the appropriate KvEntry data type.",
    "parseNumericValue": "Parses a numeric JSON node as long or double KvEntry.",
}

REST_CLIENT_PARAM_HINTS: dict[str, str] = {
    "baseURL": "ThingsBoard tb-node base URL (e.g. http://localhost:8080)",
    "restTemplate": "Spring RestTemplate used for authenticated API calls",
    "username": "ThingsBoard user login name",
    "password": "ThingsBoard user password",
    "token": "pre-issued JWT or ApiKey token",
    "accessToken": "pre-issued JWT access token",
    "authType": "JWT (auto refresh) or API_KEY (static ApiKey header)",
    "tenantId": "tenant UUID",
    "deviceId": "device UUID",
    "customerId": "customer UUID",
    "userId": "user UUID",
    "alarmId": "alarm UUID",
    "entityId": "entity id (type + UUID)",
    "entityType": "entity type enum value",
    "key": "settings or attribute key name",
    "pageSize": "maximum number of entities per page",
    "page": "zero-based page index",
    "textSearch": "optional text filter for entity search",
    "sortProperty": "entity property used for sorting",
    "sortOrder": "ASC or DESC sort direction",
    "attributes": "REST JSON attribute nodes from tb-node response",
    "timeseries": "REST JSON timeseries map from tb-node response",
    "value": "JSON value node to parse",
    "adminSettings": "admin settings payload",
    "securitySettings": "security settings payload",
    "jwtSettings": "JWT settings payload",
    "repositorySettings": "version-control repository settings payload",
    "testSmsRequest": "SMS test request payload",
}

URL_SUFFIX_RE = re.compile(r'baseURL \+ "(/api/[^"]+)"')
HTTP_PATTERNS: list[tuple[re.Pattern[str], str]] = [
    (re.compile(r"\.getForEntity\s*\(\s*baseURL \+"), "GET"),
    (re.compile(r"\.postForEntity\s*\(\s*baseURL \+"), "POST"),
    (re.compile(r"\.postForLocation\s*\(\s*baseURL \+"), "POST"),
    (re.compile(r"\.delete\s*\(\s*baseURL \+"), "DELETE"),
    (re.compile(r"\.put\s*\(\s*baseURL \+"), "PUT"),
    (re.compile(r"\.exchange\s*\(\s*baseURL \+"), "EXCHANGE"),
]


def extract_http_endpoints(body_slice: str) -> list[tuple[str, str]]:
    """Return (HTTP verb, path) pairs found in a method body slice."""
    paths = URL_SUFFIX_RE.findall(body_slice)
    if not paths:
        return []
    verb = "HTTP"
    for pat, v in HTTP_PATTERNS:
        if pat.search(body_slice):
            verb = v
            break
    exchange = re.search(
        r"\.exchange\s*\(\s*baseURL \+ \"[^\"]+\"[^)]*HttpMethod\.(\w+)",
        body_slice,
        re.DOTALL,
    )
    if exchange:
        verb = exchange.group(1).upper()
    return [(verb, p) for p in dict.fromkeys(paths)]


def endpoint_lines(body_slice: str) -> list[str]:
    endpoints = extract_http_endpoints(body_slice)
    if not endpoints:
        return []
    lines = ["", "<p>ThingsBoard REST endpoint(s):"]
    for verb, path in endpoints:
        lines.append(f"<br>{{@code {verb} {path}}}")
    lines.append("")
    return lines


def rest_method_summary(name: str, body_slice: str) -> str:
    if name in REST_CLIENT_METHOD_DOCS:
        return REST_CLIENT_METHOD_DOCS[name]
    endpoints = extract_http_endpoints(body_slice)
    ep_suffix = ""
    if endpoints:
        verb, path = endpoints[0]
        ep_suffix = f" Invokes {{@code {verb} {path}}}."
    if name == "login":
        return f"Authenticates with username/password via POST /api/auth/login.{ep_suffix}"
    if name == "refreshToken":
        return f"Refreshes JWT using POST /api/auth/token.{ep_suffix}"
    if name == "getToken":
        return "Returns the current main JWT used in X-Authorization header."
    if name == "close":
        return "Shuts down the internal executor service."
    if name.startswith("get") and "ById" in name:
        return f"Fetches entity by id.{ep_suffix}"
    if name.startswith("get"):
        return f"Fetches {humanize(name[3:])}.{ep_suffix}"
    if name.startswith("save") or name.startswith("create"):
        entity = humanize(name.replace("save", "").replace("create", ""))
        return f"Creates or updates {entity}.{ep_suffix}"
    if name.startswith("delete"):
        return f"Deletes {humanize(name[6:])}.{ep_suffix}"
    if name.startswith("find"):
        return f"Queries or searches entities: {humanize(name[4:])}.{ep_suffix}"
    if name.startswith("assign"):
        return f"Assigns {humanize(name[6:])}.{ep_suffix}"
    if name.startswith("unassign"):
        return f"Unassigns {humanize(name[8:])}.{ep_suffix}"
    if name.startswith("ack") or name.endswith("Ack"):
        return f"Acknowledges alarm.{ep_suffix}"
    if name.startswith("clear"):
        return f"Clears alarm.{ep_suffix}"
    return f"REST API call: {humanize(name)}.{ep_suffix}"


def rest_param_description(name: str, type_hint: str) -> str:
    if name in REST_CLIENT_PARAM_HINTS:
        return REST_CLIENT_PARAM_HINTS[name]
    return param_description(name, type_hint)


NO_THROW_METHODS = frozenset({"getToken", "close", "withApiKey"})


def method_body_slice(body: str, method) -> str:
    sig = method.sig_start
    search_from = body.find("(", sig)
    if search_from < 0:
        return body[sig : sig + 400]
    brace = body.find("{", search_from)
    if brace < 0:
        return body[sig : search_from + 200]
    depth = 0
    for j in range(brace, len(body)):
        ch = body[j]
        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                return body[sig : j + 1]
    return body[sig : min(sig + 800, len(body))]


def class_description(name: str, kind: str, body: str, path: Path) -> list[str]:
    if name in REST_CLIENT_CLASS_DOCS:
        return REST_CLIENT_CLASS_DOCS[name]
    if kind == "interface":
        return [f"REST client {humanize(name)} contract."]
    return [f"{humanize(name).capitalize()} (ThingsBoard REST client)."]


def class_javadoc_is_incomplete(doc: str, class_name: str) -> bool:
    if class_name in REST_CLIENT_CLASS_DOCS and len(doc) < 120:
        return True
    if "ThingsBoard REST API" in doc and "@param" not in doc and len(doc) < 200:
        return False
    if len(doc.strip()) < 80:
        return True
    return False


def add_class_javadoc_at(
    body: str, decl_match: re.Match, class_name: str, kind: str, path: Path, force: bool
) -> tuple[str, bool]:
    insert_pos = find_insert_position(body, decl_match.start())
    new_doc = format_javadoc(decl_match.group("indent"), class_description(class_name, kind, body, path))
    existing = javadoc_block_before(body, insert_pos)
    if existing:
        start, end = existing
        if not force and not class_javadoc_is_incomplete(body[start:end], class_name):
            return body, False
        body = body[:start] + body[end:]
        insert_pos = start
    return insert_text_at(body, insert_pos, new_doc), True


def add_all_class_javadocs(body: str, path: Path, force: bool) -> tuple[str, int]:
    count = 0
    for m in reversed(list(DECL_LINE_RE.finditer(body))):
        name = m.group("name")
        kind = detect_kind(m.group(0))
        body, changed = add_class_javadoc_at(body, m, name, kind, path, force)
        if changed:
            count += 1
    return body, count


def strip_all_javadocs_before(body: str, pos: int) -> tuple[str, int]:
    while True:
        existing = javadoc_block_before(body, pos)
        if not existing:
            break
        start, end = existing
        body = body[:start] + body[end:]
        pos = start
    return body, pos


def rest_javadoc_is_complete(doc: str, method) -> bool:
    is_void = method.return_type.strip() == "void"
    if not is_void and "@return" not in doc:
        return False
    for p in method.params:
        if f"@param {p.name}" not in doc:
            return False
    if method.name in NO_THROW_METHODS and "@throws" in doc:
        return False
    if len(doc.strip()) < 90:
        return False
    return True


def method_javadoc_is_thin(doc: str, method) -> bool:
    is_void = method.return_type.strip() == "void"
    if not is_void and "@return" not in doc:
        return True
    if method.name in NO_THROW_METHODS and "@throws" in doc:
        return True
    if "empty if not found when found" in doc:
        return True
    if method.name in ("toAttributes", "toTimeseries") and "RestClientException" in doc:
        return True
    if "Calls `" in doc or "Calls `/api" in doc:
        return True
    if "@param" in doc and "@return" in doc and len(doc) > 100:
        return False
    first = ""
    for line in doc.split("\n"):
        t = line.strip().lstrip("* ").strip()
        if t and t not in ("/**", "*/"):
            first = t
            break
    if first.startswith("REST call:") or first.startswith("GET ") or first.startswith("POST "):
        return True
    if len(doc.strip()) < 80:
        return True
    return False


def rest_method_javadoc(body: str, method) -> str:
    raw = method_line_indent(body, method.sig_start) or method.indent or "    "
    indent = "    " if len(raw.replace("\t", "    ")) > 4 else raw
    slice_ = method_body_slice(body, method)
    lines = [rest_method_summary(method.name, slice_)]
    lines.extend(endpoint_lines(slice_))
    for p in method.params:
        lines.append(f"@param {p.name} {rest_param_description(p.name, p.type_hint)}")
    ret = format_return(method.return_type)
    if method.return_type.strip() == "void":
        pass
    elif method.return_type.strip().startswith("Optional<"):
        inner = method.return_type.strip()[9:-1]
        ret = format_return(inner)
        lines.append(f"@return {{@code Optional}} containing {ret}, or empty when tb-node returns 404")
    else:
        lines.append(f"@return {ret}")
    throws = list(method.throws)
    if (
        method.name not in NO_THROW_METHODS
        and "catch (HttpClientErrorException" in slice_
        and "HttpClientErrorException" not in throws
    ):
        throws.append("HttpClientErrorException")
    if throws and method.name not in NO_THROW_METHODS:
        for t in throws:
            if t == "HttpClientErrorException":
                lines.append(
                    f"@throws {t} when tb-node returns a non-2xx HTTP status "
                    "(404 may yield empty Optional)"
                )
            else:
                lines.append(f"@throws {t} {throws_description(t)}")
    elif method.name not in NO_THROW_METHODS and method.name not in (
        "parseValue",
        "parseNumericValue",
        "toAttributes",
        "toTimeseries",
    ):
        if extract_http_endpoints(slice_):
            lines.append("@throws org.springframework.web.client.RestClientException if the HTTP request fails")
    return format_javadoc(indent, lines)


def dedupe_methods(methods) -> list:
    seen: set[int] = set()
    unique = []
    for method in methods:
        if method.sig_start in seen:
            continue
        seen.add(method.sig_start)
        unique.append(method)
    return unique


def add_rest_method_javadocs(body: str, decl_match, class_name: str, kind: str) -> tuple[str, int]:
    class_start, _, _ = class_body_bounds_at(body, decl_match)
    methods = dedupe_methods(find_class_methods(body, class_name, kind, decl_match))
    to_update = []
    for method in methods:
        if method.name in ("main",):
            continue
        if method.doc_start < class_start:
            continue
        existing = javadoc_block_before(body, method.doc_start)
        if existing:
            start, end = existing
            doc = body[start:end]
            if (
                rest_javadoc_is_complete(doc, method)
                and not method_javadoc_needs_fixup(body, method.doc_start, method.sig_start)
                and not method_javadoc_is_thin(doc, method)
            ):
                continue
        to_update.append(method)
    count = 0
    for method in reversed(to_update):
        pos = max(method.doc_start, class_start + 1)
        body, pos = strip_all_javadocs_before(body, pos)
        new_doc = rest_method_javadoc(body, method)
        body = dao_insert_text_at(body, pos, new_doc)
        count += 1
    return body, count


def add_private_method_javadocs(body: str, class_name: str) -> tuple[str, int]:
    """Add Javadoc to private static helper methods in small utility classes."""
    if class_name != "RestJsonConverter":
        return body, 0
    private_docs = {
        "parseValue": (
            "Parses a JSON value node into the appropriate KvEntry data type (boolean, number, string, or JSON object).",
            [("key", "attribute or telemetry key name"), ("value", "JSON value node to parse")],
            "KvEntry",
            ["RuntimeException if the JSON value type cannot be parsed"],
        ),
        "parseNumericValue": (
            "Parses a numeric JSON node as long or double KvEntry.",
            [("key", "telemetry key name"), ("value", "numeric JSON node")],
            "KvEntry",
            ["IllegalArgumentException if the value is a big integer not representable as long"],
        ),
    }
    count = 0
    for name, (desc, params, ret, throws_list) in private_docs.items():
        pat = re.compile(rf"^(\s+)private static \w[\w<>,\s\[\]?]*\s+{name}\s*\(", re.MULTILINE)
        m = pat.search(body)
        if not m:
            continue
        pos = m.start()
        existing = javadoc_block_before(body, pos)
        if existing:
            start, end = existing
            doc = body[start:end]
            if "@param" in doc and "@return" in doc and len(doc) > 100:
                continue
            body = body[:start] + body[end:]
            pos = start
        indent = m.group(1)
        lines = [desc, ""]
        for pname, pdesc in params:
            lines.append(f"@param {pname} {pdesc}")
        lines.append(f"@return {format_return(ret)} instance")
        for t in throws_list:
            tname = t.split()[0]
            msg = t if " " not in t else t[t.index(" ") + 1 :]
            lines.append(f"@throws {tname} {msg}")
        body = dao_insert_text_at(body, pos, format_javadoc(indent, lines))
        count += 1
    return body, count


def process_file(path: Path, dry_run: bool, force_class: bool) -> bool:
    if path.name == "package-info.java":
        return False
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    if not DECL_LINE_RE.search(body):
        return False
    changed = False
    body, cc = add_all_class_javadocs(body, path, force_class)
    if cc:
        changed = True
    for decl_match in DECL_LINE_RE.finditer(body):
        class_name = decl_match.group("name")
        kind = detect_kind(decl_match.group(0))
        body, mc = add_rest_method_javadocs(body, decl_match, class_name, kind)
        if mc:
            changed = True
        body, pc = add_private_method_javadocs(body, class_name)
        if pc:
            changed = True
    body, fc = fix_unindented_method_javadocs(body)
    if fc:
        changed = True
    if changed and not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def collect_files(roots: list[Path]) -> list[Path]:
    files: list[Path] = []
    seen: set[str] = set()
    for root in roots:
        if not root.exists():
            continue
        for path in sorted(root.rglob("*.java")):
            if path.name == "package-info.java":
                continue
            key = str(path.resolve())
            if key not in seen:
                seen.add(key)
                files.append(path)
    return files


def main() -> int:
    parser = argparse.ArgumentParser(description="Add detailed Javadoc to rest-client sources")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--force-class", action="store_true", help="Replace thin class Javadoc")
    args = parser.parse_args()
    modified = 0
    for path in collect_files(REST_CLIENT_ROOTS):
        if process_file(path, args.dry_run, args.force_class):
            print(f"updated: {path}", file=sys.stderr)
            modified += 1
    print(f"Done: {modified} files modified", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
