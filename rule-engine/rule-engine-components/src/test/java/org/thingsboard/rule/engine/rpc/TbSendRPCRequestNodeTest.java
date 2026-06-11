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
package org.thingsboard.rule.engine.rpc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.api.RuleEngineDeviceRpcRequest;
import org.thingsboard.rule.engine.api.RuleEngineDeviceRpcResponse;
import org.thingsboard.rule.engine.api.RuleEngineRpcService;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityIdFactory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.data.msg.TbNodeConnectionType;
import org.thingsboard.server.common.data.rpc.RpcError;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;
/**
 * Unit test for tb send rpcrequest node (device RPC request/reply nodes).
 */


@ExtendWith(MockitoExtension.class)
public class TbSendRPCRequestNodeTest {

    private final TenantId TENANT_ID = TenantId.fromUUID(UUID.fromString("d3a47f8b-d863-4c1f-b6f0-2c946b43f21c"));
    private final DeviceId DEVICE_ID = new DeviceId(UUID.fromString("b052ae59-b9b4-47e8-ac71-39e7124bbd66"));

    private final String MSG_DATA = """
            {
              "method": "setGpio",
              "params": {
                "pin": "23",
                "value": 1
              },
              "additionalInfo": "information"
            }
            """;

    private TbSendRPCRequestNode node;
    private TbSendRpcRequestNodeConfiguration config;

    @Mock
    private TbContext ctxMock;
    @Mock
    private RuleEngineRpcService rpcServiceMock;
    /**
     * Set up.
     *
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @BeforeEach
    public void setUp() throws TbNodeException {
        node = new TbSendRPCRequestNode();
        config = new TbSendRpcRequestNodeConfiguration().defaultConfiguration();
        var configuration = new TbNodeConfiguration(JacksonUtil.valueToTree(config));
        node.init(ctxMock, configuration);
    }
    /**
     * Verify default config.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test
    public void verifyDefaultConfig() {
        assertThat(config.getTimeoutInSeconds()).isEqualTo(60);
    }
    /**
     * Given oneway when on msg then verify request.
     *
     * @param mdKeyValue md key value ({@link String})
     * @param expectedResult expected result
     * @throws Exception if an unexpected error occurs during processing
     */

