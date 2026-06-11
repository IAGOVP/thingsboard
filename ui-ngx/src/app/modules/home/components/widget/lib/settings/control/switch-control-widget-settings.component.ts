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
import { TargetDevice, WidgetSettings, WidgetSettingsComponent } from '@shared/models/widget.models';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { switchRpcDefaultSettings } from '@home/components/widget/lib/settings/control/switch-rpc-settings.component';
import { deepClone } from '@core/utils';


/**
 * Angular component: switch control widget settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-switch-control-widget-settings`.
 */
@Component({
    selector: 'tb-switch-control-widget-settings',
    templateUrl: './switch-control-widget-settings.component.html',
    styleUrls: ['./../widget-settings.scss'],
standalone: false
})
export class SwitchControlWidgetSettingsComponent extends WidgetSettingsComponent {

  switchControlWidgetSettingsForm: UntypedFormGroup;

  constructor(protected store: Store<AppState>,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  get targetDevice(): TargetDevice {
    return this.widgetConfig?.config?.targetDevice;
  }

  /**
   * settings form.
   *
   * @returns UntypedFormGroup observable or value
   */

  protected settingsForm(): UntypedFormGroup {
    return this.switchControlWidgetSettingsForm;
  }

  /**
   * default settings.
   *
   * @returns WidgetSettings observable or value
   */

  protected defaultSettings(): WidgetSettings {
    return {
      title: '',
      showOnOffLabels: true,
      ...switchRpcDefaultSettings()
    };
  }

  /**
   * Event handler for settings set.
   *
   * @param settings settings (WidgetSettings)
   */

  protected onSettingsSet(settings: WidgetSettings) {
    this.switchControlWidgetSettingsForm = this.fb.group({
      title: [settings.title, []],
      showOnOffLabels: [settings.showOnOffLabels, []],
      switchRpcSettings: [settings.switchRpcSettings, []]
    });
  }

  /**
   * prepare input settings.
   *
   * @param settings settings (WidgetSettings)
   * @returns WidgetSettings observable or value
   */

  protected prepareInputSettings(settings: WidgetSettings): WidgetSettings {
    const switchRpcSettings = deepClone(settings, ['title', 'showOnOffLabels']);
    return {
      title: settings.title,
      showOnOffLabels: settings.showOnOffLabels,
      switchRpcSettings
    };
  }

  /**
   * prepare output settings.
   *
   * @param settings settings (any)
   * @returns WidgetSettings observable or value
   */

  protected prepareOutputSettings(settings: any): WidgetSettings {
    return {
      ...settings.switchRpcSettings,
      title: settings.title,
      showOnOffLabels: settings.showOnOffLabels
    };
  }
}
