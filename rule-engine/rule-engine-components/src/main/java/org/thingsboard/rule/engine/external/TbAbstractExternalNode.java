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
package org.thingsboard.rule.engine.external;

import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNode;
import org.thingsboard.server.common.data.msg.TbNodeConnectionType;
import org.thingsboard.server.common.msg.TbMsg;


/**

 * Abstract base class for external node rule nodes (ThingsBoard rule engine).

 */


public abstract class TbAbstractExternalNode implements TbNode {

    protected boolean forceAck;
    /**
     * Initializes the rule node: parses configuration and prepares resources (script engine, HTTP client, etc.).
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @throws TbNodeException if configuration or processing fails
     */

    public void init(TbContext ctx) {
        this.forceAck = ctx.isExternalNodeForceAck();
    }
    /**
     * Routes the message to the Success connection of the current node.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param tbMsg rule engine message being processed
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void tellSuccess(TbContext ctx, TbMsg tbMsg) {
        if (forceAck) {
            ctx.enqueueForTellNext(tbMsg.copyWithNewCtx().build(), TbNodeConnectionType.SUCCESS);
        } else {
            ctx.tellSuccess(tbMsg);
        }
    }
    /**
     * Routes the message to the Failure connection with an error message.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param tbMsg rule engine message being processed
     * @param t t ({@link Throwable})
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void tellFailure(TbContext ctx, TbMsg tbMsg, Throwable t) {
        if (forceAck) {
            if (t == null) {
                ctx.enqueueForTellNext(tbMsg.copyWithNewCtx().build(), TbNodeConnectionType.FAILURE);
            } else {
                ctx.enqueueForTellFailure(tbMsg.copyWithNewCtx().build(), t);
            }
        } else {
            if (t == null) {
                ctx.tellNext(tbMsg, TbNodeConnectionType.FAILURE);
            } else {
                ctx.tellFailure(tbMsg, t);
            }
        }
    }
    /**
     * Ack if needed.
     *
     * @param ctx rule engine execution context (routing, DAO, cluster APIs)
     * @param msg incoming or outgoing rule engine message
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected TbMsg ackIfNeeded(TbContext ctx, TbMsg msg) {
        if (forceAck) {
            ctx.ack(msg);
            return msg.copyWithNewCtx().build();
        } else {
            return msg;
        }
    }

}
