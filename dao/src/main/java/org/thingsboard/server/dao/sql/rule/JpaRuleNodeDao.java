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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.RuleNodeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rule.RuleNode;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.model.sql.RuleNodeEntity;
import org.thingsboard.server.dao.rule.RuleNodeDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * JPA/PostgreSQL implementation of rule node dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Slf4j
@Component
@SqlDao
public class JpaRuleNodeDao extends JpaAbstractDao<RuleNodeEntity, RuleNode> implements RuleNodeDao, TenantEntityDao<RuleNode> {

    @Autowired
    private RuleNodeRepository ruleNodeRepository;
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected Class<RuleNodeEntity> getEntityClass() {
        return RuleNodeEntity.class;
    }
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected JpaRepository<RuleNodeEntity, UUID> getRepository() {
        return ruleNodeRepository;
    }
    /**
     * Finds rule nodes by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link String})
     * @param configurationSearch configuration search ({@link String})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<RuleNode> findRuleNodesByTenantIdAndType(TenantId tenantId, String type, String configurationSearch) {
        return DaoUtil.convertDataList(ruleNodeRepository.findRuleNodesByTenantIdAndType(tenantId.getId(), type, configurationSearch));
    }
    /**
     * Finds all rule nodes by type.
     *
     * @param type type ({@link String})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<RuleNode> findAllRuleNodesByType(String type, PageLink pageLink) {
        return DaoUtil.toPageData(ruleNodeRepository
                .findAllRuleNodesByType(
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds all rule nodes by type and version less than.
     *
     * @param type type ({@link String})
     * @param version version
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<RuleNode> findAllRuleNodesByTypeAndVersionLessThan(String type, int version, PageLink pageLink) {
        return DaoUtil.toPageData(ruleNodeRepository
                .findAllRuleNodesByTypeAndVersionLessThan(
                        type,
                        version,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }
    /**
     * Finds all rule node ids by type and version less than.
     *
     * @param type type ({@link String})
     * @param version version
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<RuleNodeId> findAllRuleNodeIdsByTypeAndVersionLessThan(String type, int version, PageLink pageLink) {
        return DaoUtil.pageToPageData(ruleNodeRepository
                        .findAllRuleNodeIdsByTypeAndVersionLessThan(
                                type,
                                version,
                                DaoUtil.toPageable(pageLink)))
                .mapData(RuleNodeId::new);
    }
    /**
     * Finds all rule node by ids.
     *
     * @param ruleNodeIds rule node ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<RuleNode> findAllRuleNodeByIds(List<RuleNodeId> ruleNodeIds) {
        return DaoUtil.convertDataList(ruleNodeRepository.findAllById(
                ruleNodeIds.stream().map(RuleNodeId::getId).collect(Collectors.toList())));
    }
    /**
     * Finds by external ids.
     *
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @param externalIds external ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public List<RuleNode> findByExternalIds(RuleChainId ruleChainId, List<RuleNodeId> externalIds) {
        return DaoUtil.convertDataList(ruleNodeRepository.findRuleNodesByRuleChainIdAndExternalIdIn(ruleChainId.getId(),
                externalIds.stream().map(RuleNodeId::getId).collect(Collectors.toList())));
    }
    /**
     * Deletes by id in.
     *
     * @param ruleNodeIds rule node ids ({@link List})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void deleteByIdIn(List<RuleNodeId> ruleNodeIds) {
        ruleNodeRepository.deleteAllById(ruleNodeIds.stream().map(RuleNodeId::getId).collect(Collectors.toList()));
    }
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityType getEntityType() {
        return EntityType.RULE_NODE;
    }

}
