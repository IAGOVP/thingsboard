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
package org.thingsboard.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.ListenableFuture;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.thingsboard.common.util.DonAsynchron;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.cluster.TbClusterService;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.DashboardInfo;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceInfo;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.EntityViewInfo;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.HomeDashboardInfo;
import org.thingsboard.server.common.data.OtaPackage;
import org.thingsboard.server.common.data.OtaPackageInfo;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.TbResourceInfo;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.TenantInfo;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.ai.AiModel;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmComment;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetInfo;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.cf.CalculatedField;
import org.thingsboard.server.common.data.cf.configuration.CalculatedFieldConfiguration;
import org.thingsboard.server.common.data.domain.Domain;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeInfo;
import org.thingsboard.server.common.data.exception.EntityVersionMismatchException;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.AiModelId;
import org.thingsboard.server.common.data.id.AlarmCommentId;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.ApiKeyId;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.CalculatedFieldId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.DomainId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityIdFactory;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.JobId;
import org.thingsboard.server.common.data.id.MobileAppBundleId;
import org.thingsboard.server.common.data.id.MobileAppId;
import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.id.OAuth2ClientId;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.QueueId;
import org.thingsboard.server.common.data.id.RpcId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.RuleNodeId;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.id.UUIDBased;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.job.Job;
import org.thingsboard.server.common.data.mobile.app.MobileApp;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundle;
import org.thingsboard.server.common.data.notification.targets.NotificationTarget;
import org.thingsboard.server.common.data.oauth2.OAuth2Client;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.pat.ApiKey;
import org.thingsboard.server.common.data.plugin.ComponentDescriptor;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.query.EntityDataSortOrder;
import org.thingsboard.server.common.data.query.EntityKey;
import org.thingsboard.server.common.data.queue.Queue;
import org.thingsboard.server.common.data.rpc.Rpc;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.common.data.rule.RuleNode;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.common.data.util.ThrowingBiFunction;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetTypeInfo;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.dao.ai.AiModelService;
import org.thingsboard.server.dao.alarm.AlarmCommentService;
import org.thingsboard.server.dao.asset.AssetProfileService;
import org.thingsboard.server.dao.asset.AssetService;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.audit.AuditLogService;
import org.thingsboard.server.dao.cf.CalculatedFieldService;
import org.thingsboard.server.dao.customer.CustomerService;
import org.thingsboard.server.dao.dashboard.DashboardService;
import org.thingsboard.server.dao.device.ClaimDevicesService;
import org.thingsboard.server.dao.device.DeviceCredentialsService;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.domain.DomainService;
import org.thingsboard.server.dao.edge.EdgeService;
import org.thingsboard.server.dao.entityview.EntityViewService;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.job.JobService;
import org.thingsboard.server.dao.mobile.MobileAppBundleService;
import org.thingsboard.server.dao.mobile.MobileAppService;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.notification.NotificationTargetService;
import org.thingsboard.server.dao.oauth2.OAuth2ClientService;
import org.thingsboard.server.dao.oauth2.OAuth2ConfigTemplateService;
import org.thingsboard.server.dao.ota.OtaPackageService;
import org.thingsboard.server.dao.pat.ApiKeyService;
import org.thingsboard.server.dao.queue.QueueService;
import org.thingsboard.server.dao.relation.RelationService;
import org.thingsboard.server.dao.resource.ResourceService;
import org.thingsboard.server.dao.rpc.RpcService;
import org.thingsboard.server.dao.rule.RuleChainService;
import org.thingsboard.server.dao.service.ConstraintValidator;
import org.thingsboard.server.dao.service.Validator;
import org.thingsboard.server.dao.tenant.TbTenantProfileCache;
import org.thingsboard.server.dao.tenant.TenantProfileService;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.widget.WidgetTypeService;
import org.thingsboard.server.dao.widget.WidgetsBundleService;
import org.thingsboard.server.exception.DataValidationException;
import org.thingsboard.server.exception.EntitiesLimitExceededException;
import org.thingsboard.server.exception.ThingsboardErrorResponseHandler;
import org.thingsboard.server.queue.discovery.PartitionService;
import org.thingsboard.server.queue.discovery.TbServiceInfoProvider;
import org.thingsboard.server.queue.provider.TbQueueProducerProvider;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.action.EntityActionService;
import org.thingsboard.server.service.component.ComponentDiscoveryService;
import org.thingsboard.server.service.entitiy.TbLogEntityActionService;
import org.thingsboard.server.service.entitiy.ai.TbAiModelService;
import org.thingsboard.server.service.entitiy.user.TbUserSettingsService;
import org.thingsboard.server.service.ota.OtaPackageStateService;
import org.thingsboard.server.service.profile.TbAssetProfileCache;
import org.thingsboard.server.service.profile.TbDeviceProfileCache;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.AccessControlService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;
import org.thingsboard.server.service.state.DeviceStateService;
import org.thingsboard.server.service.sync.ie.exporting.ExportableEntitiesService;
import org.thingsboard.server.service.sync.vc.EntitiesVersionControlService;
import org.thingsboard.server.service.telemetry.AlarmSubscriptionService;
import org.thingsboard.server.service.telemetry.TelemetrySubscriptionService;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

import static org.thingsboard.server.common.data.StringUtils.isNotEmpty;
import static org.thingsboard.server.common.data.query.EntityKeyType.ENTITY_FIELD;
import static org.thingsboard.server.controller.ControllerConstants.DEFAULT_DASHBOARD;
import static org.thingsboard.server.controller.ControllerConstants.HOME_DASHBOARD;
import static org.thingsboard.server.controller.UserController.YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION;
import static org.thingsboard.server.dao.service.Validator.validateId;

/**
 * Base class for all ThingsBoard REST API controllers.
 *
 * <p>Not mapped to a REST path directly; concrete controllers extend this class and define
 * {@code @RequestMapping} base paths (typically {@code /api}).
 *
 * <p>Provides shared infrastructure:
 * <ul>
 *   <li>Autowired DAO and service beans (tenant, device, asset, alarm, rule chain, etc.)</li>
 *   <li>{@link #getCurrentUser()} / {@link #getTenantId()} security context helpers</li>
 *   <li>Entity existence and {@link org.thingsboard.server.service.security.permission.AccessControlService} permission checks</li>
 *   <li>Pagination helpers ({@link #createPageLink}, {@link #createTimePageLink})</li>
 *   <li>Global {@code @ExceptionHandler} methods for {@link ThingsboardException} and validation errors</li>
 *   <li>Async {@link DeferredResult} wrapping via {@link #wrapFuture}</li>
 * </ul>
 *
 * <p>Type-specific helpers such as {@code checkDeviceId} load the entity and verify the caller
 * has the requested {@link org.thingsboard.server.service.security.permission.Operation} on the
 * corresponding {@link org.thingsboard.server.service.security.permission.Resource}.
 */
