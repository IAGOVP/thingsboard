#!/usr/bin/env python3
"""
Add class- and public/protected method-level Javadoc to ThingsBoard actor packages.
Class docs describe actor role in the ThingsBoard actor system; methods include @param, @return, @throws.
"""
from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass, field
from pathlib import Path

# Reuse core helpers from service javadoc script
sys.path.insert(0, str(Path(__file__).resolve().parent))
from add_service_javadoc import (  # noqa: E402
    CLASS_METHOD_RE,
    DECL_LINE_RE,
    INTERFACE_METHOD_RE,
    MethodInfo,
    ParamInfo,
    SKIP_METHOD_NAMES,
    detect_kind,
    extract_throws,
    find_insert_position,
    find_method_doc_insertion_point,
    format_javadoc,
    format_return,
    insert_text_at,
    javadoc_block_before,
    javadoc_is_complete,
    method_description,
    param_description,
    parse_params,
    split_license_and_body,
    throws_description,
)

ACTOR_SUBPACKAGE: dict[str, str] = {
    "app": "root application actor that bootstraps tenant actors",
    "calculatedField": "calculated-field manager and per-entity evaluation actors",
    "device": "per-device session, RPC, and transport fan-in actors",
    "ruleChain": "rule chain, rule node, and rule-engine message routing",
    "tenant": "tenant-scoped actors (devices, rule chains, calculated fields)",
    "service": "actor system bootstrap, base classes, and shared infrastructure",
    "shared": "shared actor processors and cross-cutting actor helpers",
    "stats": "queue and service statistics persistence actor",
}

