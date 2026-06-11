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

import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { StateObject, StateParams } from '@core/api/widget-api.models';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, Observable, of } from 'rxjs';
import { StateControllerState } from './state-controller.models';
import { StateControllerComponent } from './state-controller.component';
import { StatesControllerService } from '@home/components/dashboard-page/states/states-controller.service';
import { EntityId } from '@app/shared/models/id/entity-id';
import { UtilsService } from '@core/services/utils.service';
import { base64toObj, insertVariable, isEmpty, objToBase64 } from '@app/core/utils';
import { DashboardUtilsService } from '@core/services/dashboard-utils.service';
import { EntityService } from '@core/http/entity.service';
import { EntityType } from '@shared/models/entity-type.models';
import { map, tap } from 'rxjs/operators';
import { MobileService } from '@core/services/mobile.service';


/**
 * Angular component: entity state controller (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-entity-state-controller`.
 */
@Component({
    selector: 'tb-entity-state-controller',
    templateUrl: './entity-state-controller.component.html',
    styleUrls: ['./entity-state-controller.component.scss'],
standalone: false
})
export class EntityStateControllerComponent extends StateControllerComponent implements OnInit, OnDestroy {

  selectedStateIndex = -1;

  constructor(protected router: Router,
              protected route: ActivatedRoute,
              protected ngZone: NgZone,
              protected statesControllerService: StatesControllerService,
              private utils: UtilsService,
              private entityService: EntityService,
              private mobileService: MobileService,
              private dashboardUtils: DashboardUtilsService) {
    super(router, route, ngZone, statesControllerService);
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    super.ngOnInit();
  }

  /**
   * Angular lifecycle hook: unsubscribe and release resources.
   *
   */

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  /**
   * init.
   *
   */

  public init() {
    if (this.preservedState) {
      this.stateObject = this.preservedState;
      this.selectedStateIndex = this.stateObject.length - 1;
      setTimeout(() => {
        this.gotoState(this.stateObject[this.stateObject.length - 1].id, true);
      }, 1);
    } else {
      const initialState = this.currentState;
      this.stateObject = this.parseState(initialState);
      this.selectedStateIndex = this.stateObject.length - 1;
      setTimeout(() => {
        this.gotoState(this.stateObject[this.stateObject.length - 1].id, false);
      }, 1);
    }
  }

  /**
   * Event handler for mobile changed.
   *
   */

  protected onMobileChanged() {
  }

  /**
   * Event handler for state id changed.
   *
   */

  protected onStateIdChanged() {
  }

  /**
   * Event handler for states changed.
   *
   */

  protected onStatesChanged() {
  }

  /**
   * Event handler for state changed.
   *
   */

  protected onStateChanged() {
    this.stateObject = this.parseState(this.currentState);
    this.selectedStateIndex = this.stateObject.length - 1;
    this.gotoState(this.stateObject[this.stateObject.length - 1].id, false);
  }

  /**
   * state controller id.
   *
   * @returns string observable or value
   */

  protected stateControllerId(): string {
    return 'entity';
  }

  /**
   * get state params.
   *
   * @returns StateParams observable or value
   */

  public getStateParams(): StateParams {
    if (this.stateObject && this.stateObject.length) {
      return this.stateObject[this.stateObject.length - 1].params;
    } else {
      return {};
    }
  }

  /**
   * open state.
   *
   * @param id id (string)
   * @param params params (StateParams)
   * @param openRightLayout open right layout (boolean)
   */

  public openState(id: string, params?: StateParams, openRightLayout?: boolean): void {
    if (this.states && this.states[id]) {
      this.resolveEntity(params).subscribe(
        () => {
          const newState: StateObject = {
            id,
            params
          };
          this.stateObject.push(newState);
          this.selectedStateIndex = this.stateObject.length - 1;
          this.gotoState(this.stateObject[this.stateObject.length - 1].id, true, openRightLayout);
        }
      );
    }
  }

  /**
   * push and open state.
   *
   * @param states states (Array<StateObject>)
   * @param openRightLayout open right layout (boolean)
   */

