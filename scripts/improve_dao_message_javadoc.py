#!/usr/bin/env python3
"""Improve Javadoc in dao-api and message modules. No logic changes."""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent / "tools" / "scripts"))
from add_service_javadoc import (  # noqa: E402
    DECL_LINE_RE,
    MethodInfo,
    split_license_and_body,
    humanize,
    detect_kind,
    format_return,
    format_javadoc,
    find_insert_position,
    javadoc_block_before,
    class_body_bounds,
    insert_text_at,
    find_methods,
    method_description,
)
from fix_common_javadoc import dedupe_consecutive_javadocs  # noqa: E402

STUB_MARKERS = (
    "ThingsBoard common module",
    "Message: ",
    "Service contract for",
    "Implemented by the corresponding class in this or the dao module",
)

DAO_ENTITIES = {
    "DeviceService": "Device",
    "AlarmService": "Alarm",
    "AssetService": "Asset",
    "CustomerService": "Customer",
    "TenantService": "Tenant",
    "UserService": "User",
    "DashboardService": "Dashboard",
    "RuleChainService": "RuleChain",
    "RelationService": "Relation",
    "AttributesService": "Attributes",
    "TimeseriesService": "Timeseries",
    "EntityViewService": "EntityView",
    "EdgeService": "Edge",
    "RpcService": "Rpc",
    "QueueService": "Queue",
    "OtaPackageService": "OtaPackage",
    "ResourceService": "Resource",
    "EventService": "Event",
    "AuditLogService": "AuditLog",
    "NotificationService": "Notification",
    "CalculatedFieldService": "CalculatedField",
    "DeviceProfileService": "DeviceProfile",
    "DeviceCredentialsService": "DeviceCredentials",
    "AssetProfileService": "AssetProfile",
    "TenantProfileService": "TenantProfile",
    "WidgetTypeService": "WidgetType",
    "WidgetsBundleService": "WidgetsBundle",
    "ImageService": "Image",
    "JobService": "Job",
    "DomainService": "Domain",
    "ApiKeyService": "ApiKey",
    "AiModelService": "AiModel",
    "EntityService": "Entity",
}

MESSAGE_DOCS: dict[str, list[str]] = {
    "TbMsg": [
        "Immutable message flowing through the <strong>rule engine</strong> (rule chains and rule nodes).",
        "",
        "<p>Carries originator {@link org.thingsboard.server.common.data.id.EntityId}, JSON (or binary) payload,",
        "{@link TbMsgMetaData}, queue name, and {@link org.thingsboard.server.common.data.msg.TbMsgType}.",
        "May be persisted to Kafka between core and rule-engine services in microservices mode.",
        "",
        "<p>Factory methods: {@link #newMsg(...)} variants at bottom of this class.",
    ],
    "TbActorMsg": ["Marker for messages delivered to ThingsBoard actors."],
    "ToDeviceActorNotificationMsg": [
        "Notification sent to a device actor (tenant- and device-scoped).",
        "",
        "<p>Combines {@link TbActorMsg}, {@link org.thingsboard.server.common.msg.aware.TenantAwareMsg},",
        "and {@link org.thingsboard.server.common.msg.aware.DeviceAwareMsg}.",
    ],
    "TbMsgMetaData": ["String key-value metadata map carried alongside {@link TbMsg}."],
    "PartitionChangeMsg": ["Broadcast when a service Kafka partition assignment changes."],
    "QueueToRuleEngineMsg": ["Wraps a {@link TbMsg} consumed from the rule-engine input queue."],
    "ToDeviceRpcRequest": ["Server-side RPC request targeting a device."],
    "FromDeviceRpcResponse": ["RPC response payload returned from a device."],
    "ComponentLifecycleMsg": ["Rule-engine component lifecycle create/update/delete notification."],
    "DeviceAttributes": ["Snapshot of client, shared, and server-side device attributes."],
    "TenantAwareMsg": ["Message carrying {@link org.thingsboard.server.common.data.id.TenantId}."],
    "DeviceAwareMsg": ["Message carrying {@link org.thingsboard.server.common.data.id.DeviceId}."],
}

PARAM_HINTS = {
    "tenantId": "tenant that owns the entity or operation",
    "deviceId": "target device identifier",
    "customerId": "customer to assign or filter by",
    "pageLink": "pagination and sort parameters",
    "request": "request payload with operation parameters",
    "name": "entity name (unique within tenant scope where applicable)",
    "doValidate": "whether to run validation before persist",
    "accessToken": "device access token for MQTT/HTTP connectivity",
    "nameConflictStrategy": "behavior when an entity with the same name already exists",
}


def is_stub(text: str) -> bool:
    return len(text.strip()) < 50 or any(m in text for m in STUB_MARKERS)


