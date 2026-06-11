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
import {
  TimeSeriesChartThreshold,
  timeSeriesChartThresholdDefaultSettings,
  timeSeriesChartThresholdValid,
  timeSeriesChartThresholdValidator,
  TimeSeriesChartYAxisId
} from '@home/components/widget/lib/chart/time-series-chart.models';
import { mergeDeep } from '@core/utils';
import { IAliasController } from '@core/api/widget-api.models';
import { DataKeysCallbacks } from '@home/components/widget/lib/settings/common/key/data-keys.component.models';
import { DataKey, Datasource, WidgetConfig } from '@shared/models/widget.models';
import { DataKeyType } from '@shared/models/telemetry/telemetry.models';
import { coerceBoolean } from '@shared/decorators/coercion';
import { ValueSourceType } from '@shared/models/widget-settings.models';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: time series chart thresholds panel (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-time-series-chart-thresholds-panel`.
 */
@Component({
    selector: 'tb-time-series-chart-thresholds-panel',
    templateUrl: './time-series-chart-thresholds-panel.component.html',
    styleUrls: ['./time-series-chart-thresholds-panel.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => TimeSeriesChartThresholdsPanelComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => TimeSeriesChartThresholdsPanelComponent),
            multi: true
        }
    ],
    encapsulation: ViewEncapsulation.None,
standalone: false
})
export class TimeSeriesChartThresholdsPanelComponent implements ControlValueAccessor, OnInit, Validator {

  @Input()
  disabled: boolean;

  @Input()
  aliasController: IAliasController;

  @Input()
  dataKeyCallbacks: DataKeysCallbacks;

  @Input()
  datasource: Datasource;

  @Input()
  widgetConfig: WidgetConfig;

  @Input()
  yAxisIds: TimeSeriesChartYAxisId[];

  @Input()
  @coerceBoolean()
  hideYAxis = false;

  @Input()
  @coerceBoolean()
  supportsUnitConversion = true;

  thresholdsFormGroup: UntypedFormGroup;

  private propagateChange = (_val: any) => {};

  constructor(private fb: UntypedFormBuilder,
              private destroyRef: DestroyRef) {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    this.thresholdsFormGroup = this.fb.group({
      thresholds: [this.fb.array([]), []]
    });
    this.thresholdsFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(
      () => {
        let thresholds: TimeSeriesChartThreshold[] = this.thresholdsFormGroup.get('thresholds').value;
        if (thresholds) {
          thresholds = thresholds.filter(t => timeSeriesChartThresholdValid(t));
        }
        this.updateLatestDataKeys(thresholds);
        this.propagateChange(thresholds);
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
      this.thresholdsFormGroup.disable({emitEvent: false});
    } else {
      this.thresholdsFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (TimeSeriesChartThreshold[] | undefined)
   */

  writeValue(value: TimeSeriesChartThreshold[] | undefined): void {
    const thresholds = this.checkLatestDataKeys(value || []);
    this.thresholdsFormGroup.setControl('thresholds', this.prepareThresholdsFormArray(thresholds), {emitEvent: false});
  }

  /**
   * validate.
   *
   * @param c c (UntypedFormControl)
   */

  public validate(c: UntypedFormControl) {
    const valid = this.thresholdsFormGroup.valid;
    return valid ? null : {
      thresholds: {
        valid: false,
      },
    };
  }

  /**
   * thresholds form array.
   *
   * @returns UntypedFormArray observable or value
   */

  thresholdsFormArray(): UntypedFormArray {
    return this.thresholdsFormGroup.get('thresholds') as UntypedFormArray;
  }

  /**
   * track by threshold.
   *
   * @param index index (number)
   * @param thresholdControl threshold control (AbstractControl)
   * @returns any observable or value
   */

  trackByThreshold(index: number, thresholdControl: AbstractControl): any {
    return thresholdControl;
  }

  /**
   * DELETE — remove threshold.
   *
   * @param index index (number)
   */

  removeThreshold(index: number) {
    (this.thresholdsFormGroup.get('thresholds') as UntypedFormArray).removeAt(index);
  }

  /**
   * POST/PUT entity — add threshold.
   *
   */

  addThreshold() {
    const threshold = mergeDeep<TimeSeriesChartThreshold>({} as TimeSeriesChartThreshold,
      timeSeriesChartThresholdDefaultSettings);
    const thresholdsArray = this.thresholdsFormGroup.get('thresholds') as UntypedFormArray;
    const thresholdControl = this.fb.control(threshold, [timeSeriesChartThresholdValidator]);
    thresholdsArray.push(thresholdControl);
  }

  /**
   * prepare thresholds form array.
   *
   * @param thresholds thresholds (TimeSeriesChartThreshold[] | undefined)
   * @returns UntypedFormArray observable or value
   */

  private prepareThresholdsFormArray(thresholds: TimeSeriesChartThreshold[] | undefined): UntypedFormArray {
    const thresholdsControls: Array<AbstractControl> = [];
    if (thresholds) {
      thresholds.forEach((threshold) => {
        thresholdsControls.push(this.fb.control(threshold, [timeSeriesChartThresholdValidator]));
      });
    }
    return this.fb.array(thresholdsControls);
  }

  /**
   * check latest data keys.
   *
   * @param thresholds thresholds (TimeSeriesChartThreshold[])
   * @returns TimeSeriesChartThreshold[] observable or value
   */

  private checkLatestDataKeys(thresholds: TimeSeriesChartThreshold[]): TimeSeriesChartThreshold[] {
    const result: TimeSeriesChartThreshold[] = [];
    const latestKeys = this.datasource?.latestDataKeys || [];
    for (const threshold of thresholds) {
      if (threshold.type === ValueSourceType.latestKey) {
        const found = latestKeys.find(k => this.isThresholdKey(k, threshold));
        if (found) {
          result.push(threshold);
        }
      } else {
        result.push(threshold);
      }
    }
    return result;
  }

  /**
   * update latest data keys.
   *
   * @param thresholds thresholds (TimeSeriesChartThreshold[])
   */

  private updateLatestDataKeys(thresholds: TimeSeriesChartThreshold[]) {
    if (this.datasource) {
      let latestKeys = this.datasource.latestDataKeys;
      if (!latestKeys) {
        latestKeys = [];
        this.datasource.latestDataKeys = latestKeys;
      }
      const existingThresholdKeys = latestKeys.filter(k => k.settings?.__thresholdKey === true);
      const foundThresholdKeys: DataKey[] = [];
      for (const threshold of thresholds) {
        if (threshold.type === ValueSourceType.latestKey) {
          const found = existingThresholdKeys.find(k => this.isThresholdKey(k, threshold));
          if (!found) {
            const newKey = this.dataKeyCallbacks.generateDataKey(threshold.latestKey, threshold.latestKeyType,
              null, true, null);
            newKey.settings.__thresholdKey = true;
            latestKeys.push(newKey);
          } else if (foundThresholdKeys.indexOf(found) === -1) {
            foundThresholdKeys.push(found);
          }
        }
      }
      const toRemove = existingThresholdKeys.filter(k => foundThresholdKeys.indexOf(k) === -1);
      for (const key of toRemove) {
        const index = latestKeys.indexOf(key);
        if (index > -1) {
          latestKeys.splice(index, 1);
        }
      }
    }
  }

  /**
   * is threshold key.
   *
   * @param d d (DataKey)
   * @param threshold threshold (TimeSeriesChartThreshold)
   * @returns boolean observable or value
   */

  private isThresholdKey(d: DataKey, threshold: TimeSeriesChartThreshold): boolean {
    return (d.type === DataKeyType.function && d.label === threshold.latestKey) ||
    (d.type !== DataKeyType.function && d.name === threshold.latestKey &&
      d.type === threshold.latestKeyType);
  }
}
