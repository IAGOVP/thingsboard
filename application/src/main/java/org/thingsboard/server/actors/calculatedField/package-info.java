/**
 * Actors for <strong>Calculated Fields</strong> (server-side computed telemetry/attributes).
 *
 * <p>Hierarchy per tenant:
 * <ol>
 *   <li>{@link org.thingsboard.server.actors.calculatedField.CalculatedFieldManagerActor} — one per tenant;
 *       handles cache init, partition changes, lifecycle (create/update/delete CF definitions)</li>
 *   <li>{@link org.thingsboard.server.actors.calculatedField.CalculatedFieldEntityActor} — one per target entity;
 *       evaluates expressions when telemetry, relations, or alarms change</li>
 * </ol>
 *
 * <p>Messages implement {@link org.thingsboard.server.common.msg.ToCalculatedFieldSystemMsg} and are dispatched by
 * {@link org.thingsboard.server.actors.calculatedField.AbstractCalculatedFieldActor#doProcess(org.thingsboard.server.common.msg.TbActorMsg)}.
 *
 * <p>REST management: {@link org.thingsboard.server.controller.CalculatedFieldController} ({@code /api/calculatedFields/...}).
 * Evaluation logic and state: {@code org.thingsboard.server.service.cf}.
 */
package org.thingsboard.server.actors.calculatedField;
