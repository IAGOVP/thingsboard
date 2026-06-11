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

import { Component, ElementRef, Inject, InjectionToken, OnInit, ViewChild } from '@angular/core';
import { NotificationTemplateTypeTranslateMap, NotificationType } from '@shared/models/notification.models';
import { MatChipInputEvent } from '@angular/material/chips';
import { COMMA, ENTER, SEMICOLON } from '@angular/cdk/keycodes';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
import { FormControl } from '@angular/forms';
import { debounceTime, map } from 'rxjs/operators';
import { OverlayRef } from '@angular/cdk/overlay';

export const NOTIFICATION_TYPE_FILTER_PANEL_DATA = new InjectionToken<any>('NotificationTypeFilterPanelData');

export interface NotificationTypeFilterPanelData {
  notificationTypes: Array<NotificationType>;
  notificationTypesUpdated: (notificationTypes: Array<NotificationType>) => void;
}


/**
 * Angular component: notification type filter panel (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-notification-type-filter-panel`.
 */
@Component({
    selector: 'tb-notification-type-filter-panel',
    templateUrl: './notification-type-filter-panel.component.html',
    styleUrls: ['notification-type-filter-panel.component.scss'],
standalone: false
})
export class NotificationTypeFilterPanelComponent implements OnInit{

  @ViewChild('searchInput') searchInputField: ElementRef;

  searchText = '';
  searchControlName = new FormControl('');

  filteredNotificationTypesList: Observable<Array<NotificationType>>;
  selectedNotificationTypes: Array<NotificationType> = [];
  notificationTypesTranslateMap = NotificationTemplateTypeTranslateMap;

  separatorKeysCodes: number[] = [ENTER, COMMA, SEMICOLON];

  private notificationType = NotificationType;
  private notificationTypes = Object.keys(NotificationType) as Array<NotificationType>;

  private dirty = false;

  @ViewChild('notificationTypeInput') notificationTypeInput: ElementRef<HTMLInputElement>;

  constructor(@Inject(NOTIFICATION_TYPE_FILTER_PANEL_DATA) public data: NotificationTypeFilterPanelData,
              private overlayRef: OverlayRef) {
    this.selectedNotificationTypes = this.data.notificationTypes;
    this.dirty = true;
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    this.filteredNotificationTypesList = this.searchControlName.valueChanges.pipe(
      debounceTime(150),
      map(value => {
        this.searchText = value;
        return this.notificationTypes.filter(type => !this.selectedNotificationTypes.includes(type))
          .filter(type => value ? type.toUpperCase().startsWith(value.toUpperCase()) : true);
      })
    );
  }

  /**
   * update.
   *
   */

  public update() {
    this.data.notificationTypesUpdated(this.selectedNotificationTypes);
    if (this.overlayRef) {
      this.overlayRef.dispose();
    }
  }

  /**
   * cancel.
   *
   */

  cancel() {
    if (this.overlayRef) {
      this.overlayRef.dispose();
    }
  }

  /**
   * reset.
   *
   */

  public reset() {
    this.selectedNotificationTypes.length = 0;
    this.searchControlName.updateValueAndValidity({emitEvent: true});
  }

  /**
   * DELETE — remove.
   *
   * @param type type (NotificationType)
   */

  remove(type: NotificationType) {
    const index = this.selectedNotificationTypes.indexOf(type);
    if (index >= 0) {
      this.selectedNotificationTypes.splice(index, 1);
      this.searchControlName.updateValueAndValidity({emitEvent: true});
    }
  }

  /**
   * Event handler for focus.
   *
   */

  onFocus() {
    if (this.dirty) {
      this.searchControlName.updateValueAndValidity({emitEvent: true});
      this.dirty = false;
    }
  }

  /**
   * POST/PUT entity — add.
   *
   * @param type type (NotificationType)
   */

  private add(type: NotificationType): void {
    this.selectedNotificationTypes.push(type);
  }

  /**
   * chip add.
   *
   * @param event DOM or Angular event object
   */

  chipAdd(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (value && this.notificationType[value]) {
      this.add(this.notificationType[value]);
      this.clear('');
    }
  }

  /**
   * selected.
   *
   * @param event DOM or Angular event object
   */

  selected(event: MatAutocompleteSelectedEvent): void {
    if (this.notificationType[event.option.value]) {
      this.add(this.notificationType[event.option.value]);
    }
    this.clear('');
  }

  /**
   * clear.
   *
   * @param value value (string)
   */

  clear(value: string = '') {
    this.notificationTypeInput.nativeElement.value = value;
    this.searchControlName.patchValue(value, {emitEvent: true});
    setTimeout(() => {
      this.notificationTypeInput.nativeElement.blur();
      this.notificationTypeInput.nativeElement.focus();
    }, 0);
  }

  /**
   * display type fn.
   *
   * @param type type (string)
   * @returns string | undefined observable or value
   */

  displayTypeFn(type?: string): string | undefined {
    return type ? type : undefined;
  }
}
