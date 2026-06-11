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

 * Selenium element locators for alarm details entity tab page (page object element locators and helpers — Selenium page objects).

 *

 * <p>Defines CSS/XPath selectors; use with matching *Helper for interactions.

 */


public class AlarmDetailsEntityTabElements extends OtherPageElements {
    public AlarmDetailsEntityTabElements(WebDriver driver) {
        super(driver);
    }

    private static final String ASSIGN_BTN = "//span[text() = '%s']/ancestor::mat-row//mat-icon[contains(text(),'keyboard_arrow_down')]/parent::button";
    private static final String USER_ASSIGN_DROPDOWN = "//div[@class='user-display-name']/span[text() = '%s']";
    protected static final String ASSIGN_USERS_DISPLAY_NAME = "//div[@class='user-display-name']/span";
    private static final String ASSIGN_USER_DISPLAY_NAME = "//span[@class='user-display-name'][contains(text(),'%s')]";
    private static final String SEARCH_FIELD = "//input[@placeholder='Search users']";
    private static final String UNASSIGNED_BTN = "//div[@role='listbox']//mat-icon[text() = 'account_circle']/following-sibling::span";
    private static final String UNASSIGNED = "//span[text() = '%s']/ancestor::mat-row//span[@class='assignee-cell']//mat-icon[text() = 'account_circle']/following-sibling::span";
    private static final String ALARM_DETAILS_BTN = "//span[text() = '%s']/ancestor::mat-row//mat-icon[contains(text(),'more_horiz')]/parent::button";
    private static final String ACCESS_FORBIDDEN_DIALOG_VIEW = "//h2[text() = 'Access Forbidden']/parent::tb-confirm-dialog";
    private static final String ALARM_ASSIGNEE_DROPDOWN = "//tb-alarm-assignee-panel";
    private static final String NO_USERS_FOUND_MESSAGE = "//div[@class='tb-not-found-content']/span";
    /**
     * Assigns btn.
     *
     * @param type type ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignBtn(String type) {
        return waitUntilElementToBeClickable(String.format(ASSIGN_BTN, type));
    }
    /**
     * User from assign drop down.
     *
     * @param userEmail user email ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement userFromAssignDropDown(String userEmail) {
        return waitUntilElementToBeClickable(String.format(USER_ASSIGN_DROPDOWN, userEmail));
    }
    /**
     * Assigns ed user.
     *
     * @param userEmail user email ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignedUser(String userEmail) {
        return waitUntilElementToBeClickable(String.format(ASSIGN_USER_DISPLAY_NAME, userEmail));
    }
    /**
     * Assigns users.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public List<WebElement> assignUsers() {
        return waitUntilElementsToBeClickable(ASSIGN_USERS_DISPLAY_NAME);
    }
    /**
     * Search user field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement searchUserField() {
        return waitUntilElementToBeClickable(SEARCH_FIELD);
    }
    /**
     * Unassigns ed btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement unassignedBtn() {
        return waitUntilElementToBeClickable(UNASSIGNED_BTN);
    }
    /**
     * Unassigns ed.
     *
     * @param alarmType alarm type ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement unassigned(String alarmType) {
        return waitUntilVisibilityOfElementLocated(String.format(UNASSIGNED, alarmType));
    }
    /**
     * Alarm details btn.
     *
     * @param alarmType alarm type ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement alarmDetailsBtn(String alarmType) {
        return waitUntilElementToBeClickable(String.format(ALARM_DETAILS_BTN, alarmType));
    }
    /**
     * Access forbidden dialog view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement accessForbiddenDialogView() {
        return waitUntilVisibilityOfElementLocated(ACCESS_FORBIDDEN_DIALOG_VIEW);
    }
    /**
     * Alarm assignee dropdown.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement alarmAssigneeDropdown() {
        return waitUntilVisibilityOfElementLocated(ALARM_ASSIGNEE_DROPDOWN);
    }
    /**
     * No users found message.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement noUsersFoundMessage() {
        return waitUntilVisibilityOfElementLocated(NO_USERS_FOUND_MESSAGE);
    }
}
