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
package org.thingsboard.server.common.data;

/**
 * Service API for fst stats persistence and domain operations.
 */
public interface FstStatsService {

    void incrementEncode(Class<?> clazz);
/**
 * Increment decode.
 *
 * @param clazz clazz ({@link Class})
 */

    void incrementDecode(Class<?> clazz);
/**
 * Record encode time.
 *
 * @param clazz clazz ({@link Class})
 * @param startTime start time
 */

    void recordEncodeTime(Class<?> clazz, long startTime);
/**
 * Record decode time.
 *
 * @param clazz clazz ({@link Class})
 * @param startTime start time
 */

    void recordDecodeTime(Class<?> clazz, long startTime);

}
