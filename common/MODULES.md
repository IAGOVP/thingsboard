# `common` submodules — package layout

## `data` — `org.thingsboard.server.common.data`

| Subpackage | Contents |
|------------|----------|
| `id` | `EntityId`, `TenantId`, `DeviceId`, … |
| `device` | `Device`, profiles, credentials, transport config |
| `asset` | Assets, profiles |
| `alarm` | Alarms, comments, severity |
| `cf` | Calculated fields |
| `query` | Entity/alarm/data queries |
| `edqs` | EDQS query/response types |
| `notification` | Rules, templates, targets |
| `rule` | Rule chains, nodes, metadata |
| `widget` | Dashboard widgets |
| `security` | Credentials, JWT models |
| `sync` | Import/export, version control DTOs |
| `ai` | AI models |

## `dao-api` — `org.thingsboard.server.dao`

One package per domain (`device`, `alarm`, `tenant`, …). See [DAO_API_SERVICES.md](./DAO_API_SERVICES.md).

## `message` — `org.thingsboard.server.common.msg`

`TbMsg`, actor messages, queue callbacks, CF/edge message types.

## `queue` — `org.thingsboard.server.queue`

`kafka`, `discovery`, `provider`, `edqs`, `notification`, `task`, `environment`.

## `actor` — `org.thingsboard.server.actors`

`TbActor`, `TbActorSystem`, `TbActorRef`, mailbox, settings.

## `cache` — `org.thingsboard.server.cache`

`TbCache`, device/profile/edge caches, Valkey/Redis configuration.

## `script` — `org.thingsboard.script.api`

`ScriptInvokeService`, TBEL, `remote-js-client` for `js-executor` microservice.

## `edqs` — `org.thingsboard.server.edqs`

Query processors for entity data query service.

## `util` — `org.thingsboard.common.util`

Jackson helpers, geo, thread factories, `ThingsBoardExecutors`.

## `edge-api` — `org.thingsboard.edge.rpc`

`EdgeRpcClient` — Edge ↔ cloud sync.

## `version-control` — `org.thingsboard.server.service.sync.vc`

Git repository operations API (used by VC executor).

## `coap-server` — `org.thingsboard.server.coapserver`

CoAP server message deliverer, resources.

## `stats` — `org.thingsboard.server.common.stats`

Statistics/metrics recording helpers.

## `discovery-api` — service info

Service discovery for clustered deployments.
