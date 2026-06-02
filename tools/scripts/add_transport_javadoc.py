#!/usr/bin/env python3
"""
Add class-level Javadoc to transport deployables (transport/) and common/transport Java sources.
Uses transport-specific descriptions for entry points, handlers, and adaptors.
"""
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

CLASS_DOCS: dict[str, str] = {
    "ThingsboardHttpTransportApplication": (
        "Spring Boot entry for the HTTP transport microservice. Loads tb-http-transport.yml, "
        "scans org.thingsboard.server.transport.http for {@link DeviceApiController} (/api/v1)."
    ),
    "ThingsboardMqttTransportApplication": (
        "Spring Boot entry for the MQTT transport microservice (Netty). Loads tb-mqtt-transport.yml."
    ),
    "ThingsboardCoapTransportApplication": (
        "Spring Boot entry for the CoAP transport microservice (Californium). Loads tb-coap-transport.yml."
    ),
    "ThingsboardLwm2mTransportApplication": (
        "Spring Boot entry for the LwM2M transport microservice. Loads tb-lwm2m-transport.yml."
    ),
    "ThingsboardSnmpTransportApplication": (
        "Spring Boot entry for the SNMP transport microservice. Loads tb-snmp-transport.yml."
    ),
    "DeviceApiController": (
        "Device-facing HTTP API under /api/v1/{deviceToken}/… — telemetry, attributes, RPC, OTA, provision. "
        "See transport/DEVICE_API.md."
    ),
    "HttpTransportContext": "Spring context for HTTP transport: {@link TransportService}, timeouts, SSL.",
    "TransportSecurityConfiguration": "Spring Security for /api/v1/** — device token auth, permit device API paths.",
    "MqttTransportHandler": (
        "Netty inbound handler: MQTT connect/auth, publish routing to {@link MqttTransportAdaptor}, "
        "gateway and Sparkplug topics. Topics in {@link org.thingsboard.server.common.data.device.profile.MqttTopics}."
    ),
    "MqttTransportService": "Starts Netty MQTT server and registers {@link MqttTransportHandler}.",
    "MqttTransportContext": "MQTT transport configuration: SSL, topic filters, session limits.",
    "MqttTransportServerInitializer": "Netty pipeline: SSL, MQTT codec, {@link MqttTransportHandler}.",
    "MqttTransportAdaptor": "Converts MQTT publish payloads to transport protobuf messages.",
    "CoapTransportResource": (
        "Californium resource for device API: CoAP URI /api/v1/{token}/{featureType}/… — mirrors HTTP device API. "
        "See transport/PROTOCOLS.md#coap."
    ),
    "CoapTransportService": "Registers CoAP resources with Californium server.",
    "CoapTransportContext": "CoAP transport beans: timeouts, DTLS session map, client context.",
    "AbstractCoapTransportResource": "Base CoAP resource: auth via access token in URI, delegates to {@link TransportService}.",
    "DefaultTransportService": (
        "Default {@link TransportService}: validates credentials via queue, posts telemetry/attributes, "
        "manages RPC and OTA sessions."
    ),
    "TransportService": (
        "Core bridge from transport microservices to tb-core (queues/protobuf). "
        "See interface Javadoc and transport/README.md."
    ),
    "TransportContext": "Shared transport Spring context: {@link TransportService}, cache, scheduler.",
    "TransportActivityManager": "Tracks device session activity and reports to core.",
    "SnmpTransportContext": "SNMP transport: device list refresh, PDU scheduling, balancing.",
    "LwM2mTransportContext": "LwM2M server configuration, DTLS, model and OTA services.",
    "DefaultLwM2mUplinkMsgHandler": "Handles LwM2M uplink (observe, write, registration) and forwards to core.",
    "MqttTopics": (
        "Canonical MQTT topic constants for device (v1/v2), gateway, provision, firmware, and RPC. "
        "See transport/MQTT_TOPICS.md."
    ),
}


def split_license_and_body(content: str) -> tuple[str, str]:
    m = re.match(r"(\s*/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    return (m.group(1), m.group(2)) if m else ("", content)


def humanize(name: str) -> str:
    return re.sub(r"([a-z])([A-Z])", r"\1 \2", name).replace("_", " ").lower()


def transport_class_doc(name: str, kind: str) -> str:
    if name in CLASS_DOCS:
        return CLASS_DOCS[name]
    if name.endswith("TransportApplication"):
        proto = humanize(name.replace("Thingsboard", "").replace("TransportApplication", ""))
        return f"Spring Boot entry for the {proto} transport microservice."
    if name.endswith("TransportContext"):
        return f"Spring/context beans for {humanize(name.replace('TransportContext', ''))} transport."
    if name.endswith("TransportService") and kind == "class":
        return f"Lifecycle service that starts {humanize(name.replace('TransportService', ''))} transport listeners."
    if name.endswith("TransportResource"):
        return f"Protocol resource/handler for {humanize(name.replace('TransportResource', ''))} device API."
    if name.endswith("TransportHandler"):
        return f"Inbound protocol handler for {humanize(name.replace('TransportHandler', ''))} messages."
    if name.endswith("TransportAdaptor") or name.endswith("Adaptor"):
        return f"Converts {humanize(name.replace('Adaptor', '').replace('Transport', ''))} payloads to transport protobuf."
    if name.endswith("Callback"):
        return f"Async callback for {humanize(name.replace('Callback', ''))} transport operation."
    if name.startswith("TbLwM2M"):
        return f"LwM2M transport: {humanize(name)}."
    if name.endswith("MsgHandler"):
        return f"Handles {humanize(name.replace('MsgHandler', ''))} messages toward core."
    if kind == "interface":
        return f"Transport {humanize(name)} contract."
    if kind == "enum":
        return f"Transport enum: {humanize(name)}."
    return f"Transport component: {humanize(name)}."


def indent_javadoc(indent: str, text: str) -> str:
    return f"{indent}/**\n{indent} * {text}\n{indent} */\n"


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


def has_class_javadoc_near(body: str, decl_start: int) -> bool:
    """True if Javadoc exists immediately before class or between annotations and class."""
    insert = find_insert_position(body, decl_start)
    between = body[insert:decl_start]
    if "/**" in between and "*/" in between:
        return True
    before = body[:insert].rstrip()
    return before.endswith("*/")


def detect_kind(line: str) -> str:
    for k in ("class", "interface", "enum", "record"):
        if re.search(rf"\b{k}\b", line):
            return k
    return "class"


def process_file(path: Path, dry_run: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    m = DECL_LINE_RE.search(body)
    if not m:
        return False
    if has_class_javadoc_near(body, m.start()):
        return False
    name = m.group("name")
    kind = detect_kind(m.group(0))
    indent = m.group("indent")
    insert_at = find_insert_position(body, m.start())
    body = body[:insert_at] + indent_javadoc(indent, transport_class_doc(name, kind)) + body[insert_at:]
    if not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return True


def main() -> int:
    p = argparse.ArgumentParser()
    p.add_argument("roots", nargs="*", default=["transport", "common/transport"])
    p.add_argument("--dry-run", action="store_true")
    args = p.parse_args()
    n = 0
    for root in args.roots:
        root_path = Path(root)
        if not root_path.is_dir():
            print(f"Skip missing: {root_path}", file=sys.stderr)
            continue
        for path in sorted(root_path.rglob("*.java")):
            posix = path.as_posix()
            if "/test/" in posix or "\\test\\" in posix:
                continue
            if path.name == "package-info.java":
                continue
            if process_file(path, args.dry_run):
                n += 1
    print(f"Updated {n} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
