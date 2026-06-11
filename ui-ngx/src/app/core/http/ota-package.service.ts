///
/// Copyright © 2016-2026 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { defaultHttpOptionsFromConfig, defaultHttpUploadOptions, RequestConfig } from '@core/http/http-utils';
import { forkJoin, Observable, of } from 'rxjs';
import { PageData } from '@shared/models/page/page-data';
import {
  ChecksumAlgorithm,
  OtaPackage,
  OtaPackageInfo,
  OtaPagesIds,
  OtaUpdateType
} from '@shared/models/ota-package.models';
import { catchError, mergeMap } from 'rxjs/operators';
import { deepClone } from '@core/utils';
import { BaseData } from '@shared/models/base-data';
import { EntityId } from '@shared/models/id/entity-id';
import { TranslateService } from '@ngx-translate/core';
import { DialogService } from '@core/services/dialog.service';
import { ResourcesService } from '@core/services/resources.service';

/**
 * Angular injectable service: ota package (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class OtaPackageService {
  constructor(
    private http: HttpClient,
    private translate: TranslateService,
    private dialogService: DialogService,
    private resourcesService: ResourcesService
  ) {

  }

  
  /**
   * get ota packages.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<OtaPackageInfo>> observable or value
   */


  public getOtaPackages(pageLink: PageLink, config?: RequestConfig): Observable<PageData<OtaPackageInfo>> {
    return this.http.get<PageData<OtaPackageInfo>>(`/api/otaPackages${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get ota packages info by device profile id.
   *
   * @param pageLink pagination and sort parameters
   * @param deviceProfileId device profile id (string)
   * @param type type (OtaUpdateType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<OtaPackageInfo>> observable or value
   */


  public getOtaPackagesInfoByDeviceProfileId(pageLink: PageLink, deviceProfileId: string, type: OtaUpdateType,
                                             config?: RequestConfig): Observable<PageData<OtaPackageInfo>> {
    const url = `/api/otaPackages/${deviceProfileId}/${type}${pageLink.toQuery()}`;
    return this.http.get<PageData<OtaPackageInfo>>(url, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get ota package.
   *
   * @param otaPackageId ota package id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<OtaPackage> observable or value
   */


  public getOtaPackage(otaPackageId: string, config?: RequestConfig): Observable<OtaPackage> {
    return this.http.get<OtaPackage>(`/api/otaPackage/${otaPackageId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get ota package info.
   *
   * @param otaPackageId ota package id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<OtaPackageInfo> observable or value
   */


  public getOtaPackageInfo(otaPackageId: string, config?: RequestConfig): Observable<OtaPackageInfo> {
    return this.http.get<OtaPackageInfo>(`/api/otaPackage/info/${otaPackageId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * Calls ThingsBoard REST `/api/otaPackage/*`.
   *
   * REST endpoint(s): `/api/otaPackage/*`
   *
   * @param otaPackageId ota package id (string)
   * @returns Observable<any> observable or value
   */


  public downloadOtaPackage(otaPackageId: string): Observable<any> {
    return this.resourcesService.downloadResource(`/api/otaPackage/${otaPackageId}/download`);
  }

  
  /**
   * POST/PUT entity — save ota package.
   *
   * @param otaPackage ota package (OtaPackage)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<OtaPackage> observable or value
   */


  public saveOtaPackage(otaPackage: OtaPackage, config?: RequestConfig): Observable<OtaPackage> {
    if (!otaPackage.file) {
      return this.saveOtaPackageInfo(otaPackage, config);
    }
    const otaPackageInfo = deepClone(otaPackage);
    delete otaPackageInfo.file;
    delete otaPackageInfo.checksum;
    delete otaPackageInfo.checksumAlgorithm;
    return this.saveOtaPackageInfo(otaPackageInfo, config).pipe(
      mergeMap(res => {
        return this.uploadOtaPackageFile(res.id.id, otaPackage.file, otaPackage.checksumAlgorithm, otaPackage.checksum).pipe(
          catchError(() => this.deleteOtaPackage(res.id.id))
        );
      })
    );
  }

  
  /**
   * POST/PUT entity — save ota package info.
   *
   * @param otaPackageInfo ota package info (OtaPackageInfo)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<OtaPackage> observable or value
   */


  public saveOtaPackageInfo(otaPackageInfo: OtaPackageInfo, config?: RequestConfig): Observable<OtaPackage> {
    return this.http.post<OtaPackage>('/api/otaPackage', otaPackageInfo, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * upload ota package file.
   *
   * @param otaPackageId ota package id (string)
   * @param file file (File)
   * @param checksumAlgorithm checksum algorithm (ChecksumAlgorithm)
   * @param checksum checksum (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<any> observable or value
   */


  public uploadOtaPackageFile(otaPackageId: string, file: File, checksumAlgorithm: ChecksumAlgorithm,
                              checksum?: string, config?: RequestConfig): Observable<any> {
    if (!config) {
      config = {};
    }
    const formData = new FormData();
    formData.append('file', file);
    let url = `/api/otaPackage/${otaPackageId}?checksumAlgorithm=${checksumAlgorithm}`;
    if (checksum) {
      url += `&checksum=${checksum}`;
    }
    return this.http.post(url, formData,
      defaultHttpUploadOptions(config.ignoreLoading, config.ignoreErrors, config.resendRequest));
  }

  /**
   * DELETE — delete ota package.
   *
   * @param otaPackageId ota package id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteOtaPackage(otaPackageId: string, config?: RequestConfig) {
    return this.http.delete(`/api/otaPackage/${otaPackageId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * count update device after change package.
   *
   * @param type type (OtaUpdateType)
   * @param entityId entity UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<number> observable or value
   */


  public countUpdateDeviceAfterChangePackage(type: OtaUpdateType, entityId: EntityId, config?: RequestConfig): Observable<number> {
    return this.http.get<number>(`/api/devices/count/${type}/${entityId.id}`, defaultHttpOptionsFromConfig(config));
  }

  /**
   * confirm dialog update package.
   *
   * @param entity entity (BaseData<EntityId>&OtaPagesIds)
   * @param originEntity origin entity (BaseData<EntityId>&OtaPagesIds)
   * @returns Observable<boolean> observable or value
   */

  public confirmDialogUpdatePackage(entity: BaseData<EntityId>&OtaPagesIds,
                                    originEntity: BaseData<EntityId>&OtaPagesIds): Observable<boolean> {
    const tasks: Observable<number>[] = [];
    if (originEntity?.id?.id && originEntity.firmwareId?.id !== entity.firmwareId?.id) {
      tasks.push(this.countUpdateDeviceAfterChangePackage(OtaUpdateType.FIRMWARE, entity.id));
    } else {
      tasks.push(of(0));
    }
    if (originEntity?.id?.id && originEntity.softwareId?.id !== entity.softwareId?.id) {
      tasks.push(this.countUpdateDeviceAfterChangePackage(OtaUpdateType.SOFTWARE, entity.id));
    } else {
      tasks.push(of(0));
    }
    return forkJoin(tasks).pipe(
      mergeMap(([deviceFirmwareUpdate, deviceSoftwareUpdate]) => {
        const lines: string[] = [];
        if (deviceFirmwareUpdate > 0) {
          lines.push(this.translate.instant('ota-update.change-firmware', {count: deviceFirmwareUpdate}));
        }
        if (deviceSoftwareUpdate > 0) {
          lines.push(this.translate.instant('ota-update.change-software', {count: deviceSoftwareUpdate}));
        }
        return lines.length
          ? this.dialogService.confirm(this.translate.instant('ota-update.change-ota-setting-title'), lines.join('<br/>'), null, this.translate.instant('common.proceed'))
          : of(true);
      })
    );
  }

}
