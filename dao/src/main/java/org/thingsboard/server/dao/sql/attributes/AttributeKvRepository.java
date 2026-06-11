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
package org.thingsboard.server.dao.sql.attributes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.BooleanDataEntry;
import org.thingsboard.server.common.data.kv.DoubleDataEntry;
import org.thingsboard.server.common.data.kv.JsonDataEntry;
import org.thingsboard.server.common.data.kv.KvEntry;
import org.thingsboard.server.common.data.kv.LongDataEntry;
import org.thingsboard.server.common.data.kv.StringDataEntry;
import org.thingsboard.server.dao.model.sql.AttributeKvCompositeKey;
import org.thingsboard.server.dao.model.sql.AttributeKvEntity;

import java.util.List;
import java.util.UUID;


/**

 * Spring Data JPA repository for attribute kv entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface AttributeKvRepository extends JpaRepository<AttributeKvEntity, AttributeKvCompositeKey> {

    @Query("SELECT a FROM AttributeKvEntity a WHERE a.id.entityId = :entityId " +
            "AND a.id.attributeType = :attributeType")
    /**
     * Finds all by entity id and attribute type.
     *
     * @param entityId target entity identifier
     * @param attributeType attribute type
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<AttributeKvEntity> findAllByEntityIdAndAttributeType(@Param("entityId") UUID entityId,
                                                              @Param("attributeType") int attributeType);

    @Transactional
    @Modifying
    @Query("DELETE FROM AttributeKvEntity a WHERE a.id.entityId = :entityId " +
            "AND a.id.attributeType = :attributeType " +
            "AND a.id.attributeKey = :attributeKey")
    /**
     * Deletes the requested data.
     *
     * @param entityId target entity identifier
     * @param attributeType attribute type
     * @param attributeKey attribute key
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */
    void delete(@Param("entityId") UUID entityId,
                @Param("attributeType") int attributeType,
                @Param("attributeKey") int attributeKey);

    @Query(value = "SELECT DISTINCT attribute_key FROM attribute_kv WHERE " +
            "entity_id in (SELECT id FROM device WHERE tenant_id = :tenantId and device_profile_id = :deviceProfileId limit 100) ORDER BY attribute_key", nativeQuery = true)
    /**
     * Finds all keys by device profile id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param deviceProfileId device profile id ({@link UUID})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<Integer> findAllKeysByDeviceProfileId(@Param("tenantId") UUID tenantId,
                                               @Param("deviceProfileId") UUID deviceProfileId);

    @Query(value = "SELECT DISTINCT attribute_key FROM attribute_kv WHERE " +
            "entity_id in (SELECT id FROM device WHERE tenant_id = :tenantId limit 100) ORDER BY attribute_key", nativeQuery = true)
    /**
     * Finds all keys by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<Integer> findAllKeysByTenantId(@Param("tenantId") UUID tenantId);

    @Query(value = "SELECT DISTINCT attribute_key FROM attribute_kv WHERE " +
            "entity_id in :entityIds ORDER BY attribute_key", nativeQuery = true)
    /**
     * Finds all keys by entity ids.
     *
     * @param entityIds entity ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<Integer> findAllKeysByEntityIds(@Param("entityIds") List<UUID> entityIds);

    @Query(value = "SELECT DISTINCT attribute_key FROM attribute_kv WHERE " +
            "entity_id in :entityIds AND attribute_type = :attributeType ORDER BY attribute_key", nativeQuery = true)
    /**
     * Finds all keys by entity ids and attribute type.
     *
     * @param entityIds entity ids ({@link List})
     * @param attributeType attribute type
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<Integer> findAllKeysByEntityIdsAndAttributeType(@Param("entityIds") List<UUID> entityIds,
            /**
             * On.
             *
             * @return {@link DISTINCT}
             * @throws Exception if an unexpected error occurs during processing
             */
                                                         @Param("attributeType") int attributeType);

    @Query(value = """
            SELECT DISTINCT ON (a.attribute_key)
                kd.key AS strKey,
                a.bool_v AS boolV, a.str_v AS strV, a.long_v AS longV,
                a.dbl_v AS dblV, a.json_v AS jsonV,
                a.last_update_ts AS lastUpdateTs, a.version AS version
            FROM attribute_kv a
            INNER JOIN key_dictionary kd ON a.attribute_key = kd.key_id
            WHERE a.entity_id IN :entityIds AND a.attribute_type = :attributeType
            ORDER BY a.attribute_key, a.last_update_ts DESC""", nativeQuery = true)
    /**
     * Finds latest by entity ids and attribute type.
     *
     * @param entityIds entity ids ({@link List})
     * @param attributeType attribute type
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<AttributeKvProjection> findLatestByEntityIdsAndAttributeType(@Param("entityIds") List<UUID> entityIds,
                                                                      @Param("attributeType") int attributeType);

    @Query(value = "SELECT attribute_key, attribute_type, entity_id, bool_v, dbl_v, json_v, last_update_ts, long_v, str_v, version FROM attribute_kv WHERE (entity_id, attribute_type, attribute_key) > " +
            "(:entityId, :attributeType, :attributeKey) ORDER BY entity_id, attribute_type, attribute_key LIMIT :batchSize", nativeQuery = true)
    /**
     * Finds next batch.
     *
     * @param entityId target entity identifier
     * @param attributeType attribute type
     * @param attributeKey attribute key
     * @param batchSize batch size
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */
    List<AttributeKvEntity> findNextBatch(@Param("entityId") UUID entityId,

    

    

    

    

    

    /**

     * attribute kv projection contract (JPA/PostgreSQL persistence layer (JPA repositories and PostgreSQL DAO implementations)).

     */





                                          @Param("attributeType") int attributeType,
                                          @Param("attributeKey") int attributeKey,
                                          @Param("batchSize") int batchSize);

    interface AttributeKvProjection {
    /**
     * Returns str key.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

        String getStrKey();
    /**
     * Returns bool v.
     *
     * @return {@link Boolean}
     * @throws Exception if an unexpected error occurs during processing
     */

        Boolean getBoolV();
    /**
     * Returns str v.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

        String getStrV();
    /**
     * Returns long v.
     *
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

        Long getLongV();
    /**
     * Returns dbl v.
     *
     * @return {@link Double}
     * @throws Exception if an unexpected error occurs during processing
     */

        Double getDblV();
    /**
     * Returns json v.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

        String getJsonV();
    /**
     * Returns last update ts.
     *
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

        Long getLastUpdateTs();
    /**
     * Returns version.
     *
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

        Long getVersion();
    /**
     * To attribute kv entry.
     *
     * @param p p ({@link AttributeKvProjection})
     * @return {@link AttributeKvEntry}
     * @throws Exception if an unexpected error occurs during processing
     */

        static AttributeKvEntry toAttributeKvEntry(AttributeKvProjection p) {
            KvEntry kvEntry = null;
            if (p.getStrV() != null) {
                kvEntry = new StringDataEntry(p.getStrKey(), p.getStrV());
            } else if (p.getBoolV() != null) {
                kvEntry = new BooleanDataEntry(p.getStrKey(), p.getBoolV());
            } else if (p.getDblV() != null) {
                kvEntry = new DoubleDataEntry(p.getStrKey(), p.getDblV());
            } else if (p.getLongV() != null) {
                kvEntry = new LongDataEntry(p.getStrKey(), p.getLongV());
            } else if (p.getJsonV() != null) {
                kvEntry = new JsonDataEntry(p.getStrKey(), p.getJsonV());
            }
            return new BaseAttributeKvEntry(kvEntry, p.getLastUpdateTs(), p.getVersion());
        }

    }

}
