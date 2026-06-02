# MQTT device & gateway topics

Constants: `common/data/.../MqttTopics.java`. Handler: `MqttTransportHandler`.

Auth: device access token as MQTT username (or TLS client cert per profile).

## Device API v1 (`v1/devices/me`)

| Topic | Direction | Purpose |
|-------|-----------|---------|
| `v1/devices/me/telemetry` | Publish | Time-series JSON or protobuf |
| `v1/devices/me/attributes` | Publish | Client attribute updates |
| `v1/devices/me/attributes/request/{id}` | Publish | Request shared/client attributes |
| `v1/devices/me/attributes/response/+` | Subscribe | Attribute read responses |
| `v1/devices/me/rpc/request/+` | Subscribe | Server-side RPC to device |
| `v1/devices/me/rpc/response/{id}` | Publish | RPC response from device |
| `v1/devices/me/claim` | Publish | Device claiming |
| `v1/gateway/connect` | Publish | Gateway child connect |
| `v1/gateway/disconnect` | Publish | Gateway child disconnect |
| `v1/gateway/telemetry` | Publish | Gateway multiplexed telemetry |
| `v1/gateway/attributes` | Publish | Gateway attributes |
| `v1/gateway/rpc` | Publish | Gateway RPC |
| `v1/gateway/attributes/request` | Publish | Gateway attribute request |
| `v1/gateway/attributes/response` | Subscribe | Gateway attribute response |
| `v1/gateway/claim` | Publish | Gateway claim |
| `/provision/request` | Publish | Device provisioning request |
| `/provision/response` | Subscribe | Provisioning response |

## Device API v2 (short topics under `v2/`)

Compact telemetry (`v2/t`, `v2/t/j`, `v2/t/p`), attributes (`v2/a/…`), RPC (`v2/r/…`), firmware (`v2/fw/…`), software (`v2/sw/…`). See `MqttTopics` for exact patterns with request/chunk IDs.

## Sparkplug B

Supported when enabled in device profile; handled in `MqttTransportHandler` (NBIRTH, NDATA, etc.).
