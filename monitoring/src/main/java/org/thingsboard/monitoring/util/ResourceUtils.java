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
package org.thingsboard.monitoring.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import org.thingsboard.common.util.JacksonUtil;

import java.io.InputStream;


/**

 * Loads classpath resources (LwM2M models, rule chain JSON) for monitoring entity setup.

 */


public class ResourceUtils {
    /**
     * Returns resource.
     *
     * @param path path ({@link String})
     * @param type type ({@link Class})
     * @return {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    @SneakyThrows
    public static <T> T getResource(String path, Class<T> type) {
        InputStream resource = getResourceStream(path);
        return JacksonUtil.OBJECT_MAPPER.readValue(resource, type);
    }
    /**
     * Returns resource.
     *
     * @param path path ({@link String})
     * @return {@link JsonNode}
     * @throws Exception if an unexpected error occurs during processing
     */

    @SneakyThrows
    public static JsonNode getResource(String path) {
        InputStream resource = getResourceStream(path);
        return JacksonUtil.OBJECT_MAPPER.readTree(resource);
    }
    /**
     * Returns resource as stream.
     *
     * @param path path ({@link String})
     * @return {@link InputStream}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static InputStream getResourceAsStream(String path) {
        return getResourceStream(path);
    }

    private static InputStream getResourceStream(String path) {
        InputStream resource = ResourceUtils.class.getClassLoader().getResourceAsStream(path);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found for path " + path);
        }
        return resource;
    }

}
