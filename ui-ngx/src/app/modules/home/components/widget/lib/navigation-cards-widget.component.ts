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

import { PageComponent } from '@shared/components/page.component';
import { Component, Input, NgZone, OnInit } from '@angular/core';
import { WidgetContext } from '@home/models/widget-component.models';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { MenuService } from '@core/services/menu.service';
import { HomeSection, MenuSection } from '@core/services/menu.models';
import { Router } from '@angular/router';
import { map } from 'rxjs/operators';

interface NavigationCardsWidgetSettings {
  filterType: 'all' | 'include' | 'exclude';
  filter: string[];
}


/**
 * Angular component: navigation cards widget (ThingsBoard web UI).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-navigation-cards-widget`.
 */
@Component({
    selector: 'tb-navigation-cards-widget',
    templateUrl: './navigation-cards-widget.component.html',
    styleUrls: ['./navigation-cards-widget.component.scss'],
standalone: false
})
export class NavigationCardsWidgetComponent extends PageComponent implements OnInit {

  homeSections$ = this.menuService.homeSections();
  showHomeSections$ = this.homeSections$.pipe(
    map((sections) => sections.filter((section) => this.sectionPlaces(section).length > 0))
  );

  cols = null;

  settings: NavigationCardsWidgetSettings;

  @Input()
  ctx: WidgetContext;

  constructor(protected store: Store<AppState>,
              private menuService: MenuService,
              private ngZone: NgZone,
              private router: Router) {
    super(store);
  }

  /**
   * Angular lifecycle hook: initialize component state and subscriptions.
   *
   */

  ngOnInit(): void {
    this.ctx.$scope.navigationCardsWidget = this;
    this.settings = this.ctx.settings;
  }

  /**
   * resize.
   *
   */

  resize() {
    this.updateColumnCount();
  }

  /**
   * update column count.
   *
   */

  private updateColumnCount() {
    this.cols = 2;
    const width = this.ctx.width;
    if (width >= 1280) {
      this.cols = 3;
      if (width >= 1920) {
        this.cols = 4;
      }
    }
    this.ctx.detectChanges();
  }

  /**
   * navigate.
   *
   * @param path path (string)
   */

  navigate($event: Event, path: string) {
    $event.preventDefault();
    this.ngZone.run(() => {
      this.router.navigateByUrl(path);
    });
  }

  /**
   * section places.
   *
   * @param section section (HomeSection)
   * @returns MenuSection[] observable or value
   */

  sectionPlaces(section: HomeSection): MenuSection[] {
    return section && section.places ? section.places.filter((place) => this.filterPlace(place)) : [];
  }

  /**
   * filter place.
   *
   * @param place place (MenuSection)
   * @returns boolean observable or value
   */

  private filterPlace(place: MenuSection): boolean {
    if (this.settings.filterType === 'include') {
      return this.settings.filter.includes(place.path);
    } else if (this.settings.filterType === 'exclude') {
      return !this.settings.filter.includes(place.path);
    }
    return true;
  }

  /**
   * section colspan.
   *
   * @param section section (HomeSection)
   * @returns number observable or value
   */

  sectionColspan(section: HomeSection): number {
    if (this.ctx.width >= 960) {
      let colspan = this.cols;
      const places = this.sectionPlaces(section);
      if (places.length <= colspan) {
        colspan = places.length;
      }
      return colspan;
    } else {
      return 2;
    }
  }

}
