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
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator,
  Validators
} from '@angular/forms';
import { DeviceProfileAlarmRule, alarmRuleValidator } from '@shared/models/device.models';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { AlarmSeverity, alarmSeverityTranslations } from '@shared/models/alarm.models';
import { EntityId } from '@shared/models/id/entity-id';
import { takeUntil } from 'rxjs/operators';


/**
 * Angular component: create alarm rules (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-create-alarm-rules`.
 */
@Component({
    selector: 'tb-create-alarm-rules',
    templateUrl: './create-alarm-rules.component.html',
    styleUrls: ['./create-alarm-rules.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => CreateAlarmRulesComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => CreateAlarmRulesComponent),
            multi: true,
        }
    ],
standalone: false
})
export class CreateAlarmRulesComponent implements ControlValueAccessor, OnInit, Validator, OnDestroy {

  alarmSeverities = Object.keys(AlarmSeverity);
  alarmSeverityEnum = AlarmSeverity;

  alarmSeverityTranslationMap = alarmSeverityTranslations;

  @Input()
  disabled: boolean;

  @Input()
  deviceProfileId: EntityId;

  createAlarmRulesFormGroup: UntypedFormGroup;

  private usedSeverities: AlarmSeverity[] = [];

  private destroy$ = new Subject<void>();
  private propagateChange = (v: any) => { };

  constructor(private dialog: MatDialog,
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
    this.createAlarmRulesFormGroup = this.fb.group({
      createAlarmRules: this.fb.array([])
    });
    this.createAlarmRulesFormGroup.valueChanges.pipe(
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
   * @param createAlarmRules create alarm rules ({[severity: string]: DeviceProfileAlarmRule})
   */

  writeValue(createAlarmRules: {[severity: string]: DeviceProfileAlarmRule}): void {
    const createAlarmRulesControls: Array<AbstractControl> = [];
    if (createAlarmRules) {
      Object.keys(createAlarmRules).forEach((severity) => {
        const createAlarmRule = createAlarmRules[severity];
        if (severity === 'empty') {
          severity = null;
        }
        createAlarmRulesControls.push(this.fb.group({
          severity: [severity, Validators.required],
          alarmRule: [createAlarmRule, Validators.required]
        }));
      });
    }
    this.createAlarmRulesFormGroup.setControl('createAlarmRules', this.fb.array(createAlarmRulesControls), {emitEvent: false});
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
    (this.createAlarmRulesFormGroup.get('createAlarmRules') as UntypedFormArray).removeAt(index);
  }

  /**
   * POST/PUT entity — add create alarm rule.
   *
   */

  public addCreateAlarmRule() {
    const createAlarmRule: DeviceProfileAlarmRule = {
      condition: {
        condition: []
      }
    };
    const createAlarmRulesArray = this.createAlarmRulesFormGroup.get('createAlarmRules') as UntypedFormArray;
    createAlarmRulesArray.push(this.fb.group({
      severity: [this.getFirstUnusedSeverity(), Validators.required],
      alarmRule: [createAlarmRule, alarmRuleValidator]
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
   * @param c c (UntypedFormControl)
   */

  public validate(c: UntypedFormControl) {
    return (this.createAlarmRulesFormGroup.valid) ? null : {
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
    const value: {severity: string, alarmRule: DeviceProfileAlarmRule}[] = this.createAlarmRulesFormGroup.get('createAlarmRules').value;
    value.forEach((rule, index) => {
      this.usedSeverities[index] = AlarmSeverity[rule.severity];
    });
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    const value: {severity: string, alarmRule: DeviceProfileAlarmRule}[] = this.createAlarmRulesFormGroup.get('createAlarmRules').value;
    const createAlarmRules: {[severity: string]: DeviceProfileAlarmRule} = {};
    value.forEach(v => {
      createAlarmRules[v.severity] = v.alarmRule;
    });
    this.updateUsedSeverities();
    this.propagateChange(createAlarmRules);
  }
}
