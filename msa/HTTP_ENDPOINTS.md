# HTTP endpoints in `msa`

Java microservices (`tb-node`, transports, `vc-executor`) use the **ThingsBoard REST API** documented in [../docs/REST_API.md](../docs/REST_API.md).

## `web-ui` (default port from `config/default.yml`)

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/*` (static) | Angular app from `web/public` |
| ALL | `/api/*` | Proxied to `thingsboard.host:port` when `thingsboard.enableProxy=true` |
| ALL | `/static/rulenode/*` | Proxied rule-node static assets |
| WebSocket | upgrade on same host | Proxied WS to core when proxy enabled |

Implementation: [web-ui/server.ts](./web-ui/server.ts).

## `js-executor` (port `http_port` in config)

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/livenessProbe` | Kubernetes liveness; returns `{ now: ISO timestamp }` |

Script execution is via **Kafka** topics, not HTTP. See [js-executor/api/jsInvokeMessageProcessor.ts](./js-executor/api/jsInvokeMessageProcessor.ts).

## No HTTP

| Module | Protocol |
|--------|----------|
| `tb-node` | REST on embedded Tomcat (same as monolith) |
| `transport/*` | Device protocols (MQTT, HTTP device API, …) |
| `vc-executor` | Internal queues + Git |
| `edqs` | gRPC/queue to core |
