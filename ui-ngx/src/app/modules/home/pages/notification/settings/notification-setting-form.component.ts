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

import { Component, forwardRef, Input, OnDestroy, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { deepClone, isDefinedAndNotNull } from '@core/utils';
import { Subscription } from 'rxjs';
import {
  NotificationDeliveryMethod,
  NotificationTemplateTypeTranslateMap,
  NotificationUserSetting
} from '@shared/models/notification.models';


/**
 * Angular component: notification setting form (home/notification pages).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-notification-setting-form`.
 */
@Component({
    selector: 'tb-notification-setting-form',
    templateUrl: './notification-setting-form.component.html',
    styleUrls: ['./notification-setting-form.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => NotificationSettingFormComponent),
            multi: true
        }
    ],
standalone: false
})
export class NotificationSettingFormComponent implements ControlValueAccessor, OnInit, OnDestroy {

  @Input()
  disabled: boolean;

  @Input()
  deliveryMethods: NotificationDeliveryMethod[] = [];

  notificationSettingsFormGroup: UntypedFormGroup;

  notificationTemplateTypeTranslateMap = NotificationTemplateTypeTranslateMap;

  private propagateChange = null;

  private valueChange$: Subscription = null;

  constructor(private fb: UntypedFormBuilder) {
  }

  /**
   * register on change.
   *
   * @param fn fn (any)
   */

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  /**
   * register on touched.
   *
   * @param fn fn (any)
   */

  registerOnTouched(fn: any): void {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    const deliveryMethod = {};
    this.deliveryMethods.forEach(value => {
      deliveryMethod[value] = true;
    });
    this.notificationSettingsFormGroup = this.fb.group(
      {
        name: [''],
        enabled: [true],
        enabledDeliveryMethods: this.fb.group({
          ...deliveryMethod
        })
      });
    this.valueChange$ = this.notificationSettingsFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  /**
   * Angular lifecycle hook: unsubscribe and release resources.
   *
   */

  ngOnDestroy() {
    if (this.valueChange$) {
      this.valueChange$.unsubscribe();
      this.valueChange$ = null;
    }
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.notificationSettingsFormGroup.disable({emitEvent: false});
    } else {
      this.notificationSettingsFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * get checked.
   *
   * @param deliveryMethod delivery method (NotificationDeliveryMethod)
   * @returns boolean observable or value
   */

  getChecked(deliveryMethod?: NotificationDeliveryMethod): boolean {
    if (deliveryMethod) {
      return this.notificationSettingsFormGroup.get('enabledDeliveryMethods').get(deliveryMethod).value;
    } else {
      const enabledDeliveryMethod = Object.values(this.notificationSettingsFormGroup.get('enabledDeliveryMethods').value);
      const checked = enabledDeliveryMethod.some(value => value === true);
      if (checked !== this.notificationSettingsFormGroup.get('enabled').value) {
        setTimeout(() => this.notificationSettingsFormGroup.get('enabled').patchValue(checked), 0);
      }
      return enabledDeliveryMethod.every(value => value);
    }
  }

  /**
   * toggle deliviry method.
   *
   * @param deliveryMethod delivery method (NotificationDeliveryMethod)
   */

  toggleDeliviryMethod(deliveryMethod: NotificationDeliveryMethod) {
    this.notificationSettingsFormGroup.get('enabledDeliveryMethods').get(deliveryMethod)
      .patchValue(!this.notificationSettingsFormGroup.get('enabledDeliveryMethods').get(deliveryMethod).value);
  }

  /**
   * change instance type check box.
   *
   * @param value value (any)
   */

  changeInstanceTypeCheckBox(value: any) {
    const enabledDeliveryMethod = deepClone(this.notificationSettingsFormGroup.get('enabledDeliveryMethods').value);
    Object.keys(enabledDeliveryMethod).forEach(key => {
      enabledDeliveryMethod[key] = value;
    });
    this.notificationSettingsFormGroup.get('enabled').patchValue(value, {emitEvent: false});
    this.notificationSettingsFormGroup.get('enabledDeliveryMethods').patchValue(enabledDeliveryMethod);
    this.notificationSettingsFormGroup.markAsDirty();
  }

  /**
   * get indeterminate.
   *
   */

  getIndeterminate() {
    if (!this.notificationSettingsFormGroup.get('enabled').value) {
      return false;
    }
    const enabledDeliveryMethod: Array<boolean> = Object.values(this.notificationSettingsFormGroup.get('enabledDeliveryMethods').value);
    const checkedResource = enabledDeliveryMethod.filter(value => value);
    return checkedResource.length !== 0 && checkedResource.length !== enabledDeliveryMethod.length;
  }

  /**
   * write value.
   *
   * @param value value (NotificationUserSetting)
   */

  writeValue(value: NotificationUserSetting): void {
    if (isDefinedAndNotNull(value)) {
      this.notificationSettingsFormGroup.patchValue(value, {emitEvent: false});
    }
  }

  /**
   * update model.
   *
   */

  private updateModel() {
      this.propagateChange(this.notificationSettingsFormGroup.value);
  }
}
