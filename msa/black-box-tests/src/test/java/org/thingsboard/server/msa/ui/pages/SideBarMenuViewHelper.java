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


/**

 * Page object helper for side bar menu view UI actions (page object element locators and helpers — Selenium page objects).

 */


public class SideBarMenuViewHelper extends SideBarMenuViewElements {
    public SideBarMenuViewHelper(WebDriver driver) {
        super(driver);
    }
    /**
     * Open device profiles.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openDeviceProfiles() {
        openProfilesDropDown();
        deviceProfileBtn().click();
    }
    /**
     * Open asset profiles.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openAssetProfiles() {
        openProfilesDropDown();
        assetProfileBtn().click();
    }
    /**
     * Go to devices page.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void goToDevicesPage() {
        openEntitiesDropdown();
        devicesBtn().click();
    }
    /**
     * Go to assets page.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void goToAssetsPage() {
        openEntitiesDropdown();
        assetsBtn().click();
    }
    /**
     * Go to entity views page.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void goToEntityViewsPage() {
        openEntitiesDropdown();
        entityViewsBtn().click();
    }
    /**
     * Open entities dropdown.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openEntitiesDropdown() {
        if (entitiesDropdownIsClose()) {
            entitiesDropdown().click();
        }
    }
    /**
     * Open profiles drop down.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void openProfilesDropDown() {
        if (profilesIsClose()) {
            profilesDropdown().click();
        }
    }
    /**
     * Entities dropdown is close.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean entitiesDropdownIsClose() {
        return dropdownIsClose(entitiesDropdown());
    }
    /**
     * Profiles is close.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    public boolean profilesIsClose() {
        return dropdownIsClose(profilesDropdown());
    }

    private boolean dropdownIsClose(WebElement dropdown) {
        return !dropdown.getAttribute("class").contains("tb-toggled");
    }
}