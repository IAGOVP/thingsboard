# Device protocols (`common/transport`)

ThingsBoard devices connect via **MQTT, HTTP, CoAP, LwM2M, or SNMP**.  
Protocol servers live in the **`transport`** Maven module; shared logic lives here under `common/transport`.

## Central API: `TransportService`

`org.thingsboard.server.common.transport.TransportService`

Used by transport microservices to:

| Operation | Description |
|-----------|-------------|
| Validate credentials | Token, MQTT user/pass, X.509, LwM2M |
| `getEntityProfile` / `getDevice` | Load profile and device after auth |
| `process(PostTelemetryMsg)` | Submit telemetry to queues → core |
| `process(PostAttributeMsg)` | Submit attributes |
| `process(SessionEventMsg)` | Connect/disconnect activity |
| RPC | `ToDeviceRpcRequestMsg`, subscriptions |
| Gateway | Child device create, gateway telemetry batches |
| Provision | `ProvisionDeviceRequestMsg` |
| LwM2M | `LwM2MRequestMsg` / response |

Callbacks: `TransportServiceCallback<T>`.  
Protobuf types: `org.thingsboard.server.gen.transport.TransportProtos`.

## Submodule layout

| Submodule | Protocol |
|-----------|----------|
| `transport-api` | `TransportService`, session, rate limits, auth |
| `mqtt` | MQTT / Sparkplug helpers |
| `http` | HTTP device API |
| `coap` | CoAP |
| `lwm2m` | LwM2M server integration |
| `snmp` | SNMP polling |

## Device-facing URLs (monolith / gateway)

When HTTP transport is enabled, devices typically use paths under the transport HTTP port (not `/api` UI API). Exact paths depend on `DeviceProfile` transport configuration.

UI and integrations use **`/api/plugins/telemetry`** etc. from `application` — see [../application/REST_API_CONTROLLERS.md](../application/REST_API_CONTROLLERS.md).

## Related docs

- [PROTO_AND_MESSAGING.md](./PROTO_AND_MESSAGING.md) — `PostTelemetryMsg`, sessions
- [../transport/](../transport/) — runnable transport applications
