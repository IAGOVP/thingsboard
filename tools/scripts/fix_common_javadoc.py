#!/usr/bin/env python3
"""Clean up Javadoc in common/ after bulk generation: dedupe and fix placement."""
from __future__ import annotations

import re
import sys
from pathlib import Path

ANNOTATION_JAVADOC_RE = re.compile(
    r"((?:^[ \t]*@\w+[\s\S]*?\n)+)(^[ \t]*/\*\*[\s\S]*?\*/\s*\n)(^[ \t]*(?:public|protected|private|sealed|non-sealed|abstract|final|\s)+(?:class|interface|enum|record)\s+)",
    re.MULTILINE,
)

CONSECUTIVE_JAVADOC_RE = re.compile(
    r"(^[ \t]*/\*\*[\s\S]*?\*/\s*\n)(^[ \t]*/\*\*[\s\S]*?\*/\s*\n)",
    re.MULTILINE,
)


def dedupe_consecutive_javadocs(content: str) -> str:
    prev = None
    while prev != content:
        prev = content

        def repl(m: re.Match) -> str:
            a, b = m.group(1), m.group(2)
            return b if len(b) >= len(a) else a

        content = CONSECUTIVE_JAVADOC_RE.sub(repl, content)
    return content


def fix_annotation_placement(content: str) -> str:
    def repl(m: re.Match) -> str:
        return m.group(2) + m.group(1) + m.group(3)

    return ANNOTATION_JAVADOC_RE.sub(repl, content)


def fix_method_javadoc_indent(text: str) -> str:
    """Move method Javadoc glued after semicolons to before the following method."""
    lines = text.split("\n")
    out: list[str] = []
    i = 0
    while i < len(lines):
        line = lines[i]
        if (
            i > 0
            and lines[i - 1].rstrip().endswith(";")
            and line.startswith("/**")
            and not line.startswith("    /**")
        ):
            j = i + 1
            while j < len(lines) and not lines[j].strip().endswith("*/"):
                j += 1
            if j < len(lines):
                j += 1
            indent = "    "
            if j < len(lines):
                nxt = lines[j]
                if nxt.startswith(" ") and not nxt.strip().startswith("*"):
                    indent = nxt[: len(nxt) - len(nxt.lstrip())]
            if out and out[-1].strip():
                out.append("")
            out.append(indent + line)
            i += 1
            while i < len(lines):
                cur = lines[i]
                if cur.strip().endswith("*/"):
                    out.append(indent + cur if not cur.startswith(indent) else cur)
                    i += 1
                    break
                if cur.startswith(" *"):
                    out.append(indent + cur if not cur.startswith(indent) else cur)
                else:
                    out.append(cur)
                i += 1
            continue
        out.append(line)
        i += 1
    return "\n".join(out)


def process_file(path: Path) -> bool:
    text = path.read_text(encoding="utf-8")
    original = text
    text = dedupe_consecutive_javadocs(text)
    text = fix_annotation_placement(text)
    text = fix_method_javadoc_indent(text)
    if text != original:
        path.write_text(text, encoding="utf-8", newline="\n")
        return True
    return False


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("common")
    n = 0
    for p in sorted(root.rglob("*.java")):
        if "src/main/java" not in p.as_posix():
            continue
        if process_file(p):
            n += 1
    print(f"Fixed {n} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
