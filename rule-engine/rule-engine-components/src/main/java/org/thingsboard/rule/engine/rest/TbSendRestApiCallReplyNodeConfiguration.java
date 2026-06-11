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
package org.thingsboard.rule.engine.rest;

import lombok.Data;
import org.thingsboard.rule.engine.api.NodeConfiguration;
import org.thingsboard.server.common.data.StringUtils;
/**
 * JSON configuration POJO for {@link TbSendRestApiCallReply} rule node.
 *
 * <p>Deserialized from {@link TbNodeConfiguration} in {@link TbNode#init(TbContext, TbNodeConfiguration)}.
 */


@Data
public class TbSendRestApiCallReplyNodeConfiguration implements NodeConfiguration<TbSendRestApiCallReplyNodeConfiguration> {
    public static final String SERVICE_ID = "serviceId";
    public static final String REQUEST_UUID = "requestUUID";

    private String serviceIdMetaDataAttribute;
    private String requestIdMetaDataAttribute;
    /**
     * Default configuration.
     *
     * @return {@link TbSendRestApiCallReplyNodeConfiguration}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbSendRestApiCallReplyNodeConfiguration defaultConfiguration() {
        TbSendRestApiCallReplyNodeConfiguration configuration = new TbSendRestApiCallReplyNodeConfiguration();
        configuration.setRequestIdMetaDataAttribute(REQUEST_UUID);
        configuration.setServiceIdMetaDataAttribute(SERVICE_ID);
        return configuration;
    }
    /**
     * Returns service id meta data attribute.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getServiceIdMetaDataAttribute() {
        return !StringUtils.isEmpty(serviceIdMetaDataAttribute) ? serviceIdMetaDataAttribute : SERVICE_ID;
    }
    /**
     * Returns request id meta data attribute.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getRequestIdMetaDataAttribute() {
        return !StringUtils.isEmpty(requestIdMetaDataAttribute) ? requestIdMetaDataAttribute : REQUEST_UUID;
    }
}
