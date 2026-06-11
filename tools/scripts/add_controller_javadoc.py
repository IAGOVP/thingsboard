#!/usr/bin/env python3
"""
Add class- and method-level Javadoc to ThingsBoard REST controllers.
Style matches TrendzController: HTTP path, Auth, @param, @return, @throws.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

CLASS_REQUEST_MAPPING = re.compile(
    r"@RequestMapping\s*\(\s*(?:value\s*=\s*)?[\"']([^\"']*)[\"']"
)
METHOD_MAPPING = re.compile(
    r"@(Get|Post|Put|Delete|Patch)Mapping\s*\(\s*"
    r"(?:value\s*=\s*)?[\"']([^\"']*)[\"']"
)
REQUEST_MAPPING = re.compile(
    r"@RequestMapping\s*\(\s*"
    r"(?:value\s*=\s*)?[\"']([^\"']*)[\"']"
    r"[^)]*method\s*=\s*RequestMethod\.(\w+)",
    re.DOTALL,
)
REQUEST_MAPPING_ROOT = re.compile(
    r"@RequestMapping\s*\(\s*method\s*=\s*RequestMethod\.(\w+)\s*\)",
)
REQUEST_MAPPING_VALUE_METHOD = re.compile(
    r"@RequestMapping\s*\(\s*(?:value\s*=\s*)?[\"']([^\"']*)[\"']\s*,\s*method\s*=\s*RequestMethod\.(\w+)",
)
PREAUTHORIZE = re.compile(r"@PreAuthorize\s*\(\s*\"([^\"]+)\"\s*\)")
API_OPERATION = re.compile(
    r'@ApiOperation\s*\(\s*value\s*=\s*"([^"]+)"',
    re.DOTALL,
)
CLASS_DECL = re.compile(
    r"^(?P<indent>\s*)public\s+class\s+(?P<name>\w+)",
    re.MULTILINE,
)
MAPPING_ANCHOR = re.compile(
    r"@(Get|Post|Put|Delete|Patch)Mapping|@RequestMapping\s*\("
)
THROWS_CLAUSE = re.compile(r"throws\s+([\w.\s,]+?)\s*\{")

BASE_PATH_BY_CLASS: dict[str, str] = {
    "RpcV1Controller": "/api/plugins/rpc",
    "RpcV2Controller": "/api/rpc",
    "RuleEngineController": "/api/rule-engine/",
    "TelemetryController": "/api/plugins/telemetry",
}

CLASS_DESCRIPTIONS: dict[str, tuple[str, str, str]] = {
    "NotificationRuleController": (
        "notification rules",
        "/api/notification",
        "Defines when and how platform events trigger notifications.",
    ),
    "NotificationTargetController": (
        "notification targets",
        "/api/notification",
        "Recipients and delivery channels (platform users, Slack, etc.) for notification rules.",
    ),
    "NotificationTemplateController": (
        "notification templates",
        "/api/notification",
        "Message templates per delivery method and notification type.",
    ),
    "OAuth2ConfigTemplateController": (
        "OAuth2 client registration templates",
        "/api/oauth2/config/template",
        "System-level OAuth2 provider templates used when registering OAuth2 clients.",
    ),
    "OAuth2Controller": (
        "OAuth2 clients and login",
        "/api",
        "OAuth2 client CRUD, login discovery, and login-processing URL configuration.",
    ),
    "OtaPackageController": (
        "OTA (over-the-air) firmware and software packages",
        "/api",
        "Package metadata, binary upload/download, and tenant-scoped package listings.",
    ),
    "QrCodeSettingsController": (
        "mobile QR code and deep-link settings",
        "mixed (see method paths)",
        "Mobile app association files, QR settings, deep links, and token exchange for mobile login.",
    ),
    "QueueController": (
        "message queues",
        "/api",
        "Rule-engine queue registration and lookup by id or name.",
    ),
    "QueueStatsController": (
        "queue statistics",
        "/api",
        "Per-service queue statistics entities for monitoring.",
    ),
    "RpcV1Controller": (
        "device RPC (legacy v1 API)",
        "/api/plugins/rpc",
        "Deprecated one-way and two-way device RPC; prefer RpcV2Controller.",
    ),
    "RpcV2Controller": (
        "device RPC (v2 API)",
        "/api/rpc",
        "One-way/two-way RPC and persistent RPC lifecycle.",
    ),
    "RuleChainController": (
        "rule chains",
        "/api",
        "Rule chain CRUD, metadata, import/export, edge assignment, and script testing.",
    ),
    "RuleEngineController": (
        "rule engine REST injection",
        "/api/rule-engine/",
        "Push REST payloads into the rule engine as TbMsg for custom processing.",
    ),
    "SystemInfoController": (
        "system build info and UI parameters",
        "/api",
        "Version metadata and tenant/user-scoped UI capability flags (hidden from Swagger).",
    ),
    "TbResourceController": (
        "tenant and system resources",
        "/api",
        "Files such as LwM2M models, keystores, and JS modules.",
    ),
    "TelemetryController": (
        "entity telemetry and attributes",
        "/api/plugins/telemetry",
        "Read/write attributes and time-series for devices and other entities.",
    ),
    "TenantController": (
        "tenants",
        "/api",
        "Tenant CRUD and paginated listings for system administrators.",
    ),
    "TenantProfileController": (
        "tenant profiles",
        "/api",
        "Tenant profile CRUD, defaults, and API/rate-limit configuration.",
    ),
}


def split_license(content: str) -> tuple[str, str]:
    m = re.match(r"(\s*/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    if m:
        return m.group(1), m.group(2)
    return "", content


def has_javadoc_before(body: str, pos: int) -> bool:
    before = body[:pos].rstrip()
    return before.endswith("*/")


def normalize_path(base: str, sub: str) -> str:
    if base.startswith("mixed"):
        path = sub or "/"
    elif sub.startswith("/api") or sub.startswith("/."):
        path = sub
    elif not sub or sub == "/":
        path = base.rstrip("/") or "/"
    elif not base:
        path = sub if sub.startswith("/") else "/" + sub
    elif base.endswith("/") and sub.startswith("/"):
        path = base.rstrip("/") + sub
    elif not base.endswith("/") and not sub.startswith("/"):
        path = f"{base}/{sub}"
    else:
        path = base + sub
    return re.sub(r"//+", "/", path)


def parse_auth(preauth: str | None) -> str:
    if not preauth:
        return "None (public / noauth endpoint)"
    m = re.search(r"hasAnyAuthority\s*\(([^)]+)\)", preauth)
    if m:
        roles = re.findall(r"'([^']+)'", m.group(1))
        return ", ".join(f"{{@code {r}}}" for r in roles) if roles else preauth
    m = re.search(r"hasAuthority\s*\(\s*'([^']+)'", preauth)
    if m:
        return f"{{@code {m.group(1)}}}"
    return preauth


def first_sentence_from_api(value: str) -> str:
    paren = value.find("(")
    if paren > 0 and value.rstrip().endswith(")"):
        return value[:paren].strip() + "."
    return value.split("(")[0].strip() + "."


def humanize(name: str) -> str:
    s = re.sub(r"([a-z])([A-Z])", r"\1 \2", name)
    return s.replace("_", " ")


def extract_http(block: str, base: str) -> tuple[str, str] | None:
    m = METHOD_MAPPING.search(block)
    if m:
        return m.group(1).upper(), normalize_path(base, m.group(2))
    m = REQUEST_MAPPING_VALUE_METHOD.search(block)
    if m:
        return m.group(2).upper(), normalize_path(base, m.group(1))
    m = REQUEST_MAPPING.search(block)
    if m:
        return m.group(2).upper(), normalize_path(base, m.group(1))
    m = REQUEST_MAPPING_ROOT.search(block)
    if m:
        return m.group(1).upper(), normalize_path(base, "")
    return None


def extract_params(signature: str) -> list[tuple[str, str]]:
    params: list[tuple[str, str]] = []
    seen: set[str] = set()
    patterns = [
        r"@PathVariable(?:\([^)]*\))?\s+(?:[\w<>,\s\[\]?]+\s+)?(?P<name>\w+)",
        r"@RequestParam(?:\([^)]*\))?\s+(?:[\w<>,\s\[\]?]+\s+)?(?P<name>\w+)",
        r"@RequestPart(?:\([^)]*\))?\s+(?:[\w<>,\s\[\]?]+\s+)?(?P<name>\w+)",
        r"@RequestHeader(?:\([^)]*\))?\s+(?:[\w<>,\s\[\]?]+\s+)?(?P<name>\w+)",
        r"@AuthenticationPrincipal\s+(?:[\w<>,\s\[\]?]+\s+)?(?P<name>\w+)",
        r"@RequestBody(?:\([^)]*\))?\s+(?:@\w+\s+)*(?:[\w<>,\s\[\]?]+\s+)?(?P<name>\w+)",
    ]
    for pattern in patterns:
        for m in re.finditer(pattern, signature):
            name = m.group("name")
            if name not in seen:
                seen.add(name)
                params.append((name, humanize(name)))
    return params


def extract_return(ret: str) -> str:
    ret = ret.strip()
    if ret == "void":
        return "empty response body"
    simple = ret.split("<")[0].split()[-1]
    if simple and simple[0].isupper():
        return f"{{@link {simple}}} response body"
    return ret


def extract_return_from_sig(sig: str) -> str:
    inner = re.sub(r"^(?:public|protected)\s+", "", sig.strip())
    paren = inner.find("(")
    if paren < 0:
        return "response body"
    before = inner[:paren].strip()
    parts = before.rsplit(None, 1)
    if len(parts) == 2:
        return extract_return(parts[0])
    return "response body"


def extract_throws(signature: str) -> list[str]:
    m = THROWS_CLAUSE.search(signature)
    if not m:
        return []
    return [t.strip().split(".")[-1] for t in m.group(1).split(",") if t.strip()]


def format_javadoc(indent: str, lines: list[str]) -> str:
    inner = "\n".join(f"{indent} * {line}" for line in lines)
    return f"{indent}/**\n{inner}\n{indent} */\n"


def class_javadoc(class_name: str, indent: str) -> str:
    entry = CLASS_DESCRIPTIONS.get(class_name)
    if entry:
        label, base, detail = entry
        lines = [
            f"REST API for {label}.",
            "",
            f"<p>Base path: {{@code {base}}}. {detail}",
            "Clients authenticate with a JWT ({@code Authorization: Bearer <token>}) unless noted as public.",
        ]
    else:
        lines = [f"REST controller ({humanize(class_name)})."]
    return format_javadoc(indent, lines)


def method_javadoc(
    indent: str,
    method_name: str,
    http: tuple[str, str],
    auth: str,
    api_value: str | None,
    params: list[tuple[str, str]],
    ret: str,
    throws: list[str],
) -> str:
    desc = first_sentence_from_api(api_value) if api_value else humanize(method_name).capitalize() + "."
    lines = [
        desc,
        "",
        f"<p><b>HTTP:</b> {{@code {http[0]} {http[1]}}}",
        f"<p><b>Auth:</b> {auth}",
    ]
    for name, hint in params:
        lines.append(f"@param {name} {hint}")
    lines.append(f"@return {ret}")
    for t in throws:
        lines.append(f"@throws {t} if the request fails validation or authorization")
    if not throws:
        lines.append("@throws Exception if an unexpected error occurs during processing")
    return format_javadoc(indent, lines)


def lifecycle_javadoc(indent: str, name: str, desc: str) -> str:
    lines = [
        desc,
        "",
        "<p><b>HTTP:</b> not applicable (Spring lifecycle callback)",
        "<p><b>Auth:</b> not applicable",
        "@return empty response body",
    ]
    return format_javadoc(indent, lines)


def get_base_path(body: str, class_name: str) -> str:
    if class_name in BASE_PATH_BY_CLASS:
        return BASE_PATH_BY_CLASS[class_name]
    m = CLASS_REQUEST_MAPPING.search(body)
    return m.group(1) if m else "/api"


def find_class_javadoc_position(body: str, cm: re.Match) -> int:
    chunk = body[: cm.start()]
    for ann in ("@RestController", "@Controller"):
        idx = chunk.rfind(ann)
        if idx >= 0:
            return chunk.rfind("\n", 0, idx) + 1
    return cm.start()
def find_method_doc_insertion_point(body: str, mapping_pos: int) -> int:
    """Insert JavaDoc before @ApiOperation, or before security/mapping annotations for this method only."""
    prev_method_end = body.rfind("\n    }", 0, mapping_pos)
    if prev_method_end < 0:
        prev_method_end = body.rfind("{", 0, mapping_pos)
    region_start = prev_method_end
    region = body[region_start:mapping_pos]
    api_op = region.rfind("@ApiOperation")
    if api_op >= 0:
        abs_pos = region_start + api_op
        return body.rfind("\n", 0, abs_pos) + 1
    pos = body.rfind("\n", 0, mapping_pos) + 1
    while pos > region_start:
        prev_nl = body.rfind("\n", 0, pos - 1)
        prev_start = prev_nl + 1
        if prev_start < region_start or prev_start >= pos:
            break
        prev_line = body[prev_start:pos].strip()
        if prev_line.startswith(
            ("@PreAuthorize", "@Hidden", "@ResponseBody", "@ResponseStatus", "@Deprecated")
        ):
            pos = prev_start
            continue
        break
    return pos


def collect_block_from(body: str, start: int) -> tuple[str, str, str]:
    """Return (block_text, method_name, method signature through closing paren)."""
    rest = body[start:]
    pub = re.search(
        r"^(?P<indent>\s*)(?:public|protected)\s+"
        r"(?P<sig>(?:[\w<>,\s\[\]?]+\s+)+)(?P<name>\w+)\s*\(",
        rest,
        re.MULTILINE,
    )
    if not pub:
        return "", "", ""
    depth = 0
    i = pub.end() - 1
    while i < len(rest):
        if rest[i] == "(":
            depth += 1
        elif rest[i] == ")":
            depth -= 1
            if depth == 0:
                block = rest[: i + 1]
                sig = rest[pub.start() : i + 1]
                return block, pub.group("name"), sig
        i += 1
    return rest[:2000], pub.group("name"), pub.group(0)


def process_file(path: Path, dry_run: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license(content)
    cm = CLASS_DECL.search(body)
    if not cm:
        return False
    class_name = cm.group("name")
    indent = cm.group("indent")
    changed = False

    class_insert = find_class_javadoc_position(body, cm)
    if not has_javadoc_before(body, class_insert):
        jd = class_javadoc(class_name, indent)
        body = body[:class_insert] + jd + body[class_insert:]
        changed = True
        cm = CLASS_DECL.search(body)
        if not cm:
            return False

    base = get_base_path(body, class_name)
    class_open = re.search(r"public\s+class\s+" + re.escape(class_name) + r"[^{]*\{", body)
    class_body_start = class_open.end() - 1 if class_open else body.find("{", cm.end())

    anchors: list[int] = []
    for m in MAPPING_ANCHOR.finditer(body, class_body_start):
        anchors.append(m.start())

    for mapping_pos in reversed(anchors):
        block_start = find_method_doc_insertion_point(body, mapping_pos)
        if has_javadoc_before(body, block_start):
            continue
        _, method_name, sig = collect_block_from(body, block_start)
        if not method_name:
            continue
        block_end = body.find(sig, block_start) + len(sig) if sig else mapping_pos
        block = body[block_start:block_end]
        http = extract_http(block, base)
        if not http:
            continue
        pre_m = PREAUTHORIZE.search(block)
        auth = parse_auth(pre_m.group(1) if pre_m else None)
        api_m = API_OPERATION.search(block)
        params = extract_params(sig)
        ret = extract_return_from_sig(sig)
        throws = extract_throws(sig)
        jd = method_javadoc(
            indent + "    ",
            method_name,
            http,
            auth,
            api_m.group(1) if api_m else None,
            params,
            ret,
            throws,
        )
        body = body[:block_start] + jd + body[block_start:]
        changed = True

    # Lifecycle methods without HTTP mappings
    lifecycle = {
        "init": "Logs system build information at application startup.",
        "initExecutor": "Creates the background executor used for telemetry controller callbacks.",
        "shutdownExecutor": "Shuts down the telemetry controller executor on application stop.",
    }
    for lm in re.finditer(
        r"^(?P<indent>\s*)@PostConstruct\s*\n\s*public\s+void\s+(?P<name>\w+)\s*\(",
        body,
        re.MULTILINE,
    ):
        name = lm.group("name")
        if name not in lifecycle:
            continue
        block_start = lm.start()
        if has_javadoc_before(body, block_start):
            continue
        jd = lifecycle_javadoc(lm.group("indent"), name, lifecycle[name])
        body = body[:block_start] + jd + body[block_start:]
        changed = True

    if changed and not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("files", nargs="+", type=Path)
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()
    count = 0
    for f in args.files:
        if process_file(f, args.dry_run):
            count += 1
            print(f"Updated {f.name}")
    print(f"Done: {count} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
