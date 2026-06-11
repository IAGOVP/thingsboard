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
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import {
  doughnutDefaultSettings,
  DoughnutLayout,
  DoughnutWidgetSettings
} from '@home/components/widget/lib/chart/doughnut-widget.models';
import {
  LatestChartWidgetSettingsComponent
} from '@home/components/widget/lib/settings/chart/latest-chart-widget-settings.component';


/**
 * Angular component: doughnut widget settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-doughnut-widget-settings`.
 */
@Component({
    selector: 'tb-doughnut-widget-settings',
    templateUrl: './latest-chart-widget-settings.component.html',
    styleUrls: ['./../widget-settings.scss'],
standalone: false
})
export class DoughnutWidgetSettingsComponent extends LatestChartWidgetSettingsComponent<DoughnutWidgetSettings> {

  @ViewChild('doughnutChart')
  doughnutChartConfigTemplate: TemplateRef<any>;

  constructor(protected store: Store<AppState>,
              protected fb: UntypedFormBuilder) {
    super(store, fb);
  }

  /**
   * default latest chart settings.
   *
   */

  protected defaultLatestChartSettings() {
    return doughnutDefaultSettings(this.doughnutHorizontal);
  }

  /**
   * latest chart config template.
   *
   * @returns TemplateRef<any> observable or value
   */

  public latestChartConfigTemplate(): TemplateRef<any> {
    return this.doughnutChartConfigTemplate;
  }

  /**
   * setup latest chart controls.
   *
   * @param latestChartWidgetSettingsForm latest chart widget settings form (UntypedFormGroup)
   * @param settings settings (WidgetSettings)
   */

  protected setupLatestChartControls(latestChartWidgetSettingsForm: UntypedFormGroup, settings: WidgetSettings) {
    latestChartWidgetSettingsForm.addControl('layout', this.fb.control(settings.layout, []));
    latestChartWidgetSettingsForm.addControl('autoScale', this.fb.control(settings.autoScale, []));
    latestChartWidgetSettingsForm.addControl('clockwise', this.fb.control(settings.clockwise, []));
    latestChartWidgetSettingsForm.addControl('totalValueFont', this.fb.control(settings.totalValueFont, []));
    latestChartWidgetSettingsForm.addControl('totalValueColor', this.fb.control(settings.totalValueColor, []));
  }

  /**
   * latest chart validator triggers.
   *
   * @returns string[] observable or value
   */

  protected latestChartValidatorTriggers(): string[] {
    return ['layout'];
  }

  /**
   * update latest chart validators.
   *
   * @param latestChartWidgetSettingsForm latest chart widget settings form (UntypedFormGroup)
   * @param emitEvent emit event (boolean)
   * @param trigger trigger (string)
   */

  protected updateLatestChartValidators(latestChartWidgetSettingsForm: UntypedFormGroup, emitEvent: boolean, trigger?: string) {
    const layout: DoughnutLayout = latestChartWidgetSettingsForm.get('layout').value;
    const totalEnabled = layout === DoughnutLayout.with_total;
    if (totalEnabled) {
      latestChartWidgetSettingsForm.get('totalValueFont').enable();
      latestChartWidgetSettingsForm.get('totalValueColor').enable();
      latestChartWidgetSettingsForm.get('legendShowTotal').disable();
    } else {
      latestChartWidgetSettingsForm.get('totalValueFont').disable();
      latestChartWidgetSettingsForm.get('totalValueColor').disable();
      latestChartWidgetSettingsForm.get('legendShowTotal').enable();
    }
  }
}
