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
import { TbPopoverComponent } from '@shared/components/popover.component';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { WidgetAction, WidgetActionType, widgetType } from '@shared/models/widget.models';
import { WidgetActionCallbacks } from '@home/components/widget/action/manage-widget-actions.component.models';
import { coerceBoolean } from '@shared/decorators/coercion';
import { TranslateService } from '@ngx-translate/core';


/**
 * Angular component: widget action settings panel (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-widget-action-settings-panel`.
 */
@Component({
    selector: 'tb-widget-action-settings-panel',
    templateUrl: './widget-action-settings-panel.component.html',
    providers: [],
    styleUrls: ['./action-settings-panel.component.scss'],
    encapsulation: ViewEncapsulation.None,
standalone: false
})
export class WidgetActionSettingsPanelComponent implements OnInit {

  @Input()
  widgetAction: WidgetAction;

  @Input()
  panelTitle: string;

  @Input()
  widgetType: widgetType;

  @Input()
  callbacks: WidgetActionCallbacks;

  @Input()
  @coerceBoolean()
  withName = false;

  @Input()
  actionNames: string[];

  @Input()
  applyTitle = this.translate.instant('action.apply');

  @Input()
  additionalWidgetActionTypes: WidgetActionType[];

  @Output()
  widgetActionApplied = new EventEmitter<WidgetAction>();

  widgetActionFormGroup: UntypedFormGroup;

  constructor(private fb: UntypedFormBuilder,
              private translate: TranslateService,
              private popover: TbPopoverComponent) {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    this.widgetActionFormGroup = this.fb.group(
      {
        widgetAction: [this.widgetAction, []]
      }
    );
  }

  /**
   * cancel.
   *
   */

  cancel() {
    this.popover?.hide();
  }

  /**
   * apply widget action.
   *
   */

  applyWidgetAction() {
    const widgetAction: WidgetAction = this.widgetActionFormGroup.get('widgetAction').getRawValue();
    this.widgetActionApplied.emit(widgetAction);
  }
}
