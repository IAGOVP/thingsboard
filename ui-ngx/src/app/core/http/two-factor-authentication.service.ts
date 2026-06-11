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
import { HttpClient } from '@angular/common/http';
import { defaultHttpOptionsFromConfig, RequestConfig } from '@core/http/http-utils';
import { Observable } from 'rxjs';
import {
  AccountTwoFaSettings,
  TwoFactorAuthAccountConfig,
  TwoFactorAuthProviderType,
  TwoFactorAuthSettings
} from '@shared/models/two-factor-auth.models';
import { isDefinedAndNotNull } from '@core/utils';

/**
 * Angular injectable service: two factor authentication (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class TwoFactorAuthenticationService {

  constructor(
    private http: HttpClient
  ) {
  }

  /**
   * get two fa settings.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<TwoFactorAuthSettings> observable or value
   */

  getTwoFaSettings(config?: RequestConfig): Observable<TwoFactorAuthSettings> {
    return this.http.get<TwoFactorAuthSettings>(`/api/2fa/settings`, defaultHttpOptionsFromConfig(config));
  }

  /**
   * POST/PUT entity — save two fa settings.
   *
   * @param settings settings (TwoFactorAuthSettings)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<TwoFactorAuthSettings> observable or value
   */

  saveTwoFaSettings(settings: TwoFactorAuthSettings, config?: RequestConfig): Observable<TwoFactorAuthSettings> {
    return this.http.post<TwoFactorAuthSettings>(`/api/2fa/settings`, settings, defaultHttpOptionsFromConfig(config));
  }

  /**
   * get available two fa providers.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<TwoFactorAuthProviderType>> observable or value
   */

  getAvailableTwoFaProviders(config?: RequestConfig): Observable<Array<TwoFactorAuthProviderType>> {
    return this.http.get<Array<TwoFactorAuthProviderType>>(`/api/2fa/providers`, defaultHttpOptionsFromConfig(config));
  }

  /**
   * generate two fa account config.
   *
   * @param providerType provider type (TwoFactorAuthProviderType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<TwoFactorAuthAccountConfig> observable or value
   */

  generateTwoFaAccountConfig(providerType: TwoFactorAuthProviderType, config?: RequestConfig): Observable<TwoFactorAuthAccountConfig> {
    return this.http.post<TwoFactorAuthAccountConfig>(`/api/2fa/account/config/generate?providerType=${providerType}`,
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * get account two fa settings.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AccountTwoFaSettings> observable or value
   */

  getAccountTwoFaSettings(config?: RequestConfig): Observable<AccountTwoFaSettings> {
    return this.http.get<AccountTwoFaSettings>(`/api/2fa/account/settings`, defaultHttpOptionsFromConfig(config));
  }

  /**
   * update two fa account config.
   *
   * @param providerType provider type (TwoFactorAuthProviderType)
   * @param useByDefault use by default (boolean)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AccountTwoFaSettings> observable or value
   */

  updateTwoFaAccountConfig(providerType: TwoFactorAuthProviderType, useByDefault: boolean,
                           config?: RequestConfig): Observable<AccountTwoFaSettings> {
    return this.http.put<AccountTwoFaSettings>(`/api/2fa/account/config?providerType=${providerType}`, {useByDefault},
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * submit two fa account config.
   *
   * @param authConfig auth config (TwoFactorAuthAccountConfig)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<any> observable or value
   */

  submitTwoFaAccountConfig(authConfig: TwoFactorAuthAccountConfig, config?: RequestConfig): Observable<any> {
    return this.http.post(`/api/2fa/account/config/submit`, authConfig, defaultHttpOptionsFromConfig(config));
  }

  /**
   * verify and save two fa account config.
   *
   * @param authConfig auth config (TwoFactorAuthAccountConfig)
   * @param verificationCode verification code (number)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AccountTwoFaSettings> observable or value
   */

  verifyAndSaveTwoFaAccountConfig(authConfig: TwoFactorAuthAccountConfig, verificationCode?: number,
                                  config?: RequestConfig): Observable<AccountTwoFaSettings> {
    let url = '/api/2fa/account/config';
    if (isDefinedAndNotNull(verificationCode)) {
      url += `?verificationCode=${verificationCode}`;
    }
    return this.http.post<AccountTwoFaSettings>(url, authConfig, defaultHttpOptionsFromConfig(config));
  }

  /**
   * DELETE — delete two fa account config.
   *
   * @param providerType provider type (TwoFactorAuthProviderType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AccountTwoFaSettings> observable or value
   */

  deleteTwoFaAccountConfig(providerType: TwoFactorAuthProviderType, config?: RequestConfig): Observable<AccountTwoFaSettings> {
    return this.http.delete<AccountTwoFaSettings>(`/api/2fa/account/config?providerType=${providerType}`,
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * request two fa verification code send.
   *
   * @param providerType provider type (TwoFactorAuthProviderType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  requestTwoFaVerificationCodeSend(providerType: TwoFactorAuthProviderType, config?: RequestConfig) {
    return this.http.post(`/api/auth/2fa/verification/send?providerType=${providerType}`, defaultHttpOptionsFromConfig(config));
  }

}
