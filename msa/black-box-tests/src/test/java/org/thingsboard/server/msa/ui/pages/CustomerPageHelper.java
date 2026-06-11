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

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
/**
 * Page object helper for customer page UI actions (page object element locators and helpers — Selenium page objects).
 */


@Slf4j
public class CustomerPageHelper extends CustomerPageElements {
    public CustomerPageHelper(WebDriver driver) {
        super(driver);
    }

    private String customerName;
    private String country;
    private String dashboard;
    private String dashboardFromView;
    private String description;
    private String customerEmail;
    private String customerCountry;
    private String customerCity;
    /**
     * Set customer name.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setCustomerName() {
        this.customerName = entityTitles().get(0).getText();
    }
    /**
     * Set customer name.
     *
     * @param number number
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setCustomerName(int number) {
        this.customerName = entityTitles().get(number).getText();
    }
    /**
     * Returns customer name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getCustomerName() {
        return customerName;
    }
    /**
     * Set country.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setCountry() {
        this.country = countries().get(0).getText();
    }
    /**
     * Returns country.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getCountry() {
        return country;
    }
    /**
     * Set dashboard.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setDashboard() {
        this.dashboard = listOfEntity().get(0).getText();
    }
    /**
     * Set dashboard from view.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setDashboardFromView() {
        this.dashboardFromView = editMenuDashboardField().getAttribute("value");
    }
    /**
     * Set description.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setDescription() {
        scrollToElement(descriptionEntityView());
        this.description = descriptionEntityView().getAttribute("value");
    }
    /**
     * Returns dashboard.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getDashboard() {
        return dashboard;
    }
    /**
     * Returns dashboard from view.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getDashboardFromView() {
        return dashboardFromView;
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
     * Set customer email.
     *
     * @param title title ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setCustomerEmail(String title) {
        this.customerEmail = email(title).getText();
    }
    /**
     * Returns customer email.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getCustomerEmail() {
        return customerEmail;
    }
    /**
     * Set customer country.
     *
     * @param title title ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setCustomerCountry(String title) {
        this.customerCountry = country(title).getText();
    }
    /**
     * Returns customer country.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getCustomerCountry() {
        return customerCountry;
    }
    /**
     * Set customer city.
     *
     * @param title title ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setCustomerCity(String title) {
        this.customerCity = city(title).getText();
    }
    /**
     * Returns customer city.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getCustomerCity() {
        return customerCity;
    }
    /**
     * Change title edit menu.
     *
     * @param newTitle new title ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void changeTitleEditMenu(String newTitle) {
        titleFieldEntityView().click();
        titleFieldEntityView().clear();
        wait.until(ExpectedConditions.textToBe(By.xpath(String.format(INPUT_FIELD, INPUT_FIELD_NAME_TITLE)), ""));
        titleFieldEntityView().sendKeys(newTitle);
    }
    /**
     * Choose dashboard.
     *
     * @param dashboardName dashboard name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void chooseDashboard(String dashboardName) {
        editMenuDashboardField().click();
        editMenuDashboard(dashboardName).click();
    }
    /**
     * Creates customers user.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void createCustomersUser() {
        plusBtn().click();
        addUserEmailField().click();
        addUserEmailField().sendKeys(getRandomNumber() + "@gmail.com");
        addBtnC().click();
        activateWindowOkBtn().click();
    }
    /**
     * Select country entity view.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void selectCountryEntityView() {
        countrySelectMenuEntityView().click();
        setCountry();
        countries().get(0).click();
    }
    /**
     * Select country add entity view.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void selectCountryAddEntityView() {
        countrySelectMenuAddEntityView().click();
        setCountry();
        countries().get(0).click();
    }
    /**
     * Assigns ed dashboard.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void assignedDashboard() {
        plusBtn().click();
        assignedField().click();
        setDashboard();
        listOfEntity().get(0).click();
        assignedField().sendKeys(Keys.ESCAPE);
        submitAssignedBtn().click();
    }
    /**
     * Assigns ed dashboard.
     *
     * @param dashboardName dashboard name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void assignedDashboard(String dashboardName) {
        plusBtn().click();
        assignedField().click();
        entityFromList(dashboardName).click();
        assignedField().sendKeys(Keys.ESCAPE);
        submitAssignedBtn().click();
    }
    /**
     * Customer is not present.
     *
     * @param title title ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean customerIsNotPresent(String title) {
        return elementsIsNotPresent(getEntity(title));
    }
    /**
     * Sort by name down.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void sortByNameDown() {
        doubleClick(sortByTitleBtn());
    }
    /**
     * Add customer view enter name.
     *
     * @param keysToEnter keys to enter ({@link CharSequence})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addCustomerViewEnterName(CharSequence keysToEnter) {
        enterText(titleFieldAddEntityView(), keysToEnter);
    }
    /**
     * Enter phone number.
     *
     * @param number number ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void enterPhoneNumber(String number) {
        phoneNumberEntityView().sendKeys(number);
        phoneNumberEntityView().sendKeys(Keys.TAB);
    }
    /**
     * Open customer alarms.
     *
     * @param customerName customer name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openCustomerAlarms(String customerName) {
        if (!customerDetailsView().isDisplayed()) {
            customer(customerName).click();
        }
        customerDetailsAlarmsBtn().click();
    }
    /**
     * Disable hide home dashboard toolbar.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void disableHideHomeDashboardToolbar() {
        hideHomeDashboardToolbarCheckbox().click();
        waitUntilAttributeToBe("//mat-checkbox[@formcontrolname='homeDashboardHideToolbar']//input", "class", "mdc-checkbox__native-control");
    }
    /**
     * Wait until dashboard field to be not empty.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void waitUntilDashboardFieldToBeNotEmpty() {
        waitUntilAttributeToBeNotEmpty(editMenuDashboardField(), "value");
    }
}
