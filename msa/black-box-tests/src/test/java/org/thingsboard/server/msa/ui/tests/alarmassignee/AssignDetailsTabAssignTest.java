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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.msa.ui.pages.AssetPageHelper;
import org.thingsboard.server.msa.ui.pages.EntityViewPageHelper;
import org.thingsboard.server.msa.ui.utils.Const;
import org.thingsboard.server.msa.ui.utils.EntityPrototypes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.thingsboard.server.msa.ui.base.AbstractBasePage.random;
/**
 * Black-box test: assign details tab assign (TestNG smoke and regression test cases — UI smoke/regression tests).
 */


@Feature("Assign from details tab of entity (by tenant)")
public class AssignDetailsTabAssignTest extends AbstractAssignTest {

    private AssetId assetId;
    private AlarmId propageteAlarmId;
    private AlarmId propageteAssigneAlarmId;
    private AlarmId customerAlarmId;
    private AlarmId assetAlarmId;
    private AlarmId entityViewAlarmId;
    private EntityViewId entityViewId;
    private String assetName;
    private String entityViewName;
    private String propagateAlarmType;
    private String propagateAssignedAlarmType;
    private String customerAlarmType;
    private String assetAlarmType;
    private String entityViewAlarmType;
    private AssetPageHelper assetPage;
    private EntityViewPageHelper entityViewPage;
    /**
     * Generate test entities.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @BeforeClass
    public void generateTestEntities() {
        assetPage = new AssetPageHelper(driver);
        entityViewPage = new EntityViewPageHelper(driver);

        customerAlarmType = "Test customer alarm" + random();
        assetAlarmType = "Test asset alarm" + random();
        entityViewAlarmType = "Test entity view alarm" + random();
        propagateAlarmType = "Test propagated alarm " + random();
        propagateAssignedAlarmType = "Test propagated alarm " + random();

        customerAlarmId = testRestClient.postAlarm(EntityPrototypes.defaultAlarm(customerId, customerAlarmType)).getId();
        assetId = testRestClient.postAsset(EntityPrototypes.defaultAssetPrototype("Asset", customerId)).getId();
        assetName = testRestClient.getAssetById(assetId).getName();
        assetAlarmId = testRestClient.postAlarm(EntityPrototypes.defaultAlarm(assetId, assetAlarmType)).getId();
        entityViewId = testRestClient.postEntityView(EntityPrototypes.defaultEntityViewPrototype("Entity view", "", "DEVICE")).getId();
        entityViewName = testRestClient.getEntityViewById(entityViewId).getName();
        entityViewAlarmId = testRestClient.postAlarm(EntityPrototypes.defaultAlarm(entityViewId, entityViewAlarmType)).getId();
    }
    /**
     * Generate test alarms.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @BeforeMethod
    public void generateTestAlarms() {
        propageteAlarmId = testRestClient.postAlarm(EntityPrototypes.defaultAlarm(deviceId, propagateAlarmType, true)).getId();
        propageteAssigneAlarmId = testRestClient.postAlarm(EntityPrototypes.defaultAlarm(deviceId, propagateAssignedAlarmType, userId, true)).getId();
    }
    /**
     * Deletes test entities.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @AfterClass
    public void deleteTestEntities() {
        deleteAlarmById(customerAlarmId);
        deleteAlarmById(assetAlarmId);
        deleteAlarmById(entityViewAlarmId);
        deleteAssetById(assetId);
        deleteEntityView(entityViewId);
    }
    /**
     * Deletes test alarms.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @AfterMethod
    public void deleteTestAlarms() {
        deleteAlarmsByIds(propageteAlarmId, propageteAssigneAlarmId);
    }
    /**
     * Alarms.
     *
     * @return the Object[][] value
     * @throws Exception if an unexpected error occurs during processing
     */

    @DataProvider
    public Object[][] alarms() {
        return new Object[][]{
                {alarmType},
                {propagateAlarmType}};
    }
    /**
     * Assigns ed alarms.
     *
     * @return the Object[][] value
     * @throws Exception if an unexpected error occurs during processing
     */

