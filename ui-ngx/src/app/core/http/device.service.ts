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
 * HTTP client for device CRUD, credentials, claim, and RPC under `/api/.../device*`.
 */
@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  constructor(
    private http: HttpClient,
    private resourcesService: ResourcesService
  ) { }

  /** Calls ThingsBoard REST `/api/tenant/deviceInfos${pageLink.toQuery()}&type=${type}, ...`. */

  public getDeviceInfosByQuery(deviceInfoQuery: DeviceInfoQuery, config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api${deviceInfoQuery.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/tenant/deviceInfos${pageLink.toQuery()}&type=${type}, ...`. */

  public getTenantDeviceInfos(pageLink: PageLink, type: string = '',
                              config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api/tenant/deviceInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/tenant/deviceInfos${pageLink.toQuery()}&deviceProfileId=${deviceProfileId}, ...`. */

  public getTenantDeviceInfosByDeviceProfileId(pageLink: PageLink, deviceProfileId: string = '',
                                               config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api/tenant/deviceInfos${pageLink.toQuery()}&deviceProfileId=${deviceProfileId}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/customer/${customerId}/deviceInfos${pageLink.toQuery()}&type=${type}, ...`. */

  public getCustomerDeviceInfos(customerId: string, pageLink: PageLink, type: string = '',
                                config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api/customer/${customerId}/deviceInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/customer/${customerId}/deviceInfos${pageLink.toQuery()}&deviceProfileId=${deviceProfileId}, ...`. */

  public getCustomerDeviceInfosByDeviceProfileId(customerId: string, pageLink: PageLink, deviceProfileId: string = '',
                                                 config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api/customer/${customerId}/deviceInfos${pageLink.toQuery()}&deviceProfileId=${deviceProfileId}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/device/${deviceId}, ...`. */

  public getDevice(deviceId: string, config?: RequestConfig): Observable<Device> {
    return this.http.get<Device>(`/api/device/${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/devices?deviceIds=${deviceIds.join(, ...`. */

  public getDevices(deviceIds: Array<string>, config?: RequestConfig): Observable<Array<Device>> {
    return this.http.get<Array<Device>>(`/api/devices?deviceIds=${deviceIds.join(',')}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/device/info/${deviceId}, ...`. */

  public getDeviceInfo(deviceId: string, config?: RequestConfig): Observable<DeviceInfo> {
    return this.http.get<DeviceInfo>(`/api/device/info/${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/device, ...`. */

  public saveDevice(device: Device, config?: RequestConfig): Observable<Device>;
  public saveDevice(device: Device, saveParams?: SaveDeviceParams, config?: RequestConfig): Observable<Device>;
  public saveDevice(device: Device, saveParamsOrConfig?: SaveDeviceParams | RequestConfig, config?: RequestConfig): Observable<Device> {
    return this.http.post<Device>('/api/device', device, createDefaultHttpOptions(saveParamsOrConfig, config));
  }

  /** Calls ThingsBoard REST `/api/device-with-credentials, ...`. */

  public saveDeviceWithCredentials(device: Device, credentials: DeviceCredentials, config?: RequestConfig): Observable<Device>;
  public saveDeviceWithCredentials(device: Device, credentials: DeviceCredentials, saveParams: SaveEntityParams, config?: RequestConfig): Observable<Device>;
  public saveDeviceWithCredentials(device: Device, credentials: DeviceCredentials, saveParamsOrConfig?: SaveEntityParams | RequestConfig, config?: RequestConfig): Observable<Device> {
    return this.http.post<Device>('/api/device-with-credentials', {
      device,
      credentials
    }, createDefaultHttpOptions(saveParamsOrConfig, config));
  }

  public deleteDevice(deviceId: string, config?: RequestConfig) {
    return this.http.delete(`/api/device/${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/device/types, ...`. */

  public getDeviceTypes(config?: RequestConfig): Observable<Array<EntitySubtype>> {
    return this.http.get<Array<EntitySubtype>>('/api/device/types', defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/device/${deviceId}/credentials, ...`. */

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

  /** Calls ThingsBoard REST `/api/device/credentials, ...`. */

  public saveDeviceCredentials(deviceCredentials: DeviceCredentials, config?: RequestConfig): Observable<DeviceCredentials> {
    return this.http.post<DeviceCredentials>('/api/device/credentials', deviceCredentials, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/customer/public/device/${deviceId}, ...`. */

  public makeDevicePublic(deviceId: string, config?: RequestConfig): Observable<Device> {
    return this.http.post<Device>(`/api/customer/public/device/${deviceId}`, null, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/customer/${customerId}/device/${deviceId}, ...`. */

  public assignDeviceToCustomer(customerId: string, deviceId: string,
                                config?: RequestConfig): Observable<Device> {
    return this.http.post<Device>(`/api/customer/${customerId}/device/${deviceId}`, null, defaultHttpOptionsFromConfig(config));
  }

  public unassignDeviceFromCustomer(deviceId: string, config?: RequestConfig) {
    return this.http.delete(`/api/customer/device/${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/rpc/oneway/${deviceId}, ...`. */

  public sendOneWayRpcCommand(deviceId: string, requestBody: any, config?: RequestConfig): Observable<any> {
    return this.http.post<any>(`/api/rpc/oneway/${deviceId}`, requestBody, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/rpc/twoway/${deviceId}, ...`. */

  public sendTwoWayRpcCommand(deviceId: string, requestBody: any, config?: RequestConfig): Observable<any> {
    return this.http.post<any>(`/api/rpc/twoway/${deviceId}`, requestBody, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/rpc/persistent/${rpcId}, ...`. */

  public getPersistedRpc(rpcId: string, fullResponse = false, config?: RequestConfig): Observable<PersistentRpc> {
    return this.http.get<PersistentRpc>(`/api/rpc/persistent/${rpcId}`, defaultHttpOptionsFromConfig(config));
  }

  public deletePersistedRpc(rpcId: string, config?: RequestConfig) {
    return this.http.delete<PersistentRpc>(`/api/rpc/persistent/${rpcId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/rpc/persistent/device/${deviceId}${pageLink.toQuery()}, ...`. */

  public getPersistedRpcRequests(deviceId: string, pageLink: PageLink,
                                 rpcStatus?: RpcStatus, config?: RequestConfig): Observable<PageData<PersistentRpc>> {
    let url = `/api/rpc/persistent/device/${deviceId}${pageLink.toQuery()}`;
    if (rpcStatus && rpcStatus.length) {
      url += `&rpcStatus=${rpcStatus}`;
    }
    return this.http.get<PageData<PersistentRpc>>(url, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/devices, ...`. */

  public findByQuery(query: DeviceSearchQuery,
                     config?: RequestConfig): Observable<Array<Device>> {
    return this.http.post<Array<Device>>('/api/devices', query, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/tenant/devices?deviceName=${deviceName}, ...`. */

  public findByName(deviceName: string, config?: RequestConfig): Observable<Device> {
    return this.http.get<Device>(`/api/tenant/devices?deviceName=${deviceName}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/customer/device/${deviceName}/claim, ...`. */

  public claimDevice(deviceName: string, claimRequest: ClaimRequest,
                     config?: RequestConfig): Observable<ClaimResult> {
    return this.http.post<ClaimResult>(`/api/customer/device/${deviceName}/claim`, claimRequest, defaultHttpOptionsFromConfig(config));
  }

  public unclaimDevice(deviceName: string, config?: RequestConfig) {
    return this.http.delete(`/api/customer/device/${deviceName}/claim`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge/${edgeId}/device/${deviceId}, ...`. */

  public assignDeviceToEdge(edgeId: string, deviceId: string,
                            config?: RequestConfig): Observable<Device> {
    return this.http.post<Device>(`/api/edge/${edgeId}/device/${deviceId}`,
      defaultHttpOptionsFromConfig(config));
  }

  public unassignDeviceFromEdge(edgeId: string, deviceId: string,
                                config?: RequestConfig) {
    return this.http.delete(`/api/edge/${edgeId}/device/${deviceId}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/edge/${edgeId}/devices${pageLink.toQuery()}&type=${type}, ...`. */

  public getEdgeDevices(edgeId: string, pageLink: PageLink, type: string = '',
                        config?: RequestConfig): Observable<PageData<DeviceInfo>> {
    return this.http.get<PageData<DeviceInfo>>(`/api/edge/${edgeId}/devices${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/device/bulk_import, ...`. */

  public bulkImportDevices(entitiesData: BulkImportRequest, config?: RequestConfig): Observable<BulkImportResult> {
    return this.http.post<BulkImportResult>('/api/device/bulk_import', entitiesData, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/device-connectivity/${deviceId}, ...`. */

  public getDevicePublishTelemetryCommands(deviceId: string, config?: RequestConfig): Observable<PublishTelemetryCommand> {
    return this.http.get<PublishTelemetryCommand>(`/api/device-connectivity/${deviceId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/device-connectivity/gateway-launch/${deviceId}/docker-compose/download`. */

  public downloadGatewayDockerComposeFile(deviceId: string): Observable<any> {
    return this.resourcesService.downloadResource(`/api/device-connectivity/gateway-launch/${deviceId}/docker-compose/download`);
  }

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
