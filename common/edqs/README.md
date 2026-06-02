# `common/edqs` library

Shared EDQS implementation used by the `edqs` application and tb-node (producer side).

## Packages

| Package | Purpose |
|---------|---------|
| `processor` | `EdqsProcessor`, `EdqsProducer` — Kafka consume/produce |
| `repo` | `EdqsRepository`, `TenantRepo`, `DefaultEdqsRepository` |
| `query` | `EdqsQuery`, filters, sort keys |
| `query.processor` | One class per `EntityFilter` type |
| `data` | In-memory entity projections (`DeviceData`, `AssetData`, …) |
| `data.dp` | Typed attribute/telemetry values in the index |
| `state` | `EdqsStateService`, Kafka/local state replay |
| `util` | `RepositoryUtils`, `DefaultEdqsMapper`, RocksDB helpers |
| `stats` | `DefaultEdqsStatsService` metrics |

## Entry points for reading code

1. `EdqsProcessor` — event ingestion + query RPC
2. `DefaultEdqsRepository` / `TenantRepo` — index structure
3. `EntityQueryProcessorFactory` — filter → processor routing

Application HTTP: [../../edqs/HTTP_ENDPOINTS.md](../../edqs/HTTP_ENDPOINTS.md).
