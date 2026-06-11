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
import { TenantProfile } from '@shared/models/tenant.model';
import { EntityInfoData } from '@shared/models/entity.models';
import { sortEntitiesByIds } from '@shared/models/base-data';
import { map } from 'rxjs/operators';

/**
 * Angular injectable service: tenant profile (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class TenantProfileService {

  constructor(
    private http: HttpClient
  ) { }

  
  /**
   * get tenant profiles.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<TenantProfile>> observable or value
   */


  public getTenantProfiles(pageLink: PageLink, config?: RequestConfig): Observable<PageData<TenantProfile>> {
    return this.http.get<PageData<TenantProfile>>(`/api/tenantProfiles${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant profile.
   *
   * @param tenantProfileId tenant profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<TenantProfile> observable or value
   */


  public getTenantProfile(tenantProfileId: string, config?: RequestConfig): Observable<TenantProfile> {
    return this.http.get<TenantProfile>(`/api/tenantProfile/${tenantProfileId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save tenant profile.
   *
   * @param tenantProfile tenant profile (TenantProfile)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<TenantProfile> observable or value
   */


  public saveTenantProfile(tenantProfile: TenantProfile, config?: RequestConfig): Observable<TenantProfile> {
    return this.http.post<TenantProfile>('/api/tenantProfile', tenantProfile, defaultHttpOptionsFromConfig(config));
  }

  /**
   * DELETE — delete tenant profile.
   *
   * @param tenantProfileId tenant profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteTenantProfile(tenantProfileId: string, config?: RequestConfig) {
    return this.http.delete(`/api/tenantProfile/${tenantProfileId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * set default tenant profile.
   *
   * @param tenantProfileId tenant profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<TenantProfile> observable or value
   */


  public setDefaultTenantProfile(tenantProfileId: string, config?: RequestConfig): Observable<TenantProfile> {
    return this.http.post<TenantProfile>(`/api/tenantProfile/${tenantProfileId}/default`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get default tenant profile info.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EntityInfoData> observable or value
   */


  public getDefaultTenantProfileInfo(config?: RequestConfig): Observable<EntityInfoData> {
    return this.http.get<EntityInfoData>('/api/tenantProfileInfo/default', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant profile info.
   *
   * @param tenantProfileId tenant profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EntityInfoData> observable or value
   */


  public getTenantProfileInfo(tenantProfileId: string, config?: RequestConfig): Observable<EntityInfoData> {
    return this.http.get<EntityInfoData>(`/api/tenantProfileInfo/${tenantProfileId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant profile infos.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<EntityInfoData>> observable or value
   */


  public getTenantProfileInfos(pageLink: PageLink, config?: RequestConfig): Observable<PageData<EntityInfoData>> {
    return this.http.get<PageData<EntityInfoData>>(`/api/tenantProfileInfos${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant profiles by ids.
   *
   * @param tenantProfileIds tenant profile ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityInfoData>> observable or value
   */


  public getTenantProfilesByIds(tenantProfileIds: Array<string>, config?: RequestConfig): Observable<Array<EntityInfoData>> {
    return this.http.get<Array<EntityInfoData>>(`/api/tenantProfiles?ids=${tenantProfileIds.join(',')}`,
      defaultHttpOptionsFromConfig(config)).pipe(
      map((tenantProfiles) => sortEntitiesByIds(tenantProfiles, tenantProfileIds))
    );
  }

}
