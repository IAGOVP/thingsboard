/**
 * Shared domain model for ThingsBoard: entities, value objects, query DTOs, and enums.
 *
 * <p>Identifiers: {@link org.thingsboard.server.common.data.id.EntityId} and subtypes in {@code id}.
 * Type discriminator: {@link org.thingsboard.server.common.data.EntityType}.
 *
 * <p>No persistence or HTTP here — see {@code common/dao-api} and {@code application}.
 *
 * @see org.thingsboard.server.common.data.Device
 * @see org.thingsboard.server.common.data.Tenant
 */
package org.thingsboard.server.common.data;
