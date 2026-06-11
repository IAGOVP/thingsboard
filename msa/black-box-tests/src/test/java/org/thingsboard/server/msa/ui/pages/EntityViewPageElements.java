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
package org.thingsboard.server.msa.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**

 * Selenium element locators for entity view page page (page object element locators and helpers — Selenium page objects).

 *

 * <p>Defines CSS/XPath selectors; use with matching *Helper for interactions.

 */


public class EntityViewPageElements extends OtherPageElementsHelper {
    public EntityViewPageElements(WebDriver driver) {
        super(driver);
    }

    private static final String ENTITY_VIEW_DETAILS_VIEW = "//tb-details-panel";
    private static final String ENTITY_VIEW_DETAILS_ALARMS = ENTITY_VIEW_DETAILS_VIEW + "//span[text()='Alarms']";
    /**
     * Entity view details view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entityViewDetailsView() {
        return waitUntilPresenceOfElementLocated(ENTITY_VIEW_DETAILS_VIEW);
    }
    /**
     * Entity view details alarms btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entityViewDetailsAlarmsBtn() {
        return waitUntilElementToBeClickable(ENTITY_VIEW_DETAILS_ALARMS);
    }

}
