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
package org.thingsboard.rule.engine.profile;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.thingsboard.server.common.data.kv.DataType;
/**
 * Entity key value (device profile state nodes).
 */


@EqualsAndHashCode
@Deprecated
class EntityKeyValue {

    @Getter
    private DataType dataType;
    private Long lngValue;
    private Double dblValue;
    private Boolean boolValue;
    private String strValue;
    /**
     * Returns lng value.
     *
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    public Long getLngValue() {
        return dataType == DataType.LONG ? lngValue : null;
    }
    /**
     * Set lng value.
     *
     * @param lngValue lng value ({@link Long})
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setLngValue(Long lngValue) {
        this.dataType = DataType.LONG;
        this.lngValue = lngValue;
    }
    /**
     * Returns dbl value.
     *
     * @return {@link Double}
     * @throws Exception if an unexpected error occurs during processing
     */

    public Double getDblValue() {
        return dataType == DataType.DOUBLE ? dblValue : null;
    }
    /**
     * Set dbl value.
     *
     * @param dblValue dbl value ({@link Double})
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setDblValue(Double dblValue) {
        this.dataType = DataType.DOUBLE;
        this.dblValue = dblValue;
    }
    /**
     * Returns bool value.
     *
     * @return {@link Boolean}
     * @throws Exception if an unexpected error occurs during processing
     */

    public Boolean getBoolValue() {
        return dataType == DataType.BOOLEAN ? boolValue : null;
    }
    /**
     * Set bool value.
     *
     * @param boolValue bool value ({@link Boolean})
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setBoolValue(Boolean boolValue) {
        this.dataType = DataType.BOOLEAN;
        this.boolValue = boolValue;
    }
    /**
     * Returns str value.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getStrValue() {
        return dataType == DataType.STRING ? strValue : null;
    }
    /**
     * Set str value.
     *
     * @param strValue str value ({@link String})
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setStrValue(String strValue) {
        this.dataType = DataType.STRING;
        this.strValue = strValue;
    }
    /**
     * Set json value.
     *
     * @param jsonValue json value ({@link String})
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setJsonValue(String jsonValue) {
        this.dataType = DataType.JSON;
        this.strValue = jsonValue;
    }
    /**
     * Returns json value.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getJsonValue() {
        return dataType == DataType.JSON ? strValue : null;
    }

    boolean isSet() {
        return dataType != null;
    }

    static EntityKeyValue fromString(String s) {
        EntityKeyValue result = new EntityKeyValue();
        result.setStrValue(s);
        return result;
    }

    static EntityKeyValue fromBool(boolean b) {
        EntityKeyValue result = new EntityKeyValue();
        result.setBoolValue(b);
        return result;
    }

    static EntityKeyValue fromLong(long l) {
        EntityKeyValue result = new EntityKeyValue();
        result.setLngValue(l);
        return result;
    }

    static EntityKeyValue fromDouble(double d) {
        EntityKeyValue result = new EntityKeyValue();
        result.setDblValue(d);
        return result;
    }

    static EntityKeyValue fromJson(String s) {
        EntityKeyValue result = new EntityKeyValue();
        result.setJsonValue(s);
        return result;
    }

}
