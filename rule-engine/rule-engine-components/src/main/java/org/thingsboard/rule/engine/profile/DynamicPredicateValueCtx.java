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
package org.thingsboard.rule.engine.profile;
/**
 * dynamic predicate value ctx contract (device profile state nodes).
 */


@Deprecated
public interface DynamicPredicateValueCtx {
    /**
     * Returns tenant value.
     *
     * @param key key ({@link String})
     * @return {@link EntityKeyValue}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityKeyValue getTenantValue(String key);
    /**
     * Returns customer value.
     *
     * @param key key ({@link String})
     * @return {@link EntityKeyValue}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityKeyValue getCustomerValue(String key);
    /**
     * Reset customer.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    void resetCustomer();
}
