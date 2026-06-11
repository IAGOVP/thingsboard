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
import { ControlValueAccessor, UntypedFormBuilder, UntypedFormGroup, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { DeviceProfileConfiguration, DeviceProfileType } from '@shared/models/device.models';
import { deepClone } from '@core/utils';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';


/**
 * Angular component: device profile configuration (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-device-profile-configuration`.
 */
@Component({
    selector: 'tb-device-profile-configuration',
    templateUrl: './device-profile-configuration.component.html',
    styleUrls: [],
    providers: [{
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DeviceProfileConfigurationComponent),
            multi: true
        }],
standalone: false
})
export class DeviceProfileConfigurationComponent implements ControlValueAccessor, OnInit, OnDestroy {

  deviceProfileType = DeviceProfileType;

  deviceProfileConfigurationFormGroup: UntypedFormGroup;

  private destroy$ = new Subject<void>();

  @Input()
  disabled: boolean;

  type: DeviceProfileType;

  private propagateChange = (v: any) => { };

  constructor(private store: Store<AppState>,
              private fb: UntypedFormBuilder) {
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
    this.deviceProfileConfigurationFormGroup = this.fb.group({
      configuration: [null, Validators.required]
    });
    this.deviceProfileConfigurationFormGroup.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.updateModel();
    });
  }

  /**
   * Angular lifecycle hook: unsubscribe and release resources.
   *
   */

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.deviceProfileConfigurationFormGroup.disable({emitEvent: false});
    } else {
      this.deviceProfileConfigurationFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (DeviceProfileConfiguration | null)
   */

  writeValue(value: DeviceProfileConfiguration | null): void {
    this.type = value?.type;
    const configuration = deepClone(value);
    if (configuration) {
      delete configuration.type;
    }
    this.deviceProfileConfigurationFormGroup.patchValue({configuration}, {emitEvent: false});
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    let configuration: DeviceProfileConfiguration = null;
    if (this.deviceProfileConfigurationFormGroup.valid) {
      configuration = this.deviceProfileConfigurationFormGroup.getRawValue().configuration;
      configuration.type = this.type;
    }
    this.propagateChange(configuration);
  }
}
