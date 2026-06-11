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
package org.thingsboard.server.dao.sqlts.dictionary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sqlts.dictionary.KeyDictionaryCompositeKey;
import org.thingsboard.server.dao.model.sqlts.dictionary.KeyDictionaryEntry;

import java.util.Optional;


/**

 * Spring Data JPA repository for key dictionary entities.

 *

 * <p>Defines query methods and native SQL used by the corresponding {@code Jpa*Dao}.

 */


public interface KeyDictionaryRepository extends JpaRepository<KeyDictionaryEntry, KeyDictionaryCompositeKey> {
    /**
     * Finds by key id.
     *
     * @param keyId key id
     * @return optional {@link KeyDictionaryEntry}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<KeyDictionaryEntry> findByKeyId(int keyId);
    /**
     * Finds all.
     *
     * @param pageable pageable ({@link Pageable})
     * @return {@link Page}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query("SELECT e FROM KeyDictionaryEntry e ORDER BY e.keyId ASC")
    Page<KeyDictionaryEntry> findAll(Pageable pageable);
    /**
     * Upsert and get key id.
     *
     * @param key attribute or cache key
     * @return {@link Integer}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Query(value = "INSERT INTO key_dictionary (key) VALUES (:key) ON CONFLICT (key) DO UPDATE SET key = EXCLUDED.key RETURNING key_id", nativeQuery = true)
    Integer upsertAndGetKeyId(@Param("key") String key);

}
