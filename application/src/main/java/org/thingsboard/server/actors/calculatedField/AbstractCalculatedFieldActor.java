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
package org.thingsboard.server.actors.calculatedField;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.DebugModeUtil;
import org.thingsboard.server.actors.ActorSystemContext;
import org.thingsboard.server.actors.service.ContextAwareActor;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.TbActorMsg;
import org.thingsboard.server.common.msg.ToCalculatedFieldSystemMsg;

/**
 * Base actor for calculated-field processing at tenant or entity scope.
 *
 * <p>Handles only {@link ToCalculatedFieldSystemMsg} subtypes; other {@link TbActorMsg} types return {@code false}
 * from {@link #doProcess(TbActorMsg)} so the actor framework can warn about unhandled mail.
 *
 * <p>On failure: {@link CalculatedFieldException} may persist debug events when debug mode is enabled;
 * all failures invoke {@link org.thingsboard.server.common.msg.queue.TbCallback#onFailure(Throwable)}.
 *
 * @see CalculatedFieldManagerActor
 * @see CalculatedFieldEntityActor
 */
@Slf4j
public abstract class AbstractCalculatedFieldActor extends ContextAwareActor {

    /** Tenant that owns this actor instance (manager or entity actor). */
    protected final TenantId tenantId;

    public AbstractCalculatedFieldActor(ActorSystemContext systemContext, TenantId tenantId) {
        super(systemContext);
        this.tenantId = tenantId;
    }

    /**
     * Dispatches calculated-field system messages; delegates to {@link #doProcessCfMsg(ToCalculatedFieldSystemMsg)}.
     */
    @Override
    protected boolean doProcess(TbActorMsg msg) {
        if (msg instanceof ToCalculatedFieldSystemMsg cfm) {
            Exception cause;
            try {
                return doProcessCfMsg(cfm);
            } catch (CalculatedFieldException cfe) {
                if (DebugModeUtil.isDebugFailuresAvailable(cfe.getCtx().getCalculatedField())) {
                    systemContext.persistCalculatedFieldDebugError(cfe);
                }
                cause = cfe.getCause();
            } catch (Exception e) {
                logProcessingException(e);
                cause = e;
            }
            cfm.getCallback().onFailure(cause);
            return true;
        } else {
            return false;
        }
    }

    /** Subclass-specific logging when an unexpected exception escapes evaluation. */
    abstract void logProcessingException(Exception e);

    /**
     * Process one calculated-field message. Return {@code true} if the message was consumed.
     *
     * @throws CalculatedFieldException when evaluation fails in a controlled way (debug + callback)
     */
    abstract boolean doProcessCfMsg(ToCalculatedFieldSystemMsg msg) throws CalculatedFieldException;

}
