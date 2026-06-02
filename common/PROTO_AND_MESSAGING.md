# Protobuf and messaging (`common/proto`, `common/message`, `common/queue`)

## Proto files (`common/proto/src/main/proto`)

| File | Java package | Purpose |
|------|--------------|---------|
| `queue.proto` | `org.thingsboard.server.gen.transport` | Core transport messages: telemetry, attributes, sessions, entity protos, `ToCoreMsg`, `ToRuleEngineMsg`, calculated fields |
| `transport.proto` | `transportapi` / `TransportApiProtos` | Transport API request wrappers (claim, gateway batches, RPC) |
| `jsinvoke.proto` | (JS executor) | Remote JavaScript invocation |
| `tbmsg.proto` | (imported) | Serialized `TbMsg` payload |

`EntityTypeProto` in `queue.proto` mirrors `org.thingsboard.server.common.data.EntityType`.

Generated Java is compiled into `common/proto` and depended on by `message`, `transport`, `application`.

## Rule engine message: `TbMsg`

Class: `org.thingsboard.server.common.msg.TbMsg`

- Flows through **rule chains** and **rule nodes**
- Fields: originator `EntityId`, type (`TbMsgType`), JSON data, metadata, rule chain id, queue name
- Serialized to Kafka between core and rule-engine in microservices mode

Related: `TbMsgMetaData`, `TbMsgCallback`, `MsgProtos`.

## Actor messages

Package: `org.thingsboard.server.common.msg`

- `TbActorMsg` — base for actor mailbox
- `ToCalculatedFieldSystemMsg` — calculated field subsystem
- `ToDeviceActorNotificationMsg` — device actor notifications
- Edge: `org.thingsboard.server.common.msg.edge.*`

Actor runtime: `common/actor` (`TbActorSystem`).

## Queue abstractions (`common/queue`, `common/cluster-api`)

| Type | Role |
|------|------|
| `TbQueueMsg` | Binary queue record + headers |
| `TbQueueProducer` / `TbQueueConsumer` | Publish/subscribe |
| `TbClusterService` | Route to core, rule engine, transport, EDQS, VC |
| `PartitionService` | Tenant/entity → Kafka partition |
| `TbCoreQueueFactory` | Topics used by core node |
| `TbRuleEngineQueueFactory` | Rule engine topics |
| `TbTransportQueueFactory` | Transport ↔ core |

Configuration is in `thingsboard.yml` (`queue.type`, Kafka topic names).

## Not HTTP

These APIs are **internal** (Java + Kafka + protobuf). External HTTP is documented under `application` and `docs/REST_API.md`.
