#!/usr/bin/env python3
"""
Add class-level Javadoc to Java types that lack documentation after the license header.
Inserts immediately before annotations (if any) or before the type declaration.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

DECL_LINE_RE = re.compile(
    r"^(?P<indent>\s*)(?:(?:public|protected|private)\s+)?"
    r"(?:(?:abstract|sealed|non-sealed|static|final)\s+)*"
    r"(?:class|interface|enum|record)\s+"
    r"(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)


def split_license_and_body(content: str) -> tuple[str, str]:
    m = re.match(r"(\s*/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    if m:
        return m.group(1), m.group(2)
    return "", content


def has_javadoc_immediately_before(body: str, pos: int) -> bool:
    before = body[:pos].rstrip()
    return before.endswith("*/")


def humanize_name(name: str) -> str:
    s = re.sub(r"([a-z])([A-Z])", r"\1 \2", name)
    return s.replace("_", " ").lower()


def class_description(name: str, kind: str) -> str:
    words = humanize_name(name)
    if name.endswith("Service") and kind == "interface":
        return f"Service API for {humanize_name(name[:-7])} persistence and domain operations."
    if name.endswith("Dao") and kind == "interface":
        return f"DAO contract for {humanize_name(name[:-3])}."
    if kind == "interface":
        return f"{words} contract."
    if kind == "enum":
        return f"{words} values."
    if name.endswith("Exception"):
        return f"Exception: {words}."
    if name.endswith("Id"):
        return f"Typed identifier for {humanize_name(name[:-2])}."
    if name.endswith("Config") or name.endswith("Configuration"):
        return f"Configuration for {words.replace(' config', '').replace(' configuration', '')}."
    if name.endswith("Impl"):
        return f"Default implementation of {humanize_name(name[:-4])}."
    if name.endswith("Msg") or name.endswith("Message"):
        return f"Message: {words}."
    if name.endswith("Processor"):
        return f"Processes {humanize_name(name[:-9])}."
    if name.endswith("Handler"):
        return f"Handles {humanize_name(name[:-7])}."
    if name.endswith("Factory"):
        return f"Factory for {humanize_name(name[:-7])}."
    if kind == "class":
        return f"{words.capitalize()}."
    return f"{words}."


def indent_javadoc(indent: str, lines: list[str]) -> str:
    inner = "\n".join(f"{indent} * {line}" if line else f"{indent} *" for line in lines)
    return f"{indent}/**\n{inner}\n{indent} */\n"


def find_insert_position(body: str, decl_line_start: int) -> int:
    """Insert point: before consecutive annotations immediately above the declaration line."""
    line_start = body.rfind("\n", 0, decl_line_start) + 1
    pos = line_start
    while pos > 0:
        prev_nl = body.rfind("\n", 0, pos - 1)
        prev_line_start = prev_nl + 1
        prev_line = body[prev_line_start:pos].strip()
        if not prev_line:
            pos = prev_line_start
            continue
        if prev_line.startswith("@") or prev_line.endswith(")"):
            pos = prev_line_start
            continue
        break
    return pos


def detect_kind(decl_line: str) -> str:
    for k in ("class", "interface", "enum", "record"):
        if re.search(rf"\b{k}\b", decl_line):
            return k
    return "class"


def process_file(path: Path, dry_run: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)

    m = DECL_LINE_RE.search(body)
    if not m:
        return False

    decl_line_start = m.start()
    if has_javadoc_immediately_before(body, find_insert_position(body, decl_line_start)):
        return False

    name = m.group("name")
    kind = detect_kind(m.group(0))
    indent = m.group("indent")
    insert_at = find_insert_position(body, decl_line_start)
    javadoc = indent_javadoc(indent, [class_description(name, kind)])
    body = body[:insert_at] + javadoc + body[insert_at:]

    if not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return True


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("root", type=Path)
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--exclude-test", action="store_true", default=True)
    parser.add_argument("--skip-path", action="append", default=[])
    args = parser.parse_args()

    count = 0
    for path in sorted(args.root.rglob("*.java")):
        posix = path.as_posix()
        if args.skip_path and any(s in posix for s in args.skip_path):
            continue
        if path.name == "package-info.java":
            continue
        if args.exclude_test and ("/test/" in posix or "\\test\\" in posix):
            continue
        if process_file(path, args.dry_run):
            count += 1
    print(f"Updated {count} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
