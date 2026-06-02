# REST API controllers (application module)

All paths are relative to the server host (e.g. `http://localhost:8080`).  
Most user/tenant APIs require `Authorization: Bearer <JWT>` unless noted as `noauth`.

Constants are defined in `org.thingsboard.server.controller.TbUrlConstants`.

| Controller | Base path | Main responsibilities |
|------------|-----------|------------------------|
| `AdminController` | `/api/admin` | System administration |
| `AiModelController` | `/api/ai/model` | AI model configuration |
| `AlarmController` | `/api` | Alarms CRUD, assign, ack, clear |
| `AlarmCommentController` | `/api` | Alarm comments |
| `AlarmRuleController` | `/api` | Alarm rules |
| `ApiKeyController` | `/api` | User API keys (PAT) |
| `AssetController` | `/api` | Assets |
| `AssetProfileController` | `/api` | Asset profiles |
| `AuditLogController` | `/api` | Audit logs |
| `AuthController` | `/api` | Login user info, logout, password, activation (`/noauth/*`) |
| `CalculatedFieldController` | `/api` | Calculated fields CRUD, test script |
| `ComponentDescriptorController` | `/api` | Rule engine component descriptors |
| `CustomerController` | `/api` | Customers |
| `DashboardController` | `/api` | Dashboards |
| `DeviceController` | `/api` | Devices |
| `DeviceConnectivityController` | `/api` | Device connectivity info |
| `DeviceProfileController` | `/api` | Device profiles |
| `DomainController` | `/api` | Domains (white-label) |
| `EdgeController` | `/api` | Edge instances |
| `EdgeEventController` | `/api` | Edge events |
| `EntitiesVersionControlController` | `/api/entities/vc` | Git VC for entities |
| `EntityQueryController` | `/api` | Complex entity queries (EDQS) |
| `EntityRelationController` | `/api` | Entity relations |
| `EntityViewController` | `/api` | Entity views |
| `EventController` | `/api` | Lifecycle/debug events |
| `JobController` | `/api` | Background jobs |
| `Lwm2mController` | `/api` | LwM2M bootstrap/server config |
| `MailConfigTemplateController` | `/api/mail/config/template` | Mail templates |
| `MobileAppController` | `/api` | Mobile apps |
| `MobileAppBundleController` | `/api` | Mobile app bundles |
| `NotificationController` | `/api` | User notifications, settings |
| `NotificationRuleController` | `/api/notification` | Notification rules |
| `NotificationTargetController` | `/api/notification` | Notification targets |
| `NotificationTemplateController` | `/api/notification` | Notification templates |
| `OAuth2Controller` | `/api` | OAuth2 clients |
| `OAuth2ConfigTemplateController` | `/api/oauth2/config/template` | OAuth2 templates |
| `OtaPackageController` | `/api` | OTA packages |
| `QueueController` | `/api` | Queues (rule engine) |
| `QueueStatsController` | `/api` | Queue statistics |
| `RuleChainController` | `/api` | Rule chains |
| `RuleEngineController` | `/api/rule-engine/` | Inject messages into rule engine |
| `RpcV1Controller` | `/api/plugins/rpc` | One-way/two-way RPC v1 |
| `RpcV2Controller` | `/api/rpc` | RPC v2 |
| `SystemInfoController` | `/api` | Build info, system parameters |
| `TbResourceController` | `/api` | JS/CSS resources, images |
| `TelemetryController` | `/api/plugins/telemetry` | Telemetry & attributes API |
| `TenantController` | `/api` | Tenants |
| `TenantProfileController` | `/api` | Tenant profiles |
| `TrendzController` | `/api` | Trendz integration |
| `TwoFactorAuthController` | `/api/auth/2fa` | 2FA during login |
| `TwoFactorAuthConfigController` | `/api/2fa` | 2FA account/tenant settings |
| `UiSettingsController` | `/api` | UI settings |
| `UsageInfoController` | `/api` | License/usage info |
| `UserController` | `/api` | Users, credentials, settings |
| `WidgetsBundleController` | `/api` | Widget bundles |
| `WidgetTypeController` | `/api` | Widget types |

### Supporting controller types

| Class | Role |
|-------|------|
| `BaseController` | Shared helpers: `getCurrentUser()`, entity checks, `ThingsboardException` → HTTP |
| `AbstractRpcController` | Base for RPC controllers |
| `AutoCommitController` | Version control auto-commit |
| `ImageController` | Image serving |
| `QrCodeSettingsController` | QR code settings |

### WebSocket (not REST)

WebSocket API lives under `service/ws` and `controller/plugin` (e.g. telemetry subscriptions, notifications). Config: `config/WebSocketConfiguration.java`.

### Finding exact endpoints

1. Open the controller class and search for `@GetMapping`, `@PostMapping`, `@RequestMapping`.
2. Use Swagger UI when `swagger.api_path` is enabled in `thingsboard.yml`.
3. Official docs: https://thingsboard.io/docs/reference/rest-api/
