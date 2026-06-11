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
  Component, DestroyRef,
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
import { FormSelectItem } from '@shared/models/dynamic-form.models';
import {
  DynamicFormSelectItemRowComponent,
  selectItemValid
} from '@home/components/widget/lib/settings/common/dynamic-form/dynamic-form-select-item-row.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: dynamic form select items (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-dynamic-form-select-items`.
 */
@Component({
    selector: 'tb-dynamic-form-select-items',
    templateUrl: './dynamic-form-select-items.component.html',
    styleUrls: ['./dynamic-form-select-items.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DynamicFormSelectItemsComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => DynamicFormSelectItemsComponent),
            multi: true
        }
    ],
    encapsulation: ViewEncapsulation.None,
standalone: false
})
export class DynamicFormSelectItemsComponent implements ControlValueAccessor, OnInit, Validator {

  @HostBinding('style.display') styleDisplay = 'flex';

  @ViewChildren(DynamicFormSelectItemRowComponent)
  selectItemRows: QueryList<DynamicFormSelectItemRowComponent>;

  @Input()
  disabled: boolean;

  selectItemsFormGroup: UntypedFormGroup;

  errorText = '';

  get dragEnabled(): boolean {
    return !this.disabled && this.selectItemsFormArray().controls.length > 1;
  }

  private propagateChange = (_val: any) => {};

  constructor(private fb: UntypedFormBuilder,
              private destroyRef: DestroyRef,
              private translate: TranslateService) {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    this.selectItemsFormGroup = this.fb.group({
      selectItems: this.fb.array([])
    });
    this.selectItemsFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(
      () => {
        let items: FormSelectItem[] = this.selectItemsFormGroup.get('selectItems').value;
        if (items) {
          items = items.filter(i => selectItemValid(i));
        }
        this.propagateChange(items);
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
      this.selectItemsFormGroup.disable({emitEvent: false});
    } else {
      this.selectItemsFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (FormSelectItem[] | undefined)
   */

  writeValue(value: FormSelectItem[] | undefined): void {
    const items= value || [];
    this.selectItemsFormGroup.setControl('selectItems', this.prepareSelectItemsFormArray(items), {emitEvent: false});
  }

  /**
   * validate.
   *
   * @param _c  c (UntypedFormControl)
   */

  public validate(_c: UntypedFormControl) {
    this.errorText = '';
    const itemsArray = this.selectItemsFormGroup.get('selectItems') as UntypedFormArray;
    const notUniqueControls =
      itemsArray.controls.filter(control => control.hasError('itemValueNotUnique'));
    for (const control of notUniqueControls) {
      control.updateValueAndValidity({onlySelf: false, emitEvent: false});
      if (control.hasError('itemValueNotUnique')) {
        this.errorText = this.translate.instant('dynamic-form.property.not-unique-select-option-value-error');
      }
    }
    let valid =  this.selectItemsFormGroup.valid;
    if (valid) {
      const items: FormSelectItem[] = this.selectItemsFormGroup.get('selectItems').value;
      valid = !items.some(item => !selectItemValid(item));
    }

    return valid ? null : {
      selectItems: {
        valid: false,
      },
    };
  }

  /**
   * select item value unique.
   *
   * @param value value (any)
   * @param index index (number)
   * @returns boolean observable or value
   */

  public selectItemValueUnique(value: any, index: number): boolean {
    const itemsArray = this.selectItemsFormGroup.get('selectItems') as UntypedFormArray;
    for (let i = 0; i < itemsArray.controls.length; i++) {
      if (i !== index) {
        const otherControl = itemsArray.controls[i];
        if (value === otherControl.value.value) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * select item drop.
   *
   * @param event DOM or Angular event object
   */

  selectItemDrop(event: CdkDragDrop<string[]>) {
    const itemsArray = this.selectItemsFormGroup.get('selectItems') as UntypedFormArray;
    const item = itemsArray.at(event.previousIndex);
    itemsArray.removeAt(event.previousIndex, {emitEvent: false});
    itemsArray.insert(event.currentIndex, item, {emitEvent: true});
  }

  /**
   * select items form array.
   *
   * @returns UntypedFormArray observable or value
   */

  selectItemsFormArray(): UntypedFormArray {
    return this.selectItemsFormGroup.get('selectItems') as UntypedFormArray;
  }

  /**
   * track by select item.
   *
   * @param _index  index (number)
   * @param selectItemControl select item control (AbstractControl)
   * @returns any observable or value
   */

  trackBySelectItem(_index: number, selectItemControl: AbstractControl): any {
    return selectItemControl;
  }

  /**
   * DELETE — remove select item.
   *
   * @param index index (number)
   */

  removeSelectItem(index: number, emitEvent = true) {
    (this.selectItemsFormGroup.get('selectItems') as UntypedFormArray).removeAt(index, {emitEvent});
  }

  /**
   * POST/PUT entity — add select item.
   *
   */

  addSelectItem() {
    const item: FormSelectItem = {
      value: '',
      label: ''
    };
    const itemsArray = this.selectItemsFormGroup.get('selectItems') as UntypedFormArray;
    const itemControl = this.fb.control(item, []);
    itemsArray.push(itemControl);
  }

  /**
   * prepare select items form array.
   *
   * @param items items (FormSelectItem[] | undefined)
   * @returns UntypedFormArray observable or value
   */

  private prepareSelectItemsFormArray(items: FormSelectItem[] | undefined): UntypedFormArray {
    const selectItemsControls: Array<AbstractControl> = [];
    if (items) {
      items.forEach((item) => {
        selectItemsControls.push(this.fb.control(item, []));
      });
    }
    return this.fb.array(selectItemsControls);
  }
}
