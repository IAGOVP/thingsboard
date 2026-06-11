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
import { AlarmComment, AlarmCommentInfo } from '@shared/models/alarm.models';

/**
 * Angular injectable service: alarm comment (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class AlarmCommentService {

  constructor(
    private http: HttpClient
  ) { }

  
  /**
   * POST/PUT entity — save alarm comment.
   *
   * @param alarmId alarm UUID
   * @param alarmComment alarm comment (AlarmComment)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AlarmComment> observable or value
   */


  public saveAlarmComment(alarmId: string, alarmComment: AlarmComment, config?: RequestConfig): Observable<AlarmComment> {
    return this.http.post<AlarmComment>(`/api/alarm/${alarmId}/comment`, alarmComment, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get alarm comments.
   *
   * @param alarmId alarm UUID
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AlarmCommentInfo>> observable or value
   */


  public getAlarmComments(alarmId: string, pageLink: PageLink, config?: RequestConfig): Observable<PageData<AlarmCommentInfo>> {
    return this.http.get<PageData<AlarmComment>>(`/api/alarm/${alarmId}/comment${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete alarm comments.
   *
   * @param alarmId alarm UUID
   * @param commentId comment id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public deleteAlarmComments(alarmId: string, commentId: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/alarm/${alarmId}/comment/${commentId}`, defaultHttpOptionsFromConfig(config));
  }

}
