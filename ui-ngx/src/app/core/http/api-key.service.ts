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
import { ApiKeyInfo, ApiKey } from '@shared/models/api-key.models';

/**
 * Angular injectable service: api key (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class ApiKeyService {

  constructor(
    private http: HttpClient
  ) {
  }

  
  /**
   * POST/PUT entity — save api key.
   *
   * @param apiKey api key (ApiKeyInfo)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ApiKey> observable or value
   */


  public saveApiKey(apiKey: ApiKeyInfo, config?: RequestConfig): Observable<ApiKey> {
    return this.http.post<ApiKey>('/api/apiKey', apiKey, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete api key.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public deleteApiKey(id: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/apiKey/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * update api key description.
   *
   * @param id id (string)
   * @param description description (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ApiKeyInfo> observable or value
   */


  public updateApiKeyDescription(id: string, description: string, config?: RequestConfig): Observable<ApiKeyInfo> {
    return this.http.put<ApiKeyInfo>(`/api/apiKey/${id}/description`, description, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * enable api key.
   *
   * @param id id (string)
   * @param enabledValue enabled value (boolean)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ApiKeyInfo> observable or value
   */


  public enableApiKey(id: string, enabledValue: boolean, config?: RequestConfig): Observable<ApiKeyInfo> {
    return this.http.put<ApiKeyInfo>(`/api/apiKey/${id}/enabled/${enabledValue}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get user api keys.
   *
   * @param userId user UUID
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<ApiKeyInfo>> observable or value
   */


  public getUserApiKeys(userId: string, pageLink: PageLink, config?: RequestConfig): Observable<PageData<ApiKeyInfo>> {
    return this.http.get<PageData<ApiKeyInfo>>(`/api/apiKeys/${userId}${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }
}
