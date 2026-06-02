# HTTP API usage from ui-ngx

The UI does **not** implement REST endpoints — it **consumes** ThingsBoard tb-node APIs.

## Convention

- Base: **`/api`** (relative; proxy in dev forwards to backend).
- Auth header: **`X-Authorization: Bearer &lt;jwt&gt;`** (see `GlobalHttpInterceptor`).
- Pagination: `PageLink.toQuery()` → `?pageSize=&page=&sortProperty=&sortOrder=`
- Entity scope: `/api/tenant/...`, `/api/customer/{customerId}/...`, `/api/user/...`

## Service map (`src/app/core/http/`)

| Service | Primary REST areas |
|---------|-------------------|
| `DeviceService` | `/api/device*`, `/api/tenant/deviceInfos`, claim, RPC |
| `AssetService` | `/api/asset*`, deviceInfos pattern for assets |
| `EntityService` | `/api/entitiesQuery`, timeseries, attributes |
| `DashboardService` | `/api/dashboard*`, `/api/customer/{id}/dashboards` |
| `RuleChainService` | `/api/ruleChain*`, `/api/ruleNode*` |
| `AlarmService` | `/api/alarm*` |
| `UserService` | `/api/user*`, `/api/users*` |
| `TenantService` | `/api/tenant*` |
| `CustomerService` | `/api/customer*` |
| `AttributeService` | Telemetry plugin URLs, attribute keys |
| `NotificationService` | `/api/notification/**` |
| `AdminService` | System settings, mail, queues |
| `OAuth2Service` | `/api/oauth2/**` |
| `CalculatedFieldsService` | `/api/calculatedField/**` |
| `EdgeService` | `/api/edge*` |
| `WidgetService` | `/api/widget*` |
| `EntitiesVersionControlService` | `/api/entities/vc/**` |

Full path inventory: **`HTTP_API_PATHS.txt`** (123+ unique patterns, generated from sources).

## WebSocket

Live telemetry and alarms use **WebSocket** APIs (not in `core/http` services) — see `websocket.service.ts` and dashboard subscriptions.

## Related

- Backend: [docs/REST_API.md](../docs/REST_API.md)
- Device protocols (not used by this UI): [transport/DEVICE_API.md](../transport/DEVICE_API.md)
