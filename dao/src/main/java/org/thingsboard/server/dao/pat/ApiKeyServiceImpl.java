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
package org.thingsboard.server.dao.pat;

import com.google.common.util.concurrent.FluentFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.exception.DataValidationException;
import org.thingsboard.server.common.data.id.ApiKeyId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.pat.ApiKey;
import org.thingsboard.server.common.data.pat.ApiKeyInfo;
import org.thingsboard.server.dao.entity.AbstractCachedEntityService;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.dao.service.validator.ApiKeyDataValidator;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.user.UserServiceImpl.INCORRECT_TENANT_ID;
import static org.thingsboard.server.dao.user.UserServiceImpl.INCORRECT_USER_ID;
/**
 * Spring {@code @Service} implementing the api key DAO API.
 *
 * <p>Delegates to {@code *Dao} implementations and manages cache eviction (personal access tokens (API keys)).
 */


@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl extends AbstractCachedEntityService<ApiKeyCacheKey, ApiKey, ApiKeyEvictEvent> implements ApiKeyService {

    private static final String INCORRECT_API_KEY_ID = "Incorrect ApiKeyId ";
    private static final int MAX_API_KEY_VALUE_LENGTH = 255;

    private final ApiKeyDao apiKeyDao;
    private final ApiKeyInfoDao apiKeyInfoDao;
    @Lazy
    private final ApiKeyDataValidator apiKeyValidator;

    @Value("${security.api_key.value_prefix:}")
    private String prefix;

    @Value("${security.api_key.value_bytes_size:64}")
    private int valueBytesSize;

    
    /**
     * Handles evict event.
     *
     * @param event event ({@link ApiKeyEvictEvent})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    @TransactionalEventListener
    public void handleEvictEvent(ApiKeyEvictEvent event) {
        cache.evict(ApiKeyCacheKey.of(event.value()));
    }

    
    /**
     * Saves or persists api key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param apiKeyInfo api key info ({@link ApiKeyInfo})
     * @return {@link ApiKey}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ApiKey saveApiKey(TenantId tenantId, ApiKeyInfo apiKeyInfo) {
        return saveApiKey(tenantId, apiKeyInfo, null, true);
    }

    
    /**
     * Saves or persists api key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param apiKeyInfo api key info ({@link ApiKeyInfo})
     * @param value value ({@link String})
     * @param doValidate do validate
     * @return {@link ApiKey}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ApiKey saveApiKey(TenantId tenantId, ApiKeyInfo apiKeyInfo, String value, boolean doValidate) {
        log.trace("Executing saveApiKey [{}]", apiKeyInfo);
        try {
            var apiKey = new ApiKey(apiKeyInfo);
            ApiKey old = doValidate ? apiKeyValidator.validate(apiKey, ApiKeyInfo::getTenantId) :
                    (apiKey.getId() != null ? apiKeyDao.findById(tenantId, apiKey.getUuidId()) : null);
            if (value != null) {
                apiKey.setValue(value);
            } else if (old == null) {
                apiKey.setValue(generateApiKeySecret());
            } else {
                apiKey.setValue(old.getValue());
            }
            var savedApiKey = apiKeyDao.save(tenantId, apiKey);
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(tenantId).entityId(savedApiKey.getId()).entity(savedApiKey).created(apiKey.getId() == null).build());
            if (old != null && old.isEnabled() != apiKey.isEnabled()) {
                publishEvictEvent(new ApiKeyEvictEvent(apiKey.getValue()));
            }
            return savedApiKey;
        } catch (Exception e) {
            checkConstraintViolation(e, "api_key_value_unq_key", "API Key with such value already exists!");
            throw e;
        }
    }

    
    /**
     * Finds api key by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param apiKeyId api key id ({@link ApiKeyId})
     * @return {@link ApiKey}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ApiKey findApiKeyById(TenantId tenantId, ApiKeyId apiKeyId) {
        log.trace("Executing findApiKeyById [{}] [{}]", tenantId, apiKeyId);
        validateId(apiKeyId, id -> INCORRECT_API_KEY_ID + id);
        return apiKeyDao.findById(tenantId, apiKeyId.getId());
    }

    
    /**
     * Finds api keys by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<ApiKeyInfo> findApiKeysByUserId(TenantId tenantId, UserId userId, PageLink pageLink) {
        log.trace("Executing findApiKeysByUserId [{}][{}]", tenantId, userId);
        validateId(userId, id -> INCORRECT_USER_ID + id);
        return apiKeyInfoDao.findByUserId(tenantId, userId, pageLink);
    }

    
    /**
     * Finds api keys by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<ApiKey> findApiKeysByUserId(TenantId tenantId, UserId userId) {
        log.trace("Executing findApiKeysByUserId [{}][{}]", tenantId, userId);
        validateId(userId, id -> INCORRECT_USER_ID + id);
        return apiKeyDao.findByTenantIdAndUserId(tenantId, userId);
    }

    
    /**
     * Finds entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return optional {@link HasId}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findApiKeyById(tenantId, new ApiKeyId(entityId.getId())));
    }

    
    /**
     * Finds entity async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link FluentFuture}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public FluentFuture<Optional<HasId<?>>> findEntityAsync(TenantId tenantId, EntityId entityId) {
        return FluentFuture.from(apiKeyDao.findByIdAsync(tenantId, entityId.getId()))
                .transform(Optional::ofNullable, directExecutor());
    }

    
    /**
     * Deletes api key.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param apiKey api key ({@link ApiKey})
     * @param force force
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteApiKey(TenantId tenantId, ApiKey apiKey, boolean force) {
        UUID apiKeyId = apiKey.getUuidId();
        validateId(apiKeyId, id -> INCORRECT_API_KEY_ID + id);
        apiKeyDao.removeById(tenantId, apiKeyId);
        publishEvictEvent(new ApiKeyEvictEvent(apiKey.getValue()));
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(apiKey.getId()).build());
    }

    
    /**
     * Deletes entity.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @param force force
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteEntity(TenantId tenantId, EntityId id, boolean force) {
        ApiKey apiKey = findApiKeyById(tenantId, new ApiKeyId(id.getId()));
        if (apiKey == null) {
            if (force) {
                return;
            } else {
                throw new DataValidationException("Unable to delete non-existent API key.");
            }
        }
        deleteApiKey(tenantId, apiKey, force);
    }

    
    /**
     * Deletes by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteByTenantId(TenantId tenantId) {
        log.trace("Executing deleteApiKeysByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        Set<String> values = apiKeyDao.deleteByTenantId(tenantId);
        values.forEach(value -> publishEvictEvent(new ApiKeyEvictEvent(value)));
    }

    
    /**
     * Deletes by user id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param userId target user identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void deleteByUserId(TenantId tenantId, UserId userId) {
        log.trace("Executing deleteApiKeysByUserId, tenantId [{}]", tenantId);
        validateId(userId, id -> INCORRECT_USER_ID + id);
        Set<String> values = apiKeyDao.deleteByUserId(tenantId, userId);
        values.forEach(value -> publishEvictEvent(new ApiKeyEvictEvent(value)));
    }

    
    /**
     * Finds api keys by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<ApiKey> findApiKeysByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findApiKeysByTenantId [{}]", tenantId);
        validateId(tenantId, id -> INCORRECT_TENANT_ID + id);
        return apiKeyDao.findByTenantId(tenantId, pageLink);
    }

    
    /**
     * Finds api key by value.
     *
     * @param value value ({@link String})
     * @return {@link ApiKey}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ApiKey findApiKeyByValue(String value) {
        log.trace("Executing findApiKeyByValue [{}]", value);
        var cacheKey = ApiKeyCacheKey.of(value);
        return cache.getAndPutInTransaction(cacheKey, () -> apiKeyDao.findByValue(value), true);
    }

    private String generateApiKeySecret() {
        return prefix + StringUtils.generateSafeToken(Math.min(valueBytesSize, MAX_API_KEY_VALUE_LENGTH));
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
