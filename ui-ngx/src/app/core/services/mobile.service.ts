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

import { Inject, Injectable } from '@angular/core';
import { WINDOW } from '@core/services/window.service';
import { isDefined } from '@core/utils';
import { MobileActionResult, WidgetMobileActionResult, WidgetMobileActionType } from '@shared/models/widget.models';
import { from, of } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';
import { catchError, tap } from 'rxjs/operators';
import { OpenDashboardMessage, ReloadUserMessage, WindowMessage } from '@shared/models/window-message.model';
import { Params, Router } from '@angular/router';
import { AuthService } from '@core/auth/auth.service';

const dashboardStateNameHandler = 'tbMobileDashboardStateNameHandler';
const dashboardLoadedHandler = 'tbMobileDashboardLoadedHandler';
const dashboardLayoutHandler = 'tbMobileDashboardLayoutHandler';
const navigationHandler = 'tbMobileNavigationHandler';
const mobileHandler = 'tbMobileHandler';
const mobileReadyHandler = 'tbMobileReadyHandler';

// @dynamic
/**
 * Angular injectable service: mobile (ThingsBoard web UI).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class MobileService {

  private readonly mobileApp;
  private readonly mobileChannel;

  private readonly onWindowMessageListener = this.onWindowMessage.bind(this);

  private reloadUserObservable: Observable<boolean>;
  private lastDashboardId: string;
  private toggleLayoutFunction: () => void;

  constructor(@Inject(WINDOW) private window: Window,
              private router: Router,
              private authService: AuthService) {
    const w = (this.window as any);
    this.mobileChannel = w.flutter_inappwebview;
    this.mobileApp = isDefined(this.mobileChannel);
    if (this.mobileApp) {
      window.addEventListener('message', this.onWindowMessageListener);
      this.mobileChannel.callHandler(mobileReadyHandler);
    }
  }

  /**
   * is mobile app.
   *
   * @returns boolean observable or value
   */

  public isMobileApp(): boolean {
    return this.mobileApp;
  }

  /**
   * handle dashboard state name.
   *
   * @param name name (string)
   */

  public handleDashboardStateName(name: string) {
    if (this.mobileApp) {
      this.mobileChannel.callHandler(dashboardStateNameHandler, name);
    }
  }

  /**
   * Event handler for dashboard loaded.
   *
   * @param hasRightLayout has right layout (boolean)
   * @param rightLayoutOpened right layout opened (boolean)
   */

  public onDashboardLoaded(hasRightLayout: boolean, rightLayoutOpened: boolean) {
    if (this.mobileApp) {
      this.mobileChannel.callHandler(dashboardLoadedHandler, hasRightLayout, rightLayoutOpened);
    }
  }

  /**
   * Event handler for dashboard right layout changed.
   *
   * @param opened opened (boolean)
   */

  public onDashboardRightLayoutChanged(opened: boolean) {
    if (this.mobileApp) {
      this.mobileChannel.callHandler(dashboardLayoutHandler, opened);
    }
  }

  public registerToggleLayoutFunction(toggleLayoutFunction: () => void) {
    this.toggleLayoutFunction = toggleLayoutFunction;
  }

  /**
   * unregister toggle layout function.
   *
   */

  public unregisterToggleLayoutFunction() {
    this.toggleLayoutFunction = null;
  }

  public handleWidgetMobileAction<T extends MobileActionResult>(type: WidgetMobileActionType, ...args: any[]):
    Observable<WidgetMobileActionResult<T>> {
    if (this.mobileApp) {
      return from(
        this.mobileChannel.callHandler(mobileHandler, type, ...args) as Promise<WidgetMobileActionResult<T>>).pipe(
        catchError((err: Error) => {
          return of({
            hasError: true,
            error: err?.message ? err.message : `Failed to execute mobile action ${type}`
          } as WidgetMobileActionResult<any>);
        })
      );
    } else {
      return of(null);
    }
  }

  /**
   * handle mobile navigation.
   *
   * @param path path (string)
   * @param params params (Params)
   * @param queryParams query params (Params)
   */

  public handleMobileNavigation(path?: string, params?: Params, queryParams?: Params) {
    if (this.mobileApp) {
      this.mobileChannel.callHandler(navigationHandler, path, params, queryParams);
    }
  }

  /**
   * Event handler for window message.
   *
   * @param event DOM or Angular event object
   */

  private onWindowMessage(event: MessageEvent) {
    if (event.data) {
      let message: WindowMessage;
      try {
        message = JSON.parse(event.data);
      } catch (e) {}
      if (message && message.type) {
        switch (message.type) {
          case 'openDashboardMessage':
            const openDashboardMessage: OpenDashboardMessage = message.data;
            this.openDashboard(openDashboardMessage);
            break;
          case 'reloadUserMessage':
            const reloadUserMessage: ReloadUserMessage = message.data;
            this.reloadUser(reloadUserMessage);
            break;
          case 'toggleDashboardLayout':
            if (this.toggleLayoutFunction) {
              this.toggleLayoutFunction();
            }
            break;
        }
      }
    }
  }

  /**
   * open dashboard.
   *
   * @param openDashboardMessage open dashboard message (OpenDashboardMessage)
   */

  private openDashboard(openDashboardMessage: OpenDashboardMessage) {
    if (openDashboardMessage && openDashboardMessage.dashboardId) {
      if (this.reloadUserObservable) {
        this.reloadUserObservable.subscribe(
          (authenticated) => {
            if (authenticated) {
              this.doDashboardNavigation(openDashboardMessage);
            }
          }
        );
      } else {
        this.doDashboardNavigation(openDashboardMessage);
      }
    }
  }

  /**
   * do dashboard navigation.
   *
   * @param openDashboardMessage open dashboard message (OpenDashboardMessage)
   */

  private doDashboardNavigation(openDashboardMessage: OpenDashboardMessage) {
    let url = `/dashboard/${openDashboardMessage.dashboardId}`;
    const params = [];
    if (openDashboardMessage.state) {
      params.push(`state=${openDashboardMessage.state}`);
    }
    if (openDashboardMessage.embedded) {
      params.push(`embedded=true`);
    }
    if (openDashboardMessage.hideToolbar) {
      params.push(`hideToolbar=true`);
    }
    if (this.lastDashboardId === openDashboardMessage.dashboardId) {
      params.push(`reload=${new Date().getTime()}`);
    }
    if (params.length) {
      url += `?${params.join('&')}`;
    }
    this.lastDashboardId = openDashboardMessage.dashboardId;
    this.router.navigateByUrl(url, {replaceUrl: true});
  }

  /**
   * reload user.
   *
   * @param reloadUserMessage reload user message (ReloadUserMessage)
   */

  private reloadUser(reloadUserMessage: ReloadUserMessage) {
    if (reloadUserMessage && reloadUserMessage.accessToken && reloadUserMessage.refreshToken) {
      this.reloadUserObservable = this.authService.setUserFromJwtToken(reloadUserMessage.accessToken,
        reloadUserMessage.refreshToken, true).pipe(
        tap(
          () => {
            this.reloadUserObservable = null;
          }
        )
      );
    }
  }

}
