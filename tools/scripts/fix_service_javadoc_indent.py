#!/usr/bin/env python3
"""Fix method Javadoc glued after semicolons without indentation."""
from __future__ import annotations

import re
import sys
from pathlib import Path

TARGET_PACKAGES = (
    "notification", "install", "telemetry", "ttl", "mail", "housekeeper",
    "executors", "transport", "rpc", "edqs", "sms", "ai", "apiusage",
    "job", "state", "mobile", "component", "device", "script", "system",
    "profile", "resource", "session", "partition", "query", "ota",
    "lwm2m", "gateway_device", "rule", "ruleengine", "stats", "update",
    "action", "asset", "user",
)


def fix_content(text: str) -> str:
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
            indent = "    "
            j = i + 1
            while j < len(lines):
                if lines[j].strip().endswith("*/"):
                    j += 1
                    break
                j += 1
            if j < len(lines):
                next_line = lines[j]
                if next_line.startswith(" ") and not next_line.strip().startswith("*"):
                    indent = next_line[: len(next_line) - len(next_line.lstrip())]
            if out and out[-1].strip():
                out.append("")
            out.append(indent + line)
            i += 1
            while i < len(lines):
                cur = lines[i]
                if cur.strip().endswith("*/"):
                    if cur.startswith(" *") or cur == " */":
                        out.append(indent + cur if not cur.startswith(indent) else cur)
                    else:
                        out.append(indent + cur)
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


def main() -> int:
    base = Path("application/src/main/java/org/thingsboard/server/service")
    count = 0
    for pkg in TARGET_PACKAGES:
        for path in sorted((base / pkg).rglob("*.java")):
            text = path.read_text(encoding="utf-8")
            fixed = fix_content(text)
            if fixed != text:
                path.write_text(fixed, encoding="utf-8", newline="\n")
                count += 1
    print(f"Fixed indentation in {count} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
