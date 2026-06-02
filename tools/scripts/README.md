# Documentation maintenance scripts

Python utilities to add **class-level Javadoc** across ThingsBoard modules. They insert comments **before** type declarations (and before annotations when present).

## Usage

```bash
python tools/scripts/add_java_javadoc.py <path-to-module>
python tools/scripts/add_dao_javadoc.py dao
python tools/scripts/add_rule_engine_javadoc.py rule-engine
```

Add `--dry-run` where supported to preview without writing files.

## Scripts

| File | Description |
|------|-------------|
| `add_java_javadoc.py` | Generic humanized class descriptions for any Java tree |
| `add_dao_javadoc.py` | DAO-specific class names (`Base*Service`, `Jpa*Dao`, …) |
| `add_edqs_javadoc.py` | EDQS processor/repository wording |
| `add_monitoring_javadoc.py` | Monitoring service classes |
| `add_netty_mqtt_javadoc.py` | Netty MQTT client types |
| `add_rest_client_javadoc.py` | `RestClient` + method docs from `/api/` literals |
| `add_rule_engine_javadoc.py` | Rule nodes from `@RuleNode` metadata |
| `fix_javadoc_before_annotations.py` | Move class Javadoc above `@Slf4j` / `@Service` |
| `fix_rule_engine_javadoc_placement.py` | Fix Javadoc accidentally inside `@RuleNode(...)` |
| `extract_rest_client_paths.py` | Writes `rest-client/API_PATHS.txt` |
| `extract_rule_nodes.py` | Writes `rule-engine/RULE_NODES.md` |
| `add_tools_javadoc.py` | Class Javadoc for `tools/src/main/java` only |
| `add_transport_javadoc.py` | Class Javadoc for `transport/` and `common/transport/` |
| `fix_transport_javadoc_placement.py` | Move class Javadoc above `@Slf4j` / `@RestController` |
| `extract_transport_http_api.py` | Regenerate `transport/DEVICE_API.md` path table |
| `add_ui_ngx_javadoc.py` | Class-level JSDoc for `ui-ngx/src/app` TypeScript |
| `add_ui_ngx_service_method_docs.py` | Method JSDoc on `core/http/*.service.ts` with `/api/` URLs |
| `extract_ui_ngx_api_paths.py` | Generate `ui-ngx/HTTP_API_PATHS.txt` |
| `extract_ui_ngx_routes.py` | Generate `ui-ngx/ROUTES.txt` |
| `fix_ui_ngx_javadoc_placement.py` | Move JSDoc out of `@Injectable` / `@NgModule` blocks |

## Notes

- Prefer **manual** detailed Javadoc on core classes (`DefaultTbActorSystem`, `TbNode`, `RestClient` auth) after bulk runs.
- `add_rest_client_javadoc.py` adds many method comments; run `fix_javadoc` if annotations confuse placement.
- Do not run `fix_javadoc_before_annotations.py` on huge trees without timeout — regex can be slow on files with many `@link` tags.
