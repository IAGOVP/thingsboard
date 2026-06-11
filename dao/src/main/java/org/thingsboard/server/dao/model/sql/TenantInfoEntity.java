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
package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.TenantInfo;

import java.util.HashMap;
import java.util.Map;
/**
 * JPA/Cassandra row model for tenant info.
 *
 * <p>Maps database columns to domain objects via {@code toData()} conversion.
 */


@Data
@EqualsAndHashCode(callSuper = true)
public class TenantInfoEntity extends AbstractTenantEntity<TenantInfo> {

    public static final Map<String,String> tenantInfoColumnMap = new HashMap<>();
    static {
        tenantInfoColumnMap.put("tenantProfileName", "p.name");
    }

    private String tenantProfileName;

    public TenantInfoEntity() {
        super();
    }

    public TenantInfoEntity(TenantEntity tenantEntity, String tenantProfileName) {
        super(tenantEntity);
        this.tenantProfileName = tenantProfileName;
    }
    /**
     * To data.
     *
     * @return {@link TenantInfo}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TenantInfo toData() {
        return new TenantInfo(super.toTenant(), this.tenantProfileName);
    }
}
