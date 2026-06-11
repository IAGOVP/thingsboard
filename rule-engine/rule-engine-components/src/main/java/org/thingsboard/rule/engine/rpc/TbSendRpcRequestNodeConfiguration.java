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
package org.thingsboard.rule.engine.rpc;

import lombok.Data;
import org.thingsboard.rule.engine.api.NodeConfiguration;
/**
 * JSON configuration POJO for {@link TbSendRpcRequest} rule node.
 *
 * <p>Deserialized from {@link TbNodeConfiguration} in {@link TbNode#init(TbContext, TbNodeConfiguration)}.
 */


@Data
public class TbSendRpcRequestNodeConfiguration implements NodeConfiguration<TbSendRpcRequestNodeConfiguration> {

    private int timeoutInSeconds;
    /**
     * Default configuration.
     *
     * @return {@link TbSendRpcRequestNodeConfiguration}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbSendRpcRequestNodeConfiguration defaultConfiguration() {
        TbSendRpcRequestNodeConfiguration configuration = new TbSendRpcRequestNodeConfiguration();
        configuration.setTimeoutInSeconds(60);
        return configuration;
    }
}
