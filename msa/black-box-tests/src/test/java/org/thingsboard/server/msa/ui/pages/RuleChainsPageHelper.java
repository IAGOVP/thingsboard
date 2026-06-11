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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
/**
 * Page object helper for rule chains page UI actions (page object element locators and helpers — Selenium page objects).
 */


@Slf4j
public class RuleChainsPageHelper extends RuleChainsPageElements {
    public RuleChainsPageHelper(WebDriver driver) {
        super(driver);
    }
    /**
     * Open create rule chain view.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openCreateRuleChainView() {
        plusBtn().click();
        createRuleChainBtn().click();
    }
    /**
     * Open import rule chain view.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openImportRuleChainView() {
        plusBtn().click();
        importRuleChainBtn().click();
    }

    private int getRandomNumberFromRuleChainsCount() {
        Random random = new Random();
        return random.nextInt(notRootRuleChainsNames().size());
    }

    private String ruleChainName;
    private String description;
    /**
     * Set rule chain name without root.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setRuleChainNameWithoutRoot() {
        this.ruleChainName = notRootRuleChainsNames().get(getRandomNumberFromRuleChainsCount()).getText();
    }
    /**
     * Set rule chain name without root.
     *
     * @param number number
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setRuleChainNameWithoutRoot(int number) {
        this.ruleChainName = notRootRuleChainsNames().get(number).getText();
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
     * Set rule chain name.
     *
     * @param number number
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setRuleChainName(int number) {
        this.ruleChainName = allNames().get(number).getText();
    }
    /**
     * Returns rule chain name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getRuleChainName() {
        return this.ruleChainName;
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
     * Deletes rule chain from view.
     *
     * @param ruleChainName rule chain name ({@link String})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String deleteRuleChainFromView(String ruleChainName) {
        String s = "";
        if (deleteBtnFromView() != null) {
            deleteBtnFromView().click();
            warningPopUpYesBtn().click();
            if (elementIsNotPresent(getWarningMessage())) {
                return getEntity(ruleChainName);
            }
        } else {
            for (int i = 0; i < notRootRuleChainsNames().size(); i++) {
                notRootRuleChainsNames().get(i).click();
                if (deleteBtnFromView() != null) {
                    deleteBtnFromView().click();
                    warningPopUpYesBtn().click();
                    if (elementIsNotPresent(getWarningMessage())) {
                        s = notRootRuleChainsNames().get(i).getText();
                        break;
                    }
                }
            }
        }
        return s;
    }
    /**
     * Assert check box is not displayed.
     *
     * @param entityName entity name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void assertCheckBoxIsNotDisplayed(String entityName) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//mat-checkbox)[2]")));
        Assert.assertFalse(driver.findElement(By.xpath(getCheckbox(entityName))).isDisplayed());
    }
    /**
     * Deletes btn in root rule chain is not displayed.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean deleteBtnInRootRuleChainIsNotDisplayed() {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(getDeleteRuleChainFromViewBtn())));
    }
    /**
     * Assert rule chains is not present.
     *
     * @param ruleChainName rule chain name ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean assertRuleChainsIsNotPresent(String ruleChainName) {
        return elementsIsNotPresent(getEntity(ruleChainName));
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

    ArrayList<String> sort;
    /**
     * Set sort.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setSort() {
        ArrayList<String> createdTime = new ArrayList<>();
        createdTime().forEach(x -> createdTime.add(x.getText()));
        Collections.sort(createdTime);
        sort = createdTime;
    }
    /**
     * Returns sort.
     *
     * @return {@link ArrayList}
     * @throws Exception if an unexpected error occurs during processing
     */

    public ArrayList<String> getSort() {
        return sort;
    }
}
