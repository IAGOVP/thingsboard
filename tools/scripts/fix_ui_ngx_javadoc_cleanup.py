#!/usr/bin/env python3
"""Repair misplaced JSDoc inside @Injectable/@Component/@NgModule and duplicate thin class docs."""
import re
import sys
from pathlib import Path


def fix_injectable(text: str) -> str:
    return re.sub(
        r"(?:/\*\*[\s\S]*?\*/\s*)?"
        r"@Injectable\(\{\s*\n\s*providedIn:\s*['\"]root['\"]\s*\n"
        r"(/\*\*[\s\S]*?\*/)\s*\n\}\)",
        r"\1\n@Injectable({\n  providedIn: 'root'\n})",
        text,
    )


def fix_component_inner_doc(text: str) -> str:
    def repl(m: re.Match) -> str:
        meta = m.group(1)
        doc = m.group(2)
        tail = m.group(3)
        tail = re.sub(r"\s*\}\)\s*$", "\n})", tail.strip())
        if not tail.endswith("})"):
            tail = tail.rstrip("}") + "\n})"
        return doc + "\n@Component({" + meta + tail

    return re.sub(
        r"@Component\(\{([\s\S]*?)(/\*\*[\s\S]*?\*/)\s*((?:standalone:\s*false|changeDetection:[^}]+)[^}]*\}\))",
        repl,
        text,
    )


def fix_component_generic(text: str) -> str:
    pat = re.compile(r"@Component\(\{([\s\S]*?)(/\*\*[\s\S]*?\*/)([\s\S]*?)\n\}\)")

    def repl(m: re.Match) -> str:
        before_doc = m.group(1)
        doc = m.group(2)
        after_doc = m.group(3)
        if "/**" in before_doc:
            return m.group(0)
        combined = before_doc.rstrip() + "\n" + after_doc.lstrip()
        combined = re.sub(r"standalone:\s*false\s*\}\)", "standalone: false\n})", combined)
        if not combined.strip().endswith("})"):
            combined = combined.rstrip().rstrip("}") + "\n})"
        return doc + "\n@Component({" + combined

    return pat.sub(repl, text)


def fix_ngmodule_inner_doc(text: str) -> str:
    return re.sub(
        r"(@NgModule\(\{[\s\S]*?)(/\*\*[\s\S]*?\*/)\s*(\]\s*\}\))",
        r"\2\n\1\3",
        text,
    )


def remove_thin_duplicate(text: str) -> str:
    return re.sub(
        r"/\*\*\s*\n\s*\* Angular (?:component|HTTP service|injectable service):[^\n]+\n\s*\*/\s*\n"
        r"(?=\s*/\*\*)",
        "",
        text,
    )


def fix_file(text: str) -> str:
    text = remove_thin_duplicate(text)
    text = fix_injectable(text)
    text = fix_component_inner_doc(text)
    text = fix_component_generic(text)
    text = fix_ngmodule_inner_doc(text)
    text = re.sub(r"standalone:\s*false\}\)", "standalone: false\n})", text)
    return text


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("ui-ngx/src/app")
    n = 0
    for path in sorted(root.rglob("*.ts")):
        if ".spec.ts" in path.name:
            continue
        text = path.read_text(encoding="utf-8")
        fixed = fix_file(text)
        if fixed != text:
            path.write_text(fixed, encoding="utf-8", newline="\n")
            n += 1
    print(f"Repaired {n} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
