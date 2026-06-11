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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;


/**

 * Page object helper for profiles page UI actions (page object element locators and helpers — Selenium page objects).

 */


public class ProfilesPageHelper extends ProfilesPageElements {
    public ProfilesPageHelper(WebDriver driver) {
        super(driver);
    }

    private String name;
    private String ruleChain;
    private String mobileDashboard;
    private String queue;
    private String description;
    private String profile;
    /**
     * Set name.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setName() {
        this.name = profileViewNameField().getAttribute("value");
    }
    /**
     * Set rule chain.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setRuleChain() {
        this.ruleChain = profileViewRuleChainField().getAttribute("value");
    }
    /**
     * Set mobile dashboard.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setMobileDashboard() {
        this.mobileDashboard = profileViewMobileDashboardField().getAttribute("value");
    }
    /**
     * Set queue.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setQueue() {
        this.queue = profileViewQueueField().getAttribute("value");
    }
    /**
     * Set description.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setDescription() {
        scrollToElement(profileViewDescriptionField());
        this.description = profileViewDescriptionField().getAttribute("value");
    }
    /**
     * Set profile name.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setProfileName() {
        this.profile = profileNames().get(0).getText();
    }
    /**
     * Set profile name.
     *
     * @param number number
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setProfileName(int number) {
        this.profile = profileNames().get(number).getText();
    }
    /**
     * Returns name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getName() {
        return this.name;
    }
    /**
     * Returns rule chain.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getRuleChain() {
        return this.ruleChain;
    }
    /**
     * Returns mobile dashboard.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getMobileDashboard() {
        return this.mobileDashboard;
    }
    /**
     * Returns queue.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getQueue() {
        return this.queue;
    }
    /**
     * Returns description.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getDescription() {
        return this.description;
    }
    /**
     * Returns profile name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getProfileName() {
        return this.profile;
    }
    /**
     * Creates device profile enter name.
     *
     * @param keysToEnter keys to enter ({@link CharSequence})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void createDeviceProfileEnterName(CharSequence keysToEnter) {
        enterText(addDeviceProfileNameField(), keysToEnter);
    }
    /**
     * Add device profile view choose rule chain.
     *
     * @param ruleChain rule chain ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addDeviceProfileViewChooseRuleChain(String ruleChain) {
        addDeviceProfileRuleChainField().click();
        entityFromList(ruleChain).click();
    }
    /**
     * Add asset profile view choose rule chain.
     *
     * @param ruleChain rule chain ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addAssetProfileViewChooseRuleChain(String ruleChain) {
        addAssetProfileRuleChainField().click();
        entityFromList(ruleChain).click();
    }
    /**
     * Add device profile view choose mobile dashboard.
     *
     * @param mobileDashboard mobile dashboard ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addDeviceProfileViewChooseMobileDashboard(String mobileDashboard) {
        addDeviceProfileMobileDashboardField().click();
        entityFromList(mobileDashboard).click();
    }
    /**
     * Add asset profile view choose mobile dashboard.
     *
     * @param mobileDashboard mobile dashboard ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addAssetProfileViewChooseMobileDashboard(String mobileDashboard) {
        addAssetProfileMobileDashboardField().click();
        entityFromList(mobileDashboard).click();
    }
    /**
     * Add device profile view choose queue.
     *
     * @param queue queue ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addDeviceProfileViewChooseQueue(String queue) {
        addDeviceProfileQueueField().click();
        entityFromList(queue).click();
        waitUntilAttributeContains(addDeviceProfileQueueField(), "aria-expanded", "false");
    }
    /**
     * Add assets profile view choose queue.
     *
     * @param queue queue ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addAssetsProfileViewChooseQueue(String queue) {
        addAssetProfileQueueField().click();
        entityFromList(queue).click();
        waitUntilAttributeContains(addAssetProfileQueueField(), "aria-expanded", "false");
    }
    /**
     * Add device profile view enter description.
     *
     * @param description description ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addDeviceProfileViewEnterDescription(String description) {
        addDeviceDescriptionField().sendKeys(description);
    }
    /**
     * Add asset profile view enter description.
     *
     * @param description description ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addAssetProfileViewEnterDescription(String description) {
        addAssetDescriptionField().sendKeys(description);
    }
    /**
     * Open create device profile view.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openCreateDeviceProfileView() {
        plusBtn().click();
        createNewDeviceProfileBtn().click();
    }
    /**
     * Open create asset profile view.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openCreateAssetProfileView() {
        plusBtn().click();
        createNewAssetProfileBtn().click();
    }
    /**
     * Add asset profile view enter name.
     *
     * @param name name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void addAssetProfileViewEnterName(String name) {
        addAssetProfileNameField().click();
        addAssetProfileNameField().sendKeys(name);
    }
    /**
     * Open import device profile view.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openImportDeviceProfileView() {
        plusBtn().click();
        importDeviceProfileBtn().click();
    }
    /**
     * Open import asset profile view.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openImportAssetProfileView() {
        plusBtn().click();
        importAssetProfileBtn().click();
    }
    /**
     * Deletes device profile from view btn is not displayed.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean deleteDeviceProfileFromViewBtnIsNotDisplayed() {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(getDeviseProfileViewDeleteBtn())));
    }
    /**
     * Deletes asset profile from view btn is not displayed.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean deleteAssetProfileFromViewBtnIsNotDisplayed() {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(getAssetProfileViewDeleteBtn())));
    }
    /**
     * Go to profile help page.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void goToProfileHelpPage() {
        jsClick(helpBtn());
        goToNextTab(2);
    }
    /**
     * Sort by name down.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void sortByNameDown() {
        doubleClick(sortByNameBtn());
    }
    /**
     * Profile is not present.
     *
     * @param name name ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean profileIsNotPresent(String name) {
        return elementsIsNotPresent(getEntity(name));
    }
    /**
     * Checks box is displayed.
     *
     * @param name name ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean checkBoxIsDisplayed(String name) {
        return waitUntilPresenceOfElementLocated(getCheckbox(name)).isDisplayed();
    }
}

