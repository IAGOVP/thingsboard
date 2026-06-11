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

import { Component, DestroyRef, forwardRef, Input, OnInit, Renderer2, ViewContainerRef } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  Validator,
  Validators
} from '@angular/forms';
import { PageComponent } from '@shared/components/page.component';
import {
  entityTypesWithoutRelatedData,
  EntityTypeVersionLoadConfig,
  exportableEntityTypes,
  typesWithCalculatedFields
} from '@shared/models/vc.models';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { TranslateService } from '@ngx-translate/core';
import { EntityType, entityTypeTranslations } from '@shared/models/entity-type.models';
import { MatCheckbox } from '@angular/material/checkbox';
import { TbPopoverService } from '@shared/components/popover.service';
import { RemoveOtherEntitiesConfirmComponent } from '@home/components/vc/remove-other-entities-confirm.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';


/**
 * Angular component: entity types version load (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-entity-types-version-load`.
 */
@Component({
    selector: 'tb-entity-types-version-load',
    templateUrl: './entity-types-version-load.component.html',
    styleUrls: ['./entity-types-version.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => EntityTypesVersionLoadComponent),
            multi: true
        },
        {
            provide: NG_VALIDATORS,
            useExisting: forwardRef(() => EntityTypesVersionLoadComponent),
            multi: true
        }
    ],
standalone: false
})
export class EntityTypesVersionLoadComponent extends PageComponent implements OnInit, ControlValueAccessor, Validator {

  @Input()
  disabled: boolean;

  private modelValue: {[entityType: string]: EntityTypeVersionLoadConfig};

  private propagateChange = null;

  public entityTypesVersionLoadFormGroup: UntypedFormGroup;

  entityTypes = EntityType;
  entityTypesWithoutRelatedData = entityTypesWithoutRelatedData;

  loading = true;

  readonly typesWithCalculatedFields = typesWithCalculatedFields;

  constructor(protected store: Store<AppState>,
              private translate: TranslateService,
              private popoverService: TbPopoverService,
              private renderer: Renderer2,
              private viewContainerRef: ViewContainerRef,
              private fb: UntypedFormBuilder,
              private destroyRef: DestroyRef) {
    super(store);
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    this.entityTypesVersionLoadFormGroup = this.fb.group({
      entityTypes: this.fb.array([], [])
    });
    this.entityTypesVersionLoadFormGroup.valueChanges.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      this.updateModel();
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
   * set disabled state.
   *
   * @param isDisabled is disabled (boolean)
   */

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    if (isDisabled) {
      this.entityTypesVersionLoadFormGroup.disable({emitEvent: false});
    } else {
      this.entityTypesVersionLoadFormGroup.enable({emitEvent: false});
    }
  }

  /**
   * write value.
   *
   * @param value value ({[entityType: string]: EntityTypeVersionLoadConfig} | undefined)
   */

  writeValue(value: {[entityType: string]: EntityTypeVersionLoadConfig} | undefined): void {
    this.modelValue = value;
    this.entityTypesVersionLoadFormGroup.setControl('entityTypes',
      this.prepareEntityTypesFormArray(value), {emitEvent: false});
  }

  /**
   * validate.
   *
   * @param c c (UntypedFormControl)
   */

  public validate(c: UntypedFormControl) {
    return this.entityTypesVersionLoadFormGroup.valid && this.entityTypesFormGroupArray().length ? null : {
      entityTypes: {
        valid: false,
      },
    };
  }

  /**
   * prepare entity types form array.
   *
   * @param entityTypes entity types ({[entityType: string]: EntityTypeVersionLoadConfig} | undefined)
   * @returns UntypedFormArray observable or value
   */

  private prepareEntityTypesFormArray(entityTypes: {[entityType: string]: EntityTypeVersionLoadConfig} | undefined): UntypedFormArray {
    const entityTypesControls: Array<AbstractControl> = [];
    if (entityTypes) {
      for (const entityType of Object.keys(entityTypes)) {
        const config = entityTypes[entityType];
        entityTypesControls.push(this.createEntityTypeControl(entityType as EntityType, config));
      }
    }
    return this.fb.array(entityTypesControls);
  }

  /**
   * POST/PUT entity — create entity type control.
   *
   * @param entityType entity type (EntityType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns AbstractControl observable or value
   */

  private createEntityTypeControl(entityType: EntityType, config: EntityTypeVersionLoadConfig): AbstractControl {
    const entityTypeControl = this.fb.group(
      {
        entityType: [entityType, [Validators.required]],
        config: this.fb.group({
          loadRelations: [config.loadRelations, []],
          loadAttributes: [config.loadAttributes, []],
          loadCredentials: [config.loadCredentials, []],
          loadCalculatedFields: [config.loadCalculatedFields, []],
          removeOtherEntities: [config.removeOtherEntities, []],
          findExistingEntityByName: [config.findExistingEntityByName, []]
        })
      }
    );
    return entityTypeControl;
  }

  /**
   * entity types form group array.
   *
   * @returns UntypedFormGroup[] observable or value
   */

  entityTypesFormGroupArray(): UntypedFormGroup[] {
    return (this.entityTypesVersionLoadFormGroup.get('entityTypes') as UntypedFormArray).controls as UntypedFormGroup[];
  }

  /**
   * entity types form group expanded.
   *
   * @param entityTypeControl entity type control (AbstractControl)
   * @returns boolean observable or value
   */

