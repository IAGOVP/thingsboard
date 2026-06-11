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
package org.thingsboard.server.dao.sql.rule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edqs.fields.RuleChainFields;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.RuleChainEntity;
import org.thingsboard.server.dao.rule.RuleChainDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of rule chain dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Slf4j
@Component
@SqlDao
public class JpaRuleChainDao extends JpaAbstractDao<RuleChainEntity, RuleChain> implements RuleChainDao {

    @Autowired
    private RuleChainRepository ruleChainRepository;
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<RuleChainEntity> getEntityClass() {
        return RuleChainEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<RuleChainEntity, UUID> getRepository() {
        return ruleChainRepository;
    }
    /**
     * Finds rule chains by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<RuleChain> findRuleChainsByTenantId(UUID tenantId, PageLink pageLink) {
        log.debug("Try to find rule chains by tenantId [{}] and pageLink [{}]", tenantId, pageLink);
        return DaoUtil.toPageData(ruleChainRepository
                .findByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds rule chains by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link RuleChainType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<RuleChain> findRuleChainsByTenantIdAndType(UUID tenantId, RuleChainType type, PageLink pageLink) {
        log.debug("Try to find rule chains by tenantId [{}], type [{}] and pageLink [{}]", tenantId, type, pageLink);
        return DaoUtil.toPageData(ruleChainRepository
                .findByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds root rule chain by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link RuleChainType})
     * @return {@link RuleChain}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleChain findRootRuleChainByTenantIdAndType(UUID tenantId, RuleChainType type) {
        log.debug("Try to find root rule chain by tenantId [{}] and type [{}]", tenantId, type);
        return DaoUtil.getData(ruleChainRepository.findByTenantIdAndTypeAndRootIsTrue(tenantId, type));
    }
    /**
     * Finds rule chains by tenant id and edge id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param edgeId edge id ({@link UUID})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<RuleChain> findRuleChainsByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink) {
        log.debug("Try to find rule chains by tenantId [{}], edgeId [{}] and pageLink [{}]", tenantId, edgeId, pageLink);
        return DaoUtil.toPageData(ruleChainRepository
                .findByTenantIdAndEdgeId(
                        tenantId,
                        edgeId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds auto assign to edge rule chains by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<RuleChain> findAutoAssignToEdgeRuleChainsByTenantId(UUID tenantId, PageLink pageLink) {
        log.debug("Try to find auto assign to edge rule chains by tenantId [{}]", tenantId);
        return DaoUtil.toPageData(ruleChainRepository
                .findAutoAssignByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds by tenant id and type and name.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link RuleChainType})
     * @param name entity or attribute name
     * @return {@link Collection}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public Collection<RuleChain> findByTenantIdAndTypeAndName(TenantId tenantId, RuleChainType type, String name) {
        return DaoUtil.convertDataList(ruleChainRepository.findByTenantIdAndTypeAndName(tenantId.getId(), type, name));
    }
    /**
     * Finds rule chains by tenant id and ids.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param ruleChainIds rule chain ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<RuleChain> findRuleChainsByTenantIdAndIds(UUID tenantId, List<UUID> ruleChainIds) {
        return DaoUtil.convertDataList(ruleChainRepository.findRuleChainsByTenantIdAndIdIn(tenantId, ruleChainIds));
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
        return ruleChainRepository.countByTenantId(tenantId.getId());
    }
    /**
     * Finds by tenant id and external id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param externalId external id ({@link UUID})
     * @return {@link RuleChain}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleChain findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return DaoUtil.getData(ruleChainRepository.findByTenantIdAndExternalId(tenantId, externalId));
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
    public PageData<RuleChain> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findRuleChainsByTenantId(tenantId, pageLink);
    }
    /**
     * Returns external id by internal.
     *
     * @param internalId internal id ({@link RuleChainId})
     * @return {@link RuleChainId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleChainId getExternalIdByInternal(RuleChainId internalId) {
        return Optional.ofNullable(ruleChainRepository.getExternalIdById(internalId.getId()))
                .map(RuleChainId::new).orElse(null);
    }
    /**
     * Finds default entity by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link RuleChain}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleChain findDefaultEntityByTenantId(UUID tenantId) {
        return findRootRuleChainByTenantIdAndType(tenantId, RuleChainType.CORE);
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
    public PageData<RuleChain> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return findRuleChainsByTenantId(tenantId.getId(), pageLink);
    }
    /**
     * Finds by tenant id and resource.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param reference reference ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<EntityInfo> findByTenantIdAndResource(TenantId tenantId, String reference, int limit) {
        return ruleChainRepository.findRuleChainsByTenantIdAndResource(tenantId.getId(), reference, PageRequest.of(0, limit));
    }
    /**
     * Finds by resource.
     *
     * @param reference reference ({@link String})
     * @param limit maximum number of records to return
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<EntityInfo> findByResource(String reference, int limit) {
        return ruleChainRepository.findRuleChainsByResource(reference, PageRequest.of(0, limit));
    }
    /**
     * Finds next batch.
     *
     * @param id entity UUID primary key
     * @param batchSize batch size
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<RuleChainFields> findNextBatch(UUID id, int batchSize) {
        return ruleChainRepository.findNextBatch(id, Limit.of(batchSize));
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.RULE_CHAIN;
    }

}
