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
package org.thingsboard.server.dao.sql.cf;

import com.google.common.base.Strings;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.cf.CalculatedField;
import org.thingsboard.server.common.data.cf.CalculatedFieldFilter;
import org.thingsboard.server.common.data.cf.CalculatedFieldType;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.cf.CalculatedFieldDao;
import org.thingsboard.server.dao.model.sql.CalculatedFieldEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Set;
import java.util.UUID;
/**
 * JPA/PostgreSQL implementation of calculated field dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Slf4j
@Component
@AllArgsConstructor
@SqlDao
public class JpaCalculatedFieldDao extends JpaAbstractDao<CalculatedFieldEntity, CalculatedField> implements CalculatedFieldDao {

    private final CalculatedFieldRepository calculatedFieldRepository;
    private final NativeCalculatedFieldRepository nativeCalculatedFieldRepository;

    
    /**
     * Finds all by tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<CalculatedField> findAllByTenantId(TenantId tenantId) {
        return DaoUtil.convertDataList(calculatedFieldRepository.findAllByTenantId(tenantId.getId()));
    }

    
    /**
     * Finds calculated field ids by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<CalculatedFieldId> findCalculatedFieldIdsByEntityId(TenantId tenantId, EntityId entityId) {
        return calculatedFieldRepository.findCalculatedFieldIdsByTenantIdAndEntityId(tenantId.getId(), entityId.getId());
    }

    
    /**
     * Finds calculated fields by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<CalculatedField> findCalculatedFieldsByEntityId(TenantId tenantId, EntityId entityId) {
        return DaoUtil.convertDataList(calculatedFieldRepository.findAllByTenantIdAndEntityId(tenantId.getId(), entityId.getId()));
    }

    
    /**
     * Finds all.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public List<CalculatedField> findAll() {
        return DaoUtil.convertDataList(calculatedFieldRepository.findAll());
    }

    
    /**
     * Finds by entity id and type and name.
     *
     * @param entityId target entity identifier
     * @param type type ({@link CalculatedFieldType})
     * @param name entity or attribute name
     * @return {@link CalculatedField}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public CalculatedField findByEntityIdAndTypeAndName(EntityId entityId, CalculatedFieldType type, String name) {
        return DaoUtil.getData(calculatedFieldRepository.findByEntityIdAndTypeAndName(entityId.getId(), type.name(), name));
    }

    
    /**
     * Finds all.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<CalculatedField> findAll(PageLink pageLink) {
        log.debug("Try to find calculated fields by pageLink [{}]", pageLink);
        return nativeCalculatedFieldRepository.findCalculatedFields(DaoUtil.toPageable(pageLink));
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
    public PageData<CalculatedField> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        log.debug("Try to find calculated fields by tenantId[{}] and pageLink [{}]", tenantId, pageLink);
        return DaoUtil.toPageData(calculatedFieldRepository.findAllByTenantId(tenantId.getId(), DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds by entity id and types.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param types types ({@link Set})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<CalculatedField> findByEntityIdAndTypes(TenantId tenantId, EntityId entityId, Set<CalculatedFieldType> types, PageLink pageLink) {
        log.debug("Try to find calculated fields by entityId [{}] and type [{}] and pageLink [{}]", entityId, types, pageLink);
        return DaoUtil.toPageData(calculatedFieldRepository.findByTenantIdAndEntityIdAndTypes(tenantId.getId(), entityId.getId(),
                types.stream().map(Enum::name).toList(), pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Removes all by entity id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    @Transactional
    public List<CalculatedField> removeAllByEntityId(TenantId tenantId, EntityId entityId) {
        return DaoUtil.convertDataList(calculatedFieldRepository.removeAllByTenantIdAndEntityId(tenantId.getId(), entityId.getId()));
    }

    
    /**
     * Counts by entity id and type not.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param entityId target entity identifier
     * @param type type ({@link CalculatedFieldType})
     * @return the long result
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public long countByEntityIdAndTypeNot(TenantId tenantId, EntityId entityId, CalculatedFieldType type) {
        return calculatedFieldRepository.countByTenantIdAndEntityIdAndTypeNot(tenantId.getId(), entityId.getId(), type.name());
    }

    
    /**
     * Finds by tenant id and filter.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param filter filter ({@link CalculatedFieldFilter})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<CalculatedField> findByTenantIdAndFilter(TenantId tenantId, CalculatedFieldFilter filter, PageLink pageLink) {
        return DaoUtil.toPageData(calculatedFieldRepository.findByTenantIdAndFilter(tenantId.getId(),
                filter.getTypes().stream().map(Enum::name).toList(),
                filter.getEntityTypes().stream().map(Enum::name).toList(),
                CollectionUtils.isNotEmpty(filter.getEntityIds()) ? filter.getEntityIds() : null,
                CollectionUtils.isNotEmpty(filter.getNames()) ? filter.getNames() : null,
                pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds names by tenant id and type.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link CalculatedFieldType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<String> findNamesByTenantIdAndType(TenantId tenantId, CalculatedFieldType type, PageLink pageLink) {
        return DaoUtil.pageToPageData(calculatedFieldRepository.findNamesByTenantIdAndType(tenantId.getId(), type.name(),
                Strings.emptyToNull(pageLink.getTextSearch()), DaoUtil.toPageable(pageLink, false)));
    }

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<CalculatedFieldEntity> getEntityClass() {
        return CalculatedFieldEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<CalculatedFieldEntity, UUID> getRepository() {
        return calculatedFieldRepository;
    }

    
    /**
     * Returns entity type.
     *
     * @return {@link EntityType}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public EntityType getEntityType() {
        return EntityType.CALCULATED_FIELD;
    }

}
