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
 * Angular component: rpc terminal widget settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-rpc-terminal-widget-settings`.
 */
@Component({
    selector: 'tb-rpc-terminal-widget-settings',
    templateUrl: './rpc-terminal-widget-settings.component.html',
    styleUrls: ['./../widget-settings.scss'],
standalone: false
})
export class RpcTerminalWidgetSettingsComponent extends WidgetSettingsComponent {

  rpcTerminalWidgetSettingsForm: UntypedFormGroup;

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
    return this.rpcTerminalWidgetSettingsForm;
  }

  /**
   * default settings.
   *
   * @returns WidgetSettings observable or value
   */

  protected defaultSettings(): WidgetSettings {
    return {
      requestTimeout: 500,
      requestPersistent: false,
      persistentPollingInterval: 5000
    };
  }

  /**
   * Event handler for settings set.
   *
   * @param settings settings (WidgetSettings)
   */

  protected onSettingsSet(settings: WidgetSettings) {
    this.rpcTerminalWidgetSettingsForm = this.fb.group({
      // RPC settings

      requestTimeout: [settings.requestTimeout, [Validators.min(0), Validators.required]],

      // --> Persistent RPC settings

      requestPersistent: [settings.requestPersistent, []],
      persistentPollingInterval: [settings.persistentPollingInterval, [Validators.min(1000)]]
    });
  }

  /**
   * validator triggers.
   *
   * @returns string[] observable or value
   */

  protected validatorTriggers(): string[] {
    return ['requestPersistent'];
  }

  /**
   * update validators.
   *
   * @param emitEvent emit event (boolean)
   */

  protected updateValidators(emitEvent: boolean): void {
    const requestPersistent: boolean = this.rpcTerminalWidgetSettingsForm.get('requestPersistent').value;
    if (requestPersistent) {
      this.rpcTerminalWidgetSettingsForm.get('persistentPollingInterval').enable({emitEvent});
    } else {
      this.rpcTerminalWidgetSettingsForm.get('persistentPollingInterval').disable({emitEvent});
    }
    this.rpcTerminalWidgetSettingsForm.get('persistentPollingInterval').updateValueAndValidity({emitEvent: false});
  }
}
