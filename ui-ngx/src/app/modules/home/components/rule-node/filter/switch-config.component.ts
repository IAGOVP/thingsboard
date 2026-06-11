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

import { Component, EventEmitter, ViewChild } from '@angular/core';
import { getCurrentAuthState, isDefinedAndNotNull, NodeScriptTestService } from '@core/public-api';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import {
  RuleNodeConfiguration,
  RuleNodeConfigurationComponent,
  ScriptLanguage
} from '@app/shared/models/rule-node.models';
import type { JsFuncComponent } from '@app/shared/components/js-func.component';
import { DebugRuleNodeEventBody } from '@shared/models/event.models';


/**
 * Angular component: switch config (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-filter-node-switch-config`.
 */
@Component({
    selector: 'tb-filter-node-switch-config',
    templateUrl: './switch-config.component.html',
    styleUrls: [],
standalone: false
})
export class SwitchConfigComponent extends RuleNodeConfigurationComponent {

  @ViewChild('jsFuncComponent', {static: false}) jsFuncComponent: JsFuncComponent;
  @ViewChild('tbelFuncComponent', {static: false}) tbelFuncComponent: JsFuncComponent;

  switchConfigForm: UntypedFormGroup;

  tbelEnabled = getCurrentAuthState(this.store).tbelEnabled;

  scriptLanguage = ScriptLanguage;

  changeScript: EventEmitter<void> = new EventEmitter<void>();

  readonly hasScript = true;

  readonly testScriptLabel = 'rule-node-config.test-switch-function';

  constructor(private fb: UntypedFormBuilder,
              private nodeScriptTestService: NodeScriptTestService,
              private translate: TranslateService) {
    super();
  }

  /**
   * config form.
   *
   * @returns UntypedFormGroup observable or value
   */

  protected configForm(): UntypedFormGroup {
    return this.switchConfigForm;
  }

  /**
   * Event handler for configuration set.
   *
   * @param configuration configuration (RuleNodeConfiguration)
   */

  protected onConfigurationSet(configuration: RuleNodeConfiguration) {
    this.switchConfigForm = this.fb.group({
      scriptLang: [configuration.scriptLang, [Validators.required]],
      jsScript: [configuration.jsScript, []],
      tbelScript: [configuration.tbelScript, []]
    });
  }

  /**
   * validator triggers.
   *
   * @returns string[] observable or value
   */

  protected validatorTriggers(): string[] {
    return ['scriptLang'];
  }

  /**
   * update validators.
   *
   * @param emitEvent emit event (boolean)
   */

  protected updateValidators(emitEvent: boolean) {
    let scriptLang: ScriptLanguage = this.switchConfigForm.get('scriptLang').value;
    if (scriptLang === ScriptLanguage.TBEL && !this.tbelEnabled) {
      scriptLang = ScriptLanguage.JS;
      this.switchConfigForm.get('scriptLang').patchValue(scriptLang, {emitEvent: false});
      setTimeout(() => {
        this.switchConfigForm.updateValueAndValidity({emitEvent: true});
      });
    }
    this.switchConfigForm.get('jsScript').setValidators(scriptLang === ScriptLanguage.JS ? [Validators.required] : []);
    this.switchConfigForm.get('jsScript').updateValueAndValidity({emitEvent});
    this.switchConfigForm.get('tbelScript').setValidators(scriptLang === ScriptLanguage.TBEL ? [Validators.required] : []);
    this.switchConfigForm.get('tbelScript').updateValueAndValidity({emitEvent});
  }

  /**
   * prepare input config.
   *
   * @param configuration configuration (RuleNodeConfiguration)
   * @returns RuleNodeConfiguration observable or value
   */

  protected prepareInputConfig(configuration: RuleNodeConfiguration): RuleNodeConfiguration {
    if (configuration) {
      if (!configuration.scriptLang) {
        configuration.scriptLang = ScriptLanguage.JS;
      }
    }
    return {
      scriptLang: isDefinedAndNotNull(configuration?.scriptLang) ? configuration.scriptLang : ScriptLanguage.JS,
      jsScript: isDefinedAndNotNull(configuration?.jsScript) ? configuration.jsScript : null,
      tbelScript: isDefinedAndNotNull(configuration?.tbelScript) ? configuration.tbelScript : null
    };
  }

  /**
   * test script.
   *
   * @param debugEventBody debug event body (DebugRuleNodeEventBody)
   */

  testScript(debugEventBody?: DebugRuleNodeEventBody) {
    const scriptLang: ScriptLanguage = this.switchConfigForm.get('scriptLang').value;
    const scriptField = scriptLang === ScriptLanguage.JS ? 'jsScript' : 'tbelScript';
    const helpId = scriptLang === ScriptLanguage.JS ? 'rulenode/switch_node_script_fn' : 'rulenode/tbel/switch_node_script_fn';
    const script: string = this.switchConfigForm.get(scriptField).value;
    this.nodeScriptTestService.testNodeScript(
      script,
      'switch',
      this.translate.instant('rule-node-config.switch'),
      'Switch',
      ['msg', 'metadata', 'msgType'],
      this.ruleNodeId,
      helpId,
      scriptLang,
      debugEventBody
    ).subscribe((theScript) => {
      if (theScript) {
        this.switchConfigForm.get(scriptField).setValue(theScript);
        this.changeScript.emit();
      }
    });
  }

  /**
   * Event handler for validate.
   *
   */

  protected onValidate() {
    const scriptLang: ScriptLanguage = this.switchConfigForm.get('scriptLang').value;
    if (scriptLang === ScriptLanguage.JS) {
      this.jsFuncComponent.validateOnSubmit();
    }
  }
}
