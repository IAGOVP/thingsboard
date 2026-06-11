/**
 * Copyright © 2016-2026 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.service.entitiy.widgets.bundle;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.service.entitiy.SimpleTbEntityService;

import java.util.List;

/**

 * Application-layer service API for widgets bundle entity operations.

 *

 * <p>Wraps DAO services with audit logging, validation, and optional version-control auto-commit.

 */

public interface TbWidgetsBundleService extends SimpleTbEntityService<WidgetsBundle> {
/**
 * Updates widgets bundle widget types.
 *
 * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
 * @param widgetTypeIds widget type ids ({@link List})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */



    void updateWidgetsBundleWidgetTypes(WidgetsBundleId widgetsBundleId, List<WidgetTypeId> widgetTypeIds, User user) throws Exception;
/**
 * Updates widgets bundle widget fqns.
 *
 * @param widgetsBundleId widgets bundle id ({@link WidgetsBundleId})
 * @param widgetFqns widget fqns ({@link List})
 * @param user authenticated user performing the action
 * @return nothing
 * @throws Exception if an unexpected error occurs during processing
 */

    void updateWidgetsBundleWidgetFqns(WidgetsBundleId widgetsBundleId, List<String> widgetFqns, User user) throws Exception;


}
