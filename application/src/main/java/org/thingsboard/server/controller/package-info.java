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
 * <p>Controllers documented in this package (representative index):
 * <ul>
 *   <li>{@link org.thingsboard.server.controller.TrendzController} — {@code /api/trendz} Trendz integration settings</li>
 *   <li>{@link org.thingsboard.server.controller.TwoFactorAuthController} — {@code /api/auth/2fa} login-time 2FA verification</li>
 *   <li>{@link org.thingsboard.server.controller.TwoFactorAuthConfigController} — {@code /api/2fa} account and platform 2FA config</li>
 *   <li>{@link org.thingsboard.server.controller.UiSettingsController} — {@code /api/uiSettings} UI server settings</li>
 *   <li>{@link org.thingsboard.server.controller.UsageInfoController} — {@code /api/usage} tenant license usage</li>
 *   <li>{@link org.thingsboard.server.controller.UserController} — {@code /api/user}, {@code /api/users} user lifecycle and preferences</li>
 *   <li>{@link org.thingsboard.server.controller.WidgetTypeController} — {@code /api/widgetType*} widget type CRUD and queries</li>
 *   <li>{@link org.thingsboard.server.controller.WidgetsBundleController} — {@code /api/widgetsBundle*} widget bundle CRUD</li>
 * </ul>
 *
 * <p>WebSocket real-time API: {@link org.thingsboard.server.controller.plugin.TbWebSocketHandler}
 * on {@code /api/ws/**} (see {@link org.thingsboard.server.config.WebSocketConfiguration}).
 *
 * <p>Full controller index: {@code application/REST_API_CONTROLLERS.md}.
 * Method-level documentation is provided via {@code @ApiOperation} (OpenAPI/Swagger) and JavaDoc.
 */
package org.thingsboard.server.controller;
