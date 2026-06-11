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
 * Angular injectable service: admin (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
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

  
  /**
   * send test mail.
   *
   * @param adminSettings admin settings (AdminSettings<MailServerSettings>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public sendTestMail(adminSettings: AdminSettings<MailServerSettings>,
                      config?: RequestConfig): Observable<void> {
    return this.http.post<void>('/api/admin/settings/testMail', adminSettings, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * send test sms.
   *
   * @param testSmsRequest test sms request (TestSmsRequest)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public sendTestSms(testSmsRequest: TestSmsRequest,
                     config?: RequestConfig): Observable<void> {
    return this.http.post<void>('/api/admin/settings/testSms', testSmsRequest, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get security settings.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<SecuritySettings> observable or value
   */


  public getSecuritySettings(config?: RequestConfig): Observable<SecuritySettings> {
    return this.http.get<SecuritySettings>(`/api/admin/securitySettings`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save security settings.
   *
   * @param securitySettings security settings (SecuritySettings)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<SecuritySettings> observable or value
   */


  public saveSecuritySettings(securitySettings: SecuritySettings,
                              config?: RequestConfig): Observable<SecuritySettings> {
    return this.http.post<SecuritySettings>('/api/admin/securitySettings', securitySettings,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get jwt settings.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<JwtSettings> observable or value
   */


  public getJwtSettings(config?: RequestConfig): Observable<JwtSettings> {
    return this.http.get<JwtSettings>(`/api/admin/jwtSettings`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save jwt settings.
   *
   * @param jwtSettings jwt settings (JwtSettings)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<LoginResponse> observable or value
   */


  public saveJwtSettings(jwtSettings: JwtSettings, config?: RequestConfig): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/admin/jwtSettings', jwtSettings, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get repository settings.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<RepositorySettings> observable or value
   */


  public getRepositorySettings(config?: RequestConfig): Observable<RepositorySettings> {
    return this.http.get<RepositorySettings>(`/api/admin/repositorySettings`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save repository settings.
   *
   * @param repositorySettings repository settings (RepositorySettings)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<RepositorySettings> observable or value
   */


  public saveRepositorySettings(repositorySettings: RepositorySettings,
                                config?: RequestConfig): Observable<RepositorySettings> {
    return this.http.post<RepositorySettings>('/api/admin/repositorySettings', repositorySettings,
      defaultHttpOptionsFromConfig(config)).pipe(
      tap(() => {
        this.entitiesVersionControlService.clearBranchList();
      })
    );
  }

  /**
   * DELETE — delete repository settings.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteRepositorySettings(config?: RequestConfig) {
    return this.http.delete('/api/admin/repositorySettings', defaultHttpOptionsFromConfig(config)).pipe(
      tap(() => {
        this.entitiesVersionControlService.clearBranchList();
      })
    );
  }

  
  /**
   * check repository access.
   *
   * @param repositorySettings repository settings (RepositorySettings)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public checkRepositoryAccess(repositorySettings: RepositorySettings,
                               config?: RequestConfig): Observable<void> {
    return this.http.post<void>('/api/admin/repositorySettings/checkAccess', repositorySettings, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get repository settings info.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<RepositorySettingsInfo> observable or value
   */


  public getRepositorySettingsInfo(config?: RequestConfig): Observable<RepositorySettingsInfo> {
    return this.http.get<RepositorySettingsInfo>('/api/admin/repositorySettings/info', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get auto commit settings.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AutoCommitSettings> observable or value
   */


  public getAutoCommitSettings(config?: RequestConfig): Observable<AutoCommitSettings> {
    return this.http.get<AutoCommitSettings>(`/api/admin/autoCommitSettings`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * auto commit settings exists.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<boolean> observable or value
   */


  public autoCommitSettingsExists(config?: RequestConfig): Observable<boolean> {
    return this.http.get<boolean>('/api/admin/autoCommitSettings/exists', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save auto commit settings.
   *
   * @param autoCommitSettings auto commit settings (AutoCommitSettings)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AutoCommitSettings> observable or value
   */


  public saveAutoCommitSettings(autoCommitSettings: AutoCommitSettings,
                                config?: RequestConfig): Observable<AutoCommitSettings> {
    return this.http.post<AutoCommitSettings>('/api/admin/autoCommitSettings', autoCommitSettings, defaultHttpOptionsFromConfig(config));
  }

  /**
   * DELETE — delete auto commit settings.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteAutoCommitSettings(config?: RequestConfig) {
    return this.http.delete('/api/admin/autoCommitSettings', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * check updates.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<UpdateMessage> observable or value
   */


  public checkUpdates(config?: RequestConfig): Observable<UpdateMessage> {
    return this.http.get<UpdateMessage>(`/api/admin/updates`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get features info.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<FeaturesInfo> observable or value
   */


  public getFeaturesInfo(config?: RequestConfig): Observable<FeaturesInfo> {
    return this.http.get<FeaturesInfo>('/api/admin/featuresInfo', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * GET /api/admin/mail/oauth2/loginProcessingUrl — fetch login processing url.
   *
   * REST endpoint(s): `/api/admin/mail/oauth2/loginProcessingUrl`
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<string> observable or value
   */


  public getLoginProcessingUrl(config?: RequestConfig): Observable<string> {
    return this.http.get<string>(`/api/admin/mail/oauth2/loginProcessingUrl`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * generate access token.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<string> observable or value
   */


  public generateAccessToken(config?: RequestConfig): Observable<string> {
    return this.http.get<string>(`/api/admin/mail/oauth2/authorize`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get mail config template.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<MailConfigTemplate>> observable or value
   */


  public getMailConfigTemplate(config?: RequestConfig): Observable<Array<MailConfigTemplate>> {
    return this.http.get<Array<MailConfigTemplate>>('/api/mail/config/template', defaultHttpOptionsFromConfig(config));
  }
}
