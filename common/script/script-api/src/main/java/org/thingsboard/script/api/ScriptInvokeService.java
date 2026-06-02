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
package org.thingsboard.script.api;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.script.ScriptLanguage;

import java.util.UUID;

/**
 * Compiles and runs user scripts (JS or TBEL) for rule nodes and calculated fields.
 *
 * <p>Lifecycle: {@link #eval} registers script → {@link #invokeScript} executes → {@link #release} frees resources.
 */
public interface ScriptInvokeService {

    /** Compiles script body and returns a script instance id. */
    ListenableFuture<UUID> eval(TenantId tenantId, ScriptType scriptType, String scriptBody, String... argNames);

    /** Runs a previously compiled script with positional arguments. */
    ListenableFuture<Object> invokeScript(TenantId tenantId, CustomerId customerId, UUID scriptId, Object... args);

    /** Releases script engine resources for the given script id. */
    ListenableFuture<Void> release(UUID scriptId);

    ScriptLanguage getLanguage();

}
