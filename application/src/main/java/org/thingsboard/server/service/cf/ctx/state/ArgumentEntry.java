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
package org.thingsboard.server.service.cf.ctx.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.script.api.tbel.TbelCfArg;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.KvEntry;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.service.cf.ctx.state.aggregation.RelatedEntitiesArgumentEntry;
import org.thingsboard.server.service.cf.ctx.state.aggregation.single.EntityAggregationArgumentEntry;
import org.thingsboard.server.service.cf.ctx.state.geofencing.GeofencingArgumentEntry;
import org.thingsboard.server.service.cf.ctx.state.propagation.PropagationArgumentEntry;

import java.util.List;
import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
/**
 * Argument or aggregation entry for calculated-field state (argument entry).
 */
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SingleValueArgumentEntry.class, name = "SINGLE_VALUE"),
        @JsonSubTypes.Type(value = TsRollingArgumentEntry.class, name = "TS_ROLLING"),
        @JsonSubTypes.Type(value = GeofencingArgumentEntry.class, name = "GEOFENCING"),
        @JsonSubTypes.Type(value = PropagationArgumentEntry.class, name = "PROPAGATION"),
        @JsonSubTypes.Type(value = RelatedEntitiesArgumentEntry.class, name = "RELATED_ENTITIES"),
        @JsonSubTypes.Type(value = EntityAggregationArgumentEntry.class, name = "ENTITY_AGGREGATION")
})
public interface ArgumentEntry {
    
    /**
     * Returns type.
     *
     * @return {@link ArgumentEntryType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @JsonIgnore
    ArgumentEntryType getType();
/**
 * Returns value.
 *
 * @return {@link Object}
 * @throws Exception if an unexpected error occurs during processing
 */

    Object getValue();
/**
 * Updates entry.
 *
 * @param entry entry ({@link ArgumentEntry})
 * @param ctx calculated-field execution context
 * @return the boolean result
 * @throws Exception if an unexpected error occurs during processing
 */

    boolean updateEntry(ArgumentEntry entry, CalculatedFieldCtx ctx);
/**
 * Is empty.
 *
 * @return the boolean result
 * @throws Exception if an unexpected error occurs during processing
 */

    boolean isEmpty();
/**
 * Json value.
 *
 * @return {@link JsonNode}
 * @throws Exception if an unexpected error occurs during processing
 */

    default JsonNode jsonValue() {
        return JacksonUtil.valueToTree(toTbelCfArg());
    }
/**
 * To tbel cf arg.
 *
 * @return {@link TbelCfArg}
 * @throws Exception if an unexpected error occurs during processing
 */

    TbelCfArg toTbelCfArg();
/**
 * Is force reset previous.
 *
 * @return the boolean result
 * @throws Exception if an unexpected error occurs during processing
 */

    boolean isForceResetPrevious();
/**
 * Set force reset previous.
 *
 * @param forceResetPrevious force reset previous
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void setForceResetPrevious(boolean forceResetPrevious);
/**
 * Creates single value argument.
 *
 * @param kvEntry kv entry ({@link KvEntry})
 * @return {@link ArgumentEntry}
 * @throws Exception if an unexpected error occurs during processing
 */

    static ArgumentEntry createSingleValueArgument(KvEntry kvEntry) {
        
        /**
         * Single value argument entry.
         *
         * @return the return new value
         * @throws Exception if an unexpected error occurs during processing
         */

        return new SingleValueArgumentEntry(kvEntry);
    }
/**
 * Creates single value argument.
 *
 * @param entityId target entity identifier
 * @param argumentEntry argument entry ({@link ArgumentEntry})
 * @return {@link ArgumentEntry}
 * @throws Exception if an unexpected error occurs during processing
 */

    static ArgumentEntry createSingleValueArgument(EntityId entityId, ArgumentEntry argumentEntry) {
        
        /**
         * Single value argument entry.
         *
         * @return the return new value
         * @throws Exception if an unexpected error occurs during processing
         */

        return new SingleValueArgumentEntry(entityId, argumentEntry);
    }
/**
 * Creates ts rolling argument.
 *
 * @param kvEntries kv entries ({@link List})
 * @param limit limit
 * @param timeWindow time window
 * @return {@link ArgumentEntry}
 * @throws Exception if an unexpected error occurs during processing
 */

    static ArgumentEntry createTsRollingArgument(List<TsKvEntry> kvEntries, int limit, long timeWindow) {
        
        /**
         * Ts rolling argument entry.
         *
         * @return the return new value
         * @throws Exception if an unexpected error occurs during processing
         */

        return new TsRollingArgumentEntry(kvEntries, limit, timeWindow);
    }
/**
 * Creates geofencing value argument.
 *
 * @param entityIdkvEntryMap entity idkv entry map ({@link Map})
 * @return {@link ArgumentEntry}
 * @throws Exception if an unexpected error occurs during processing
 */

    static ArgumentEntry createGeofencingValueArgument(Map<EntityId, KvEntry> entityIdkvEntryMap) {
        
        /**
         * Geofencing argument entry.
         *
         * @return the return new value
         * @throws Exception if an unexpected error occurs during processing
         */

        return new GeofencingArgumentEntry(entityIdkvEntryMap);
    }
/**
 * Creates propagation argument.
 *
 * @param entityIds entity ids ({@link List})
 * @return {@link ArgumentEntry}
 * @throws Exception if an unexpected error occurs during processing
 */

    static ArgumentEntry createPropagationArgument(List<EntityId> entityIds) {
        
        /**
         * Propagation argument entry.
         *
         * @return the return new value
         * @throws Exception if an unexpected error occurs during processing
         */

        return new PropagationArgumentEntry(entityIds);
    }
/**
 * Creates agg argument.
 *
 * @param entityIdkvEntryMap entity idkv entry map ({@link Map})
 * @return {@link ArgumentEntry}
 * @throws Exception if an unexpected error occurs during processing
 */

    static ArgumentEntry createAggArgument(Map<EntityId, ArgumentEntry> entityIdkvEntryMap) {
        
        /**
         * Related entities argument entry.
         *
         * @return the return new value
         * @throws Exception if an unexpected error occurs during processing
         */

        return new RelatedEntitiesArgumentEntry(entityIdkvEntryMap, false);
    }

}
