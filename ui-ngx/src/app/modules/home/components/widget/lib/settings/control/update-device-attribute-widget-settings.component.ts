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
import { WidgetSettings, WidgetSettingsComponent, widgetTitleAutocompleteValues } from '@shared/models/widget.models';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { ContentType } from '@shared/models/constants';


/**
 * Angular component: update device attribute widget settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-update-device-attribute-widget-settings`.
 */
@Component({
    selector: 'tb-update-device-attribute-widget-settings',
    templateUrl: './update-device-attribute-widget-settings.component.html',
    styleUrls: ['./../widget-settings.scss'],
standalone: false
})
export class UpdateDeviceAttributeWidgetSettingsComponent extends WidgetSettingsComponent {

  updateDeviceAttributeWidgetSettingsForm: UntypedFormGroup;

  contentTypes = ContentType;

  predefinedValues = widgetTitleAutocompleteValues;

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
    return this.updateDeviceAttributeWidgetSettingsForm;
  }

  /**
   * default settings.
   *
   * @returns WidgetSettings observable or value
   */

  protected defaultSettings(): WidgetSettings {
    return {
      title: '',
      buttonText: 'Update device attribute',
      entityAttributeType: 'SERVER_SCOPE',
      entityParameters: '{}',
      styleButton: {
        isRaised: true,
        isPrimary: false,
        bgColor: null,
        textColor: null
      }
    };
  }

  /**
   * Event handler for settings set.
   *
   * @param settings settings (WidgetSettings)
   */

  protected onSettingsSet(settings: WidgetSettings) {
    this.updateDeviceAttributeWidgetSettingsForm = this.fb.group({
      title: [settings.title, []],
      buttonText: [settings.buttonText, []],
      entityAttributeType: [settings.entityAttributeType, []],
      entityParameters: [settings.entityParameters, []],
      styleButton: [settings.styleButton, []]
    });
  }
}