CLASS_DESCRIPTIONS: dict[str, list[str]] = {
    "ActorSystemContext": [
        "Spring component holding shared dependencies for all ThingsBoard actors.",
        "",
        "<p>Exposes DAO services, cluster messaging, calculated-field helpers, rule-engine utilities, "
        "and debug/rate-limit state. Injected into {@link org.thingsboard.server.actors.service.ContextAwareActor} "
        "subclasses and message processors.",
    ],
    "TbEntityTypeActorIdPredicate": [
        "Predicate that matches {@link org.thingsboard.server.actors.TbActorId} instances by {@link org.thingsboard.server.common.data.EntityType}.",
        "",
        "<p>Used when broadcasting actor messages to children of a specific entity type "
        "(for example, all tenant actors under the root {@link org.thingsboard.server.actors.app.AppActor}).",
    ],
    "AppActor": [
        "Root actor of the ThingsBoard actor system (singleton per tb-core node).",
        "",
        "<p>Bootstraps {@link org.thingsboard.server.actors.tenant.TenantActor} children on {@link org.thingsboard.server.actors.app.AppInitMsg}, "
        "routes transport and lifecycle messages to the correct tenant, and broadcasts partition-change "
        "and session-timeout ticks to tenant actors.",
    ],
    "AppInitMsg": [
        "Initialization message sent to {@link org.thingsboard.server.actors.app.AppActor} after the actor system starts.",
        "",
        "<p>Triggers creation of tenant actors and rule-chain initialization across the cluster partition.",
    ],
    "TenantActor": [
        "Per-tenant actor managing child device, rule-chain, and calculated-field actors.",
        "",
        "<p>Extends {@link org.thingsboard.server.actors.ruleChain.RuleChainManagerActor} to own rule chains "
        "and dispatches device transport, RPC, calculated-field, and lifecycle messages to the appropriate child actor.",
    ],
    "DebugTbRateLimits": [
        "Per-tenant rate limiters for rule-chain and calculated-field debug event persistence.",
        "",
        "<p>Prevents debug-mode event storms from overwhelming the event store during rule or CF troubleshooting.",
    ],
    "DeviceActor": [
        "Per-device actor handling transport sessions, attribute updates, and RPC.",
        "",
        "<p>One instance exists per active {@link org.thingsboard.server.common.data.id.DeviceId} under a "
        "{@link org.thingsboard.server.actors.tenant.TenantActor}. Delegates processing to "
        "{@link org.thingsboard.server.actors.device.DeviceActorMessageProcessor}.",
    ],
    "DeviceActorCreator": [
        "Factory that creates {@link org.thingsboard.server.actors.device.DeviceActor} instances for a tenant/device pair.",
    ],
    "DeviceActorMessageProcessor": [
        "Message processor for {@link org.thingsboard.server.actors.device.DeviceActor}.",
        "",
        "<p>Manages transport sessions, server-side and client-side RPC state, attribute/credential/name updates, "
        "and session timeout checks.",
    ],
    "SessionInfo": [
        "Runtime metadata for an active device transport session (protocol, gateway, subscriptions).",
    ],
    "SessionInfoMetaData": [
        "Extended session metadata including last activity time and RPC correlation state.",
    ],
    "SessionTimeoutCheckMsg": [
        "Periodic tick message broadcast from {@link org.thingsboard.server.actors.app.AppActor} to detect stale device sessions.",
    ],
    "ToDeviceRpcRequestMetadata": [
        "Tracks pending to-device RPC requests (request id, expiration, retry policy).",
    ],
    "ToServerRpcRequestMetadata": [
        "Tracks pending to-server (client-side) RPC requests from the device.",
    ],
    "TransportSessionCloseReason": [
        "Reason codes recorded when a device transport session is closed.",
    ],
    "RuleChainManagerActor": [
        "Abstract tenant-scoped actor that owns rule-chain and rule-node child actors.",
        "",
        "<p>Creates and stops {@link org.thingsboard.server.actors.ruleChain.RuleChainActor} instances on lifecycle events "
        "and routes {@link org.thingsboard.server.common.msg.queue.QueueToRuleEngineMsg} to the target chain.",
    ],
    "RuleChainActor": [
        "Actor representing one rule chain; routes {@link org.thingsboard.server.common.msg.TbMsg} through its rule nodes.",
    ],
    "RuleChainActorMessageProcessor": [
        "Processor for {@link org.thingsboard.server.actors.ruleChain.RuleChainActor}.",
        "",
        "<p>Loads chain metadata, manages rule-node actor children, and forwards messages along chain relations.",
    ],
    "RuleNodeActor": [
        "Actor representing one rule node within a rule chain.",
        "",
        "<p>Executes the node component (filter, transformation, external REST, etc.) via "
        "{@link org.thingsboard.server.actors.ruleChain.RuleNodeActorMessageProcessor}.",
    ],
    "RuleNodeActorMessageProcessor": [
        "Processor for {@link org.thingsboard.server.actors.ruleChain.RuleNodeActor}.",
        "",
        "<p>Initializes the rule-engine component, applies debug/rate limits, and routes output to next nodes.",
    ],
    "RuleEngineComponentActor": [
        "Base class for rule-chain and rule-node actors with shared lifecycle and debug handling.",
    ],
    "DefaultTbContext": [
        "Default {@link org.thingsboard.rule.engine.api.TbContext} implementation passed to rule-engine components.",
        "",
        "<p>Provides enqueue, tell-next, alarm, attribute, and external service calls from within a rule node actor.",
    ],
    "RuleChainInputMsg": [
        "Entry message delivering a {@link org.thingsboard.server.common.msg.TbMsg} into a rule chain actor.",
    ],
    "RuleChainOutputMsg": [
        "Message carrying rule-chain output {@link org.thingsboard.server.common.msg.TbMsg} to a caller or parent chain.",
    ],
    "RuleChainToRuleChainMsg": [
        "Routes a {@link org.thingsboard.server.common.msg.TbMsg} from one rule chain actor to another.",
    ],
    "RuleChainToRuleNodeMsg": [
        "Routes a {@link org.thingsboard.server.common.msg.TbMsg} from a rule chain actor to a specific rule node.",
    ],
    "RuleNodeToRuleChainTellNextMsg": [
        "Rule node completion message instructing the parent chain to continue on a named relation.",
    ],
    "RuleNodeToSelfMsg": [
        "Internal message routed back to the same rule node actor (for example, async completion).",
    ],
    "RuleNodeCtx": [
        "Runtime context for a rule node: component instance, configuration, and actor references.",
    ],
    "RuleNodeRelation": [
        "Lightweight relation descriptor (type and target node id) used when telling next in a rule chain.",
    ],
    "TbToRuleChainActorMsg": [
        "Base class for messages addressed to a {@link org.thingsboard.server.actors.ruleChain.RuleChainActor}.",
    ],
    "TbToRuleNodeActorMsg": [
        "Base class for messages addressed to a {@link org.thingsboard.server.actors.ruleChain.RuleNodeActor}.",
    ],
    "AbstractCalculatedFieldActor": [
        "Base actor for calculated-field processing at tenant or entity scope.",
        "",
        "<p>Handles {@link org.thingsboard.server.common.msg.ToCalculatedFieldSystemMsg} subtypes; "
        "failures invoke the message callback and may persist debug events.",
    ],
    "CalculatedFieldManagerActor": [
        "Per-tenant calculated-field manager actor.",
        "",
        "<p>Owns CF definition cache, partition routing, and child "
        "{@link org.thingsboard.server.actors.calculatedField.CalculatedFieldEntityActor} instances.",
    ],
    "CalculatedFieldEntityActor": [
        "Per-entity calculated-field actor that evaluates expressions when inputs change.",
    ],
    "CalculatedFieldManagerActorCreator": [
        "Factory for {@link org.thingsboard.server.actors.calculatedField.CalculatedFieldManagerActor}.",
    ],
    "CalculatedFieldEntityActorCreator": [
        "Factory for {@link org.thingsboard.server.actors.calculatedField.CalculatedFieldEntityActor}.",
    ],
    "CalculatedFieldManagerMessageProcessor": [
        "Processor for {@link org.thingsboard.server.actors.calculatedField.CalculatedFieldManagerActor}.",
        "",
        "<p>Handles CF lifecycle, cache init, partition changes, and telemetry fan-in to entity actors.",
    ],
    "CalculatedFieldEntityMessageProcessor": [
        "Processor for {@link org.thingsboard.server.actors.calculatedField.CalculatedFieldEntityActor}.",
        "",
        "<p>Evaluates calculated fields, manages argument state, and publishes computed telemetry/attributes.",
    ],
    "CalculatedFieldException": [
        "Controlled failure during calculated-field evaluation with optional debug context.",
    ],
    "MultipleTbCallback": [
        "Composite {@link org.thingsboard.server.common.msg.queue.TbCallback} invoked when all delegate callbacks complete.",
    ],
    "CalculatedFieldTelemetryMsg": [
        "Telemetry update routed to the calculated-field manager for CFs bound to the reporting entity.",
    ],
    "CalculatedFieldLinkedTelemetryMsg": [
        "Telemetry from a linked (argument) entity routed to the calculated-field manager.",
    ],
    "EntityCalculatedFieldTelemetryMsg": [
        "Telemetry update delivered directly to a calculated-field entity actor.",
    ],
    "EntityCalculatedFieldLinkedTelemetryMsg": [
        "Linked-entity telemetry delivered to a calculated-field entity actor.",
    ],
    "EntityInitCalculatedFieldMsg": [
        "Initializes or reloads calculated-field state on a calculated-field entity actor.",
    ],
    "CalculatedFieldEntityDeleteMsg": [
        "Removes a calculated-field entity actor and clears its runtime state.",
    ],
    "CalculatedFieldEntityActionEventMsg": [
        "Entity lifecycle or action event that may trigger calculated-field re-evaluation.",
    ],
    "CalculatedFieldAlarmActionMsg": [
        "Alarm create/clear/update event routed to calculated-field actors.",
    ],
    "CalculatedFieldRelationActionMsg": [
        "Entity relation change that may affect calculated-field argument resolution.",
    ],
    "CalculatedFieldArgumentResetMsg": [
        "Resets cached argument state for one or more calculated fields on an entity actor.",
    ],
    "CalculatedFieldReevaluateMsg": [
        "Forces re-evaluation of calculated fields on an entity actor.",
    ],
    "CalculatedFieldStateRestoreMsg": [
        "Restores persisted calculated-field state after partition reassignment.",
    ],
    "ActorService": [
        "Service API for starting and stopping the ThingsBoard actor system.",
    ],
    "DefaultActorService": [
        "Default implementation that bootstraps the actor system on application start.",
        "",
        "<p>Creates the root {@link org.thingsboard.server.actors.app.AppActor}, registers partition-change listeners, "
        "and coordinates graceful shutdown.",
    ],
    "ContextAwareActor": [
        "Base {@link org.thingsboard.server.actors.TbActor} with access to {@link ActorSystemContext}.",
    ],
    "ContextBasedCreator": [
        "Base {@link org.thingsboard.server.actors.TbActorCreator} that injects {@link ActorSystemContext}.",
    ],
    "ComponentActor": [
        "Base actor for rule-engine components (rule chains and rule nodes) with a typed message processor.",
    ],
    "AbstractContextAwareMsgProcessor": [
        "Base message processor with {@link ActorSystemContext} and actor context references.",
    ],
    "ComponentMsgProcessor": [
        "Message processor for rule-engine component actors with lifecycle and partition handling.",
    ],
    "ActorTerminationMsg": [
        "Base message signaling that an actor or component should terminate.",
    ],
    "RuleChainErrorActor": [
        "Fallback actor that logs and acknowledges rule-engine messages when target chain/node actors are missing.",
    ],
    "StatsActor": [
        "Singleton actor that periodically persists queue and API usage statistics.",
    ],
    "StatsPersistMsg": [
        "Carries statistics snapshot data to {@link org.thingsboard.server.actors.stats.StatsActor} for persistence.",
    ],
    "StatsPersistTick": [
        "Periodic tick enum message triggering stats flush in {@link org.thingsboard.server.actors.stats.StatsActor}.",
    ],
    "ActorCreator": [
        "Factory for creating instances of the enclosing actor type.",
    ],
}


