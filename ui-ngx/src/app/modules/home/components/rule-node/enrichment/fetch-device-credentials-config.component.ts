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
import { isDefinedAndNotNull } from '@core/public-api';
import { FormBuilder, FormGroup } from '@angular/forms';
import { RuleNodeConfiguration, RuleNodeConfigurationComponent } from '@shared/models/rule-node.models';
import { FetchTo } from '@home/components/rule-node/rule-node-config.models';



/**
 * Angular component: fetch device credentials config (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-enrichment-node-fetch-device-credentials-config`.
 */
@Component({
    selector: 'tb-enrichment-node-fetch-device-credentials-config',
    templateUrl: './fetch-device-credentials-config.component.html',

standalone: false
})

export class FetchDeviceCredentialsConfigComponent extends RuleNodeConfigurationComponent {

  fetchDeviceCredentialsConfigForm: FormGroup;

  constructor(private fb: FormBuilder) {
    super();
  }

  /**
   * config form.
   *
   * @returns FormGroup observable or value
   */

  protected configForm(): FormGroup {
    return this.fetchDeviceCredentialsConfigForm;
  }

  /**
   * prepare input config.
   *
   * @param configuration configuration (RuleNodeConfiguration)
   * @returns RuleNodeConfiguration observable or value
   */

  protected prepareInputConfig(configuration: RuleNodeConfiguration): RuleNodeConfiguration {
    return {
      fetchTo: isDefinedAndNotNull(configuration?.fetchTo) ? configuration.fetchTo : FetchTo.METADATA
    };
  }

  /**
   * Event handler for configuration set.
   *
   * @param configuration configuration (RuleNodeConfiguration)
   */

  protected onConfigurationSet(configuration: RuleNodeConfiguration) {
    this.fetchDeviceCredentialsConfigForm = this.fb.group({
      fetchTo: [configuration.fetchTo, []]
    });
  }
}