    @ParameterizedTest
    @MethodSource
    public void givenOneway_whenOnMsg_thenVerifyRequest(String mdKeyValue, boolean expectedResult) {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        TbMsgMetaData msgMetadata = new TbMsgMetaData();
        msgMetadata.putValue("oneway", mdKeyValue);
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(msgMetadata)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        var ruleEngineDeviceRpcRequestCaptor = captureRequest();
        assertThat(ruleEngineDeviceRpcRequestCaptor.getValue().isOneway()).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> givenOneway_whenOnMsg_thenVerifyRequest() {
        return Stream.of(
                Arguments.of("true", true),
                Arguments.of("false", false),
                Arguments.of(null, false),
                Arguments.of("", false)
        );
    }
    /**
     * Given msg body when on msg then verify request.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test
    public void givenMsgBody_whenOnMsg_thenVerifyRequest() {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(TbMsgMetaData.EMPTY)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = ArgumentCaptor.forClass(RuleEngineDeviceRpcRequest.class);
        then(rpcServiceMock).should().sendRpcRequestToDevice(requestCaptor.capture(), any(Consumer.class));
        assertThat(requestCaptor.getValue())
                .hasFieldOrPropertyWithValue("method", "setGpio")
                .hasFieldOrPropertyWithValue("body", "{\"pin\":\"23\",\"value\":1}")
                .hasFieldOrPropertyWithValue("deviceId", DEVICE_ID)
                .hasFieldOrPropertyWithValue("tenantId", TENANT_ID)
                .hasFieldOrPropertyWithValue("additionalInfo", "information");
    }
    /**
     * Given request id is not set when on msg then verify request.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test
    public void givenRequestIdIsNotSet_whenOnMsg_thenVerifyRequest() {
        Random randomMock = mock(Random.class);
        given(randomMock.nextInt()).willReturn(123);
        ReflectionTestUtils.setField(node, "random", randomMock);
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.TO_SERVER_RPC_REQUEST)
                .originator(DEVICE_ID)
                .copyMetaData(TbMsgMetaData.EMPTY)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        assertThat(requestCaptor.getValue().getRequestId()).isEqualTo(123);
    }
    /**
     * Given request id when on msg then verify request.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test
    public void givenRequestId_whenOnMsg_thenVerifyRequest() {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);
        String data = """
                {
                  "method": "setGpio",
                  "params": {
                    "pin": "23",
                    "value": 1
                  },
                  "requestId": 12345
                }
                """;
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.TO_SERVER_RPC_REQUEST)
                .originator(DEVICE_ID)
                .copyMetaData(TbMsgMetaData.EMPTY)
                .data(data)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        assertThat(requestCaptor.getValue().getRequestId()).isEqualTo(12345);
    }
    /**
     * Given request uuid when on msg then verify request.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test
    public void givenRequestUUID_whenOnMsg_thenVerifyRequest() {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        String requestUUID = "b795a241-5a30-48fb-92d5-46b864d47130";
        TbMsgMetaData metadata = new TbMsgMetaData();
        metadata.putValue("requestUUID", requestUUID);
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(metadata)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        assertThat(requestCaptor.getValue().getRequestUUID()).isEqualTo(UUID.fromString(requestUUID));
    }
    /**
     * Given invalid request uuid when on msg then verify request.
     *
     * @param requestUUID request uuid ({@link String})
     * @throws Exception if an unexpected error occurs during processing
     */

    @ParameterizedTest
    @NullAndEmptySource
    public void givenInvalidRequestUUID_whenOnMsg_thenVerifyRequest(String requestUUID) {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        TbMsgMetaData metadata = new TbMsgMetaData();
        metadata.putValue("requestUUID", requestUUID);
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(metadata)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        assertThat(requestCaptor.getValue().getRequestUUID()).isNotNull();
    }
    /**
     * Given origin service id when on msg then verify request.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test
    public void givenOriginServiceId_whenOnMsg_thenVerifyRequest() {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        String originServiceId = "service-id-123";
        TbMsgMetaData metadata = new TbMsgMetaData();
        metadata.putValue("originServiceId", originServiceId);
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(metadata)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        assertThat(requestCaptor.getValue().getOriginServiceId()).isEqualTo(originServiceId);
    }
    /**
     * Given invalid origin service id when on msg then verify request.
     *
     * @param originServiceId origin service id ({@link String})
     * @throws Exception if an unexpected error occurs during processing
     */

    @ParameterizedTest
    @NullAndEmptySource
    public void givenInvalidOriginServiceId_whenOnMsg_thenVerifyRequest(String originServiceId) {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        TbMsgMetaData metadata = new TbMsgMetaData();
        metadata.putValue("originServiceId", originServiceId);
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(metadata)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        assertThat(requestCaptor.getValue().getOriginServiceId()).isNull();
    }
    /**
     * Given expiration time when on msg then verify request.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test
    public void givenExpirationTime_whenOnMsg_thenVerifyRequest() {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        String expirationTime = "2000000000000";
        TbMsgMetaData metadata = new TbMsgMetaData();
        metadata.putValue(DataConstants.EXPIRATION_TIME, expirationTime);
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(metadata)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        assertThat(requestCaptor.getValue().getExpirationTime()).isEqualTo(Long.parseLong(expirationTime));
    }
    /**
     * Given invalid expiration time when on msg then verify request.
     *
     * @param expirationTime expiration time ({@link String})
     * @throws Exception if an unexpected error occurs during processing
     */

    @ParameterizedTest
    @NullAndEmptySource
    public void givenInvalidExpirationTime_whenOnMsg_thenVerifyRequest(String expirationTime) {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        TbMsgMetaData metadata = new TbMsgMetaData();
        metadata.putValue(DataConstants.EXPIRATION_TIME, expirationTime);
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(metadata)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        assertThat(requestCaptor.getValue().getExpirationTime()).isGreaterThan(System.currentTimeMillis());
    }
    /**
     * Given retries when on msg then verify request.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test
    public void givenRetries_whenOnMsg_thenVerifyRequest() {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        Integer retries = 3;
        TbMsgMetaData metadata = new TbMsgMetaData();
        metadata.putValue(DataConstants.RETRIES, String.valueOf(retries));
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(metadata)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        assertThat(requestCaptor.getValue().getRetries()).isEqualTo(retries);
    }
    /**
     * Given invalid retries value when on msg then verify request.
     *
     * @param retries retries ({@link String})
     * @throws Exception if an unexpected error occurs during processing
     */

    @ParameterizedTest
    @NullAndEmptySource
    public void givenInvalidRetriesValue_whenOnMsg_thenVerifyRequest(String retries) {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        TbMsgMetaData metadata = new TbMsgMetaData();
        metadata.putValue(DataConstants.RETRIES, retries);
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(metadata)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        assertThat(requestCaptor.getValue().getRetries()).isNull();
    }
    /**
     * Given tb msg type when on msg then verify request.
     *
     * @param msgType msg type ({@link TbMsgType})
     * @throws Exception if an unexpected error occurs during processing
     */

    @ParameterizedTest
    @EnumSource(TbMsgType.class)
    public void givenTbMsgType_whenOnMsg_thenVerifyRequest(TbMsgType msgType) {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        TbMsg msg = TbMsg.newMsg()
                .type(msgType)
                .originator(DEVICE_ID)
                .copyMetaData(TbMsgMetaData.EMPTY)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        if (msgType == TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE) {
            assertThat(requestCaptor.getValue().isRestApiCall()).isTrue();
            return;
        }
        assertThat(requestCaptor.getValue().isRestApiCall()).isFalse();
    }
    /**
     * Given persistent when on msg then verify request.
     *
     * @param isPersisted is persisted ({@link String})
     * @param expectedPersistence expected persistence
     * @throws Exception if an unexpected error occurs during processing
     */

    @ParameterizedTest
    @MethodSource
    public void givenPersistent_whenOnMsg_thenVerifyRequest(String isPersisted, boolean expectedPersistence) {
        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);

        TbMsgMetaData metadata = new TbMsgMetaData();
        metadata.putValue(DataConstants.PERSISTENT, isPersisted);
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(metadata)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = captureRequest();
        assertThat(requestCaptor.getValue().isPersisted()).isEqualTo(expectedPersistence);
    }