    @DataProvider
    public Object[][] assignedAlarms() {
        return new Object[][]{
                {assignedAlarmType},
                {propagateAssignedAlarmType}};
    }
    /**
     * Assigns alarm to yourself.
     *
     * @param alarm alarm ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Can assign alarm to yourself/Can assign propagate alarm to yourself")
    @Test(dataProvider = "alarms")
    public void assignAlarmToYourself(String alarm) {
        sideBarMenuView.goToDevicesPage();
        devicePage.openDeviceAlarms(deviceName);
        alarmPage.assignAlarmTo(alarm, Const.TENANT_EMAIL);

        assertIsDisplayed(alarmPage.assignedUser(Const.TENANT_EMAIL));
    }
    /**
     * Assigns alarm to another user.
     *
     * @param alarm alarm ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Can assign alarm to another user/Can assign propagate alarm to another user")
    @Test(dataProvider = "alarms")
    public void assignAlarmToAnotherUser(String alarm) {
        sideBarMenuView.goToDevicesPage();
        devicePage.openDeviceAlarms(deviceName);
        alarmPage.assignAlarmTo(alarm, userEmail);

        assertIsDisplayed(alarmPage.assignedUser(userEmail));
    }
    /**
     * Unassigns ed alarm.
     *
     * @param assignedAlarm assigned alarm ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Can unassign alarm/Can unassign propagate alarm")
    @Test(dataProvider = "assignedAlarms")
    public void unassignedAlarm(String assignedAlarm) {
        sideBarMenuView.goToDevicesPage();
        devicePage.openDeviceAlarms(deviceName);
        alarmPage.unassignedAlarm(assignedAlarm);

        assertIsDisplayed(alarmPage.unassigned(assignedAlarm));
    }
    /**
     * Reassign alarm.
     *
     * @param assignedAlarm assigned alarm ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Can reassign alarm to another user/Can reassign propagate alarm to another user")
    @Test(dataProvider = "assignedAlarms")
    public void reassignAlarm(String assignedAlarm) {
        sideBarMenuView.goToDevicesPage();
        devicePage.openDeviceAlarms(deviceName);
        alarmPage.assignAlarmTo(assignedAlarm, Const.TENANT_EMAIL);

        assertIsDisplayed(alarmPage.assignedUser(Const.TENANT_EMAIL));
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
        sideBarMenuView.goToDevicesPage();
        devicePage.openDeviceAlarms(deviceName);
        alarmPage.searchAlarm(alarmType, Const.TENANT_EMAIL);
        alarmPage.setUsers();

        assertThat(alarmPage.getUsers()).hasSize(1).as("Search result contains search input").contains(Const.TENANT_EMAIL);
        alarmPage.assignUsers().forEach(this::assertIsDisplayed);
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
        sideBarMenuView.goToDevicesPage();
        devicePage.openDeviceAlarms(deviceName);
        alarmPage.searchAlarm(alarmType, userName);

        assertIsDisplayed(alarmPage.noUsersFoundMessage());
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
        sideBarMenuView.goToDevicesPage();
        devicePage.openDeviceAlarms(deviceName);
        alarmPage.alarmDetailsBtn(alarmType).click();
        alarmDetailsView.assignAlarmTo(Const.TENANT_EMAIL);
        alarmDetailsView.closeAlarmDetailsViewBtn().click();

        assertIsDisplayed(alarmPage.assignedUser(Const.TENANT_EMAIL));
    }
    /**
     * Assigns alarm to another user from details.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Assign alarm to another user from details of alarm")
    @Test
    public void assignAlarmToAnotherUserFromDetails() {
        sideBarMenuView.goToDevicesPage();
        devicePage.openDeviceAlarms(deviceName);
        alarmPage.alarmDetailsBtn(alarmType).click();
        alarmDetailsView.assignAlarmTo(userEmail);
        alarmDetailsView.closeAlarmDetailsViewBtn().click();

        assertIsDisplayed(alarmPage.assignedUser(userEmail));
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
        sideBarMenuView.goToDevicesPage();
        devicePage.openDeviceAlarms(deviceName);
        alarmPage.alarmDetailsBtn(assignedAlarmType).click();
        alarmDetailsView.unassignedAlarm();
        alarmDetailsView.closeAlarmDetailsViewBtn().click();

        assertIsDisplayed(alarmPage.unassigned(assignedAlarmType));
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
        sideBarMenuView.goToDevicesPage();
        devicePage.openDeviceAlarms(deviceName);
        alarmPage.alarmDetailsBtn(assignedAlarmType).click();
        alarmDetailsView.assignAlarmTo(Const.TENANT_EMAIL);
        alarmDetailsView.closeAlarmDetailsViewBtn().click();

        assertIsDisplayed(alarmPage.assignedUser(Const.TENANT_EMAIL));
    }
    /**
     * Assigns customer alarm to yourself.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Assign alarm to yourself for Customer entity details")
    @Test
    public void assignCustomerAlarmToYourself() {
        sideBarMenuView.customerBtn().click();
        customerPage.openCustomerAlarms(customerTitle);
        alarmPage.assignAlarmTo(customerAlarmType, Const.TENANT_EMAIL);

        assertIsDisplayed(alarmPage.assignedUser(Const.TENANT_EMAIL));
    }
    /**
     * Assigns asset alarm to yourself.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Assign alarm to yourself for Asset details")
    @Test
    public void assignAssetAlarmToYourself() {
        sideBarMenuView.goToAssetsPage();
        assetPage.openAssetAlarms(assetName);
        alarmPage.assignAlarmTo(assetAlarmType, Const.TENANT_EMAIL);

        assertIsDisplayed(alarmPage.assignedUser(Const.TENANT_EMAIL));
    }
    /**
     * Assigns entity views alarm to yourself.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Description("Assign alarm to yourself for Entity view details")
    @Test
    public void assignEntityViewsAlarmToYourself() {
        sideBarMenuView.goToEntityViewsPage();
        entityViewPage.openEntityViewAlarms(entityViewName);
        alarmPage.assignAlarmTo(entityViewAlarmType, Const.TENANT_EMAIL);

        assertIsDisplayed(alarmPage.assignedUser(Const.TENANT_EMAIL));
    }

}
