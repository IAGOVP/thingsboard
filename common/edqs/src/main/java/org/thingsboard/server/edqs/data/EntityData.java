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
package org.thingsboard.server.edqs.data;

import org.thingsboard.server.common.data.AttributeScope;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edqs.fields.EntityFields;
import org.thingsboard.server.common.data.permission.QueryContext;
import org.thingsboard.server.common.data.query.EntityKeyType;
import org.thingsboard.server.common.data.edqs.DataPoint;
import org.thingsboard.server.edqs.query.DataKey;
import org.thingsboard.server.edqs.repo.TenantRepo;

import java.util.UUID;

/**
 * entity data contract (EDQS microservice — in-memory entity projections).
 */

public interface EntityData<T extends EntityFields> {

    
    /**
     * Returns id.
     *
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    UUID getId();

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityType getEntityType();

    
     /**
      * Returns customer id.
      *
      * @return {@link UUID}
      * @throws Exception if an unexpected error occurs during processing
      */

    UUID getCustomerId();

    
    /**
     * Set customer id.
     *
     * @param customerId customer scope for permission filtering (may be null)
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void setCustomerId(UUID customerId);

    
   /**
    * Set repo.
    *
    * @param repo tenant-scoped in-memory index to query
    * @return nothing
    * @throws Exception if an unexpected error occurs during processing
    */

    void setRepo(TenantRepo repo);

    
    /**
     * Returns fields.
     *
     * @return {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    T getFields();

    
   /**
    * Set fields.
    *
    * @param fields fields ({@link T})
    * @return nothing
    * @throws Exception if an unexpected error occurs during processing
    */

    void setFields(T fields);

    
   /**
    * Returns attr.
    *
    * @param keyId key id ({@link Integer})
    * @param entityKeyType entity key type ({@link EntityKeyType})
    * @return {@link DataPoint}
    * @throws Exception if an unexpected error occurs during processing
    */

    DataPoint getAttr(Integer keyId, EntityKeyType entityKeyType);

    
    /**
     * Put attr.
     *
     * @param keyId key id ({@link Integer})
     * @param scope scope ({@link AttributeScope})
     * @param value value ({@link DataPoint})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean putAttr(Integer keyId, AttributeScope scope, DataPoint value);

    
   /**
    * Removes attr.
    *
    * @param keyId key id ({@link Integer})
    * @param scope scope ({@link AttributeScope})
    * @return the boolean result
    * @throws Exception if an unexpected error occurs during processing
    */

    boolean removeAttr(Integer keyId, AttributeScope scope);

    
      /**
       * Returns ts.
       *
       * @param keyId key id ({@link Integer})
       * @return {@link DataPoint}
       * @throws Exception if an unexpected error occurs during processing
       */

    DataPoint getTs(Integer keyId);

    
     /**
      * Put ts.
      *
      * @param keyId key id ({@link Integer})
      * @param value value ({@link DataPoint})
      * @return the boolean result
      * @throws Exception if an unexpected error occurs during processing
      */

    boolean putTs(Integer keyId, DataPoint value);

    
      /**
       * Removes ts.
       *
       * @param keyId key id ({@link Integer})
       * @return the boolean result
       * @throws Exception if an unexpected error occurs during processing
       */

    boolean removeTs(Integer keyId);

    
     /**
      * Returns owner name.
      *
      * @return {@link String}
      * @throws Exception if an unexpected error occurs during processing
      */

    String getOwnerName();

    
    /**
     * Returns owner type.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    String getOwnerType();

    
     /**
      * Returns data point.
      *
      * @param key key ({@link DataKey})
      * @param queryContext query context ({@link QueryContext})
      * @return {@link DataPoint}
      * @throws Exception if an unexpected error occurs during processing
      */

    DataPoint getDataPoint(DataKey key, QueryContext queryContext);

    
    /**
     * Returns field.
     *
     * @param name name ({@link String})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    String getField(String name);

    
    /**
     * Is empty.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean isEmpty();

}
