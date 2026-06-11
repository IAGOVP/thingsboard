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
package org.thingsboard.server.msa.ui.tabs;

import org.openqa.selenium.WebDriver;


/**

 * Page object helper for create device tab UI actions (modal/tab page fragments for UI tests).

 */


public class CreateDeviceTabHelper extends CreateDeviceTabElements {
    public CreateDeviceTabHelper(WebDriver driver) {
        super(driver);
    }
    /**
     * Enter name.
     *
     * @param deviceName device name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void enterName(String deviceName) {
        enterText(nameField(), deviceName);
    }
    /**
     * Creates new device profile.
     *
     * @param deviceProfileTitle device profile title ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void createNewDeviceProfile(String deviceProfileTitle) {
        if (!createNewDeviceProfileRadioBtn().getAttribute("class").contains("checked")) {
            createNewDeviceProfileRadioBtn().click();
        }
        deviceProfileTitleField().sendKeys(deviceProfileTitle);
    }
    /**
     * Change device profile.
     *
     * @param deviceProfileName device profile name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void changeDeviceProfile(String deviceProfileName) {
        if (!selectExistingDeviceProfileRadioBtn().getAttribute("class").contains("checked")) {
            selectExistingDeviceProfileRadioBtn().click();
        }
        clearProfileFieldBtn().click();
        entityFromDropdown(deviceProfileName).click();
    }
    /**
     * Assigns on customer.
     *
     * @param customerTitle customer title ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void assignOnCustomer(String customerTitle) {
        customerOptionBtn().click();
        assignOnCustomerField().click();
        customerFromDropDown(customerTitle).click();
        sleep(2); //waiting for the action to count
    }
    /**
     * Enter label.
     *
     * @param label label ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void enterLabel(String label) {
        enterText(deviceLabelField(), label);
    }
    /**
     * Enter description.
     *
     * @param description description ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void enterDescription(String description) {
        enterText(descriptionField(), description);
    }
}
