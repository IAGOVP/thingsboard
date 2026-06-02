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
 * Angular HTTP service: edge REST wrappers (`@core/http`).
 */
@Injectable({
  providedIn: 'root'
})
export class EdgeService {

  constructor(
    private http: HttpClient
  ) { }

  /** Calls ThingsBoard REST `/api/edges?edgeIds=${edgeIds.join(, ...`. */

  public getEdges(edgeIds: Array<string>, config?: RequestConfig): Observable<Array<Edge>> {
    return this.http.get<Array<Edge>>(`/api/edges?edgeIds=${edgeIds.join(',')}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge/${edgeId}, ...`. */

  public getEdge(edgeId: string, config?: RequestConfig): Observable<Edge> {
    return this.http.get<Edge>(`/api/edge/${edgeId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge/info/${edgeId}, ...`. */

  public getEdgeInfo(edgeId: string, config?: RequestConfig): Observable<EdgeInfo> {
    return this.http.get<EdgeInfo>(`/api/edge/info/${edgeId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge, ...`. */

  public saveEdge(edge: Edge, config?: RequestConfig): Observable<Edge> {
    return this.http.post<Edge>('/api/edge', edge, defaultHttpOptionsFromConfig(config));
  }

  public deleteEdge(edgeId: string, config?: RequestConfig) {
    return this.http.delete(`/api/edge/${edgeId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge/types, ...`. */

  public getEdgeTypes(config?: RequestConfig): Observable<Array<EntitySubtype>> {
    return this.http.get<Array<EntitySubtype>>('/api/edge/types', defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/customer/${customerId}/edgeInfos${pageLink.toQuery()}&type=${type}, ...`. */

  public getCustomerEdgeInfos(customerId: string, pageLink: PageLink, type: string = '',
                              config?: RequestConfig): Observable<PageData<EdgeInfo>> {
    return this.http.get<PageData<EdgeInfo>>(`/api/customer/${customerId}/edgeInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/customer/${customerId}/edge/${edgeId}, ...`. */

  public assignEdgeToCustomer(customerId: string, edgeId: string,
                              config?: RequestConfig): Observable<Edge> {
    return this.http.post<Edge>(`/api/customer/${customerId}/edge/${edgeId}`,
      defaultHttpOptionsFromConfig(config));
  }

  public unassignEdgeFromCustomer(edgeId: string, config?: RequestConfig) {
    return this.http.delete(`/api/customer/edge/${edgeId}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/customer/public/edge/${edgeId}, ...`. */

  public makeEdgePublic(edgeId: string, config?: RequestConfig): Observable<Edge> {
    return this.http.post<Edge>(`/api/customer/public/edge/${edgeId}`, null,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/tenant/edgeInfos${pageLink.toQuery()}&type=${type}, ...`. */

  public getTenantEdgeInfos(pageLink: PageLink, type: string = '',
                            config?: RequestConfig): Observable<PageData<EdgeInfo>> {
    return this.http.get<PageData<EdgeInfo>>(`/api/tenant/edgeInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edges, ...`. */

  public findByQuery(query: EdgeSearchQuery, config?: RequestConfig): Observable<Array<Edge>> {
    return this.http.post<Array<Edge>>('/api/edges', query,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge/${entityId.id}/events, ...`. */

  public getEdgeEvents(entityId: EntityId, pageLink: TimePageLink,
                       config?: RequestConfig): Observable<PageData<EdgeEvent>> {
    return this.http.get<PageData<EdgeEvent>>(`/api/edge/${entityId.id}/events` + `${pageLink.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  public syncEdge(edgeId: string, config?: RequestConfig) {
    return this.http.post(`/api/edge/sync/${edgeId}`, edgeId, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge/missingToRelatedRuleChains/${edgeId}, ...`. */

  public findMissingToRelatedRuleChains(edgeId: string, config?: RequestConfig): Observable<string> {
    return this.http.get<string>(`/api/edge/missingToRelatedRuleChains/${edgeId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/tenant/edges?edgeName=${edgeName}, ...`. */

  public findByName(edgeName: string, config?: RequestConfig): Observable<Edge> {
    return this.http.get<Edge>(`/api/tenant/edges?edgeName=${edgeName}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge/bulk_import, ...`. */

  public bulkImportEdges(entitiesData: BulkImportRequest, config?: RequestConfig): Observable<BulkImportResult> {
    return this.http.post<BulkImportResult>('/api/edge/bulk_import', entitiesData, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge/instructions/install/${edgeId}/${method}, ...`. */

  public getEdgeInstallInstructions(edgeId: string, method: string = 'ubuntu', config?: RequestConfig): Observable<EdgeInstructions> {
    return this.http.get<EdgeInstructions>(`/api/edge/instructions/install/${edgeId}/${method}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge/instructions/upgrade/${edgeVersion}/${method}, ...`. */

  public getEdgeUpgradeInstructions(edgeVersion: string, method: string = 'ubuntu', config?: RequestConfig): Observable<EdgeInstructions> {
    return this.http.get<EdgeInstructions>(`/api/edge/instructions/upgrade/${edgeVersion}/${method}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge/${edgeId}/upgrade/available`. */

  public isEdgeUpgradeAvailable(edgeId: string, config?: RequestConfig): Observable<boolean> {
    return this.http.get<boolean>(`/api/edge/${edgeId}/upgrade/available`, defaultHttpOptionsFromConfig(config));
  }
}
