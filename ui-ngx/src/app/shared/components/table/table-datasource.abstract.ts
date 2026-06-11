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

import { DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';


/**
 * Tb table datasource (shared UI components).
 */


export abstract class TbTableDatasource<T> implements DataSource<T> {

  protected dataSubject = new BehaviorSubject<Array<T>>([]);

  /**
   * connect.
   *
   * @returns Observable<Array<T>> observable or value
   */

  connect(): Observable<Array<T>> {
    return this.dataSubject.asObservable();
  }

  /**
   * disconnect.
   *
   */

  disconnect(): void {
    this.dataSubject.complete();
  }

  /**
   * load data.
   *
   * @param data dialog or route input data
   */

  loadData(data: Array<T>): void {
    this.dataSubject.next(data);
  }

  /**
   * is empty.
   *
   * @returns Observable<boolean> observable or value
   */

  isEmpty(): Observable<boolean> {
    return this.dataSubject.pipe(
      map((data: T[]) => !data.length)
    );
  }

  /**
   * total.
   *
   * @returns Observable<number> observable or value
   */

  total(): Observable<number> {
    return this.dataSubject.pipe(
      map((data: T[]) => data.length)
    );
  }
}