    private static Stream<Arguments> givenPersistent_whenOnMsg_thenVerifyRequest() {
        return Stream.of(
                Arguments.of("true", true),
                Arguments.of("false", false),
                Arguments.of(null, false),
                Arguments.of("", false)
        );
    }

    private ArgumentCaptor<RuleEngineDeviceRpcRequest> captureRequest() {
        ArgumentCaptor<RuleEngineDeviceRpcRequest> requestCaptor = ArgumentCaptor.forClass(RuleEngineDeviceRpcRequest.class);
        then(rpcServiceMock).should().sendRpcRequestToDevice(requestCaptor.capture(), any(Consumer.class));
        return requestCaptor;
    }
    /**
     * Given rpc response without error when on msg then sends rpc request.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test
    public void givenRpcResponseWithoutError_whenOnMsg_thenSendsRpcRequest() {
        TbMsg outMsg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(TbMsgMetaData.EMPTY)
                .data(TbMsg.EMPTY_JSON_OBJECT)
                .build();

        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);
        // TODO: replace deprecated method newMsg()
        given(ctxMock.newMsg(any(), any(String.class), any(), any(), any(), any())).willReturn(outMsg);
        willAnswer(invocation -> {
            Consumer<RuleEngineDeviceRpcResponse> consumer = invocation.getArgument(1);
            RuleEngineDeviceRpcResponse rpcResponseMock = mock(RuleEngineDeviceRpcResponse.class);
            given(rpcResponseMock.getError()).willReturn(Optional.empty());
            given(rpcResponseMock.getResponse()).willReturn(Optional.of(TbMsg.EMPTY_JSON_OBJECT));
            consumer.accept(rpcResponseMock);
            return null;
        }).given(rpcServiceMock).sendRpcRequestToDevice(any(RuleEngineDeviceRpcRequest.class), any(Consumer.class));

        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(TbMsgMetaData.EMPTY)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        then(ctxMock).should().enqueueForTellNext(outMsg, TbNodeConnectionType.SUCCESS);
        then(ctxMock).should().ack(msg);
    }
    /**
     * Given rpc response with error when on msg then tell failure.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    @Test
    public void givenRpcResponseWithError_whenOnMsg_thenTellFailure() {
        TbMsg outMsg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(TbMsgMetaData.EMPTY)
                .data(TbMsg.EMPTY_JSON_OBJECT)
                .build();

        given(ctxMock.getRpcService()).willReturn(rpcServiceMock);
        given(ctxMock.getTenantId()).willReturn(TENANT_ID);
        // TODO: replace deprecated method newMsg()
        given(ctxMock.newMsg(any(), any(String.class), any(), any(), any(), any())).willReturn(outMsg);
        willAnswer(invocation -> {
            Consumer<RuleEngineDeviceRpcResponse> consumer = invocation.getArgument(1);
            RuleEngineDeviceRpcResponse rpcResponseMock = mock(RuleEngineDeviceRpcResponse.class);
            given(rpcResponseMock.getError()).willReturn(Optional.of(RpcError.NO_ACTIVE_CONNECTION));
            consumer.accept(rpcResponseMock);
            return null;
        }).given(rpcServiceMock).sendRpcRequestToDevice(any(RuleEngineDeviceRpcRequest.class), any(Consumer.class));

        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.RPC_CALL_FROM_SERVER_TO_DEVICE)
                .originator(DEVICE_ID)
                .copyMetaData(TbMsgMetaData.EMPTY)
                .data(MSG_DATA)
                .build();
        node.onMsg(ctxMock, msg);

        then(ctxMock).should().enqueueForTellFailure(outMsg, RpcError.NO_ACTIVE_CONNECTION.name());
        then(ctxMock).should().ack(msg);
    }
    /**
     * Given originator is not device when on msg then throws exception.
     *
     * @param entityType entity type ({@link EntityType})
     * @throws Exception if an unexpected error occurs during processing
     */

