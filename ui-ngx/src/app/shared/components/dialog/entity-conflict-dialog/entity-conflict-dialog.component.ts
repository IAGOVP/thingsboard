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

import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SharedModule } from '@shared/shared.module';
import { ImportExportService } from '@shared/import-export/import-export.service';
import { entityTypeTranslations } from '@shared/models/entity-type.models';
import { EntityInfoData, VersionedEntity } from '@shared/models/entity.models';
import { EntityId } from '@shared/models/id/entity-id';
import { RuleChainMetaData } from '@shared/models/rule-chain.models';

interface EntityConflictDialogData {
  message: string;
  entity: VersionedEntity;
}


/**
 * Angular component: entity conflict dialog UI.
 */
@Component({
    selector: 'tb-entity-conflict-dialog',
    templateUrl: 'entity-conflict-dialog.component.html',
    styleUrls: ['./entity-conflict-dialog.component.scss'],
    imports: [
    SharedModule
/**
 * Angular component: entity conflict dialog (shared UI components).
 *
 * <p>Template UI for the ThingsBoard web application. Selector: `tb-entity-conflict-dialog`.
 */
]})
export class EntityConflictDialogComponent {

  entityId: EntityId;
  entityTypeLabel: string;

  readonly entityTypeTranslations = entityTypeTranslations;
  private readonly defaultEntityLabel = 'entity.entity';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: EntityConflictDialogData,
    private dialogRef: MatDialogRef<EntityConflictDialogComponent>,
    private importExportService: ImportExportService,
  ) {
    this.entityId = (data.entity as EntityInfoData).id ?? (data.entity as RuleChainMetaData).ruleChainId;
    this.entityTypeLabel = entityTypeTranslations.has(this.entityId.entityType)
      ? (entityTypeTranslations.get(this.entityId.entityType).type)
      : this.defaultEntityLabel;
  }

  /**
   * Event handler for cancel.
   *
   */

  onCancel(): void {
    this.dialogRef.close();
  }

  /**
   * Event handler for discard.
   *
   */

  onDiscard(): void {
    this.dialogRef.close(false);
  }

  /**
   * Event handler for confirm.
   *
   */

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  /**
   * Event handler for link click.
   *
   * @param event DOM or Angular event object
   */

  onLinkClick(event: MouseEvent): void {
    event.preventDefault();
    this.importExportService.exportEntity(this.data.entity);
  }
}
