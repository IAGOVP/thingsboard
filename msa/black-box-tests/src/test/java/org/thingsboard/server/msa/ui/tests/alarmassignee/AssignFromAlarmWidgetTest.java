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
package org.thingsboard.server.msa.ui.tests.alarmassignee;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.msa.ui.pages.AlarmWidgetElements;
import org.thingsboard.server.msa.ui.pages.CreateWidgetPopupHelper;
import org.thingsboard.server.msa.ui.pages.DashboardPageHelper;
import org.thingsboard.server.msa.ui.utils.Const;
import org.thingsboard.server.msa.ui.utils.EntityPrototypes;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * Black-box test: assign from alarm widget (TestNG smoke and regression test cases — UI smoke/regression tests).
 */


@Feature("Assign from details tab of entity")
public class AssignFromAlarmWidgetTest extends AbstractAssignTest {

    private Dashboard dashboard;
    private DashboardPageHelper dashboardPage;
    private AlarmWidgetElements alarmWidget;
    /**
     * Creates the requested data.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @BeforeClass
    public void create() {
        dashboardPage = new DashboardPageHelper(driver);
        CreateWidgetPopupHelper createWidgetPopup = new CreateWidgetPopupHelper(driver);
        alarmWidget = new AlarmWidgetElements(driver);

        dashboard = testRestClient.postDashboard(EntityPrototypes.defaultDashboardPrototype("Dashboard"));
        sideBarMenuView.dashboardBtn().click();
        dashboardPage.entity(dashboard.getName()).click();
        dashboardPage.editBtn().click();
        dashboardPage.openSelectWidgetsBundleMenu();
        dashboardPage.openCreateWidgetPopup();
        createWidgetPopup.goToCreateEntityAliasPopup("Alias");
        createWidgetPopup.selectFilterType("Single entity");
        createWidgetPopup.selectType("Device");
        createWidgetPopup.selectEntity(deviceName);
        createWidgetPopup.addAliasBtn().click();
        createWidgetPopup.addWidgetBtn().click();
        dashboardPage.increaseSizeOfTheWidget();
        dashboardPage.saveBtn().click();
    }
    /**
     * Deletes the requested data.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @AfterClass
    public void delete() {
        deleteDashboardById(dashboard.getId());
    }
    /**
     * Go to dashboard page.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @BeforeMethod
    public void goToDashboardPage() {
        sideBarMenuView.dashboardBtn().click();
        dashboardPage.entity(dashboard.getName()).click();
    }
    /**
     * Assigns alarm to yourself.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Can assign alarm to yourself")
    @Test
    public void assignAlarmToYourself() {
        alarmWidget.assignAlarmTo(alarmType, Const.TENANT_EMAIL);

        assertIsDisplayed(alarmWidget.assignedUser(Const.TENANT_EMAIL));
    }
    /**
     * Reassign alarm.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Can reassign alarm to another user")
    @Test
    public void reassignAlarm() {
        alarmWidget.assignAlarmTo(assignedAlarmType, userWithNameEmail);

        assertIsDisplayed(alarmWidget.assignedUser(userName));
    }
    /**
     * Unassigns ed alarm.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Can unassign alarm")
    @Test
    public void unassignedAlarm() {
        alarmWidget.unassignedAlarm(assignedAlarmType);

        assertIsDisplayed(alarmWidget.unassigned(assignedAlarmType));
    }
    /**
     * Assigns alarm to yourself from details.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Assign alarm to yourself from details of alarm")
    @Test
    public void assignAlarmToYourselfFromDetails() {
        alarmWidget.alarmDetailsBtn(alarmType).click();
        alarmDetailsView.assignAlarmTo(Const.TENANT_EMAIL);
        alarmDetailsView.closeAlarmDetailsViewBtn().click();

        assertIsDisplayed(alarmWidget.assignedUser(Const.TENANT_EMAIL));
    }
    /**
     * Reassign alarm from details.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Reassign alarm to another user from details of alarm")
    @Test
    public void reassignAlarmFromDetails() {
        alarmWidget.alarmDetailsBtn(assignedAlarmType).click();
        alarmDetailsView.assignAlarmTo(userWithNameEmail);
        alarmDetailsView.closeAlarmDetailsViewBtn().click();

        assertIsDisplayed(alarmWidget.assignedUser(userName));
    }
    /**
     * Unassigns ed alarm from details.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Unassign alarm from details of alarm")
    @Test
    public void unassignedAlarmFromDetails() {
        alarmWidget.alarmDetailsBtn(assignedAlarmType).click();
        alarmDetailsView.unassignedAlarm();
        alarmDetailsView.closeAlarmDetailsViewBtn().click();

        assertIsDisplayed(alarmWidget.unassigned(assignedAlarmType));
    }
    /**
     * Search by email.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Search by email")
    @Test
    public void searchByEmail() {
        alarmWidget.searchAlarm(alarmType, Const.TENANT_EMAIL);
        alarmWidget.setUsers();

        assertThat(alarmWidget.getUsers()).hasSize(1).as("Search result contains search input").contains(Const.TENANT_EMAIL);
        alarmWidget.assignUsers().forEach(this::assertIsDisplayed);
    }
    /**
     * Search by name.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Search by name")
    @Test
    public void searchByName() {
        alarmWidget.searchAlarm(alarmType, userName);

        assertIsDisplayed(alarmWidget.noUsersFoundMessage());
    }
}
