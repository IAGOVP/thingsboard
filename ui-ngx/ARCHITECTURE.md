# ui-ngx architecture

## Bootstrap

```
main.ts → AppModule → AppComponent
         ├── AppRoutingModule (redirect '' → home)
         ├── CoreModule (HTTP, NgRx, translate, interceptors)
         ├── LoginModule
         └── HomeModule → HomeRoutingModule (AuthGuard) → HomePagesModule (lazy routes)
```

- **`TbUrlSerializer`**: encodes `(` `)` in URLs for router compatibility (`app.module.ts`).
- **`AppComponent`**: shell, outlet, global layout hooks.

## Authentication

- **`AuthService`**: login (`/api/auth/login`), JWT in memory/local storage, refresh token flow.
- **`GlobalHttpInterceptor`**: for `req.url.startsWith('/api/')` — adds `X-Authorization: Bearer …`, loading bar, 401 handling.
- **`AuthGuard`**: blocks `/home/**` when not authenticated.

Public auth endpoints (no JWT): `/api/auth/login`, `/api/auth/token`, etc. — see interceptor `isTokenBasedAuthEntryPoint`.

## HTTP layer

All tenant REST calls go through **`@core/http/*Service`** classes:

- Build URLs like `` `/api/tenant/devices${pageLink.toQuery()}` ``
- Options via **`http-utils.ts`**: `RequestConfig` (`ignoreLoading`, `ignoreErrors`, `resendRequest`)

Barrel export: `core/http/public-api.ts`.

## Feature modules

`modules/home/pages/*` — one area per entity (device, dashboard, rulechain, …). Typical files:

- `*-routing.module.ts` — child routes under `/home/...`
- `*.component.ts` — list/detail tabs
- `*-table-config.resolver.ts` — table columns and API for entity tables

## Widgets & dashboards

- Widget types: `modules/home/components/widget/`
- Runtime: dashboard page loads widget descriptors from server, compiles settings JSON into components.

## State

- **NgRx** in `core/`: load counter (interceptor), notifications, optional feature slices.
- Most entity screens use **RxJS** + services directly (not full entity store).

## Assets

- `src/assets/` — i18n JSON, help markdown for rule nodes, default dashboards, map styles.
