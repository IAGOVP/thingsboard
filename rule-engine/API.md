# Rule Engine — programming API

## `TbNode` lifecycle

| Method | When called | Responsibility |
|--------|-------------|----------------|
| `init(TbContext, TbNodeConfiguration)` | Node started / rule chain activated | Parse config, create clients |
| `onMsg(TbContext, TbMsg)` | Each message arrives | Process msg, route via context |
| `destroy()` | Node removed / chain stopped | Release script engines, connections |
| `onPartitionChangeMsg(...)` | Kafka partition change (cluster) | Optional rebalance handling |
| `upgrade(fromVersion, oldConfiguration)` | Config version migration | Return upgraded JSON config |

## `TbContext` — message flow

| Method | Effect |
|--------|--------|
| `tellSuccess(TbMsg)` | Route to connections labeled **Success** |
| `tellNext(TbMsg, relationType)` | Route to custom relation (e.g. `True`, `False`) |
| `tellFailure(TbMsg, Throwable)` | Route to **Failure** |
| `enqueue(TbMsg, ...)` | Async queue to another rule chain |
| `ack(TbMsg)` | Acknowledge without forwarding |

## `TbContext` — platform services (selection)

| Accessor | Use in nodes |
|----------|----------------|
| `getTimeseriesService()` | Read/write telemetry |
| `getAttributesService()` | Read/write attributes |
| `getAlarmService()` / `RuleEngineAlarmService` | Alarms |
| `getRpcService()` / `RuleEngineRpcService` | Device RPC |
| `createScriptEngine(...)` | TBEL/JS filters and transforms |
| `getMailService()` / `getSmsService()` | Notifications |
| `getDeviceService()`, `getAssetService()`, ... | Entity CRUD |
| `getRelationService()` | Entity relations |
| `getCalculatedFieldService()` | Calculated fields |

Full interface: `rule-engine-api/.../TbContext.java`.

## `@RuleNode` annotation

Registers metadata for the rule engine UI:

- `type` — FILTER, ENRICHMENT, ACTION, EXTERNAL, FLOW, etc.
- `name` — unique component name
- `configClazz` — configuration POJO class
- `relationTypes` — outgoing edge labels
- `nodeDescription` / `docUrl` — UI help

## HTTP endpoints

**None** in this module. REST for managing rule chains is in `application` controllers (`RuleChainController`, etc.) — see [../docs/REST_API.md](../docs/REST_API.md).

Nodes such as **REST API Call** perform outbound HTTP as a **client** when processing messages.
