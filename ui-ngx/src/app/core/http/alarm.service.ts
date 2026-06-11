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
import { PageData } from '@shared/models/page/page-data';
import { EntityId } from '@shared/models/id/entity-id';
import {
  Alarm,
  AlarmInfo,
  AlarmQuery, AlarmQueryV2,
  AlarmSearchStatus,
  AlarmSeverity,
  AlarmStatus
} from '@shared/models/alarm.models';
import { EntitySubtype } from '@shared/models/entity-type.models';
import { PageLink } from '@shared/models/page/page-link';

/**
 * Alarm search, acknowledge, clear, and assign.
 *
 * <p>REST base: `/api/alarm*`, `/api/v2/alarm*`.
 */
@Injectable({
  providedIn: 'root'
})
export class AlarmService {

  constructor(
    private http: HttpClient
  ) { }

  
  /**
   * get alarm.
   *
   * @param alarmId alarm UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Alarm> observable or value
   */


  public getAlarm(alarmId: string, config?: RequestConfig): Observable<Alarm> {
    return this.http.get<Alarm>(`/api/alarm/${alarmId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get alarm info.
   *
   * @param alarmId alarm UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AlarmInfo> observable or value
   */


  public getAlarmInfo(alarmId: string, config?: RequestConfig): Observable<AlarmInfo> {
    return this.http.get<AlarmInfo>(`/api/alarm/info/${alarmId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save alarm.
   *
   * @param alarm alarm (Alarm)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Alarm> observable or value
   */


  public saveAlarm(alarm: Alarm, config?: RequestConfig): Observable<Alarm> {
    return this.http.post<Alarm>('/api/alarm', alarm, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * ack alarm.
   *
   * @param alarmId alarm UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AlarmInfo> observable or value
   */


  public ackAlarm(alarmId: string, config?: RequestConfig): Observable<AlarmInfo> {
    return this.http.post<AlarmInfo>(`/api/alarm/${alarmId}/ack`, null, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * clear alarm.
   *
   * @param alarmId alarm UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AlarmInfo> observable or value
   */


  public clearAlarm(alarmId: string, config?: RequestConfig): Observable<AlarmInfo> {
    return this.http.post<AlarmInfo>(`/api/alarm/${alarmId}/clear`, null, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * assign alarm.
   *
   * @param alarmId alarm UUID
   * @param assigneeId assignee id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public assignAlarm(alarmId: string, assigneeId: string, config?: RequestConfig): Observable<void> {
    return this.http.post<void>(`/api/alarm/${alarmId}/assign/${assigneeId}`, null, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * unassign alarm.
   *
   * @param alarmId alarm UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public unassignAlarm(alarmId: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/alarm/${alarmId}/assign`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete alarm.
   *
   * @param alarmId alarm UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<boolean> observable or value
   */


  public deleteAlarm(alarmId: string, config?: RequestConfig): Observable<boolean> {
    return this.http.delete<boolean>(`/api/alarm/${alarmId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get alarms.
   *
   * @param query query (AlarmQuery)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AlarmInfo>> observable or value
   */


  public getAlarms(query: AlarmQuery,
                   config?: RequestConfig): Observable<PageData<AlarmInfo>> {
    return this.http.get<PageData<AlarmInfo>>(`/api/alarm${query.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get alarms v2.
   *
   * @param query query (AlarmQueryV2)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AlarmInfo>> observable or value
   */


  public getAlarmsV2(query: AlarmQueryV2,
                     config?: RequestConfig): Observable<PageData<AlarmInfo>> {
    return this.http.get<PageData<AlarmInfo>>(`/api/v2/alarm${query.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get all alarms.
   *
   * @param query query (AlarmQuery)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AlarmInfo>> observable or value
   */


  public getAllAlarms(query: AlarmQuery,
                      config?: RequestConfig): Observable<PageData<AlarmInfo>> {
    return this.http.get<PageData<AlarmInfo>>(`/api/alarms${query.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get all alarms v2.
   *
   * @param query query (AlarmQueryV2)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AlarmInfo>> observable or value
   */


  public getAllAlarmsV2(query: AlarmQueryV2,
      config?: RequestConfig): Observable<PageData<AlarmInfo>> {
      return this.http.get<PageData<AlarmInfo>>(`/api/v2/alarms${query.toQuery()}`,
        defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get highest alarm severity.
   *
   * @param entityId entity UUID
   * @param alarmSearchStatus alarm search status (AlarmSearchStatus)
   * @param alarmStatus alarm status (AlarmStatus)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AlarmSeverity> observable or value
   */


  public getHighestAlarmSeverity(entityId: EntityId, alarmSearchStatus: AlarmSearchStatus, alarmStatus: AlarmStatus,
                                 config?: RequestConfig): Observable<AlarmSeverity> {
    let url = `/api/alarm/highestSeverity/${entityId.entityType}/${entityId.id}`;
    if (alarmSearchStatus) {
      url += `?searchStatus=${alarmSearchStatus}`;
    } else if (alarmStatus) {
      url += `?status=${alarmStatus}`;
    }
    return this.http.get<AlarmSeverity>(url,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get alarm types.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<EntitySubtype>> observable or value
   */


  public getAlarmTypes(pageLink: PageLink, config?: RequestConfig): Observable<PageData<EntitySubtype>> {
    return this.http.get<PageData<EntitySubtype>>(`/api/alarm/types${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

}