  entityTypesFormGroupExpanded(entityTypeControl: AbstractControl): boolean {
    return !!(entityTypeControl as any).expanded;
  }

  /**
   * track by entity type.
   *
   * @param index index (number)
   * @param entityTypeControl entity type control (AbstractControl)
   * @returns any observable or value
   */

  public trackByEntityType(index: number, entityTypeControl: AbstractControl): any {
    return entityTypeControl;
  }

  /**
   * DELETE — remove entity type.
   *
   * @param index index (number)
   */

  public removeEntityType(index: number) {
    (this.entityTypesVersionLoadFormGroup.get('entityTypes') as UntypedFormArray).removeAt(index);
  }

  /**
   * POST/PUT entity — add enabled.
   *
   * @returns boolean observable or value
   */

  public addEnabled(): boolean {
    const entityTypesArray = this.entityTypesVersionLoadFormGroup.get('entityTypes') as UntypedFormArray;
    return entityTypesArray.length < exportableEntityTypes.length;
  }

  /**
   * POST/PUT entity — add entity type.
   *
   */

  public addEntityType() {
    const entityTypesArray = this.entityTypesVersionLoadFormGroup.get('entityTypes') as UntypedFormArray;
    const config: EntityTypeVersionLoadConfig = {
      loadAttributes: true,
      loadRelations: true,
      loadCredentials: true,
      loadCalculatedFields: true,
      removeOtherEntities: false,
      findExistingEntityByName: true
    };
    const allowed = this.allowedEntityTypes();
    let entityType: EntityType = null;
    if (allowed.length) {
      entityType = allowed[0];
    }
    const entityTypeControl = this.createEntityTypeControl(entityType, config);
    (entityTypeControl as any).expanded = true;
    entityTypesArray.push(entityTypeControl);
    this.entityTypesVersionLoadFormGroup.updateValueAndValidity();
  }

  /**
   * DELETE — remove all.
   *
   */

  public removeAll() {
    const entityTypesArray = this.entityTypesVersionLoadFormGroup.get('entityTypes') as UntypedFormArray;
    entityTypesArray.clear();
    this.entityTypesVersionLoadFormGroup.updateValueAndValidity();
  }

  /**
   * entity type text.
   *
   * @param entityTypeControl entity type control (AbstractControl)
   * @returns string observable or value
   */

  entityTypeText(entityTypeControl: AbstractControl): string {
    const entityType: EntityType = entityTypeControl.get('entityType').value;
    if (entityType) {
      return this.translate.instant(entityTypeTranslations.get(entityType).typePlural);
    } else {
      return 'Undefined';
    }
  }

  /**
   * allowed entity types.
   *
   * @param entityTypeControl entity type control (AbstractControl)
   * @returns Array<EntityType> observable or value
   */

  allowedEntityTypes(entityTypeControl?: AbstractControl): Array<EntityType> {
    let res = [...exportableEntityTypes];
    const currentEntityType: EntityType = entityTypeControl?.get('entityType')?.value;
    const value: [{entityType: string; config: EntityTypeVersionLoadConfig}] =
      this.entityTypesVersionLoadFormGroup.get('entityTypes').value || [];
    const usedEntityTypes = value.map(val => val.entityType).filter(val => val);
    res = res.filter(entityType => !usedEntityTypes.includes(entityType) || entityType === currentEntityType);
    return res;
  }

  /**
   * Event handler for remove other entities.
   *
   * @param removeOtherEntitiesCheckbox remove other entities checkbox (MatCheckbox)
   * @param entityTypeControl entity type control (AbstractControl)
   */

  onRemoveOtherEntities(removeOtherEntitiesCheckbox: MatCheckbox, entityTypeControl: AbstractControl) {
    const removeOtherEntities: boolean = entityTypeControl.get('config.removeOtherEntities').value;
    if (removeOtherEntities) {
      entityTypeControl.get('config').get('removeOtherEntities').patchValue(false, {emitEvent: true});
      const trigger = $('.mdc-checkbox__background', removeOtherEntitiesCheckbox._elementRef.nativeElement)[0];
      if (this.popoverService.hasPopover(trigger)) {
        this.popoverService.hidePopover(trigger);
      } else {
        const removeOtherEntitiesConfirmPopover = this.popoverService.displayPopover({
          trigger,
          renderer: this.renderer,
          componentType: RemoveOtherEntitiesConfirmComponent,
          hostView: this.viewContainerRef,
          preferredPlacement: 'bottom',
          context: {
            onClose: (result: boolean | null) => {
              removeOtherEntitiesConfirmPopover.hide();
              if (result) {
                entityTypeControl.get('config').get('removeOtherEntities').patchValue(true, {emitEvent: true});
              }
            }
          },
          showCloseButton: false,
          isModal: true
        });
      }
    }
  }

  /**
   * update model.
   *
   */

  private updateModel() {
    const value: [{entityType: string; config: EntityTypeVersionLoadConfig}] =
      this.entityTypesVersionLoadFormGroup.get('entityTypes').value || [];
    let modelValue: {[entityType: string]: EntityTypeVersionLoadConfig} = null;
    if (value && value.length) {
      modelValue = {};
      value.forEach((val) => {
        modelValue[val.entityType] = val.config;
      });
    }
    this.modelValue = modelValue;
    this.propagateChange(this.modelValue);
  }
}
