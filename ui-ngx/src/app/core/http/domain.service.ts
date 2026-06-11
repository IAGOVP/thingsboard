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
import { Domain, DomainInfo } from '@shared/models/oauth2.models';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';

/**
 * Angular injectable service: domain (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class DomainService {

  constructor(
    private http: HttpClient
  ) {
  }

  
  /**
   * POST/PUT entity — save domain.
   *
   * @param domain domain (Domain)
   * @param oauth2ClientIds oauth2client ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Domain> observable or value
   */


  public saveDomain(domain: Domain, oauth2ClientIds?: Array<string>, config?: RequestConfig): Observable<Domain> {
    let url = '/api/domain';
    if (oauth2ClientIds?.length) {
      url += `?oauth2ClientIds=${oauth2ClientIds.join(',')}`;
    }
    return this.http.post<Domain>(url, domain, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * update oauth2clients.
   *
   * @param id id (string)
   * @param oauth2ClientIds oauth2client ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public updateOauth2Clients(id: string, oauth2ClientIds: Array<string>, config?: RequestConfig): Observable<void> {
    return this.http.put<void>(`/api/domain/${id}/oauth2Clients`, oauth2ClientIds, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant domain infos.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<DomainInfo>> observable or value
   */


  public getTenantDomainInfos(pageLink: PageLink, config?: RequestConfig): Observable<PageData<DomainInfo>> {
    return this.http.get<PageData<DomainInfo>>(`/api/domain/infos${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get domain info by id.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<DomainInfo> observable or value
   */


  public getDomainInfoById(id: string, config?: RequestConfig): Observable<DomainInfo> {
    return this.http.get<DomainInfo>(`/api/domain/info/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete domain.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public deleteDomain(id: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/domain/${id}`, defaultHttpOptionsFromConfig(config));
  }

}
