#!/usr/bin/env python3
"""Extract Angular route paths from *routing*.ts under ui-ngx."""
import re
from pathlib import Path

ROOT = Path("ui-ngx/src/app")
PAT = re.compile(r"\bpath:\s*['\"]([^'\"]+)['\"]")

routes: list[tuple[str, str]] = []
for path in sorted(ROOT.rglob("*routing*.ts")):
    rel = path.relative_to(ROOT).as_posix()
    for m in PAT.finditer(path.read_text(encoding="utf-8", errors="replace")):
        routes.append((rel, m.group(1)))

out = Path("ui-ngx/ROUTES.txt")
body = [f"{len(routes)} route entries", ""]
for f, p in routes:
    body.append(f"{p}\t# {f}")
out.write_text("\n".join(body) + "\n", encoding="utf-8")
print(f"Wrote {len(routes)} entries to {out}")
