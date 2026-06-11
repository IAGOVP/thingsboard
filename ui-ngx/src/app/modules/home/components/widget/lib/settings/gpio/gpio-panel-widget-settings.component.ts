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

import { Component } from '@angular/core';
import { WidgetSettings, WidgetSettingsComponent } from '@shared/models/widget.models';
import { AbstractControl, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { GpioItem, gpioItemValidator } from '@home/components/widget/lib/settings/gpio/gpio-item.component';


/**
 * Angular component: gpio panel widget settings (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-gpio-panel-widget-settings`.
 */
@Component({
    selector: 'tb-gpio-panel-widget-settings',
    templateUrl: './gpio-panel-widget-settings.component.html',
    styleUrls: ['./../widget-settings.scss'],
standalone: false
})
export class GpioPanelWidgetSettingsComponent extends WidgetSettingsComponent {

  gpioPanelWidgetSettingsForm: UntypedFormGroup;

  constructor(protected store: Store<AppState>,
              private fb: UntypedFormBuilder) {
    super(store);
  }

  /**
   * settings form.
   *
   * @returns UntypedFormGroup observable or value
   */

  protected settingsForm(): UntypedFormGroup {
    return this.gpioPanelWidgetSettingsForm;
  }

  /**
   * default settings.
   *
   * @returns WidgetSettings observable or value
   */

  protected defaultSettings(): WidgetSettings {
    return {
      ledPanelBackgroundColor: '#008a00',
      gpioList: []
    };
  }

  /**
   * Event handler for settings set.
   *
   * @param settings settings (WidgetSettings)
   */

  protected onSettingsSet(settings: WidgetSettings) {
    this.gpioPanelWidgetSettingsForm = this.fb.group({

      // Panel settings

      ledPanelBackgroundColor: [settings.ledPanelBackgroundColor, [Validators.required]],

      // --> GPIO leds

      gpioList: this.prepareGpioListFormArray(settings.gpioList),

    });
  }

  /**
   * do update settings.
   *
   * @param settingsForm settings form (UntypedFormGroup)
   * @param settings settings (WidgetSettings)
   */

  protected doUpdateSettings(settingsForm: UntypedFormGroup, settings: WidgetSettings) {
    settingsForm.setControl('gpioList', this.prepareGpioListFormArray(settings.gpioList), {emitEvent: false});
  }

  /**
   * prepare gpio list form array.
   *
   * @param gpioList gpio list (GpioItem[] | undefined)
   * @returns UntypedFormArray observable or value
   */

  private prepareGpioListFormArray(gpioList: GpioItem[] | undefined): UntypedFormArray {
    const gpioListControls: Array<AbstractControl> = [];
    if (gpioList) {
      gpioList.forEach((gpioItem) => {
        gpioListControls.push(this.fb.control(gpioItem, [gpioItemValidator(true)]));
      });
    }
    return this.fb.array(gpioListControls, [(control: AbstractControl) => {
      const gpioItems = control.value;
      if (!gpioItems || !gpioItems.length) {
        return {
          gpioItems: true
        };
      }
      return null;
    }]);
  }

  /**
   * gpio list form array.
   *
   * @returns UntypedFormArray observable or value
   */

  gpioListFormArray(): UntypedFormArray {
    return this.gpioPanelWidgetSettingsForm.get('gpioList') as UntypedFormArray;
  }

  get typedSelectOptions() {
    return this.gpioListFormArray().controls as (AbstractControl & { new?: boolean })[];
  }

  /**
   * track by gpio item.
   *
   * @param index index (number)
   * @param gpioItemControl gpio item control (AbstractControl)
   * @returns any observable or value
   */

  public trackByGpioItem(index: number, gpioItemControl: AbstractControl): any {
    return gpioItemControl;
  }

  /**
   * DELETE — remove gpio item.
   *
   * @param index index (number)
   */

  public removeGpioItem(index: number) {
    (this.gpioPanelWidgetSettingsForm.get('gpioList') as UntypedFormArray).removeAt(index);
  }

  /**
   * POST/PUT entity — add gpio item.
   *
   */

  public addGpioItem() {
    const gpioItem: GpioItem = {
      pin: null,
      label: null,
      row: null,
      col: null,
      color: null
    };
    const gpioListArray = this.gpioPanelWidgetSettingsForm.get('gpioList') as UntypedFormArray;
    const gpioItemControl = this.fb.control(gpioItem, [gpioItemValidator(true)]);
    (gpioItemControl as any).new = true;
    gpioListArray.push(gpioItemControl);
    this.gpioPanelWidgetSettingsForm.updateValueAndValidity();
    if (!this.gpioPanelWidgetSettingsForm.valid) {
      this.onSettingsChanged(this.gpioPanelWidgetSettingsForm.value);
    }
  }
}
