/**
 * Copyright © 2016-2026 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.service.install.migrate;

import com.datastax.oss.driver.api.core.cql.Row;
import lombok.Data;
import org.thingsboard.server.common.data.UUIDConverter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

    /**
     * Cassandra to sql column (database schema installation, upgrades, and demo data loading).
     */

@Data
public class CassandraToSqlColumn {

    private static final ThreadLocal<Pattern> PATTERN_THREAD_LOCAL = ThreadLocal.withInitial(() -> Pattern.compile(String.valueOf(Character.MIN_VALUE)));
    private static final String EMPTY_STR = "";

    private int index;
    private int sqlIndex;
    private String cassandraColumnName;
    private String sqlColumnName;
    private CassandraToSqlColumnType type;
    private int sqlType;
    private int size;
    private Class<? extends Enum> enumClass;
    private boolean allowNullBoolean = false;
    /**
     * Id column.
     *
     * @param name name ({@link String})
     * @return {@link CassandraToSqlColumn}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static CassandraToSqlColumn idColumn(String name) {
        return new CassandraToSqlColumn(name, CassandraToSqlColumnType.ID);
    }
    /**
     * String column.
     *
     * @param name name ({@link String})
     * @return {@link CassandraToSqlColumn}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static CassandraToSqlColumn stringColumn(String name) {
        return new CassandraToSqlColumn(name, CassandraToSqlColumnType.STRING);
    }
    /**
     * String column.
     *
     * @param cassandraColumnName cassandra column name ({@link String})
     * @param sqlColumnName sql column name ({@link String})
     * @return {@link CassandraToSqlColumn}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static CassandraToSqlColumn stringColumn(String cassandraColumnName, String sqlColumnName) {
        return new CassandraToSqlColumn(cassandraColumnName, sqlColumnName);
    }
    /**
     * Bigint column.
     *
     * @param name name ({@link String})
     * @return {@link CassandraToSqlColumn}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static CassandraToSqlColumn bigintColumn(String name) {
        return new CassandraToSqlColumn(name, CassandraToSqlColumnType.BIGINT);
    }
    /**
     * Double column.
     *
     * @param name name ({@link String})
     * @return {@link CassandraToSqlColumn}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static CassandraToSqlColumn doubleColumn(String name) {
        return new CassandraToSqlColumn(name, CassandraToSqlColumnType.DOUBLE);
    }
    /**
     * Boolean column.
     *
     * @param name name ({@link String})
     * @return {@link CassandraToSqlColumn}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static CassandraToSqlColumn booleanColumn(String name) {
        return booleanColumn(name, false);
    }
    /**
     * Boolean column.
     *
     * @param name name ({@link String})
     * @param allowNullBoolean allow null boolean
     * @return {@link CassandraToSqlColumn}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static CassandraToSqlColumn booleanColumn(String name, boolean allowNullBoolean) {
        return new CassandraToSqlColumn(name, name, CassandraToSqlColumnType.BOOLEAN, null, allowNullBoolean);
    }
    /**
     * Json column.
     *
     * @param name name ({@link String})
     * @return {@link CassandraToSqlColumn}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static CassandraToSqlColumn jsonColumn(String name) {
        return new CassandraToSqlColumn(name, CassandraToSqlColumnType.JSON);
    }
    /**
     * Enum to int column.
     *
     * @param name name ({@link String})
     * @param enumClass enum class ({@link Class})
     * @return {@link CassandraToSqlColumn}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static CassandraToSqlColumn enumToIntColumn(String name, Class<? extends Enum> enumClass) {
        return new CassandraToSqlColumn(name, CassandraToSqlColumnType.ENUM_TO_INT, enumClass);
    }

    public CassandraToSqlColumn(String columnName) {
        this(columnName, columnName, CassandraToSqlColumnType.STRING, null, false);
    }

    public CassandraToSqlColumn(String columnName, CassandraToSqlColumnType type) {
        this(columnName, columnName, type, null, false);
    }

    public CassandraToSqlColumn(String columnName, CassandraToSqlColumnType type, Class<? extends Enum> enumClass) {
        this(columnName, columnName, type, enumClass, false);
    }

    public CassandraToSqlColumn(String cassandraColumnName, String sqlColumnName) {
        this(cassandraColumnName, sqlColumnName, CassandraToSqlColumnType.STRING, null, false);
    }

    public CassandraToSqlColumn(String cassandraColumnName, String sqlColumnName, CassandraToSqlColumnType type,
                                Class<? extends Enum> enumClass, boolean allowNullBoolean) {
        this.cassandraColumnName = cassandraColumnName;
        this.sqlColumnName = sqlColumnName;
        this.type = type;
        this.enumClass = enumClass;
        this.allowNullBoolean = allowNullBoolean;
    }
    /**
     * Returns column value.
     *
     * @param row row ({@link Row})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getColumnValue(Row row) {
        if (row.isNull(index)) {
            if (this.type == CassandraToSqlColumnType.BOOLEAN && !this.allowNullBoolean) {
                return Boolean.toString(false);
            } else {
                return null;
            }
        } else {
            switch (this.type) {
                case ID:
                    return UUIDConverter.fromTimeUUID(row.getUuid(index));
                case DOUBLE:
                    return Double.toString(row.getDouble(index));
                case INTEGER:
                    return Integer.toString(row.getInt(index));
                case FLOAT:
                    return Float.toString(row.getFloat(index));
                case BIGINT:
                    return Long.toString(row.getLong(index));
                case BOOLEAN:
                    return Boolean.toString(row.getBoolean(index));
                case STRING:
                case JSON:
                case ENUM_TO_INT:
                default:
                    String value = row.getString(index);
                    return this.replaceNullChars(value);
            }
        }
    }
    /**
     * Set column value.
     *
     * @param sqlInsertStatement sql insert statement ({@link PreparedStatement})
     * @param value value ({@link String})
     * @return nothing
     * @throws SQLException if sqlexception is thrown during processing
     */

    public void setColumnValue(PreparedStatement sqlInsertStatement, String value) throws SQLException {
        if (value == null) {
            sqlInsertStatement.setNull(this.sqlIndex, this.sqlType);
        } else {
            switch (this.type) {
                case DOUBLE:
                    sqlInsertStatement.setDouble(this.sqlIndex, Double.parseDouble(value));
                    break;
                case INTEGER:
                    sqlInsertStatement.setInt(this.sqlIndex, Integer.parseInt(value));
                    break;
                case FLOAT:
                    sqlInsertStatement.setFloat(this.sqlIndex, Float.parseFloat(value));
                    break;
                case BIGINT:
                    sqlInsertStatement.setLong(this.sqlIndex, Long.parseLong(value));
                    break;
                case BOOLEAN:
                    sqlInsertStatement.setBoolean(this.sqlIndex, Boolean.parseBoolean(value));
                    break;
                case ENUM_TO_INT:
                    @SuppressWarnings("unchecked")
                    Enum<?> enumVal = Enum.valueOf(this.enumClass, value);
                    int intValue = enumVal.ordinal();
                    sqlInsertStatement.setInt(this.sqlIndex, intValue);
                    break;
                case JSON:
                case STRING:
                case ID:
                default:
                    sqlInsertStatement.setString(this.sqlIndex, value);
                    break;
            }
        }
    }

    private String replaceNullChars(String strValue) {
        if (strValue != null) {
            return PATTERN_THREAD_LOCAL.get().matcher(strValue).replaceAll(EMPTY_STR);
        }
        return strValue;
    }

}

