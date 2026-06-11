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
  CirclesDataLayerSettings,
  defaultBaseCirclesDataLayerSettings,
  isJSON, MapDataLayerType,
  TbCircleData,
  TbMapDatasource
} from '@shared/models/widget/maps/map.models';
import L from 'leaflet';
import { DataKey, FormattedData } from '@shared/models/widget.models';
import { ShapeStyleInfo, TbShapesDataLayer } from '@home/components/widget/lib/maps/data-layer/shapes-data-layer';
import { TbMap } from '@home/components/widget/lib/maps/map';
import { Observable } from 'rxjs';
import { isNotEmptyStr } from '@core/utils';
import {
  TbLatestDataLayerItem,
  UnplacedMapDataItem
} from '@home/components/widget/lib/maps/data-layer/latest-map-data-layer';
import { map } from 'rxjs/operators';


/**
 * Tb circle data layer item (ThingsBoard web UI).
 */


class TbCircleDataLayerItem extends TbLatestDataLayerItem<CirclesDataLayerSettings, TbCirclesDataLayer> {

  private circle: L.Circle;
  private circleStyleInfo: ShapeStyleInfo;
  private editing = false;

  constructor(data: FormattedData<TbMapDatasource>,
              dsData: FormattedData<TbMapDatasource>[],
              protected settings: CirclesDataLayerSettings,
              protected dataLayer: TbCirclesDataLayer) {
    super(data, dsData, settings, dataLayer);
  }

  /**
   * is editing.
   *
   */

  public isEditing() {
    return this.editing;
  }

  /**
   * update bubbling mouse events.
   *
   */

  public updateBubblingMouseEvents() {
    this.circle.options.bubblingMouseEvents =  !this.dataLayer.isEditMode();
  }

  /**
   * DELETE — remove.
   *
   */

  public remove() {
    super.remove();
    if (this.circleStyleInfo?.patternId) {
      this.dataLayer.getMap().unUseShapePattern(this.circleStyleInfo.patternId);
    }
  }

  /**
   * POST/PUT entity — create.
   *
   * @param data dialog or route input data
   * @param dsData ds data (FormattedData<TbMapDatasource>[])
   * @returns L.Layer observable or value
   */

  protected create(data: FormattedData<TbMapDatasource>, dsData: FormattedData<TbMapDatasource>[]): L.Layer {
    const circleData = this.dataLayer.extractCircleCoordinates(data);
    const center = new L.LatLng(circleData.latitude, circleData.longitude);
    this.circle = L.circle(center, {
      bubblingMouseEvents: !this.dataLayer.isEditMode(),
      radius: circleData.radius,
      snapIgnore: !this.dataLayer.isSnappable()
    });

    this.dataLayer.getShapeStyle(data, dsData, this.circleStyleInfo?.patternId).subscribe((styleInfo) => {
      this.circleStyleInfo = styleInfo;
      if (this.circle) {
        this.circle.setStyle(this.circleStyleInfo.style);
      }
    });

    this.updateLabel(data, dsData);
    return this.circle;
  }

  /**
   * unbind label.
   *
   */

  protected unbindLabel() {
    this.circle.unbindTooltip();
  }

  /**
   * bind label.
   *
   * @param content content (L.Content)
   */

  protected bindLabel(content: L.Content): void {
    this.circle.bindTooltip(content, { className: 'tb-circle-label', permanent: true, direction: 'center'})
    .openTooltip(this.circle.getLatLng());
  }

  /**
   * do update.
   *
   * @param data dialog or route input data
   * @param dsData ds data (FormattedData<TbMapDatasource>[])
   */

  protected doUpdate(data: FormattedData<TbMapDatasource>, dsData: FormattedData<TbMapDatasource>[]): void {
    this.dataLayer.getShapeStyle(data, dsData, this.circleStyleInfo?.patternId).subscribe((styleInfo) => {
      this.circleStyleInfo = styleInfo;
      this.updateCircleShape(data);
      this.updateTooltip(data, dsData);
      this.updateLabel(data, dsData);
      this.circle.setStyle(this.circleStyleInfo.style);
    });
  }

  /**
   * do invalidate coordinates.
   *
   * @param data dialog or route input data
   * @param dsData ds data (FormattedData<TbMapDatasource>[])
   */

  protected doInvalidateCoordinates(data: FormattedData<TbMapDatasource>, dsData: FormattedData<TbMapDatasource>[]): void {
    this.updateCircleShape(data);
    this.updateLabel(data, dsData);
  }

  /**
   * POST/PUT entity — add item class.
   *
   * @param clazz clazz (string)
   */

  protected addItemClass(clazz: string): void {
    if ((this.circle as any)._path) {
      L.DomUtil.addClass((this.circle as any)._path, clazz);
    }
  }

  /**
   * DELETE — remove item class.
   *
   * @param clazz clazz (string)
   */

  protected removeItemClass(clazz: string): void {
    if ((this.circle as any)._path) {
      L.DomUtil.removeClass((this.circle as any)._path, clazz);
    }
  }

  /**
   * enable drag.
   *
   */

