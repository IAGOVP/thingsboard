# EDQS HTTP endpoints

EDQS exposes **one** REST endpoint for orchestration health checks. All entity query traffic uses **Kafka** (`EdqsProcessor`).

| Method | Path | Handler | Description |
|--------|------|---------|-------------|
| `GET` | `/api/edqs/ready` | `EdqsController#isReady` | **200** when `EdqsStateService.isReady()` (state topic replay finished). **400** while warming up. |

## Kafka APIs (not HTTP)

| Direction | Topic (from `edqs.yml`) | Payload |
|-----------|-------------------------|---------|
| tb-node → EDQS | Events + request topics | `ToEdqsMsg` (protobuf) |
| EDQS → tb-node | Response topic | `FromEdqsMsg` with JSON `EdqsResponse` |

See `EdqsProcessor` and `common/data/.../edqs/query/EdqsRequest.java`.