def method_line_start(body: str, sig_start: int) -> int:
    pos = sig_start
    while pos < len(body) and body[pos] in "\r\n\t ":
        pos += 1
    return body.rfind("\n", 0, pos) + 1


def has_method_javadoc(body: str, sig_start: int) -> bool:
    pos = method_line_start(body, sig_start)
    trimmed = body[:pos].rstrip()
    if not trimmed.endswith("*/"):
        return False
    if body[len(trimmed) : pos].strip():
        return False
    start = trimmed.rfind("/**")
    if start < 0:
        return False
    between = body[start:pos]
    if re.search(r"(?:^|\n)\s*(?:public|protected|private|class|interface|enum|record)\b", between):
        return False
    if re.search(r"\)\s*;\s*(?:\n|$)", between):
        return False
    return True


def param_desc(name: str, method: MethodInfo) -> str:
    if name in PARAM_HINTS:
        return PARAM_HINTS[name]
    for p in method.params:
        if p.name == name:
            parts = p.type_hint.replace("?", "").split("<")[0].strip().split()
            if parts:
                t = parts[-1]
                if t and t[0].isupper() and re.match(r"^[A-Z]\w*$", t):
                    return f"{humanize(name)} ({{@link {t}}})"
    return humanize(name)


def build_method_doc(method: MethodInfo) -> str:
    lines = [method_description(method.name), ""]
    for p in method.params:
        lines.append(f"@param {p.name} {param_desc(p.name, method)}")
    ret = method.return_type.strip()
    if ret and ret != "void":
        fr = format_return(ret)
        if fr != "nothing":
            lines.append(f"@return {fr}")
    if method.throws:
        for t in method.throws:
            lines.append(f"@throws {t} if {humanize(t)} is thrown")
    return format_javadoc(method.indent or "    ", lines)


def strip_bogus_tags(doc: str, indent: str = "    ", method: MethodInfo | None = None) -> str:
    out = []
    for raw in doc.split("\n"):
        s = raw.strip()
        if s.startswith("* "):
            s = s[2:]
        elif s == "*":
            s = ""
        elif s in ("/**", "*/"):
            continue
        if s.startswith("@throws Exception if an unexpected error"):
            continue
        if s.startswith("@return nothing"):
            continue
        if s.startswith("@param ") and method:
            m = re.match(r"@param\s+(\w+)\s+(.*)", s)
            if m:
                pn, pd = m.group(1), m.group(2).strip()
                pd = re.sub(r"\s*\(\{@link [^}]+\}\)\.?\s*$", "", pd)
                if not pd or pd == pn or pd in ("do validate", "ack ts", "clear ts"):
                    pd = param_desc(pn, method)
                s = f"@param {pn} {pd}"
        out.append(s)
    while out and out[-1] == "":
        out.pop()
    indent = (method.indent if method else None) or indent
    return format_javadoc(indent, out)


def dao_class_doc(name: str, kind: str) -> list[str]:
    if name in DAO_ENTITIES and kind == "interface":
        e = DAO_ENTITIES[name]
        lines = [
            f"Persistence API for {{@link {e}}} entities.",
            "",
            "<p>Implemented in the {@code dao} module; consumed by application services and rule engine.",
        ]
        if name == "DeviceService":
            lines[0] = "Persistence API for {@link Device} entities (credentials via {@link DeviceCredentialsService})."
            lines.append("<p>REST: {@code DeviceController} ({@code /api/device*}).")
        return lines
    if name.endswith("Service") and kind == "interface":
        return [
            f"DAO service API for {humanize(name[:-7])} persistence and queries.",
            "",
            "<p>Implemented in the {@code dao} module.",
        ]
    if name.endswith("Exception"):
        return [f"Raised when {humanize(name)} occurs during DAO operations."]
    if kind == "interface":
        return [f"{humanize(name)} contract for the DAO layer."]
    if kind == "enum":
        return [f"Enumerates {humanize(name)} values used by DAO configuration."]
    return [f"{humanize(name).capitalize()}."]


def message_class_doc(name: str, kind: str) -> list[str]:
    if name in MESSAGE_DOCS:
        return MESSAGE_DOCS[name]
    if name.endswith("Msg") or name.endswith("Message"):
        words = humanize(name)
        if kind == "interface":
            return [f"Message contract: {words}."]
        return [
            f"Internal actor or queue message: {words}.",
            "",
            "<p>Serialized between tb-core, rule-engine, transport, and edge services.",
        ]
    if name.endswith("Exception"):
        return [f"Exception in message/actor pipeline: {humanize(name)}."]
    if kind == "interface":
        return [f"{humanize(name)} message contract."]
    if kind == "enum":
        return [f"Enumerates {humanize(name)} values for message routing or payloads."]
    return [f"{humanize(name).capitalize()}."]


