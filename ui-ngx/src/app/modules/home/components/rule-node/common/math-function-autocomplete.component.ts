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

import { Component, ElementRef, forwardRef, Input, OnInit, ViewChild } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { FunctionData, MathFunction, MathFunctionMap } from '../rule-node-config.models';
import { map, tap } from 'rxjs/operators';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { MatFormFieldAppearance } from '@angular/material/form-field';


/**
 * Angular component: math function autocomplete (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-math-function-autocomplete`.
 */
@Component({
    selector: 'tb-math-function-autocomplete',
    templateUrl: './math-function-autocomplete.component.html',
    styleUrls: [],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => MathFunctionAutocompleteComponent),
            multi: true
        }
    ],
standalone: false
})
export class MathFunctionAutocompleteComponent implements ControlValueAccessor, OnInit {

  private requiredValue: boolean;

  get required(): boolean {
    return this.requiredValue;
  }

  @Input()
  set required(value: boolean) {
    this.requiredValue = coerceBooleanProperty(value);
  }

  @Input() disabled: boolean;

  @Input()
  appearance: MatFormFieldAppearance = 'fill';

  @ViewChild('operationInput', {static: true}) operationInput: ElementRef;

  mathFunctionForm: UntypedFormGroup;

  modelValue: MathFunction | null;

  searchText = '';

  filteredOptions: Observable<FunctionData[]>;

  private dirty = false;

  private mathOperation = [...MathFunctionMap.values()];

  private propagateChange = null;

  constructor(public translate: TranslateService,
              private fb: UntypedFormBuilder) {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    this.mathFunctionForm = this.fb.group({
      operation: ['']
    });
    this.filteredOptions = this.mathFunctionForm.get('operation').valueChanges.pipe(
      /**
       * tap.
       *
       */
      tap(value => {
        let modelValue: MathFunction;
        if (typeof value === 'string' && MathFunction[value]) {
          modelValue = MathFunction[value];
        } else {
          modelValue = null;
        }
        this.updateView(modelValue);
      }),
      map(value => {
        this.searchText = value || '';
        return value ? this._filter(value) : this.mathOperation.slice();
      }),
    );
  }

  private _filter(searchText: string) {
    const filterValue = searchText.toLowerCase();

    return this.mathOperation.filter(option => option.name.toLowerCase().includes(filterValue)
      || option.value.toLowerCase().includes(filterValue));
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
      this.mathFunctionForm.disable({emitEvent: false});
    } else {
      this.mathFunctionForm.enable({emitEvent: false});
    }
  }

  /**
   * math function display fn.
   *
   * @param value value (MathFunction | null)
   */

  mathFunctionDisplayFn(value: MathFunction | null) {
    if (value) {
      const funcData = MathFunctionMap.get(value)
      return funcData.value + ' | ' + funcData.name;
    }
    return '';
  }

  /**
   * write value.
   *
   * @param value value (MathFunction | null)
   */

  writeValue(value: MathFunction | null): void {
    this.modelValue = value;
    this.mathFunctionForm.get('operation').setValue(value, {emitEvent: false});
    this.dirty = true;
  }

  /**
   * update view.
   *
   * @param value value (MathFunction | null)
   */

  updateView(value: MathFunction | null) {
    if (this.modelValue !== value) {
      this.modelValue = value;
      this.propagateChange(this.modelValue);
    }
  }

  /**
   * Event handler for focus.
   *
   */

  onFocus() {
    if (this.dirty) {
      this.mathFunctionForm.get('operation').updateValueAndValidity({onlySelf: true});
      this.dirty = false;
    }
  }

  /**
   * clear.
   *
   */

  clear() {
    this.mathFunctionForm.get('operation').patchValue('');
    setTimeout(() => {
      this.operationInput.nativeElement.blur();
      this.operationInput.nativeElement.focus();
    }, 0);
  }

}
