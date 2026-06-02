# ThingsBoard transport deployables (`transport/`)

Maven aggregator for **standalone transport microservices**. Each submodule is a thin Spring Boot launcher; protocol logic lives in [`common/transport/`](../common/transport/README.md).

## Modules

| Submodule | Main class | Config | Protocol |
|-----------|------------|--------|----------|
| [http](http/) | `ThingsboardHttpTransportApplication` | `tb-http-transport.yml` | HTTP REST `/api/v1` |
| [mqtt](mqtt/) | `ThingsboardMqttTransportApplication` | `tb-mqtt-transport.yml` | MQTT (Netty) |
| [coap](coap/) | `ThingsboardCoapTransportApplication` | `tb-coap-transport.yml` | CoAP |
| [lwm2m](lwm2m/) | `ThingsboardLwm2mTransportApplication` | `tb-lwm2m-transport.yml` | LwM2M |
| [snmp](snmp/) | `ThingsboardSnmpTransportApplication` | `tb-snmp-transport.yml` | SNMP polling |

## Build & run

```bash
mvn -pl transport/http package
java -jar transport/http/target/tb-http-transport-*-boot.jar
```

Each `main` injects `--spring.config.name=tb-<proto>-transport` when omitted.

## Device APIs & topics

| Doc | Content |
|-----|---------|
| [DEVICE_API.md](DEVICE_API.md) | HTTP paths (`DeviceApiController`) |
| [MQTT_TOPICS.md](MQTT_TOPICS.md) | MQTT v1/v2/gateway topic catalog |
| [PROTOCOLS.md](PROTOCOLS.md) | CoAP URI layout, LwM2M, SNMP overview |

Official device API docs: [ThingsBoard MQTT API](https://thingsboard.io/docs/reference/mqtt-api/), [HTTP API](https://thingsboard.io/docs/reference/http-api/).

## Documentation scripts

```bash
python tools/scripts/add_transport_javadoc.py transport common/transport
python tools/scripts/extract_transport_http_api.py
```
