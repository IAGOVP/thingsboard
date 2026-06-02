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
import {
  AdminSettings,
  AutoCommitSettings,
  FeaturesInfo,
  JwtSettings,
  MailConfigTemplate,
  MailServerSettings,
  RepositorySettings,
  RepositorySettingsInfo,
  SecuritySettings,
  TestSmsRequest,
  UpdateMessage
} from '@shared/models/settings.models';
import { EntitiesVersionControlService } from '@core/http/entities-version-control.service';
import { tap } from 'rxjs/operators';
import { LoginResponse } from '@shared/models/login.models';

/**
 * Angular HTTP service: admin REST wrappers (`@core/http`).
 */
@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(
    private http: HttpClient,
    private entitiesVersionControlService: EntitiesVersionControlService
  ) { }

  public getAdminSettings<T>(key: string, config?: RequestConfig): Observable<AdminSettings<T>> {
    return this.http.get<AdminSettings<T>>(`/api/admin/settings/${key}`, defaultHttpOptionsFromConfig(config));
  }

  public saveAdminSettings<T>(adminSettings: AdminSettings<T>,
                              config?: RequestConfig): Observable<AdminSettings<T>> {
    return this.http.post<AdminSettings<T>>('/api/admin/settings', adminSettings, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/settings/testMail, ...`. */

  public sendTestMail(adminSettings: AdminSettings<MailServerSettings>,
                      config?: RequestConfig): Observable<void> {
    return this.http.post<void>('/api/admin/settings/testMail', adminSettings, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/settings/testSms, ...`. */

  public sendTestSms(testSmsRequest: TestSmsRequest,
                     config?: RequestConfig): Observable<void> {
    return this.http.post<void>('/api/admin/settings/testSms', testSmsRequest, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/securitySettings, ...`. */

  public getSecuritySettings(config?: RequestConfig): Observable<SecuritySettings> {
    return this.http.get<SecuritySettings>(`/api/admin/securitySettings`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/securitySettings, ...`. */

  public saveSecuritySettings(securitySettings: SecuritySettings,
                              config?: RequestConfig): Observable<SecuritySettings> {
    return this.http.post<SecuritySettings>('/api/admin/securitySettings', securitySettings,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/jwtSettings, ...`. */

  public getJwtSettings(config?: RequestConfig): Observable<JwtSettings> {
    return this.http.get<JwtSettings>(`/api/admin/jwtSettings`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/jwtSettings, ...`. */

  public saveJwtSettings(jwtSettings: JwtSettings, config?: RequestConfig): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/admin/jwtSettings', jwtSettings, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/repositorySettings, ...`. */

  public getRepositorySettings(config?: RequestConfig): Observable<RepositorySettings> {
    return this.http.get<RepositorySettings>(`/api/admin/repositorySettings`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/repositorySettings, ...`. */

  public saveRepositorySettings(repositorySettings: RepositorySettings,
                                config?: RequestConfig): Observable<RepositorySettings> {
    return this.http.post<RepositorySettings>('/api/admin/repositorySettings', repositorySettings,
      defaultHttpOptionsFromConfig(config)).pipe(
      tap(() => {
        this.entitiesVersionControlService.clearBranchList();
      })
    );
  }

  public deleteRepositorySettings(config?: RequestConfig) {
    return this.http.delete('/api/admin/repositorySettings', defaultHttpOptionsFromConfig(config)).pipe(
      tap(() => {
        this.entitiesVersionControlService.clearBranchList();
      })
    );
  }

  /** Calls ThingsBoard REST `/api/admin/repositorySettings/checkAccess, ...`. */

  public checkRepositoryAccess(repositorySettings: RepositorySettings,
                               config?: RequestConfig): Observable<void> {
    return this.http.post<void>('/api/admin/repositorySettings/checkAccess', repositorySettings, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/repositorySettings/info, ...`. */

  public getRepositorySettingsInfo(config?: RequestConfig): Observable<RepositorySettingsInfo> {
    return this.http.get<RepositorySettingsInfo>('/api/admin/repositorySettings/info', defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/autoCommitSettings, ...`. */

  public getAutoCommitSettings(config?: RequestConfig): Observable<AutoCommitSettings> {
    return this.http.get<AutoCommitSettings>(`/api/admin/autoCommitSettings`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/autoCommitSettings/exists, ...`. */

  public autoCommitSettingsExists(config?: RequestConfig): Observable<boolean> {
    return this.http.get<boolean>('/api/admin/autoCommitSettings/exists', defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/autoCommitSettings, ...`. */

  public saveAutoCommitSettings(autoCommitSettings: AutoCommitSettings,
                                config?: RequestConfig): Observable<AutoCommitSettings> {
    return this.http.post<AutoCommitSettings>('/api/admin/autoCommitSettings', autoCommitSettings, defaultHttpOptionsFromConfig(config));
  }

  public deleteAutoCommitSettings(config?: RequestConfig) {
    return this.http.delete('/api/admin/autoCommitSettings', defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/updates, ...`. */

  public checkUpdates(config?: RequestConfig): Observable<UpdateMessage> {
    return this.http.get<UpdateMessage>(`/api/admin/updates`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/featuresInfo, ...`. */

  public getFeaturesInfo(config?: RequestConfig): Observable<FeaturesInfo> {
    return this.http.get<FeaturesInfo>('/api/admin/featuresInfo', defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/mail/oauth2/loginProcessingUrl, ...`. */

  public getLoginProcessingUrl(config?: RequestConfig): Observable<string> {
    return this.http.get<string>(`/api/admin/mail/oauth2/loginProcessingUrl`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/admin/mail/oauth2/authorize, ...`. */

  public generateAccessToken(config?: RequestConfig): Observable<string> {
    return this.http.get<string>(`/api/admin/mail/oauth2/authorize`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/mail/config/template`. */

  public getMailConfigTemplate(config?: RequestConfig): Observable<Array<MailConfigTemplate>> {
    return this.http.get<Array<MailConfigTemplate>>('/api/mail/config/template', defaultHttpOptionsFromConfig(config));
  }
}
