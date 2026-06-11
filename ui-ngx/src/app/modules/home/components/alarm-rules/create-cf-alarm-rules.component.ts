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
  UntypedFormArray,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import { AlarmSeverity, alarmSeverityColors, alarmSeverityTranslations } from '@shared/models/alarm.models';
import { AlarmRule } from "@shared/models/alarm-rule.models";
import { CalculatedFieldArgument } from "@shared/models/calculated-field.models";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { coerceBoolean } from "@shared/decorators/coercion";
import { Observable } from "rxjs";


/**
 * Angular component: create cf alarm rules (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-create-cf-alarm-rules`.
 */
@Component({
    selector: 'tb-create-cf-alarm-rules',
    templateUrl: './create-cf-alarm-rules.component.html',
    styleUrls: ['./create-cf-alarm-rules.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => CreateCfAlarmRulesComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => CreateCfAlarmRulesComponent),
            multi: true,
        }
    ],
standalone: false
})
export class CreateCfAlarmRulesComponent implements ControlValueAccessor, Validator {

  @Input()
  @coerceBoolean()
  disabled: boolean;

  @Input()
  arguments: Record<string, CalculatedFieldArgument>;

  @Input({required: true})
  testScript: (expression: string) => Observable<string>;

  alarmSeverities = Object.keys(AlarmSeverity);
  alarmSeverityEnum = AlarmSeverity;
  alarmSeverityTranslationMap = alarmSeverityTranslations;

  AlarmSeverityNotificationColors = alarmSeverityColors;

  createAlarmRulesFormGroup = this.fb.group({
    createAlarmRules: this.fb.array<{severity: AlarmSeverity, alarmRule: AlarmRule}>([])
  });

  private usedSeverities: AlarmSeverity[] = [];

  private propagateChange = (v: any) => { };

  constructor(private fb: FormBuilder,
              private destroyRef: DestroyRef) {
    this.createAlarmRulesFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => this.updateModel());
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
   * POST/PUT entity — create alarm rules form array.
   *
   * @returns UntypedFormArray observable or value
   */

  createAlarmRulesFormArray(): UntypedFormArray {
    return this.createAlarmRulesFormGroup.get('createAlarmRules') as UntypedFormArray;
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.createAlarmRulesFormGroup.disable({emitEvent: false});
    } else {
      this.createAlarmRulesFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param createAlarmRules create alarm rules (Record<AlarmSeverity, AlarmRule>)
   */

  writeValue(createAlarmRules: Record<AlarmSeverity, AlarmRule>): void {
    const createAlarmRulesControls: Array<AbstractControl> = [];
    if (createAlarmRules) {
      Object.keys(createAlarmRules).forEach((severity) => {
        const createAlarmRule = createAlarmRules[severity];
        if (severity === 'empty') {
          severity = null;
        }
        createAlarmRulesControls.push(this.fb.group({
          severity: [severity, Validators.required],
          alarmRule: [{value: createAlarmRule, disabled: this.disabled}, Validators.required]
        }));
      });
    }
    const formArray = this.createAlarmRulesFormGroup.get('createAlarmRules') as FormArray;
    formArray.clear({emitEvent: false});
    createAlarmRulesControls.forEach(c => formArray.push(c, {emitEvent: false}));
    if (this.disabled) {
      this.createAlarmRulesFormGroup.disable({emitEvent: false});
    } else {
      this.createAlarmRulesFormGroup.enable({emitEvent: false});
    }
    this.updateUsedSeverities();
    if (!this.disabled && !this.createAlarmRulesFormGroup.valid) {
      this.updateModel();
    }
  }

  /**
   * DELETE — remove create alarm rule.
   *
   * @param index index (number)
   */

  public removeCreateAlarmRule(index: number) {
    (this.createAlarmRulesFormGroup.get('createAlarmRules') as FormArray).removeAt(index);
  }

  /**
   * POST/PUT entity — add create alarm rule.
   *
   */

  public addCreateAlarmRule() {
    const createAlarmRulesArray = this.createAlarmRulesFormGroup.get('createAlarmRules') as FormArray;
    createAlarmRulesArray.push(this.fb.group({
      severity: [this.getFirstUnusedSeverity(), Validators.required],
      alarmRule: [null, Validators.required]
    }));
    this.createAlarmRulesFormGroup.updateValueAndValidity();
    if (!this.createAlarmRulesFormGroup.valid) {
      this.updateModel();
    }
  }

  /**
   * get first unused severity.
   *
   * @returns AlarmSeverity observable or value
   */

  private getFirstUnusedSeverity(): AlarmSeverity {
    for (const severityKey of Object.keys(AlarmSeverity)) {
      const severity = AlarmSeverity[severityKey];
      if (this.usedSeverities.indexOf(severity) === -1) {
        return severity;
      }
    }
    return null;
  }

  /**
   * validate.
   *
   * @returns ValidationErrors | null observable or value
   */

  public validate(): ValidationErrors | null {
    return this.createAlarmRulesFormGroup.valid && this.createAlarmRulesFormArray().length > 0 ? null : {
      createAlarmRules: {
        valid: false,
      },
    };
  }

  /**
   * is disabled severity.
   *
   * @param severity severity (AlarmSeverity)
   * @param index index (number)
   * @returns boolean observable or value
   */

  public isDisabledSeverity(severity: AlarmSeverity, index: number): boolean {
    const usedIndex = this.usedSeverities.indexOf(severity);
    return usedIndex > -1 && usedIndex !== index;
  }

  /**
   * update used severities.
   *
   */

  private updateUsedSeverities() {
    this.usedSeverities = [];
    const value = this.createAlarmRulesFormGroup.get('createAlarmRules').value;
    value.forEach((rule, index) => {
      this.usedSeverities[index] = AlarmSeverity[rule.severity];
    });
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    const value = this.createAlarmRulesFormGroup.get('createAlarmRules').value;
    const createAlarmRules = {} as Record<AlarmSeverity, AlarmRule>;
    value.forEach(v => createAlarmRules[v.severity] = v.alarmRule);
    this.updateUsedSeverities();
    this.propagateChange(createAlarmRules);
  }
}
