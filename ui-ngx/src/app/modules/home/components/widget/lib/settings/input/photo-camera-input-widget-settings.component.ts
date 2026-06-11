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
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { WidgetSettings, WidgetSettingsComponent, widgetTitleAutocompleteValues } from '@shared/models/widget.models';
import { AppState } from '@core/core.state';
import { Store } from '@ngrx/store';


/**
 * Angular component: photo camera input widget settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-photo-camera-input-widget-settings`.
 */
@Component({
    selector: 'tb-photo-camera-input-widget-settings',
    templateUrl: './photo-camera-input-widget-settings.component.html',
    styleUrls: ['./../widget-settings.scss'],
standalone: false
})
export class PhotoCameraInputWidgetSettingsComponent extends WidgetSettingsComponent {

  photoCameraInputWidgetSettingsForm: UntypedFormGroup;

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
    return this.photoCameraInputWidgetSettingsForm;
  }

  /**
   * default settings.
   *
   * @returns WidgetSettings observable or value
   */

  protected defaultSettings(): WidgetSettings {
    return {
      widgetTitle: '',

      saveToGallery: false,
      usePublicGalleryLink: true,
      imageFormat: 'image/png',
      imageQuality: 0.92,
      maxWidth: 640,
      maxHeight: 480
    };
  }

  /**
   * Event handler for settings set.
   *
   * @param settings settings (WidgetSettings)
   */

  protected onSettingsSet(settings: WidgetSettings) {
    this.photoCameraInputWidgetSettingsForm = this.fb.group({

      // General settings

      widgetTitle: [settings.widgetTitle, []],

      // Image settings
      saveToGallery: [settings.saveToGallery],
      usePublicGalleryLink: [settings.usePublicGalleryLink],
      imageFormat: [settings.imageFormat, []],
      imageQuality: [settings.imageQuality, [Validators.min(0), Validators.max(100)]],
      maxWidth: [settings.maxWidth, [Validators.min(1)]],
      maxHeight: [settings.maxHeight, [Validators.min(1)]]
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
      ...settings,
      saveToGallery: settings.saveToGallery ?? false,
      usePublicGalleryLink: settings.usePublicGalleryLink ?? false,
      imageQuality: settings.imageQuality * 100
    }
  }

  /**
   * prepare output settings.
   *
   * @param settings settings (WidgetSettings)
   * @returns WidgetSettings observable or value
   */

  protected prepareOutputSettings(settings: WidgetSettings): WidgetSettings {
    return {
      ...settings,
      imageQuality: settings.imageQuality / 100
    }
  }
}
