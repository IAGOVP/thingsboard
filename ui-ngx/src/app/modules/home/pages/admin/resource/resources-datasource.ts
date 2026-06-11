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

import { CollectionViewer, DataSource, SelectionModel } from '@angular/cdk/collections';
import { ResourceInfo, ResourceSubType, ResourceType } from '@shared/models/resource.models';
import { BehaviorSubject, Observable, of, ReplaySubject, Subject } from 'rxjs';
import { emptyPageData, PageData } from '@shared/models/page/page-data';
import { EntityBooleanFunction } from '@home/models/entity/entities-table-config.models';
import { PageLink } from '@shared/models/page/page-link';
import { catchError, map, take, tap } from 'rxjs/operators';
import { ResourceService } from "@core/http/resource.service";


/**
 * Resources datasource (home/admin pages).
 */


export class ResourcesDatasource implements DataSource<ResourceInfo> {
  private entitiesSubject: Subject<ResourceInfo[]>;
  private readonly pageDataSubject: Subject<PageData<ResourceInfo>>;

  public pageData$: Observable<PageData<ResourceInfo>>;

  public selection = new SelectionModel<ResourceInfo>(true, []);

  public dataLoading = true;

  constructor(private resourceService: ResourceService,
              private resources: ResourceInfo[],
              private selectionEnabledFunction: EntityBooleanFunction<ResourceInfo>) {
    if (this.resources && this.resources.length) {
      this.entitiesSubject = new BehaviorSubject<ResourceInfo[]>(this.resources);
    } else {
      this.entitiesSubject = new BehaviorSubject<ResourceInfo[]>([]);
      this.pageDataSubject = new BehaviorSubject<PageData<ResourceInfo>>(emptyPageData<ResourceInfo>());
      this.pageData$ = this.pageDataSubject.asObservable();
    }
  }

  /**
   * connect.
   *
   * @param collectionViewer collection viewer (CollectionViewer)
   * @returns Observable<ResourceInfo[] | ReadonlyArray<ResourceInfo>> observable or value
   */

  connect(collectionViewer: CollectionViewer):
    Observable<ResourceInfo[] | ReadonlyArray<ResourceInfo>> {
    return this.entitiesSubject.asObservable();
  }

  /**
   * disconnect.
   *
   * @param collectionViewer collection viewer (CollectionViewer)
   */

  disconnect(collectionViewer: CollectionViewer): void {
    this.entitiesSubject.complete();
    if (this.pageDataSubject) {
      this.pageDataSubject.complete();
    }
  }

  /**
   * reset.
   *
   */

  reset() {
    this.entitiesSubject.next([]);
    if (this.pageDataSubject) {
      this.pageDataSubject.next(emptyPageData<ResourceInfo>());
    }
  }

  /**
   * load entities.
   *
   * @param pageLink pagination and sort parameters
   * @param resourceType resource type (ResourceType)
   * @param subType sub type (ResourceSubType)
   * @returns Observable<PageData<ResourceInfo>> observable or value
   */

  loadEntities(pageLink: PageLink, resourceType: ResourceType, subType: ResourceSubType): Observable<PageData<ResourceInfo>> {
    this.dataLoading = true;
    const result = new ReplaySubject<PageData<ResourceInfo>>();
    this.fetchEntities(pageLink, resourceType, subType).pipe(
      tap(() => {
        this.selection.clear();
      }),
      catchError(() => of(emptyPageData<ResourceInfo>())),
    ).subscribe(
      (pageData) => {
        this.entitiesSubject.next(pageData.data);
        this.pageDataSubject.next(pageData);
        result.next(pageData);
        this.dataLoading = false;
      }
    );
    return result;
  }

  /**
   * fetch entities.
   *
   * @param pageLink pagination and sort parameters
   * @param resourceType resource type (ResourceType)
   * @param subType sub type (ResourceSubType)
   * @returns Observable<PageData<ResourceInfo>> observable or value
   */

  fetchEntities(pageLink: PageLink, resourceType: ResourceType, subType: ResourceSubType): Observable<PageData<ResourceInfo>> {
    return this.resourceService.getResources(pageLink, resourceType, subType);
  }

  /**
   * is all selected.
   *
   * @returns Observable<boolean> observable or value
   */

  isAllSelected(): Observable<boolean> {
    const numSelected = this.selection.selected.length;
    return this.entitiesSubject.pipe(
      map((entities) => numSelected === entities.length)
    );
  }

  /**
   * is empty.
   *
   * @returns Observable<boolean> observable or value
   */

  isEmpty(): Observable<boolean> {
    return this.entitiesSubject.pipe(
      map((entities) => !entities.length)
    );
  }

  /**
   * total.
   *
   * @returns Observable<number> observable or value
   */

  total(): Observable<number> {
    return this.pageDataSubject.pipe(
      map((pageData) => pageData.totalElements)
    );
  }

  /**
   * master toggle.
   *
   */

  masterToggle() {
    this.entitiesSubject.pipe(
      tap((entities) => {
        const numSelected = this.selection.selected.length;
        if (numSelected === this.selectableEntitiesCount(entities)) {
          this.selection.clear();
        } else {
          entities.forEach(row => {
            if (this.selectionEnabledFunction(row)) {
              this.selection.select(row);
            }
          });
        }
      }),
      take(1)
    ).subscribe();
  }

  /**
   * selectable entities count.
   *
   * @param entities entities (Array<ResourceInfo>)
   * @returns number observable or value
   */

  private selectableEntitiesCount(entities: Array<ResourceInfo>): number {
    return entities.filter((entity) => this.selectionEnabledFunction(entity)).length;
  }
}
