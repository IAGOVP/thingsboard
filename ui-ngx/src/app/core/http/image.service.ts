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
import { Observable, of, ReplaySubject } from 'rxjs';
import { PageData } from '@shared/models/page/page-data';
import {
  ImageExportData,
  ImageResourceInfo,
  ImageResourceType,
  imageResourceType,
  IMAGES_URL_PREFIX,
  isImageResourceUrl,
  NO_IMAGE_DATA_URI,
  removeTbImagePrefix,
  ResourceSubType
} from '@shared/models/resource.models';
import { catchError, finalize, map, switchMap } from 'rxjs/operators';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { blobToBase64, blobToText } from '@core/utils';
import { ResourcesService } from '@core/services/resources.service';

/**
 * Angular injectable service: image (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class ImageService {

  private imagesLoading: { [url: string]: ReplaySubject<Blob> } = {};

  constructor(
    private http: HttpClient,
    private sanitizer: DomSanitizer,
    private resourcesService: ResourcesService
  ) {
  }

  
  /**
   * upload image.
   *
   * @param file file (File)
   * @param title title (string)
   * @param imageSubType image sub type (ResourceSubType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ImageResourceInfo> observable or value
   */


  public uploadImage(file: File, title: string, imageSubType: ResourceSubType = ResourceSubType.IMAGE,
                     config?: RequestConfig): Observable<ImageResourceInfo> {
    if (!config) {
      config = {};
    }
    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    formData.append('imageSubType', imageSubType);
    return this.http.post<ImageResourceInfo>('/api/image', formData,
      defaultHttpUploadOptions(config.ignoreLoading, config.ignoreErrors, config.resendRequest));
  }

  
  /**
   * update image.
   *
   * @param type type (ImageResourceType)
   * @param key key (string)
   * @param file file (File)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ImageResourceInfo> observable or value
   */


  public updateImage(type: ImageResourceType, key: string, file: File, config?: RequestConfig): Observable<ImageResourceInfo> {
    if (!config) {
      config = {};
    }
    const formData = new FormData();
    formData.append('file', file);
    return this.http.put<ImageResourceInfo>(`${IMAGES_URL_PREFIX}/${type}/${encodeURIComponent(key)}`, formData,
      defaultHttpUploadOptions(config.ignoreLoading, config.ignoreErrors, config.resendRequest));
  }

  
  /**
   * update image info.
   *
   * @param imageInfo image info (ImageResourceInfo)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ImageResourceInfo> observable or value
   */


  public updateImageInfo(imageInfo: ImageResourceInfo, config?: RequestConfig): Observable<ImageResourceInfo> {
    const type = imageResourceType(imageInfo);
    const key = encodeURIComponent(imageInfo.resourceKey);
    return this.http.put<ImageResourceInfo>(`${IMAGES_URL_PREFIX}/${type}/${key}/info`,
      imageInfo, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * update image public status.
   *
   * @param imageInfo image info (ImageResourceInfo)
   * @param isPublic is public (boolean)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ImageResourceInfo> observable or value
   */


  public updateImagePublicStatus(imageInfo: ImageResourceInfo, isPublic: boolean, config?: RequestConfig): Observable<ImageResourceInfo> {
    const type = imageResourceType(imageInfo);
    const key = encodeURIComponent(imageInfo.resourceKey);
    return this.http.put<ImageResourceInfo>(`${IMAGES_URL_PREFIX}/${type}/${key}/public/${isPublic}`,
      imageInfo, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get images.
   *
   * @param pageLink pagination and sort parameters
   * @param imageSubType image sub type (ResourceSubType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<ImageResourceInfo>> observable or value
   */


  public getImages(pageLink: PageLink, includeSystemImages = false,
                   imageSubType: ResourceSubType = ResourceSubType.IMAGE, config?: RequestConfig): Observable<PageData<ImageResourceInfo>> {
    return this.http.get<PageData<ImageResourceInfo>>(
      `${IMAGES_URL_PREFIX}${pageLink.toQuery()}&imageSubType=${imageSubType}&includeSystemImages=${includeSystemImages}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get image info.
   *
   * @param type type (ImageResourceType)
   * @param key key (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ImageResourceInfo> observable or value
   */


  public getImageInfo(type: ImageResourceType, key: string, config?: RequestConfig): Observable<ImageResourceInfo> {
    return this.http.get<ImageResourceInfo>(`${IMAGES_URL_PREFIX}/${type}/${encodeURIComponent(key)}/info`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get image data url.
   *
   * @param imageUrl image url (string)
   * @returns Observable<SafeUrl | string> observable or value
   */


  public getImageDataUrl(imageUrl: string, preview = false, asString = false, emptyUrl = NO_IMAGE_DATA_URI): Observable<SafeUrl | string> {
    const parts = imageUrl.split('/');
    const key = parts[parts.length - 1];
    parts[parts.length - 1] = encodeURIComponent(key);
    const encodedUrl = parts.join('/');
    const imageLink = preview ? (encodedUrl + '/preview') : encodedUrl;
    return this.loadImageDataUrl(imageLink, asString, emptyUrl);
  }

  /**
   * load image data url.
   *
   * @param imageLink image link (string)
   * @returns Observable<SafeUrl | string> observable or value
   */

  private loadImageDataUrl(imageLink: string, asString = false, emptyUrl = NO_IMAGE_DATA_URI): Observable<SafeUrl | string> {
    let request: ReplaySubject<Blob>;
    if (this.imagesLoading[imageLink]) {
      request = this.imagesLoading[imageLink];
    } else {
      request = new ReplaySubject<Blob>(1);
      this.imagesLoading[imageLink] = request;
      const options = defaultHttpOptionsFromConfig({ignoreLoading: true, ignoreErrors: true});
      this.http.get(imageLink, {...options, ...{ responseType: 'blob' } }).pipe(
        finalize(()=> delete this.imagesLoading[imageLink])
      ).subscribe({
        next: (value) => {
          request.next(value);
          request.complete();
        },
        error: err => {
          request.error(err);
        }
      });
    }
    return request.pipe(
      switchMap(val => blobToBase64(val).pipe(
        map((dataUrl) => asString ? dataUrl : this.sanitizer.bypassSecurityTrustUrl(dataUrl))
      )),
      catchError(() => of(asString ? emptyUrl : this.sanitizer.bypassSecurityTrustUrl(emptyUrl)))
    );
  }

  
  /**
   * get image string.
   *
   * @param imageUrl image url (string)
   * @returns Observable<string> observable or value
   */


  public getImageString(imageUrl: string): Observable<string> {
    imageUrl = removeTbImagePrefix(imageUrl);
    let request: ReplaySubject<Blob>;
    if (this.imagesLoading[imageUrl]) {
      request = this.imagesLoading[imageUrl];
    } else {
      request = new ReplaySubject<Blob>(1);
      this.imagesLoading[imageUrl] = request;
      const options = defaultHttpOptionsFromConfig({ignoreLoading: true, ignoreErrors: true});
      this.http.get(imageUrl, {...options, ...{ responseType: 'blob' } }).subscribe({
        next: (value) => {
          request.next(value);
          request.complete();
        },
        error: err => {
          request.error(err);
        },
        complete: () => {
          delete this.imagesLoading[imageUrl];
        }
      });
    }
    return request.pipe(
      switchMap(val => blobToText(val))
    );
  }

  
  /**
   * resolve image url.
   *
   * @param imageUrl image url (string)
   * @returns Observable<SafeUrl | string> observable or value
   */


  public resolveImageUrl(imageUrl: string, preview = false, asString = false, emptyUrl = NO_IMAGE_DATA_URI): Observable<SafeUrl | string> {
    imageUrl = removeTbImagePrefix(imageUrl);
    if (isImageResourceUrl(imageUrl)) {
      return this.getImageDataUrl(imageUrl, preview, asString, emptyUrl);
    } else {
      return of(asString ? imageUrl : this.sanitizer.bypassSecurityTrustUrl(imageUrl));
    }
  }

  
  /**
   * download image.
   *
   * @param type type (ImageResourceType)
   * @param key key (string)
   * @returns Observable<any> observable or value
   */


  public downloadImage(type: ImageResourceType, key: string): Observable<any> {
    return this.resourcesService.downloadResource(`${IMAGES_URL_PREFIX}/${type}/${encodeURIComponent(key)}`);
  }

  /**
   * DELETE — delete image.
   *
   * @param type type (ImageResourceType)
   * @param key key (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteImage(type: ImageResourceType, key: string, force = false, config?: RequestConfig) {
    return this.http.delete(`${IMAGES_URL_PREFIX}/${type}/${encodeURIComponent(key)}?force=${force}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * export image.
   *
   * @param type type (ImageResourceType)
   * @param key key (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ImageExportData> observable or value
   */


  public exportImage(type: ImageResourceType, key: string, config?: RequestConfig): Observable<ImageExportData> {
    return this.http.get<ImageExportData>(`${IMAGES_URL_PREFIX}/${type}/${encodeURIComponent(key)}/export`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * import image.
   *
   * @param imageData image data (ImageExportData)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<ImageResourceInfo> observable or value
   */


  public importImage(imageData: ImageExportData, config?: RequestConfig): Observable<ImageResourceInfo> {
    return this.http.put<ImageResourceInfo>('/api/image/import',
      imageData, defaultHttpOptionsFromConfig(config));
  }

}
