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
import { WidgetSettings, WidgetSettingsComponent } from '@shared/models/widget.models';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { flotDefaultSettings } from '@home/components/widget/lib/settings/chart/flot-widget-settings.component';
import { deepClone } from '@core/utils';


/**
 * Angular component: flot line widget settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-flot-line-widget-settings`.
 */
@Component({
    selector: 'tb-flot-line-widget-settings',
    templateUrl: './flot-line-widget-settings.component.html',
    styleUrls: [],
standalone: false
})
export class FlotLineWidgetSettingsComponent extends WidgetSettingsComponent {

  flotLineWidgetSettingsForm: UntypedFormGroup;

  constructor(protected store: Store<AppState>,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  /**
   * settings form.
   *
   * @returns UntypedFormGroup observable or value
   */

  protected settingsForm(): UntypedFormGroup {
    return this.flotLineWidgetSettingsForm;
  }

  /**
   * default settings.
   *
   * @returns WidgetSettings observable or value
   */

  protected defaultSettings(): WidgetSettings {
    return flotDefaultSettings('graph');
  }

  /**
   * Event handler for settings set.
   *
   * @param settings settings (WidgetSettings)
   */

  protected onSettingsSet(settings: WidgetSettings) {
    this.flotLineWidgetSettingsForm = this.fb.group({
      flotSettings: [settings.flotSettings, []]
    });
  }

  /**
   * prepare input settings.
   *
   * @param settings settings (WidgetSettings)
   * @returns WidgetSettings observable or value
   */

  protected prepareInputSettings(settings: WidgetSettings): WidgetSettings {
    return {
      flotSettings: settings
    };
  }

  /**
   * prepare output settings.
   *
   * @param settings settings (any)
   * @returns WidgetSettings observable or value
   */

  protected prepareOutputSettings(settings: any): WidgetSettings {
    return settings.flotSettings;
  }
}
