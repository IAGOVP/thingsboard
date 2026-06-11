#!/usr/bin/env python3
"""Safe JavaDoc enhancement — only type and member declarations, never method bodies."""

import re
import sys
from pathlib import Path

MODULES = [
    "common/edqs/src/main/java",
    "common/script/script-api/src/main/java",
    "common/script/remote-js-client/src/main/java",
    "common/util/src/main/java",
    "common/actor/src/main/java",
    "common/coap-server/src/main/java",
    "common/cluster-api/src/main/java",
    "common/stats/src/main/java",
    "common/proto/src/main/java",
    "common/version-control/src/main/java",
    "common/edge-api/src/main/java",
    "common/discovery-api/src/main/java",
]

ROOT = Path(__file__).resolve().parent.parent

CLASS_DOCS = {
    "TbServiceInfoProvider": (
        "Provides identity and readiness of the local ThingsBoard microservice for queue discovery.\n\n"
        "<p>Exposes {@link org.thingsboard.server.gen.transport.TransportProtos.ServiceInfo} to partition "
        "services and tracks tenant-profile assignment for this node."
    ),
    "EdgeRpcClient": (
        "Bidirectional gRPC client for Edge-to-cloud synchronization.\n\n"
        "<p>Connects with edge credentials, receives downlink messages from the cloud, "
        "and sends uplink messages from the edge."
    ),
    "EdgeGrpcClient": "Default {@link EdgeRpcClient} using gRPC with optional TLS and HTTP proxy support.",
    "EdgeVersionComparator": (
        "Orders {@link org.thingsboard.server.gen.edge.v1.EdgeVersion} values for capability negotiation.\n\n"
        "<p>Treats {@code V_LATEST} as the newest defined version and {@code UNRECOGNIZED} as lowest."
    ),
    "EdgeConnectionException": "Unchecked exception when the edge cannot establish or maintain a cloud RPC connection.",
    "TbQueueMsg": (
        "Serialized payload on the cluster queue (typically Kafka).\n\n"
        "<p>Producers set {@link #getKey()} for partitioning; consumers decode {@link #getData()} per topic."
    ),
    "EntityData": (
        "In-memory EDQS view of a ThingsBoard entity with typed fields and attribute/time-series data points."
    ),
    "EdqsProcessor": "Core EDQS service that applies entity events to the in-memory repository and serves queries.",
    "JacksonUtil": "Central Jackson ObjectMapper helpers for ThingsBoard JSON serialization.",
    "ScriptInvokeService": "Invokes user-defined rule-engine and JS/TBEL scripts with sandboxing and timeout controls.",
    "GitSyncService": "Synchronizes ThingsBoard entity configuration with a remote Git repository.",
    "CoapServerService": "Lifecycle and configuration of the embedded Californium CoAP/DTLS server.",
    "StatsFactory": "Factory for Micrometer-backed counters, timers, and message statistics.",
    "MessagesStats": "Tracks message counts and throughput for transport and queue components.",
}


def camel_to_words(name: str) -> str:
    s = re.sub(r"([a-z])([A-Z])", r"\1 \2", name)
    s = re.sub(r"([A-Z]+)([A-Z][a-z])", r"\1 \2", s)
    return s.lower()


def is_bad(text: str) -> bool:
    if not text.strip():
        return True
    markers = [
        " contract.", "@return nothing", "@return returns the", "@return the boolean",
        "@throws Exception if an unexpected error", "Processes the requested",
        "(ThingsBoard common module)", "Exception:", "Compares the requested",
    ]
    t = text.lower()
    return any(m.lower() in t for m in markers)


