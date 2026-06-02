/**
 * Server-side business logic and infrastructure services.
 *
 * <p>Notable subpackages:
 * <ul>
 *   <li>{@code entitiy} — TB entity services (device, dashboard, calculated field, …)</li>
 *   <li>{@code security} — authentication, JWT, OAuth2, permissions</li>
 *   <li>{@code queue} — Kafka consumers (rule engine, calculated field, core)</li>
 *   <li>{@code subscription} / {@code ws} — real-time WebSocket subscriptions</li>
 *   <li>{@code cf} — calculated field evaluation and persistent state</li>
 *   <li>{@code edge} — Edge synchronization RPC</li>
 *   <li>{@code sync} — import/export and version control</li>
 *   <li>{@code install} — schema install and upgrade</li>
 * </ul>
 *
 * <p>Controllers should delegate to these services rather than calling DAOs directly.
 */
package org.thingsboard.server.service;
