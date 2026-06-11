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
import { PageLink } from '@shared/models/page/page-link';
import { defaultHttpOptionsFromConfig, defaultHttpUploadOptions, RequestConfig } from '@core/http/http-utils';
import { forkJoin, Observable, of } from 'rxjs';
import { PageData } from '@shared/models/page/page-data';
import { Resource, ResourceInfo, ResourceSubType, ResourceType, TBResourceScope } from '@shared/models/resource.models';
import { catchError, mergeMap } from 'rxjs/operators';
import { isNotEmptyStr } from '@core/utils';
import { ResourcesService } from '@core/services/resources.service';

/**
 * Angular injectable service: resource (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class ResourceService {
  constructor(
    private http: HttpClient,
    private resourcesService: ResourcesService
  ) {

  }

  
  /**
   * get resources.
   *
   * @param pageLink pagination and sort parameters
   * @param resourceType resource type (ResourceType)
   * @param resourceSubType resource sub type (ResourceSubType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<ResourceInfo>> observable or value
   */


  public getResources(pageLink: PageLink, resourceType?: ResourceType, resourceSubType?: ResourceSubType, config?: RequestConfig): Observable<PageData<ResourceInfo>> {
    let url = `/api/resource${pageLink.toQuery()}`;
    if (isNotEmptyStr(resourceType)) {
      url += `&resourceType=${resourceType}`;
    }
    if (isNotEmptyStr(resourceSubType)) {
      url += `&resourceSubType=${resourceSubType}`;
    }
    return this.http.get<PageData<ResourceInfo>>(url, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get tenant resources.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<ResourceInfo>> observable or value
   */


  public getTenantResources(pageLink: PageLink, config?: RequestConfig): Observable<PageData<ResourceInfo>> {
    return this.http.get<PageData<ResourceInfo>>(`/api/resource/tenant${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config))
  }

  
  /**
   * get resource.
   *
   * @param resourceId resource id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Resource> observable or value
   */


  public getResource(resourceId: string, config?: RequestConfig): Observable<Resource> {
    return this.http.get<Resource>(`/api/resource/${resourceId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get resource info by id.
   *
   * @param resourceId resource id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ResourceInfo> observable or value
   */


  public getResourceInfoById(resourceId: string, config?: RequestConfig): Observable<ResourceInfo> {
    return this.http.get<Resource>(`/api/resource/info/${resourceId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get resource info.
   *
   * @param type type (ResourceType)
   * @param scope scope (TBResourceScope)
   * @param key key (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ResourceInfo> observable or value
   */


  public getResourceInfo(type: ResourceType, scope: TBResourceScope, key: string, config?: RequestConfig): Observable<ResourceInfo> {
    return this.http.get<Resource>(`/api/resource/${type}/${scope}/${key}/info`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * Calls ThingsBoard REST `/api/resource/*`.
   *
   * REST endpoint(s): `/api/resource/*`
   *
   * @param resourceId resource id (string)
   * @returns Observable<any> observable or value
   */


  public downloadResource(resourceId: string): Observable<any> {
    return this.resourcesService.downloadResource(`/api/resource/${resourceId}/download`);
  }

  
  /**
   * POST/PUT entity — save resources.
   *
   * @param resources resources (Resource[])
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Resource[]> observable or value
   */


  public saveResources(resources: Resource[], config?: RequestConfig): Observable<Resource[]> {
    let partSize = 100;
    partSize = resources.length > partSize ? partSize : resources.length;
    const resourceObservables: Observable<Resource>[] = [];
    for (let i = 0; i < partSize; i++) {
      resourceObservables.push(this.saveResource(resources[i], config).pipe(catchError(() => of({} as Resource))));
    }
    return forkJoin(resourceObservables).pipe(
      mergeMap((resource) => {
        resources.splice(0, partSize);
        if (resources.length) {
          return this.saveResources(resources, config);
        } else {
          return of(resource);
        }
      })
    );
  }

  
  /**
   * POST/PUT entity — save resource.
   *
   * @param resource resource (Resource)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Resource> observable or value
   */


  public saveResource(resource: Resource, config?: RequestConfig): Observable<Resource> {
    return this.http.post<Resource>('/api/resource', resource, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * upload resources.
   *
   * @param resources resources (Resource[])
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Resource[]> observable or value
   */


  public uploadResources(resources: Resource[], config?: RequestConfig): Observable<Resource[]> {
    let partSize = 100;
    partSize = resources.length > partSize ? partSize : resources.length;
    const resourceObservables: Observable<Resource>[] = [];
    for (let i = 0; i < partSize; i++) {
      resourceObservables.push(this.uploadResource(resources[i], config).pipe(catchError(() => of({} as Resource))));
    }
    return forkJoin(resourceObservables).pipe(
      mergeMap((resource) => {
        resources.splice(0, partSize);
        if (resources.length) {
          return this.uploadResources(resources, config);
        } else {
          return of(resource);
        }
      })
    );
  }

  
  /**
   * upload resource.
   *
   * @param resource resource (Resource)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Resource> observable or value
   */


  public uploadResource(resource: Resource, config?: RequestConfig): Observable<Resource> {
    if (!config) {
      config = {};
    }
    const formData = new FormData();
    formData.append('file', resource.data);
    formData.append('title', resource.title);
    formData.append('resourceType', resource.resourceType);
    if (resource.resourceSubType) {
      formData.append('resourceSubType', resource.resourceSubType);
    }
    return this.http.post<Resource>('/api/resource/upload', formData,
      defaultHttpUploadOptions(config.ignoreLoading, config.ignoreErrors, config.resendRequest));
  }

  
  /**
   * updated resource info.
   *
   * @param resourceId resource id (string)
   * @param updatedResources updated resources (Partial<Omit<Resource, 'data'>>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Resource> observable or value
   */


  public updatedResourceInfo(resourceId: string, updatedResources: Partial<Omit<Resource, 'data'>>, config?: RequestConfig): Observable<Resource> {
    return this.http.put<Resource>(`/api/resource/${resourceId}/info`, updatedResources, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * updated resource data.
   *
   * @param resourceId resource id (string)
   * @param data dialog or route input data
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Resource> observable or value
   */


  public updatedResourceData(resourceId: string, data: File, config?: RequestConfig): Observable<Resource> {
    if (!config) {
      config = {};
    }
    const formData = new FormData();
    formData.append('file', data);
    return this.http.put<Resource>(`/api/resource/${resourceId}/data`, formData,
      defaultHttpUploadOptions(config.ignoreLoading, config.ignoreErrors, config.resendRequest));
  }

  /**
   * DELETE — delete resource.
   *
   * @param resourceId resource id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteResource(resourceId: string, force = false, config?: RequestConfig) {
    return this.http.delete(`/api/resource/${resourceId}?force=${force}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get resources by ids.
   *
   * @param ids ids (string[])
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<ResourceInfo>> observable or value
   */


  public getResourcesByIds(ids: string[], config?: RequestConfig): Observable<Array<ResourceInfo>> {
    return this.http.get<Array<ResourceInfo>>(`/api/resource?resourceIds=${ids.join(',')}`,
      defaultHttpOptionsFromConfig(config));
  }

}
