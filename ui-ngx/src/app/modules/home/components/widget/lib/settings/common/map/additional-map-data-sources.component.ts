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
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validator
} from '@angular/forms';
import { mergeDeep } from '@core/utils';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  AdditionalMapDataSourceSettings,
  additionalMapDataSourceValid,
  additionalMapDataSourceValidator,
  defaultAdditionalMapDataSourceSettings
} from '@shared/models/widget/maps/map.models';
import { MapSettingsContext } from '@home/components/widget/lib/settings/common/map/map-settings.component.models';


/**
 * Angular component: additional map data sources (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-additional-map-data-sources`.
 */
@Component({
    selector: 'tb-additional-map-data-sources',
    templateUrl: './additional-map-data-sources.component.html',
    styleUrls: ['./additional-map-data-sources.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => AdditionalMapDataSourcesComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => AdditionalMapDataSourcesComponent),
            multi: true
        }
    ],
    encapsulation: ViewEncapsulation.None,
standalone: false
})
export class AdditionalMapDataSourcesComponent implements ControlValueAccessor, OnInit, Validator {

  @Input()
  disabled: boolean;

  @Input()
  context: MapSettingsContext;

  dataSourcesFormGroup: UntypedFormGroup;

  private propagateChange = (_val: any) => {};

  constructor(private fb: UntypedFormBuilder,
              private destroyRef: DestroyRef) {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    this.dataSourcesFormGroup = this.fb.group({
      dataSources: [this.fb.array([]), []]
    });
    this.dataSourcesFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(
      () => {
        let dataSources: AdditionalMapDataSourceSettings[] = this.dataSourcesFormGroup.get('dataSources').value;
        if (dataSources) {
          dataSources = dataSources.filter(dataSource => additionalMapDataSourceValid(dataSource));
        }
        this.propagateChange(dataSources);
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
      this.dataSourcesFormGroup.disable({emitEvent: false});
    } else {
      this.dataSourcesFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (AdditionalMapDataSourceSettings[] | undefined)
   */

  writeValue(value: AdditionalMapDataSourceSettings[] | undefined): void {
    const dataSources: AdditionalMapDataSourceSettings[] = value || [];
    this.dataSourcesFormGroup.setControl('dataSources', this.prepareDataSourcesFormArray(dataSources), {emitEvent: false});
  }

  /**
   * validate.
   *
   * @param c c (UntypedFormControl)
   */

  public validate(c: UntypedFormControl) {
    const valid = this.dataSourcesFormGroup.valid;
    return valid ? null : {
      dataSources: {
        valid: false,
      },
    };
  }

  /**
   * data sources form array.
   *
   * @returns UntypedFormArray observable or value
   */

  dataSourcesFormArray(): UntypedFormArray {
    return this.dataSourcesFormGroup.get('dataSources') as UntypedFormArray;
  }

  /**
   * track by data source.
   *
   * @param index index (number)
   * @param dataSourceControl data source control (AbstractControl)
   * @returns any observable or value
   */

  trackByDataSource(index: number, dataSourceControl: AbstractControl): any {
    return dataSourceControl;
  }

  /**
   * DELETE — remove data source.
   *
   * @param index index (number)
   */

  removeDataSource(index: number) {
    (this.dataSourcesFormGroup.get('dataSources') as UntypedFormArray).removeAt(index);
  }

  /**
   * POST/PUT entity — add data source.
   *
   */

  addDataSource() {
    const dataSource = mergeDeep<AdditionalMapDataSourceSettings>({} as AdditionalMapDataSourceSettings,
      defaultAdditionalMapDataSourceSettings(this.context.functionsOnly));
    const dataSourcesArray = this.dataSourcesFormGroup.get('dataSources') as UntypedFormArray;
    const dataSourceControl = this.fb.control(dataSource, [additionalMapDataSourceValidator]);
    dataSourcesArray.push(dataSourceControl);
  }

  /**
   * prepare data sources form array.
   *
   * @param dataSources data sources (AdditionalMapDataSourceSettings[])
   * @returns UntypedFormArray observable or value
   */

  private prepareDataSourcesFormArray(dataSources: AdditionalMapDataSourceSettings[]): UntypedFormArray {
    const dataSourcesControls: Array<AbstractControl> = [];
    dataSources.forEach((dataSource) => {
      dataSourcesControls.push(this.fb.control(dataSource, [additionalMapDataSourceValidator]));
    });
    return this.fb.array(dataSourcesControls);
  }
}
