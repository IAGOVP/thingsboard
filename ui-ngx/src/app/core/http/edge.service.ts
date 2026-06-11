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
import { PageLink, TimePageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { EntitySubtype } from '@app/shared/models/entity-type.models';
import { Edge, EdgeEvent, EdgeInfo, EdgeInstructions, EdgeSearchQuery } from '@shared/models/edge.models';
import { EntityId } from '@shared/models/id/entity-id';
import { BulkImportRequest, BulkImportResult } from '@shared/import-export/import-export.models';

/**
 * Angular injectable service: edge (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class EdgeService {

  constructor(
    private http: HttpClient
  ) { }

  
  /**
   * get edges.
   *
   * @param edgeIds edge ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<Edge>> observable or value
   */


  public getEdges(edgeIds: Array<string>, config?: RequestConfig): Observable<Array<Edge>> {
    return this.http.get<Array<Edge>>(`/api/edges?edgeIds=${edgeIds.join(',')}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get edge.
   *
   * @param edgeId edge id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Edge> observable or value
   */


  public getEdge(edgeId: string, config?: RequestConfig): Observable<Edge> {
    return this.http.get<Edge>(`/api/edge/${edgeId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get edge info.
   *
   * @param edgeId edge id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EdgeInfo> observable or value
   */


  public getEdgeInfo(edgeId: string, config?: RequestConfig): Observable<EdgeInfo> {
    return this.http.get<EdgeInfo>(`/api/edge/info/${edgeId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save edge.
   *
   * @param edge edge (Edge)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Edge> observable or value
   */


  public saveEdge(edge: Edge, config?: RequestConfig): Observable<Edge> {
    return this.http.post<Edge>('/api/edge', edge, defaultHttpOptionsFromConfig(config));
  }

  /**
   * DELETE — delete edge.
   *
   * @param edgeId edge id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteEdge(edgeId: string, config?: RequestConfig) {
    return this.http.delete(`/api/edge/${edgeId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get edge types.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntitySubtype>> observable or value
   */


  public getEdgeTypes(config?: RequestConfig): Observable<Array<EntitySubtype>> {
    return this.http.get<Array<EntitySubtype>>('/api/edge/types', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get customer edge infos.
   *
   * @param customerId customer UUID
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<EdgeInfo>> observable or value
   */


  public getCustomerEdgeInfos(customerId: string, pageLink: PageLink, type: string = '',
                              config?: RequestConfig): Observable<PageData<EdgeInfo>> {
    return this.http.get<PageData<EdgeInfo>>(`/api/customer/${customerId}/edgeInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * assign edge to customer.
   *
   * @param customerId customer UUID
   * @param edgeId edge id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Edge> observable or value
   */


  public assignEdgeToCustomer(customerId: string, edgeId: string,
                              config?: RequestConfig): Observable<Edge> {
    return this.http.post<Edge>(`/api/customer/${customerId}/edge/${edgeId}`,
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * unassign edge from customer.
   *
   * @param edgeId edge id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public unassignEdgeFromCustomer(edgeId: string, config?: RequestConfig) {
    return this.http.delete(`/api/customer/edge/${edgeId}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * make edge public.
   *
   * @param edgeId edge id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Edge> observable or value
   */


  public makeEdgePublic(edgeId: string, config?: RequestConfig): Observable<Edge> {
    return this.http.post<Edge>(`/api/customer/public/edge/${edgeId}`, null,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant edge infos.
   *
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<EdgeInfo>> observable or value
   */


  public getTenantEdgeInfos(pageLink: PageLink, type: string = '',
                            config?: RequestConfig): Observable<PageData<EdgeInfo>> {
    return this.http.get<PageData<EdgeInfo>>(`/api/tenant/edgeInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by query.
   *
   * @param query query (EdgeSearchQuery)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<Edge>> observable or value
   */


  public findByQuery(query: EdgeSearchQuery, config?: RequestConfig): Observable<Array<Edge>> {
    return this.http.post<Array<Edge>>('/api/edges', query,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get edge events.
   *
   * @param entityId entity UUID
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<EdgeEvent>> observable or value
   */


  public getEdgeEvents(entityId: EntityId, pageLink: TimePageLink,
                       config?: RequestConfig): Observable<PageData<EdgeEvent>> {
    return this.http.get<PageData<EdgeEvent>>(`/api/edge/${entityId.id}/events` + `${pageLink.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * sync edge.
   *
   * @param edgeId edge id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public syncEdge(edgeId: string, config?: RequestConfig) {
    return this.http.post(`/api/edge/sync/${edgeId}`, edgeId, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find missing to related rule chains.
   *
   * @param edgeId edge id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<string> observable or value
   */


  public findMissingToRelatedRuleChains(edgeId: string, config?: RequestConfig): Observable<string> {
    return this.http.get<string>(`/api/edge/missingToRelatedRuleChains/${edgeId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by name.
   *
   * @param edgeName edge name (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Edge> observable or value
   */


  public findByName(edgeName: string, config?: RequestConfig): Observable<Edge> {
    return this.http.get<Edge>(`/api/tenant/edges?edgeName=${edgeName}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * bulk import edges.
   *
   * @param entitiesData entities data (BulkImportRequest)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<BulkImportResult> observable or value
   */


  public bulkImportEdges(entitiesData: BulkImportRequest, config?: RequestConfig): Observable<BulkImportResult> {
    return this.http.post<BulkImportResult>('/api/edge/bulk_import', entitiesData, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get edge install instructions.
   *
   * @param edgeId edge id (string)
   * @param method method (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EdgeInstructions> observable or value
   */


  public getEdgeInstallInstructions(edgeId: string, method: string = 'ubuntu', config?: RequestConfig): Observable<EdgeInstructions> {
    return this.http.get<EdgeInstructions>(`/api/edge/instructions/install/${edgeId}/${method}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get edge upgrade instructions.
   *
   * @param edgeVersion edge version (string)
   * @param method method (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EdgeInstructions> observable or value
   */


  public getEdgeUpgradeInstructions(edgeVersion: string, method: string = 'ubuntu', config?: RequestConfig): Observable<EdgeInstructions> {
    return this.http.get<EdgeInstructions>(`/api/edge/instructions/upgrade/${edgeVersion}/${method}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * is edge upgrade available.
   *
   * @param edgeId edge id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<boolean> observable or value
   */


  public isEdgeUpgradeAvailable(edgeId: string, config?: RequestConfig): Observable<boolean> {
    return this.http.get<boolean>(`/api/edge/${edgeId}/upgrade/available`, defaultHttpOptionsFromConfig(config));
  }
}
