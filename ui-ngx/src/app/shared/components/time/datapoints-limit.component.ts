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
  ControlValueAccessor,
  FormBuilder,
  FormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { TimeService } from '@core/services/time.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { isDefined } from '@core/utils';


/**
 * Angular component: datapoints limit (shared UI components).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-datapoints-limit`.
 */
@Component({
    selector: 'tb-datapoints-limit',
    templateUrl: './datapoints-limit.component.html',
    styleUrls: ['./datapoints-limit.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DatapointsLimitComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => DatapointsLimitComponent),
            multi: true
        }
    ],
standalone: false
})
export class DatapointsLimitComponent implements ControlValueAccessor, Validator, OnInit, OnDestroy {

  datapointsLimitFormGroup: FormGroup;

  modelValue: number | null;

  private requiredValue: boolean;
  get required(): boolean {
    return this.requiredValue;
  }
  @Input()
  set required(value: boolean) {
    const newVal = coerceBooleanProperty(value);
    if (this.requiredValue !== newVal) {
      this.requiredValue = newVal;
      this.updateValidators();
    }
  }

  @Input()
  disabled: boolean;

  private propagateChangeValue: any;

  private propagateChange = (v: any) => {
    this.propagateChangeValue = v;
  };

  private destroy$ = new Subject<void>();

  constructor(private fb: FormBuilder,
              private timeService: TimeService) {
  }

  /**
   * register on change.
   *
   * @param fn fn (any)
   */

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
    if (isDefined(this.propagateChangeValue)) {
      this.propagateChange(this.propagateChangeValue);
    }
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
    this.datapointsLimitFormGroup = this.fb.group({
      limit: [null, [Validators.min(this.minDatapointsLimit()), Validators.max(this.maxDatapointsLimit())]]
    });
    this.datapointsLimitFormGroup.get('limit').valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe((value) => {
      this.updateView(value);
    });
  }

  /**
   * update validators.
   *
   */

  updateValidators() {
    if (this.datapointsLimitFormGroup) {
      if (this.required) {
        this.datapointsLimitFormGroup.get('limit').addValidators(Validators.required);
      } else {
        this.datapointsLimitFormGroup.get('limit').removeValidators(Validators.required);
      }
      this.datapointsLimitFormGroup.get('limit').updateValueAndValidity();
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
      this.datapointsLimitFormGroup.disable({emitEvent: false});
    } else {
      this.datapointsLimitFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (number | null)
   */

  writeValue(value: number | null): void {
    this.modelValue = value;
    let limit = this.modelValue;
    if (!limit) {
      limit = Math.ceil(this.maxDatapointsLimit() / 2);
    } else if (limit < this.minDatapointsLimit()) {
      limit = this.minDatapointsLimit();
    } else if (limit > this.maxDatapointsLimit()) {
      limit = this.maxDatapointsLimit();
    }

    this.updateView(limit);
    this.datapointsLimitFormGroup.patchValue(
      { limit: limit }, {emitEvent: false}
    );
  }

  /**
   * update view.
   *
   * @param value value (number | null)
   */

  updateView(value: number | null) {
    if (this.modelValue !== value) {
      this.modelValue = value;
      this.propagateChange(this.modelValue);
    }
  }

  /**
   * validate.
   *
   * @returns ValidationErrors observable or value
   */

  validate(): ValidationErrors {
    return this.datapointsLimitFormGroup.get('limit').valid ? null : {
      datapointsLimitFormGroup: false,
    };
  }

  /**
   * min datapoints limit.
   *
   */

  minDatapointsLimit() {
    return this.timeService.getMinDatapointsLimit();
  }

  /**
   * max datapoints limit.
   *
   */

  maxDatapointsLimit() {
    return this.timeService.getMaxDatapointsLimit();
  }

  /**
   * Angular lifecycle hook: unsubscribe and release resources.
   *
   */

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
