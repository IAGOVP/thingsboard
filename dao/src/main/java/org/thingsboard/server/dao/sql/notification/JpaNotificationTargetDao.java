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
package org.thingsboard.server.dao.sql.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.notification.targets.NotificationTarget;
import org.thingsboard.server.common.data.notification.targets.platform.UsersFilterType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.NotificationTargetEntity;
import org.thingsboard.server.dao.notification.NotificationTargetDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * JPA/PostgreSQL implementation of notification target dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Component
@SqlDao
@RequiredArgsConstructor
public class JpaNotificationTargetDao extends JpaAbstractDao<NotificationTargetEntity, NotificationTarget> implements NotificationTargetDao {

    private final NotificationTargetRepository notificationTargetRepository;
    /**
     * Finds by tenant id and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<NotificationTarget> findByTenantIdAndPageLink(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(notificationTargetRepository.findByTenantIdAndSearchText(tenantId.getId(),
                pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds by tenant id and supported notification type and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param notificationType notification type ({@link NotificationType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<NotificationTarget> findByTenantIdAndSupportedNotificationTypeAndPageLink(TenantId tenantId, NotificationType notificationType, PageLink pageLink) {
        return DaoUtil.toPageData(notificationTargetRepository.findByTenantIdAndSearchTextAndUsersFilterTypeIfPresent(tenantId.getId(),
                pageLink.getTextSearch(),
                Arrays.stream(UsersFilterType.values())
                        .filter(type -> notificationType != NotificationType.GENERAL || !type.isForRules())
                        .map(Enum::name).collect(Collectors.toList()),
                DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ids ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<NotificationTarget> findByTenantIdAndIds(TenantId tenantId, List<NotificationTargetId> ids) {
        return DaoUtil.convertDataList(notificationTargetRepository.findByTenantIdAndIdIn(tenantId.getId(), DaoUtil.toUUIDs(ids)));
    }
    /**
     * Finds by tenant id and users filter type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param filterType filter type ({@link UsersFilterType})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<NotificationTarget> findByTenantIdAndUsersFilterType(TenantId tenantId, UsersFilterType filterType) {
        return DaoUtil.convertDataList(notificationTargetRepository.findByTenantIdAndUsersFilterType(tenantId.getId(),
                List.of(filterType.name()), DaoUtil.toPageable(new PageLink(Integer.MAX_VALUE))).getContent());
    }
    /**
     * Removes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void removeByTenantId(TenantId tenantId) {
        notificationTargetRepository.deleteByTenantId(tenantId.getId());
    }
    /**
     * Counts by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Long}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Long countByTenantId(TenantId tenantId) {
        return notificationTargetRepository.countByTenantId(tenantId.getId());
    }
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link NotificationTarget}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationTarget findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(notificationTargetRepository.findByTenantIdAndExternalId(tenantId, externalId));
    }
    /**
     * Finds by tenant id and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param name entity or attribute name
     * @return {@link NotificationTarget}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationTarget findByTenantIdAndName(UUID tenantId, String name) {
        return DaoUtil.getData(notificationTargetRepository.findByTenantIdAndName(tenantId, name));
    }
    /**
     * Finds by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<NotificationTarget> findByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(notificationTargetRepository.findByTenantId(tenantId, DaoUtil.toPageable(pageLink)));
    }
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link NotificationTargetId})
     * @return {@link NotificationTargetId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationTargetId getExternalIdByInternal(NotificationTargetId internalId) {
        return DaoUtil.toEntityId(notificationTargetRepository.getExternalIdByInternal(internalId.getId()), NotificationTargetId::new);
    }
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<NotificationTarget> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findByTenantId(tenantId.getId(), pageLink);
    }
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<NotificationTargetEntity> getEntityClass() {
        return NotificationTargetEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<NotificationTargetEntity, UUID> getRepository() {
        return notificationTargetRepository;
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION_TARGET;
    }

}
