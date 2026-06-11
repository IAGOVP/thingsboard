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
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import {
  DeviceProfileTransportConfiguration,
  DeviceTransportType,
  deviceTransportTypeTranslationMap
} from '@shared/models/device.models';
import { deepClone } from '@core/utils';
import {
  createSmsProviderConfiguration,
  SmsProviderConfiguration, smsProviderConfigurationValidator,
  SmsProviderType,
  smsProviderTypeTranslationMap
} from '@shared/models/settings.models';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: sms provider configuration (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-sms-provider-configuration`.
 */
@Component({
    selector: 'tb-sms-provider-configuration',
    templateUrl: './sms-provider-configuration.component.html',
    styleUrls: [],
    providers: [{
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => SmsProviderConfigurationComponent),
            multi: true
        }],
standalone: false
})
export class SmsProviderConfigurationComponent implements ControlValueAccessor, OnInit {

  smsProviderType = SmsProviderType;
  smsProviderTypes = Object.values(SmsProviderType);
  smsProviderTypeTranslations = smsProviderTypeTranslationMap;

  smsProviderConfigurationFormGroup: UntypedFormGroup;

  private requiredValue: boolean;
  get required(): boolean {
    return this.requiredValue;
  }
  @Input()
  set required(value: boolean) {
    this.requiredValue = coerceBooleanProperty(value);
  }

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
    this.smsProviderConfigurationFormGroup = this.fb.group({
      type: [null, Validators.required],
      configuration: [null, smsProviderConfigurationValidator(true)]
    });
    this.smsProviderConfigurationFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      this.updateModel();
    });
    this.smsProviderConfigurationFormGroup.get('type').valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      this.smsProviderTypeChanged();
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
      this.smsProviderConfigurationFormGroup.disable({emitEvent: false});
    } else {
      this.smsProviderConfigurationFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (SmsProviderConfiguration | null)
   */

  writeValue(value: SmsProviderConfiguration | null): void {
    const configuration = deepClone(value);
    const type = configuration?.type;
    if (configuration) {
      delete configuration.type;
    }
    this.smsProviderConfigurationFormGroup.patchValue({type}, {emitEvent: false});
    this.smsProviderConfigurationFormGroup.patchValue({configuration}, {emitEvent: false});
  }

  /**
   * sms provider type changed.
   *
   */

  private smsProviderTypeChanged() {
    const type: SmsProviderType = this.smsProviderConfigurationFormGroup.get('type').value;
    this.smsProviderConfigurationFormGroup.patchValue({configuration: createSmsProviderConfiguration(type)}, {emitEvent: false});
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    let configuration: SmsProviderConfiguration = null;
    if (this.smsProviderConfigurationFormGroup.valid) {
      configuration = this.smsProviderConfigurationFormGroup.getRawValue().configuration;
      configuration.type = this.smsProviderConfigurationFormGroup.getRawValue().type;
    }
    this.propagateChange(configuration);
  }
}
