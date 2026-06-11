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

import { Component, forwardRef, OnDestroy } from '@angular/core';
import {
  ControlValueAccessor,
  UntypedFormBuilder,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import {
  Lwm2mClientSecretKeyTooltipTranslationsMap,
  Lwm2mPublicKeyOrIdTooltipTranslationsMap,
  Lwm2mSecurityType,
  Lwm2mSecurityTypeTranslationMap,
  ServerSecurityConfig
} from '@shared/models/lwm2m-security-config.models';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';



/**
 * Angular component: device credentials lwm2m server (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-device-credentials-lwm2m-server`.
 */
@Component({
    selector: 'tb-device-credentials-lwm2m-server',
    templateUrl: './device-credentials-lwm2m-server.component.html',
    styleUrls: [],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DeviceCredentialsLwm2mServerComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => DeviceCredentialsLwm2mServerComponent),
            multi: true
        }
    ],

standalone: false
})

export class DeviceCredentialsLwm2mServerComponent implements OnDestroy, ControlValueAccessor, Validator {

  serverFormGroup: UntypedFormGroup;
  securityConfigLwM2MType = Lwm2mSecurityType;
  securityConfigLwM2MTypes = Object.values(Lwm2mSecurityType);
  lwm2mSecurityTypeTranslationMap = Lwm2mSecurityTypeTranslationMap;
  publicKeyOrIdTooltipNamesMap = Lwm2mPublicKeyOrIdTooltipTranslationsMap;
  clientSecretKeyTooltipNamesMap = Lwm2mClientSecretKeyTooltipTranslationsMap;

  private destroy$ = new Subject<void>();
  private propagateChange = (v: any) => {};

  constructor(private fb: UntypedFormBuilder) {
    this.serverFormGroup = this.fb.group({
      securityMode: [Lwm2mSecurityType.NO_SEC],
      clientPublicKeyOrId: [''],
      clientSecretKey: ['']
    });
    this.serverFormGroup.get('securityMode').valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe((securityMode) => {
      this.updateValidate(securityMode);
    });

    this.serverFormGroup.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe((value) => {
      this.propagateChange(value);
    });
  }

  /**
   * write value.
   *
   * @param value value (any)
   */

  writeValue(value: any): void {
    if (value) {
      this.updateValueFields(value);
    }
  }

  /**
   * register on change.
   *
   * @param fn fn (any)
   */

  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }

  /**
   * register on touched.
   *
   * @param fn fn (any)
   */

  registerOnTouched(fn: any): void {
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    if (isDisabled) {
      this.serverFormGroup.disable({emitEvent: false});
    } else {
      this.serverFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * validate.
   *
   * @returns ValidationErrors | null observable or value
   */

  validate(control): ValidationErrors | null {
    return this.serverFormGroup.valid ? null : {
      securityConfig: {valid: false}
    };
  }

  /**
   * Angular lifecycle hook: unsubscribe and release resources.
   *
   */

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * update value fields.
   *
   * @param serverData server data (ServerSecurityConfig)
   */

  private updateValueFields(serverData: ServerSecurityConfig): void {
    this.serverFormGroup.patchValue(serverData, {emitEvent: false});
    this.updateValidate(serverData.securityMode, true);
  }

  /**
   * update validate.
   *
   * @param securityMode security mode (Lwm2mSecurityType)
   */

  private updateValidate(securityMode: Lwm2mSecurityType, initValue = false): void {
    switch (securityMode) {
      case Lwm2mSecurityType.NO_SEC:
        this.serverFormGroup.get('clientPublicKeyOrId').clearValidators();
        this.serverFormGroup.get('clientSecretKey').clearValidators();
        this.serverFormGroup.get('clientPublicKeyOrId').disable({emitEvent: false});
        this.serverFormGroup.get('clientSecretKey').disable();
        break;
      case Lwm2mSecurityType.PSK:
      case Lwm2mSecurityType.RPK:
      case Lwm2mSecurityType.X509:
        this.setValidatorsSecurity();
        break;
    }
    this.serverFormGroup.get('clientPublicKeyOrId').updateValueAndValidity({emitEvent: false});
    this.serverFormGroup.get('clientSecretKey').updateValueAndValidity({emitEvent: !initValue});
  }

  private setValidatorsSecurity = (): void => {
    const clientSecretKeyValidators = [Validators.required];
    const clientPublicKeyOrIdValidators = [Validators.required];

    this.serverFormGroup.get('clientPublicKeyOrId').setValidators(clientPublicKeyOrIdValidators);
    this.serverFormGroup.get('clientSecretKey').setValidators(clientSecretKeyValidators);

    this.serverFormGroup.get('clientPublicKeyOrId').enable({emitEvent: false});
    this.serverFormGroup.get('clientSecretKey').enable();
  };
}
