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
package org.thingsboard.server.actors.ruleChain;

import org.thingsboard.server.actors.ActorSystemContext;
import org.thingsboard.server.actors.TbActor;
import org.thingsboard.server.actors.TbActorCtx;
import org.thingsboard.server.actors.TbActorId;
import org.thingsboard.server.actors.TbEntityActorId;
import org.thingsboard.server.actors.service.ContextBasedCreator;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.msg.TbActorMsg;
import org.thingsboard.server.common.msg.plugin.ComponentLifecycleMsg;
import org.thingsboard.server.common.msg.queue.PartitionChangeMsg;
import org.thingsboard.server.common.msg.queue.QueueToRuleEngineMsg;

/**

 * Actor representing one rule chain; routes {@link org.thingsboard.server.common.msg.TbMsg} through its rule nodes.

 */

public class RuleChainActor extends RuleEngineComponentActor<RuleChainId, RuleChainActorMessageProcessor> {

    private final RuleChain ruleChain;

    private RuleChainActor(ActorSystemContext systemContext, TenantId tenantId, RuleChain ruleChain) {
        super(systemContext, tenantId, ruleChain.getId());
        this.ruleChain = ruleChain;
    }
    /**
     * Creates processor.
     *
     * @param ctx actor context ({@link org.thingsboard.server.actors.TbActorCtx})
     * @return {@link RuleChainActorMessageProcessor}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected RuleChainActorMessageProcessor createProcessor(TbActorCtx ctx) {
        return new RuleChainActorMessageProcessor(tenantId, ruleChain, systemContext,
                ctx.getParentRef(), ctx);
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
        switch (msg.getMsgType()) {
            case COMPONENT_LIFE_CYCLE_MSG:
                onComponentLifecycleMsg((ComponentLifecycleMsg) msg);
                break;
            case QUEUE_TO_RULE_ENGINE_MSG:
                processor.onQueueToRuleEngineMsg((QueueToRuleEngineMsg) msg);
                break;
            case RULE_TO_RULE_CHAIN_TELL_NEXT_MSG:
                processor.onTellNext((RuleNodeToRuleChainTellNextMsg) msg);
                break;
            case RULE_CHAIN_TO_RULE_CHAIN_MSG:
                processor.onRuleChainToRuleChainMsg((RuleChainToRuleChainMsg) msg);
                break;
            case RULE_CHAIN_INPUT_MSG:
                processor.onRuleChainInputMsg((RuleChainInputMsg) msg);
                break;
            case RULE_CHAIN_OUTPUT_MSG:
                processor.onRuleChainOutputMsg((RuleChainOutputMsg) msg);
                break;
            case PARTITION_CHANGE_MSG:
                processor.onPartitionChangeMsg((PartitionChangeMsg) msg);
                break;
            case STATS_PERSIST_TICK_MSG:
                onStatsPersistTick(id);
                break;
            default:
                return false;
        }
        return true;
    }

    /**

     * Factory for creating instances of the enclosing actor type.

     */

    public static class ActorCreator extends ContextBasedCreator {
        private static final long serialVersionUID = 1L;

        private final TenantId tenantId;
        private final RuleChain ruleChain;

        public ActorCreator(ActorSystemContext context, TenantId tenantId, RuleChain ruleChain) {
            super(context);
            this.tenantId = tenantId;
            this.ruleChain = ruleChain;
        }
        
        /**
         * Builds the {@link org.thingsboard.server.actors.TbActorId} used to register the actor.
         *
         * @return {@link TbActorId}
         * @throws Exception if an unexpected error occurs during processing
         */


        @Override
        public TbActorId createActorId() {
            return new TbEntityActorId(ruleChain.getId());
        }
        
        /**
         * Creates a new actor instance for the given actor id and context.
         *
         * @return {@link TbActor}
         * @throws Exception if an unexpected error occurs during processing
         */


        @Override
        public TbActor createActor() {
            return new RuleChainActor(context, tenantId, ruleChain);
        }
    }
    /**
     * Returns rule chain id.
     *
     * @return {@link RuleChainId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected RuleChainId getRuleChainId() {
        return ruleChain.getId();
    }
    /**
     * Returns rule chain name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected String getRuleChainName() {
        return ruleChain.getName();
    }
    /**
     * Returns error persist frequency.
     *
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected long getErrorPersistFrequency() {
        return systemContext.getRuleChainErrorPersistFrequency();
    }

}
