# REST Client — HTTP endpoints

`RestClient` is a **client** for tb-node. Each method builds `baseURL + path` and uses Spring `RestTemplate`.

## Authentication

| Method | HTTP | Path |
|--------|------|------|
| `login(username, password)` | POST | `/api/auth/login` |
| `refreshToken()` | POST | `/api/auth/token` |
| `getAuthUser()` | GET | `/api/auth/user` |
| `logout()` | POST | `/api/auth/logout` |
| `changePassword(...)` | POST | `/api/auth/changePassword` |
| `withApiKey(...)` | — | Sets `ApiKey` on all requests |

## API areas (by path prefix)

| Prefix | `RestClient` examples | Server domain |
|--------|----------------------|---------------|
| `/api/admin/**` | `getAdminSettings`, `saveSecuritySettings` | System admin |
| `/api/alarm/**` | `getAlarmById`, `saveAlarm`, `ackAlarm` | Alarms |
| `/api/asset/**` | `getAssetById`, `saveAsset`, `getTenantAssets` | Assets |
| `/api/device/**` | `getDeviceById`, `saveDevice`, `getDevices` | Devices |
| `/api/customer/**` | `getCustomerById`, `saveCustomer` | Customers |
| `/api/tenant/**` | `getTenantById`, `saveTenant` | Tenants |
| `/api/user/**` | `saveUser`, `getUsers` | Users |
| `/api/dashboard/**` | `getDashboardById`, `saveDashboard` | Dashboards |
| `/api/ruleChain/**` | `getRuleChainById`, `saveRuleChain` | Rule chains |
| `/api/telemetry/**` | `saveEntityTelemetry`, `getTimeseries` | Telemetry |
| `/api/plugins/telemetry/**` | Attribute/timeseries plugin API | Telemetry plugin |
| `/api/entitiesQuery/**` | `findEntityDataByQuery`, `countEntitiesByQuery` | Entity queries |
| `/api/edge/**` | `getEdgeById`, `saveEdge` | Edges |
| `/api/entityView/**` | Entity views | Entity views |
| `/api/otaPackage/**` | OTA packages | OTA |
| `/api/resource/**` | TB resources / JS modules | Resources |
| `/api/cf/**` | Calculated fields | Calculated fields |
| `/api/ai/**` | AI models | AI |
| `/api/audit/**` | Audit logs | Audit |
| `/api/noauth/**` | Activation, password reset | Public auth flows |
| `/api/versionControl/**` | Git VC settings | Version control |
| `/api/apiKey/**` | API keys | API keys |
| `/api/domain/**` | Custom domains | Domains |
| `/api/queue/**` | Queues | Queues |
| `/api/notification/**` | Notification targets/templates | Notifications |
| `/api/image/**` | Image gallery | Images |
| `/api/mobile/**` | Mobile apps/bundles/QR | Mobile |
| `/api/oauth2/**` | OAuth2 clients | OAuth2 |
| `/api/twoFa/**` | 2FA settings | 2FA |
| `/api/ws/**` | WebSocket-related REST helpers | Web UI |

Every method in `RestClient.java` includes Javadoc with the **`/api/...` path** when it is a string literal in the method body.

## Complete path index

See [API_PATHS.txt](./API_PATHS.txt) for all **334** path templates extracted from `RestClient.java`.

## Related

- Server controllers: `application/src/main/java/org/thingsboard/server/controller/`
- OpenAPI-style catalog: [../docs/REST_API.md](../docs/REST_API.md)
