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

import { PageLink } from '@shared/models/page/page-link';
import { BehaviorSubject, Observable, of, ReplaySubject, Subscription } from 'rxjs';
import { emptyPageData, PageData } from '@shared/models/page/page-data';
import { BaseData, HasId } from '@shared/models/base-data';
import { CollectionViewer, DataSource, SelectionModel } from '@angular/cdk/collections';
import { catchError, map, share, take, tap } from 'rxjs/operators';
import { EntityBooleanFunction } from '@home/models/entity/entities-table-config.models';

export type EntitiesFetchFunction<T extends BaseData<HasId>, P extends PageLink> = (pageLink: P) => Observable<PageData<T>>;


/**
 * TypeScript interfaces, types, and enums for entities data source (ThingsBoard web UI).
 */


export class EntitiesDataSource<T extends BaseData<HasId>, P extends PageLink = PageLink> implements DataSource<T> {

  private entitiesSubject = new BehaviorSubject<T[]>([]);
  private pageDataSubject = new BehaviorSubject<PageData<T>>(emptyPageData<T>());
  private currentLoadSubscription: Subscription = null;

  public pageData$ = this.pageDataSubject.asObservable();

  public selection = new SelectionModel<T>(true, []);

  public currentEntity: T = null;

  public dataLoading = true;

  constructor(private fetchFunction: EntitiesFetchFunction<T, P>,
              protected selectionEnabledFunction: EntityBooleanFunction<T>,
              protected dataLoadedFunction: (col?: number, row?: number) => void) {}

  /**
   * connect.
   *
   * @param collectionViewer collection viewer (CollectionViewer)
   * @returns Observable<T[] | ReadonlyArray<T>> observable or value
   */

  connect(collectionViewer: CollectionViewer): Observable<T[] | ReadonlyArray<T>> {
    return this.entitiesSubject.asObservable();
  }

  /**
   * disconnect.
   *
   * @param collectionViewer collection viewer (CollectionViewer)
   */

  disconnect(collectionViewer: CollectionViewer): void {
    this.entitiesSubject.complete();
    this.pageDataSubject.complete();
  }

  /**
   * reset.
   *
   */

  reset() {
    const pageData = emptyPageData<T>();
    this.onEntities(pageData.data);
    this.pageDataSubject.next(pageData);
    this.dataLoadedFunction();
  }

  /**
   * load entities.
   *
   * @param pageLink pagination and sort parameters
   * @returns Observable<PageData<T>> observable or value
   */

  loadEntities(pageLink: P): Observable<PageData<T>> {
    if (this.currentLoadSubscription) {
      this.currentLoadSubscription.unsubscribe();
    }
    this.dataLoading = true;
    const result = new ReplaySubject<PageData<T>>();
    this.currentLoadSubscription = this.fetchFunction(pageLink).pipe(
      tap(() => {
        this.selection.clear();
      }),
      catchError(() => of(emptyPageData<T>())),
    ).subscribe(
      (pageData) => {
        this.onEntities(pageData.data);
        this.pageDataSubject.next(pageData);
        result.next(pageData);
        this.dataLoadedFunction();
        this.dataLoading = false;
      }
    );
    return result;
  }

  /**
   * Event handler for entities.
   *
   * @param entities entities (T[])
   */

  protected onEntities(entities: T[]) {
    this.entitiesSubject.next(entities);
  }

  /**
   * is all selected.
   *
   * @returns Observable<boolean> observable or value
   */

  isAllSelected(): Observable<boolean> {
    const numSelected = this.selection.selected.length;
    return this.entitiesSubject.pipe(
      map((entities) => numSelected === this.selectableEntitiesCount(entities)),
      share()
    );
  }

  /**
   * is empty.
   *
   * @returns Observable<boolean> observable or value
   */

  isEmpty(): Observable<boolean> {
    return this.entitiesSubject.pipe(
      map((entities) => !entities.length),
      share()
    );
  }

  /**
   * total.
   *
   * @returns Observable<number> observable or value
   */

  total(): Observable<number> {
    return this.pageDataSubject.pipe(
      map((pageData) => pageData.totalElements),
      share()
    );
  }

  /**
   * toggle current entity.
   *
   * @param entity entity (T)
   * @returns boolean observable or value
   */

  toggleCurrentEntity(entity: T): boolean {
    if (this.currentEntity !== entity) {
      this.currentEntity = entity;
      return true;
    } else {
      return false;
    }
  }

  /**
   * is current entity.
   *
   * @param entity entity (T)
   * @returns boolean observable or value
   */

  isCurrentEntity(entity: T): boolean {
    return (this.currentEntity && entity && this.currentEntity.id && entity.id) &&
      (this.currentEntity.id.id === entity.id.id);
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
   * @param entities entities (Array<T>)
   * @returns number observable or value
   */

  private selectableEntitiesCount(entities: Array<T>): number {
    return entities.filter((entity) => this.selectionEnabledFunction(entity)).length;
  }
}
