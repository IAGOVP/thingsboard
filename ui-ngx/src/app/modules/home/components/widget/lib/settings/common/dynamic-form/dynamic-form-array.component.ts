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

import { Component, DestroyRef, forwardRef, Input, OnInit, ViewEncapsulation } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validator
} from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { defaultFormPropertyValue, FormProperty } from '@shared/models/dynamic-form.models';
import { CdkDragDrop } from '@angular/cdk/drag-drop';


/**
 * Angular component: dynamic form array (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-dynamic-form-array`.
 */
@Component({
    selector: 'tb-dynamic-form-array',
    templateUrl: './dynamic-form-array.component.html',
    styleUrls: ['./dynamic-form-array.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DynamicFormArrayComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => DynamicFormArrayComponent),
            multi: true
        }
    ],
    encapsulation: ViewEncapsulation.None,
standalone: false
})
export class DynamicFormArrayComponent implements ControlValueAccessor, OnInit, Validator {

  @Input()
  disabled: boolean;

  @Input()
  itemProperty: FormProperty;

  @Input()
  title: string;

  propertiesFormGroup: UntypedFormGroup;

  get dragEnabled(): boolean {
    return !this.disabled && this.propertiesFormArray().controls.length > 1;
  }

  private propagateChange = (_val: any) => {};

  constructor(private fb: UntypedFormBuilder,
              private destroyRef: DestroyRef) {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    this.propertiesFormGroup = this.fb.group({
      properties: this.fb.array([])
    });
    this.propertiesFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(
      () => {
        const properties: {[id: string]: any}[] = this.propertiesFormGroup.get('properties').value;
        const value = properties.map(prop => prop[this.itemProperty.id]);
        this.propagateChange(value);
      }
    );
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
   * @param _fn  fn (any)
   */

  registerOnTouched(_fn: any): void {
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (isDisabled) {
      this.propertiesFormGroup.disable({emitEvent: false});
    } else {
      this.propertiesFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param values values (any[] | undefined)
   */

  writeValue(values: any[] | undefined): void {
    this.propertiesFormGroup.setControl('properties', this.preparePropertiesFormArray(values || []), {emitEvent: false});
  }

  /**
   * validate.
   *
   * @param _c  c (UntypedFormControl)
   */

  public validate(_c: UntypedFormControl) {
    const valid =  this.propertiesFormGroup.valid;
    return valid ? null : {
      properties: {
        valid: false,
      },
    };
  }

  /**
   * property drop.
   *
   * @param event DOM or Angular event object
   */

  propertyDrop(event: CdkDragDrop<string[]>) {
    const propertiesArray = this.propertiesFormGroup.get('properties') as UntypedFormArray;
    const property = propertiesArray.at(event.previousIndex);
    propertiesArray.removeAt(event.previousIndex, {emitEvent: false});
    propertiesArray.insert(event.currentIndex, property, {emitEvent: true});
  }

  /**
   * properties form array.
   *
   * @returns UntypedFormArray observable or value
   */

  propertiesFormArray(): UntypedFormArray {
    return this.propertiesFormGroup.get('properties') as UntypedFormArray;
  }

  /**
   * track by property.
   *
   * @param _index  index (number)
   * @param propertyControl property control (AbstractControl)
   * @returns any observable or value
   */

  trackByProperty(_index: number, propertyControl: AbstractControl): any {
    return propertyControl;
  }

  /**
   * DELETE — remove property.
   *
   * @param index index (number)
   */

  removeProperty(index: number, emitEvent = true) {
    (this.propertiesFormGroup.get('properties') as UntypedFormArray).removeAt(index, {emitEvent});
  }

  /**
   * POST/PUT entity — add property.
   *
   */

  addProperty() {
    const property = {
      [this.itemProperty.id]: defaultFormPropertyValue(this.itemProperty)
    };
    const propertiesArray = this.propertiesFormGroup.get('properties') as UntypedFormArray;
    const propertyControl = this.fb.control(property, []);
    propertiesArray.push(propertyControl);
    setTimeout(() => {
      propertyControl.updateValueAndValidity();
    });
  }

  /**
   * prepare properties form array.
   *
   * @param values values (any[] | undefined)
   * @returns UntypedFormArray observable or value
   */

  private preparePropertiesFormArray(values: any[] | undefined): UntypedFormArray {
    const propertiesControls: Array<AbstractControl> = [];
    if (values) {
      values.forEach((value) => {
        const property = {
          [this.itemProperty.id]: value
        };
        propertiesControls.push(this.fb.control(property, []));
      });
    }
    return this.fb.array(propertiesControls);
  }
}
