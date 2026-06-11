#!/usr/bin/env python3
"""Move class Javadoc incorrectly placed between license and package to after imports."""
from __future__ import annotations

import re
import sys
from pathlib import Path

LICENSE_END = re.compile(
    r"(\s*/\*\*[\s\S]*?Licensed under the Apache License[\s\S]*?\*/\s*)"
    r"(\s*/\*\*[\s\S]*?\*/\s*)"
    r"(package\s+[\w.]+;\s*)"
    r"((?:import\s+[\w.*]+;\s*)*)",
)


def fix_content(content: str) -> tuple[str, bool]:
    m = LICENSE_END.match(content)
    if not m:
        return content, False
    license_block, class_doc, package_line, imports = m.groups()
    rest = content[m.end() :]
    # Remove duplicate class doc right after imports if present
    rest = re.sub(r"^\s*/\*\*[\s\S]*?\*/\s*", "", rest, count=1)
    fixed = license_block + package_line + imports
    if imports and not fixed.endswith("\n"):
        fixed += "\n"
    if imports:
        fixed += "\n"
    fixed += class_doc + rest.lstrip("\n")
    return fixed, True


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("common")
    n = 0
    for path in sorted(root.rglob("*.java")):
        if "src/main/java" not in path.as_posix():
            continue
        text = path.read_text(encoding="utf-8")
        fixed, changed = fix_content(text)
        if changed:
            path.write_text(fixed, encoding="utf-8", newline="\n")
            n += 1
    print(f"Fixed {n} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
