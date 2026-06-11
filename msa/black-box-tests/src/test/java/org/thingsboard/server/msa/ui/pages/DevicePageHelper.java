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

 * Page object helper for device page UI actions (page object element locators and helpers — Selenium page objects).

 */


public class DevicePageHelper extends DevicePageElements {
    public DevicePageHelper(WebDriver driver) {
        super(driver);
    }

    private String description;
    private String label;
    /**
     * Open device alarms.
     *
     * @param deviceName device name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openDeviceAlarms(String deviceName) {
        if (!deviceDetailsView().isDisplayed()) {
            device(deviceName).click();
        }
        deviceDetailsAlarmsBtn().click();
    }
    /**
     * Assigns to customer.
     *
     * @param customerTitle customer title ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void assignToCustomer(String customerTitle) {
        chooseCustomerForAssignField().click();
        entityFromDropdown(customerTitle).click();
        submitBtn().click();
    }
    /**
     * Open create device view.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openCreateDeviceView() {
        plusBtn().click();
        addDeviceBtn().click();
    }
    /**
     * Deletes device by right side btn.
     *
     * @param deviceName device name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void deleteDeviceByRightSideBtn(String deviceName) {
        deleteBtn(deviceName).click();
        warningPopUpYesBtn().click();
    }
    /**
     * Deletes device from details tab.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void deleteDeviceFromDetailsTab() {
        deleteBtnDetailsTab().click();
        warningPopUpYesBtn().click();
    }
    /**
     * Set description.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setDescription() {
        scrollToElement(descriptionEntityView());
        description = descriptionEntityView().getAttribute("value");
    }
    /**
     * Set label.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setLabel() {
        label = deviceLabelDetailsField().getAttribute("value");
    }
    /**
     * Returns description.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getDescription() {
        return description;
    }
    /**
     * Returns label.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getLabel() {
        return label;
    }
    /**
     * Change device profile.
     *
     * @param deviceProfileName device profile name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void changeDeviceProfile(String deviceProfileName) {
        clearProfileFieldBtn().click();
        entityFromDropdown(deviceProfileName).click();
    }
    /**
     * Unassigns ed device by right side btn.
     *
     * @param deviceName device name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void unassignedDeviceByRightSideBtn(String deviceName) {
        unassignBtn(deviceName).click();
        warningPopUpYesBtn().click();
    }
    /**
     * Unassigns ed device from details tab.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void unassignedDeviceFromDetailsTab() {
        unassignBtnDetailsTab().click();
        warningPopUpYesBtn().click();
    }
    /**
     * Select devices.
     *
     * @param deviceNames device names
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void selectDevices(String... deviceNames) {
        for (String deviceName : deviceNames) {
            checkBox(deviceName).click();
        }
    }
    /**
     * Assigns selected devices.
     *
     * @param deviceNames device names
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void assignSelectedDevices(String... deviceNames) {
        selectDevices(deviceNames);
        assignMarkedDeviceBtn().click();
    }
    /**
     * Deletes selected devices.
     *
     * @param deviceNames device names
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void deleteSelectedDevices(String... deviceNames) {
        selectDevices(deviceNames);
        deleteSelectedBtn().click();
        warningPopUpYesBtn().click();
    }
    /**
     * Filter device by device profile.
     *
     * @param deviceProfileTitle device profile title ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void filterDeviceByDeviceProfile(String deviceProfileTitle) {
        clearProfileFieldBtn().click();
        entityFromDropdown(deviceProfileTitle).click();
        submitBtn().click();
    }
    /**
     * Filter device by state.
     *
     * @param state state ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void filterDeviceByState(String state) {
        deviceStateSelect().click();
        entityFromDropdown(" " + state + " ").click();
        sleep(2); //wait until the action is counted
        submitBtn().click();
    }
    /**
     * Filter device by device profile and state.
     *
     * @param deviceProfileTitle device profile title ({@link String})
     * @param state state ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void filterDeviceByDeviceProfileAndState(String deviceProfileTitle, String state) {
        clearProfileFieldBtn().click();
        entityFromDropdown(deviceProfileTitle).click();
        deviceStateSelect().click();
        entityFromDropdown(" " + state + " ").click();
        sleep(2); //wait until the action is counted
        submitBtn().click();
    }
    /**
     * Make device public by right side btn.
     *
     * @param deviceName device name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void makeDevicePublicByRightSideBtn(String deviceName) {
        makeDevicePublicBtn(deviceName).click();
        warningPopUpYesBtn().click();
    }
    /**
     * Make device public from details tab.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void makeDevicePublicFromDetailsTab() {
        makeDevicePublicBtnDetailsTab().click();
        warningPopUpYesBtn().click();
    }
    /**
     * Make device private by right side btn.
     *
     * @param deviceName device name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void makeDevicePrivateByRightSideBtn(String deviceName) {
        makeDevicePrivateBtn(deviceName).click();
        warningPopUpYesBtn().click();
    }
    /**
     * Make device private from details tab.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void makeDevicePrivateFromDetailsTab() {
        makeDevicePrivateBtnDetailsTab().click();
        warningPopUpYesBtn().click();
    }
}
