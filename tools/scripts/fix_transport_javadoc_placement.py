#!/usr/bin/env python3
"""Move class Javadoc from after @Slf4j/@RestController/... to before the annotation block."""
import re
import sys
from pathlib import Path

PATTERN = re.compile(
    r"((?:[ \t]*@\w+(?:\([^)]*\))?\s*\n)+)"
    r"(\s*/\*\*[\s\S]*?\*/\s*\n)"
    r"(\s*(?:public|protected|private)?\s*(?:abstract\s+|sealed\s+|non-sealed\s+)?(?:class|interface|enum|record)\s+)",
    re.MULTILINE,
)


def fix_content(content: str) -> tuple[str, int]:
    def repl(m: re.Match) -> str:
        return m.group(2) + m.group(1) + m.group(3)

    return PATTERN.subn(repl, content)


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("common/transport")
    files = 0
    placements = 0
    for path in sorted(root.rglob("*.java")):
        if "/test/" in path.as_posix():
            continue
        text = path.read_text(encoding="utf-8")
        fixed, n = fix_content(text)
        if n:
            path.write_text(fixed, encoding="utf-8", newline="\n")
            print(f"{path}: {n}")
            files += 1
            placements += n
    print(f"Fixed {placements} placement(s) in {files} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
