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
import { flotDataKeyDefaultSettings } from '@home/components/widget/lib/settings/chart/flot-key-settings.component';


/**
 * Angular component: flot bar key settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-flot-bar-key-settings`.
 */
@Component({
    selector: 'tb-flot-bar-key-settings',
    templateUrl: './flot-bar-key-settings.component.html',
    styleUrls: [],
standalone: false
})
export class FlotBarKeySettingsComponent extends WidgetSettingsComponent {

  flotBarKeySettingsForm: UntypedFormGroup;

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
    return this.flotBarKeySettingsForm;
  }

  /**
   * default settings.
   *
   * @returns WidgetSettings observable or value
   */

  protected defaultSettings(): WidgetSettings {
    return flotDataKeyDefaultSettings('bar');
  }

  /**
   * Event handler for settings set.
   *
   * @param settings settings (WidgetSettings)
   */

  protected onSettingsSet(settings: WidgetSettings) {
    this.flotBarKeySettingsForm = this.fb.group({
      flotKeySettings: [settings.flotKeySettings, []]
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
      flotKeySettings: settings
    };
  }

  /**
   * prepare output settings.
   *
   * @param settings settings (any)
   * @returns WidgetSettings observable or value
   */

  protected prepareOutputSettings(settings: any): WidgetSettings {
    return settings.flotKeySettings;
  }
}
