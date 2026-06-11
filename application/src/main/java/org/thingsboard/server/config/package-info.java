/**
 * Spring Boot configuration for the ThingsBoard application module.
 *
 * <h2>Package contents</h2>
 * <ul>
 *   <li>{@link org.thingsboard.server.config.ThingsboardSecurityConfiguration} — JWT, API key, OAuth2, CORS</li>
 *   <li>{@link org.thingsboard.server.config.TbRuleEngineSecurityConfiguration} — rule-engine microservice security</li>
 *   <li>{@link org.thingsboard.server.config.WebSocketConfiguration} — real-time WebSocket API</li>
 *   <li>{@link org.thingsboard.server.config.SwaggerConfiguration} — OpenAPI 3.1 / Swagger UI</li>
 *   <li>{@link org.thingsboard.server.config.WebConfig} — SPA deep-link forwarding</li>
 *   <li>{@link org.thingsboard.server.config.RateLimitProcessingFilter} — REST rate limiting</li>
 *   <li>{@link org.thingsboard.server.config.SchedulingConfiguration} — {@code @Scheduled} thread pool</li>
 *   <li>{@link org.thingsboard.server.config.mqtt} — MQTT client settings for rule engine</li>
 *   <li>{@link org.thingsboard.server.config.annotations.ApiOperation} — Swagger operation annotation</li>
 * </ul>
 *
 * <p>Beans are conditionally loaded based on {@code service.type} (monolith, tb-core, tb-rule-engine).
 */
package org.thingsboard.server.config;
