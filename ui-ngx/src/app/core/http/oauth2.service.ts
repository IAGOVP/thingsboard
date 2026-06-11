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
import { OAuth2Client, OAuth2ClientInfo, OAuth2ClientRegistrationTemplate } from '@shared/models/oauth2.models';
import { PageData } from '@shared/models/page/page-data';
import { PageLink } from '@shared/models/page/page-link';

/**
 * Angular injectable service: oauth2 (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class OAuth2Service {

  constructor(
    private http: HttpClient
  ) {
  }

  
  /**
   * get oauth2template.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<OAuth2ClientRegistrationTemplate>> observable or value
   */


  public getOAuth2Template(config?: RequestConfig): Observable<Array<OAuth2ClientRegistrationTemplate>> {
    return this.http.get<Array<OAuth2ClientRegistrationTemplate>>(`/api/oauth2/config/template`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save oauth2client.
   *
   * @param oAuth2Client o auth2client (OAuth2Client)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<OAuth2Client> observable or value
   */


  public saveOAuth2Client(oAuth2Client: OAuth2Client, config?: RequestConfig): Observable<OAuth2Client> {
    return this.http.post<OAuth2Client>('/api/oauth2/client', oAuth2Client, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find tenant oauth2client infos.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<OAuth2ClientInfo>> observable or value
   */


  public findTenantOAuth2ClientInfos(pageLink: PageLink, config?: RequestConfig): Observable<PageData<OAuth2ClientInfo>> {
    return this.http.get<PageData<OAuth2ClientInfo>>(`/api/oauth2/client/infos${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find tenant oauth2client infos by ids.
   *
   * @param clientIds client ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<OAuth2ClientInfo>> observable or value
   */


  public findTenantOAuth2ClientInfosByIds(clientIds: Array<string>, config?: RequestConfig): Observable<Array<OAuth2ClientInfo>> {
    return this.http.get<Array<OAuth2ClientInfo>>(`/api/oauth2/client/infos?clientIds=${clientIds.join(',')}`, defaultHttpOptionsFromConfig(config))
  }

  
  /**
   * get oauth2client by id.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<OAuth2Client> observable or value
   */


  public getOAuth2ClientById(id: string, config?: RequestConfig): Observable<OAuth2Client> {
    return this.http.get<OAuth2Client>(`/api/oauth2/client/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete oauth2client.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public deleteOauth2Client(id: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/oauth2/client/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get login processing url.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<string> observable or value
   */


  public getLoginProcessingUrl(config?: RequestConfig): Observable<string> {
    return this.http.get<string>('/api/oauth2/loginProcessingUrl', defaultHttpOptionsFromConfig(config));
  }

}
