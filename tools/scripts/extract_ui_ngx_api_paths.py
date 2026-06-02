#!/usr/bin/env python3
"""Extract unique /api/ URL patterns from ui-ngx TypeScript sources."""
import re
from pathlib import Path

ROOT = Path("ui-ngx/src/app")
# Match `/api/...` template strings (stop at quote or ${)
PAT = re.compile(r"`(/api/[^`$]+)`|['\"](/api/[^'\"$]+)['\"]")

paths: set[str] = set()
for path in ROOT.rglob("*.ts"):
    if path.name.endswith(".spec.ts"):
        continue
    text = path.read_text(encoding="utf-8", errors="replace")
    for m in PAT.finditer(text):
        p = m.group(1) or m.group(2)
        # normalize ${...} to placeholder
        p = re.sub(r"\$\{[^}]+\}", "{id}", p)
        paths.add(p)

out = Path("ui-ngx/HTTP_API_PATHS.txt")
lines = sorted(paths)
out.write_text(f"{len(lines)} unique path patterns\n\n" + "\n".join(lines) + "\n", encoding="utf-8")
print(f"Wrote {len(lines)} paths to {out}")
