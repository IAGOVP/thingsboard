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
package org.thingsboard.server.common.data.cf.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import org.thingsboard.server.common.data.AttributeScope;

import java.util.Objects;

@Schema(
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "TIME_SERIES", schema = TimeSeriesOutput.class),
                @DiscriminatorMapping(value = "ATTRIBUTES", schema = AttributesOutput.class)
        }
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TimeSeriesOutput.class, name = "TIME_SERIES"),
        @JsonSubTypes.Type(value = AttributesOutput.class, name = "ATTRIBUTES")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * output contract.
 */
public interface Output {
    /**
     * Returns type.
     *
     * @return {@link OutputType}
     */

    @JsonIgnore
    OutputType getType();
/**
 * Returns name.
 *
 * @return {@link String}
 */

    String getName();
/**
 * Returns strategy.
 *
 * @return {@link OutputStrategy}
 */

    OutputStrategy getStrategy();
/**
 * Returns scope.
 *
 * @return {@link AttributeScope}
 */

    default AttributeScope getScope() {
        return null;
    }
/**
 * Returns decimals by default.
 *
 * @return {@link Integer}
 */

    Integer getDecimalsByDefault();
/**
 * Set decimals by default.
 *
 * @param decimalsByDefault decimals by default ({@link Integer})
 */

    void setDecimalsByDefault(Integer decimalsByDefault);
/**
 * Has context only changes.
 *
 * @param other other ({@link Output})
 * @return the boolean result
 */

    default boolean hasContextOnlyChanges(Output other) {
        if (!getType().equals(other.getType())) {
            return true;
        }
        if (!Objects.equals(getName(), other.getName())) {
            return true;
        }
        if (getScope() != (other.getScope())) {
            return true;
        }
        if (!Objects.equals(getDecimalsByDefault(), other.getDecimalsByDefault())) {
            return true;
        }
        if (getStrategy().hasContextOnlyChanges(other.getStrategy())) {
            return true;
        }
        return false;
    }

}
