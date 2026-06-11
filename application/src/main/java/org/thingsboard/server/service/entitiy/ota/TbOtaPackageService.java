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
package org.thingsboard.server.service.entitiy.ota;

import org.thingsboard.server.common.data.OtaPackageInfo;
import org.thingsboard.server.common.data.SaveOtaPackageInfoRequest;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;

/**

 * Application-layer service API for ota package entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbOtaPackageService {
/**
 * Saves or persists the requested data.
 *
 * @param saveOtaPackageInfoRequest save ota package info request ({@link SaveOtaPackageInfoRequest})
 * @param user authenticated user performing the action
 * @return {@link OtaPackageInfo}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */



    OtaPackageInfo save(SaveOtaPackageInfoRequest saveOtaPackageInfoRequest, User user) throws ThingsboardException;
/**
 * Saves or persists ota package data.
 *
 * @param otaPackageInfo ota package info ({@link OtaPackageInfo})
 * @param checksum checksum ({@link String})
 * @param checksumAlgorithm checksum algorithm ({@link ChecksumAlgorithm})
 * @param data data
 * @param filename filename ({@link String})
 * @param contentType content type ({@link String})
 * @param user authenticated user performing the action
 * @return {@link OtaPackageInfo}
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    OtaPackageInfo saveOtaPackageData(OtaPackageInfo otaPackageInfo, String checksum, ChecksumAlgorithm checksumAlgorithm,
                                      byte[] data, String filename, String contentType, User user) throws ThingsboardException;
/**
 * Deletes the requested data.
 *
 * @param otaPackageInfo ota package info ({@link OtaPackageInfo})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws ThingsboardException if the operation fails validation, authorization, or business rules
 */

    void delete(OtaPackageInfo otaPackageInfo, User user) throws ThingsboardException;

}
