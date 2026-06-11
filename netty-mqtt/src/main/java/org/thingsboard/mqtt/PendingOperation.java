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
package org.thingsboard.mqtt;


/**

 * Cancellable pending MQTT operation (subscribe, publish, or unsubscribe).

 */


public interface PendingOperation {
    
    
    
    /**
     * Is cancelled.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */




    boolean isCancelled();
    /**
     * Handles max retransmission attempts reached.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    void onMaxRetransmissionAttemptsReached();

}
