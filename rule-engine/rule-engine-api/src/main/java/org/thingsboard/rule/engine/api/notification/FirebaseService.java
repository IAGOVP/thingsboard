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
package org.thingsboard.rule.engine.api.notification;

import org.thingsboard.server.common.data.id.TenantId;

import java.util.Map;


/**

 * Rule engine service facade for firebase (rule engine public API contracts and services).

 */


public interface FirebaseService {
    /**
     * Send message.
     *
     * @param tenantId tenant UUID
     * @param credentials credentials ({@link String})
     * @param fcmToken fcm token ({@link String})
     * @param title title ({@link String})
     * @param body body ({@link String})
     * @param data data ({@link Map})
     * @param badge badge ({@link Integer})
     * @throws Exception if an unexpected error occurs during processing
     */

    void sendMessage(TenantId tenantId, String credentials, String fcmToken, String title, String body, Map<String, String> data, Integer badge) throws Exception;

}
