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
package org.thingsboard.server.actors.ruleChain;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.thingsboard.common.util.DebugModeUtil;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.common.util.ListeningExecutor;
import org.thingsboard.rule.engine.api.DeviceStateManager;
import org.thingsboard.rule.engine.api.JobManager;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.rule.engine.api.MqttClientSettings;
import org.thingsboard.rule.engine.api.TbHttpClientSettings;
import org.thingsboard.rule.engine.api.NotificationCenter;
import org.thingsboard.rule.engine.api.RuleEngineAiChatModelService;
import org.thingsboard.rule.engine.api.RuleEngineAlarmService;
import org.thingsboard.rule.engine.api.RuleEngineApiUsageStateService;
import org.thingsboard.rule.engine.api.RuleEngineAssetProfileCache;
import org.thingsboard.rule.engine.api.RuleEngineCalculatedFieldQueueService;
import org.thingsboard.rule.engine.api.RuleEngineDeviceProfileCache;
import org.thingsboard.rule.engine.api.RuleEngineRpcService;
import org.thingsboard.rule.engine.api.RuleEngineTelemetryService;
import org.thingsboard.rule.engine.api.ScriptEngine;
import org.thingsboard.rule.engine.api.SmsService;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.notification.SlackService;
import org.thingsboard.rule.engine.api.sms.SmsSenderFactory;
import org.thingsboard.rule.engine.util.TenantIdLoader;
import org.thingsboard.server.actors.ActorSystemContext;
import org.thingsboard.server.actors.TbActorRef;
import org.thingsboard.server.cluster.TbClusterService;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.HasRuleEngineProfile;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.RuleNodeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.data.msg.TbNodeConnectionType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rule.RuleNode;
import org.thingsboard.server.common.data.rule.RuleNodeState;
import org.thingsboard.server.common.data.script.ScriptLanguage;
import org.thingsboard.server.common.msg.TbActorMsg;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.common.msg.TbMsgProcessingStackItem;
import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.dao.ai.AiModelService;
import org.thingsboard.server.dao.alarm.AlarmCommentService;
import org.thingsboard.server.dao.asset.AssetProfileService;
import org.thingsboard.server.dao.asset.AssetService;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.audit.AuditLogService;
import org.thingsboard.server.dao.cassandra.CassandraCluster;
import org.thingsboard.server.dao.cf.CalculatedFieldService;
import org.thingsboard.server.dao.customer.CustomerService;
import org.thingsboard.server.dao.dashboard.DashboardService;
import org.thingsboard.server.dao.device.DeviceCredentialsService;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.domain.DomainService;
import org.thingsboard.server.dao.edge.EdgeEventService;
import org.thingsboard.server.dao.edge.EdgeService;
import org.thingsboard.server.dao.entity.EntityService;
import org.thingsboard.server.dao.entityview.EntityViewService;
import org.thingsboard.server.dao.event.EventService;
import org.thingsboard.server.dao.job.JobService;
import org.thingsboard.server.dao.mobile.MobileAppBundleService;
import org.thingsboard.server.dao.mobile.MobileAppService;
import org.thingsboard.server.dao.nosql.CassandraStatementTask;
import org.thingsboard.server.dao.nosql.TbResultSetFuture;
import org.thingsboard.server.dao.notification.NotificationRequestService;
import org.thingsboard.server.dao.notification.NotificationRuleService;
import org.thingsboard.server.dao.notification.NotificationTargetService;
import org.thingsboard.server.dao.notification.NotificationTemplateService;
import org.thingsboard.server.dao.oauth2.OAuth2ClientService;
import org.thingsboard.server.dao.ota.OtaPackageService;
import org.thingsboard.server.dao.pat.ApiKeyService;
import org.thingsboard.server.dao.queue.QueueService;
import org.thingsboard.server.dao.queue.QueueStatsService;
import org.thingsboard.server.dao.relation.RelationService;
import org.thingsboard.server.dao.resource.TbResourceDataCache;
import org.thingsboard.server.dao.resource.ResourceService;
import org.thingsboard.server.dao.rule.RuleChainService;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.dao.timeseries.TimeseriesService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.widget.WidgetTypeService;
import org.thingsboard.server.dao.widget.WidgetsBundleService;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.TbQueueCallback;
import org.thingsboard.server.queue.common.SimpleTbQueueCallback;
import org.thingsboard.server.service.executors.PubSubRuleNodeExecutorProvider;
import org.thingsboard.server.service.script.RuleNodeJsScriptEngine;
import org.thingsboard.server.service.script.RuleNodeTbelScriptEngine;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.thingsboard.server.common.data.msg.TbMsgType.ATTRIBUTES_DELETED;
import static org.thingsboard.server.common.data.msg.TbMsgType.ATTRIBUTES_UPDATED;
import static org.thingsboard.server.common.data.msg.TbMsgType.ENTITY_CREATED;

/**
 * Default {@link org.thingsboard.rule.engine.api.TbContext} implementation passed to rule-engine components.
 *
 * <p>Provides enqueue, tell-next, alarm, attribute, and external service calls from within a rule node actor.
 */

@Slf4j
public class DefaultTbContext implements TbContext {

    private final ActorSystemContext mainCtx;
    private final String ruleChainName;
    private final RuleNodeCtx nodeCtx;

