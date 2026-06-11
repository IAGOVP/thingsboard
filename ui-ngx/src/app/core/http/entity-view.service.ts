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
import { createDefaultHttpOptions, defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { EntitySubtype } from '@app/shared/models/entity-type.models';
import { EntityView, EntityViewInfo, EntityViewSearchQuery } from '@app/shared/models/entity-view.models';
import { SaveEntityParams } from '@shared/models/entity.models';

/**
 * Angular injectable service: entity view (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class EntityViewService {

  constructor(
    private http: HttpClient
  ) { }

  
  /**
   * get tenant entity view infos.
   *
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<EntityViewInfo>> observable or value
   */


  public getTenantEntityViewInfos(pageLink: PageLink, type: string = '', config?: RequestConfig): Observable<PageData<EntityViewInfo>> {
    return this.http.get<PageData<EntityViewInfo>>(`/api/tenant/entityViewInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get customer entity view infos.
   *
   * @param customerId customer UUID
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<EntityViewInfo>> observable or value
   */


  public getCustomerEntityViewInfos(customerId: string, pageLink: PageLink, type: string = '',
                                    config?: RequestConfig): Observable<PageData<EntityViewInfo>> {
    return this.http.get<PageData<EntityViewInfo>>(`/api/customer/${customerId}/entityViewInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get entity view.
   *
   * @param entityViewId entity view id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EntityView> observable or value
   */


  public getEntityView(entityViewId: string, config?: RequestConfig): Observable<EntityView> {
    return this.http.get<EntityView>(`/api/entityView/${entityViewId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get entity views.
   *
   * @param entityViewIds entity view ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityView>> observable or value
   */


  public getEntityViews(entityViewIds: Array<string>, config?: RequestConfig): Observable<Array<EntityView>> {
    return this.http.get<Array<EntityView>>(`/api/entityViews?entityViewIds=${entityViewIds.join(',')}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get entity view info.
   *
   * @param entityViewId entity view id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EntityViewInfo> observable or value
   */


  public getEntityViewInfo(entityViewId: string, config?: RequestConfig): Observable<EntityViewInfo> {
    return this.http.get<EntityViewInfo>(`/api/entityView/info/${entityViewId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/entityView, ...`. */

  public saveEntityView(entityView: EntityView, config?: RequestConfig): Observable<EntityView>;
  public saveEntityView(entityView: EntityView, saveParams: SaveEntityParams, config?: RequestConfig): Observable<EntityView>;
  /**
   * POST/PUT entity — save entity view.
   *
   * @param entityView entity view (EntityView)
   * @param saveParamsOrConfig save params or config (SaveEntityParams | RequestConfig)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EntityView> observable or value
   */
  public saveEntityView(entityView: EntityView, saveParamsOrConfig?: SaveEntityParams | RequestConfig, config?: RequestConfig): Observable<EntityView> {
    return this.http.post<EntityView>('/api/entityView', entityView,  createDefaultHttpOptions(saveParamsOrConfig, config));
  }

  /**
   * DELETE — delete entity view.
   *
   * @param entityViewId entity view id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteEntityView(entityViewId: string, config?: RequestConfig) {
    return this.http.delete(`/api/entityView/${entityViewId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get entity view types.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntitySubtype>> observable or value
   */


  public getEntityViewTypes(config?: RequestConfig): Observable<Array<EntitySubtype>> {
    return this.http.get<Array<EntitySubtype>>('/api/entityView/types', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * make entity view public.
   *
   * @param entityViewId entity view id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EntityView> observable or value
   */


  public makeEntityViewPublic(entityViewId: string, config?: RequestConfig): Observable<EntityView> {
    return this.http.post<EntityView>(`/api/customer/public/entityView/${entityViewId}`, null,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * assign entity view to customer.
   *
   * @param customerId customer UUID
   * @param entityViewId entity view id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EntityView> observable or value
   */


  public assignEntityViewToCustomer(customerId: string, entityViewId: string,
                                    config?: RequestConfig): Observable<EntityView> {
    return this.http.post<EntityView>(`/api/customer/${customerId}/entityView/${entityViewId}`, null,
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * unassign entity view from customer.
   *
   * @param entityViewId entity view id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public unassignEntityViewFromCustomer(entityViewId: string, config?: RequestConfig) {
    return this.http.delete(`/api/customer/entityView/${entityViewId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by query.
   *
   * @param query query (EntityViewSearchQuery)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityView>> observable or value
   */


  public findByQuery(query: EntityViewSearchQuery,
                     config?: RequestConfig): Observable<Array<EntityView>> {
    return this.http.post<Array<EntityView>>('/api/entityViews', query, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * assign entity view to edge.
   *
   * @param edgeId edge id (string)
   * @param entityViewId entity view id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EntityView> observable or value
   */


  public assignEntityViewToEdge(edgeId: string, entityViewId: string, config?: RequestConfig): Observable<EntityView> {
    return this.http.post<EntityView>(`/api/edge/${edgeId}/entityView/${entityViewId}`, null,
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * unassign entity view from edge.
   *
   * @param edgeId edge id (string)
   * @param entityViewId entity view id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public unassignEntityViewFromEdge(edgeId: string, entityViewId: string,
                                    config?: RequestConfig) {
    return this.http.delete(`/api/edge/${edgeId}/entityView/${entityViewId}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get edge entity views.
   *
   * @param edgeId edge id (string)
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<EntityViewInfo>> observable or value
   */


  public getEdgeEntityViews(edgeId: string, pageLink: PageLink, type: string = '',
                            config?: RequestConfig): Observable<PageData<EntityViewInfo>> {
    return this.http.get<PageData<EntityViewInfo>>(`/api/edge/${edgeId}/entityViews${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config))
  }

}
