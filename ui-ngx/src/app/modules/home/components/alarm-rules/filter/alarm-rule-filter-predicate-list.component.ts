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

import { Component, DestroyRef, forwardRef, Input } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  FormArray,
  FormBuilder,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import { Observable, of } from 'rxjs';
import {
  ComplexOperation,
  complexOperationTranslationMap,
  EntityKeyValueType,
  entityKeyValueTypeToFilterPredicateType
} from '@shared/models/query/query.models';
import { MatDialog } from '@angular/material/dialog';
import { map } from 'rxjs/operators';
import {
  AlarmRuleComplexFilterPredicateDialogComponent,
  AlarmRuleComplexFilterPredicateDialogData
} from "@home/components/alarm-rules/filter/alarm-rule-complex-filter-predicate-dialog.component";
import {
  AlarmRuleBooleanOperation,
  AlarmRuleFilterPredicate,
  AlarmRuleFilterPredicateType,
  AlarmRuleNumericOperation,
  AlarmRulePredicateInfo,
  AlarmRuleStringOperation,
  ComplexAlarmRuleFilterPredicate
} from "@shared/models/alarm-rule.models";
import { CalculatedFieldArgument } from "@shared/models/calculated-field.models";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";


/**
 * Angular component: alarm rule filter predicate list (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-alarm-rule-filter-predicate-list`.
 */
@Component({
    selector: 'tb-alarm-rule-filter-predicate-list',
    templateUrl: './alarm-rule-filter-predicate-list.component.html',
    styleUrls: ['./alarm-rule-filter-predicate-list.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => AlarmRuleFilterPredicateListComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => AlarmRuleFilterPredicateListComponent),
            multi: true
        }
    ],
standalone: false
})
export class AlarmRuleFilterPredicateListComponent implements ControlValueAccessor, Validator {

  @Input() disabled: boolean;

  @Input() valueType: EntityKeyValueType;

  @Input() operation: ComplexOperation = ComplexOperation.AND;

  @Input() arguments: Record<string, CalculatedFieldArgument>;

  @Input() argumentInUse: string;

  @Input() readonly: boolean;

  filterListFormGroup = this.fb.group({
    predicates: this.fb.array([])
  });

  valueTypeEnum = EntityKeyValueType;

  complexOperationTranslations = complexOperationTranslationMap;

  private propagateChange= (v: any) => { };

  constructor(private fb: FormBuilder,
              private dialog: MatDialog,
              private destroyRef: DestroyRef) {
    this.filterListFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => this.updateModel());
  }

  get predicatesFormArray(): FormArray {
    return this.filterListFormGroup.get('predicates') as FormArray;
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
      this.filterListFormGroup.disable({emitEvent: false});
    } else {
      this.filterListFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * validate.
   *
   * @param control control (AbstractControl)
   * @returns ValidationErrors | null observable or value
   */

  validate(control: AbstractControl): ValidationErrors | null {
    return this.filterListFormGroup.valid ? null : {
      filterList: {valid: false}
    };
  }

  /**
   * write value.
   *
   * @param predicates predicates (Array<AlarmRulePredicateInfo>)
   */

  writeValue(predicates: Array<AlarmRulePredicateInfo>): void {
    const predicateControls: Array<AbstractControl> = [];
    if (predicates) {
      for (const predicate of predicates) {
        predicateControls.push(this.fb.control(predicate, [Validators.required]));
      }
    }
    this.predicatesFormArray.clear();
    predicateControls.forEach(predicate => this.predicatesFormArray.push(predicate));
  }

  /**
   * DELETE — remove predicate.
   *
   * @param index index (number)
   */

  public removePredicate(index: number) {
    this.predicatesFormArray.removeAt(index);
  }

  /**
   * POST/PUT entity — add predicate.
   *
   * @param complex complex (boolean)
   */

  public addPredicate(complex: boolean) {
    const predicatesFormArray = this.filterListFormGroup.get('predicates') as FormArray;
    const predicate = this.createDefaultFilterPredicate(this.valueType, complex);
    let observable: Observable<AlarmRuleFilterPredicate>;
    if (complex) {
      observable = this.openComplexFilterDialog(predicate as ComplexAlarmRuleFilterPredicate);
    } else {
      observable = of(predicate);
    }
    observable.subscribe((result) => {
      if (result) {
        predicatesFormArray.push(this.fb.control(result, [Validators.required]));
      }
    });
  }

  /**
   * POST/PUT entity — create default filter predicate.
   *
   * @param valueType value type (EntityKeyValueType)
   * @param complex complex (boolean)
   * @returns AlarmRuleFilterPredicate observable or value
   */

  private createDefaultFilterPredicate(valueType: EntityKeyValueType, complex: boolean): AlarmRuleFilterPredicate {
    const predicate = {
      type: complex ? AlarmRuleFilterPredicateType.COMPLEX : entityKeyValueTypeToFilterPredicateType(valueType)
    } as AlarmRuleFilterPredicate;
    switch (predicate.type) {
      case AlarmRuleFilterPredicateType.STRING:
        predicate.operation = AlarmRuleStringOperation.STARTS_WITH;
        predicate.value = {
          staticValue: ''
        };
        predicate.ignoreCase = false;
        break;
      case AlarmRuleFilterPredicateType.NUMERIC:
        predicate.operation = AlarmRuleNumericOperation.EQUAL;
        predicate.value = {
          staticValue: valueType === EntityKeyValueType.DATE_TIME ? Date.now() : 0
        };
        break;
      case AlarmRuleFilterPredicateType.BOOLEAN:
        predicate.operation = AlarmRuleBooleanOperation.EQUAL;
        predicate.value = {
          staticValue: false
        };
        break;
      case AlarmRuleFilterPredicateType.COMPLEX:
        predicate.operation = ComplexOperation.AND;
        predicate.predicates = [];
        break;
    }
    return predicate;
  }

  /**
   * open complex filter dialog.
   *
   * @param predicate predicate (ComplexAlarmRuleFilterPredicate)
   * @returns Observable<ComplexAlarmRuleFilterPredicate> observable or value
   */

  private openComplexFilterDialog(predicate: ComplexAlarmRuleFilterPredicate): Observable<ComplexAlarmRuleFilterPredicate> {
    return this.dialog.open<AlarmRuleComplexFilterPredicateDialogComponent, AlarmRuleComplexFilterPredicateDialogData,
      ComplexAlarmRuleFilterPredicate>(AlarmRuleComplexFilterPredicateDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        complexPredicate: predicate as ComplexAlarmRuleFilterPredicate,
        valueType: this.valueType,
        isAdd: true,
        arguments: this.arguments,
        argumentInUse: this.argumentInUse,
        readonly: this.readonly
      }
    }).afterClosed().pipe(
      map(result => result)
    );
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    this.propagateChange(this.filterListFormGroup.get('predicates').value);
  }
}
