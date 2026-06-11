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
 * Angular component: flot latest key settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-flot-latest-key-settings`.
 */
@Component({
    selector: 'tb-flot-latest-key-settings',
    templateUrl: './flot-latest-key-settings.component.html',
    styleUrls: ['./../widget-settings.scss'],
standalone: false
})
export class FlotLatestKeySettingsComponent extends WidgetSettingsComponent {

  flotLatestKeySettingsForm: UntypedFormGroup;

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
    return this.flotLatestKeySettingsForm;
  }

  /**
   * default settings.
   *
   * @returns WidgetSettings observable or value
   */

  protected defaultSettings(): WidgetSettings {
    return {
      useAsThreshold: false,
      thresholdLineWidth: null,
      thresholdColor: null
    };
  }

  /**
   * Event handler for settings set.
   *
   * @param settings settings (WidgetSettings)
   */

  protected onSettingsSet(settings: WidgetSettings) {
    this.flotLatestKeySettingsForm = this.fb.group({
      useAsThreshold: [settings.useAsThreshold, []],
      thresholdLineWidth: [settings.thresholdLineWidth, [Validators.min(0)]],
      thresholdColor: [settings.thresholdColor, []]
    });
  }

  /**
   * validator triggers.
   *
   * @returns string[] observable or value
   */

  protected validatorTriggers(): string[] {
    return ['useAsThreshold'];
  }

  /**
   * update validators.
   *
   * @param emitEvent emit event (boolean)
   */

  protected updateValidators(emitEvent: boolean) {
    const useAsThreshold: boolean = this.flotLatestKeySettingsForm.get('useAsThreshold').value;
    if (useAsThreshold) {
      this.flotLatestKeySettingsForm.get('thresholdLineWidth').enable();
      this.flotLatestKeySettingsForm.get('thresholdColor').enable();
    } else {
      this.flotLatestKeySettingsForm.get('thresholdLineWidth').disable();
      this.flotLatestKeySettingsForm.get('thresholdColor').disable();
    }
    this.flotLatestKeySettingsForm.get('thresholdLineWidth').updateValueAndValidity({emitEvent});
    this.flotLatestKeySettingsForm.get('thresholdColor').updateValueAndValidity({emitEvent});
  }

}
