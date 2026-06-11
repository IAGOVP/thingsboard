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

import { Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TbPopoverComponent } from '@shared/components/popover.component';
import { UnplacedMapDataItem } from '@home/components/widget/lib/maps/data-layer/latest-map-data-layer';


/**
 * Angular component: select map entity panel (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-select-map-entity-panel`.
 */
@Component({
    selector: 'tb-select-map-entity-panel',
    templateUrl: './select-map-entity-panel.component.html',
    providers: [],
    styleUrls: ['./select-map-entity-panel.component.scss'],
    encapsulation: ViewEncapsulation.None,
standalone: false
})
export class SelectMapEntityPanelComponent implements OnInit {

  @Input()
  entities: UnplacedMapDataItem[];

  @Output()
  entitySelected = new EventEmitter<UnplacedMapDataItem>();

  selectEntityFormGroup: UntypedFormGroup;

  selectedEntity: UnplacedMapDataItem = null;

  constructor(private fb: UntypedFormBuilder,
              private popover: TbPopoverComponent) {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    this.selectEntityFormGroup = this.fb.group(
      {
        entity: ['', Validators.required]
      }
    );
    this.popover.tbDestroy.subscribe(() => {
      this.entitySelected.emit(this.selectedEntity);
    });
  }

  /**
   * cancel.
   *
   */

  cancel() {
    this.popover.hide();
  }

  /**
   * select entity.
   *
   */

  selectEntity() {
    this.selectedEntity = this.selectEntityFormGroup.value.entity;
    this.popover.hide();
  }
}
