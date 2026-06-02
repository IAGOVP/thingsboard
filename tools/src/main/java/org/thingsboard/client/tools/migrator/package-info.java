/**
 * Offline migration from PostgreSQL SQL dumps (COPY blocks) to Cassandra SSTables
 * for {@code ts_kv_cf}, {@code ts_kv_latest_cf}, and {@code ts_kv_partitions_cf}.
 *
 * <p>Entry point: {@link org.thingsboard.client.tools.migrator.MigratorTool#main(String[])}.
 * See {@code migrator/README.md} and {@code tools/CLI.md} for CLI flags.
 */
package org.thingsboard.client.tools.migrator;
