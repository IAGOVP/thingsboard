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
package org.thingsboard.rule.engine.api;

import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sms.config.TestSmsRequest;


/**

 * Facade for sending SMS from rule engine SMS nodes.

 */


public interface SmsService {
    /**
     * Updates sms configuration.
     *
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void updateSmsConfiguration();
    /**
     * Send sms.
     *
     * @param tenantId tenant UUID
     * @param customerId customer id ({@link CustomerId})
     * @param numbersTo numbers to
     * @param message message ({@link String})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendSms(TenantId tenantId, CustomerId customerId, String[] numbersTo, String message) throws ThingsboardException;;
    /**
     * Send test sms.
     *
     * @param testSmsRequest test sms request ({@link TestSmsRequest})
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */

    void sendTestSms(TestSmsRequest testSmsRequest) throws ThingsboardException;
    /**
     * Is configured.
     *
     * @param tenantId tenant UUID
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean isConfigured(TenantId tenantId);

}
