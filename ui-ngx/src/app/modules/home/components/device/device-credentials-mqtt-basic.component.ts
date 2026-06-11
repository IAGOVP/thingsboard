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

import { Component, forwardRef, Input, OnDestroy } from '@angular/core';
import {
  ControlValueAccessor,
  UntypedFormBuilder,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { Subject } from 'rxjs';
import { DeviceCredentialMQTTBasic } from '@shared/models/device.models';
import { takeUntil } from 'rxjs/operators';
import { generateSecret, isDefinedAndNotNull, isEmptyStr } from '@core/utils';


/**
 * Angular component: device credentials mqtt basic (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-device-credentials-mqtt-basic`.
 */
@Component({
    selector: 'tb-device-credentials-mqtt-basic',
    templateUrl: './device-credentials-mqtt-basic.component.html',
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DeviceCredentialsMqttBasicComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => DeviceCredentialsMqttBasicComponent),
            multi: true,
        }
    ],
    styleUrls: [],
standalone: false
})
export class DeviceCredentialsMqttBasicComponent implements ControlValueAccessor, Validator, OnDestroy {

  @Input()
  disabled: boolean;

  deviceCredentialsMqttFormGroup: UntypedFormGroup;

  private destroy$ = new Subject<void>();
  private propagateChange = (v: any) => {};

  constructor(public fb: UntypedFormBuilder) {
    this.deviceCredentialsMqttFormGroup = this.fb.group({
      clientId: [null],
      userName: [null],
      password: [null]
    }, {validators: this.atLeastOne(Validators.required, ['clientId', 'userName'])});
    this.deviceCredentialsMqttFormGroup.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe((value) => {
      this.updateView(value);
    });
  }

  /**
   * Angular lifecycle hook: unsubscribe and release resources.
   *
   */

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
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

  registerOnTouched(fn: any): void {}

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean) {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.deviceCredentialsMqttFormGroup.disable({emitEvent: false});
    } else {
      this.deviceCredentialsMqttFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * validate.
   *
   * @returns ValidationErrors | null observable or value
   */

  validate(): ValidationErrors | null {
    return this.deviceCredentialsMqttFormGroup.valid ? null : {
      deviceCredentialsMqttBasic: false
    };
  }

  /**
   * write value.
   *
   * @param mqttBasic mqtt basic (string)
   */

  writeValue(mqttBasic: string) {
    if (isDefinedAndNotNull(mqttBasic) && !isEmptyStr(mqttBasic)) {
      const value = JSON.parse(mqttBasic);
      this.deviceCredentialsMqttFormGroup.patchValue(value, {emitEvent: false});
    }
  }

  /**
   * update view.
   *
   * @param value value (DeviceCredentialMQTTBasic)
   */

  updateView(value: DeviceCredentialMQTTBasic) {
    const formValue = JSON.stringify(value);
    this.propagateChange(formValue);
  }

  /**
   * password changed.
   *
   */

  passwordChanged() {
    const value = this.deviceCredentialsMqttFormGroup.get('password').value;
    if (value !== '') {
      this.deviceCredentialsMqttFormGroup.get('userName').setValidators([Validators.required]);
    } else {
      this.deviceCredentialsMqttFormGroup.get('userName').setValidators([]);
    }
    this.deviceCredentialsMqttFormGroup.get('userName').updateValueAndValidity({emitEvent: false});
  }

  /**
   * at least one.
   *
   * @param validator validator (ValidatorFn)
   * @param controls controls (string[])
   */

  private atLeastOne(validator: ValidatorFn, controls: string[] = null) {
    return (group: UntypedFormGroup): ValidationErrors | null => {
      if (!controls) {
        controls = Object.keys(group.controls);
      }
      const hasAtLeastOne = group?.controls && controls.some(k => !validator(group.controls[k]));

      return hasAtLeastOne ? null : {atLeastOne: true};
    };
  }

  /**
   * generate.
   *
   * @param formControlName form control name (string)
   */

  public generate(formControlName: string) {
    this.deviceCredentialsMqttFormGroup.get(formControlName).patchValue(generateSecret(20));
  }
}