def fix_class_doc(body: str, name: str, kind: str, path: Path) -> tuple[str, bool]:
    m = DECL_LINE_RE.search(body)
    if not m:
        return body, False
    mod = "dao-api" if "dao-api" in path.as_posix() else "message"
    lines = dao_class_doc(name, kind) if mod == "dao-api" else message_class_doc(name, kind)
    new_doc = format_javadoc(m.group("indent"), lines)
    pos = find_insert_position(body, m.start())
    existing = javadoc_block_before(body, pos)
    if existing:
        start, end = existing
        old = body[start:end]
        if is_stub(old):
            body = body[:start] + new_doc + body[end:]
            return body, True
        return body, False
    return insert_text_at(body, pos, new_doc), True


def fix_class_javadoc_indent(body: str) -> str:
    """Remove erroneous 4-space indent on type-level Javadoc before declarations."""
    return re.sub(
        r"(\n)    (/\*\*[\s\S]*?\*/\s*\n)(\s*(?:public|protected|private)\s+(?:class|interface|enum|record)\s+)",
        r"\1\2\3",
        body,
        count=1,
    )


def add_missing_method_docs(body: str, class_name: str, kind: str) -> tuple[str, int]:
    count = 0
    class_start, _ = class_body_bounds(body, class_name)
    while True:
        added = False
        for method in reversed(find_methods(body, class_name, kind)):
            if method.sig_start < class_start:
                continue
            if has_method_javadoc(body, method.sig_start):
                continue
            if kind != "interface" and not re.search(
                r"\bpublic\b", body[max(class_start, method.sig_start - 80) : method.sig_start]
            ):
                continue
            line_start = method_line_start(body, method.sig_start)
            body = insert_text_at(body, line_start, build_method_doc(method))
            count += 1
            added = True
            break
        if not added:
            break
    return body, count


METHOD_DOC_AFTER_SIG_RE = re.compile(
    r"(?P<indent>^[ \t]{4})(?P<sig>[\w<>,\s\[\]?]+\s+\w+\s*\([^)]*\)\s*;\s*)\n"
    r"(?P<doc>/\*\*[\s\S]*?\*/\s*)\n(?=\s*\n|\s*(?:@|\w))",
    re.MULTILINE,
)


def fix_misplaced_interface_method_docs(body: str) -> str:
    """Move method Javadoc from after a signature to before it (interface bodies only)."""

    def repl(m: re.Match) -> str:
        indent = m.group("indent")
        sig = m.group("sig").rstrip()
        doc = m.group("doc")
        norm = []
        for dl in doc.split("\n"):
            s = dl.strip()
            if s == "/**":
                norm.append(f"{indent}/**")
            elif s == "*/":
                norm.append(f"{indent} */")
            elif s.startswith("*"):
                norm.append(f"{indent}{s}")
            else:
                norm.append(dl)
        return "\n".join(norm) + "\n\n" + indent + sig + "\n"

    return METHOD_DOC_AFTER_SIG_RE.sub(repl, body)


def remove_orphan_stubs(content: str) -> str:
    def repl(m: re.Match) -> str:
        return m.group(2) if is_stub(m.group(1)) else m.group(0)

    return re.sub(
        r"(^[ \t]*/\*\*[\s\S]*?\*/\s*\n)(^[ \t]*(?:@\w+|public|protected|private|class|interface|enum|record|\}))",
        repl,
        content,
        flags=re.MULTILINE,
    )


def normalize_javadoc_spacing(body: str) -> str:
    """Collapse extra blank lines inside Javadoc blocks."""
    prev = None
    while prev != body:
        prev = body
        body = re.sub(r"\n\n( \*)", r"\n\1", body)
    return body


def process_file(path: Path) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    original = body

    body = dedupe_consecutive_javadocs(body)
    body = remove_orphan_stubs(body)

    m = DECL_LINE_RE.search(body)
    if not m:
        if body != original:
            path.write_text(license + body, encoding="utf-8", newline="\n")
        return body != original

    name, kind = m.group("name"), detect_kind(m.group(0))
    changed = body != original
    body, c1 = fix_class_doc(body, name, kind, path)
    changed = changed or c1
    body, c2 = add_missing_method_docs(body, name, kind)
    changed = changed or c2 > 0

    body2 = fix_class_javadoc_indent(body)
    if body2 != body:
        body = body2
        changed = True

    body3 = normalize_javadoc_spacing(body)
    if body3 != body:
        body = body3
        changed = True

    if changed:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("roots", nargs="+", type=Path)
    args = parser.parse_args()
    count = 0
    for root in args.roots:
        for path in sorted(root.rglob("*.java")):
            if "src/main/java" not in path.as_posix() or path.name == "package-info.java":
                continue
            if process_file(path):
                count += 1
    print(f"Modified {count} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
