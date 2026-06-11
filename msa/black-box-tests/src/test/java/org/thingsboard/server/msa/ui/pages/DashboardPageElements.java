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

import java.util.List;


/**

 * Selenium element locators for dashboard page page (page object element locators and helpers — Selenium page objects).

 *

 * <p>Defines CSS/XPath selectors; use with matching *Helper for interactions.

 */


public class DashboardPageElements extends OtherPageElementsHelper {
    public DashboardPageElements(WebDriver driver) {
        super(driver);
    }

    private static final String TITLES = "//mat-cell[contains(@class,'cdk-column-title')]/span";
    private static final String ASSIGNED_BTN = ENTITY + "/../..//mat-icon[contains(text(),' assignment_ind')]/../..";
    private static final String MANAGE_ASSIGNED_ENTITY_LIST_FIELD = "//input[@formcontrolname='entity']";
    private static final String MANAGE_ASSIGNED_ENTITY = "//mat-option//span[contains(text(),'%s')]";
    private static final String MANAGE_ASSIGNED_UPDATE_BTN = "//button[@type='submit']";
    private static final String EDIT_BTN = "//mat-icon[text() = 'edit']/parent::button[@mat-stroked-button]";
    private static final String ADD_BTN = "//mat-fab-actions//mat-icon[text() = 'add']/parent::button";
    private static final String ALARM_WIDGET_BUNDLE = "//mat-card-title[text() = 'Alarm widgets']/ancestor::mat-card";
    private static final String ALARM_TABLE_WIDGET = "//img[@alt='Alarms table']/ancestor::mat-card";
    private static final String WIDGET_SE_CORNER = "//div[contains(@class,'handle-se')]";
    private static final String SAVE_BTN = "//mat-icon[text() = 'done']/parent::button[@fxhide.lt-lg]";
    /**
     * Entity titles.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public List<WebElement> entityTitles() {
        return waitUntilVisibilityOfElementsLocated(TITLES);
    }
    /**
     * Assigns ed btn.
     *
     * @param title title ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignedBtn(String title) {
        return waitUntilElementToBeClickable(String.format(ASSIGNED_BTN, title));
    }
    /**
     * Manage assigned entity list field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageAssignedEntityListField() {
        return waitUntilElementToBeClickable(MANAGE_ASSIGNED_ENTITY_LIST_FIELD);
    }
    /**
     * Manage assigned entity.
     *
     * @param title title ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageAssignedEntity(String title) {
        return waitUntilElementToBeClickable(String.format(MANAGE_ASSIGNED_ENTITY, title));
    }
    /**
     * Manage assigned update btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageAssignedUpdateBtn() {
        return waitUntilElementToBeClickable(MANAGE_ASSIGNED_UPDATE_BTN);
    }
    /**
     * Edit btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement editBtn() {
        return waitUntilElementToBeClickable(EDIT_BTN);
    }
    /**
     * Add btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement addBtn() {
        return waitUntilElementToBeClickable(ADD_BTN);
    }
    /**
     * Alarm widget bundle.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement alarmWidgetBundle() {
        return waitUntilElementToBeClickable(ALARM_WIDGET_BUNDLE);
    }
    /**
     * Alarm table widget.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement alarmTableWidget() {
        return waitUntilElementToBeClickable(ALARM_TABLE_WIDGET);
    }
    /**
     * Widget secorner.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement widgetSECorner() {
        return waitUntilElementToBeClickable(WIDGET_SE_CORNER);
    }
    /**
     * Saves or persists btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement saveBtn() {
        return waitUntilVisibilityOfElementLocated(SAVE_BTN);
    }
}
