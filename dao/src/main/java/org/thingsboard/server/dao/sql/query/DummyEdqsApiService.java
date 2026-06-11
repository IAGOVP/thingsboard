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
package org.thingsboard.server.dao.sql.query;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.edqs.query.EdqsRequest;
import org.thingsboard.server.common.data.edqs.query.EdqsResponse;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.edqs.EdqsApiService;
/**
 * Spring component for dummy edqs api service (JPA/PostgreSQL persistence layer (JPA repositories and PostgreSQL DAO implementations)).
 */







@Service
@Slf4j
@ConditionalOnMissingBean(value = EdqsApiService.class, ignored = DummyEdqsApiService.class)
public class DummyEdqsApiService implements EdqsApiService {
    /**
     * Processes request.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param customerId target customer identifier
     * @param request request payload with operation parameters
     * @return future completing with {@link EdqsResponse}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ListenableFuture<EdqsResponse> processRequest(TenantId tenantId, CustomerId customerId, EdqsRequest request) {
        throw new UnsupportedOperationException();
    }
    /**
     * Is supported.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean isSupported() {
        return false;
    }

}
