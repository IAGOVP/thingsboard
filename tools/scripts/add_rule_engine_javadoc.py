#!/usr/bin/env python3
"""
Add class- and public/protected method-level Javadoc to rule-engine/ Java sources.
Documents @RuleNode plugin metadata (type, UI name, relations, docUrl) for component nodes.
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

RULE_ENGINE_ROOTS = [
    Path("rule-engine/rule-engine-api/src/main/java"),
    Path("rule-engine/rule-engine-components/src/main/java"),
    Path("rule-engine/rule-engine-components/src/test/java"),
    Path("rule-engine/rule-engine-api/src/test/java"),
]

RULE_NODE_RE = re.compile(
    r"@RuleNode\s*\(\s*"
    r"type\s*=\s*ComponentType\.(\w+)\s*,\s*"
    r"name\s*=\s*\"([^\"]+)\""
    r"(.*?)\)\s*\n",
    re.DOTALL,
)

RULE_ENGINE_PACKAGES: dict[str, str] = {
    "filter": "message filtering and branching rule nodes",
    "action": "entity lifecycle, alarm, and side-effect rule nodes",
    "transform": "message transformation and originator change nodes",
    "telemetry": "telemetry and attribute persistence nodes",
    "metadata": "entity metadata and related-data fetch nodes",
    "flow": "rule chain flow control (input, output, ack, checkpoint)",
    "rpc": "device RPC request/reply nodes",
    "rest": "outbound REST API call nodes",
    "mail": "email notification nodes",
    "sms": "SMS notification nodes",
    "mqtt": "MQTT publish/subscribe nodes",
    "kafka": "Apache Kafka producer/consumer nodes",
    "rabbitmq": "RabbitMQ publish nodes",
    "aws": "AWS integration nodes (SQS, SNS, Lambda)",
    "gcp": "Google Cloud integration nodes",
    "edge": "ThingsBoard Edge synchronization nodes",
    "geo": "GPS geofencing filter and action nodes",
    "math": "expression evaluation nodes",
    "ai": "AI/LLM integration nodes",
    "debug": "debug and test message generator nodes",
    "delay": "message delay nodes",
    "deduplication": "message deduplication nodes",
    "transaction": "message synchronization transaction nodes",
    "notification": "notification center and Slack nodes",
    "profile": "device profile state nodes",
    "util": "shared rule-engine utilities and async loaders",
    "data": "rule node configuration DTOs and query objects",
    "credentials": "credentials helper types",
    "api": "rule engine public API contracts and services",
}

RULE_ENGINE_API_CLASS_DOCS: dict[str, list[str]] = {
    "TbNode": [
        "Contract for a rule chain node executed by the rule engine.",
        "",
        "<p>Lifecycle: {@link #init(TbContext, TbNodeConfiguration)} → repeated {@link #onMsg(TbContext, TbMsg)} "
        "→ optional {@link #destroy()}. Implementations are registered with {@link RuleNode}.",
    ],
    "TbContext": [
        "Execution context passed to every rule node invocation.",
        "",
        "<p>Provides message routing ({@link #tellSuccess}, {@link #tellNext}, {@link #tellFailure}), "
        "DAO services, script engine, cluster APIs, and tenant-scoped helpers.",
    ],
    "RuleNode": [
        "Runtime annotation that registers a rule node plugin in the UI component library.",
        "",
        "<p>Defines type, display name, configuration class, output relation types, and documentation URL.",
    ],
    "NodeConfiguration": [
        "Marker interface for deserializable rule node configuration POJOs.",
    ],
    "TbNodeConfiguration": [
        "Wrapper holding rule node configuration as {@link com.fasterxml.jackson.databind.JsonNode}.",
    ],
    "TbNodeException": [
        "Checked exception for rule node initialization or processing failures.",
    ],
    "ScriptEngine": [
        "TBEL/JavaScript script engine used by filter and transform nodes.",
    ],
    "RuleEngineTelemetryService": [
        "Facade for saving telemetry and attributes from rule nodes.",
    ],
    "RuleEngineAlarmService": [
        "Facade for creating, updating, and clearing alarms from rule nodes.",
    ],
    "RuleEngineRpcService": [
        "Facade for sending two-way device RPC from rule nodes.",
    ],
    "MailService": [
        "Facade for sending email from rule engine mail nodes.",
    ],
    "SmsService": [
        "Facade for sending SMS from rule engine SMS nodes.",
    ],
    "NotificationCenter": [
        "Dispatches notification rules triggered from the rule engine.",
    ],
    "JobManager": [
        "Schedules and manages background jobs initiated from rule nodes.",
    ],
    "DeviceStateManager": [
        "Updates device active/inactive connectivity state from rule nodes.",
    ],
    "TbNodeUtils": [
        "Configuration conversion and JSON helper utilities for rule nodes.",
    ],
}

RULE_ENGINE_METHOD_DOCS: dict[str, str] = {
    "init": "Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).",
    "onMsg": "Processes one incoming {@link org.thingsboard.server.common.msg.TbMsg} and routes the result via {@link TbContext}.",
    "destroy": "Releases resources held by the node (script engines, clients, thread pools).",
    "onPartitionChangeMsg": "Cluster hook invoked on Kafka partition reassignment for this tenant/queue.",
    "upgrade": "Upgrades persisted node configuration from an older {@link RuleNode#version()} to the current schema.",
    "tellSuccess": "Routes the message to the Success connection of the current node.",
    "tellNext": "Routes the message to a named output relation (True, False, Custom, etc.).",
    "tellFailure": "Routes the message to the Failure connection with an error message.",
    "enqueue": "Enqueues a new message for processing by the rule engine.",
    "getTenantId": "Returns the tenant id for the current rule engine context.",
    "getSelfId": "Returns the rule node id of the currently executing node.",
    "createScriptEngine": "Creates a TBEL or JavaScript script engine for filter/transform nodes.",
}

RULE_ENGINE_PARAM_HINTS: dict[str, str] = {
    "ctx": "rule engine execution context (routing, DAO, cluster APIs)",
    "msg": "incoming or outgoing rule engine message",
    "configuration": "node configuration wrapper ({@link TbNodeConfiguration})",
    "config": "deserialized node configuration POJO",
    "fromVersion": "configuration schema version stored in the database",
    "oldConfiguration": "previous JSON configuration to upgrade",
    "relationType": "output connection name (Success, Failure, True, False, etc.)",
    "request": "async service request DTO",
    "callback": "completion callback for async rule engine operations",
    "tenantId": "tenant UUID",
    "deviceId": "device UUID",
    "originator": "message originator entity id",
    "tbMsg": "rule engine message being processed",
}


def rule_engine_package(path: Path) -> str:
    posix = path.as_posix()
    m = re.search(r"/rule/engine/([^/]+)/", posix)
    if m:
        return m.group(1)
    if "/rule-engine-api/" in posix:
        return "api"
    return "rule-engine"


def parse_rule_node(body: str) -> dict[str, str] | None:
    m = RULE_NODE_RE.search(body)
    if not m:
        return None
    inner = m.group(3)
    desc_m = re.search(r'nodeDescription\s*=\s*"([^"]*)"', inner)
    details_m = re.search(r"nodeDetails\s*=\s*(\"[^\"]*\"|[^,]+)", inner, re.DOTALL)
    doc_m = re.search(r'docUrl\s*=\s*"([^"]*)"', inner)
    rel_m = re.search(r"relationTypes\s*=\s*\{([^}]+)\}", inner)
    cfg_m = re.search(r"configClazz\s*=\s*(\w+\.class|\w+)", inner)
    result = {
        "type": m.group(1),
        "name": m.group(2),
        "nodeDescription": desc_m.group(1) if desc_m else "",
        "nodeDetails": "",
        "docUrl": doc_m.group(1) if doc_m else "",
        "relationTypes": "",
        "configClazz": "",
    }
    if details_m:
        raw = details_m.group(1).strip()
        if raw.startswith('"'):
            result["nodeDetails"] = raw.strip('"').replace("<br>", " ").replace("<b>", "").replace("</b>", "")
        else:
            result["nodeDetails"] = raw[:200]
    if rel_m:
        result["relationTypes"] = rel_m.group(1).replace("\n", " ").strip()
    if cfg_m:
        result["configClazz"] = cfg_m.group(1).replace(".class", "")
    return result


def rule_node_class_description(name: str, meta: dict[str, str]) -> list[str]:
    lines = [
        f"{meta['type'].lower().capitalize()} rule node — <b>{meta['name']}</b>.",
        "",
        f"<p>{meta['nodeDescription']}",
    ]
    if meta.get("nodeDetails"):
        lines.append(f"<br>{meta['nodeDetails']}")
    lines.append("")
    if meta.get("configClazz"):
        cfg = meta["configClazz"]
        lines.append(
            f"<p>Implements {{@link org.thingsboard.rule.engine.api.TbNode}}. "
            f"Configuration: {{@link {cfg}}}."
        )
    else:
        lines.append("<p>Implements {@link org.thingsboard.rule.engine.api.TbNode}.")
    if meta.get("relationTypes"):
        rel = meta["relationTypes"]
        lines.append(f"<br>Output relations: {{@code {rel}}}.")
    if meta.get("docUrl"):
        url = meta["docUrl"]
        lines.append(f"<br>Documentation: <a href=\"{url}\">{url}</a>")
    return lines


def class_description(name: str, kind: str, pkg: str, body: str, path: Path) -> list[str]:
    if name in RULE_ENGINE_API_CLASS_DOCS:
        return RULE_ENGINE_API_CLASS_DOCS[name]
    meta = parse_rule_node(body)
    if meta and name.startswith("Tb") and ("Node" in name or name.endswith("Node")):
        return rule_node_class_description(name, meta)
    ctx = RULE_ENGINE_PACKAGES.get(pkg, "ThingsBoard rule engine")
    if name.endswith("NodeConfiguration") or (name.endswith("Configuration") and "Node" in name):
        base = name.replace("Configuration", "").replace("Node", "")
        return [
            f"JSON configuration POJO for {{@link {base}}} rule node.",
            "",
            "<p>Deserialized from {@link TbNodeConfiguration} in {@link TbNode#init(TbContext, TbNodeConfiguration)}.",
        ]
    if name.startswith("TbAbstract") or name.startswith("AbstractTb"):
        return [
            f"Abstract base class for {humanize(name.replace('TbAbstract', '').replace('AbstractTb', ''))} rule nodes ({ctx}).",
        ]
    if name.endswith("Test"):
        return [f"Unit test for {humanize(name.replace('Test', ''))} ({ctx})."]
    if name.endswith("Request"):
        return [f"Async request DTO for rule engine {humanize(name.replace('Request', ''))} ({ctx})."]
    if name.endswith("Service") and kind == "interface":
        return [f"Rule engine service facade for {humanize(name.replace('Service', ''))} ({ctx})."]
    if kind == "interface":
        return [f"{humanize(name)} contract ({ctx})."]
    if kind == "enum":
        return [f"Enumerates {humanize(name)} values used by rule engine {ctx}."]
    if kind == "record":
        return [f"Immutable record for {humanize(name)} ({ctx})."]
    return [f"{humanize(name).capitalize()} ({ctx})."]


def class_javadoc_is_incomplete(doc: str, class_name: str) -> bool:
    if class_name in RULE_ENGINE_API_CLASS_DOCS and len(doc) < 100:
        return True
    thin = (
        "Rule engine component:",
        "Implements org.thingsboard.rule.engine.api.TbNode.",
        "Filter incoming messages",
        "contract (",
    )
    if any(t in doc for t in thin) and "<p>" not in doc and len(doc) < 180:
        return True
    if "Rule engine filter node" in doc and "Output relations" not in doc and "Documentation" not in doc:
        return True
    if len(doc.strip()) < 70:
        return True
    return False


def add_class_javadoc_at(
    body: str, decl_match: re.Match, class_name: str, kind: str, pkg: str, path: Path, force: bool
) -> tuple[str, bool]:
    insert_pos = find_insert_position(body, decl_match.start())
    new_doc = format_javadoc(decl_match.group("indent"), class_description(class_name, kind, pkg, body, path))
    existing = javadoc_block_before(body, insert_pos)
    if existing:
        start, end = existing
        if not force and not class_javadoc_is_incomplete(body[start:end], class_name):
            return body, False
        body = body[:start] + body[end:]
        insert_pos = start
    return insert_text_at(body, insert_pos, new_doc), True


def add_all_class_javadocs(body: str, pkg: str, path: Path, force: bool) -> tuple[str, int]:
    count = 0
    for m in reversed(list(DECL_LINE_RE.finditer(body))):
        name = m.group("name")
        kind = detect_kind(m.group(0))
        body, changed = add_class_javadoc_at(body, m, name, kind, pkg, path, force)
        if changed:
            count += 1
    return body, count


def rule_engine_param_description(name: str, type_hint: str) -> str:
    if name in RULE_ENGINE_PARAM_HINTS:
        return RULE_ENGINE_PARAM_HINTS[name]
    return param_description(name, type_hint)


def strip_all_javadocs_before(body: str, pos: int) -> tuple[str, int]:
    while True:
        existing = javadoc_block_before(body, pos)
        if not existing:
            break
        start, end = existing
        body = body[:start] + body[end:]
        pos = start
    return body, pos


def method_javadoc_is_thin(doc: str, method) -> bool:
    is_void = method.return_type.strip() == "void"
    if not is_void and "@return" not in doc:
        if method.name not in RULE_ENGINE_METHOD_DOCS or method.name in ("init", "onMsg", "destroy", "onPartitionChangeMsg"):
            pass
        elif len(doc) < 120:
            return True
    if method.name in RULE_ENGINE_METHOD_DOCS and len(doc) < 90:
        return True
    if "Rule engine component:" in doc:
        return True
    if "@param" in doc and "@return" in doc and len(doc) > 110:
        return False
    if is_void and "@param" in doc and len(doc) > 100:
        return False
    if len(doc.strip()) < 75:
        return True
    return False


def rule_engine_method_javadoc(body: str, method) -> str:
    raw = method_line_indent(body, method.sig_start) or method.indent or "    "
    indent = "    " if len(raw.replace("\t", "    ")) > 4 else raw
    desc = RULE_ENGINE_METHOD_DOCS.get(method.name, method_description(method.name))
    lines = [desc, ""]
    for p in method.params:
        lines.append(f"@param {p.name} {rule_engine_param_description(p.name, p.type_hint)}")
    if method.return_type.strip() != "void":
        lines.append(f"@return {format_return(method.return_type)}")
    if method.throws:
        for t in method.throws:
            lines.append(f"@throws {t} {throws_description(t)}")
    elif method.name in ("init", "onMsg", "upgrade"):
        lines.append(f"@throws TbNodeException if configuration or processing fails")
    elif method.name not in ("destroy", "onPartitionChangeMsg", "main"):
        lines.append("@throws Exception if an unexpected error occurs during processing")
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


def add_rule_engine_method_javadocs(body: str, decl_match, class_name: str, kind: str) -> tuple[str, int]:
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
                javadoc_is_complete(doc, method)
                and not method_javadoc_needs_fixup(body, method.doc_start, method.sig_start)
                and not method_javadoc_is_thin(doc, method)
            ):
                continue
            # Preserve detailed TbNode interface defaults
            if class_name == "TbNode" and len(doc) > 80 and "TbContext" in doc:
                continue
        to_update.append(method)
    count = 0
    for method in reversed(to_update):
        pos = max(method.doc_start, class_start + 1)
        body, pos = strip_all_javadocs_before(body, pos)
        new_doc = rule_engine_method_javadoc(body, method)
        body = dao_insert_text_at(body, pos, new_doc)
        count += 1
    return body, count


def process_file(path: Path, dry_run: bool, force_class: bool) -> bool:
    if path.name == "package-info.java":
        return False
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    if not DECL_LINE_RE.search(body):
        return False
    pkg = rule_engine_package(path)
    changed = False
    body, cc = add_all_class_javadocs(body, pkg, path, force_class)
    if cc:
        changed = True
    for decl_match in DECL_LINE_RE.finditer(body):
        class_name = decl_match.group("name")
        kind = detect_kind(decl_match.group(0))
        body, mc = add_rule_engine_method_javadocs(body, decl_match, class_name, kind)
        if mc:
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
    parser = argparse.ArgumentParser(description="Add detailed Javadoc to rule-engine sources")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--force-class", action="store_true", help="Replace thin class Javadoc")
    args = parser.parse_args()
    main_n = test_n = 0
    for path in collect_files(RULE_ENGINE_ROOTS):
        if process_file(path, args.dry_run, args.force_class):
            if "/test/" in path.as_posix():
                test_n += 1
            else:
                main_n += 1
    print(f"main: {main_n} files modified", file=sys.stderr)
    print(f"test: {test_n} files modified", file=sys.stderr)
    print(f"Done: {main_n + test_n} files modified", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
