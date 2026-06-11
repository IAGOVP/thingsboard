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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.thingsboard.server.msa.ui.base.AbstractBasePage;

import java.util.List;


/**

 * Selenium element locators for other page page (page object element locators and helpers — Selenium page objects).

 *

 * <p>Defines CSS/XPath selectors; use with matching *Helper for interactions.

 */


public class OtherPageElements extends AbstractBasePage {
    public OtherPageElements(WebDriver driver) {
        super(driver);
    }

    protected static final String ENTITY = "//mat-row//span[contains(text(),'%s')]";
    protected static final String DELETE_BTN = ENTITY + "/ancestor::mat-row//mat-icon[contains(text(),'delete')]/ancestor::button";
    protected static final String DETAILS_BTN = ENTITY + "/../..//mat-icon[contains(text(),'edit')]/../..";
    private static final String ENTITY_COUNT = "//div[@class='mat-paginator-range-label']";
    private static final String WARNING_DELETE_POPUP_YES = "//tb-confirm-dialog//button[2]";
    private static final String WARNING_DELETE_POPUP_TITLE = "//tb-confirm-dialog/h2";
    private static final String REFRESH_BTN = "//mat-icon[contains(text(),'refresh')]/parent::button";
    private static final String HELP_BTN = "//mat-icon[contains(text(),'help')]/ancestor::button";
    private static final String CHECKBOX = "//mat-row//span[contains(text(),'%s')]/../..//mat-checkbox";
    private static final String CHECKBOXES = "//tbody//mat-checkbox";
    private static final String DELETE_SELECTED_BTN = "//div[@class='mat-toolbar-tools']//mat-icon[contains(text(),'delete')]/parent::button";
    private static final String DELETE_BTNS = "//mat-icon[contains(text(),' delete')]/../..";
    private static final String MARKS_CHECKBOX = "//mat-row[contains (@class,'mat-selected')]//mat-checkbox[contains(@class, 'checked')]";
    private static final String SELECT_ALL_CHECKBOX = "//thead//mat-checkbox";
    private static final String ALL_ENTITY = "//mat-row[@class='mat-mdc-row mdc-data-table__row cdk-row mat-row-select ng-star-inserted']";
    private static final String EDIT_PENCIL_BTN = "//tb-details-panel//mat-icon[contains(text(),'edit')]/ancestor::button";
    private static final String NAME_FIELD_EDIT_VIEW = "//input[@formcontrolname='name']";
    private static final String HEADER_NAME_VIEW = "//header//div[@class='tb-details-title']/span";
    private static final String DONE_BTN_EDIT_VIEW = "//mat-icon[contains(text(),'done')]/ancestor::button";
    private static final String DESCRIPTION_ENTITY_VIEW = "//textarea";
    private static final String DESCRIPTION_ADD_ENTITY_VIEW = "//tb-add-entity-dialog//textarea";
    private static final String DEBUG_CHECKBOX_EDIT = "//mat-checkbox[@formcontrolname='debugMode']";
    private static final String DEBUG_CHECKBOX_VIEW = "//mat-checkbox[@formcontrolname='debugMode']//input";
    private static final String CLOSE_ENTITY_VIEW_BTN = "//header//mat-icon[contains(text(),'close')]/parent::button";
    private static final String SEARCH_BTN = "//mat-toolbar//mat-icon[contains(text(),'search')]/ancestor::button[contains(@class,'ng-star')]";
    private static final String SORT_BY_NAME_BTN = "//div[contains(text(),'Name')]";
    private static final String SORT_BY_TITLE_BTN = "//div[contains(text(),'Title')]";
    private static final String SORT_BY_TIME_BTN = "//div[contains(text(),'Created time')]/..";
    private static final String CREATED_TIME = "//tbody[@role='rowgroup']//mat-cell[2]/span";
    private static final String PLUS_BTN = "//mat-icon[contains(text(),'add')]/ancestor::button";
    private static final String CREATE_VIEW_ADD_BTN = "//span[contains(text(),'Add')]/..";
    private static final String WARNING_MESSAGE = "//tb-snack-bar-component/div/div";
    private static final String ERROR_MESSAGE = "//mat-error";
    private static final String ENTITY_VIEW_TITLE = "//div[@class='tb-details-title']//span";
    private static final String LIST_OF_ENTITY = "//div[@role='listbox']/mat-option";
    private static final String ENTITY_FROM_LIST = "//div[@role='listbox']/mat-option//span[contains(text(),'%s')]";
    protected static final String ADD_ENTITY_VIEW = "//tb-add-entity-dialog";
    protected static final String STATE_CONTROLLER = "//tb-entity-state-controller";
    private static final String SEARCH_FIELD = "//input[contains (@placeholder,'Search')]";
    private static final String BROWSE_FILE = "//input[@class='file-input']";
    private static final String IMPORT_BROWSE_FILE = "//mat-dialog-container//span[contains(text(),'Import')]/..";
    private static final String IMPORTING_FILE = "//div[contains(text(),'%s')]";
    private static final String CLEAR_IMPORT_FILE_BTN = "//div[@class='tb-file-clear-container']//button";
    /**
     * Returns entity.
     *
     * @param entityName entity name ({@link String})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getEntity(String entityName) {
        return String.format(ENTITY, entityName);
    }
    /**
     * Returns warning message.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getWarningMessage() {
        return WARNING_MESSAGE;
    }
    /**
     * Returns delete btns.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getDeleteBtns() {
        return DELETE_BTNS;
    }
    /**
     * Returns checkbox.
     *
     * @param entityName entity name ({@link String})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getCheckbox(String entityName) {
        return String.format(CHECKBOX, entityName);
    }
    /**
     * Returns checkboxes.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getCheckboxes() {
        return String.format(CHECKBOXES);
    }
    /**
     * Warning pop up yes btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement warningPopUpYesBtn() {
        return waitUntilElementToBeClickable(WARNING_DELETE_POPUP_YES);
    }
    /**
     * Warning pop up title.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement warningPopUpTitle() {
        return waitUntilElementToBeClickable(WARNING_DELETE_POPUP_TITLE);
    }
    /**
     * Entity count.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entityCount() {
        return waitUntilVisibilityOfElementLocated(ENTITY_COUNT);
    }
    /**
     * Refresh btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement refreshBtn() {
        return waitUntilElementToBeClickable(REFRESH_BTN);
    }
    /**
     * Help btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement helpBtn() {
        return waitUntilElementToBeClickable(HELP_BTN);
    }
    /**
     * Checks box.
     *
     * @param entityName entity name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement checkBox(String entityName) {
        return waitUntilElementToBeClickable(String.format(CHECKBOX, entityName));
    }
    /**
     * Present check box.
     *
     * @param name name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement presentCheckBox(String name) {
        return waitUntilPresenceOfElementLocated(getCheckbox(name));
    }
    /**
     * Deletes selected btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deleteSelectedBtn() {
        return waitUntilElementToBeClickable(DELETE_SELECTED_BTN);
    }
    /**
     * Select all check box.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement selectAllCheckBox() {
        return waitUntilElementToBeClickable(SELECT_ALL_CHECKBOX);
    }
    /**
     * Edit pencil btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement editPencilBtn() {
        waitUntilVisibilityOfElementsLocated(EDIT_PENCIL_BTN);
        return waitUntilElementToBeClickable(EDIT_PENCIL_BTN);
    }
    /**
     * Name field edit menu.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement nameFieldEditMenu() {
        return waitUntilElementToBeClickable(NAME_FIELD_EDIT_VIEW);
    }
    /**
     * Header name view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement headerNameView() {
        return waitUntilVisibilityOfElementLocated(HEADER_NAME_VIEW);
    }
    /**
     * Done btn edit view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement doneBtnEditView() {
        return waitUntilElementToBeClickable(DONE_BTN_EDIT_VIEW);
    }
    /**
     * Description entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement descriptionEntityView() {
        return waitUntilVisibilityOfElementLocated(DESCRIPTION_ENTITY_VIEW);
    }
    /**
     * Description add entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement descriptionAddEntityView() {
        return waitUntilVisibilityOfElementLocated(DESCRIPTION_ADD_ENTITY_VIEW);
    }
    /**
     * Debug checkbox edit.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement debugCheckboxEdit() {
        return waitUntilElementToBeClickable(DEBUG_CHECKBOX_EDIT);
    }
    /**
     * Debug checkbox view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement debugCheckboxView() {
        return waitUntilPresenceOfElementLocated(DEBUG_CHECKBOX_VIEW);
    }
    /**
     * Close entity view btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement closeEntityViewBtn() {
        return waitUntilElementToBeClickable(CLOSE_ENTITY_VIEW_BTN);
    }
    /**
     * Search btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement searchBtn() {
        return waitUntilElementToBeClickable(SEARCH_BTN);
    }
    /**
     * Deletes btns.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public List<WebElement> deleteBtns() {
        return waitUntilVisibilityOfElementsLocated(DELETE_BTNS);
    }
    /**
     * Checks boxes.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public List<WebElement> checkBoxes() {
        return waitUntilElementsToBeClickable(CHECKBOXES);
    }
    /**
     * Mark checkbox.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public List<WebElement> markCheckbox() {
        return waitUntilVisibilityOfElementsLocated(MARKS_CHECKBOX);
    }
    /**
     * All entity.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public List<WebElement> allEntity() {
        return waitUntilVisibilityOfElementsLocated(ALL_ENTITY);
    }
    /**
     * Done btn edit view visible.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement doneBtnEditViewVisible() {
        return waitUntilVisibilityOfElementLocated(DONE_BTN_EDIT_VIEW);
    }
    /**
     * Sort by name btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement sortByNameBtn() {
        return waitUntilElementToBeClickable(SORT_BY_NAME_BTN);
    }
    /**
     * Sort by title btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement sortByTitleBtn() {
        return waitUntilElementToBeClickable(SORT_BY_TITLE_BTN);
    }
    /**
     * Sort by time btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement sortByTimeBtn() {
        return waitUntilElementToBeClickable(SORT_BY_TIME_BTN);
    }
    /**
     * Creates d time.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public List<WebElement> createdTime() {
        return waitUntilVisibilityOfElementsLocated(CREATED_TIME);
    }
    /**
     * Plus btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement plusBtn() {
        return waitUntilElementToBeClickable(PLUS_BTN);
    }
    /**
     * Add btn c.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement addBtnC() {
        return waitUntilElementToBeClickable(CREATE_VIEW_ADD_BTN);
    }
    /**
     * Add btn v.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement addBtnV() {
        return waitUntilVisibilityOfElementLocated(CREATE_VIEW_ADD_BTN);
    }
    /**
     * Warning message.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement warningMessage() {
        return waitUntilVisibilityOfElementLocated(WARNING_MESSAGE);
    }
    /**
     * Deletes btn.
     *
     * @param entityName entity name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deleteBtn(String entityName) {
        return waitUntilVisibilityOfElementLocated(String.format(DELETE_BTN, entityName));
    }
    /**
     * Details btn.
     *
     * @param entityName entity name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement detailsBtn(String entityName) {
        return waitUntilVisibilityOfElementLocated(String.format(DETAILS_BTN, entityName));
    }
    /**
     * Entity.
     *
     * @param entityName entity name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entity(String entityName) {
        return waitUntilElementToBeClickable(String.format(ENTITY, entityName));
    }
    /**
     * Error message.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement errorMessage() {
        return waitUntilVisibilityOfElementLocated(ERROR_MESSAGE);
    }
    /**
     * Entity view title.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entityViewTitle() {
        return waitUntilVisibilityOfElementLocated(ENTITY_VIEW_TITLE);
    }
    /**
     * Lists of entity.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public List<WebElement> listOfEntity() {
        return waitUntilElementsToBeClickable(LIST_OF_ENTITY);
    }
    /**
     * Entity from list.
     *
     * @param entityName entity name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entityFromList(String entityName) {
        return waitUntilVisibilityOfElementLocated(String.format(ENTITY_FROM_LIST, entityName));
    }
    /**
     * Add entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement addEntityView() {
        return waitUntilVisibilityOfElementLocated(ADD_ENTITY_VIEW);
    }
    /**
     * State controller.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement stateController() {
        return waitUntilVisibilityOfElementLocated(STATE_CONTROLLER);
    }
    /**
     * Search field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement searchField() {
        return waitUntilElementToBeClickable(SEARCH_FIELD);
    }
    /**
     * Browse file.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement browseFile() {
        waitUntilElementToBeClickable(BROWSE_FILE + "/preceding-sibling::button");
        return driver.findElement(By.xpath(BROWSE_FILE));
    }
    /**
     * Imports browse file btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement importBrowseFileBtn() {
        return waitUntilElementToBeClickable(IMPORT_BROWSE_FILE);
    }
    /**
     * Imports ing file.
     *
     * @param fileName file name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement importingFile(String fileName) {
        return waitUntilVisibilityOfElementLocated(String.format(IMPORTING_FILE, fileName));
    }
    /**
     * Clear import file btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement clearImportFileBtn() {
        return waitUntilElementToBeClickable(CLEAR_IMPORT_FILE_BTN);
    }
}
