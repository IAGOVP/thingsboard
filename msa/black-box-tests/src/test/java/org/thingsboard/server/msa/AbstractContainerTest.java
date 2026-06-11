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
package org.thingsboard.server.msa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.DeviceProfileProvisionType;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.device.profile.AllowCreateNewDevicesDeviceProfileProvisionConfiguration;
import org.thingsboard.server.common.data.device.profile.CheckPreProvisionedDevicesDeviceProfileProvisionConfiguration;
import org.thingsboard.server.common.data.device.profile.DeviceProfileData;
import org.thingsboard.server.common.data.device.profile.DeviceProfileProvisionConfiguration;
import org.thingsboard.server.common.data.device.profile.DisabledDeviceProfileProvisionConfiguration;
import org.thingsboard.server.common.data.id.DeviceId;

import java.net.URI;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
/**
 * Base class for Docker-based black-box integration tests.
 *
 * <p>Bootstraps {@link ContainerTestSuite}, {@link TestRestClient}, and shared device profile fixtures.
 */


@Slf4j
@Listeners(TestListener.class)
public abstract class AbstractContainerTest {

    protected static final int TIMEOUT = 30;

    protected final static String TEST_PROVISION_DEVICE_KEY = "test_provision_key";
    protected final static String TEST_PROVISION_DEVICE_SECRET = "test_provision_secret";
    protected static long timeoutMultiplier = 1;
    protected ObjectMapper mapper = new ObjectMapper();
    private static final ContainerTestSuite containerTestSuite = ContainerTestSuite.getInstance();
    protected static TestRestClient testRestClient;
    /**
     * TestNG hook: starts containers and initializes REST client.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @BeforeSuite
    public void beforeSuite() {
        if ("false".equals(System.getProperty("runLocal", "false"))) {
            containerTestSuite.start();
        }
        testRestClient = new TestRestClient(TestProperties.getBaseUrl());
        if (!"kafka".equals(System.getProperty("blackBoxTests.queue", "kafka"))) {
            timeoutMultiplier = 10;
        }
    }
    /**
     * TestNG hook: stops containers and cleans temporary files.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @AfterSuite()
    public void afterSuite() {
        if (containerTestSuite.isActive()) {
            containerTestSuite.stop();
        }
    }
    /**
     * Subscribe to web socket.
     *
     * @param deviceId device under test
     * @param scope scope ({@link String})
     * @param property property ({@link CmdsType})
     * @return {@link WsClient}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected WsClient subscribeToWebSocket(DeviceId deviceId, String scope, CmdsType property) throws Exception {
        String webSocketUrl = TestProperties.getWebSocketUrl();
        WsClient wsClient = new WsClient(new URI(webSocketUrl + "/api/ws/plugins/telemetry?token=" + testRestClient.getToken()), timeoutMultiplier);
        if (webSocketUrl.matches("^(wss)://.*$")) {
            SSLContextBuilder builder = SSLContexts.custom();
            builder.loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true);
            wsClient.setSocketFactory(builder.build().getSocketFactory());
        }
        wsClient.connectBlocking();

        JsonObject cmdsObject = new JsonObject();
        cmdsObject.addProperty("entityType", EntityType.DEVICE.name());
        cmdsObject.addProperty("entityId", deviceId.toString());
        cmdsObject.addProperty("scope", scope);
        cmdsObject.addProperty("cmdId", new Random().nextInt(100));

        JsonArray cmd = new JsonArray();
        cmd.add(cmdsObject);
        JsonObject wsRequest = new JsonObject();
        wsRequest.add(property.toString(), cmd);
        wsClient.send(wsRequest.toString());
        wsClient.waitForFirstReply();
        return wsClient;
    }
    /**
     * Returns expected latest values.
     *
     * @param ts ts
     * @return {@link Map}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected Map<String, Long> getExpectedLatestValues(long ts) {
        return ImmutableMap.<String, Long>builder()
                .put("booleanKey", ts)
                .put("stringKey", ts)
                .put("doubleKey", ts)
                .put("longKey", ts)
                .build();
    }
    /**
     * Creates gateway connect payload.
     *
     * @param deviceName device name ({@link String})
     * @return {@link JsonObject}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected JsonObject createGatewayConnectPayload(String deviceName){
        JsonObject payload = new JsonObject();
        payload.addProperty("device", deviceName);
        return payload;
    }
    /**
     * Creates gateway payload.
     *
     * @param deviceName device name ({@link String})
     * @param ts ts
     * @return {@link JsonObject}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected JsonObject createGatewayPayload(String deviceName, long ts){
        JsonObject payload = new JsonObject();
        payload.add(deviceName, createGatewayTelemetryArray(ts));
        return payload;
    }
    /**
     * Creates gateway telemetry array.
     *
     * @param ts ts
     * @return {@link JsonArray}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected JsonArray createGatewayTelemetryArray(long ts){
        JsonArray telemetryArray = new JsonArray();
        if (ts > 0)
            telemetryArray.add(createPayload(ts));
        else
            telemetryArray.add(createPayload());
        return telemetryArray;
    }
    /**
     * Creates payload.
     *
     * @param ts ts
     * @return {@link JsonObject}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected JsonObject createPayload(long ts) {
        JsonObject values = createPayload();
        JsonObject payload = new JsonObject();
        payload.addProperty("ts", ts);
        payload.add("values", values);
        return payload;
    }
    /**
     * Creates payload.
     *
     * @return {@link JsonObject}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected JsonObject createPayload() {
        JsonObject values = new JsonObject();
        values.addProperty("stringKey", "value1");
        values.addProperty("booleanKey", true);
        values.addProperty("doubleKey", 42.0);
        values.addProperty("longKey", 73L);

        return values;
    }

    /**

     * Enumerates cmds type values (black-box test infrastructure).

     */

