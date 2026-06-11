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

import { Component } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { BasicWidgetConfigComponent } from '@home/components/widget/config/widget-config.component.models';
import { WidgetConfigComponentData } from '@home/models/widget-component.models';
import {
  DataKey,
  datasourcesHasAggregation,
  datasourcesHasOnlyComparisonAggregation,
  WidgetConfig,
  widgetTitleAutocompleteValues,
} from '@shared/models/widget.models';
import { WidgetConfigComponent } from '@home/components/widget/widget-config.component';
import { DataKeyType } from '@shared/models/telemetry/telemetry.models';
import {
  getTimewindowConfig,
  setTimewindowConfig
} from '@home/components/widget/config/timewindow-config-panel.component';
import { isUndefined } from '@core/utils';
import { getLabel, setLabel } from '@shared/models/widget-settings.models';


/**
 * Angular component: simple card basic config (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-simple-card-basic-config`.
 */
@Component({
    selector: 'tb-simple-card-basic-config',
    templateUrl: './simple-card-basic-config.component.html',
    styleUrls: ['../basic-config.scss'],
standalone: false
})
export class SimpleCardBasicConfigComponent extends BasicWidgetConfigComponent {

  public get displayTimewindowConfig(): boolean {
    const datasources = this.simpleCardWidgetConfigForm.get('datasources').value;
    return datasourcesHasAggregation(datasources);
  }

  /**
   * only history timewindow.
   *
   * @returns boolean observable or value
   */

  public onlyHistoryTimewindow(): boolean {
    const datasources = this.simpleCardWidgetConfigForm.get('datasources').value;
    return datasourcesHasOnlyComparisonAggregation(datasources);
  }

  simpleCardWidgetConfigForm: UntypedFormGroup;

  predefinedValues = widgetTitleAutocompleteValues;

  constructor(protected store: Store<AppState>,
              protected widgetConfigComponent: WidgetConfigComponent,
              private fb: UntypedFormBuilder) {
    super(store, widgetConfigComponent);
  }

  /**
   * config form.
   *
   * @returns UntypedFormGroup observable or value
   */

  protected configForm(): UntypedFormGroup {
    return this.simpleCardWidgetConfigForm;
  }

  /**
   * default data keys.
   *
   * @param configData config data (WidgetConfigComponentData)
   * @returns DataKey[] observable or value
   */

  protected defaultDataKeys(configData: WidgetConfigComponentData): DataKey[] {
    return [{ name: 'temperature', label: 'Temperature', type: DataKeyType.timeseries }];
  }

  /**
   * Event handler for config set.
   *
   * @param configData config data (WidgetConfigComponentData)
   */

  protected onConfigSet(configData: WidgetConfigComponentData) {
    this.simpleCardWidgetConfigForm = this.fb.group({
      timewindowConfig: [getTimewindowConfig(configData.config), []],
      datasources: [configData.config.datasources, []],
      label: [getLabel(configData.config.datasources), []],
      labelPosition: [configData.config.settings?.labelPosition, []],
      units: [configData.config.units, []],
      decimals: [configData.config.decimals, []],
      cardButtons: [this.getCardButtons(configData.config), []],
      color: [configData.config.color, []],
      backgroundColor: [configData.config.backgroundColor, []],
      actions: [configData.config.actions || {}, []]
    });
  }

  /**
   * prepare output config.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns WidgetConfigComponentData observable or value
   */

  protected prepareOutputConfig(config: any): WidgetConfigComponentData {
    setTimewindowConfig(this.widgetConfig.config, config.timewindowConfig);
    this.widgetConfig.config.datasources = config.datasources;
    setLabel(config.label, this.widgetConfig.config.datasources);
    this.widgetConfig.config.actions = config.actions;
    this.widgetConfig.config.units = config.units;
    this.widgetConfig.config.decimals = config.decimals;
    this.widgetConfig.config.settings = this.widgetConfig.config.settings || {};
    this.setCardButtons(config.cardButtons, this.widgetConfig.config);
    this.widgetConfig.config.color = config.color;
    this.widgetConfig.config.backgroundColor = config.backgroundColor;
    this.widgetConfig.config.settings.labelPosition = config.labelPosition;
    return this.widgetConfig;
  }

  /**
   * get card buttons.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns string[] observable or value
   */

  private getCardButtons(config: WidgetConfig): string[] {
    const buttons: string[] = [];
    if (isUndefined(config.enableFullscreen) || config.enableFullscreen) {
      buttons.push('fullscreen');
    }
    return buttons;
  }

  /**
   * set card buttons.
   *
   * @param buttons buttons (string[])
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  private setCardButtons(buttons: string[], config: WidgetConfig) {
    config.enableFullscreen = buttons.includes('fullscreen');
  }

}
