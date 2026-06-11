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

import { Inject, Injectable } from '@angular/core';
import { defaultHttpOptions, defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { Dashboard, DashboardInfo, HomeDashboard, HomeDashboardInfo } from '@shared/models/dashboard.models';
import { WINDOW } from '@core/services/window.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter, map, publishReplay, refCount } from 'rxjs/operators';

// @dynamic
/**
 * Dashboard CRUD, customer assignment, and import/export.
 *
 * <p>REST base: `/api/dashboard*`, `/api/customer/{id}/dashboard*`.
 */
@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  stDiffObservable: Observable<number>;
  currentUrl: string;

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(WINDOW) private window: Window
  ) {
    this.currentUrl = this.router.url.split('?')[0];
    this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(
      () => {
        const newUrl = this.router.url.split('?')[0];
        if (this.currentUrl !== newUrl) {
          this.stDiffObservable = null;
          this.currentUrl = newUrl;
        }
      }
    );
  }

  
  /**
   * get tenant dashboards.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<DashboardInfo>> observable or value
   */


  public getTenantDashboards(pageLink: PageLink, config?: RequestConfig): Observable<PageData<DashboardInfo>> {
    return this.http.get<PageData<DashboardInfo>>(`/api/tenant/dashboards${pageLink.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant dashboards by tenant id.
   *
   * @param tenantId tenant UUID
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<DashboardInfo>> observable or value
   */


  public getTenantDashboardsByTenantId(tenantId: string, pageLink: PageLink,
                                       config?: RequestConfig): Observable<PageData<DashboardInfo>> {
    return this.http.get<PageData<DashboardInfo>>(`/api/tenant/${tenantId}/dashboards${pageLink.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get customer dashboards.
   *
   * @param customerId customer UUID
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<DashboardInfo>> observable or value
   */


  public getCustomerDashboards(customerId: string, pageLink: PageLink, config?: RequestConfig): Observable<PageData<DashboardInfo>> {
    return this.http.get<PageData<DashboardInfo>>(`/api/customer/${customerId}/dashboards${pageLink.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get dashboard.
   *
   * @param dashboardId dashboard UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Dashboard> observable or value
   */


  public getDashboard(dashboardId: string, config?: RequestConfig): Observable<Dashboard> {
    return this.http.get<Dashboard>(`/api/dashboard/${dashboardId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * export dashboard.
   *
   * @param dashboardId dashboard UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Dashboard> observable or value
   */


  public exportDashboard(dashboardId: string, includeResources = true, config?: RequestConfig): Observable<Dashboard> {
    let url = `/api/dashboard/${dashboardId}`;
    if (includeResources) {
      url += '?includeResources=true';
    }
    return this.http.get<Dashboard>(url, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get dashboard info.
   *
   * @param dashboardId dashboard UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<DashboardInfo> observable or value
   */


  public getDashboardInfo(dashboardId: string, config?: RequestConfig): Observable<DashboardInfo> {
    return this.http.get<DashboardInfo>(`/api/dashboard/info/${dashboardId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get dashboards.
   *
   * @param dashboardIds dashboard ids (string[])
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<DashboardInfo>> observable or value
   */


  public getDashboards(dashboardIds: string[], config?: RequestConfig): Observable<Array<DashboardInfo>> {
    return this.http.get<Array<DashboardInfo>>(`/api/dashboards?dashboardIds=${dashboardIds.join(',')}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save dashboard.
   *
   * @param dashboard dashboard (Dashboard)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Dashboard> observable or value
   */


  public saveDashboard(dashboard: Dashboard, config?: RequestConfig): Observable<Dashboard> {
    return this.http.post<Dashboard>('/api/dashboard', dashboard, defaultHttpOptionsFromConfig(config));
  }

  /**
   * DELETE — delete dashboard.
   *
   * @param dashboardId dashboard UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteDashboard(dashboardId: string, config?: RequestConfig) {
    return this.http.delete(`/api/dashboard/${dashboardId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * assign dashboard to customer.
   *
   * @param customerId customer UUID
   * @param dashboardId dashboard UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Dashboard> observable or value
   */


  public assignDashboardToCustomer(customerId: string, dashboardId: string,
                                   config?: RequestConfig): Observable<Dashboard> {
    return this.http.post<Dashboard>(`/api/customer/${customerId}/dashboard/${dashboardId}`,
      null, defaultHttpOptionsFromConfig(config));
  }

  /**
   * unassign dashboard from customer.
   *
   * @param customerId customer UUID
   * @param dashboardId dashboard UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public unassignDashboardFromCustomer(customerId: string, dashboardId: string,
                                       config?: RequestConfig) {
    return this.http.delete(`/api/customer/${customerId}/dashboard/${dashboardId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * make dashboard public.
   *
   * @param dashboardId dashboard UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Dashboard> observable or value
   */


  public makeDashboardPublic(dashboardId: string, config?: RequestConfig): Observable<Dashboard> {
    return this.http.post<Dashboard>(`/api/customer/public/dashboard/${dashboardId}`, null,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * make dashboard private.
   *
   * @param dashboardId dashboard UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Dashboard> observable or value
   */


  public makeDashboardPrivate(dashboardId: string, config?: RequestConfig): Observable<Dashboard> {
    return this.http.delete<Dashboard>(`/api/customer/public/dashboard/${dashboardId}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * update dashboard customers.
   *
   * @param dashboardId dashboard UUID
   * @param customerIds customer ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Dashboard> observable or value
   */


  public updateDashboardCustomers(dashboardId: string, customerIds: Array<string>,
                                  config?: RequestConfig): Observable<Dashboard> {
    return this.http.post<Dashboard>(`/api/dashboard/${dashboardId}/customers`, customerIds,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — add dashboard customers.
   *
   * @param dashboardId dashboard UUID
   * @param customerIds customer ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Dashboard> observable or value
   */


  public addDashboardCustomers(dashboardId: string, customerIds: Array<string>,
                               config?: RequestConfig): Observable<Dashboard> {
    return this.http.post<Dashboard>(`/api/dashboard/${dashboardId}/customers/add`, customerIds,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — remove dashboard customers.
   *
   * @param dashboardId dashboard UUID
   * @param customerIds customer ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Dashboard> observable or value
   */


  public removeDashboardCustomers(dashboardId: string, customerIds: Array<string>,
                                  config?: RequestConfig): Observable<Dashboard> {
    return this.http.post<Dashboard>(`/api/dashboard/${dashboardId}/customers/remove`, customerIds,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get home dashboard.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<HomeDashboard> observable or value
   */


  public getHomeDashboard(config?: RequestConfig): Observable<HomeDashboard> {
    return this.http.get<HomeDashboard>('/api/dashboard/home', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant home dashboard info.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<HomeDashboardInfo> observable or value
   */


  public getTenantHomeDashboardInfo(config?: RequestConfig): Observable<HomeDashboardInfo> {
    return this.http.get<HomeDashboardInfo>('/api/tenant/dashboard/home/info', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * set tenant home dashboard info.
   *
   * @param homeDashboardInfo home dashboard info (HomeDashboardInfo)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<any> observable or value
   */


  public setTenantHomeDashboardInfo(homeDashboardInfo: HomeDashboardInfo, config?: RequestConfig): Observable<any> {
    return this.http.post<any>('/api/tenant/dashboard/home/info', homeDashboardInfo,
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * get public dashboard link.
   *
   * @param dashboard dashboard (DashboardInfo)
   * @returns string | null observable or value
   */

  public getPublicDashboardLink(dashboard: DashboardInfo): string | null {
    if (dashboard && dashboard.assignedCustomers && dashboard.assignedCustomers.length > 0) {
      const publicCustomers = dashboard.assignedCustomers
        .filter(customerInfo => customerInfo.public);
      if (publicCustomers.length > 0) {
        const publicCustomerId = publicCustomers[0].customerId.id;
        let url = this.window.location.protocol + '//' + this.window.location.hostname;
        const port = this.window.location.port;
        if (port && port.length > 0 && port !== '80' && port !== '443') {
          url += ':' + port;
        }
        url += `/dashboard/${dashboard.id.id}?publicId=${publicCustomerId}`;
        return url;
      }
    }
    return null;
  }

  
  /**
   * get server time diff.
   *
   * @returns Observable<number> observable or value
   */


  public getServerTimeDiff(): Observable<number> {
    if (!this.stDiffObservable) {
      const url = '/api/dashboard/serverTime';
      const ct1 = Date.now();
      this.stDiffObservable = this.http.get<number>(url, defaultHttpOptions(true)).pipe(
        map((st) => {
          const ct2 = Date.now();
          const stDiff = Math.ceil(st - (ct1 + ct2) / 2);
          return stDiff;
        }),
        publishReplay(1),
        refCount()
      );
    }
    return this.stDiffObservable;
  }

  
  /**
   * get edge dashboards.
   *
   * @param edgeId edge id (string)
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<DashboardInfo>> observable or value
   */


  public getEdgeDashboards(edgeId: string, pageLink: PageLink, type: string = '',
                           config?: RequestConfig): Observable<PageData<DashboardInfo>> {
    return this.http.get<PageData<DashboardInfo>>(`/api/edge/${edgeId}/dashboards${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config))
  }

  
  /**
   * assign dashboard to edge.
   *
   * @param edgeId edge id (string)
   * @param dashboardId dashboard UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Dashboard> observable or value
   */


  public assignDashboardToEdge(edgeId: string, dashboardId: string,
                               config?: RequestConfig): Observable<Dashboard> {
    return this.http.post<Dashboard>(`/api/edge/${edgeId}/dashboard/${dashboardId}`, null,
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * unassign dashboard from edge.
   *
   * @param edgeId edge id (string)
   * @param dashboardId dashboard UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public unassignDashboardFromEdge(edgeId: string, dashboardId: string,
                                   config?: RequestConfig) {
    return this.http.delete(`/api/edge/${edgeId}/dashboard/${dashboardId}`,
      defaultHttpOptionsFromConfig(config));
  }

}