    @ParameterizedTest
    @EnumSource(EntityType.class)
    public void givenOriginatorIsNotDevice_whenOnMsg_thenThrowsException(EntityType entityType) {
        EntityId entityId = EntityIdFactory.getByTypeAndUuid(entityType, "ac21a1bb-eabf-4463-8313-24bea1f498d9");

        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.POST_TELEMETRY_REQUEST)
                .originator(entityId)
                .copyMetaData(TbMsgMetaData.EMPTY)
                .data(TbMsg.EMPTY_JSON_OBJECT)
                .build();
        node.onMsg(ctxMock, msg);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        then(ctxMock).should().tellFailure(eq(msg), throwableCaptor.capture());
        assertThat(throwableCaptor.getValue()).isInstanceOf(RuntimeException.class)
                .hasMessage(EntityType.DEVICE != entityType ? "Message originator is not a device entity!"
                        : "Method is not present in the message!");
    }
    /**
     * Given method or params are not present when on msg then throws exception.
     *
     * @param key key ({@link String})
     * @throws Exception if an unexpected error occurs during processing
     */

    @ParameterizedTest
    @ValueSource(strings = {"method", "params"})
    public void givenMethodOrParamsAreNotPresent_whenOnMsg_thenThrowsException(String key) {
        TbMsg msg = TbMsg.newMsg()
                .type(TbMsgType.POST_TELEMETRY_REQUEST)
                .originator(DEVICE_ID)
                .copyMetaData(TbMsgMetaData.EMPTY)
                .data("{\"" + key + "\": \"value\"}")
                .build();

        node.onMsg(ctxMock, msg);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        then(ctxMock).should().tellFailure(eq(msg), throwableCaptor.capture());
        assertThat(throwableCaptor.getValue()).isInstanceOf(RuntimeException.class)
                .hasMessage(key.equals("method") ? "Params are not present in the message!" : "Method is not present in the message!");
    }
}
