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
package org.thingsboard.rule.engine.api;

import io.netty.channel.EventLoopGroup;
import org.thingsboard.common.util.ExecutorProvider;
import org.thingsboard.common.util.ListeningExecutor;
import org.thingsboard.rule.engine.api.notification.SlackService;
import org.thingsboard.rule.engine.api.sms.SmsSenderFactory;
import org.thingsboard.server.cluster.TbClusterService;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.HasTenantId;
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
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rule.RuleNode;
import org.thingsboard.server.common.data.rule.RuleNodeState;
import org.thingsboard.server.common.data.script.ScriptLanguage;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;
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
import org.thingsboard.server.dao.resource.ResourceService;
import org.thingsboard.server.dao.resource.TbResourceDataCache;
import org.thingsboard.server.dao.rule.RuleChainService;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.dao.timeseries.TimeseriesService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.widget.WidgetTypeService;
import org.thingsboard.server.dao.widget.WidgetsBundleService;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Execution context passed to every rule node invocation.
 *
 * <p>Provides message routing ({@link #tellSuccess}, {@link #tellNext}, {@link #tellFailure}), DAO services, script engine, cluster APIs, and tenant-scoped helpers.
 */

public interface TbContext {

    /*
     *
     *  METHODS TO CONTROL THE MESSAGE FLOW
     *
     */

    
    /**
     * Routes the message to the Success connection of the current node.
     *
     * @param msg incoming or outgoing rule engine message
     * @throws Exception if an unexpected error occurs during processing
     */

    void tellSuccess(TbMsg msg);

    
    /**
     * Routes the message to a named output relation (True, False, Custom, etc.).
     *
     * @param msg incoming or outgoing rule engine message
     * @param relationType output connection name (Success, Failure, True, False, etc.)
     * @throws Exception if an unexpected error occurs during processing
     */

    void tellNext(TbMsg msg, String relationType);

    
    /**
     * Routes the message to a named output relation (True, False, Custom, etc.).
     *
     * @param msg incoming or outgoing rule engine message
     * @param relationTypes relation types ({@link Set})
     * @throws Exception if an unexpected error occurs during processing
     */

    void tellNext(TbMsg msg, Set<String> relationTypes);

    
    /**
     * Tell self.
     *
     * @param msg incoming or outgoing rule engine message
     * @param delayMs delay ms
     * @throws Exception if an unexpected error occurs during processing
     */

    void tellSelf(TbMsg msg, long delayMs);

    
    /**
     * Routes the message to the Failure connection with an error message.
     *
     * @param msg incoming or outgoing rule engine message
     * @param th th ({@link Throwable})
     * @throws Exception if an unexpected error occurs during processing
     */

    void tellFailure(TbMsg msg, Throwable th);

    
   /**
    * Enqueues a new message for processing by the rule engine.
    *
    * @param msg incoming or outgoing rule engine message
    * @param onSuccess on success ({@link Runnable})
    * @param onFailure on failure ({@link Consumer})
    * @throws Exception if an unexpected error occurs during processing
    */

    void enqueue(TbMsg msg, Runnable onSuccess, Consumer<Throwable> onFailure);

    
  /**
   * Enqueues a new message for processing by the rule engine.
   *
   * @param msg incoming or outgoing rule engine message
   * @param queueName queue name ({@link String})
   * @param onSuccess on success ({@link Runnable})
   * @param onFailure on failure ({@link Consumer})
   * @throws Exception if an unexpected error occurs during processing
   */

    void enqueue(TbMsg msg, String queueName, Runnable onSuccess, Consumer<Throwable> onFailure);

    
    /**
     * Input.
     *
     * @param msg incoming or outgoing rule engine message
     * @param ruleChainId rule chain id ({@link RuleChainId})
     * @throws Exception if an unexpected error occurs during processing
     */

    void input(TbMsg msg, RuleChainId ruleChainId);

    
    /**
     * Output.
     *
     * @param msg incoming or outgoing rule engine message
     * @param relationType output connection name (Success, Failure, True, False, etc.)
     * @throws Exception if an unexpected error occurs during processing
     */

    void output(TbMsg msg, String relationType);
    /**
     * Enqueue for tell failure.
     *
     * @param msg incoming or outgoing rule engine message
     * @param failureMessage failure message ({@link String})
     * @throws Exception if an unexpected error occurs during processing
     */

    void enqueueForTellFailure(TbMsg msg, String failureMessage);
    /**
     * Enqueue for tell failure.
     *
     * @param tbMsg rule engine message being processed
     * @param t t ({@link Throwable})
     * @throws Exception if an unexpected error occurs during processing
     */

    void enqueueForTellFailure(TbMsg tbMsg, Throwable t);
    /**
     * Enqueue for tell next.
     *
     * @param msg incoming or outgoing rule engine message
     * @param relationType output connection name (Success, Failure, True, False, etc.)
     * @throws Exception if an unexpected error occurs during processing
     */

    void enqueueForTellNext(TbMsg msg, String relationType);
    /**
     * Enqueue for tell next.
     *
     * @param msg incoming or outgoing rule engine message
     * @param relationTypes relation types ({@link Set})
     * @throws Exception if an unexpected error occurs during processing
     */

    void enqueueForTellNext(TbMsg msg, Set<String> relationTypes);
    /**
     * Enqueue for tell next.
     *
     * @param msg incoming or outgoing rule engine message
     * @param relationType output connection name (Success, Failure, True, False, etc.)
     * @param onSuccess on success ({@link Runnable})
     * @param onFailure on failure ({@link Consumer})
     * @throws Exception if an unexpected error occurs during processing
     */

    void enqueueForTellNext(TbMsg msg, String relationType, Runnable onSuccess, Consumer<Throwable> onFailure);
    /**
     * Enqueue for tell next.
     *
     * @param msg incoming or outgoing rule engine message
     * @param relationTypes relation types ({@link Set})
     * @param onSuccess on success ({@link Runnable})
     * @param onFailure on failure ({@link Consumer})
     * @throws Exception if an unexpected error occurs during processing
     */

    void enqueueForTellNext(TbMsg msg, Set<String> relationTypes, Runnable onSuccess, Consumer<Throwable> onFailure);
    /**
     * Enqueue for tell next.
     *
     * @param msg incoming or outgoing rule engine message
     * @param queueName queue name ({@link String})
     * @param relationType output connection name (Success, Failure, True, False, etc.)
     * @param onSuccess on success ({@link Runnable})
     * @param onFailure on failure ({@link Consumer})
     * @throws Exception if an unexpected error occurs during processing
     */

    void enqueueForTellNext(TbMsg msg, String queueName, String relationType, Runnable onSuccess, Consumer<Throwable> onFailure);
    /**
     * Enqueue for tell next.
     *
     * @param msg incoming or outgoing rule engine message
     * @param queueName queue name ({@link String})
     * @param relationTypes relation types ({@link Set})
     * @param onSuccess on success ({@link Runnable})
     * @param onFailure on failure ({@link Consumer})
     * @throws Exception if an unexpected error occurs during processing
     */

    void enqueueForTellNext(TbMsg msg, String queueName, Set<String> relationTypes, Runnable onSuccess, Consumer<Throwable> onFailure);
    /**
     * Ack.
     *
     * @param tbMsg rule engine message being processed
     * @throws Exception if an unexpected error occurs during processing
     */

    void ack(TbMsg tbMsg);

    
    /**
     * Schedule.
     *
     * @param runnable runnable ({@link Runnable})
     * @param delay delay
     * @param timeUnit time unit ({@link TimeUnit})
     * @throws TbNodeException if tb node exception is thrown during processing
     */


    void schedule(Runnable runnable, long delay, TimeUnit timeUnit);
    /**
     * Checks tenant entity.
     *
     * @param entityId target entity identifier
     * @throws TbNodeException if tb node exception is thrown during processing
     */

    void checkTenantEntity(EntityId entityId) throws TbNodeException;

    <E extends HasId<I> & HasTenantId, I extends EntityId> void checkTenantOrSystemEntity(E entity) throws TbNodeException;
    /**
     * Is local entity.
     *
     * @param entityId target entity identifier
     * @return the boolean result
     * @throws Exception if an unexpected error occurs duri
    /**
     * New msg.
     *
     * @param queueName queue name ({@link String})
     * @param type type ({@link String})
     * @param originator message originator entity id
     * @param customerId customer id ({@link CustomerId})
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
ng processing
     */

    boolean isLocalEntity(EntityId entityId);
    /**
     * Returns the rule node id of the currently executing node.
     *
     * @return
    /**
     * Transform msg.
     *
     * @param origMsg orig msg ({@link TbMsg})
     * @param type type ({@link String})
     * @param originator message originator entity id
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
 {@link RuleNodeId}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleNodeId getSelfId();
    /**
     * Returns self.
    
    /**
     * New msg.
     *
     * @param queueName queue name ({@link String})
     * @param type type ({@link TbMsgType})
     * @param originator message originator entity id
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
 *
     * @return {@link RuleNode}
     * @throws Exception if an unexpected error occurs during processing
   
    /**
     * New msg.
     *
     * @param queueName queue name ({@link String})
     * @param type type ({@link TbMsgType})
     * @param originator message originator entity id
     * @param customerId customer id ({@link CustomerId})
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
  */

    RuleNode getSelf();
    /**
     * Returns rule chain name.
     *
     * @return {@link String}
     * @throws Exception if
    /**
     * Transform msg.
     *
     * @param origMsg orig msg ({@link TbMsg})
     * @param type type ({@link TbMsgType})
     * @param originator message originator entity id
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
 an unexpected error occurs during processing
     */

    String getRuleChainName();
    /**
     * Returns queue
    /**
     * Transform msg.
     *
     * @param origMsg orig msg ({@link TbMsg})
     * @param metaData meta data ({@link TbMsgMetaData})
     * @param data data ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
 name.
     *
     * @return {@link String}
     * @throws Exception if an un
    /**
     * Transform msg originator.
     *
     * @param origMsg orig msg ({@link TbMsg})
     * @param originator message originator entity id
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
expected error occurs during processing
     */

    String getQueueNam
    /**
     * Customer created msg.
     *
     * @param customer customer ({@link Customer})
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
e();
    
    /**
     * Alarm action msg.
     *
     * @param alarm ala
    /**
     * Device created msg.
     *
     * @param device device ({@link Device})
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
rm ({@link Alarm})
     * @param ruleNodeId rule node id ({@link Ru
    /**
     * Asset created msg.
     *
     * @param asset asset ({@link Asset})
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
leNodeId})
     * @param action action ({@link String})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */


    TenantId getTenantId();
    /**
     * Returns attributes service.
     *
     * @return {@link AttributesService}
     * @throws 
    /**
     * Alarm action msg.
     *
     * @param alarm alarm ({@link Alarm})
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @param actionMsgType action msg type ({@link TbMsgType})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
Exception if an unexpected error occurs during processing
     */

    AttributesService
    /**
     * Attributes updated action msg.
     *
     * @param originator message originator entity id
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @param scope scope ({@link String})
     * @param attributes attributes ({@link List})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
 getAttributesService();
    /**
     * Returns customer service.
     *
     * @return {@link CustomerService}
     * @throws Excep
    /**
     * Attributes deleted action msg.
     *
     * @param originator message originator entity id
     * @param ruleNodeId rule node id ({@link RuleNodeId})
     * @param scope scope ({@link String})
     * @param keys keys ({@link List})
     * @return {@link TbMsg}
     * @throws Exception if an unexpected error occurs during processing
     */
tion if an unexpected error occurs during processing
     */

    CustomerService getCustomerService();
    /**
     * Returns tenant service.
     *
     * @return {@link TenantService}
     * @throws Exception if an unexpected error occurs during processing
     */

    TenantService getTenantService();
    /**
     * Returns user service.
     *
     * @return {@link UserService}
     * @throws Exception if an unexpected error occurs during processing
     */

    UserService getUserService();
    /**
     * Returns asset service.
     *
     * @return {@link AssetService}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetService getAssetService();
    /**
     * Returns device service.
     *
     * @return {@link DeviceService}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceService getDeviceService();
    /**
     * Returns device profile service.
     *
     * @return {@link DeviceProfileService}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceProfileService getDeviceProfileService();
    /**
     * Returns asset profile service.
     *
     * @return {@link AssetProfileService}
     * @throws Exception if an unexpected error occurs during processing
     */

    AssetProfileService getAssetProfileService();
    /**
     * Returns device credentials service.
     *
     * @return {@link DeviceCredentialsService}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceCredentialsService getDeviceCredentialsService();
    /**
     * Returns device state manager.
     *
     * @return {@link DeviceStateManager}
     * @throws Exception if an unexpected error occurs during processing
     */

    DeviceStateManager getDeviceStateManager();
    /**
     * Returns device state node rate limit config.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    String getDeviceStateNodeRateLimitConfig();
    /**
     * Returns cluster service.
     *
     * @return {@link TbClusterService}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbClusterService getClusterService();
    /**
     * Returns dashboard service.
     *
     * @return {@link DashboardService}
     * @throws Exception if an unexpected error occurs during processing
     */

    DashboardService getDashboardService();
    /**
     * Returns alarm service.
     *
     * @return {@link RuleEngineAlarmService}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleEngineAlarmService getAlarmService();
    /**
     * Returns alarm comment service.
     *
     * @return {@link AlarmCommentService}
     * @throws Exception if an unexpected error occurs during processing
     */

    AlarmCommentService getAlarmCommentService();
    /**
     * Returns rule chain service.
     *
     * @return {@link RuleChainService}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleChainService getRuleChainService();
    /**
     * Returns rpc service.
     *
     * @return {@link RuleEngineRpcService}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleEngineRpcService getRpcService();
    /**
     * Returns telemetry service.
     *
     * @return {@link RuleEngineTelemetryService}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleEngineTelemetryService getTelemetryService();
    /**
     * Returns timeseries service.
     *
     * @return {@link TimeseriesService}
     * @throws Exception if an unexpected error occurs during processing
     */

    TimeseriesService getTimeseriesService();
    /**
     * Returns relation service.
     *
     * @return {@link RelationService}
     * @throws Exception if an unexpected error occurs during processing
     */

    RelationService getRelationService();
    /**
     * Returns entity view service.
     *
     * @return {@link EntityViewService}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityViewService getEntityViewService();
    /**
     * Returns resource service.
     *
     * @return {@link ResourceService}
     * @throws Exception if an unexpected error occurs during processing
     */

    ResourceService getResourceService();
    /**
     * Returns tb resource data cache.
     *
     * @return {@link TbResourceDataCache}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResourceDataCache getTbResourceDataCache();
    /**
     * Returns ota package service.
     *
     * @return {@link OtaPackageService}
     * @throws Exception if an unexpected error occurs during processing
     */

    OtaPackageService getOtaPackageService();
    /**
     * Returns device profile cache.
     *
     * @return {@link RuleEngineDeviceProfileCache}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleEngineDeviceProfileCache getDeviceProfileCache();
    /**
     * Returns asset profile cache.
     *
     * @return {@link RuleEngineAssetProfileCache}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleEngineAssetProfileCache getAssetProfileCache();
    /**
     * Returns edge service.
     *
     * @return {@link EdgeService}
     * @throws Exception if an unexpected error occurs during processing
     */

    EdgeService getEdgeService();
    /**
     * Returns edge event service.
     *
     * @return {@link EdgeEventService}
     * @throws Exception if an unexpected error occurs during processing
     */

    EdgeEventService getEdgeEventService();
    /**
     * Returns queue service.
     *
     * @return {@link QueueService}
     * @throws Exception if an unexpected error occurs during processing
     */

    QueueService getQueueService();
    /**
     * Returns queue stats service.
     *
     * @return {@link QueueStatsService}
     * @throws Exception if an unexpected error occurs during processing
     */

    QueueStatsService getQueueStatsService();
    /**
     * Returns mail executor.
     *
     * @return {@link ListeningExecutor}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListeningExecutor getMailExecutor();
    /**
     * Returns sms executor.
     *
     * @return {@link ListeningExecutor}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListeningExecutor getSmsExecutor();
    /**
     * Returns db callback executor.
     *
     * @return {@link ListeningExecutor}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListeningExecutor getDbCallbackExecutor();
    /**
     * Returns external call executor.
     *
     * @return {@link ListeningExecutor}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListeningExecutor getExternalCallExecutor();
    /**
     * Returns notification executor.
     *
     * @return {@link ListeningExecutor}
     * @throws Exception if an unexpected error occurs during processing
     */

    ListeningExecutor getNotificationExecutor();
    /**
     * Returns pub sub rule node executor provider.
     *
     * @return {@link ExecutorProvider}
     * @throws Exception if an unexpected error occurs during processing
     */

    ExecutorProvider getPubSubRuleNodeExecutorProvider();
    /**
     * Returns mail service.
     *
     * @param isSystem is system
     * @return {@link MailService}
     * @throws Exception if an unexpected error occurs during processing
     */

    MailService getMailService(boolean isSystem);
    /**
     * Returns sms service.
     *
     * @return {@link SmsService}
     * @throws Exception if an unexpected error occurs during processing
     */

    SmsService getSmsService();
    /**
     * Returns sms sender factory.
     *
     * @return {@link SmsSenderFactory}
     * @throws Exception if an unexpected error occurs during processing
     */

    SmsSenderFactory getSmsSenderFactory();
    /**
     * Returns notification center.
     *
     * @return {@link NotificationCenter}
     * @throws Exception if an unexpected error occurs during processing
     */

    NotificationCenter getNotificationCenter();
    /**
     * Returns notification target service.
     *
     * @return {@link NotificationTargetService}
     * @throws Exception if an unexpected error occurs during processing
     */

    NotificationTargetService getNotificationTargetService();
    /**
     * Returns notification template service.
     *
     * @return {@link NotificationTemplateService}
     * @throws Exception if an unexpected error occurs during processing
     */

    NotificationTemplateService getNotificationTemplateService();
    /**
     * Returns notification request service.
     *
     * @return {@link NotificationRequestService}
     * @throws Exception if an unexpected error occurs during processing
     */

    NotificationRequestService getNotificationRequestService();
    /**
     * Returns notification rule service.
     *
     * @return {@link NotificationRuleService}
     * @throws Exception if an unexpected error occurs during processing
     */

    NotificationRuleService getNotificationRuleService();
    /**
     * Returns oauth2client service.
     *
     * @return {@link OAuth2ClientService}
     * @throws Exception if an unexpected error occurs during processing
     */

    OAuth2ClientService getOAuth2ClientService();
    /**
     * Returns domain service.
     *
     * @return {@link DomainService}
     * @throws Exception if an unexpected error occurs during processing
     */

    DomainService getDomainService();
    /**
     * Returns mobile app service.
     *
     * @return {@link MobileAppService}
     * @throws Exception if an unexpected error occurs during processing
     */

    MobileAppService getMobileAppService();
    /**
     * Returns mobile app bundle service.
     *
     * @return {@link MobileAppBundleService}
     * @throws Exception if an unexpected error occurs during processing
     */

    MobileAppBundleService getMobileAppBundleService();
    /**
     * Returns slack service.
     *
     * @return {@link SlackService}
     * @throws Exception if an unexpected error occurs during processing
     */

    SlackService getSlackService();
    /**
     * Returns calculated field service.
     *
     * @return {@link CalculatedFieldService}
     * @throws Exception if an unexpected error occurs during processing
     */

    CalculatedFieldService getCalculatedFieldService();
    /**
     * Returns calculated field queue service.
     *
     * @return {@link RuleEngineCalculatedFieldQueueService}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleEngineCalculatedFieldQueueService getCalculatedFieldQueueService();
    /**
     * Returns job service.
     *
     * @return {@link JobService}
     * @throws Exception if an unexpected error occurs during processing
     */

    JobService getJobService();
    /**
     * Returns job manager.
     *
     * @return {@link JobManager}
     * @throws Exception if an unexpected error occurs during processing
     */

    JobManager getJobManager();
    /**
     * Returns api key service.
     *
     * @return {@link ApiKeyService}
     * @throws Exception if an unexpected error occurs during processing
     */

    ApiKeyService getApiKeyService();
    /**
     * Is external node force ack.
     *
     * @return the boolean result
     * @throws Exception if an unexpected error occurs during processing
     */

    boolean isExternalNodeForceAck();

    
    /**
     * Creates js script engine.
     *
     * @param script script ({@link String})
     * @param argNames arg names
     * @return {@link ScriptEngine}
     * @throws Exception if an unexpected error occurs during processing
     */

    @Deprecated
    ScriptEngine createJsScriptEngine(String script, String... argNames);
    /**
     * Creates a TBEL or JavaScript script engine for filter/transform nodes.
     *
     * @param scriptLang script lang ({@link ScriptLanguage})
     * @param script script ({@link String})
     * @param argNames arg names
     * @return {@link ScriptEngine}
     * @throws Exception if an unexpected error occurs during processing
     */

    ScriptEngine createScriptEngine(ScriptLanguage scriptLang, String script, String... argNames);
    /**
     * Returns service id.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */

    String getServiceId();
    /**
     * Returns shared event loop.
     *
     * @return {@link EventLoopGroup}
     * @throws Exception if an unexpected error occurs during processing
     */

    EventLoopGroup getSharedEventLoop();
    /**
     * Returns cassandra cluster.
     *
     * @return {@link CassandraCluster}
     * @throws Exception if an unexpected error occurs during processing
     */

    CassandraCluster getCassandraCluster();
    /**
     * Submit cassandra read task.
     *
     * @param task task ({@link CassandraStatementTask})
     * @return {@link TbResultSetFuture}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResultSetFuture submitCassandraReadTask(CassandraStatementTask task);
    /**
     * Submit cassandra write task.
     *
     * @param task task ({@link CassandraStatementTask})
     * @return {@link TbResultSetFuture}
     * @throws Exception if an unexpected error occurs during processing
     */

    TbResultSetFuture submitCassandraWriteTask(CassandraStatementTask task);
    /**
     * Finds rule node states.
     *
     * @param pageLink pagination and sort parameters
     * @return {@link PageData}
     * @throws Exception if an unexpected error occurs during processing
     */

    PageData<RuleNodeState> findRuleNodeStates(PageLink pageLink);
    /**
     * Finds rule node state for entity.
     *
     * @param entityId target entity identifier
     * @return {@link RuleNodeState}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleNodeState findRuleNodeStateForEntity(EntityId entityId);
    /**
     * Removes rule node state for entity.
     *
     * @param entityId target entity identifier
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeRuleNodeStateForEntity(EntityId entityId);
    /**
     * Saves or persists rule node state.
     *
     * @param state state ({@link RuleNodeState})
     * @return {@link RuleNodeState}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleNodeState saveRuleNodeState(RuleNodeState state);
    /**
     * Clear rule node states.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    void clearRuleNodeStates();
    /**
     * Add tenant profile listener.
     *
     * @param listener listener ({@link Consumer})
     * @throws Exception if an unexpected error occurs during processing
     */

    void addTenantProfileListener(Consumer<TenantProfile> listener);
    /**
     * Add device profile listeners.
     *
     * @param listener listener ({@link Consumer})
     * @param deviceListener device listener ({@link BiConsumer})
     * @throws Exception if an unexpected error occurs during processing
     */

    void addDeviceProfileListeners(Consumer<DeviceProfile> listener, BiConsumer<DeviceId, DeviceProfile> deviceListener);
    /**
     * Add asset profile listeners.
     *
     * @param listener listener ({@link Consumer})
     * @param assetListener asset listener ({@link BiConsumer})
     * @throws Exception if an unexpected error occurs during processing
     */

    void addAssetProfileListeners(Consumer<AssetProfile> listener, BiConsumer<AssetId, AssetProfile> assetListener);
    /**
     * Removes listeners.
     *
     * @throws Exception if an unexpected error occurs during processing
     */

    void removeListeners();
    /**
     * Returns tenant profile.
     *
     * @return {@link TenantProfile}
     * @throws Exception if an unexpected error occurs during processing
     */

    TenantProfile getTenantProfile();
    /**
     * Returns widget bundle service.
     *
     * @return {@link WidgetsBundleService}
     * @throws Exception if an unexpected error occurs during processing
     */

    WidgetsBundleService getWidgetBundleService();
    /**
     * Returns widget type service.
     *
     * @return {@link WidgetTypeService}
     * @throws Exception if an unexpected error occurs during processing
     */

    WidgetTypeService getWidgetTypeService();
    /**
     * Returns rule engine api usage state service.
     *
     * @return {@link RuleEngineApiUsageStateService}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleEngineApiUsageStateService getRuleEngineApiUsageStateService();
    /**
     * Returns entity service.
     *
     * @return {@link EntityService}
     * @throws Exception if an unexpected error occurs during processing
     */

    EntityService getEntityService();
    /**
     * Returns event service.
     *
     * @return {@link EventService}
     * @throws Exception if an unexpected error occurs during processing
     */

    EventService getEventService();
    /**
     * Returns audit log service.
     *
     * @return {@link AuditLogService}
     * @throws Exception if an unexpected error occurs during processing
     */

    AuditLogService getAuditLogService();
    /**
     * Returns ai chat model service.
     *
     * @return {@link RuleEngineAiChatModelService}
     * @throws Exception if an unexpected error occurs during processing
     */

    RuleEngineAiChatModelService getAiChatModelService();
    /**
     * Returns ai model service.
     *
     * @return {@link AiModelService}
     * @throws Exception if an unexpected error occurs during processing
     */

    AiModelService getAiModelService();

    // Configuration parameters for the MQTT client that is used in the MQTT node and Azure IoT hub node
    /**
     * Returns mqtt client settings.
     *
     * @return {@link MqttClientSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    MqttClientSettings getMqttClientSettings();

    // Server-level safety caps for the HTTP client used by the REST API Call rule node (read from thingsboard.yml)
    /**
     * Returns tb http client settings.
     *
     * @return {@link TbHttpClientSettings}
     * @throws Exception if an unexpected error occurs during processing
     */

    default TbHttpClientSettings getTbHttpClientSettings() {
        return TbHttpClientSettings.DEFAULT;
    }

}
