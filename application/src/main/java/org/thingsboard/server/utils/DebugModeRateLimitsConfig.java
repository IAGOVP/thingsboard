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
package org.thingsboard.server.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration holder for debug-mode rate limits on rule chains and calculated fields.
 *
 * <p>Values are injected from {@code actors.rule.chain.debug_mode_rate_limits_per_tenant.*}
 * and {@code actors.calculated_fields.debug_mode_rate_limits_per_tenant.*} properties.
 * Lombok {@code @Data} exposes public getters for each setting.
 */
@Component
@Data
public class DebugModeRateLimitsConfig {

    @Value("${actors.rule.chain.debug_mode_rate_limits_per_tenant.enabled:true}")
    private boolean ruleChainDebugPerTenantLimitsEnabled;
    @Value("${actors.rule.chain.debug_mode_rate_limits_per_tenant.configuration:50000:3600}")
    private String ruleChainDebugPerTenantLimitsConfiguration;

    @Value("${actors.calculated_fields.debug_mode_rate_limits_per_tenant.enabled:true}")
    private boolean calculatedFieldDebugPerTenantLimitsEnabled;
    @Value("${actors.calculated_fields.debug_mode_rate_limits_per_tenant.configuration:50000:3600}")
    private String calculatedFieldDebugPerTenantLimitsConfiguration;

}
