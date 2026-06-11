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
package org.thingsboard.server.msa.ui.tests.devicessmoke;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.msa.ui.pages.CustomerPageHelper;
import org.thingsboard.server.msa.ui.utils.EntityPrototypes;

import static org.thingsboard.server.msa.ui.base.AbstractBasePage.random;
import static org.thingsboard.server.msa.ui.utils.Const.ENTITY_NAME;
import static org.thingsboard.server.msa.ui.utils.Const.PUBLIC_CUSTOMER_NAME;
/**
 * Black-box test: make device private (TestNG smoke and regression test cases — UI smoke/regression tests).
 */


@Feature("Make device private")
public class MakeDevicePrivateTest extends AbstractDeviceTest {

    private CustomerPageHelper customerPage;
    /**
     * Creates public device.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @BeforeMethod
    public void createPublicDevice() {
        customerPage = new CustomerPageHelper(driver);
        Device device = testRestClient.postDevice("", EntityPrototypes.defaultDevicePrototype(ENTITY_NAME + random()));
        testRestClient.setDevicePublic(device.getId());
        deviceName = device.getName();
    }
    /**
     * Deletes public customer.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @AfterClass
    public void deletePublicCustomer() {
        deleteCustomerByName(PUBLIC_CUSTOMER_NAME);
    }
    /**
     * Make device private by right side btn.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test(groups = "smoke")
    @Description("Make device private by right side btn")
    public void makeDevicePrivateByRightSideBtn() {
        sideBarMenuView.goToDevicesPage();
        devicePage.makeDevicePrivateByRightSideBtn(deviceName);
        WebElement customerInColumn = devicePage.deviceCustomerOnPage(deviceName);
        assertIsDisplayed(devicePage.deviceIsPrivateCheckbox(deviceName));
        assertInvisibilityOfElement(customerInColumn);

        sideBarMenuView.customerBtn().click();
        customerPage.manageCustomersDevicesBtn(PUBLIC_CUSTOMER_NAME).click();
        devicePage.assertEntityIsNotPresent(deviceName);
    }
    /**
     * Make device private from details tab.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test(groups = "smoke")
    @Description("Make device public by btn on details tab")
    public void makeDevicePrivateFromDetailsTab() {
        sideBarMenuView.goToDevicesPage();
        devicePage.device(deviceName).click();
        WebElement customerInColumn = devicePage.deviceCustomerOnPage(deviceName);
        devicePage.makeDevicePrivateFromDetailsTab();
        devicePage.closeDeviceDetailsViewBtn().click();
        assertIsDisplayed(devicePage.deviceIsPrivateCheckbox(deviceName));
        assertInvisibilityOfElement(customerInColumn);

        sideBarMenuView.customerBtn().click();
        customerPage.manageCustomersDevicesBtn(PUBLIC_CUSTOMER_NAME).click();
        devicePage.assertEntityIsNotPresent(deviceName);
    }
}
