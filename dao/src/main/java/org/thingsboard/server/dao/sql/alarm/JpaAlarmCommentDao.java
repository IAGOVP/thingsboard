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
package org.thingsboard.server.dao.sql.alarm;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.alarm.AlarmComment;
import org.thingsboard.server.common.data.alarm.AlarmCommentInfo;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.alarm.AlarmCommentDao;
import org.thingsboard.server.dao.model.sql.AlarmCommentEntity;
import org.thingsboard.server.dao.sql.JpaPartitionedAbstractDao;
import org.thingsboard.server.dao.sqlts.insert.sql.SqlPartitioningRepository;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.thingsboard.server.dao.model.ModelConstants.ALARM_COMMENT_TABLE_NAME;
/**
 * JPA/PostgreSQL implementation of alarm comment dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@Slf4j
@Component
@SqlDao
@RequiredArgsConstructor
public class JpaAlarmCommentDao extends JpaPartitionedAbstractDao<AlarmCommentEntity, AlarmComment> implements AlarmCommentDao, TenantEntityDao<AlarmComment> {
    private final SqlPartitioningRepository partitioningRepository;
    @Value("${sql.alarm_comments.partition_size:168}")
    private int partitionSizeInHours;

    @Autowired
    private AlarmCommentRepository alarmCommentRepository;

    
    /**
     * Finds alarm comments.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param id entity UUID primary key
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public PageData<AlarmCommentInfo> findAlarmComments(TenantId tenantId, AlarmId id, PageLink pageLink) {
        log.trace("Try to find alarm comments by alarm id using [{}]", id);
        return DaoUtil.toPageData(
                alarmCommentRepository.findAllByAlarmId(id.getId(), DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Finds alarm comment by id.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return {@link AlarmComment}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public AlarmComment findAlarmCommentById(TenantId tenantId, UUID key) {
        log.trace("Try to find alarm comment by id using [{}]", key);
        return DaoUtil.getData(alarmCommentRepository.findById(key));
    }

    
    /**
     * Finds alarm comment by id async.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param key attribute or cache key
     * @return future completing with {@link AlarmComment}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public ListenableFuture<AlarmComment> findAlarmCommentByIdAsync(TenantId tenantId, UUID key) {
        log.trace("Try to find alarm comment by id using [{}]", key);
        return findByIdAsync(tenantId, key);
    }

    
    /**
     * Creates partition.
     *
     * @param entity domain entity to persist or validate
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    public void createPartition(AlarmCommentEntity entity) {
        partitioningRepository.createPartitionIfNotExists(ALARM_COMMENT_TABLE_NAME, entity.getCreatedTime(), TimeUnit.HOURS.toMillis(partitionSizeInHours));
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
    public PageData<AlarmComment> findAllByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(alarmCommentRepository.findByTenantId(tenantId.getId(), DaoUtil.toPageable(pageLink)));
    }

    
    /**
     * Returns entity class.
     *
     * @return {@link Class}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected Class<AlarmCommentEntity> getEntityClass() {
        return AlarmCommentEntity.class;
    }

    
    /**
     * Returns repository.
     *
     * @return {@link JpaRepository}
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected JpaRepository<AlarmCommentEntity, UUID> getRepository() {
        return alarmCommentRepository;
    }

}
