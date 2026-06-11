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
  UntypedFormBuilder,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import {
  BooleanFilterPredicate,
  BooleanOperation,
  booleanOperationTranslationMap,
  EntityKeyValueType,
  FilterPredicateType
} from '@shared/models/query/query.models';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: boolean filter predicate (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-boolean-filter-predicate`.
 */
@Component({
    selector: 'tb-boolean-filter-predicate',
    templateUrl: './boolean-filter-predicate.component.html',
    styleUrls: ['./filter-predicate.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => BooleanFilterPredicateComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => BooleanFilterPredicateComponent),
            multi: true
        }
    ],
standalone: false
})
export class BooleanFilterPredicateComponent implements ControlValueAccessor, Validator, OnInit {

  @Input() disabled: boolean;

  @Input() allowUserDynamicSource = true;

  @Input() onlyUserDynamicSource = false;

  valueTypeEnum = EntityKeyValueType;

  booleanFilterPredicateFormGroup: UntypedFormGroup;

  booleanOperations = Object.keys(BooleanOperation);
  booleanOperationEnum = BooleanOperation;
  booleanOperationTranslations = booleanOperationTranslationMap;

  private propagateChange = null;

  constructor(private fb: UntypedFormBuilder,
              private destroyRef: DestroyRef) {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    this.booleanFilterPredicateFormGroup = this.fb.group({
      operation: [BooleanOperation.EQUAL, [Validators.required]],
      value: [null, [Validators.required]]
    });
    this.booleanFilterPredicateFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      this.updateModel();
    });
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
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.booleanFilterPredicateFormGroup.disable({emitEvent: false});
    } else {
      this.booleanFilterPredicateFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * validate.
   *
   * @returns ValidationErrors | null observable or value
   */

  validate(): ValidationErrors | null {
    return this.booleanFilterPredicateFormGroup ? null : {
      booleanFilterPredicate: {valid: false}
    };
  }

  /**
   * write value.
   *
   * @param predicate predicate (BooleanFilterPredicate)
   */

  writeValue(predicate: BooleanFilterPredicate): void {
    this.booleanFilterPredicateFormGroup.get('operation').patchValue(predicate.operation, {emitEvent: false});
    this.booleanFilterPredicateFormGroup.get('value').patchValue(predicate.value, {emitEvent: false});
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    let predicate: BooleanFilterPredicate = null;
    if (this.booleanFilterPredicateFormGroup.valid) {
      predicate = this.booleanFilterPredicateFormGroup.getRawValue();
      predicate.type = FilterPredicateType.BOOLEAN;
    }
    this.propagateChange(predicate);
  }

}