INNER_CLASS_DESCRIPTIONS: dict[str, list[str]] = {
    "ActorCreator": CLASS_DESCRIPTIONS["ActorCreator"],
}


def actor_subpackage(path: Path) -> str:
    posix = path.as_posix()
    marker = "/actors/"
    if marker not in posix:
        return ""
    rest = posix.split(marker, 1)[1]
    return rest.split("/")[0] if "/" in rest else ""


def class_description(name: str, kind: str, path: Path, body: str) -> list[str]:
    if name in CLASS_DESCRIPTIONS:
        return CLASS_DESCRIPTIONS[name]
    if name in INNER_CLASS_DESCRIPTIONS:
        return INNER_CLASS_DESCRIPTIONS[name]

    sub = actor_subpackage(path)
    ctx = ACTOR_SUBPACKAGE.get(sub, "ThingsBoard actor system")

    if name.endswith("Msg") or name.endswith("Message"):
        return [
            f"Actor message ({humanize(name)}) in the {ctx}.",
            "",
            "<p>Delivered as {@link org.thingsboard.server.common.msg.TbActorMsg} to the appropriate actor mailbox.",
        ]
    if name.endswith("Creator") or name.endswith("ActorCreator"):
        base = name.replace("Creator", "").replace("Actor", "")
        return [f"Factory that creates {{@code {base}}} actor instances."]
    if name.endswith("Processor"):
        actor = name.replace("MessageProcessor", "").replace("Processor", "")
        return [
            f"Message processor for {{@code {actor}}} actors.",
            "",
            f"<p>Contains the business logic invoked by the corresponding actor ({ctx}).",
        ]
    if name.endswith("Exception"):
        return [f"Exception during {ctx}: {humanize(name)}."]
    if kind == "interface":
        return [f"{humanize(name)} contract for the {ctx}."]
    if kind == "enum":
        return [f"Enumerates {humanize(name)} values used in the {ctx}."]
    if "Actor" in name and kind == "class":
        return [
            f"Actor ({humanize(name)}) in the {ctx}.",
            "",
            "<p>Processes {@link org.thingsboard.server.common.msg.TbActorMsg} messages serially for one entity or scope.",
        ]
    return [f"{humanize(name).capitalize()} ({ctx})."]


