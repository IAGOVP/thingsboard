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
package org.thingsboard.server.dao.sql;

import org.thingsboard.server.dao.model.BaseEntity;
import org.thingsboard.server.dao.util.SqlDao;
/**
 * JPA/PostgreSQL implementation of partitioned abstract dao.
 *
 * <p>Uses Spring Data repositories and {@link org.thingsboard.server.dao.sql.JpaAbstractDao} helpers.
 */


@SqlDao
public abstract class JpaPartitionedAbstractDao<E extends BaseEntity<D>, D> extends JpaAbstractDao<E, D> {
    /**
     * Do save.
     *
     * @param entity domain entity to persist or validate
     * @param isNew is new
     * @param flush flush
     * @return {@link E}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    protected E doSave(E entity, boolean isNew, boolean flush) {
        createPartition(entity);
        return super.doSave(entity, isNew, flush);
    }
    /**
     * Creates partition.
     *
     * @param entity domain entity to persist or validate
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public abstract void createPartition(E entity);

}