@TbCoreComponent
public abstract class BaseController {

    protected static final String HOME_DASHBOARD_ID = "homeDashboardId";
    protected static final String HOME_DASHBOARD_HIDE_TOOLBAR = "homeDashboardHideToolbar";

    protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    /*Swagger UI description*/

    @Autowired
    private ThingsboardErrorResponseHandler errorResponseHandler;

    @Autowired
    protected AccessControlService accessControlService;

    @Autowired
    protected TenantService tenantService;

    @Autowired
    protected TenantProfileService tenantProfileService;

    @Autowired
    protected CustomerService customerService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected TbUserSettingsService userSettingsService;

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected DeviceProfileService deviceProfileService;

    @Autowired
    protected AssetService assetService;

    @Autowired
    protected AssetProfileService assetProfileService;

    @Autowired
    protected AlarmSubscriptionService alarmService;

    @Autowired
    protected AlarmCommentService alarmCommentService;

    @Autowired
    protected DeviceCredentialsService deviceCredentialsService;

    @Autowired
    protected WidgetsBundleService widgetsBundleService;

    @Autowired
    protected WidgetTypeService widgetTypeService;

    @Autowired
    protected DashboardService dashboardService;

    @Autowired
    protected OAuth2ClientService oAuth2ClientService;

    @Autowired
    protected DomainService domainService;

    @Autowired
    protected MobileAppService mobileAppService;

    @Autowired
    protected MobileAppBundleService mobileAppBundleService;

    @Autowired
    protected OAuth2ConfigTemplateService oAuth2ConfigTemplateService;

    @Autowired
    protected ComponentDiscoveryService componentDescriptorService;

    @Autowired
    protected RuleChainService ruleChainService;

    @Autowired
    protected TbClusterService tbClusterService;

    @Autowired
    protected RelationService relationService;

    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    protected DeviceStateService deviceStateService;

    @Autowired
    protected EntityViewService entityViewService;

    @Autowired
    protected TelemetrySubscriptionService tsSubService;

    @Autowired
    protected AttributesService attributesService;

    @Autowired
    protected ClaimDevicesService claimDevicesService;

    @Autowired
    protected PartitionService partitionService;

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    protected OtaPackageService otaPackageService;

    @Autowired
    protected OtaPackageStateService otaPackageStateService;

    @Autowired
    protected RpcService rpcService;

    @Autowired
    protected TbQueueProducerProvider producerProvider;

    @Autowired
    protected TbTenantProfileCache tenantProfileCache;

    @Autowired
    protected TbDeviceProfileCache deviceProfileCache;

    @Autowired
    protected TbAssetProfileCache assetProfileCache;

    @Autowired(required = false)
    protected EdgeService edgeService;

    @Autowired
    protected TbLogEntityActionService logEntityActionService;

    @Autowired
    protected EntityActionService entityActionService;

    @Autowired
    protected QueueService queueService;

    @Autowired
    protected EntitiesVersionControlService vcService;

    @Autowired
    protected ExportableEntitiesService entitiesService;

    @Autowired
    protected TbServiceInfoProvider serviceInfoProvider;

    @Autowired
    protected NotificationTargetService notificationTargetService;

    @Autowired
    protected JobService jobService;

    @Autowired
    protected CalculatedFieldService calculatedFieldService;

    @Autowired
    protected AiModelService aiModelService;

    @Autowired
    protected TbAiModelService tbAiModelService;

    @Autowired
    protected ApiKeyService apiKeyService;

    @Value("${server.log_controller_error_stack_trace}")
    @Getter
    private boolean logControllerErrorStackTrace;

    @Value("${edges.enabled}")
    @Getter
    protected boolean edgesEnabled;

    @ExceptionHandler(Exception.class)
    /**
     * Global exception handler that maps any {@link Exception} to {@link ThingsboardException}
     * and delegates to {@link org.thingsboard.server.exception.ThingsboardErrorResponseHandler}.
     *
     * @param e the uncaught exception
     * @param response HTTP response to write the error payload to
     */
    public void handleControllerException(Exception e, HttpServletResponse response) {
        ThingsboardException thingsboardException = handleException(e);
        if (thingsboardException.getErrorCode() == ThingsboardErrorCode.GENERAL && thingsboardException.getCause() instanceof Exception
                && StringUtils.equals(thingsboardException.getCause().getMessage(), thingsboardException.getMessage())) {
            e = (Exception) thingsboardException.getCause();
        } else {
            e = thingsboardException;
        }
        errorResponseHandler.handle(e, response);
    }

    @ExceptionHandler(ThingsboardException.class)
    /**
     * Exception handler for {@link ThingsboardException} thrown from controller methods.
     *
     * @param ex the ThingsBoard exception
     * @param response HTTP response to write the error payload to
     */
    public void handleThingsboardException(ThingsboardException ex, HttpServletResponse response) {
        errorResponseHandler.handle(ex, response);
    }

    /**
     * @deprecated Exceptions that are not of {@link ThingsboardException} type
     * are now caught and mapped to {@link ThingsboardException} by
     * {@link ExceptionHandler} {@link BaseController#handleControllerException(Exception, HttpServletResponse)}
     * which basically acts like the following boilerplate:
     * {@code
     *  try {
     *      someExceptionThrowingMethod();
     *  } catch (Exception e) {
     *      throw handleException(e);
     *  }
     * }
     * */
    @Deprecated
    ThingsboardException handleException(Exception exception) {
        return handleException(exception, true);
    }

