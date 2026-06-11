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
package org.thingsboard.server.config.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ThingsBoard-specific alias for Swagger/OpenAPI {@link Operation} annotations on REST endpoints.
 *
 * <p>Used on controller methods to document API operations in the generated OpenAPI spec.
 * {@link #value} maps to {@code summary}; {@link #notes} maps to {@code description}.
 * Processed by {@link org.thingsboard.server.config.SwaggerConfiguration} and springdoc.
 *
 * <p>Example:
 * <pre>{@code
 * @ApiOperation(value = "Get Device", notes = "Fetch device by ID. Requires TENANT_ADMIN.")
 * @GetMapping("/device/{deviceId}")
 * public Device getDeviceById(@PathVariable String deviceId) { ... }
 * }</pre>
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Operation
public @interface ApiOperation {

    /** Short operation title shown in Swagger UI (alias for {@link Operation#summary}). */
    @AliasFor(annotation = Operation.class, attribute = "summary")
    String value();

    /** Detailed operation description including auth requirements and behavior notes. */
    @AliasFor(annotation = Operation.class, attribute = "description")
    String notes() default "";

    /** When true, hides this operation from the generated OpenAPI document. */
    boolean hidden() default false;

    /** Optional request body schema override for this operation. */
    RequestBody requestBody() default @RequestBody;

    /** Optional per-operation response definitions (status codes, schemas). */
    ApiResponse[] responses() default {};

}
