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
package org.thingsboard.server.dao;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UUIDBased;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.dao.model.ToData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;






















/**






 * Dao util (ThingsBoard DAO layer).






 */







public final class DaoUtil {

    private DaoUtil() {}
    /**
     * To page data.
     *
     * @param page page ({@link Page})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static <T> PageData<T> toPageData(Page<? extends ToData<T>> page) {
        List<T> data = convertDataList(page.getContent());
        return new PageData<>(data, page.getTotalPages(), page.getTotalElements(), page.hasNext());
    }
    /**
     * Page to page data.
     *
     * @param slice slice ({@link Slice})
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static <T> PageData<T> pageToPageData(Slice<T> slice) {
        int totalPages;
        long totalElements;
        if (slice instanceof Page<T> page) {
            totalPages = page.getTotalPages();
            totalElements = page.getTotalElements();
        } else {
            totalPages = 0;
            totalElements = 0;
        }
        return new PageData<>(slice.getContent(), totalPages, totalElements, slice.hasNext());
    }
    /**
     * To pageable.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link Pageable}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static Pageable toPageable(PageLink pageLink) {
        return toPageable(pageLink, true);
    }
    /**
     * To pageable.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @param addDefaultSorting add default sorting
     * @return {@link Pageable}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static Pageable toPageable(PageLink pageLink, boolean addDefaultSorting) {
        return toPageable(pageLink, Collections.emptyMap(), addDefaultSorting);
    }
    /**
     * To pageable.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @param columnMap column map ({@link Map})
     * @return {@link Pageable}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static Pageable toPageable(PageLink pageLink, Map<String, String> columnMap) {
        return toPageable(pageLink, columnMap, true);
    }
    /**
     * To pageable.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @param columnMap column map ({@link Map})
     * @param addDefaultSorting add default sorting
     * @return {@link Pageable}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static Pageable toPageable(PageLink pageLink, Map<String, String> columnMap, boolean addDefaultSorting) {
        return PageRequest.of(pageLink.getPage(), pageLink.getPageSize(), pageLink.toSort(pageLink.getSortOrder(), columnMap, addDefaultSorting));
    }
    /**
     * To pageable.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @param sortOrders sort orders ({@link List})
     * @return {@link Pageable}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static Pageable toPageable(PageLink pageLink, List<SortOrder> sortOrders) {
        return toPageable(pageLink, Collections.emptyMap(), sortOrders);
    }
    /**
     * To pageable.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @param sortColumns sort columns
     * @return {@link Pageable}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static Pageable toPageable(PageLink pageLink, String... sortColumns) {
        return toPageable(pageLink, Collections.emptyMap(), Arrays.stream(sortColumns).map(column -> new SortOrder(column, SortOrder.Direction.ASC)).toList(), false);
    }
    /**
     * To pageable.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @param columnMap column map ({@link Map})
     * @param sortOrders sort orders ({@link List})
     * @return {@link Pageable}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static Pageable toPageable(PageLink pageLink, Map<String, String> columnMap, List<SortOrder> sortOrders) {
        return toPageable(pageLink, columnMap, sortOrders, true);
    }
    /**
     * To pageable.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @param columnMap column map ({@link Map})
     * @param sortOrders sort orders ({@link List})
     * @param addDefaultSorting add default sorting
     * @return {@link Pageable}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static Pageable toPageable(PageLink pageLink, Map<String, String> columnMap, List<SortOrder> sortOrders, boolean addDefaultSorting) {
        return PageRequest.of(pageLink.getPage(), pageLink.getPageSize(), pageLink.toSort(sortOrders, columnMap, addDefaultSorting));
    }
    /**
     * Convert data list.
     *
     * @param toConvert to convert ({@link Collection})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static <T> List<T> convertDataList(Collection<? extends ToData<T>> toConvert) {
        if (CollectionUtils.isEmpty(toConvert)) {
            return Collections.emptyList();
        }
        List<T> converted = new ArrayList<>(toConvert.size());
        for (ToData<T> object : toConvert) {
            if (object != null) {
                converted.add(object.toData());
            }
        }
        return converted;
    }
    /**
     * Returns data.
     *
     * @param data data ({@link ToData})
     * @return {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static <T> T getData(ToData<T> data) {
        T object = null;
        if (data != null) {
            object = data.toData();
        }
        return object;
    }
    /**
     * Returns data.
     *
     * @param data data ({@link Optional})
     * @return {@link T}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static <T> T getData(Optional<? extends ToData<T>> data) {
        T object = null;
        if (data.isPresent()) {
            object = data.get().toData();
        }
        return object;
    }
    /**
     * Returns id.
     *
     * @param idBased id based ({@link UUIDBased})
     * @return {@link UUID}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static UUID getId(UUIDBased idBased) {
        UUID id = null;
        if (idBased != null) {
            id = idBased.getId();
        }
        return id;
    }
    /**
     * To uuids.
     *
     * @param idBasedIds id based ids ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static List<UUID> toUUIDs(List<? extends UUIDBased> idBasedIds) {
        List<UUID> ids = new ArrayList<>();
        for (UUIDBased idBased : idBasedIds) {
            ids.add(getId(idBased));
        }
        return ids;
    }
    /**
     * From uuids.
     *
     * @param uuids uuids ({@link List})
     * @param mapper mapper ({@link Function})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static <I> List<I> fromUUIDs(List<UUID> uuids, Function<UUID, I> mapper) {
        return uuids.stream().map(mapper).collect(Collectors.toList());
    }
    /**
     * To entity id.
     *
     * @param uuid uuid ({@link UUID})
     * @param creator creator ({@link Function})
     * @return {@link I}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static <I> I toEntityId(UUID uuid, Function<UUID, I> creator) {
        if (uuid != null) {
            return creator.apply(uuid);
        } else {
            return null;
        }
    }
    /**
     * Processes in batches.
     *
     * @param finder finder ({@link Function})
     * @param batchSize batch size
     * @param processor processor ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public static <T> void processInBatches(Function<PageLink, PageData<T>> finder, int batchSize, Consumer<T> processor) {
        processBatches(finder, batchSize, batch -> batch.getData().forEach(processor));
    }
    /**
     * Processes batches.
     *
     * @param finder finder ({@link Function})
     * @param batchSize batch size
     * @param processor processor ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public static <T> void processBatches(Function<PageLink, PageData<T>> finder, int batchSize, Consumer<PageData<T>> processor) {
        PageLink pageLink = new PageLink(batchSize);
        PageData<T> batch;

        boolean hasNextBatch;
        do {
            batch = finder.apply(pageLink);
            processor.accept(batch);

            hasNextBatch = batch.hasNext();
            pageLink = pageLink.nextPageLink();
        } while (hasNextBatch);
    }
    /**
     * Returns string id.
     *
     * @param id entity UUID primary key
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static String getStringId(UUIDBased id) {
        if (id != null) {
            return id.toString();
        } else {
            return null;
        }
    }
    /**
     * Convert tenant entity types to dto.
     *
     * @param tenantUUID tenant uuid ({@link UUID})
     * @param entityType entity type discriminator
     * @param types types ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static List<EntitySubtype> convertTenantEntityTypesToDto(UUID tenantUUID, EntityType entityType, List<String> types) {
        if (CollectionUtils.isEmpty(types)) {
            return Collections.emptyList();
        }
        TenantId tenantId = TenantId.fromUUID(tenantUUID);
        return types.stream()
                .map(type -> new EntitySubtype(tenantId, entityType, type))
                .collect(Collectors.toList());
    }
    /**
     * Convert tenant entity infos to dto.
     *
     * @param tenantUUID tenant uuid ({@link UUID})
     * @param entityType entity type discriminator
     * @param entityInfos entity infos ({@link List})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Deprecated // used only in deprecated DAO api
    public static List<EntitySubtype> convertTenantEntityInfosToDto(UUID tenantUUID, EntityType entityType, List<EntityInfo> entityInfos) {
        if (CollectionUtils.isEmpty(entityInfos)) {
            return Collections.emptyList();
        }
        var tenantId = TenantId.fromUUID(tenantUUID);
        return entityInfos.stream()
                .map(info -> new EntitySubtype(tenantId, entityType, info.getName()))
                .sorted(Comparator.comparing(EntitySubtype::getType))
                .collect(Collectors.toList());
    }
    /**
     * Extract constraint violation.
     *
     * @param t t ({@link Throwable})
     * @return {@link ConstraintViolationException}
     * @throws Exception if an unexpected error occurs during processing
     */

    public static ConstraintViolationException extractConstraintViolation(Throwable t) {
        if (t instanceof ConstraintViolationException cve) {
            return cve;
        } else if (t != null && t.getCause() instanceof ConstraintViolationException cve) {
            return cve;
        }
        return null;
    }

}
