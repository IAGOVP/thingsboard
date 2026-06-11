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
  FormArray,
  FormBuilder,
  FormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { Subject } from 'rxjs';
import { NonConfirmedNotificationEscalation } from '@shared/models/notification.models';
import { takeUntil } from 'rxjs/operators';
import { coerceBoolean } from '@shared/decorators/coercion';


/**
 * Angular component: escalations (home/notification pages).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-escalations-component`.
 */
@Component({
    selector: 'tb-escalations-component',
    templateUrl: './escalations.component.html',
    styleUrls: [],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => EscalationsComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => EscalationsComponent),
            multi: true,
        }
    ],
standalone: false
})
export class EscalationsComponent implements ControlValueAccessor, Validator, OnInit, OnDestroy {

  escalationsFormGroup: FormGroup;

  @Input()
  @coerceBoolean()
  required: boolean;

  @Input()
  disabled: boolean;

  private destroy$ = new Subject<void>();

  private propagateChange = (v: any) => { };

  constructor(private store: Store<AppState>,
              private fb: FormBuilder) {
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
   * Angular lifecycle hook: unsubscribe and release resources.
   *
   */

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
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
    this.escalationsFormGroup = this.fb.group({
      escalations: this.fb.array([])
    });

    this.escalationsFormGroup.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => this.updateModel());
  }

  get escalationsFormArray(): FormArray {
    return this.escalationsFormGroup.get('escalations') as FormArray;
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.escalationsFormGroup.disable({emitEvent: false});
    } else {
      this.escalationsFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param escalations escalations ({[key: string]: Array<string>} | null)
   */

  writeValue(escalations: {[key: string]: Array<string>} | null): void {
    const escalationParse: Array<NonConfirmedNotificationEscalation> = [];
    // eslint-disable-next-line guard-for-in
    for (const escalation in escalations) {
      escalationParse.push({delayInSec: Number(escalation) * 1000, targets: escalations[escalation]});
    }
    if (escalationParse.length === 0) {
      this.addEscalation(0);
    } else if (escalationParse?.length === this.escalationsFormArray.length) {
      this.escalationsFormArray.patchValue(escalationParse, {emitEvent: false});
    } else {
      const escalationsControls: Array<AbstractControl> = [];
      escalationParse.forEach(escalation => {
        escalationsControls.push(this.fb.control(escalation, [Validators.required]));
      });
      this.escalationsFormGroup.setControl('escalations', this.fb.array(escalationsControls), {emitEvent: false});
      if (this.disabled) {
        this.escalationsFormGroup.disable({emitEvent: false});
      } else {
        this.escalationsFormGroup.enable({emitEvent: false});
      }
    }
  }

  /**
   * DELETE — remove escalation.
   *
   * @param index index (number)
   */

  public removeEscalation(index: number) {
    (this.escalationsFormGroup.get('escalations') as FormArray).removeAt(index);
  }

  /**
   * POST/PUT entity — add escalation.
   *
   */

  public addEscalation(delay = 3600000) {
    const escalation = {
      delayInSec: delay,
      targets: null
    };
    const escalationArray = this.escalationsFormGroup.get('escalations') as FormArray;
    escalationArray.push(this.fb.control(escalation, []), {emitEvent: false});
  }

  /**
   * validate.
   *
   * @param c c (AbstractControl)
   * @returns ValidationErrors | null observable or value
   */

  public validate(c: AbstractControl): ValidationErrors | null {
    return this.escalationsFormGroup.valid ? null : {
      escalation: {
        valid: false,
      },
    };
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    const escalations = {};
    this.escalationsFormGroup.get('escalations').value.forEach(
      escalation => escalations[escalation.delayInSec / 1000] = escalation.targets
    );
    this.propagateChange(escalations);
  }
}
