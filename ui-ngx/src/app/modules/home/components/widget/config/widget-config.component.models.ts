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

import { WidgetActionCallbacks } from '@home/components/widget/action/manage-widget-actions.component.models';
import { DatasourceCallbacks } from '@home/components/widget/config/datasource.component.models';
import { WidgetConfigComponentData } from '@home/models/widget-component.models';
import { Observable } from 'rxjs';
import { AfterViewInit, DestroyRef, Directive, EventEmitter, inject, Inject, OnInit } from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { AbstractControl, UntypedFormGroup } from '@angular/forms';
import {
  DataKey,
  DatasourceType,
  Widget,
  widgetTypeCanHaveTimewindow,
  WidgetConfigMode,
  widgetType
} from '@shared/models/widget.models';
import { WidgetConfigComponent } from '@home/components/widget/widget-config.component';
import { isDefinedAndNotNull, isUndefinedOrNull } from '@core/utils';
import { IAliasController } from '@core/api/widget-api.models';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { initModelFromDefaultTimewindow } from '@shared/models/time/time.models';

export type WidgetConfigCallbacks = DatasourceCallbacks & WidgetActionCallbacks;

export interface IBasicWidgetConfigComponent {
  isAdd: boolean;
  widgetConfig: WidgetConfigComponentData;
  widgetConfigChanged: Observable<WidgetConfigComponentData>;
  validateConfig(): boolean;

}

@Directive()
// eslint-disable-next-line @angular-eslint/directive-class-suffix
/**
 * Angular component: basic widget config (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application.
 */

