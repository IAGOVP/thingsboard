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
 * Angular injectable service: asset profile (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class AssetProfileService {

  constructor(
    private http: HttpClient
  ) {
  }

  
  /**
   * get asset profiles.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AssetProfile>> observable or value
   */


  public getAssetProfiles(pageLink: PageLink, config?: RequestConfig): Observable<PageData<AssetProfile>> {
    return this.http.get<PageData<AssetProfile>>(`/api/assetProfiles${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get asset profiles by ids.
   *
   * @param assetProfileIds asset profile ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<AssetProfileInfo>> observable or value
   */


  public getAssetProfilesByIds(assetProfileIds: Array<string>, config?: RequestConfig): Observable<Array<AssetProfileInfo>> {
    return this.http.get<Array<AssetProfileInfo>>(`/api/assetProfileInfos?assetProfileIds=${assetProfileIds.join(',')}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get asset profile.
   *
   * @param assetProfileId asset profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AssetProfile> observable or value
   */


  public getAssetProfile(assetProfileId: string, config?: RequestConfig): Observable<AssetProfile> {
    return this.http.get<AssetProfile>(`/api/assetProfile/${assetProfileId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * export asset profile.
   *
   * @param assetProfileId asset profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AssetProfile> observable or value
   */


  public exportAssetProfile(assetProfileId: string, config?: RequestConfig): Observable<AssetProfile> {
    return this.http.get<AssetProfile>(`/api/assetProfile/${assetProfileId}?inlineImages=true`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save asset profile.
   *
   * @param assetProfile asset profile (AssetProfile)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AssetProfile> observable or value
   */


  public saveAssetProfile(assetProfile: AssetProfile, config?: RequestConfig): Observable<AssetProfile> {
    return this.http.post<AssetProfile>('/api/assetProfile', assetProfile, defaultHttpOptionsFromConfig(config));
  }

  /**
   * DELETE — delete asset profile.
   *
   * @param assetProfileId asset profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteAssetProfile(assetProfileId: string, config?: RequestConfig) {
    return this.http.delete(`/api/assetProfile/${assetProfileId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * set default asset profile.
   *
   * @param assetProfileId asset profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AssetProfile> observable or value
   */


  public setDefaultAssetProfile(assetProfileId: string, config?: RequestConfig): Observable<AssetProfile> {
    return this.http.post<AssetProfile>(`/api/assetProfile/${assetProfileId}/default`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get default asset profile info.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AssetProfileInfo> observable or value
   */


  public getDefaultAssetProfileInfo(config?: RequestConfig): Observable<AssetProfileInfo> {
    return this.http.get<AssetProfileInfo>('/api/assetProfileInfo/default', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get asset profile info.
   *
   * @param assetProfileId asset profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AssetProfileInfo> observable or value
   */


  public getAssetProfileInfo(assetProfileId: string, config?: RequestConfig): Observable<AssetProfileInfo> {
    return this.http.get<AssetProfileInfo>(`/api/assetProfileInfo/${assetProfileId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get asset profile infos.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AssetProfileInfo>> observable or value
   */


  public getAssetProfileInfos(pageLink: PageLink, config?: RequestConfig): Observable<PageData<AssetProfileInfo>> {
    return this.http.get<PageData<AssetProfileInfo>>(`/api/assetProfileInfos${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get asset profile names.
   *
   * @param activeOnly active only (boolean)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityInfoData>> observable or value
   */


  public getAssetProfileNames(activeOnly: boolean = false, config?: RequestConfig): Observable<Array<EntityInfoData>> {
    let url = '/api/assetProfile/names';
    if (isDefinedAndNotNull(activeOnly)) {
      url += `?activeOnly=${activeOnly}`;
    }
    return this.http.get<Array<EntityInfoData>>(url, defaultHttpOptionsFromConfig(config));
  }

}
