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

import { Component, Inject, InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { OverlayRef } from '@angular/cdk/overlay';
import { EntityType } from '@shared/models/entity-type.models';
import { FilterEventBody } from '@shared/models/event.models';
import { deepTrim } from '@core/utils';

export const EVENT_FILTER_PANEL_DATA = new InjectionToken<any>('AlarmFilterPanelData');

export interface EventFilterPanelData {
  filterParams: FilterEventBody;
  columns: Array<FilterEntityColumn>;
}

export interface FilterEntityColumn {
  key: string;
  title: string;
}



/**
 * Angular component: event filter panel (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-event-filter-panel`.
 */
@Component({
    selector: 'tb-event-filter-panel',
    templateUrl: './event-filter-panel.component.html',
    styleUrls: ['./event-filter-panel.component.scss'],
standalone: false
})
export class EventFilterPanelComponent {

  eventFilterFormGroup: UntypedFormGroup;
  result: EventFilterPanelData;

  private conditionError = false;

  private msgDirectionTypes = ['IN', 'OUT'];
  private statusTypes = ['Success', 'Failure'];
  private entityTypes = Object.keys(EntityType);

  showColumns: FilterEntityColumn[] = [];

  constructor(@Inject(EVENT_FILTER_PANEL_DATA)
              public data: EventFilterPanelData,
              public overlayRef: OverlayRef,
              private fb: UntypedFormBuilder) {
    this.eventFilterFormGroup = this.fb.group({});
    this.data.columns.forEach((column) => {
      this.showColumns.push(column);
      const validators = [];
      if (this.isNumberFields(column.key)) {
        validators.push(Validators.min(0));
      }
      this.eventFilterFormGroup.addControl(column.key, this.fb.control(this.data.filterParams[column.key] || '', validators));
      if (column.key === 'isError') {
        this.conditionError = true;
      }
    });
  }

  /**
   * is selector.
   *
   * @param key key (string)
   * @returns string observable or value
   */

  isSelector(key: string): string {
    return ['msgDirectionType', 'status', 'entityName'].includes(key) ? key : '';
  }

  /**
   * is number fields.
   *
   * @param key key (string)
   * @returns string observable or value
   */

  isNumberFields(key: string): string {
    return ['minMessagesProcessed', 'maxMessagesProcessed', 'minErrorsOccurred', 'maxErrorsOccurred'].includes(key) ? key : '';
  }

  /**
   * selector values.
   *
   * @param key key (string)
   * @returns string[] observable or value
   */

  selectorValues(key: string): string[] {
    switch (key) {
      case 'msgDirectionType':
        return this.msgDirectionTypes;
      case 'status':
        return this.statusTypes;
      case 'entityName':
        return this.entityTypes;
    }
  }

  /**
   * update.
   *
   */

  update() {
    const filter = deepTrim(Object.fromEntries(Object.entries(this.eventFilterFormGroup.value).filter(([_, v]) => v !== '')));
    this.result = {
      filterParams: filter,
      columns: this.data.columns
    };
    this.overlayRef.dispose();
  }

  /**
   * show error msg fields.
   *
   */

  showErrorMsgFields() {
    return !this.conditionError || this.eventFilterFormGroup.get('isError').value !== '';
  }

  /**
   * cancel.
   *
   */

  cancel() {
    this.overlayRef.dispose();
  }

  /**
   * change is error.
   *
   * @param value value (boolean | string)
   */

  changeIsError(value: boolean | string) {
    if (this.conditionError && value === '') {
      this.eventFilterFormGroup.get('errorStr').reset('', {emitEvent: false});
    }
  }
}

