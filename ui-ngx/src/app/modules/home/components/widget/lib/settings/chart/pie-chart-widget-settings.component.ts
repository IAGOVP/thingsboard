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

import { Component, TemplateRef, ViewChild } from '@angular/core';
import { WidgetSettings } from '@shared/models/widget.models';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import {
  pieChartWidgetDefaultSettings,
  PieChartWidgetSettings
} from '@home/components/widget/lib/chart/pie-chart-widget.models';
import {
  LatestChartWidgetSettingsComponent
} from '@home/components/widget/lib/settings/chart/latest-chart-widget-settings.component';


/**
 * Angular component: pie chart widget settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-pie-chart-widget-settings`.
 */
@Component({
    selector: 'tb-pie-chart-widget-settings',
    templateUrl: './latest-chart-widget-settings.component.html',
    styleUrls: ['./../widget-settings.scss'],
standalone: false
})
export class PieChartWidgetSettingsComponent extends LatestChartWidgetSettingsComponent<PieChartWidgetSettings> {

  @ViewChild('pieChart')
  pieChartConfigTemplate: TemplateRef<any>;

  constructor(protected store: Store<AppState>,
              protected fb: UntypedFormBuilder) {
    super(store, fb);
  }

  /**
   * default latest chart settings.
   *
   */

  protected defaultLatestChartSettings() {
    return pieChartWidgetDefaultSettings;
  }

  /**
   * latest chart config template.
   *
   * @returns TemplateRef<any> observable or value
   */

  public latestChartConfigTemplate(): TemplateRef<any> {
    return this.pieChartConfigTemplate;
  }

  /**
   * setup latest chart controls.
   *
   * @param latestChartWidgetSettingsForm latest chart widget settings form (UntypedFormGroup)
   * @param settings settings (WidgetSettings)
   */

  protected setupLatestChartControls(latestChartWidgetSettingsForm: UntypedFormGroup, settings: WidgetSettings) {
    latestChartWidgetSettingsForm.addControl('showLabel', this.fb.control(settings.showLabel, []));
    latestChartWidgetSettingsForm.addControl('labelPosition', this.fb.control(settings.labelPosition, []));
    latestChartWidgetSettingsForm.addControl('labelFont', this.fb.control(settings.labelFont, []));
    latestChartWidgetSettingsForm.addControl('labelColor', this.fb.control(settings.labelColor, []));
    latestChartWidgetSettingsForm.addControl('borderWidth', this.fb.control(settings.borderWidth, [Validators.min(0)]));
    latestChartWidgetSettingsForm.addControl('borderColor', this.fb.control(settings.borderColor, []));
    latestChartWidgetSettingsForm.addControl('radius', this.fb.control(settings.radius, []));
    latestChartWidgetSettingsForm.addControl('clockwise', this.fb.control(settings.clockwise, []));
  }

  /**
   * latest chart validator triggers.
   *
   * @returns string[] observable or value
   */

  protected latestChartValidatorTriggers(): string[] {
    return ['showLabel'];
  }

  /**
   * update latest chart validators.
   *
   * @param latestChartWidgetSettingsForm latest chart widget settings form (UntypedFormGroup)
   * @param emitEvent emit event (boolean)
   * @param trigger trigger (string)
   */

  protected updateLatestChartValidators(latestChartWidgetSettingsForm: UntypedFormGroup, emitEvent: boolean, trigger?: string) {
    const showLabel: boolean = latestChartWidgetSettingsForm.get('showLabel').value;
    if (showLabel) {
      latestChartWidgetSettingsForm.get('labelPosition').enable();
      latestChartWidgetSettingsForm.get('labelFont').enable();
      latestChartWidgetSettingsForm.get('labelColor').enable();
    } else {
      latestChartWidgetSettingsForm.get('labelPosition').disable();
      latestChartWidgetSettingsForm.get('labelFont').disable();
      latestChartWidgetSettingsForm.get('labelColor').disable();
    }
  }
}
