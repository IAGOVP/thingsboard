#!/usr/bin/env python3
"""
Add class-level Javadoc to tools module Java sources (migrator, i18n, MqttSslClient).
Skips types that already have Javadoc immediately before the declaration.
"""
from __future__ import annotations

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from add_java_javadoc import process_file  # noqa: E402

TOOLS_JAVA = Path(__file__).resolve().parents[1] / "src" / "main" / "java"


def main() -> int:
    parser = argparse.ArgumentParser(description="Add class Javadoc under tools/src/main/java")
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()
    if not TOOLS_JAVA.is_dir():
        print(f"Not found: {TOOLS_JAVA}", file=sys.stderr)
        return 1
    count = 0
    for path in sorted(TOOLS_JAVA.rglob("*.java")):
        if path.name == "package-info.java":
            continue
        if process_file(path, args.dry_run):
            count += 1
    print(f"Updated {count} file(s)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
