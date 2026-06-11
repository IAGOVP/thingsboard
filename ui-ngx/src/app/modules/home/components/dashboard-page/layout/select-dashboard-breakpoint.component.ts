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

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { DashboardPageComponent } from '@home/components/dashboard-page/dashboard-page.component';
import { Subscription } from 'rxjs';
import { BreakpointId } from '@shared/models/dashboard.models';
import { DashboardUtilsService } from '@core/services/dashboard-utils.service';


/**
 * Angular component: select dashboard breakpoint (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-select-dashboard-breakpoint`.
 */
@Component({
    selector: 'tb-select-dashboard-breakpoint',
    templateUrl: './select-dashboard-breakpoint.component.html',
    styleUrls: ['./select-dashboard-breakpoint.component.scss'],
standalone: false
})
export class SelectDashboardBreakpointComponent implements OnInit, OnDestroy {

  @Input()
  dashboardCtrl: DashboardPageComponent;

  selectedBreakpoint: BreakpointId = 'default';

  breakpointIds: Array<BreakpointId> = ['default'];

  private layoutDataChanged$: Subscription;

  constructor(private dashboardUtils: DashboardUtilsService) {
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit() {
    this.layoutDataChanged$ = this.dashboardCtrl.layouts.main.layoutCtx.layoutDataChanged.subscribe(() => {
      if (this.dashboardCtrl.layouts.main.layoutCtx.layoutData) {
        this.breakpointIds = Object.keys(this.dashboardCtrl.layouts.main.layoutCtx?.layoutData) as BreakpointId[];
        this.breakpointIds.sort((a, b) => {
          const aMaxWidth = this.dashboardUtils.getBreakpointInfoById(a)?.maxWidth || Infinity;
          const bMaxWidth = this.dashboardUtils.getBreakpointInfoById(b)?.maxWidth || Infinity;
          return bMaxWidth - aMaxWidth;
        });
        if (this.breakpointIds.indexOf(this.dashboardCtrl.layouts.main.layoutCtx.breakpoint) > -1) {
          this.selectedBreakpoint = this.dashboardCtrl.layouts.main.layoutCtx.breakpoint;
        } else {
          this.selectedBreakpoint = 'default';
          this.dashboardCtrl.layouts.main.layoutCtx.breakpoint = this.selectedBreakpoint;
        }
      }
    });
  }

  /**
   * Angular lifecycle hook: unsubscribe and release resources.
   *
   */

  ngOnDestroy() {
    this.layoutDataChanged$.unsubscribe();
  }

  /**
   * select layout changed.
   *
   */

  selectLayoutChanged() {
    this.dashboardUtils.updatedLayoutForBreakpoint(this.dashboardCtrl.layouts.main, this.selectedBreakpoint);
    this.dashboardCtrl.updateLayoutSizes();
  }

  /**
   * get name.
   *
   * @param breakpointId breakpoint id (BreakpointId)
   * @returns string observable or value
   */

  getName(breakpointId: BreakpointId): string {
    return this.dashboardUtils.getBreakpointName(breakpointId);
  }

  /**
   * get icon.
   *
   * @param breakpointId breakpoint id (BreakpointId)
   * @returns string observable or value
   */

  getIcon(breakpointId: BreakpointId): string {
    return this.dashboardUtils.getBreakpointIcon(breakpointId);
  }

  /**
   * get size description.
   *
   * @param breakpointId breakpoint id (BreakpointId)
   * @returns string observable or value
   */

  getSizeDescription(breakpointId: BreakpointId): string {
    return this.dashboardUtils.getBreakpointSizeDescription(breakpointId);
  }
}
