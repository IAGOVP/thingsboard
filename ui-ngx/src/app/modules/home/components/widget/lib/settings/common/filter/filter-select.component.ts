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

import { Component, DestroyRef, ElementRef, forwardRef, Input, OnInit, SkipSelf, ViewChild } from '@angular/core';
import {
  ControlValueAccessor,
  FormGroupDirective,
  NG_VALUE_ACCESSOR,
  NgForm,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup
} from '@angular/forms';
import { Observable, of } from 'rxjs';
import { map, mergeMap, share, tap } from 'rxjs/operators';
import { IAliasController } from '@core/api/widget-api.models';
import { MatAutocomplete } from '@angular/material/autocomplete';
import { ENTER } from '@angular/cdk/keycodes';
import { ErrorStateMatcher } from '@angular/material/core';
import { FilterSelectCallbacks } from './filter-select.component.models';
import { Filter } from '@shared/models/query/query.models';
import { coerceBoolean } from '@shared/decorators/coercion';
import { MatFormFieldAppearance, SubscriptSizing } from '@angular/material/form-field';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: filter select (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-filter-select`.
 */
@Component({
    selector: 'tb-filter-select',
    templateUrl: './filter-select.component.html',
    styleUrls: [],
    providers: [{
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => FilterSelectComponent),
            multi: true
        },
        {
            provide: ErrorStateMatcher,
            useExisting: FilterSelectComponent
        }],
standalone: false
})
export class FilterSelectComponent implements ControlValueAccessor, OnInit, ErrorStateMatcher {

  selectFilterFormGroup: UntypedFormGroup;

  modelValue: string | null;

  @Input()
  aliasController: IAliasController;

  @Input()
  callbacks: FilterSelectCallbacks;

  @Input()
  @coerceBoolean()
  showLabel: boolean;

  @Input()
  @coerceBoolean()
  inlineField: boolean;

  @Input()
  appearance: MatFormFieldAppearance = 'fill';

  @Input()
  subscriptSizing: SubscriptSizing = 'fixed';

  @ViewChild('filterAutocomplete') filterAutocomplete: MatAutocomplete;

  @Input()
  @coerceBoolean()
  tbRequired: boolean;

  @Input()
  disabled: boolean;

  @ViewChild('filterInput', {static: true}) filterInput: ElementRef;

  filteredFilters: Observable<Array<Filter>>;

  searchText = '';

  private dirty = false;
  private filterList: Array<Filter> = [];
  private propagateChange = (_v: any) => { };

  constructor(@SkipSelf() private errorStateMatcher: ErrorStateMatcher,
              private fb: UntypedFormBuilder,
              private destroyRef: DestroyRef) {
    this.selectFilterFormGroup = this.fb.group({
      filter: [null]
    });
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
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    this.loadFilters();

    this.filteredFilters = this.selectFilterFormGroup.get('filter').valueChanges
      .pipe(
        /**
         * tap.
         *
         */
        tap(value => {
          let modelValue: Filter;
          if (typeof value === 'string' || !value) {
            modelValue = null;
          } else {
            modelValue = value;
          }
          this.updateView(modelValue);
          if (value === null) {
            this.clear();
          }
        }),
        map(value => value ? (typeof value === 'string' ? value : value.filter) : ''),
        mergeMap(name => this.fetchFilters(name) ),
        share()
      );

    this.aliasController.filtersChanged.pipe(
      takeUntilDestroyed(this.destroyRef),
    ).subscribe(() => {
      this.loadFilters();
    });
  }

  /**
   * is error state.
   *
   * @param control control (UntypedFormControl | null)
   * @param form Angular reactive form group
   * @returns boolean observable or value
   */

  isErrorState(control: UntypedFormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const originalErrorState = this.errorStateMatcher.isErrorState(control, form);
    const customErrorState = this.tbRequired && !this.modelValue;
    return originalErrorState || customErrorState;
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (this.disabled) {
      this.selectFilterFormGroup.disable({emitEvent: false});
    } else {
      this.selectFilterFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (string | null)
   */

  writeValue(value: string | null): void {
    this.searchText = '';
    let filter = null;
    if (value != null) {
      const filters = this.aliasController.getFilters();
      if (filters[value]) {
        filter = filters[value];
      }
    }
    if (filter != null) {
      this.modelValue = filter.id;
      this.selectFilterFormGroup.get('filter').patchValue(filter, {emitEvent: false});
    } else {
      this.modelValue = null;
      this.selectFilterFormGroup.get('filter').patchValue('', {emitEvent: false});
    }
    this.dirty = true;
  }

  /**
   * Event handler for focus.
   *
   */

  onFocus() {
    if (this.dirty) {
      this.selectFilterFormGroup.get('filter').updateValueAndValidity({onlySelf: true, emitEvent: true});
      this.dirty = false;
    }
  }

  /**
   * update view.
   *
   * @param value value (Filter | null)
   */

  updateView(value: Filter | null) {
    const filterId = value ? value.id : null;
    if (this.modelValue !== filterId) {
      this.modelValue = filterId;
      this.propagateChange(this.modelValue);
    }
  }

  /**
   * display filter fn.
   *
   * @param filter filter (Filter)
   * @returns string | undefined observable or value
   */

  displayFilterFn(filter?: Filter): string | undefined {
    return filter ? filter.filter : undefined;
  }

  /**
   * fetch filters.
   *
   * @param searchText search text (string)
   * @returns Observable<Array<Filter>> observable or value
   */

  fetchFilters(searchText?: string): Observable<Array<Filter>> {
    this.searchText = searchText;
    let result = this.filterList;
    if (searchText && searchText.length) {
      result = this.filterList.filter((filter) => filter.filter.toLowerCase().includes(searchText.toLowerCase()));
    }
    return of(result);
  }

  /**
   * clear.
   *
   * @param value value (string)
   */

  clear(value: string = '') {
    this.filterInput.nativeElement.value = value;
    this.selectFilterFormGroup.get('filter').patchValue(value, {emitEvent: true});
    setTimeout(() => {
      this.filterInput.nativeElement.blur();
      this.filterInput.nativeElement.focus();
    }, 0);
  }

  /**
   * text is not empty.
   *
   * @param text text (string)
   * @returns boolean observable or value
   */

  textIsNotEmpty(text: string): boolean {
    return text?.length > 0;
  }

  /**
   * filter enter.
   *
   */

  filterEnter($event: KeyboardEvent) {
    if ($event.keyCode === ENTER) {
      $event.preventDefault();
      if (!this.modelValue) {
        this.createFilter($event, this.searchText);
      }
    }
  }

  /**
   * POST/PUT entity — create filter.
   *
   * @param filter filter (string)
   */

  createFilter($event: Event, filter: string, focusOnCancel = true) {
    $event.preventDefault();
    $event.stopPropagation();
    if (this.callbacks && this.callbacks.createFilter) {
      this.callbacks.createFilter(filter).subscribe((newFilter) => {
          if (!newFilter) {
            if (focusOnCancel) {
              setTimeout(() => {
                this.filterInput.nativeElement.blur();
                this.filterInput.nativeElement.focus();
              }, 0);
            }
          } else {
            this.modelValue = newFilter.id;
            this.selectFilterFormGroup.get('filter').patchValue(newFilter, {emitEvent: true});
            this.propagateChange(this.modelValue);
          }
        }
      );
    }
  }

  /**
   * load filters.
   *
   */

  private loadFilters(): void {
    this.filterList = [];
    const filters = this.aliasController.getFilters();
    for (const filterId of Object.keys(filters)) {
      this.filterList.push(filters[filterId]);
    }
    this.dirty = true;
  }
}
