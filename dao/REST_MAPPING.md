# DAO layer ↔ REST API mapping

The `dao` module has **no HTTP endpoints**. Use this table to find the REST controller for a DAO service.

| DAO service (`dao` / `dao-api`) | REST controller (`application`) | Base path |
|--------------------------------|----------------------------------|-----------|
| `DeviceService` | `DeviceController` | `/api/device*` |
| `AlarmService` | `AlarmController` | `/api/alarm*` |
| `AlarmCommentService` | `AlarmCommentController` | `/api/alarm/{alarmId}/comment*` |
| `AssetService` | `AssetController` | `/api/asset*` |
| `CustomerService` | `CustomerController` | `/api/customer*` |
| `TenantService` | `TenantController` | `/api/tenant*` |
| `UserService` | `UserController` | `/api/user*` |
| `DashboardService` | `DashboardController` | `/api/dashboard*` |
| `RuleChainService` | `RuleChainController` | `/api/ruleChain*` |
| `TimeseriesService` | `TelemetryController` | `/api/plugins/telemetry` |
| `AttributesService` | `TelemetryController` | `/api/plugins/telemetry` |
| `RelationService` | `EntityRelationController` | `/api/relation*` |
| `CalculatedFieldService` | `CalculatedFieldController` | `/api/calculatedField*` |
| `EdgeService` | `EdgeController` | `/api/edge*` |

Full catalog: [../docs/REST_API.md](../docs/REST_API.md).
