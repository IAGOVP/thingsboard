/**
 * ThingsBoard actor system: per-tenant and per-entity mailboxes for concurrent message processing.
 *
 * <p>Actors extend {@link org.thingsboard.server.actors.AbstractTbActor} or
 * {@link org.thingsboard.server.actors.service.ContextAwareActor} and receive
 * {@link org.thingsboard.server.common.msg.TbActorMsg} messages serially per actor instance.
 *
 * <p>Subpackages:
 * <ul>
 *   <li>{@code device} — device sessions, RPC, attribute/telemetry fan-in</li>
 *   <li>{@code ruleChain} — rule chain and rule node execution</li>
 *   <li>{@code calculatedField} — calculated field manager and per-entity evaluation</li>
 *   <li>{@code tenant} — tenant-level actors</li>
 *   <li>{@code service} — actor base classes and shared processors</li>
 * </ul>
 *
 * <p>Context: {@link org.thingsboard.server.actors.ActorSystemContext} exposes DAOs,
 * cluster service, and CF/rule helpers to actors.
 */
package org.thingsboard.server.actors;
