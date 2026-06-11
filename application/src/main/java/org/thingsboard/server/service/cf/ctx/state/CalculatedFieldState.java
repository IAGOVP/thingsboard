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
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.actors.TbActorRef;
import org.thingsboard.server.common.data.cf.CalculatedFieldType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.util.CollectionsUtil;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.service.cf.CalculatedFieldResult;
import org.thingsboard.server.service.cf.ctx.CalculatedFieldEntityCtxId;
import org.thingsboard.server.service.cf.ctx.state.aggregation.RelatedEntitiesAggregationCalculatedFieldState;
import org.thingsboard.server.service.cf.ctx.state.aggregation.single.EntityAggregationCalculatedFieldState;
import org.thingsboard.server.service.cf.ctx.state.alarm.AlarmCalculatedFieldState;
import org.thingsboard.server.service.cf.ctx.state.geofencing.GeofencingArgumentEntry;
import org.thingsboard.server.service.cf.ctx.state.geofencing.GeofencingCalculatedFieldState;
import org.thingsboard.server.service.cf.ctx.state.propagation.PropagationCalculatedFieldState;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import static org.thingsboard.server.common.data.cf.configuration.PropagationCalculatedFieldConfiguration.PROPAGATION_CONFIG_ARGUMENT;
import static org.thingsboard.server.utils.CalculatedFieldUtils.toSingleValueArgumentProto;
/**
 * Runtime state for calculated field calculated fields.
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @Type(value = SimpleCalculatedFieldState.class, name = "SIMPLE"),
        @Type(value = ScriptCalculatedFieldState.class, name = "SCRIPT"),
        @Type(value = GeofencingCalculatedFieldState.class, name = "GEOFENCING"),
        @Type(value = AlarmCalculatedFieldState.class, name = "ALARM"),
        @Type(value = PropagationCalculatedFieldState.class, name = "PROPAGATION"),
        @Type(value = RelatedEntitiesAggregationCalculatedFieldState.class, name = "RELATED_ENTITIES_AGGREGATION"),
        @Type(value = EntityAggregationCalculatedFieldState.class, name = "ENTITY_AGGREGATION")
})
public interface CalculatedFieldState extends Closeable {
    
    /**
     * Returns type.
     *
     * @return {@link CalculatedFieldType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @JsonIgnore
    CalculatedFieldType getType();
/**
 * Returns entity id.
 *
 * @return {@link EntityId}
 * @throws Exception if an unexpected error occurs during processing
 */

    EntityId getEntityId();
/**
 * Returns arguments.
 *
 * @return {@link Map}
 * @throws Exception if an unexpected error occurs during processing
 */

    Map<String, ArgumentEntry> getArguments();
/**
 * Returns arguments json.
 *
 * @return {@link JsonNode}
 * @throws Exception if an unexpected error occurs during processing
 */

    JsonNode getArgumentsJson();
/**
 * Returns latest timestamp.
 *
 * @return the long result
 * @throws Exception if an unexpected error occurs during processing
 */

    long getLatestTimestamp();
/**
 * Set ctx.
 *
 * @param ctx calculated-field execution context
 * @param actorCtx actor ctx ({@link TbActorRef})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void setCtx(CalculatedFieldCtx ctx, TbActorRef actorCtx);
/**
 * Init.
 *
 * @param restored restored
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void init(boolean restored);
/**
 * Updates the requested data.
 *
 * @param arguments arguments ({@link Map})
 * @param ctx calculated-field execution context
 * @return {@link Map}
 * @throws Exception if an unexpected error occurs during processing
 */

    Map<String, ArgumentEntry> update(Map<String, ArgumentEntry> arguments, CalculatedFieldCtx ctx);
/**
 * Reset.
 *
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void reset();
/**
 * Perform calculation.
 *
 * @param updatedArgs updated args ({@link Map})
 * @param ctx calculated-field execution context
 * @return future completing with {@link CalculatedFieldResult}
 * @throws Exception if an unexpected error occurs during processing
 */

    ListenableFuture<CalculatedFieldResult> performCalculation(Map<String, ArgumentEntry> updatedArgs, CalculatedFieldCtx ctx) throws Exception;
    /**
     * Is ready.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @JsonIgnore
    boolean isReady();
/**
 * Returns readiness status.
 *
 * @return {@link ReadinessStatus}
 * @throws Exception if an unexpected error occurs during processing
 */

    ReadinessStatus getReadinessStatus();
