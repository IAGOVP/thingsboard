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
import { ControlValueAccessor, UntypedFormBuilder, UntypedFormGroup, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import {
  DefaultDeviceProfileConfiguration,
  DeviceProfileConfiguration,
  DeviceProfileType
} from '@shared/models/device.models';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: default device profile configuration (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-default-device-profile-configuration`.
 */
@Component({
    selector: 'tb-default-device-profile-configuration',
    templateUrl: './default-device-profile-configuration.component.html',
    styleUrls: [],
    providers: [{
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DefaultDeviceProfileConfigurationComponent),
            multi: true
        }],
standalone: false
})
export class DefaultDeviceProfileConfigurationComponent implements ControlValueAccessor, OnInit {

  defaultDeviceProfileConfigurationFormGroup: UntypedFormGroup;

  @Input()
  disabled: boolean;

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
    this.defaultDeviceProfileConfigurationFormGroup = this.fb.group({
      configuration: [null, Validators.required]
    });
    this.defaultDeviceProfileConfigurationFormGroup.valueChanges.pipe(
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
      this.defaultDeviceProfileConfigurationFormGroup.disable({emitEvent: false});
    } else {
      this.defaultDeviceProfileConfigurationFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (DefaultDeviceProfileConfiguration | null)
   */

  writeValue(value: DefaultDeviceProfileConfiguration | null): void {
    this.defaultDeviceProfileConfigurationFormGroup.patchValue({configuration: value}, {emitEvent: false});
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    let configuration: DeviceProfileConfiguration = null;
    if (this.defaultDeviceProfileConfigurationFormGroup.valid) {
      configuration = this.defaultDeviceProfileConfigurationFormGroup.getRawValue().configuration;
      configuration.type = DeviceProfileType.DEFAULT;
    }
    this.propagateChange(configuration);
  }
}
