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
import { createDefaultHttpOptions, defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { catchError, Observable, of, ReplaySubject, throwError, timeout } from 'rxjs';
import { map, switchMap } from "rxjs/operators";
import { HttpClient } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import {
  ClaimRequest,
  ClaimResult,
  Device,
  DeviceCredentials,
  DeviceInfo,
  DeviceInfoQuery,
  DeviceSearchQuery,
  PublishTelemetryCommand,
  SaveDeviceParams
} from '@shared/models/device.models';
import { EntitySubtype } from '@shared/models/entity-type.models';
import { AuthService } from '@core/auth/auth.service';
import { BulkImportRequest, BulkImportResult } from '@shared/import-export/import-export.models';
import { PersistentRpc, RpcStatus } from '@shared/models/rpc.models';
import { ResourcesService } from '@core/services/resources.service';
import { SaveEntityParams } from '@shared/models/entity.models';

/**
 * Angular HTTP service for device CRUD, credentials, claim, bulk import, and RPC.
 *
 * <p>Wraps ThingsBoard REST endpoints under `/api/device*`, `/api/tenant/deviceInfos`, `/api/customer/{id}/device*`, and `/api/rpc/*`.
 */
@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  constructor(
    private http: HttpClient,
    private resourcesService: ResourcesService
  ) { }

  
  /**
   * get device infos by query.
   *
   * @param deviceInfoQuery device info query (DeviceInfoQuery)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<DeviceInfo>> observable or value
   */


  public getDeviceInfosByQuery(deviceInfoQuery: DeviceInfoQuery, config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api${deviceInfoQuery.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant device infos.
   *
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<DeviceInfo>> observable or value
   */


  public getTenantDeviceInfos(pageLink: PageLink, type: string = '',
                              config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api/tenant/deviceInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant device infos by device profile id.
   *
   * @param pageLink pagination and sort parameters
   * @param deviceProfileId device profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<DeviceInfo>> observable or value
   */


  public getTenantDeviceInfosByDeviceProfileId(pageLink: PageLink, deviceProfileId: string = '',
                                               config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api/tenant/deviceInfos${pageLink.toQuery()}&deviceProfileId=${deviceProfileId}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get customer device infos.
   *
   * @param customerId customer UUID
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<DeviceInfo>> observable or value
   */


  public getCustomerDeviceInfos(customerId: string, pageLink: PageLink, type: string = '',
                                config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api/customer/${customerId}/deviceInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get customer device infos by device profile id.
   *
   * @param customerId customer UUID
   * @param pageLink pagination and sort parameters
   * @param deviceProfileId device profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<DeviceInfo>> observable or value
   */


  public getCustomerDeviceInfosByDeviceProfileId(customerId: string, pageLink: PageLink, deviceProfileId: string = '',
                                                 config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api/customer/${customerId}/deviceInfos${pageLink.toQuery()}&deviceProfileId=${deviceProfileId}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get device.
   *
   * @param deviceId device UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Device> observable or value
   */


  public getDevice(deviceId: string, config?: RequestConfig): Observable<Device> {
    return this.http.get<Device>(`/api/device/${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get devices.
   *
   * @param deviceIds device ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<Device>> observable or value
   */


  public getDevices(deviceIds: Array<string>, config?: RequestConfig): Observable<Array<Device>> {
    return this.http.get<Array<Device>>(`/api/devices?deviceIds=${deviceIds.join(',')}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get device info.
   *
   * @param deviceId device UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<DeviceInfo> observable or value
   */


  public getDeviceInfo(deviceId: string, config?: RequestConfig): Observable<DeviceInfo> {
    return this.http.get<DeviceInfo>(`/api/device/info/${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/device, ...`. */

  public saveDevice(device: Device, config?: RequestConfig): Observable<Device>;
  public saveDevice(device: Device, saveParams?: SaveDeviceParams, config?: RequestConfig): Observable<Device>;
  /**
   * POST/PUT entity — save device.
   *
   * @param device device (Device)
   * @param saveParamsOrConfig save params or config (SaveDeviceParams | RequestConfig)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Device> observable or value
   */
  public saveDevice(device: Device, saveParamsOrConfig?: SaveDeviceParams | RequestConfig, config?: RequestConfig): Observable<Device> {
    return this.http.post<Device>('/api/device', device, createDefaultHttpOptions(saveParamsOrConfig, config));
  }

  /** Calls ThingsBoard REST `/api/device-with-credentials, ...`. */

  public saveDeviceWithCredentials(device: Device, credentials: DeviceCredentials, config?: RequestConfig): Observable<Device>;
  public saveDeviceWithCredentials(device: Device, credentials: DeviceCredentials, saveParams: SaveEntityParams, config?: RequestConfig): Observable<Device>;
  /**
   * POST/PUT entity — save device with credentials.
   *
   * @param device device (Device)
   * @param credentials credentials (DeviceCredentials)
   * @param saveParamsOrConfig save params or config (SaveEntityParams | RequestConfig)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Device> observable or value
   */
  public saveDeviceWithCredentials(device: Device, credentials: DeviceCredentials, saveParamsOrConfig?: SaveEntityParams | RequestConfig, config?: RequestConfig): Observable<Device> {
    return this.http.post<Device>('/api/device-with-credentials', {
      device,
      credentials
    }, createDefaultHttpOptions(saveParamsOrConfig, config));
  }

  /**
   * DELETE — delete device.
   *
   * @param deviceId device UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteDevice(deviceId: string, config?: RequestConfig) {
    return this.http.delete(`/api/device/${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get device types.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntitySubtype>> observable or value
   */


  public getDeviceTypes(config?: RequestConfig): Observable<Array<EntitySubtype>> {
    return this.http.get<Array<EntitySubtype>>('/api/device/types', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get device credentials.
   *
   * @param deviceId device UUID
   * @param sync sync (boolean)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<DeviceCredentials> observable or value
   */


  public getDeviceCredentials(deviceId: string, sync: boolean = false, config?: RequestConfig): Observable<DeviceCredentials> {
    const url = `/api/device/${deviceId}/credentials`;
    if (sync) {
      const responseSubject = new ReplaySubject<DeviceCredentials>();
      const request = new XMLHttpRequest();
      request.open('GET', url, false);
      request.setRequestHeader('Accept', 'application/json, text/plain, */*');
      const jwtToken = AuthService.getJwtToken();
      if (jwtToken) {
        request.setRequestHeader('X-Authorization', 'Bearer ' + jwtToken);
      }
      request.send(null);
      if (request.status === 200) {
        const credentials = JSON.parse(request.responseText) as DeviceCredentials;
        responseSubject.next(credentials);
      } else {
        responseSubject.error(null);
      }
      return responseSubject.asObservable();
    } else {
      return this.http.get<DeviceCredentials>(url, defaultHttpOptionsFromConfig(config));
    }
  }

  
  /**
   * POST/PUT entity — save device credentials.
   *
   * @param deviceCredentials device credentials (DeviceCredentials)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<DeviceCredentials> observable or value
   */


  public saveDeviceCredentials(deviceCredentials: DeviceCredentials, config?: RequestConfig): Observable<DeviceCredentials> {
    return this.http.post<DeviceCredentials>('/api/device/credentials', deviceCredentials, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * make device public.
   *
   * @param deviceId device UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Device> observable or value
   */


  public makeDevicePublic(deviceId: string, config?: RequestConfig): Observable<Device> {
    return this.http.post<Device>(`/api/customer/public/device/${deviceId}`, null, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * assign device to customer.
   *
   * @param customerId customer UUID
   * @param deviceId device UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Device> observable or value
   */


  public assignDeviceToCustomer(customerId: string, deviceId: string,
                                config?: RequestConfig): Observable<Device> {
    return this.http.post<Device>(`/api/customer/${customerId}/device/${deviceId}`, null, defaultHttpOptionsFromConfig(config));
  }

  /**
   * unassign device from customer.
   *
   * @param deviceId device UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public unassignDeviceFromCustomer(deviceId: string, config?: RequestConfig) {
    return this.http.delete(`/api/customer/device/${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * send one way rpc command.
   *
   * @param deviceId device UUID
   * @param requestBody request body (any)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<any> observable or value
   */


  public sendOneWayRpcCommand(deviceId: string, requestBody: any, config?: RequestConfig): Observable<any> {
    return this.http.post<any>(`/api/rpc/oneway/${deviceId}`, requestBody, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * send two way rpc command.
   *
   * @param deviceId device UUID
   * @param requestBody request body (any)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<any> observable or value
   */


  public sendTwoWayRpcCommand(deviceId: string, requestBody: any, config?: RequestConfig): Observable<any> {
    return this.http.post<any>(`/api/rpc/twoway/${deviceId}`, requestBody, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get persisted rpc.
   *
   * @param rpcId rpc id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PersistentRpc> observable or value
   */


  public getPersistedRpc(rpcId: string, fullResponse = false, config?: RequestConfig): Observable<PersistentRpc> {
    return this.http.get<PersistentRpc>(`/api/rpc/persistent/${rpcId}`, defaultHttpOptionsFromConfig(config));
  }

  /**
   * DELETE — delete persisted rpc.
   *
   * @param rpcId rpc id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deletePersistedRpc(rpcId: string, config?: RequestConfig) {
    return this.http.delete<PersistentRpc>(`/api/rpc/persistent/${rpcId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get persisted rpc requests.
   *
   * @param deviceId device UUID
   * @param pageLink pagination and sort parameters
   * @param rpcStatus rpc status (RpcStatus)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<PersistentRpc>> observable or value
   */


  public getPersistedRpcRequests(deviceId: string, pageLink: PageLink,
                                 rpcStatus?: RpcStatus, config?: RequestConfig): Observable<PageData<PersistentRpc>> {
    let url = `/api/rpc/persistent/device/${deviceId}${pageLink.toQuery()}`;
    if (rpcStatus && rpcStatus.length) {
      url += `&rpcStatus=${rpcStatus}`;
    }
    return this.http.get<PageData<PersistentRpc>>(url, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by query.
   *
   * @param query query (DeviceSearchQuery)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<Device>> observable or value
   */


  public findByQuery(query: DeviceSearchQuery,
                     config?: RequestConfig): Observable<Array<Device>> {
    return this.http.post<Array<Device>>('/api/devices', query, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by name.
   *
   * @param deviceName device name (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Device> observable or value
   */


  public findByName(deviceName: string, config?: RequestConfig): Observable<Device> {
    return this.http.get<Device>(`/api/tenant/devices?deviceName=${deviceName}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * claim device.
   *
   * @param deviceName device name (string)
   * @param claimRequest claim request (ClaimRequest)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ClaimResult> observable or value
   */


  public claimDevice(deviceName: string, claimRequest: ClaimRequest,
                     config?: RequestConfig): Observable<ClaimResult> {
    return this.http.post<ClaimResult>(`/api/customer/device/${deviceName}/claim`, claimRequest, defaultHttpOptionsFromConfig(config));
  }

  /**
   * unclaim device.
   *
   * @param deviceName device name (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public unclaimDevice(deviceName: string, config?: RequestConfig) {
    return this.http.delete(`/api/customer/device/${deviceName}/claim`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * assign device to edge.
   *
   * @param edgeId edge id (string)
   * @param deviceId device UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Device> observable or value
   */


  public assignDeviceToEdge(edgeId: string, deviceId: string,
                            config?: RequestConfig): Observable<Device> {
    return this.http.post<Device>(`/api/edge/${edgeId}/device/${deviceId}`,
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * unassign device from edge.
   *
   * @param edgeId edge id (string)
   * @param deviceId device UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public unassignDeviceFromEdge(edgeId: string, deviceId: string,
                                config?: RequestConfig) {
    return this.http.delete(`/api/edge/${edgeId}/device/${deviceId}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get edge devices.
   *
   * @param edgeId edge id (string)
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<DeviceInfo>> observable or value
   */


  public getEdgeDevices(edgeId: string, pageLink: PageLink, type: string = '',
                        config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api/edge/${edgeId}/devices${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * bulk import devices.
   *
   * @param entitiesData entities data (BulkImportRequest)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<BulkImportResult> observable or value
   */


  public bulkImportDevices(entitiesData: BulkImportRequest, config?: RequestConfig): Observable<BulkImportResult> {
    return this.http.post<BulkImportResult>('/api/device/bulk_import', entitiesData, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get device publish telemetry commands.
   *
   * @param deviceId device UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PublishTelemetryCommand> observable or value
   */


  public getDevicePublishTelemetryCommands(deviceId: string, config?: RequestConfig): Observable<PublishTelemetryCommand> {
    return this.http.get<PublishTelemetryCommand>(`/api/device-connectivity/${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * Calls ThingsBoard REST `/api/device-connectivity/gateway-launch/*`.
   *
   * REST endpoint(s): `/api/device-connectivity/gateway-launch/*`
   *
   * @param deviceId device UUID
   * @returns Observable<any> observable or value
   */


  public downloadGatewayDockerComposeFile(deviceId: string): Observable<any> {
    return this.resourcesService.downloadResource(`/api/device-connectivity/gateway-launch/${deviceId}/docker-compose/download`);
  }

  /**
   * reboot device.
   *
   * @param deviceId device UUID
   * @param isBootstrapServer is bootstrap server (boolean)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable< observable or value
   */

  public rebootDevice(deviceId: string, isBootstrapServer: boolean, config?: RequestConfig): Observable<{
    result: string,
    msg: string
  }> {
    const rebootName = isBootstrapServer ? 'Bootstrap-Request Trigger' : 'Registration Update Trigger';
    return this.sendTwoWayRpcCommand(deviceId, {method: 'DiscoverAll'}, config).pipe(
      timeout(10000),
      switchMap((response: any) => {
        if (response.result && response.result.toUpperCase() === 'CONTENT') {
          const resourceId = isBootstrapServer ? 9 : 8;
          const resourcePath = `/1/0/${resourceId}`;
          return this.rebootTrigger(deviceId, resourcePath, config).pipe(
            map((responseReboot: any) => {
              if (responseReboot.result === 'CHANGED') {
                return {
                  result: 'SUCCESS',
                  msg: `<b>"${rebootName}"</b> - Started Successfully.`
                };
              } else {
                return {
                  result: 'ERROR',
                  msg: `<b>"${rebootName}"</b> failed:<pre>${JSON.stringify(responseReboot, null, 2)}</pre>`
                }
              }
            }),
            catchError(err =>
              of({
                result: 'ERROR',
                msg: `<b>"${rebootName}"</b> failed.<br>Error: ${err.message || err}`
              })
            )
          );
        } else {
          return of({
            result: 'ERROR',
            msg: `<b>"${rebootName}"</b> failed.<br>Bad registration device with id = ${deviceId}.<br><b>"DiscoverAll"</b> - RPC result is not "CONTENT"`
          });
        }
      }),
      catchError(err =>
        of({
          result: 'ERROR',
          msg: `<b>"${rebootName}"</b> failed.<br>Bad registration device with id = ${deviceId}.<br>Error: ${err.message || err}`
        })
      )
    );
  }

  /**
   * reboot trigger.
   *
   * @param deviceId device UUID
   * @param resourcePath resource path (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable< observable or value
   */

  private rebootTrigger(deviceId: string, resourcePath: string, config?: RequestConfig): Observable<{ result: string, msg?: string }> {
    return this.sendTwoWayRpcCommand(deviceId, {method: 'Execute', params: {id: resourcePath}}, config).pipe(
      timeout(10000),
      map(res => {
        if (res?.result?.toUpperCase() === 'CHANGED') {
          return {result: 'CHANGED'};
        } else {
          return {
            result: `${res?.result}`,
            msg: `${res?.error}`
          }
        }
      }),
      catchError(err => {
        return throwError(() => err);
      })
    );
  }
}
