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

import { Component, forwardRef, Input, OnDestroy } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import { ResourceLwM2M } from '@home/components/profile/device/lwm2m/lwm2m-profile-config.models';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { combineLatest, Subject } from 'rxjs';
import { startWith, takeUntil } from 'rxjs/operators';



/**
 * Angular component: lwm2m observe attr telemetry resources (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-profile-lwm2m-observe-attr-telemetry-resource`.
 */
@Component({
    selector: 'tb-profile-lwm2m-observe-attr-telemetry-resource',
    templateUrl: './lwm2m-observe-attr-telemetry-resources.component.html',
    styleUrls: ['./lwm2m-observe-attr-telemetry-resources.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => Lwm2mObserveAttrTelemetryResourcesComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => Lwm2mObserveAttrTelemetryResourcesComponent),
            multi: true
        }
    ],

standalone: false
})

export class Lwm2mObserveAttrTelemetryResourcesComponent implements ControlValueAccessor, OnDestroy, Validator {

  resourcesFormGroup: UntypedFormGroup;

  @Input()
  disabled = false;

  private requiredValue: boolean;
  get required(): boolean {
    return this.requiredValue;
  }

  @Input()
  set required(value: boolean) {
    const newVal = coerceBooleanProperty(value);
    if (this.requiredValue !== newVal) {
      this.requiredValue = newVal;
    }
  }

  private destroy$ = new Subject<void>();
  private propagateChange = (v: any) => { };

  constructor(private fb: UntypedFormBuilder) {
    this.resourcesFormGroup = this.fb.group({
      resources: this.fb.array([])
    });

    this.resourcesFormGroup.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => this.updateModel(this.resourcesFormGroup.getRawValue().resources));
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
   * register on touched.
   *
   * @param fn fn (any)
   */

  registerOnTouched(fn: any): void {
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
   * write value.
   *
   * @param value value (ResourceLwM2M[])
   */

  writeValue(value: ResourceLwM2M[]): void {
    this.updatedResources(value);
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (isDisabled) {
      this.resourcesFormGroup.disable({emitEvent: false});
    } else {
      this.resourcesFormArray.controls.forEach(resource => {
        resource.get('id').enable({emitEvent: false});
        resource.get('name').enable({emitEvent: false});
        resource.get('keyName').enable({emitEvent: false});
        resource.get('attribute').enable({emitEvent: false});
        resource.get('telemetry').enable({onlySelf: true});
        resource.get('attributes').enable({emitEvent: false});
      });
    }
  }

  /**
   * validate.
   *
   * @returns ValidationErrors | null observable or value
   */

  validate(): ValidationErrors | null {
    return this.resourcesFormGroup.valid ? null : {
      resources: false
    };
  }

  get resourcesFormArray(): UntypedFormArray {
    return this.resourcesFormGroup.get('resources') as UntypedFormArray;
  }

  /**
   * get name resource lwm2m.
   *
   * @param resourceLwM2M resource lw m2m (ResourceLwM2M)
   * @returns string observable or value
   */

  getNameResourceLwm2m(resourceLwM2M: ResourceLwM2M): string {
    return `#${resourceLwM2M.id} ${resourceLwM2M.name}`;
  }

  /**
   * updated resources.
   *
   * @param resources resources (ResourceLwM2M[])
   */

  private updatedResources(resources: ResourceLwM2M[]): void {
    if (resources.length === this.resourcesFormArray.length) {
      this.resourcesFormArray.patchValue(resources, {onlySelf: true, emitEvent: false});
    } else {
      const resourcesControl: Array<AbstractControl> = [];
      if (resources) {
        resources.forEach((resource) => {
          resourcesControl.push(this.createdResourceFormGroup(resource));
        });
      }
      this.resourcesFormGroup.setControl('resources', this.fb.array(resourcesControl), {emitEvent: false});
      if (this.disabled) {
        this.resourcesFormGroup.disable({emitEvent: false});
      }
    }
  }

  /**
   * POST/PUT entity — created resource form group.
   *
   * @param resource resource (ResourceLwM2M)
   * @returns UntypedFormGroup observable or value
   */

  private createdResourceFormGroup(resource: ResourceLwM2M): UntypedFormGroup {
    const form = this.fb.group( {
      id: [resource.id],
      name: [resource.name],
      attribute: [resource.attribute],
      telemetry: [resource.telemetry],
      observe: [resource.observe],
      keyName: [resource.keyName, [Validators.required, Validators.pattern('(.|\\s)*\\S(.|\\s)*')]],
      attributes: [resource.attributes]
    });
    combineLatest([
      form.get('attribute').valueChanges.pipe(startWith(resource.attribute), takeUntil(this.destroy$)),
      form.get('telemetry').valueChanges.pipe(startWith(resource.telemetry), takeUntil(this.destroy$))
    ]).subscribe(([attribute, telemetry]) => {
      if (!this.disabled) {
        if (attribute || telemetry) {
          form.get('observe').enable({emitEvent: false});
        } else {
          form.get('observe').disable({emitEvent: false});
          form.get('observe').patchValue(false, {emitEvent: false});
          form.get('attributes').patchValue({}, {emitEvent: false});
        }
      }
    });
    return form;
  }

  /**
   * update model.
   *
   * @param value value (ResourceLwM2M[])
   */

  private updateModel(value: ResourceLwM2M[]) {
    if (value && this.resourcesFormGroup.valid) {
      this.propagateChange(value);
    } else {
      this.propagateChange(null);
    }
  }

  /**
   * track by params.
   *
   * @param index index (number)
   * @param resource resource (ResourceLwM2M)
   * @returns number observable or value
   */

  trackByParams(index: number, resource: ResourceLwM2M): number {
    return resource.id;
  }

  /**
   * is disabled observe.
   *
   * @param index index (number)
   * @returns boolean observable or value
   */

  isDisabledObserve(index: number): boolean{
    return this.resourcesFormArray.at(index).get('observe').disabled;
  }
}
