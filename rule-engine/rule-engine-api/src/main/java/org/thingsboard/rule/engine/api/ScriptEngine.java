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
package org.thingsboard.rule.engine.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.msg.TbMsg;

import java.util.List;
import java.util.Set;

/**

 * TBEL/JS script execution for filter/transform nodes.

 */


/**

 * TBEL/JavaScript script engine used by filter and transform nodes.

 */


public interface ScriptEngine {
    /**
     * Executes update async.
     *
     * @param msg incoming or outgoing rule engine message
     * @return future completing with {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<List<TbMsg>> executeUpdateAsync(TbMsg msg);
    /**
     * Executes generate async.
     *
     * @param prevMsg prev msg ({@link TbMsg})
     * @return future completing with {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<TbMsg> executeGenerateAsync(TbMsg prevMsg);
    /**
     * Executes filter async.
     *
     * @param msg incoming or outgoing rule engine message
     * @return future completing with {@link Boolean}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Boolean> executeFilterAsync(TbMsg msg);
    /**
     * Executes switch async.
     *
     * @param msg incoming or outgoing rule engine message
     * @return future completing with {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<Set<String>> executeSwitchAsync(TbMsg msg);
    /**
     * Executes json async.
     *
     * @param msg incoming or outgoing rule engine message
     * @return future completing with {@link JsonNode}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<JsonNode> executeJsonAsync(TbMsg msg);
    /**
     * Executes to string async.
     *
     * @param msg incoming or outgoing rule engine message
     * @return future completing with {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListenableFuture<String> executeToStringAsync(TbMsg msg);
    /**
     * Releases resources held by the node (script engines, clients, thread pools).
     *
     */

    void destroy();

}
