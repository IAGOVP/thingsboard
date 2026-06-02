# ThingsBoard Server Tools (`tools`)

Maven module with **offline utilities** for migration, SSL/MQTT testing, i18n maintenance, and **repository documentation scripts**.

There is **no HTTP server** and no production runtime dependency from tb-node.

## Layout

| Path | Purpose |
|------|---------|
| `src/main/java/.../migrator/` | PostgreSQL → Cassandra SSTable migration (`MigratorTool`) |
| `src/main/java/.../MqttSslClient.java` | Manual two-way MQTT SSL test client |
| `src/main/java/.../i18n/TranslationPruner.java` | Prune locale JSON to match reference keys |
| `src/main/python/` | Sample MQTT clients and YAML checker |
| `src/main/shell/` | TLS keystore generation, LwM2M test PKI chains |
| `scripts/` | Python helpers to bulk-add Javadoc across the repo |

## Build & run (Java)

```bash
mvn -pl tools package
java -jar tools/target/tools-*-jar-with-dependencies.jar [MigratorTool options]
```

Default main class: `org.thingsboard.client.tools.migrator.MigratorTool`.

## CLI reference

See [CLI.md](./CLI.md) for all commands, arguments, and MQTT topics.

## Documentation scripts (`scripts/`)

Used during codebase documentation work; safe to re-run on a module path:

| Script | Target |
|--------|--------|
| `add_java_javadoc.py` | Generic class-level Javadoc |
| `add_dao_javadoc.py` | `dao/` module |
| `add_edqs_javadoc.py` | `common/edqs`, `edqs` |
| `add_monitoring_javadoc.py` | `monitoring/` |
| `add_netty_mqtt_javadoc.py` | `netty-mqtt/` |
| `add_rest_client_javadoc.py` | `rest-client/` |
| `add_rule_engine_javadoc.py` | `rule-engine/` |
| `fix_javadoc_before_annotations.py` | Move misplaced class Javadoc |
| `fix_rule_engine_javadoc_placement.py` | Fix Javadoc inside `@RuleNode` |
| `extract_rest_client_paths.py` | Generate `rest-client/API_PATHS.txt` |
| `extract_rule_nodes.py` | Generate `rule-engine/RULE_NODES.md` |
| `add_tools_javadoc.py` | Class Javadoc for this module's Java sources |

See [scripts/README.md](./scripts/README.md).

## Related

- Migrator details: [src/main/java/org/thingsboard/client/tools/migrator/README.md](src/main/java/org/thingsboard/client/tools/migrator/README.md)