  public pushAndOpenState(states: Array<StateObject>, openRightLayout?: boolean): void {
    if (this.states) {
      for (const state of states) {
        if (!this.states[state.id]) {
          return;
        }
      }
      forkJoin(states.map(state => this.resolveEntity(state.params))).subscribe(
        () => {
          this.stateObject.push(...states);
          this.selectedStateIndex = this.stateObject.length - 1;
          this.gotoState(this.stateObject[this.stateObject.length - 1].id, true, openRightLayout);
        }
      );
    }
  }

  /**
   * update state.
   *
   * @param id id (string)
   * @param params params (StateParams)
   * @param openRightLayout open right layout (boolean)
   */

  public updateState(id: string, params?: StateParams, openRightLayout?: boolean): void {
    if (!id) {
      id = this.getStateId();
    }
    if (this.states && this.states[id]) {
      this.resolveEntity(params).subscribe(
        () => {
          this.stateObject[this.stateObject.length - 1] = {
            id,
            params
          };
          this.gotoState(this.stateObject[this.stateObject.length - 1].id, true, openRightLayout);
        }
      );
    }
  }

  /**
   * get entity id.
   *
   * @param entityParamName entity param name (string)
   * @returns EntityId observable or value
   */

  public getEntityId(entityParamName: string): EntityId {
    const stateParams = this.getStateParams();
    if (!entityParamName || !entityParamName.length) {
      return stateParams.entityId;
    } else if (stateParams[entityParamName]) {
      return stateParams[entityParamName].entityId;
    }
    return null;
  }

  /**
   * get state id.
   *
   * @returns string observable or value
   */

  public getStateId(): string {
    if (this.stateObject && this.stateObject.length) {
      return this.stateObject[this.stateObject.length - 1].id;
    } else {
      return '';
    }
  }

  /**
   * get state id at index.
   *
   * @param index index (number)
   * @returns string observable or value
   */

  public getStateIdAtIndex(index: number): string {
    if (this.stateObject && this.stateObject[index]) {
      return this.stateObject[index].id;
    } else {
      return '';
    }
  }

  /**
   * get state index.
   *
   * @returns number observable or value
   */

  public getStateIndex(): number {
    if (this.stateObject && this.stateObject.length) {
      return this.stateObject.length - 1;
    } else {
      return -1;
    }
  }

  /**
   * get state params by state id.
   *
   * @param stateId state id (string)
   * @returns StateParams observable or value
   */

  public getStateParamsByStateId(stateId: string): StateParams {
    const stateObj = this.getStateObjById(stateId);
    if (stateObj) {
      return stateObj.params;
    } else {
      return null;
    }
  }

  /**
   * navigate prev state.
   *
   * @param index index (number)
   * @param params params (StateParams)
   */

  public navigatePrevState(index: number, params?: StateParams): void {
    if (index < this.stateObject.length - 1) {
      this.stateObject.splice(index + 1, this.stateObject.length - index - 1);
      this.selectedStateIndex = this.stateObject.length - 1;
      if (params) {
        this.stateObject[this.selectedStateIndex].params = params;
      }
      this.gotoState(this.stateObject[this.selectedStateIndex].id, true);
    }
  }

  /**
   * reset state.
   *
   */

  public resetState(): void {
    const rootStateId = this.dashboardUtils.getRootStateId(this.states);
    this.stateObject = [ { id: rootStateId, params: {} } ];
    this.gotoState(rootStateId, true);
  }

  /**
   * get state name.
   *
   * @param index index (number)
   * @returns string observable or value
   */

  public getStateName(index: number): string {
    let result = '';
    const state = this.stateObject[index];
    if (state) {
      const dashboardState = this.states[state.id];
      if (dashboardState) {
        let stateName = dashboardState.name;
        stateName = this.utils.customTranslation(stateName, stateName);
        const params = this.stateObject[index].params;
        const entityName = params && params.entityName ? params.entityName : '';
        const entityLabel = params && params.entityLabel ? params.entityLabel : '';
        result = insertVariable(stateName, 'entityName', entityName);
        result = insertVariable(result, 'entityLabel', entityLabel);
        for (const prop of Object.keys(params)) {
          if (params[prop] && params[prop].entityName) {
            result = insertVariable(result, prop + ':entityName', params[prop].entityName);
          }
          if (params[prop] && params[prop].entityLabel) {
            result = insertVariable(result, prop + ':entityLabel', params[prop].entityLabel);
          }
        }
      }
    }
    return result;
  }

