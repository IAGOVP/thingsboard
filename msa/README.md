# ThingsBoard `msa` Module (Microservices Assembly)

Maven aggregator for **Docker images** and **standalone microservices** used in clustered deployments.

## Submodules

| Module | Runtime | Role |
|--------|---------|------|
| `tb` | Docker | Monolith image + Postgres/Cassandra DB images |
| `tb-node` | Docker (Java) | Core/rule-engine node (`application` JAR) |
| `web-ui` | Node.js | Serves Angular UI; optional proxy to `tb-node` |
| `js-executor` | Node.js | Remote JavaScript execution for rule engine |
| `vc-executor` | Java | Git version-control worker |
| `vc-executor-docker` | Docker | VC executor image build |
| `transport/*` | Docker | MQTT, HTTP, CoAP, LwM2M, SNMP transport images |
| `edqs` | Docker | Entity Data Query Service image |
| `monitoring` | Docker | Monitoring service image |
| `black-box-tests` | Java/Test | Integration and UI tests against Docker stacks |

## HTTP endpoints in `msa`

Only **Node** services expose HTTP here. Java services use the same REST API as `application` when run as `tb-node`.

See [HTTP_ENDPOINTS.md](./HTTP_ENDPOINTS.md).

## Build order (Yarn)

`ui-ngx` → `web-ui` → `js-executor` (serialized in Maven to avoid Yarn races).

## Related docs

- Cluster Docker: [../docker/README.md](../docker/README.md)
- REST API: [../docs/REST_API.md](../docs/REST_API.md)
- Core server: [../application/README.md](../application/README.md)