    protected enum CmdsType {
        TS_SUB_CMDS("tsSubCmds"),
        HISTORY_CMDS("historyCmds"),
        ATTR_SUB_CMDS("attrSubCmds");

        private final String text;

        CmdsType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
    /**
     * Updates device profile with provisioning strategy.
     *
     * @param deviceProfile device profile ({@link DeviceProfile})
     * @param provisionType provision type ({@link DeviceProfileProvisionType})
     * @return {@link DeviceProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    protected DeviceProfile updateDeviceProfileWithProvisioningStrategy(DeviceProfile deviceProfile, DeviceProfileProvisionType provisionType) {
        DeviceProfileProvisionConfiguration provisionConfiguration;
        String testProvisionDeviceKey = TEST_PROVISION_DEVICE_KEY;
        deviceProfile.setProvisionType(provisionType);
        provisionConfiguration = switch (provisionType) {
            case ALLOW_CREATE_NEW_DEVICES -> new AllowCreateNewDevicesDeviceProfileProvisionConfiguration(TEST_PROVISION_DEVICE_SECRET);
            case CHECK_PRE_PROVISIONED_DEVICES -> new CheckPreProvisionedDevicesDeviceProfileProvisionConfiguration(TEST_PROVISION_DEVICE_SECRET);
            default -> {
                testProvisionDeviceKey = null;
                yield new DisabledDeviceProfileProvisionConfiguration(null);
            }
        };
        DeviceProfileData deviceProfileData = deviceProfile.getProfileData();
        deviceProfileData.setProvisionConfiguration(provisionConfiguration);
        deviceProfile.setProfileData(deviceProfileData);
        deviceProfile.setProvisionDeviceKey(testProvisionDeviceKey);
        return testRestClient.postDeviceProfile(deviceProfile);
    }
    /**
     * Updates default tenant profile.
     *
     * @param updater updater ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    protected void updateDefaultTenantProfile(Consumer<TenantProfile> updater) {
        EntityInfo defaultTenantProfileInfo = testRestClient.getDefaultTenantProfileInfo();
        TenantProfile oldTenantProfile = testRestClient.getTenantProfileById(defaultTenantProfileInfo.getId().getId().toString());
        TenantProfile tenantProfile = JacksonUtil.clone(oldTenantProfile);
        updater.accept(tenantProfile);
        testRestClient.postTenantProfile(tenantProfile);
    }

}
