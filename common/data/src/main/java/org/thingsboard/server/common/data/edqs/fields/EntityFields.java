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
package org.thingsboard.server.common.data.edqs.fields;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thingsboard.server.common.data.id.EntityId;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Lightweight entity field selection for EDQS queries and events (EDQS data — lightweight entity field DTOs for EDQS).
 */

public interface EntityFields {

    Logger log = LoggerFactory.getLogger(EntityFields.class);
 /**
  * Returns id.
  *
  * @return {@link UUID}
  * @throws Exception if an unexpected error occurs during processing
  */


    UUID getId();
  /**
   * Returns tenant id.
   *
   * @return {@link UUID}
   * @throws Exception if an unexpected error occurs during processing
   */


    default UUID getTenantId() {
        return null;
    }
  /**
   * Returns customer id.
   *
   * @return {@link UUID}
   * @throws Exception if an unexpected error occurs during processing
   */


    default UUID getCustomerId() {
        return null;
    }
        /**
         * Returns assigned customer ids.
         *
         * @return {@link List}
         * @throws Exception if an unexpected error occurs during processing
         */


    default List<UUID> getAssignedCustomerIds() {
        return Collections.emptyList();
    }
  /**
   * Returns created time.
   *
   * @return the long result
   * @throws Exception if an unexpected error occurs during processing
   */


    default long getCreatedTime() {
        return 0;
    }
   /**
    * Returns name.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getName() {
        return "";
    }
   /**
    * Returns type.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getType() {
        return "";
    }
   /**
    * Returns label.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getLabel() {
        return "";
    }
    /**
     * Returns additional info.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */


    default String getAdditionalInfo() {
        return "";
    }
   /**
    * Returns email.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getEmail() {
        return "";
    }
   /**
    * Returns country.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getCountry() {
        return "";
    }
   /**
    * Returns state.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getState() {
        return "";
    }
   /**
    * Returns city.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getCity() {
        return "";
    }
   /**
    * Returns address.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getAddress() {
        return "";
    }
   /**
    * Returns address2.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getAddress2() {
        return "";
    }
   /**
    * Returns zip.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getZip() {
        return "";
    }
   /**
    * Returns phone.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getPhone() {
        return "";
    }
   /**
    * Returns region.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getRegion() {
        return "";
    }
    /**
     * Returns first name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */


    default String getFirstName() {
        return "";
    }
    /**
     * Returns last name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */


    default String getLastName() {
        return "";
    }
    /**
     * Is edge template.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */


    default boolean isEdgeTemplate() {
        return false;
    }
   /**
    * Returns configuration.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getConfiguration() {
        return "";
    }
   /**
    * Returns schedule.
    *
    * @return {@link String}
    * @throws Exception if an unexpected error occurs during processing
    */


    default String getSchedule() {
        return "";
    }
  /**
   * Returns originator id.
   *
   * @return {@link EntityId}
   * @throws Exception if an unexpected error occurs during processing
   */


    default EntityId getOriginatorId() {
        return null;
    }
    /**
     * Returns queue name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */


    default String getQueueName() {
        return "";
    }
    /**
     * Returns service id.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */


    default String getServiceId() {
        return "";
    }
        /**
         * Is default.
         *
         * @return the boolean result
         * @throws Exception if an unexpected error occurs during processing
         */


    default boolean isDefault() {
        return false;
    }
  /**
   * Returns owner id.
   *
   * @return {@link UUID}
   * @throws Exception if an unexpected error occurs during processing
   */


    default UUID getOwnerId() {
        return null;
    }
 /**
  * Returns version.
  *
  * @return {@link Long}
  * @throws Exception if an unexpected error occurs during processing
  */


    default Long getVersion() {
        return null;
    }
            /**
             * Returns as string.
             *
             * @param key key ({@link String})
             * @return {@link String}
             * @throws Exception if an unexpected error occurs during processing
             */


    default String getAsString(String key) {
        return switch (key) {
            case "id" -> getId().toString();
            case "createdTime" -> Long.toString(getCreatedTime());
            case "title" -> getName();
            case "type" -> getType();
            case "label" -> getLabel();
            case "additionalInfo" -> getAdditionalInfo();
            case "email" -> getEmail();
            case "country" -> getCountry();
            case "state" -> getState();
            case "city" -> getCity();
            case "address" -> getAddress();
            case "address2" -> getAddress2();
            case "zip" -> getZip();
            case "phone" -> getPhone();
            case "region" -> getRegion();
            case "firstName" -> getFirstName();
            case "lastName" -> getLastName();
            case "edgeTemplate" -> Boolean.toString(isEdgeTemplate());
            case "configuration" -> getConfiguration();
            case "schedule" -> getSchedule();
            case "originatorId" -> getOriginatorId().getId().toString();
            case "originatorType" -> getOriginatorId().getEntityType().toString();
            case "queueName" -> getQueueName();
            case "serviceId" -> getServiceId();
            default -> {
                log.warn("Unknown field '{}'", key);
                yield null;
            }
        };
    }

}
