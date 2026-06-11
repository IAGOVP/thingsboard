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

import { Component, DestroyRef, ElementRef, forwardRef, Input, OnInit, ViewChild } from '@angular/core';
import { ControlValueAccessor, UntypedFormBuilder, UntypedFormGroup, NG_VALUE_ACCESSOR, Validators } from '@angular/forms';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Datasource } from '@shared/models/widget.models';
import { EntityService } from '@core/http/entity.service';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: datasources key autocomplete (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-datasources-key-autocomplete`.
 */
@Component({
    selector: 'tb-datasources-key-autocomplete',
    templateUrl: './datasources-key-autocomplete.component.html',
    styleUrls: [],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DatasourcesKeyAutocompleteComponent),
            multi: true
        }
    ],
standalone: false
})
export class DatasourcesKeyAutocompleteComponent extends PageComponent implements OnInit, ControlValueAccessor {

  @ViewChild('keyInput') keyInput: ElementRef;

  @Input()
  disabled: boolean;

  @Input()
  datasources: Array<Datasource>;

  @Input()
  label = 'entity.key-name';

  private requiredValue: boolean;
  get required(): boolean {
    return this.requiredValue;
  }
  @Input()
  set required(value: boolean) {
    this.requiredValue = coerceBooleanProperty(value);
  }

  private modelValue: string;

  private propagateChange = null;

  public keyFormGroup: UntypedFormGroup;

  filteredKeys: Observable<Array<string>>;
  keySearchText = '';

  constructor(protected store: Store<AppState>,
              private translate: TranslateService,
              private entityService: EntityService,
              private fb: UntypedFormBuilder,
              private destroyRef: DestroyRef) {
    super(store);
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    this.keyFormGroup = this.fb.group({
      key: [null, this.required ? [Validators.required] : []]
    });
    this.keyFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      this.updateModel();
    });

    this.filteredKeys = this.keyFormGroup.get('key').valueChanges
      .pipe(
        map(value => value ? value : ''),
        mergeMap(name => this.fetchKeys(name) )
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
    this.disabled = isDisabled;
    if (isDisabled) {
      this.keyFormGroup.disable({emitEvent: false});
    } else {
      this.keyFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (string)
   */

  writeValue(value: string): void {
    this.modelValue = value;
    this.keyFormGroup.patchValue(
      {key: value}, {emitEvent: false}
    );
  }

  /**
   * clear key.
   *
   */

  clearKey() {
    this.keyFormGroup.get('key').patchValue(null, {emitEvent: true});
    setTimeout(() => {
      this.keyInput.nativeElement.blur();
      this.keyInput.nativeElement.focus();
    }, 0);
  }

  /**
   * Event handler for focus.
   *
   */

  onFocus() {
    this.keyFormGroup.get('key').updateValueAndValidity({onlySelf: true, emitEvent: true});
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    this.modelValue = this.keyFormGroup.get('key').value;
    this.propagateChange(this.modelValue);
  }

  /**
   * fetch keys.
   *
   * @param searchText search text (string)
   * @returns Observable<Array<string>> observable or value
   */

  private fetchKeys(searchText?: string): Observable<Array<string>> {
      this.keySearchText = searchText;
      const dataKeyFilter = this.createKeyFilter(this.keySearchText);
      return of(this.allKeys()).pipe(
        map(name => name.filter(dataKeyFilter))
      );
  }

  /**
   * POST/PUT entity — create key filter.
   *
   * @param query query (string)
   * @returns (key: string) => boolean observable or value
   */

  private createKeyFilter(query: string): (key: string) => boolean {
    const lowercaseQuery = query.toLowerCase();
    return key => key.toLowerCase().startsWith(lowercaseQuery);
  }

  /**
   * all keys.
   *
   * @returns string[] observable or value
   */

  private allKeys(): string[] {
    const allDataKeys = (this.datasources || []).map(ds => ds.dataKeys)
      .reduce((accumulator, value) => accumulator.concat(value), []).map(dataKey => dataKey.label);
    return [...new Set(allDataKeys)].sort((a, b) => a.localeCompare(b));
  }
}
