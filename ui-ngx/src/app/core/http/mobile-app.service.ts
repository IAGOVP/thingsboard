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
import { defaultHttpOptionsFromConfig, RequestConfig } from '@core/http/http-utils';
import { Observable } from 'rxjs';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { MobileApp, MobileAppBundle, MobileAppBundleInfo } from '@shared/models/mobile-app.models';
import { PlatformType } from '@shared/models/oauth2.models';

/**
 * Angular injectable service: mobile app (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class MobileAppService {

  constructor(
    private http: HttpClient
  ) {
  }

  
  /**
   * POST/PUT entity — save mobile app.
   *
   * @param mobileApp mobile app (MobileApp)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<MobileApp> observable or value
   */


  public saveMobileApp(mobileApp: MobileApp, config?: RequestConfig): Observable<MobileApp> {
    return this.http.post<MobileApp>(`/api/mobile/app`, mobileApp, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant mobile app infos.
   *
   * @param pageLink pagination and sort parameters
   * @param platformType platform type (PlatformType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<MobileApp>> observable or value
   */


  public getTenantMobileAppInfos(pageLink: PageLink, platformType?: PlatformType, config?: RequestConfig): Observable<PageData<MobileApp>> {
    let url = `/api/mobile/app${pageLink.toQuery()}`;
    if (platformType) {
      url += `&platformType=${platformType}`
    }
    return this.http.get<PageData<MobileApp>>(url, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get mobile app info by id.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<MobileApp> observable or value
   */


  public getMobileAppInfoById(id: string, config?: RequestConfig): Observable<MobileApp> {
    return this.http.get<MobileApp>(`/api/mobile/app/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete mobile app.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public deleteMobileApp(id: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/mobile/app/${id}`, defaultHttpOptionsFromConfig(config));
  }

  /**
   * POST/PUT entity — save mobile app bundle.
   *
   * @param mobileAppBundle mobile app bundle (MobileAppBundle)
   * @param oauth2ClientIds oauth2client ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public saveMobileAppBundle(mobileAppBundle: MobileAppBundle, oauth2ClientIds?: Array<string>, config?: RequestConfig) {
    let url = '/api/mobile/bundle';
    if (oauth2ClientIds?.length) {
      url += `?oauth2ClientIds=${oauth2ClientIds.join(',')}`;
    }
    return this.http.post<MobileAppBundle>(url, mobileAppBundle, defaultHttpOptionsFromConfig(config));
  }

  /**
   * update oauth2clients.
   *
   * @param id id (string)
   * @param oauth2ClientIds oauth2client ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public updateOauth2Clients(id: string, oauth2ClientIds: Array<string>, config?: RequestConfig) {
    return this.http.put(`/api/mobile/bundle/${id}/oauth2Clients`, oauth2ClientIds ?? [], defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant mobile app bundle infos.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<MobileAppBundleInfo>> observable or value
   */


  public getTenantMobileAppBundleInfos(pageLink: PageLink, config?: RequestConfig): Observable<PageData<MobileAppBundleInfo>> {
    return this.http.get<PageData<MobileAppBundleInfo>>(`/api/mobile/bundle/infos${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get mobile app bundle info by id.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<MobileAppBundleInfo> observable or value
   */


  public getMobileAppBundleInfoById(id: string, config?: RequestConfig): Observable<MobileAppBundleInfo> {
    return this.http.get<MobileAppBundleInfo>(`/api/mobile/bundle/info/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete mobile app bundle.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public deleteMobileAppBundle(id: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/mobile/bundle/${id}`, defaultHttpOptionsFromConfig(config));
  }

}
