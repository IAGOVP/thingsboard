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

import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { interval } from 'rxjs';
import { filter } from 'rxjs/operators';
import { HistorySelectSettings } from '@app/modules/home/components/widget/lib/maps-legacy/map-models';


/**
 * Angular component: history selector (shared UI components).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-history-selector`.
 */
@Component({
    selector: 'tb-history-selector',
    templateUrl: './history-selector.component.html',
    styleUrls: ['./history-selector.component.scss'],
standalone: false
})
export class HistorySelectorComponent implements OnChanges {

  @Input() settings: HistorySelectSettings;
  @Input() minTime: number;
  @Input() maxTime: number;
  @Input() step = 1000;
  @Input() anchors = [];
  @Input() useAnchors = false;

  @Output() timeUpdated: EventEmitter<number> = new EventEmitter();

  minTimeIndex = 0;
  maxTimeIndex = 0;
  speed = 1;
  index = 0;
  playing = false;
  interval;
  speeds = [1, 5, 10, 25];
  currentTime = null;


  constructor(private cd: ChangeDetectorRef) { }

  ngOnChanges() {
    this.maxTimeIndex =  Math.ceil((this.maxTime - this.minTime) / this.step);
    this.currentTime = this.minTime === Infinity ? null : this.minTime;
  }

  /**
   * play.
   *
   */

  play() {
    this.playing = true;
    if (!this.interval) {
      this.interval = interval(1000 / this.speed)
        .pipe(
          filter(() => this.playing)
        ).subscribe(() => {
          this.index++;
          this.currentTime = this.minTime + this.index * this.step;
          if (this.index <= this.maxTimeIndex) {
            this.cd.detectChanges();
            this.timeUpdated.emit(this.currentTime);
          } else {
            this.playing = false;
            this.interval.complete();
            this.cd.detectChanges();
          }
        }, err => {
          console.error(err);
        }, () => {
          this.interval = null;
        });
    }
  }

  /**
   * re init.
   *
   */

  reInit() {
    if (this.interval) {
      this.interval.complete();
    }
    if (this.playing) {
      this.play();
    }
  }

  /**
   * pause.
   *
   */

  pause() {
    this.playing = false;
    this.currentTime = this.minTime + this.index * this.step;
    this.cd.detectChanges();
    this.timeUpdated.emit(this.currentTime);
  }

  /**
   * move next.
   *
   */

  moveNext() {
    if (this.index < this.maxTimeIndex) {
      if (this.useAnchors) {
        const anchorIndex = this.findIndex(this.currentTime, this.anchors) + 1;
        this.index = Math.floor((this.anchors[anchorIndex] - this.minTime) / this.step);
      } else {
        this.index++;
      }
    }
    this.pause();
  }

  /**
   * move prev.
   *
   */

  movePrev() {
    if (this.index > this.minTimeIndex) {
      if (this.useAnchors) {
        const anchorIndex = this.findIndex(this.currentTime, this.anchors) - 1;
        this.index = Math.floor((this.anchors[anchorIndex] - this.minTime) / this.step);
      } else {
        this.index--;
      }
    }
    this.pause();
  }

  /**
   * find index.
   *
   * @param value value (number)
   * @param array array (number[])
   * @returns number observable or value
   */

  findIndex(value: number, array: number[]): number {
    let i = 0;
    while (array[i] < value) {
      i++;
    }
    return i;
  }

  /**
   * move start.
   *
   */

  moveStart() {
    this.index = this.minTimeIndex;
    this.pause();
  }

  /**
   * move end.
   *
   */

  moveEnd() {
    this.index = this.maxTimeIndex;
    this.pause();
  }

  /**
   * change index.
   *
   * @param index index (number)
   */

  changeIndex(index: number) {
    this.index = index;
    this.currentTime = this.minTime + index * this.step;
    this.timeUpdated.emit(this.currentTime);
  }
}
