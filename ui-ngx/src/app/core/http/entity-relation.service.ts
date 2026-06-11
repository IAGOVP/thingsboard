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
import { EntityRelation, EntityRelationInfo, EntityRelationsQuery } from '@shared/models/relation.models';
import { EntityId } from '@app/shared/models/id/entity-id';

/**
 * Angular injectable service: entity relation (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class EntityRelationService {

  constructor(
    private http: HttpClient
  ) { }

  
  /**
   * POST/PUT entity — save relation.
   *
   * @param relation relation (EntityRelation)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EntityRelation> observable or value
   */


  public saveRelation(relation: EntityRelation, config?: RequestConfig): Observable<EntityRelation> {
    return this.http.post<EntityRelation>('/api/relation', relation, defaultHttpOptionsFromConfig(config));
  }

  /**
   * DELETE — delete relation.
   *
   * @param fromId from id (EntityId)
   * @param relationType relation type (string)
   * @param toId to id (EntityId)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteRelation(fromId: EntityId, relationType: string, toId: EntityId,
                        config?: RequestConfig) {
    return this.http.delete(`/api/relation?fromId=${fromId.id}&fromType=${fromId.entityType}` +
      `&relationType=${relationType}&toId=${toId.id}&toType=${toId.entityType}`,
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * DELETE — delete relations.
   *
   * @param entityId entity UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteRelations(entityId: EntityId,
                         config?: RequestConfig) {
    return this.http.delete(`/api/relations?entityId=${entityId.id}&entityType=${entityId.entityType}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get relation.
   *
   * @param fromId from id (EntityId)
   * @param relationType relation type (string)
   * @param toId to id (EntityId)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<EntityRelation> observable or value
   */


  public getRelation(fromId: EntityId, relationType: string, toId: EntityId,
                     config?: RequestConfig): Observable<EntityRelation> {
    return this.http.get<EntityRelation>(`/api/relation?fromId=${fromId.id}&fromType=${fromId.entityType}` +
      `&relationType=${relationType}&toId=${toId.id}&toType=${toId.entityType}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by from.
   *
   * @param fromId from id (EntityId)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityRelation>> observable or value
   */


  public findByFrom(fromId: EntityId,
                    config?: RequestConfig): Observable<Array<EntityRelation>> {
    return this.http.get<Array<EntityRelation>>(
      `/api/relations?fromId=${fromId.id}&fromType=${fromId.entityType}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find info by from.
   *
   * @param fromId from id (EntityId)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityRelationInfo>> observable or value
   */


  public findInfoByFrom(fromId: EntityId,
                        config?: RequestConfig): Observable<Array<EntityRelationInfo>> {
    return this.http.get<Array<EntityRelationInfo>>(
      `/api/relations/info?fromId=${fromId.id}&fromType=${fromId.entityType}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by from and type.
   *
   * @param fromId from id (EntityId)
   * @param relationType relation type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityRelation>> observable or value
   */


  public findByFromAndType(fromId: EntityId, relationType: string,
                           config?: RequestConfig): Observable<Array<EntityRelation>> {
    return this.http.get<Array<EntityRelation>>(
      `/api/relations?fromId=${fromId.id}&fromType=${fromId.entityType}&relationType=${relationType}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by to.
   *
   * @param toId to id (EntityId)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityRelation>> observable or value
   */


  public findByTo(toId: EntityId,
                  config?: RequestConfig): Observable<Array<EntityRelation>> {
    return this.http.get<Array<EntityRelation>>(
      `/api/relations?toId=${toId.id}&toType=${toId.entityType}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find info by to.
   *
   * @param toId to id (EntityId)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityRelationInfo>> observable or value
   */


  public findInfoByTo(toId: EntityId,
                      config?: RequestConfig): Observable<Array<EntityRelationInfo>> {
    return this.http.get<Array<EntityRelationInfo>>(
      `/api/relations/info?toId=${toId.id}&toType=${toId.entityType}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by to and type.
   *
   * @param toId to id (EntityId)
   * @param relationType relation type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityRelation>> observable or value
   */


  public findByToAndType(toId: EntityId, relationType: string,
                         config?: RequestConfig): Observable<Array<EntityRelation>> {
    return this.http.get<Array<EntityRelation>>(
      `/api/relations?toId=${toId.id}&toType=${toId.entityType}&relationType=${relationType}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by query.
   *
   * @param query query (EntityRelationsQuery)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityRelation>> observable or value
   */


  public findByQuery(query: EntityRelationsQuery,
                     config?: RequestConfig): Observable<Array<EntityRelation>> {
    return this.http.post<Array<EntityRelation>>(
      '/api/relations', query,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find info by query.
   *
   * @param query query (EntityRelationsQuery)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntityRelationInfo>> observable or value
   */


  public findInfoByQuery(query: EntityRelationsQuery,
                         config?: RequestConfig): Observable<Array<EntityRelationInfo>> {
    return this.http.post<Array<EntityRelationInfo>>(
      '/api/relations/info', query,
      defaultHttpOptionsFromConfig(config));
  }

}
