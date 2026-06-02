#!/usr/bin/env python3
"""
Add brief JSDoc to public methods in ui-ngx *service.ts that call /api/ URLs.
Skips methods that already have a JSDoc block immediately above.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

METHOD_RE = re.compile(
    r"^(\s*)(public\s+)(\w+)\([^)]*\)\s*:\s*Observable[^{]*\{",
    re.MULTILINE,
)
API_IN_METHOD = re.compile(r"[`'\"](/api/[^`'\"]+)[`'\"]")


def split_license(content: str) -> tuple[str, str]:
    m = re.match(r"(///[\s\S]*?\n\n)([\s\S]*)", content)
    return (m.group(1), m.group(2)) if m else ("", content)


def has_jsdoc_before(body: str, pos: int) -> bool:
    before = body[:pos].rstrip()
    return before.endswith("*/") or before.endswith("///")


def process_file(path: Path, dry_run: bool) -> int:
    if not path.name.endswith(".service.ts"):
        return 0
    content = path.read_text(encoding="utf-8")
    license, body = split_license(content)
    count = 0
    offset = 0
    parts = [body]
    # process in reverse to preserve positions
    matches = list(METHOD_RE.finditer(body))
    inserts: list[tuple[int, str]] = []
    for m in reversed(matches):
        if has_jsdoc_before(body, m.start()):
            continue
        method_start = m.start()
        # search method body until next method at same indent (approx 4000 chars)
        chunk = body[method_start : method_start + 8000]
        apis = API_IN_METHOD.findall(chunk)
        if not apis:
            continue
        api = apis[0]
        if len(apis) > 1:
            api = apis[0] + ", ..."
        indent = m.group(1)
        doc = f"{indent}/** Calls ThingsBoard REST `{api}`. */\n"
        inserts.append((method_start, doc))
        count += 1
    if not inserts:
        return 0
    for pos, doc in inserts:
        body = body[:pos] + doc + body[pos:]
    if not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return count


def main() -> int:
    p = argparse.ArgumentParser()
    p.add_argument("root", nargs="?", default="ui-ngx/src/app/core/http")
    p.add_argument("--dry-run", action="store_true")
    args = p.parse_args()
    total = 0
    for path in sorted(Path(args.root).rglob("*.service.ts")):
        n = process_file(path, args.dry_run)
        if n:
            print(f"{path}: {n}")
            total += n
    print(f"Documented {total} methods", file=sys.stderr)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
