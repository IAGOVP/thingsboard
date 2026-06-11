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
  ChangeDetectorRef,
  Component,
  forwardRef,
  Input,
  OnInit,
  Renderer2,
  ViewContainerRef,
  ViewEncapsulation
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import {
  BackgroundSettings,
  backgroundStyle,
  BackgroundType,
  ComponentStyle,
  overlayStyle, validateAndUpdateBackgroundSettings
} from '@shared/models/widget-settings.models';
import { MatButton } from '@angular/material/button';
import { TbPopoverService } from '@shared/components/popover.service';
import {
  BackgroundSettingsPanelComponent
} from '@home/components/widget/lib/settings/common/background-settings-panel.component';
import { Observable, of, pipe } from 'rxjs';
import { ImagePipe } from '@shared/pipe/image.pipe';
import { DomSanitizer } from '@angular/platform-browser';
import { tap } from 'rxjs/operators';


/**
 * Angular component: background settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-background-settings`.
 */
@Component({
    selector: 'tb-background-settings',
    templateUrl: './background-settings.component.html',
    styleUrls: ['./background-settings.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => BackgroundSettingsComponent),
            multi: true
        }
    ],
    encapsulation: ViewEncapsulation.None,
standalone: false
})
export class BackgroundSettingsComponent implements OnInit, ControlValueAccessor {

  @Input()
  disabled = false;

  backgroundType = BackgroundType;

  modelValue: BackgroundSettings;

  backgroundStyle$: Observable<ComponentStyle>;

  overlayStyle: ComponentStyle = {};

  private propagateChange = null;

  constructor(private imagePipe: ImagePipe,
              private sanitizer: DomSanitizer,
              private popoverService: TbPopoverService,
              private renderer: Renderer2,
              private viewContainerRef: ViewContainerRef,
              private cd: ChangeDetectorRef) {}

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
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
    if (this.disabled !== isDisabled) {
      this.disabled = isDisabled;
      this.updateBackgroundStyle();
    }
  }

  /**
   * write value.
   *
   * @param value value (BackgroundSettings)
   */

  writeValue(value: BackgroundSettings): void {
    this.modelValue = validateAndUpdateBackgroundSettings(value);
    this.updateBackgroundStyle();
  }

  /**
   * open background settings popup.
   *
   * @param matButton mat button (MatButton)
   */

  openBackgroundSettingsPopup($event: Event, matButton: MatButton) {
    if ($event) {
      $event.stopPropagation();
    }
    const trigger = matButton._elementRef.nativeElement;
    if (this.popoverService.hasPopover(trigger)) {
      this.popoverService.hidePopover(trigger);
    } else {
     const backgroundSettingsPanelPopover = this.popoverService.displayPopover({
       trigger,
       renderer: this.renderer,
       componentType: BackgroundSettingsPanelComponent,
       hostView: this.viewContainerRef,
       preferredPlacement: 'left',
       context: {
         backgroundSettings: this.modelValue
       },
       isModal: true
     });
      backgroundSettingsPanelPopover.tbComponentRef.instance.popover = backgroundSettingsPanelPopover;
      backgroundSettingsPanelPopover.tbComponentRef.instance.backgroundSettingsApplied.subscribe((backgroundSettings) => {
        backgroundSettingsPanelPopover.hide();
        this.modelValue = backgroundSettings;
        this.updateBackgroundStyle();
        this.propagateChange(this.modelValue);
      });
    }
  }

  /**
   * update background style.
   *
   */

  private updateBackgroundStyle() {
    if (!this.disabled) {
      this.backgroundStyle$ = backgroundStyle(this.modelValue, this.imagePipe, this.sanitizer,  true);
      this.overlayStyle = overlayStyle(this.modelValue.overlay);
    } else {
      this.backgroundStyle$ = of({});
      this.overlayStyle = {};
    }
    this.cd.markForCheck();
  }

}
