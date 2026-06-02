#!/usr/bin/env python3
"""Move class Javadoc that was incorrectly placed after annotations to before them."""
import re
import sys
from pathlib import Path

PATTERN = re.compile(
    r"((?:^[ \t]*@\w+[\s\S]*?\n)+)(^[ \t]*/\*\*[\s\S]*?\*/\s*\n)(^[ \t]*(?:public|protected|private|sealed|non-sealed|abstract|final|\s)+(?:class|interface|enum|record)\s+)",
    re.MULTILINE,
)


def fix_content(content: str) -> tuple[str, int]:
    count = 0

    def repl(m: re.Match) -> str:
        nonlocal count
        count += 1
        return m.group(2) + m.group(1) + m.group(3)

    fixed, n = PATTERN.subn(repl, content)
    return fixed, n


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("common")
    total_files = 0
    total_moves = 0
    for path in sorted(root.rglob("*.java")):
        text = path.read_text(encoding="utf-8")
        fixed, n = fix_content(text)
        if n:
            path.write_text(fixed, encoding="utf-8", newline="\n")
            total_files += 1
            total_moves += n
            print(f"{path}: {n}")
    print(f"Fixed {total_moves} blocks in {total_files} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
