# ThingsBoard `dao` Module

Maven artifact: `org.thingsboard:dao`  
Implements persistence for all entities defined in `common/dao-api`.

## Role in the stack

```
REST (application/controller)  →  Tb*Service (application)  →  *Service (dao-api)
                                                                    ↓
                                                            *ServiceImpl / Base*Service (dao)
                                                                    ↓
                                                            *Dao → Jpa*Dao / Cassandra*
```

There are **no HTTP endpoints** in this module. REST paths are documented in [../application/REST_API_CONTROLLERS.md](../application/REST_API_CONTROLLERS.md) and [../docs/REST_API.md](../docs/REST_API.md).

## Package layout

| Package | Purpose |
|---------|---------|
| `dao.alarm` | Alarms, alarm comments |
| `dao.device` | Devices, profiles, credentials |
| `dao.attributes` | Key-value attributes (SQL/Cassandra) |
| `dao.timeseries` | Telemetry (`sqlts`, Cassandra) |
| `dao.sql` | JPA entities, repositories, `Jpa*Dao` |
| `dao.sqlts` | SQL time-series |
| `dao.nosql` | Cassandra base DAOs |
| `dao.relation` | Entity relations |
| `dao.entity` | Cross-entity helpers, `AbstractEntityService` |
| `dao.service` | `DataValidator`, install helpers |

API contracts: [../common/DAO_API_SERVICES.md](../common/DAO_API_SERVICES.md).

REST mapping (dao service → controller): [REST_MAPPING.md](./REST_MAPPING.md).

## Example: alarm comments

| Layer | Class |
|-------|--------|
| REST | `AlarmCommentController` → `/api/alarm/.../comment` |
| API | `common/dao-api` → `AlarmCommentService` |
| Service | `BaseAlarmCommentService` |
| DAO | `AlarmCommentDao` → `JpaAlarmCommentDao` |
| DB | `AlarmCommentEntity` / `alarm_comment` table |

## Regenerating class/method Javadoc

```bash
python tools/scripts/add_dao_javadoc.py dao/src/main/java
```
