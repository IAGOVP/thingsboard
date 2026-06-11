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
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { Tenant, TenantInfo } from '@shared/models/tenant.model';

/**
 * Tenant CRUD and tenant admin user management.
 *
 * <p>REST base: `/api/tenant*`.
 */
@Injectable({
  providedIn: 'root'
})
export class TenantService {

  constructor(
    private http: HttpClient
  ) { }

  
  /**
   * get tenants.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<Tenant>> observable or value
   */


  public getTenants(pageLink: PageLink, config?: RequestConfig): Observable<PageData<Tenant>> {
    return this.http.get<PageData<Tenant>>(`/api/tenants${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenants by ids.
   *
   * @param tenantIds tenant ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<Tenant>> observable or value
   */


  public getTenantsByIds(tenantIds: Array<string>, config?: RequestConfig): Observable<Array<Tenant>> {
    return this.http.get<Array<Tenant>>(`/api/tenants?tenantIds=${tenantIds.join(',')}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant infos.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<TenantInfo>> observable or value
   */


  public getTenantInfos(pageLink: PageLink, config?: RequestConfig): Observable<PageData<TenantInfo>> {
    return this.http.get<PageData<TenantInfo>>(`/api/tenantInfos${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant.
   *
   * @param tenantId tenant UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Tenant> observable or value
   */


  public getTenant(tenantId: string, config?: RequestConfig): Observable<Tenant> {
    return this.http.get<Tenant>(`/api/tenant/${tenantId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant info.
   *
   * @param tenantId tenant UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<TenantInfo> observable or value
   */


  public getTenantInfo(tenantId: string, config?: RequestConfig): Observable<TenantInfo> {
    return this.http.get<TenantInfo>(`/api/tenant/info/${tenantId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save tenant.
   *
   * @param tenant tenant (Tenant)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Tenant> observable or value
   */


  public saveTenant(tenant: Tenant, config?: RequestConfig): Observable<Tenant> {
    return this.http.post<Tenant>('/api/tenant', tenant, defaultHttpOptionsFromConfig(config));
  }

  /**
   * DELETE — delete tenant.
   *
   * @param tenantId tenant UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteTenant(tenantId: string, config?: RequestConfig) {
    return this.http.delete(`/api/tenant/${tenantId}`, defaultHttpOptionsFromConfig(config));
  }

}
