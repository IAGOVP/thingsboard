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

import { Component, EventEmitter, forwardRef, Input, OnInit, Output, SkipSelf } from '@angular/core';
import {
  ControlValueAccessor, FormGroupDirective,
  NG_VALUE_ACCESSOR, NgForm,
  UntypedFormBuilder, UntypedFormControl,
  UntypedFormGroup,
  Validators
} from '@angular/forms';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { DocumentationLink } from '@shared/models/user-settings.models';
import { ErrorStateMatcher } from '@angular/material/core';


/**
 * Angular component: doc link (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-doc-link`.
 */
@Component({
    selector: 'tb-doc-link',
    templateUrl: './doc-link.component.html',
    styleUrls: ['./link.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DocLinkComponent),
            multi: true
        },
        { provide: ErrorStateMatcher, useExisting: DocLinkComponent }
    ],
standalone: false
})
export class DocLinkComponent extends PageComponent implements OnInit, ControlValueAccessor, ErrorStateMatcher {

  @Input()
  disabled: boolean;

  @Input()
  addOnly = false;

  @Input()
  disableEdit = false;

  @Output()
  docLinkAdded = new EventEmitter<DocumentationLink>();

  @Output()
  docLinkAddCanceled = new EventEmitter<void>();

  @Output()
  docLinkUpdated = new EventEmitter<DocumentationLink>();

  @Output()
  docLinkDeleted = new EventEmitter<void>();

  @Output()
  editModeChanged = new EventEmitter<boolean>();

  editMode = false;
  addMode = false;

  docLink: DocumentationLink;

  private propagateChange = null;

  public editDocLinkFormGroup: UntypedFormGroup;

  private submitted = false;

  constructor(protected store: Store<AppState>,
              private fb: UntypedFormBuilder,
              @SkipSelf() private errorStateMatcher: ErrorStateMatcher) {
    super(store);
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    this.addMode = this.addOnly;
    this.editDocLinkFormGroup = this.fb.group({
      icon: [null, [Validators.required]],
      name: [null, [Validators.required]],
      link: [null, [Validators.required]]
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
    const customErrorState = !!(control && control.invalid && this.submitted);
    return originalErrorState || customErrorState;
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
      this.editDocLinkFormGroup.disable({emitEvent: false});
    } else {
      this.editDocLinkFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (DocumentationLink)
   */

  writeValue(value: DocumentationLink): void {
    this.docLink = value;
    this.editDocLinkFormGroup.patchValue(
      value, {emitEvent: false}
    );
    if (!this.editDocLinkFormGroup.valid) {
      this.addMode = true;
      this.editModeChanged.emit(true);
    }
  }

  /**
   * switch to edit mode.
   *
   */

  switchToEditMode() {
    if (!this.disableEdit && !this.editMode) {
      this.submitted = false;
      this.editDocLinkFormGroup.patchValue(
        this.docLink, {emitEvent: false}
      );
      this.editMode = true;
      this.editModeChanged.emit(true);
    }
  }

  /**
   * apply.
   *
   */

  apply() {
    this.submitted = true;
    this.updateModel();
    if (this.editDocLinkFormGroup.valid) {
      this.editMode = false;
      this.editModeChanged.emit(false);
      this.docLinkUpdated.next(this.editDocLinkFormGroup.value);
    }
  }

  /**
   * cancel edit.
   *
   */

  cancelEdit() {
    this.submitted = false;
    this.editMode = false;
    this.editModeChanged.emit(false);
  }

  /**
   * POST/PUT entity — add.
   *
   */

  add() {
    this.submitted = true;
    this.updateModel();
    if (this.editDocLinkFormGroup.valid) {
      if (!this.addOnly) {
        this.addMode = false;
        this.editModeChanged.emit(false);
      }
      this.docLinkAdded.next(this.editDocLinkFormGroup.value);
    }
  }

  /**
   * cancel add.
   *
   */

  cancelAdd() {
    this.editModeChanged.emit(false);
    this.docLinkAddCanceled.emit();
  }

  /**
   * DELETE — delete.
   *
   */

  delete() {
    this.docLinkDeleted.emit();
  }

  /**
   * is editing.
   *
   */

  isEditing() {
    return this.editMode || this.addMode;
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    if (this.editDocLinkFormGroup.valid) {
      this.docLink = this.editDocLinkFormGroup.value;
      this.propagateChange(this.editDocLinkFormGroup.value);
    } else {
      this.propagateChange(null);
    }
  }

}
