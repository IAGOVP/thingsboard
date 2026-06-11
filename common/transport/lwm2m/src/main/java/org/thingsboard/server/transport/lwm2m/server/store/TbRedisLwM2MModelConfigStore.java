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
package org.thingsboard.server.transport.lwm2m.server.store;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.transport.lwm2m.server.model.LwM2MModelConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Tb redis lw m2mmodel config store.
 */
@Slf4j
@AllArgsConstructor
public class TbRedisLwM2MModelConfigStore implements TbLwM2MModelConfigStore {
    private static final String MODEL_EP = "MODEL#EP#";
    private final RedisConnectionFactory connectionFactory;
    /**
     * Returns all.
     *
     * @return {@link List}
     * @throws Exception on processing failure
     */

    @Override
    public List<LwM2MModelConfig> getAll() {
        try (var scanConnection = connectionFactory.getConnection();
             var getConnection = connectionFactory.getConnection()) {
            List<LwM2MModelConfig> configs = new ArrayList<>();
            ScanOptions scanOptions = ScanOptions.scanOptions().count(100).match(MODEL_EP + "*").build();
            List<Cursor<byte[]>> scans = new ArrayList<>();
            if (scanConnection instanceof RedisClusterConnection clusterConnection) {
                clusterConnection.clusterGetNodes().forEach(node ->
                        scans.add(clusterConnection.scan(node, scanOptions)));
            } else {
                scans.add(scanConnection.scan(scanOptions));
            }

            scans.forEach(scan -> {
                scan.forEachRemaining(key -> {
                    byte[] element = getConnection.get(key);
                    if (element != null) {
                        configs.add(JacksonUtil.fromBytes(element, LwM2MModelConfig.class));
                    }
                });
            });
            return configs;
        }
    }
    /**
     * Put.
     *
     * @param modelConfig model config ({@link LwM2MModelConfig})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void put(LwM2MModelConfig modelConfig) {
        byte[] clientSerialized = JacksonUtil.writeValueAsBytes(modelConfig);
        try (var connection = connectionFactory.getConnection()) {
            connection.getSet(getKey(modelConfig.getEndpoint()), clientSerialized);
        }
    }
    /**
     * Removes the requested data.
     *
     * @param endpoint endpoint ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void remove(String endpoint) {
        try (var connection = connectionFactory.getConnection()) {
            connection.del(getKey(endpoint));
        }
    }

    private byte[] getKey(String endpoint) {
        return (MODEL_EP + endpoint).getBytes();
    }

}
