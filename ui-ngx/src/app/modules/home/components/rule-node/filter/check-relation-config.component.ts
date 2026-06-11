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
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { EntitySearchDirection, entitySearchDirectionTranslations } from '@app/shared/models/relation.models';
import { RuleNodeConfiguration, RuleNodeConfigurationComponent } from '@app/shared/models/rule-node.models';


/**
 * Angular component: check relation config (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-filter-node-check-relation-config`.
 */
@Component({
    selector: 'tb-filter-node-check-relation-config',
    templateUrl: './check-relation-config.component.html',
    styleUrls: ['./check-relation-config.component.scss'],
standalone: false
})
export class CheckRelationConfigComponent extends RuleNodeConfigurationComponent {

  checkRelationConfigForm: UntypedFormGroup;

  entitySearchDirection: Array<EntitySearchDirection> = Object.values(EntitySearchDirection);
  entitySearchDirectionTranslationsMap = entitySearchDirectionTranslations;

  constructor(private fb: UntypedFormBuilder) {
    super();
  }

  /**
   * config form.
   *
   * @returns UntypedFormGroup observable or value
   */

  protected configForm(): UntypedFormGroup {
    return this.checkRelationConfigForm;
  }

  /**
   * prepare input config.
   *
   * @param configuration configuration (RuleNodeConfiguration)
   * @returns RuleNodeConfiguration observable or value
   */

  protected prepareInputConfig(configuration: RuleNodeConfiguration): RuleNodeConfiguration {
    return {
      checkForSingleEntity: isDefinedAndNotNull(configuration?.checkForSingleEntity) ? configuration.checkForSingleEntity : false,
      direction: isDefinedAndNotNull(configuration?.direction) ? configuration.direction : null,
      entityType: isDefinedAndNotNull(configuration?.entityType) ? configuration.entityType : null,
      entityId: isDefinedAndNotNull(configuration?.entityId) ? configuration.entityId : null,
      relationType: isDefinedAndNotNull(configuration?.relationType) ? configuration.relationType : null
    };
  }

  /**
   * Event handler for configuration set.
   *
   * @param configuration configuration (RuleNodeConfiguration)
   */

  protected onConfigurationSet(configuration: RuleNodeConfiguration) {
    this.checkRelationConfigForm = this.fb.group({
      checkForSingleEntity: [configuration.checkForSingleEntity, []],
      direction: [configuration.direction, []],
      entityType: [configuration.entityType, [Validators.required]],
      entityId: [configuration.entityId, [Validators.required]],
      relationType: [configuration.relationType, [Validators.required]]
    });
  }

  /**
   * validator triggers.
   *
   * @returns string[] observable or value
   */

  protected validatorTriggers(): string[] {
    return ['checkForSingleEntity', 'entityType'];
  }

  /**
   * update validators.
   *
   * @param _emitEvent  emit event (boolean)
   * @param trigger trigger (string)
   */

  protected updateValidators(_emitEvent: boolean, trigger: string) {
    if (trigger === 'entityType') {
      this.checkRelationConfigForm.get('entityId').reset(null);
    } else {
      const checkForSingleEntity: boolean = this.checkRelationConfigForm.get('checkForSingleEntity').value;
      if (checkForSingleEntity) {
        this.checkRelationConfigForm.get('entityType').enable({emitEvent: false});
        this.checkRelationConfigForm.get('entityId').enable({emitEvent: false});
      } else {
        this.checkRelationConfigForm.get('entityType').disable({emitEvent: false});
        this.checkRelationConfigForm.get('entityId').disable({emitEvent: false});
      }
    }
  }

}
