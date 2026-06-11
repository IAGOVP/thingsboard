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

 * Selenium element locators for device page page (page object element locators and helpers — Selenium page objects).

 *

 * <p>Defines CSS/XPath selectors; use with matching *Helper for interactions.

 */


public class DevicePageElements extends OtherPageElementsHelper {
    public DevicePageElements(WebDriver driver) {
        super(driver);
    }

    private static final String DEVICE = "//table//span[text()='%s']";
    private static final String DEVICE_DETAILS_VIEW = "//tb-details-panel";
    private static final String DEVICE_DETAILS_ALARMS = DEVICE_DETAILS_VIEW + "//span[text()='Alarms']";
    private static final String ASSIGN_TO_CUSTOMER_BTN = "//mat-cell[contains(@class,'name')]/span[text()='%s']" +
            "/ancestor::mat-row//mat-icon[contains(text(),'assignment_ind')]/parent::button";
    private static final String CHOOSE_CUSTOMER_FOR_ASSIGN_FIELD = "//input[@formcontrolname='entity']";
    private static final String ENTITY_FROM_DROPDOWN = "//div[@role = 'listbox']//span[text() = '%s']";
    private static final String CLOSE_DEVICE_DETAILS_VIEW = "//header//mat-icon[contains(text(),'close')]/parent::button";
    private static final String SUBMIT_BTN = "//button[@type='submit']";
    private static final String ADD_DEVICE_BTN = "//mat-icon[text() = 'insert_drive_file']/parent::button";
    private static final String HEADER_NAME_VIEW = "//header//div[@class='tb-details-title']/span";
    private static final String ADD_DEVICE_VIEW = "//tb-device-wizard";
    private static final String DELETE_BTN_DETAILS_TAB = "//span[contains(text(),'Delete device')]/parent::button";
    private static final String CHECKBOX_GATEWAY_EDIT = "//mat-checkbox[@formcontrolname='gateway']//label";
    private static final String CHECKBOX_OVERWRITE_ACTIVITY_TIME_EDIT = "//mat-checkbox[@formcontrolname='overwriteActivityTime']//label";
    private static final String CHECKBOX_GATEWAY_DETAILS = "//mat-checkbox[@formcontrolname='gateway']//input";
    private static final String CHECKBOX_GATEWAY_PAGE = DEVICE + "/ancestor::mat-row//mat-cell[contains(@class,'cdk-column-gateway')]//mat-icon[text() = 'check_box']";
    private static final String CHECKBOX_OVERWRITE_ACTIVITY_TIME_DETAILS = "//mat-checkbox[@formcontrolname='overwriteActivityTime']//input";
    private static final String CLEAR_PROFILE_FIELD_BTN = "//button[@aria-label='Clear']";
    private static final String DEVICE_PROFILE_REDIRECTED_BTN = "//a[@aria-label='Open device profile']";
    private static final String DEVICE_LABEL_PAGE = DEVICE + "/ancestor::mat-row//mat-cell[contains(@class,'cdk-column-label')]/span";
    private static final String DEVICE_CUSTOMER_PAGE = DEVICE + "/ancestor::mat-row//mat-cell[contains(@class,'cdk-column-customerTitle')]/span";
    private static final String DEVICE_LABEL_EDIT = "//input[@formcontrolname='label']";
    private static final String DEVICE_DEVICE_PROFILE_PAGE = DEVICE + "/ancestor::mat-row//mat-cell[contains(@class,'cdk-column-deviceProfileName')]/span";
    private static final String ASSIGN_BTN = ENTITY + "/ancestor::mat-row//mat-icon[contains(text(),'assignment_ind')]/ancestor::button";
    private static final String UNASSIGN_BTN = ENTITY + "/ancestor::mat-row//mat-icon[contains(text(),' assignment_return')]/ancestor::button";
    private static final String ASSIGN_BTN_DETAILS_TAB = "//span[contains(text(),'Assign to customer')]/parent::button";
    private static final String UNASSIGN_BTN_DETAILS_TAB = "//span[contains(text(),'Unassign from customer')]/parent::button";
    private static final String ASSIGNED_FIELD_DETAILS_TAB = "//mat-label[text() = 'Assigned to customer']/parent::label/parent::div/input";
    private static final String ASSIGN_MARKED_DEVICE_BTN = "//mat-icon[text() = 'assignment_ind']/parent::button";
    private static final String FILTER_BTN = "//tb-device-info-filter/button";
    private static final String DEVICE_PROFILE_FIELD = "(//input[@formcontrolname='deviceProfile'])[2]";
    private static final String DEVICE_STATE_SELECT = "//div[contains(@class,'tb-filter-panel')]//mat-select[@role='combobox']";
    private static final String LIST_OF_DEVICES_STATE = "//div[@class='status']";
    private static final String LIST_OF_DEVICES_PROFILE = "//mat-cell[contains(@class,'deviceProfileName')]";
    private static final String MAKE_DEVICE_PUBLIC_BTN = DEVICE + "/ancestor::mat-row//mat-icon[contains(text(),'share')]/parent::button";
    private static final String DEVICE_IS_PUBLIC_CHECKBOX = DEVICE + "/ancestor::mat-row//mat-icon[contains(text(),'check_box')]";
    private static final String MAKE_DEVICE_PUBLIC_BTN_DETAILS_TAB = "//span[contains(text(),'Make device public')]/parent::button";
    private static final String MAKE_DEVICE_PRIVATE_BTN = DEVICE + "/ancestor::mat-row//mat-icon[contains(text(),'reply')]/parent::button";
    private static final String DEVICE_IS_PRIVATE_CHECKBOX = DEVICE + "/ancestor::mat-row//mat-icon[contains(text(),'check_box_outline_blank')]";
    private static final String MAKE_DEVICE_PRIVATE_BTN_DETAILS_TAB = "//span[contains(text(),'Make device private')]/parent::button";
    /**
     * Device.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement device(String deviceName) {
        return waitUntilElementToBeClickable(String.format(DEVICE, deviceName));
    }
    /**
     * Device details alarms btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceDetailsAlarmsBtn() {
        return waitUntilElementToBeClickable(DEVICE_DETAILS_ALARMS);
    }
    /**
     * Device details view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceDetailsView() {
        return waitUntilPresenceOfElementLocated(DEVICE_DETAILS_VIEW);
    }
    /**
     * Assigns to customer btn.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignToCustomerBtn(String deviceName) {
        return waitUntilElementToBeClickable(String.format(ASSIGN_TO_CUSTOMER_BTN, deviceName));
    }
    /**
     * Choose customer for assign field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement chooseCustomerForAssignField() {
        return waitUntilElementToBeClickable(CHOOSE_CUSTOMER_FOR_ASSIGN_FIELD);
    }
    /**
     * Entity from dropdown.
     *
     * @param customerTitle customer title ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entityFromDropdown(String customerTitle) {
        return waitUntilElementToBeClickable(String.format(ENTITY_FROM_DROPDOWN, customerTitle));
    }
    /**
     * Close device details view btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement closeDeviceDetailsViewBtn() {
        return waitUntilElementToBeClickable(CLOSE_DEVICE_DETAILS_VIEW);
    }
    /**
     * Submit btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement submitBtn() {
        return waitUntilElementToBeClickable(SUBMIT_BTN);
    }
    /**
     * Add device btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement addDeviceBtn() {
        return waitUntilElementToBeClickable(ADD_DEVICE_BTN);
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
     * Add device view.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement addDeviceView() {
        return waitUntilPresenceOfElementLocated(ADD_DEVICE_VIEW);
    }
    /**
     * Deletes btn details tab.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deleteBtnDetailsTab() {
        return waitUntilElementToBeClickable(DELETE_BTN_DETAILS_TAB);
    }
    /**
     * Checks box gateway edit.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement checkboxGatewayEdit() {
        return waitUntilElementToBeClickable(CHECKBOX_GATEWAY_EDIT);
    }
    /**
     * Checks box overwrite activity time edit.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement checkboxOverwriteActivityTimeEdit() {
        return waitUntilElementToBeClickable(CHECKBOX_OVERWRITE_ACTIVITY_TIME_EDIT);
    }
    /**
     * Checks box gateway details tab.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement checkboxGatewayDetailsTab() {
        return waitUntilPresenceOfElementLocated(CHECKBOX_GATEWAY_DETAILS);
    }
    /**
     * Checks box gateway page.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement checkboxGatewayPage(String deviceName) {
        return waitUntilPresenceOfElementLocated(String.format(CHECKBOX_GATEWAY_PAGE, deviceName));
    }
    /**
     * Checks box overwrite activity time details.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement checkboxOverwriteActivityTimeDetails() {
        return waitUntilPresenceOfElementLocated(CHECKBOX_OVERWRITE_ACTIVITY_TIME_DETAILS);
    }
    /**
     * Clear profile field btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement clearProfileFieldBtn() {
        return waitUntilElementToBeClickable(CLEAR_PROFILE_FIELD_BTN);
    }
    /**
     * Device profile redirected btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceProfileRedirectedBtn() {
        return waitUntilElementToBeClickable(DEVICE_PROFILE_REDIRECTED_BTN);
    }
    /**
     * Device label on page.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceLabelOnPage(String deviceName) {
        return waitUntilVisibilityOfElementLocated(String.format(DEVICE_LABEL_PAGE, deviceName));
    }
    /**
     * Device customer on page.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceCustomerOnPage(String deviceName) {
        return waitUntilVisibilityOfElementLocated(String.format(DEVICE_CUSTOMER_PAGE, deviceName));
    }
    /**
     * Device label edit field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceLabelEditField() {
        return waitUntilElementToBeClickable(DEVICE_LABEL_EDIT);
    }
    /**
     * Device label details field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceLabelDetailsField() {
        return waitUntilVisibilityOfElementLocated(DEVICE_LABEL_EDIT);
    }
    /**
     * Device device profile on page.
     *
     * @param deviceProfileTitle device profile title ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceDeviceProfileOnPage(String deviceProfileTitle) {
        return waitUntilVisibilityOfElementLocated(String.format(DEVICE_DEVICE_PROFILE_PAGE, deviceProfileTitle));
    }
    /**
     * Assigns btn.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignBtn(String deviceName) {
        return waitUntilElementToBeClickable(String.format(ASSIGN_BTN, deviceName));
    }
    /**
     * Assigns btn visible.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignBtnVisible(String deviceName) {
        return waitUntilVisibilityOfElementLocated(String.format(ASSIGN_BTN, deviceName));
    }
    /**
     * Unassigns btn.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement unassignBtn(String deviceName) {
        return waitUntilElementToBeClickable(String.format(UNASSIGN_BTN, deviceName));
    }
    /**
     * Assigns btn details tab.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignBtnDetailsTab() {
        return waitUntilElementToBeClickable(ASSIGN_BTN_DETAILS_TAB);
    }
    /**
     * Unassigns btn details tab.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement unassignBtnDetailsTab() {
        return waitUntilElementToBeClickable(UNASSIGN_BTN_DETAILS_TAB);
    }
    /**
     * Assigns field details tab.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignFieldDetailsTab() {
        return waitUntilVisibilityOfElementLocated(ASSIGNED_FIELD_DETAILS_TAB);
    }
    /**
     * Assigns marked device btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignMarkedDeviceBtn() {
        return waitUntilVisibilityOfElementLocated(ASSIGN_MARKED_DEVICE_BTN);
    }
    /**
     * Filter btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement filterBtn() {
        return waitUntilElementToBeClickable(FILTER_BTN);
    }
    /**
     * Device profile field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceProfileField() {
        return waitUntilElementToBeClickable(DEVICE_PROFILE_FIELD);
    }
    /**
     * Device state select.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceStateSelect() {
        return waitUntilElementToBeClickable(DEVICE_STATE_SELECT);
    }
    /**
     * Lists of devices state.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public List<WebElement> listOfDevicesState() {
        return waitUntilVisibilityOfElementsLocated(LIST_OF_DEVICES_STATE);
    }
    /**
     * Lists of devices profile.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    public List<WebElement> listOfDevicesProfile() {
        return waitUntilVisibilityOfElementsLocated(LIST_OF_DEVICES_PROFILE);
    }
    /**
     * Make device public btn.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement makeDevicePublicBtn(String deviceName) {
        return waitUntilElementToBeClickable(String.format(MAKE_DEVICE_PUBLIC_BTN, deviceName));
    }
    /**
     * Device is public checkbox.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceIsPublicCheckbox(String deviceName) {
        return waitUntilVisibilityOfElementLocated(String.format(DEVICE_IS_PUBLIC_CHECKBOX, deviceName));
    }
    /**
     * Make device public btn details tab.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement makeDevicePublicBtnDetailsTab() {
        return waitUntilElementToBeClickable(MAKE_DEVICE_PUBLIC_BTN_DETAILS_TAB);
    }
    /**
     * Make device private btn.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement makeDevicePrivateBtn(String deviceName) {
        return waitUntilElementToBeClickable(String.format(MAKE_DEVICE_PRIVATE_BTN, deviceName));
    }
    /**
     * Device is private checkbox.
     *
     * @param deviceName device name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceIsPrivateCheckbox(String deviceName) {
        return waitUntilVisibilityOfElementLocated(String.format(DEVICE_IS_PRIVATE_CHECKBOX, deviceName));
    }
    /**
     * Make device private btn details tab.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement makeDevicePrivateBtnDetailsTab() {
        return waitUntilElementToBeClickable(MAKE_DEVICE_PRIVATE_BTN_DETAILS_TAB);
    }
}