def method_doc(name: str, ret: str) -> str:
    docs = {
        "getScheduler": "Shared scheduler for actor retries and delayed tasks.",
        "createDispatcher": "Binds a thread pool to a dispatcher id used when creating actors.",
        "destroyDispatcher": "Shuts down the executor and unregisters the dispatcher.",
        "getActor": "Returns the registered actor reference, or {@code null} if not found.",
        "createRootActor": "Creates a top-level actor (e.g. tenant or app root).",
        "createChildActor": "Creates a child actor under {@code parent}.",
        "tell": "Enqueues a message on the target actor's mailbox (normal priority).",
        "tellWithHighPriority": "Enqueues a high-priority message processed before normal queue traffic.",
        "filterChildren": "Returns child actor ids matching {@code childFilter}.",
        "broadcastToChildren": "Broadcasts a message to child actors.",
        "process": "Handles one mailbox message; return value meaning is actor-specific (often ignored).",
        "getActorRef": "Reference used to enqueue messages to this actor.",
        "init": "Invoked once after the actor is created; override to initialize state.",
        "destroy": "Invoked when the actor stops; override to release resources.",
        "onInitFailure": "Defines retry behavior after {@link #init} failure.",
        "onProcessFailure": "Defines behavior after an uncaught error in {@link #process}.",
        "getServiceId": "Unique id of this service instance in the cluster.",
        "getServiceType": "Logical service type (core, rule-engine, transport, edqs, …).",
        "getServiceInfo": "Protobuf descriptor published to the discovery topic.",
        "isMonolith": "Whether this JVM runs all service types (monolith deployment).",
        "isService": "Whether this JVM hosts the given service type.",
        "generateNewServiceInfoWithCurrentSystemInfo": "Builds service info with current JVM metrics.",
        "getAssignedTenantProfiles": "Tenant profile ids this node is responsible for (empty in monolith).",
        "setReady": "Marks the service ready to receive partition assignments; returns previous state.",
        "getKey": "Partition key for the queue message (often entity id).",
        "getHeaders": "Optional metadata headers attached to the message.",
        "getData": "Serialized message body (protobuf or JSON bytes).",
        "connect": "Opens the bidirectional RPC stream and sends a connect request.",
        "disconnect": "Completes the stream and shuts down the gRPC channel.",
        "sendUplinkMsg": "Sends an edge-originated uplink message to the cloud.",
        "sendSyncRequestMsg": "Requests a full or incremental sync from the cloud.",
        "sendDownlinkResponseMsg": "Acknowledges processing of a cloud downlink message.",
        "getServerMaxInboundMessageSize": "Maximum inbound message size negotiated with the server.",
        "compare": "Compares versions; {@code UNRECOGNIZED} is lowest, {@code V_LATEST} maps to newest.",
        "getNewestEdgeVersion": "Highest EdgeVersion enum constant excluding placeholders.",
        "commit": "Applies staged changes and completes the transaction.",
        "rollback": "Discards staged changes and releases the transaction.",
        "put": "Stages a key-value pair in the current transaction.",
        "stop": "Stops the actor and releases its resources.",
    }
    if name in docs:
        return docs[name]
    if name.startswith("get") and len(name) > 3:
        return f"Returns the {camel_to_words(name[3:])}."
    if name.startswith("is") and len(name) > 2:
        return f"Whether {camel_to_words(name[2:])}."
    if name.startswith("set") and len(name) > 3:
        return f"Sets the {camel_to_words(name[3:])}."
    if ret == "void":
        return f"{camel_to_words(name).capitalize()}."
    return f"{camel_to_words(name).capitalize()}."


def class_doc(name: str, kind: str, pkg: str) -> str:
    if name in CLASS_DOCS:
        return CLASS_DOCS[name]
    w = camel_to_words(name.replace("Default", ""))
    if kind == "interface":
        return f"Contract for {w}."
    if "Exception" in name:
        return f"Thrown when {w.replace(' exception', '')}."
    if name.startswith("Default"):
        return f"Default {{@link {name[7:]}}} implementation."
    if ".edqs.data." in pkg:
        return f"In-memory EDQS representation of {w}."
    if ".edqs.query." in pkg:
        return f"EDQS query processor for {w}."
    if ".edqs." in pkg:
        return f"EDQS component for {w}."
    if ".stats." in pkg:
        return f"Metrics support for {w}."
    if ".util." in pkg:
        return f"Utility methods for {w}."
    if ".script." in pkg:
        return f"Script engine support for {w}."
    if ".coapserver." in pkg:
        return f"CoAP server component for {w}."
    if ".queue." in pkg or ".cluster." in pkg:
        return f"Cluster queue API for {w}."
    if ".sync." in pkg or ".vc." in pkg:
        return f"Git entity version control for {w}."
    if ".adaptor." in pkg:
        return f"Transport adaptor utilities for {w}."
    return f"{kind.capitalize()} for {w}."


def wrap_block(doc: str) -> str:
    lines = ["/**"]
    for i, para in enumerate(doc.split("\n\n")):
        for ln in para.split("\n"):
            lines.append(f" * {ln}")
        if i < len(doc.split("\n\n")) - 1:
            lines.append(" *")
    lines.append(" */")
    return "\n".join(lines)


