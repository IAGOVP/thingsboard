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

 * Page object helper for other page elements UI actions (page object element locators and helpers — Selenium page objects).

 */


public class OtherPageElementsHelper extends OtherPageElements {
    public OtherPageElementsHelper(WebDriver driver) {
        super(driver);
    }

    private String headerName;
    /**
     * Set header name.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setHeaderName() {
        this.headerName = headerNameView().getText();
    }
    /**
     * Returns header name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getHeaderName() {
        return headerName;
    }
    /**
     * Assert entity is not present.
     *
     * @param entityName entity name ({@link String})
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean assertEntityIsNotPresent(String entityName) {
        return elementIsNotPresent(getEntity(entityName));
    }
    /**
     * Go to help page.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void goToHelpPage() {
        helpBtn().click();
        goToNextTab(2);
    }
    /**
     * Click on check boxes.
     *
     * @param count count
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void clickOnCheckBoxes(int count) {
        for (int i = 0; i < count; i++) {
            checkBoxes().get(i).click();
        }
    }
    /**
     * Change name edit menu.
     *
     * @param keysToSend keys to send ({@link CharSequence})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void changeNameEditMenu(CharSequence keysToSend) {
        nameFieldEditMenu().click();
        nameFieldEditMenu().clear();
        nameFieldEditMenu().sendKeys(keysToSend);
    }
    /**
     * Change description.
     *
     * @param newDescription new description ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void changeDescription(String newDescription) {
        descriptionEntityView().click();
        descriptionEntityView().clear();
        descriptionEntityView().sendKeys(newDescription);
    }
    /**
     * Deletes rule chain trash.
     *
     * @param entityName entity name ({@link String})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String deleteRuleChainTrash(String entityName) {
        deleteBtn(entityName).click();
        warningPopUpYesBtn().click();
        return entityName;
    }
    /**
     * Deletes selected.
     *
     * @param entityName entity name ({@link String})
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String deleteSelected(String entityName) {
        checkBox(entityName).click();
        jsClick(deleteSelectedBtn());
        warningPopUpYesBtn().click();
        return entityName;
    }
    /**
     * Deletes selected.
     *
     * @param countOfCheckBoxes count of check boxes
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void deleteSelected(int countOfCheckBoxes) {
        clickOnCheckBoxes(countOfCheckBoxes);
        jsClick(deleteSelectedBtn());
        warningPopUpYesBtn().click();
    }
    /**
     * Search entity.
     *
     * @param namePath name path ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void searchEntity(String namePath) {
        searchBtn().click();
        searchField().sendKeys(namePath);
        sleep(0.5);
    }
}

