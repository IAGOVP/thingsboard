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

import {
  Component,
  DestroyRef,
  forwardRef,
  HostBinding,
  Input,
  OnInit,
  QueryList,
  ViewChildren,
  ViewEncapsulation
} from '@angular/core';
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
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { TranslateService } from '@ngx-translate/core';
import {
  cleanupFormProperties,
  FormProperty,
  FormPropertyType,
  propertyValid
} from '@shared/models/dynamic-form.models';
import {
  DynamicFormPropertyRowComponent
} from '@home/components/widget/lib/settings/common/dynamic-form/dynamic-form-property-row.component';
import { coerceBoolean } from '@shared/decorators/coercion';
import { ImportExportService } from '@shared/import-export/import-export.service';
import { DialogService } from '@core/services/dialog.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: dynamic form properties (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-dynamic-form-properties`.
 */
@Component({
    selector: 'tb-dynamic-form-properties',
    templateUrl: './dynamic-form-properties.component.html',
    styleUrls: ['./dynamic-form-properties.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DynamicFormPropertiesComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => DynamicFormPropertiesComponent),
            multi: true
        }
    ],
    encapsulation: ViewEncapsulation.None,
standalone: false
})
export class DynamicFormPropertiesComponent implements ControlValueAccessor, OnInit, Validator {

  @HostBinding('style.display') styleDisplay = 'flex';
  @HostBinding('style.overflow') styleOverflow = 'hidden';
  @HostBinding('style.height')
  get containerHeight(): string {
    return this.fillHeight ? '100%': 'auto';
  }

  @ViewChildren(DynamicFormPropertyRowComponent)
  propertyRows: QueryList<DynamicFormPropertyRowComponent>;

  @Input()
  disabled: boolean;

  @Input()
  @coerceBoolean()
  noBorder = false;

  @Input()
  @coerceBoolean()
  noMargin = false;

  @Input()
  @coerceBoolean()
  fillHeight = false;

  @Input()
  @coerceBoolean()
  importExport = false;

  @Input()
  exportFileName = 'form';

  booleanPropertyIds: string[] = [];

  propertiesFormGroup: UntypedFormGroup;

  errorText = '';

  get dragEnabled(): boolean {
    return !this.disabled && this.propertiesFormArray().controls.length > 1;
  }

  private propagateChange = (_val: any) => {};

  constructor(private fb: UntypedFormBuilder,
              private translate: TranslateService,
              private importExportService: ImportExportService,
              private dialogService: DialogService,
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
        const properties = this.getProperties();
        this.booleanPropertyIds = properties.filter(p => p.type === FormPropertyType.switch).map(p => p.id);
        properties.forEach((p, i) => {
          if (p.disableOnProperty && !this.booleanPropertyIds.includes(p.disableOnProperty)) {
            p.disableOnProperty = null;
            const controls = this.propertiesFormArray().controls;
            controls[i].patchValue(p, {emitEvent: false});
          }
        });
        this.propagateChange(cleanupFormProperties(properties));
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
   * @param value value (FormProperty[] | undefined)
   */

  writeValue(value: FormProperty[] | undefined): void {
    const properties= value || [];
    this.propertiesFormGroup.setControl('properties', this.preparePropertiesFormArray(properties), {emitEvent: false});
    this.booleanPropertyIds = properties.filter(p => p.type === FormPropertyType.switch).map(p => p.id);
  }

  /**
   * validate.
   *
   * @param _c  c (UntypedFormControl)
   */

  public validate(_c: UntypedFormControl) {
    this.errorText = '';
    const propertiesArray = this.propertiesFormGroup.get('properties') as UntypedFormArray;
    const notUniqueControls =
      propertiesArray.controls.filter(control => control.hasError('propertyIdNotUnique'));
    for (const control of notUniqueControls) {
      control.updateValueAndValidity({onlySelf: false, emitEvent: false});
      if (control.hasError('propertyIdNotUnique')) {
        this.errorText = this.translate.instant('dynamic-form.property.not-unique-property-ids-error');
      }
    }
    const valid =  this.propertiesFormGroup.valid;
    return valid ? null : {
      properties: {
        valid: false,
      },
    };
  }

  /**
   * property id unique.
   *
   * @param id id (string)
   * @param index index (number)
   * @returns boolean observable or value
   */

  public propertyIdUnique(id: string, index: number): boolean {
    const propertiesArray = this.propertiesFormGroup.get('properties') as UntypedFormArray;
    for (let i = 0; i < propertiesArray.controls.length; i++) {
      if (i !== index) {
        const otherControl = propertiesArray.controls[i];
        if (id === otherControl.value.id) {
          return false;
        }
      }
    }
    return true;
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
    const property: FormProperty = {
      id: '',
      name: '',
      type: FormPropertyType.text,
      default: ''
    };
    const propertiesArray = this.propertiesFormGroup.get('properties') as UntypedFormArray;
    const propertyControl = this.fb.control(property, []);
    propertiesArray.push(propertyControl);
    setTimeout(() => {
      const propertyRow = this.propertyRows.get(this.propertyRows.length-1);
      propertyRow.onAdd(() => {
        this.removeProperty(propertiesArray.length-1);
      });
    });
  }

  /**
   * export.
   *
   */

  export($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    const properties = this.getProperties();
    this.importExportService.exportFormProperties(properties, this.exportFileName);
  }

  /**
   * import.
   *
   */

  import($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    this.importExportService.importFormProperties().subscribe((properties) => {
      if (properties) {
        this.propertiesFormGroup.setControl('properties', this.preparePropertiesFormArray(properties), {emitEvent: true});
      }
    });
  }

  /**
   * clear.
   *
   */

  clear($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialogService.confirm(this.translate.instant('dynamic-form.clear-form'),
      this.translate.instant('dynamic-form.clear-form-prompt'), null, this.translate.instant('action.clear'))
    .subscribe((clear) => {
      if (clear) {
        (this.propertiesFormGroup.get('properties') as UntypedFormArray).clear({emitEvent: true});
      }
    });
  }

  /**
   * prepare properties form array.
   *
   * @param properties properties (FormProperty[] | undefined)
   * @returns UntypedFormArray observable or value
   */

  private preparePropertiesFormArray(properties: FormProperty[] | undefined): UntypedFormArray {
    const propertiesControls: Array<AbstractControl> = [];
    if (properties) {
      properties.forEach((property) => {
        propertiesControls.push(this.fb.control(property, []));
      });
    }
    return this.fb.array(propertiesControls);
  }

  /**
   * get properties.
   *
   * @returns FormProperty[] observable or value
   */

  private getProperties(): FormProperty[] {
    let properties: FormProperty[] = this.propertiesFormGroup.get('properties').value;
    if (properties) {
      properties = properties.filter(p => propertyValid(p));
    }
    return properties;
  }
}
