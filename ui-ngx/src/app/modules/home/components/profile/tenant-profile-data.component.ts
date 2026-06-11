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
import { ControlValueAccessor, UntypedFormBuilder, UntypedFormGroup, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { TenantProfileData } from '@shared/models/tenant.model';
import { Subscription } from 'rxjs';


/**
 * Angular component: tenant profile data (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-tenant-profile-data`.
 */
@Component({
    selector: 'tb-tenant-profile-data',
    templateUrl: './tenant-profile-data.component.html',
    styleUrls: [],
    providers: [{
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => TenantProfileDataComponent),
            multi: true
        }],
standalone: false
})
export class TenantProfileDataComponent implements ControlValueAccessor, OnInit, OnDestroy {

  tenantProfileDataFormGroup: UntypedFormGroup;

  private requiredValue: boolean;
  get required(): boolean {
    return this.requiredValue;
  }
  @Input()
  set required(value: boolean) {
    this.requiredValue = coerceBooleanProperty(value);
  }

  @Input()
  disabled: boolean;

  private valueChange$: Subscription = null;
  private propagateChange = (v: any) => { };

  constructor(private store: Store<AppState>,
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
    this.tenantProfileDataFormGroup = this.fb.group({
      configuration: [null, Validators.required]
    });
    this.valueChange$ = this.tenantProfileDataFormGroup.valueChanges.subscribe(() => {
      this.updateModel();
    });
  }

  /**
   * Angular lifecycle hook: unsubscribe and release resources.
   *
   */

  ngOnDestroy() {
    if (this.valueChange$) {
      this.valueChange$.unsubscribe();
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
      this.tenantProfileDataFormGroup.disable({emitEvent: false});
    } else {
      this.tenantProfileDataFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (TenantProfileData | null)
   */

  writeValue(value: TenantProfileData | null): void {
    this.tenantProfileDataFormGroup.patchValue({configuration: value}, {emitEvent: false});
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    let tenantProfileData: TenantProfileData = null;
    if (this.tenantProfileDataFormGroup.valid) {
      tenantProfileData = this.tenantProfileDataFormGroup.getRawValue();
    }
    this.propagateChange(tenantProfileData?.configuration);
  }

}
