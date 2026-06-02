# Common transport libraries (`common/transport`)

Shared implementation used by **transport microservices** (`transport/`) and optionally the **monolith** when `transport.*.enabled` flags are set.

## Submodules

| Artifact | Role |
|----------|------|
| `transport-api` | `TransportService`, session/auth, limits, SSL config, `DefaultTransportService` |
| `http` | `DeviceApiController` — device REST under `/api/v1` |
| `mqtt` | Netty MQTT server, `MqttTransportHandler`, adaptors, gateway/Sparkplug |
| `coap` | Californium `CoapTransportResource`, DTLS, Efento adaptor |
| `lwm2m` | Leshan-based LwM2M server, OTA, downlink/uplink handlers |
| `snmp` | SNMP device polling, PDU service, transport balancing |

## Data flow

```
Device → (MQTT/HTTP/CoAP/LwM2M/SNMP) → TransportService → Kafka queue → tb-core / rule engine
```

Protobuf messages: `org.thingsboard.server.gen.transport.TransportProtos`.

## Key types

- `TransportService` — credential validation, telemetry/attributes/RPC to core
- `TransportContext` — Spring wiring per protocol
- `MqttTopics` (in `common/data`) — MQTT topic constants

See [transport/README.md](../../transport/README.md) for deployable services and endpoint catalogs.
