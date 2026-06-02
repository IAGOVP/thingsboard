#!/usr/bin/env python3
"""Move JSDoc from inside @Injectable({...}) or after @NgModule({ to before decorators."""
import re
import sys
from pathlib import Path

# JSDoc between @Injectable({ ... and closing })
INJECTABLE = re.compile(
    r"(@Injectable\(\{\s*\n(?:[^}]*\n)*?)(\s*/\*\*[\s\S]*?\*/\s*\n)(\}\))",
    re.MULTILINE,
)
# JSDoc after @NgModule({ ... exports without closing on same block - before })
NGMODULE = re.compile(
    r"(@NgModule\(\{[\s\S]*?)(\s*/\*\*[\s\S]*?\*/\s*\n)(\}\))",
)


def fix(content: str) -> tuple[str, int]:
    n = 0

    def inj_repl(m: re.Match) -> str:
        nonlocal n
        n += 1
        return m.group(2) + m.group(1) + m.group(3)

    content = INJECTABLE.sub(inj_repl, content)

    def ng_repl(m: re.Match) -> str:
        nonlocal n
        n += 1
        return m.group(2) + m.group(1) + m.group(3)

    content = NGMODULE.sub(ng_repl, content)
    return content, n


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
