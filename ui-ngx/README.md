# ThingsBoard Web UI (`ui-ngx`)

Angular 20 single-page application for the ThingsBoard platform (tenant admin, dashboards, rule chains, devices, alarms, etc.).

## Stack

| Layer | Technology |
|-------|------------|
| Framework | Angular 20, Angular Material, RxJS |
| State | NgRx (`@core/core.state`) |
| i18n | `@ngx-translate/core` |
| Maps/charts | Leaflet, ECharts, Flot (legacy widgets) |
| API | `HttpClient` → ThingsBoard REST `/api/**` (proxied to tb-node in dev) |

## Project layout (`src/app`)

| Path | Purpose |
|------|---------|
| `core/` | Auth, HTTP services, interceptors, guards, NgRx store, translate |
| `shared/` | Reusable components, pipes, directives, domain models |
| `modules/home/` | Main authenticated UI (entities, dashboards, rule engine, admin) |
| `modules/login/` | Login, 2FA, password reset |
| `modules/dashboard/` | Full-screen dashboard viewer routes |

## Build & dev

```bash
cd ui-ngx
yarn install
yarn start          # ng serve — http://localhost:4200
yarn build:prod     # production bundle → target/generated-resources
```

Dev server proxies `/api` to the backend (see `proxy.conf.js`).

## Documentation in this repo

| File | Content |
|------|---------|
| [ARCHITECTURE.md](ARCHITECTURE.md) | Bootstrap, routing, HTTP, state |
| [HTTP_API.md](HTTP_API.md) | How UI calls tb-node REST |
| [HTTP_API_PATHS.txt](HTTP_API_PATHS.txt) | Extracted `/api/` patterns (generated) |
| [ROUTES.md](ROUTES.md) | Angular route catalog |
| [ROUTES.txt](ROUTES.txt) | Raw route list (generated) |

Server REST reference: [docs/REST_API.md](../docs/REST_API.md).

## Documentation scripts

```bash
python tools/scripts/extract_ui_ngx_api_paths.py
python tools/scripts/extract_ui_ngx_routes.py
python tools/scripts/add_ui_ngx_javadoc.py ui-ngx/src/app
python tools/scripts/add_ui_ngx_service_method_docs.py
```
