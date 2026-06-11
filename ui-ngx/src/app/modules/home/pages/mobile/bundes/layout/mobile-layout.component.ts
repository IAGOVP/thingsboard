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

import { booleanAttribute, Component, ElementRef, forwardRef, Input, ViewChild } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  FormArray,
  FormBuilder,
  FormControl,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator
} from '@angular/forms';
import {
  CustomMobilePage,
  getDefaultMobileMenuItem,
  isDefaultMobilePagesConfig,
  MobileLayoutConfig,
  mobileMenuDividers,
  MobilePage,
  MobilePageType
} from '@shared/models/mobile-app.models';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { deepClone, isDefined } from '@core/utils';
import { Subject } from 'rxjs';
import { BreakpointObserver } from '@angular/cdk/layout';
import { MediaBreakpoints } from '@shared/models/constants';
import { MatDialog } from '@angular/material/dialog';
import { AddMobilePageDialogComponent } from '@home/pages/mobile/bundes/layout/add-mobile-page-dialog.component';


/**
 * Angular component: mobile layout (home/mobile pages).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-mobile-layout`.
 */
@Component({
    selector: 'tb-mobile-layout',
    templateUrl: './mobile-layout.component.html',
    styleUrls: ['./mobile-layout.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => MobileLayoutComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => MobileLayoutComponent),
            multi: true
        }
    ],
standalone: false
})
export class MobileLayoutComponent implements ControlValueAccessor, Validator {

  @Input({transform: booleanAttribute})
  readonly!: boolean

  @ViewChild('mobilePagesContainer')
  mobilePagesContainer: ElementRef<HTMLElement>;

  pagesForm = this.fb.group({
    pages: this.fb.array<MobilePage>([])
  });

  maxIconNameBlockWidth = 256;

  showHiddenPages = new FormControl(true);

  private hideItemsSubject = new Subject<void>();
  hideItems$ = this.hideItemsSubject.asObservable();

  private propagateChange = (_val: any) => {};

  constructor(private fb: FormBuilder,
              private breakpointObserver: BreakpointObserver,
              private dialog: MatDialog,
              ) {

    this.pagesForm.valueChanges.pipe(
      takeUntilDestroyed()
    ).subscribe(
      () => this.updateModel()
    );

    this.breakpointObserver.observe([MediaBreakpoints.xs, MediaBreakpoints['gt-xs'], MediaBreakpoints['gt-sm']]).pipe(
      takeUntilDestroyed()
    ).subscribe(() => {
      this.computeMaxIconNameBlockWidth();
    });
    this.computeMaxIconNameBlockWidth();
  }

  /**
   * register on change.
   *
   * @param fn fn (any)
   */

  registerOnChange(fn: any) {
    this.propagateChange = fn;
  }

  /**
   * register on touched.
   *
   * @param _fn  fn (any)
   */

  registerOnTouched(_fn: any) {
  }

  /**
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean) {
    if (isDisabled) {
      this.pagesForm.disable({emitEvent: false});
    } else {
      this.pagesForm.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param layout layout (MobileLayoutConfig)
   */

  writeValue(layout: MobileLayoutConfig) {
    const processLayout = this.prepareMobilePages(layout);
    this.pagesForm.setControl('pages', this.prepareMobilePagesFormArray(processLayout));
  }

  /**
   * validate.
   *
   * @param _c  c (FormControl)
   */

  validate(_c: FormControl) {
    if (!this.pagesForm.valid) {
      return {
        invalidLayoutForm: true
      };
    }
    return null;
  }

  /**
   * hide all.
   *
   */

  hideAll() {
    this.hideItemsSubject.next();
    if (this.showHiddenPages.value) {
      this.showHiddenPages.setValue(false);
    }
  }

  /**
   * reset to default.
   *
   */

  resetToDefault() {
    if (!isDefaultMobilePagesConfig(this.pagesForm.value.pages as MobilePage[])) {
      const processLayout = this.prepareMobilePages(null);
      this.pagesForm.setControl('pages', this.prepareMobilePagesFormArray(processLayout));
    }
  }

  get dragEnabled(): boolean {
    return !this.readonly && this.visibleMobilePagesControls().length > 1;
  }

  /**
   * visible mobile pages controls.
   *
   * @returns Array<AbstractControl> observable or value
   */

  visibleMobilePagesControls(): Array<AbstractControl> {
    return this.pagesFormArray().controls.filter(c => this.showHiddenPages.value || c.value.visible);
  }

  /**
   * mobile item drop.
   *
   * @param event DOM or Angular event object
   */

