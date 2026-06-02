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
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable } from 'rxjs';
import { PageData } from '@shared/models/page/page-data';
import { AssetProfile, AssetProfileInfo } from '@shared/models/asset.models';
import { EntityInfoData } from '@shared/models/entity.models';
import { isDefinedAndNotNull } from '@core/utils';

/**
 * Angular HTTP service: asset profile REST wrappers (`@core/http`).
 */
@Injectable({
  providedIn: 'root'
})
export class AssetProfileService {

  constructor(
    private http: HttpClient
  ) {
  }

  /** Calls ThingsBoard REST `/api/assetProfiles${pageLink.toQuery()}, ...`. */

  public getAssetProfiles(pageLink: PageLink, config?: RequestConfig): Observable<PageData<AssetProfile>> {
    return this.http.get<PageData<AssetProfile>>(`/api/assetProfiles${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/assetProfileInfos?assetProfileIds=${assetProfileIds.join(, ...`. */

  public getAssetProfilesByIds(assetProfileIds: Array<string>, config?: RequestConfig): Observable<Array<AssetProfileInfo>> {
    return this.http.get<Array<AssetProfileInfo>>(`/api/assetProfileInfos?assetProfileIds=${assetProfileIds.join(',')}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/assetProfile/${assetProfileId}, ...`. */

  public getAssetProfile(assetProfileId: string, config?: RequestConfig): Observable<AssetProfile> {
    return this.http.get<AssetProfile>(`/api/assetProfile/${assetProfileId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/assetProfile/${assetProfileId}?inlineImages=true, ...`. */

  public exportAssetProfile(assetProfileId: string, config?: RequestConfig): Observable<AssetProfile> {
    return this.http.get<AssetProfile>(`/api/assetProfile/${assetProfileId}?inlineImages=true`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/assetProfile, ...`. */

  public saveAssetProfile(assetProfile: AssetProfile, config?: RequestConfig): Observable<AssetProfile> {
    return this.http.post<AssetProfile>('/api/assetProfile', assetProfile, defaultHttpOptionsFromConfig(config));
  }

  public deleteAssetProfile(assetProfileId: string, config?: RequestConfig) {
    return this.http.delete(`/api/assetProfile/${assetProfileId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/assetProfile/${assetProfileId}/default, ...`. */

  public setDefaultAssetProfile(assetProfileId: string, config?: RequestConfig): Observable<AssetProfile> {
    return this.http.post<AssetProfile>(`/api/assetProfile/${assetProfileId}/default`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/assetProfileInfo/default, ...`. */

  public getDefaultAssetProfileInfo(config?: RequestConfig): Observable<AssetProfileInfo> {
    return this.http.get<AssetProfileInfo>('/api/assetProfileInfo/default', defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/assetProfileInfo/${assetProfileId}, ...`. */

  public getAssetProfileInfo(assetProfileId: string, config?: RequestConfig): Observable<AssetProfileInfo> {
    return this.http.get<AssetProfileInfo>(`/api/assetProfileInfo/${assetProfileId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/assetProfileInfos${pageLink.toQuery()}, ...`. */

  public getAssetProfileInfos(pageLink: PageLink, config?: RequestConfig): Observable<PageData<AssetProfileInfo>> {
    return this.http.get<PageData<AssetProfileInfo>>(`/api/assetProfileInfos${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/assetProfile/names`. */

  public getAssetProfileNames(activeOnly: boolean = false, config?: RequestConfig): Observable<Array<EntityInfoData>> {
    let url = '/api/assetProfile/names';
    if (isDefinedAndNotNull(activeOnly)) {
      url += `?activeOnly=${activeOnly}`;
    }
    return this.http.get<Array<EntityInfoData>>(url, defaultHttpOptionsFromConfig(config));
  }

}
