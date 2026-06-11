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

import { Component, Injector } from '@angular/core';
import { WidgetSettings, WidgetSettingsComponent } from '@shared/models/widget.models';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { formatValue } from '@core/utils';
import {
  valueCartCardLayouts,
  valueChartCardDefaultSettings,
  valueChartCardLayoutImages,
  valueChartCardLayoutTranslations
} from '@home/components/widget/lib/cards/value-chart-card-widget.models';
import { getSourceTbUnitSymbol } from '@shared/models/unit.models';


/**
 * Angular component: value chart card widget settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-value-chart-card-widget-settings`.
 */
@Component({
    selector: 'tb-value-chart-card-widget-settings',
    templateUrl: './value-chart-card-widget-settings.component.html',
    styleUrls: [],
standalone: false
})
export class ValueChartCardWidgetSettingsComponent extends WidgetSettingsComponent {

  valueChartCardLayouts = valueCartCardLayouts;

  valueChartCardLayoutTranslationMap = valueChartCardLayoutTranslations;
  valueChartCardLayoutImageMap = valueChartCardLayoutImages;

  valueChartCardWidgetSettingsForm: UntypedFormGroup;

  valuePreviewFn = this._valuePreviewFn.bind(this);

  constructor(protected store: Store<AppState>,
              private $injector: Injector,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  /**
   * settings form.
   *
   * @returns UntypedFormGroup observable or value
   */

  protected settingsForm(): UntypedFormGroup {
    return this.valueChartCardWidgetSettingsForm;
  }

  /**
   * default settings.
   *
   * @returns WidgetSettings observable or value
   */

  protected defaultSettings(): WidgetSettings {
    return valueChartCardDefaultSettings;
  }

  /**
   * Event handler for settings set.
   *
   * @param settings settings (WidgetSettings)
   */

  protected onSettingsSet(settings: WidgetSettings) {
    this.valueChartCardWidgetSettingsForm = this.fb.group({
      layout: [settings.layout, []],
      autoScale: [settings.autoScale, []],

      showValue: [settings.showValue, []],
      valueFont: [settings.valueFont, []],
      valueColor: [settings.valueColor, []],

      background: [settings.background, []],
      padding: [settings.padding, []]
    });
  }

  /**
   * validator triggers.
   *
   * @returns string[] observable or value
   */

  protected validatorTriggers(): string[] {
    return ['showValue'];
  }

  /**
   * update validators.
   *
   * @param emitEvent emit event (boolean)
   */

  protected updateValidators(emitEvent: boolean) {
    const showValue: boolean = this.valueChartCardWidgetSettingsForm.get('showValue').value;

    if (showValue) {
      this.valueChartCardWidgetSettingsForm.get('valueFont').enable();
      this.valueChartCardWidgetSettingsForm.get('valueColor').enable();
    } else {
      this.valueChartCardWidgetSettingsForm.get('valueFont').disable();
      this.valueChartCardWidgetSettingsForm.get('valueColor').disable();
    }

    this.valueChartCardWidgetSettingsForm.get('valueFont').updateValueAndValidity({emitEvent});
    this.valueChartCardWidgetSettingsForm.get('valueColor').updateValueAndValidity({emitEvent});
  }

  private _valuePreviewFn(): string {
    const units = getSourceTbUnitSymbol(this.widgetConfig.config.units);
    const decimals: number = this.widgetConfig.config.decimals;
    return formatValue(22, decimals, units, true);
  }

}
