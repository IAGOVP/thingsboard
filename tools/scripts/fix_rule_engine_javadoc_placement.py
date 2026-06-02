#!/usr/bin/env python3
"""Move class Javadoc mistakenly inserted inside @RuleNode annotation to before @RuleNode."""
import re
import sys
from pathlib import Path

PATTERN = re.compile(
    r"(@RuleNode\s*\()(.*?)(/\*\*[\s\S]*?\*/)\s*(\)\s*\n(\s*public\s+(?:class|abstract\s+class)))",
    re.DOTALL,
)


def fix_content(content: str) -> tuple[str, int]:
    count = 0

    def repl(m: re.Match) -> str:
        nonlocal count
        count += 1
        return m.group(3) + "\n" + m.group(1) + m.group(2) + m.group(4) + "\n" + m.group(5)

    fixed = PATTERN.sub(repl, content)
    return fixed, count


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("rule-engine")
    total = 0
    for path in sorted(root.rglob("*.java")):
        text = path.read_text(encoding="utf-8")
        fixed, n = fix_content(text)
        if n:
            path.write_text(fixed, encoding="utf-8", newline="\n")
            print(f"{path}: {n}")
            total += n
    print(f"Fixed {total} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
