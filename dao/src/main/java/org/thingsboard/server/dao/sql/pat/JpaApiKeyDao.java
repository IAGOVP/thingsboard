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
package org.thingsboard.server.dao.sql.pat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.pat.ApiKey;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.ApiKeyEntity;
import org.thingsboard.server.dao.pat.ApiKeyDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Set;
import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of api key dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Slf4j
@SqlDao
@Component
public class JpaApiKeyDao extends JpaAbstractDao<ApiKeyEntity, ApiKey> implements ApiKeyDao {

    @Autowired
    private ApiKeyRepository apiKeyRepository;
    /**
     * Finds by value.
     *
     * @param value value ({@link String})
     * @return {@link ApiKey}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ApiKey findByValue(String value) {
        return DaoUtil.getData(apiKeyRepository.findByValue(value));
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
    public PageData<ApiKey> findByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(apiKeyRepository.findByTenantId(tenantId.getId(), DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds by tenant id and user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<ApiKey> findByTenantIdAndUserId(TenantId tenantId, UserId userId) {
        return DaoUtil.convertDataList(apiKeyRepository.findByTenantIdAndUserId(tenantId.getId(), userId.getId()));
    }
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Set<String> deleteByTenantId(TenantId tenantId) {
        return apiKeyRepository.deleteByTenantId(tenantId.getId());
    }
    /**
     * Deletes by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return {@link Set}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Set<String> deleteByUserId(TenantId tenantId, UserId userId) {
        return apiKeyRepository.deleteByUserId(tenantId.getId(), userId.getId());
    }
    /**
     * Deletes all by expiration time before.
     *
     * @param ts ts
     * @return the int result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public int deleteAllByExpirationTimeBefore(long ts) {
        return apiKeyRepository.deleteAllByExpirationTimeBefore(ts);
    }
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<ApiKeyEntity> getEntityClass() {
        return ApiKeyEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<ApiKeyEntity, UUID> getRepository() {
        return apiKeyRepository;
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.API_KEY;
    }

}
