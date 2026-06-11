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
import { ActivationLinkInfo, User, UserEmailInfo } from '@shared/models/user.model';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { isDefined } from '@core/utils';
import { InterceptorHttpParams } from '@core/interceptors/interceptor-http-params';

/**
 * User administration, activation links, and credentials.
 *
 * <p>REST base: `/api/user*`, `/api/users*`.
 */
@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private http: HttpClient
  ) { }

  
  /**
   * get users.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<User>> observable or value
   */


  public getUsers(pageLink: PageLink,
                  config?: RequestConfig): Observable<PageData<User>> {
    return this.http.get<PageData<User>>(`/api/users${pageLink.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant admins.
   *
   * @param tenantId tenant UUID
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<User>> observable or value
   */


  public getTenantAdmins(tenantId: string, pageLink: PageLink,
                         config?: RequestConfig): Observable<PageData<User>> {
    return this.http.get<PageData<User>>(`/api/tenant/${tenantId}/users${pageLink.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get customer users.
   *
   * @param customerId customer UUID
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<User>> observable or value
   */


  public getCustomerUsers(customerId: string, pageLink: PageLink,
                          config?: RequestConfig): Observable<PageData<User>> {
    return this.http.get<PageData<User>>(`/api/customer/${customerId}/users${pageLink.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get users for assign.
   *
   * @param alarmId alarm UUID
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<UserEmailInfo>> observable or value
   */


  public getUsersForAssign(alarmId: string, pageLink: PageLink,
                          config?: RequestConfig): Observable<PageData<UserEmailInfo>> {
    return this.http.get<PageData<UserEmailInfo>>(`/api/users/assign/${alarmId}${pageLink.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get user.
   *
   * @param userId user UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<User> observable or value
   */


  public getUser(userId: string, config?: RequestConfig): Observable<User> {
    return this.http.get<User>(`/api/user/${userId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get users by ids.
   *
   * @param userIds user ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<User>> observable or value
   */


  public getUsersByIds(userIds: Array<string>, config?: RequestConfig): Observable<Array<User>> {
    return this.http.get<Array<User>>(`/api/users?userIds=${userIds.join(',')}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save user.
   *
   * @param user user (User)
   * @param sendActivationMail send activation mail (boolean)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<User> observable or value
   */


  public saveUser(user: User, sendActivationMail: boolean = false,
                  config?: RequestConfig): Observable<User> {
    let url = '/api/user';
    url += '?sendActivationMail=' + sendActivationMail;
    return this.http.post<User>(url, user, defaultHttpOptionsFromConfig(config));
  }

  /**
   * DELETE — delete user.
   *
   * @param userId user UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteUser(userId: string, config?: RequestConfig) {
    return this.http.delete(`/api/user/${userId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get activation link.
   *
   * @param userId user UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<string> observable or value
   */


  public getActivationLink(userId: string, config?: RequestConfig): Observable<string> {
    return this.http.get(`/api/user/${userId}/activationLink`,
      {...{responseType: 'text'}, ...defaultHttpOptionsFromConfig(config)});
  }

  
  /**
   * get activation link info.
   *
   * @param userId user UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ActivationLinkInfo> observable or value
   */


  public getActivationLinkInfo(userId: string, config?: RequestConfig): Observable<ActivationLinkInfo> {
    return this.http.get<ActivationLinkInfo>(`/api/user/${userId}/activationLinkInfo`, defaultHttpOptionsFromConfig(config));
  }

  /**
   * send activation email.
   *
   * @param email email (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public sendActivationEmail(email: string, config?: RequestConfig) {
    const encodeEmail = encodeURIComponent(email);
    return this.http.post(`/api/user/sendActivationMail?email=${encodeEmail}`, null, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * set user credentials enabled.
   *
   * @param userId user UUID
   * @param userCredentialsEnabled user credentials enabled (boolean)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<any> observable or value
   */


  public setUserCredentialsEnabled(userId: string, userCredentialsEnabled?: boolean, config?: RequestConfig): Observable<any> {
    let url = `/api/user/${userId}/userCredentialsEnabled`;
    if (isDefined(userCredentialsEnabled)) {
      url += `?userCredentialsEnabled=${userCredentialsEnabled}`;
    }
    return this.http.post<User>(url, null, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find users by query.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<UserEmailInfo>> observable or value
   */


  public findUsersByQuery(pageLink: PageLink, config?: RequestConfig) : Observable<PageData<UserEmailInfo>> {
    return this.http.get<PageData<UserEmailInfo>>(`/api/users/info${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

}
