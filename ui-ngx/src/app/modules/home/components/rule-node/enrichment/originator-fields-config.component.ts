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
import { deepTrim, isDefinedAndNotNull } from '@core/public-api';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { allowedOriginatorFields, FetchTo, SvMapOption } from '@home/components/rule-node/rule-node-config.models';
import { RuleNodeConfiguration, RuleNodeConfigurationComponent } from '@app/shared/models/rule-node.models';


/**
 * Angular component: originator fields config (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-enrichment-node-originator-fields-config`.
 */
@Component({
    selector: 'tb-enrichment-node-originator-fields-config',
    templateUrl: './originator-fields-config.component.html',
standalone: false
})
export class OriginatorFieldsConfigComponent extends RuleNodeConfigurationComponent {

  originatorFieldsConfigForm: FormGroup;
  public originatorFields: SvMapOption[] = [];

  constructor(private fb: FormBuilder,
              private translate: TranslateService) {
    super();
    for (const field of allowedOriginatorFields) {
      this.originatorFields.push({
        value: field.value,
        name: this.translate.instant(field.name)
      });
    }
  }

  /**
   * config form.
   *
   * @returns FormGroup observable or value
   */

  protected configForm(): FormGroup {
    return this.originatorFieldsConfigForm;
  }

  /**
   * prepare output config.
   *
   * @param configuration configuration (RuleNodeConfiguration)
   * @returns RuleNodeConfiguration observable or value
   */

  protected prepareOutputConfig(configuration: RuleNodeConfiguration): RuleNodeConfiguration {
    return deepTrim(configuration);
  }

  /**
   * prepare input config.
   *
   * @param configuration configuration (RuleNodeConfiguration)
   * @returns RuleNodeConfiguration observable or value
   */

  protected prepareInputConfig(configuration: RuleNodeConfiguration): RuleNodeConfiguration {
    return {
      dataMapping: isDefinedAndNotNull(configuration?.dataMapping) ? configuration.dataMapping : null,
      ignoreNullStrings: isDefinedAndNotNull(configuration?.ignoreNullStrings) ? configuration.ignoreNullStrings : null,
      fetchTo: isDefinedAndNotNull(configuration?.fetchTo) ? configuration.fetchTo : FetchTo.METADATA
    };
  }

  /**
   * Event handler for configuration set.
   *
   * @param configuration configuration (RuleNodeConfiguration)
   */

  protected onConfigurationSet(configuration: RuleNodeConfiguration) {
    this.originatorFieldsConfigForm = this.fb.group({
      dataMapping: [configuration.dataMapping, [Validators.required]],
      ignoreNullStrings: [configuration.ignoreNullStrings, []],
      fetchTo: [configuration.fetchTo, []]
    });
  }
}
