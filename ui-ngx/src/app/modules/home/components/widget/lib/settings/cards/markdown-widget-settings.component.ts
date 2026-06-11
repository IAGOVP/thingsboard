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
 * Angular component: markdown widget settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-markdown-widget-settings`.
 */
@Component({
    selector: 'tb-markdown-widget-settings',
    templateUrl: './markdown-widget-settings.component.html',
    styleUrls: ['./../widget-settings.scss'],
standalone: false
})
export class MarkdownWidgetSettingsComponent extends WidgetSettingsComponent {

  markdownWidgetSettingsForm: UntypedFormGroup;

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
    return this.markdownWidgetSettingsForm;
  }

  /**
   * default settings.
   *
   * @returns WidgetSettings observable or value
   */

  protected defaultSettings(): WidgetSettings {
    return {
      useMarkdownTextFunction: false,
      markdownTextPattern: '# Markdown/HTML card \\n - **Current entity**: **${entityName}**. \\n - **Current value**: **${Random}**.',
      markdownTextFunction: 'return \'# Some title\\\\n - Entity name: \' + data[0][\'entityName\'];',
      applyDefaultMarkdownStyle: true,
      markdownCss: ''
    };
  }

  /**
   * Event handler for settings set.
   *
   * @param settings settings (WidgetSettings)
   */

  protected onSettingsSet(settings: WidgetSettings) {
    this.markdownWidgetSettingsForm = this.fb.group({
      useMarkdownTextFunction: [settings.useMarkdownTextFunction, []],
      markdownTextPattern: [settings.markdownTextPattern, []],
      markdownTextFunction: [settings.markdownTextFunction, []],
      applyDefaultMarkdownStyle: [settings.applyDefaultMarkdownStyle, []],
      markdownCss: [settings.markdownCss, []]
    });
  }

  /**
   * validator triggers.
   *
   * @returns string[] observable or value
   */

  protected validatorTriggers(): string[] {
    return ['useMarkdownTextFunction'];
  }

  /**
   * update validators.
   *
   * @param emitEvent emit event (boolean)
   */

  protected updateValidators(emitEvent: boolean) {
    const useMarkdownTextFunction: boolean = this.markdownWidgetSettingsForm.get('useMarkdownTextFunction').value;
    if (useMarkdownTextFunction) {
      this.markdownWidgetSettingsForm.get('markdownTextPattern').disable();
      this.markdownWidgetSettingsForm.get('markdownTextFunction').enable();
    } else {
      this.markdownWidgetSettingsForm.get('markdownTextPattern').enable();
      this.markdownWidgetSettingsForm.get('markdownTextFunction').disable();
    }
    this.markdownWidgetSettingsForm.get('markdownTextPattern').updateValueAndValidity({emitEvent});
    this.markdownWidgetSettingsForm.get('markdownTextFunction').updateValueAndValidity({emitEvent});
  }

}
