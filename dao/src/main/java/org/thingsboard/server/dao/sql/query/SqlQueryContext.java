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
package org.thingsboard.server.dao.sql.query;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.permission.QueryContext;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
/**
 * Sql query context (JPA/PostgreSQL persistence layer (JPA repositories and PostgreSQL DAO implementations)).
 */







@Slf4j
public class SqlQueryContext implements SqlParameterSource {
    private static final UUIDJdbcType UUID_TYPE = UUIDJdbcType.INSTANCE;

    private final QueryContext securityCtx;
    private final StringBuilder query;
    private final Map<String, Parameter> params;

    public SqlQueryContext(QueryContext securityCtx) {
        this.securityCtx = securityCtx;
        query = new StringBuilder();
        params = new HashMap<>();
    }

    void addParameter(String name, Object value, int type, String typeName) {
        Parameter newParam = new Parameter(value, type, typeName);
        Parameter oldParam = params.put(name, newParam);
        if (oldParam != null && oldParam.value != null && !oldParam.value.equals(newParam.value)) {
            throw new RuntimeException("Parameter with name: " + name + " was already registered!");
        }
        if (value == null) {
            log.warn("[{}][{}][{}] Trying to set null value", getTenantId(), getCustomerId(), name);
        }
    }
    /**
     * Append.
     *
     * @param s s ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void append(String s) {
        query.append(s);
    }
    /**
     * Has value.
     *
     * @param paramName param name ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean hasValue(String paramName) {
        return params.containsKey(paramName);
    }
    /**
     * Returns value.
     *
     * @param paramName param name ({@link String})
     * @return {@link Object}
     * @throws IllegalArgumentException if illegal argument exception is thrown during processing
     */

    @Override
    public Object getValue(String paramName) throws IllegalArgumentException {
        return checkParameter(paramName).value;
    }
    /**
     * Returns sql type.
     *
     * @param paramName param name ({@link String})
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public int getSqlType(String paramName) {
        return checkParameter(paramName).type;
    }

    private Parameter checkParameter(String paramName) {
        Parameter param = params.get(paramName);
        if (param == null) {
            throw new RuntimeException("Parameter with name: " + paramName + " is not set!");
        }
        return param;
    }
    /**
     * Returns type name.
     *
     * @param paramName param name ({@link String})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String getTypeName(String paramName) {
        return params.get(paramName).name;
    }
    /**
     * Returns parameter names.
     *
     * @return the String[] value
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String[] getParameterNames() {
        return params.keySet().toArray(new String[]{});
    }
    /**
     * Add uuid parameter.
     *
     * @param name entity or attribute name
     * @param value value ({@link UUID})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addUuidParameter(String name, UUID value) {
        addParameter(name, value, UUID_TYPE.getJdbcTypeCode(), UUID_TYPE.getFriendlyName());
    }
    /**
     * Add string parameter.
     *
     * @param name entity or attribute name
     * @param value value ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addStringParameter(String name, String value) {
        addParameter(name, value, Types.VARCHAR, "VARCHAR");
    }
    /**
     * Add double parameter.
     *
     * @param name entity or attribute name
     * @param value value
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addDoubleParameter(String name, double value) {
        addParameter(name, value, Types.DOUBLE, "DOUBLE");
    }
    /**
     * Add long parameter.
     *
     * @param name entity or attribute name
     * @param value value
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addLongParameter(String name, long value) {
        addParameter(name, value, Types.BIGINT, "BIGINT");
    }
    /**
     * Add string list parameter.
     *
     * @param name entity or attribute name
     * @param value value ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addStringListParameter(String name, List<String> value) {
        addParameter(name, value, Types.VARCHAR, "VARCHAR");
    }
    /**
     * Add boolean parameter.
     *
     * @param name entity or attribute name
     * @param value value
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addBooleanParameter(String name, boolean value) {
        addParameter(name, value, Types.BOOLEAN, "BOOLEAN");
    }
    /**
     * Add uuid list parameter.
     *
     * @param name entity or attribute name
     * @param value value ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addUuidListParameter(String name, List<UUID> value) {
        addParameter(name, value, UUID_TYPE.getJdbcTypeCode(), UUID_TYPE.getFriendlyName());
    }
    /**
     * Returns query.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getQuery() {
        return query.toString();
    }


    


    



    




    





    






    /**






     * Parameter (JPA/PostgreSQL persistence layer (JPA repositories and PostgreSQL DAO implementations)).






     */







    public static class Parameter {
        private final Object value;
        private final int type;
        private final String name;

        public Parameter(Object value, int type, String name) {
            this.value = value;
            this.type = type;
            this.name = name;
        }
    }
    /**
     * Returns tenant id.
     *
     * @return {@link TenantId}
     * @throws Exception if an unexpected error occurs during processing
     */

    public TenantId getTenantId() {
        return securityCtx.getTenantId();
    }
    /**
     * Returns customer id.
     *
     * @return {@link CustomerId}
     * @throws Exception if an unexpected error occurs during processing
     */

    public CustomerId getCustomerId() {
        return securityCtx.getCustomerId();
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    public EntityType getEntityType() {
        return securityCtx.getEntityType();
    }
    /**
     * Is ignore permission check.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean isIgnorePermissionCheck() {
        return securityCtx.isIgnorePermissionCheck();
    }
}
