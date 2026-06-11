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
import {
  AbstractControl,
  ControlValueAccessor,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import { SnmpMapping } from '@shared/models/device.models';
import { Subject } from 'rxjs';
import { DataType, DataTypeTranslationMap } from '@shared/models/constants';
import { isUndefinedOrNull } from '@core/utils';
import { takeUntil } from 'rxjs/operators';


/**
 * Angular component: snmp device profile mapping (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-snmp-device-profile-mapping`.
 */
@Component({
    selector: 'tb-snmp-device-profile-mapping',
    templateUrl: './snmp-device-profile-mapping.component.html',
    styleUrls: ['./snmp-device-profile-mapping.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => SnmpDeviceProfileMappingComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => SnmpDeviceProfileMappingComponent),
            multi: true
        }
    ],
standalone: false
})
export class SnmpDeviceProfileMappingComponent implements OnInit, OnDestroy, ControlValueAccessor, Validator {

  mappingsConfigForm: UntypedFormGroup;

  dataTypes = Object.values(DataType);
  dataTypesTranslationMap = DataTypeTranslationMap;

  @Input()
  disabled: boolean;

  private readonly oidPattern: RegExp  = /^\.?([0-2])((\.0)|(\.[1-9][0-9]*))*$/;

  private destroy$ = new Subject<void>();
  private propagateChange = (v: any) => { };

  constructor(private fb: UntypedFormBuilder) { }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    this.mappingsConfigForm = this.fb.group({
      mappings: this.fb.array([])
    });
    this.mappingsConfigForm.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => this.updateModel());
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
   * register on change.
   *
   * @param fn fn (any)
   */

  registerOnChange(fn: any) {
    this.propagateChange = fn;
  }

  /**
   * register on touched.
   *
   * @param fn fn (any)
   */

  registerOnTouched(fn: any) {
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean) {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.mappingsConfigForm.disable({emitEvent: false});
    } else {
      this.mappingsConfigForm.enable({emitEvent: false});
    }
  }

  /**
   * validate.
   *
   * @returns ValidationErrors | null observable or value
   */

  validate(): ValidationErrors | null {
    return this.mappingsConfigForm.valid && this.mappingsConfigForm.value.mappings.length ? null : {
      mapping: false
    };
  }

  /**
   * write value.
   *
   * @param mappings mappings (SnmpMapping[])
   */

  writeValue(mappings: SnmpMapping[]) {
    if (mappings?.length === this.mappingsConfigFormArray.length) {
      this.mappingsConfigFormArray.patchValue(mappings, {emitEvent: false});
    } else {
      const mappingsControl: Array<AbstractControl> = [];
      if (mappings) {
        mappings.forEach((config) => {
          mappingsControl.push(this.createdFormGroup(config));
        });
      }
      this.mappingsConfigForm.setControl('mappings', this.fb.array(mappingsControl), {emitEvent: false});
      if (!mappings || !mappings.length) {
        this.addMappingConfig();
      }
      if (this.disabled) {
        this.mappingsConfigForm.disable({emitEvent: false});
      } else {
        this.mappingsConfigForm.enable({emitEvent: false});
      }
    }
    if (!this.disabled && !this.mappingsConfigForm.valid) {
      this.updateModel();
    }
  }

  get mappingsConfigFormArray(): UntypedFormArray {
    return this.mappingsConfigForm.get('mappings') as UntypedFormArray;
  }

  /**
   * POST/PUT entity — add mapping config.
   *
   */

  public addMappingConfig() {
    this.mappingsConfigFormArray.push(this.createdFormGroup());
    this.mappingsConfigForm.updateValueAndValidity();
    if (!this.mappingsConfigForm.valid) {
      this.updateModel();
    }
  }

  /**
   * DELETE — remove mapping config.
   *
   * @param index index (number)
   */

  public removeMappingConfig(index: number) {
    this.mappingsConfigFormArray.removeAt(index);
  }

  /**
   * POST/PUT entity — created form group.
   *
   * @param value value (SnmpMapping)
   * @returns UntypedFormGroup observable or value
   */

  private createdFormGroup(value?: SnmpMapping): UntypedFormGroup {
    if (isUndefinedOrNull(value)) {
      value = {
        dataType: DataType.STRING,
        key: '',
        oid: ''
      };
    }
    return this.fb.group({
      dataType: [value.dataType, Validators.required],
      key: [value.key, Validators.required],
      oid: [value.oid, [Validators.required, Validators.pattern(this.oidPattern)]]
    });
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    const value: SnmpMapping[] = this.mappingsConfigForm.get('mappings').value;
    this.propagateChange(value);
  }

}
