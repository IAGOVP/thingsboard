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
package org.thingsboard.server.dao.sql.component;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.id.ComponentDescriptorId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.plugin.ComponentDescriptor;
import org.thingsboard.server.common.data.plugin.ComponentScope;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.component.ComponentDescriptorDao;
import org.thingsboard.server.dao.model.sql.ComponentDescriptorEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA/PostgreSQL implementation of base component descriptor dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */

@Component
public class JpaBaseComponentDescriptorDao extends JpaAbstractDao<ComponentDescriptorEntity, ComponentDescriptor>
        implements ComponentDescriptorDao {

    @Autowired
    private ComponentDescriptorRepository componentDescriptorRepository;

    @Autowired
    private ComponentDescriptorInsertRepository componentDescriptorInsertRepository;

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<ComponentDescriptorEntity> getEntityClass() {
        return ComponentDescriptorEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<ComponentDescriptorEntity, UUID> getRepository() {
        return componentDescriptorRepository;
    }

    
    /**
     * Saves or persists if not exist.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param component component ({@link ComponentDescriptor})
     * @return optional {@link ComponentDescriptor}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public Optional<ComponentDescriptor> saveIfNotExist(TenantId tenantId, ComponentDescriptor component) {
        if (component.getId() == null) {
            UUID uuid = Uuids.timeBased();
            component.setId(new ComponentDescriptorId(uuid));
            component.setCreatedTime(Uuids.unixTimestamp(uuid));
        }
        if (!componentDescriptorRepository.existsById(component.getId().getId())) {
            ComponentDescriptorEntity componentDescriptorEntity = new ComponentDescriptorEntity(component);
            ComponentDescriptorEntity savedEntity = componentDescriptorInsertRepository.saveOrUpdate(componentDescriptorEntity);
            return Optional.of(savedEntity.toData());
        }
        return Optional.empty();
    }

    
    /**
     * Finds by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param componentId component id ({@link ComponentDescriptorId})
     * @return {@link ComponentDescriptor}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ComponentDescriptor findById(TenantId tenantId, ComponentDescriptorId componentId) {
        return findById(tenantId, componentId.getId());
    }

    
    /**
     * Finds by clazz.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param clazz clazz ({@link String})
     * @return {@link ComponentDescriptor}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ComponentDescriptor findByClazz(TenantId tenantId, String clazz) {
        return DaoUtil.getData(componentDescriptorRepository.findByClazz(clazz));
    }

    
    /**
     * Finds by type and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param type type ({@link ComponentType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<ComponentDescriptor> findByTypeAndPageLink(TenantId tenantId, ComponentType type, PageLink pageLink) {
        return DaoUtil.toPageData(componentDescriptorRepository
                .findByType(
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds by scope and type and page link.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param scope attribute scope (SERVER_SCOPE, SHARED_SCOPE, etc.)
     * @param type type ({@link ComponentType})
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<ComponentDescriptor> findByScopeAndTypeAndPageLink(TenantId tenantId, ComponentScope scope, ComponentType type, PageLink pageLink) {
        return DaoUtil.toPageData(componentDescriptorRepository
                .findByScopeAndType(
                        type,
                        scope,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Deletes by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param componentId component id ({@link ComponentDescriptorId})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    @Transactional
    public void deleteById(TenantId tenantId, ComponentDescriptorId componentId) {
        removeById(tenantId, componentId.getId());
    }

    
    /**
     * Deletes by clazz.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param clazz clazz ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    @Transactional
    public void deleteByClazz(TenantId tenantId, String clazz) {
        componentDescriptorRepository.deleteByClazz(clazz);
    }
}
