#!/usr/bin/env python3
"""
Add class- and public/protected method-level Javadoc to monitoring/ module sources.
Includes @param, @return, and @throws tags. Skips tests and package-info.java.
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

MONITORING_CLASS_DOCS: dict[str, list[str]] = {
    "ThingsboardMonitoringApplication": [
        "Spring Boot entry point for ThingsBoard synthetic monitoring.",
        "",
        "<p>Schedules {@link org.thingsboard.monitoring.service.BaseMonitoringService} health probes, "
        "provisions monitoring entities, reports latencies to telemetry, and sends Slack notifications.",
    ],
    "BaseMonitoringService": [
        "Abstract monitoring loop shared by domain-specific monitoring services.",
        "",
        "<p>Performs REST login, WebSocket telemetry subscription, per-target health checks, "
        "optional EDQS validation, and latency reporting via {@link org.thingsboard.monitoring.service.MonitoringReporter}.",
    ],
    "BaseHealthChecker": [
        "End-to-end transport health probe base class.",
        "",
        "<p>Publishes a test payload through a protocol client, waits for a matching WebSocket telemetry update "
        "(and calculated-field key when enabled), and records step latencies.",
    ],
    "TransportsMonitoringService": [
        "Monitors MQTT, HTTP, CoAP, and LwM2M transports.",
        "",
        "<p>Registers {@link org.thingsboard.monitoring.config.transport.TransportType}-specific "
        "{@link org.thingsboard.monitoring.service.transport.TransportHealthChecker} beans from configuration.",
    ],
    "MonitoringReporter": [
        "Aggregates latencies and failure counts for monitored services.",
        "",
        "<p>Emits {@link org.thingsboard.monitoring.data.notification.Notification} alerts and saves latency "
        "telemetry to the monitoring asset on the target tenant.",
    ],
    "MonitoringEntityService": [
        "Provisions monitoring devices, assets, dashboard, and rule chain on the target ThingsBoard tenant.",
        "",
        "<p>Called once at startup before health checks begin.",
    ],
    "TbClient": [
        "REST client to the monitored ThingsBoard instance.",
        "",
        "<p>Extends {@link org.thingsboard.rest.client.RestClient} for login, entity query, and telemetry save.",
    ],
    "WsClient": [
        "WebSocket client for live telemetry validation during health checks.",
        "",
        "<p>Subscribes to monitoring device keys and waits for {@code testData} updates echoed from transports.",
    ],
    "WsClientFactory": [
        "Factory for authenticated {@link WsClient} instances.",
        "",
        "<p>Uses JWT from {@link TbClient#logIn()} for WebSocket session setup.",
    ],
    "Lwm2mClient": [
        "LwM2M client used by {@link org.thingsboard.monitoring.service.transport.impl.Lwm2mTransportHealthChecker}.",
    ],
    "NotificationService": [
        "Dispatches {@link org.thingsboard.monitoring.data.notification.Notification} to all configured channels.",
        "",
        "<p>Runs delivery asynchronously and returns futures for shutdown synchronization.",
    ],
    "IncidentManager": [
        "Tracks open incidents and routes failure/recovery messages.",
        "",
        "<p>Delegates to {@link org.thingsboard.monitoring.notification.incident.IncidentTransport} implementations (e.g. Slack).",
    ],
    "NotificationChannel": [
        "Outbound notification sink contract (Slack webhook, etc.).",
    ],
    "SlackNotificationChannel": [
        "Sends monitoring alerts to Slack via {@link org.thingsboard.monitoring.notification.channels.impl.SlackApiClient}.",
    ],
    "SlackApiClient": [
        "HTTP client for Slack chat.postMessage and incident APIs.",
    ],
    "SlackIncidentTransport": [
        "Slack-specific {@link org.thingsboard.monitoring.notification.incident.IncidentTransport} implementation.",
    ],
    "IncidentTransport": [
        "Contract for creating and updating external incident tickets (Slack, etc.).",
    ],
    "TransportHealthChecker": [
        "Marker interface for transport-specific {@link BaseHealthChecker} Spring beans.",
    ],
    "MqttTransportHealthChecker": [
        "Publishes test MQTT telemetry and validates WebSocket echo latency.",
    ],
    "HttpTransportHealthChecker": [
        "POSTs test telemetry over the HTTP device API and validates WebSocket echo.",
    ],
    "CoapTransportHealthChecker": [
        "Sends a CoAP test payload to the transport endpoint and validates echo.",
    ],
    "Lwm2mTransportHealthChecker": [
        "LwM2M observe/write probe for transport health validation.",
    ],
    "MonitoringConfig": [
        "Base configuration: list of {@link MonitoringTarget} URLs and shared monitoring options.",
    ],
    "MonitoringTarget": [
        "One monitored ThingsBoard base URL and per-target device identifiers.",
    ],
    "TransportMonitoringConfig": [
        "Transport-type-specific monitoring configuration (targets, timeouts, QoS).",
    ],
    "TransportMonitoringTarget": [
        "Transport monitoring target with queue name and device naming prefix.",
    ],
    "TransportType": [
        "Supported transport protocols and their health-checker Spring bean classes.",
    ],
    "MonitoredServiceKey": [
        "Keys for login, WebSocket, EDQS, and per-transport failure/latency reporting.",
    ],
    "Latencies": [
        "Constants for latency metric names (login, subscribe, per-transport request/WebSocket).",
    ],
    "Latency": [
        "Single latency sample with formatted value for notifications and telemetry.",
    ],
    "ServiceFailureException": [
        "Raised when a monitored step fails; carries the {@link MonitoredServiceKey} that failed.",
    ],
    "Notification": [
        "Base type for Slack/info/failure/recovery monitoring messages.",
    ],
    "ServiceFailureNotification": [
        "Alert text when a monitored service exceeds the configured failure threshold.",
    ],
    "ServiceRecoveryNotification": [
        "Alert when a previously failing monitored service succeeds again.",
    ],
    "HighLatencyNotification": [
        "Alert when one or more monitoring steps exceed the configured latency threshold.",
    ],
    "InfoNotification": [
        "Informational monitoring message (startup, shutdown, or operator info).",
    ],
    "AffectedService": [
        "Describes which monitored service failed inside a composite notification.",
    ],
    "ShortNameProvider": [
        "Short display names for monitored services in Slack and log output.",
    ],
    "TbStopWatch": [
        "NanoTime stopwatch for latency measurement in health checks.",
    ],
    "ResourceUtils": [
        "Loads classpath resources (LwM2M models, rule chain JSON) for monitoring entity setup.",
    ],
    "EntityDataCmd": [
        "WebSocket command wrapper for entity data subscription during monitoring.",
    ],
    "EntityDataUpdate": [
        "Parsed WebSocket entity data update payload from the monitored tenant.",
    ],
    "LatestValueCmd": [
        "WebSocket command to read latest telemetry for entity keys.",
    ],
    "CmdsWrapper": [
        "Batch of WebSocket commands sent to ThingsBoard during subscription setup.",
    ],
    "DeviceConfig": [
        "Per-device transport credentials and naming used by health checkers.",
    ],
    "TransportInfo": [
        "Resolved transport connection details (host, port, protocol) for a monitoring target.",
    ],
}

MONITORING_METHOD_DOCS: dict[str, str] = {
    "runChecks": "Executes one monitoring cycle: login, subscribe, health probes, and latency reporting.",
    "init": "Builds health checkers from configuration and prepares WebSocket subscriptions.",
    "checkHealth": "Runs the transport-specific probe and validates WebSocket telemetry echo.",
    "check": "Runs the transport-specific probe and validates WebSocket telemetry echo.",
    "logIn": "Authenticates with configured tenant credentials and returns JWT for WebSocket clients.",
    "sendNotification": "Dispatches a notification to all configured channels asynchronously.",
    "checkEntities": "Creates or verifies monitoring devices, assets, dashboard, and rule chain exist.",
    "getDashboardPublicLink": "Returns the public URL of the monitoring latency dashboard.",
    "startMonitoring": "After Spring context is ready: provisions entities, inits checkers, and schedules probes.",
    "onShutdown": "Sends shutdown notification before the monitoring JVM exits.",
    "shutdownScheduler": "Stops the monitoring scheduler thread pool.",
    "reportSuccess": "Records successful probe latencies and clears failure state for the service key.",
    "reportFailure": "Increments failure counter and may emit failure notification when threshold is exceeded.",
    "reportLatency": "Persists latency samples as telemetry on the monitoring asset.",
    "waitForUpdate": "Blocks until WebSocket receives expected telemetry or timeout elapses.",
    "connect": "Opens WebSocket session to the monitored ThingsBoard tenant.",
    "subscribe": "Sends entity data subscription commands for monitoring device UUIDs.",
    "createIncident": "Opens an external incident ticket for a service failure.",
    "resolveIncident": "Closes or updates an external incident when the service recovers.",
    "getShortName": "Returns a compact display name for the monitored service key.",
    "stop": "Stops timers and releases transport client resources.",
    "format": "Formats latency in milliseconds for notifications and logs.",
    "loadResource": "Reads a classpath resource as UTF-8 text for entity provisioning.",
}

MONITORING_PARAM_HINTS: dict[str, str] = {
    "target": "monitoring target URL and device configuration",
    "config": "monitoring configuration for this transport or domain",
    "key": "monitored service step identifier",
    "latency": "measured step latency sample",
    "notification": "alert payload to deliver to notification channels",
    "serviceKey": "which monitored step failed or recovered",
    "wsClient": "authenticated WebSocket client for telemetry validation",
    "deviceId": "monitoring device UUID on the target tenant",
    "timeoutMs": "maximum wait time for WebSocket echo in milliseconds",
    "message": "Slack or notification message body",
    "channel": "Slack channel id or webhook target",
    "event": "Spring application lifecycle event",
    "futures": "async notification delivery futures to await on shutdown",
}

MONITORING_PACKAGES: dict[str, str] = {
    "service": "monitoring orchestration and health-check execution",
    "client": "REST/WebSocket/LwM2M clients to the monitored tenant",
    "config": "monitoring targets and transport configuration beans",
    "data": "latency samples, failure keys, and notification DTOs",
    "notification": "alert dispatch and external incident integration",
    "util": "stopwatch and classpath resource helpers",
}


def monitoring_package(path: Path) -> str:
    posix = path.as_posix()
    m = re.search(r"/monitoring/(?:src/main/java/org/thingsboard/monitoring/)?([^/]+)/", posix)
    if m:
        return m.group(1)
    if "/monitoring/ThingsboardMonitoringApplication" in posix.replace("\\", "/"):
        return "app"
    return "monitoring"


def class_description(name: str, kind: str, pkg: str, body: str, path: Path) -> list[str]:
    if name in MONITORING_CLASS_DOCS:
        return MONITORING_CLASS_DOCS[name]
    ctx = MONITORING_PACKAGES.get(pkg, "ThingsBoard synthetic monitoring")

    if name.endswith("TransportMonitoringConfig"):
        transport = humanize(name.replace("TransportMonitoringConfig", ""))
        return [f"Configuration for {transport} transport monitoring ({ctx})."]
    if name.endswith("TransportHealthChecker") or name.endswith("HealthChecker"):
        transport = humanize(name.replace("TransportHealthChecker", "").replace("HealthChecker", ""))
        return [f"Health checker for {transport} transport ({ctx})."]
    if name.endswith("Notification") and kind == "class":
        return [f"Monitoring notification payload: {humanize(name)} ({ctx})."]
    if name.endswith("Cmd") or name.endswith("Update"):
        return [f"WebSocket command or update DTO for monitoring ({humanize(name)})."]
    if kind == "interface":
        return [f"{humanize(name)} contract ({ctx})."]
    if kind == "enum":
        return [f"Enumerates {humanize(name)} values used by monitoring ({ctx})."]
    if "@Service" in body or "@Component" in body:
        return [f"Spring component for monitoring {humanize(name)} ({ctx})."]
    return [f"{humanize(name).capitalize()} ({ctx})."]


def class_javadoc_is_incomplete(doc: str, class_name: str) -> bool:
    if class_name in MONITORING_CLASS_DOCS and len(doc) < 90:
        return True
    thin = (
        "Monitoring component:",
        "contract (",
        "Abstract monitoring loop:",
        "without hitting",
    )
    if any(t in doc for t in thin) and "<p>" not in doc and "@param" not in doc:
        if len(doc) < 120:
            return True
    if len(doc.strip()) < 65:
        return True
    return False


def add_class_javadoc_at(
    body: str, decl_match: re.Match, class_name: str, kind: str, pkg: str, path: Path, force: bool
) -> tuple[str, bool]:
    insert_pos = find_insert_position(body, decl_match.start())
    new_doc = format_javadoc(decl_match.group("indent"), class_description(class_name, kind, pkg, body, path))
    existing = javadoc_block_before(body, insert_pos)
    if existing:
        start, end = existing
        if not force and not class_javadoc_is_incomplete(body[start:end], class_name):
            return body, False
        body = body[:start] + body[end:]
        insert_pos = start
    return insert_text_at(body, insert_pos, new_doc), True


def add_all_class_javadocs(body: str, pkg: str, path: Path, force: bool) -> tuple[str, int]:
    count = 0
    for m in reversed(list(DECL_LINE_RE.finditer(body))):
        name = m.group("name")
        kind = detect_kind(m.group(0))
        body, changed = add_class_javadoc_at(body, m, name, kind, pkg, path, force)
        if changed:
            count += 1
    return body, count


def monitoring_param_description(name: str, type_hint: str) -> str:
    if name in MONITORING_PARAM_HINTS:
        return MONITORING_PARAM_HINTS[name]
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
    if len(doc) < 100 and "@param" not in doc:
        return True
    for thin in (
        "Authenticates with configured tenant credentials",
        "After context is ready:",
        "Sends shutdown notification",
        "Single-thread scheduler",
        "Loads {@code tb-monitoring.yml}",
    ):
        if doc.count("*") <= 4 and thin in doc:
            return True
    return False


def monitoring_method_javadoc(body: str, method) -> str:
    raw = method_line_indent(body, method.sig_start) or method.indent or "    "
    indent = "    " if len(raw.replace("\t", "    ")) > 4 else raw
    desc = MONITORING_METHOD_DOCS.get(method.name, method_description(method.name))
    lines = [desc, ""]
    for p in method.params:
        lines.append(f"@param {p.name} {monitoring_param_description(p.name, p.type_hint)}")
    lines.append(f"@return {format_return(method.return_type)}")
    if method.throws:
        for t in method.throws:
            lines.append(f"@throws {t} {throws_description(t)}")
    else:
        lines.append("@throws Exception if an unexpected error occurs during processing")
    return format_javadoc(indent, lines)


def add_monitoring_method_javadocs(body: str, decl_match, class_name: str, kind: str) -> tuple[str, int]:
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
        new_doc = monitoring_method_javadoc(body, pending)
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
    pkg = monitoring_package(path)
    changed = False
    body, cc = add_all_class_javadocs(body, pkg, path, force_class)
    if cc:
        changed = True
    for decl_match in DECL_LINE_RE.finditer(body):
        class_name = decl_match.group("name")
        kind = detect_kind(decl_match.group(0))
        body, mc = add_monitoring_method_javadocs(body, decl_match, class_name, kind)
        if mc:
            changed = True
    body, fc = fix_unindented_method_javadocs(body)
    if fc:
        changed = True
    if changed and not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def collect_files(root: Path) -> list[Path]:
    files: list[Path] = []
    for path in sorted(root.rglob("*.java")):
        if "src/main/java" not in path.as_posix():
            continue
        if path.name == "package-info.java":
            continue
        files.append(path)
    return files


def main() -> int:
    parser = argparse.ArgumentParser(description="Add detailed Javadoc to monitoring module sources")
    parser.add_argument(
        "--base",
        type=Path,
        default=Path("monitoring/src/main/java"),
    )
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--force-class", action="store_true", help="Replace thin class Javadoc")
    args = parser.parse_args()
    totals: dict[str, int] = {k: 0 for k in list(MONITORING_PACKAGES) + ["app", "monitoring"]}
    for path in collect_files(args.base):
        if process_file(path, args.dry_run, args.force_class):
            pkg = monitoring_package(path)
            totals[pkg] = totals.get(pkg, 0) + 1
    total = sum(totals.values())
    for pkg in sorted(totals.keys()):
        if totals[pkg]:
            print(f"{pkg}: {totals[pkg]} files modified", file=sys.stderr)
    print(f"Done: {total} files modified", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
