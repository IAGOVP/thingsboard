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
import { defaultHttpOptionsFromConfig, defaultHttpOptionsFromParams, RequestConfig } from './http-utils';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PageData } from '@shared/models/page/page-data';
import {
  CalculatedField,
  CalculatedFieldInfo,
  CalculatedFieldsQuery,
  CalculatedFieldTestScriptInputParams,
  CalculatedFieldType
} from '@shared/models/calculated-field.models';
import { PageLink } from '@shared/models/page/page-link';
import { EntityId } from '@shared/models/id/entity-id';
import { EntityTestScriptResult } from '@shared/models/entity.models';
import { CalculatedFieldEventBody } from '@shared/models/event.models';

/**
 * Angular HTTP service: calculated fields REST wrappers (`@core/http`).
 */
@Injectable({
  providedIn: 'root'
})
export class CalculatedFieldsService {

  constructor(
    private http: HttpClient
  ) { }

  /** Calls ThingsBoard REST `/api/calculatedField/${calculatedFieldId}, ...`. */

  public getCalculatedFieldById(calculatedFieldId: string, config?: RequestConfig): Observable<CalculatedField> {
    return this.http.get<CalculatedField>(`/api/calculatedField/${calculatedFieldId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/calculatedField, ...`. */

  public saveCalculatedField(calculatedField: CalculatedField, config?: RequestConfig): Observable<CalculatedField> {
    return this.http.post<CalculatedField>('/api/calculatedField', calculatedField, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/calculatedField/${calculatedFieldId}, ...`. */

  public deleteCalculatedField(calculatedFieldId: string, config?: RequestConfig): Observable<boolean> {
    return this.http.delete<boolean>(`/api/calculatedField/${calculatedFieldId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/calculatedFields${pageLink.toQuery()}, ...`. */

  public getCalculatedFields(pageLink: PageLink, query: CalculatedFieldsQuery, config?: RequestConfig): Observable<PageData<CalculatedFieldInfo>> {
    return this.http.get<PageData<CalculatedFieldInfo>>(`/api/calculatedFields${pageLink.toQuery()}`, defaultHttpOptionsFromParams(query, config));
  }

  /** Calls ThingsBoard REST `/api/${entityType}/${id}/calculatedFields${pageLink.toQuery()}, ...`. */

  public getCalculatedFieldsByEntityId({ entityType, id }: EntityId, pageLink: PageLink, type?: CalculatedFieldType, config?: RequestConfig): Observable<PageData<CalculatedField>> {
    return this.http.get<PageData<CalculatedField>>(`/api/${entityType}/${id}/calculatedFields${pageLink.toQuery()}`, defaultHttpOptionsFromParams({type} , config));
  }

  /** Calls ThingsBoard REST `/api/calculatedField/testScript, ...`. */

  public testScript(inputParams: CalculatedFieldTestScriptInputParams, config?: RequestConfig): Observable<EntityTestScriptResult> {
    return this.http.post<EntityTestScriptResult>('/api/calculatedField/testScript', inputParams, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/calculatedField/${id}/debug, ...`. */

  public getLatestCalculatedFieldDebugEvent(id: string, config?: RequestConfig): Observable<CalculatedFieldEventBody> {
    return this.http.get<CalculatedFieldEventBody>(`/api/calculatedField/${id}/debug`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/calculatedFields/names${pageLink.toQuery()}`. */

  public getCalculatedFieldNames(pageLink: PageLink, type: CalculatedFieldType, config?: RequestConfig): Observable<PageData<string>> {
    return this.http.get<PageData<string>>(`/api/calculatedFields/names${pageLink.toQuery()}`, defaultHttpOptionsFromParams({type}, config));
  }
}
