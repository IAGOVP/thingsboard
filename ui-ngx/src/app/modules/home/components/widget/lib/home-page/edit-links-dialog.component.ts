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

import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { AbstractControl, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { DialogComponent } from '@shared/components/dialog.component';
import { Router } from '@angular/router';
import { DocumentationLink, DocumentationLinks, QuickLinks } from '@shared/models/user-settings.models';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { UserSettingsService } from '@core/http/user-settings.service';
import { Observable } from 'rxjs';

export interface EditLinksDialogData {
  mode: 'docs' | 'quickLinks';
  links: DocumentationLinks | QuickLinks;
}


/**
 * Angular component: edit links dialog (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-edit-links-dialog`.
 */
@Component({
    selector: 'tb-edit-links-dialog',
    templateUrl: './edit-links-dialog.component.html',
    styleUrls: ['./edit-links-dialog.component.scss'],
standalone: false
})
export class EditLinksDialogComponent extends
  DialogComponent<EditLinksDialogComponent, boolean> implements OnInit {

  updated = false;
  addMode = false;
  editMode = false;

  links = this.data.links;
  mode = this.data.mode;
  addingLink: Partial<DocumentationLink> | string;

  editLinksFormGroup: UntypedFormGroup;

  constructor(protected store: Store<AppState>,
              protected router: Router,
              @Inject(MAT_DIALOG_DATA) public data: EditLinksDialogData,
              public dialogRef: MatDialogRef<EditLinksDialogComponent, boolean>,
              public fb: UntypedFormBuilder,
              private userSettingsService: UserSettingsService) {
    super(store, router, dialogRef);
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    const linksControls: Array<AbstractControl> = [];
    for (const link of this.links.links) {
      linksControls.push(this.fb.control(link, [Validators.required]));
    }
    this.editLinksFormGroup = this.fb.group({
      links: this.fb.array(linksControls)
    });
  }

  /**
   * links form array.
   *
   * @returns UntypedFormArray observable or value
   */

  linksFormArray(): UntypedFormArray {
    return this.editLinksFormGroup.get('links') as UntypedFormArray;
  }

  /**
   * track by link.
   *
   * @param index index (number)
   * @param linkControl link control (AbstractControl)
   * @returns any observable or value
   */

  trackByLink(index: number, linkControl: AbstractControl): any {
    return linkControl;
  }

  /**
   * link drop.
   *
   * @param event DOM or Angular event object
   */

  linkDrop(event: CdkDragDrop<string[]>) {
    const linksArray = this.editLinksFormGroup.get('links') as UntypedFormArray;
    const link = linksArray.at(event.previousIndex);
    linksArray.removeAt(event.previousIndex);
    linksArray.insert(event.currentIndex, link);
    this.update();
  }

  /**
   * POST/PUT entity — add link.
   *
   */

  addLink() {
    this.addingLink = this.mode === 'docs' ? { icon: 'notifications' } : null;
    this.addMode = true;
  }

  /**
   * link added.
   *
   * @param link link (DocumentationLink | string)
   */

  linkAdded(link: DocumentationLink | string) {
    this.addMode = false;
    const linksArray = this.editLinksFormGroup.get('links') as UntypedFormArray;
    const linkControl = this.fb.control(link, [Validators.required]);
    linksArray.push(linkControl);
    this.update();
  }

  /**
   * DELETE — delete link.
   *
   * @param index index (number)
   */

  deleteLink(index: number) {
    (this.editLinksFormGroup.get('links') as UntypedFormArray).removeAt(index);
    this.update();
  }

  /**
   * update.
   *
   */

  update() {
    if (this.editLinksFormGroup.valid) {
      let updateObservable: Observable<void>;
      if (this.mode === 'docs') {
        updateObservable = this.userSettingsService.updateDocumentationLinks(this.editLinksFormGroup.value);
      } else {
        updateObservable = this.userSettingsService.updateQuickLinks(this.editLinksFormGroup.value);
      }
      updateObservable.subscribe(() => {
        this.updated = true;
      });
    }
  }

  /**
   * close.
   *
   */

  close(): void {
    this.dialogRef.close(this.updated);
  }
}
