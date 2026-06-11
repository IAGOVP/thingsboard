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
import { createDefaultHttpOptions, RequestConfig } from './http-utils';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { TimePageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { AuditLog, AuditLogFilter } from '@shared/models/audit-log.models';
import { EntityId } from '@shared/models/id/entity-id';

/**
 * Angular injectable service: audit log (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class AuditLogService {

  constructor(
    private http: HttpClient
  ) { }

  /** Calls ThingsBoard REST `/api/audit/logs${pageLink.toQuery()}, ...`. */

  public getAuditLogs(pageLink: TimePageLink, config?: RequestConfig): Observable<PageData<AuditLog>>;
  public getAuditLogs(pageLink: TimePageLink, filters: AuditLogFilter, config?: RequestConfig): Observable<PageData<AuditLog>>;
  /**
   * get audit logs.
   *
   * @param pageLink pagination and sort parameters
   * @param filtersOrConfig filters or config (AuditLogFilter | RequestConfig)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AuditLog>> observable or value
   */
  public getAuditLogs(
    pageLink: TimePageLink,
    filtersOrConfig?: AuditLogFilter | RequestConfig,
    config?: RequestConfig
  ): Observable<PageData<AuditLog>> {
    return this.http.get<PageData<AuditLog>>(
      `/api/audit/logs${pageLink.toQuery()}`,
      createDefaultHttpOptions(filtersOrConfig, config)
    );
  }

  /** Calls ThingsBoard REST `/api/audit/logs/customer/${customerId}${pageLink.toQuery()}, ...`. */

  public getAuditLogsByCustomerId(customerId: string, pageLink: TimePageLink, config?: RequestConfig): Observable<PageData<AuditLog>>;
  public getAuditLogsByCustomerId(customerId: string, pageLink: TimePageLink, filters: AuditLogFilter, config?: RequestConfig): Observable<PageData<AuditLog>>;
  /**
   * get audit logs by customer id.
   *
   * @param customerId customer UUID
   * @param pageLink pagination and sort parameters
   * @param filtersOrConfig filters or config (AuditLogFilter | RequestConfig)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AuditLog>> observable or value
   */
  public getAuditLogsByCustomerId(
    customerId: string,
    pageLink: TimePageLink,
    filtersOrConfig?: AuditLogFilter | RequestConfig,
    config?: RequestConfig
  ): Observable<PageData<AuditLog>> {
    return this.http.get<PageData<AuditLog>>(
      `/api/audit/logs/customer/${customerId}${pageLink.toQuery()}`,
      createDefaultHttpOptions(filtersOrConfig, config)
    );
  }

  /** Calls ThingsBoard REST `/api/audit/logs/user/${userId}${pageLink.toQuery()}, ...`. */

  public getAuditLogsByUserId(userId: string, pageLink: TimePageLink, config?: RequestConfig): Observable<PageData<AuditLog>>;
  public getAuditLogsByUserId(userId: string, pageLink: TimePageLink, filters: AuditLogFilter, config?: RequestConfig): Observable<PageData<AuditLog>>;
  /**
   * get audit logs by user id.
   *
   * @param userId user UUID
   * @param pageLink pagination and sort parameters
   * @param filtersOrConfig filters or config (AuditLogFilter | RequestConfig)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AuditLog>> observable or value
   */
  public getAuditLogsByUserId(
    userId: string,
    pageLink: TimePageLink,
    filtersOrConfig?: AuditLogFilter | RequestConfig,
    config?: RequestConfig
  ): Observable<PageData<AuditLog>> {
    return this.http.get<PageData<AuditLog>>(
      `/api/audit/logs/user/${userId}${pageLink.toQuery()}`,
      createDefaultHttpOptions(filtersOrConfig, config)
    );
  }

  /** Calls ThingsBoard REST `/api/audit/logs/entity/${entityId.entityType}/${entityId.id}${pageLink.toQuery()}`. */

  public getAuditLogsByEntityId(entityId: EntityId, pageLink: TimePageLink, config?: RequestConfig): Observable<PageData<AuditLog>>;
  public getAuditLogsByEntityId(entityId: EntityId, pageLink: TimePageLink, filters: AuditLogFilter, config?: RequestConfig): Observable<PageData<AuditLog>>;
  /**
   * get audit logs by entity id.
   *
   * @param entityId entity UUID
   * @param pageLink pagination and sort parameters
   * @param filtersOrConfig filters or config (AuditLogFilter | RequestConfig)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AuditLog>> observable or value
   */
  public getAuditLogsByEntityId(
    entityId: EntityId,
    pageLink: TimePageLink,
    filtersOrConfig?: AuditLogFilter | RequestConfig,
    config?: RequestConfig
  ): Observable<PageData<AuditLog>> {
    return this.http.get<PageData<AuditLog>>(
      `/api/audit/logs/entity/${entityId.entityType}/${entityId.id}${pageLink.toQuery()}`,
      createDefaultHttpOptions(filtersOrConfig, config)
    );
  }
}
