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

import { ChangeDetectorRef, Component, Inject, Optional } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { EntityComponent } from '../../components/entity/entity.component';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { User } from '@shared/models/user.model';
import { selectAuth } from '@core/auth/auth.selectors';
import { map } from 'rxjs/operators';
import { Authority } from '@shared/models/authority.enum';
import { isDefinedAndNotNull, validateEmail } from '@core/utils';
import { EntityTableConfig } from '@home/models/entity/entities-table-config.models';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { TranslateService } from '@ngx-translate/core';
import { environment as env } from '@env/environment';
import { UnitSystems } from '@shared/models/unit.models';


/**
 * Angular component: user (home/user pages).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-user`.
 */
@Component({
    selector: 'tb-user',
    templateUrl: './user.component.html',
    styleUrls: ['./user.component.scss'],
standalone: false
})
export class UserComponent extends EntityComponent<User>{

  authority = Authority;
  languageList = env.supportedLangs;
  UnitSystems = UnitSystems;

  loginAsUserEnabled$ = this.store.pipe(
    select(selectAuth),
    map((auth) => auth.userTokenAccessEnabled)
  );

  constructor(protected store: Store<AppState>,
              @Optional() @Inject('entity') protected entityValue: User,
              @Optional() @Inject('entitiesTableConfig') protected entitiesTableConfigValue: EntityTableConfig<User>,
              public fb: UntypedFormBuilder,
              protected cd: ChangeDetectorRef,
              protected translate: TranslateService) {
    super(store, fb, entityValue, entitiesTableConfigValue, cd);
  }

  /**
   * hide delete.
   *
   */

  hideDelete() {
    if (this.entitiesTableConfig) {
      return !this.entitiesTableConfig.deleteEnabled(this.entity);
    } else {
      return false;
    }
  }

  /**
   * is user credentials enabled.
   *
   * @returns boolean observable or value
   */

  isUserCredentialsEnabled(): boolean {
      return this.entity?.additionalInfo?.userCredentialsEnabled === true;
  }

  /**
   * is user activated.
   *
   * @returns boolean observable or value
   */

  isUserActivated(): boolean {
    return this.entity?.additionalInfo?.userActivated === true;
  }

  /**
   * build form.
   *
   * @param entity entity (User)
   * @returns UntypedFormGroup observable or value
   */

  buildForm(entity: User): UntypedFormGroup {
    return this.fb.group(
      {
        email: [entity ? entity.email : '', [Validators.required, validateEmail]],
        firstName: [entity ? entity.firstName : ''],
        lastName: [entity ? entity.lastName : ''],
        phone: [entity ? entity.phone : ''],
        additionalInfo: this.fb.group(
          {
            description: [entity && entity.additionalInfo ? entity.additionalInfo.description : ''],
            lang: [entity && entity.additionalInfo ? entity.additionalInfo.lang : null],
            unitSystem: [entity && entity.additionalInfo ? entity.additionalInfo.unitSystem : null],
            defaultDashboardId: [entity && entity.additionalInfo ? entity.additionalInfo.defaultDashboardId : null],
            defaultDashboardFullscreen: [entity && entity.additionalInfo ? entity.additionalInfo.defaultDashboardFullscreen : false],
            homeDashboardId: [entity && entity.additionalInfo ? entity.additionalInfo.homeDashboardId : null],
            homeDashboardHideToolbar: [entity && entity.additionalInfo &&
            isDefinedAndNotNull(entity.additionalInfo.homeDashboardHideToolbar) ? entity.additionalInfo.homeDashboardHideToolbar : true]
          }
        )
      }
    );
  }

  /**
   * update form.
   *
   * @param entity entity (User)
   */

  updateForm(entity: User) {
    this.entityForm.patchValue({email: entity.email});
    this.entityForm.patchValue({firstName: entity.firstName});
    this.entityForm.patchValue({lastName: entity.lastName});
    this.entityForm.patchValue({phone: entity.phone});
    this.entityForm.patchValue({additionalInfo: {description: entity.additionalInfo ? entity.additionalInfo.description : ''}});
    this.entityForm.patchValue({additionalInfo:
        {lang: entity.additionalInfo ? entity.additionalInfo.lang : null}});
    this.entityForm.patchValue({additionalInfo:
        {unitSystem: entity.additionalInfo ? entity.additionalInfo.unitSystem : null}});
    this.entityForm.patchValue({additionalInfo:
        {defaultDashboardId: entity.additionalInfo ? entity.additionalInfo.defaultDashboardId : null}});
    this.entityForm.patchValue({additionalInfo:
        {defaultDashboardFullscreen: entity.additionalInfo ? entity.additionalInfo.defaultDashboardFullscreen : false}});
    this.entityForm.patchValue({additionalInfo:
        {homeDashboardId: entity.additionalInfo ? entity.additionalInfo.homeDashboardId : null}});
    this.entityForm.patchValue({additionalInfo:
        {homeDashboardHideToolbar: entity.additionalInfo &&
          isDefinedAndNotNull(entity.additionalInfo.homeDashboardHideToolbar) ? entity.additionalInfo.homeDashboardHideToolbar : true}});
  }

  /**
   * Event handler for user id copied.
   *
   */

  onUserIdCopied($event) {
    this.store.dispatch(new ActionNotificationShow(
      {
        message: this.translate.instant('user.idCopiedMessage'),
        type: 'success',
        duration: 750,
        verticalPosition: 'bottom',
        horizontalPosition: 'right'
      }
    ));
  }

}