  /**
   * get current state name.
   *
   * @returns string observable or value
   */

  public getCurrentStateName(): string {
    return this.getStateName(this.stateObject.length - 1);
  }

  /**
   * selected state index changed.
   *
   */

  public selectedStateIndexChanged() {
    this.navigatePrevState(this.selectedStateIndex);
  }

  /**
   * parse state.
   *
   * @param stateBase64 state base64 (string)
   * @returns StateControllerState observable or value
   */

  private parseState(stateBase64: string): StateControllerState {
    let result: StateControllerState;
    if (stateBase64) {
      try {
        result = base64toObj(stateBase64);
      } catch (e) {
        result = [ { id: null, params: {} } ];
      }
    }
    if (!result) {
      result = [];
    }
    if (!result.length) {
      result[0] = { id: null, params: {} };
    }
    const rootStateId = this.dashboardUtils.getRootStateId(this.states);
    if (!result[0].id) {
      result[0].id = rootStateId;
    }
    if (!this.states[result[0].id]) {
      result[0].id = rootStateId;
    }
    let i = result.length;
    while (i--) {
      if (!result[i].id || !this.states[result[i].id]) {
        result.splice(i, 1);
      }
    }
    return result;
  }

  /**
   * goto state.
   *
   * @param stateId state id (string)
   * @param update update (boolean)
   * @param openRightLayout open right layout (boolean)
   */

  private gotoState(stateId: string, update: boolean, openRightLayout?: boolean) {
    const isStateIdChanged = this.dashboardCtrl.dashboardCtx.state !== stateId;
    this.dashboardCtrl.openDashboardState(stateId, openRightLayout);
    if (isStateIdChanged) {
      this.stateIdSubject.next(stateId);
    }
    if (this.syncStateWithQueryParam) {
      this.mobileService.handleDashboardStateName(this.getStateName(this.stateObject.length - 1));
    }
    if (update) {
      this.updateLocation(isStateIdChanged);
    }
  }

  /**
   * update location.
   *
   * @param isStateIdChanged is state id changed (boolean)
   */

  private updateLocation(isStateIdChanged: boolean) {
    if (this.stateObject[this.stateObject.length - 1].id) {
      let newState;
      if (this.isDefaultState()) {
        newState = null;
      } else {
        newState = objToBase64(this.stateObject);
      }
      this.updateStateParam(newState, !isStateIdChanged);
    }
  }

  /**
   * is default state.
   *
   * @returns boolean observable or value
   */

  private isDefaultState(): boolean {
    if (this.stateObject.length === 1) {
      const state = this.stateObject[0];
      const rootStateId = this.dashboardUtils.getRootStateId(this.states);
      if (state.id === rootStateId && (!state.params || isEmpty(state.params))) {
        return true;
      }
    }
    return false;
  }

  /**
   * resolve entity.
   *
   * @param params params (StateParams)
   * @returns Observable<void> observable or value
   */

  private resolveEntity(params: StateParams): Observable<void> {
    if (params && params.targetEntityParamName) {
      params = params[params.targetEntityParamName];
    }
    if (params && params.entityId && params.entityId.id && params.entityId.entityType) {
      if (this.isEntityResolved(params)) {
        return of(null);
      } else {
        return this.entityService.getEntity(params.entityId.entityType as EntityType,
          params.entityId.id, {ignoreLoading: true, ignoreErrors: true}).pipe(
            tap((entity) => {
              params.entityName = entity.name;
              params.entityLabel = entity.label;
            }),
          map(() => null)
        );
      }
    } else {
      return of(null);
    }
  }

  /**
   * is entity resolved.
   *
   * @param params params (StateParams)
   * @returns boolean observable or value
   */

  private isEntityResolved(params: StateParams): boolean {
    return !(!params.entityName || !params.entityName.length);
  }

  /**
   * get state obj by id.
   *
   * @param id id (string)
   * @returns StateObject observable or value
   */

  private getStateObjById(id: string): StateObject {
    return this.stateObject.find((stateObj) => stateObj.id === id);
  }
}
