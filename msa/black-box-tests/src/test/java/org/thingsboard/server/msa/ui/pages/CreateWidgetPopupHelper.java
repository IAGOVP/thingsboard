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

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;


/**

 * Page object helper for create widget popup UI actions (page object element locators and helpers — Selenium page objects).

 */


public class CreateWidgetPopupHelper extends CreateWidgetPopupElements {
    public CreateWidgetPopupHelper(WebDriver driver) {
        super(driver);
    }
    /**
     * Go to create entity alias popup.
     *
     * @param aliasName alias name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void goToCreateEntityAliasPopup(String aliasName) {
        entityAlias().sendKeys(aliasName + RandomStringUtils.randomAlphanumeric(7));
        createNewAliasBtn().click();
    }
    /**
     * Select filter type.
     *
     * @param filterType filter type ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void selectFilterType(String filterType) {
        filterTypeFiled().click();
        optionFromDropdown(filterType).click();
    }
    /**
     * Select type.
     *
     * @param entityType entity type ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void selectType(String entityType) {
        typeFiled().click();
        optionFromDropdown(entityType).click();
    }
    /**
     * Select entity.
     *
     * @param entityName entity name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void selectEntity(String entityName) {
        entityFiled().sendKeys(entityName);
        entityFromDropdown(entityName);
        entityFiled().sendKeys(Keys.ARROW_DOWN);
        entityFiled().sendKeys(Keys.ENTER);
    }
}
