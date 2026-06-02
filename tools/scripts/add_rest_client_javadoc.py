#!/usr/bin/env python3
"""Add class and public-method Javadoc to rest-client sources."""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

DECL_RE = re.compile(
    r"^(?P<indent>\s*)(?:(?:public|protected)\s+)"
    r"(?:(?:static|final|synchronized)\s+)*"
    r"(?:<[^>]+>\s+)?"
    r"(?:[\w.<>,\s\[\]]+\s+)+"
    r"(?P<name>[a-z][A-Za-z0-9_]*)\s*\(",
    re.MULTILINE,
)

CLASS_RE = re.compile(
    r"^(?P<indent>\s*)(?:(?:public)\s+)?"
    r"(?:(?:abstract|final)\s+)*"
    r"(?:class|interface|enum)\s+(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)

URL_RE = re.compile(r'baseURL \+ "(/api/[^"]+)"')


def humanize(name: str) -> str:
    return re.sub(r"([a-z])([A-Z])", r"\1 \2", name).replace("_", " ").lower()


def method_doc(name: str, body_slice: str) -> str:
    urls = URL_RE.findall(body_slice[:800])
    endpoint = urls[0] if urls else None
    ep = f" Calls `{endpoint}`." if endpoint else ""
    if name == "login":
        return f"Authenticates with username/password via POST /api/auth/login.{ep}"
    if name == "refreshToken":
        return f"Refreshes JWT using POST /api/auth/token.{ep}"
    if name == "getToken":
        return "Returns the current main JWT used in X-Authorization header."
    if name == "close":
        return "Shuts down the internal executor service."
    if name.startswith("get") and "ById" in name:
        return f"GET entity by id.{ep}"
    if name.startswith("get"):
        return f"GET {humanize(name[3:])}.{ep}"
    if name.startswith("save") or name.startswith("create"):
        return f"POST create or update {humanize(name.replace('save', '').replace('create', ''))}.{ep}"
    if name.startswith("delete"):
        return f"DELETE {humanize(name[6:])}.{ep}"
    if name.startswith("find"):
        return f"POST entity query or search: {humanize(name[4:])}.{ep}"
    if name.startswith("assign"):
        return f"POST assign {humanize(name[6:])}.{ep}"
    if name.startswith("unassign"):
        return f"DELETE unassign {humanize(name[8:])}.{ep}"
    if name.startswith("ack") or name.endswith("Ack"):
        return f"POST acknowledge alarm.{ep}"
    if name.startswith("clear"):
        return f"POST clear alarm.{ep}"
    return f"REST call: {humanize(name)}.{ep}"


def has_javadoc_before(text: str, pos: int) -> bool:
    return text[:pos].rstrip().endswith("*/")


def indent_javadoc(indent: str, line: str) -> str:
    return f"{indent}/**\n{indent} * {line}\n{indent} */\n"


def process_rest_client(path: Path, dry_run: bool) -> int:
    content = path.read_text(encoding="utf-8")
    if "public class RestClient" not in content and not content.strip().startswith("/**"):
        pass
    changed = 0
    # Class javadoc
    m = CLASS_RE.search(content)
    if m and m.group("name") == "RestClient":
        insert = m.start()
        if not has_javadoc_before(content, insert):
            doc = indent_javadoc(
                m.group("indent"),
                "Java client for ThingsBoard REST API (Spring RestTemplate). Mirrors server controllers in application module.",
            )
            content = content[:insert] + doc + content[insert:]
            changed += 1
    # Method javadocs
    offset = 0
    for m in list(DECL_RE.finditer(content)):
        pos = m.start() + offset
        if has_javadoc_before(content, pos):
            continue
        line_start = content.rfind("\n", 0, pos) + 1
        line = content[line_start:pos]
        if " abstract " in line or ";" in line.split("(")[0]:
            continue
        name = m.group("name")
        end = content.find("\n    public ", pos + 1)
        if end == -1:
            end = min(pos + 1200, len(content))
        slice_ = content[pos:end]
        doc = indent_javadoc(m.group("indent"), method_doc(name, slice_))
        content = content[:pos] + doc + content[pos:]
        offset += len(doc)
        changed += 1
    if changed and not dry_run:
        path.write_text(content, encoding="utf-8", newline="\n")
    return changed


def process_converter(path: Path, dry_run: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    changed = False
    m = CLASS_RE.search(content)
    if m and not has_javadoc_before(content, m.start()):
        doc = indent_javadoc(
            m.group("indent"),
            "Converts REST JSON telemetry/attribute payloads into KvEntry domain objects.",
        )
        content = content[: m.start()] + doc + content[m.start() :]
        changed = True
    for m in DECL_RE.finditer(content):
        pos = m.start()
        if has_javadoc_before(content, pos):
            continue
        name = m.group("name")
        docs = {
            "toAttributes": "Maps REST attribute JSON array to List of AttributeKvEntry.",
            "toTimeseries": "Maps REST timeseries JSON map to List of TsKvEntry.",
            "parseValue": "Parses a JSON value node into the appropriate KvEntry data type.",
            "parseNumericValue": "Parses numeric JSON as long or double KvEntry.",
        }
        if name in docs:
            content = content[:pos] + indent_javadoc(m.group("indent"), docs[name]) + content[pos:]
            changed = True
    if changed and not dry_run:
        path.write_text(content, encoding="utf-8", newline="\n")
    return changed


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("rest-client")
    dry = "--dry-run" in sys.argv
    rc = root / "src/main/java/org/thingsboard/rest/client/RestClient.java"
    cv = root / "src/main/java/org/thingsboard/rest/client/utils/RestJsonConverter.java"
    n = process_rest_client(rc, dry)
    print(f"RestClient: {n} blocks")
    if process_converter(cv, dry):
        print("RestJsonConverter: updated")
    return 0


if __name__ == "__main__":
    sys.exit(main())
