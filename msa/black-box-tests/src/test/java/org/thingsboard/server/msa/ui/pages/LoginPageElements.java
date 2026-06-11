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

 * Selenium element locators for login page page (page object element locators and helpers — Selenium page objects).

 *

 * <p>Defines CSS/XPath selectors; use with matching *Helper for interactions.

 */


public class LoginPageElements extends AbstractBasePage {
    public LoginPageElements(WebDriver driver) {
        super(driver);
    }

    private static final String EMAIL_FIELD = "//input[@id='username-input']";
    private static final String PASSWORD_FIELD = "//input[@id='password-input']";
    private static final String SUBMIT_BTN = "//button[@type='submit']";
    private static final String TITLE_LOGO = "//img[@class='tb-logo-title']";
    /**
     * Email field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement emailField() {
        return waitUntilElementToBeClickable(EMAIL_FIELD);
    }
    /**
     * Password field.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement passwordField() {
        return waitUntilElementToBeClickable(PASSWORD_FIELD);
    }
    /**
     * Submit btn.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement submitBtn() {
        return waitUntilElementToBeClickable(SUBMIT_BTN);
    }
    /**
     * Title logo.
     *
     * @return {@link WebElement}
     * @throws Exception if an unexpected error occurs during processing
     */

    public WebElement titleLogo() {
        return waitUntilVisibilityOfElementLocated(TITLE_LOGO);
    }

}
