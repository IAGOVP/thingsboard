# Transport protocols (non-HTTP)

## CoAP

Resource: `CoapTransportResource` (Californium).

URI pattern (access token in path):

```
/api/v1/{deviceToken}/{featureType}
/api/v1/{deviceToken}/{featureType}/{requestId}   # RPC
```

`featureType` maps to `FeatureType`: `attributes`, `telemetry`, `rpc`, etc.

- **GET** + Observe: subscribe to attribute or RPC updates
- **POST**: publish telemetry, post attributes, RPC request/response
- **DTLS**: X.509 or PSK; session map `TbCoapDtlsSessionInfo`

Efento devices: `CoapEfentoTransportResource` with vendor-specific URI layout.

Default port: configured in `tb-coap-transport.yml` (often 5683/5684).

## LwM2M

Server: Leshan in `common/transport/lwm2m`.

- Registration, observe, read/write on LwM2M objects
- OTA firmware/software via LwM2M objects
- DTLS identity per device profile
- Uplink: `DefaultLwM2mUplinkMsgHandler` → `TransportService`

Config: `tb-lwm2m-transport.yml`. No REST device API — standard LwM2M protocol only.

## SNMP

Polling transport for SNMP-managed devices (not device-initiated HTTP/MQTT).

- `SnmpTransportContext` — device list from core
- `PduService` — GET/WALK schedules per device profile
- Telemetries posted via `TransportService` like other transports

Config: `tb-snmp-transport.yml`.

## Monolith vs microservice

HTTP device API is also available in monolith when:

`service.type=monolith` AND `transport.api_enabled=true` AND `transport.http.enabled=true`

See `@ConditionalOnExpression` on `DeviceApiController`.
