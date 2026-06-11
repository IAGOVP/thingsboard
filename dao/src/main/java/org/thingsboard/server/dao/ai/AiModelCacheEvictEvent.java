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
package org.thingsboard.server.dao.ai;

import org.thingsboard.server.common.data.ai.AiModel;

import static java.util.Objects.requireNonNull;
import static org.thingsboard.server.dao.ai.AiModelCacheEvictEvent.Deleted;
import static org.thingsboard.server.dao.ai.AiModelCacheEvictEvent.Saved;


/**

 * Cache eviction event published when ai model cache data changes.

 *

 * <p>Consumed by cache listeners to invalidate stale entries cluster-wide.

 */


sealed interface AiModelCacheEvictEvent permits Saved, Deleted {
    /**
     * Cache key.
     *
     * @return {@link AiModelCacheKey}
     * @throws Exception if an unexpected error occurs during processing
     */

    AiModelCacheKey cacheKey();

    
    


    
    




    
    






    
    








    
    










    
    /**
     * Saved.
     *
     * @param cacheKey cache key ({@link AiModelCacheKey})
     * @param savedModel saved model ({@link AiModel})
     * @return the record value
     * @throws Exception if an unexpected error occurs during processing
     */












    record Saved(AiModelCacheKey cacheKey, AiModel savedModel) implements AiModelCacheEvictEvent {

        public Saved {
            requireNonNull(cacheKey);
            requireNonNull(savedModel);
        }

    }

    
    


    
    




    
    






    
    








    
    










    
    /**
     * Deleted.
     *
     * @param cacheKey cache key ({@link AiModelCacheKey})
     * @return the record value
     * @throws Exception if an unexpected error occurs during processing
     */












    record Deleted(AiModelCacheKey cacheKey) implements AiModelCacheEvictEvent {

        public Deleted {
            requireNonNull(cacheKey);
        }

    }

}
