#!/usr/bin/env python3
"""Add class-level Javadoc to rule-engine Java sources."""
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

RULE_NODE_RE = re.compile(
    r"@RuleNode\s*\(\s*type\s*=\s*ComponentType\.(\w+),\s*name\s*=\s*\"([^\"]+)\"[^)]*"
    r"nodeDescription\s*=\s*\"([^\"]+)\"",
    re.DOTALL,
)


def humanize(name: str) -> str:
    return re.sub(r"([a-z])([A-Z])", r"\1 \2", name).replace("_", " ").lower()


def class_doc(name: str, kind: str, file_text: str) -> str:
    m = RULE_NODE_RE.search(file_text)
    if m and name.startswith("Tb") and "Node" in name:
        ctype, rname, desc = m.group(1), m.group(2), m.group(3)
        return (
            f"Rule engine {ctype.lower()} node '{rname}': {desc} "
            "Implements org.thingsboard.rule.engine.api.TbNode."
        )
    if name.endswith("NodeConfiguration") or name.endswith("Configuration"):
        base = name.replace("Configuration", "").replace("Node", "")
        return f"JSON configuration for {base} rule node."
    if name.startswith("TbAbstract"):
        return f"Base implementation for {humanize(name[10:])} rule nodes."
    api_docs = {
        "TbNode": "Contract for a rule chain node: init, process {@link org.thingsboard.server.common.msg.TbMsg}, optional destroy.",
        "TbContext": "Execution context: message routing (tellSuccess/tellNext), DAO services, script engine, and cluster APIs.",
        "RuleNode": "Runtime annotation registering a rule node plugin in the UI and component registry.",
        "NodeConfiguration": "Marker for deserializable rule node configuration POJOs.",
        "TbNodeConfiguration": "Wrapper holding rule node configuration as {@link com.fasterxml.jackson.databind.JsonNode}.",
        "TbNodeException": "Checked exception for rule node init/processing failures.",
        "TbNodeState": "Persisted per-node state in rule engine.",
        "ScriptEngine": "TBEL/JS script execution for filter/transform nodes.",
        "RuleEngineTelemetryService": "Saves telemetry and attributes from rule nodes.",
        "RuleEngineAlarmService": "Creates/clears alarms from rule nodes.",
        "RuleEngineRpcService": "Device RPC from rule nodes.",
        "MailService": "Sends email from rule nodes.",
        "SmsService": "Sends SMS from rule nodes.",
        "NotificationCenter": "Dispatches notification rules from rule engine.",
        "JobManager": "Schedules background jobs from rule nodes.",
        "DeviceStateManager": "Updates device active/inactive state.",
        "TbNodeUtils": "Configuration conversion and JSON helpers for rule nodes.",
    }
    if name in api_docs:
        return api_docs[name]
    if name.endswith("Service"):
        return f"Rule engine facade for {humanize(name.replace('Service', ''))} operations."
    if name.endswith("Request"):
        return f"Request DTO for rule engine {humanize(name.replace('Request', ''))}."
    if name.endswith("Test"):
        return f"Unit test for {humanize(name.replace('Test', ''))} rule node."
    if kind == "interface":
        return f"Rule engine {humanize(name)} API."
    return f"Rule engine component: {humanize(name)}."


def indent_javadoc(indent: str, text: str) -> str:
    return f"{indent}/**\n{indent} * {text}\n{indent} */\n"


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


def has_class_javadoc(body: str, pos: int) -> bool:
    before = body[:pos].rstrip()
    if not before.endswith("*/"):
        return False
    # skip if only license block
    return "Rule engine" in before[-500:] or "rule node" in before[-500:].lower() or "Contract for" in before[-500:]


def process_file(path: Path, dry_run: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    m = re.match(r"(\s*/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    license_, body = (m.group(1), m.group(2)) if m else ("", content)
    dm = DECL_RE.search(body)
    if not dm:
        return False
    insert_at = find_insert_position(body, dm.start())
    if has_class_javadoc(body, insert_at):
        return False
    doc = class_doc(dm.group("name"), detect_kind(dm.group(0)), body)
    body = body[:insert_at] + indent_javadoc(dm.group("indent"), doc) + body[insert_at:]
    if not dry_run:
        path.write_text(license_ + body, encoding="utf-8", newline="\n")
    return True


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("rule-engine")
    dry = "--dry-run" in sys.argv
    n = 0
    for path in sorted(root.rglob("*.java")):
        if process_file(path, dry):
            n += 1
            print(path)
    print(f"Updated {n} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
