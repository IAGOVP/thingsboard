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
  UntypedFormControl,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator
} from '@angular/forms';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { TranslateService } from '@ngx-translate/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { widgetTitleAutocompleteValues } from '@app/shared/public-api';

export interface UpdateAttributeGeneralSettings {
  widgetTitle: string;
  showLabel: boolean;
  labelValue?: string;
  isRequired: boolean;
  requiredErrorMessage: string;
  showResultMessage: boolean;
}

export function updateAttributeGeneralDefaultSettings(hasLabelValue = true): UpdateAttributeGeneralSettings {
  const updateAttributeGeneralSettings: UpdateAttributeGeneralSettings = {
    widgetTitle: '',
    showLabel: true,
    isRequired: true,
    requiredErrorMessage: '',
    showResultMessage: true
  };
  if (hasLabelValue) {
    updateAttributeGeneralSettings.labelValue = '';
  }
  return updateAttributeGeneralSettings;
}


/**
 * Angular component: update attribute general settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-update-attribute-general-settings`.
 */
@Component({
    selector: 'tb-update-attribute-general-settings',
    templateUrl: './update-attribute-general-settings.component.html',
    styleUrls: ['./../widget-settings.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => UpdateAttributeGeneralSettingsComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => UpdateAttributeGeneralSettingsComponent),
            multi: true
        }
    ],
standalone: false
})
export class UpdateAttributeGeneralSettingsComponent extends PageComponent implements OnInit, ControlValueAccessor, Validator {

  @Input()
  disabled: boolean;

  @Input()
  hasLabelValue = true;
  
  predefinedValues = widgetTitleAutocompleteValues;

  private modelValue: UpdateAttributeGeneralSettings;

  private propagateChange = null;

  public updateAttributeGeneralSettingsFormGroup: UntypedFormGroup;

  constructor(protected store: Store<AppState>,
              private translate: TranslateService,
              private fb: UntypedFormBuilder,
              private destroyRef: DestroyRef) {
    super(store);
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    this.updateAttributeGeneralSettingsFormGroup = this.fb.group({
      widgetTitle: ['', []],
      showLabel: [true, []],
      isRequired: [true, []],
      requiredErrorMessage: ['', []],
      showResultMessage: [true, []]
    });
    if (this.hasLabelValue) {
      this.updateAttributeGeneralSettingsFormGroup.addControl('labelValue', this.fb.control('', []));
      this.updateAttributeGeneralSettingsFormGroup.get('showLabel').valueChanges.pipe(
        takeUntilDestroyed(this.destroyRef)
      ).subscribe(() => {
        this.updateValidators(true);
      });
    }
    this.updateAttributeGeneralSettingsFormGroup.get('isRequired').valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      this.updateValidators(true);
    });
    this.updateAttributeGeneralSettingsFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      this.updateModel();
    });
    this.updateValidators(false);
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
    if (isDisabled) {
      this.updateAttributeGeneralSettingsFormGroup.disable({emitEvent: false});
    } else {
      this.updateAttributeGeneralSettingsFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (UpdateAttributeGeneralSettings)
   */

  writeValue(value: UpdateAttributeGeneralSettings): void {
    this.modelValue = value;
    this.updateAttributeGeneralSettingsFormGroup.patchValue(
      value, {emitEvent: false}
    );
    this.updateValidators(false);
  }

  /**
   * validate.
   *
   * @param c c (UntypedFormControl)
   */

  public validate(c: UntypedFormControl) {
    return this.updateAttributeGeneralSettingsFormGroup.valid ? null : {
      updateAttributeGeneralSettings: {
        valid: false,
      },
    };
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    const value: UpdateAttributeGeneralSettings = this.updateAttributeGeneralSettingsFormGroup.value;
    this.modelValue = value;
    this.propagateChange(this.modelValue);
  }

  /**
   * update validators.
   *
   * @param emitEvent emit event (boolean)
   */

  private updateValidators(emitEvent?: boolean): void {
    if (this.hasLabelValue) {
      const showLabel: boolean = this.updateAttributeGeneralSettingsFormGroup.get('showLabel').value;
      if (showLabel) {
        this.updateAttributeGeneralSettingsFormGroup.get('labelValue').enable({emitEvent});
      } else {
        this.updateAttributeGeneralSettingsFormGroup.get('labelValue').disable({emitEvent});
      }
      this.updateAttributeGeneralSettingsFormGroup.get('labelValue').updateValueAndValidity({emitEvent: false});
    }

    const isRequired: boolean = this.updateAttributeGeneralSettingsFormGroup.get('isRequired').value;
    if (isRequired) {
      this.updateAttributeGeneralSettingsFormGroup.get('requiredErrorMessage').enable({emitEvent});
    } else {
      this.updateAttributeGeneralSettingsFormGroup.get('requiredErrorMessage').disable({emitEvent});
    }
    this.updateAttributeGeneralSettingsFormGroup.get('requiredErrorMessage').updateValueAndValidity({emitEvent: false});
  }
}
