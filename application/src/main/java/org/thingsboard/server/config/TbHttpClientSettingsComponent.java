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
package org.thingsboard.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thingsboard.rule.engine.api.TbHttpClientSettings;
import org.thingsboard.server.queue.util.TbRuleEngineComponent;

/**
 * Exposes HTTP client pool settings to the rule engine for external REST call nodes.
 *
 * <p>Rule nodes that invoke external HTTP endpoints (e.g. REST API call, send email
 * via HTTP) use these limits to cap concurrency and connection pool size. A value of
 * {@code 0} for any setting typically means "use library default / unlimited".
 *
 * <p>Configuration keys (under {@code actors.rule.external.http_client}):
 * <ul>
 *   <li>{@code max_parallel_requests} — concurrent in-flight HTTP requests</li>
 *   <li>{@code max_pending_requests} — queued requests waiting for a slot</li>
 *   <li>{@code pool_max_connections} — total connections in the HTTP client pool</li>
 * </ul>
 *
 * <p>Active only when {@code service.type=tb-rule-engine} ({@link TbRuleEngineComponent}).
 */
@TbRuleEngineComponent
@Component
public class TbHttpClientSettingsComponent implements TbHttpClientSettings {

    /** Max concurrent HTTP requests; 0 = default. */
    @Value("${actors.rule.external.http_client.max_parallel_requests:0}")
    private int maxParallelRequests;

    /** Max queued requests awaiting execution; 0 = default. */
    @Value("${actors.rule.external.http_client.max_pending_requests:0}")
    private int maxPendingRequests;

    /** Max connections in the HTTP connection pool; 0 = default. */
    @Value("${actors.rule.external.http_client.pool_max_connections:0}")
    private int poolMaxConnections;

    /** {@inheritDoc} */
    @Override
    public int getMaxParallelRequests() {
        return maxParallelRequests;
    }

    /** {@inheritDoc} */
    @Override
    public int getMaxPendingRequests() {
        return maxPendingRequests;
    }

    /** {@inheritDoc} */
    @Override
    public int getPoolMaxConnections() {
        return poolMaxConnections;
    }

}
