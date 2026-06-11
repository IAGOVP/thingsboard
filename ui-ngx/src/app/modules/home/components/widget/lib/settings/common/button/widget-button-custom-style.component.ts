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
  OnChanges,
  OnInit,
  Renderer2,
  SimpleChanges,
  ViewContainerRef,
  ViewEncapsulation
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import {
  WidgetButtonAppearance,
  WidgetButtonCustomStyle,
  WidgetButtonState
} from '@shared/components/button/widget-button.models';
import { TbPopoverService } from '@shared/components/popover.service';
import { MatIconButton } from '@angular/material/button';
import {
  WidgetButtonCustomStylePanelComponent
} from '@home/components/widget/lib/settings/common/button/widget-button-custom-style-panel.component';
import { deepClone } from '@core/utils';


/**
 * Angular component: widget button custom style (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-widget-button-custom-style`.
 */
@Component({
    selector: 'tb-widget-button-custom-style',
    templateUrl: './widget-button-custom-style.component.html',
    styleUrls: ['./widget-button-custom-style.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => WidgetButtonCustomStyleComponent),
            multi: true
        }
    ],
    encapsulation: ViewEncapsulation.None,
standalone: false
})
export class WidgetButtonCustomStyleComponent implements OnInit, OnChanges, ControlValueAccessor {

  @Input()
  disabled = false;

  @Input()
  appearance: WidgetButtonAppearance;

  @Input()
  borderRadius: string;

  @Input()
  autoScale: boolean;

  @Input()
  state: WidgetButtonState;

  widgetButtonState = WidgetButtonState;

  modelValue: WidgetButtonCustomStyle;

  previewAppearance: WidgetButtonAppearance;

  private propagateChange = (_val: any) => {};

  constructor(private popoverService: TbPopoverService,
              private renderer: Renderer2,
              private viewContainerRef: ViewContainerRef,
              private cd: ChangeDetectorRef) {}

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    this.updatePreviewAppearance();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const propName of Object.keys(changes)) {
      const change = changes[propName];
      if (!change.firstChange) {
        if (propName === 'appearance') {
          this.updatePreviewAppearance();
        }
      }
    }
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
   * @param _isDisabled  is disabled (boolean)
   */

  setDisabledState(_isDisabled: boolean): void {
  }

  /**
   * write value.
   *
   * @param value value (WidgetButtonCustomStyle)
   */

  writeValue(value: WidgetButtonCustomStyle): void {
    this.modelValue = value;
    this.updatePreviewAppearance();
  }

  /**
   * clear style.
   *
   */

  clearStyle() {
    this.updateModel(null);
  }

  /**
   * open button custom style popup.
   *
   * @param matButton mat button (MatIconButton)
   */

  openButtonCustomStylePopup($event: Event, matButton: MatIconButton) {
    if ($event) {
      $event.stopPropagation();
    }
    const trigger = matButton._elementRef.nativeElement;
    if (this.popoverService.hasPopover(trigger)) {
      this.popoverService.hidePopover(trigger);
    } else {
      const widgetButtonCustomStylePanelPopover = this.popoverService.displayPopover({
        trigger,
        renderer: this.renderer,
        componentType: WidgetButtonCustomStylePanelComponent,
        hostView: this.viewContainerRef,
        preferredPlacement: ['leftTopOnly', 'leftOnly', 'leftBottomOnly'],
        context: {
          appearance: this.appearance,
          borderRadius: this.borderRadius,
          autoScale: this.autoScale,
          state: this.state,
          customStyle: this.modelValue
        },
        isModal: true
      });
      widgetButtonCustomStylePanelPopover.tbComponentRef.instance.popover = widgetButtonCustomStylePanelPopover;
      widgetButtonCustomStylePanelPopover.tbComponentRef.instance.customStyleApplied.subscribe((customStyle) => {
        widgetButtonCustomStylePanelPopover.hide();
        this.updateModel(customStyle);
      });
    }
  }

  /**
   * update model.
   *
   * @param value value (WidgetButtonCustomStyle)
   */

  private updateModel(value: WidgetButtonCustomStyle): void {
    this.modelValue = value;
    this.updatePreviewAppearance();
    this.propagateChange(this.modelValue);
  }

  /**
   * update preview appearance.
   *
   */

  private updatePreviewAppearance() {
    this.previewAppearance = deepClone(this.appearance);
    if (this.modelValue) {
      this.previewAppearance.customStyle[this.state] = this.modelValue;
    }
    this.cd.markForCheck();
  }

}
