# Calculated Field — API and runtime

## REST (`CalculatedFieldController`)

Base: `/api` (JWT required; tenant/customer permissions apply).

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/calculatedField` | Create or update calculated field |
| GET | `/calculatedField/{calculatedFieldId}` | Get by id |
| DELETE | `/calculatedField/{calculatedFieldId}` | Delete |
| GET | `/{entityType}/{entityId}/calculatedFields` | List for entity (paged; `page`, `pageSize`) |
| GET | `/calculatedField/{entityType}/{entityId}` | List by entity |
| GET | `/calculatedFields` | List with filter (query params) |
| GET | `/calculatedFields/names` | List names |
| GET | `/calculatedField/{calculatedFieldId}/debug` | Debug events |
| POST | `/calculatedField/testScript` | Test expression with sample arguments |

See OpenAPI annotations on `CalculatedFieldController` for request bodies and query params.

## Runtime (actors)

1. **Queue** delivers `ToCalculatedFieldSystemMsg` to tenant manager or entity actor.
2. **`CalculatedFieldManagerActor`** — definitions, cache, partition changes, spawns entity actors.
3. **`CalculatedFieldEntityActor`** — runs `CalculatedFieldEntityMessageProcessor` on telemetry/relations/alarms.
4. **`service.cf`** — expression evaluation, RocksDB state, geofencing/aggregation/propagation strategies.

Entry class for message dispatch: `AbstractCalculatedFieldActor`.
