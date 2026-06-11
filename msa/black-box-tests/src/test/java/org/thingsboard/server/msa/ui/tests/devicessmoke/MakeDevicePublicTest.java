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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.thingsboard.server.msa.ui.pages.CustomerPageHelper;
import org.thingsboard.server.msa.ui.tabs.AssignDeviceTabHelper;
import org.thingsboard.server.msa.ui.utils.EntityPrototypes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.thingsboard.server.msa.ui.base.AbstractBasePage.random;
import static org.thingsboard.server.msa.ui.utils.Const.ENTITY_NAME;
import static org.thingsboard.server.msa.ui.utils.Const.PUBLIC_CUSTOMER_NAME;
/**
 * Black-box test: make device public (TestNG smoke and regression test cases — UI smoke/regression tests).
 */


@Feature("Make device public")
public class MakeDevicePublicTest extends AbstractDeviceTest {

    private CustomerPageHelper customerPage;
    private AssignDeviceTabHelper assignDeviceTab;
    private String deviceName1;
    /**
     * Creates first device.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @BeforeClass
    public void createFirstDevice() {
        customerPage = new CustomerPageHelper(driver);
        assignDeviceTab = new AssignDeviceTabHelper(driver);

        deviceName1 = testRestClient.postDevice("", EntityPrototypes.defaultDevicePrototype(ENTITY_NAME + random())).getName();
    }
    /**
     * Clean up.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @AfterClass
    public void cleanUp() {
        deleteCustomerByName(PUBLIC_CUSTOMER_NAME);
        deleteDeviceByName(deviceName1);
    }
    /**
     * Creates second device.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @BeforeMethod
    public void createSecondDevice() {
        deviceName = testRestClient.postDevice("", EntityPrototypes.defaultDevicePrototype(ENTITY_NAME + random())).getName();
    }
    /**
     * Make device public by right side btn.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test(groups = "smoke", priority = 10)
    @Description("Make device public by right side btn")
    public void makeDevicePublicByRightSideBtn() {
        sideBarMenuView.goToDevicesPage();
        devicePage.makeDevicePublicByRightSideBtn(deviceName);

        assertIsDisplayed(devicePage.deviceIsPublicCheckbox(deviceName));
        assertIsDisplayed(devicePage.deviceCustomerOnPage(deviceName));
        assertThat(devicePage.deviceCustomerOnPage(deviceName).getText())
                .as("Customer in customer column is Public customer")
                .isEqualTo(PUBLIC_CUSTOMER_NAME);

        sideBarMenuView.customerBtn().click();
        customerPage.manageCustomersDevicesBtn(PUBLIC_CUSTOMER_NAME).click();
        assertIsDisplayed(devicePage.device(deviceName));
    }
    /**
     * Make device public from details tab.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test(groups = "smoke", priority = 10)
    @Description("Make device public by btn on details tab")
    public void makeDevicePublicFromDetailsTab() {
        sideBarMenuView.goToDevicesPage();
        devicePage.device(deviceName).click();
        devicePage.makeDevicePublicFromDetailsTab();
        devicePage.closeDeviceDetailsViewBtn().click();

        assertIsDisplayed(devicePage.deviceIsPublicCheckbox(deviceName));
        assertIsDisplayed(devicePage.deviceCustomerOnPage(deviceName));
        assertThat(devicePage.deviceCustomerOnPage(deviceName).getText())
                .as("Customer in customer column is Public customer")
                .isEqualTo(PUBLIC_CUSTOMER_NAME);

        sideBarMenuView.customerBtn().click();
        customerPage.manageCustomersDevicesBtn(PUBLIC_CUSTOMER_NAME).click();
        assertIsDisplayed(devicePage.device(deviceName));
    }
    /**
     * Make device public by assign to public customer.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test(groups = "smoke", priority = 20)
    @Description("Make device public by assign to public customer")
    public void makeDevicePublicByAssignToPublicCustomer() {
        sideBarMenuView.goToDevicesPage();
        devicePage.assignBtn(deviceName).click();
        assignDeviceTab.assignOnCustomer(PUBLIC_CUSTOMER_NAME);
        assertIsDisplayed(devicePage.deviceIsPublicCheckbox(deviceName));
        assertIsDisplayed(devicePage.deviceCustomerOnPage(deviceName));
        assertThat(devicePage.deviceCustomerOnPage(deviceName).getText()).isEqualTo(PUBLIC_CUSTOMER_NAME);

        sideBarMenuView.customerBtn().click();
        customerPage.manageCustomersDevicesBtn(PUBLIC_CUSTOMER_NAME).click();
        assertIsDisplayed(devicePage.device(deviceName));
    }
    /**
     * Make public several devices by assign on public customer.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test(groups = "smoke", priority = 20)
    @Description("Make several devices public by assign to public customer")
    public void makePublicSeveralDevicesByAssignOnPublicCustomer() {
        sideBarMenuView.goToDevicesPage();
        devicePage.assignSelectedDevices(deviceName, deviceName1);
        assignDeviceTab.assignOnCustomer(PUBLIC_CUSTOMER_NAME);
        assertIsDisplayed(devicePage.deviceIsPublicCheckbox(deviceName));
        assertIsDisplayed(devicePage.deviceCustomerOnPage(deviceName));
        assertThat(devicePage.deviceCustomerOnPage(deviceName).getText())
                .as("Customer in customer column is Public customer")
                .isEqualTo(PUBLIC_CUSTOMER_NAME);

        sideBarMenuView.customerBtn().click();
        customerPage.manageCustomersDevicesBtn(PUBLIC_CUSTOMER_NAME).click();
        assertIsDisplayed(devicePage.device(deviceName));
        assertIsDisplayed(devicePage.device(deviceName1));
    }
}