def enhance(content: str) -> str:
    pkg = re.search(r"package\s+([\w.]+);", content)
    pkg = pkg.group(1) if pkg else ""

    # Remove duplicate class javadocs
    content = re.sub(
        r"((?:/\*\*(?:[^*]|\*(?!/))*\*/\s*\n){2,})(?=\s*(?:@\w+|\s*(?:public|class|interface|enum)))",
        lambda m: max(re.findall(r"/\*\*(?:[^*]|\*(?!/))*\*/", m.group(1), re.DOTALL), key=len) + "\n",
        content,
        count=1,
    )

    tm = re.search(
        r"(/\*\*(?:[^*]|\*(?!/))*\*/\s*\n)?\s*((?:@\w+(?:\([^)]*\))?\s*\n)*)\s*"
        r"((?:public\s+)?(?:abstract\s+)?(?:class|interface|enum)\s+(\w+))",
        content,
    )
    if tm:
        existing = tm.group(1) or ""
        body = re.search(r"/\*\*(.*?)\*/", existing, re.DOTALL).group(1) if existing else ""
        kind = re.search(r"(class|interface|enum)", tm.group(3)).group(1)
        cname = tm.group(4)
        if not existing or is_bad(body):
            nd = wrap_block(class_doc(cname, kind, pkg))
            start = tm.start(1) if tm.group(1) else tm.start()
            content = content[:start] + nd + "\n" + content[tm.start(2):]

    content = re.sub(
        r"((?:@\w+(?:\([^)]*\))?\s*\n)+)\s*(/\*\*(?:[^*]|\*(?!/))*\*/\s*\n)\s*(public\s+(?:class|interface|enum))",
        r"\2\1\3",
        content,
        count=1,
    )

    lines = content.splitlines(keepends=True)
    out = []
    i = 0
    while i < len(lines):
        line = lines[i]

        # Interface / abstract method declaration ending with ;
        m = re.match(
            r"^(\s*)((?:/\*\*(?:[^*]|\*(?!/))*\*/\s*\n)*)((?:@\w+(?:\([^)]*\))?\s*\n)*)"
            r"((?:public|protected|private|default\s+)?(?:static\s+|final\s+|abstract\s+)*)"
            r"([\w<>,\[\]?]+\s+)(\w+)\([^)]*\)\s*(?:throws\s+[\w.,\s]+)?\s*;\s*$",
            line,
        )
        if not m:
            # interface method without modifiers: UUID getId();
            m = re.match(
                r"^(\s*)((?:/\*\*(?:[^*]|\*(?!/))*\*/\s*\n)*)((?:@\w+(?:\([^)]*\))?\s*\n)*)"
                r"([\w<>,\[\]?]+\s+)(\w+)\([^)]*\)\s*(?:throws\s+[\w.,\s]+)?\s*;\s*$",
                line,
            )
            if m:
                indent, docs, ann, ret, name = m.group(1), m.group(2), m.group(3), m.group(4).strip(), m.group(5)
                sig_prefix, access = "", ""
            else:
                out.append(line)
                i += 1
                continue
        else:
            indent, docs, ann, access, ret, name = m.group(1), m.group(2), m.group(3), m.group(4), m.group(5).strip(), m.group(6)
            if "private" in access:
                out.append(line)
                i += 1
                continue

        if name in ("values", "valueOf"):
            out.append(line)
            i += 1
            continue

        blocks = re.findall(r"/\*\*(?:[^*]|\*(?!/))*\*/", docs or "", re.DOTALL)
        keep = None
        if blocks:
            b = re.search(r"/\*\*(.*?)\*/", blocks[-1], re.DOTALL).group(1)
            if not is_bad(b):
                keep = blocks[-1].strip()
        if keep and keep.startswith("/**") and keep.endswith("*/") and "\n" not in keep:
            out.append(f"{indent}{keep}\n")
        elif keep:
            out.append(f"{indent}{keep}\n")
        else:
            out.append(f"{indent}/** {method_doc(name, ret.split()[0] if ret else '')} */\n")
        out.append(line)
        i += 1

    content = "".join(out)

    # Multi-line method javadoc before declarations (access-modifier methods)
    decl = re.compile(
        r"^(\s*)(/\*\*(?:[^*]|\*(?!/))*\*/\s*\n)\s*((?:@\w+(?:\([^)]*\))?\s*\n)*)"
        r"((?:public|protected|default)\s+(?:static\s+|synchronized\s+|final\s+|abstract\s+)*)"
        r"([\w<>,\[\]?]+\s+)?"
        r"(?P<tail>\w+\([^)]*\)\s*(?:throws\s+[\w.,\s]+)?\s*[;{])",
        re.MULTILINE,
    )

    def fix_decl(m):
        indent, doc, ann, access, ret, tail = m.groups()
        name = re.match(r"(\w+)", tail).group(1)
        if "private" in access or name in ("values", "valueOf", "main", "if", "for", "while"):
            return m.group(0)
        body = re.search(r"/\*\*(.*?)\*/", doc, re.DOTALL).group(1)
        if not is_bad(body):
            if "\n" not in doc.strip() and not re.search(r"@return|@param|@throws", body):
                return f"{indent}/** {body.strip()} */\n{ann}{access}{ret or ''}{tail}"
            return m.group(0)
        ret_type = (ret or "").strip()
        return f"{indent}/** {method_doc(name, ret_type)} */\n{ann}{access}{ret or ''}{tail}"

    # Only apply fix_decl outside of already processed single-line interface methods
    # Process class/interface body only once for { methods
    brace = content.find("{")
    if brace != -1:
        content = content[:brace] + decl.sub(fix_decl, content[brace:])

    content = re.sub(r"\n{3,}", "\n\n", content)
    return content


def main():
    n = 0
    for mod in MODULES:
        base = ROOT / mod
        if not base.exists():
            continue
        for path in sorted(base.rglob("*.java")):
            if path.name == "package-info.java":
                continue
            orig = path.read_text(encoding="utf-8")
            new = enhance(orig)
            if new != orig:
                path.write_text(new, encoding="utf-8", newline="\n")
                n += 1
    print(f"Total files modified: {n}", file=sys.stderr)


if __name__ == "__main__":
    main()
