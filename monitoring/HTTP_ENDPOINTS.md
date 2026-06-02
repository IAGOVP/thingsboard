# Monitoring — external endpoints

The monitoring microservice has **no inbound HTTP API**. It acts as a **client** to other systems.

## Outbound — ThingsBoard REST (`TbClient` / `RestClient`)

Base URL: `monitoring.rest.base_url` (default `https://${monitoring.domain}`).

| Usage | Typical REST API (tb-node) | Called from |
|-------|---------------------------|-------------|
| Login | `POST /api/auth/login` | `TbClient.logIn()` |
| Entity query (EDQS check) | `POST /api/entitiesQuery/find` | `BaseMonitoringService.checkEdqs()` |
| Save latency telemetry | `POST /api/plugins/telemetry/ASSET/{id}/timeseries/ANY` | `MonitoringReporter.reportLatencies()` |
| Entity setup | devices, assets, dashboards, rule chains | `MonitoringEntityService` |

Full catalog: [../docs/REST_API.md](../docs/REST_API.md).

## Outbound — ThingsBoard WebSocket (`WsClient`)

URL: `monitoring.ws.base_url` (default `wss://${monitoring.domain}/api/ws`).

| Message | Purpose |
|---------|---------|
| Entity data subscribe (`EntityDataCmd`) | Listen for `testData` / `testDataCf` updates on monitoring devices |
| Latest value commands | Parse telemetry in `EntityDataUpdate` |

## Outbound — transport protocols

Configured under `monitoring.transports.*` in `tb-monitoring.yml`:

| Transport | Default target | Health checker |
|-----------|----------------|----------------|
| MQTT | `tcp://domain:1883` | `MqttTransportHealthChecker` |
| HTTP | device HTTP API URL | `HttpTransportHealthChecker` |
| CoAP | `coap://domain` | `CoapTransportHealthChecker` |
| LwM2M | LwM2M server URL | `Lwm2mTransportHealthChecker` |

## Outbound — notifications (Slack)

When `monitoring.notifications.slack` is enabled:

| API | Client | Purpose |
|-----|--------|---------|
| Slack Web API | `SlackApiClient` | Post failure/recovery/latency messages |
| Slack incidents (optional) | `SlackIncidentTransport` | Open/update incidents via `IncidentManager` |
