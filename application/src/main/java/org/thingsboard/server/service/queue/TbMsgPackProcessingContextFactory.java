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

import org.springframework.stereotype.Component;
import org.thingsboard.server.service.queue.processing.TbRuleEngineSubmitStrategy;

/**
 * Factory that selects or builds tb msg pack processing context implementations.
 */

public interface TbMsgPackProcessingContextFactory {

    /**
     * Creates create.
     * @param queueName queue name
     * @param submitStrategy submit strategy
     * @param skipTimeouts skip timeouts
     * @return {@link TbMsgPackProcessingContext}
     */

    TbMsgPackProcessingContext create(String queueName, TbRuleEngineSubmitStrategy submitStrategy, boolean skipTimeouts);

    @Component
    class DefaultTbMsgPackProcessingContextFactory implements TbMsgPackProcessingContextFactory {

        /**
         * Creates create.
         * @param queueName queue name
         * @param submitStrategy submit strategy
         * @param skipTimeouts skip timeouts
         * @return {@link TbMsgPackProcessingContext}
         */

        @Override
        public TbMsgPackProcessingContext create(String queueName, TbRuleEngineSubmitStrategy submitStrategy, boolean skipTimeouts) {
            /**
             * Tb msg pack processing context.
             *
             * <p>Default implementation inherited from the supertype.
             * @param queueName queue name
             * @param submitStrategy submit strategy
             * @param skipTimeouts skip timeouts
             * @return return new
             */
            return new TbMsgPackProcessingContext(queueName, submitStrategy, skipTimeouts);
        }

    }

}