    public DefaultTbContext(ActorSystemContext mainCtx, String ruleChainName, RuleNodeCtx nodeCtx) {
        this.mainCtx = mainCtx;
        this.ruleChainName = ruleChainName;
        this.nodeCtx = nodeCtx;
    }
    /**
     * Tell success.
     *
     * @param msg actor message to process
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void tellSuccess(TbMsg msg) {
        tellNext(msg, Collections.singleton(TbNodeConnectionType.SUCCESS));
    }
    /**
     * Tell next.
     *
     * @param msg actor message to process
     * @param relationType relation type ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void tellNext(TbMsg msg, String relationType) {
        tellNext(msg, Collections.singleton(relationType));
    }
    /**
     * Tell next.
     *
     * @param msg actor message to process
     * @param relationTypes relation types ({@link Set})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void tellNext(TbMsg msg, Set<String> relationTypes) {
        RuleNode ruleNode = nodeCtx.getSelf();
        persistDebugOutput(msg, relationTypes);
        msg.getCallback().onProcessingEnd(ruleNode.getId());
        nodeCtx.getChainActor().tell(new RuleNodeToRuleChainTellNextMsg(ruleNode.getRuleChainId(), ruleNode.getId(), relationTypes, msg, null));
    }
    /**
     * Tell self.
     *
     * @param msg actor message to process
     * @param delayMs delay ms
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void tellSelf(TbMsg msg, long delayMs) {
        //TODO: add persistence layer
        scheduleMsgWithDelay(new RuleNodeToSelfMsg(this, msg), delayMs, nodeCtx.getSelfActor());
    }
    /**
     * Input.
     *
     * @param msg actor message to process
     * @param ruleChainId target rule chain identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void input(TbMsg msg, RuleChainId ruleChainId) {
        if (!msg.isValid()) {
            return;
        }
        RuleChainId selfRuleChainId = nodeCtx.getSelf().getRuleChainId();
        RuleNodeId selfId = nodeCtx.getSelf().getId();
        if (msg.isAlreadyInStack(selfRuleChainId, selfId)) {
            log.warn("[{}] Detected rule chain processing loop for rule node [{}] in rule chain [{}]. " +
                    "The message will be failed to prevent infinite loop. " +
                    "Please check the rule chain configuration for circular references.",
                    nodeCtx.getTenantId(), selfId, selfRuleChainId);
            tellFailure(msg, new RuntimeException(
                    "Detected rule chain processing loop for rule node [" + selfId + "] " +
                    "in rule chain [" + selfRuleChainId + "]. " +
                    "Please check the rule chain configuration for circular references."));
            return;
        }
        TbMsg tbMsg = msg.copy()
                .ruleChainId(ruleChainId)
                .resetRuleNodeId()
                .build();
        tbMsg.pushToStack(selfRuleChainId, selfId);
        TopicPartitionInfo tpi = resolvePartition(msg);
        doEnqueue(tpi, tbMsg, new SimpleTbQueueCallback(md -> ack(msg), t -> tellFailure(msg, t)));
    }
    /**
     * Output.
     *
     * @param msg actor message to process
     * @param relationType relation type ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void output(TbMsg msg, String relationType) {
        TbMsgProcessingStackItem item = msg.popFormStack();
        if (item == null) {
            ack(msg);
        } else {
            persistDebugOutput(msg, relationType);
            nodeCtx.getChainActor().tell(new RuleChainOutputMsg(item.getRuleChainId(), item.getRuleNodeId(), relationType, msg));
        }
    }
    /**
     * Enqueue.
     *
     * @param tbMsg tb msg ({@link TbMsg})
     * @param onSuccess on success ({@link Runnable})
     * @param onFailure on failure ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void enqueue(TbMsg tbMsg, Runnable onSuccess, Consumer<Throwable> onFailure) {
        enqueue(tbMsg, tbMsg.getQueueName(), onSuccess, onFailure);
    }
    /**
     * Enqueue.
     *
     * @param tbMsg tb msg ({@link TbMsg})
     * @param queueName queue name ({@link String})
     * @param onSuccess on success ({@link Runnable})
     * @param onFailure on failure ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void enqueue(TbMsg tbMsg, String queueName, Runnable onSuccess, Consumer<Throwable> onFailure) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg, queueName);
        enqueue(tpi, tbMsg, onFailure, onSuccess);
    }

    private void enqueue(TopicPartitionInfo tpi, TbMsg tbMsg, Consumer<Throwable> onFailure, Runnable onSuccess) {
        if (!tbMsg.isValid()) {
            log.trace("[{}] Skip invalid message: {}", getTenantId(), tbMsg);
            if (onFailure != null) {
                onFailure.accept(new IllegalArgumentException("Source message is no longer valid!"));
            }
            return;
        }
        doEnqueue(tpi, tbMsg, new SimpleTbQueueCallback(
                metadata -> {
                    persistDebugOutput(tbMsg, TbNodeConnectionType.TO_ROOT_RULE_CHAIN);
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                },
                t -> {
                    if (onFailure != null) {
                        onFailure.accept(t);
                    } else {
                        log.debug("[{}] Failed to put item into queue!", nodeCtx.getTenantId().getId(), t);
                    }
                }));
    }

    private void doEnqueue(TopicPartitionInfo tpi, TbMsg tbMsg, TbQueueCallback callback) {
        TransportProtos.ToRuleEngineMsg msg = TransportProtos.ToRuleEngineMsg.newBuilder()
                .setTenantIdMSB(getTenantId().getId().getMostSignificantBits())
                .setTenantIdLSB(getTenantId().getId().getLeastSignificantBits())
                .setTbMsgProto(TbMsg.toProto(tbMsg))
                .build();
        mainCtx.getClusterService().pushMsgToRuleEngine(tpi, tbMsg.getId(), msg, callback);
    }
    /**
     * Enqueue for tell failure.
     *
     * @param tbMsg tb msg ({@link TbMsg})
     * @param failureMessage failure message ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void enqueueForTellFailure(TbMsg tbMsg, String failureMessage) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg);
        enqueueForTellNext(tpi, tbMsg, Collections.singleton(TbNodeConnectionType.FAILURE), failureMessage, null, null);
    }
    /**
     * Enqueue for tell failure.
     *
     * @param tbMsg tb msg ({@link TbMsg})
     * @param th th ({@link Throwable})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void enqueueForTellFailure(TbMsg tbMsg, Throwable th) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg);
        enqueueForTellNext(tpi, tbMsg, Collections.singleton(TbNodeConnectionType.FAILURE), getFailureMessage(th), null, null);
    }
    /**
     * Enqueue for tell next.
     *
     * @param tbMsg tb msg ({@link TbMsg})
     * @param relationType relation type ({@link String})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, String relationType) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg);
        enqueueForTellNext(tpi, tbMsg, Collections.singleton(relationType), null, null, null);
    }
    /**
     * Enqueue for tell next.
     *
     * @param tbMsg tb msg ({@link TbMsg})
     * @param relationTypes relation types ({@link Set})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, Set<String> relationTypes) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg);
        enqueueForTellNext(tpi, tbMsg, relationTypes, null, null, null);
    }
    /**
     * Enqueue for tell next.
     *
     * @param tbMsg tb msg ({@link TbMsg})
     * @param relationType relation type ({@link String})
     * @param onSuccess on success ({@link Runnable})
     * @param onFailure on failure ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, String relationType, Runnable onSuccess, Consumer<Throwable> onFailure) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg);
        enqueueForTellNext(tpi, tbMsg, Collections.singleton(relationType), null, onSuccess, onFailure);
    }
    /**
     * Enqueue for tell next.
     *
     * @param tbMsg tb msg ({@link TbMsg})
     * @param relationTypes relation types ({@link Set})
     * @param onSuccess on success ({@link Runnable})
     * @param onFailure on failure ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, Set<String> relationTypes, Runnable onSuccess, Consumer<Throwable> onFailure) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg);
        enqueueForTellNext(tpi, tbMsg, relationTypes, null, onSuccess, onFailure);
    }
    /**
     * Enqueue for tell next.
     *
     * @param tbMsg tb msg ({@link TbMsg})
     * @param queueName queue name ({@link String})
     * @param relationType relation type ({@link String})
     * @param onSuccess on success ({@link Runnable})
     * @param onFailure on failure ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, String queueName, String relationType, Runnable onSuccess, Consumer<Throwable> onFailure) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg, queueName);
        enqueueForTellNext(tpi, queueName, tbMsg, Collections.singleton(relationType), null, onSuccess, onFailure);
    }
    /**
     * Enqueue for tell next.
     *
     * @param tbMsg tb msg ({@link TbMsg})
     * @param queueName queue name ({@link String})
     * @param relationTypes relation types ({@link Set})
     * @param onSuccess on success ({@link Runnable})
     * @param onFailure on failure ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, String queueName, Set<String> relationTypes, Runnable onSuccess, Consumer<Throwable> onFailure) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg, queueName);
        enqueueForTellNext(tpi, queueName, tbMsg, relationTypes, null, onSuccess, onFailure);
    }

    private TopicPartitionInfo resolvePartition(TbMsg tbMsg, String queueName) {
        return mainCtx.resolve(ServiceType.TB_RULE_ENGINE, queueName, getTenantId(), tbMsg.getOriginator());
    }

    private TopicPartitionInfo resolvePartition(TbMsg tbMsg) {
        return resolvePartition(tbMsg, tbMsg.getQueueName());
    }

    private void enqueueForTellNext(TopicPartitionInfo tpi, TbMsg source, Set<String> relationTypes, String failureMessage, Runnable onSuccess, Consumer<Throwable> onFailure) {
        enqueueForTellNext(tpi, source.getQueueName(), source, relationTypes, failureMessage, onSuccess, onFailure);
    }

    private void enqueueForTellNext(TopicPartitionInfo tpi, String queueName, TbMsg source, Set<String> relationTypes, String failureMessage, Runnable onSuccess, Consumer<Throwable> onFailure) {
        if (!source.isValid()) {
            log.trace("[{}] Skip invalid message: {}", getTenantId(), source);
            if (onFailure != null) {
                onFailure.accept(new IllegalArgumentException("Source message is no longer valid!"));
            }
            return;
        }
        RuleNode ruleNode = nodeCtx.getSelf();
        RuleChainId ruleChainId = ruleNode.getRuleChainId();
        RuleNodeId ruleNodeId = ruleNode.getId();
        TbMsg tbMsg = TbMsg.newMsg(source, queueName, ruleChainId, ruleNodeId);
        TransportProtos.ToRuleEngineMsg.Builder msg = TransportProtos.ToRuleEngineMsg.newBuilder()
                .setTenantIdMSB(getTenantId().getId().getMostSignificantBits())
                .setTenantIdLSB(getTenantId().getId().getLeastSignificantBits())
                .setTbMsgProto(TbMsg.toProto(tbMsg))
                .addAllRelationTypes(relationTypes);
        if (failureMessage != null) {
            msg.setFailureMessage(failureMessage);
        }
        mainCtx.getClusterService().pushMsgToRuleEngine(tpi, tbMsg.getId(), msg.build(), new SimpleTbQueueCallback(
                metadata -> {
                    persistDebugOutput(tbMsg, relationTypes, null, failureMessage);
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                },
                t -> {
                    if (onFailure != null) {
                        onFailure.accept(t);
                    } else {
                        log.debug("[{}] Failed to put item into queue!", nodeCtx.getTenantId().getId(), t);
                    }
                }));
    }
    /**
     * Ack.
     *
     * @param tbMsg tb msg ({@link TbMsg})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void ack(TbMsg tbMsg) {
        RuleNode ruleNode = nodeCtx.getSelf();
        persistDebugOutput(tbMsg, TbNodeConnectionType.ACK);
        tbMsg.getCallback().onProcessingEnd(ruleNode.getId());
        tbMsg.getCallback().onSuccess();
    }
    /**
     * Is local entity.
     *
     * @param entityId target entity identifier
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean isLocalEntity(EntityId entityId) {
        return mainCtx.resolve(ServiceType.TB_RULE_ENGINE, getQueueName(), getTenantId(), entityId).isMyPartition();
    }

    private void scheduleMsgWithDelay(TbActorMsg msg, long delayInMs, TbActorRef target) {
        mainCtx.scheduleMsgWithDelay(target, msg, delayInMs);
    }
    /**
     * Tell failure.
     *
     * @param msg actor message to process
     * @param th th ({@link Throwable})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void tellFailure(TbMsg msg, Throwable th) {
        RuleNode ruleNode = nodeCtx.getSelf();
        persistDebugOutput(msg, Set.of(TbNodeConnectionType.FAILURE), th, null);
        String failureMessage = getFailureMessage(th);
        nodeCtx.getChainActor().tell(new RuleNodeToRuleChainTellNextMsg(ruleNode.getRuleChainId(),
                ruleNode.getId(), Collections.singleton(TbNodeConnectionType.FAILURE),
                msg, failureMessage));
    }
    /**
     * Updates self.
     *
     * @param self self ({@link RuleNode})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    public void updateSelf(RuleNode self) {
        nodeCtx.setSelf(self);
    }
    /**
     * New msg.
     *
     * @param queueName queue name ({@link String})
     * @param type type ({@link String})
     * @param originator originator ({@link EntityId})
     * @param customerId customer id ({@link CustomerId})
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg newMsg(String queueName, String type, EntityId originator, CustomerId customerId, TbMsgMetaData metaData, String data) {
        return TbMsg.newMsg()
                .queueName(queueName)
                .type(type)
                .originator(originator)
                .customerId(customerId)
                .copyMetaData(metaData)
                .data(data)
                .ruleChainId(nodeCtx.getSelf().getRuleChainId())
                .ruleNodeId(nodeCtx.getSelf().getId())
                .build();
    }
    /**
     * Transform msg.
     *
     * @param origMsg orig msg ({@link TbMsg})
     * @param type type ({@link String})
     * @param originator originator ({@link EntityId})
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg transformMsg(TbMsg origMsg, String type, EntityId originator, TbMsgMetaData metaData, String data) {
        return origMsg.transform()
                .type(type)
                .originator(originator)
                .metaData(metaData)
                .data(data)
                .build();
    }
    /**
     * New msg.
     *
     * @param queueName queue name ({@link String})
     * @param type type ({@link TbMsgType})
     * @param originator originator ({@link EntityId})
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg newMsg(String queueName, TbMsgType type, EntityId originator, TbMsgMetaData metaData, String data) {
        return newMsg(queueName, type, originator, null, metaData, data);
    }
    /**
     * New msg.
     *
     * @param queueName queue name ({@link String})
     * @param type type ({@link TbMsgType})
     * @param originator originator ({@link EntityId})
     * @param customerId customer id ({@link CustomerId})
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg newMsg(String queueName, TbMsgType type, EntityId originator, CustomerId customerId, TbMsgMetaData metaData, String data) {
        return TbMsg.newMsg()
                .queueName(queueName)
                .type(type)
                .originator(originator)
                .customerId(customerId)
                .copyMetaData(metaData)
                .data(data)
                .ruleChainId(nodeCtx.getSelf().getRuleChainId())
                .ruleNodeId(nodeCtx.getSelf().getId())
                .build();
    }
    /**
     * Transform msg.
     *
     * @param origMsg orig msg ({@link TbMsg})
     * @param type type ({@link TbMsgType})
     * @param originator originator ({@link EntityId})
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg transformMsg(TbMsg origMsg, TbMsgType type, EntityId originator, TbMsgMetaData metaData, String data) {
        return origMsg.transform()
                .type(type)
                .originator(originator)
                .metaData(metaData)
                .data(data)
                .build();
    }
    /**
     * Transform msg.
     *
     * @param origMsg orig msg ({@link TbMsg})
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg transformMsg(TbMsg origMsg, TbMsgMetaData metaData, String data) {
        return origMsg.transform()
                .metaData(metaData)
                .data(data)
                .build();
    }
    /**
     * Transform msg originator.
     *
     * @param origMsg orig msg ({@link TbMsg})
     * @param originator originator ({@link EntityId})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg transformMsgOriginator(TbMsg origMsg, EntityId originator) {
        return origMsg.transform()
                .originator(originator)
                .build();
    }
    /**
     * Customer created msg.
     *
     * @param customer customer ({@link Customer})
     * @param ruleNodeId target rule node identifier
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg customerCreatedMsg(Customer customer, RuleNodeId ruleNodeId) {
        return entityActionMsg(customer, customer.getId(), ruleNodeId, ENTITY_CREATED);
    }
    /**
     * Device created msg.
     *
     * @param device device ({@link Device})
     * @param ruleNodeId target rule node identifier
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg deviceCreatedMsg(Device device, RuleNodeId ruleNodeId) {
        DeviceProfile deviceProfile = null;
        if (device.getDeviceProfileId() != null) {
            deviceProfile = mainCtx.getDeviceProfileCache().find(device.getDeviceProfileId());
        }
        return entityActionMsg(device, device.getId(), ruleNodeId, ENTITY_CREATED, deviceProfile);
    }
    /**
     * Asset created msg.
     *
     * @param asset asset ({@link Asset})
     * @param ruleNodeId target rule node identifier
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg assetCreatedMsg(Asset asset, RuleNodeId ruleNodeId) {
        AssetProfile assetProfile = null;
        if (asset.getAssetProfileId() != null) {
            assetProfile = mainCtx.getAssetProfileCache().find(asset.getAssetProfileId());
        }
        return entityActionMsg(asset, asset.getId(), ruleNodeId, ENTITY_CREATED, assetProfile);
    }
    /**
     * Alarm action msg.
     *
     * @param alarm alarm ({@link Alarm})
     * @param ruleNodeId target rule node identifier
     * @param action action ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg alarmActionMsg(Alarm alarm, RuleNodeId ruleNodeId, String action) {
        EntityId originator = alarm.getOriginator();
        HasRuleEngineProfile profile = getRuleEngineProfile(originator);
        return entityActionMsg(alarm, originator, ruleNodeId, action, profile);
    }
    /**
     * Alarm action msg.
     *
     * @param alarm alarm ({@link Alarm})
     * @param ruleNodeId target rule node identifier
     * @param actionMsgType action msg type ({@link TbMsgType})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg alarmActionMsg(Alarm alarm, RuleNodeId ruleNodeId, TbMsgType actionMsgType) {
        EntityId originator = alarm.getOriginator();
        HasRuleEngineProfile profile = getRuleEngineProfile(originator);
        return entityActionMsg(alarm, originator, ruleNodeId, actionMsgType, profile);
    }

    private HasRuleEngineProfile getRuleEngineProfile(EntityId originator) {
        HasRuleEngineProfile profile = null;
        if (EntityType.DEVICE.equals(originator.getEntityType())) {
            DeviceId deviceId = new DeviceId(originator.getId());
            profile = mainCtx.getDeviceProfileCache().get(getTenantId(), deviceId);
        } else if (EntityType.ASSET.equals(originator.getEntityType())) {
            AssetId assetId = new AssetId(originator.getId());
            profile = mainCtx.getAssetProfileCache().get(getTenantId(), assetId);
        }
        return profile;
    }
    /**
     * Attributes updated action msg.
     *
     * @param originator originator ({@link EntityId})
     * @param ruleNodeId target rule node identifier
     * @param scope scope ({@link String})
     * @param attributes attributes ({@link List})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg attributesUpdatedActionMsg(EntityId originator, RuleNodeId ruleNodeId, String scope, List<AttributeKvEntry> attributes) {
        ObjectNode entityNode = JacksonUtil.newObjectNode();
        if (attributes != null) {
            attributes.forEach(attributeKvEntry -> JacksonUtil.addKvEntry(entityNode, attributeKvEntry));
        }
        return attributesActionMsg(originator, ruleNodeId, scope, ATTRIBUTES_UPDATED, JacksonUtil.toString(entityNode));
    }
    /**
     * Attributes deleted action msg.
     *
     * @param originator originator ({@link EntityId})
     * @param ruleNodeId target rule node identifier
     * @param scope scope ({@link String})
     * @param keys keys ({@link List})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbMsg attributesDeletedActionMsg(EntityId originator, RuleNodeId ruleNodeId, String scope, List<String> keys) {
        ObjectNode entityNode = JacksonUtil.newObjectNode();
        ArrayNode attrsArrayNode = entityNode.putArray("attributes");
        if (keys != null) {
            keys.forEach(attrsArrayNode::add);
        }
        return attributesActionMsg(originator, ruleNodeId, scope, ATTRIBUTES_DELETED, JacksonUtil.toString(entityNode));
    }

    private TbMsg attributesActionMsg(EntityId originator, RuleNodeId ruleNodeId, String scope, TbMsgType actionMsgType, String msgData) {
        TbMsgMetaData tbMsgMetaData = getActionMetaData(ruleNodeId);
        tbMsgMetaData.putValue("scope", scope);
        HasRuleEngineProfile profile = getRuleEngineProfile(originator);
        return entityActionMsg(originator, tbMsgMetaData, msgData, actionMsgType, profile);
    }
    /**
     * Entity action msg.
     *
     * @param entity entity ({@link E})
     * @param id id ({@link I})
     * @param ruleNodeId target rule node identifier
     * @param actionMsgType action msg type ({@link TbMsgType})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    public <E, I extends EntityId> TbMsg entityActionMsg(E entity, I id, RuleNodeId ruleNodeId, TbMsgType actionMsgType) {
        return entityActionMsg(entity, id, ruleNodeId, actionMsgType, null);
    }
    /**
     * Entity action msg.
     *
     * @param entity entity ({@link E})
     * @param id id ({@link I})
     * @param ruleNodeId target rule node identifier
     * @param action action ({@link String})
     * @param profile profile ({@link K})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Deprecated(since = "3.6.0", forRemoval = true)
    public <E, I extends EntityId, K extends HasRuleEngineProfile> TbMsg entityActionMsg(E entity, I id, RuleNodeId ruleNodeId, String action, K profile) {
        try {
            return entityActionMsg(id, getActionMetaData(ruleNodeId), JacksonUtil.toString(JacksonUtil.valueToTree(entity)), action, profile);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to process " + id.getEntityType().name().toLowerCase() + " " + action + " msg: " + e);
        }
    }

    @Deprecated(since = "3.6.0", forRemoval = true)
    private <I extends EntityId, K extends HasRuleEngineProfile> TbMsg entityActionMsg(I id, TbMsgMetaData msgMetaData, String msgData, String action, K profile) {
        String defaultQueueName = null;
        RuleChainId defaultRuleChainId = null;
        if (profile != null) {
            defaultQueueName = profile.getDefaultQueueName();
            defaultRuleChainId = profile.getDefaultRuleChainId();
        }
        return TbMsg.newMsg()
                .queueName(defaultQueueName)
                .type(action)
                .originator(id)
                .copyMetaData(msgMetaData)
                .data(msgData)
                .ruleChainId(defaultRuleChainId)
                .build();
    }
    /**
     * Entity action msg.
     *
     * @param entity entity ({@link E})
     * @param id id ({@link I})
     * @param ruleNodeId target rule node identifier
     * @param actionMsgType action msg type ({@link TbMsgType})
     * @param profile profile ({@link K})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */

