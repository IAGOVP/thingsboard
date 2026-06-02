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
package org.thingsboard.server.controller;

/**
 * Shared URL path prefixes for REST controllers that are not mounted on plain {@code /api}.
 *
 * @see TelemetryController
 * @see RpcV1Controller
 * @see RpcV2Controller
 * @see RuleEngineController
 */
public class TbUrlConstants {

    /** Device and server telemetry/attributes plugin API ({@link TelemetryController}). */
    public static final String TELEMETRY_URL_PREFIX = "/api/plugins/telemetry";

    /** Legacy device RPC API ({@link RpcV1Controller}). */
    public static final String RPC_V1_URL_PREFIX = "/api/plugins/rpc";

    /** Device RPC API v2 ({@link RpcV2Controller}). */
    public static final String RPC_V2_URL_PREFIX = "/api/rpc";

    /** HTTP entry to push payloads into the rule engine ({@link RuleEngineController}). */
    public static final String RULE_ENGINE_URL_PREFIX = "/api/rule-engine/";

    private TbUrlConstants() {
    }
}
