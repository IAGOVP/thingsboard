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
package org.thingsboard.server.service.security.permission;

import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Contract for permission checker in role-based access control (RBAC).
 *
 * <p><b>Responsibilities:</b> Evaluates tenant/customer/system-admin scopes against Resource and Operation.
 */

public interface PermissionChecker<I extends EntityId, T extends HasTenantId> {

    default boolean hasPermission(SecurityUser user, Operation operation) {
        return false;
    }

    default boolean hasPermission(SecurityUser user, Operation operation, I entityId, T entity) {
        return false;
    }

    public class GenericPermissionChecker<I extends EntityId, T extends HasTenantId> implements PermissionChecker<I,T> {

        private final Set<Operation> allowedOperations;

        public GenericPermissionChecker(Operation... operations) {
            allowedOperations = new HashSet<Operation>(Arrays.asList(operations));
        }
        /**
         * Returns whether the user is allowed to perform the operation.
         *
         * @param user user (SecurityUser)
         * @param operation operation (Operation)
         * @return boolean
         */
        @Override
        public boolean hasPermission(SecurityUser user, Operation operation) {
            return allowedOperations.contains(Operation.ALL) || allowedOperations.contains(operation);
        }
        /**
         * Returns whether the user is allowed to perform the operation.
         *
         * @param user user (SecurityUser)
         * @param operation operation (Operation)
         * @param entityId entity id (I)
         * @param entity entity (T)
         * @return boolean
         */
        @Override
        public boolean hasPermission(SecurityUser user, Operation operation, I entityId, T entity) {
            return allowedOperations.contains(Operation.ALL) || allowedOperations.contains(operation);
        }
    }

    public static PermissionChecker denyAllPermissionChecker = new PermissionChecker() {};

    public static PermissionChecker allowAllPermissionChecker = new PermissionChecker<EntityId, HasTenantId>() {
        /**
         * Returns whether the user is allowed to perform the operation.
         *
         * @param user user (SecurityUser)
         * @param operation operation (Operation)
         * @return boolean
         */
        @Override
        public boolean hasPermission(SecurityUser user, Operation operation) {
            return true;
        }
        /**
         * Returns whether the user is allowed to perform the operation.
         *
         * @param user user (SecurityUser)
         * @param operation operation (Operation)
         * @param entityId entity id (EntityId)
         * @param entity entity (HasTenantId)
         * @return boolean
         */
        @Override
        public boolean hasPermission(SecurityUser user, Operation operation, EntityId entityId, HasTenantId entity) {
            return true;
        }
    };


}