    public <E, I extends EntityId, K extends HasRuleEngineProfile> TbMsg entityActionMsg(E entity, I id, RuleNodeId ruleNodeId, TbMsgType actionMsgType, K profile) {
        try {
            return entityActionMsg(id, getActionMetaData(ruleNodeId), JacksonUtil.toString(JacksonUtil.valueToTree(entity)), actionMsgType, profile);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to process " + id.getEntityType().name().toLowerCase() + " " + actionMsgType.name() + " msg: " + e);
        }
    }

    private <I extends EntityId, K extends HasRuleEngineProfile> TbMsg entityActionMsg(I id, TbMsgMetaData msgMetaData, String msgData, TbMsgType actionMsgType, K profile) {
        String defaultQueueName = null;
        RuleChainId defaultRuleChainId = null;
        if (profile != null) {
            defaultQueueName = profile.getDefaultQueueName();
            defaultRuleChainId = profile.getDefaultRuleChainId();
        }
        return TbMsg.newMsg()
                .queueName(defaultQueueName)
                .type(actionMsgType)
                .originator(id)
                .copyMetaData(msgMetaData)
                .data(msgData)
                .ruleChainId(defaultRuleChainId)
                .build();
    }
    /**
     * Returns self id.
     *
     * @return {@link RuleNodeId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleNodeId getSelfId() {
        return nodeCtx.getSelf().getId();
    }
    /**
     * Returns self.
     *
     * @return {@link RuleNode}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleNode getSelf() {
        return nodeCtx.getSelf();
    }
    /**
     * Returns rule chain name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String getRuleChainName() {
        return ruleChainName;
    }
    /**
     * Returns queue name.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String getQueueName() {
        return getSelf().getQueueName();
    }
    /**
     * Returns tenant id.
     *
     * @return {@link TenantId}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TenantId getTenantId() {
        return nodeCtx.getTenantId();
    }
    /**
     * Returns mail executor.
     *
     * @return {@link ListeningExecutor}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ListeningExecutor getMailExecutor() {
        return mainCtx.getMailExecutor();
    }
    /**
     * Returns sms executor.
     *
     * @return {@link ListeningExecutor}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ListeningExecutor getSmsExecutor() {
        return mainCtx.getSmsExecutor();
    }
    /**
     * Returns db callback executor.
     *
     * @return {@link ListeningExecutor}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ListeningExecutor getDbCallbackExecutor() {
        return mainCtx.getDbCallbackExecutor();
    }
    /**
     * Returns external call executor.
     *
     * @return {@link ListeningExecutor}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ListeningExecutor getExternalCallExecutor() {
        return mainCtx.getExternalCallExecutorService();
    }
    /**
     * Returns notification executor.
     *
     * @return {@link ListeningExecutor}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ListeningExecutor getNotificationExecutor() {
        return mainCtx.getNotificationExecutor();
    }
    /**
     * Returns pub sub rule node executor provider.
     *
     * @return {@link PubSubRuleNodeExecutorProvider}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PubSubRuleNodeExecutorProvider getPubSubRuleNodeExecutorProvider() {
        return mainCtx.getPubSubRuleNodeExecutorProvider();
    }
    /**
     * Creates js script engine.
     *
     * @param script script ({@link String})
     * @param argNames arg names
     * @return {@link ScriptEngine}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    @Deprecated
    public ScriptEngine createJsScriptEngine(String script, String... argNames) {
        return new RuleNodeJsScriptEngine(getTenantId(), mainCtx.getJsInvokeService(), script, argNames);
    }

    private ScriptEngine createTbelScriptEngine(String script, String... argNames) {
        if (mainCtx.getTbelInvokeService() == null) {
            throw new RuntimeException("TBEL execution is disabled!");
        }
        return new RuleNodeTbelScriptEngine(getTenantId(), mainCtx.getTbelInvokeService(), script, argNames);
    }
    /**
     * Creates script engine.
     *
     * @param scriptLang script lang ({@link ScriptLanguage})
     * @param script script ({@link String})
     * @param argNames arg names
     * @return {@link ScriptEngine}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ScriptEngine createScriptEngine(ScriptLanguage scriptLang, String script, String... argNames) {
        if (scriptLang == null) {
            scriptLang = ScriptLanguage.JS;
        }
        if (StringUtils.isBlank(script)) {
            throw new RuntimeException(scriptLang.name() + " script is blank!");
        }
        switch (scriptLang) {
            case JS:
                return createJsScriptEngine(script, argNames);
            case TBEL:
                if (Arrays.isNullOrEmpty(argNames)) {
                    return createTbelScriptEngine(script, "msg", "metadata", "msgType");
                } else {
                    return createTbelScriptEngine(script, argNames);
                }
            default:
                throw new RuntimeException("Unsupported script language: " + scriptLang.name());
        }
    }
    /**
     * Returns service id.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String getServiceId() {
        return mainCtx.getServiceInfoProvider().getServiceId();
    }
    /**
     * Returns attributes service.
     *
     * @return {@link AttributesService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public AttributesService getAttributesService() {
        return mainCtx.getAttributesService();
    }
    /**
     * Returns customer service.
     *
     * @return {@link CustomerService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public CustomerService getCustomerService() {
        return mainCtx.getCustomerService();
    }
    /**
     * Returns tenant service.
     *
     * @return {@link TenantService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TenantService getTenantService() {
        return mainCtx.getTenantService();
    }
    /**
     * Returns user service.
     *
     * @return {@link UserService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public UserService getUserService() {
        return mainCtx.getUserService();
    }
    /**
     * Returns asset service.
     *
     * @return {@link AssetService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public AssetService getAssetService() {
        return mainCtx.getAssetService();
    }
    /**
     * Returns device service.
     *
     * @return {@link DeviceService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public DeviceService getDeviceService() {
        return mainCtx.getDeviceService();
    }
    /**
     * Returns device profile service.
     *
     * @return {@link DeviceProfileService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public DeviceProfileService getDeviceProfileService() {
        return mainCtx.getDeviceProfileService();
    }
    /**
     * Returns asset profile service.
     *
     * @return {@link AssetProfileService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public AssetProfileService getAssetProfileService() {
        return mainCtx.getAssetProfileService();
    }
    /**
     * Returns device credentials service.
     *
     * @return {@link DeviceCredentialsService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public DeviceCredentialsService getDeviceCredentialsService() {
        return mainCtx.getDeviceCredentialsService();
    }
    /**
     * Returns device state manager.
     *
     * @return {@link DeviceStateManager}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public DeviceStateManager getDeviceStateManager() {
        return mainCtx.getDeviceStateManager();
    }
    /**
     * Returns device state node rate limit config.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public String getDeviceStateNodeRateLimitConfig() {
        return mainCtx.getDeviceStateNodeRateLimitConfig();
    }
    /**
     * Returns cluster service.
     *
     * @return {@link TbClusterService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbClusterService getClusterService() {
        return mainCtx.getClusterService();
    }
    /**
     * Returns dashboard service.
     *
     * @return {@link DashboardService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public DashboardService getDashboardService() {
        return mainCtx.getDashboardService();
    }
    /**
     * Returns alarm service.
     *
     * @return {@link RuleEngineAlarmService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleEngineAlarmService getAlarmService() {
        return mainCtx.getAlarmService();
    }
    /**
     * Returns alarm comment service.
     *
     * @return {@link AlarmCommentService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public AlarmCommentService getAlarmCommentService() {
        return mainCtx.getAlarmCommentService();
    }
    /**
     * Returns rule chain service.
     *
     * @return {@link RuleChainService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleChainService getRuleChainService() {
        return mainCtx.getRuleChainService();
    }
    /**
     * Returns timeseries service.
     *
     * @return {@link TimeseriesService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TimeseriesService getTimeseriesService() {
        return mainCtx.getTsService();
    }
    /**
     * Returns telemetry service.
     *
     * @return {@link RuleEngineTelemetryService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleEngineTelemetryService getTelemetryService() {
        return mainCtx.getTsSubService();
    }
    /**
     * Returns relation service.
     *
     * @return {@link RelationService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RelationService getRelationService() {
        return mainCtx.getRelationService();
    }
    /**
     * Returns entity view service.
     *
     * @return {@link EntityViewService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityViewService getEntityViewService() {
        return mainCtx.getEntityViewService();
    }
    /**
     * Returns resource service.
     *
     * @return {@link ResourceService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ResourceService getResourceService() {
        return mainCtx.getResourceService();
    }
    /**
     * Returns tb resource data cache.
     *
     * @return {@link TbResourceDataCache}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbResourceDataCache getTbResourceDataCache() {
        return mainCtx.getResourceDataCache();
    }
    /**
     * Returns ota package service.
     *
     * @return {@link OtaPackageService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public OtaPackageService getOtaPackageService() {
        return mainCtx.getOtaPackageService();
    }
    /**
     * Returns device profile cache.
     *
     * @return {@link RuleEngineDeviceProfileCache}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleEngineDeviceProfileCache getDeviceProfileCache() {
        return mainCtx.getDeviceProfileCache();
    }
    /**
     * Returns asset profile cache.
     *
     * @return {@link RuleEngineAssetProfileCache}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleEngineAssetProfileCache getAssetProfileCache() {
        return mainCtx.getAssetProfileCache();
    }
    /**
     * Returns edge service.
     *
     * @return {@link EdgeService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EdgeService getEdgeService() {
        return mainCtx.getEdgeService();
    }
    /**
     * Returns edge event service.
     *
     * @return {@link EdgeEventService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EdgeEventService getEdgeEventService() {
        return mainCtx.getEdgeEventService();
    }
    /**
     * Returns queue service.
     *
     * @return {@link QueueService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public QueueService getQueueService() {
        return mainCtx.getQueueService();
    }
    /**
     * Returns queue stats service.
     *
     * @return {@link QueueStatsService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public QueueStatsService getQueueStatsService() {
        return mainCtx.getQueueStatsService();
    }
    /**
     * Returns shared event loop.
     *
     * @return {@link EventLoopGroup}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EventLoopGroup getSharedEventLoop() {
        return mainCtx.getSharedEventLoopGroupService().getSharedEventLoopGroup();
    }
    /**
     * Returns mail service.
     *
     * @param isSystem is system
     * @return {@link MailService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public MailService getMailService(boolean isSystem) {
        if (!isSystem || mainCtx.isAllowSystemMailService()) {
            return mainCtx.getMailService();
        } else {
            throw new RuntimeException("Access to System Mail Service is forbidden!");
        }
    }
    /**
     * Returns sms service.
     *
     * @return {@link SmsService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public SmsService getSmsService() {
        if (mainCtx.isAllowSystemSmsService()) {
            return mainCtx.getSmsService();
        } else {
            throw new RuntimeException("Access to System SMS Service is forbidden!");
        }
    }
    /**
     * Returns sms sender factory.
     *
     * @return {@link SmsSenderFactory}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public SmsSenderFactory getSmsSenderFactory() {
        return mainCtx.getSmsSenderFactory();
    }
    /**
     * Returns notification center.
     *
     * @return {@link NotificationCenter}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationCenter getNotificationCenter() {
        return mainCtx.getNotificationCenter();
    }
    /**
     * Returns notification target service.
     *
     * @return {@link NotificationTargetService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationTargetService getNotificationTargetService() {
        return mainCtx.getNotificationTargetService();
    }
    /**
     * Returns notification template service.
     *
     * @return {@link NotificationTemplateService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationTemplateService getNotificationTemplateService() {
        return mainCtx.getNotificationTemplateService();
    }
    /**
     * Returns notification request service.
     *
     * @return {@link NotificationRequestService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationRequestService getNotificationRequestService() {
        return mainCtx.getNotificationRequestService();
    }
    /**
     * Returns notification rule service.
     *
     * @return {@link NotificationRuleService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public NotificationRuleService getNotificationRuleService() {
        return mainCtx.getNotificationRuleService();
    }
    /**
     * Returns oauth2client service.
     *
     * @return {@link OAuth2ClientService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public OAuth2ClientService getOAuth2ClientService() {
        return mainCtx.getOAuth2ClientService();
    }
    /**
     * Returns domain service.
     *
     * @return {@link DomainService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public DomainService getDomainService() {
        return mainCtx.getDomainService();
    }
    /**
     * Returns mobile app service.
     *
     * @return {@link MobileAppService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public MobileAppService getMobileAppService() {
        return mainCtx.getMobileAppService();
    }
    /**
     * Returns mobile app bundle service.
     *
     * @return {@link MobileAppBundleService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public MobileAppBundleService getMobileAppBundleService() {
        return mainCtx.getMobileAppBundleService();
    }
    /**
     * Returns slack service.
     *
     * @return {@link SlackService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public SlackService getSlackService() {
        return mainCtx.getSlackService();
    }
    /**
     * Returns calculated field service.
     *
     * @return {@link CalculatedFieldService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public CalculatedFieldService getCalculatedFieldService() {
        return mainCtx.getCalculatedFieldService();
    }
    /**
     * Returns calculated field queue service.
     *
     * @return {@link RuleEngineCalculatedFieldQueueService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleEngineCalculatedFieldQueueService getCalculatedFieldQueueService() {
        return mainCtx.getCalculatedFieldQueueService();
    }
    /**
     * Returns job service.
     *
     * @return {@link JobService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public JobService getJobService() {
        return mainCtx.getJobService();
    }
    /**
     * Returns job manager.
     *
     * @return {@link JobManager}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public JobManager getJobManager() {
        return mainCtx.getJobManager();
    }
    /**
     * Returns api key service.
     *
     * @return {@link ApiKeyService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public ApiKeyService getApiKeyService() {
        return mainCtx.getApiKeyService();
    }
    /**
     * Is external node force ack.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public boolean isExternalNodeForceAck() {
        return mainCtx.isExternalNodeForceAck();
    }
    /**
     * Returns rpc service.
     *
     * @return {@link RuleEngineRpcService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleEngineRpcService getRpcService() {
        return mainCtx.getTbRuleEngineDeviceRpcService();
    }
    /**
     * Returns cassandra cluster.
     *
     * @return {@link CassandraCluster}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public CassandraCluster getCassandraCluster() {
        return mainCtx.getCassandraCluster();
    }
    /**
     * Submit cassandra read task.
     *
     * @param task task ({@link CassandraStatementTask})
     * @return {@link TbResultSetFuture}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbResultSetFuture submitCassandraReadTask(CassandraStatementTask task) {
        return mainCtx.getCassandraBufferedRateReadExecutor().submit(task);
    }
    /**
     * Submit cassandra write task.
     *
     * @param task task ({@link CassandraStatementTask})
     * @return {@link TbResultSetFuture}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbResultSetFuture submitCassandraWriteTask(CassandraStatementTask task) {
        return mainCtx.getCassandraBufferedRateWriteExecutor().submit(task);
    }
    /**
     * Finds rule node states.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public PageData<RuleNodeState> findRuleNodeStates(PageLink pageLink) {
        if (log.isDebugEnabled()) {
            log.debug("[{}][{}] Fetch Rule Node States.", getTenantId(), getSelfId());
        }
        return mainCtx.getRuleNodeStateService().findByRuleNodeId(getTenantId(), getSelfId(), pageLink);
    }
    /**
     * Finds rule node state for entity.
     *
     * @param entityId target entity identifier
     * @return {@link RuleNodeState}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleNodeState findRuleNodeStateForEntity(EntityId entityId) {
        if (log.isDebugEnabled()) {
            log.debug("[{}][{}][{}] Fetch Rule Node State for entity.", getTenantId(), getSelfId(), entityId);
        }
        return mainCtx.getRuleNodeStateService().findByRuleNodeIdAndEntityId(getTenantId(), getSelfId(), entityId);
    }
    /**
     * Saves or persists rule node state.
     *
     * @param state state ({@link RuleNodeState})
     * @return {@link RuleNodeState}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleNodeState saveRuleNodeState(RuleNodeState state) {
        if (log.isDebugEnabled()) {
            log.debug("[{}][{}][{}] Persist Rule Node State for entity: {}", getTenantId(), getSelfId(), state.getEntityId(), state.getStateData());
        }
        state.setRuleNodeId(getSelfId());
        return mainCtx.getRuleNodeStateService().save(getTenantId(), state);
    }
    /**
     * Clear rule node states.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void clearRuleNodeStates() {
        if (log.isDebugEnabled()) {
            log.debug("[{}][{}] Going to clear rule node states", getTenantId(), getSelfId());
        }
        mainCtx.getRuleNodeStateService().removeByRuleNodeId(getTenantId(), getSelfId());
    }
    /**
     * Removes rule node state for entity.
     *
     * @param entityId target entity identifier
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void removeRuleNodeStateForEntity(EntityId entityId) {
        if (log.isDebugEnabled()) {
            log.debug("[{}][{}][{}] Remove Rule Node State for entity.", getTenantId(), getSelfId(), entityId);
        }
        mainCtx.getRuleNodeStateService().removeByRuleNodeIdAndEntityId(getTenantId(), getSelfId(), entityId);
    }
    /**
     * Add tenant profile listener.
     *
     * @param listener listener ({@link Consumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void addTenantProfileListener(Consumer<TenantProfile> listener) {
        mainCtx.getTenantProfileCache().addListener(getTenantId(), getSelfId(), listener);
    }
    /**
     * Add device profile listeners.
     *
     * @param profileListener profile listener ({@link Consumer})
     * @param deviceListener device listener ({@link BiConsumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void addDeviceProfileListeners(Consumer<DeviceProfile> profileListener, BiConsumer<DeviceId, DeviceProfile> deviceListener) {
        mainCtx.getDeviceProfileCache().addListener(getTenantId(), getSelfId(), profileListener, deviceListener);
    }
    /**
     * Add asset profile listeners.
     *
     * @param profileListener profile listener ({@link Consumer})
     * @param assetListener asset listener ({@link BiConsumer})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void addAssetProfileListeners(Consumer<AssetProfile> profileListener, BiConsumer<AssetId, AssetProfile> assetListener) {
        mainCtx.getAssetProfileCache().addListener(getTenantId(), getSelfId(), profileListener, assetListener);
    }
    /**
     * Removes listeners.
     *
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void removeListeners() {
        mainCtx.getDeviceProfileCache().removeListener(getTenantId(), getSelfId());
        mainCtx.getAssetProfileCache().removeListener(getTenantId(), getSelfId());
        mainCtx.getTenantProfileCache().removeListener(getTenantId(), getSelfId());
    }
    /**
     * Returns tenant profile.
     *
     * @return {@link TenantProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TenantProfile getTenantProfile() {
        return mainCtx.getTenantProfileCache().get(getTenantId());
    }
    /**
     * Returns widget bundle service.
     *
     * @return {@link WidgetsBundleService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetsBundleService getWidgetBundleService() {
        return mainCtx.getWidgetsBundleService();
    }
    /**
     * Returns widget type service.
     *
     * @return {@link WidgetTypeService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public WidgetTypeService getWidgetTypeService() {
        return mainCtx.getWidgetTypeService();
    }
    /**
     * Returns rule engine api usage state service.
     *
     * @return {@link RuleEngineApiUsageStateService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleEngineApiUsageStateService getRuleEngineApiUsageStateService() {
        return mainCtx.getApiUsageStateService();
    }
    /**
     * Returns entity service.
     *
     * @return {@link EntityService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EntityService getEntityService() {
        return mainCtx.getEntityService();
    }
    /**
     * Returns event service.
     *
     * @return {@link EventService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public EventService getEventService() {
        return mainCtx.getEventService();
    }
    /**
     * Returns audit log service.
     *
     * @return {@link AuditLogService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public AuditLogService getAuditLogService() {
        return mainCtx.getAuditLogService();
    }
    /**
     * Returns ai chat model service.
     *
     * @return {@link RuleEngineAiChatModelService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public RuleEngineAiChatModelService getAiChatModelService() {
        return mainCtx.getAiChatModelService();
    }
    /**
     * Returns ai model service.
     *
     * @return {@link AiModelService}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public AiModelService getAiModelService() {
        return mainCtx.getAiModelService();
    }
    /**
     * Returns mqtt client settings.
     *
     * @return {@link MqttClientSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public MqttClientSettings getMqttClientSettings() {
        return mainCtx.getMqttClientSettings();
    }
    /**
     * Returns tb http client settings.
     *
     * @return {@link TbHttpClientSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public TbHttpClientSettings getTbHttpClientSettings() {
        return mainCtx.getTbHttpClientSettings();
    }

    private TbMsgMetaData getActionMetaData(RuleNodeId ruleNodeId) {
        TbMsgMetaData metaData = new TbMsgMetaData();
        metaData.putValue("ruleNodeId", ruleNodeId.toString());
        return metaData;
    }
    /**
     * Schedule.
     *
     * @param runnable runnable ({@link Runnable})
     * @param delay delay
     * @param timeUnit time unit ({@link TimeUnit})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */

