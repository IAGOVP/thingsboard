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
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { EntitySubtype } from '@shared/models/entity-type.models';
import { Asset, AssetInfo, AssetSearchQuery } from '@shared/models/asset.models';
import { BulkImportRequest, BulkImportResult } from '@shared/import-export/import-export.models';
import { SaveEntityParams } from '@shared/models/entity.models';

/**
 * Angular injectable service: asset (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class AssetService {

  constructor(
    private http: HttpClient
  ) { }

  
  /**
   * get tenant asset infos.
   *
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AssetInfo>> observable or value
   */


  public getTenantAssetInfos(pageLink: PageLink, type: string = '', config?: RequestConfig): Observable<PageData<AssetInfo>> {
    return this.http.get<PageData<AssetInfo>>(`/api/tenant/assetInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant asset infos by asset profile id.
   *
   * @param pageLink pagination and sort parameters
   * @param assetProfileId asset profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AssetInfo>> observable or value
   */


  public getTenantAssetInfosByAssetProfileId(pageLink: PageLink, assetProfileId: string = '',
                                             config?: RequestConfig): Observable<PageData<AssetInfo>> {
    return this.http.get<PageData<AssetInfo>>(`/api/tenant/assetInfos${pageLink.toQuery()}&assetProfileId=${assetProfileId}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get customer asset infos.
   *
   * @param customerId customer UUID
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AssetInfo>> observable or value
   */


  public getCustomerAssetInfos(customerId: string, pageLink: PageLink, type: string = '',
                               config?: RequestConfig): Observable<PageData<AssetInfo>> {
    return this.http.get<PageData<AssetInfo>>(`/api/customer/${customerId}/assetInfos${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get customer asset infos by asset profile id.
   *
   * @param customerId customer UUID
   * @param pageLink pagination and sort parameters
   * @param assetProfileId asset profile id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AssetInfo>> observable or value
   */


  public getCustomerAssetInfosByAssetProfileId(customerId: string, pageLink: PageLink, assetProfileId: string = '',
                                               config?: RequestConfig): Observable<PageData<AssetInfo>> {
    return this.http.get<PageData<AssetInfo>>
    (`/api/customer/${customerId}/assetInfos${pageLink.toQuery()}&assetProfileId=${assetProfileId}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get asset.
   *
   * @param assetId asset id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Asset> observable or value
   */


  public getAsset(assetId: string, config?: RequestConfig): Observable<Asset> {
    return this.http.get<Asset>(`/api/asset/${assetId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get assets.
   *
   * @param assetIds asset ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<Asset>> observable or value
   */


  public getAssets(assetIds: Array<string>, config?: RequestConfig): Observable<Array<Asset>> {
    return this.http.get<Array<Asset>>(`/api/assets?assetIds=${assetIds.join(',')}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get asset info.
   *
   * @param assetId asset id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<AssetInfo> observable or value
   */


  public getAssetInfo(assetId: string, config?: RequestConfig): Observable<AssetInfo> {
    return this.http.get<AssetInfo>(`/api/asset/info/${assetId}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/asset, ...`. */

  public saveAsset(asset: Asset, config?: RequestConfig): Observable<Asset>;
  public saveAsset(asset: Asset, saveParams: SaveEntityParams, config?: RequestConfig): Observable<Asset>;
  /**
   * POST/PUT entity — save asset.
   *
   * @param asset asset (Asset)
   * @param saveParamsOrConfig save params or config (SaveEntityParams | RequestConfig)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Asset> observable or value
   */
  public saveAsset(asset: Asset, saveParamsOrConfig?: SaveEntityParams | RequestConfig, config?: RequestConfig): Observable<Asset> {
    return this.http.post<Asset>('/api/asset', asset, createDefaultHttpOptions(saveParamsOrConfig, config));
  }

  /**
   * DELETE — delete asset.
   *
   * @param assetId asset id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteAsset(assetId: string, config?: RequestConfig) {
    return this.http.delete(`/api/asset/${assetId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get asset types.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<EntitySubtype>> observable or value
   */


  public getAssetTypes(config?: RequestConfig): Observable<Array<EntitySubtype>> {
    return this.http.get<Array<EntitySubtype>>('/api/asset/types', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * make asset public.
   *
   * @param assetId asset id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Asset> observable or value
   */


  public makeAssetPublic(assetId: string, config?: RequestConfig): Observable<Asset> {
    return this.http.post<Asset>(`/api/customer/public/asset/${assetId}`, null, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * assign asset to customer.
   *
   * @param customerId customer UUID
   * @param assetId asset id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Asset> observable or value
   */


  public assignAssetToCustomer(customerId: string, assetId: string,
                               config?: RequestConfig): Observable<Asset> {
    return this.http.post<Asset>(`/api/customer/${customerId}/asset/${assetId}`, null, defaultHttpOptionsFromConfig(config));
  }

  /**
   * unassign asset from customer.
   *
   * @param assetId asset id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public unassignAssetFromCustomer(assetId: string, config?: RequestConfig) {
    return this.http.delete(`/api/customer/asset/${assetId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by query.
   *
   * @param query query (AssetSearchQuery)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<Asset>> observable or value
   */


  public findByQuery(query: AssetSearchQuery,
                     config?: RequestConfig): Observable<Array<Asset>> {
    return this.http.post<Array<Asset>>('/api/assets', query, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * find by name.
   *
   * @param assetName asset name (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Asset> observable or value
   */


  public findByName(assetName: string, config?: RequestConfig): Observable<Asset> {
    return this.http.get<Asset>(`/api/tenant/assets?assetName=${assetName}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * assign asset to edge.
   *
   * @param edgeId edge id (string)
   * @param assetId asset id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Asset> observable or value
   */


  public assignAssetToEdge(edgeId: string, assetId: string, config?: RequestConfig): Observable<Asset> {
    return this.http.post<Asset>(`/api/edge/${edgeId}/asset/${assetId}`, null,
      defaultHttpOptionsFromConfig(config));
  }

  /**
   * unassign asset from edge.
   *
   * @param edgeId edge id (string)
   * @param assetId asset id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public unassignAssetFromEdge(edgeId: string, assetId: string,
                               config?: RequestConfig) {
    return this.http.delete(`/api/edge/${edgeId}/asset/${assetId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get edge assets.
   *
   * @param edgeId edge id (string)
   * @param pageLink pagination and sort parameters
   * @param type type (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<AssetInfo>> observable or value
   */


  public getEdgeAssets(edgeId: string, pageLink: PageLink, type: string = '',
                       config?: RequestConfig): Observable<PageData<AssetInfo>> {
    return this.http.get<PageData<AssetInfo>>(`/api/edge/${edgeId}/assets${pageLink.toQuery()}&type=${type}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * bulk import assets.
   *
   * @param entitiesData entities data (BulkImportRequest)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<BulkImportResult> observable or value
   */


  public bulkImportAssets(entitiesData: BulkImportRequest, config?: RequestConfig): Observable<BulkImportResult> {
    return this.http.post<BulkImportResult>('/api/asset/bulk_import', entitiesData, defaultHttpOptionsFromConfig(config));
  }

}