/**
 * Is size exceeds limit.
 *
 * @return the boolean result
 * @throws Exception if an unexpected error occurs during processing
 */

    boolean isSizeExceedsLimit();
    /**
     * Is size ok.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @JsonIgnore
    default boolean isSizeOk() {
        return !isSizeExceedsLimit();
    }
/**
 * Returns partition.
 *
 * @return {@link TopicPartitionInfo}
 * @throws Exception if an unexpected error occurs during processing
 */

    TopicPartitionInfo getPartition();
/**
 * Set partition.
 *
 * @param partition partition ({@link TopicPartitionInfo})
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void setPartition(TopicPartitionInfo partition);
/**
 * Checks state size.
 *
 * @param ctxId ctx id ({@link CalculatedFieldEntityCtxId})
 * @param maxStateSize max state size
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void checkStateSize(CalculatedFieldEntityCtxId ctxId, long maxStateSize);
/**
 * Checks argument size.
 *
 * @param name name ({@link String})
 * @param entry entry ({@link ArgumentEntry})
 * @param ctx calculated-field execution context
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    default void checkArgumentSize(String name, ArgumentEntry entry, CalculatedFieldCtx ctx) {
        if (entry instanceof TsRollingArgumentEntry || entry instanceof GeofencingArgumentEntry) {
            return;
        }
        if (entry instanceof SingleValueArgumentEntry singleValueArgumentEntry) {
            if (ctx.getMaxSingleValueArgumentSize() > 0 && toSingleValueArgumentProto(name, singleValueArgumentEntry).getSerializedSize() > ctx.getMaxSingleValueArgumentSize()) {
                
                /**
                 * Illegal argument exception.
                 *
                 * @param calculation." calculation."
                 * @return the throw new value
                 * @throws Exception if an unexpected error occurs during processing
                 */

                throw new IllegalArgumentException("Single value size exceeds the maximum allowed limit. The argument will not be used for calculation.");
            }
        }
    }
/**
 * Readiness status.
 *
 * @param ready ready
 * @param errorMsg error msg ({@link String})
 * @return the record value
 * @throws Exception if an unexpected error occurs during processing
 */

    record ReadinessStatus(boolean ready, String errorMsg) {

        private static final String MISSING_REQUIRED_ARGUMENTS_ERROR = "Required arguments are missing: ";
        private static final String MISSING_PROPAGATION_TARGETS_ERROR = "No entities found via 'Propagation path to related entities'. " +
                                                                        "Verify the configured relation type and direction.";
        private static final String MISSING_PROPAGATION_TARGETS_AND_ARGUMENTS_ERROR = MISSING_PROPAGATION_TARGETS_ERROR + " Missing arguments to propagate: ";
        public static final String MISSING_AGGREGATION_ENTITIES_ERROR = "No entities found via 'Aggregation path to related entities'. " +
                                                                        "Verify the configured relation type and direction.";
        public static final ReadinessStatus READY = new ReadinessStatus(true, null);
/**
 * Not ready.
 *
 * @param errorMsg error msg ({@link String})
 * @return {@link ReadinessStatus}
 * @throws Exception if an unexpected error occurs during processing
 */

        public static ReadinessStatus notReady(String errorMsg) {
            
            /**
             * Readiness status.
             *
             * @return the return new value
             * @throws Exception if an unexpected error occurs during processing
             */

            return new ReadinessStatus(false, errorMsg);
        }
/**
 * From.
 *
 * @param emptyOrMissingArguments empty or missing arguments ({@link List})
 * @return {@link ReadinessStatus}
 * @throws Exception if an unexpected error occurs during processing
 */

        public static ReadinessStatus from(List<String> emptyOrMissingArguments) {
            if (CollectionsUtil.isEmpty(emptyOrMissingArguments)) {
                return ReadinessStatus.READY;
            }
            boolean propagationCtxIsEmpty = emptyOrMissingArguments.remove(PROPAGATION_CONFIG_ARGUMENT);
            if (!propagationCtxIsEmpty) {
                
                /**
                 * Not ready.
                 *
                 * @param String.join(" string.join("
                 * @return the return value
                 * @throws Exception if an unexpected error occurs during processing
                 */

                return notReady(MISSING_REQUIRED_ARGUMENTS_ERROR + String.join(", ", emptyOrMissingArguments));
            }
            if (emptyOrMissingArguments.isEmpty()) {
                
                /**
                 * Not ready.
                 *
                 * @return the return value
                 * @throws Exception if an unexpected error occurs during processing
                 */

                return notReady(MISSING_PROPAGATION_TARGETS_ERROR);
            }
            /**
             * Not ready.
             *
             * @param String.join(" string.join("
             * @return the return value
             * @throws Exception if an unexpected error occurs during processing
             */
            return notReady(MISSING_PROPAGATION_TARGETS_AND_ARGUMENTS_ERROR + String.join(", ", emptyOrMissingArguments));
        }
    }

}
