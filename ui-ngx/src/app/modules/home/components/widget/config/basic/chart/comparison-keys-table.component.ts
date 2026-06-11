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

import { Component, DestroyRef, forwardRef, Input, OnInit, ViewEncapsulation } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  NG_VALUE_ACCESSOR,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormGroup
} from '@angular/forms';
import { DataKey, DatasourceType } from '@shared/models/widget.models';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: comparison keys table (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-comparison-keys-table`.
 */
@Component({
    selector: 'tb-comparison-keys-table',
    templateUrl: './comparison-keys-table.component.html',
    styleUrls: ['./comparison-keys-table.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => ComparisonKeysTableComponent),
            multi: true
        }
    ],
    encapsulation: ViewEncapsulation.None,
standalone: false
})
export class ComparisonKeysTableComponent implements ControlValueAccessor, OnInit {

  @Input()
  disabled: boolean;

  @Input()
  datasourceType: DatasourceType;

  keysListFormGroup: UntypedFormGroup;

  get noKeys(): boolean {
    const keys: DataKey[] = this.keysListFormGroup.get('keys').value;
    return keys.length === 0;
  }

  private propagateChange = (_val: any) => {};

  constructor(private fb: UntypedFormBuilder,
              private destroyRef: DestroyRef) {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    this.keysListFormGroup = this.fb.group({
      keys: [this.fb.array([]), []]
    });
    this.keysListFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(
      () => {
        const keys: DataKey[] = this.keysListFormGroup.get('keys').value;
        this.propagateChange(keys);
      }
    );
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
    if (isDisabled) {
      this.keysListFormGroup.disable({emitEvent: false});
    } else {
      this.keysListFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (DataKey[] | undefined)
   */

  writeValue(value: DataKey[] | undefined): void {
    this.keysListFormGroup.setControl('keys', this.prepareKeysFormArray(value), {emitEvent: false});
  }

  /**
   * keys form array.
   *
   * @returns UntypedFormArray observable or value
   */

  keysFormArray(): UntypedFormArray {
    return this.keysListFormGroup.get('keys') as UntypedFormArray;
  }

  /**
   * track by key.
   *
   * @param _index  index (number)
   * @param keyControl key control (AbstractControl)
   * @returns any observable or value
   */

  trackByKey(_index: number, keyControl: AbstractControl): any {
    return keyControl;
  }

  /**
   * prepare keys form array.
   *
   * @param keys keys (DataKey[] | undefined)
   * @returns UntypedFormArray observable or value
   */

  private prepareKeysFormArray(keys: DataKey[] | undefined): UntypedFormArray {
    const keysControls: Array<AbstractControl> = [];
    if (keys) {
      keys.forEach((key) => {
        keysControls.push(this.fb.control(key, []));
      });
    }
    return this.fb.array(keysControls);
  }

}
