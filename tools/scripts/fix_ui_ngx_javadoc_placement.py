#!/usr/bin/env python3
"""Move JSDoc from inside @Component/@Injectable/@NgModule/@Directive blocks to before decorators."""
import re
import sys
from pathlib import Path

DECORATOR_BLOCK = re.compile(
    r"(@(?:Component|Injectable|NgModule|Directive|Pipe)\(\{[\s\S]*?)"
    r"(\s*/\*\*[\s\S]*?\*/\s*\n)"
    r"(\}\))",
)


def fix(content: str) -> tuple[str, int]:
    n = 0

    def repl(m: re.Match) -> str:
        nonlocal n
        n += 1
        return m.group(2) + m.group(1) + m.group(3)

    return DECORATOR_BLOCK.sub(repl, content), n


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("ui-ngx/src/app")
    total = 0
    for path in sorted(root.rglob("*.ts")):
        if ".spec.ts" in path.name:
            continue
        text = path.read_text(encoding="utf-8")
        fixed, n = fix(text)
        if n:
            path.write_text(fixed, encoding="utf-8", newline="\n")
            total += n
    print(f"Fixed {total} blocks", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
