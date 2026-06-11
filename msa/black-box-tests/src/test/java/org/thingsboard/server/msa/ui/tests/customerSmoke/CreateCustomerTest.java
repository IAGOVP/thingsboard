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
import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.thingsboard.server.msa.ui.base.AbstractDriverBaseTest;
import org.thingsboard.server.msa.ui.pages.CustomerPageHelper;
import org.thingsboard.server.msa.ui.pages.LoginPageHelper;
import org.thingsboard.server.msa.ui.pages.SideBarMenuViewElements;

import static org.thingsboard.server.msa.ui.base.AbstractBasePage.random;
import static org.thingsboard.server.msa.ui.utils.Const.EMPTY_CUSTOMER_MESSAGE;
import static org.thingsboard.server.msa.ui.utils.Const.ENTITY_NAME;
import static org.thingsboard.server.msa.ui.utils.Const.SAME_NAME_WARNING_CUSTOMER_MESSAGE;


/**

 * Black-box test: create customer (TestNG smoke and regression test cases — UI smoke/regression tests).

 */


public class CreateCustomerTest extends AbstractDriverBaseTest {

    private SideBarMenuViewElements sideBarMenuView;
    private CustomerPageHelper customerPage;
    private String customerName;
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
     * Deletes the requested data.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @AfterMethod
    public void delete() {
        if (customerName != null) {
            testRestClient.deleteCustomer(getCustomerByName(customerName).getId());
            customerName = null;
        }
    }
    /**
     * Creates customer.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Customers smoke tests")
    @Feature("Create customer")
    @Test(priority = 10, groups = "smoke")
    @Description("Add customer specifying the name (text/numbers /special characters)")
    public void createCustomer() {
        String customerName = ENTITY_NAME + random();

        sideBarMenuView.customerBtn().click();
        customerPage.plusBtn().click();
        customerPage.addCustomerViewEnterName(customerName);
        customerPage.addBtnC().click();
        this.customerName = customerName;
        customerPage.refreshBtn().click();

        Assert.assertNotNull(customerPage.customer(customerName));
        Assert.assertTrue(customerPage.customer(customerName).isDisplayed());
    }
    /**
     * Creates customer with full information.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Customers smoke tests")
    @Feature("Create customer")
    @Test(priority = 20, groups = "smoke")
    @Description("Add customer after specifying the name (text/numbers /special characters) with full information")
    public void createCustomerWithFullInformation() {
        String customerName = ENTITY_NAME + random();
        String text = "Text";
        String email = "email@mail.com";
        String number = "12015550123";

        sideBarMenuView.customerBtn().click();
        customerPage.plusBtn().click();
        customerPage.addCustomerViewEnterName(customerName);
        customerPage.selectCountryAddEntityView();
        customerPage.descriptionAddEntityView().sendKeys(text);
        customerPage.cityAddEntityView().sendKeys(text);
        customerPage.stateAddEntityView().sendKeys(text);
        customerPage.zipAddEntityView().sendKeys(text);
        customerPage.addressAddEntityView().sendKeys(text);
        customerPage.address2AddEntityView().sendKeys(text);
        customerPage.phoneNumberAddEntityView().sendKeys(number);
        customerPage.emailAddEntityView().sendKeys(email);
        customerPage.addBtnC().click();
        this.customerName = customerName;
        customerPage.setCustomerEmail(customerName);
        customerPage.setCustomerCountry(customerName);
        customerPage.setCustomerCity(customerName);
        customerPage.entity(customerName).click();

        Assert.assertNotNull(customerPage.customer(customerName));
        Assert.assertEquals(customerPage.entityViewTitle().getText(), customerName);
        Assert.assertEquals(customerPage.titleFieldEntityView().getAttribute("value"), customerName);
        Assert.assertEquals(customerPage.countrySelectMenuEntityView().getText(), customerPage.getCountry());
        Assert.assertEquals(customerPage.descriptionEntityView().getAttribute("value"), text);
        Assert.assertEquals(customerPage.cityEntityView().getAttribute("value"), text);
        Assert.assertEquals(customerPage.stateEntityView().getAttribute("value"), text);
        Assert.assertEquals(customerPage.zipEntityView().getAttribute("value"), text);
        Assert.assertEquals(customerPage.addressEntityView().getAttribute("value"), text);
        Assert.assertEquals(customerPage.address2EntityView().getAttribute("value"), text);
        Assert.assertEquals(customerPage.phoneNumberEntityView().getAttribute("value"), "+" + number);
        Assert.assertEquals(customerPage.emailEntityView().getAttribute("value"), email);
        Assert.assertEquals(customerPage.getCustomerEmail(), email);
        Assert.assertEquals(customerPage.getCustomerCountry(), customerPage.getCountry());
        Assert.assertEquals(customerPage.getCustomerCity(), text);
    }
    /**
     * Creates customer without name.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Customers smoke tests")
    @Feature("Create customer")
    @Test(priority = 20, groups = "smoke")
    @Description("Add customer without the name")
    public void createCustomerWithoutName() {
        sideBarMenuView.customerBtn().click();
        customerPage.plusBtn().click();

        Assert.assertFalse(customerPage.addBtnV().isEnabled());
    }
    /**
     * Creates customer with only space.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Customers smoke tests")
    @Feature("Create customer")
    @Test(priority = 20, groups = "smoke")
    @Description("Create customer only with spase in name")
    public void createCustomerWithOnlySpace() {
        sideBarMenuView.customerBtn().click();
        customerPage.plusBtn().click();
        customerPage.addCustomerViewEnterName(Keys.SPACE);
        customerPage.addBtnC().click();

        Assert.assertNotNull(customerPage.warningMessage());
        Assert.assertTrue(customerPage.warningMessage().isDisplayed());
        Assert.assertEquals(customerPage.warningMessage().getText(), EMPTY_CUSTOMER_MESSAGE);
        Assert.assertNotNull(customerPage.addEntityView());
        Assert.assertTrue(customerPage.addEntityView().isDisplayed());
    }
    /**
     * Creates customer same name.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Customers smoke tests")
    @Feature("Create customer")
    @Test(priority = 20, groups = "smoke")
    @Description("Create a customer with the same name")
    public void createCustomerSameName() {
        sideBarMenuView.customerBtn().click();
        customerPage.setCustomerName();
        String customerName = customerPage.getCustomerName();
        customerPage.plusBtn().click();
        customerPage.addCustomerViewEnterName(customerName);
        customerPage.addBtnC().click();

        Assert.assertNotNull(customerPage.warningMessage());
        Assert.assertTrue(customerPage.warningMessage().isDisplayed());
        Assert.assertEquals(customerPage.warningMessage().getText(), SAME_NAME_WARNING_CUSTOMER_MESSAGE);
        Assert.assertNotNull(customerPage.addEntityView());
        Assert.assertTrue(customerPage.addEntityView().isDisplayed());
    }
    /**
     * Creates customer without refresh.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Customers smoke tests")
    @Feature("Create customer")
    @Test(priority = 20, groups = "smoke")
    @Description("Add customer specifying the name (text/numbers /special characters) without refresh")
    public void createCustomerWithoutRefresh() {
        String customerName = ENTITY_NAME + random();

        sideBarMenuView.customerBtn().click();
        customerPage.plusBtn().click();
        customerPage.addCustomerViewEnterName(customerName);
        customerPage.addBtnC().click();
        this.customerName = customerName;

        Assert.assertNotNull(customerPage.customer(customerName));
        Assert.assertTrue(customerPage.customer(customerName).isDisplayed());
    }
    /**
     * Documentation.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Customers smoke tests")
    @Feature("Create customer")
    @Test(priority = 40, groups = "smoke")
    @Description("Go to customer documentation page")
    public void documentation() {
        String urlPath = "docs/user-guide/ui/customers/";

        sideBarMenuView.customerBtn().click();
        customerPage.setCustomerName();
        customerPage.customer(customerPage.getCustomerName()).click();
        customerPage.goToHelpPage();

        Assert.assertTrue(urlContains(urlPath), "URL contains " + urlPath);
    }
    /**
     * Creates customer add and remove phone number.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Customers smoke tests")
    @Feature("Create customer")
    @Test(groups = "smoke")
    @Description("Go to customer documentation page")
    public void createCustomerAddAndRemovePhoneNumber() {
        String customerName = ENTITY_NAME;
        String number = "12015550123";

        sideBarMenuView.customerBtn().click();
        customerPage.plusBtn().click();
        customerPage.addCustomerViewEnterName(customerName);
        customerPage.enterText(customerPage.phoneNumberAddEntityView(), number);
        customerPage.clearInputField(customerPage.phoneNumberAddEntityView());
        customerPage.addBtnC().click();
        this.customerName = customerName;
        customerPage.entity(customerName).click();

        Assert.assertTrue(customerPage.phoneNumberEntityView().getAttribute("value").isEmpty(), "Phone field is empty");
    }
}
