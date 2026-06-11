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

import { Component, forwardRef, Input, OnInit } from '@angular/core';
import {
  ControlValueAccessor,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import {
  RateLimitsDetailsDialogComponent,
  RateLimitsDetailsDialogData
} from '@home/components/profile/tenant/rate-limits/rate-limits-details-dialog.component';
import {
  RateLimits,
  rateLimitsDialogTitleTranslationMap,
  rateLimitsLabelTranslationMap,
  RateLimitsType,
  stringToRateLimitsArray
} from './rate-limits.models';
import { isDefined } from '@core/utils';


/**
 * Angular component: rate limits (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-rate-limits`.
 */
@Component({
    selector: 'tb-rate-limits',
    templateUrl: './rate-limits.component.html',
    styleUrls: ['./rate-limits.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => RateLimitsComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => RateLimitsComponent),
            multi: true,
        }
    ],
standalone: false
})
export class RateLimitsComponent implements ControlValueAccessor, OnInit, Validator {

  @Input()
  disabled: boolean;

  @Input()
  type: RateLimitsType;

  label: string;

  rateLimitsFormGroup: UntypedFormGroup;

  get rateLimitsArray(): Array<RateLimits> {
    return this.rateLimitsFormGroup.get('rateLimits').value;
  }

  private modelValue: string;

  private propagateChange = null;

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
    this.label = rateLimitsLabelTranslationMap.get(this.type);
    this.rateLimitsFormGroup = this.fb.group({
      rateLimits: [null, []]
    });
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean) {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.rateLimitsFormGroup.disable({emitEvent: false});
    } else {
      this.rateLimitsFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (string)
   */

  writeValue(value: string) {
    this.modelValue = value;
    this.updateRateLimitsInfo();
  }

  /**
   * validate.
   *
   * @param c c (UntypedFormControl)
   */

  public validate(c: UntypedFormControl) {
    return null;
  }

  /**
   * Event handler for click.
   *
   */

  public onClick($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    const title = rateLimitsDialogTitleTranslationMap.get(this.type);
    this.dialog.open<RateLimitsDetailsDialogComponent, RateLimitsDetailsDialogData,
      string>(RateLimitsDetailsDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
      data: {
        rateLimits: this.modelValue,
        title,
        readonly: this.disabled
      }
    }).afterClosed().subscribe((result) => {
      if (isDefined(result)) {
        this.modelValue = result;
        this.updateModel();
      }
    });
  }

  /**
   * update rate limits info.
   *
   */

  private updateRateLimitsInfo() {
    this.rateLimitsFormGroup.patchValue(
      {
        rateLimits: stringToRateLimitsArray(this.modelValue)
      }
    );
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    this.updateRateLimitsInfo();
    this.propagateChange(this.modelValue);
  }

}
