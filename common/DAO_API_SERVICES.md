# DAO API services (`common/dao-api`)

Interfaces in `org.thingsboard.server.dao.*` define **persistence and domain operations**.  
Implementations are in the **`dao`** Maven module (`Jpa*`, `Cassandra*`, etc.).

REST controllers in **`application`** call TB services (`service/entitiy/*`), which delegate to these interfaces.

## Base contracts

| Interface | Role |
|-----------|------|
| `entity.EntityDaoService` | Minimal find/count/delete by `EntityId`; extended by most entity services |
| `entity.EntityService` | Cross-entity operations, counts, existence |
| `entity.EntityCountService` | Entity count limits |
| `attributes.AttributesService` | Server/client/shared attributes |
| `timeseries.TimeseriesService` | Telemetry save/query/delete |
| `relation.RelationService` | Entity graph relations |

## Entity services (`extends EntityDaoService`)

| Interface | `EntityType` | Domain |
|-----------|--------------|--------|
| `tenant.TenantService` | TENANT | Tenants |
| `tenant.TenantProfileService` | TENANT_PROFILE | Tenant profiles |
| `customer.CustomerService` | CUSTOMER | Customers |
| `user.UserService` | USER | Users |
| `device.DeviceService` | DEVICE | Devices |
| `device.DeviceProfileService` | DEVICE_PROFILE | Device profiles |
| `asset.AssetService` | ASSET | Assets |
| `asset.AssetProfileService` | ASSET_PROFILE | Asset profiles |
| `dashboard.DashboardService` | DASHBOARD | Dashboards |
| `entityview.EntityViewService` | ENTITY_VIEW | Entity views |
| `alarm.AlarmService` | ALARM | Alarms |
| `rule.RuleChainService` | RULE_CHAIN | Rule chains |
| `widget.WidgetsBundleService` | WIDGETS_BUNDLE | Widget bundles |
| `widget.WidgetTypeService` | WIDGET_TYPE | Widget types |
| `resource.ResourceService` | TB_RESOURCE | Resources (JS, images) |
| `ota.OtaPackageService` | OTA_PACKAGE | OTA packages |
| `edge.EdgeService` | EDGE | Edge instances |
| `queue.QueueService` | QUEUE | Rule engine queues |
| `queue.QueueStatsService` | QUEUE_STATS | Queue statistics |
| `rpc.RpcService` | RPC | RPC records |
| `cf.CalculatedFieldService` | CALCULATED_FIELD | Calculated fields |
| `oauth2.OAuth2ClientService` | OAUTH2_CLIENT | OAuth2 clients |
| `domain.DomainService` | DOMAIN | Domains |
| `mobile.MobileAppService` | MOBILE_APP | Mobile apps |
| `mobile.MobileAppBundleService` | MOBILE_APP_BUNDLE | Mobile bundles |
| `usagerecord.ApiUsageStateService` | API_USAGE_STATE | API usage state |
| `settings.AdminSettingsService` | ADMIN_SETTINGS | Admin settings |
| `job.JobService` | JOB | Jobs |
| `ai.AiModelService` | AI_MODEL | AI models |
| `pat.ApiKeyService` | API_KEY | API keys |

## Other services (no `EntityDaoService`)

| Interface | Purpose |
|-----------|---------|
| `alarm.AlarmCommentService` | Alarm comments |
| `audit.AuditLogService` | Audit logs |
| `user.UserSettingsService` | Per-user UI settings |
| `device.DeviceCredentialsService` | Device credentials |
| `device.DeviceProvisionService` | Device provisioning |
| `device.DeviceConnectivityService` | Connectivity info |
| `device.ClaimDevicesService` | Device claiming |
| `component.ComponentDescriptorService` | Rule engine components |
| `event.EventService` | Debug/lifecycle events |
| `notification.NotificationService` | In-app notifications |
| `notification.NotificationRuleService` | Notification rules |
| `notification.NotificationTargetService` | Targets |
| `notification.NotificationTemplateService` | Templates |
| `notification.NotificationRequestService` | Notification requests |
| `notification.NotificationSettingsService` | Delivery settings |
| `oauth2.OAuth2ConfigTemplateService` | OAuth2 templates |
| `edge.EdgeEventService` | Edge events |
| `edge.RelatedEdgesService` | Related edges cache API |
| `resource.ImageService` | Image storage |
| `rule.RuleNodeStateService` | Rule node state |
| `usagerecord.ApiLimitService` | API limits |
| `usage.UsageInfoService` | Usage / license info |
| `trendz.TrendzSettingsService` | Trendz integration |

## Typical method patterns

- `find*ById(TenantId, *Id)` — load one entity (tenant-scoped)
- `save*(TenantId, entity)` — create/update
- `delete*(TenantId, id)` — remove
- `find*(TenantId, PageLink)` — paged lists
- `*Async` / `ListenableFuture` — non-blocking variants

See interface source for exact signatures; Swagger documents REST only in `application`.
