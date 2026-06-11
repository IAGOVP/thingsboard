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

 * Selenium element locators for customer page page (page object element locators and helpers — Selenium page objects).

 *

 * <p>Defines CSS/XPath selectors; use with matching *Helper for interactions.

 */


public class CustomerPageElements extends OtherPageElementsHelper {
    public CustomerPageElements(WebDriver driver) {
        super(driver);
    }

    private static final String CUSTOMER = "//mat-row//span[contains(text(),'%s')]";
    private static final String EMAIL = ENTITY + "/../..//mat-cell[contains(@class,'email')]/span";
    private static final String COUNTRY = ENTITY + "/../..//mat-cell[contains(@class,'country')]/span";
    private static final String CITY = ENTITY + "/../..//mat-cell[contains(@class,'city')]/span";
    private static final String TITLES = "//mat-cell[contains(@class,'cdk-column-title')]/span";
    protected static final String EDIT_MENU_DASHBOARD_FIELD = "//input[@formcontrolname='dashboard']";
    private static final String EDIT_MENU_DASHBOARD = "//div[@class='cdk-overlay-pane']//span/span[contains(text(),'%s')]";
    private static final String MANAGE_CUSTOMERS_USERS_BTN = ENTITY + "/ancestor::mat-row//mat-icon[contains(text(),' account_circle')]/parent::button";
    private static final String MANAGE_CUSTOMERS_ASSETS_BTN = ENTITY + "/ancestor::mat-row//mat-icon[contains(text(),' domain')]/parent::button";
    private static final String MANAGE_CUSTOMERS_DEVICES_BTN = ENTITY + "/ancestor::mat-row//mat-icon[contains(text(),'devices_other')]/parent::button";
    private static final String MANAGE_CUSTOMERS_DASHBOARDS_BTN = ENTITY + "/ancestor::mat-row//mat-icon[contains(text(),'dashboard')]/parent::button";
    private static final String MANAGE_CUSTOMERS_EDGE_BTN = ENTITY + "/ancestor::mat-row//mat-icon[contains(text(),'router')]/parent::button";
    private static final String ADD_USER_EMAIL = "//tb-add-user-dialog//input[@formcontrolname='email']";
    private static final String ACTIVATE_WINDOW_OK_BTN = "//span[contains(text(),'OK')]";
    private static final String USER_LOGIN_BTN = "//mat-icon[@data-mat-icon-name='login']/parent::button";
    private static final String USER_LOGIN_BTN_BY_EMAIL = "//mat-cell[contains(@class,'email')]/span[contains(text(),'%s')]" +
            "/ancestor::mat-row//mat-icon[@data-mat-icon-name='login']/parent::button";
    private static final String USERS_WIDGET = "//tb-widget";
    private static final String SELECT_COUNTRY_MENU = "//mat-form-field//mat-select[@formcontrolname='country']";
    private static final String COUNTRIES = "//span[@class='mdc-list-item__primary-text']";
    protected static final String INPUT_FIELD = "//input[@formcontrolname='%s']";
    protected static final String INPUT_FIELD_NAME_TITLE = "title";
    private static final String INPUT_FIELD_NAME_CITY = "city";
    private static final String INPUT_FIELD_NAME_STATE = "state";
    private static final String INPUT_FIELD_NAME_ZIP = "zip";
    private static final String INPUT_FIELD_NAME_ADDRESS = "address";
    private static final String INPUT_FIELD_NAME_ADDRESS2 = "address2";
    private static final String INPUT_FIELD_NAME_EMAIL = "email";
    private static final String INPUT_FIELD_NAME_NUMBER = "phoneNumber";
    private static final String INPUT_FIELD_NAME_ASSIGNED_LIST = "entity";
    private static final String ASSIGNED_BTN = "//button[@type='submit']";
    private static final String HIDE_HOME_DASHBOARD_TOOLBAR = "//mat-checkbox[@formcontrolname='homeDashboardHideToolbar']//label";
    private static final String FILTER_BTN = "//tb-filters-edit";
    private static final String TIME_BTN = "//tb-timewindow[not(@hidelabel)]";
    private static final String CUSTOMER_ICON_HEADER = "//tb-breadcrumb//span[contains(text(),'Customer %s')]";
    private static final String CUSTOMER_USER_ICON_HEADER = "Users";
    private static final String CUSTOMER_ASSETS_ICON_HEADER = "Assets";
    private static final String CUSTOMER_DEVICES_ICON_HEADER = "Devices";
    private static final String CUSTOMER_DASHBOARD_ICON_HEADER = "Dashboards";
    private static final String CUSTOMER_EDGE_ICON_HEADER = "edge instances";
    private static final String CUSTOMER_USER_ICON_HEAD = "(//mat-drawer-content//span[contains(@class,'tb-entity-table')])[1]";
    private static final String MANAGE_BTN_VIEW = "//span[contains(text(),'%s')]";
    private static final String MANAGE_CUSTOMERS_USERS_BTN_VIEW = "Manage users";
    private static final String MANAGE_CUSTOMERS_ASSETS_BTN_VIEW = "Manage assets";
    private static final String MANAGE_CUSTOMERS_DEVICE_BTN_VIEW = "Manage devices";
    private static final String MANAGE_CUSTOMERS_DASHBOARD_BTN_VIEW = "Manage dashboards";
    private static final String MANAGE_CUSTOMERS_EDGE_BTN_VIEW = "Manage edges ";
    private static final String DELETE_FROM_VIEW_BTN = "//tb-customer//span[contains(text(),' Delete')]";
    private static final String CUSTOMER_DETAILS_VIEW = "//tb-details-panel";
    private static final String CUSTOMER_DETAILS_ALARMS = CUSTOMER_DETAILS_VIEW + "//span[text()='Alarms']";
    /**
     * Title field add entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement titleFieldAddEntityView() {
        return waitUntilElementToBeClickable(ADD_ENTITY_VIEW + String.format(INPUT_FIELD, INPUT_FIELD_NAME_TITLE));
    }
    /**
     * Title field entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement titleFieldEntityView() {
        return waitUntilVisibilityOfElementLocated(String.format(INPUT_FIELD, INPUT_FIELD_NAME_TITLE));
    }
    /**
     * Customer.
     *
     * @param entityName entity name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customer(String entityName) {
        return waitUntilElementToBeClickable(String.format(CUSTOMER, entityName));
    }
    /**
     * Email.
     *
     * @param entityName entity name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement email(String entityName) {
        return waitUntilVisibilityOfElementLocated(String.format(EMAIL, entityName));
    }
    /**
     * Counts ry.
     *
     * @param entityName entity name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement country(String entityName) {
        return waitUntilVisibilityOfElementLocated(String.format(COUNTRY, entityName));
    }
    /**
     * City.
     *
     * @param entityName entity name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement city(String entityName) {
        return waitUntilVisibilityOfElementLocated(String.format(CITY, entityName));
    }
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
     * Edit menu dashboard field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement editMenuDashboardField() {
        return waitUntilVisibilityOfElementLocated(EDIT_MENU_DASHBOARD_FIELD);
    }
    /**
     * Edit menu dashboard.
     *
     * @param dashboardName dashboard name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement editMenuDashboard(String dashboardName) {
        return waitUntilElementToBeClickable(String.format(EDIT_MENU_DASHBOARD, dashboardName));
    }
    /**
     * Phone number entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement phoneNumberEntityView() {
        return waitUntilVisibilityOfElementLocated(String.format(INPUT_FIELD, INPUT_FIELD_NAME_NUMBER));
    }
    /**
     * Phone number add entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement phoneNumberAddEntityView() {
        return waitUntilVisibilityOfElementLocated(ADD_ENTITY_VIEW + String.format(INPUT_FIELD, INPUT_FIELD_NAME_NUMBER));
    }
    /**
     * Manage customers user btn.
     *
     * @param title title ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageCustomersUserBtn(String title) {
        return waitUntilElementToBeClickable(String.format(MANAGE_CUSTOMERS_USERS_BTN, title));
    }
    /**
     * Manage customers assets btn.
     *
     * @param title title ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageCustomersAssetsBtn(String title) {
        return waitUntilElementToBeClickable(String.format(MANAGE_CUSTOMERS_ASSETS_BTN, title));
    }
    /**
     * Manage customers devices btn.
     *
     * @param title title ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageCustomersDevicesBtn(String title) {
        return waitUntilElementToBeClickable(String.format(MANAGE_CUSTOMERS_DEVICES_BTN, title));
    }
    /**
     * Manage customers dashboards btn.
     *
     * @param title title ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageCustomersDashboardsBtn(String title) {
        return waitUntilElementToBeClickable(String.format(MANAGE_CUSTOMERS_DASHBOARDS_BTN, title));
    }
    /**
     * Manage customers edge btn.
     *
     * @param title title ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageCustomersEdgeBtn(String title) {
        return waitUntilElementToBeClickable(String.format(MANAGE_CUSTOMERS_EDGE_BTN, title));
    }
    /**
     * Add user email field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement addUserEmailField() {
        return waitUntilElementToBeClickable(ADD_USER_EMAIL);
    }
    /**
     * Activate window ok btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement activateWindowOkBtn() {
        return waitUntilElementToBeClickable(ACTIVATE_WINDOW_OK_BTN);
    }
    /**
     * User login btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement userLoginBtn() {
        return waitUntilElementToBeClickable(USER_LOGIN_BTN);
    }
    /**
     * Returns user login btn by email.
     *
     * @param email email ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement getUserLoginBtnByEmail(String email) {
        return waitUntilElementToBeClickable(String.format(USER_LOGIN_BTN_BY_EMAIL, email));
    }
    /**
     * Users widget.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement usersWidget() {
        return waitUntilVisibilityOfElementLocated(USERS_WIDGET);
    }
    /**
     * Counts ry select menu entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement countrySelectMenuEntityView() {
        return waitUntilElementToBeClickable(SELECT_COUNTRY_MENU);
    }
    /**
     * Counts ry select menu add entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement countrySelectMenuAddEntityView() {
        return waitUntilElementToBeClickable(ADD_ENTITY_VIEW + SELECT_COUNTRY_MENU);
    }
    /**
     * Counts ries.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public List<WebElement> countries() {
        return waitUntilElementsToBeClickable(COUNTRIES);
    }
    /**
     * City entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement cityEntityView() {
        return waitUntilVisibilityOfElementLocated(String.format(INPUT_FIELD, INPUT_FIELD_NAME_CITY));
    }
    /**
     * City add entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement cityAddEntityView() {
        return waitUntilVisibilityOfElementLocated(ADD_ENTITY_VIEW + String.format(INPUT_FIELD, INPUT_FIELD_NAME_CITY));
    }
    /**
     * State entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement stateEntityView() {
        return waitUntilVisibilityOfElementLocated(String.format(INPUT_FIELD, INPUT_FIELD_NAME_STATE));
    }
    /**
     * State add entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement stateAddEntityView() {
        return waitUntilVisibilityOfElementLocated(ADD_ENTITY_VIEW + String.format(INPUT_FIELD, INPUT_FIELD_NAME_STATE));
    }
    /**
     * Zip entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement zipEntityView() {
        return waitUntilVisibilityOfElementLocated(String.format(INPUT_FIELD, INPUT_FIELD_NAME_ZIP));
    }
    /**
     * Zip add entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement zipAddEntityView() {
        return waitUntilVisibilityOfElementLocated(ADD_ENTITY_VIEW + String.format(INPUT_FIELD, INPUT_FIELD_NAME_ZIP));
    }
    /**
     * Address entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement addressEntityView() {
        return waitUntilVisibilityOfElementLocated(String.format(INPUT_FIELD, INPUT_FIELD_NAME_ADDRESS));
    }
    /**
     * Address add entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement addressAddEntityView() {
        return waitUntilVisibilityOfElementLocated(ADD_ENTITY_VIEW + String.format(INPUT_FIELD, INPUT_FIELD_NAME_ADDRESS));
    }
    /**
     * Address2entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement address2EntityView() {
        return waitUntilVisibilityOfElementLocated(String.format(INPUT_FIELD, INPUT_FIELD_NAME_ADDRESS2));
    }
    /**
     * Address2add entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement address2AddEntityView() {
        return waitUntilVisibilityOfElementLocated(ADD_ENTITY_VIEW + String.format(INPUT_FIELD, INPUT_FIELD_NAME_ADDRESS2));
    }
    /**
     * Email entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement emailEntityView() {
        return waitUntilVisibilityOfElementLocated(String.format(INPUT_FIELD, INPUT_FIELD_NAME_EMAIL));
    }
    /**
     * Email add entity view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement emailAddEntityView() {
        return waitUntilVisibilityOfElementLocated(ADD_ENTITY_VIEW + String.format(INPUT_FIELD, INPUT_FIELD_NAME_EMAIL));
    }
    /**
     * Assigns ed field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignedField() {
        return waitUntilVisibilityOfElementLocated(String.format(INPUT_FIELD, INPUT_FIELD_NAME_ASSIGNED_LIST));
    }
    /**
     * Submit assigned btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement submitAssignedBtn() {
        return waitUntilElementToBeClickable(ASSIGNED_BTN);
    }
    /**
     * Hide home dashboard toolbar checkbox.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement hideHomeDashboardToolbarCheckbox() {
        return waitUntilElementToBeClickable(HIDE_HOME_DASHBOARD_TOOLBAR);
    }
    /**
     * Filter btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement filterBtn() {
        return waitUntilVisibilityOfElementLocated(FILTER_BTN);
    }
    /**
     * Time btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement timeBtn() {
        return waitUntilVisibilityOfElementLocated(TIME_BTN);
    }
    /**
     * Customer user icon header.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customerUserIconHeader() {
        return waitUntilVisibilityOfElementLocated(String.format(CUSTOMER_ICON_HEADER, CUSTOMER_USER_ICON_HEADER));
    }
    /**
     * Customer assets icon header.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customerAssetsIconHeader() {
        return waitUntilVisibilityOfElementLocated(String.format(CUSTOMER_ICON_HEADER, CUSTOMER_ASSETS_ICON_HEADER));
    }
    /**
     * Customer devices icon header.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customerDevicesIconHeader() {
        return waitUntilVisibilityOfElementLocated(String.format(CUSTOMER_ICON_HEADER, CUSTOMER_DEVICES_ICON_HEADER));
    }
    /**
     * Customer dashboard icon header.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customerDashboardIconHeader() {
        return waitUntilVisibilityOfElementLocated(String.format(CUSTOMER_ICON_HEADER, CUSTOMER_DASHBOARD_ICON_HEADER));
    }
    /**
     * Customer edge icon header.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customerEdgeIconHeader() {
        return waitUntilVisibilityOfElementLocated(String.format(CUSTOMER_ICON_HEADER, CUSTOMER_EDGE_ICON_HEADER));
    }
    /**
     * Customer manage window icon head.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customerManageWindowIconHead() {
        return waitUntilVisibilityOfElementLocated(CUSTOMER_USER_ICON_HEAD);
    }
    /**
     * Manage customers user btn view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageCustomersUserBtnView() {
        return waitUntilElementToBeClickable(String.format(MANAGE_BTN_VIEW, MANAGE_CUSTOMERS_USERS_BTN_VIEW));
    }
    /**
     * Manage customers assets btn view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageCustomersAssetsBtnView() {
        return waitUntilElementToBeClickable(String.format(MANAGE_BTN_VIEW, MANAGE_CUSTOMERS_ASSETS_BTN_VIEW));
    }
    /**
     * Manage customers device btn view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageCustomersDeviceBtnView() {
        return waitUntilElementToBeClickable(String.format(MANAGE_BTN_VIEW, MANAGE_CUSTOMERS_DEVICE_BTN_VIEW));
    }
    /**
     * Manage customers dashboards btn view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageCustomersDashboardsBtnView() {
        return waitUntilElementToBeClickable(String.format(MANAGE_BTN_VIEW, MANAGE_CUSTOMERS_DASHBOARD_BTN_VIEW));
    }
    /**
     * Manage customers edge btn view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement manageCustomersEdgeBtnView() {
        return waitUntilElementToBeClickable(String.format(MANAGE_BTN_VIEW, MANAGE_CUSTOMERS_EDGE_BTN_VIEW));
    }
    /**
     * Customer view delete btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customerViewDeleteBtn() {
        return waitUntilElementToBeClickable(DELETE_FROM_VIEW_BTN);
    }
    /**
     * Customer details view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customerDetailsView() {
        return waitUntilPresenceOfElementLocated(CUSTOMER_DETAILS_VIEW);
    }
    /**
     * Customer details alarms btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customerDetailsAlarmsBtn() {
        return waitUntilElementToBeClickable(CUSTOMER_DETAILS_ALARMS);
    }
}
