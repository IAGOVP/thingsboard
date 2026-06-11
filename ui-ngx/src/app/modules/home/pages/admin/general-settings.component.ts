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

import { Component, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { PageComponent } from '@shared/components/page.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  AdminSettings,
  DeviceConnectivityProtocol,
  DeviceConnectivitySettings,
  GeneralSettings
} from '@shared/models/settings.models';
import { AdminService } from '@core/http/admin.service';
import { HasConfirmForm } from '@core/guards/confirm-on-exit.guard';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';


/**
 * Angular component: general settings (home/admin pages).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-general-settings`.
 */
@Component({
    selector: 'tb-general-settings',
    templateUrl: './general-settings.component.html',
    styleUrls: ['./general-settings.component.scss', './settings-card.scss'],
standalone: false
})
export class GeneralSettingsComponent extends PageComponent implements HasConfirmForm, OnDestroy {

  generalSettings: FormGroup;
  deviceConnectivitySettingsForm: FormGroup;

  protocol: DeviceConnectivityProtocol = 'http';

  private adminSettings: AdminSettings<GeneralSettings>;
  private deviceConnectivitySettings: AdminSettings<DeviceConnectivitySettings>;

  private readonly destroy$ = new Subject<void>();

  constructor(protected store: Store<AppState>,
              private adminService: AdminService,
              public fb: FormBuilder) {
    super(store);
    this.buildGeneralServerSettingsForm();
    this.adminService.getAdminSettings<GeneralSettings>('general')
      .subscribe(adminSettings => this.processGeneralSettings(adminSettings));
    this.buildDeviceConnectivitySettingsForm();
    this.adminService.getAdminSettings<DeviceConnectivitySettings>('connectivity')
      .subscribe(deviceConnectivitySettings => this.processDeviceConnectivitySettings(deviceConnectivitySettings));
  }

  /**
   * Angular lifecycle hook: unsubscribe and release resources.
   *
   */

  ngOnDestroy() {
    super.ngOnDestroy();
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * build general server settings form.
   *
   */

  private buildGeneralServerSettingsForm() {
    this.generalSettings = this.fb.group({
      baseUrl: ['', [Validators.required]],
      prohibitDifferentUrl: ['',[]]
    });
  }

  /**
   * build device connectivity settings form.
   *
   */

  private buildDeviceConnectivitySettingsForm() {
    this.deviceConnectivitySettingsForm = this.fb.group({
      http: this.buildDeviceConnectivityInfoForm(),
      https: this.buildDeviceConnectivityInfoForm(),
      mqtt: this.buildDeviceConnectivityInfoForm(),
      mqtts: this.buildDeviceConnectivityInfoForm(),
      coap: this.buildDeviceConnectivityInfoForm(),
      coaps: this.buildDeviceConnectivityInfoForm()
    });
  }

  /**
   * build device connectivity info form.
   *
   * @returns FormGroup observable or value
   */

  private buildDeviceConnectivityInfoForm(): FormGroup {
    const formGroup = this.fb.group({
      enabled: [false, []],
      host: [{value: '', disabled: true}],
      port: [{value: null, disabled: true}, [Validators.min(1), Validators.max(65535), Validators.pattern('[0-9]*')]]
    });
    formGroup.get('enabled').valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(value => {
      if (value) {
        formGroup.get('host').enable({emitEvent: false});
        formGroup.get('port').enable({emitEvent: false});
      } else {
        formGroup.get('host').disable({emitEvent: false});
        formGroup.get('port').disable({emitEvent: false});
      }
    });
    return formGroup;
  }

  /**
   * POST/PUT entity — save.
   *
   */

  save(): void {
    this.adminSettings.jsonValue = {...this.adminSettings.jsonValue, ...this.generalSettings.value};
    this.adminService.saveAdminSettings(this.adminSettings)
      .subscribe(adminSettings => this.processGeneralSettings(adminSettings));
  }

  /**
   * POST/PUT entity — save device connectivity settings.
   *
   */

  saveDeviceConnectivitySettings(): void {
    this.deviceConnectivitySettings.jsonValue = {
      ...this.deviceConnectivitySettings.jsonValue,
      ...this.deviceConnectivitySettingsForm.getRawValue()
    };
    this.adminService.saveAdminSettings<DeviceConnectivitySettings>(this.deviceConnectivitySettings)
      .subscribe(deviceConnectivitySettings => this.processDeviceConnectivitySettings(deviceConnectivitySettings));
  }

  /**
   * discard general settings.
   *
   */

  discardGeneralSettings(): void {
    this.generalSettings.reset(this.adminSettings.jsonValue);
  }

  /**
   * discard device connectivity settings.
   *
   */

  discardDeviceConnectivitySettings(): void {
    this.deviceConnectivitySettingsForm.reset(this.deviceConnectivitySettings.jsonValue);
  }

  /**
   * process general settings.
   *
   * @param generalSettings general settings (AdminSettings<GeneralSettings>)
   */

  private processGeneralSettings(generalSettings: AdminSettings<GeneralSettings>): void {
    this.adminSettings = generalSettings;
    this.generalSettings.reset(this.adminSettings.jsonValue);
  }

  /**
   * process device connectivity settings.
   *
   * @param deviceConnectivitySettings device connectivity settings (AdminSettings<DeviceConnectivitySettings>)
   */

  private processDeviceConnectivitySettings(deviceConnectivitySettings: AdminSettings<DeviceConnectivitySettings>): void {
    this.deviceConnectivitySettings = deviceConnectivitySettings;
    this.deviceConnectivitySettingsForm.reset(this.deviceConnectivitySettings.jsonValue);
  }

  /**
   * confirm form.
   *
   * @returns FormGroup observable or value
   */

  confirmForm(): FormGroup {
    return this.generalSettings.dirty ? this.generalSettings : this.deviceConnectivitySettingsForm;
  }

}
