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
package org.thingsboard.server.common.data.cf.configuration.geofencing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.thingsboard.server.common.data.cf.CalculatedFieldType;
import org.thingsboard.server.common.data.cf.configuration.Argument;
import org.thingsboard.server.common.data.cf.configuration.ArgumentsBasedCalculatedFieldConfiguration;
import org.thingsboard.server.common.data.cf.configuration.CalculatedFieldConfiguration;
import org.thingsboard.server.common.data.cf.configuration.HasUseLatestTsConfig;
import org.thingsboard.server.common.data.cf.configuration.Output;
import org.thingsboard.server.common.data.cf.configuration.OutputType;
import org.thingsboard.server.common.data.cf.configuration.ScheduledUpdateSupportedCalculatedFieldConfiguration;
import org.thingsboard.server.common.data.id.EntityId;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Schema
@Data
/**
 * Configuration for geofencing calculated fielduration.
 */
public class GeofencingCalculatedFieldConfiguration implements ArgumentsBasedCalculatedFieldConfiguration, ScheduledUpdateSupportedCalculatedFieldConfiguration, HasUseLatestTsConfig {

    @Valid
    @NotNull
    private EntityCoordinates entityCoordinates;

    @Valid
    @NotNull
    private Map<String, ZoneGroupConfiguration> zoneGroups;

    private boolean scheduledUpdateEnabled;
    private Integer scheduledUpdateInterval;

    @NotNull
    private Output output;
    /**
     * Is use latest ts.
     *
     * @return the boolean result
     */

    @Override
    @JsonIgnore
    public boolean isUseLatestTs() {
        return output.getType() == OutputType.TIME_SERIES;
    }
    /**
     * Returns type.
     *
     * @return {@link CalculatedFieldType}
     */

    @Override
    public CalculatedFieldType getType() {
        return CalculatedFieldType.GEOFENCING;
    }
    /**
     * Returns arguments.
     *
     * @return {@link Map}
     */

    @Override
    @JsonIgnore
    public Map<String, Argument> getArguments() {
        Map<String, Argument> args = new HashMap<>(entityCoordinates.toArguments());
        zoneGroups.forEach((zgName, zgConfig) -> args.put(zgName, zgConfig.toArgument()));
        return args;
    }
    /**
     * Returns referenced entities.
     *
     * @return {@link Set}
     */


    @Override
    public Set<EntityId> getReferencedEntities() {
        return zoneGroups == null ? Collections.emptySet() : zoneGroups.values().stream()
                .map(ZoneGroupConfiguration::getRefEntityId)
                .filter(Objects::nonNull)
                .collect(toSet());
    }
    /**
     * Returns output.
     *
     * @return {@link Output}
     */

    @Override
    public Output getOutput() {
        return output;
    }
    /**
     * Validates the requested data.
     *
     */

    @Override
    public void validate() {
        if (scheduledUpdateEnabled && scheduledUpdateInterval == null) {
            throw new IllegalArgumentException("Refresh interval is required when periodic zone group refresh is enabled.");
        }
        zoneGroups.forEach((key, value) -> value.validate(key));
    }

}
