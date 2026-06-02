# ThingsBoard Monitoring Service (`monitoring`)

Synthetic **health monitoring** for a ThingsBoard deployment: probes transports (MQTT, HTTP, CoAP, LwM2M), validates WebSocket telemetry delivery, optionally checks EDQS entity queries, and notifies operators (Slack, etc.).

## How it works

```
Scheduler (ThingsboardMonitoringApplication)
    → BaseMonitoringService.runChecks()
        → TbClient.logIn()          # ThingsBoard REST
        → WsClient subscribe        # ThingsBoard WebSocket
        → BaseHealthChecker.check() # per transport target
        → MonitoringReporter        # latencies + Slack notifications
```

On first start, `MonitoringEntityService` creates devices, rule chain, and a public dashboard (`dashboard_cloud_monitoring.json`).

## Maven layout

| Path | Role |
|------|------|
| `monitoring/` | This module — application source |
| `msa/monitoring/` | Docker image (`tb-monitoring`) |

## HTTP / APIs

This service **does not expose** a public REST API. It **calls** ThingsBoard and Slack — see [HTTP_ENDPOINTS.md](./HTTP_ENDPOINTS.md).

## Configuration

- `src/main/resources/tb-monitoring.yml` — domains, transports, thresholds, Slack
- Main class: `org.thingsboard.monitoring.ThingsboardMonitoringApplication`

## Key classes

| Class | Role |
|-------|------|
| `ThingsboardMonitoringApplication` | Bootstraps scheduler and startup notification |
| `TransportsMonitoringService` | Transport monitoring loop |
| `BaseHealthChecker` | Single transport end-to-end test |
| `MonitoringReporter` | Failures, recovery, latency telemetry |
| `TbClient` / `WsClient` | ThingsBoard REST + WebSocket clients |
