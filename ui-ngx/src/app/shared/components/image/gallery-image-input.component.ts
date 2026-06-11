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

import { ChangeDetectorRef, Component, DestroyRef, forwardRef, Input, OnDestroy, OnInit } from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR, } from '@angular/forms';
import { coerceBoolean } from '@shared/decorators/coercion';
import {
  extractParamsFromImageResourceUrl,
  ImageResourceInfo,
  isBase64DataImageUrl,
  isImageResourceUrl,
  prependTbImagePrefix,
  removeTbImagePrefix,
  ResourceSubType
} from '@shared/models/resource.models';
import { ImageService } from '@core/http/image.service';
import { MatDialog } from '@angular/material/dialog';
import {
  ImageGalleryDialogComponent,
  ImageGalleryDialogData
} from '@shared/components/image/image-gallery-dialog.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

export enum ImageLinkType {
  none = 'none',
  base64 = 'base64',
  external = 'external',
  resource = 'resource'
}


/**
 * Angular component: gallery image input (shared UI components).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-gallery-image-input`.
 */
@Component({
    selector: 'tb-gallery-image-input',
    templateUrl: './gallery-image-input.component.html',
    styleUrls: ['./gallery-image-input.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => GalleryImageInputComponent),
            multi: true
        }
    ],
standalone: false
})
export class GalleryImageInputComponent extends PageComponent implements OnInit, OnDestroy, ControlValueAccessor {

  @Input()
  label: string;

  @Input()
  @coerceBoolean()
  required = false;

  @Input()
  disabled: boolean;

  imageUrl: string;

  imageResource: ImageResourceInfo;

  loadingImageResource = false;

  ImageLinkType = ImageLinkType;

  linkType: ImageLinkType = ImageLinkType.none;

  externalLinkControl = new FormControl(null);

  private propagateChange = null;

  constructor(protected store: Store<AppState>,
              private imageService: ImageService,
              private dialog: MatDialog,
              private cd: ChangeDetectorRef,
              private destroyRef: DestroyRef) {
    super(store);
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    this.externalLinkControl.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe((value) => {
      if (this.linkType === ImageLinkType.external) {
        this.updateModel(value);
      }
    });
  }

  /**
   * Angular lifecycle hook: unsubscribe and release resources.
   *
   */

  ngOnDestroy() {
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
    if (this.disabled) {
      this.detectLinkType();
      this.externalLinkControl.disable({emitEvent: false});
    } else {
      this.externalLinkControl.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value (string)
   */

  writeValue(value: string): void {
    value = removeTbImagePrefix(value);
    if (this.imageUrl !== value) {
      this.reset();
      this.imageUrl = value;
      this.detectLinkType();
      if (this.linkType === ImageLinkType.resource) {
        const params = extractParamsFromImageResourceUrl(this.imageUrl);
        if (params) {
          this.loadingImageResource = true;
          this.imageService.getImageInfo(params.type, params.key, {ignoreLoading: true, ignoreErrors: true}).subscribe(
            {
              next: (res) => {
                this.imageResource = res;
                this.loadingImageResource = false;
                this.cd.markForCheck();
              },
              error: () => {
                this.reset();
                this.loadingImageResource = false;
                this.cd.markForCheck();
              }
            }
          );
        } else {
          this.reset();
          this.cd.markForCheck();
        }
      } else if (this.linkType === ImageLinkType.base64) {
        this.cd.markForCheck();
      } else if (this.linkType === ImageLinkType.external) {
        this.externalLinkControl.setValue(this.imageUrl, {emitEvent: false});
        this.cd.markForCheck();
      }
    }
  }

  /**
   * detect link type.
   *
   */

  private detectLinkType() {
    if (this.imageUrl) {
      if (isImageResourceUrl(this.imageUrl)) {
        this.linkType = ImageLinkType.resource;
      } else if (isBase64DataImageUrl(this.imageUrl)) {
        this.linkType = ImageLinkType.base64;
      } else {
        this.linkType = ImageLinkType.external;
      }
    } else {
      this.linkType = ImageLinkType.none;
    }
  }

  /**
   * update model.
   *
   * @param value value (string)
   */

  private updateModel(value: string, forcedToUpdate = false): void {
    this.cd.markForCheck();
    if (this.imageUrl !== value || forcedToUpdate) {
      this.imageUrl = value;
      this.propagateChange(prependTbImagePrefix(this.imageUrl));
    }
  }

  /**
   * reset.
   *
   */

  private reset() {
    this.linkType = ImageLinkType.none;
    this.imageResource = null;
    this.externalLinkControl.setValue(null, {emitEvent: false});
  }

  /**
   * clear image.
   *
   */

  clearImage() {
    this.reset();
    this.updateModel(null);
  }

  /**
   * set link.
   *
   */

  setLink($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    this.linkType = ImageLinkType.external;
  }

  /**
   * open gallery.
   *
   */

  openGallery($event: Event): void {
    if ($event) {
      $event.stopPropagation();
    }
    this.dialog.open<ImageGalleryDialogComponent, ImageGalleryDialogData,
      ImageResourceInfo>(ImageGalleryDialogComponent, {
        autoFocus: false,
        disableClose: false,
        panelClass: ['tb-dialog', 'tb-fullscreen-dialog'],
        data: {
          imageSubType: ResourceSubType.IMAGE
        }
    }).afterClosed().subscribe((image) => {
      if (image) {
        const forcedToUpdate =  this.imageUrl === image.link && this.imageResource?.descriptor?.etag !== image.descriptor.etag;
        this.linkType = ImageLinkType.resource;
        this.imageResource = image;
        this.updateModel(image.link, forcedToUpdate);
      }
    });
  }

}
