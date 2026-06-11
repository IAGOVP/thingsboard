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
package org.thingsboard.rule.engine.api.sms;

import org.thingsboard.rule.engine.api.sms.exception.SmsException;


/**

 * sms sender contract (rule engine public API contracts and services).

 */


public interface SmsSender {
    /**
     * Send sms.
     *
     * @param numberTo number to ({@link String})
     * @param message message ({@link String})
     * @return the int result
     * @throws SmsException if sms exception is thrown during processing
     */

    int sendSms(String numberTo, String message) throws SmsException;
    /**
     * Releases resources held by the node (script engines, clients, thread pools).
     *
     */

    void destroy();

}
