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
import org.openqa.selenium.WebElement;
import org.thingsboard.server.msa.ui.base.AbstractBasePage;


/**

 * Selenium element locators for assign device tab page (modal/tab page fragments for UI tests).

 *

 * <p>Defines CSS/XPath selectors; use with matching *Helper for interactions.

 */


public class AssignDeviceTabElements extends AbstractBasePage {
    public AssignDeviceTabElements(WebDriver driver) {
        super(driver);
    }

    private static final String ASSIGN_ON_CUSTOMER_FIELD = "//input[@formcontrolname='entity']";
    private static final String CUSTOMER_FROM_DROPDOWN = "//div[@role='listbox']/mat-option//span[contains(text(),'%s')]";
    private static final String ASSIGN_BTN = "//button[@type='submit']";
    /**
     * Assigns on customer field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignOnCustomerField() {
        return waitUntilElementToBeClickable(ASSIGN_ON_CUSTOMER_FIELD);
    }
    /**
     * Customer from drop down.
     *
     * @param entityName entity name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customerFromDropDown(String entityName) {
        return waitUntilVisibilityOfElementLocated(String.format(CUSTOMER_FROM_DROPDOWN, entityName));
    }
    /**
     * Assigns btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assignBtn() {
        return waitUntilElementToBeClickable(ASSIGN_BTN);
    }
}
