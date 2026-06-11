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

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.msg.MsgType;
import org.thingsboard.server.common.msg.TbMsg;

/**
 * Entry message delivering a {@link org.thingsboard.server.common.msg.TbMsg} into a rule chain actor.
 */

@EqualsAndHashCode(callSuper = true)
@ToString
public final class RuleChainInputMsg extends TbToRuleChainActorMsg {

    public RuleChainInputMsg(RuleChainId target, TbMsg tbMsg) {
        super(tbMsg, target);
    }
    
    /**
     * Returns the {@link org.thingsboard.server.common.msg.MsgType} discriminator for this message.
     *
     * @return {@link MsgType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public MsgType getMsgType() {
        return MsgType.RULE_CHAIN_INPUT_MSG;
    }
}
