#!/usr/bin/env python3
"""
Add class and public-method Javadoc to dao/src/main Java sources.
Skips tests; places class Javadoc before annotations.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

# Reuse logic from add_java_javadoc (inline minimal copy)
DECL_LINE_RE = re.compile(
    r"^(?P<indent>\s*)(?:(?:public|protected|private)\s+)?"
    r"(?:(?:abstract|sealed|non-sealed|static|final)\s+)*"
    r"(?:class|interface|enum|record)\s+"
    r"(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)

# Method-level docs are added manually for Base*Service; bulk run is class-level only (performance).


def split_license_and_body(content: str) -> tuple[str, str]:
    m = re.match(r"(\s*/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    return (m.group(1), m.group(2)) if m else ("", content)


def has_javadoc_before(body: str, pos: int) -> bool:
    return body[:pos].rstrip().endswith("*/")


def humanize(name: str) -> str:
    return re.sub(r"([a-z])([A-Z])", r"\1 \2", name).replace("_", " ").lower()


def class_doc(name: str, kind: str) -> str:
    if name.startswith("Base") and name.endswith("Service"):
        domain = humanize(name[4:-7])
        return f"Default DAO-layer service implementation for {domain}."
    if name.startswith("Jpa") and name.endswith("Dao"):
        return f"JPA implementation of {humanize(name[3:])}."
    if name.endswith("ServiceImpl"):
        return f"Spring service implementing {humanize(name[:-11])} API."
    if name.endswith("Dao") and kind == "interface":
        return f"Persistence contract for {humanize(name[:-3])} (see JPA/Cassandra implementations)."
    if name == "Dao":
        return "Generic CRUD contract for a single entity type (tenant-scoped)."
    if kind == "interface":
        return f"{humanize(name)} contract."
    return f"{humanize(name).capitalize()}."


def method_doc(name: str) -> str:
    if name.startswith("find"):
        return f"Loads {humanize(name[4:])}."
    if name.startswith("save"):
        return f"Persists {humanize(name[4:])}."
    if name.startswith("delete") or name.startswith("remove"):
        return f"Removes {humanize(name.replace('delete', '').replace('remove', ''))}."
    if name.startswith("create"):
        return f"Creates {humanize(name[6:])}."
    if name.startswith("update"):
        return f"Updates {humanize(name[6:])}."
    if name.startswith("exists"):
        return f"Checks whether {humanize(name[6:])} exists."
    if name.startswith("count"):
        return f"Counts {humanize(name[5:])}."
    if name == "process":
        return "Processes the request."
    return f"{humanize(name).capitalize()}."


def indent_javadoc(indent: str, lines: list[str]) -> str:
    inner = "\n".join(f"{indent} * {line}" if line else f"{indent} *" for line in lines)
    return f"{indent}/**\n{inner}\n{indent} */\n"


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


def process_file(path: Path, dry_run: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    m = DECL_LINE_RE.search(body)
    if not m:
        return False
    changed = False
    name = m.group("name")
    kind = detect_kind(m.group(0))
    indent = m.group("indent")
    insert_at = find_insert_position(body, m.start())
    if not has_javadoc_before(body, insert_at):
        body = body[:insert_at] + indent_javadoc(indent, [class_doc(name, kind)]) + body[insert_at:]
        changed = True
    if changed and not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("dao/src/main/java")
    dry = "--dry-run" in sys.argv
    n = sum(1 for p in sorted(root.rglob("*.java")) if process_file(p, dry))
    print(f"Updated {n} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