  protected enableDrag(): void {
    this.circle.pm.setOptions({
      snappable: this.dataLayer.isSnappable()
    });
    this.circle.pm.enableLayerDrag();
    this.circle.on('pm:dragstart', () => {
      this.editing = true;
    });
    this.circle.on('pm:dragend', () => {
      this.saveCircleCoordinates();
      this.editing = false;
    });
  }

  /**
   * disable drag.
   *
   */

  protected disableDrag(): void {
    this.circle.pm.disableLayerDrag();
    this.circle.off('pm:dragstart');
    this.circle.off('pm:dragend');
  }

  /**
   * Event handler for selected.
   *
   * @returns L.TB.ToolbarButtonOptions[] observable or value
   */

  protected onSelected(): L.TB.ToolbarButtonOptions[] {
    if (this.dataLayer.isEditEnabled()) {
      this.circle.on('pm:markerdragstart', () => this.editing = true);
      this.circle.on('pm:markerdragend', () => this.editing = false);
      this.circle.on('pm:edit', () => this.saveCircleCoordinates());
      this.circle.pm.enable({draggable: true, snappable: this.dataLayer.isSnappable()});
    }
    return [];
  }

  /**
   * Event handler for deselected.
   *
   */

  protected onDeselected(): void {
    if (this.dataLayer.isEditEnabled()) {
      this.circle.pm.disable();
      this.circle.off('pm:markerdragstart');
      this.circle.off('pm:markerdragend');
      this.circle.off('pm:edit');
    }
  }

  /**
   * DELETE — remove data item title.
   *
   * @returns string observable or value
   */

  protected removeDataItemTitle(): string {
    return this.dataLayer.getCtx().translate.instant('widgets.maps.data-layer.circle.remove-circle-for', {entityName: this.data.entityName});
  }

  /**
   * DELETE — remove data item.
   *
   * @returns Observable<any> observable or value
   */

  protected removeDataItem(): Observable<any> {
    return this.dataLayer.saveCircleCoordinates(this.data, null, null);
  }

  /**
   * POST/PUT entity — save circle coordinates.
   *
   */

  private saveCircleCoordinates() {
    const center = this.circle.getLatLng();
    const radius = this.circle.getRadius();
    this.dataLayer.saveCircleCoordinates(this.data, center, radius).subscribe();
  }

  /**
   * update circle shape.
   *
   * @param data dialog or route input data
   */

  private updateCircleShape(data: FormattedData<TbMapDatasource>) {
    if (this.editing) {
      return;
    }
    const circleData = this.dataLayer.extractCircleCoordinates(data);
    const center = new L.LatLng(circleData.latitude, circleData.longitude);
    if (!this.circle.getLatLng().equals(center)) {
      this.circle.setLatLng(center);
    }
    if (this.circle.getRadius() !== circleData.radius) {
      this.circle.setRadius(circleData.radius);
    }
  }
}

export class TbCirclesDataLayer extends TbShapesDataLayer<CirclesDataLayerSettings, TbCirclesDataLayer> {

  constructor(protected map: TbMap<any>,
              inputSettings: CirclesDataLayerSettings) {
    super(map, inputSettings);
  }

  public dataLayerType(): MapDataLayerType {
    return 'circles';
  }

  public placeItem(item: UnplacedMapDataItem, layer: L.Layer): void {
    if (layer instanceof L.Circle) {
      const center = layer.getLatLng();
      const radius = layer.getRadius();
      this.saveCircleCoordinates(item.entity, center, radius).subscribe(
        (converted) => {
          item.entity[this.settings.circleKey.label] = JSON.stringify(converted);
          this.createItemFromUnplaced(item);
        }
      );
    } else {
      console.warn('Unable to place item, layer is not a circle.');
    }
  }

  public extractCircleCoordinates(data: FormattedData<TbMapDatasource>) {
    const circleData: TbCircleData = JSON.parse(data[this.settings.circleKey.label]);
    return this.map.circleDataToCoordinates(circleData);
  }

  public saveCircleCoordinates(data: FormattedData<TbMapDatasource>, center: L.LatLng, radius: number): Observable<TbCircleData> {
    const converted = center ? this.map.coordinatesToCircleData(center, radius) : null;
    const circleData = [
      {
        dataKey: this.settings.circleKey,
        value: converted
      }
    ];
    return this.map.saveItemData(data.$datasource, circleData, this.settings.edit?.attributeScope).pipe(
      map(() => converted)
    );
  }

  protected getDataKeys(): DataKey[] {
    return [this.settings.circleKey];
  }

  protected defaultBaseSettings(map: TbMap<any>): Partial<CirclesDataLayerSettings> {
    return defaultBaseCirclesDataLayerSettings(map.type());
  }

  protected doSetup(): Observable<void> {
    return super.doSetup();
  }

  protected isValidLayerData(layerData: FormattedData<TbMapDatasource>): boolean {
    return layerData && isNotEmptyStr(layerData[this.settings.circleKey.label]) && isJSON(layerData[this.settings.circleKey.label]);
  }

  protected createLayerItem(data: FormattedData<TbMapDatasource>, dsData: FormattedData<TbMapDatasource>[]): TbLatestDataLayerItem<CirclesDataLayerSettings, TbCirclesDataLayer> {
    return new TbCircleDataLayerItem(data, dsData, this.settings, this);
  }

}
