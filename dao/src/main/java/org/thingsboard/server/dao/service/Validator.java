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
package org.thingsboard.server.dao.service;

import org.apache.commons.lang3.StringUtils;
import org.thingsboard.common.util.RegexUtils;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.UUIDBased;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.query.EntityDataPageLink;
import org.thingsboard.server.common.data.query.EntityKey;
import org.thingsboard.server.common.data.query.EntityKeyType;
import org.thingsboard.server.dao.exception.IncorrectParameterException;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;






















/**






 * Validator (shared DAO validators, removers, and constraints).






 */







public final class Validator {

    private Validator() {}

    public static final Pattern PROPERTY_PATTERN = Pattern.compile("^[\\p{L}0-9_-]+$"); // Unicode letters, numbers, '_' and '-' allowed

    
    /**
     * Validates entity id.
     *
     * @param entityId target entity identifier
     * @param errorMessage error message ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Deprecated
    public static void validateEntityId(EntityId entityId, String errorMessage) {
        if (entityId == null || entityId.getId() == null) {
            throw new IncorrectParameterException(errorMessage);
        }
    }

    
    /**
     * Validates entity id.
     *
     * @param entityId target entity identifier
     * @param errorMessageFunction error message function ({@link Function})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public static void validateEntityId(EntityId entityId, Function<EntityId, String> errorMessageFunction) {
        if (entityId == null || entityId.getId() == null) {
            throw new IncorrectParameterException(errorMessageFunction.apply(entityId));
        }
    }

    
    /**
     * Validates string.
     *
     * @param val val ({@link String})
     * @param errorMessage error message ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public static void validateString(String val, String errorMessage) {
        if (val == null || val.isEmpty()) {
            throw new IncorrectParameterException(errorMessage);
        }
    }

    
    /**
     * Validates string.
     *
     * @param val val ({@link String})
     * @param errorMessageFunction error message function ({@link Function})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public static void validateString(String val, Function<String, String> errorMessageFunction) {
        if (val == null || val.isEmpty()) {
            throw new IncorrectParameterException(errorMessageFunction.apply(val));
        }
    }

    
    /**
     * Validates positive number.
     *
     * @param val val
     * @param errorMessage error message ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public static void validatePositiveNumber(long val, String errorMessage) {
        if (val <= 0) {
            throw new IncorrectParameterException(errorMessage);
        }
    }

    
    /**
     * Validates id.
     *
     * @param id entity UUID primary key
     * @param errorMessage error message ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Deprecated
    public static void validateId(UUID id, String errorMessage) {
        if (id == null) {
            throw new IncorrectParameterException(errorMessage);
        }
    }

    
    /**
     * Validates id.
     *
     * @param id entity UUID primary key
     * @param errorMessageFunction error message function ({@link Function})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public static void validateId(UUID id, Function<UUID, String> errorMessageFunction) {
        if (id == null) {
            throw new IncorrectParameterException(errorMessageFunction.apply(id));
        }
    }

    
    /**
     * Validates id.
     *
     * @param id entity UUID primary key
     * @param errorMessage error message ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Deprecated
    public static void validateId(UUIDBased id, String errorMessage) {
        if (id == null || id.getId() == null) {
            throw new IncorrectParameterException(errorMessage);
        }
    }

    
    /**
     * Validates id.
     *
     * @param id entity UUID primary key
     * @param errorMessageFunction error message function ({@link Function})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public static void validateId(UUIDBased id, Function<UUIDBased, String> errorMessageFunction) {
        if (id == null || id.getId() == null) {
            throw new IncorrectParameterException(errorMessageFunction.apply(id));
        }
    }

    /**
     * This method validate <code>UUIDBased</code> id. If id is null than throw
     * <code>IncorrectParameterException</code> exception
     *
     * @param id                    the id
     * @param ids                   the list of ids
     * @param errorMessageFunction  the error message function for exception that applies ids
     */
    static void validateId(UUIDBased id, List<? extends UUIDBased> ids, Function<List<? extends UUIDBased>, String> errorMessageFunction) {
        if (id == null) {
            throw new IncorrectParameterException(errorMessageFunction.apply(ids));
        }
    }

    
    /**
     * Validates ids.
     *
     * @param ids ids ({@link List})
     * @param errorMessage error message ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Deprecated
    public static void validateIds(List<? extends UUIDBased> ids, String errorMessage) {
        if (ids == null || ids.isEmpty()) {
            throw new IncorrectParameterException(errorMessage);
        } else {
            for (UUIDBased id : ids) {
                validateId(id, errorMessage);
            }
        }
    }

    
    /**
     * Validates ids.
     *
     * @param ids ids ({@link List})
     * @param errorMessageFunction error message function ({@link Function})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public static void validateIds(List<? extends UUIDBased> ids, Function<List<? extends UUIDBased>, String> errorMessageFunction) {
        if (ids == null || ids.isEmpty()) {
            throw new IncorrectParameterException(errorMessageFunction.apply(ids));
        } else {
            for (UUIDBased id : ids) {
                validateId(id, ids, errorMessageFunction);
            }
        }
    }

    
    /**
     * Validates page link.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public static void validatePageLink(PageLink pageLink) {
        validatePageLink(pageLink, null);
    }

    
    /**
     * Validates page link.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @param allowedSortProperties allowed sort properties ({@link Set})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public static void validatePageLink(PageLink pageLink, Set<String> allowedSortProperties) {
        if (pageLink == null) {
            throw new IncorrectParameterException("Page link must be specified.");
        } else if (pageLink.getPageSize() < 1) {
            throw new IncorrectParameterException("Incorrect page link page size '" + pageLink.getPageSize() + "'. Page size must be greater than zero.");
        } else if (pageLink.getPage() < 0) {
            throw new IncorrectParameterException("Incorrect page link page '" + pageLink.getPage() + "'. Page must be positive integer.");
        } else if (pageLink.getSortOrder() != null) {
            String sortProperty = pageLink.getSortOrder().getProperty();
            if (!isValidProperty(sortProperty)) {
                throw new IncorrectParameterException("Invalid page link sort property");
            }
            if (allowedSortProperties != null && !allowedSortProperties.contains(sortProperty)) {
                throw new IncorrectParameterException(
                        "Unsupported sort property '" + sortProperty + "'. Only '" + String.join("', '", allowedSortProperties) + "' are allowed."
                );
            }
        }
    }

    
    /**
     * Validates entity data page link.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    public static void validateEntityDataPageLink(EntityDataPageLink pageLink) {
        if (pageLink == null) {
            throw new IncorrectParameterException("Entity Data Page link must be specified.");
        } else if (pageLink.getPageSize() < 1) {
            throw new IncorrectParameterException("Incorrect entity data page link page size '" + pageLink.getPageSize() + "'. Page size must be greater than zero.");
        } else if (pageLink.getPage() < 0) {
            throw new IncorrectParameterException("Incorrect entity data page link page '" + pageLink.getPage() + "'. Page must be positive integer.");
        } else if (pageLink.getSortOrder() != null && pageLink.getSortOrder().getKey() != null) {
            EntityKey sortKey = pageLink.getSortOrder().getKey();
            if ((sortKey.getType() == EntityKeyType.ENTITY_FIELD || sortKey.getType() == EntityKeyType.ALARM_FIELD)
                    && !isValidProperty(sortKey.getKey())) {
                throw new IncorrectParameterException("Invalid entity data page link sort property");
            }
        }
    }

    
    /**
     * Is valid property.
     *
     * @param key attribute or cache key
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */


    public static boolean isValidProperty(String key) {
        return StringUtils.isEmpty(key) || RegexUtils.matches(key, PROPERTY_PATTERN);
    }

    
    /**
     * Checks not null.
     *
     * @param reference reference ({@link Object})
     * @param errorMessage error message ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    public static void checkNotNull(Object reference, String errorMessage) {
        if (reference == null) {
            throw new IncorrectParameterException(errorMessage);
        }
    }

}
