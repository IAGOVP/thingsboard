#!/usr/bin/env python3
"""
Add class- and public/protected method-level Javadoc to netty-mqtt/ Java sources.
Includes @param, @return, and @throws tags. Skips package-info.java.
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

NETTY_MQTT_ROOTS = [
    Path("netty-mqtt/src/main/java"),
    Path("netty-mqtt/src/test/java"),
]

NETTY_MQTT_CLASS_DOCS: dict[str, list[str]] = {
    "MqttClient": [
        "Public MQTT 3.x client API over Netty.",
        "",
        "<p>Provides connect, subscribe, publish, unsubscribe, and disconnect. "
        "Obtain an instance via {@link #create(MqttClientConfig, MqttHandler, ListeningExecutor)}.",
    ],
    "MqttClientImpl": [
        "Netty-based MQTT 3.x client implementation.",
        "",
        "<p>Supports automatic reconnect, pending QoS 1/2 flows, subscription routing to "
        "{@link MqttHandler}, and retransmission via {@link RetransmissionHandler}. "
        "Pipeline: {@code MqttDecoder} → {@link MqttChannelHandler} → {@link MqttPingHandler} → {@code MqttEncoder}.",
    ],
    "MqttChannelHandler": [
        "Inbound Netty channel handler for MQTT wire protocol.",
        "",
        "<p>Decodes CONNACK, SUBACK, UNSUBACK, PUBLISH, PUBACK, PUBREC, PUBREL, PUBCOMP and "
        "updates {@link MqttClientImpl} pending-operation state.",
    ],
    "MqttClientConfig": [
        "MQTT connection configuration.",
        "",
        "<p>SSL/TLS, credentials, last will ({@link MqttLastWill}), reconnect strategy, "
        "retransmission limits, and Netty channel class.",
    ],
    "MqttHandler": [
        "Callback invoked when a PUBLISH arrives for a subscribed topic filter.",
        "",
        "<p>Registered per subscription via {@link MqttClient#on(String, MqttHandler)}.",
    ],
    "MqttClientCallback": [
        "Client lifecycle callbacks for connection loss and reconnect.",
        "",
        "<p>Optional hooks: {@link #connectionLost(Throwable)}, reconnect attempts, and successful reconnect.",
    ],
    "MqttConnectResult": [
        "Result of MQTT CONNECT/CONNACK handshake.",
        "",
        "<p>Includes {@link io.netty.handler.codec.mqtt.MqttConnectReturnCode} and session-present flag.",
    ],
    "MqttSubscription": [
        "Client-side subscription: topic filter, QoS, handler, and optional once-flag.",
    ],
    "MqttPendingPublish": [
        "Outbound PUBLISH awaiting broker acknowledgment (PUBACK or QoS2 completion).",
        "",
        "<p>May use {@link RetransmissionHandler} when unacknowledged.",
    ],
    "MqttPendingSubscription": [
        "SUBSCRIBE message sent; awaiting SUBACK from broker.",
    ],
    "MqttPendingUnsubscription": [
        "UNSUBSCRIBE message sent; awaiting UNSUBACK from broker.",
    ],
    "MqttIncomingQos2Publish": [
        "Inbound QoS 2 PUBLISH during PUBREC/PUBREL/PUBCOMP handshake.",
    ],
    "MqttPingHandler": [
        "Netty handler that sends MQTT PINGREQ on channel idle and processes PINGRESP.",
    ],
    "MqttLastWill": [
        "Last will and testament published by broker if client disconnects unexpectedly.",
    ],
    "RetransmissionHandler": [
        "Schedules exponential-backoff retransmission for unacknowledged MQTT messages.",
    ],
    "PendingOperation": [
        "Cancellable pending MQTT operation (subscribe, publish, or unsubscribe).",
    ],
    "ReconnectStrategy": [
        "Computes delay in milliseconds before the next TCP reconnect attempt.",
    ],
    "ReconnectStrategyExponential": [
        "Exponential backoff implementation of {@link ReconnectStrategy}.",
    ],
    "ChannelClosedException": [
        "Thrown when an MQTT operation is attempted on a closed Netty channel.",
    ],
    "MaxRetransmissionsReachedException": [
        "Thrown when {@link RetransmissionHandler} exhausts the configured maximum attempts.",
    ],
    "MqttTestProxy": [
        "Test-side MQTT broker proxy for integration tests.",
    ],
}

NETTY_MQTT_METHOD_DOCS: dict[str, str] = {
    "connect": "Opens TCP connection to the broker and completes MQTT CONNECT/CONNACK handshake.",
    "reconnect": "Reconnects to the last host/port used by {@link #connect(String, int)}.",
    "disconnect": "Sends MQTT DISCONNECT and closes the Netty channel.",
    "on": "Subscribes to a topic filter and registers a {@link MqttHandler} for incoming PUBLISH messages.",
    "off": "Unsubscribes from a topic filter and removes the handler.",
    "publish": "Publishes a message to the given topic with the specified QoS.",
    "create": "Factory method that constructs a configured {@link MqttClientImpl}.",
    "channelRead0": "Dispatches decoded MQTT frames to type-specific handler methods.",
    "handleConack": "Processes CONNACK: completes connect promise and resumes pending subscriptions.",
    "handlePublish": "Routes inbound PUBLISH to subscription handlers and sends PUBACK/PUBREC as required.",
    "handleSubAck": "Completes pending SUBSCRIBE operations from SUBACK packet ids.",
    "handlePuback": "Completes pending QoS 1 PUBLISH operations from PUBACK.",
    "handleUnsuback": "Completes pending UNSUBSCRIBE operations from UNSUBACK.",
    "onMessage": "Application callback for one received PUBLISH payload on a subscribed topic.",
    "connectionLost": "Called when the TCP/MQTT connection is lost unexpectedly.",
    "onSuccessfulReconnect": "Called after a successful automatic reconnect.",
    "getDelay": "Returns milliseconds to wait before the next reconnect attempt.",
    "start": "Schedules the first retransmission timer for an unacknowledged packet.",
    "stop": "Cancels pending retransmission timers.",
    "isConnected": "Returns true when the Netty channel is active and CONNACK succeeded.",
    "getEventLoop": "Returns the Netty {@link io.netty.channel.EventLoopGroup} used for I/O.",
    "setEventLoop": "Overrides the default {@link io.netty.channel.nio.NioEventLoopGroup}.",
    "getHandlerExecutor": "Returns executor used to invoke {@link MqttHandler} callbacks off the event loop.",
    "channelActive": "Netty hook: initiates MQTT CONNECT when the TCP channel becomes active.",
    "channelInactive": "Netty hook: notifies callbacks and triggers reconnect when the channel closes.",
    "exceptionCaught": "Netty hook: logs protocol errors and closes the channel on failure.",
}

NETTY_MQTT_PARAM_HINTS: dict[str, str] = {
    "host": "broker hostname or IP address",
    "port": "broker TCP port (default 1883, 8883 for TLS)",
    "topic": "MQTT topic filter or publish topic name",
    "handler": "callback invoked for matching inbound PUBLISH messages",
    "qos": "MQTT quality of service (AT_MOST_ONCE, AT_LEAST_ONCE, EXACTLY_ONCE)",
    "payload": "message body as Netty {@link io.netty.buffer.ByteBuf}",
    "config": "client connection and behavior configuration",
    "ctx": "Netty channel handler context",
    "msg": "decoded MQTT wire message",
    "channel": "active Netty channel to the broker",
    "connectFuture": "promise completed on successful CONNACK",
    "attempt": "current reconnect attempt number (1-based)",
    "cause": "failure that closed the connection or caused the error",
    "eventLoop": "Netty event loop group for client I/O",
    "executor": "executor for offloading handler callbacks",
}


def class_description(name: str, kind: str, body: str, path: Path) -> list[str]:
    if name in NETTY_MQTT_CLASS_DOCS:
        return NETTY_MQTT_CLASS_DOCS[name]
    is_test = "/test/" in path.as_posix()
    if name.endswith("Test"):
        return [f"Unit test for netty-mqtt {humanize(name.replace('Test', ''))}."]
    if kind == "interface":
        return [f"MQTT client {humanize(name)} contract."]
    if kind == "enum":
        return [f"Enumerates {humanize(name)} values for the MQTT client."]
    if kind == "record":
        return [f"Configuration record for {humanize(name)}."]
    ctx = "netty-mqtt tests" if is_test else "netty-mqtt client library"
    return [f"{humanize(name).capitalize()} ({ctx})."]


def class_javadoc_is_incomplete(doc: str, class_name: str) -> bool:
    if class_name in NETTY_MQTT_CLASS_DOCS and len(doc) < 90:
        return True
    thin = (
        "Inbound Netty handler:",
        "Netty MQTT component:",
        "Public MQTT client API:",
        "contract.",
    )
    if any(t in doc for t in thin) and "<p>" not in doc and len(doc) < 150:
        return True
    if len(doc.strip()) < 60:
        return True
    return False


def add_class_javadoc_at(
    body: str, decl_match: re.Match, class_name: str, kind: str, path: Path, force: bool
) -> tuple[str, bool]:
    insert_pos = find_insert_position(body, decl_match.start())
    new_doc = format_javadoc(decl_match.group("indent"), class_description(class_name, kind, body, path))
    existing = javadoc_block_before(body, insert_pos)
    if existing:
        start, end = existing
        if not force and not class_javadoc_is_incomplete(body[start:end], class_name):
            return body, False
        body = body[:start] + body[end:]
        insert_pos = start
    return insert_text_at(body, insert_pos, new_doc), True


def add_all_class_javadocs(body: str, path: Path, force: bool) -> tuple[str, int]:
    count = 0
    for m in reversed(list(DECL_LINE_RE.finditer(body))):
        name = m.group("name")
        kind = detect_kind(m.group(0))
        body, changed = add_class_javadoc_at(body, m, name, kind, path, force)
        if changed:
            count += 1
    return body, count


def mqtt_param_description(name: str, type_hint: str) -> str:
    if name in NETTY_MQTT_PARAM_HINTS:
        return NETTY_MQTT_PARAM_HINTS[name]
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


def method_javadoc_is_thin(doc: str, method_name: str) -> bool:
    if "@return" not in doc:
        return True
    if "@param" in doc and len(doc.strip()) > 100:
        return False
    thin_starts = (
        "Dispatches decoded",
        "Initialize.",
    )
    first = ""
    for line in doc.split("\n"):
        t = line.strip().lstrip("* ").strip()
        if t and t not in ("/**", "*/"):
            first = t
            break
    if first in thin_starts or (first.endswith(".") and len(first) < 40 and "@param" not in doc):
        return True
    return False


def mqtt_method_javadoc(body: str, method) -> str:
    raw = method_line_indent(body, method.sig_start) or method.indent or "    "
    indent = "    " if len(raw.replace("\t", "    ")) > 4 else raw
    desc = NETTY_MQTT_METHOD_DOCS.get(method.name, method_description(method.name))
    lines = [desc, ""]
    for p in method.params:
        lines.append(f"@param {p.name} {mqtt_param_description(p.name, p.type_hint)}")
    ret = format_return(method.return_type)
    if method.return_type.strip().startswith("Promise<") or method.return_type.strip().startswith("Future<"):
        lines.append(f"@return Netty future completing with {ret}")
    else:
        lines.append(f"@return {ret}")
    if method.throws:
        for t in method.throws:
            lines.append(f"@throws {t} {throws_description(t)}")
    elif method.name == "reconnect":
        lines.append("@throws IllegalStateException when no previous {@link #connect(String, int)} was attempted")
    else:
        lines.append("@throws Exception if an unexpected error occurs during processing")
    return format_javadoc(indent, lines)


def add_mqtt_method_javadocs(body: str, decl_match, class_name: str, kind: str) -> tuple[str, int]:
    count = 0
    class_start, _, _ = class_body_bounds_at(body, decl_match)
    methods = find_class_methods(body, class_name, kind, decl_match)
    max_passes = max(len(methods) + 3, 1)
    for _ in range(max_passes):
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
                    (javadoc_is_complete(doc, method) or ("@param" in doc and "@return" in doc and len(doc) > 100))
                    and not method_javadoc_needs_fixup(body, method.doc_start, method.sig_start)
                    and not method_javadoc_is_thin(doc, method.name)
                ):
                    continue
            pending = method
            break
        if pending is None:
            break
        pos = max(pending.doc_start, class_start + 1)
        body, pos = strip_all_javadocs_before(body, pos)
        new_doc = mqtt_method_javadoc(body, pending)
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
    changed = False
    body, cc = add_all_class_javadocs(body, path, force_class)
    if cc:
        changed = True
    for decl_match in DECL_LINE_RE.finditer(body):
        class_name = decl_match.group("name")
        kind = detect_kind(decl_match.group(0))
        body, mc = add_mqtt_method_javadocs(body, decl_match, class_name, kind)
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
    parser = argparse.ArgumentParser(description="Add detailed Javadoc to netty-mqtt sources")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--force-class", action="store_true", help="Replace thin class Javadoc")
    args = parser.parse_args()
    main_n = test_n = 0
    for path in collect_files(NETTY_MQTT_ROOTS):
        if process_file(path, args.dry_run, args.force_class):
            if "/test/" in path.as_posix():
                test_n += 1
            else:
                main_n += 1
    print(f"main: {main_n} files modified", file=sys.stderr)
    print(f"test: {test_n} files modified", file=sys.stderr)
    print(f"Done: {main_n + test_n} files modified", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
