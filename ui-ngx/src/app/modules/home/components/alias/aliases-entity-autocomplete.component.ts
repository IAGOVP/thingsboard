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

import { AfterViewInit, Component, ElementRef, forwardRef, Input, OnInit, ViewChild } from '@angular/core';
import { ControlValueAccessor, UntypedFormBuilder, UntypedFormGroup, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, map, share, switchMap, tap } from 'rxjs/operators';
import { emptyPageData, PageData } from '@shared/models/page/page-data';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { TranslateService } from '@ngx-translate/core';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { EntityInfo } from '@shared/models/entity.models';
import { EntityFilter } from '@shared/models/query/query.models';
import { EntityService } from '@core/http/entity.service';
import { isDefinedAndNotNull } from '@core/utils';


/**
 * Angular component: aliases entity autocomplete (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-aliases-entity-autocomplete`.
 */
@Component({
    selector: 'tb-aliases-entity-autocomplete',
    templateUrl: './aliases-entity-autocomplete.component.html',
    styleUrls: [],
    providers: [{
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => AliasesEntityAutocompleteComponent),
            multi: true
        }],
standalone: false
})
export class AliasesEntityAutocompleteComponent implements ControlValueAccessor, OnInit, AfterViewInit {

  selectEntityInfoFormGroup: UntypedFormGroup;

  modelValue: EntityInfo | null;

  @Input()
  alias: string;

  @Input()
  entityFilter: EntityFilter;

  private requiredValue: boolean;
  get required(): boolean {
    return this.requiredValue;
  }
  @Input()
  set required(value: boolean) {
    this.requiredValue = coerceBooleanProperty(value);
  }

  @Input()
  disabled: boolean;

  @ViewChild('entityInfoInput', {static: true}) entityInfoInput: ElementRef;

  filteredEntityInfos: Observable<Array<EntityInfo>>;

  searchText = '';

  private propagateChange = (v: any) => { };

  constructor(private store: Store<AppState>,
              public translate: TranslateService,
              private entityService: EntityService,
              private fb: UntypedFormBuilder) {
    this.selectEntityInfoFormGroup = this.fb.group({
      entityInfo: [null]
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
   * @param fn fn (any)
   */

  registerOnTouched(fn: any): void {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    this.filteredEntityInfos = this.selectEntityInfoFormGroup.get('entityInfo').valueChanges
      .pipe(
        debounceTime(150),
        /**
         * tap.
         *
         */
        tap(value => {
          let modelValue;
          if (typeof value === 'string' || !value) {
            modelValue = null;
          } else {
            modelValue = value;
          }
          this.updateView(modelValue);
        }),
        map(value => value ? (typeof value === 'string' ? value : value.name) : ''),
        distinctUntilChanged(),
        switchMap(name => this.fetchEntityInfos(name)),
        share()
      );
  }

  /**
   * Angular lifecycle hook: run after the component view is initialized.
   *
   */

  ngAfterViewInit(): void {
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  /**
   * write value.
   *
   * @param value value (EntityInfo | null)
   */

  writeValue(value: EntityInfo | null): void {
    this.searchText = '';
    if (isDefinedAndNotNull(value)) {
      this.modelValue = value;
      this.selectEntityInfoFormGroup.get('entityInfo').patchValue(value, {emitEvent: true});
    } else {
      this.modelValue = null;
      this.selectEntityInfoFormGroup.get('entityInfo').patchValue(null, {emitEvent: false});
    }
  }

  /**
   * update view.
   *
   * @param value value (EntityInfo | null)
   */

  updateView(value: EntityInfo | null) {
    if (this.modelValue !== value) {
      this.modelValue = value;
      this.propagateChange(this.modelValue);
    }
  }

  /**
   * display entity info fn.
   *
   * @param entityInfo entity info (EntityInfo)
   * @returns string | undefined observable or value
   */

  displayEntityInfoFn(entityInfo?: EntityInfo): string | undefined {
    return entityInfo ? entityInfo.name : undefined;
  }

  /**
   * fetch entity infos.
   *
   * @param searchText search text (string)
   * @returns Observable<Array<EntityInfo>> observable or value
   */

  fetchEntityInfos(searchText?: string): Observable<Array<EntityInfo>> {
    this.searchText = searchText;
    return this.getEntityInfos(this.searchText).pipe(
      map(pageData => {
        return pageData.data;
      })
    );
  }

  /**
   * get entity infos.
   *
   * @param searchText search text (string)
   * @returns Observable<PageData<EntityInfo>> observable or value
   */

  getEntityInfos(searchText: string): Observable<PageData<EntityInfo>> {
    return this.entityService.findEntityInfosByFilterAndName(this.entityFilter, searchText, {ignoreLoading: true}).pipe(
      catchError(() => of(emptyPageData<EntityInfo>()))
    );
  }

  /**
   * clear.
   *
   */

  clear() {
    this.selectEntityInfoFormGroup.get('entityInfo').patchValue(null, {emitEvent: true});
    setTimeout(() => {
      this.entityInfoInput.nativeElement.blur();
      this.entityInfoInput.nativeElement.focus();
    }, 0);
  }

}