    @Override
    public void schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        mainCtx.getScheduler().schedule(runnable, delay, timeUnit);
    }
    /**
     * Checks tenant entity.
     *
     * @param entityId target entity identifier
     * @return nothing
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    @Override
    public void checkTenantEntity(EntityId entityId) throws TbNodeException {
        TenantId actualTenantId = TenantIdLoader.findTenantId(this, entityId);
        if (!getTenantId().equals(actualTenantId)) {
            throw new TbNodeException("Entity with id: '" + entityId + "' specified in the configuration doesn't belong to the current tenant.", true);
        }
    }

    @Override
    public <E extends HasId<I> & HasTenantId, I extends EntityId> void checkTenantOrSystemEntity(E entity) throws TbNodeException {
        TenantId actualTenantId = entity.getTenantId();
        if (!getTenantId().equals(actualTenantId) && !actualTenantId.isSysTenantId()) {
            throw new TbNodeException("Entity with id: '" + entity.getId() + "' specified in the configuration doesn't belong to the current or system tenant.", true);
        }
    }

    private static String getFailureMessage(Throwable th) {
        String failureMessage;
        if (th != null) {
            if (!StringUtils.isEmpty(th.getMessage())) {
                failureMessage = th.getMessage();
            } else {
                failureMessage = th.getClass().getSimpleName();
            }
        } else {
            failureMessage = null;
        }
        return failureMessage;
    }

    private void persistDebugOutput(TbMsg msg, String relationType) {
        persistDebugOutput(msg, Set.of(relationType));
    }

    private void persistDebugOutput(TbMsg msg, Set<String> relationTypes) {
        persistDebugOutput(msg, relationTypes, null, null);
    }

    private void persistDebugOutput(TbMsg msg, Set<String> relationTypes, Throwable error, String failureMessage) {
        RuleNode ruleNode = nodeCtx.getSelf();
        if (DebugModeUtil.isDebugAllAvailable(ruleNode)) {
            relationTypes.forEach(relationType -> mainCtx.persistDebugOutput(getTenantId(), ruleNode.getId(), msg, relationType, error, failureMessage));
        } else if (DebugModeUtil.isDebugFailuresAvailable(ruleNode, relationTypes)) {
            mainCtx.persistDebugOutput(getTenantId(), ruleNode.getId(), msg, TbNodeConnectionType.FAILURE, error, failureMessage);
        }
    }

}
