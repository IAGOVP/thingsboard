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

 * Selenium element locators for side bar menu view page (page object element locators and helpers — Selenium page objects).

 *

 * <p>Defines CSS/XPath selectors; use with matching *Helper for interactions.

 */


public class SideBarMenuViewElements extends AbstractBasePage {
    public SideBarMenuViewElements(WebDriver driver) {
        super(driver);
    }

    private static final String RULE_CHAINS_BTN = "//mat-toolbar//a[@href='/ruleChains']";
    private static final String CUSTOMER_BTN = "//mat-toolbar//a[@href='/customers']";
    private static final String DASHBOARD_BTN = "//mat-toolbar//a[@href='/dashboards']";
    private static final String PROFILES_DROPDOWN = "//mat-toolbar//mat-icon[text()='badge']/ancestor::a//span[contains(@class,'pull-right')]";
    private static final String DEVICE_PROFILE_BTN = "//mat-toolbar//a[@href='/profiles/deviceProfiles']";
    private static final String ASSET_PROFILE_BTN = "//mat-toolbar//a[@href='/profiles/assetProfiles']";
    private static final String ALARMS_BTN = "//mat-toolbar//a[@href='/alarms']";
    private static final String ENTITIES_DROPDOWN = "//mat-toolbar//mat-icon[text()='category']/ancestor::a//span[contains(@class,'pull-right')]";
    private static final String DEVICES_BTN = "//ul[@id='docs-menu-entity.entities']//span[text()='Devices']";
    private static final String ASSETS_BTN = "//ul[@id='docs-menu-entity.entities']//span[text()='Assets']";
    private static final String ENTITY_VIEWS_BTN = "//ul[@id='docs-menu-entity.entities']//span[text()='Entity Views']";
    /**
     * Entities dropdown.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entitiesDropdown() {
        return waitUntilElementToBeClickable(ENTITIES_DROPDOWN);
    }
    /**
     * Rule chains btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement ruleChainsBtn() {
        return waitUntilElementToBeClickable(RULE_CHAINS_BTN);
    }
    /**
     * Customer btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement customerBtn() {
        return waitUntilElementToBeClickable(CUSTOMER_BTN);
    }
    /**
     * Dashboard btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement dashboardBtn() {
        return waitUntilElementToBeClickable(DASHBOARD_BTN);
    }
    /**
     * Profiles dropdown.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement profilesDropdown() {
        return waitUntilElementToBeClickable(PROFILES_DROPDOWN);
    }
    /**
     * Device profile btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement deviceProfileBtn() {
        return waitUntilElementToBeClickable(DEVICE_PROFILE_BTN);
    }
    /**
     * Asset profile btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assetProfileBtn() {
        return waitUntilElementToBeClickable(ASSET_PROFILE_BTN);
    }
    /**
     * Alarms btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement alarmsBtn() {
        return waitUntilElementToBeClickable(ALARMS_BTN);
    }
    /**
     * Devices btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement devicesBtn() {
        return waitUntilElementToBeClickable(DEVICES_BTN);
    }
    /**
     * Assets btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement assetsBtn() {
        return waitUntilElementToBeClickable(ASSETS_BTN);
    }
    /**
     * Entity views btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement entityViewsBtn() {
        return waitUntilElementToBeClickable(ENTITY_VIEWS_BTN);
    }
}