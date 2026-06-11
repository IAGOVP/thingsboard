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
package org.thingsboard.server.service.cf;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.actors.calculatedField.CalculatedFieldTelemetryMsg;
import org.thingsboard.server.common.data.cf.configuration.Argument;
import org.thingsboard.server.common.data.cf.configuration.aggregation.AggMetric;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.TbCallback;
import org.thingsboard.server.service.cf.ctx.CalculatedFieldEntityCtxId;
import org.thingsboard.server.service.cf.ctx.state.ArgumentEntry;
import org.thingsboard.server.service.cf.ctx.state.CalculatedFieldCtx;
import org.thingsboard.server.service.cf.ctx.state.aggregation.single.AggIntervalEntry;
import org.thingsboard.server.service.cf.ctx.state.propagation.PropagationArgumentEntry;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**

 * Service API for calculated-field argument resolution and result processing.

 *

 * <p>Fetches telemetry, attributes, and relations; dispatches results to the rule engine and linked fields.

 */

public interface CalculatedFieldProcessingService {
/**
 * Fetches arguments.
 *
 * @param ctx calculated-field execution context
 * @param entityId target entity identifier
 * @return future completing with {@link Map}
 * @throws Exception if an unexpected error occurs during processing
 */



    ListenableFuture<Map<String, ArgumentEntry>> fetchArguments(CalculatedFieldCtx ctx, EntityId entityId);
/**
 * Fetches dynamic args from db.
 *
 * @param ctx calculated-field execution context
 * @param entityId target entity identifier
 * @return {@link Map}
 * @throws Exception if an unexpected error occurs during processing
 */

    Map<String, ArgumentEntry> fetchDynamicArgsFromDb(CalculatedFieldCtx ctx, EntityId entityId);
/**
 * Fetches propagation argument from db.
 *
 * @param ctx calculated-field execution context
 * @param entityId target entity identifier
 * @return optional {@link PropagationArgumentEntry}, empty if not found
 * @throws Exception if an unexpected error occurs during processing
 */

    Optional<PropagationArgumentEntry> fetchPropagationArgumentFromDb(CalculatedFieldCtx ctx, EntityId entityId);
/**
 * Fetches related entities.
 *
 * @param ctx calculated-field execution context
 * @param entityId target entity identifier
 * @return {@link List}
 * @throws Exception if an unexpected error occurs during processing
 */

    List<EntityId> fetchRelatedEntities(CalculatedFieldCtx ctx, EntityId entityId);
/**
 * Fetches args from db.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityId target entity identifier
 * @param arguments arguments ({@link Map})
 * @return {@link Map}
 * @throws Exception if an unexpected error occurs during processing
 */

    Map<String, ArgumentEntry> fetchArgsFromDb(TenantId tenantId, EntityId entityId, Map<String, Argument> arguments);
/**
 * Processes result.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityId target entity identifier
 * @param cfName cf name ({@link String})
 * @param result result ({@link CalculatedFieldResult})
 * @param cfIds cf ids ({@link List})
 * @param callback queue callback invoked when processing completes
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void processResult(TenantId tenantId, EntityId entityId, String cfName, CalculatedFieldResult result, List<CalculatedFieldId> cfIds, TbCallback callback);
/**
 * Fetches metric during interval.
 *
 * @param tenantId tenant that owns the entity or operation
 * @param entityId target entity identifier
 * @param argKey arg key ({@link String})
 * @param metric metric ({@link AggMetric})
 * @param interval interval ({@link AggIntervalEntry})
 * @return {@link ArgumentEntry}
 * @throws Exception if an unexpected error occurs during processing
 */

    ArgumentEntry fetchMetricDuringInterval(TenantId tenantId, EntityId entityId, String argKey, AggMetric metric, AggIntervalEntry interval);
/**
 * Pushes msg to links.
 *
 * @param msg msg ({@link CalculatedFieldTelemetryMsg})
 * @param linkedCalculatedFields linked calculated fields ({@link List})
 * @param callback queue callback invoked when processing completes
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void pushMsgToLinks(CalculatedFieldTelemetryMsg msg, List<CalculatedFieldEntityCtxId> linkedCalculatedFields, TbCallback callback);

}
