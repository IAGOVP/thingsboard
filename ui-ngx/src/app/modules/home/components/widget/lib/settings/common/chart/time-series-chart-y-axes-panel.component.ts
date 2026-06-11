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

import {
  Component,
  DestroyRef,
  EventEmitter,
  forwardRef,
  Input,
  OnInit,
  Output,
  ViewEncapsulation
} from '@angular/core';
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
  checkLatestDataKeys,
  defaultTimeSeriesChartYAxisSettings,
  getNextTimeSeriesYAxisId,
  TimeSeriesChartYAxes,
  TimeSeriesChartYAxisId,
  TimeSeriesChartYAxisSettings,
  timeSeriesChartYAxisValid,
  timeSeriesChartYAxisValidator,
  updateLatestDataKeys
} from '@home/components/widget/lib/chart/time-series-chart.models';
import { mergeDeep } from '@core/utils';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { coerceBoolean } from '@shared/decorators/coercion';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { IAliasController } from '@app/core/public-api';
import { DataKeysCallbacks } from '@home/components/widget/lib/settings/common/key/data-keys.component.models';
import { Datasource } from '@app/shared/public-api';


/**
 * Angular component: time series chart yaxes panel (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-time-series-chart-y-axes-panel`.
 */
@Component({
    selector: 'tb-time-series-chart-y-axes-panel',
    templateUrl: './time-series-chart-y-axes-panel.component.html',
    styleUrls: ['./time-series-chart-y-axes-panel.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => TimeSeriesChartYAxesPanelComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => TimeSeriesChartYAxesPanelComponent),
            multi: true
        }
    ],
    encapsulation: ViewEncapsulation.None,
standalone: false
})
export class TimeSeriesChartYAxesPanelComponent implements ControlValueAccessor, OnInit, Validator {

  @Input()
  aliasController: IAliasController;

  @Input()
  dataKeyCallbacks: DataKeysCallbacks;

  @Input()
  datasource: Datasource;

  @Input()
  disabled: boolean;

  @Input()
  @coerceBoolean()
  advanced = false;

  @Input()
  @coerceBoolean()
  supportsUnitConversion = false;

  @Output()
  axisRemoved = new EventEmitter<TimeSeriesChartYAxisId>();

  yAxesFormGroup: UntypedFormGroup;

  get dragEnabled(): boolean {
    return this.axesFormArray().controls.length > 1;
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
    this.yAxesFormGroup = this.fb.group({
      axes: [this.fb.array([]), []]
    });
    this.yAxesFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(
      () => {
        let axes: TimeSeriesChartYAxisSettings[] = this.yAxesFormGroup.get('axes').value;
        for (let i = 0; i < axes.length; i++) {
          axes[i].order = i;
        }
        if (axes) {
          axes = axes.filter(axis => timeSeriesChartYAxisValid(axis));
        }
        const yAxes: TimeSeriesChartYAxes = {};
        for (const axis of axes) {
          yAxes[axis.id] = axis;
        }
        updateLatestDataKeys(Object.values(yAxes), this.datasource, this.dataKeyCallbacks);
        this.propagateChange(yAxes);
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
      this.yAxesFormGroup.disable({emitEvent: false});
    } else {
      this.yAxesFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (TimeSeriesChartYAxes | undefined)
   */

  writeValue(value: TimeSeriesChartYAxes | undefined): void {
    const yAxes: TimeSeriesChartYAxes = checkLatestDataKeys(value || {}, this.datasource);
    if (!yAxes.default) {
      yAxes.default = mergeDeep({} as TimeSeriesChartYAxisSettings, defaultTimeSeriesChartYAxisSettings,
        {id: 'default', order: 0} as TimeSeriesChartYAxisSettings);
    }
    const yAxisSettingsList = Object.values(yAxes);
    yAxisSettingsList.sort((a1, a2) => a1.order - a2.order);
    this.yAxesFormGroup.setControl('axes', this.prepareAxesFormArray(yAxisSettingsList), {emitEvent: false});
  }

  /**
   * validate.
   *
   * @param c c (UntypedFormControl)
   */

  public validate(c: UntypedFormControl) {
    const valid = this.yAxesFormGroup.valid;
    return valid ? null : {
      yAxes: {
        valid: false,
      },
    };
  }

  /**
   * axis drop.
   *
   * @param event DOM or Angular event object
   */

  axisDrop(event: CdkDragDrop<string[]>) {
    const axesArray = this.yAxesFormGroup.get('axes') as UntypedFormArray;
    const axis = axesArray.at(event.previousIndex);
    axesArray.removeAt(event.previousIndex);
    axesArray.insert(event.currentIndex, axis);
  }

  /**
   * axes form array.
   *
   * @returns UntypedFormArray observable or value
   */

  axesFormArray(): UntypedFormArray {
    return this.yAxesFormGroup.get('axes') as UntypedFormArray;
  }

  /**
   * track by axis.
   *
   * @param index index (number)
   * @param axisControl axis control (AbstractControl)
   * @returns any observable or value
   */

  trackByAxis(index: number, axisControl: AbstractControl): any {
    return axisControl;
  }

  /**
   * DELETE — remove axis.
   *
   * @param index index (number)
   */

  removeAxis(index: number) {
    const axis =
      (this.yAxesFormGroup.get('axes') as UntypedFormArray).at(index).value as TimeSeriesChartYAxisSettings;
    (this.yAxesFormGroup.get('axes') as UntypedFormArray).removeAt(index);
    this.axisRemoved.emit(axis.id);
  }

  /**
   * POST/PUT entity — add axis.
   *
   */

  addAxis() {
    const axis = mergeDeep<TimeSeriesChartYAxisSettings>({} as TimeSeriesChartYAxisSettings,
      defaultTimeSeriesChartYAxisSettings);
    const axes: TimeSeriesChartYAxisSettings[] = this.yAxesFormGroup.get('axes').value;
    axis.id = getNextTimeSeriesYAxisId(axes);
    axis.order = axes.length;
    const axesArray = this.yAxesFormGroup.get('axes') as UntypedFormArray;
    const axisControl = this.fb.control(axis, [timeSeriesChartYAxisValidator]);
    axesArray.push(axisControl);
  }

  /**
   * prepare axes form array.
   *
   * @param axes axes (TimeSeriesChartYAxisSettings[])
   * @returns UntypedFormArray observable or value
   */

  private prepareAxesFormArray(axes: TimeSeriesChartYAxisSettings[]): UntypedFormArray {
    const axesControls: Array<AbstractControl> = [];
    axes.forEach((axis) => {
      axesControls.push(this.fb.control(axis, [timeSeriesChartYAxisValidator]));
    });
    return this.fb.array(axesControls);
  }

}
