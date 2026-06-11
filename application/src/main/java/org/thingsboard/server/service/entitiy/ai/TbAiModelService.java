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
package org.thingsboard.server.service.entitiy.ai;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.ai.AiModel;

/**

 * Application-layer service API for ai model entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbAiModelService {
/**
 * Saves or persists the requested data.
 *
 * @param model model ({@link AiModel})
 * @param user authenticated user performing the action
 * @return {@link AiModel}
 * @throws Exception if an unexpected error occurs during processing
 */



    AiModel save(AiModel model, User user);
/**
 * Deletes the requested data.
 *
 * @param model model ({@link AiModel})
 * @param user authenticated user performing the action
 * @return the boolean result
 * @throws Exception if an unexpected error occurs during processing
 */

    boolean delete(AiModel model, User user);

}
