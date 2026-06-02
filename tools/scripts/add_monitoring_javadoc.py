#!/usr/bin/env python3
"""Add class-level Javadoc to monitoring module Java sources."""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

DECL_LINE_RE = re.compile(
    r"^(?P<indent>\s*)(?:(?:public|protected|private)\s+)?"
    r"(?:(?:abstract|sealed|non-sealed|static|final)\s+)*"
    r"(?:class|interface|enum|record)\s+"
    r"(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)


def split_license_and_body(content: str) -> tuple[str, str]:
    m = re.match(r"(\s*/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    return (m.group(1), m.group(2)) if m else ("", content)


def humanize(name: str) -> str:
    return re.sub(r"([a-z])([A-Z])", r"\1 \2", name).replace("_", " ").lower()


def monitoring_class_doc(name: str, kind: str) -> str:
    docs = {
        "ThingsboardMonitoringApplication": (
            "Spring Boot entry point for ThingsBoard synthetic monitoring: schedules transport health checks, "
            "reports latencies to telemetry, and sends Slack (or other) notifications."
        ),
        "BaseMonitoringService": (
            "Abstract monitoring loop: REST login, WebSocket telemetry subscription, per-target health checks, "
            "optional EDQS validation, and latency reporting."
        ),
        "BaseHealthChecker": (
            "End-to-end transport probe: publish test payload via protocol client, wait for matching WebSocket "
            "telemetry update (and calculated-field key when enabled)."
        ),
        "TransportsMonitoringService": (
            "Monitors MQTT, HTTP, CoAP, and LwM2M transports using {@link TransportType}-specific health checkers."
        ),
        "MonitoringReporter": (
            "Aggregates latencies and failure counts; emits notifications and saves latency telemetry to a monitoring asset."
        ),
        "MonitoringEntityService": (
            "Ensures monitoring devices, assets, dashboard, and rule chain exist on the target ThingsBoard tenant."
        ),
        "TbClient": (
            "REST client to the monitored ThingsBoard ({@link org.thingsboard.rest.client.RestClient}): login, entity query, telemetry save."
        ),
        "WsClient": (
            "WebSocket client for live telemetry: subscribes to monitoring device keys and waits for testData updates."
        ),
        "WsClientFactory": "Creates {@link WsClient} instances authenticated with a JWT from {@link TbClient}.",
        "Lwm2mClient": "LwM2M client used by {@link org.thingsboard.monitoring.service.transport.impl.Lwm2mTransportHealthChecker}.",
        "NotificationService": "Dispatches {@link org.thingsboard.monitoring.data.notification.Notification} to all configured channels asynchronously.",
        "IncidentManager": "Tracks open incidents and routes failure/recovery messages through {@link org.thingsboard.monitoring.notification.incident.IncidentTransport}.",
        "NotificationChannel": "Outbound notification sink (e.g. Slack webhook).",
        "SlackNotificationChannel": "Sends monitoring alerts to Slack via {@link SlackApiClient}.",
        "SlackApiClient": "HTTP client for Slack chat.postMessage / incident APIs.",
        "SlackIncidentTransport": "Slack-specific {@link IncidentTransport} implementation.",
        "IncidentTransport": "Contract for creating/updating external incident tickets (Slack, etc.).",
        "TransportHealthChecker": "Marker for transport-specific {@link BaseHealthChecker} beans.",
        "MqttTransportHealthChecker": "Publishes test MQTT telemetry and validates WebSocket echo.",
        "HttpTransportHealthChecker": "POSTs test telemetry over HTTP transport API.",
        "CoapTransportHealthChecker": "Sends CoAP test payload to the transport endpoint.",
        "Lwm2mTransportHealthChecker": "LwM2M observe/write probe for transport health.",
        "MonitoringConfig": "Base configuration: list of {@link MonitoringTarget} URLs and shared options.",
        "MonitoringTarget": "One monitored ThingsBoard/transport base URL and per-target device id.",
        "TransportMonitoringConfig": "Transport-type-specific monitoring configuration (targets, timeouts, QoS).",
        "TransportMonitoringTarget": "Transport monitoring target with queue name and device naming prefix.",
        "TransportType": "Enum of supported transports and their health-checker Spring bean class.",
        "MonitoredServiceKey": "Keys for login, WebSocket, EDQS, and per-transport failure reporting.",
        "Latencies": "Constants for latency metric names (login, subscribe, per-transport request/ws).",
        "Latency": "Single latency sample with formatted value for notifications.",
        "ServiceFailureException": "Thrown when a monitored step fails; carries {@link MonitoredServiceKey}.",
        "Notification": "Base type for Slack/info/failure/recovery messages.",
        "ServiceFailureNotification": "Alert text when a monitored service exceeds failure threshold.",
        "ServiceRecoveryNotification": "Alert when a previously failing service succeeds again.",
        "HighLatencyNotification": "Alert when one or more steps exceed configured latency threshold.",
        "InfoNotification": "Informational message (startup/shutdown).",
        "AffectedService": "Describes which service failed in a composite notification.",
        "ShortNameProvider": "Short display names for monitored services in notifications.",
        "TbStopWatch": "NanoTime stopwatch for latency measurement in health checks.",
        "ResourceUtils": "Loads classpath resources (LwM2M models, rule chain JSON) for setup.",
        "EntityDataCmd": "WebSocket command wrapper for entity data subscription.",
        "EntityDataUpdate": "Parsed WebSocket entity data update payload.",
        "LatestValueCmd": "WebSocket command to read latest telemetry for entity keys.",
        "CmdsWrapper": "Batch of WebSocket commands sent to ThingsBoard.",
    }
    if name in docs:
        return docs[name]
    if name.endswith("Test"):
        return f"Unit test for monitoring {humanize(name.replace('Test', ''))}."
    if name.endswith("TransportMonitoringConfig"):
        return f"Configuration for {humanize(name.replace('TransportMonitoringConfig', ''))} transport monitoring."
    if name.endswith("HealthChecker"):
        return f"Health checker for {humanize(name.replace('TransportHealthChecker', '').replace('HealthChecker', ''))} transport."
    if kind == "interface":
        return f"Monitoring {humanize(name)} contract."
    return f"Monitoring component: {humanize(name)}."


def indent_javadoc(indent: str, lines: list[str]) -> str:
    inner = "\n".join(f"{indent} * {line}" if line else f"{indent} *" for line in lines)
    return f"{indent}/**\n{inner}\n{indent} */\n"


def find_insert_position(body: str, decl_start: int) -> int:
    line_start = body.rfind("\n", 0, decl_start) + 1
    pos = line_start
    while pos > 0:
        prev_nl = body.rfind("\n", 0, pos - 1)
        prev_start = prev_nl + 1
        prev = body[prev_start:pos].strip()
        if not prev or prev.startswith("@") or prev.endswith(")"):
            pos = prev_start
            continue
        break
    return pos


def detect_kind(line: str) -> str:
    for k in ("class", "interface", "enum", "record"):
        if re.search(rf"\b{k}\b", line):
            return k
    return "class"


def has_javadoc_before(body: str, pos: int) -> bool:
    return body[:pos].rstrip().endswith("*/")


def process_file(path: Path, dry_run: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    m = DECL_LINE_RE.search(body)
    if not m:
        return False
    if has_javadoc_before(body, find_insert_position(body, m.start())):
        return False
    name = m.group("name")
    kind = detect_kind(m.group(0))
    indent = m.group("indent")
    insert_at = find_insert_position(body, m.start())
    body = body[:insert_at] + indent_javadoc(indent, [monitoring_class_doc(name, kind)]) + body[insert_at:]
    if not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return True


def main() -> int:
    p = argparse.ArgumentParser()
    p.add_argument("root", nargs="?", default="monitoring")
    p.add_argument("--dry-run", action="store_true")
    args = p.parse_args()
    n = 0
    for path in sorted(Path(args.root).rglob("*.java")):
        if process_file(path, args.dry_run):
            n += 1
            print(path)
    print(f"Updated {n} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
