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


/**

 * Page object helper for dashboard page UI actions (page object element locators and helpers — Selenium page objects).

 */


public class DashboardPageHelper extends DashboardPageElements {
    public DashboardPageHelper(WebDriver driver) {
        super(driver);
    }

    private String dashboardTitle;
    /**
     * Set dashboard title.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setDashboardTitle() {
        this.dashboardTitle = entityTitles().get(0).getText();
    }
    /**
     * Returns dashboard title.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getDashboardTitle() {
        return dashboardTitle;
    }
    /**
     * Assigns ed customer.
     *
     * @param title title ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void assignedCustomer(String title) {
        jsClick(manageAssignedEntityListField());
        jsClick(manageAssignedEntity(title));
        jsClick(manageAssignedUpdateBtn());
    }
    /**
     * Open select widgets bundle menu.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openSelectWidgetsBundleMenu() {
        addBtn().click();
    }
    /**
     * Open create widget popup.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openCreateWidgetPopup() {
        alarmWidgetBundle().click();
        alarmTableWidget().click();
    }
    /**
     * Increase size of the widget.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void increaseSizeOfTheWidget() {
        pull(widgetSECorner(), 700, 200);
    }
}
