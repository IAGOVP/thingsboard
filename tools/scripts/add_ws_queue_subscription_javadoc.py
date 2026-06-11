#!/usr/bin/env python3
"""
Add detailed class- and method-level Javadoc to ThingsBoard service packages:
  - org.thingsboard.server.service.ws
  - org.thingsboard.server.service.queue
  - org.thingsboard.server.service.subscription
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

DECL_RE = re.compile(
    r"^(?P<indent>\s*)(?:(?:public|protected|private)\s+)?"
    r"(?:(?:abstract|sealed|non-sealed|static|final)\s+)*"
    r"(?:class|interface|enum|record)\s+"
    r"(?P<name>[A-Za-z_][A-Za-z0-9_]*)",
    re.MULTILINE,
)

METHOD_LINE_RE = re.compile(
    r"^(?P<indent>\s+)(?P<sig>"
    r"(?:(?:public|protected)\s+)?"
    r"(?:(?:static|final|abstract|synchronized|native)\s+)*"
    r"(?:[\w<>,\[\]?@.]+\s+)+"
    r"(?P<name>\w+)\s*"
    r"(?P<params>\([^)]*\))"
    r"(?:\s+throws\s+(?P<throws>[\w.\s,]+))?"
    r"\s*(?:\{|;)\s*"
    r")$",
    re.MULTILINE,
)

STUB_JAVADOC_BLOCK = re.compile(
    r"/\*\*\s*\n(?:\s*\*[^\n]*\n)*?\s*\*\s*Created by[^\n]*\n(?:\s*\*[^\n]*\n)*?\s*\*/\s*\n?",
    re.MULTILINE,
)

STUB_JAVADOC = re.compile(r"/\*\*\s*\n\s*\*\s*Created by", re.MULTILINE)

CLASS_DOCS: dict[str, str] = {
    "WebSocketService": (
        "Service API for the ThingsBoard WebSocket plugin ({@code /api/ws}). "
        "Routes session lifecycle events, inbound commands, subscription updates, and errors to connected UI clients."
    ),
    "DefaultWebSocketService": (
        "Default {@link WebSocketService} implementation on tb-core. "
        "Authenticates sessions, dispatches telemetry/notification commands to {@link org.thingsboard.server.service.subscription.TbLocalSubscriptionService}, "
        "and pushes {@link org.thingsboard.server.service.ws.telemetry.sub.TelemetrySubscriptionUpdate} payloads back to browsers."
    ),
    "WebSocketSessionRef": (
        "Lightweight handle to an open WebSocket session: session id, tenant, user, and outbound message sender."
    ),
    "WebSocketMsgEndpoint": (
        "Abstraction for sending text/binary WebSocket frames to a connected client session."
    ),
    "WsSessionMetaData": "Per-session metadata tracked by {@link DefaultWebSocketService} (auth state, ping counters, command ids).",
    "WsCmd": "Marker for inbound WebSocket command DTOs deserialized from JSON; exposes command id and {@link WsCmdType}.",
    "WsCmdType": "Discriminator for WebSocket command/update message types exchanged on {@code /api/ws}.",
    "WsCommandsWrapper": "Root JSON envelope for a batch of WebSocket commands sent by the UI in one frame.",
    "AuthCmd": "WebSocket command carrying JWT or login token to authenticate the session before other subscriptions.",
    "TbCoreConsumerService": (
        "tb-core queue consumer contract. Listens for {@link org.thingsboard.server.queue.discovery.event.PartitionChangeEvent} "
        "and drives consumption of {@code ToCoreMsg} from the core queue."
    ),
    "DefaultTbCoreConsumerService": (
        "Consumes {@code ToCoreMsg} on tb-core: telemetry/attribute updates, RPC, notifications, subscription events, "
        "usage stats, OTA, and device-actor messages. Dispatches work to actors and subscription services."
    ),
    "TbRuleEngineConsumerService": "Contract for the rule-engine queue consumer that processes {@code ToRuleEngineMsg} packs.",
    "DefaultTbRuleEngineConsumerService": (
        "Rule-engine queue consumer: submits message packs to the actor system using configurable "
        "{@link org.thingsboard.server.service.queue.processing.TbRuleEngineSubmitStrategy} and "
        "{@link org.thingsboard.server.service.queue.processing.TbRuleEngineProcessingStrategy}."
    ),
    "TbCalculatedFieldConsumerService": "Contract for consuming calculated-field queue messages on tb-core.",
    "DefaultTbCalculatedFieldConsumerService": "Processes calculated-field lifecycle and recalculation messages from the queue.",
    "TbEdgeConsumerService": "Contract for edge-related queue consumption on tb-core.",
    "DefaultTbEdgeConsumerService": "Handles edge sync, RPC, and event messages delivered via the core queue.",
    "DefaultTbClusterService": (
        "Cluster messaging facade: pushes protobuf messages to peer services (core, rule-engine, transport, edqs) "
        "via the configured queue implementation."
    ),
    "AbstractConsumerService": (
        "Base class for partition-aware queue consumers. Manages consumer managers, pack callbacks, "
        "and reacts to {@link org.thingsboard.server.queue.discovery.event.PartitionChangeEvent}."
    ),
    "AbstractPartitionBasedConsumerService": "Extends {@link AbstractConsumerService} with per-partition consumer manager wiring.",
    "TbLocalSubscriptionService": (
        "Local (in-process) subscription registry for WebSocket clients. "
        "Registers entity/time-series/attribute/alarm/notification subscriptions and forwards updates from the cluster."
    ),
    "DefaultTbLocalSubscriptionService": (
        "Default {@link TbLocalSubscriptionService}: indexes subscriptions by session and entity, "
        "applies rate limits, and invokes {@link org.thingsboard.server.service.ws.WebSocketService} to push updates."
    ),
    "SubscriptionManagerService": (
        "Cluster-wide subscription coordinator. Tracks which tb-core node owns each entity subscription "
        "and routes {@code SubscriptionMgrMsgProto} between nodes."
    ),
    "DefaultSubscriptionManagerService": "Default {@link SubscriptionManagerService} using partition service and cluster push.",
    "TbEntityDataSubscriptionService": (
        "Complex entity-data WebSocket subscriptions (v2 API): entity queries, alarm data, counts, and aggregated time-series."
    ),
    "DefaultTbEntityDataSubscriptionService": "Default implementation of {@link TbEntityDataSubscriptionService}.",
    "TbSubscription": (
        "Base type for a single WebSocket subscription tied to a session, entity, and {@link TbSubscriptionType}."
    ),
    "SubscriptionSchedulerComponent": "Scheduled tasks for subscription housekeeping (stale session cleanup, stats).",
    "SubscriptionErrorCode": "Error codes returned to WebSocket clients when a subscription command fails.",
    "TbSubscriptionType": "Kind of local subscription: time-series, attributes, alarms, notifications, etc.",
    "PendingMsgHolder": "Thread-safe holder for a single in-flight queue message awaiting callback completion.",
}


def split_license(content: str) -> tuple[str, str]:
    m = re.match(r"(\s*/\*\*[\s\S]*?\*/\s*)([\s\S]*)", content)
    return (m.group(1), m.group(2)) if m else ("", content)


def humanize(name: str) -> str:
    s = re.sub(r"([a-z])([A-Z])", r"\1 \2", name)
    return s.replace("_", " ").lower()


def package_of(body: str) -> str:
    m = re.search(r"^package\s+([\w.]+);", body, re.MULTILINE)
    return m.group(1) if m else ""


def detect_kind(line: str) -> str:
    for k in ("class", "interface", "enum", "record"):
        if re.search(rf"\b{k}\b", line):
            return k
    return "class"


def find_insert_before_decl(body: str, decl_start: int) -> int:
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


def has_javadoc_before(body: str, pos: int) -> bool:
    before = body[:pos].rstrip()
    if not before.endswith("*/"):
        return False
    chunk = before[before.rfind("/**") :]
    if is_stub_javadoc(chunk):
        return False
    return True


def is_stub_javadoc(text: str) -> bool:
    if "Created by" in text:
        return True
    lines = [ln.strip().lstrip("*").strip() for ln in text.splitlines()]
    content_lines = [ln for ln in lines if ln and not ln.startswith("/") and ln != "*/"]
    return not content_lines


def remove_javadoc_block(body: str, end_pos: int) -> tuple[str, int]:
    """If javadoc ends at end_pos, remove it and return (new_body, insert_pos)."""
    before = body[:end_pos].rstrip()
    start = before.rfind("/**")
    if start < 0:
        return body, end_pos
    prefix = body[:start].rstrip()
    if not prefix.endswith("\n") and prefix:
        prefix += "\n"
    return prefix + body[end_pos:], len(prefix)


def class_doc(name: str, kind: str, pkg: str) -> list[str]:
    if name in CLASS_DOCS:
        return [CLASS_DOCS[name]]
    words = humanize(name)
    if "service.ws" in pkg:
        domain = "WebSocket"
    elif "service.queue" in pkg:
        domain = "queue"
    elif "service.subscription" in pkg:
        domain = "subscription"
    else:
        domain = "service"

    if name.endswith("Cmd") and kind == "class":
        base = humanize(name.replace("Cmd", ""))
        return [
            f"WebSocket command DTO for {base}.",
            "<p>Deserialized from UI JSON and handled by {@link DefaultWebSocketService}.",
        ]
    if name.endswith("UnsubscribeCmd") or name.endswith("UnsubCmd"):
        return [
            f"WebSocket command that cancels an active {domain} subscription by command id.",
            "<p>Sent by the UI when a widget or dashboard unsubscribes from live updates.",
        ]
    if name.endswith("Update") and kind == "class":
        return [
            f"Outbound WebSocket update payload for {humanize(name.replace('Update', ''))}.",
            "<p>Serialized to JSON and pushed to the client session that owns the subscription.",
        ]
    if name.endswith("Subscription") and kind == "class":
        return [
            f"Subscription state holder for {humanize(name.replace('Subscription', ''))}.",
            "<p>Links a WebSocket session to entity keys and update processors.",
        ]
    if name.endswith("SubCtx"):
        return [
            f"Subscription context for {humanize(name.replace('SubCtx', ''))} WebSocket commands.",
            "<p>Maintains query state, caches, and pending updates for one command id.",
        ]
    if name.endswith("SubmitStrategy"):
        return [
            f"Rule-engine message submit strategy: {words}.",
            "<p>Controls parallelism and ordering when a pack of {@code TbMsg} is handed to actors.",
        ]
    if name.endswith("ProcessingStrategy"):
        return [
            f"Rule-engine pack processing strategy: {words}.",
            "<p>Decides commit/ retry behavior after a pack is processed.",
        ]
    if name.endswith("Stats"):
        return [f"Consumer statistics counters for {words}."]
    if name.endswith("Callback"):
        return [f"Async callback invoked when {words.replace(' callback', '')} completes."]
    if name.endswith("Factory"):
        return [f"Factory that selects or builds {humanize(name.replace('Factory', ''))} implementations."]
    if name.endswith("Wrapper"):
        return [f"JSON wrapper grouping related {domain} commands or updates."]
    if name.startswith("Default") and kind == "class":
        base = name[7:]
        return [f"Default {{@link {base}}} implementation."]
    if name.endswith("Service") and kind == "interface":
        return [
            f"Service API for {humanize(name.replace('Service', ''))}.",
            f"<p>Part of the ThingsBoard {domain} layer on tb-core.",
        ]
    if kind == "interface":
        return [f"{words.capitalize()} contract for the ThingsBoard {domain} layer."]
    if kind == "enum":
        return [f"Enumeration of {words} values used by the {domain} layer."]
    if kind == "record":
        return [f"Immutable record holding {words} data."]
    return [f"{words.capitalize()} component in the ThingsBoard {domain} layer."]


def format_javadoc(indent: str, lines: list[str]) -> str:
    inner = "\n".join(f"{indent} * {line}" if line else f"{indent} *" for line in lines)
    return f"{indent}/**\n{inner}\n{indent} */\n"


def normalize_javadoc_blocks(body: str) -> str:
    """Collapse accidental blank lines inside Javadoc blocks."""

    def fix_block(match: re.Match[str]) -> str:
        block = match.group(0)
        block = re.sub(r"\n\n+(\s*\*)", r"\n\1", block)
        block = re.sub(r"(/\*\*)\n\n+(\s*\*)", r"\1\n\2", block)
        return block

    return re.sub(r"/\*\*(?:(?!\*/).)*?\*/", fix_block, body, flags=re.DOTALL)


def remove_created_by_stubs(body: str) -> str:
    return STUB_JAVADOC_BLOCK.sub("", body)


def is_interface_type(body: str) -> bool:
    m = DECL_RE.search(body)
    return bool(m and "interface" in m.group(0))


def class_javadoc_region(body: str, decl_start: int) -> str | None:
    insert_at = find_insert_before_decl(body, decl_start)
    between = body[insert_at:decl_start]
    start = between.rfind("/**")
    if start < 0:
        before = body[:insert_at].rstrip()
        if before.endswith("*/"):
            start = before.rfind("/**")
            if start >= 0:
                return before[start:]
        return None
    end = between.find("*/", start)
    if end < 0:
        return None
    return between[start : end + 2]


def parse_params(params_str: str) -> list[tuple[str, str]]:
    inner = params_str.strip()[1:-1].strip()
    if not inner:
        return []
    result: list[tuple[str, str]] = []
    depth = 0
    current = []
    for ch in inner + ",":
        if ch == "<":
            depth += 1
        elif ch == ">":
            depth -= 1
        elif ch == "," and depth == 0:
            part = "".join(current).strip()
            current = []
            if not part:
                continue
            part = re.sub(r"@\w+(?:\([^)]*\))?\s+", "", part)
            part = re.sub(r"final\s+", "", part)
            tokens = part.rsplit(None, 1)
            if len(tokens) == 2:
                ptype, pname = tokens
                result.append((pname, ptype.strip()))
            elif len(tokens) == 1:
                result.append((tokens[0], ""))
            continue
        current.append(ch)
    return result


def return_description(ret: str | None, method_name: str) -> str | None:
    if not ret:
        return None
    ret = ret.strip()
    if not ret or ret == "void":
        return None
    parts = ret.split("<")[0].split()
    if not parts:
        return None
    simple = parts[-1]
    if method_name.startswith("is") or method_name.startswith("has"):
        return "{@code true} when the condition holds"
    if simple in ("boolean", "Boolean"):
        return "boolean result"
    if simple in ("int", "Integer", "long", "Long"):
        return "numeric result"
    if simple in ("String",):
        return "string value"
    if simple and simple[0].isupper():
        return f"{{@link {simple}}}"
    return ret


def method_description(name: str, class_name: str, params: list[tuple[str, str]], is_ctor: bool) -> str:
    if is_ctor:
        if params:
            return f"Constructs {{@link {class_name}}} with the supplied dependencies and configuration."
        return f"Constructs an empty {{@link {class_name}}}."
    verbs = {
        "handle": "Handles",
        "on": "Invoked when",
        "send": "Sends",
        "cancel": "Cancels",
        "add": "Registers",
        "remove": "Removes",
        "get": "Returns",
        "set": "Sets",
        "init": "Initializes",
        "destroy": "Shuts down",
        "process": "Processes",
        "submit": "Submits",
        "close": "Closes",
        "cleanup": "Cleans up",
        "register": "Registers",
        "notify": "Notifies",
        "update": "Updates",
        "create": "Creates",
        "build": "Builds",
        "start": "Starts",
        "stop": "Stops",
        "pause": "Pauses",
        "resume": "Resumes",
        "commit": "Commits",
        "await": "Waits until",
        "fetch": "Fetches",
        "load": "Loads",
        "save": "Persists",
        "validate": "Validates",
        "check": "Checks",
        "find": "Finds",
        "compute": "Computes",
        "convert": "Converts",
        "parse": "Parses",
        "encode": "Encodes",
        "decode": "Decodes",
        "push": "Pushes",
        "poll": "Polls",
        "consume": "Consumes",
        "dispatch": "Dispatches",
        "execute": "Executes",
        "schedule": "Schedules",
        "reset": "Resets",
        "clear": "Clears",
        "refresh": "Refreshes",
        "apply": "Applies",
        "accept": "Accepts",
        "run": "Runs",
        "log": "Logs",
        "toString": "Returns a diagnostic string representation.",
    }
    for prefix, verb in verbs.items():
        if name.startswith(prefix) and name != "toString":
            rest = humanize(name[len(prefix):] or name)
            if prefix == "on" and len(name) > 2:
                return f"{verb} {humanize(name[2:])} occurs."
            if prefix in ("is", "has"):
                return f"Indicates whether {humanize(name[len(prefix):] or name)}."
            if len(name) == len(prefix):
                return f"{verb}."
            return f"{verb} {rest}."
    if name == "equals":
        return "Compares this object to another for equality."
    if name == "hashCode":
        return "Returns a hash code consistent with {@link #equals(Object)}."
    if name == "getCmdId":
        return "Returns the client-assigned command id used to correlate updates."
    if name == "getType":
        return "Returns the {@link WsCmdType} discriminator for this command."
    if name == "close":
        return "Closes the WebSocket session with the given status."
    return f"{humanize(name).capitalize()}."


def param_hint(pname: str, ptype: str) -> str:
    hints = {
        "sessionId": "WebSocket session identifier",
        "sessionRef": "reference to the WebSocket session",
        "tenantId": "tenant that owns the subscription or entity",
        "entityId": "target entity id",
        "subscriptionId": "client command/subscription id",
        "cmdId": "client command id",
        "callback": "queue callback to ack or retry the message",
        "update": "subscription update payload",
        "commandsWrapper": "batch of inbound WebSocket commands",
        "status": "WebSocket close status",
        "errorCode": "subscription error code",
        "errorMsg": "human-readable error detail",
        "event": "application or cluster event",
        "msg": "queue or transport message",
        "pack": "message pack being processed",
        "partition": "Kafka partition assignment",
        "query": "entity or time-series query",
        "subscription": "subscription to register or remove",
    }
    if pname in hints:
        return hints[pname]
    if ptype.endswith("Id"):
        return f"{humanize(ptype.replace('Id', ''))} identifier"
    return humanize(pname)


def method_javadoc(
    indent: str,
    method_name: str,
    class_name: str,
    ret: str | None,
    params: list[tuple[str, str]],
    throws: str | None,
    is_override: bool,
    is_ctor: bool,
) -> str:
    desc = method_description(method_name, class_name, params, is_ctor)
    lines = [desc]
    if is_override and not is_ctor:
        lines.extend(["", "<p>Default implementation inherited from the supertype."])
    for pname, ptype in params:
        lines.append(f"@param {pname} {param_hint(pname, ptype)}")
    ret_desc = return_description(ret, method_name)
    if ret_desc:
        lines.append(f"@return {ret_desc}")
    if throws:
        for t in [x.strip().split(".")[-1] for x in throws.split(",") if x.strip()]:
            lines.append(f"@throws {t} if processing fails")
    return format_javadoc(indent, lines)


def method_already_documented(body: str, method_start: int) -> bool:
    region_start = body.rfind("\n    }", 0, method_start)
    if region_start < 0:
        region_start = 0
    chunk = body[region_start:method_start]
    javadoc_end = chunk.rfind("*/")
    if javadoc_end < 0:
        return False
    javadoc_start = chunk.rfind("/**", 0, javadoc_end)
    if javadoc_start < 0:
        return False
    block = chunk[javadoc_start : javadoc_end + 2]
    if is_stub_javadoc(block):
        return False
    tail = chunk[javadoc_end + 2 :].strip("\n")
    if not tail:
        return True
    for line in tail.splitlines():
        stripped = line.strip()
        if not stripped:
            continue
        if stripped.startswith("@"):
            continue
        return False
    return True


def dedupe_consecutive_javadocs(body: str) -> str:
    pattern = re.compile(
        r"((?:/\*\*(?:(?!\*/).)*?\*/\s*\n\s*){2,})",
        re.DOTALL,
    )

    def compress(match: re.Match[str]) -> str:
        text = match.group(1)
        blocks = re.findall(r"/\*\*(?:(?!\*/).)*?\*/", text, flags=re.DOTALL)
        if len(blocks) < 2:
            return text
        norms = [re.sub(r"\s+", " ", b.strip()) for b in blocks]
        if len(set(norms)) == 1:
            return blocks[0] + "\n\n"
        return text

    prev = None
    while prev != body:
        prev = body
        body = pattern.sub(compress, body)
    return body


def find_method_insert(body: str, method_start: int) -> int:
    region_start = body.rfind("\n    }", 0, method_start)
    if region_start < 0:
        region_start = 0
    pos = body.rfind("\n", 0, method_start) + 1
    while pos > region_start:
        prev_nl = body.rfind("\n", 0, pos - 1)
        prev_start = prev_nl + 1
        line = body[prev_start:pos].strip()
        if line.startswith(
            (
                "@Override",
                "@PostConstruct",
                "@PreDestroy",
                "@Scheduled",
                "@EventListener",
                "@JsonCreator",
                "@JsonIgnore",
                "@Bean",
                "@AfterStartUp",
                "@SuppressWarnings",
                "@Deprecated",
                "@Nullable",
                "@Nonnull",
                "@SafeVarargs",
            )
        ) or (line.startswith("@") and not line.startswith("@param")):
            pos = prev_start
            continue
        break
    return pos


def extract_return_type(sig: str, method_name: str) -> str | None:
    inner = sig.strip()
    inner = re.sub(r"^(?:public|protected)\s+", "", inner)
    inner = re.sub(r"^(?:(?:static|final|abstract|synchronized|native)\s+)+", "", inner)
    paren = inner.find("(")
    if paren < 0:
        return None
    before = inner[:paren].strip()
    parts = before.rsplit(None, 1)
    if len(parts) == 2 and parts[1] == method_name:
        return parts[0]
    return None


def method_is_public_or_protected(sig: str, is_interface: bool) -> bool:
    if "public" in sig or "protected" in sig:
        return True
    return is_interface and not sig.lstrip().startswith("private")


def find_method_matches(body: str, is_interface: bool) -> list[re.Match[str]]:
    matches: list[re.Match[str]] = []
    for m in METHOD_LINE_RE.finditer(body):
        sig = m.group("sig")
        name = m.group("name")
        if name in ("class", "interface", "enum", "record", "if", "for", "while", "switch", "catch", "new"):
            continue
        if not method_is_public_or_protected(sig, is_interface):
            continue
        if re.search(r"\b(class|interface|enum|record)\b", sig):
            continue
        matches.append(m)
    return matches


def remove_class_javadoc_region(body: str, decl_start: int) -> str:
    insert_at = find_insert_before_decl(body, decl_start)
    region = class_javadoc_region(body, decl_start)
    if not region:
        return body
    pos = body.find(region, max(0, insert_at - 50), decl_start + 1)
    if pos < 0:
        return body
    return body[:pos] + body[pos + len(region) :]


def process_class_javadoc(body: str, class_name: str, pkg: str) -> tuple[str, bool]:
    m = DECL_RE.search(body)
    if not m:
        return body, False
    kind = detect_kind(m.group(0))
    indent = m.group("indent")
    existing = class_javadoc_region(body, m.start())
    if existing and not is_stub_javadoc(existing):
        return body, False

    body = remove_class_javadoc_region(body, m.start())
    m = DECL_RE.search(body)
    if not m:
        return body, False
    insert_at = find_insert_before_decl(body, m.start())
    jd = format_javadoc(indent, class_doc(class_name, kind, pkg))
    body = body[:insert_at] + jd + body[insert_at:]
    return body, True


def process_method_javadocs(body: str, class_name: str) -> tuple[str, bool]:
    changed = False
    is_interface = is_interface_type(body)
    matches = find_method_matches(body, is_interface)
    for m in reversed(matches):
        name = m.group("name")
        insert_at = find_method_insert(body, m.start())
        if method_already_documented(body, m.start()) or has_javadoc_before(body, insert_at):
            continue

        sig = m.group("sig")
        is_ctor = name == class_name
        ret = extract_return_type(sig, name)
        params = parse_params(m.group("params"))
        throws = m.group("throws")
        region = body[max(0, insert_at - 200) : m.start()]
        is_override = "@Override" in region

        indent = m.group("indent")
        jd = method_javadoc(indent, name, class_name, ret, params, throws, is_override, is_ctor)
        body = body[:insert_at] + jd + body[insert_at:]
        changed = True
    return body, changed


def process_file(path: Path, dry_run: bool) -> bool:
    content = path.read_text(encoding="utf-8")
    license, body = split_license(content)
    original_body = body
    body = remove_created_by_stubs(body)
    body = dedupe_consecutive_javadocs(body)
    m = DECL_RE.search(body)
    if not m:
        return False
    class_name = m.group("name")
    pkg = package_of(body)

    body, class_changed = process_class_javadoc(body, class_name, pkg)
    body, method_changed = process_method_javadocs(body, class_name)
    body = normalize_javadoc_blocks(body)

    if body != original_body:
        if not dry_run:
            path.write_text(license + body, encoding="utf-8", newline="\n")
        return True
    return False


def collect_java_files(roots: list[Path]) -> list[Path]:
    files: list[Path] = []
    for root in roots:
        if not root.is_dir():
            print(f"Skip missing: {root}", file=sys.stderr)
            continue
        for path in sorted(root.rglob("*.java")):
            posix = path.as_posix()
            if "/test/" in posix or "\\test\\" in posix:
                continue
            if path.name == "package-info.java":
                continue
            files.append(path)
    return files


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("roots", nargs="*", type=Path)
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    if args.roots:
        roots = args.roots
    else:
        base = Path("application/src/main/java/org/thingsboard/server/service")
        roots = [base / "ws", base / "queue", base / "subscription"]

    files = collect_java_files(roots)
    count = 0
    for path in files:
        if process_file(path, args.dry_run):
            count += 1
            print(path)
    print(f"MODIFIED_COUNT={count}", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
