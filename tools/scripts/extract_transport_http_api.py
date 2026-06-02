#!/usr/bin/env python3
"""Extract HTTP device API mappings from DeviceApiController.java to transport/DEVICE_API.md sections."""
import re
from pathlib import Path

CTRL = Path("common/transport/http/src/main/java/org/thingsboard/server/transport/http/DeviceApiController.java")
text = CTRL.read_text(encoding="utf-8")
pattern = re.compile(
    r"@RequestMapping\(value\s*=\s*\"([^\"]+)\"[^)]*method\s*=\s*RequestMethod\.(\w+)",
    re.MULTILINE,
)
rows = []
for m in pattern.finditer(text):
    path, method = m.group(1), m.group(2)
    rows.append((method, path))
base = "@RequestMapping(\"/api/v1\")"
out = ["# HTTP device transport API", "", "Base path: `/api/v1`", "", "| Method | Path |", "|--------|------|"]
for method, path in sorted(set(rows)):
    out.append(f"| {method} | `/api/v1{path}` |")
Path("transport/DEVICE_API.md").write_text("\n".join(out) + "\n", encoding="utf-8")
print(f"Wrote {len(rows)} mappings")
