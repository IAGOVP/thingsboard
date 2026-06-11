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
package org.thingsboard.server.dao.dictionary;


import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.model.sqlts.dictionary.KeyDictionaryEntry;


/**

 * Persistence contract for key dictionary.

 *

 * <p>Implemented by {@code Jpa*Dao} or Cassandra DAO classes (ThingsBoard DAO layer).

 */


public interface KeyDictionaryDao {
    /**
     * Returns or save key id.
     *
     * @param strKey str key ({@link String})
     * @return {@link Integer}
     * @throws Exception if an unexpected error occurs during processing
     */

    Integer getOrSaveKeyId(String strKey);
    /**
     * Returns key.
     *
     * @param keyId key id ({@link Integer})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    String getKey(Integer keyId);
    /**
     * Finds all.
     *
     * @param pageLink pagination, sort, and text-search parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<KeyDictionaryEntry> findAll(PageLink pageLink);
}