def humanize(name: str) -> str:
    return re.sub(r"([a-z])([A-Z])", r"\1 \2", name).replace("_", " ").lower()


ACTOR_METHOD_DESCRIPTIONS: dict[str, str] = {
    "init": "Initializes the actor after creation (schedules ticks, loads metadata, creates child actors).",
    "destroy": "Stops child actors and releases resources before the actor terminates.",
    "doProcess": "Handles one incoming actor message; returns {@code true} if the message type was recognized.",
    "doProcessCfMsg": "Handles one calculated-field system message for this tenant or entity actor.",
    "createActorId": "Builds the {@link org.thingsboard.server.actors.TbActorId} used to register the actor.",
    "createActor": "Creates a new actor instance for the given actor id and context.",
    "getMsgType": "Returns the {@link org.thingsboard.server.common.msg.MsgType} discriminator for this message.",
    "getTenantId": "Returns the tenant id associated with this message or actor scope.",
    "onProcessFailure": "Strategy invoked when message processing fails inside the actor.",
    "getSelf": "Returns a reference to this actor for tell/send operations.",
    "getCallback": "Returns the queue callback to invoke when asynchronous processing completes.",
    "instance": "Returns the singleton instance of this tick or marker message.",
    "test": "Returns {@code true} if this predicate matches the given actor id.",
    "onSuccess": "Callback invoked when the asynchronous operation completes successfully.",
    "onFailure": "Callback invoked when the asynchronous operation fails.",
    "process": "Processes the given message or event in the actor context.",
    "start": "Starts actor processing or loads initial state.",
    "stop": "Stops actor processing and notifies dependents.",
    "tellNext": "Routes the message to the next rule node on the named relation.",
    "tellFailure": "Routes a failure to the caller or error handler.",
    "enqueueForTellNext": "Enqueues a message for delivery to the next rule node.",
    "logProcessingException": "Logs an unexpected exception during calculated-field evaluation.",
}


