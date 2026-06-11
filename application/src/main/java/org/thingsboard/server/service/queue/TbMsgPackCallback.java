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
package org.thingsboard.server.service.queue;

import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.ExceptionUtil;
import org.thingsboard.server.common.data.exception.AbstractRateLimitException;
import org.thingsboard.server.common.data.id.RuleNodeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.RuleEngineException;
import org.thingsboard.server.common.msg.queue.RuleNodeInfo;
import org.thingsboard.server.common.msg.queue.TbMsgCallback;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
/**
 * Async callback invoked when tb msg pack completes.
 */

@Slf4j
public class TbMsgPackCallback implements TbMsgCallback {
    private final UUID id;
    private final TenantId tenantId;
    private final TbMsgPackProcessingContext ctx;
    private final long startMsgProcessing;
    private final Timer successfulMsgTimer;
    private final Timer failedMsgTimer;

    /**
     * Constructs {@link TbMsgPackCallback} with the supplied dependencies and configuration.
     * @param id id
     * @param tenantId tenant that owns the subscription or entity
     * @param ctx ctx
     */

    public TbMsgPackCallback(UUID id, TenantId tenantId, TbMsgPackProcessingContext ctx) {
        this(id, tenantId, ctx, null, null);
    }

    /**
     * Constructs {@link TbMsgPackCallback} with the supplied dependencies and configuration.
     * @param id id
     * @param tenantId tenant that owns the subscription or entity
     * @param ctx ctx
     * @param successfulMsgTimer successful msg timer
     * @param failedMsgTimer failed msg timer
     */

    public TbMsgPackCallback(UUID id, TenantId tenantId, TbMsgPackProcessingContext ctx, Timer successfulMsgTimer, Timer failedMsgTimer) {
        this.id = id;
        this.tenantId = tenantId;
        this.ctx = ctx;
        this.successfulMsgTimer = successfulMsgTimer;
        this.failedMsgTimer = failedMsgTimer;
        startMsgProcessing = System.currentTimeMillis();
    }

    /**
     * Invoked when success occurs.
     * @return @Override
    public void
     */

    @Override
    public void onSuccess() {
        log.trace("[{}] ON SUCCESS", id);
        if (successfulMsgTimer != null) {
            successfulMsgTimer.record(System.currentTimeMillis() - startMsgProcessing, TimeUnit.MILLISECONDS);
        }
        ctx.onSuccess(id);
    }

    /**
     * Invoked when rate limit occurs.
     * @param e e
     * @return @Override
    public void
     */

    @Override
    public void onRateLimit(RuleEngineException e) {
        log.debug("[{}] ON RATE LIMIT", id, e);
        //TODO notify tenant on rate limit
        if (failedMsgTimer != null) {
            failedMsgTimer.record(System.currentTimeMillis() - startMsgProcessing, TimeUnit.MILLISECONDS);
        }
        ctx.onSuccess(id);
    }
    
    /**
    
     * Invoked when failure occurs.
    
     * @param e e
    
     * @return @Override
    public void
    
     */
    
    @Override
    public void onFailure(RuleEngineException e) {
        if (ExceptionUtil.lookupExceptionInCause(e, AbstractRateLimitException.class) != null) {
            onRateLimit(e);
            return;
        }

        log.trace("[{}] ON FAILURE", id, e);
        if (failedMsgTimer != null) {
            failedMsgTimer.record(System.currentTimeMillis() - startMsgProcessing, TimeUnit.MILLISECONDS);
        }
        ctx.onFailure(tenantId, id, e);
    }

    /**
     * Is msg valid.
     * @return {@code true} when the condition holds
     */

    @Override
    public boolean isMsgValid() {
        return !ctx.isCanceled();
    }

    /**
     * Invoked when processing start occurs.
     *
     * <p>Default implementation inherited from the supertype.
     * @param ruleNodeInfo rule node info
     * @return @Override
    public void
     */

    @Override
    public void onProcessingStart(RuleNodeInfo ruleNodeInfo) {
        log.trace("[{}] ON PROCESSING START: {}", id, ruleNodeInfo);
        ctx.onProcessingStart(id, ruleNodeInfo);
    }

    /**
     * Invoked when processing end occurs.
     *
     * <p>Default implementation inherited from the supertype.
     * @param ruleNodeId rule node identifier
     * @return @Override
    public void
     */

    @Override
    public void onProcessingEnd(RuleNodeId ruleNodeId) {
        log.trace("[{}] ON PROCESSING END: {}", id, ruleNodeId);
        ctx.onProcessingEnd(id, ruleNodeId);
    }
}
