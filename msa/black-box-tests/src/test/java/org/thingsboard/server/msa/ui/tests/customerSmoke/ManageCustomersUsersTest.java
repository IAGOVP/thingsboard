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
package org.thingsboard.server.msa.ui.tests.customerSmoke;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.thingsboard.server.msa.ui.base.AbstractDriverBaseTest;
import org.thingsboard.server.msa.ui.pages.CustomerPageHelper;
import org.thingsboard.server.msa.ui.pages.LoginPageHelper;
import org.thingsboard.server.msa.ui.pages.SideBarMenuViewElements;


/**

 * Black-box test: manage customers users (TestNG smoke and regression test cases — UI smoke/regression tests).

 */


public class ManageCustomersUsersTest extends AbstractDriverBaseTest {

    private SideBarMenuViewElements sideBarMenuView;
    private CustomerPageHelper customerPage;
    private final String iconText = "Customer Users";
    /**
     * Fills credentials and submits the login form.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @BeforeClass
    public void login() {
        new LoginPageHelper(driver).authorizationTenant();
        sideBarMenuView = new SideBarMenuViewElements(driver);
        customerPage = new CustomerPageHelper(driver);
    }
    /**
     * Open window by right corner btn.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Customers smoke tests")
    @Feature("Manage customer users")
    @Test(groups = "smoke")
    @Description("Open manage window by right corner btn")
    public void openWindowByRightCornerBtn() {
        sideBarMenuView.customerBtn().click();
        customerPage.setCustomerName();
        jsClick(customerPage.manageCustomersUserBtn(customerPage.getCustomerName()));

        Assert.assertTrue(urlContains("user"));
        Assert.assertNotNull(customerPage.customerUserIconHeader());
        Assert.assertTrue(customerPage.customerUserIconHeader().isDisplayed());
        Assert.assertTrue(customerPage.customerManageWindowIconHead().getText().contains(iconText));
    }
    /**
     * Open window by view.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Customers smoke tests")
    @Feature("Manage customer users")
    @Test(groups = "smoke")
    @Description("Open manage window by btn in entity view")
    public void openWindowByView() {
        sideBarMenuView.customerBtn().click();
        customerPage.setCustomerName();
        customerPage.entity(customerPage.getCustomerName()).click();
        jsClick(customerPage.manageCustomersUserBtnView());

        Assert.assertTrue(urlContains("user"));
        Assert.assertNotNull(customerPage.customerUserIconHeader());
        Assert.assertTrue(customerPage.customerUserIconHeader().isDisplayed());
        Assert.assertTrue(customerPage.customerManageWindowIconHead().getText().contains(iconText));
    }
}
