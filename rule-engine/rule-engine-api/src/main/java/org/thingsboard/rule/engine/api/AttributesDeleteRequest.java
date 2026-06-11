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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.SettableFuture;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.thingsboard.common.util.NoOpFutureCallback;
import org.thingsboard.server.common.data.AttributeScope;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.msg.TbMsgType;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNullElse;
/**
 * Request DTO for rule engine attributes delete.
 */
/**
 * Async request DTO for rule engine attributes delete (rule engine public API contracts and services).
 */


@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributesDeleteRequest implements CalculatedFieldSystemAwareRequest {

    private final TenantId tenantId;
    private final EntityId entityId;
    private final AttributeScope scope;
    private final List<String> keys;
    private final boolean notifyDevice;
    private final List<CalculatedFieldId> previousCalculatedFieldIds;
    private final UUID tbMsgId;
    private final TbMsgType tbMsgType;
    private final FutureCallback<Void> callback;
    /**
     * Builder.
     *
     * @return {@link Builder}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static Builder builder() {
        return new Builder();
    }

    /**

     * Builder (rule engine public API contracts and services).

     */

    public static class Builder {

        private TenantId tenantId;
        private EntityId entityId;
        private AttributeScope scope;
        private List<String> keys;
        private boolean notifyDevice;
        private List<CalculatedFieldId> previousCalculatedFieldIds;
        private UUID tbMsgId;
        private TbMsgType tbMsgType;
        private FutureCallback<Void> callback;

        Builder() {}

        public Builder tenantId(TenantId tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder entityId(EntityId entityId) {
            this.entityId = entityId;
            return this;
        }
    /**
     * Scope.
     *
     * @param scope scope ({@link AttributeScope})
     * @return {@link Builder}
     * @throws Exception if an unexpected error occurs during processing
     */

        public Builder scope(AttributeScope scope) {
            this.scope = scope;
            return this;
        }
    /**
     * Scope.
     *
     * @param scope scope ({@link String})
     * @return {@link Builder}
     * @throws Exception if an unexpected error occurs during processing
     */

        @Deprecated
        public Builder scope(String scope) {
            try {
                this.scope = AttributeScope.valueOf(scope);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid attribute scope '" + scope + "'");
            }
            return this;
        }
    /**
     * Keys.
     *
     * @param keys keys ({@link List})
     * @return {@link Builder}
     * @throws Exception if an unexpected error occurs during processing
     */

        public Builder keys(List<String> keys) {
            this.keys = keys;
            return this;
        }
    /**
     * Notify device.
     *
     * @param notifyDevice notify device
     * @return {@link Builder}
     * @throws Exception if an unexpected error occurs during processing
     */

        public Builder notifyDevice(boolean notifyDevice) {
            this.notifyDevice = notifyDevice;
            return this;
        }
    /**
     * Previous calculated field ids.
     *
     * @param previousCalculatedFieldIds previous calculated field ids ({@link List})
     * @return {@link Builder}
     * @throws Exception if an unexpected error occurs during processing
     */

        public Builder previousCalculatedFieldIds(List<CalculatedFieldId> previousCalculatedFieldIds) {
            this.previousCalculatedFieldIds = previousCalculatedFieldIds;
            return this;
        }
    /**
     * Tb msg id.
     *
     * @param tbMsgId tb msg id ({@link UUID})
     * @return {@link Builder}
     * @throws Exception if an unexpected error occurs during processing
     */

        public Builder tbMsgId(UUID tbMsgId) {
            this.tbMsgId = tbMsgId;
            return this;
        }
    /**
     * Tb msg type.
     *
     * @param tbMsgType tb msg type ({@link TbMsgType})
     * @return {@link Builder}
     * @throws Exception if an unexpected error occurs during processing
     */

        public Builder tbMsgType(TbMsgType tbMsgType) {
            this.tbMsgType = tbMsgType;
            return this;
        }

        public Builder callback(FutureCallback<Void> callback) {
            this.callback = callback;
            return this;
        }
    /**
     * Future.
     *
     * @param future future ({@link SettableFuture})
     * @return {@link Builder}
     * @throws Exception if an unexpected error occurs during processing
     */

        public Builder future(SettableFuture<Void> future) {
            return callback(new FutureCallback<>() {
                @Override
                public void onSuccess(Void result) {
                    future.set(result);
                }

                @Override
                public void onFailure(Throwable t) {
                    future.setException(t);
                }
            });
        }
    /**
     * Build.
     *
     * @return {@link AttributesDeleteRequest}
     * @throws Exception if an unexpected error occurs during processing
     */

        public AttributesDeleteRequest build() {
            return new AttributesDeleteRequest(
                    tenantId, entityId, scope, keys, notifyDevice, previousCalculatedFieldIds, tbMsgId, tbMsgType, requireNonNullElse(callback, NoOpFutureCallback.instance())
            );
        }

    }

}
