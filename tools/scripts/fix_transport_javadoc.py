#!/usr/bin/env python3
"""
Clean up and enrich Javadoc in common/transport Java sources.
No logic changes.
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
)

CLASS_DOCS: dict[str, str] = {
    "TransportService": (
        "Bridge API from <strong>transport microservices</strong> (MQTT, HTTP, CoAP, LwM2M, SNMP) "
        "to the ThingsBoard core.\n\n"
        "<p>Validates device credentials, loads profiles, posts telemetry/attributes to queues, "
        "and manages RPC, OTA, and session lifecycle. Request/response types are protobuf "
        "({@code org.thingsboard.server.gen.transport})."
    ),
    "DefaultTransportService": (
        "Default {@link TransportService} implementation: credential validation via transport API queue, "
        "telemetry/attribute posting, RPC session tracking, and OTA chunk delivery."
    ),
    "TransportContext": (
        "Shared Spring context for transport microservices: {@link TransportService}, "
        "profile caches, rate limits, and scheduling."
    ),
    "SessionContext": (
        "Base contract for an active device transport session.\n\n"
        "<p>Tracks session id, MQTT message ids, and reacts to device/profile updates "
        "propagated from core."
    ),
    "DeviceAwareSessionContext": (
        "Session context bound to an authenticated {@link org.thingsboard.server.common.data.Device}.\n\n"
        "<p>Extends {@link SessionContext} with device info, subscriptions, and activity reporting."
    ),
    "MqttDeviceAwareSessionContext": (
        "MQTT-specific {@link DeviceAwareSessionContext}: topic filters, QoS per topic, "
        "Sparkplug/gateway flags, and protobuf adaptor selection."
    ),
    "DeviceSessionCtx": (
        "Per-connection MQTT session state: auth result, {@link MqttTransportAdaptor}, "
        "gateway/Sparkplug handlers, and {@link org.thingsboard.server.common.transport.TransportService} callbacks."
    ),
    "GatewaySessionHandler": (
        "Manages gateway child-device sessions over a single MQTT connection.\n\n"
        "<p>Handles connect/disconnect/telemetry/attributes topics under "
        "{@code v1/gateway/…} and coordinates per-device {@link GatewayDeviceSessionContext} instances."
    ),
    "AbstractGatewaySessionHandler": (
        "Shared gateway session logic: child device registration, topic routing, and "
        "delegation to {@link MqttTransportAdaptor}."
    ),
    "GatewayDeviceSessionContext": (
        "Session context for a single child device behind an MQTT gateway."
    ),
    "SparkplugDeviceSessionContext": (
        "Session context for a Sparkplug B device node under an edge gateway connection."
    ),
    "SparkplugNodeSessionHandler": (
        "Handles Sparkplug B NBIRTH/NDEATH/DDATA/DCMD topic flows and metric conversion."
    ),
    "MqttTransportAdaptor": (
        "Converts MQTT {@link io.netty.handler.codec.mqtt.MqttPublishMessage} payloads to transport "
        "protobuf messages and back.\n\n"
        "<p>Implementations: {@link JsonMqttAdaptor}, {@link ProtoMqttAdaptor}, "
        "{@link BackwardCompatibilityAdaptor}. Topic layout follows "
        "{@link org.thingsboard.server.common.data.device.profile.MqttTopics}."
    ),
    "JsonMqttAdaptor": (
        "JSON payload adaptor for default MQTT device API topics (telemetry, attributes, RPC, provision, OTA)."
    ),
    "ProtoMqttAdaptor": (
        "Protobuf payload adaptor for MQTT device API when device profile transport type is PROTOBUF."
    ),
    "BackwardCompatibilityAdaptor": (
        "Delegates to {@link JsonMqttAdaptor} or {@link ProtoMqttAdaptor} based on device profile "
        "and supports legacy topic formats."
    ),
    "MqttTransportHandler": (
        "Netty {@link io.netty.channel.ChannelInboundHandler} for MQTT transport.\n\n"
        "<p>Handles CONNECT auth, SUBSCRIBE, PUBLISH routing to {@link MqttTransportAdaptor}, "
        "gateway and Sparkplug topics, session limits, and disconnect cleanup."
    ),
    "MqttTransportService": (
        "Starts the Netty MQTT server and wires {@link MqttTransportHandler} into the pipeline."
    ),
    "MqttTransportContext": (
        "MQTT transport configuration: SSL, proxy IP filters, topic filters, and session limits."
    ),
    "MqttTransportServerInitializer": (
        "Netty child-channel initializer: SSL handler, MQTT codec, idle timeout, "
        "and {@link MqttTransportHandler}."
    ),
    "CoapTransportAdaptor": (
        "Converts CoAP request payloads to transport protobuf and builds CoAP responses."
    ),
    "JsonCoapAdaptor": (
        "JSON CoAP adaptor mirroring the HTTP device API payload format."
    ),
    "ProtoCoapAdaptor": (
        "Protobuf CoAP adaptor for device profiles configured with PROTOBUF transport."
    ),
    "CoapAdaptorUtils": (
        "Shared helpers for CoAP adaptors: content-format detection and response building."
    ),
    "AbstractCoapTransportResource": (
        "Base Californium resource for the device API: token auth from URI path, "
        "delegates to {@link TransportService}."
    ),
    "CoapTransportService": (
        "Registers CoAP device API resources with the Californium server."
    ),
    "CoapTransportContext": (
        "CoAP transport beans: timeouts, DTLS session storage, and {@link CoapClientContext}."
    ),
    "DeviceApiController": (
        "Device-facing HTTP API under {@code /api/v1/{deviceToken}/…}.\n\n"
        "<p>Endpoints: telemetry, attributes, RPC, claim, provision, and OTA — "
        "mirrored by CoAP and MQTT adaptors."
    ),
    "HttpTransportContext": (
        "Spring context for HTTP transport: {@link TransportService}, request timeouts, SSL."
    ),
    "TransportSecurityConfiguration": (
        "Spring Security for {@code /api/v1/**}: permits device API paths; token validated per request."
    ),
    "LwM2MTransportAdaptor": (
        "Converts LwM2M object/resource values to ThingsBoard telemetry and attribute protobuf messages."
    ),
    "LwM2MJsonAdaptor": (
        "JSON serialization adaptor for LwM2M observe/read results sent to the rule engine."
    ),
    "LwM2mUplinkMsgHandler": (
        "Handles LwM2M uplink operations: registration, observe notifications, writes, and "
        "forwards converted data to {@link TransportService}."
    ),
    "DefaultLwM2mUplinkMsgHandler": (
        "Default {@link LwM2mUplinkMsgHandler}: registration lifecycle, observe analysis, "
        "telemetry/attribute posting, and RPC response handling."
    ),
    "LwM2mDownlinkMsgHandler": (
        "Processes server-initiated LwM2M downlink: read, write, execute, observe, and composite requests."
    ),
    "DefaultLwM2mDownlinkMsgHandler": (
        "Default {@link LwM2mDownlinkMsgHandler} implementation using Eclipse Leshan client callbacks."
    ),
    "LwM2MRpcRequestHandler": (
        "Maps ThingsBoard device RPC requests to LwM2M downlink operations on registered clients."
    ),
    "LwM2MSessionManager": (
        "Manages LwM2M client registration sessions and links them to ThingsBoard device sessions."
    ),
    "DefaultLwM2MSessionManager": (
        "Default {@link LwM2MSessionManager}: registration store integration and session event reporting."
    ),
    "LwM2mTransportContext": (
        "LwM2M transport Spring context: server config, DTLS, model provider, OTA, and session manager."
    ),
    "TransportAdaptor": (
        "Marker for protocol-specific payload adaptors that convert wire format to transport protobuf."
    ),
    "SessionMsgListener": (
        "Callback interface for messages pushed from core to an active transport session "
        "(attributes, RPC, OTA chunks)."
    ),
    "DeviceAuthService": (
        "Validates device credentials (token, MQTT basic, X.509) via {@link TransportService}."
    ),
}

PARAM_REPLACEMENTS = {
    "calculated-field execution context": "MQTT session context",
    "if adaptor exception is thrown during processing": "on invalid payload or topic format",
    "if an unexpected error occurs during processing": "on processing failure",
    "the int result": "monotonically increasing MQTT packet identifier",
    "the return new value": "constructed MQTT publish message",
    "the new value": "constructed value",
    "optional {@link MqttMessage}, empty if not found": "MQTT publish message, or empty if conversion is not applicable",
}


def javadoc_score(lines: list[str]) -> int:
    block = "\n".join(lines)
    score = len(block)
    if "<p>" in block or "{@link" in block:
        score += 200
    if "ThingsBoard common module" in block:
        score -= 100
    if block.strip().endswith("contract.") or " contract." in block:
        score -= 50
    return score


def extract_javadoc_block(lines: list[str], start: int) -> tuple[list[str], int]:
    block = [lines[start]]
    i = start + 1
    while i < len(lines):
        block.append(lines[i])
        if lines[i].strip().endswith("*/"):
            return block, i + 1
        i += 1
    return block, start + 1


def dedupe_consecutive_javadocs_lines(lines: list[str]) -> list[str]:
    out: list[str] = []
    i = 0
    while i < len(lines):
        if lines[i].strip().startswith("/**"):
            blocks: list[list[str]] = []
            j = i
            while j < len(lines) and lines[j].strip().startswith("/**"):
                block, j = extract_javadoc_block(lines, j)
                blocks.append(block)
            if len(blocks) > 1:
                best = max(blocks, key=javadoc_score)
                out.extend(best)
            else:
                out.extend(blocks[0])
            i = j
            continue
        out.append(lines[i])
        i += 1
    return out


def collapse_javadocs_before_declarations(lines: list[str]) -> list[str]:
    """Keep a single best Javadoc block immediately before each type declaration."""
    i = 0
    while i < len(lines):
        m = DECL_LINE_RE.match(lines[i])
        if not m:
            i += 1
            continue
        start = i
        while start > 0:
            prev = lines[start - 1].strip()
            if not prev or prev.startswith("@") or prev.endswith(")"):
                start -= 1
                continue
            break
        j = start
        blocks: list[tuple[int, int, list[str]]] = []
        while j > 0 and lines[j - 1].strip().endswith("*/"):
            bend = j - 1
            bstart = bend
            while bstart > 0 and not lines[bstart - 1].strip().startswith("/**"):
                bstart -= 1
            if lines[bstart].strip().startswith("/**"):
                block = lines[bstart:j]
                blocks.insert(0, (bstart, j, block))
                j = bstart
            else:
                break
        if len(blocks) > 1:
            best = max(blocks, key=lambda x: javadoc_score(x[2]))[2]
            first_start = blocks[0][0]
            last_end = blocks[-1][1]
            lines = lines[:first_start] + best + lines[last_end:]
            i = first_start + len(best)
            continue
        i += 1
    return lines


def split_license_body_lines(lines: list[str]) -> tuple[list[str], list[str]]:
    for i, line in enumerate(lines):
        if line.startswith("package "):
            return lines[:i], lines[i:]
    return [], lines


def restore_license_header(license_lines: list[str]) -> list[str]:
    """Restore standard ThingsBoard license block formatting."""
    if not license_lines:
        return license_lines
    text = "\n".join(license_lines)
    if "Copyright" not in text:
        return license_lines
    standard = """/**
 * Copyright © 2016-2026 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */"""
    return standard.split("\n")


def remove_blank_line_after_javadoc(lines: list[str]) -> list[str]:
    out: list[str] = []
    i = 0
    while i < len(lines):
        out.append(lines[i])
        if lines[i].strip() == "*/" and i + 1 < len(lines) and lines[i + 1].strip() == "":
            nxt = i + 2
            while nxt < len(lines) and lines[nxt].strip() == "":
                nxt += 1
            if nxt < len(lines):
                nxt_line = lines[nxt].strip()
                if nxt_line and not nxt_line.startswith("@") and not nxt_line.startswith("//"):
                    i += 1
                    while i + 1 < nxt and lines[i + 1].strip() == "":
                        i += 1
        i += 1
    return out


def reformat_javadoc_blocks(lines: list[str]) -> list[str]:
    out: list[str] = []
    i = 0
    while i < len(lines):
        stripped = lines[i].lstrip()
        if stripped.startswith("/**"):
            block, next_i = extract_javadoc_block(lines, i)
            base_indent = lines[i][: len(lines[i]) - len(lines[i].lstrip())]
            content_indent = base_indent + " "
            out.append(f"{base_indent}/**")
            for bl in block[1:-1]:
                inner = bl.lstrip()
                if inner in ("*", "*  *", "* *"):
                    out.append(f"{content_indent}*")
                    continue
                if inner.startswith("*"):
                    text = inner[1:].lstrip()
                    if text.startswith("* "):
                        text = text[2:]
                    elif text.startswith("*"):
                        text = text[1:].lstrip()
                    out.append(f"{content_indent}* {text}" if text else f"{content_indent}*")
                else:
                    out.append(bl)
            out.append(f"{base_indent} */")
            i = next_i
            continue
        out.append(lines[i])
        i += 1
    return out


def fix_annotation_placement_lines(lines: list[str]) -> list[str]:
    i = 0
    while i < len(lines):
        line = lines[i]
        if line.strip().startswith("@"):
            ann_start = i
            while i < len(lines) and (lines[i].strip().startswith("@") or lines[i].strip() == ""):
                if lines[i].strip().startswith("@"):
                    i += 1
                elif lines[i].strip() == "":
                    i += 1
                else:
                    break
            ann_end = i
            while ann_end < len(lines) and lines[ann_end].strip() == "":
                ann_end += 1
            if ann_end < len(lines) and lines[ann_end].strip().startswith("/**"):
                block, after = extract_javadoc_block(lines, ann_end)
                decl_i = after
                while decl_i < len(lines) and lines[decl_i].strip() == "":
                    decl_i += 1
                if decl_i < len(lines) and DECL_LINE_RE.match(lines[decl_i]):
                    new_lines = lines[:ann_start] + block + lines[ann_start:ann_end] + lines[after:]
                    return fix_annotation_placement_lines(new_lines)
        i += 1
    return lines


def fix_method_javadoc_indent_lines(lines: list[str]) -> list[str]:
    out: list[str] = []
    i = 0
    while i < len(lines):
        line = lines[i]
        if (
            i > 0
            and lines[i - 1].rstrip().endswith(";")
            and line.strip().startswith("/**")
            and not line.startswith("    /**")
        ):
            j = i + 1
            while j < len(lines) and not lines[j].strip().endswith("*/"):
                j += 1
            if j < len(lines):
                j += 1
            indent = "    "
            if j < len(lines):
                nxt = lines[j]
                if nxt.startswith(" ") and not nxt.strip().startswith("*"):
                    indent = nxt[: len(nxt) - len(nxt.lstrip())]
            if out and out[-1].strip():
                out.append("")
            block, next_i = extract_javadoc_block(lines, i)
            for bl in block:
                stripped = bl.lstrip()
                if stripped.startswith("/**"):
                    out.append(indent + stripped)
                elif stripped.startswith("*") or stripped == "*/":
                    out.append(indent + stripped)
                else:
                    out.append(bl)
            i = next_i
            continue
        out.append(line)
        i += 1
    return out


def remove_inline_new_javadocs_lines(lines: list[str]) -> list[str]:
    out: list[str] = []
    i = 0
    while i < len(lines):
        if lines[i].strip().startswith("/**"):
            prev = out[-1].rstrip() if out else ""
            block, next_i = extract_javadoc_block(lines, i)
            j = next_i
            while j < len(lines) and lines[j].strip() == "":
                j += 1
            if prev.endswith("=") or prev.endswith("("):
                if j < len(lines) and lines[j].strip().startswith("new "):
                    i = next_i
                    continue
            out.extend(block)
            i = next_i
            continue
        out.append(lines[i])
        i += 1
    return out


def apply_param_replacements(content: str) -> str:
    for old, new in PARAM_REPLACEMENTS.items():
        content = content.replace(old, new)
    return content


def format_class_javadoc(indent: str, text: str) -> list[str]:
    result = [f"{indent}/**"]
    for line in text.split("\n"):
        result.append(f"{indent} * {line}" if line else f"{indent} *")
    result.append(f"{indent} */")
    return result


def find_decl_javadoc_range(lines: list[str], decl_i: int) -> tuple[int, int] | None:
    start = decl_i
    while start > 0:
        prev = lines[start - 1].strip()
        if not prev or prev.startswith("@") or prev.endswith(")"):
            start -= 1
            continue
        break
    if start == 0 or not lines[start - 1].strip().endswith("*/"):
        return None
    bend = start
    bstart = bend - 1
    while bstart > 0 and not lines[bstart - 1].strip().startswith("/**"):
        bstart -= 1
    if lines[bstart].strip().startswith("/**"):
        return bstart, bend
    return None


def upgrade_class_javadoc_lines(lines: list[str]) -> list[str]:
    i = 0
    while i < len(lines):
        m = DECL_LINE_RE.match(lines[i])
        if not m or m.group("name") not in CLASS_DOCS:
            i += 1
            continue
        name = m.group("name")
        indent = m.group("indent")
        rng = find_decl_javadoc_range(lines, i)
        new_block = format_class_javadoc(indent, CLASS_DOCS[name])
        if rng:
            bstart, bend = rng
            lines = lines[:bstart] + new_block + lines[bend:]
        else:
            insert = i
            while insert > 0:
                prev = lines[insert - 1].strip()
                if not prev or prev.startswith("@") or prev.endswith(")"):
                    insert -= 1
                    continue
                break
            lines = lines[:insert] + new_block + lines[insert:]
        i += len(new_block) + 1
    return lines


def process_content(content: str) -> str:
    content = apply_param_replacements(content)
    lines = content.split("\n")
    license_lines, body_lines = split_license_body_lines(lines)
    license_lines = restore_license_header(license_lines)
    body_lines = dedupe_consecutive_javadocs_lines(body_lines)
    body_lines = collapse_javadocs_before_declarations(body_lines)
    body_lines = fix_annotation_placement_lines(body_lines)
    body_lines = fix_method_javadoc_indent_lines(body_lines)
    body_lines = remove_inline_new_javadocs_lines(body_lines)
    body_lines = upgrade_class_javadoc_lines(body_lines)
    body_lines = dedupe_consecutive_javadocs_lines(body_lines)
    body_lines = collapse_javadocs_before_declarations(body_lines)
    body_lines = reformat_javadoc_blocks(body_lines)
    body_lines = remove_blank_line_after_javadoc(body_lines)
    return "\n".join(license_lines + body_lines)


def process_file(path: Path) -> bool:
    text = path.read_text(encoding="utf-8")
    fixed = process_content(text)
    if fixed != text:
        path.write_text(fixed, encoding="utf-8", newline="\n")
        return True
    return False


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("root", nargs="?", default="common/transport")
    args = parser.parse_args()
    root = Path(args.root)
    n = 0
    for p in sorted(root.rglob("*.java")):
        posix = p.as_posix()
        if "src/main/java" not in posix:
            continue
        if "/test/" in posix or "\\test\\" in posix:
            continue
        if p.name == "package-info.java":
            continue
        if process_file(p):
            n += 1
    print(f"Fixed {n} files", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
