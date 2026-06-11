#!/usr/bin/env python3
"""
Add detailed class- and method-level Javadoc to ThingsBoard security and edge service packages.
Class docs: purpose, responsibilities, key dependencies.
Method docs: description, @param, @return, @throws.
Does not modify logic; preserves license headers.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

DECL_RE = re.compile(
    r"^(?P<indent>\s*)(?:(?:public|protected|private)\s+)?"
    r"(?:(?:abstract|sealed|non-sealed|static|final)\s+)*"
    r"(?:class|interface|enum|record)\s+"
    r"(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)

EXTENDS_RE = re.compile(r"\bextends\s+([\w.<>,\s]+)")
IMPLEMENTS_RE = re.compile(r"\bimplements\s+([\w.<>,\s]+)")

METHOD_START_RE = re.compile(
    r"^(?P<indent>\s*)(?:(?:public|protected)\s+)"
    r"(?:(?:static|final|synchronized|abstract|default)\s+)*"
    r"(?:<[^>]+>\s+)?"
    r"(?P<sig>(?:[\w.<>,\s\[\]?@]+\s+)+)(?P<name>\w+)\s*\(",
    re.MULTILINE,
)

AUTOWIRED_FIELD_RE = re.compile(
    r"@Autowired(?:\([^)]*\))?\s*\n\s*(?:private|protected|public|final)?\s*"
    r"(?:[\w.<>,\s\[\]?]+\s+)+(\w+)\s*;",
    re.MULTILINE,
)

CONSTRUCTOR_PARAM_RE = re.compile(
    r"@(?:Autowired|Value)\s*\n\s*public\s+\w+\s*\(([^)]*)\)",
    re.DOTALL,
)

STUB_DOC_RE = re.compile(
    r"Created by|@author|Processes edqs|^\s*\*\s*\w+\.\s*$",
    re.MULTILINE | re.IGNORECASE,
)

MALFORMED_JAVADOC_RE = re.compile(r"\*\s*\n\s*\n\s+\*")


def split_license(content: str) -> tuple[str, str]:
    content = content.replace("\r\n", "\n").replace("\r", "\n")
    m = re.match(r"(\s*/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    return (m.group(1), m.group(2)) if m else ("", content)


def normalize_javadoc_spacing(body: str) -> str:
    """Collapse spurious blank lines inside Javadoc blocks."""
    def fix_block(block: str) -> str:
        inner_start = block.find("/**") + 3
        inner_end = block.rfind("*/")
        if inner_start < 3 or inner_end < 0:
            return block
        prefix = block[:inner_start]
        inner = block[inner_start:inner_end]
        suffix = block[inner_end:]
        inner = re.sub(r"\n\s*\n+", "\n", inner)
        return prefix + inner + suffix

    return re.sub(r"/\*\*[\s\S]*?\*/", lambda m: fix_block(m.group(0)), body)


def humanize(name: str) -> str:
    return re.sub(r"([a-z])([A-Z])", r"\1 \2", name).replace("_", " ").lower()


def is_detailed_javadoc(doc: str, needs_return: bool = False) -> bool:
    if not doc:
        return False
    if MALFORMED_JAVADOC_RE.search(doc):
        return False
    has_param = "@param" in doc
    has_return = "@return" in doc
    has_throws = "@throws" in doc
    if has_param or has_return or has_throws:
        if needs_return and not has_return:
            return False
        return True
    lines = [ln.strip().lstrip("* ").strip() for ln in doc.splitlines() if ln.strip().startswith("*")]
    content_lines = [ln for ln in lines if ln and not ln.startswith("@")]
    if not needs_return and content_lines and not has_param:
        first = content_lines[0]
        if len(first) > 3 and not re.match(r"^(Created by|@author)", first, re.I):
            return True
    if STUB_DOC_RE.search(doc):
        return False
    if len(content_lines) >= 2:
        return True
    if any(tag in doc for tag in ("<p>", "{@link", "<b>")):
        return True
    if len(doc) > 200:
        return True
    return False


def javadoc_immediately_before(body: str, pos: int) -> str:
    """Return Javadoc block directly above pos (annotations/blank lines only in between)."""
    before = body[:pos].rstrip()
    if not before.endswith("*/"):
        return ""
    end = before.rfind("*/")
    start = before.rfind("/**", 0, end)
    if start < 0:
        return ""
    between = before[end + 2 :].strip()
    if between:
        for line in between.splitlines():
            s = line.strip()
            if s and not s.startswith("@"):
                return ""
    return before[start : end + 2]


def has_javadoc_before(body: str, pos: int) -> bool:
    return bool(javadoc_immediately_before(body, pos))


def get_javadoc_before(body: str, pos: int) -> str:
    return javadoc_immediately_before(body, pos)


def find_insert_position(body: str, decl_start: int) -> int:
    line_start = body.rfind("\n", 0, decl_start) + 1
    pos = line_start
    while pos > 0:
        prev_nl = body.rfind("\n", 0, pos - 1)
        prev_start = prev_nl + 1
        prev = body[prev_start:pos].strip()
        if not prev or prev.startswith("@") or prev.endswith(")"):
            pos = prev_start
            continue
        break
    return pos


def detect_kind(line: str) -> str:
    for k in ("class", "interface", "enum", "record"):
        if re.search(rf"\b{k}\b", line):
            return k
    return "class"


def parse_declaration_line(body: str, decl_start: int) -> tuple[str | None, str | None]:
    """Parse extends/implements from the type declaration line only (not Javadoc)."""
    line_end = body.find("\n", decl_start)
    if line_end < 0:
        line_end = len(body)
    decl_line = body[decl_start:line_end]
    extends_m = EXTENDS_RE.search(decl_line)
    implements_m = IMPLEMENTS_RE.search(decl_line)
    return (
        extends_m.group(1).strip() if extends_m else None,
        implements_m.group(1).strip() if implements_m else None,
    )


def extract_package(body: str) -> str:
    m = re.search(r"^package\s+([\w.]+)\s*;", body, re.MULTILINE)
    return m.group(1) if m else ""


def extract_dependencies(body: str) -> list[str]:
    deps: list[str] = []
    seen: set[str] = set()
    for m in AUTOWIRED_FIELD_RE.finditer(body):
        name = m.group(1)
        if name not in seen:
            seen.add(name)
            deps.append(name)
    return deps[:8]


def package_area(pkg: str) -> str:
    if "service.security" in pkg:
        if ".auth.jwt" in pkg:
            return "JWT bearer-token authentication"
        if ".auth.oauth2" in pkg:
            return "OAuth2 / social login"
        if ".auth.mfa" in pkg:
            return "two-factor authentication (MFA)"
        if ".auth.rest" in pkg:
            return "username/password REST login"
        if ".auth.pat" in pkg:
            return "personal access token (API key) authentication"
        if ".auth.extractor" in pkg:
            return "HTTP token extraction for security filters"
        if ".permission" in pkg:
            return "role-based access control (RBAC)"
        if ".device" in pkg:
            return "device credential validation"
        if ".system" in pkg:
            return "system-level security (password reset, activation)"
        if ".model" in pkg:
            return "security DTOs and principals"
        if ".exception" in pkg:
            return "authentication/authorization exceptions"
        return "platform security"
    if "service.edge" in pkg:
        if ".rpc.processor" in pkg:
            return "edge downlink/uplink entity synchronization"
        if ".rpc.fetch" in pkg:
            return "edge initial-sync event fetching"
        if ".rpc.session" in pkg:
            return "edge gRPC session lifecycle"
        if ".rpc.sync" in pkg:
            return "edge-to-cloud sync requests"
        if ".rpc.service" in pkg or ".rpc." in pkg:
            return "edge gRPC RPC transport"
        if ".instructions" in pkg:
            return "edge upgrade instructions"
        return "ThingsBoard Edge integration"
    return "application service"


def class_description(name: str, kind: str, pkg: str, body: str, extends: str | None, implements: str | None) -> list[str]:
    area = package_area(pkg)
    words = humanize(name)
    deps = extract_dependencies(body)
    lines: list[str] = []

    if name == "EdgeContextComponent":
        lines.append(
            "Central Spring context for ThingsBoard Edge: wires DAO services, edge processors, "
            "and gRPC/RPC helpers used during cloud↔edge synchronization."
        )
    elif name.endswith("EdgeEventFetcher"):
        entity = humanize(name.replace("EdgeEventFetcher", ""))
        lines.append(f"Fetches {entity} entities for edge initial synchronization.")
    elif name.endswith("EdgeProcessor") or (name.endswith("Processor") and "edge" in pkg):
        entity = humanize(name.replace("EdgeProcessor", "").replace("Processor", ""))
        lines.append(f"Processes {entity} edge events for cloud↔edge synchronization.")
    elif name.endswith("Processor") and "security" in pkg:
        lines.append(f"Security processor: {words}.")
    elif name.endswith("AuthenticationProvider") or name.endswith("AuthProvider"):
        lines.append(f"Spring Security authentication provider for {area}.")
    elif name.endswith("ProcessingFilter") or name.endswith("LoginProcessingFilter"):
        lines.append(f"Servlet filter that handles {area} login/token requests.")
    elif name.endswith("AuthenticationToken"):
        lines.append(f"Spring Security authentication token carrying {area} credentials or principal.")
    elif name.endswith("AuthenticationSuccessHandler") or name.endswith("AuthenticationFailureHandler"):
        handler = "success" if "Success" in name else "failure"
        lines.append(f"Handles {handler} responses for {area}.")
    elif name.endswith("TokenExtractor"):
        lines.append(f"Extracts bearer/API tokens from HTTP requests for {area}.")
    elif name.endswith("Permissions"):
        role = humanize(name.replace("Permissions", ""))
        lines.append(f"Defines RBAC permission rules for {role} users.")
    elif name.endswith("Exception"):
        lines.append(f"Thrown when {words.replace(' exception', '')} during security operations.")
    elif name.endswith("Request") or name.endswith("Response"):
        lines.append(f"DTO for {area}: {words}.")
    elif name.endswith("Service") and kind == "interface":
        lines.append(f"Service contract for {area}.")
    elif name.endswith("Service") or name.endswith("ServiceImpl"):
        domain = humanize(name.replace("ServiceImpl", "").replace("Service", ""))
        lines.append(f"Service implementation for {domain} in {area}.")
    elif name.startswith("Default") and kind == "class":
        base = humanize(name[7:])
        lines.append(f"Default implementation of {base} for {area}.")
    elif name.startswith("Abstract") and kind == "class":
        base = humanize(name[8:])
        lines.append(f"Base class for {base} implementations in {area}.")
    elif kind == "interface":
        lines.append(f"Contract for {words} in {area}.")
    elif kind == "enum":
        lines.append(f"Enumeration of {words} used in {area}.")
    else:
        lines.append(f"{words.capitalize()} for {area}.")

    resp: list[str] = []
    if kind == "class" and ("@Component" in body or "@Service" in body):
        resp.append("Spring-managed service component.")
    if implements:
        impls = [i.strip().split(".")[-1] for i in implements.split(",") if i.strip()]
        if impls and impls != [name]:
            resp.append(f"Implements {', '.join(impls)}.")
    if extends and extends.strip():
        resp.append(f"Extends {extends.strip().split('.')[-1]}.")
    if name.endswith("EdgeProcessor") or "EdgeEventFetcher" in name:
        resp.append("Uses EdgeContextComponent and DAO services to persist and propagate changes.")
    if "AuthenticationProvider" in name:
        resp.append("Integrates with Spring Security filter chain.")
    if "AccessControl" in name or "Permission" in name:
        resp.append("Evaluates tenant/customer/system-admin scopes against Resource and Operation.")
    if resp:
        lines.append("")
        lines.append(f"<p><b>Responsibilities:</b> {' '.join(resp)}")

    if deps:
        dep_links = ", ".join(f"{{@link #{d}}}" for d in deps)
        lines.append(f"<p><b>Key dependencies:</b> {dep_links}.")

    return lines


def method_description(name: str, class_name: str, pkg: str) -> str:
    if name == "authenticate":
        return "Authenticates credentials and returns a populated security principal."
    if name == "supports":
        return "Indicates whether this provider can authenticate the given authentication token type."
    if name.startswith("checkPermission"):
        return "Verifies the user is allowed to perform the operation; throws if denied."
    if name.startswith("hasPermission"):
        return "Returns whether the user is allowed to perform the operation."
    if name.startswith("validate"):
        target = humanize(name[8:]) if len(name) > 8 else "the request"
        return f"Validates {target} and invokes the callback with the result."
    if name.startswith("process") and "FromEdge" in name:
        return f"Processes an edge-originated message and applies changes on the cloud."
    if name.startswith("process"):
        return f"Processes {humanize(name[7:])}."
    if name.startswith("fetch"):
        return f"Fetches {humanize(name[5:])} for edge synchronization."
    if name.startswith("find"):
        return f"Loads {humanize(name[4:])}."
    if name.startswith("get"):
        return f"Returns {humanize(name[3:])}."
    if name.startswith("save") or name.startswith("create"):
        return f"Creates or persists {humanize(name.replace('save', '').replace('create', ''))}."
    if name.startswith("delete") or name.startswith("remove"):
        return f"Removes {humanize(name.replace('delete', '').replace('remove', ''))}."
    if name.startswith("update"):
        return f"Updates {humanize(name[6:])}."
    if name.startswith("handle"):
        return f"Handles {humanize(name[6:])}."
    if name.startswith("init") or name == "initExecutor":
        return "Initializes resources required by this component."
    if name in ("destroy", "shutdownExecutor", "shutdown", "stop", "preDestroy"):
        return "Releases resources and shuts down background executors."
    if name.startswith("is"):
        return f"Returns whether {humanize(name[2:])}."
    if name.startswith("build"):
        return f"Builds {humanize(name[5:])}."
    if name.startswith("parse"):
        return f"Parses {humanize(name[5:])}."
    if name.startswith("convert"):
        return f"Converts {humanize(name[7:])}."
    if name == "accept":
        return "Accepts the supplied arguments and performs the operation."
    if class_name == name:
        return f"Constructs {humanize(class_name)} with injected dependencies."
    return f"{humanize(name).capitalize()}."


def parse_method_signature(body: str, start: int) -> tuple[str, str, str, list[tuple[str, str]], list[str]]:
    """Return (full_sig_through_close_paren, method_name, return_type, params, throws)."""
    m = METHOD_START_RE.search(body, start)
    if not m:
        return "", "", "", [], []
    name = m.group("name")
    ret = m.group("sig").strip() or "void"

    open_paren = body.find("(", m.end() - len(name) - 1)
    depth = 0
    close_paren = open_paren
    for i in range(open_paren, len(body)):
        if body[i] == "(":
            depth += 1
        elif body[i] == ")":
            depth -= 1
            if depth == 0:
                close_paren = i
                break

    params_str = body[open_paren + 1 : close_paren]
    params: list[tuple[str, str]] = []
    if params_str.strip():
        current = ""
        pdepth = 0
        for ch in params_str + ",":
            if ch in "(<[":
                pdepth += 1
                current += ch
            elif ch in ")>]":
                pdepth -= 1
                current += ch
            elif ch == "," and pdepth == 0:
                part = current.strip()
                if part:
                    pname = part.rsplit(None, 1)[-1].strip()
                    ptype = part[: part.rfind(pname)].strip() if pname in part else part
                    params.append((pname, ptype.split(".")[-1]))
                current = ""
            else:
                current += ch

    rest = body[close_paren + 1 : close_paren + 120]
    throws: list[str] = []
    tm = re.search(r"throws\s+([\w.\s,]+?)(?:\s*\{|\s*;)", rest)
    if tm:
        throws = [t.strip().split(".")[-1] for t in tm.group(1).split(",") if t.strip()]

    full_sig = body[m.start() : close_paren + 1]
    return full_sig, name, ret.strip(), params, throws


def format_return(ret: str) -> str:
    ret = ret.strip()
    if not ret or ret == "void":
        return "nothing"
    parts = ret.replace("?", "").split("<")[0].split()
    if not parts:
        return ret
    simple = parts[-1]
    if simple and simple[0].isupper():
        return f"{{@link {simple}}} result"
    return ret


def format_javadoc(indent: str, lines: list[str]) -> str:
    indent = indent.replace("\r", "")
    clean_lines = [line.replace("\r", "") for line in lines]
    inner = "\n".join(f"{indent} * {line}" if line else f"{indent} *" for line in clean_lines)
    return f"{indent}/**\n{inner}\n{indent} */\n"


def remove_javadoc_block(body: str, pos: int) -> tuple[str, int]:
    before = body[:pos].rstrip()
    start = before.rfind("/**")
    if start < 0:
        return body, pos
    end = before.rfind("*/")
    if end < 0:
        return body, pos
    new_before = before[:start].rstrip()
    if new_before and not new_before.endswith("\n"):
        new_before += "\n"
    new_pos = len(new_before)
    return new_before + body[pos:], new_pos


def add_or_replace_class_javadoc(body: str, pkg: str) -> tuple[str, bool]:
    m = DECL_RE.search(body)
    if not m:
        return body, False
    name = m.group("name")
    kind = detect_kind(m.group(0))
    indent = m.group("indent")
    insert_at = find_insert_position(body, m.start())
    existing = get_javadoc_before(body, insert_at)
    if existing and is_detailed_javadoc(existing):
        return body, False
    if existing:
        body, insert_at = remove_javadoc_block(body, insert_at)
    extends, implements = parse_declaration_line(body, m.start())
    lines = class_description(name, kind, pkg, body, extends, implements)
    jd = format_javadoc(indent, lines)
    body = body[:insert_at] + jd + body[insert_at:]
    return body, True


def find_method_insertion_point(body: str, method_start: int) -> int:
    pos = body.rfind("\n", 0, method_start) + 1
    region_start = body.rfind("\n    }", 0, method_start)
    if region_start < 0:
        region_start = 0
    while pos > region_start:
        prev_nl = body.rfind("\n", 0, pos - 1)
        prev_start = prev_nl + 1
        prev_line = body[prev_start:pos].strip()
        if prev_line.startswith("@") and not prev_line.startswith("@param"):
            pos = prev_start
            continue
        break
    return pos


def add_method_javadocs(body: str, class_name: str, pkg: str) -> tuple[str, int]:
    count = 0
    class_m = DECL_RE.search(body)
    if not class_m:
        return body, 0
    class_body_start = body.find("{", class_m.end())
    if class_body_start < 0:
        return body, 0

    anchors = [m.start() for m in METHOD_START_RE.finditer(body, class_body_start)]

    for method_start in reversed(anchors):
        insert_at = find_method_insertion_point(body, method_start)
        full_sig, name, ret, params, throws = parse_method_signature(body, method_start)
        if not name:
            continue

        existing = get_javadoc_before(body, insert_at)
        needs_return = ret.strip() not in ("", "void")
        if existing and is_detailed_javadoc(existing, needs_return=needs_return):
            continue

        if existing:
            body, insert_at = remove_javadoc_block(body, insert_at)

        m = METHOD_START_RE.search(body, insert_at)
        if not m:
            continue
        indent = m.group("indent")
        lines = [method_description(name, class_name, pkg), ""]
        for pname, ptype in params:
            hint = humanize(pname)
            if ptype:
                lines.append(f"@param {pname} {hint} ({ptype})")
            else:
                lines.append(f"@param {pname} {hint}")
        if ret.strip() and ret.strip() != "void":
            lines.append(f"@return {format_return(ret)}")
        for t in throws:
            lines.append(f"@throws {t} if the operation fails")

        jd = format_javadoc(indent, lines)
        body = body[:insert_at] + jd + body[insert_at:]
        count += 1

    return body, count


def process_file(path: Path, dry_run: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license(content)
    normalized = normalize_javadoc_spacing(body)
    changed = normalized != body
    body = normalized
    pkg = extract_package(body)
    m = DECL_RE.search(body)
    if not m:
        return False
    class_name = m.group("name")

    new_body, class_changed = add_or_replace_class_javadoc(body, pkg)
    if class_changed:
        changed = True
        body = new_body

    new_body, method_count = add_method_javadocs(body, class_name, pkg)
    if method_count:
        changed = True
        body = new_body

    final_body = normalize_javadoc_spacing(body)
    if final_body != body:
        changed = True
        body = final_body

    if changed and not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def process_directory(root: Path, dry_run: bool) -> int:
    count = 0
    for path in sorted(root.rglob("*.java")):
        if path.name == "package-info.java":
            continue
        if process_file(path, dry_run):
            count += 1
    return count


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("roots", nargs="+", type=Path)
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    totals: dict[str, int] = {}
    for root in args.roots:
        if not root.is_dir():
            print(f"Skip missing: {root}", file=sys.stderr)
            continue
        n = process_directory(root, args.dry_run)
        totals[str(root)] = n
        print(f"{root}: {n} files updated", file=sys.stderr)

    for root, n in totals.items():
        print(f"{root}: {n}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
