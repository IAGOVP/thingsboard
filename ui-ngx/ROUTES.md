# Angular routes (ui-ngx)

Top-level:

| Path | Module | Notes |
|------|--------|-------|
| `/` | redirect → `home` | `app-routing.module.ts` |
| `/login/**` | `LoginModule` | Public |
| `/home/**` | `HomeModule` | `AuthGuard`, lazy `HomePagesModule` |
| `/dashboard/**` | `DashboardRoutingModule` | Full-screen dashboard |

Under `/home` (lazy), major segments include:

| Segment | Area |
|---------|------|
| `devices`, `deviceProfiles` | Devices |
| `assets`, `assetProfiles` | Assets |
| `dashboards` | Dashboard library |
| `ruleChains` | Rule engine |
| `alarms` | Alarms |
| `customers` | Customers |
| `tenants`, `tenantProfiles` | Sys admin |
| `edges` | Edge instances |
| `notification/**` | Notification center |
| `resources`, `settings`, `security-settings` | Admin |
| `entities` | Entity hierarchy |
| `profiles` | Device/asset profiles |
| `vc` | Version control |
| `features` | Feature flags |
| `calculated-fields` | Calculated fields |
| `gateways` | IoT gateways |
| `mobile/**` | Mobile apps & bundles |

Regenerate full list: `python tools/scripts/extract_ui_ngx_routes.py` → `ROUTES.txt`.

Route definitions live in `src/app/modules/home/pages/**/*routing*.ts` and `home-routing.module.ts`.
