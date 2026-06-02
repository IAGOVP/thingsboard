# ThingsBoard Server Application Module

Maven artifact: `org.thingsboard:application`  
Main class: `org.thingsboard.server.ThingsboardServerApplication`

This module is the **core ThingsBoard server** (monolith or `tb-node` in microservices). It exposes REST APIs, WebSockets, runs the rule engine and calculated-field actors, and orchestrates calls into `dao`, `rule-engine`, and `common`.

## Source layout

```
src/main/
├── conf/                    # thingsboard.conf (service install)
├── data/                    # Seed widgets, SCADA symbols, LwM2M registry, DB upgrade SQL
├── java/org/thingsboard/server/
│   ├── ThingsboardServerApplication.java   # Spring Boot entry point
│   ├── controller/          # REST API (@RestController)
│   ├── service/             # Business logic, queues, security, WebSocket
│   ├── actors/              # TbActor runtime (device, rule chain, calculated fields)
│   ├── config/              # Spring @Configuration
│   ├── install/             # First-time install helpers
│   ├── exception/           # Server-specific exceptions
│   └── utils/               # Upgrade and misc utilities
└── resources/
    ├── thingsboard.yml      # Main configuration (classpath)
    ├── logback.xml
    └── templates/           # Email (FreeMarker)
```

## Request flow (typical REST call)

1. **HTTP** → Spring Security (`service/security`) validates JWT or device credentials.
2. **Controller** (`controller/*Controller.java`) maps URL → method; checks `@PreAuthorize` / permissions.
3. **Tb* service** (`service/entitiy/*`, `service/*`) applies tenant scope and business rules.
4. **DAO** (`dao` module) persists or reads PostgreSQL / Cassandra.
5. **Cluster / queue** (`service/queue`, `common/queue`) publishes events to rule engine or other nodes when clustered.

Device telemetry often arrives via **transport** modules and **queues**, then rule engine actors—not always through REST.

## REST API surface

| Prefix | Constant / controller | Purpose |
|--------|----------------------|---------|
| `/api` | Most `*Controller` classes | CRUD for tenants, devices, dashboards, alarms, users, etc. |
| `/api/plugins/telemetry` | `TelemetryController`, `TbUrlConstants.TELEMETRY_URL_PREFIX` | Post/query telemetry & attributes (device API) |
| `/api/plugins/rpc` | `RpcV1Controller`, `RPC_V1_URL_PREFIX` | Device RPC v1 |
| `/api/rpc` | `RpcV2Controller`, `RPC_V2_URL_PREFIX` | Device RPC v2 |
| `/api/rule-engine/` | `RuleEngineController`, `RULE_ENGINE_URL_PREFIX` | Push messages into rule chains |
| `/api/entities/vc` | `EntitiesVersionControlController` | Git-based entity version control |
| `/api/notification` | Notification* controllers | Notification rules, targets, templates |
| `/api/auth/2fa`, `/api/2fa` | TwoFactorAuth* controllers | MFA login and settings |
| `/api/admin` | `AdminController` | System admin operations |

Interactive API docs (when enabled): **Swagger UI** — see `config/SwaggerConfiguration` and `@ApiOperation` on controller methods.

Full controller → base path index: [REST_API_CONTROLLERS.md](./REST_API_CONTROLLERS.md).

Calculated fields (REST + actors): [CALCULATED_FIELD_API.md](./CALCULATED_FIELD_API.md).

## Package guide (`org.thingsboard.server`)

| Package | Role |
|---------|------|
| `controller` | REST endpoints; extends `BaseController` for auth, validation, exception handling |
| `service.entitiy` | Per-entity TB services (`TbDeviceService`, `TbCalculatedFieldService`, …) |
| `service.security` | JWT, OAuth2, MFA, permissions, device auth |
| `service.queue` | Kafka consumers: rule engine, calculated fields, core |
| `service.subscription` | WebSocket entity/telemetry subscriptions |
| `service.ws` | WebSocket protocol commands (telemetry, alarms, notifications) |
| `service.edge` | ThingsBoard Edge sync RPC |
| `service.cf` | Calculated field evaluation context and state (RocksDB, geofencing, etc.) |
| `service.sync` | Import/export and version control |
| `actors` | High-throughput actor mailboxes per tenant/device/rule chain |
| `actors.calculatedField` | Calculated field manager + per-entity actors |
| `actors.device` | Device sessions, RPC, attribute updates |
| `actors.ruleChain` | Rule chain and rule node execution |
| `config` | Spring beans: security, WebSocket, rate limits, Swagger |

## Actors vs services

- **Services**: Spring singletons; used from controllers and scheduled jobs.
- **Actors**: One mailbox per logical entity (e.g. one `DeviceActor` per device); serializes concurrent messages. Calculated fields use `CalculatedFieldManagerActor` (tenant) and `CalculatedFieldEntityActor` (entity).

See `actors/package-info.java` and `actors/calculatedField/package-info.java`.

## Configuration

- **File**: `src/main/resources/thingsboard.yml` (overridden by `/etc/thingsboard/conf/thingsboard.conf` in packages).
- **Profiles**: install, upgrade, `spring.config.name=thingsboard` (set automatically in `ThingsboardServerApplication` if omitted).

## Related modules

| Module | Used for |
|--------|----------|
| `dao` | Database access |
| `rule-engine` | Rule node implementations |
| `common/*` | Data models, queue, cache, transport API — see [../common/README.md](../common/README.md) |
| `ui-ngx` | Angular UI (built into jar or served by `web-ui`) |

## Building

From repo root: `mvn clean install -DskipTests` (or build only `application` with `-pl application -am`).
