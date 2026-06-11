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
package org.thingsboard.server.dao.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UUIDBased;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.sql.IdGenerator.GeneratedId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * JPA/Cassandra row model for base sql.
 *
 * <p>Maps database columns to domain objects via {@code toData()} conversion.
 */


@Data
@MappedSuperclass
public abstract class BaseSqlEntity<D> implements BaseEntity<D> {

    @Id
    @Column(name = ModelConstants.ID_PROPERTY, columnDefinition = "uuid")
    @GeneratedId
    protected UUID id;

    @Column(name = ModelConstants.CREATED_TIME_PROPERTY, updatable = false)
    protected long createdTime;

    public BaseSqlEntity() {
    }

    public BaseSqlEntity(BaseData<?> domain) {
        this.id = domain.getUuidId();
        this.createdTime = domain.getCreatedTime();
    }

    public BaseSqlEntity(BaseSqlEntity<?> entity) {
        this.id = entity.id;
        this.createdTime = entity.createdTime;
    }
    /**
     * Returns uuid.
     *
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public UUID getUuid() {
        return id;
    }
    /**
     * Set uuid.
     *
     * @param id entity UUID primary key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void setUuid(UUID id) {
        this.id = id;
    }
    /**
     * Returns created time.
     *
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public long getCreatedTime() {
        return createdTime;
    }
    /**
     * Set created time.
     *
     * @param createdTime created time
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setCreatedTime(long createdTime) {
        if (createdTime > 0) {
            this.createdTime = createdTime;
        }
    }
    /**
     * Returns uuid.
     *
     * @param uuidBased uuid based ({@link UUIDBased})
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected static UUID getUuid(UUIDBased uuidBased) {
        if (uuidBased != null) {
            return uuidBased.getId();
        } else {
            return null;
        }
    }
    /**
     * Returns tenant uuid.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected static UUID getTenantUuid(TenantId tenantId) {
        if (tenantId != null) {
            return tenantId.getId();
        } else {
            return EntityId.NULL_UUID;
        }
    }
    /**
     * Returns entity id.
     *
     * @param uuid uuid ({@link UUID})
     * @param creator creator ({@link Function})
     * @return {@link I}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected static <I> I getEntityId(UUID uuid, Function<UUID, I> creator) {
        return DaoUtil.toEntityId(uuid, creator);
    }
    /**
     * Returns tenant id.
     *
     * @param uuid uuid ({@link UUID})
     * @return {@link TenantId}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected static TenantId getTenantId(UUID uuid) {
        if (uuid != null && !uuid.equals(EntityId.NULL_UUID)) {
            return TenantId.fromUUID(uuid);
        } else {
            return TenantId.SYS_TENANT_ID;
        }
    }
    /**
     * To json.
     *
     * @param value value ({@link Object})
     * @return {@link JsonNode}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected JsonNode toJson(Object value) {
        if (value != null) {
            return JacksonUtil.valueToTree(value);
        } else {
            return null;
        }
    }
    /**
     * From json.
     *
     * @param json json ({@link JsonNode})
     * @param type type ({@link Class})
     * @return {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected <T> T fromJson(JsonNode json, Class<T> type) {
        return JacksonUtil.convertValue(json, type);
    }
    /**
     * Lists to string.
     *
     * @param list list ({@link List})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected String listToString(List<?> list) {
        if (list != null) {
            return StringUtils.join(list, ',');
        } else {
            return "";
        }
    }
    /**
     * Lists from string.
     *
     * @param string string ({@link String})
     * @param mappingFunction mapping function ({@link Function})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected <E> List<E> listFromString(String string, Function<String, E> mappingFunction) {
        if (string != null) {
            return Arrays.stream(StringUtils.split(string, ','))
                    .filter(StringUtils::isNotBlank)
                    .map(mappingFunction).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
