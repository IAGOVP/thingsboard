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
import org.openqa.selenium.WebElement;
import org.thingsboard.server.msa.ui.base.AbstractBasePage;


/**

 * Selenium element locators for create widget popup page (page object element locators and helpers — Selenium page objects).

 *

 * <p>Defines CSS/XPath selectors; use with matching *Helper for interactions.

 */


public class CreateWidgetPopupElements extends AbstractBasePage {
    public CreateWidgetPopupElements(WebDriver driver) {
        super(driver);
    }

    private static final String ENTITY_ALIAS = "//input[@formcontrolname='entityAlias']";
    private static final String CREATE_NEW_ALIAS_BTN = "//a[text() = 'Create a new one!']/parent::span";
    private static final String FILTER_TYPE_FIELD = "//div[contains(@class,'tb-entity-filter')]//mat-select//span";
    private static final String TYPE_FIELD = "//mat-select[@formcontrolname='entityType']//span";
    private static final String OPTION_FROM_DROPDOWN = "//span[text() = ' %s ']";
    private static final String ENTITY_FIELD = "//input[@formcontrolname='entity']";
    private static final String ADD_ALIAS_BTN = "//tb-entity-alias-dialog//span[text() = ' Add ']/parent::button";
    private static final String ADD_WIDGET_BTN = "//tb-add-widget-dialog//span[text() = ' Add ']/parent::button";
    private static final String ENTITY_FROM_DROPDOWN = "//b[text() = '%s']";
    /**
     * Entity alias.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entityAlias() {
        return waitUntilElementToBeClickable(ENTITY_ALIAS);
    }
    /**
     * Creates new alias btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement createNewAliasBtn() {
        return waitUntilElementToBeClickable(CREATE_NEW_ALIAS_BTN);
    }
    /**
     * Filter type filed.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement filterTypeFiled() {
        return waitUntilElementToBeClickable(FILTER_TYPE_FIELD);
    }
    /**
     * Type filed.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement typeFiled() {
        return waitUntilElementToBeClickable(TYPE_FIELD);
    }
    /**
     * Option from dropdown.
     *
     * @param type type ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement optionFromDropdown(String type) {
        return waitUntilElementToBeClickable(String.format(OPTION_FROM_DROPDOWN, type));
    }
    /**
     * Entity filed.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entityFiled() {
        return waitUntilElementToBeClickable(ENTITY_FIELD);
    }
    /**
     * Add alias btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement addAliasBtn() {
        return waitUntilElementToBeClickable(ADD_ALIAS_BTN);
    }
    /**
     * Add widget btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement addWidgetBtn() {
        return waitUntilElementToBeClickable(ADD_WIDGET_BTN);
    }
    /**
     * Entity from dropdown.
     *
     * @param entityName entity name ({@link String})
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entityFromDropdown(String entityName) {
        return waitUntilVisibilityOfElementLocated(String.format(ENTITY_FROM_DROPDOWN, entityName));
    }
}
