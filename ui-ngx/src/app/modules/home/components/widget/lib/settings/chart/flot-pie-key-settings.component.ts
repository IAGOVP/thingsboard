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


/**
 * Angular component: flot pie key settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-flot-pie-key-settings`.
 */
@Component({
    selector: 'tb-flot-pie-key-settings',
    templateUrl: './flot-pie-key-settings.component.html',
    styleUrls: ['./../widget-settings.scss'],
standalone: false
})
export class FlotPieKeySettingsComponent extends WidgetSettingsComponent {

  flotPieKeySettingsForm: UntypedFormGroup;

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
    return this.flotPieKeySettingsForm;
  }

  /**
   * default settings.
   *
   * @returns WidgetSettings observable or value
   */

  protected defaultSettings(): WidgetSettings {
    return {
      hideDataByDefault: false,
      disableDataHiding: false,
      removeFromLegend: false
    };
  }

  /**
   * Event handler for settings set.
   *
   * @param settings settings (WidgetSettings)
   */

  protected onSettingsSet(settings: WidgetSettings) {

    this.flotPieKeySettingsForm = this.fb.group({

      // Common settings

      hideDataByDefault: [settings.hideDataByDefault, []],
      disableDataHiding: [settings.disableDataHiding, []],
      removeFromLegend: [settings.removeFromLegend, []]
    });
  }
}
