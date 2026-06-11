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

import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { EntityTabsComponent } from '../../components/entity/entity-tabs.component';
import { AssetInfo } from '@app/shared/models/asset.models';
import { EntityId } from "@shared/models/id/entity-id";


/**
 * Angular component: asset tabs (home/asset pages).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-asset-tabs`.
 */
@Component({
    selector: 'tb-asset-tabs',
    templateUrl: './asset-tabs.component.html',
    styleUrls: [],
standalone: false
})
export class AssetTabsComponent extends EntityTabsComponent<AssetInfo> {

  ownerId: EntityId;

  constructor(protected store: Store<AppState>) {
    super(store);
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    super.ngOnInit();
  }

  /**
   * set entity.
   *
   * @param entity entity (AssetInfo)
   */

  protected setEntity(entity: AssetInfo) {
    this.ownerId = entity.customerId.id !== this.nullUid ? entity.customerId : entity.tenantId;
    super.setEntity(entity);
  }

}
