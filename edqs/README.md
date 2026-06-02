# Entity Data Query Service (`edqs`)

Standalone Spring Boot microservice that maintains an **in-memory entity index** for fast entity data and count queries in clustered ThingsBoard.

## Maven modules

| Path | Role |
|------|------|
| `edqs/` (this) | Runnable app: `ThingsboardEdqsApplication`, `EdqsController` |
| `common/edqs/` | Library: `EdqsProcessor`, `EdqsRepository`, query processors, RocksDB state |
| `msa/edqs/` | Docker image build |

## Request flow

```
tb-node  --Kafka request-reply-->  EdqsProcessor.handle()
                                        |
                                        v
                                 EdqsRepository / TenantRepo
                                        ^
tb-node  --Kafka events topic-->  EdqsProcessor.process()
```

State replay on startup: `EdqsStateService` → `GET /api/edqs/ready` returns 200 when complete.

## HTTP endpoints

See [HTTP_ENDPOINTS.md](./HTTP_ENDPOINTS.md). Only readiness is exposed; entity queries use Kafka.

## Configuration

- `src/main/resources/edqs.yml` — Kafka topics, partitioning, RocksDB paths
- Main class: `org.thingsboard.server.edqs.ThingsboardEdqsApplication`

## Tests

`src/test/java/.../repo/*Test.java` — filter and query behavior against in-memory `DefaultEdqsRepository`.

## Related

- Core REST (not EDQS): [../docs/REST_API.md](../docs/REST_API.md)
- Queue config: `org.thingsboard.server.queue.edqs` in `common/queue`
