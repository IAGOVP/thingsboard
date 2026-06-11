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
package org.thingsboard.server.actors.shared;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.actors.ActorSystemContext;
import org.thingsboard.server.actors.TbActor;
import org.thingsboard.server.actors.TbActorId;
import org.thingsboard.server.actors.TbEntityActorId;
import org.thingsboard.server.actors.service.ContextAwareActor;
import org.thingsboard.server.actors.service.ContextBasedCreator;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.TbActorMsg;
import org.thingsboard.server.common.msg.aware.RuleChainAwareMsg;
import org.thingsboard.server.common.msg.queue.RuleEngineException;
/**
 * Fallback actor that logs and acknowledges rule-engine messages when target chain/node actors are missing.
 */

@Slf4j
public class RuleChainErrorActor extends ContextAwareActor {

    private final TenantId tenantId;
    private final RuleEngineException error;

    private RuleChainErrorActor(ActorSystemContext systemContext, TenantId tenantId, RuleEngineException error) {
        super(systemContext);
        this.tenantId = tenantId;
        this.error = error;
    }
    
    /**
     * Handles one incoming actor message; returns {@code true} if the message type was recognized.
     *
     * @param msg actor message to process
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected boolean doProcess(TbActorMsg msg) {
        if (msg instanceof RuleChainAwareMsg rcMsg) {
            log.debug("[{}] Reply with {} for message {}", tenantId, error.getMessage(), msg);
            rcMsg.getMsg().getCallback().onFailure(error);
            return true;
        } else {
            return false;
        }
    }

    /**

     * Factory for creating instances of the enclosing actor type.

     */

    public static class ActorCreator extends ContextBasedCreator {

        private final TenantId tenantId;
        private final RuleChainId ruleChainId;
        private final RuleEngineException error;

        public ActorCreator(ActorSystemContext context, TenantId tenantId, RuleChainId ruleChainId, RuleEngineException error) {
            super(context);
            this.tenantId = tenantId;
            this.ruleChainId = ruleChainId;
            this.error = error;
        }
        
        /**
         * Builds the {@link org.thingsboard.server.actors.TbActorId} used to register the actor.
         *
         * @return {@link TbActorId}
         * @throws Exception if an unexpected error occurs during processing
         */


        @Override
        public TbActorId createActorId() {
            return new TbEntityActorId(ruleChainId);
        }
        
        /**
         * Creates a new actor instance for the given actor id and context.
         *
         * @return {@link TbActor}
         * @throws Exception if an unexpected error occurs during processing
         */


        @Override
        public TbActor createActor() {
            return new RuleChainErrorActor(context, tenantId, error);
        }
    }

}
