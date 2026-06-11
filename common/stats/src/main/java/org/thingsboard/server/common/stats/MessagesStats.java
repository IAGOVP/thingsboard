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
package org.thingsboard.server.common.stats;

/**
 * Tracks message counts and throughput for transport and queue components.
 */
public interface MessagesStats {
    default void incrementTotal() {
        incrementTotal(1);
    }

    /** Increment total. */
    void incrementTotal(int amount);

    default void incrementSuccessful() {
        incrementSuccessful(1);
    }

    /** Increment successful. */
    void incrementSuccessful(int amount);

    default void incrementFailed() {
        incrementFailed(1);
    }

    /** Increment failed. */
    void incrementFailed(int amount);

    /** Returns the total. */
    int getTotal();

    /** Returns the successful. */
    int getSuccessful();

    /** Returns the failed. */
    int getFailed();

    /** Reset. */
    void reset();
}
