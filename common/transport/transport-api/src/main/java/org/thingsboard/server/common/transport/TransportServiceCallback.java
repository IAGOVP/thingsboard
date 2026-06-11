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
package org.thingsboard.server.common.transport;

/**
 * transport service callback contract.
 */
public interface TransportServiceCallback<T> {

    TransportServiceCallback<Void> EMPTY = new TransportServiceCallback<Void>() {
        /**
         * Handles success.
         *
         * @param msg msg ({@link Void})
         * @return nothing
         * @throws Exception on processing failure
         */
        @Override
        public void onSuccess(Void msg) {

        }
        /**
         * Handles error.
         *
         * @param e e ({@link Throwable})
         * @return nothing
         * @throws Exception on processing failure
         */

        @Override
        public void onError(Throwable e) {

        }
    };

    /**
     * Handles success.
     *
     * @param msg msg ({@link T})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onSuccess(T msg);

    /**
     * Handles error.
     *
     * @param e e ({@link Throwable})
     * @return nothing
     * @throws Exception on processing failure
     */
    void onError(Throwable e);

}
