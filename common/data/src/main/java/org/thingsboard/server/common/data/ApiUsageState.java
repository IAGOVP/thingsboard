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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.thingsboard.server.common.data.id.ApiUsageStateId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;

@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
/**
 * Api usage state.
 */
public class ApiUsageState extends BaseData<ApiUsageStateId> implements HasTenantId, HasVersion {

    private static final long serialVersionUID = 8250339805336035966L;

    private TenantId tenantId;
    private EntityId entityId;
    private ApiUsageStateValue transportState;
    private ApiUsageStateValue dbStorageState;
    private ApiUsageStateValue reExecState;
    private ApiUsageStateValue jsExecState;
    private ApiUsageStateValue tbelExecState;
    private ApiUsageStateValue emailExecState;
    private ApiUsageStateValue smsExecState;
    private ApiUsageStateValue alarmExecState;
    private Long version;

    public ApiUsageState() {
        super();
    }

    public ApiUsageState(ApiUsageStateId id) {
        super(id);
    }

    public ApiUsageState(ApiUsageState ur) {
        super(ur);
        this.tenantId = ur.getTenantId();
        this.entityId = ur.getEntityId();
        this.transportState = ur.getTransportState();
        this.dbStorageState = ur.getDbStorageState();
        this.reExecState = ur.getReExecState();
        this.jsExecState = ur.getJsExecState();
        this.tbelExecState = ur.getTbelExecState();
        this.emailExecState = ur.getEmailExecState();
        this.smsExecState = ur.getSmsExecState();
        this.alarmExecState = ur.getAlarmExecState();
        this.version = ur.getVersion();
    }
    /**
     * Is transport enabled.
     *
     * @return the boolean result
     */

    public boolean isTransportEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(transportState);
    }
    /**
     * Is re exec enabled.
     *
     * @return the boolean result
     */

    public boolean isReExecEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(reExecState);
    }
    /**
     * Is db storage enabled.
     *
     * @return the boolean result
     */

    public boolean isDbStorageEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(dbStorageState);
    }
    /**
     * Is js exec enabled.
     *
     * @return the boolean result
     */

    public boolean isJsExecEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(jsExecState);
    }
    /**
     * Is tbel exec enabled.
     *
     * @return the boolean result
     */

    public boolean isTbelExecEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(tbelExecState);
    }
    /**
     * Is email send enabled.
     *
     * @return the boolean result
     */

    public boolean isEmailSendEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(emailExecState);
    }
    /**
     * Is sms send enabled.
     *
     * @return the boolean result
     */

    public boolean isSmsSendEnabled() {
        return !ApiUsageStateValue.DISABLED.equals(smsExecState);
    }
    /**
     * Is alarm creation enabled.
     *
     * @return the boolean result
     */

    public boolean isAlarmCreationEnabled() {
        return alarmExecState != ApiUsageStateValue.DISABLED;
    }

}
