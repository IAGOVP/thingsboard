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
package org.thingsboard.server.controller;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.service.sync.vc.EntitiesVersionControlService;

import java.util.UUID;

/**
 * Base controller providing version-control auto-commit support for entity save operations.
 *
 * <p>Not a standalone REST controller; extended by entity controllers that trigger
 * automatic Git commits after entity changes when auto-commit is configured.
 *
 * <p>Related service: {@link EntitiesVersionControlService}.
 */
public class AutoCommitController extends BaseController {

    @Autowired
    private EntitiesVersionControlService vcService;

    /**
     * Triggers an asynchronous auto-commit for the given entity if version control is enabled.
     *
     * @param user the user performing the entity change
     * @param entityId the entity that was created or updated
     * @return listenable future with the commit UUID, or a failed future if auto-commit is not supported
     * @throws Exception if the version control service fails
     */
    protected ListenableFuture<UUID> autoCommit(User user, EntityId entityId) throws Exception {
        if (vcService != null) {
            return vcService.autoCommit(user, entityId);
        } else {
            // We do not support auto-commit for rule engine
            return Futures.immediateFailedFuture(new RuntimeException("Operation not supported!"));
        }
    }


}