  mobileItemDrop(event: CdkDragDrop<string[]>) {
    const menuItemsArray = this.pagesFormArray();
    const menuItem = this.visibleMobilePagesControls()[event.previousIndex];
    const previousIndex = this.actualMobilePageIndex(event.previousIndex);
    const currentIndex = this.actualMobilePageIndex(event.currentIndex);
    menuItemsArray.removeAt(previousIndex);
    menuItemsArray.insert(currentIndex, menuItem);
    this.pagesForm.markAsDirty();
  }

  /**
   * track by menu item.
   *
   * @param _index  index (number)
   * @param menuItemControl menu item control (AbstractControl)
   * @returns any observable or value
   */

  trackByMenuItem(_index: number, menuItemControl: AbstractControl): any {
    return menuItemControl;
  }

  /**
   * POST/PUT entity — add custom mobile page.
   *
   * @param index index (number)
   */

  addCustomMobilePage(index?: number) {
    this.dialog.open<AddMobilePageDialogComponent, null,
      CustomMobilePage>(AddMobilePageDialogComponent, {
      disableClose: true,
      panelClass: ['tb-dialog', 'tb-fullscreen-dialog']
    }).afterClosed().subscribe((menuItem) => {
      if (menuItem) {
        const menuItemsArray = this.pagesFormArray();
        const menuItemControl = this.fb.control(menuItem, []);
        if (isDefined(index)) {
          const insertIndex = this.actualMobilePageIndex(index) + 1;
          menuItemsArray.insert(insertIndex, menuItemControl);
        } else {
          menuItemsArray.push(menuItemControl);
          setTimeout(() => {
            this.mobilePagesContainer.nativeElement.scrollTop = this.mobilePagesContainer.nativeElement.scrollHeight;
          }, 0);
        }
        this.pagesForm.markAsDirty();
      }
    });
  }

  /**
   * is custom.
   *
   * @param menuItemControl menu item control (AbstractControl)
   * @returns boolean observable or value
   */

  isCustom(menuItemControl: AbstractControl): boolean {
    return menuItemControl.value.type !== MobilePageType.DEFAULT;
  }

  /**
   * DELETE — remove custom page.
   *
   * @param index index (number)
   */

  removeCustomPage(index: number) {
    this.pagesFormArray().removeAt(this.actualMobilePageIndex(index));
    this.pagesForm.markAsDirty();
  }

  /**
   * show menu divider.
   *
   * @param index index (number)
   * @returns boolean observable or value
   */

  showMenuDivider(index: number): boolean {
    return mobileMenuDividers.has(index);
  }

  /**
   * get divider label.
   *
   * @param index index (number)
   * @returns string observable or value
   */

  getDividerLabel(index: number): string {
    return mobileMenuDividers.get(index);
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    if (isDefaultMobilePagesConfig(this.pagesForm.value.pages as MobilePage[])) {
      this.propagateChange(null);
    } else {
      this.propagateChange(this.pagesForm.value);
    }
  }

  /**
   * prepare mobile pages.
   *
   * @param layout layout (MobileLayoutConfig)
   */

  private prepareMobilePages(layout: MobileLayoutConfig) {
    if (!layout?.pages?.length) {
      return getDefaultMobileMenuItem();
    }
    return layout.pages;
  }

  /**
   * prepare mobile pages form array.
   *
   * @param items items (MobilePage[])
   * @returns FormArray<FormControl<MobilePage>> observable or value
   */

  private prepareMobilePagesFormArray(items: MobilePage[]): FormArray<FormControl<MobilePage>> {
    const menuItemsControls: Array<FormControl<MobilePage>> = [];
    items.forEach((item) => {
      menuItemsControls.push(this.fb.control(deepClone(item)));
    });
    return this.fb.array(menuItemsControls);
  }

  /**
   * compute max icon name block width.
   *
   */

  private computeMaxIconNameBlockWidth() {
    if (this.breakpointObserver.isMatched(MediaBreakpoints['gt-sm'])) {
      this.maxIconNameBlockWidth = 256;
    } else if (this.breakpointObserver.isMatched(MediaBreakpoints['gt-xs'])) {
      this.maxIconNameBlockWidth = 200;
    } else {
      this.maxIconNameBlockWidth = 0;
    }
  }

  /**
   * pages form array.
   *
   * @returns FormArray observable or value
   */

  private pagesFormArray(): FormArray {
    return this.pagesForm.get('pages') as FormArray;
  }

  /**
   * actual mobile page index.
   *
   * @param index index (number)
   * @returns number observable or value
   */

  private actualMobilePageIndex(index: number): number {
    const menuItem = this.visibleMobilePagesControls()[index];
    return this.pagesFormArray().controls.indexOf(menuItem);
  }

}
