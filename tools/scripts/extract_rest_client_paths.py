#!/usr/bin/env python3
"""Extract unique REST API path suffixes from RestClient.java into stdout (for API_PATHS.txt)."""
import re
from pathlib import Path

text = Path("rest-client/src/main/java/org/thingsboard/rest/client/RestClient.java").read_text(encoding="utf-8")
paths = sorted(set(re.findall(r'baseURL \+ "(/api/[^"]+)"', text)))
print(f"{len(paths)} paths")
for p in paths:
    print(p)