def class_javadoc_is_incomplete(doc: str, class_name: str) -> bool:
    if class_name in CLASS_DESCRIPTIONS:
        return False
    if len(doc.strip()) < 80:
        return True
    if "ThingsBoard actor system" in doc and doc.count(".") <= 2:
        return True
    return False


def add_class_javadoc_at(body: str, decl_match: re.Match, class_name: str, kind: str, path: Path) -> tuple[str, bool]:
    insert_pos = find_insert_position(body, decl_match.start())
    new_doc = format_javadoc(decl_match.group("indent"), class_description(class_name, kind, path, body))
    existing = javadoc_block_before(body, insert_pos)
    if existing:
        start, end = existing
        if not class_javadoc_is_incomplete(body[start:end], class_name):
            return body, False
        body = body[:start] + body[end:]
        insert_pos = start
    return insert_text_at(body, insert_pos, new_doc), True


def add_all_class_javadocs(body: str, path: Path) -> tuple[str, int]:
    count = 0
    matches = list(DECL_LINE_RE.finditer(body))
    for m in reversed(matches):
        name = m.group("name")
        kind = detect_kind(m.group(0))
        body, changed = add_class_javadoc_at(body, m, name, kind, path)
        if changed:
            count += 1
    return body, count


def actor_param_description(name: str, type_hint: str) -> str:
    hints = {
        "tenantId": "tenant that owns the actor or target entity",
        "deviceId": "target device identifier",
        "entityId": "target entity identifier",
        "ruleChainId": "target rule chain identifier",
        "ruleNodeId": "target rule node identifier",
        "msg": "actor message to process",
        "ctx": "actor context ({@link org.thingsboard.server.actors.TbActorCtx})",
        "systemContext": "shared {@link ActorSystemContext}",
        "callback": "queue callback invoked when processing completes",
        "cfm": "calculated-field system message",
    }
    if name in hints:
        return hints[name]
    return param_description(name, type_hint)


def actor_method_description(name: str) -> str:
    return ACTOR_METHOD_DESCRIPTIONS.get(name, method_description(name))


def actor_method_javadoc(method: MethodInfo) -> str:
    lines = [actor_method_description(method.name), ""]
    for p in method.params:
        lines.append(f"@param {p.name} {actor_param_description(p.name, p.type_hint)}")
    lines.append(f"@return {format_return(method.return_type)}")
    if method.throws:
        for t in method.throws:
            lines.append(f"@throws {t} {throws_description(t)}")
    else:
        lines.append("@throws Exception if an unexpected error occurs during processing")
    return format_javadoc(method.indent, lines)


def actor_method_first_line(doc: str) -> str:
    for line in doc.split("\n"):
        t = line.strip().lstrip("* ").strip()
        if t and t not in ("/**", "*/"):
            return t
    return ""


def actor_method_javadoc_is_complete(doc: str, method: MethodInfo) -> bool:
    if not javadoc_is_complete(doc, method):
        return False
    first_line = actor_method_first_line(doc)
    generic = {humanize(m) + "." for m in ACTOR_METHOD_DESCRIPTIONS} | {
        "Init.",
        "Do process.",
        "Destroy.",
        "Create actor id.",
        "Creates actor id.",
        "Create actor.",
        "Creates actor.",
        "Get msg type.",
        "Get tenant id.",
        "On process failure.",
        "Handles process failure.",
        "Returns msg type.",
    }
    if first_line in generic:
        return False
    return True


def class_decl_match(body: str, class_name: str, occurrence: int = 0) -> re.Match | None:
    matches = [m for m in DECL_LINE_RE.finditer(body) if m.group("name") == class_name]
    return matches[occurrence] if occurrence < len(matches) else None


