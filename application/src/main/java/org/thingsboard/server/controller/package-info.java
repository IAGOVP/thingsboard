/**
 * REST API layer for the ThingsBoard server.
 *
 * <p>Controllers are Spring {@code @RestController} classes. Most extend
 * {@link org.thingsboard.server.controller.BaseController} for authentication,
 * tenant scoping, and consistent error responses ({@code ThingsboardException}).
 *
 * <p>Common URL prefixes:
 * <ul>
 *   <li>{@code /api} — majority of tenant/admin CRUD APIs</li>
 *   <li>{@link org.thingsboard.server.controller.TbUrlConstants#TELEMETRY_URL_PREFIX} — device telemetry</li>
 *   <li>{@link org.thingsboard.server.controller.TbUrlConstants#RPC_V1_URL_PREFIX} / {@link org.thingsboard.server.controller.TbUrlConstants#RPC_V2_URL_PREFIX} — device RPC</li>
 *   <li>{@link org.thingsboard.server.controller.TbUrlConstants#RULE_ENGINE_URL_PREFIX} — rule engine injection</li>
 * </ul>
 *
 * <p>Controller index: {@code application/REST_API_CONTROLLERS.md}.
 * Method-level documentation is provided via {@code @ApiOperation} (OpenAPI/Swagger).
 */
package org.thingsboard.server.controller;
