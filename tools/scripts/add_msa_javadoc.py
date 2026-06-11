#!/usr/bin/env python3
"""
Add class- and public/protected method-level Javadoc to msa/ Java sources.
Covers vc-executor microservice and black-box integration/UI tests.
Includes @param, @return, and @throws tags.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from add_dao_javadoc import (  # noqa: E402
    DECL_LINE_RE,
    find_class_methods,
    fix_unindented_method_javadocs,
    format_javadoc,
    format_return,
    humanize,
    insert_text_at,
    javadoc_block_before,
    javadoc_is_complete,
    method_description,
    method_javadoc_needs_fixup,
    method_line_indent,
    param_description,
    split_license_and_body,
    throws_description,
    class_body_bounds_at,
    dao_insert_text_at,
    detect_kind,
)
from add_service_javadoc import find_insert_position  # noqa: E402

MSA_ROOTS = [
    Path("msa/vc-executor/src/main/java"),
    Path("msa/black-box-tests/src/test/java"),
]

MSA_CLASS_DOCS: dict[str, list[str]] = {
    "ThingsboardVersionControlExecutorApplication": [
        "Spring Boot entry point for the Version Control (VC) executor microservice.",
        "",
        "<p>Runs Git operations for entity version control (dashboards, widgets, rule chains) "
        "requested by tb-core via Kafka queue. Config: {@code tb-vc-executor.yml}.",
    ],
    "VersionControlQueueRoutingInfoService": [
        "Stub {@link org.thingsboard.server.queue.discovery.QueueRoutingInfoService} for VC executor.",
        "",
        "<p>Returns an empty queue list — VC does not host rule-engine Kafka topics.",
    ],
    "VersionControlTenantRoutingInfoService": [
        "Stub {@link org.thingsboard.server.queue.discovery.TenantRoutingInfoService} for VC executor.",
        "",
        "<p>Returns tenant id without rule-engine queue routing.",
    ],
    "ContainerTestSuite": [
        "Singleton Docker Compose lifecycle for black-box tests.",
        "",
        "<p>Starts/stops ThingsBoard, transports, Redis, and dependencies; exposes REST base URL to tests.",
    ],
    "AbstractContainerTest": [
        "Base class for Docker-based black-box integration tests.",
        "",
        "<p>Bootstraps {@link ContainerTestSuite}, {@link TestRestClient}, and shared device profile fixtures.",
    ],
    "TestRestClient": [
        "REST client wrapper for black-box tests against the running ThingsBoard container.",
    ],
    "TestProperties": [
        "Loads black-box test configuration (base URL, credentials, timeouts) from system properties.",
    ],
    "TestUtils": [
        "Shared utilities for compose file patching, waits, and test data helpers.",
    ],
    "WsClient": [
        "WebSocket test client for telemetry and notification subscription assertions.",
    ],
    "ThingsBoardDbInstaller": [
        "Prepares database schema/data before black-box Docker stack starts.",
    ],
    "PortFinder": [
        "Finds free TCP ports for testcontainers service binding.",
    ],
    "TestListener": [
        "TestNG listener that logs test start/finish and captures failures for CI artifacts.",
    ],
    "DisableUIListeners": [
        "TestNG annotation helper to skip UI screenshot listeners for headless runs.",
    ],
    "AbstractDriverBaseTest": [
        "Base Selenium WebDriver test with browser setup, login, and screenshot on failure.",
    ],
    "AbstractBasePage": [
        "Base page object with WebDriver wait helpers and common UI interactions.",
    ],
    "RetryAnalyzer": [
        "TestNG retry analyzer for flaky UI smoke tests.",
    ],
    "Const": [
        "UI test constants: timeouts, CSS selectors, and label text fragments.",
    ],
    "EntityPrototypes": [
        "Factory methods for test entity names and profile payloads used in UI tests.",
    ],
    "DevicePrototypes": [
        "Pre-built device names and credentials for connectivity black-box tests.",
    ],
    "CalculatedFieldTest": [
        "Black-box test for calculated-field evaluation end-to-end through rule engine and telemetry.",
    ],
    "MqttNodeTest": [
        "Integration test for MQTT rule-engine node publish/subscribe against a live transport.",
    ],
    "HttpClientTest": [
        "Black-box HTTP transport API test: device telemetry POST and REST validation.",
    ],
    "MqttGatewayClientTest": [
        "Gateway device session test over MQTT transport.",
    ],
    "EdqsEntityDataQueryTest": [
        "Validates Entity Data Query Service responses match SQL entity query API.",
    ],
    "JsExecutorSandboxIsolationTest": [
        "Security test ensuring JS executor sandbox isolates tenant script execution.",
    ],
    "Lwm2mDevicesForTest": [
        "Registry of LwM2M device profiles and endpoints used by connectivity tests.",
    ],
    "LwM2MTestClient": [
        "In-test LwM2M client simulating device registration, observe, and RPC.",
    ],
}

MSA_METHOD_DOCS: dict[str, str] = {
    "start": "Starts Docker Compose stack and waits until ThingsBoard is healthy.",
    "stop": "Stops containers and releases ports after the test suite.",
    "beforeSuite": "TestNG hook: starts containers and initializes REST client.",
    "afterSuite": "TestNG hook: stops containers and cleans temporary files.",
    "getInstance": "Returns singleton {@link ContainerTestSuite} instance.",
    "getBaseUrl": "Returns ThingsBoard REST API base URL for the running stack.",
    "logIn": "Authenticates test user and returns JWT token.",
    "createDevice": "Creates a test device via REST API and returns its id.",
    "deleteDevice": "Removes a device by id through REST API.",
    "waitForTelemetry": "Polls WebSocket or REST until expected telemetry key appears.",
    "open": "Navigates WebDriver to the ThingsBoard login page.",
    "login": "Fills credentials and submits the login form.",
    "click": "Clicks a WebElement after wait-until-clickable.",
    "findElement": "Locates a DOM element using the page object selector.",
    "takeScreenshot": "Captures browser screenshot on test failure for CI artifacts.",
    "retry": "TestNG retry decision for flaky UI tests.",
    "getAllQueuesRoutingInfo": "Returns empty list — VC executor has no rule-engine queues.",
    "getRoutingInfo": "Returns tenant routing info without queue name for VC executor.",
    "updateArguments": "Appends default Spring config name for VC executor YAML.",
}

MSA_PARAM_HINTS: dict[str, str] = {
    "driver": "Selenium WebDriver instance",
    "wait": "WebDriverWait for explicit expected conditions",
    "tenantId": "target tenant UUID in the test environment",
    "deviceId": "device under test",
    "token": "JWT from test REST login",
    "compose": "Docker Compose container handle",
    "timeout": "maximum wait duration in seconds",
    "selector": "CSS or XPath locator string",
    "testContext": "TestNG test context for retry and reporting",
    "result": "TestNG test result for failure capture",
}

MSA_PACKAGE_CONTEXT: dict[str, str] = {
    "vc": "version control executor microservice",
    "msa": "black-box test infrastructure",
    "ui": "Selenium UI automation",
    "pages": "page object element locators and helpers",
    "tabs": "modal/tab page fragments for UI tests",
    "tests": "TestNG smoke and regression test cases",
    "connectivity": "transport protocol integration tests",
    "lwm2m": "LwM2M connectivity and RPC tests",
    "mapper": "JSON/WebSocket response DTOs for tests",
    "prototypes": "test entity prototype builders",
    "cf": "calculated-field black-box tests",
    "edqs": "EDQS black-box validation",
    "rule": "rule-engine node integration tests",
    "security": "security and sandbox isolation tests",
    "listeners": "TestNG listeners and retry helpers",
    "utils": "UI and test utilities",
    "client": "protocol test clients (LwM2M, etc.)",
    "rpc": "LwM2M RPC/observe black-box scenarios",
}


def msa_area(path: Path) -> str:
    posix = path.as_posix()
    if "/vc-executor/" in posix:
        return "vc"
    if "/black-box-tests/" in posix:
        parts = posix.split("/msa/")
        if len(parts) > 1:
            rest = parts[1].split("/")
            for i, p in enumerate(rest):
                if p == "msa" and i + 1 < len(rest):
                    return rest[i + 1].split("/")[0] if "/" in rest[i + 1] else rest[i + 1]
            if "ui" in rest:
                idx = rest.index("ui")
                if idx + 1 < len(rest):
                    return rest[idx + 1]
        return "msa"
    return "msa"


def msa_subpackage(path: Path) -> str:
    posix = path.as_posix()
    for fragment, hint in {
        "/ui/pages/": "Selenium page objects",
        "/ui/tests/": "UI smoke/regression tests",
        "/ui/base/": "WebDriver test base classes",
        "/connectivity/lwm2m/": "LwM2M transport tests",
        "/connectivity/": "device transport connectivity tests",
        "/vc/service/": "VC executor queue discovery stubs",
    }.items():
        if fragment in posix:
            return hint
    return ""


def class_description(name: str, kind: str, area: str, body: str, path: Path) -> list[str]:
    if name in MSA_CLASS_DOCS:
        return MSA_CLASS_DOCS[name]
    hint = msa_subpackage(path)
    ctx = MSA_PACKAGE_CONTEXT.get(area, "ThingsBoard MSA") + (f" — {hint}" if hint else "")

    if name.endswith("Test"):
        subject = humanize(name.replace("Test", ""))
        if "Abstract" in name:
            return [f"Abstract base for {subject} black-box tests ({ctx})."]
        return [f"Black-box test: {subject} ({ctx})."]
    if name.endswith("Elements"):
        return [
            f"Selenium element locators for {humanize(name.replace('Elements', ''))} page ({ctx}).",
            "",
            "<p>Defines CSS/XPath selectors; use with matching *Helper for interactions.",
        ]
    if name.endswith("Helper"):
        return [f"Page object helper for {humanize(name.replace('Helper', ''))} UI actions ({ctx})."]
    if name.endswith("Response") or name.endswith("Request"):
        return [f"Test DTO for deserializing {humanize(name)} ({ctx})."]
    if name.startswith("Abstract"):
        return [f"Abstract base class for MSA {humanize(name[8:])} ({ctx})."]
    if kind == "interface":
        return [f"{humanize(name)} contract ({ctx})."]
    if kind == "enum":
        return [f"Enumerates {humanize(name)} values ({ctx})."]
    return [f"{humanize(name).capitalize()} ({ctx})."]


def class_javadoc_is_incomplete(doc: str, class_name: str) -> bool:
    if class_name in MSA_CLASS_DOCS and len(doc) < 80:
        return True
    thin = ("Abstract container test.", "Container test suite.", "MSA ", "contract (")
    if any(t in doc for t in thin) and "<p>" not in doc:
        if len(doc) < 100:
            return True
    if len(doc.strip()) < 55:
        return True
    return False


def add_class_javadoc_at(
    body: str, decl_match: re.Match, class_name: str, kind: str, area: str, path: Path, force: bool
) -> tuple[str, bool]:
    insert_pos = find_insert_position(body, decl_match.start())
    new_doc = format_javadoc(decl_match.group("indent"), class_description(class_name, kind, area, body, path))
    existing = javadoc_block_before(body, insert_pos)
    if existing:
        start, end = existing
        if not force and not class_javadoc_is_incomplete(body[start:end], class_name):
            return body, False
        body = body[:start] + body[end:]
        insert_pos = start
    return insert_text_at(body, insert_pos, new_doc), True


def add_all_class_javadocs(body: str, area: str, path: Path, force: bool) -> tuple[str, int]:
    count = 0
    for m in reversed(list(DECL_LINE_RE.finditer(body))):
        name = m.group("name")
        kind = detect_kind(m.group(0))
        body, changed = add_class_javadoc_at(body, m, name, kind, area, path, force)
        if changed:
            count += 1
    return body, count


def msa_param_description(name: str, type_hint: str) -> str:
    if name in MSA_PARAM_HINTS:
        return MSA_PARAM_HINTS[name]
    return param_description(name, type_hint)


def strip_all_javadocs_before(body: str, pos: int) -> tuple[str, int]:
    while True:
        existing = javadoc_block_before(body, pos)
        if not existing:
            break
        start, end = existing
        body = body[:start] + body[end:]
        pos = start
    return body, pos


def method_javadoc_is_thin(doc: str) -> bool:
    if "@return" not in doc or "@throws" not in doc:
        return True
    if len(doc) < 90 and "@param" not in doc:
        return True
    return False


def msa_method_javadoc(body: str, method) -> str:
    raw = method_line_indent(body, method.sig_start) or method.indent or "    "
    indent = "    " if len(raw.replace("\t", "    ")) > 4 else raw
    desc = MSA_METHOD_DOCS.get(method.name, method_description(method.name))
    lines = [desc, ""]
    for p in method.params:
        lines.append(f"@param {p.name} {msa_param_description(p.name, p.type_hint)}")
    lines.append(f"@return {format_return(method.return_type)}")
    if method.throws:
        for t in method.throws:
            lines.append(f"@throws {t} {throws_description(t)}")
    else:
        lines.append("@throws Exception if an unexpected error occurs during processing")
    return format_javadoc(indent, lines)


def add_msa_method_javadocs(body: str, decl_match, class_name: str, kind: str) -> tuple[str, int]:
    count = 0
    class_start, _, _ = class_body_bounds_at(body, decl_match)
    while True:
        pending = None
        for method in reversed(find_class_methods(body, class_name, kind, decl_match)):
            if method.name in ("main",):
                continue
            if method.doc_start < class_start:
                continue
            existing = javadoc_block_before(body, method.doc_start)
            if existing:
                start, end = existing
                doc = body[start:end]
                if (
                    javadoc_is_complete(doc, method)
                    and not method_javadoc_needs_fixup(body, method.doc_start, method.sig_start)
                    and not method_javadoc_is_thin(doc)
                ):
                    continue
            pending = method
            break
        if pending is None:
            break
        pos = max(pending.doc_start, class_start + 1)
        body, pos = strip_all_javadocs_before(body, pos)
        new_doc = msa_method_javadoc(body, pending)
        body = dao_insert_text_at(body, pos, new_doc)
        count += 1
    return body, count


def process_file(path: Path, dry_run: bool, force_class: bool) -> bool:
    if path.name == "package-info.java":
        return False
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    if not DECL_LINE_RE.search(body):
        return False
    area = msa_area(path)
    changed = False
    body, cc = add_all_class_javadocs(body, area, path, force_class)
    if cc:
        changed = True
    for decl_match in DECL_LINE_RE.finditer(body):
        class_name = decl_match.group("name")
        kind = detect_kind(decl_match.group(0))
        body, mc = add_msa_method_javadocs(body, decl_match, class_name, kind)
        if mc:
            changed = True
    body, fc = fix_unindented_method_javadocs(body)
    if fc:
        changed = True
    if changed and not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def collect_files(roots: list[Path]) -> list[Path]:
    files: list[Path] = []
    seen: set[str] = set()
    for root in roots:
        if not root.exists():
            continue
        for path in sorted(root.rglob("*.java")):
            if path.name == "package-info.java":
                continue
            key = str(path.resolve())
            if key not in seen:
                seen.add(key)
                files.append(path)
    return files


def main() -> int:
    parser = argparse.ArgumentParser(description="Add detailed Javadoc to msa/ Java sources")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--force-class", action="store_true", help="Replace thin class Javadoc")
    args = parser.parse_args()
    totals: dict[str, int] = {}
    for path in collect_files(MSA_ROOTS):
        if process_file(path, args.dry_run, args.force_class):
            area = msa_area(path)
            totals[area] = totals.get(area, 0) + 1
    total = sum(totals.values())
    for area in sorted(totals.keys()):
        print(f"{area}: {totals[area]} files modified", file=sys.stderr)
    print(f"Done: {total} files modified", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
