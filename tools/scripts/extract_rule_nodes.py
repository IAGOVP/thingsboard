#!/usr/bin/env python3
"""Extract @RuleNode metadata to markdown."""
import re
from pathlib import Path

RULE_RE = re.compile(
    r"@RuleNode\s*\(\s*type\s*=\s*ComponentType\.(\w+),\s*name\s*=\s*\"([^\"]+)\"[^)]*?"
    r"nodeDescription\s*=\s*\"([^\"]+)\"[^)]*?"
    r"(?:docUrl\s*=\s*\"([^\"]*)\")?",
    re.DOTALL,
)

root = Path("rule-engine/rule-engine-components/src/main/java")
rows = []
for p in sorted(root.rglob("Tb*Node.java")):
    text = p.read_text(encoding="utf-8")
    m = RULE_RE.search(text)
    if not m:
        continue
    cls = p.stem
    rows.append((m.group(1), m.group(2), cls, m.group(3), m.group(4) or ""))

rows.sort(key=lambda r: (r[0], r[1]))
lines = ["# Rule engine nodes catalog\n", "| Type | UI name | Java class | Description | Docs |\n", "|------|---------|------------|-------------|------|\n"]
for t, name, cls, desc, url in rows:
    desc = desc.replace("|", "\\|")[:80]
    link = f"[doc]({url})" if url else ""
    lines.append(f"| {t} | {name} | `{cls}` | {desc} | {link} |\n")

out = Path("rule-engine/RULE_NODES.md")
out.write_text("".join(lines), encoding="utf-8")
print(f"Wrote {len(rows)} nodes to {out}")
