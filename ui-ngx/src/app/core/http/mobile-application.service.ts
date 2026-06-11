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

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from '@core/http/http-utils';
import { Observable } from 'rxjs';
import { QrCodeSettings } from '@shared/models/mobile-app.models';

/**
 * Angular injectable service: mobile application (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class MobileApplicationService {

  constructor(
    private http: HttpClient
  ) {}

  
  /**
   * get mobile app settings.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<QrCodeSettings> observable or value
   */


  public getMobileAppSettings(config?: RequestConfig): Observable<QrCodeSettings> {
    return this.http.get<QrCodeSettings>(`/api/mobile/qr/settings`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save mobile app settings.
   *
   * @param mobileAppSettings mobile app settings (QrCodeSettings)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<QrCodeSettings> observable or value
   */


  public saveMobileAppSettings(mobileAppSettings: QrCodeSettings, config?: RequestConfig): Observable<QrCodeSettings> {
    return this.http.post<QrCodeSettings>(`/api/mobile/qr/settings`, mobileAppSettings, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get mobile app deep link.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<string> observable or value
   */


  public getMobileAppDeepLink(config?: RequestConfig): Observable<string> {
    return this.http.get<string>(`/api/mobile/qr/deepLink`, defaultHttpOptionsFromConfig(config));
  }

}