    private ThingsboardException handleException(Throwable exception, boolean logException) {
        if (logException && logControllerErrorStackTrace) {
            try {
                SecurityUser user = getCurrentUser();
                log.error("[{}][{}] Error", user.getTenantId(), user.getId(), exception);
            } catch (Exception e) {
                log.error("Error", exception);
            }
        }

        Throwable cause = exception.getCause();
        if (exception instanceof ExecutionException) {
            exception = cause;
        }
        if (exception instanceof ThingsboardException) {
            return (ThingsboardException) exception;
        } else if (exception instanceof EntitiesLimitExceededException) {
            return new ThingsboardException(exception, ThingsboardErrorCode.ENTITIES_LIMIT_EXCEEDED);
        } else if (exception instanceof IllegalArgumentException || exception instanceof IncorrectParameterException
                || exception instanceof DataValidationException || cause instanceof IncorrectParameterException) {
            return new ThingsboardException(exception.getMessage(), ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        } else if (exception instanceof MessagingException) {
            return new ThingsboardException("Unable to send mail", ThingsboardErrorCode.GENERAL);
        } else if (exception instanceof AsyncRequestTimeoutException) {
            return new ThingsboardException("Request timeout", ThingsboardErrorCode.GENERAL);
        } else if (exception instanceof DataAccessException) {
            return new ThingsboardException(exception, ThingsboardErrorCode.DATABASE);
        } else if (exception instanceof EntityVersionMismatchException) {
            return new ThingsboardException(exception.getMessage(), exception, ThingsboardErrorCode.VERSION_CONFLICT);
        } else if (exception instanceof MethodArgumentTypeMismatchException) {
            return new ThingsboardException(exception.getMessage(), exception, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
        return new ThingsboardException(exception.getMessage(), exception, ThingsboardErrorCode.GENERAL);
    }

    /**
     * Handles validation error for controller method arguments annotated with @{@link jakarta.validation.Valid}
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    /**
     * Exception handler for {@link jakarta.validation.Valid} constraint violations
     * on controller method arguments.
     *
     * @param validationError Spring validation exception
     * @param response HTTP response to write the error payload to
     */
    public void handleValidationError(MethodArgumentNotValidException validationError, HttpServletResponse response) {
        List<ConstraintViolation<Object>> constraintsViolations = validationError.getFieldErrors().stream()
                .map(fieldError -> {
                    try {
                        return (ConstraintViolation<Object>) fieldError.unwrap(ConstraintViolation.class);
                    } catch (Exception e) {
                        log.warn("FieldError source is not of type ConstraintViolation");
                        return null; // should not happen
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        String errorMessage = "Validation error: " + ConstraintValidator.getErrorMessage(constraintsViolations);
        ThingsboardException thingsboardException = new ThingsboardException(errorMessage, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        handleControllerException(thingsboardException, response);
    }

    <T> T checkNotNull(T reference) throws ThingsboardException {
        return checkNotNull(reference, "Requested item wasn't found!");
    }

    <T> T checkNotNull(T reference, String notFoundMessage) throws ThingsboardException {
        if (reference == null) {
            throw new ThingsboardException(notFoundMessage, ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        return reference;
    }

    <T> T checkNotNull(Optional<T> reference) throws ThingsboardException {
        return checkNotNull(reference, "Requested item wasn't found!");
    }

    <T> T checkNotNull(Optional<T> reference, String notFoundMessage) throws ThingsboardException {
        if (reference.isPresent()) {
            return reference.get();
        } else {
            throw new ThingsboardException(notFoundMessage, ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
    }

    static void checkParameter(String name, String param) throws ThingsboardException {
        if (StringUtils.isBlank(param)) {
            throw new ThingsboardException("Parameter '" + name + "' can't be empty!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    void checkArrayParameter(String name, String[] params) throws ThingsboardException {
        if (params == null || params.length == 0) {
            throw new ThingsboardException("Parameter '" + name + "' can't be empty!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        } else {
            for (String param : params) {
                checkParameter(name, param);
            }
        }
    }

    /**
     * Parses and validates an enum request parameter (case-insensitive via upper-casing).
     *
     * @param name parameter name for error messages
     * @param param raw string value
     * @param valueOf converter function (typically {@code Enum::valueOf})
     * @param <T> enum type
     * @return parsed enum value
     * @throws ThingsboardException if the value is not a valid enum constant
     */
    protected <T> T checkEnumParameter(String name, String param, Function<String, T> valueOf) throws ThingsboardException {
        try {
            return valueOf.apply(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ThingsboardException(name + " \"" + param + "\" is not supported!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    UUID toUUID(String id) throws ThingsboardException {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw handleException(e, false);
        }
    }

    PageLink createPageLink(int pageSize, int page, String textSearch, String sortProperty, String sortOrder) throws ThingsboardException {
        if (StringUtils.isNotEmpty(sortProperty)) {
            if (!Validator.isValidProperty(sortProperty)) {
                throw new IllegalArgumentException("Invalid sort property");
            }
            SortOrder.Direction direction = SortOrder.Direction.ASC;
            if (StringUtils.isNotEmpty(sortOrder)) {
                try {
                    direction = SortOrder.Direction.valueOf(sortOrder.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ThingsboardException("Unsupported sort order '" + sortOrder + "'! Only 'ASC' or 'DESC' types are allowed.", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
                }
            }
            SortOrder sort = new SortOrder(sortProperty, direction);
            return new PageLink(pageSize, page, textSearch, sort);
        } else {
            return new PageLink(pageSize, page, textSearch);
        }
    }

    TimePageLink createTimePageLink(int pageSize, int page, String textSearch,
                                    String sortProperty, String sortOrder, Long startTime, Long endTime) throws ThingsboardException {
        PageLink pageLink = this.createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return new TimePageLink(pageLink, startTime, endTime);
    }

    /**
     * Returns the authenticated {@link SecurityUser} from the Spring Security context.
     *
     * @return current security principal
     * @throws ThingsboardException with {@link ThingsboardErrorCode#AUTHENTICATION} if not authenticated
     */
    protected SecurityUser getCurrentUser() throws ThingsboardException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return (SecurityUser) authentication.getPrincipal();
        } else {
            throw new ThingsboardException("You aren't authorized to perform this operation!", ThingsboardErrorCode.AUTHENTICATION);
        }
    }

    /**
     * Loads a tenant and verifies {@link Resource#TENANT} permission for the given operation.
     */
    Tenant checkTenantId(TenantId tenantId, Operation operation) throws ThingsboardException {
        return checkEntityId(tenantId, (t, i) -> tenantService.findTenantById(tenantId), operation);
    }

    TenantInfo checkTenantInfoId(TenantId tenantId, Operation operation) throws ThingsboardException {
        return checkEntityId(tenantId, (t, i) -> tenantService.findTenantInfoById(tenantId), operation);
    }

    TenantProfile checkTenantProfileId(TenantProfileId tenantProfileId, Operation operation) throws ThingsboardException {
        try {
            validateId(tenantProfileId, id -> "Incorrect tenantProfileId " + id);
            TenantProfile tenantProfile = tenantProfileService.findTenantProfileById(getTenantId(), tenantProfileId);
            checkNotNull(tenantProfile, "Tenant profile with id [" + tenantProfileId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.TENANT_PROFILE, operation);
            return tenantProfile;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    /**
     * Returns the tenant id of the currently authenticated user.
     *
     * @return tenant id
     * @throws ThingsboardException if the user is not authenticated
     */
    protected TenantId getTenantId() throws ThingsboardException {
        return getCurrentUser().getTenantId();
    }

    /**
     * Loads a customer and verifies {@link Resource#CUSTOMER} permission for the given operation.
     */
    Customer checkCustomerId(CustomerId customerId, Operation operation) throws ThingsboardException {
        return checkEntityId(customerId, customerService::findCustomerById, operation);
    }

    /**
     * Loads a user and verifies {@link Resource#USER} permission for the given operation.
     */
    User checkUserId(UserId userId, Operation operation) throws ThingsboardException {
        return checkEntityId(userId, userService::findUserById, operation);
    }

    /**
     * Validates create vs update permission for an entity being saved.
     * Checks {@link Operation#CREATE} when {@code entityId} is null, otherwise delegates to {@link #checkEntityId}.
     *
     * @param entityId existing entity id, or null when creating
     * @param entity entity payload
     * @param resource access-control resource type
     * @throws ThingsboardException if permission is denied
     */
    protected <I extends EntityId, T extends HasTenantId> void checkEntity(I entityId, T entity, Resource resource) throws ThingsboardException {
        if (entityId == null) {
            accessControlService.checkPermission(getCurrentUser(), resource, Operation.CREATE, null, entity);
        } else {
            checkEntityId(entityId, Operation.WRITE);
        }
    }

    /**
     * Resolves an entity by {@link EntityId}, verifies it exists, and checks access permission.
     * Dispatches to type-specific {@code check*Id} helpers based on {@link EntityType}.
     *
     * @param entityId entity identifier (required)
     * @param operation required operation (READ, WRITE, DELETE, etc.)
     * @return the entity implementing {@link HasId}
     * @throws ThingsboardException if id is missing, entity not found, or permission denied
     */
    protected HasId<? extends EntityId> checkEntityId(EntityId entityId, Operation operation) throws ThingsboardException {
        try {
            if (entityId == null) {
                throw new ThingsboardException("Parameter entityId can't be empty!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            validateId(entityId.getId(), id -> "Incorrect entityId " + id);
            return switch (entityId.getEntityType()) {
                case ALARM -> checkAlarmId(new AlarmId(entityId.getId()), operation);
                case DEVICE -> checkDeviceId(new DeviceId(entityId.getId()), operation);
                case DEVICE_PROFILE -> checkDeviceProfileId(new DeviceProfileId(entityId.getId()), operation);
                case CUSTOMER -> checkCustomerId(new CustomerId(entityId.getId()), operation);
                case TENANT -> checkTenantId(TenantId.fromUUID(entityId.getId()), operation);
                case TENANT_PROFILE -> checkTenantProfileId(new TenantProfileId(entityId.getId()), operation);
                case RULE_CHAIN -> checkRuleChain(new RuleChainId(entityId.getId()), operation);
                case RULE_NODE -> checkRuleNode(new RuleNodeId(entityId.getId()), operation);
                case ASSET -> checkAssetId(new AssetId(entityId.getId()), operation);
                case ASSET_PROFILE -> checkAssetProfileId(new AssetProfileId(entityId.getId()), operation);
                case DASHBOARD -> checkDashboardId(new DashboardId(entityId.getId()), operation);
                case USER -> checkUserId(new UserId(entityId.getId()), operation);
                case ENTITY_VIEW -> checkEntityViewId(new EntityViewId(entityId.getId()), operation);
                case EDGE -> checkEdgeId(new EdgeId(entityId.getId()), operation);
                case WIDGETS_BUNDLE -> checkWidgetsBundleId(new WidgetsBundleId(entityId.getId()), operation);
                case WIDGET_TYPE -> checkWidgetTypeId(new WidgetTypeId(entityId.getId()), operation);
                case TB_RESOURCE -> checkResourceInfoId(new TbResourceId(entityId.getId()), operation);
                case OTA_PACKAGE -> checkOtaPackageId(new OtaPackageId(entityId.getId()), operation);
                case QUEUE -> checkQueueId(new QueueId(entityId.getId()), operation);
                case OAUTH2_CLIENT -> checkOauth2ClientId(new OAuth2ClientId(entityId.getId()), operation);
                case DOMAIN -> checkDomainId(new DomainId(entityId.getId()), operation);
                case MOBILE_APP -> checkMobileAppId(new MobileAppId(entityId.getId()), operation);
                case MOBILE_APP_BUNDLE -> checkMobileAppBundleId(new MobileAppBundleId(entityId.getId()), operation);
                case CALCULATED_FIELD -> checkCalculatedFieldId(new CalculatedFieldId(entityId.getId()), operation);
                case AI_MODEL -> checkAiModelId(new AiModelId(entityId.getId()), operation);
                case API_KEY -> checkApiKeyId(new ApiKeyId(entityId.getId()), operation);
                default -> (HasId<? extends EntityId>) checkEntityId(entityId, entitiesService::findEntityByTenantIdAndId, operation);
            };
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    /**
     * Generic entity lookup: loads entity via {@code findingFunction} and checks permission.
     *
     * @param entityId entity id
     * @param findingFunction tenant-scoped finder
     * @param operation required operation
     * @param <E> entity type
     * @param <I> entity id type
     * @return entity with permission verified
     * @throws ThingsboardException if not found or access denied
     */
    protected <E extends HasId<I> & HasTenantId, I extends EntityId> E checkEntityId(I entityId, ThrowingBiFunction<TenantId, I, E> findingFunction, Operation operation) throws ThingsboardException {
        try {
            validateId((UUIDBased) entityId, id -> "Invalid entity id");
            SecurityUser user = getCurrentUser();
            E entity = findingFunction.apply(user.getTenantId(), entityId);
            checkNotNull(entity, entityId.getEntityType().getNormalName() + " with id [" + entityId + "] is not found");
            return checkEntity(user, entity, operation);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    /**
     * Checks {@link org.thingsboard.server.service.security.permission.Resource} permission for a loaded entity.
     *
     * @param user security user performing the operation
     * @param entity loaded entity
     * @param operation required operation
     * @param <E> entity type
     * @param <I> entity id type
     * @return the same entity after permission check
     * @throws ThingsboardException if permission is denied
     */
    protected <E extends HasId<I> & HasTenantId, I extends EntityId> E checkEntity(SecurityUser user, E entity, Operation operation) throws ThingsboardException {
        checkNotNull(entity, "Entity not found");
        accessControlService.checkPermission(user, Resource.of(entity.getId().getEntityType()), operation, entity.getId(), entity);
        return entity;
    }

    /**
     * Validates that all entities referenced in a calculated-field configuration exist and are readable.
     * Supports TENANT (no check), CUSTOMER, ASSET, and DEVICE reference types.
     *
     * @param calculatedFieldConfig calculated field configuration containing referenced entity ids
     * @throws ThingsboardException if a referenced entity is inaccessible
     */
    protected void checkReferencedEntities(CalculatedFieldConfiguration calculatedFieldConfig) throws ThingsboardException {
        for (EntityId referencedEntityId : calculatedFieldConfig.getReferencedEntities()) {
            EntityType refEntityType = referencedEntityId.getEntityType();
            switch (refEntityType) {
                case TENANT -> {}
                case CUSTOMER, ASSET, DEVICE -> checkEntityId(referencedEntityId, Operation.READ);
                default -> throw new IllegalArgumentException("Unsupported referenced entity type: '" + refEntityType + "'.");
            }
        }
    }

    /**
     * Loads a device and verifies {@link Resource#DEVICE} permission for the given operation.
     *
     * @param deviceId device id
     * @param operation e.g. READ, WRITE, DELETE, READ_CREDENTIALS, ASSIGN_TO_CUSTOMER
     * @return device if found and accessible
     * @throws ThingsboardException if not found or permission denied
     */
    Device checkDeviceId(DeviceId deviceId, Operation operation) throws ThingsboardException {
        return checkEntityId(deviceId, deviceService::findDeviceById, operation);
    }

    /**
     * Loads device info and verifies {@link Resource#DEVICE} permission for the given operation.
     */
    DeviceInfo checkDeviceInfoId(DeviceId deviceId, Operation operation) throws ThingsboardException {
        return checkEntityId(deviceId, deviceService::findDeviceInfoById, operation);
    }

    /**
     * Loads a device profile and verifies {@link Resource#DEVICE_PROFILE} permission.
     */
    DeviceProfile checkDeviceProfileId(DeviceProfileId deviceProfileId, Operation operation) throws ThingsboardException {
        return checkEntityId(deviceProfileId, deviceProfileService::findDeviceProfileById, operation);
    }

    /**
     * Loads an entity view and checks {@link Resource#ENTITY_VIEW} permission.
     *
     * @param entityViewId entity view id
     * @param operation required operation
     * @return entity view
     * @throws ThingsboardException if not found or access denied
     */
    protected EntityView checkEntityViewId(EntityViewId entityViewId, Operation operation) throws ThingsboardException {
        return checkEntityId(entityViewId, entityViewService::findEntityViewById, operation);
    }

    EntityViewInfo checkEntityViewInfoId(EntityViewId entityViewId, Operation operation) throws ThingsboardException {
        return checkEntityId(entityViewId, entityViewService::findEntityViewInfoById, operation);
    }

    /**
     * Loads an asset and verifies {@link Resource#ASSET} permission for the given operation.
     */
    Asset checkAssetId(AssetId assetId, Operation operation) throws ThingsboardException {
        return checkEntityId(assetId, assetService::findAssetById, operation);
    }

    /**
     * Loads asset info and verifies {@link Resource#ASSET} permission.
     */
    AssetInfo checkAssetInfoId(AssetId assetId, Operation operation) throws ThingsboardException {
        return checkEntityId(assetId, assetService::findAssetInfoById, operation);
    }

    /**
     * Loads an asset profile and verifies {@link Resource#ASSET_PROFILE} permission.
     */
    AssetProfile checkAssetProfileId(AssetProfileId assetProfileId, Operation operation) throws ThingsboardException {
        return checkEntityId(assetProfileId, assetProfileService::findAssetProfileById, operation);
    }

    /**
     * Loads an alarm and verifies {@link Resource#ALARM} permission for the given operation.
     */
    Alarm checkAlarmId(AlarmId alarmId, Operation operation) throws ThingsboardException {
        return checkEntityId(alarmId, alarmService::findAlarmById, operation);
    }

    /**
     * Loads alarm info and verifies {@link Resource#ALARM} permission.
     */
    AlarmInfo checkAlarmInfoId(AlarmId alarmId, Operation operation) throws ThingsboardException {
        return checkEntityId(alarmId, alarmService::findAlarmInfoById, operation);
    }

    AlarmComment checkAlarmCommentId(AlarmCommentId alarmCommentId, AlarmId alarmId) throws ThingsboardException {
        try {
            validateId(alarmCommentId, id -> "Incorrect alarmCommentId " + id);
            AlarmComment alarmComment = alarmCommentService.findAlarmCommentByIdAsync(getCurrentUser().getTenantId(), alarmCommentId).get();
            checkNotNull(alarmComment, "Alarm comment with id [" + alarmCommentId + "] is not found");
            if (!alarmId.equals(alarmComment.getAlarmId())) {
                throw new ThingsboardException("Alarm id does not match with comment alarm id", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            return alarmComment;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    WidgetsBundle checkWidgetsBundleId(WidgetsBundleId widgetsBundleId, Operation operation) throws ThingsboardException {
        return checkEntityId(widgetsBundleId, widgetsBundleService::findWidgetsBundleById, operation);
    }

    WidgetTypeDetails checkWidgetTypeId(WidgetTypeId widgetTypeId, Operation operation) throws ThingsboardException {
        return checkEntityId(widgetTypeId, widgetTypeService::findWidgetTypeDetailsById, operation);
    }

    WidgetTypeInfo checkWidgetTypeInfoId(WidgetTypeId widgetTypeId, Operation operation) throws ThingsboardException {
        return checkEntityId(widgetTypeId, widgetTypeService::findWidgetTypeInfoById, operation);
    }

    Dashboard checkDashboardId(DashboardId dashboardId, Operation operation) throws ThingsboardException {
        return checkEntityId(dashboardId, dashboardService::findDashboardById, operation);
    }

    Edge checkEdgeId(EdgeId edgeId, Operation operation) throws ThingsboardException {
        return checkEntityId(edgeId, edgeService::findEdgeById, operation);
    }

    EdgeInfo checkEdgeInfoId(EdgeId edgeId, Operation operation) throws ThingsboardException {
        return checkEntityId(edgeId, edgeService::findEdgeInfoById, operation);
    }

    DashboardInfo checkDashboardInfoId(DashboardId dashboardId, Operation operation) throws ThingsboardException {
        return checkEntityId(dashboardId, dashboardService::findDashboardInfoById, operation);
    }

    ComponentDescriptor checkComponentDescriptorByClazz(String clazz) throws ThingsboardException {
        try {
            log.debug("[{}] Lookup component descriptor", clazz);
            return checkNotNull(componentDescriptorService.getComponent(clazz));
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<ComponentDescriptor> checkComponentDescriptorsByType(ComponentType type, RuleChainType ruleChainType) throws ThingsboardException {
        try {
            log.debug("[{}] Lookup component descriptors", type);
            return componentDescriptorService.getComponents(type, ruleChainType);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<ComponentDescriptor> checkComponentDescriptorsByTypes(Set<ComponentType> types, RuleChainType ruleChainType) throws ThingsboardException {
        try {
            log.debug("[{}] Lookup component descriptors", types);
            return componentDescriptorService.getComponents(types, ruleChainType);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    /**
     * Loads a rule chain and checks {@link Resource#RULE_CHAIN} permission.
     *
     * @param ruleChainId rule chain id
     * @param operation required operation
     * @return rule chain
     * @throws ThingsboardException if not found or access denied
     */
    protected RuleChain checkRuleChain(RuleChainId ruleChainId, Operation operation) throws ThingsboardException {
        return checkEntityId(ruleChainId, ruleChainService::findRuleChainById, operation);
    }

    /**
     * Loads a rule node, verifies its parent rule chain, and checks permission on the chain.
     *
     * @param ruleNodeId rule node id
     * @param operation required operation on the parent rule chain
     * @return rule node
     * @throws ThingsboardException if not found or access denied
     */
    protected RuleNode checkRuleNode(RuleNodeId ruleNodeId, Operation operation) throws ThingsboardException {
        validateId(ruleNodeId, id -> "Incorrect ruleNodeId " + id);
        RuleNode ruleNode = ruleChainService.findRuleNodeById(getTenantId(), ruleNodeId);
        checkNotNull(ruleNode, "Rule node with id [" + ruleNodeId + "] is not found");
        checkRuleChain(ruleNode.getRuleChainId(), operation);
        return ruleNode;
    }

    TbResource checkResourceId(TbResourceId resourceId, Operation operation) throws ThingsboardException {
        return checkEntityId(resourceId, resourceService::findResourceById, operation);
    }

    TbResourceInfo checkResourceInfoId(TbResourceId resourceId, Operation operation) throws ThingsboardException {
        return checkEntityId(resourceId, resourceService::findResourceInfoById, operation);
    }

    OtaPackage checkOtaPackageId(OtaPackageId otaPackageId, Operation operation) throws ThingsboardException {
        return checkEntityId(otaPackageId, otaPackageService::findOtaPackageById, operation);
    }

    OtaPackageInfo checkOtaPackageInfoId(OtaPackageId otaPackageId, Operation operation) throws ThingsboardException {
        return checkEntityId(otaPackageId, otaPackageService::findOtaPackageInfoById, operation);
    }

    Rpc checkRpcId(RpcId rpcId, Operation operation) throws ThingsboardException {
        return checkEntityId(rpcId, rpcService::findById, operation);
    }

    /**
     * Loads a queue and checks {@link Resource#QUEUE} permission.
     * Denies tenant access to system queues when tenant uses isolated rule engine.
     *
     * @param queueId queue id
     * @param operation required operation
     * @return queue
     * @throws ThingsboardException if not found or access denied
     */
    protected Queue checkQueueId(QueueId queueId, Operation operation) throws ThingsboardException {
        Queue queue = checkEntityId(queueId, queueService::findQueueById, operation);
        TenantId tenantId = getTenantId();
        if (queue.getTenantId().isNullUid() && !tenantId.isNullUid()) {
            TenantProfile tenantProfile = tenantProfileCache.get(tenantId);
            if (tenantProfile.isIsolatedTbRuleEngine()) {
                throw new ThingsboardException(YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION,
                        ThingsboardErrorCode.PERMISSION_DENIED);
            }
        }
        return queue;
    }

    OAuth2Client checkOauth2ClientId(OAuth2ClientId oAuth2ClientId, Operation operation) throws ThingsboardException {
        return checkEntityId(oAuth2ClientId, oAuth2ClientService::findOAuth2ClientById, operation);
    }

    Domain checkDomainId(DomainId domainId, Operation operation) throws ThingsboardException {
        return checkEntityId(domainId, domainService::findDomainById, operation);
    }

    MobileApp checkMobileAppId(MobileAppId mobileAppId, Operation operation) throws ThingsboardException {
        return checkEntityId(mobileAppId, mobileAppService::findMobileAppById, operation);
    }

    MobileAppBundle checkMobileAppBundleId(MobileAppBundleId mobileAppBundleId, Operation operation) throws ThingsboardException {
        return checkEntityId(mobileAppBundleId, mobileAppBundleService::findMobileAppBundleById, operation);
    }

    NotificationTarget checkNotificationTargetId(NotificationTargetId notificationTargetId, Operation operation) throws ThingsboardException {
        return checkEntityId(notificationTargetId, notificationTargetService::findNotificationTargetById, operation);
    }

    Job checkJobId(JobId jobId, Operation operation) throws ThingsboardException {
        return checkEntityId(jobId, jobService::findJobById, operation);
    }

    AiModel checkAiModelId(AiModelId settingsId, Operation operation) throws ThingsboardException {
        return checkEntityId(settingsId, (tenantId, id) -> aiModelService.findAiModelByTenantIdAndId(tenantId, id).orElse(null), operation);
    }

    /**
     * Loads an API key and verifies {@link Resource#API_KEY} permission for the given operation.
     */
    ApiKey checkApiKeyId(ApiKeyId apiKeyId, Operation operation) throws ThingsboardException {
        return checkEntityId(apiKeyId, apiKeyService::findApiKeyById, operation);
    }

    /**
     * Returns a null-UUID placeholder {@link EntityId} for the given entity type (used in audit logging).
     *
     * @param entityType entity type
     * @param <I> entity id type
     * @return placeholder entity id
     */
    protected <I extends EntityId> I emptyId(EntityType entityType) {
        return (I) EntityIdFactory.getByTypeAndUuid(entityType, ModelConstants.NULL_UUID);
    }

    /**
     * Converts a {@link Throwable} to an {@link Exception}, wrapping non-Exception throwables.
     *
     * @param error throwable, may be null
     * @return exception instance or null
     */
    public static Exception toException(Throwable error) {
        return error != null ? (error instanceof Exception ? (Exception) error : new Exception(error)) : null;
    }

    /**
     * Records an entity action in the audit log via {@link org.thingsboard.server.service.entitiy.TbLogEntityActionService}.
     */
    protected <E extends HasName & HasId<? extends EntityId>> void logEntityAction(SecurityUser user, EntityType entityType, E savedEntity, ActionType actionType) {
        logEntityAction(user, entityType, null, savedEntity, actionType, null);
    }

    /**
     * Records an entity action in the audit log via {@link org.thingsboard.server.service.entitiy.TbLogEntityActionService}.
     */
    protected <E extends HasName & HasId<? extends EntityId>> void logEntityAction(SecurityUser user, EntityType entityType, E entity, E savedEntity, ActionType actionType, Exception e) {
        EntityId entityId = savedEntity != null ? savedEntity.getId() : emptyId(entityType);
        entityActionService.logEntityAction(user, entityId, savedEntity != null ? savedEntity : entity,
                user.getCustomerId(), actionType, e);
    }

    /**
     * Saves an entity, logs ADDED/UPDATED audit action, and rethrows on failure.
     *
     * @param entityType entity type for audit logging
     * @param entity entity to save
     * @param savingFunction DAO save function
     * @param <E> entity type
     * @return saved entity
     * @throws Exception if save fails
     */
    protected <E extends HasName & HasId<? extends EntityId>> E doSaveAndLog(EntityType entityType, E entity, BiFunction<TenantId, E, E> savingFunction) throws Exception {
        ActionType actionType = entity.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        SecurityUser user = getCurrentUser();
        try {
            E savedEntity = savingFunction.apply(user.getTenantId(), entity);
            logEntityAction(user, entityType, savedEntity, actionType);
            return savedEntity;
        } catch (Exception e) {
            logEntityAction(user, entityType, entity, null, actionType, e);
            throw e;
        }
    }

    /**
     * Deletes an entity, logs DELETED audit action, and rethrows on failure.
     *
     * @param entityType entity type for audit logging
     * @param entity entity to delete
     * @param deleteFunction DAO delete function
     * @throws Exception if delete fails
     */
    protected <E extends HasName & HasId<I>, I extends EntityId> void doDeleteAndLog(EntityType entityType, E entity, BiConsumer<TenantId, I> deleteFunction) throws Exception {
        SecurityUser user = getCurrentUser();
        try {
            deleteFunction.accept(user.getTenantId(), entity.getId());
            logEntityAction(user, entityType, entity, ActionType.DELETED);
        } catch (Exception e) {
            logEntityAction(user, entityType, entity, entity, ActionType.DELETED, e);
            throw e;
        }
    }

    /**
     * Enriches user {@code additionalInfo} with credentials state (enabled, activated, last login)
     * and validates embedded dashboard references.
     *
     * @param user user whose additional info is updated in place
     * @throws ThingsboardException if dashboard references are invalid
     */
    protected void checkUserInfo(User user) throws ThingsboardException {
        ObjectNode info;
        if (user.getAdditionalInfo() instanceof ObjectNode additionalInfo) {
            info = additionalInfo;
            checkDashboardInfo(info);
        } else {
            info = JacksonUtil.newObjectNode();
            user.setAdditionalInfo(info);
        }

        UserCredentials userCredentials = userService.findUserCredentialsByUserId(user.getTenantId(), user.getId());
        info.put("userCredentialsEnabled", userCredentials.isEnabled());
        info.put("userActivated", userCredentials.getActivateToken() == null);
        info.put("lastLoginTs", userCredentials.getLastLoginTs());
    }

    /**
     * Removes stale default/home dashboard id references from {@code additionalInfo} JSON
     * when the dashboard no longer exists.
     *
     * @param additionalInfo user/tenant/customer additional info node
     * @throws ThingsboardException if validation fails
     */
    protected void checkDashboardInfo(JsonNode additionalInfo) throws ThingsboardException {
        checkDashboardInfo(additionalInfo, DEFAULT_DASHBOARD);
        checkDashboardInfo(additionalInfo, HOME_DASHBOARD);
    }

    /**
     * Removes a stale dashboard id from {@code additionalInfo} when the dashboard does not exist.
     *
     * @param node additional info JSON object node
     * @param dashboardField field name (e.g. {@code homeDashboardId})
     * @throws ThingsboardException if validation fails
     */
    protected void checkDashboardInfo(JsonNode node, String dashboardField) throws ThingsboardException {
        if (node instanceof ObjectNode additionalInfo) {
            DashboardId dashboardId = Optional.ofNullable(additionalInfo.get(dashboardField))
                    .filter(JsonNode::isTextual).map(JsonNode::asText)
                    .map(id -> {
                        try {
                            return new DashboardId(UUID.fromString(id));
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    }).orElse(null);

            if (dashboardId != null && !dashboardService.existsById(getTenantId(), dashboardId)) {
                additionalInfo.remove(dashboardField);
            }
        }
    }

    private CalculatedField checkCalculatedFieldId(CalculatedFieldId calculatedFieldId, Operation operation) throws ThingsboardException {
        validateId(calculatedFieldId, id -> "Invalid entity id");
        SecurityUser user = getCurrentUser();
        CalculatedField cf = calculatedFieldService.findById(user.getTenantId(), calculatedFieldId);
        checkNotNull(cf, calculatedFieldId.getEntityType().getNormalName() + " with id [" + calculatedFieldId + "] is not found");
        checkEntityId(cf.getEntityId(), operation);
        return cf;
    }

    /**
     * Resolves home dashboard configuration from user, then customer, then tenant additional info.
     *
     * @param securityUser current user
     * @param additionalInfo user additional info JSON
     * @return home dashboard info, or null if not configured
     */
    protected HomeDashboardInfo getHomeDashboardInfo(SecurityUser securityUser, JsonNode additionalInfo) {
        HomeDashboardInfo homeDashboardInfo = extractHomeDashboardInfoFromAdditionalInfo(additionalInfo);
        if (homeDashboardInfo == null) {
            if (securityUser.isCustomerUser()) {
                Customer customer = customerService.findCustomerById(securityUser.getTenantId(), securityUser.getCustomerId());
                homeDashboardInfo = extractHomeDashboardInfoFromAdditionalInfo(customer.getAdditionalInfo());
            }
            if (homeDashboardInfo == null) {
                Tenant tenant = tenantService.findTenantById(securityUser.getTenantId());
                homeDashboardInfo = extractHomeDashboardInfoFromAdditionalInfo(tenant.getAdditionalInfo());
            }
        }
        return homeDashboardInfo;
    }

    private HomeDashboardInfo extractHomeDashboardInfoFromAdditionalInfo(JsonNode additionalInfo) {
        try {
            if (additionalInfo != null && additionalInfo.has(HOME_DASHBOARD_ID) && !additionalInfo.get(HOME_DASHBOARD_ID).isNull()) {
                String strDashboardId = additionalInfo.get(HOME_DASHBOARD_ID).asText();
                DashboardId dashboardId = new DashboardId(toUUID(strDashboardId));
                checkDashboardId(dashboardId, Operation.READ);
                boolean hideDashboardToolbar = true;
                if (additionalInfo.has(HOME_DASHBOARD_HIDE_TOOLBAR)) {
                    hideDashboardToolbar = additionalInfo.get(HOME_DASHBOARD_HIDE_TOOLBAR).asBoolean();
                }
                return new HomeDashboardInfo(dashboardId, hideDashboardToolbar);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Parses a Content-Type header value, defaulting to {@link MediaType#APPLICATION_OCTET_STREAM} on error.
     *
     * @param contentType raw content type string
     * @return parsed media type
     */
    protected MediaType parseMediaType(String contentType) {
        try {
            return MediaType.parseMediaType(contentType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    /**
     * Wraps a {@link ListenableFuture} in a Spring {@link DeferredResult} for async REST responses.
     */
    protected <T> DeferredResult<T> wrapFuture(ListenableFuture<T> future) {
        DeferredResult<T> deferredResult = new DeferredResult<>(); // Timeout of spring.mvc.async.request-timeout is used
        DonAsynchron.withCallback(future, deferredResult::setResult, deferredResult::setErrorResult);
        return deferredResult;
    }

    /**
     * Wraps a {@link ListenableFuture} in a Spring {@link DeferredResult} for async REST responses.
     */
    protected <T> DeferredResult<T> wrapFuture(ListenableFuture<T> future, long timeoutMs) {
        DeferredResult<T> deferredResult = new DeferredResult<>(timeoutMs);
        DonAsynchron.withCallback(future, deferredResult::setResult, deferredResult::setErrorResult);
        return deferredResult;
    }

    /**
     * Builds an {@link EntityDataSortOrder} from REST sort query parameters.
     *
     * @param sortProperty entity field property name
     * @param sortOrder ASC or DESC
     * @return sort order, or null if sortProperty is empty
     */
    protected EntityDataSortOrder createEntityDataSortOrder(String sortProperty, String sortOrder) {
        if (isNotEmpty(sortProperty)) {
            EntityDataSortOrder entityDataSortOrder = new EntityDataSortOrder();
            entityDataSortOrder.setKey(new EntityKey(ENTITY_FIELD, sortProperty));
            if (isNotEmpty(sortOrder)) {
                entityDataSortOrder.setDirection(EntityDataSortOrder.Direction.valueOf(sortOrder));
            }
            return entityDataSortOrder;
        } else {
            return null;
        }
    }

    /**
     * Writes response bytes, gzip-compressing when Accept-Encoding contains gzip.
     *
     * @param acceptEncodingHeader Accept-Encoding request header
     * @param response servlet response
     * @param content raw response bytes
     * @throws IOException on stream errors
     */
    protected void compressResponseWithGzipIFAccepted(String acceptEncodingHeader, HttpServletResponse response, byte[] content) throws IOException {
        if (StringUtils.isNotEmpty(acceptEncodingHeader) && acceptEncodingHeader.contains("gzip")) {
            response.setHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
            response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(response.getOutputStream())) {
                gzipOutputStream.write(content);
                gzipOutputStream.finish();
            }
        } else {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(content);
                outputStream.flush();
            }
        }
    }

    /**
     * Builds an empty {@link ResponseEntity} with the given HTTP status.
     *
     * @param status HTTP status
     * @param <T> response body type
     * @return response entity without body
     */
    protected <T> ResponseEntity<T> response(HttpStatus status) {
        return ResponseEntity.status(status).build();
    }

    /**
     * Builds a 303 See Other redirect {@link ResponseEntity} to the given location.
     *
     * @param location redirect URI string
     * @param <T> response body type
     * @return redirect response
     */
    protected <T> ResponseEntity<T> redirectTo(String location) {
        URI uri;
        try {
            uri = URI.create(location);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create URI from '{}'", location, e);
            throw e;
        }
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(uri)
                .build();
    }

    /**
     * Converts UUID array to {@link OAuth2ClientId} list, validating READ permission on each client.
     *
     * @param ids OAuth2 client UUIDs, may be null
     * @return list of validated client ids (empty if ids is null)
     * @throws ThingsboardException if any client is not accessible
     */
    protected List<OAuth2ClientId> getOAuth2ClientIds(UUID[] ids) throws ThingsboardException {
        if (ids == null) {
            return Collections.emptyList();
        }
        List<OAuth2ClientId> oAuth2ClientIds = new ArrayList<>();
        for (UUID id : ids) {
            OAuth2ClientId oauth2ClientId = new OAuth2ClientId(id);
            checkOauth2ClientId(oauth2ClientId, Operation.READ);
            oAuth2ClientIds.add(oauth2ClientId);
        }
        return oAuth2ClientIds;
    }

}
