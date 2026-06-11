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
package org.thingsboard.server.common.data.page;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Schema
@EqualsAndHashCode
@ToString
/**
 * Generic paginated REST/DAO response wrapper holding a page of results.
 *
 * <p>Fields: {@code data} (current page items), {@code totalElements}, {@code totalPages},
 * {@code hasNext}. Constructed by DAO services from {@link PageLink} queries. Returned by most
 * list endpoints (devices, alarms, users, etc.).
 */
public class PageData<T> implements Serializable {

    public static final PageData EMPTY_PAGE_DATA = new PageData<>();

    private final List<T> data;
    private final int totalPages;
    private final long totalElements;
    private final boolean hasNext;

    public PageData() {
        this(Collections.emptyList(), 0, 0, false);
    }

    @JsonCreator
    public PageData(@JsonProperty("data") List<T> data,
                    @JsonProperty("totalPages") int totalPages,
                    @JsonProperty("totalElements") long totalElements,
                    @JsonProperty("hasNext") boolean hasNext) {
        this.data = data;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }
    /**
     * Empty page data.
     *
     * @return {@link PageData}
     */

    @SuppressWarnings("unchecked")
    public static <T> PageData<T> emptyPageData() {
        return (PageData<T>) EMPTY_PAGE_DATA;
    }
    /**
     * Returns data.
     *
     * @return {@link List}
     */

    @Schema(description = "Array of the entities", accessMode = Schema.AccessMode.READ_ONLY)
    public List<T> getData() {
        return data;
    }
    /**
     * Returns total pages.
     *
     * @return the int result
     */

    @Schema(description = "Total number of available pages. Calculated based on the 'pageSize' request parameter and total number of entities that match search criteria", accessMode = Schema.AccessMode.READ_ONLY)
    public int getTotalPages() {
        return totalPages;
    }
    /**
     * Returns total elements.
     *
     * @return the long result
     */

    @Schema(description = "Total number of elements in all available pages", accessMode = Schema.AccessMode.READ_ONLY)
    public long getTotalElements() {
        return totalElements;
    }
    /**
     * Has next.
     *
     * @return the boolean result
     */

    @Schema(description = "'false' value indicates the end of the result set", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("hasNext")
    public boolean hasNext() {
        return hasNext;
    }
    /**
     * Map data.
     *
     * @param mapper mapper ({@link Function})
     * @return {@link PageData}
     */

    public <D> PageData<D> mapData(Function<T, D> mapper) {
        return new PageData<>(getData().stream().map(mapper).collect(Collectors.toList()), getTotalPages(), getTotalElements(), hasNext());
    }

}
