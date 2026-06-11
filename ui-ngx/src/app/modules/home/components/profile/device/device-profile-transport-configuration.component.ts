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

import { Component, DestroyRef, forwardRef, Input, OnInit } from '@angular/core';
import {
  ControlValueAccessor,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  ValidationErrors,
  Validator
} from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { DeviceProfileTransportConfiguration, DeviceTransportType } from '@shared/models/device.models';
import { deepClone } from '@core/utils';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: device profile transport configuration (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-device-profile-transport-configuration`.
 */
@Component({
    selector: 'tb-device-profile-transport-configuration',
    templateUrl: './device-profile-transport-configuration.component.html',
    styleUrls: [],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DeviceProfileTransportConfigurationComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => DeviceProfileTransportConfigurationComponent),
            multi: true,
        }
    ],
standalone: false
})
export class DeviceProfileTransportConfigurationComponent implements ControlValueAccessor, OnInit, Validator {

  deviceTransportType = DeviceTransportType;

  deviceProfileTransportConfigurationFormGroup: UntypedFormGroup;

  @Input()
  disabled: boolean;

  @Input()
  isAdd: boolean;

  transportType: DeviceTransportType;

  private propagateChange = (v: any) => { };

  constructor(private store: Store<AppState>,
              private fb: UntypedFormBuilder,
              private destroyRef: DestroyRef) {
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
    this.deviceProfileTransportConfigurationFormGroup = this.fb.group({
      configuration: [null]
    });
    this.deviceProfileTransportConfigurationFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      this.updateModel();
    });
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.deviceProfileTransportConfigurationFormGroup.disable({emitEvent: false});
    } else {
      this.deviceProfileTransportConfigurationFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (DeviceProfileTransportConfiguration | null)
   */

  writeValue(value: DeviceProfileTransportConfiguration | null): void {
    this.transportType = value?.type;
    const configuration = deepClone(value);
    if (configuration) {
      delete configuration.type;
    }
    setTimeout(() => {
      this.deviceProfileTransportConfigurationFormGroup.patchValue({configuration}, {emitEvent: this.isAdd});
    }, 0);
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    const configuration = this.deviceProfileTransportConfigurationFormGroup.getRawValue().configuration;
    configuration.type = this.transportType;
    this.propagateChange(configuration);
  }

  /**
   * validate.
   *
   * @param c c (UntypedFormControl)
   * @returns ValidationErrors | null observable or value
   */

  public validate(c: UntypedFormControl): ValidationErrors | null {
    return (this.deviceProfileTransportConfigurationFormGroup.valid) ? null : {
      configuration: {
        valid: false,
      },
    };
  }
}
