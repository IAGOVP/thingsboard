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
import { DashboardState } from '@shared/models/dashboard.models';
import { StateControllerState } from './state-controller.models';
import { StateControllerComponent } from './state-controller.component';
import { StatesControllerService } from '@home/components/dashboard-page/states/states-controller.service';
import { EntityId } from '@app/shared/models/id/entity-id';
import { UtilsService } from '@core/services/utils.service';
import { base64toObj, objToBase64 } from '@app/core/utils';
import { DashboardUtilsService } from '@core/services/dashboard-utils.service';
import { EntityService } from '@core/http/entity.service';
import { MobileService } from '@core/services/mobile.service';


/**
 * Angular component: default state controller (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-default-state-controller`.
 */
@Component({
    selector: 'tb-default-state-controller',
    templateUrl: './default-state-controller.component.html',
    styleUrls: ['./default-state-controller.component.scss'],
standalone: false
})
export class DefaultStateControllerComponent extends StateControllerComponent implements OnInit, OnDestroy {

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
      setTimeout(() => {
        this.gotoState(this.stateObject[0].id, true);
      }, 1);
    } else {
      const initialState = this.currentState;
      this.stateObject = this.parseState(initialState);
      setTimeout(() => {
        this.gotoState(this.stateObject[0].id, false);
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
    this.gotoState(this.stateObject[0].id, false);
  }

  /**
   * state controller id.
   *
   * @returns string observable or value
   */

  protected stateControllerId(): string {
    return 'default';
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
      if (!params) {
        params = {};
      }
      const newState: StateObject = {
        id,
        params
      };
      this.stateObject[0] = newState;
      this.gotoState(this.stateObject[0].id, true, openRightLayout);
    }
  }

  /**
   * push and open state.
   *
   * @param states states (Array<StateObject>)
   * @param openRightLayout open right layout (boolean)
   */

  public pushAndOpenState(states: Array<StateObject>, openRightLayout?: boolean): void {
    const state = states[states.length - 1];
    this.openState(state.id, state.params, openRightLayout);
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
      if (!params) {
        params = {};
      }
      const newState: StateObject = {
        id,
        params
      };
      this.stateObject[0] = newState;
      this.gotoState(this.stateObject[0].id, true, openRightLayout);
    }
  }

  /**
   * get entity id.
   *
   * @param entityParamName entity param name (string)
   * @returns EntityId observable or value
   */

  public getEntityId(entityParamName: string): EntityId {
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
    const lastStateIndex = this.stateObject.length - 1;
    if (index < lastStateIndex) {
      this.stateObject.splice(index + 1, lastStateIndex - index);
      const selectedStateIndex = this.stateObject.length - 1;
      if (params) {
        this.stateObject[selectedStateIndex].params = params;
      }
      this.gotoState(this.stateObject[selectedStateIndex].id, true);
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
   * @param id id (string)
   * @param state state (DashboardState)
   * @returns string observable or value
   */

  public getStateName(id: string, state: DashboardState): string {
    const name = this.utils.customTranslation(state.name, id);
    return name === this.stateControllerId() ? name.charAt(0).toUpperCase() + name.slice(1) : name;
  }

  /**
   * get current state name.
   *
   * @returns string observable or value
   */

  public getCurrentStateName(): string {
    return this.getStateName(this.stateObject[0].id, this.statesValue[this.stateObject[0].id]);
  }

  /**
   * display state selection.
   *
   * @returns boolean observable or value
   */

  public displayStateSelection(): boolean {
    return this.states && Object.keys(this.states).length > 1;
  }

  /**
   * selected state id changed.
   *
   */

  public selectedStateIdChanged() {
    this.gotoState(this.stateObject[0].id, true);
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
    } else if (result.length > 1) {
      const newResult = [];
      newResult.push(result[result.length - 1]);
      result = newResult;
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
    if (this.dashboardCtrl.dashboardCtx.state !== stateId) {
      this.dashboardCtrl.openDashboardState(stateId, openRightLayout);
      this.stateIdSubject.next(stateId);
      if (this.syncStateWithQueryParam && stateId && this.statesValue[stateId]) {
        this.mobileService.handleDashboardStateName(this.getStateName(stateId, this.statesValue[stateId]));
      }
      if (update) {
        this.updateLocation();
      }
    }
  }

  /**
   * update location.
   *
   */

  private updateLocation() {
    if (this.stateObject[0].id) {
      const newState = objToBase64(this.stateObject);
      this.updateStateParam(newState);
    }
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
