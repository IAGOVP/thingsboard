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

 * Page object helper for open rule chain page UI actions (page object element locators and helpers — Selenium page objects).

 */


public class OpenRuleChainPageHelper extends OpenRuleChainPageElements {
    public OpenRuleChainPageHelper(WebDriver driver) {
        super(driver);
    }

    private String headName;
    /**
     * Set head name.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void setHeadName() {
        this.headName = headRuleChainName().getText().split(" ")[1];
    }
    /**
     * Returns head name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    public String getHeadName() {
        return headName;
    }
    /**
     * Wait until btn disable.
     *
     * @param element element ({@link WebElement})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void waitUntilBtnDisable(WebElement element) {
        waitUntilAttributeContains(element, "disabled", "true");
    }
}
