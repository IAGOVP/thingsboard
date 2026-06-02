# Tools — CLI & scripts reference

No REST endpoints. All tools are **command-line** or **manual test** utilities.

## `MigratorTool` (fat JAR main class)

Migrates telemetry from a **PostgreSQL SQL dump** into **Cassandra SSTables** for ThingsBoard.

| Option | Required | Description |
|--------|----------|-------------|
| `-telemetryFrom` | yes | Path to PostgreSQL dump file with telemetry |
| `-relatedEntities` | yes | Path to dump with entity id/type tables |
| `-castEnable` | yes | `true` to cast string values to double when possible |
| `-telemetryOut` | no | Output directory for `ts_kv_cf` SSTables |
| `-partitionsOut` | no | Output directory for partition metadata |
| `-latestTelemetryOut` | no | Output directory for `ts_kv_latest_cf` SSTables |

Example:

```bash
java -jar tools-*-jar-with-dependencies.jar \
  -telemetryFrom=/data/telemetry.sql \
  -relatedEntities=/data/entities.sql \
  -castEnable=true \
  -telemetryOut=/out/ts \
  -partitionsOut=/out/partitions \
  -latestTelemetryOut=/out/latest
```

## `TranslationPruner`

```bash
java -cp ... org.thingsboard.client.tools.i18n.TranslationPruner <sourceFolder> <destFolder>
```

Keeps only translation keys present in `destFolder/locale.constant-en_US.json` structure.

## `MqttSslClient`

Manual SSL MQTT publish test (hardcoded `ssl://localhost:1883`, topic `v1/devices/me/telemetry`). Requires `mqttclient.jks` on classpath.

## Python MQTT samples (`src/main/python/`)

| Script | Purpose |
|--------|---------|
| `simple-mqtt-client.py` | Basic MQTT connect/publish |
| `mqtt-send-telemetry.py` | Publish random telemetry JSON |
| `one-way-ssl-mqtt-client.py` | MQTT over TLS (server cert only) |
| `two-way-ssl-mqtt-client.py` | MQTT over mutual TLS |
| `check_yml_file.py` | Validate YAML syntax (CI/helper) |

Typical device topic: `v1/devices/me/telemetry` with device access token as username.

## Shell scripts (`src/main/shell/`)

| Script | Purpose |
|--------|---------|
| `server.keygen.sh` | Generate ThingsBoard server SSL keystore (`-c` copy to resources, `-p` properties file) |
| `client.keygen.sh` | Generate client keystore for MQTT SSL tests |
| `lwm2m/lwm2m_cfssl_chain_*.sh` | Generate test PKI chains for LwM2M (cfssl) |

`keygen.properties` — defaults for keystore paths and passwords.
