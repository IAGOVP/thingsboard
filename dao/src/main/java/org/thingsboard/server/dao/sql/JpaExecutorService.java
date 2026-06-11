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
package org.thingsboard.server.dao.sql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.AbstractListeningExecutor;
/**
 * Spring component for jpa executor service (JPA/PostgreSQL persistence layer (JPA repositories and PostgreSQL DAO implementations)).
 */







@Component
public class JpaExecutorService extends AbstractListeningExecutor {

    @Value("${spring.datasource.hikari.maximumPoolSize}")
    private int poolSize;
    /**
     * Returns thread poll size.
     *
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected int getThreadPollSize() {
        return poolSize;
    }

}
