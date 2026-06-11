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

import { Component, DestroyRef, forwardRef, Injector, Input, OnInit } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  FormArray,
  FormBuilder,
  FormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  NgControl,
  ValidationErrors,
  Validator,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { coerceBoolean } from '@shared/public-api';
import { isEqual } from '@core/public-api';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: kv map config (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-kv-map-config`.
 */
@Component({
    selector: 'tb-kv-map-config',
    templateUrl: './kv-map-config.component.html',
    styleUrls: ['./kv-map-config.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => KvMapConfigComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => KvMapConfigComponent),
            multi: true,
        }
    ],
standalone: false
})
export class KvMapConfigComponent implements ControlValueAccessor, OnInit, Validator {

  private propagateChange: (value: any) => void = () => {};

  kvListFormGroup: FormGroup;
  ngControl: NgControl;

  @Input()
  @coerceBoolean()
  disabled = false;

  @Input()
  @coerceBoolean()
  uniqueKeyValuePairValidator = false;

  @Input() labelText: string;

  @Input() requiredText: string;

  @Input() keyText: string;

  @Input() keyRequiredText: string;

  @Input() valText: string;

  @Input() valRequiredText: string;

  @Input() hintText: string;

  @Input() emptyText: string;

  @Input() popupHelpLink: string;

  @Input()
  @coerceBoolean()
  required = false;

  constructor(private injector: Injector,
              private fb: FormBuilder,
              private destroyRef: DestroyRef) {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    this.ngControl = this.injector.get(NgControl);
    if (this.ngControl != null) {
      this.ngControl.valueAccessor = this;
    }

    this.kvListFormGroup = this.fb.group({
      keyVals: this.fb.array([])
    }, {validators: [this.propagateNestedErrors, this.oneMapRequiredValidator]});

    this.kvListFormGroup.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.updateModel();
      });
  }

  /**
   * key vals form array.
   *
   * @returns FormArray observable or value
   */

  keyValsFormArray(): FormArray {
    return this.kvListFormGroup.get('keyVals') as FormArray;
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
   * @param _fn  fn (any)
   */

  registerOnTouched(_fn: any): void {
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.kvListFormGroup.disable({emitEvent: false});
    } else {
      this.kvListFormGroup.enable({emitEvent: false});
    }
  }

  private duplicateValuesValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null =>
    control.controls.key.value === control.controls.value.value
      ? control.controls.key.value && control.controls.value.value ? { uniqueKeyValuePair: true } : null
      : null;

  private oneMapRequiredValidator: ValidatorFn = (control: FormGroup): ValidationErrors | null => control.get('keyVals').value.length;


  private propagateNestedErrors: ValidatorFn = (controls: FormArray | FormGroup | AbstractControl): ValidationErrors | null => {
    if (this.kvListFormGroup && this.kvListFormGroup.get('keyVals') && this.kvListFormGroup.get('keyVals')?.status === 'VALID') {
      return null;
    }
    const errors = {};
    if (this.kvListFormGroup) {
      this.kvListFormGroup.setErrors(null);
    }
    if (controls instanceof FormArray || controls instanceof FormGroup) {
      if (controls.errors) {
        for (const errorKey of Object.keys(controls.errors)) {
          errors[errorKey] = true;
        }
      }
      for (const control of Object.keys(controls.controls)) {
        const innerErrors = this.propagateNestedErrors(controls.controls[control]);
        if (innerErrors && Object.keys(innerErrors).length) {
          for (const errorKey of Object.keys(innerErrors)) {
            errors[errorKey] = true;
          }
        }
      }
      return errors;
    } else {
      if (controls.errors) {
        for (const errorKey of Object.keys(controls.errors)) {
          errors[errorKey] = true;
        }
      }
    }

    return !isEqual(errors, {}) ? errors : null;
  };

  /**
   * write value.
   *
   * @param keyValMap key val map ({ [key: string]: string })
   */

  writeValue(keyValMap: { [key: string]: string }): void {
    const keyValuesData = Object.keys(keyValMap ?? {}).map(key => ({key, value: keyValMap[key]}));
    if (this.keyValsFormArray().length === keyValuesData.length) {
      this.keyValsFormArray().patchValue(keyValuesData, {emitEvent: false});
    } else {
      const keyValsControls: Array<FormGroup> = [];
      keyValuesData.forEach(data => {
        keyValsControls.push(this.fb.group({
          key: [data.key, [Validators.required, Validators.pattern(/(?:.|\s)*\S(&:.|\s)*/)]],
          value: [data.value, [Validators.required, Validators.pattern(/(?:.|\s)*\S(&:.|\s)*/)]]
        }, {validators: this.uniqueKeyValuePairValidator ? [this.duplicateValuesValidator] : []}));
      });
      this.kvListFormGroup.setControl('keyVals', this.fb.array(keyValsControls, this.propagateNestedErrors), {emitEvent: false});
    }
  }

  /**
   * DELETE — remove key val.
   *
   * @param index index (number)
   */

  public removeKeyVal(index: number) {
    this.keyValsFormArray().removeAt(index);
  }

  /**
   * POST/PUT entity — add key val.
   *
   */

  public addKeyVal() {
    this.keyValsFormArray().push(this.fb.group({
      key: ['', [Validators.required, Validators.pattern(/(?:.|\s)*\S(&:.|\s)*/)]],
      value: ['', [Validators.required, Validators.pattern(/(?:.|\s)*\S(&:.|\s)*/)]]
    }, {validators: this.uniqueKeyValuePairValidator ? [this.duplicateValuesValidator] : []}));
  }

  /**
   * validate.
   *
   */

  public validate() {
    const kvList: { key: string; value: string }[] = this.kvListFormGroup.get('keyVals').value;
    if (!kvList.length && this.required) {
      return {
        kvMapRequired: true
      };
    }
    if (!this.kvListFormGroup.valid) {
      return {
        kvFieldsRequired: true
      };
    }
    if (this.uniqueKeyValuePairValidator) {
      for (const kv of kvList) {
        if (kv.key === kv.value) {
          return {
            uniqueKeyValuePair: true
          };
        }
      }
    }
    return null;
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    const kvList: { key: string; value: string }[] = this.kvListFormGroup.get('keyVals').value;
    if (this.required && !kvList.length || !this.kvListFormGroup.valid) {
      this.propagateChange(null);
    } else {
      const keyValMap: { [key: string]: string } = {};
      kvList.forEach((entry) => {
        keyValMap[entry.key] = entry.value;
      });
      this.propagateChange(keyValMap);
    }
  }
}
