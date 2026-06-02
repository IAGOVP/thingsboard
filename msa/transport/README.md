# Transport Docker images (`msa/transport`)

Each submodule builds a **protocol transport** container from the matching `transport/*` Maven module:

| Submodule | Protocol |
|-----------|----------|
| `mqtt` | MQTT |
| `http` | HTTP |
| `coap` | CoAP |
| `lwm2m` | LwM2M |
| `snmp` | SNMP |

Transports connect to **Kafka** (or configured queue) and **tb-node**; they do not expose the ThingsBoard REST API. Device traffic is handled on protocol-specific ports defined in `docker/` compose files.
