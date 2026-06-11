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

/**

 * has entity limit contract for calculated fields (calculated-field argument resolution, runtime state, and result processing).

 */

public interface HasEntityLimit {
/**
 * Checks entity limit.
 *
 * @param currentEntitiesCount current entities count
 * @param ctx calculated-field execution context
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */



    default void checkEntityLimit(int currentEntitiesCount, CalculatedFieldCtx ctx) {
        if (currentEntitiesCount >= ctx.getMaxRelatedEntitiesPerCfArgument()) {
            
            /**
             * Illegal argument exception.
             *
             * @param configuration." configuration."
             * @return the throw new value
             * @throws Exception if an unexpected error occurs during processing
             */

            throw new IllegalArgumentException(
                    "Exceeded the maximum allowed related entities per argument '"
                    + ctx.getMaxRelatedEntitiesPerCfArgument() + "'. Increase the limit in the tenant profile configuration."
            );
        }
    }

}