def class_body_bounds_at(body: str, decl_match: re.Match) -> tuple[int, int]:
    brace = body.find("{", decl_match.end() - 1)
    if brace < 0:
        return 0, len(body)
    depth = 1
    i = brace + 1
    while i < len(body) and depth > 0:
        if body[i] == "{":
            depth += 1
        elif body[i] == "}":
            depth -= 1
            if depth == 0:
                return brace + 1, i
        i += 1
    return brace + 1, len(body)


def find_class_methods(body: str, class_name: str, kind: str, decl_match: re.Match) -> list[MethodInfo]:
    methods: list[MethodInfo] = []
    class_start, class_end = class_body_bounds_at(body, decl_match)
    region = body[class_start:class_end]
    pattern = INTERFACE_METHOD_RE if kind == "interface" else CLASS_METHOD_RE

    for m in pattern.finditer(region):
        method_name = m.group("name")
        if method_name == class_name or method_name in SKIP_METHOD_NAMES:
            continue
        ret_type = re.sub(
            r"\b(static|final|synchronized|native|abstract|default)\s+",
            "",
            m.group("ret"),
        ).strip()
        if not ret_type or ret_type.startswith("@"):
            continue
        rel_start = class_start + m.start()
        line_start = body.rfind("\n", 0, rel_start) + 1
        if "(" in body[line_start:rel_start]:
            continue
        paren_start = class_start + m.end() - 1
        depth = 0
        j = paren_start
        while j < class_end:
            if body[j] == "(":
                depth += 1
            elif body[j] == ")":
                depth -= 1
                if depth == 0:
                    break
            j += 1
        if j >= class_end:
            continue
        param_text = body[paren_start + 1 : j]
        after_paren = body[j + 1 : j + 200]
        doc_start = find_method_doc_insertion_point(body, rel_start, class_start)
        methods.append(
            MethodInfo(
                doc_start=doc_start,
                sig_start=rel_start,
                indent=m.group("indent"),
                name=method_name,
                return_type=ret_type,
                params=parse_params(param_text),
                throws=extract_throws(after_paren),
            )
        )
    return methods


def add_actor_method_javadocs(body: str, decl_match: re.Match, class_name: str, kind: str) -> tuple[str, int]:
    count = 0
    class_start, _ = class_body_bounds_at(body, decl_match)
    while True:
        pending: MethodInfo | None = None
        for method in reversed(find_class_methods(body, class_name, kind, decl_match)):
            if method.doc_start < class_start:
                continue
            existing = javadoc_block_before(body, method.doc_start)
            if existing:
                start, end = existing
                if actor_method_javadoc_is_complete(body[start:end], method):
                    continue
            pending = method
            break
        if pending is None:
            break
        pos = max(pending.doc_start, class_start + 1)
        new_doc = actor_method_javadoc(pending)
        existing = javadoc_block_before(body, pos)
        if existing:
            start, end = existing
            body = insert_text_at(body[:start] + body[end:], start, new_doc.rstrip("\n") + "\n")
        else:
            body = insert_text_at(body, pos, new_doc)
        count += 1
    return body, count


def process_file(path: Path, dry_run: bool) -> bool:
    if path.name == "package-info.java":
        return False
    content = path.read_text(encoding="utf-8")
    license, body = split_license_and_body(content)
    if not DECL_LINE_RE.search(body):
        return False
    changed = False
    body, cc = add_all_class_javadocs(body, path)
    if cc:
        changed = True
    decl_count = len(list(DECL_LINE_RE.finditer(body)))
    for ordinal in range(decl_count):
        decls = list(DECL_LINE_RE.finditer(body))
        if ordinal >= len(decls):
            break
        decl_match = decls[ordinal]
        class_name = decl_match.group("name")
        kind = detect_kind(decl_match.group(0))
        body, mc = add_actor_method_javadocs(body, decl_match, class_name, kind)
        if mc:
            changed = True
    if changed and not dry_run:
        path.write_text(license + body, encoding="utf-8", newline="\n")
    return changed


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--base",
        type=Path,
        default=Path("application/src/main/java/org/thingsboard/server/actors"),
    )
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()
    count = 0
    for path in sorted(args.base.rglob("*.java")):
        if process_file(path, args.dry_run):
            count += 1
            print(path.name)
    print(f"Done: {count} files modified", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
