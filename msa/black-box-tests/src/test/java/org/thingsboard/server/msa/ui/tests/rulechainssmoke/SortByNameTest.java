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
package org.thingsboard.server.msa.ui.tests.rulechainssmoke;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;
import org.thingsboard.server.msa.ui.utils.DataProviderCredential;

import static org.assertj.core.api.Assertions.assertThat;
import static org.thingsboard.server.msa.ui.utils.EntityPrototypes.defaultRuleChainPrototype;
/**
 * Black-box test: sort by name (TestNG smoke and regression test cases — UI smoke/regression tests).
 */


@Feature("Sort rule chain by name")
public class SortByNameTest extends AbstractRuleChainTest {
    /**
     * Special character up.
     *
     * @param name name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test(priority = 10, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForSort")
    @Description("Sort rule chain 'UP'")
    public void specialCharacterUp(String name) {
        ruleChainName = name;
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainName));

        sideBarMenuView.ruleChainsBtn().click();
        ruleChainsPage.sortByNameBtn().click();
        ruleChainsPage.setRuleChainName(0);

        assertThat(ruleChainsPage.getRuleChainName()).as("First rule chain after sort").isEqualTo(ruleChainName);
    }
    /**
     * All sort up.
     *
     * @param ruleChain rule chain ({@link String})
     * @param ruleChainSymbol rule chain symbol ({@link String})
     * @param ruleChainNumber rule chain number ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Rule chains smoke tests")
    @Feature("Sort rule chain by name")
    @Test(priority = 20, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForAllSort")
    @Description("Sort rule chain 'UP'")
    public void allSortUp(String ruleChain, String ruleChainSymbol, String ruleChainNumber) {
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainSymbol));
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChain));
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainNumber));

        sideBarMenuView.ruleChainsBtn().click();
        ruleChainsPage.sortByNameBtn().click();
        ruleChainsPage.setRuleChainName(0);
        String firstRuleChain = ruleChainsPage.getRuleChainName();
        ruleChainsPage.setRuleChainName(1);
        String secondRuleChain = ruleChainsPage.getRuleChainName();
        ruleChainsPage.setRuleChainName(2);
        String thirdRuleChain = ruleChainsPage.getRuleChainName();

        deleteRuleChainByName(ruleChain);
        deleteRuleChainByName(ruleChainNumber);
        deleteRuleChainByName(ruleChainSymbol);

        assertThat(firstRuleChain).as("First rule chain with symbol in name").isEqualTo(ruleChainSymbol);
        assertThat(secondRuleChain).as("Second rule chain with number in name").isEqualTo(ruleChainNumber);
        assertThat(thirdRuleChain).as("Third rule chain with number in name").isEqualTo(ruleChain);
    }
    /**
     * Special character down.
     *
     * @param ruleChainName rule chain name ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Rule chains smoke tests")
    @Feature("Sort rule chain by name")
    @Test(priority = 10, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForSort")
    @Description("Sort rule chain 'DOWN'")
    public void specialCharacterDown(String ruleChainName) {
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainName));
        this.ruleChainName = ruleChainName;

        sideBarMenuView.ruleChainsBtn().click();
        ruleChainsPage.sortByNameDown();
        ruleChainsPage.setRuleChainName(ruleChainsPage.allNames().size() - 1);

        assertThat(ruleChainsPage.getRuleChainName()).as("Last rule chain after sort").isEqualTo(ruleChainName);
    }
    /**
     * All sort down.
     *
     * @param ruleChain rule chain ({@link String})
     * @param ruleChainSymbol rule chain symbol ({@link String})
     * @param ruleChainNumber rule chain number ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Epic("Rule chains smoke tests")
    @Feature("Sort rule chain by name")
    @Test(priority = 20, groups = "smoke", dataProviderClass = DataProviderCredential.class, dataProvider = "nameForAllSort")
    @Description("Sort rule chain 'DOWN'")
    public void allSortDown(String ruleChain, String ruleChainSymbol, String ruleChainNumber) {
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainSymbol));
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChain));
        testRestClient.postRuleChain(defaultRuleChainPrototype(ruleChainNumber));

        sideBarMenuView.ruleChainsBtn().click();
        int lastIndex = ruleChainsPage.allNames().size() - 1;
        ruleChainsPage.sortByNameDown();
        ruleChainsPage.setRuleChainName(lastIndex);
        String firstRuleChain = ruleChainsPage.getRuleChainName();
        ruleChainsPage.setRuleChainName(lastIndex - 1);
        String secondRuleChain = ruleChainsPage.getRuleChainName();
        ruleChainsPage.setRuleChainName(lastIndex - 2);
        String thirdRuleChain = ruleChainsPage.getRuleChainName();

        deleteRuleChainByName(ruleChain);
        deleteRuleChainByName(ruleChainNumber);
        deleteRuleChainByName(ruleChainSymbol);

        assertThat(firstRuleChain).as("First from the end rule chain with symbol in name").isEqualTo(ruleChainSymbol);
        assertThat(secondRuleChain).as("Second from the end rule chain with number in name").isEqualTo(ruleChainNumber);
        assertThat(thirdRuleChain).as("Third rule from the end chain with number in name").isEqualTo(ruleChain);
    }
}
