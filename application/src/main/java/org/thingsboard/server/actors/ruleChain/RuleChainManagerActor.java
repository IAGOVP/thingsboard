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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.actors.ActorSystemContext;
import org.thingsboard.server.actors.TbActorRef;
import org.thingsboard.server.actors.TbEntityActorId;
import org.thingsboard.server.actors.TbEntityTypeActorIdPredicate;
import org.thingsboard.server.actors.service.ContextAwareActor;
import org.thingsboard.server.actors.service.DefaultActorService;
import org.thingsboard.server.actors.shared.RuleChainErrorActor;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageDataIterable;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.common.msg.TbActorMsg;
import org.thingsboard.server.common.msg.queue.RuleEngineException;
import org.thingsboard.server.dao.rule.RuleChainService;

import java.util.function.Function;

/**
 * Abstract tenant-scoped actor that owns rule-chain and rule-node child actors.
 *
 * <p>Creates and stops {@link org.thingsboard.server.actors.ruleChain.RuleChainActor} instances on lifecycle events and routes {@link org.thingsboard.server.common.msg.queue.QueueToRuleEngineMsg} to the target chain.
 */

@Slf4j
public abstract class RuleChainManagerActor extends ContextAwareActor {

    protected final TenantId tenantId;
    private final RuleChainService ruleChainService;
    @Getter
    protected RuleChain rootChain;
    @Getter
    protected TbActorRef rootChainActor;

    protected boolean ruleChainsInitialized;

    public RuleChainManagerActor(ActorSystemContext systemContext, TenantId tenantId) {
        super(systemContext);
        this.tenantId = tenantId;
        this.ruleChainService = systemContext.getRuleChainService();
    }
    /**
     * Init rule chains.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void initRuleChains() {
        log.debug("[{}] Initializing rule chains", tenantId);
        for (RuleChain ruleChain : new PageDataIterable<>(link -> ruleChainService.findTenantRuleChainsByType(tenantId, RuleChainType.CORE, link), ContextAwareActor.ENTITY_PACK_LIMIT)) {
            RuleChainId ruleChainId = ruleChain.getId();
            log.debug("[{}|{}] Creating rule chain actor", ruleChainId.getEntityType(), ruleChain.getId());
            TbActorRef actorRef = getOrCreateActor(ruleChainId, id -> ruleChain);
            visit(ruleChain, actorRef);
            log.debug("[{}|{}] Rule Chain actor created.", ruleChainId.getEntityType(), ruleChainId.getId());
        }
        ruleChainsInitialized = true;
    }
    /**
     * Destroy rule chains.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void destroyRuleChains() {
        log.debug("[{}] Destroying rule chains", tenantId);
        for (RuleChain ruleChain : new PageDataIterable<>(link -> ruleChainService.findTenantRuleChainsByType(tenantId, RuleChainType.CORE, link), ContextAwareActor.ENTITY_PACK_LIMIT)) {
            ctx.stop(new TbEntityActorId(ruleChain.getId()));
        }
        ruleChainsInitialized = false;
    }
    /**
     * Visit.
     *
     * @param entity entity ({@link RuleChain})
     * @param actorRef actor ref ({@link TbActorRef})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void visit(RuleChain entity, TbActorRef actorRef) {
        if (entity != null && entity.isRoot() && entity.getType().equals(RuleChainType.CORE)) {
            rootChain = entity;
            rootChainActor = actorRef;
        }
    }
    /**
     * Returns or create actor.
     *
     * @param ruleChainId target rule chain identifier
     * @return {@link TbActorRef}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected TbActorRef getOrCreateActor(RuleChainId ruleChainId) {
        return getOrCreateActor(ruleChainId, eId -> ruleChainService.findRuleChainById(TenantId.SYS_TENANT_ID, eId));
    }
    /**
     * Returns or create actor.
     *
     * @param ruleChainId target rule chain identifier
     * @param provider provider ({@link Function})
     * @return {@link TbActorRef}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected TbActorRef getOrCreateActor(RuleChainId ruleChainId, Function<RuleChainId, RuleChain> provider) {
        return ctx.getOrCreateChildActor(new TbEntityActorId(ruleChainId),
                () -> DefaultActorService.RULE_DISPATCHER_NAME,
                () -> {
                    RuleChain ruleChain = provider.apply(ruleChainId);
                    if (ruleChain == null) {
                        return new RuleChainErrorActor.ActorCreator(systemContext, tenantId, ruleChainId,
                                new RuleEngineException("Rule Chain with id: " + ruleChainId + " not found!"));
                    } else {
                        return new RuleChainActor.ActorCreator(systemContext, tenantId, ruleChain);
                    }
                },
                () -> true);
    }
    /**
     * Returns entity actor ref.
     *
     * @param entityId target entity identifier
     * @return {@link TbActorRef}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected TbActorRef getEntityActorRef(EntityId entityId) {
        TbActorRef target = null;
        if (entityId.getEntityType() == EntityType.RULE_CHAIN) {
            target = getOrCreateActor((RuleChainId) entityId);
        }
        return target;
    }
    /**
     * Broadcast.
     *
     * @param msg actor message to process
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void broadcast(TbActorMsg msg) {
        ctx.broadcastToChildren(msg, new TbEntityTypeActorIdPredicate(EntityType.RULE_CHAIN));
    }
}