export abstract class BasicWidgetConfigComponent extends PageComponent implements
  IBasicWidgetConfigComponent, OnInit, AfterViewInit {

  isAdd = false;

  basicMode = WidgetConfigMode.basic;

  widgetConfigValue: WidgetConfigComponentData;

  set widgetConfig(value: WidgetConfigComponentData) {
    this.widgetConfigValue = value;
    this.setupConfig(this.widgetConfigValue);
  }

  get widgetConfig(): WidgetConfigComponentData {
    return this.widgetConfigValue;
  }

  get aliasController(): IAliasController {
    return this.widgetConfigComponent.aliasController;
  }

  get callbacks(): WidgetConfigCallbacks {
    return this.widgetConfigComponent.widgetConfigCallbacks;
  }

  get functionsOnly(): boolean {
    return this.widgetConfigComponent.functionsOnly;
  }

  get widgetType(): widgetType {
    return this.widgetConfigComponent.widgetType;
  }

  get widgetEditMode(): boolean {
    return this.widgetConfigComponent.widgetEditMode;
  }

  get widget(): Widget {
    return this.widgetConfigComponent.widget;
  }

  widgetConfigChangedEmitter = new EventEmitter<WidgetConfigComponentData>();
  widgetConfigChanged = this.widgetConfigChangedEmitter.asObservable();

  protected destroyRef = inject(DestroyRef);

  protected constructor(@Inject(Store) protected store: Store<AppState>,
                        protected widgetConfigComponent: WidgetConfigComponent) {
    super(store);
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {}

  /**
   * Angular lifecycle hook: run after the component view is initialized.
   *
   */

  ngAfterViewInit(): void {
    if (!this.validateConfig()) {
      setTimeout(() => {
          this.onConfigChanged(this.prepareOutputConfig(this.configForm().getRawValue()));
      }, 0);
    }
  }

  /**
   * setup config.
   *
   * @param widgetConfig widget config (WidgetConfigComponentData)
   */

  protected setupConfig(widgetConfig: WidgetConfigComponentData) {
    if (this.isAdd) {
      this.setupDefaults(widgetConfig);
    }
    if (widgetTypeCanHaveTimewindow(widgetConfig.widgetType) && isUndefinedOrNull(widgetConfig.config.timewindow)) {
      widgetConfig.config.timewindow = initModelFromDefaultTimewindow(null,
        widgetConfig.widgetType === widgetType.latest, false, this.widgetConfigComponent.timeService,
        widgetConfig.widgetType === widgetType.timeseries);
    }
    this.onConfigSet(widgetConfig);
    this.updateValidators(false);
    for (const trigger of this.validatorTriggers()) {
      const path = trigger.split('.');
      let control: AbstractControl = this.configForm();
      for (const part of path) {
        control = control.get(part);
      }
      control.valueChanges.pipe(
        takeUntilDestroyed(this.destroyRef)
      ).subscribe(() => {
        this.updateValidators(true, trigger);
      });
    }
    this.configForm().valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      this.onConfigChanged(this.prepareOutputConfig(this.configForm().getRawValue()));
    });
  }

  /**
   * setup defaults.
   *
   * @param configData config data (WidgetConfigComponentData)
   */

  protected setupDefaults(configData: WidgetConfigComponentData) {
    const params = configData.typeParameters;
    let dataKeys: DataKey[];
    let latestDataKeys: DataKey[];
    if (params.defaultDataKeysFunction) {
      dataKeys = params.defaultDataKeysFunction(this, configData);
    }
    if (params.defaultLatestDataKeysFunction) {
      latestDataKeys = params.defaultLatestDataKeysFunction(this, configData);
    }
    if (!dataKeys) {
      dataKeys = this.defaultDataKeys(configData);
    }
    if (!latestDataKeys) {
      latestDataKeys = this.defaultLatestDataKeys(configData);
    }
    if (dataKeys || latestDataKeys) {
      this.setupDefaultDatasource(configData, dataKeys, latestDataKeys);
    }
  }

  /**
   * default data keys.
   *
   * @param configData config data (WidgetConfigComponentData)
   * @returns DataKey[] observable or value
   */

  protected defaultDataKeys(configData: WidgetConfigComponentData): DataKey[] {
    return null;
  }

  /**
   * default latest data keys.
   *
   * @param configData config data (WidgetConfigComponentData)
   * @returns DataKey[] observable or value
   */

  protected defaultLatestDataKeys(configData: WidgetConfigComponentData): DataKey[] {
    return null;
  }

  /**
   * update validators.
   *
   * @param emitEvent emit event (boolean)
   * @param trigger trigger (string)
   */

  protected updateValidators(emitEvent: boolean, trigger?: string) {
  }

  /**
   * validator triggers.
   *
   * @returns string[] observable or value
   */

  protected validatorTriggers(): string[] {
    return [];
  }

  /**
   * Event handler for config changed.
   *
   * @param widgetConfig widget config (WidgetConfigComponentData)
   */

  protected onConfigChanged(widgetConfig: WidgetConfigComponentData) {
    this.widgetConfigValue = widgetConfig;
    this.widgetConfigChangedEmitter.emit(this.widgetConfigValue);
  }

  /**
   * prepare output config.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns WidgetConfigComponentData observable or value
   */

  protected prepareOutputConfig(config: any): WidgetConfigComponentData {
    return config;
  }

  /**
   * validate config.
   *
   * @returns boolean observable or value
   */

  public validateConfig(): boolean {
    return this.configForm().valid;
  }

  /**
   * setup default datasource.
   *
   * @param configData config data (WidgetConfigComponentData)
   * @param keys keys (DataKey[])
   * @param latestKeys latest keys (DataKey[])
   */

  protected setupDefaultDatasource(configData: WidgetConfigComponentData, keys?: DataKey[], latestKeys?: DataKey[]) {
    let datasources = configData.config.datasources;
    if (!datasources || !datasources.length) {
      datasources = [
        {
          type: DatasourceType.device,
          dataKeys: []
        }
      ];
      configData.config.datasources = datasources;
    }
    let dataKeys = datasources[0].dataKeys;
    if (!dataKeys) {
      dataKeys = [];
      datasources[0].dataKeys = dataKeys;
    }
    let latestDataKeys = datasources[0].latestDataKeys;
    if (!latestDataKeys) {
      latestDataKeys = [];
      datasources[0].latestDataKeys = latestDataKeys;
    }
    if (keys && keys.length) {
      dataKeys.length = 0;
      keys.forEach(key => {
        const dataKey = this.constructDataKey(configData, key, false);
        dataKeys.push(dataKey);
      });
    }
    if (latestKeys && latestKeys.length) {
      latestDataKeys.length = 0;
      latestKeys.forEach(key => {
        const dataKey = this.constructDataKey(configData, key, true);
        latestDataKeys.push(dataKey);
      });
    }
  }

  /**
   * construct data key.
   *
   * @param configData config data (WidgetConfigComponentData)
   * @param key key (DataKey)
   * @param isLatestKey is latest key (boolean)
   * @returns DataKey observable or value
   */

  protected constructDataKey(configData: WidgetConfigComponentData, key: DataKey, isLatestKey: boolean): DataKey {
    const dataKey =
      this.widgetConfigComponent.widgetConfigCallbacks.generateDataKey(key.name, key.type,
        configData.dataKeySettingsForm, isLatestKey, configData.dataKeySettingsFunction);
    if (key.label) {
      dataKey.label = key.label;
    }
    if (key.units) {
      dataKey.units = key.units;
    }
    if (isDefinedAndNotNull(key.decimals)) {
      dataKey.decimals = key.decimals;
    }
    if (key.color) {
      dataKey.color = key.color;
    }
    if (isDefinedAndNotNull(key.settings)) {
      dataKey.settings = key.settings;
    }
    if (isDefinedAndNotNull(key.aggregationType)) {
      dataKey.aggregationType = key.aggregationType;
    }
    if (isDefinedAndNotNull(key.comparisonEnabled)) {
      dataKey.comparisonEnabled = key.comparisonEnabled;
    }
    if (isDefinedAndNotNull(key.timeForComparison)) {
      dataKey.timeForComparison = key.timeForComparison;
    }
    if (isDefinedAndNotNull(key.comparisonCustomIntervalValue)) {
      dataKey.comparisonCustomIntervalValue = key.comparisonCustomIntervalValue;
    }
    if (isDefinedAndNotNull(key.comparisonResultType)) {
      dataKey.comparisonResultType = key.comparisonResultType;
    }
    return dataKey;
  }

  protected abstract configForm(): UntypedFormGroup;

  protected abstract onConfigSet(configData: WidgetConfigComponentData);

}
