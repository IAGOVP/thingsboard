/**
 * Copyright © 2016-2026 The Thingsboard Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.rest.client;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.common.util.ThingsBoardExecutors;
import org.thingsboard.rest.client.utils.RestJsonConverter;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.AttributeScope;
import org.thingsboard.server.common.data.ClaimRequest;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.DashboardInfo;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceInfo;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.DeviceProfileInfo;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.EntityViewInfo;
import org.thingsboard.server.common.data.EventInfo;
import org.thingsboard.server.common.data.OtaPackage;
import org.thingsboard.server.common.data.OtaPackageInfo;
import org.thingsboard.server.common.data.ResourceExportData;
import org.thingsboard.server.common.data.ResourceSubType;
import org.thingsboard.server.common.data.SaveDeviceWithCredentialsRequest;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.SystemInfo;
import org.thingsboard.server.common.data.TbImageDeleteResult;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.TbResourceInfo;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.TenantInfo;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.UpdateMessage;
import org.thingsboard.server.common.data.UsageInfo;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.UserEmailInfo;
import org.thingsboard.server.common.data.ai.AiModel;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmComment;
import org.thingsboard.server.common.data.alarm.AlarmCommentInfo;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.alarm.AlarmSearchStatus;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.alarm.AlarmStatus;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetInfo;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.asset.AssetProfileInfo;
import org.thingsboard.server.common.data.asset.AssetSearchQuery;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.audit.AuditLog;
import org.thingsboard.server.common.data.cf.CalculatedField;
import org.thingsboard.server.common.data.cf.CalculatedFieldInfo;
import org.thingsboard.server.common.data.cf.CalculatedFieldType;
import org.thingsboard.server.common.data.device.DeviceSearchQuery;
import org.thingsboard.server.common.data.domain.Domain;
import org.thingsboard.server.common.data.domain.DomainInfo;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.edge.EdgeInfo;
import org.thingsboard.server.common.data.edge.EdgeInstructions;
import org.thingsboard.server.common.data.edge.EdgeSearchQuery;
import org.thingsboard.server.common.data.entityview.EntityViewSearchQuery;
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
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.MobileAppBundleId;
import org.thingsboard.server.common.data.id.MobileAppId;
import org.thingsboard.server.common.data.id.NotificationId;
import org.thingsboard.server.common.data.id.NotificationRequestId;
import org.thingsboard.server.common.data.id.OAuth2ClientId;
import org.thingsboard.server.common.data.id.OAuth2ClientRegistrationTemplateId;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.QueueId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.RuleNodeId;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.kv.Aggregation;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.IntervalType;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.mobile.app.MobileApp;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundle;
import org.thingsboard.server.common.data.mobile.bundle.MobileAppBundleInfo;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.notification.NotificationRequest;
import org.thingsboard.server.common.data.notification.NotificationRequestInfo;
import org.thingsboard.server.common.data.notification.NotificationRequestPreview;
import org.thingsboard.server.common.data.notification.settings.NotificationSettings;
import org.thingsboard.server.common.data.notification.settings.UserNotificationSettings;
import org.thingsboard.server.common.data.notification.targets.NotificationTarget;
import org.thingsboard.server.common.data.notification.template.NotificationTemplate;
import org.thingsboard.server.common.data.oauth2.OAuth2Client;
import org.thingsboard.server.common.data.oauth2.OAuth2ClientInfo;
import org.thingsboard.server.common.data.oauth2.OAuth2ClientLoginInfo;
import org.thingsboard.server.common.data.oauth2.OAuth2ClientRegistrationTemplate;
import org.thingsboard.server.common.data.oauth2.PlatformType;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.pat.ApiKey;
import org.thingsboard.server.common.data.pat.ApiKeyInfo;
import org.thingsboard.server.common.data.plugin.ComponentDescriptor;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.query.AlarmCountQuery;
import org.thingsboard.server.common.data.query.AlarmData;
import org.thingsboard.server.common.data.query.AlarmDataQuery;
import org.thingsboard.server.common.data.query.AvailableEntityKeys;
import org.thingsboard.server.common.data.query.AvailableEntityKeysV2;
import org.thingsboard.server.common.data.query.EntityCountQuery;
import org.thingsboard.server.common.data.query.EntityData;
import org.thingsboard.server.common.data.query.EntityDataQuery;
import org.thingsboard.server.common.data.queue.Queue;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntityRelationInfo;
import org.thingsboard.server.common.data.relation.EntityRelationsQuery;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.common.data.rule.DefaultRuleChainCreateRequest;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainData;
import org.thingsboard.server.common.data.rule.RuleChainMetaData;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.common.data.security.DeviceCredentialsType;
import org.thingsboard.server.common.data.security.model.JwtPair;
import org.thingsboard.server.common.data.security.model.JwtSettings;
import org.thingsboard.server.common.data.security.model.SecuritySettings;
import org.thingsboard.server.common.data.security.model.UserPasswordPolicy;
import org.thingsboard.server.common.data.sms.config.TestSmsRequest;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportRequest;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportResult;
import org.thingsboard.server.common.data.sync.vc.AutoCommitSettings;
import org.thingsboard.server.common.data.sync.vc.BranchInfo;
import org.thingsboard.server.common.data.sync.vc.EntityDataDiff;
import org.thingsboard.server.common.data.sync.vc.EntityDataInfo;
import org.thingsboard.server.common.data.sync.vc.EntityVersion;
import org.thingsboard.server.common.data.sync.vc.RepositorySettings;
import org.thingsboard.server.common.data.sync.vc.VersionCreationResult;
import org.thingsboard.server.common.data.sync.vc.VersionLoadResult;
import org.thingsboard.server.common.data.sync.vc.VersionedEntityInfo;
import org.thingsboard.server.common.data.sync.vc.request.create.VersionCreateRequest;
import org.thingsboard.server.common.data.sync.vc.request.load.VersionLoadRequest;
import org.thingsboard.server.common.data.widget.DeprecatedFilter;
import org.thingsboard.server.common.data.widget.WidgetType;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetTypeInfo;
import org.thingsboard.server.common.data.widget.WidgetsBundle;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static org.thingsboard.server.common.data.StringUtils.isEmpty;

/**
 * Java client for the ThingsBoard REST API using Spring {@link RestTemplate}.
 * 
 * <p>Each public method maps to one or more HTTP endpoints on tb-node (see {@code baseURL + "/api/..."}).
 * JWT is sent in the {@value #TOKEN_HEADER_PARAM} header; the interceptor refreshes tokens automatically.
 * 
 * <p>Used by tests, monitoring, integrations, and tools. Server-side controllers live in the {@code application} module;
 * full API catalog: {@code docs/REST_API.md}.
 */
public class RestClient implements Closeable {

    /**
 * Header name for Bearer JWT or ApiKey token.
 */
    private static final String TOKEN_HEADER_PARAM = "X-Authorization";
    private static final long AVG_REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(30);
    protected static final String ACTIVATE_TOKEN_REGEX = "/api/noauth/activate?activateToken=";
    private final LazyInitializer<ExecutorService> executor = LazyInitializer.<ExecutorService>builder()
            .setInitializer(() -> ThingsBoardExecutors.newWorkStealingPool(10, getClass()))
            .get();
    @Getter
    protected final RestTemplate restTemplate;
    protected final RestTemplate loginRestTemplate;
    protected final String baseURL;

    private String username;
    private String password;
    private String mainToken;
    @Getter
    private String refreshToken;
    private long mainTokenExpTs;
    private long refreshTokenExpTs;
    private long clientServerTimeDiff;

    /**
 * Authentication mode for the request interceptor.
 */
    public enum AuthType {JWT, API_KEY}

    /**
 * Creates a client with default {@link RestTemplate} and JWT auth (login required).
 */
    public RestClient(String baseURL) {
        this(new RestTemplate(), baseURL);
    }

    /**
 * Creates a client with custom {@link RestTemplate} and JWT auth.
 */
    public RestClient(RestTemplate restTemplate, String baseURL) {
        this(restTemplate, baseURL, AuthType.JWT, null);
    }

    /**
 * Creates a client with a pre-issued JWT (no username/password login).
 */
    public RestClient(RestTemplate restTemplate, String baseURL, String accessToken) {
        this(restTemplate, baseURL, AuthType.JWT, accessToken);
    }

    /**
 * Full constructor: registers auth interceptor on {@link #restTemplate}.
 * 
 * @param authType JWT (auto refresh) or API_KEY (static ApiKey header)
 * @param token    optional initial JWT/ApiKey; null triggers login on first request
 */
    public RestClient(RestTemplate restTemplate, String baseURL, AuthType authType, String token) {
        this.restTemplate = restTemplate;
        this.loginRestTemplate = new RestTemplate(restTemplate.getRequestFactory());
        this.baseURL = baseURL;
        this.restTemplate.getInterceptors().add((request, bytes, execution) -> {
            HttpRequest wrapper = new HttpRequestWrapper(request);
            switch (authType) {
                case JWT -> {
                    if (token == null) {
                        long calculatedTs = System.currentTimeMillis() + clientServerTimeDiff + AVG_REQUEST_TIMEOUT;
                        if (calculatedTs > mainTokenExpTs) {
                            synchronized (RestClient.this) {
                                if (calculatedTs > mainTokenExpTs) {
                                    if (calculatedTs < refreshTokenExpTs) {
                                        refreshToken();
                                    } else {
                                        doLogin();
                                    }
                                }
                            }
                        }
                    } else {
                        mainToken = token;
                    }
                    wrapper.getHeaders().set(TOKEN_HEADER_PARAM, "Bearer " + mainToken);
                }
                case API_KEY -> {
                    wrapper.getHeaders().set(TOKEN_HEADER_PARAM, "ApiKey " + token);
                }
            }
            return execution.execute(wrapper, bytes);
        });
    }

    /**
 * Factory for API key authentication ({@code X-Authorization: ApiKey &lt;token&gt;}).
 */
    public static RestClient withApiKey(String baseURL, String token) {
        return withApiKey(new RestTemplate(), baseURL, token);
    }

    /**
 * Factory for API key authentication with custom {@link RestTemplate}.
 */
    public static RestClient withApiKey(RestTemplate rt, String baseURL, String token) {
        return new RestClient(rt, baseURL, AuthType.API_KEY, token);
    }

    /**
 * Returns the current main JWT used in X-Authorization header.
 */

    public String getToken() {
        return mainToken;
    }

    /**
 * Refreshes JWT using POST /api/auth/token. Calls `/api/auth/token`.
 */

    public void refreshToken() {
        Map<String, String> refreshTokenRequest = new HashMap<>();
        refreshTokenRequest.put("refreshToken", refreshToken);
        long ts = System.currentTimeMillis();
        ResponseEntity<JsonNode> tokenInfo = loginRestTemplate.postForEntity(baseURL + "/api/auth/token", refreshTokenRequest, JsonNode.class);
        setTokenInfo(ts, tokenInfo.getBody());
    }

    /**
 * Authenticates with username/password via POST /api/auth/login. Calls `/api/auth/login`.
 */

    public void login(String username, String password) {
        this.username = username;
        this.password = password;
        doLogin();
    }

    private void doLogin() {
        long ts = System.currentTimeMillis();
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);
        ResponseEntity<JsonNode> tokenInfo = loginRestTemplate.postForEntity(baseURL + "/api/auth/login", loginRequest, JsonNode.class);
        setTokenInfo(ts, tokenInfo.getBody());
    }

    private synchronized void setTokenInfo(long ts, JsonNode tokenInfo) {
        this.mainToken = tokenInfo.get("token").asText();
        this.refreshToken = tokenInfo.get("refreshToken").asText();
        this.mainTokenExpTs = JWT.decode(this.mainToken).getExpiresAtAsInstant().toEpochMilli();
        this.refreshTokenExpTs = JWT.decode(refreshToken).getExpiresAtAsInstant().toEpochMilli();
        this.clientServerTimeDiff = JWT.decode(this.mainToken).getIssuedAtAsInstant().toEpochMilli() - ts;
    }

    // --- Admin API (/api/admin/**
 * ) ---
 * /** GET admin settings. Calls `/api/admin/settings/{key}`.
 */
    public Optional<AdminSettings> getAdminSettings(String key) {
        try {
            ResponseEntity<AdminSettings> adminSettings = restTemplate.getForEntity(baseURL + "/api/admin/settings/{key}", AdminSettings.class, key);
            return Optional.ofNullable(adminSettings.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update admin settings. Calls `/api/admin/settings`.
 */

    public AdminSettings saveAdminSettings(AdminSettings adminSettings) {
        return restTemplate.postForEntity(baseURL + "/api/admin/settings", adminSettings, AdminSettings.class).getBody();
    }

    /**
 * REST call: send test mail. Calls `/api/admin/settings/testMail`.
 */

    public void sendTestMail(AdminSettings adminSettings) {
        restTemplate.postForLocation(baseURL + "/api/admin/settings/testMail", adminSettings);
    }

    /**
 * REST call: send test sms. Calls `/api/admin/settings/testSms`.
 */

    public void sendTestSms(TestSmsRequest testSmsRequest) {
        restTemplate.postForLocation(baseURL + "/api/admin/settings/testSms", testSmsRequest);
    }

    /**
 * GET security settings. Calls `/api/admin/securitySettings`.
 */

    public Optional<SecuritySettings> getSecuritySettings() {
        try {
            ResponseEntity<SecuritySettings> securitySettings = restTemplate.getForEntity(baseURL + "/api/admin/securitySettings", SecuritySettings.class);
            return Optional.ofNullable(securitySettings.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update security settings. Calls `/api/admin/securitySettings`.
 */

    public SecuritySettings saveSecuritySettings(SecuritySettings securitySettings) {
        return restTemplate.postForEntity(baseURL + "/api/admin/securitySettings", securitySettings, SecuritySettings.class).getBody();
    }

    /**
 * GET jwt settings. Calls `/api/admin/jwtSettings`.
 */

    public Optional<JwtSettings> getJwtSettings() {
        try {
            ResponseEntity<JwtSettings> jwtSettings = restTemplate.getForEntity(baseURL + "/api/admin/jwtSettings", JwtSettings.class);
            return Optional.ofNullable(jwtSettings.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update jwt settings. Calls `/api/admin/jwtSettings`.
 */

    public JwtPair saveJwtSettings(JwtSettings jwtSettings) {
        return restTemplate.postForEntity(baseURL + "/api/admin/jwtSettings", jwtSettings, JwtPair.class).getBody();
    }

    /**
 * GET repository settings. Calls `/api/admin/repositorySettings`.
 */

    public Optional<RepositorySettings> getRepositorySettings() {
        try {
            ResponseEntity<RepositorySettings> repositorySettings = restTemplate.getForEntity(baseURL + "/api/admin/repositorySettings", RepositorySettings.class);
            return Optional.ofNullable(repositorySettings.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: repository settings exists. Calls `/api/admin/repositorySettings/exists`.
 */

    public Boolean repositorySettingsExists() {
        return restTemplate.getForEntity(baseURL + "/api/admin/repositorySettings/exists", Boolean.class).getBody();
    }

    /**
 * POST create or update repository settings. Calls `/api/admin/repositorySettings`.
 */

    public RepositorySettings saveRepositorySettings(RepositorySettings repositorySettings) {
        return restTemplate.postForEntity(baseURL + "/api/admin/repositorySettings", repositorySettings, RepositorySettings.class).getBody();
    }

    /**
 * DELETE repository settings. Calls `/api/admin/repositorySettings`.
 */

    public void deleteRepositorySettings() {
        restTemplate.delete(baseURL + "/api/admin/repositorySettings");
    }

    /**
 * REST call: check repository access. Calls `/api/admin/repositorySettings/checkAccess`.
 */

    public void checkRepositoryAccess(RepositorySettings repositorySettings) {
        restTemplate.postForLocation(baseURL + "/api/admin/repositorySettings/checkAccess", repositorySettings);
    }

    /**
 * GET auto commit settings. Calls `/api/admin/autoCommitSettings`.
 */

    public Optional<AutoCommitSettings> getAutoCommitSettings() {
        try {
            ResponseEntity<AutoCommitSettings> autoCommitSettings = restTemplate.getForEntity(baseURL + "/api/admin/autoCommitSettings", AutoCommitSettings.class);
            return Optional.ofNullable(autoCommitSettings.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: auto commit settings exists. Calls `/api/admin/autoCommitSettings/exists`.
 */

    public Boolean autoCommitSettingsExists() {
        return restTemplate.getForEntity(baseURL + "/api/admin/autoCommitSettings/exists", Boolean.class).getBody();
    }

    /**
 * POST create or update auto commit settings. Calls `/api/admin/autoCommitSettings`.
 */

    public AutoCommitSettings saveAutoCommitSettings(AutoCommitSettings autoCommitSettings) {
        return restTemplate.postForEntity(baseURL + "/api/admin/autoCommitSettings", autoCommitSettings, AutoCommitSettings.class).getBody();
    }

    /**
 * DELETE auto commit settings. Calls `/api/admin/autoCommitSettings`.
 */

    public void deleteAutoCommitSettings() {
        restTemplate.delete(baseURL + "/api/admin/autoCommitSettings");
    }

    /**
 * REST call: check updates. Calls `/api/admin/updates`.
 */

    public Optional<UpdateMessage> checkUpdates() {
        try {
            ResponseEntity<UpdateMessage> updateMsg = restTemplate.getForEntity(baseURL + "/api/admin/updates", UpdateMessage.class);
            return Optional.ofNullable(updateMsg.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET system info. Calls `/api/admin/systemInfo`.
 */

    public SystemInfo getSystemInfo() {
        return restTemplate.getForEntity(baseURL + "/api/admin/systemInfo", SystemInfo.class).getBody();
    }

    // --- Alarm API (/api/alarm/**, /api/alarmsQuery/**) ---

    /**
 * GET entity by id. Calls `/api/alarm/{alarmId}`.
 */

    public Optional<Alarm> getAlarmById(AlarmId alarmId) {
        try {
            ResponseEntity<Alarm> alarm = restTemplate.getForEntity(baseURL + "/api/alarm/{alarmId}", Alarm.class, alarmId.getId());
            return Optional.ofNullable(alarm.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/alarm/info/{alarmId}`.
 */

    public Optional<AlarmInfo> getAlarmInfoById(AlarmId alarmId) {
        try {
            ResponseEntity<AlarmInfo> alarmInfo = restTemplate.getForEntity(baseURL + "/api/alarm/info/{alarmId}", AlarmInfo.class, alarmId.getId());
            return Optional.ofNullable(alarmInfo.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update alarm. Calls `/api/alarm`.
 */

    public Alarm saveAlarm(Alarm alarm) {
        return restTemplate.postForEntity(baseURL + "/api/alarm", alarm, Alarm.class).getBody();
    }

    /**
 * DELETE alarm. Calls `/api/alarm/{alarmId}`.
 */

    public void deleteAlarm(AlarmId alarmId) {
        restTemplate.delete(baseURL + "/api/alarm/{alarmId}", alarmId.getId());
    }

    /**
 * POST acknowledge alarm. Calls `/api/alarm/{alarmId}/ack`.
 */

    public void ackAlarm(AlarmId alarmId) {
        restTemplate.postForLocation(baseURL + "/api/alarm/{alarmId}/ack", null, alarmId.getId());
    }

    /**
 * POST clear alarm. Calls `/api/alarm/{alarmId}/clear`.
 */

    public void clearAlarm(AlarmId alarmId) {
        restTemplate.postForLocation(baseURL + "/api/alarm/{alarmId}/clear", null, alarmId.getId());
    }

    /**
 * POST assign alarm. Calls `/api/alarm/{alarmId}/assign/{userId}`.
 */

    public void assignAlarm(AlarmId alarmId, UserId userId) {
        restTemplate.postForLocation(baseURL + "/api/alarm/{alarmId}/assign/{userId}", null, alarmId.getId(), userId.getId());
    }

    /**
 * DELETE unassign alarm. Calls `/api/alarm/{alarmId}/assign`.
 */

    public void unassignAlarm(AlarmId alarmId) {
        restTemplate.delete(baseURL + "/api/alarm/{alarmId}/assign", alarmId.getId());
    }

    /**
 * GET alarms.
 */

    public PageData<AlarmInfo> getAlarms(EntityId entityId, AlarmSearchStatus searchStatus, AlarmStatus status, TimePageLink pageLink, Boolean fetchOriginator) {
        String urlSecondPart = "/api/alarm/{entityType}/{entityId}?fetchOriginator={fetchOriginator}";
        Map<String, String> params = new HashMap<>();
        params.put("entityType", entityId.getEntityType().name());
        params.put("entityId", entityId.getId().toString());
        params.put("fetchOriginator", String.valueOf(fetchOriginator));
        if (searchStatus != null) {
            params.put("searchStatus", searchStatus.name());
            urlSecondPart += "&searchStatus={searchStatus}";
        }
        if (status != null) {
            params.put("status", status.name());
            urlSecondPart += "&status={status}";
        }

        addTimePageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + urlSecondPart + "&" + getTimeUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AlarmInfo>>() {
                },
                params).getBody();
    }

    /**
 * GET all alarms.
 */

    public PageData<AlarmInfo> getAllAlarms(AlarmSearchStatus searchStatus, AlarmStatus status, TimePageLink pageLink, Boolean fetchOriginator) {
        String urlSecondPart = "/api/alarms?";
        Map<String, String> params = new HashMap<>();
        if (fetchOriginator != null) {
            params.put("fetchOriginator", String.valueOf(fetchOriginator));
            urlSecondPart += "&fetchOriginator={fetchOriginator}";
        }
        if (searchStatus != null) {
            params.put("searchStatus", searchStatus.name());
            urlSecondPart += "&searchStatus={searchStatus}";
        }
        if (status != null) {
            params.put("status", status.name());
            urlSecondPart += "&status={status}";
        }

        addTimePageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + urlSecondPart + "&" + getTimeUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AlarmInfo>>() {
                },
                params).getBody();
    }

    /**
 * GET alarms v2.
 */

    public PageData<AlarmInfo> getAlarmsV2(EntityId entityId, List<AlarmSearchStatus> statusList, List<AlarmSeverity> severityList,
                                           List<String> typeList, String assignedId, TimePageLink pageLink) {
        String urlSecondPart = "/api/v2/alarm/{entityType}/{entityId}?";
        Map<String, String> params = new HashMap<>();
        params.put("entityType", entityId.getEntityType().name());
        params.put("entityId", entityId.getId().toString());
        if (!CollectionUtils.isEmpty(statusList)) {
            params.put("statusList", listEnumToString(statusList));
            urlSecondPart += "&statusList={statusList}";
        }
        if (!CollectionUtils.isEmpty(severityList)) {
            params.put("severityList", listEnumToString(severityList));
            urlSecondPart += "&severityList={severityList}";
        }
        if (!CollectionUtils.isEmpty(typeList)) {
            params.put("typeList", String.join(",", typeList));
            urlSecondPart += "&typeList={typeList}";
        }
        if (assignedId != null) {
            params.put("assignedId", assignedId);
            urlSecondPart += "&assignedId={assignedId}";
        }

        addTimePageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + urlSecondPart + "&" + getTimeUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AlarmInfo>>() {
                },
                params).getBody();
    }

    /**
 * GET all alarms v2.
 */

    public PageData<AlarmInfo> getAllAlarmsV2(List<AlarmSearchStatus> statusList, List<AlarmSeverity> severityList,
                                              List<String> typeList, String assignedId, TimePageLink pageLink) {
        String urlSecondPart = "/api/v2/alarms?";
        Map<String, String> params = new HashMap<>();
        if (!CollectionUtils.isEmpty(statusList)) {
            params.put("statusList", listEnumToString(statusList));
            urlSecondPart += "&statusList={statusList}";
        }
        if (!CollectionUtils.isEmpty(severityList)) {
            params.put("severityList", listEnumToString(severityList));
            urlSecondPart += "&severityList={severityList}";
        }
        if (!CollectionUtils.isEmpty(typeList)) {
            params.put("typeList", String.join(",", typeList));
            urlSecondPart += "&typeList={typeList}";
        }
        if (assignedId != null) {
            params.put("assignedId", assignedId);
            urlSecondPart += "&assignedId={assignedId}";
        }

        addTimePageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + urlSecondPart + "&" + getTimeUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AlarmInfo>>() {
                },
                params).getBody();
    }

    /**
 * GET highest alarm severity. Calls `/api/alarm/highestSeverity/{entityType}/{entityId}?searchStatus={searchStatus}&status={status}`.
 */

    public Optional<AlarmSeverity> getHighestAlarmSeverity(EntityId entityId, AlarmSearchStatus searchStatus, AlarmStatus status) {
        Map<String, String> params = new HashMap<>();
        params.put("entityType", entityId.getEntityType().name());
        params.put("entityId", entityId.getId().toString());
        params.put("searchStatus", searchStatus.name());
        params.put("status", status.name());
        try {
            ResponseEntity<AlarmSeverity> alarmSeverity = restTemplate.getForEntity(baseURL + "/api/alarm/highestSeverity/{entityType}/{entityId}?searchStatus={searchStatus}&status={status}", AlarmSeverity.class, params);
            return Optional.ofNullable(alarmSeverity.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    @Deprecated
    /**
 * POST create or update alarm. Calls `/api/alarm`.
 */
    public Alarm createAlarm(Alarm alarm) {
        return restTemplate.postForEntity(baseURL + "/api/alarm", alarm, Alarm.class).getBody();
    }

    /**
 * GET alarm types. Calls `/api/alarm/types?`.
 */

    public PageData<EntitySubtype> getAlarmTypes(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/alarm/types?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EntitySubtype>>() {
                },
                params).getBody();
    }

    /**
 * POST create or update alarm comment. Calls `/api/alarm/{alarmId}/comment`.
 */

    public AlarmComment saveAlarmComment(AlarmId alarmId, AlarmComment alarmComment) {
        return restTemplate.postForEntity(baseURL + "/api/alarm/{alarmId}/comment", alarmComment, AlarmComment.class, alarmId.getId()).getBody();
    }

    /**
 * DELETE alarm comment. Calls `/api/alarm/{alarmId}/comment/{alarmCommentId}`.
 */

    public void deleteAlarmComment(AlarmId alarmId, AlarmCommentId alarmCommentId) {
        restTemplate.delete(baseURL + "/api/alarm/{alarmId}/comment/{alarmCommentId}",
                alarmId.getId(), alarmCommentId.getId());
    }

    /**
 * GET alarm comments. Calls `/api/alarm/{alarmId}/comment?`.
 */

    public PageData<AlarmCommentInfo> getAlarmComments(AlarmId alarmId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("alarmId", alarmId.getId().toString());
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/alarm/{alarmId}/comment?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AlarmCommentInfo>>() {
                },
                params).getBody();
    }

    /**
 * GET entity by id. Calls `/api/asset/{assetId}`.
 */

    public Optional<Asset> getAssetById(AssetId assetId) {
        try {
            ResponseEntity<Asset> asset = restTemplate.getForEntity(baseURL + "/api/asset/{assetId}", Asset.class, assetId.getId());
            return Optional.ofNullable(asset.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/asset/info/{assetId}`.
 */

    public Optional<AssetInfo> getAssetInfoById(AssetId assetId) {
        try {
            ResponseEntity<AssetInfo> asset = restTemplate.getForEntity(baseURL + "/api/asset/info/{assetId}", AssetInfo.class, assetId.getId());
            return Optional.ofNullable(asset.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update asset. Calls `/api/asset`.
 */

    public Asset saveAsset(Asset asset) {
        return restTemplate.postForEntity(baseURL + "/api/asset", asset, Asset.class).getBody();
    }

    /**
 * DELETE asset. Calls `/api/asset/{assetId}`.
 */

    public void deleteAsset(AssetId assetId) {
        restTemplate.delete(baseURL + "/api/asset/{assetId}", assetId.getId());
    }

    /**
 * POST assign asset to customer. Calls `/api/customer/{customerId}/asset/{assetId}`.
 */

    public Optional<Asset> assignAssetToCustomer(CustomerId customerId, AssetId assetId) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.getId().toString());
        params.put("assetId", assetId.getId().toString());

        try {
            ResponseEntity<Asset> asset = restTemplate.postForEntity(baseURL + "/api/customer/{customerId}/asset/{assetId}", null, Asset.class, params);
            return Optional.ofNullable(asset.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE unassign asset from customer. Calls `/api/customer/asset/{assetId}`.
 */

    public Optional<Asset> unassignAssetFromCustomer(AssetId assetId) {
        try {
            ResponseEntity<Asset> asset = restTemplate.exchange(baseURL + "/api/customer/asset/{assetId}", HttpMethod.DELETE, HttpEntity.EMPTY, Asset.class, assetId.getId());
            return Optional.ofNullable(asset.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST assign asset to public customer. Calls `/api/customer/public/asset/{assetId}`.
 */

    public Optional<Asset> assignAssetToPublicCustomer(AssetId assetId) {
        try {
            ResponseEntity<Asset> asset = restTemplate.postForEntity(baseURL + "/api/customer/public/asset/{assetId}", null, Asset.class, assetId.getId());
            return Optional.ofNullable(asset.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET tenant assets. Calls `/api/tenant/assets?type={type}&`.
 */

    public PageData<Asset> getTenantAssets(PageLink pageLink, String assetType) {
        Map<String, String> params = new HashMap<>();
        params.put("type", assetType);
        addPageLinkToParam(params, pageLink);

        ResponseEntity<PageData<Asset>> assets = restTemplate.exchange(
                baseURL + "/api/tenant/assets?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Asset>>() {
                },
                params);
        return assets.getBody();
    }

    /**
 * GET tenant asset infos. Calls `/api/tenant/assetInfos?type={type}&assetProfileId={assetProfileId}&`.
 */

    public PageData<AssetInfo> getTenantAssetInfos(String type, AssetProfileId assetProfileId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("assetProfileId", assetProfileId != null ? assetProfileId.toString() : null);
        addPageLinkToParam(params, pageLink);

        ResponseEntity<PageData<AssetInfo>> assets = restTemplate.exchange(
                baseURL + "/api/tenant/assetInfos?type={type}&assetProfileId={assetProfileId}&" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AssetInfo>>() {
                },
                params);
        return assets.getBody();
    }

    /**
 * GET tenant asset. Calls `/api/tenant/assets?assetName={assetName}`.
 */

    public Optional<Asset> getTenantAsset(String assetName) {
        try {
            ResponseEntity<Asset> asset = restTemplate.getForEntity(baseURL + "/api/tenant/assets?assetName={assetName}", Asset.class, assetName);
            return Optional.ofNullable(asset.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET customer assets. Calls `/api/customer/{customerId}/assets?type={type}&`.
 */

    public PageData<Asset> getCustomerAssets(CustomerId customerId, PageLink pageLink, String assetType) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.getId().toString());
        params.put("type", assetType);
        addPageLinkToParam(params, pageLink);

        ResponseEntity<PageData<Asset>> assets = restTemplate.exchange(
                baseURL + "/api/customer/{customerId}/assets?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Asset>>() {
                },
                params);
        return assets.getBody();
    }

    /**
 * GET customer asset infos. Calls `/api/customer/{customerId}/assetInfos?type={type}&assetProfileId={assetProfileId}&`.
 */

    public PageData<AssetInfo> getCustomerAssetInfos(CustomerId customerId, String assetType, AssetProfileId assetProfileId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.getId().toString());
        params.put("type", assetType);
        params.put("assetProfileId", assetProfileId != null ? assetProfileId.toString() : null);
        addPageLinkToParam(params, pageLink);

        ResponseEntity<PageData<AssetInfo>> assets = restTemplate.exchange(
                baseURL + "/api/customer/{customerId}/assetInfos?type={type}&assetProfileId={assetProfileId}&" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AssetInfo>>() {
                },
                params);
        return assets.getBody();
    }

    /**
 * GET entity by id. Calls `/api/assets?assetIds={assetIds}`.
 */

    public List<Asset> getAssetsByIds(List<AssetId> assetIds) {
        return restTemplate.exchange(
                        baseURL + "/api/assets?assetIds={assetIds}",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<List<Asset>>() {
                        },
                        listIdsToString(assetIds))
                .getBody();
    }

    /**
 * POST entity query or search: by query. Calls `/api/assets`.
 */

    public List<Asset> findByQuery(AssetSearchQuery query) {
        return restTemplate.exchange(
                URI.create(baseURL + "/api/assets"),
                HttpMethod.POST,
                new HttpEntity<>(query),
                new ParameterizedTypeReference<List<Asset>>() {
                }).getBody();
    }

    @Deprecated(since = "3.6.2")
    /**
 * GET asset types. Calls `/api/asset/types`.
 */
    public List<EntitySubtype> getAssetTypes() {
        return restTemplate.exchange(URI.create(
                        baseURL + "/api/asset/types"),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntitySubtype>>() {
                }).getBody();
    }

    /**
 * GET asset profile names. Calls `/api/assetProfile/names?activeOnly={activeOnly}`.
 */

    public List<EntitySubtype> getAssetProfileNames(boolean activeOnly) {
        return restTemplate.exchange(
                baseURL + "/api/assetProfile/names?activeOnly={activeOnly}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntitySubtype>>() {
                }, activeOnly).getBody();
    }

    /**
 * REST call: process assets bulk import. Calls `/api/asset/bulk_import`.
 */

    public BulkImportResult<Asset> processAssetsBulkImport(BulkImportRequest request) {
        return restTemplate.exchange(
                baseURL + "/api/asset/bulk_import",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<BulkImportResult<Asset>>() {
                }).getBody();
    }

    @Deprecated
    /**
 * POST entity query or search: asset. Calls `/api/tenant/assets?assetName={assetName}`.
 */
    public Optional<Asset> findAsset(String name) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("assetName", name);
        try {
            ResponseEntity<Asset> assetEntity = restTemplate.getForEntity(baseURL + "/api/tenant/assets?assetName={assetName}", Asset.class, params);
            return Optional.of(assetEntity.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    @Deprecated
    /**
 * POST create or update asset. Calls `/api/asset`.
 */
    public Asset createAsset(Asset asset) {
        return restTemplate.postForEntity(baseURL + "/api/asset", asset, Asset.class).getBody();
    }

    @Deprecated
    /**
 * POST create or update asset. Calls `/api/asset`.
 */
    public Asset createAsset(String name, String type) {
        Asset asset = new Asset();
        asset.setName(name);
        asset.setType(type);
        return restTemplate.postForEntity(baseURL + "/api/asset", asset, Asset.class).getBody();
    }

    @Deprecated
    /**
 * POST assign asset. Calls `/api/customer/{customerId}/asset/{assetId}`.
 */
    public Asset assignAsset(CustomerId customerId, AssetId assetId) {
        return restTemplate.postForEntity(baseURL + "/api/customer/{customerId}/asset/{assetId}", HttpEntity.EMPTY, Asset.class,
                customerId.toString(), assetId.toString()).getBody();
    }

    /**
 * GET audit logs by customer id. Calls `/api/audit/logs/customer/{customerId}?actionTypes={actionTypes}&`.
 */

    public PageData<AuditLog> getAuditLogsByCustomerId(CustomerId customerId, TimePageLink pageLink, List<ActionType> actionTypes) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.getId().toString());
        params.put("actionTypes", listEnumToString(actionTypes));
        addTimePageLinkToParam(params, pageLink);

        ResponseEntity<PageData<AuditLog>> auditLog = restTemplate.exchange(
                baseURL + "/api/audit/logs/customer/{customerId}?actionTypes={actionTypes}&" + getTimeUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AuditLog>>() {
                },
                params);
        return auditLog.getBody();
    }

    /**
 * GET audit logs by user id. Calls `/api/audit/logs/user/{userId}?actionTypes={actionTypes}&`.
 */

    public PageData<AuditLog> getAuditLogsByUserId(UserId userId, TimePageLink pageLink, List<ActionType> actionTypes) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId.getId().toString());
        params.put("actionTypes", listEnumToString(actionTypes));
        addTimePageLinkToParam(params, pageLink);

        ResponseEntity<PageData<AuditLog>> auditLog = restTemplate.exchange(
                baseURL + "/api/audit/logs/user/{userId}?actionTypes={actionTypes}&" + getTimeUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AuditLog>>() {
                },
                params);
        return auditLog.getBody();
    }

    /**
 * GET audit logs by entity id. Calls `/api/audit/logs/entity/{entityType}/{entityId}?actionTypes={actionTypes}&`.
 */

    public PageData<AuditLog> getAuditLogsByEntityId(EntityId entityId, List<ActionType> actionTypes, TimePageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("entityType", entityId.getEntityType().name());
        params.put("entityId", entityId.getId().toString());
        params.put("actionTypes", listEnumToString(actionTypes));
        addTimePageLinkToParam(params, pageLink);

        ResponseEntity<PageData<AuditLog>> auditLog = restTemplate.exchange(
                baseURL + "/api/audit/logs/entity/{entityType}/{entityId}?actionTypes={actionTypes}&" + getTimeUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AuditLog>>() {
                },
                params);
        return auditLog.getBody();
    }

    /**
 * GET audit logs. Calls `/api/audit/logs?actionTypes={actionTypes}&`.
 */

    public PageData<AuditLog> getAuditLogs(TimePageLink pageLink, List<ActionType> actionTypes) {
        Map<String, String> params = new HashMap<>();
        params.put("actionTypes", listEnumToString(actionTypes));
        addTimePageLinkToParam(params, pageLink);

        ResponseEntity<PageData<AuditLog>> auditLog = restTemplate.exchange(
                baseURL + "/api/audit/logs?actionTypes={actionTypes}&" + getTimeUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AuditLog>>() {
                },
                params);
        return auditLog.getBody();
    }

    /**
 * GET activate token.
 */

    public String getActivateToken(UserId userId) {
        String activationLink = getActivationLink(userId);
        return activationLink.substring(activationLink.lastIndexOf(ACTIVATE_TOKEN_REGEX) + ACTIVATE_TOKEN_REGEX.length());
    }

    /**
 * GET user. Calls `/api/auth/user`.
 */

    public Optional<User> getUser() {
        ResponseEntity<User> user = restTemplate.getForEntity(baseURL + "/api/auth/user", User.class);
        return Optional.ofNullable(user.getBody());
    }

    /**
 * REST call: logout. Calls `/api/auth/logout`.
 */

    public void logout() {
        restTemplate.postForLocation(baseURL + "/api/auth/logout", null);
    }

    /**
 * REST call: change password. Calls `/api/auth/changePassword`.
 */

    public void changePassword(String currentPassword, String newPassword) {
        ObjectNode changePasswordRequest = JacksonUtil.newObjectNode();
        changePasswordRequest.put("currentPassword", currentPassword);
        changePasswordRequest.put("newPassword", newPassword);
        restTemplate.postForLocation(baseURL + "/api/auth/changePassword", changePasswordRequest);
    }

    /**
 * GET user password policy. Calls `/api/noauth/userPasswordPolicy`.
 */

    public Optional<UserPasswordPolicy> getUserPasswordPolicy() {
        try {
            ResponseEntity<UserPasswordPolicy> userPasswordPolicy = restTemplate.getForEntity(baseURL + "/api/noauth/userPasswordPolicy", UserPasswordPolicy.class);
            return Optional.ofNullable(userPasswordPolicy.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: check activate token. Calls `/api/noauth/activate?activateToken={activateToken}`.
 */

    public ResponseEntity<String> checkActivateToken(UserId userId) {
        String activateToken = getActivateToken(userId);
        return restTemplate.getForEntity(baseURL + "/api/noauth/activate?activateToken={activateToken}", String.class, activateToken);
    }

    /**
 * REST call: request reset password by email. Calls `/api/noauth/resetPasswordByEmail`.
 */

    public void requestResetPasswordByEmail(String email) {
        ObjectNode resetPasswordByEmailRequest = JacksonUtil.newObjectNode();
        resetPasswordByEmailRequest.put("email", email);
        restTemplate.postForLocation(baseURL + "/api/noauth/resetPasswordByEmail", resetPasswordByEmailRequest);
    }

    /**
 * REST call: activate user.
 */

    public Optional<JsonNode> activateUser(UserId userId, String password) {
        return activateUser(userId, password, true);
    }

    /**
 * REST call: activate user. Calls `/api/noauth/activate?sendActivationMail={sendActivationMail}`.
 */

    public Optional<JsonNode> activateUser(UserId userId, String password, boolean sendActivationMail) {
        ObjectNode activateRequest = JacksonUtil.newObjectNode();
        activateRequest.put("activateToken", getActivateToken(userId));
        activateRequest.put("password", password);
        try {
            ResponseEntity<JsonNode> jsonNode = restTemplate.postForEntity(baseURL + "/api/noauth/activate?sendActivationMail={sendActivationMail}", activateRequest, JsonNode.class, sendActivationMail);
            return Optional.ofNullable(jsonNode.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET component descriptor by clazz. Calls `/api/component/{componentDescriptorClazz}`.
 */

    public Optional<ComponentDescriptor> getComponentDescriptorByClazz(String componentDescriptorClazz) {
        try {
            ResponseEntity<ComponentDescriptor> componentDescriptor = restTemplate.getForEntity(baseURL + "/api/component/{componentDescriptorClazz}", ComponentDescriptor.class, componentDescriptorClazz);
            return Optional.ofNullable(componentDescriptor.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET component descriptors by type.
 */

    public List<ComponentDescriptor> getComponentDescriptorsByType(ComponentType componentType) {
        return getComponentDescriptorsByType(componentType, RuleChainType.CORE);
    }

    /**
 * GET component descriptors by type. Calls `/api/components/`.
 */

    public List<ComponentDescriptor> getComponentDescriptorsByType(ComponentType componentType, RuleChainType ruleChainType) {
        return restTemplate.exchange(
                baseURL + "/api/components/" + componentType.name() + "/?ruleChainType={ruleChainType}",
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<ComponentDescriptor>>() {
                },
                ruleChainType).getBody();
    }

    /**
 * GET component descriptors by types.
 */

    public List<ComponentDescriptor> getComponentDescriptorsByTypes(List<ComponentType> componentTypes) {
        return getComponentDescriptorsByTypes(componentTypes, RuleChainType.CORE);
    }

    /**
 * GET component descriptors by types. Calls `/api/components?componentTypes={componentTypes}&ruleChainType={ruleChainType}`.
 */

    public List<ComponentDescriptor> getComponentDescriptorsByTypes(List<ComponentType> componentTypes, RuleChainType ruleChainType) {
        return restTemplate.exchange(
                        baseURL + "/api/components?componentTypes={componentTypes}&ruleChainType={ruleChainType}",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<List<ComponentDescriptor>>() {
                        },
                        listEnumToString(componentTypes),
                        ruleChainType)
                .getBody();
    }

    /**
 * GET entity by id. Calls `/api/customer/{customerId}`.
 */

    public Optional<Customer> getCustomerById(CustomerId customerId) {
        try {
            ResponseEntity<Customer> customer = restTemplate.getForEntity(baseURL + "/api/customer/{customerId}", Customer.class, customerId.getId());
            return Optional.ofNullable(customer.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/customer/{customerId}/shortInfo`.
 */

    public Optional<JsonNode> getShortCustomerInfoById(CustomerId customerId) {
        try {
            ResponseEntity<JsonNode> customerInfo = restTemplate.getForEntity(baseURL + "/api/customer/{customerId}/shortInfo", JsonNode.class, customerId.getId());
            return Optional.ofNullable(customerInfo.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/customer/{customerId}/title`.
 */

    public String getCustomerTitleById(CustomerId customerId) {
        return restTemplate.getForObject(baseURL + "/api/customer/{customerId}/title", String.class, customerId.getId());
    }

    /**
 * POST create or update customer. Calls `/api/customer`.
 */

    public Customer saveCustomer(Customer customer) {
        return restTemplate.postForEntity(baseURL + "/api/customer", customer, Customer.class).getBody();
    }

    /**
 * DELETE customer. Calls `/api/customer/{customerId}`.
 */

    public void deleteCustomer(CustomerId customerId) {
        restTemplate.delete(baseURL + "/api/customer/{customerId}", customerId.getId());
    }

    /**
 * GET customers. Calls `/api/customers?`.
 */

    public PageData<Customer> getCustomers(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);

        ResponseEntity<PageData<Customer>> customer = restTemplate.exchange(
                baseURL + "/api/customers?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Customer>>() {
                },
                params);
        return customer.getBody();
    }

    /**
 * GET tenant customer. Calls `/api/tenant/customers?customerTitle={customerTitle}`.
 */

    public Optional<Customer> getTenantCustomer(String customerTitle) {
        try {
            ResponseEntity<Customer> customer = restTemplate.getForEntity(baseURL + "/api/tenant/customers?customerTitle={customerTitle}", Customer.class, customerTitle);
            return Optional.ofNullable(customer.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    @Deprecated
    /**
 * POST entity query or search: customer. Calls `/api/tenant/customers?customerTitle={customerTitle}`.
 */
    public Optional<Customer> findCustomer(String title) {
        Map<String, String> params = new HashMap<>();
        params.put("customerTitle", title);
        try {
            ResponseEntity<Customer> customerEntity = restTemplate.getForEntity(baseURL + "/api/tenant/customers?customerTitle={customerTitle}", Customer.class, params);
            return Optional.of(customerEntity.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    @Deprecated
    /**
 * POST create or update customer. Calls `/api/customer`.
 */
    public Customer createCustomer(Customer customer) {
        return restTemplate.postForEntity(baseURL + "/api/customer", customer, Customer.class).getBody();
    }

    @Deprecated
    /**
 * POST create or update customer. Calls `/api/customer`.
 */
    public Customer createCustomer(String title) {
        Customer customer = new Customer();
        customer.setTitle(title);
        return restTemplate.postForEntity(baseURL + "/api/customer", customer, Customer.class).getBody();
    }

    /**
 * GET server time. Calls `/api/dashboard/serverTime`.
 */

    public Long getServerTime() {
        return restTemplate.getForObject(baseURL + "/api/dashboard/serverTime", Long.class);
    }

    /**
 * GET max datapoints limit. Calls `/api/dashboard/maxDatapointsLimit`.
 */

    public Long getMaxDatapointsLimit() {
        return restTemplate.getForObject(baseURL + "/api/dashboard/maxDatapointsLimit", Long.class);
    }

    /**
 * GET entity by id. Calls `/api/dashboard/info/{dashboardId}`.
 */

    public Optional<DashboardInfo> getDashboardInfoById(DashboardId dashboardId) {
        try {
            ResponseEntity<DashboardInfo> dashboardInfo = restTemplate.getForEntity(baseURL + "/api/dashboard/info/{dashboardId}", DashboardInfo.class, dashboardId.getId());
            return Optional.ofNullable(dashboardInfo.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/dashboard/{dashboardId}`.
 */

    public Optional<Dashboard> getDashboardById(DashboardId dashboardId) {
        try {
            ResponseEntity<Dashboard> dashboard = restTemplate.getForEntity(baseURL + "/api/dashboard/{dashboardId}", Dashboard.class, dashboardId.getId());
            return Optional.ofNullable(dashboard.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update dashboard. Calls `/api/dashboard`.
 */

    public Dashboard saveDashboard(Dashboard dashboard) {
        return restTemplate.postForEntity(baseURL + "/api/dashboard", dashboard, Dashboard.class).getBody();
    }

    /**
 * DELETE dashboard. Calls `/api/dashboard/{dashboardId}`.
 */

    public void deleteDashboard(DashboardId dashboardId) {
        restTemplate.delete(baseURL + "/api/dashboard/{dashboardId}", dashboardId.getId());
    }

    /**
 * POST assign dashboard to customer. Calls `/api/customer/{customerId}/dashboard/{dashboardId}`.
 */

    public Optional<Dashboard> assignDashboardToCustomer(CustomerId customerId, DashboardId dashboardId) {
        try {
            ResponseEntity<Dashboard> dashboard = restTemplate.postForEntity(baseURL + "/api/customer/{customerId}/dashboard/{dashboardId}", null, Dashboard.class, customerId.getId(), dashboardId.getId());
            return Optional.ofNullable(dashboard.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE unassign dashboard from customer. Calls `/api/customer/{customerId}/dashboard/{dashboardId}`.
 */

    public Optional<Dashboard> unassignDashboardFromCustomer(CustomerId customerId, DashboardId dashboardId) {
        try {
            ResponseEntity<Dashboard> dashboard = restTemplate.exchange(baseURL + "/api/customer/{customerId}/dashboard/{dashboardId}", HttpMethod.DELETE, HttpEntity.EMPTY, Dashboard.class, customerId.getId(), dashboardId.getId());
            return Optional.ofNullable(dashboard.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: update dashboard customers. Calls `/api/dashboard/{dashboardId}/customers`.
 */

    public Optional<Dashboard> updateDashboardCustomers(DashboardId dashboardId, List<CustomerId> customerIds) {
        Object[] customerIdArray = customerIds.stream().map(customerId -> customerId.getId().toString()).toArray();
        try {
            ResponseEntity<Dashboard> dashboard = restTemplate.postForEntity(baseURL + "/api/dashboard/{dashboardId}/customers", customerIdArray, Dashboard.class, dashboardId.getId());
            return Optional.ofNullable(dashboard.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: add dashboard customers. Calls `/api/dashboard/{dashboardId}/customers/add`.
 */

    public Optional<Dashboard> addDashboardCustomers(DashboardId dashboardId, List<CustomerId> customerIds) {
        Object[] customerIdArray = customerIds.stream().map(customerId -> customerId.getId().toString()).toArray();
        try {
            ResponseEntity<Dashboard> dashboard = restTemplate.postForEntity(baseURL + "/api/dashboard/{dashboardId}/customers/add", customerIdArray, Dashboard.class, dashboardId.getId());
            return Optional.ofNullable(dashboard.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: remove dashboard customers. Calls `/api/dashboard/{dashboardId}/customers/remove`.
 */

    public Optional<Dashboard> removeDashboardCustomers(DashboardId dashboardId, List<CustomerId> customerIds) {
        Object[] customerIdArray = customerIds.stream().map(customerId -> customerId.getId().toString()).toArray();
        try {
            ResponseEntity<Dashboard> dashboard = restTemplate.postForEntity(baseURL + "/api/dashboard/{dashboardId}/customers/remove", customerIdArray, Dashboard.class, dashboardId.getId());
            return Optional.ofNullable(dashboard.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST assign dashboard to public customer. Calls `/api/customer/public/dashboard/{dashboardId}`.
 */

    public Optional<Dashboard> assignDashboardToPublicCustomer(DashboardId dashboardId) {
        try {
            ResponseEntity<Dashboard> dashboard = restTemplate.postForEntity(baseURL + "/api/customer/public/dashboard/{dashboardId}", null, Dashboard.class, dashboardId.getId());
            return Optional.ofNullable(dashboard.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE unassign dashboard from public customer. Calls `/api/customer/public/dashboard/{dashboardId}`.
 */

    public Optional<Dashboard> unassignDashboardFromPublicCustomer(DashboardId dashboardId) {
        try {
            ResponseEntity<Dashboard> dashboard = restTemplate.exchange(baseURL + "/api/customer/public/dashboard/{dashboardId}", HttpMethod.DELETE, HttpEntity.EMPTY, Dashboard.class, dashboardId.getId());
            return Optional.ofNullable(dashboard.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET tenant dashboards. Calls `/api/tenant/{tenantId}/dashboards?`.
 */

    public PageData<DashboardInfo> getTenantDashboards(TenantId tenantId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("tenantId", tenantId.getId().toString());
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenant/{tenantId}/dashboards?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<DashboardInfo>>() {
                }, params).getBody();
    }

    /**
 * GET tenant dashboards. Calls `/api/tenant/dashboards?`.
 */

    public PageData<DashboardInfo> getTenantDashboards(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenant/dashboards?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<DashboardInfo>>() {
                }, params).getBody();
    }

    /**
 * GET customer dashboards. Calls `/api/customer/{customerId}/dashboards?`.
 */

    public PageData<DashboardInfo> getCustomerDashboards(CustomerId customerId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.getId().toString());
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/customer/{customerId}/dashboards?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<DashboardInfo>>() {
                }, params).getBody();
    }

    @Deprecated
    /**
 * POST create or update dashboard. Calls `/api/dashboard`.
 */
    public Dashboard createDashboard(Dashboard dashboard) {
        return restTemplate.postForEntity(baseURL + "/api/dashboard", dashboard, Dashboard.class).getBody();
    }

    @Deprecated
    /**
 * POST entity query or search: tenant dashboards. Calls `/api/tenant/dashboards?pageSize=100000`.
 */
    public List<DashboardInfo> findTenantDashboards() {
        try {
            ResponseEntity<PageData<DashboardInfo>> dashboards =
                    restTemplate.exchange(baseURL + "/api/tenant/dashboards?pageSize=100000", HttpMethod.GET, null, new ParameterizedTypeReference<PageData<DashboardInfo>>() {
                    });
            return dashboards.getBody().getData();
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Collections.emptyList();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/device/{deviceId}`.
 */

    // --- Device API (/api/device/**) ---

    public Optional<Device> getDeviceById(DeviceId deviceId) {
        try {
            ResponseEntity<Device> device = restTemplate.getForEntity(baseURL + "/api/device/{deviceId}", Device.class, deviceId.getId());
            return Optional.ofNullable(device.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/device/info/{deviceId}`.
 */

    public Optional<DeviceInfo> getDeviceInfoById(DeviceId deviceId) {
        try {
            ResponseEntity<DeviceInfo> device = restTemplate.getForEntity(baseURL + "/api/device/info/{deviceId}", DeviceInfo.class, deviceId);
            return Optional.ofNullable(device.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update device.
 */

    public Device saveDevice(Device device) {
        return saveDevice(device, null);
    }

    /**
 * POST create or update device. Calls `/api/device?accessToken={accessToken}`.
 */

    public Device saveDevice(Device device, String accessToken) {
        return restTemplate.postForEntity(baseURL + "/api/device?accessToken={accessToken}", device, Device.class, accessToken).getBody();
    }

    /**
 * DELETE device. Calls `/api/device/{deviceId}`.
 */

    public void deleteDevice(DeviceId deviceId) {
        restTemplate.delete(baseURL + "/api/device/{deviceId}", deviceId.getId());
    }

    /**
 * POST assign device to customer. Calls `/api/customer/{customerId}/device/{deviceId}`.
 */

    public Optional<Device> assignDeviceToCustomer(CustomerId customerId, DeviceId deviceId) {
        try {
            ResponseEntity<Device> device = restTemplate.postForEntity(baseURL + "/api/customer/{customerId}/device/{deviceId}", null, Device.class, customerId.getId(), deviceId.getId());
            return Optional.ofNullable(device.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE unassign device from customer. Calls `/api/customer/device/{deviceId}`.
 */

    public Optional<Device> unassignDeviceFromCustomer(DeviceId deviceId) {
        try {
            ResponseEntity<Device> device = restTemplate.exchange(baseURL + "/api/customer/device/{deviceId}", HttpMethod.DELETE, HttpEntity.EMPTY, Device.class, deviceId.getId());
            return Optional.ofNullable(device.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST assign device to public customer. Calls `/api/customer/public/device/{deviceId}`.
 */

    public Optional<Device> assignDeviceToPublicCustomer(DeviceId deviceId) {
        try {
            ResponseEntity<Device> device = restTemplate.postForEntity(baseURL + "/api/customer/public/device/{deviceId}", null, Device.class, deviceId.getId());
            return Optional.ofNullable(device.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET device credentials by device id. Calls `/api/device/{deviceId}/credentials`.
 */

    public Optional<DeviceCredentials> getDeviceCredentialsByDeviceId(DeviceId deviceId) {
        try {
            ResponseEntity<DeviceCredentials> deviceCredentials = restTemplate.getForEntity(baseURL + "/api/device/{deviceId}/credentials", DeviceCredentials.class, deviceId.getId());
            return Optional.ofNullable(deviceCredentials.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update device credentials. Calls `/api/device/credentials`.
 */

    public DeviceCredentials saveDeviceCredentials(DeviceCredentials deviceCredentials) {
        return restTemplate.postForEntity(baseURL + "/api/device/credentials", deviceCredentials, DeviceCredentials.class).getBody();
    }

    /**
 * POST create or update device with credentials. Calls `/api/device-with-credentials`.
 */

    public Optional<Device> saveDeviceWithCredentials(Device device, DeviceCredentials credentials) {
        try {
            SaveDeviceWithCredentialsRequest request = new SaveDeviceWithCredentialsRequest(device, credentials);
            ResponseEntity<Device> deviceOpt = restTemplate.postForEntity(baseURL + "/api/device-with-credentials", request, Device.class);
            return Optional.ofNullable(deviceOpt.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET tenant devices. Calls `/api/tenant/devices?type={type}&`.
 */

    public PageData<Device> getTenantDevices(String type, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenant/devices?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Device>>() {
                }, params).getBody();
    }

    /**
 * GET tenant device infos. Calls `/api/tenant/deviceInfos?type={type}&deviceProfileId={deviceProfileId}&`.
 */

    public PageData<DeviceInfo> getTenantDeviceInfos(String type, Boolean active, DeviceProfileId deviceProfileId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("deviceProfileId", deviceProfileId != null ? deviceProfileId.toString() : null);
        if (active != null) {
            params.put("active", active.toString());
        }
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenant/deviceInfos?type={type}&deviceProfileId={deviceProfileId}&"
                        + (active != null ? "active={active}&" : "") + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<DeviceInfo>>() {
                }, params).getBody();
    }

    /**
 * GET tenant device. Calls `/api/tenant/devices?deviceName={deviceName}`.
 */

    public Optional<Device> getTenantDevice(String deviceName) {
        try {
            ResponseEntity<Device> device = restTemplate.getForEntity(baseURL + "/api/tenant/devices?deviceName={deviceName}", Device.class, deviceName);
            return Optional.ofNullable(device.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET customer devices. Calls `/api/customer/{customerId}/devices?type={type}&`.
 */

    public PageData<Device> getCustomerDevices(CustomerId customerId, String deviceType, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.getId().toString());
        params.put("type", deviceType);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/customer/{customerId}/devices?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Device>>() {
                }, params).getBody();
    }

    /**
 * GET customer device infos. Calls `/api/customer/{customerId}/devices?type={type}&deviceProfileId={deviceProfileId}&`.
 */

    public PageData<DeviceInfo> getCustomerDeviceInfos(CustomerId customerId, String deviceType, DeviceProfileId deviceProfileId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.toString());
        params.put("type", deviceType);
        params.put("deviceProfileId", deviceProfileId != null ? deviceProfileId.toString() : null);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/customer/{customerId}/devices?type={type}&deviceProfileId={deviceProfileId}&" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<DeviceInfo>>() {
                }, params).getBody();
    }

    /**
 * GET entity by id. Calls `/api/devices?deviceIds={deviceIds}`.
 */

    public List<Device> getDevicesByIds(List<DeviceId> deviceIds) {
        return restTemplate.exchange(baseURL + "/api/devices?deviceIds={deviceIds}",
                HttpMethod.GET,
                HttpEntity.EMPTY, new ParameterizedTypeReference<List<Device>>() {
                }, listIdsToString(deviceIds)).getBody();
    }

    /**
 * POST entity query or search: by query. Calls `/api/devices`.
 */

    public List<Device> findByQuery(DeviceSearchQuery query) {
        return restTemplate.exchange(
                baseURL + "/api/devices",
                HttpMethod.POST,
                new HttpEntity<>(query),
                new ParameterizedTypeReference<List<Device>>() {
                }).getBody();
    }

    @Deprecated(since = "3.6.2")
    /**
 * GET device types. Calls `/api/device/types`.
 */
    public List<EntitySubtype> getDeviceTypes() {
        return restTemplate.exchange(
                baseURL + "/api/device/types",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntitySubtype>>() {
                }).getBody();
    }

    /**
 * GET device profile names. Calls `/api/deviceProfile/names?activeOnly={activeOnly}`.
 */

    public List<EntitySubtype> getDeviceProfileNames(boolean activeOnly) {
        return restTemplate.exchange(
                baseURL + "/api/deviceProfile/names?activeOnly={activeOnly}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntitySubtype>>() {
                }, activeOnly).getBody();
    }

    /**
 * GET entity by id.
 */

    public List<DeviceProfileInfo> getDeviceProfileInfosByIds(Set<UUID> ids) {
        URIBuilder builder;
        try {
            builder = new URIBuilder(baseURL);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid base URL: " + baseURL, e);
        }

        builder.appendPath("/api/deviceProfileInfos");

        String commaSeparatedIds = ids.stream()
                .filter(Objects::nonNull)
                .map(UUID::toString)
                .collect(joining(","));

        builder.addParameter("deviceProfileIds", commaSeparatedIds);

        URI uri;
        try {
            uri = builder.build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to construct API URI from base URL and provided params", e);
        }

        return restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<DeviceProfileInfo>>() {}).getBody();
    }

    /**
 * REST call: claim device. Calls `/api/customer/device/{deviceName}/claim`.
 */

    public JsonNode claimDevice(String deviceName, ClaimRequest claimRequest) {
        return restTemplate.exchange(
                baseURL + "/api/customer/device/{deviceName}/claim",
                HttpMethod.POST,
                new HttpEntity<>(claimRequest),
                new ParameterizedTypeReference<JsonNode>() {
                }, deviceName).getBody();
    }

    /**
 * REST call: re claim device. Calls `/api/customer/device/{deviceName}/claim`.
 */

    public void reClaimDevice(String deviceName) {
        restTemplate.delete(baseURL + "/api/customer/device/{deviceName}/claim", deviceName);
    }

    /**
 * POST assign device to tenant. Calls `/api/tenant/{tenantId}/device/{deviceId}`.
 */

    public Device assignDeviceToTenant(TenantId tenantId, DeviceId deviceId) {
        return restTemplate.postForEntity(
                baseURL + "/api/tenant/{tenantId}/device/{deviceId}",
                HttpEntity.EMPTY, Device.class, tenantId, deviceId).getBody();
    }

    /**
 * REST call: count by device profile and empty ota package. Calls `/api/devices/count/{otaPackageType}/{deviceProfileId}`.
 */

    public Long countByDeviceProfileAndEmptyOtaPackage(OtaPackageType otaPackageType, DeviceProfileId deviceProfileId) {
        Map<String, String> params = new HashMap<>();
        params.put("otaPackageType", otaPackageType.name());
        params.put("deviceProfileId", deviceProfileId.getId().toString());

        return restTemplate.exchange(
                baseURL + "/api/devices/count/{otaPackageType}/{deviceProfileId}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Long>() {
                },
                params
        ).getBody();
    }

    /**
 * REST call: process devices bulk import. Calls `/api/device/bulk_import`.
 */

    public BulkImportResult<Device> processDevicesBulkImport(BulkImportRequest request) {
        return restTemplate.exchange(
                baseURL + "/api/device/bulk_import",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<BulkImportResult<Device>>() {
                }).getBody();
    }

    @Deprecated
    /**
 * POST create or update device.
 */
    public Device createDevice(String name, String type) {
        Device device = new Device();
        device.setName(name);
        device.setType(type);
        return doCreateDevice(device, null);
    }

    @Deprecated
    /**
 * POST create or update device.
 */
    public Device createDevice(Device device) {
        return doCreateDevice(device, null);
    }

    @Deprecated
    /**
 * POST create or update device.
 */
    public Device createDevice(Device device, String accessToken) {
        return doCreateDevice(device, accessToken);
    }

    @Deprecated
    private Device doCreateDevice(Device device, String accessToken) {
        Map<String, String> params = new HashMap<>();
        String deviceCreationUrl = "/api/device";
        if (!StringUtils.isEmpty(accessToken)) {
            deviceCreationUrl = deviceCreationUrl + "?accessToken={accessToken}";
            params.put("accessToken", accessToken);
        }
        return restTemplate.postForEntity(baseURL + deviceCreationUrl, device, Device.class, params).getBody();
    }

    @Deprecated
    /**
 * GET credentials. Calls `/api/device/`.
 */
    public DeviceCredentials getCredentials(DeviceId id) {
        return restTemplate.getForEntity(baseURL + "/api/device/" + id.getId().toString() + "/credentials", DeviceCredentials.class).getBody();
    }

    @Deprecated
    /**
 * POST entity query or search: device. Calls `/api/tenant/devices?deviceName={deviceName}`.
 */
    public Optional<Device> findDevice(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("deviceName", name);
        try {
            ResponseEntity<Device> deviceEntity = restTemplate.getForEntity(baseURL + "/api/tenant/devices?deviceName={deviceName}", Device.class, params);
            return Optional.of(deviceEntity.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    @Deprecated
    /**
 * REST call: update device credentials.
 */
    public DeviceCredentials updateDeviceCredentials(DeviceId deviceId, String token) {
        DeviceCredentials deviceCredentials = getCredentials(deviceId);
        deviceCredentials.setCredentialsType(DeviceCredentialsType.ACCESS_TOKEN);
        deviceCredentials.setCredentialsId(token);
        return saveDeviceCredentials(deviceCredentials);
    }

    @Deprecated
    /**
 * POST assign device. Calls `/api/customer/{customerId}/device/{deviceId}`.
 */
    public Device assignDevice(CustomerId customerId, DeviceId deviceId) {
        return restTemplate.postForEntity(baseURL + "/api/customer/{customerId}/device/{deviceId}", null, Device.class,
                customerId.toString(), deviceId.toString()).getBody();
    }

    /**
 * GET entity by id. Calls `/api/deviceProfile/{deviceProfileId}`.
 */

    public Optional<DeviceProfile> getDeviceProfileById(DeviceProfileId deviceProfileId) {
        try {
            ResponseEntity<DeviceProfile> deviceProfile = restTemplate.getForEntity(baseURL + "/api/deviceProfile/{deviceProfileId}", DeviceProfile.class, deviceProfileId);
            return Optional.ofNullable(deviceProfile.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/deviceProfileInfo/{deviceProfileId}`.
 */

    public Optional<DeviceProfileInfo> getDeviceProfileInfoById(DeviceProfileId deviceProfileId) {
        try {
            ResponseEntity<DeviceProfileInfo> deviceProfileInfo = restTemplate.getForEntity(baseURL + "/api/deviceProfileInfo/{deviceProfileId}", DeviceProfileInfo.class, deviceProfileId);
            return Optional.ofNullable(deviceProfileInfo.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET default device profile info. Calls `/api/deviceProfileInfo/default`.
 */

    public DeviceProfileInfo getDefaultDeviceProfileInfo() {
        return restTemplate.getForEntity(baseURL + "/api/deviceProfileInfo/default", DeviceProfileInfo.class).getBody();
    }

    /**
 * POST create or update device profile. Calls `/api/deviceProfile`.
 */

    public DeviceProfile saveDeviceProfile(DeviceProfile deviceProfile) {
        return restTemplate.postForEntity(baseURL + "/api/deviceProfile", deviceProfile, DeviceProfile.class).getBody();
    }

    /**
 * DELETE device profile. Calls `/api/deviceProfile/{deviceProfileId}`.
 */

    public void deleteDeviceProfile(DeviceProfileId deviceProfileId) {
        restTemplate.delete(baseURL + "/api/deviceProfile/{deviceProfileId}", deviceProfileId);
    }

    /**
 * REST call: set default device profile. Calls `/api/deviceProfile/{deviceProfileId}/default`.
 */

    public DeviceProfile setDefaultDeviceProfile(DeviceProfileId deviceProfileId) {
        return restTemplate.postForEntity(
                baseURL + "/api/deviceProfile/{deviceProfileId}/default",
                HttpEntity.EMPTY, DeviceProfile.class, deviceProfileId).getBody();
    }

    /**
 * GET device profiles. Calls `/api/deviceProfiles?`.
 */

    public PageData<DeviceProfile> getDeviceProfiles(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/deviceProfiles?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<DeviceProfile>>() {
                }, params).getBody();
    }

    /**
 * GET device profile infos. Calls `/api/deviceProfileInfos?deviceTransportType={deviceTransportType}&`.
 */

    public PageData<DeviceProfileInfo> getDeviceProfileInfos(PageLink pageLink, DeviceTransportType deviceTransportType) {
        Map<String, String> params = new HashMap<>();
        params.put("deviceTransportType", deviceTransportType != null ? deviceTransportType.name() : null);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/deviceProfileInfos?deviceTransportType={deviceTransportType}&" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<DeviceProfileInfo>>() {
                }, params).getBody();
    }

    /**
 * GET entity by id. Calls `/api/assetProfile/{assetProfileId}`.
 */

    public Optional<AssetProfile> getAssetProfileById(AssetProfileId assetProfileId) {
        try {
            ResponseEntity<AssetProfile> assetProfile = restTemplate.getForEntity(baseURL + "/api/assetProfile/{assetProfileId}", AssetProfile.class, assetProfileId);
            return Optional.ofNullable(assetProfile.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/assetProfileInfo/{assetProfileId}`.
 */

    public Optional<AssetProfileInfo> getAssetProfileInfoById(AssetProfileId assetProfileId) {
        try {
            ResponseEntity<AssetProfileInfo> assetProfileInfo = restTemplate.getForEntity(baseURL + "/api/assetProfileInfo/{assetProfileId}", AssetProfileInfo.class, assetProfileId);
            return Optional.ofNullable(assetProfileInfo.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET default asset profile info. Calls `/api/assetProfileInfo/default`.
 */

    public AssetProfileInfo getDefaultAssetProfileInfo() {
        return restTemplate.getForEntity(baseURL + "/api/assetProfileInfo/default", AssetProfileInfo.class).getBody();
    }

    /**
 * POST create or update asset profile. Calls `/api/assetProfile`.
 */

    public AssetProfile saveAssetProfile(AssetProfile assetProfile) {
        return restTemplate.postForEntity(baseURL + "/api/assetProfile", assetProfile, AssetProfile.class).getBody();
    }

    /**
 * DELETE asset profile. Calls `/api/assetProfile/{assetProfileId}`.
 */

    public void deleteAssetProfile(AssetProfileId assetProfileId) {
        restTemplate.delete(baseURL + "/api/assetProfile/{assetProfileId}", assetProfileId);
    }

    /**
 * REST call: set default asset profile. Calls `/api/assetProfile/{assetProfileId}/default`.
 */

    public AssetProfile setDefaultAssetProfile(AssetProfileId assetProfileId) {
        return restTemplate.postForEntity(
                baseURL + "/api/assetProfile/{assetProfileId}/default",
                HttpEntity.EMPTY, AssetProfile.class, assetProfileId).getBody();
    }

    /**
 * GET asset profiles. Calls `/api/assetProfiles?`.
 */

    public PageData<AssetProfile> getAssetProfiles(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/assetProfiles?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AssetProfile>>() {
                }, params).getBody();
    }

    /**
 * GET asset profile infos. Calls `/api/assetProfileInfos?`.
 */

    public PageData<AssetProfileInfo> getAssetProfileInfos(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/assetProfileInfos?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<AssetProfileInfo>>() {
                }, params).getBody();
    }

    /**
 * GET entity by id.
 */

    public List<AssetProfileInfo> getAssetProfilesByIds(Set<UUID> ids) {
        URIBuilder builder;
        try {
            builder = new URIBuilder(baseURL);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid base URL: " + baseURL, e);
        }

        builder.appendPath("/api/assetProfileInfos");

        String commaSeparatedIds = ids.stream()
                .filter(Objects::nonNull)
                .map(UUID::toString)
                .collect(joining(","));

        builder.addParameter("assetProfileIds", commaSeparatedIds);

        URI uri;
        try {
            uri = builder.build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to construct API URI from base URL and provided params", e);
        }

        return restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<AssetProfileInfo>>() {}).getBody();
    }

    /**
 * REST call: count entities by query. Calls `/api/entitiesQuery/count`.
 */

    public Long countEntitiesByQuery(EntityCountQuery query) {
        return restTemplate.postForObject(baseURL + "/api/entitiesQuery/count", query, Long.class);
    }

    /**
 * POST entity query or search: entity data by query. Calls `/api/entitiesQuery/find`.
 */

    public PageData<EntityData> findEntityDataByQuery(EntityDataQuery query) {
        return restTemplate.exchange(
                baseURL + "/api/entitiesQuery/find",
                HttpMethod.POST, new HttpEntity<>(query),
                new ParameterizedTypeReference<PageData<EntityData>>() {
                }).getBody();
    }

    /**
 * @deprecated Use {@link #findAvailableEntityKeysV2(EntityDataQuery, boolean, boolean, Set, boolean)} instead.
 */
    @Deprecated(forRemoval = true)
    /**
 * POST entity query or search: available entity keys by query.
 */
    public AvailableEntityKeys findAvailableEntityKeysByQuery(EntityDataQuery query, boolean includeTimeseries, boolean includeAttributes, AttributeScope scope) {
        var uri = UriComponentsBuilder.fromUriString(baseURL)
                .path("/api/entitiesQuery/find/keys")
                .queryParam("timeseries", includeTimeseries)
                .queryParam("attributes", includeAttributes)
                .queryParamIfPresent("scope", Optional.ofNullable(scope))
                .build()
                .toUri();
        return restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(query), new ParameterizedTypeReference<AvailableEntityKeys>() {}).getBody();
    }

    @SneakyThrows(URISyntaxException.class)
    /**
 * POST entity query or search: available entity keys v2.
 */
    public AvailableEntityKeysV2 findAvailableEntityKeysV2(
            EntityDataQuery query, boolean includeTimeseries, boolean includeAttributes, Set<AttributeScope> scopes, boolean includeSamples
    ) {
        var builder = new URIBuilder(baseURL).appendPath("/api/v2/entitiesQuery/find/keys")
                .addParameter("includeTimeseries", String.valueOf(includeTimeseries))
                .addParameter("includeAttributes", String.valueOf(includeAttributes))
                .addParameter("includeSamples", String.valueOf(includeSamples));
        if (scopes != null) {
            for (AttributeScope scope : scopes) {
                builder.addParameter("scopes", scope.name());
            }
        }
        return restTemplate.exchange(builder.build(), HttpMethod.POST, new HttpEntity<>(query), new ParameterizedTypeReference<AvailableEntityKeysV2>() {}).getBody();
    }

    /**
 * POST entity query or search: alarm data by query. Calls `/api/alarmsQuery/find`.
 */

    public PageData<AlarmData> findAlarmDataByQuery(AlarmDataQuery query) {
        return restTemplate.exchange(
                baseURL + "/api/alarmsQuery/find",
                HttpMethod.POST, new HttpEntity<>(query),
                new ParameterizedTypeReference<PageData<AlarmData>>() {
                }).getBody();
    }

    /**
 * REST call: count alarms by query. Calls `/api/alarmsQuery/count`.
 */

    public Long countAlarmsByQuery(AlarmCountQuery query) {
        return restTemplate.postForObject(baseURL + "/api/alarmsQuery/count", query, Long.class);
    }

    /**
 * POST create or update relation. Calls `/api/relation`.
 */

    public void saveRelation(EntityRelation relation) {
        restTemplate.postForLocation(baseURL + "/api/relation", relation);
    }

    /**
 * POST create or update relation v2. Calls `/api/v2/relation`.
 */

    public EntityRelation saveRelationV2(EntityRelation relation) {
        return restTemplate.postForEntity(baseURL + "/api/v2/relation", relation, EntityRelation.class).getBody();
    }

    /**
 * DELETE relation. Calls `/api/relation?fromId={fromId}&fromType={fromType}&relationType={relationType}&relationTypeGroup={relationTypeGroup}&toId={toId}&toType={toType}`.
 */

    public void deleteRelation(EntityId fromId, String relationType, RelationTypeGroup relationTypeGroup, EntityId toId) {
        Map<String, String> params = new HashMap<>();
        params.put("fromId", fromId.getId().toString());
        params.put("fromType", fromId.getEntityType().name());
        params.put("relationType", relationType);
        params.put("relationTypeGroup", relationTypeGroup.name());
        params.put("toId", toId.getId().toString());
        params.put("toType", toId.getEntityType().name());
        restTemplate.delete(baseURL + "/api/relation?fromId={fromId}&fromType={fromType}&relationType={relationType}&relationTypeGroup={relationTypeGroup}&toId={toId}&toType={toType}", params);
    }

    /**
 * DELETE relation v2. Calls `/api/relation?fromId={fromId}&fromType={fromType}&relationType={relationType}&relationTypeGroup={relationTypeGroup}&toId={toId}&toType={toType}`.
 */

    public Optional<EntityRelation> deleteRelationV2(EntityId fromId, String relationType, RelationTypeGroup relationTypeGroup, EntityId toId) {
        Map<String, String> params = new HashMap<>();
        params.put("fromId", fromId.getId().toString());
        params.put("fromType", fromId.getEntityType().name());
        params.put("relationType", relationType);
        params.put("relationTypeGroup", relationTypeGroup.name());
        params.put("toId", toId.getId().toString());
        params.put("toType", toId.getEntityType().name());
        try {
            var relation = restTemplate.exchange(baseURL + "/api/relation?fromId={fromId}&fromType={fromType}&relationType={relationType}&relationTypeGroup={relationTypeGroup}&toId={toId}&toType={toType}", HttpMethod.DELETE, HttpEntity.EMPTY, EntityRelation.class, params);
            return Optional.ofNullable(relation.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE relations. Calls `/api/relations?entityId={entityId}&entityType={entityType}`.
 */

    public void deleteRelations(EntityId entityId) {
        restTemplate.delete(baseURL + "/api/relations?entityId={entityId}&entityType={entityType}", entityId.getId().toString(), entityId.getEntityType().name());
    }

    /**
 * GET relation.
 */

    public Optional<EntityRelation> getRelation(EntityId fromId, String relationType, RelationTypeGroup relationTypeGroup, EntityId toId) {
        Map<String, String> params = new HashMap<>();
        params.put("fromId", fromId.getId().toString());
        params.put("fromType", fromId.getEntityType().name());
        params.put("relationType", relationType);
        params.put("relationTypeGroup", relationTypeGroup.name());
        params.put("toId", toId.getId().toString());
        params.put("toType", toId.getEntityType().name());

        try {
            ResponseEntity<EntityRelation> entityRelation = restTemplate.getForEntity(
                    baseURL + "/api/relation?fromId={fromId}&fromType={fromType}&relationType={relationType}&relationTypeGroup={relationTypeGroup}&toId={toId}&toType={toType}",
                    EntityRelation.class,
                    params);
            return Optional.ofNullable(entityRelation.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST entity query or search: by from. Calls `/api/relations?fromId={fromId}&fromType={fromType}&relationTypeGroup={relationTypeGroup}`.
 */

    public List<EntityRelation> findByFrom(EntityId fromId, RelationTypeGroup relationTypeGroup) {
        Map<String, String> params = new HashMap<>();
        params.put("fromId", fromId.getId().toString());
        params.put("fromType", fromId.getEntityType().name());
        params.put("relationTypeGroup", relationTypeGroup.name());

        return restTemplate.exchange(
                baseURL + "/api/relations?fromId={fromId}&fromType={fromType}&relationTypeGroup={relationTypeGroup}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntityRelation>>() {
                },
                params).getBody();
    }

    /**
 * POST entity query or search: info by from. Calls `/api/relations/info?fromId={fromId}&fromType={fromType}&relationTypeGroup={relationTypeGroup}`.
 */

    public List<EntityRelationInfo> findInfoByFrom(EntityId fromId, RelationTypeGroup relationTypeGroup) {
        Map<String, String> params = new HashMap<>();
        params.put("fromId", fromId.getId().toString());
        params.put("fromType", fromId.getEntityType().name());
        params.put("relationTypeGroup", relationTypeGroup.name());

        return restTemplate.exchange(
                baseURL + "/api/relations/info?fromId={fromId}&fromType={fromType}&relationTypeGroup={relationTypeGroup}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntityRelationInfo>>() {
                },
                params).getBody();
    }

    /**
 * POST entity query or search: by from. Calls `/api/relations?fromId={fromId}&fromType={fromType}&relationType={relationType}&relationTypeGroup={relationTypeGroup}`.
 */

    public List<EntityRelation> findByFrom(EntityId fromId, String relationType, RelationTypeGroup relationTypeGroup) {
        Map<String, String> params = new HashMap<>();
        params.put("fromId", fromId.getId().toString());
        params.put("fromType", fromId.getEntityType().name());
        params.put("relationType", relationType);
        params.put("relationTypeGroup", relationTypeGroup.name());

        return restTemplate.exchange(
                baseURL + "/api/relations?fromId={fromId}&fromType={fromType}&relationType={relationType}&relationTypeGroup={relationTypeGroup}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntityRelation>>() {
                },
                params).getBody();
    }

    /**
 * POST entity query or search: by to. Calls `/api/relations?toId={toId}&toType={toType}&relationTypeGroup={relationTypeGroup}`.
 */

    public List<EntityRelation> findByTo(EntityId toId, RelationTypeGroup relationTypeGroup) {
        Map<String, String> params = new HashMap<>();
        params.put("toId", toId.getId().toString());
        params.put("toType", toId.getEntityType().name());
        params.put("relationTypeGroup", relationTypeGroup.name());

        return restTemplate.exchange(
                baseURL + "/api/relations?toId={toId}&toType={toType}&relationTypeGroup={relationTypeGroup}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntityRelation>>() {
                },
                params).getBody();
    }

    /**
 * POST entity query or search: info by to. Calls `/api/relations/info?toId={toId}&toType={toType}&relationTypeGroup={relationTypeGroup}`.
 */

    public List<EntityRelationInfo> findInfoByTo(EntityId toId, RelationTypeGroup relationTypeGroup) {
        Map<String, String> params = new HashMap<>();
        params.put("toId", toId.getId().toString());
        params.put("toType", toId.getEntityType().name());
        params.put("relationTypeGroup", relationTypeGroup.name());

        return restTemplate.exchange(
                baseURL + "/api/relations/info?toId={toId}&toType={toType}&relationTypeGroup={relationTypeGroup}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntityRelationInfo>>() {
                },
                params).getBody();
    }

    /**
 * POST entity query or search: by to. Calls `/api/relations?toId={toId}&toType={toType}&relationType={relationType}&relationTypeGroup={relationTypeGroup}`.
 */

    public List<EntityRelation> findByTo(EntityId toId, String relationType, RelationTypeGroup relationTypeGroup) {
        Map<String, String> params = new HashMap<>();
        params.put("toId", toId.getId().toString());
        params.put("toType", toId.getEntityType().name());
        params.put("relationType", relationType);
        params.put("relationTypeGroup", relationTypeGroup.name());

        return restTemplate.exchange(
                baseURL + "/api/relations?toId={toId}&toType={toType}&relationType={relationType}&relationTypeGroup={relationTypeGroup}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntityRelation>>() {
                },
                params).getBody();
    }

    /**
 * POST entity query or search: by query. Calls `/api/relations`.
 */

    public List<EntityRelation> findByQuery(EntityRelationsQuery query) {
        return restTemplate.exchange(
                baseURL + "/api/relations",
                HttpMethod.POST,
                new HttpEntity<>(query),
                new ParameterizedTypeReference<List<EntityRelation>>() {
                }).getBody();
    }

    /**
 * POST entity query or search: info by query. Calls `/api/relations/info`.
 */

    public List<EntityRelationInfo> findInfoByQuery(EntityRelationsQuery query) {
        return restTemplate.exchange(
                baseURL + "/api/relations/info",
                HttpMethod.POST,
                new HttpEntity<>(query),
                new ParameterizedTypeReference<List<EntityRelationInfo>>() {
                }).getBody();
    }

    @Deprecated
    /**
 * REST call: make relation. Calls `/api/relation`.
 */
    public EntityRelation makeRelation(String relationType, EntityId idFrom, EntityId idTo) {
        EntityRelation relation = new EntityRelation();
        relation.setFrom(idFrom);
        relation.setTo(idTo);
        relation.setType(relationType);
        return restTemplate.postForEntity(baseURL + "/api/relation", relation, EntityRelation.class).getBody();
    }

    /**
 * GET entity by id. Calls `/api/entityView/{entityViewId}`.
 */

    public Optional<EntityView> getEntityViewById(EntityViewId entityViewId) {
        try {
            ResponseEntity<EntityView> entityView = restTemplate.getForEntity(baseURL + "/api/entityView/{entityViewId}", EntityView.class, entityViewId.getId());
            return Optional.ofNullable(entityView.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/entityView/info/{entityViewId}`.
 */

    public Optional<EntityViewInfo> getEntityViewInfoById(EntityViewId entityViewId) {
        try {
            ResponseEntity<EntityViewInfo> entityView = restTemplate.getForEntity(baseURL + "/api/entityView/info/{entityViewId}", EntityViewInfo.class, entityViewId);
            return Optional.ofNullable(entityView.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update entity view. Calls `/api/entityView`.
 */

    public EntityView saveEntityView(EntityView entityView) {
        return restTemplate.postForEntity(baseURL + "/api/entityView", entityView, EntityView.class).getBody();
    }

    /**
 * DELETE entity view. Calls `/api/entityView/{entityViewId}`.
 */

    public void deleteEntityView(EntityViewId entityViewId) {
        restTemplate.delete(baseURL + "/api/entityView/{entityViewId}", entityViewId.getId());
    }

    /**
 * GET tenant entity view. Calls `/api/tenant/entityViews?entityViewName={entityViewName}`.
 */

    public Optional<EntityView> getTenantEntityView(String entityViewName) {
        try {
            ResponseEntity<EntityView> entityView = restTemplate.getForEntity(baseURL + "/api/tenant/entityViews?entityViewName={entityViewName}", EntityView.class, entityViewName);
            return Optional.ofNullable(entityView.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST assign entity view to customer. Calls `/api/customer/{customerId}/entityView/{entityViewId}`.
 */

    public Optional<EntityView> assignEntityViewToCustomer(CustomerId customerId, EntityViewId entityViewId) {
        try {
            ResponseEntity<EntityView> entityView = restTemplate.postForEntity(baseURL + "/api/customer/{customerId}/entityView/{entityViewId}", null, EntityView.class, customerId.getId(), entityViewId.getId());
            return Optional.ofNullable(entityView.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE unassign entity view from customer. Calls `/api/customer/entityView/{entityViewId}`.
 */

    public Optional<EntityView> unassignEntityViewFromCustomer(EntityViewId entityViewId) {
        try {
            ResponseEntity<EntityView> entityView = restTemplate.exchange(baseURL + "/api/customer/entityView/{entityViewId}", HttpMethod.DELETE, HttpEntity.EMPTY, EntityView.class, entityViewId.getId());
            return Optional.ofNullable(entityView.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET customer entity views. Calls `/api/customer/{customerId}/entityViews?type={type}&`.
 */

    public PageData<EntityView> getCustomerEntityViews(CustomerId customerId, String entityViewType, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.getId().toString());
        params.put("type", entityViewType);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/customer/{customerId}/entityViews?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EntityView>>() {
                }, params).getBody();
    }

    /**
 * GET customer entity view infos. Calls `/api/customer/{customerId}/entityViewInfos?type={type}&`.
 */

    public PageData<EntityViewInfo> getCustomerEntityViewInfos(CustomerId customerId, String entityViewType, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.toString());
        params.put("type", entityViewType);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/customer/{customerId}/entityViewInfos?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EntityViewInfo>>() {
                }, params).getBody();
    }

    /**
 * GET tenant entity views. Calls `/api/tenant/entityViews?type={type}&`.
 */

    public PageData<EntityView> getTenantEntityViews(String entityViewType, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("type", entityViewType);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenant/entityViews?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EntityView>>() {
                }, params).getBody();
    }

    /**
 * GET tenant entity view infos. Calls `/api/tenant/entityViewInfos?type={type}&`.
 */

    public PageData<EntityViewInfo> getTenantEntityViewInfos(String entityViewType, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("type", entityViewType);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenant/entityViewInfos?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EntityViewInfo>>() {
                }, params).getBody();
    }

    /**
 * POST entity query or search: by query. Calls `/api/entityViews`.
 */

    public List<EntityView> findByQuery(EntityViewSearchQuery query) {
        return restTemplate.exchange(baseURL + "/api/entityViews", HttpMethod.POST, new HttpEntity<>(query), new ParameterizedTypeReference<List<EntityView>>() {
        }).getBody();
    }

    /**
 * GET entity view types. Calls `/api/entityView/types`.
 */

    public List<EntitySubtype> getEntityViewTypes() {
        return restTemplate.exchange(baseURL + "/api/entityView/types", HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<EntitySubtype>>() {
        }).getBody();
    }

    /**
 * POST assign entity view to public customer. Calls `/api/customer/public/entityView/{entityViewId}`.
 */

    public Optional<EntityView> assignEntityViewToPublicCustomer(EntityViewId entityViewId) {
        try {
            ResponseEntity<EntityView> entityView = restTemplate.postForEntity(baseURL + "/api/customer/public/entityView/{entityViewId}", null, EntityView.class, entityViewId.getId());
            return Optional.ofNullable(entityView.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET events. Calls `/api/events/{entityType}/{entityId}/{eventType}?tenantId={tenantId}&`.
 */

    public PageData<EventInfo> getEvents(EntityId entityId, String eventType, TenantId tenantId, TimePageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("entityType", entityId.getEntityType().name());
        params.put("entityId", entityId.getId().toString());
        params.put("eventType", eventType);
        params.put("tenantId", tenantId.getId().toString());
        addTimePageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + "/api/events/{entityType}/{entityId}/{eventType}?tenantId={tenantId}&" + getTimeUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EventInfo>>() {
                },
                params).getBody();
    }

    /**
 * GET events. Calls `/api/events/{entityType}/{entityId}?tenantId={tenantId}&`.
 */

    public PageData<EventInfo> getEvents(EntityId entityId, TenantId tenantId, TimePageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("entityType", entityId.getEntityType().name());
        params.put("entityId", entityId.getId().toString());
        params.put("tenantId", tenantId.getId().toString());
        addTimePageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + "/api/events/{entityType}/{entityId}?tenantId={tenantId}&" + getTimeUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EventInfo>>() {
                }, params).getBody();
    }

    /**
 * POST create or update client registration template. Calls `/api/oauth2/config/template`.
 */

    public OAuth2ClientRegistrationTemplate saveClientRegistrationTemplate(OAuth2ClientRegistrationTemplate clientRegistrationTemplate) {
        return restTemplate.postForEntity(baseURL + "/api/oauth2/config/template", clientRegistrationTemplate, OAuth2ClientRegistrationTemplate.class).getBody();
    }

    /**
 * DELETE client registration template. Calls `/api/oauth2/config/template/{clientRegistrationTemplateId}`.
 */

    public void deleteClientRegistrationTemplate(OAuth2ClientRegistrationTemplateId oAuth2ClientRegistrationTemplateId) {
        restTemplate.delete(baseURL + "/api/oauth2/config/template/{clientRegistrationTemplateId}", oAuth2ClientRegistrationTemplateId);
    }

    /**
 * GET client registration templates. Calls `/api/oauth2/config/template`.
 */

    public List<OAuth2ClientRegistrationTemplate> getClientRegistrationTemplates() {
        return restTemplate.exchange(
                baseURL + "/api/oauth2/config/template",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<OAuth2ClientRegistrationTemplate>>() {
                }).getBody();
    }

    /**
 * GET oauth2clients.
 */

    public List<OAuth2ClientLoginInfo> getOAuth2Clients(String pkgName, PlatformType platformType) {
        Map<String, String> params = new HashMap<>();
        StringBuilder urlBuilder = new StringBuilder(baseURL);
        urlBuilder.append("/api/noauth/oauth2Clients");
        if (pkgName != null) {
            urlBuilder.append("?pkgName={pkgName}");
            params.put("pkgName", pkgName);
        }
        if (platformType != null) {
            if (pkgName != null) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            urlBuilder.append("platform={platform}");
            params.put("platform", platformType.name());
        }
        return restTemplate.exchange(
                urlBuilder.toString(),
                HttpMethod.POST,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<OAuth2ClientLoginInfo>>() {
                }, params).getBody();
    }

    /**
 * GET tenant oauth2clients. Calls `/api/oauth2/client/infos`.
 */

    public PageData<OAuth2ClientInfo> getTenantOAuth2Clients() {
        return restTemplate.exchange(
                baseURL + "/api/oauth2/client/infos",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<OAuth2ClientInfo>>() {
                }).getBody();
    }

    /**
 * GET entity by id. Calls `/api/oauth2/client/{id}`.
 */

    public Optional<OAuth2Client> getOauth2ClientById(OAuth2ClientId oAuth2ClientId) {
        try {
            ResponseEntity<OAuth2Client> oauth2Client = restTemplate.getForEntity(baseURL + "/api/oauth2/client/{id}", OAuth2Client.class, oAuth2ClientId.getId());
            return Optional.ofNullable(oauth2Client.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update oauth2client. Calls `/api/oauth2/client`.
 */

    public OAuth2Client saveOAuth2Client(OAuth2Client oAuth2Client) {
        return restTemplate.postForEntity(baseURL + "/api/oauth2/client", oAuth2Client, OAuth2Client.class).getBody();
    }

    /**
 * DELETE oauth2client. Calls `/api/oauth2/client/{id}`.
 */

    public void deleteOauth2CLient(OAuth2ClientId oAuth2ClientId) {
        restTemplate.delete(baseURL + "/api/oauth2/client/{id}", oAuth2ClientId.getId());
    }

    /**
 * GET tenant domain infos. Calls `/api/domain/infos?`.
 */

    public PageData<DomainInfo> getTenantDomainInfos(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/domain/infos?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<DomainInfo>>() {
                },
                params).getBody();
    }

    /**
 * GET entity by id. Calls `/api/domain/info/{id}`.
 */

    public Optional<DomainInfo> getDomainInfoById(DomainId domainId) {
        try {
            ResponseEntity<DomainInfo> domainInfo = restTemplate.getForEntity(baseURL + "/api/domain/info/{id}", DomainInfo.class, domainId.getId());
            return Optional.ofNullable(domainInfo.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update domain. Calls `/api/domain`.
 */

    public Domain saveDomain(Domain domain) {
        return restTemplate.postForEntity(baseURL + "/api/domain", domain, Domain.class).getBody();
    }

    /**
 * DELETE domain. Calls `/api/domain/{id}`.
 */

    public void deleteDomain(DomainId domainId) {
        restTemplate.delete(baseURL + "/api/domain/{id}", domainId.getId());
    }

    /**
 * REST call: update domain oauth2clients. Calls `/api/domain/{id}/oauth2Clients`.
 */

    public void updateDomainOauth2Clients(DomainId domainId, UUID[] oauth2ClientIds) {
        restTemplate.postForLocation(baseURL + "/api/domain/{id}/oauth2Clients", oauth2ClientIds, domainId.getId());
    }

    /**
 * GET tenant mobile apps. Calls `/api/mobile/app?`.
 */

    public PageData<MobileApp> getTenantMobileApps(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/mobile/app?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<MobileApp>>() {
                },
                params).getBody();
    }

    /**
 * GET entity by id. Calls `/api/mobile/app/{id}`.
 */

    public Optional<MobileApp> getMobileAppById(MobileAppId mobileAppId) {
        try {
            ResponseEntity<MobileApp> mobileApp = restTemplate.getForEntity(baseURL + "/api/mobile/app/{id}", MobileApp.class, mobileAppId.getId());
            return Optional.ofNullable(mobileApp.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update mobile app. Calls `/api/mobile/app`.
 */

    public MobileApp saveMobileApp(MobileApp mobileApp) {
        return restTemplate.postForEntity(baseURL + "/api/mobile/app", mobileApp, MobileApp.class).getBody();
    }

    /**
 * DELETE mobile app. Calls `/api/mobile/app/{id}`.
 */

    public void deleteMobileApp(MobileAppId mobileAppId) {
        restTemplate.delete(baseURL + "/api/mobile/app/{id}", mobileAppId.getId());
    }

    /**
 * GET tenant mobile bundle infos. Calls `/api/mobile/bundle/infos?`.
 */

    public PageData<MobileAppBundleInfo> getTenantMobileBundleInfos(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/mobile/bundle/infos?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<MobileAppBundleInfo>>() {
                },
                params).getBody();
    }

    /**
 * GET entity by id. Calls `/api/mobile/bundle/{id}`.
 */

    public Optional<MobileAppBundle> getMobileBundleById(MobileAppBundleId mobileAppBundleId) {
        try {
            ResponseEntity<MobileAppBundle> mobileApp = restTemplate.getForEntity(baseURL + "/api/mobile/bundle/{id}", MobileAppBundle.class, mobileAppBundleId.getId());
            return Optional.ofNullable(mobileApp.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update mobile bundle. Calls `/api/mobile/bundle`.
 */

    public MobileAppBundle saveMobileBundle(MobileAppBundle mobileAppBundle) {
        return restTemplate.postForEntity(baseURL + "/api/mobile/bundle", mobileAppBundle, MobileAppBundle.class).getBody();
    }

    /**
 * DELETE mobile bundle. Calls `/api/mobile/bundle/{id}`.
 */

    public void deleteMobileBundle(MobileAppBundleId mobileAppBundleId) {
        restTemplate.delete(baseURL + "/api/mobile/bundle/{id}", mobileAppBundleId.getId());
    }

    /**
 * REST call: update mobile app bundle oauth2clients. Calls `/api/mobile/bundle/{id}/oauth2Clients`.
 */

    public void updateMobileAppBundleOauth2Clients(MobileAppBundleId mobileAppBundleId, UUID[] oauth2ClientIds) {
        restTemplate.postForLocation(baseURL + "/api/mobile/bundle/{id}/oauth2Clients", oauth2ClientIds, mobileAppBundleId.getId());
    }

    /**
 * GET login processing url. Calls `/api/oauth2/loginProcessingUrl`.
 */

    public String getLoginProcessingUrl() {
        return restTemplate.getForEntity(baseURL + "/api/oauth2/loginProcessingUrl", String.class).getBody();
    }

    /**
 * REST call: handle one way device rpcrequest. Calls `/api/rpc/oneway/{deviceId}`.
 */

    public void handleOneWayDeviceRPCRequest(DeviceId deviceId, JsonNode requestBody) {
        restTemplate.postForLocation(baseURL + "/api/rpc/oneway/{deviceId}", requestBody, deviceId.getId());
    }

    /**
 * REST call: handle two way device rpcrequest. Calls `/api/rpc/twoway/{deviceId}`.
 */

    public JsonNode handleTwoWayDeviceRPCRequest(DeviceId deviceId, JsonNode requestBody) {
        return restTemplate.exchange(
                baseURL + "/api/rpc/twoway/{deviceId}",
                HttpMethod.POST,
                new HttpEntity<>(requestBody),
                new ParameterizedTypeReference<JsonNode>() {
                },
                deviceId.getId()).getBody();
    }

    /**
 * GET entity by id. Calls `/api/ruleChain/{ruleChainId}`.
 */

    public Optional<RuleChain> getRuleChainById(RuleChainId ruleChainId) {
        try {
            ResponseEntity<RuleChain> ruleChain = restTemplate.getForEntity(baseURL + "/api/ruleChain/{ruleChainId}", RuleChain.class, ruleChainId.getId());
            return Optional.ofNullable(ruleChain.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET rule chain meta data. Calls `/api/ruleChain/{ruleChainId}/metadata`.
 */

    public Optional<RuleChainMetaData> getRuleChainMetaData(RuleChainId ruleChainId) {
        try {
            ResponseEntity<RuleChainMetaData> ruleChainMetaData = restTemplate.getForEntity(baseURL + "/api/ruleChain/{ruleChainId}/metadata", RuleChainMetaData.class, ruleChainId.getId());
            return Optional.ofNullable(ruleChainMetaData.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update rule chain. Calls `/api/ruleChain`.
 */

    public RuleChain saveRuleChain(RuleChain ruleChain) {
        return restTemplate.postForEntity(baseURL + "/api/ruleChain", ruleChain, RuleChain.class).getBody();
    }

    /**
 * POST create or update rule chain. Calls `/api/ruleChain/device/default`.
 */

    public RuleChain saveRuleChain(DefaultRuleChainCreateRequest request) {
        return restTemplate.postForEntity(baseURL + "/api/ruleChain/device/default", request, RuleChain.class).getBody();
    }

    /**
 * REST call: set root rule chain. Calls `/api/ruleChain/{ruleChainId}/root`.
 */

    public Optional<RuleChain> setRootRuleChain(RuleChainId ruleChainId) {
        try {
            ResponseEntity<RuleChain> ruleChain = restTemplate.postForEntity(baseURL + "/api/ruleChain/{ruleChainId}/root", null, RuleChain.class, ruleChainId.getId());
            return Optional.ofNullable(ruleChain.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update rule chain meta data. Calls `/api/ruleChain/metadata`.
 */

    public RuleChainMetaData saveRuleChainMetaData(RuleChainMetaData ruleChainMetaData) {
        return restTemplate.postForEntity(baseURL + "/api/ruleChain/metadata", ruleChainMetaData, RuleChainMetaData.class).getBody();
    }

    /**
 * GET rule chains.
 */

    public PageData<RuleChain> getRuleChains(PageLink pageLink) {
        return getRuleChains(RuleChainType.CORE, pageLink);
    }

    /**
 * GET rule chains. Calls `/api/ruleChains?type={type}&`.
 */

    public PageData<RuleChain> getRuleChains(RuleChainType ruleChainType, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("type", ruleChainType.name());
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/ruleChains?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<RuleChain>>() {
                },
                params).getBody();
    }

    /**
 * DELETE rule chain. Calls `/api/ruleChain/{ruleChainId}`.
 */

    public void deleteRuleChain(RuleChainId ruleChainId) {
        restTemplate.delete(baseURL + "/api/ruleChain/{ruleChainId}", ruleChainId.getId());
    }

    /**
 * GET latest rule node debug input. Calls `/api/ruleNode/{ruleNodeId}/debugIn`.
 */

    public Optional<JsonNode> getLatestRuleNodeDebugInput(RuleNodeId ruleNodeId) {
        try {
            ResponseEntity<JsonNode> jsonNode = restTemplate.getForEntity(baseURL + "/api/ruleNode/{ruleNodeId}/debugIn", JsonNode.class, ruleNodeId.getId());
            return Optional.ofNullable(jsonNode.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: test script. Calls `/api/ruleChain/testScript`.
 */

    public Optional<JsonNode> testScript(JsonNode inputParams) {
        try {
            ResponseEntity<JsonNode> jsonNode = restTemplate.postForEntity(baseURL + "/api/ruleChain/testScript", inputParams, JsonNode.class);
            return Optional.ofNullable(jsonNode.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: export rule chains. Calls `/api/ruleChains/export?limit=`.
 */

    public RuleChainData exportRuleChains(int limit) {
        return restTemplate.getForEntity(baseURL + "/api/ruleChains/export?limit=" + limit, RuleChainData.class).getBody();
    }

    /**
 * REST call: import rule chains. Calls `/api/ruleChains/import?overwrite=`.
 */

    public void importRuleChains(RuleChainData ruleChainData, boolean overwrite) {
        restTemplate.postForLocation(baseURL + "/api/ruleChains/import?overwrite=" + overwrite, ruleChainData);
    }

    /**
 * GET attribute keys. Calls `/api/plugins/telemetry/{entityType}/{entityId}/keys/attributes`.
 */

    public List<String> getAttributeKeys(EntityId entityId) {
        return restTemplate.exchange(
                baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/keys/attributes",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<String>>() {
                },
                entityId.getEntityType().name(),
                entityId.getId().toString()).getBody();
    }

    /**
 * GET attribute keys by scope. Calls `/api/plugins/telemetry/{entityType}/{entityId}/keys/attributes/{scope}`.
 */

    public List<String> getAttributeKeysByScope(EntityId entityId, String scope) {
        return restTemplate.exchange(
                baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/keys/attributes/{scope}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<String>>() {
                },
                entityId.getEntityType().name(),
                entityId.getId().toString(),
                scope).getBody();
    }

    /**
 * GET attribute kv entries. Calls `/api/plugins/telemetry/{entityType}/{entityId}/values/attributes?keys={keys}`.
 */

    public List<AttributeKvEntry> getAttributeKvEntries(EntityId entityId, List<String> keys) {
        List<JsonNode> attributes = restTemplate.exchange(
                baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/values/attributes?keys={keys}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<JsonNode>>() {
                },
                entityId.getEntityType().name(),
                entityId.getId(),
                listToString(keys)).getBody();

        return RestJsonConverter.toAttributes(attributes);
    }

    /**
 * GET attribute kv entries async.
 */

    public Future<List<AttributeKvEntry>> getAttributeKvEntriesAsync(EntityId entityId, List<String> keys) {
        return getExecutor().submit(() -> getAttributeKvEntries(entityId, keys));
    }

    /**
 * GET attributes by scope. Calls `/api/plugins/telemetry/{entityType}/{entityId}/values/attributes/{scope}?keys={keys}`.
 */

    public List<AttributeKvEntry> getAttributesByScope(EntityId entityId, String scope, List<String> keys) {
        List<JsonNode> attributes = restTemplate.exchange(
                baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/values/attributes/{scope}?keys={keys}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<JsonNode>>() {
                },
                entityId.getEntityType().name(),
                entityId.getId().toString(),
                scope,
                listToString(keys)).getBody();

        return RestJsonConverter.toAttributes(attributes);
    }

    /**
 * GET timeseries keys. Calls `/api/plugins/telemetry/{entityType}/{entityId}/keys/timeseries`.
 */

    public List<String> getTimeseriesKeys(EntityId entityId) {
        return restTemplate.exchange(
                baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/keys/timeseries",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<String>>() {
                },
                entityId.getEntityType().name(),
                entityId.getId().toString()).getBody();
    }

    /**
 * GET latest timeseries.
 */

    public List<TsKvEntry> getLatestTimeseries(EntityId entityId, List<String> keys) {
        return getLatestTimeseries(entityId, keys, true);
    }

    /**
 * GET latest timeseries. Calls `/api/plugins/telemetry/{entityType}/{entityId}/values/timeseries?keys={keys}&useStrictDataTypes={useStrictDataTypes}`.
 */

    public List<TsKvEntry> getLatestTimeseries(EntityId entityId, List<String> keys, boolean useStrictDataTypes) {
        Map<String, List<JsonNode>> timeseries = restTemplate.exchange(
                baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/values/timeseries?keys={keys}&useStrictDataTypes={useStrictDataTypes}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Map<String, List<JsonNode>>>() {
                },
                entityId.getEntityType().name(),
                entityId.getId().toString(),
                listToString(keys),
                useStrictDataTypes).getBody();

        return RestJsonConverter.toTimeseries(timeseries);
    }

    @Deprecated
    /**
 * GET timeseries.
 */
    public List<TsKvEntry> getTimeseries(EntityId entityId, List<String> keys, Long interval, Aggregation agg, TimePageLink pageLink) {
        return getTimeseries(entityId, keys, interval, agg, pageLink, true);
    }

    @Deprecated
    /**
 * GET timeseries.
 */
    public List<TsKvEntry> getTimeseries(EntityId entityId, List<String> keys, Long interval, Aggregation agg, TimePageLink pageLink, boolean useStrictDataTypes) {
        SortOrder sortOrder = pageLink.getSortOrder();
        return getTimeseries(entityId, keys, interval, agg, sortOrder != null ? sortOrder.getDirection() : null, pageLink.getStartTime(), pageLink.getEndTime(), 100, useStrictDataTypes);
    }

    @Deprecated
    /**
 * GET timeseries.
 */
    public List<TsKvEntry> getTimeseries(EntityId entityId, List<String> keys, Long interval, Aggregation agg, SortOrder.Direction sortOrder, Long startTime, Long endTime, Integer limit, boolean useStrictDataTypes) {
        return getTimeseries(entityId, keys, interval, null, null, agg, sortOrder, startTime, endTime, limit, useStrictDataTypes);
    }

    /**
 * GET timeseries.
 */

    public List<TsKvEntry> getTimeseries(EntityId entityId, List<String> keys, Long interval, IntervalType intervalType, String timeZone, Aggregation agg, SortOrder.Direction sortOrder, Long startTime, Long endTime, Integer limit, boolean useStrictDataTypes) {
        Map<String, String> params = new HashMap<>();
        params.put("entityType", entityId.getEntityType().name());
        params.put("entityId", entityId.getId().toString());
        params.put("keys", listToString(keys));
        params.put("interval", interval == null ? "0" : interval.toString());
        params.put("agg", agg == null ? "NONE" : agg.name());
        params.put("limit", limit != null ? limit.toString() : "100");
        params.put("orderBy", sortOrder != null ? sortOrder.name() : "DESC");
        params.put("useStrictDataTypes", Boolean.toString(useStrictDataTypes));

        StringBuilder urlBuilder = new StringBuilder(baseURL);
        urlBuilder.append("/api/plugins/telemetry/{entityType}/{entityId}/values/timeseries?keys={keys}&interval={interval}&limit={limit}&agg={agg}&useStrictDataTypes={useStrictDataTypes}&orderBy={orderBy}");

        if (intervalType != null) {
            urlBuilder.append("&intervalType={intervalType}");
            params.put("intervalType", intervalType.name());
        }

        if (timeZone != null) {
            urlBuilder.append("&timeZone={timeZone}");
            params.put("timeZone", timeZone);
        }

        if (startTime != null) {
            urlBuilder.append("&startTs={startTs}");
            params.put("startTs", String.valueOf(startTime));
        }
        if (endTime != null) {
            urlBuilder.append("&endTs={endTs}");
            params.put("endTs", String.valueOf(endTime));
        }

        Map<String, List<JsonNode>> timeseries = restTemplate.exchange(
                urlBuilder.toString(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Map<String, List<JsonNode>>>() {
                },
                params).getBody();

        return RestJsonConverter.toTimeseries(timeseries);
    }

    /**
 * POST create or update device attributes. Calls `/api/plugins/telemetry/{deviceId}/{scope}`.
 */

    public boolean saveDeviceAttributes(DeviceId deviceId, String scope, JsonNode request) {
        return restTemplate
                .postForEntity(baseURL + "/api/plugins/telemetry/{deviceId}/{scope}", request, Object.class, deviceId.getId().toString(), scope)
                .getStatusCode()
                .is2xxSuccessful();
    }

    /**
 * POST create or update entity attributes v1. Calls `/api/plugins/telemetry/{entityType}/{entityId}/{scope}`.
 */

    public boolean saveEntityAttributesV1(EntityId entityId, String scope, JsonNode request) {
        return restTemplate
                .postForEntity(
                        baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/{scope}",
                        request,
                        Object.class,
                        entityId.getEntityType().name(),
                        entityId.getId().toString(),
                        scope)
                .getStatusCode()
                .is2xxSuccessful();
    }

    /**
 * POST create or update entity attributes v2. Calls `/api/plugins/telemetry/{entityType}/{entityId}/attributes/{scope}`.
 */

    public boolean saveEntityAttributesV2(EntityId entityId, String scope, JsonNode request) {
        return restTemplate
                .postForEntity(
                        baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/attributes/{scope}",
                        request,
                        Object.class,
                        entityId.getEntityType().name(),
                        entityId.getId().toString(),
                        scope)
                .getStatusCode()
                .is2xxSuccessful();
    }

    /**
 * POST create or update entity telemetry. Calls `/api/plugins/telemetry/{entityType}/{entityId}/timeseries/{scope}`.
 */

    public boolean saveEntityTelemetry(EntityId entityId, String scope, JsonNode request) {
        return restTemplate
                .postForEntity(
                        baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/timeseries/{scope}",
                        request,
                        Object.class,
                        entityId.getEntityType().name(),
                        entityId.getId().toString(),
                        scope)
                .getStatusCode()
                .is2xxSuccessful();
    }

    /**
 * POST create or update entity telemetry with ttl. Calls `/api/plugins/telemetry/{entityType}/{entityId}/timeseries/{scope}/{ttl}`.
 */

    public boolean saveEntityTelemetryWithTTL(EntityId entityId, String scope, Long ttl, JsonNode request) {
        return restTemplate
                .postForEntity(
                        baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/timeseries/{scope}/{ttl}",
                        request,
                        Object.class,
                        entityId.getEntityType().name(),
                        entityId.getId().toString(),
                        scope,
                        ttl)
                .getStatusCode()
                .is2xxSuccessful();
    }

    /**
 * DELETE entity timeseries.
 */

    public boolean deleteEntityTimeseries(EntityId entityId,
                                          List<String> keys,
                                          boolean deleteAllDataForKeys,
                                          Long startTs,
                                          Long endTs,
                                          boolean rewriteLatestIfDeleted,
                                          boolean deleteLatest) {
        Map<String, String> params = new HashMap<>();
        params.put("entityType", entityId.getEntityType().name());
        params.put("entityId", entityId.getId().toString());
        params.put("keys", listToString(keys));
        params.put("deleteAllDataForKeys", String.valueOf(deleteAllDataForKeys));
        params.put("startTs", startTs.toString());
        params.put("endTs", endTs.toString());
        params.put("rewriteLatestIfDeleted", String.valueOf(rewriteLatestIfDeleted));
        params.put("deleteLatest", String.valueOf(deleteLatest));

        return restTemplate
                .exchange(
                        baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/timeseries/delete?keys={keys}&deleteAllDataForKeys={deleteAllDataForKeys}&startTs={startTs}&endTs={endTs}&rewriteLatestIfDeleted={rewriteLatestIfDeleted}&deleteLatest={deleteLatest}",
                        HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        Object.class,
                        params)
                .getStatusCode()
                .is2xxSuccessful();
    }

    /**
 * DELETE entity latest timeseries. Calls `/api/plugins/telemetry/{entityType}/{entityId}/timeseries/latest/delete?keys={keys}`.
 */

    public boolean deleteEntityLatestTimeseries(EntityId entityId, List<String> keys) {
        Map<String, String> params = new HashMap<>();
        params.put("entityType", entityId.getEntityType().name());
        params.put("entityId", entityId.getId().toString());
        params.put("keys", listToString(keys));

        return restTemplate
                .exchange(
                        baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/timeseries/latest/delete?keys={keys}",
                        HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        Object.class,
                        params)
                .getStatusCode()
                .is2xxSuccessful();
    }

    /**
 * DELETE entity attributes. Calls `/api/plugins/telemetry/{deviceId}/{scope}?keys={keys}`.
 */

    public boolean deleteEntityAttributes(DeviceId deviceId, String scope, List<String> keys) {
        return restTemplate
                .exchange(
                        baseURL + "/api/plugins/telemetry/{deviceId}/{scope}?keys={keys}",
                        HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        Object.class,
                        deviceId.getId().toString(),
                        scope,
                        listToString(keys))
                .getStatusCode()
                .is2xxSuccessful();
    }

    /**
 * DELETE entity attributes. Calls `/api/plugins/telemetry/{entityType}/{entityId}/{scope}?keys={keys}`.
 */

    public boolean deleteEntityAttributes(EntityId entityId, String scope, List<String> keys) {
        return restTemplate
                .exchange(
                        baseURL + "/api/plugins/telemetry/{entityType}/{entityId}/{scope}?keys={keys}",
                        HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        Object.class,
                        entityId.getEntityType().name(),
                        entityId.getId().toString(),
                        scope,
                        listToString(keys))
                .getStatusCode()
                .is2xxSuccessful();

    }

    /**
 * GET entity by id. Calls `/api/tenant/{tenantId}`.
 */

    public Optional<Tenant> getTenantById(TenantId tenantId) {
        try {
            ResponseEntity<Tenant> tenant = restTemplate.getForEntity(baseURL + "/api/tenant/{tenantId}", Tenant.class, tenantId.getId());
            return Optional.ofNullable(tenant.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/tenant/info/{tenantId}`.
 */

    public Optional<TenantInfo> getTenantInfoById(TenantId tenantId) {
        try {
            ResponseEntity<TenantInfo> tenant = restTemplate.getForEntity(baseURL + "/api/tenant/info/{tenantId}", TenantInfo.class, tenantId);
            return Optional.ofNullable(tenant.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update tenant. Calls `/api/tenant`.
 */

    public Tenant saveTenant(Tenant tenant) {
        return restTemplate.postForEntity(baseURL + "/api/tenant", tenant, Tenant.class).getBody();
    }

    /**
 * DELETE tenant. Calls `/api/tenant/{tenantId}`.
 */

    public void deleteTenant(TenantId tenantId) {
        restTemplate.delete(baseURL + "/api/tenant/{tenantId}", tenantId.getId());
    }

    /**
 * GET tenants. Calls `/api/tenants?`.
 */

    public PageData<Tenant> getTenants(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenants?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Tenant>>() {
                }, params).getBody();
    }

    /**
 * GET tenant infos. Calls `/api/tenantInfos?`.
 */

    public PageData<TenantInfo> getTenantInfos(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenantInfos?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<TenantInfo>>() {
                }, params).getBody();
    }

    /**
 * GET usage info. Calls `/api/usage`.
 */

    public UsageInfo getUsageInfo() {
        return restTemplate.exchange(
                baseURL + "/api/usage",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                UsageInfo.class).getBody();
    }

    /**
 * GET entity by id. Calls `/api/tenantProfile/{tenantProfileId}`.
 */

    public Optional<TenantProfile> getTenantProfileById(TenantProfileId tenantProfileId) {
        try {
            ResponseEntity<TenantProfile> tenantProfile = restTemplate.getForEntity(baseURL + "/api/tenantProfile/{tenantProfileId}", TenantProfile.class, tenantProfileId);
            return Optional.ofNullable(tenantProfile.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/tenantProfileInfo/{tenantProfileId}`.
 */

    public Optional<EntityInfo> getTenantProfileInfoById(TenantProfileId tenantProfileId) {
        try {
            ResponseEntity<EntityInfo> entityInfo = restTemplate.getForEntity(baseURL + "/api/tenantProfileInfo/{tenantProfileId}", EntityInfo.class, tenantProfileId);
            return Optional.ofNullable(entityInfo.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET default tenant profile info. Calls `/api/tenantProfileInfo/default`.
 */

    public EntityInfo getDefaultTenantProfileInfo() {
        return restTemplate.getForEntity(baseURL + "/api/tenantProfileInfo/default", EntityInfo.class).getBody();
    }

    /**
 * POST create or update tenant profile. Calls `/api/tenantProfile`.
 */

    public TenantProfile saveTenantProfile(TenantProfile tenantProfile) {
        return restTemplate.postForEntity(baseURL + "/api/tenantProfile", tenantProfile, TenantProfile.class).getBody();
    }

    /**
 * DELETE tenant profile. Calls `/api/tenantProfile/{tenantProfileId}`.
 */

    public void deleteTenantProfile(TenantProfileId tenantProfileId) {
        restTemplate.delete(baseURL + "/api/tenantProfile/{tenantProfileId}", tenantProfileId);
    }

    /**
 * REST call: set default tenant profile. Calls `/api/tenantProfile/{tenantProfileId}/default`.
 */

    public TenantProfile setDefaultTenantProfile(TenantProfileId tenantProfileId) {
        return restTemplate.exchange(baseURL + "/api/tenantProfile/{tenantProfileId}/default", HttpMethod.POST, HttpEntity.EMPTY, TenantProfile.class, tenantProfileId).getBody();
    }

    /**
 * GET tenant profiles. Calls `/api/tenantProfiles?`.
 */

    public PageData<TenantProfile> getTenantProfiles(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenantProfiles?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<TenantProfile>>() {
                }, params).getBody();
    }

    /**
 * GET tenant profile infos. Calls `/api/tenantProfileInfos?`.
 */

    public PageData<EntityInfo> getTenantProfileInfos(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenantProfileInfos?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EntityInfo>>() {
                }, params).getBody();
    }

    /**
 * GET entity by id. Calls `/api/user/{userId}`.
 */

    public Optional<User> getUserById(UserId userId) {
        try {
            ResponseEntity<User> user = restTemplate.getForEntity(baseURL + "/api/user/{userId}", User.class, userId.getId());
            return Optional.ofNullable(user.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: is user token access enabled. Calls `/api/user/tokenAccessEnabled`.
 */

    public Boolean isUserTokenAccessEnabled() {
        return restTemplate.getForEntity(baseURL + "/api/user/tokenAccessEnabled", Boolean.class).getBody();
    }

    /**
 * GET user token. Calls `/api/user/{userId}/token`.
 */

    public Optional<JsonNode> getUserToken(UserId userId) {
        try {
            ResponseEntity<JsonNode> userToken = restTemplate.getForEntity(baseURL + "/api/user/{userId}/token", JsonNode.class, userId.getId());
            return Optional.ofNullable(userToken.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update user. Calls `/api/user?sendActivationMail={sendActivationMail}`.
 */

    public User saveUser(User user, boolean sendActivationMail) {
        return restTemplate.postForEntity(baseURL + "/api/user?sendActivationMail={sendActivationMail}", user, User.class, sendActivationMail).getBody();
    }

    /**
 * REST call: send activation email. Calls `/api/user/sendActivationMail?email={email}`.
 */

    public void sendActivationEmail(String email) {
        restTemplate.postForLocation(baseURL + "/api/user/sendActivationMail?email={email}", null, email);
    }

    /**
 * GET activation link. Calls `/api/user/{userId}/activationLink`.
 */

    public String getActivationLink(UserId userId) {
        return restTemplate.getForEntity(baseURL + "/api/user/{userId}/activationLink", String.class, userId.getId()).getBody();
    }

    /**
 * DELETE user. Calls `/api/user/{userId}`.
 */

    public void deleteUser(UserId userId) {
        restTemplate.delete(baseURL + "/api/user/{userId}", userId.getId());
    }

    /**
 * GET users. Calls `/api/users?`.
 */

    public PageData<User> getUsers(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/users?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<User>>() {
                }, params).getBody();
    }

    /**
 * GET users by query. Calls `/api/users/info?`.
 */

    public PageData<UserEmailInfo> getUsersByQuery(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/users/info?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<UserEmailInfo>>() {
                }, params).getBody();
    }

    /**
 * GET tenant admins. Calls `/api/tenant/{tenantId}/users?`.
 */

    public PageData<User> getTenantAdmins(TenantId tenantId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("tenantId", tenantId.getId().toString());
        addPageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + "/api/tenant/{tenantId}/users?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<User>>() {
                }, params).getBody();
    }

    /**
 * GET customer users. Calls `/api/customer/{customerId}/users?`.
 */

    public PageData<User> getCustomerUsers(CustomerId customerId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.getId().toString());
        addPageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + "/api/customer/{customerId}/users?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<User>>() {
                }, params).getBody();
    }

    /**
 * GET users for assign.
 */

    public PageData<UserEmailInfo> getUsersForAssign(AlarmId alarmId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("alarmId", alarmId.getId().toString());
        addPageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + "/users/assign/{alarmId}" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<UserEmailInfo>>() {
                }, params).getBody();
    }

    /**
 * REST call: set user credentials enabled. Calls `/api/user/{userId}/userCredentialsEnabled?userCredentialsEnabled={userCredentialsEnabled}`.
 */

    public void setUserCredentialsEnabled(UserId userId, boolean userCredentialsEnabled) {
        restTemplate.postForLocation(
                baseURL + "/api/user/{userId}/userCredentialsEnabled?userCredentialsEnabled={userCredentialsEnabled}",
                null,
                userId.getId(),
                userCredentialsEnabled);
    }

    /**
 * POST create or update api key. Calls `/api/apiKey`.
 */

    public ApiKey saveApiKey(ApiKeyInfo apiKeyInfo) {
        return restTemplate.postForEntity(baseURL + "/api/apiKey", apiKeyInfo, ApiKey.class).getBody();
    }

    /**
 * GET user api keys. Calls `/api/apiKeys/{userId}?`.
 */

    public PageData<ApiKeyInfo> getUserApiKeys(UserId userId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId.getId().toString());
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/apiKeys/{userId}?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<ApiKeyInfo>>() {}, params).getBody();
    }

    /**
 * REST call: update api key description. Calls `/api/apiKey/{id}/description`.
 */

    public ApiKeyInfo updateApiKeyDescription(ApiKeyId apiKeyId, String description) {
        return restTemplate.exchange(
                baseURL + "/api/apiKey/{id}/description",
                HttpMethod.PUT,
                new HttpEntity<>(description),
                ApiKeyInfo.class,
                apiKeyId.getId()).getBody();
    }

    /**
 * REST call: enable api key. Calls `/api/apiKey/{id}/enabled/{enabledValue}`.
 */

    public ApiKeyInfo enableApiKey(ApiKeyId apiKeyId, boolean enabled) {
        return restTemplate.exchange(
                baseURL + "/api/apiKey/{id}/enabled/{enabledValue}",
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                ApiKeyInfo.class,
                apiKeyId.getId(),
                enabled).getBody();
    }

    /**
 * DELETE api key. Calls `/api/apiKey/{id}`.
 */

    public void deleteApiKey(ApiKeyId apiKeyId) {
        restTemplate.delete(baseURL + "/api/apiKey/{id}", apiKeyId.getId());
    }

    /**
 * GET entity by id. Calls `/api/widgetsBundle/{widgetsBundleId}`.
 */

    public Optional<WidgetsBundle> getWidgetsBundleById(WidgetsBundleId widgetsBundleId) {
        try {
            ResponseEntity<WidgetsBundle> widgetsBundle =
                    restTemplate.getForEntity(baseURL + "/api/widgetsBundle/{widgetsBundleId}", WidgetsBundle.class, widgetsBundleId.getId());
            return Optional.ofNullable(widgetsBundle.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update widgets bundle. Calls `/api/widgetsBundle`.
 */

    public WidgetsBundle saveWidgetsBundle(WidgetsBundle widgetsBundle) {
        return restTemplate.postForEntity(baseURL + "/api/widgetsBundle", widgetsBundle, WidgetsBundle.class).getBody();
    }

    /**
 * REST call: update widgets bundle widget types. Calls `/api/widgetsBundle/{widgetsBundleId}/widgetTypes`.
 */

    public void updateWidgetsBundleWidgetTypes(WidgetsBundleId widgetsBundleId, List<WidgetTypeId> widgetTypeIds) {
        var httpEntity = new HttpEntity<>(widgetTypeIds.stream()
                .map(widgetTypeId -> widgetTypeId.getId().toString())
                .collect(Collectors.toList()));
        restTemplate.exchange(baseURL + "/api/widgetsBundle/{widgetsBundleId}/widgetTypes",
                HttpMethod.POST, httpEntity, Void.class, widgetsBundleId.getId());
    }

    /**
 * REST call: update widgets bundle widget fqns. Calls `/api/widgetsBundle/{widgetsBundleId}/widgetTypeFqns`.
 */

    public void updateWidgetsBundleWidgetFqns(WidgetsBundleId widgetsBundleId, List<String> widgetTypeFqns) {
        restTemplate.exchange(baseURL + "/api/widgetsBundle/{widgetsBundleId}/widgetTypeFqns",
                HttpMethod.POST, new HttpEntity<>(widgetTypeFqns), Void.class, widgetsBundleId.getId());
    }

    /**
 * DELETE widgets bundle. Calls `/api/widgetsBundle/{widgetsBundleId}`.
 */

    public void deleteWidgetsBundle(WidgetsBundleId widgetsBundleId) {
        restTemplate.delete(baseURL + "/api/widgetsBundle/{widgetsBundleId}", widgetsBundleId.getId());
    }

    /**
 * GET widgets bundles.
 */

    public PageData<WidgetsBundle> getWidgetsBundles(PageLink pageLink) {
        return getWidgetsBundles(pageLink, null, null);
    }

    /**
 * GET widgets bundles. Calls `/api/widgetsBundles?`.
 */

    public PageData<WidgetsBundle> getWidgetsBundles(PageLink pageLink, Boolean tenantOnly, Boolean fullSearch) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        addTenantOnlyAndFullSearchToParams(tenantOnly, fullSearch, params);
        return restTemplate.exchange(
                baseURL + "/api/widgetsBundles?" + getUrlParams(pageLink) + getTenantOnlyAndFullSearchUrlParams(tenantOnly, fullSearch),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<WidgetsBundle>>() {
                }, params).getBody();
    }

    /**
 * GET widgets bundles. Calls `/api/widgetsBundles`.
 */

    public List<WidgetsBundle> getWidgetsBundles() {
        return restTemplate.exchange(
                baseURL + "/api/widgetsBundles",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<WidgetsBundle>>() {
                }).getBody();
    }

    /**
 * GET entity by id. Calls `/api/widgetType/{widgetTypeId}`.
 */

    public Optional<WidgetTypeDetails> getWidgetTypeById(WidgetTypeId widgetTypeId) {
        try {
            ResponseEntity<WidgetTypeDetails> widgetTypeDetails =
                    restTemplate.getForEntity(baseURL + "/api/widgetType/{widgetTypeId}", WidgetTypeDetails.class, widgetTypeId.getId());
            return Optional.ofNullable(widgetTypeDetails.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/widgetTypeInfo/{widgetTypeId}`.
 */

    public Optional<WidgetTypeInfo> getWidgetTypeInfoById(WidgetTypeId widgetTypeId) {
        try {
            ResponseEntity<WidgetTypeInfo> widgetTypeInfo =
                    restTemplate.getForEntity(baseURL + "/api/widgetTypeInfo/{widgetTypeId}", WidgetTypeInfo.class, widgetTypeId.getId());
            return Optional.ofNullable(widgetTypeInfo.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw exception;
        }
    }

    /**
 * POST create or update widget type.
 */

    public WidgetTypeDetails saveWidgetType(WidgetTypeDetails widgetTypeDetails) {
        return saveWidgetType(widgetTypeDetails, null);
    }

    /**
 * POST create or update widget type. Calls `/api/widgetType`.
 */

    public WidgetTypeDetails saveWidgetType(WidgetTypeDetails widgetTypeDetails, Boolean updateExistingByFqn) {
        if (updateExistingByFqn == null) {
            return restTemplate.postForEntity(baseURL + "/api/widgetType", widgetTypeDetails, WidgetTypeDetails.class).getBody();
        }
        return restTemplate.postForEntity(baseURL + "/api/widgetType?updateExistingByFqn={updateExistingByFqn}", widgetTypeDetails, WidgetTypeDetails.class, updateExistingByFqn).getBody();
    }

    /**
 * DELETE widget type. Calls `/api/widgetType/{widgetTypeId}`.
 */

    public void deleteWidgetType(WidgetTypeId widgetTypeId) {
        restTemplate.delete(baseURL + "/api/widgetType/{widgetTypeId}", widgetTypeId.getId());
    }

    /**
 * GET widget types.
 */

    public PageData<WidgetTypeInfo> getWidgetTypes(PageLink pageLink) {
        return getWidgetTypes(pageLink, null, null, null, null);
    }

    /**
 * GET widget types. Calls `/api/widgetTypes?`.
 */

    public PageData<WidgetTypeInfo> getWidgetTypes(PageLink pageLink, Boolean tenantOnly, Boolean fullSearch,
                                                   DeprecatedFilter deprecatedFilter, List<String> widgetTypeList) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        addWidgetInfoFiltersToParams(tenantOnly, fullSearch, deprecatedFilter, widgetTypeList, params);
        return restTemplate.exchange(
                baseURL + "/api/widgetTypes?" + getUrlParams(pageLink) +
                        getWidgetTypeInfoPageRequestUrlParams(tenantOnly, fullSearch, deprecatedFilter, widgetTypeList),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<WidgetTypeInfo>>() {
                },
                params).getBody();
    }

    @Deprecated // current name in the controller: getBundleWidgetTypesByBundleAlias
    /**
 * GET bundle widget types. Calls `/api/widgetTypes?isSystem={isSystem}&bundleAlias={bundleAlias}`.
 */
    public List<WidgetType> getBundleWidgetTypes(boolean isSystem, String bundleAlias) {
        return restTemplate.exchange(
                baseURL + "/api/widgetTypes?isSystem={isSystem}&bundleAlias={bundleAlias}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<WidgetType>>() {
                },
                isSystem,
                bundleAlias).getBody();
    }

    /**
 * GET bundle widget types. Calls `/api/widgetTypes?widgetsBundleId={widgetsBundleId}`.
 */

    public List<WidgetType> getBundleWidgetTypes(WidgetsBundleId widgetsBundleId) {
        return restTemplate.exchange(
                baseURL + "/api/widgetTypes?widgetsBundleId={widgetsBundleId}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<WidgetType>>() {
                },
                widgetsBundleId.getId()).getBody();
    }

    @Deprecated // current name in the controller: getBundleWidgetTypesDetailsByBundleAlias
    /**
 * GET bundle widget types details. Calls `/api/widgetTypesDetails?isSystem={isSystem}&bundleAlias={bundleAlias}`.
 */
    public List<WidgetTypeDetails> getBundleWidgetTypesDetails(boolean isSystem, String bundleAlias) {
        return restTemplate.exchange(
                baseURL + "/api/widgetTypesDetails?isSystem={isSystem}&bundleAlias={bundleAlias}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<WidgetTypeDetails>>() {
                },
                isSystem,
                bundleAlias).getBody();
    }

    /**
 * GET bundle widget types details. Calls `/api/widgetTypesDetails?widgetsBundleId={widgetsBundleId}&inlineImages={inlineImages}`.
 */

    public List<WidgetTypeDetails> getBundleWidgetTypesDetails(WidgetsBundleId widgetsBundleId, boolean inlineImages) {
        return restTemplate.exchange(
                baseURL + "/api/widgetTypesDetails?widgetsBundleId={widgetsBundleId}&inlineImages={inlineImages}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<WidgetTypeDetails>>() {
                },
                widgetsBundleId.getId(),
                inlineImages).getBody();
    }

    /**
 * GET bundle widget type fqns. Calls `/api/widgetTypeFqns?widgetsBundleId={widgetsBundleId}`.
 */

    public List<String> getBundleWidgetTypeFqns(WidgetsBundleId widgetsBundleId) {
        return restTemplate.exchange(
                baseURL + "/api/widgetTypeFqns?widgetsBundleId={widgetsBundleId}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<String>>() {
                },
                widgetsBundleId.getId()).getBody();
    }

    @Deprecated // current name in the controller: getBundleWidgetTypesInfosByBundleAlias
    /**
 * GET bundle widget types infos. Calls `/api/widgetTypesInfos?isSystem={isSystem}&bundleAlias={bundleAlias}`.
 */
    public List<WidgetTypeInfo> getBundleWidgetTypesInfos(boolean isSystem, String bundleAlias) {
        return restTemplate.exchange(
                baseURL + "/api/widgetTypesInfos?isSystem={isSystem}&bundleAlias={bundleAlias}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<WidgetTypeInfo>>() {
                },
                isSystem,
                bundleAlias).getBody();
    }

    /**
 * GET bundle widget types infos.
 */

    public PageData<WidgetTypeInfo> getBundleWidgetTypesInfos(WidgetsBundleId widgetsBundleId, PageLink pageLink) {
        return getBundleWidgetTypesInfos(widgetsBundleId, pageLink, null, null, null, null);
    }

    /**
 * GET bundle widget types infos. Calls `/api/widgetTypesInfos?widgetsBundleId={widgetsBundleId}&`.
 */

    public PageData<WidgetTypeInfo> getBundleWidgetTypesInfos(WidgetsBundleId widgetsBundleId, PageLink pageLink,
                                                              Boolean tenantOnly, Boolean fullSearch,
                                                              DeprecatedFilter deprecatedFilter, List<String> widgetTypeList) {
        Map<String, String> params = new HashMap<>();
        params.put("widgetsBundleId", widgetsBundleId.getId().toString());
        addPageLinkToParam(params, pageLink);
        addWidgetInfoFiltersToParams(tenantOnly, fullSearch, deprecatedFilter, widgetTypeList, params);
        return restTemplate.exchange(
                baseURL + "/api/widgetTypesInfos?widgetsBundleId={widgetsBundleId}&" + getUrlParams(pageLink) +
                        getWidgetTypeInfoPageRequestUrlParams(tenantOnly, fullSearch, deprecatedFilter, widgetTypeList),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<WidgetTypeInfo>>() {
                },
                params).getBody();
    }

    @Deprecated // current name in the controller: getWidgetTypeByBundleAliasAndTypeAlias
    /**
 * GET widget type. Calls `/api/widgetType?isSystem={isSystem}&bundleAlias={bundleAlias}&alias={alias}`.
 */
    public Optional<WidgetType> getWidgetType(boolean isSystem, String bundleAlias, String alias) {
        try {
            ResponseEntity<WidgetType> widgetType =
                    restTemplate.getForEntity(
                            baseURL + "/api/widgetType?isSystem={isSystem}&bundleAlias={bundleAlias}&alias={alias}",
                            WidgetType.class,
                            isSystem,
                            bundleAlias,
                            alias);
            return Optional.ofNullable(widgetType.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET widget type. Calls `/api/widgetType?fqn={fqn}`.
 */

    public Optional<WidgetType> getWidgetType(String fqn) {
        try {
            ResponseEntity<WidgetType> widgetType =
                    restTemplate.getForEntity(
                            baseURL + "/api/widgetType?fqn={fqn}",
                            WidgetType.class,
                            fqn);
            return Optional.ofNullable(widgetType.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw exception;
        }
    }

    /**
 * REST call: is edges support enabled. Calls `/api/edges/enabled`.
 */

    public Boolean isEdgesSupportEnabled() {
        return restTemplate.getForEntity(baseURL + "/api/edges/enabled", Boolean.class).getBody();
    }

    /**
 * POST create or update edge. Calls `/api/edge`.
 */

    public Edge saveEdge(Edge edge) {
        return restTemplate.postForEntity(baseURL + "/api/edge", edge, Edge.class).getBody();
    }

    /**
 * DELETE edge. Calls `/api/edge/{edgeId}`.
 */

    public void deleteEdge(EdgeId edgeId) {
        restTemplate.delete(baseURL + "/api/edge/{edgeId}", edgeId.getId());
    }

    /**
 * GET entity by id. Calls `/api/edge/{edgeId}`.
 */

    public Optional<Edge> getEdgeById(EdgeId edgeId) {
        try {
            ResponseEntity<Edge> edge = restTemplate.getForEntity(baseURL + "/api/edge/{edgeId}", Edge.class, edgeId.getId());
            return Optional.ofNullable(edge.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET entity by id. Calls `/api/edge/info/{edgeId}`.
 */

    public Optional<EdgeInfo> getEdgeInfoById(EdgeId edgeId) {
        try {
            ResponseEntity<EdgeInfo> edge = restTemplate.getForEntity(baseURL + "/api/edge/info/{edgeId}", EdgeInfo.class, edgeId.getId());
            return Optional.ofNullable(edge.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST assign edge to customer. Calls `/api/customer/{customerId}/edge/{edgeId}`.
 */

    public Optional<Edge> assignEdgeToCustomer(CustomerId customerId, EdgeId edgeId) {
        try {
            ResponseEntity<Edge> edge = restTemplate.postForEntity(baseURL + "/api/customer/{customerId}/edge/{edgeId}", null, Edge.class, customerId.getId(), edgeId.getId());
            return Optional.ofNullable(edge.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST assign edge to public customer. Calls `/api/customer/public/edge/{edgeId}`.
 */

    public Optional<Edge> assignEdgeToPublicCustomer(EdgeId edgeId) {
        try {
            ResponseEntity<Edge> edge = restTemplate.postForEntity(baseURL + "/api/customer/public/edge/{edgeId}", null, Edge.class, edgeId.getId());
            return Optional.ofNullable(edge.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: set edge root rule chain. Calls `/api/edge/{edgeId}/{ruleChainId}/root`.
 */

    public Optional<Edge> setEdgeRootRuleChain(EdgeId edgeId, RuleChainId ruleChainId) {
        try {
            ResponseEntity<Edge> ruleChain = restTemplate.postForEntity(baseURL + "/api/edge/{edgeId}/{ruleChainId}/root", null, Edge.class, edgeId.getId(), ruleChainId.getId());
            return Optional.ofNullable(ruleChain.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET edges. Calls `/api/edges?`.
 */

    public PageData<Edge> getEdges(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/edges?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Edge>>() {
                }, params).getBody();
    }

    /**
 * DELETE unassign edge from customer. Calls `/api/customer/edge/{edgeId}`.
 */

    public Optional<Edge> unassignEdgeFromCustomer(EdgeId edgeId) {
        try {
            ResponseEntity<Edge> edge = restTemplate.exchange(baseURL + "/api/customer/edge/{edgeId}", HttpMethod.DELETE, HttpEntity.EMPTY, Edge.class, edgeId.getId());
            return Optional.ofNullable(edge.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST assign device to edge. Calls `/api/edge/{edgeId}/device/{deviceId}`.
 */

    public Optional<Device> assignDeviceToEdge(EdgeId edgeId, DeviceId deviceId) {
        try {
            ResponseEntity<Device> device = restTemplate.postForEntity(baseURL + "/api/edge/{edgeId}/device/{deviceId}", null, Device.class, edgeId.getId(), deviceId.getId());
            return Optional.ofNullable(device.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE unassign device from edge. Calls `/api/edge/{edgeId}/device/{deviceId}`.
 */

    public Optional<Device> unassignDeviceFromEdge(EdgeId edgeId, DeviceId deviceId) {
        try {
            ResponseEntity<Device> device = restTemplate.exchange(baseURL + "/api/edge/{edgeId}/device/{deviceId}", HttpMethod.DELETE, HttpEntity.EMPTY, Device.class, edgeId.getId(), deviceId.getId());
            return Optional.ofNullable(device.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET edge devices. Calls `/api/edge/{edgeId}/devices?`.
 */

    public PageData<Device> getEdgeDevices(EdgeId edgeId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("edgeId", edgeId.getId().toString());
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/edge/{edgeId}/devices?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Device>>() {
                }, params).getBody();
    }

    /**
 * POST assign asset to edge. Calls `/api/edge/{edgeId}/asset/{assetId}`.
 */

    public Optional<Asset> assignAssetToEdge(EdgeId edgeId, AssetId assetId) {
        try {
            ResponseEntity<Asset> asset = restTemplate.postForEntity(baseURL + "/api/edge/{edgeId}/asset/{assetId}", null, Asset.class, edgeId.getId(), assetId.getId());
            return Optional.ofNullable(asset.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE unassign asset from edge. Calls `/api/edge/{edgeId}/asset/{assetId}`.
 */

    public Optional<Asset> unassignAssetFromEdge(EdgeId edgeId, AssetId assetId) {
        try {
            ResponseEntity<Asset> asset = restTemplate.exchange(baseURL + "/api/edge/{edgeId}/asset/{assetId}", HttpMethod.DELETE, HttpEntity.EMPTY, Asset.class, edgeId.getId(), assetId.getId());
            return Optional.ofNullable(asset.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET edge assets. Calls `/api/edge/{edgeId}/assets?`.
 */

    public PageData<Asset> getEdgeAssets(EdgeId edgeId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("edgeId", edgeId.getId().toString());
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/edge/{edgeId}/assets?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Asset>>() {
                }, params).getBody();
    }

    /**
 * POST assign dashboard to edge. Calls `/api/edge/{edgeId}/dashboard/{dashboardId}`.
 */

    public Optional<Dashboard> assignDashboardToEdge(EdgeId edgeId, DashboardId dashboardId) {
        try {
            ResponseEntity<Dashboard> dashboard = restTemplate.postForEntity(baseURL + "/api/edge/{edgeId}/dashboard/{dashboardId}", null, Dashboard.class, edgeId.getId(), dashboardId.getId());
            return Optional.ofNullable(dashboard.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE unassign dashboard from edge. Calls `/api/edge/{edgeId}/dashboard/{dashboardId}`.
 */

    public Optional<Dashboard> unassignDashboardFromEdge(EdgeId edgeId, DashboardId dashboardId) {
        try {
            ResponseEntity<Dashboard> dashboard = restTemplate.exchange(baseURL + "/api/edge/{edgeId}/dashboard/{dashboardId}", HttpMethod.DELETE, HttpEntity.EMPTY, Dashboard.class, edgeId.getId(), dashboardId.getId());
            return Optional.ofNullable(dashboard.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET edge dashboards. Calls `/api/edge/{edgeId}/dashboards?`.
 */

    public PageData<DashboardInfo> getEdgeDashboards(EdgeId edgeId, TimePageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("edgeId", edgeId.getId().toString());
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/edge/{edgeId}/dashboards?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<DashboardInfo>>() {
                }, params).getBody();
    }

    /**
 * POST assign entity view to edge. Calls `/api/edge/{edgeId}/entityView/{entityViewId}`.
 */

    public Optional<EntityView> assignEntityViewToEdge(EdgeId edgeId, EntityViewId entityViewId) {
        try {
            ResponseEntity<EntityView> entityView = restTemplate.postForEntity(baseURL + "/api/edge/{edgeId}/entityView/{entityViewId}", null, EntityView.class, edgeId.getId(), entityViewId.getId());
            return Optional.ofNullable(entityView.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE unassign entity view from edge. Calls `/api/edge/{edgeId}/entityView/{entityViewId}`.
 */

    public Optional<EntityView> unassignEntityViewFromEdge(EdgeId edgeId, EntityViewId entityViewId) {
        try {
            ResponseEntity<EntityView> entityView = restTemplate.exchange(baseURL + "/api/edge/{edgeId}/entityView/{entityViewId}",
                    HttpMethod.DELETE, HttpEntity.EMPTY, EntityView.class, edgeId.getId(), entityViewId.getId());
            return Optional.ofNullable(entityView.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET edge entity views. Calls `/api/edge/{edgeId}/entityViews?`.
 */

    public PageData<EntityView> getEdgeEntityViews(EdgeId edgeId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("edgeId", edgeId.getId().toString());
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/edge/{edgeId}/entityViews?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EntityView>>() {
                }, params).getBody();
    }

    /**
 * POST assign rule chain to edge. Calls `/api/edge/{edgeId}/ruleChain/{ruleChainId}`.
 */

    public Optional<RuleChain> assignRuleChainToEdge(EdgeId edgeId, RuleChainId ruleChainId) {
        try {
            ResponseEntity<RuleChain> ruleChain = restTemplate.postForEntity(baseURL + "/api/edge/{edgeId}/ruleChain/{ruleChainId}", null, RuleChain.class, edgeId.getId(), ruleChainId.getId());
            return Optional.ofNullable(ruleChain.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE unassign rule chain from edge. Calls `/api/edge/{edgeId}/ruleChain/{ruleChainId}`.
 */

    public Optional<RuleChain> unassignRuleChainFromEdge(EdgeId edgeId, RuleChainId ruleChainId) {
        try {
            ResponseEntity<RuleChain> ruleChain = restTemplate.exchange(baseURL + "/api/edge/{edgeId}/ruleChain/{ruleChainId}", HttpMethod.DELETE, HttpEntity.EMPTY, RuleChain.class, edgeId.getId(), ruleChainId.getId());
            return Optional.ofNullable(ruleChain.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET edge rule chains. Calls `/api/edge/{edgeId}/ruleChains?`.
 */

    public PageData<RuleChain> getEdgeRuleChains(EdgeId edgeId, TimePageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("edgeId", edgeId.getId().toString());
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/edge/{edgeId}/ruleChains?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<RuleChain>>() {
                }, params).getBody();
    }

    /**
 * REST call: set auto assign to edge rule chain. Calls `/api/ruleChain/{ruleChainId}/autoAssignToEdge`.
 */

    public Optional<RuleChain> setAutoAssignToEdgeRuleChain(RuleChainId ruleChainId) {
        try {
            ResponseEntity<RuleChain> ruleChain = restTemplate.postForEntity(baseURL + "/api/ruleChain/{ruleChainId}/autoAssignToEdge", null, RuleChain.class, ruleChainId.getId());
            return Optional.ofNullable(ruleChain.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: unset auto assign to edge rule chain. Calls `/api/ruleChain/{ruleChainId}/autoAssignToEdge`.
 */

    public Optional<RuleChain> unsetAutoAssignToEdgeRuleChain(RuleChainId ruleChainId) {
        try {
            ResponseEntity<RuleChain> ruleChain = restTemplate.exchange(baseURL + "/api/ruleChain/{ruleChainId}/autoAssignToEdge", HttpMethod.DELETE, HttpEntity.EMPTY, RuleChain.class, ruleChainId.getId());
            return Optional.ofNullable(ruleChain.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET auto assign to edge rule chains. Calls `/api/ruleChain/autoAssignToEdgeRuleChains`.
 */

    public List<RuleChain> getAutoAssignToEdgeRuleChains() {
        return restTemplate.exchange(baseURL + "/api/ruleChain/autoAssignToEdgeRuleChains",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<RuleChain>>() {
                }).getBody();
    }

    /**
 * REST call: set root edge template rule chain. Calls `/api/ruleChain/{ruleChainId}/edgeTemplateRoot`.
 */

    public Optional<RuleChain> setRootEdgeTemplateRuleChain(RuleChainId ruleChainId) {
        try {
            ResponseEntity<RuleChain> ruleChain = restTemplate.postForEntity(baseURL + "/api/ruleChain/{ruleChainId}/edgeTemplateRoot", null, RuleChain.class, ruleChainId.getId());
            return Optional.ofNullable(ruleChain.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET tenant edges. Calls `/api/tenant/edges?type={type}&`.
 */

    public PageData<Edge> getTenantEdges(String type, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenant/edges?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Edge>>() {
                }, params).getBody();
    }

    /**
 * GET tenant edge infos. Calls `/api/tenant/edgeInfos?type={type}&`.
 */

    public PageData<EdgeInfo> getTenantEdgeInfos(String type, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/tenant/edgeInfos?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EdgeInfo>>() {
                }, params).getBody();
    }

    /**
 * GET tenant edge. Calls `/api/tenant/edges?edgeName={edgeName}`.
 */

    public Optional<Edge> getTenantEdge(String edgeName) {
        try {
            ResponseEntity<Edge> edge = restTemplate.getForEntity(baseURL + "/api/tenant/edges?edgeName={edgeName}", Edge.class, edgeName);
            return Optional.ofNullable(edge.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET customer edges. Calls `/api/customer/{customerId}/edges?type={type}&`.
 */

    public PageData<Edge> getCustomerEdges(CustomerId customerId, PageLink pageLink, String edgeType) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.getId().toString());
        params.put("type", edgeType);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/customer/{customerId}/edges?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Edge>>() {
                }, params).getBody();
    }

    /**
 * GET customer edge infos. Calls `/api/customer/{customerId}/edgeInfos?type={type}&`.
 */

    public PageData<EdgeInfo> getCustomerEdgeInfos(CustomerId customerId, PageLink pageLink, String edgeType) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", customerId.getId().toString());
        params.put("type", edgeType);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/customer/{customerId}/edgeInfos?type={type}&" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EdgeInfo>>() {
                }, params).getBody();
    }

    /**
 * GET entity by id. Calls `/api/edges?edgeIds={edgeIds}`.
 */

    public List<Edge> getEdgesByIds(List<EdgeId> edgeIds) {
        return restTemplate.exchange(baseURL + "/api/edges?edgeIds={edgeIds}",
                HttpMethod.GET,
                HttpEntity.EMPTY, new ParameterizedTypeReference<List<Edge>>() {
                }, listIdsToString(edgeIds)).getBody();
    }

    /**
 * POST entity query or search: by query. Calls `/api/edges`.
 */

    public List<Edge> findByQuery(EdgeSearchQuery query) {
        return restTemplate.exchange(
                baseURL + "/api/edges",
                HttpMethod.POST,
                new HttpEntity<>(query),
                new ParameterizedTypeReference<List<Edge>>() {
                }).getBody();
    }

    /**
 * GET edge types. Calls `/api/edge/types`.
 */

    public List<EntitySubtype> getEdgeTypes() {
        return restTemplate.exchange(
                baseURL + "/api/edge/types",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntitySubtype>>() {
                }).getBody();
    }

    /**
 * GET edge events. Calls `/api/edge/{edgeId}/events?`.
 */

    public PageData<EdgeEvent> getEdgeEvents(EdgeId edgeId, TimePageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("edgeId", edgeId.toString());
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/edge/{edgeId}/events?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EdgeEvent>>() {
                },
                params).getBody();
    }

    /**
 * REST call: sync edge. Calls `/api/edge/sync/{edgeId}`.
 */

    public void syncEdge(EdgeId edgeId) {
        Map<String, String> params = new HashMap<>();
        params.put("edgeId", edgeId.toString());
        restTemplate.postForEntity(baseURL + "/api/edge/sync/{edgeId}", null, EdgeId.class, params);
    }

    /**
 * POST entity query or search: missing to related rule chains. Calls `/api/edge/missingToRelatedRuleChains/{edgeId}`.
 */

    public String findMissingToRelatedRuleChains(EdgeId edgeId) {
        return restTemplate.getForEntity(baseURL + "/api/edge/missingToRelatedRuleChains/{edgeId}", String.class, edgeId.getId()).getBody();
    }

    /**
 * REST call: process edges bulk import. Calls `/api/edge/bulk_import`.
 */

    public BulkImportResult<Edge> processEdgesBulkImport(BulkImportRequest request) {
        return restTemplate.exchange(
                baseURL + "/api/edge/bulk_import",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<BulkImportResult<Edge>>() {
                }).getBody();
    }

    /**
 * GET edge install instructions. Calls `/api/edge/instructions/install/{edgeId}/{method}`.
 */

    public Optional<EdgeInstructions> getEdgeInstallInstructions(EdgeId edgeId, String method) {
        ResponseEntity<EdgeInstructions> edgeInstallInstructionsResult =
                restTemplate.getForEntity(baseURL + "/api/edge/instructions/install/{edgeId}/{method}", EdgeInstructions.class, edgeId.getId(), method);
        return Optional.ofNullable(edgeInstallInstructionsResult.getBody());
    }

    /**
 * GET edge upgrade instructions. Calls `/api/edge/instructions/upgrade/{edgeVersion}/{method}`.
 */

    public Optional<EdgeInstructions> getEdgeUpgradeInstructions(String edgeVersion, String method) {
        ResponseEntity<EdgeInstructions> edgeUpgradeInstructionsResult =
                restTemplate.getForEntity(baseURL + "/api/edge/instructions/upgrade/{edgeVersion}/{method}", EdgeInstructions.class, edgeVersion, method);
        return Optional.ofNullable(edgeUpgradeInstructionsResult.getBody());
    }

    /**
 * POST create or update entities version. Calls `/api/entities/vc/version`.
 */

    public UUID saveEntitiesVersion(VersionCreateRequest request) {
        return restTemplate.postForEntity(baseURL + "/api/entities/vc/version", request, UUID.class).getBody();
    }

    /**
 * GET version create request status. Calls `/api/entities/vc/version/{requestId}/status`.
 */

    public Optional<VersionCreationResult> getVersionCreateRequestStatus(UUID requestId) {
        try {
            ResponseEntity<VersionCreationResult> versionCreateResult = restTemplate.getForEntity(baseURL + "/api/entities/vc/version/{requestId}/status", VersionCreationResult.class, requestId);
            return Optional.ofNullable(versionCreateResult.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: list entity versions. Calls `/api/entities/vc/version/{entityType}/{externalEntityUuid}?branch={branch}&`.
 */

    public PageData<EntityVersion> listEntityVersions(EntityId externalEntityId, String branch, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("entityType", externalEntityId.getEntityType().name());
        params.put("externalEntityUuid", externalEntityId.getId().toString());
        params.put("branch", branch);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/entities/vc/version/{entityType}/{externalEntityUuid}?branch={branch}&" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EntityVersion>>() {
                },
                params).getBody();
    }

    /**
 * REST call: list entity type versions. Calls `/api/entities/vc/version/{entityType}?branch={branch}&`.
 */

    public PageData<EntityVersion> listEntityTypeVersions(EntityType entityType, String branch, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("entityType", entityType.name());
        params.put("branch", branch);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/entities/vc/version/{entityType}?branch={branch}&" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EntityVersion>>() {
                },
                params).getBody();
    }

    /**
 * REST call: list versions. Calls `/api/entities/vc/version?branch={branch}&`.
 */

    public PageData<EntityVersion> listVersions(String branch, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("branch", branch);
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/entities/vc/version?branch={branch}&" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<EntityVersion>>() {
                },
                params).getBody();
    }

    /**
 * REST call: list entities at version. Calls `/api/entities/vc/entity/{entityType}/{versionId}`.
 */

    public List<VersionedEntityInfo> listEntitiesAtVersion(EntityType entityType, String versionId) {
        Map<String, String> params = new HashMap<>();
        params.put("entityType", entityType.name());
        params.put("versionId", versionId);
        return restTemplate.exchange(
                baseURL + "/api/entities/vc/entity/{entityType}/{versionId}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<VersionedEntityInfo>>() {
                },
                params).getBody();
    }

    /**
 * REST call: list all entities at version. Calls `/api/entities/vc/entity/{versionId}`.
 */

    public List<VersionedEntityInfo> listAllEntitiesAtVersion(String versionId) {
        Map<String, String> params = new HashMap<>();
        params.put("versionId", versionId);
        return restTemplate.exchange(
                baseURL + "/api/entities/vc/entity/{versionId}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<VersionedEntityInfo>>() {
                },
                params).getBody();
    }

    /**
 * GET entity data info. Calls `/api/entities/vc/info/{versionId}/{entityType}/{externalEntityUuid}`.
 */

    public EntityDataInfo getEntityDataInfo(EntityId externalEntityId, String versionId) {
        return restTemplate.getForEntity(baseURL + "/api/entities/vc/info/{versionId}/{entityType}/{externalEntityUuid}",
                EntityDataInfo.class, versionId, externalEntityId.getEntityType(), externalEntityId.getId()).getBody();
    }

    /**
 * REST call: compare entity data to version. Calls `/api/entities/vc/diff/{entityType}/{internalEntityUuid}?versionId={versionId}`.
 */

    public EntityDataDiff compareEntityDataToVersion(EntityId internalEntityId, String versionId) {
        return restTemplate.getForEntity(baseURL + "/api/entities/vc/diff/{entityType}/{internalEntityUuid}?versionId={versionId}",
                EntityDataDiff.class, internalEntityId.getEntityType(), internalEntityId.getId(), versionId).getBody();
    }

    /**
 * REST call: load entities version. Calls `/api/entities/vc/entity`.
 */

    public UUID loadEntitiesVersion(VersionLoadRequest request) {
        return restTemplate.postForEntity(baseURL + "/api/entities/vc/entity", request, UUID.class).getBody();
    }

    /**
 * GET version load request status. Calls `/api/entities/vc/entity/{requestId}/status`.
 */

    public Optional<VersionLoadResult> getVersionLoadRequestStatus(UUID requestId) {
        try {
            ResponseEntity<VersionLoadResult> versionLoadResult = restTemplate.getForEntity(baseURL + "/api/entities/vc/entity/{requestId}/status", VersionLoadResult.class, requestId);
            return Optional.ofNullable(versionLoadResult.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: list branches. Calls `/api/entities/vc/branches`.
 */

    public List<BranchInfo> listBranches() {
        return restTemplate.exchange(
                baseURL + "/api/entities/vc/branches",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<BranchInfo>>() {
                }).getBody();
    }

    /**
 * REST call: download resource. Calls `/api/resource/{resourceId}/download`.
 */

    public ResponseEntity<Resource> downloadResource(TbResourceId resourceId) {
        Map<String, String> params = new HashMap<>();
        params.put("resourceId", resourceId.getId().toString());

        return restTemplate.exchange(
                baseURL + "/api/resource/{resourceId}/download",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                params
        );
    }

    /**
 * GET entity by id. Calls `/api/resource/info/{resourceId}`.
 */

    public TbResourceInfo getResourceInfoById(TbResourceId resourceId) {
        Map<String, String> params = new HashMap<>();
        params.put("resourceId", resourceId.getId().toString());

        return restTemplate.exchange(
                baseURL + "/api/resource/info/{resourceId}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<TbResourceInfo>() {
                },
                params
        ).getBody();
    }

    /**
 * GET resource id. Calls `/api/resource/{resourceId}`.
 */

    public TbResource getResourceId(TbResourceId resourceId) {
        Map<String, String> params = new HashMap<>();
        params.put("resourceId", resourceId.getId().toString());

        return restTemplate.exchange(
                baseURL + "/api/resource/{resourceId}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<TbResource>() {
                },
                params
        ).getBody();
    }

    /**
 * POST create or update resource. Calls `/api/resource`.
 */

    public TbResource saveResource(TbResource resource) {
        return restTemplate.postForEntity(
                baseURL + "/api/resource",
                resource,
                TbResource.class
        ).getBody();
    }

    /**
 * GET resources. Calls `/api/resource?`.
 */

    public PageData<TbResourceInfo> getResources(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/resource?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<TbResourceInfo>>() {
                },
                params
        ).getBody();
    }

    /**
 * DELETE resource.
 */

    public void deleteResource(TbResourceId resourceId) {
        restTemplate.delete("/api/resource/{resourceId}", resourceId.getId().toString());
    }

    /**
 * GET image info. Calls `/api/images/{type}/{key}/info`.
 */

    public TbResourceInfo getImageInfo(String type, String key) {
        return restTemplate.getForObject(baseURL + "/api/images/{type}/{key}/info", TbResourceInfo.class, Map.of(
                "type", type,
                "key", key
        ));
    }

    /**
 * GET images.
 */

    public PageData<TbResourceInfo> getImages(PageLink pageLink, boolean includeSystemImages) {
        return this.getImages(pageLink, null, includeSystemImages);
    }

    /**
 * GET images. Calls `/api/images?includeSystemImages={includeSystemImages}&`.
 */

    public PageData<TbResourceInfo> getImages(PageLink pageLink, ResourceSubType imageSubType, boolean includeSystemImages) {
        Map<String, String> params = new HashMap<>();
        var url = baseURL + "/api/images?includeSystemImages={includeSystemImages}&";
        addPageLinkToParam(params, pageLink);
        params.put("includeSystemImages", String.valueOf(includeSystemImages));
        if (imageSubType != null) {
            url += "imageSubType={imageSubType}&";
            params.put("imageSubType", imageSubType.name());
        }
        return restTemplate.exchange(url + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<TbResourceInfo>>() {},
                params
        ).getBody();
    }

    /**
 * REST call: upload image. Calls `/api/image`.
 */

    public TbResourceInfo uploadImage(String fileName, byte[] data, String contentType, String title) {
        HttpEntity<MultiValueMap<String, Object>> request = createMultipartRequest(fileName, data, contentType, Map.of(
                "title", Strings.nullToEmpty(title)
        ));
        return restTemplate.postForObject(baseURL + "/api/image", request, TbResourceInfo.class);
    }

    /**
 * REST call: update image. Calls `/api/images/{type}/{key}`.
 */

    public TbResourceInfo updateImage(String type, String key, String fileName, byte[] data, String contentType) {
        HttpEntity<MultiValueMap<String, Object>> request = createMultipartRequest(fileName, data, contentType, Map.of());
        return restTemplate.exchange(baseURL + "/api/images/{type}/{key}", HttpMethod.PUT, request, TbResourceInfo.class, Map.of(
                "type", type,
                "key", key
        )).getBody();
    }

    /**
 * REST call: update image info. Calls `/api/images/{type}/{key}/info`.
 */

    public TbResourceInfo updateImageInfo(String type, String key, TbResourceInfo request) {
        return restTemplate.exchange(baseURL + "/api/images/{type}/{key}/info", HttpMethod.PUT, new HttpEntity<>(request), TbResourceInfo.class, Map.of(
                "type", type,
                "key", key
        )).getBody();
    }

    /**
 * REST call: update image public status. Calls `/api/images/{type}/{key}/public/{isPublic}`.
 */

    public void updateImagePublicStatus(String type, String key, boolean isPublic) {
        restTemplate.put(baseURL + "/api/images/{type}/{key}/public/{isPublic}", null, Map.of(
                "type", type,
                "key", key,
                "isPublic", isPublic
        ));
    }

    /**
 * REST call: download image. Calls `/api/images/{type}/{key}`.
 */

    public byte[] downloadImage(String type, String key) throws IOException {
        Resource image = restTemplate.exchange(baseURL + "/api/images/{type}/{key}", HttpMethod.GET, null, Resource.class, Map.of(
                "type", type,
                "key", key
        )).getBody();
        return IOUtils.toByteArray(image.getInputStream());
    }

    /**
 * REST call: download image preview. Calls `/api/images/{type}/{key}/preview`.
 */

    public byte[] downloadImagePreview(String type, String key) throws IOException {
        Resource image = restTemplate.exchange(baseURL + "/api/images/{type}/{key}/preview", HttpMethod.GET, null, Resource.class, Map.of(
                "type", type,
                "key", key
        )).getBody();
        return IOUtils.toByteArray(image.getInputStream());
    }

    /**
 * REST call: download public image. Calls `/api/images/public/{publicResourceKey}`.
 */

    public byte[] downloadPublicImage(String publicResourceKey) throws IOException {
        Resource image = restTemplate.exchange(baseURL + "/api/images/public/{publicResourceKey}", HttpMethod.GET, null, Resource.class, Map.of(
                "publicResourceKey", publicResourceKey
        )).getBody();
        return IOUtils.toByteArray(image.getInputStream());
    }

    /**
 * REST call: export image. Calls `/api/images/{type}/{key}/export`.
 */

    public ResourceExportData exportImage(String type, String key) {
        return restTemplate.getForObject(baseURL + "/api/images/{type}/{key}/export", ResourceExportData.class, Map.of(
                "type", type,
                "key", key
        ));
    }

    /**
 * REST call: import image. Calls `/api/image/import`.
 */

    public TbResourceInfo importImage(ResourceExportData exportData) {
        return restTemplate.exchange(baseURL + "/api/image/import", HttpMethod.PUT, new HttpEntity<>(exportData), TbResourceInfo.class).getBody();
    }

    /**
 * DELETE image. Calls `/api/images/{type}/{key}?force={force}`.
 */

    public TbImageDeleteResult deleteImage(String type, String key, boolean force) {
        return restTemplate.exchange(baseURL + "/api/images/{type}/{key}?force={force}", HttpMethod.DELETE, null, TbImageDeleteResult.class, Map.of(
                "type", type,
                "key", key,
                "force", force
        )).getBody();
    }

    /**
 * REST call: download ota package. Calls `/api/otaPackage/{otaPackageId}/download`.
 */

    public ResponseEntity<Resource> downloadOtaPackage(OtaPackageId otaPackageId) {
        Map<String, String> params = new HashMap<>();
        params.put("otaPackageId", otaPackageId.getId().toString());

        return restTemplate.exchange(
                baseURL + "/api/otaPackage/{otaPackageId}/download",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                params
        );
    }

    /**
 * GET entity by id. Calls `/api/otaPackage/info/{otaPackageId}`.
 */

    public OtaPackageInfo getOtaPackageInfoById(OtaPackageId otaPackageId) {
        Map<String, String> params = new HashMap<>();
        params.put("otaPackageId", otaPackageId.getId().toString());

        return restTemplate.exchange(
                baseURL + "/api/otaPackage/info/{otaPackageId}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<OtaPackageInfo>() {
                },
                params
        ).getBody();
    }

    /**
 * GET entity by id. Calls `/api/otaPackage/{otaPackageId}`.
 */

    public OtaPackage getOtaPackageById(OtaPackageId otaPackageId) {
        Map<String, String> params = new HashMap<>();
        params.put("otaPackageId", otaPackageId.getId().toString());

        return restTemplate.exchange(
                baseURL + "/api/otaPackage/{otaPackageId}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<OtaPackage>() {
                },
                params
        ).getBody();
    }

    /**
 * POST create or update ota package info. Calls `/api/otaPackage?isUrl={isUrl}`.
 */

    public OtaPackageInfo saveOtaPackageInfo(OtaPackageInfo otaPackageInfo, boolean isUrl) {
        Map<String, String> params = new HashMap<>();
        params.put("isUrl", Boolean.toString(isUrl));
        return restTemplate.postForEntity(baseURL + "/api/otaPackage?isUrl={isUrl}", otaPackageInfo, OtaPackageInfo.class, params).getBody();
    }

    /**
 * POST create or update ota package data.
 */

    public OtaPackageInfo saveOtaPackageData(OtaPackageId otaPackageId, String checkSum, ChecksumAlgorithm checksumAlgorithm, String fileName, byte[] fileBytes) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = createMultipartRequest(fileName, fileBytes, null, Collections.emptyMap());

        Map<String, String> params = new HashMap<>();
        params.put("otaPackageId", otaPackageId.getId().toString());
        params.put("checksumAlgorithm", checksumAlgorithm.name());
        String url = "/api/otaPackage/{otaPackageId}?checksumAlgorithm={checksumAlgorithm}";

        if (checkSum != null) {
            url += "&checkSum={checkSum}";
        }

        return restTemplate.postForEntity(
                baseURL + url, requestEntity, OtaPackageInfo.class, params
        ).getBody();
    }

    /**
 * GET ota packages. Calls `/api/otaPackages?`.
 */

    public PageData<OtaPackageInfo> getOtaPackages(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + "/api/otaPackages?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<OtaPackageInfo>>() {
                },
                params
        ).getBody();
    }

    /**
 * GET ota packages. Calls `/api/otaPackages/{deviceProfileId}/{type}/{hasData}?`.
 */

    public PageData<OtaPackageInfo> getOtaPackages(DeviceProfileId deviceProfileId,
                                                   OtaPackageType otaPackageType,
                                                   boolean hasData,
                                                   PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("hasData", String.valueOf(hasData));
        params.put("deviceProfileId", deviceProfileId.getId().toString());
        params.put("type", otaPackageType.name());
        addPageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + "/api/otaPackages/{deviceProfileId}/{type}/{hasData}?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<OtaPackageInfo>>() {
                },
                params
        ).getBody();
    }

    /**
 * DELETE ota package. Calls `/api/otaPackage/{otaPackageId}`.
 */

    public void deleteOtaPackage(OtaPackageId otaPackageId) {
        restTemplate.delete(baseURL + "/api/otaPackage/{otaPackageId}", otaPackageId.getId().toString());
    }

    /**
 * GET queues by service type. Calls `/api/queues?serviceType={serviceType}&`.
 */

    public PageData<Queue> getQueuesByServiceType(String serviceType, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        params.put("serviceType", serviceType);
        addPageLinkToParam(params, pageLink);

        return restTemplate.exchange(
                baseURL + "/api/queues?serviceType={serviceType}&" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Queue>>() {
                },
                params
        ).getBody();
    }

    /**
 * GET entity by id. Calls `/api/queues/`.
 */

    public Queue getQueueById(QueueId queueId) {
        return restTemplate.exchange(
                baseURL + "/api/queues/" + queueId,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Queue>() {
                }
        ).getBody();
    }

    /**
 * POST create or update queue. Calls `/api/queues?serviceType=`.
 */

    public Queue saveQueue(Queue queue, String serviceType) {
        return restTemplate.postForEntity(baseURL + "/api/queues?serviceType=" + serviceType, queue, Queue.class).getBody();
    }

    /**
 * DELETE queue. Calls `/api/queues/`.
 */

    public void deleteQueue(QueueId queueId) {
        restTemplate.delete(baseURL + "/api/queues/" + queueId);
    }

    @Deprecated
    /**
 * GET attributes. Calls `/api/v1/{accessToken}/attributes?clientKeys={clientKeys}&sharedKeys={sharedKeys}`.
 */
    public Optional<JsonNode> getAttributes(String accessToken, String clientKeys, String sharedKeys) {
        Map<String, String> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("clientKeys", clientKeys);
        params.put("sharedKeys", sharedKeys);
        try {
            ResponseEntity<JsonNode> telemetryEntity = restTemplate.getForEntity(baseURL + "/api/v1/{accessToken}/attributes?clientKeys={clientKeys}&sharedKeys={sharedKeys}", JsonNode.class, params);
            return Optional.of(telemetryEntity.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: handle rule engine request. Calls `/api/rule-engine`.
 */

    public JsonNode handleRuleEngineRequest(JsonNode requestBody) {
        return restTemplate.exchange(
                baseURL + "/api/rule-engine",
                HttpMethod.POST,
                new HttpEntity<>(requestBody),
                new ParameterizedTypeReference<JsonNode>() {
                }).getBody();
    }

    /**
 * REST call: handle rule engine request. Calls `/api/rule-engine/{entityType}/{entityId}`.
 */

    public JsonNode handleRuleEngineRequest(EntityId entityId, JsonNode requestBody) {
        return restTemplate.exchange(
                baseURL + "/api/rule-engine/{entityType}/{entityId}",
                HttpMethod.POST,
                new HttpEntity<>(requestBody),
                new ParameterizedTypeReference<JsonNode>() {
                },
                entityId.getEntityType(),
                entityId.getId()).getBody();
    }

    /**
 * REST call: handle rule engine request. Calls `/api/rule-engine/{entityType}/{entityId}/{timeout}`.
 */

    public JsonNode handleRuleEngineRequest(EntityId entityId, int timeout, JsonNode requestBody) {
        return restTemplate.exchange(
                baseURL + "/api/rule-engine/{entityType}/{entityId}/{timeout}",
                HttpMethod.POST,
                new HttpEntity<>(requestBody),
                new ParameterizedTypeReference<JsonNode>() {
                },
                entityId.getEntityType(),
                entityId.getId(),
                timeout).getBody();
    }

    /**
 * REST call: handle rule engine request. Calls `/api/rule-engine/{entityType}/{entityId}/{queueName}/{timeout}`.
 */

    public JsonNode handleRuleEngineRequest(EntityId entityId, String queueName, int timeout, JsonNode requestBody) {
        return restTemplate.exchange(
                baseURL + "/api/rule-engine/{entityType}/{entityId}/{queueName}/{timeout}",
                HttpMethod.POST,
                new HttpEntity<>(requestBody),
                new ParameterizedTypeReference<JsonNode>() {
                },
                entityId.getEntityType(),
                entityId.getId(),
                queueName,
                timeout).getBody();
    }

    /**
 * POST create or update calculated field. Calls `/api/calculatedField`.
 */

    public CalculatedField saveCalculatedField(CalculatedField calculatedField) {
        return restTemplate.postForEntity(baseURL + "/api/calculatedField", calculatedField, CalculatedField.class).getBody();
    }

    /**
 * GET entity by id. Calls `/api/calculatedField/{calculatedFieldId}`.
 */

    public Optional<CalculatedField> getCalculatedFieldById(CalculatedFieldId calculatedFieldId) {
        try {
            ResponseEntity<CalculatedField> calculatedField = restTemplate.getForEntity(baseURL + "/api/calculatedField/{calculatedFieldId}", CalculatedField.class, calculatedFieldId.getId());
            return Optional.ofNullable(calculatedField.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET calculated fields by entity id.
 */

    public PageData<CalculatedField> getCalculatedFieldsByEntityId(EntityId entityId, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/" + entityId.getEntityType() + "/" + entityId.getId() + "/calculatedFields?" + getUrlParams(pageLink),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<CalculatedField>>() {
                }, params).getBody();

    }

    @SneakyThrows(URISyntaxException.class)
    /**
 * GET calculated fields.
 */
    public PageData<CalculatedFieldInfo> getCalculatedFields(PageLink pageLink,
                                                             Set<CalculatedFieldType> types,
                                                             EntityType entityType,
                                                             Set<UUID> entities,
                                                             Set<String> names) {
        var urlBuilder = new URIBuilder(baseURL).appendPath("/api/calculatedFields");
        urlBuilder.addParameter("pageSize", String.valueOf(pageLink.getPageSize()));
        urlBuilder.addParameter("page", String.valueOf(pageLink.getPage()));
        if (!isEmpty(pageLink.getTextSearch())) {
            urlBuilder.addParameter("textSearch", pageLink.getTextSearch());
        }
        if (pageLink.getSortOrder() != null) {
            urlBuilder.addParameter("sortProperty", pageLink.getSortOrder().getProperty());
            urlBuilder.addParameter("sortOrder", pageLink.getSortOrder().getDirection().name());
        }
        if (!CollectionUtils.isEmpty(types)) {
            for (CalculatedFieldType type : types) {
                urlBuilder.addParameter("types", type.name());
            }
        }
        if (entityType != null) {
            urlBuilder.addParameter("entityType", entityType.name());
        }
        if (!CollectionUtils.isEmpty(entities)) {
            for (UUID entity : entities) {
                urlBuilder.addParameter("entities", entity.toString());
            }
        }
        if (!CollectionUtils.isEmpty(names)) {
            for (String name : names) {
                urlBuilder.addParameter("name", name);
            }
        }
        return restTemplate.exchange(
                urlBuilder.build(),
                HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<CalculatedFieldInfo>>() {}).getBody();
    }

    /**
 * DELETE calculated field. Calls `/api/calculatedField/{calculatedFieldId}`.
 */

    public void deleteCalculatedField(CalculatedFieldId calculatedFieldId) {
        restTemplate.delete(baseURL + "/api/calculatedField/{calculatedFieldId}", calculatedFieldId.getId());
    }

    /**
 * GET latest calculated field debug event. Calls `/api/calculatedField/{calculatedFieldId}/debug`.
 */

    public Optional<JsonNode> getLatestCalculatedFieldDebugEvent(CalculatedFieldId calculatedFieldId) {
        try {
            ResponseEntity<JsonNode> jsonNode = restTemplate.getForEntity(baseURL + "/api/calculatedField/{calculatedFieldId}/debug", JsonNode.class, calculatedFieldId.getId());
            return Optional.ofNullable(jsonNode.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * REST call: test calculated field script. Calls `/api/calculatedField/testScript`.
 */

    public Optional<JsonNode> testCalculatedFieldScript(JsonNode inputParams) {
        try {
            ResponseEntity<JsonNode> jsonNode = restTemplate.postForEntity(baseURL + "/api/calculatedField/testScript", inputParams, JsonNode.class);
            return Optional.ofNullable(jsonNode.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET notifications.
 */

    public PageData<Notification> getNotifications(Boolean unreadOnly, NotificationDeliveryMethod deliveryMethod, PageLink pageLink) {
        Map<String, String> params = new HashMap<>();

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseURL).append("/api/notifications?").append(getUrlParams(pageLink));
        addPageLinkToParam(params, pageLink);

        if (unreadOnly != null) {
            urlBuilder.append("&unreadOnly={unreadOnly}");
            params.put("unreadOnly", unreadOnly.toString());
        }
        if (deliveryMethod != null) {
            urlBuilder.append("&deliveryMethod={deliveryMethod}");
            params.put("deliveryMethod", deliveryMethod.name());
        }

        return restTemplate.exchange(urlBuilder.toString(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<Notification>>() {
                }, params).getBody();
    }

    /**
 * GET unread notifications count.
 */

    public Integer getUnreadNotificationsCount(NotificationDeliveryMethod deliveryMethod) {
        String uri = "/api/notifications/unread/count?";
        Map<String, String> params = new HashMap<>();
        if (deliveryMethod != null) {
            params.put("deliveryMethod", deliveryMethod.name());
            uri += "&deliveryMethod={deliveryMethod}";
        }
        return restTemplate.exchange(
                baseURL + uri,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Integer.class, params).getBody();
    }

    /**
 * REST call: mark notification as read. Calls `/api/notification/{id}/read`.
 */

    public void markNotificationAsRead(NotificationId notificationId) {
        restTemplate.exchange(
                baseURL + "/api/notification/{id}/read",
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Void.class,
                notificationId.getId());
    }

    /**
 * REST call: mark all notifications as read.
 */

    public void markAllNotificationsAsRead(NotificationDeliveryMethod deliveryMethod) {
        String uri = "/api/notifications/read?";
        Map<String, String> params = new HashMap<>();
        if (deliveryMethod != null) {
            params.put("deliveryMethod", deliveryMethod.name());
            uri += "&deliveryMethod={deliveryMethod}";
        }
        restTemplate.exchange(
                baseURL + uri,
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Void.class,
                params);
    }


    /**
 * DELETE notification.
 */


    public void deleteNotification(NotificationId notificationId) {
        restTemplate.delete(baseURL + "/api/notification/{id}", notificationId.getId());
    }

    /**
 * POST create or update notification request. Calls `/api/notification/request`.
 */

    public NotificationRequest saveNotificationRequest(NotificationRequest notificationRequest) {
        return restTemplate.postForEntity(baseURL + "/api/notification/request", notificationRequest, NotificationRequest.class).getBody();
    }

    /**
 * GET notification request preview. Calls `/api/notification/request/preview?recipientsPreviewSize={recipientsPreviewSize}`.
 */

    public NotificationRequestPreview getNotificationRequestPreview(NotificationRequest notificationRequest, int recipientsPreviewSize) {
        return restTemplate.postForEntity(baseURL + "/api/notification/request/preview?recipientsPreviewSize={recipientsPreviewSize}", notificationRequest, NotificationRequestPreview.class, recipientsPreviewSize).getBody();
    }

    /**
 * GET entity by id. Calls `/api/notification/request/{id}`.
 */

    public Optional<NotificationRequestInfo> getNotificationRequestById(NotificationRequestId notificationRequestId) {
        try {
            ResponseEntity<NotificationRequestInfo> notificationRequest = restTemplate.getForEntity(baseURL + "/api/notification/request/{id}", NotificationRequestInfo.class, notificationRequestId.getId());
            return Optional.ofNullable(notificationRequest.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET notification requests. Calls `/api/notification/requests?`.
 */

    public PageData<NotificationRequestInfo> getNotificationRequests(PageLink pageLink) {
        Map<String, String> params = new HashMap<>();
        addPageLinkToParam(params, pageLink);
        return restTemplate.exchange(
                baseURL + "/api/notification/requests?" + getUrlParams(pageLink),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageData<NotificationRequestInfo>>() {
                }, params).getBody();
    }

    /**
 * DELETE notification request. Calls `/api/notification/request/{id}`.
 */

    public void deleteNotificationRequest(NotificationRequestId notificationRequestId) {
        restTemplate.delete(baseURL + "/api/notification/request/{id}", notificationRequestId.getId());
    }

    /**
 * POST create or update notification settings. Calls `/api/notification/settings`.
 */

    public NotificationSettings saveNotificationSettings(NotificationSettings notificationSettings) {
        return restTemplate.postForEntity(baseURL + "/api/notification/settings", notificationSettings, NotificationSettings.class).getBody();
    }

    /**
 * GET notification settings. Calls `/api/notification/settings`.
 */

    public Optional<NotificationSettings> getNotificationSettings() {
        try {
            ResponseEntity<NotificationSettings> notificationSettings = restTemplate.getForEntity(baseURL + "/api/notification/settings", NotificationSettings.class);
            return Optional.ofNullable(notificationSettings.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * GET available delivery methods. Calls `/api/notification/deliveryMethods`.
 */

    public List<NotificationDeliveryMethod> getAvailableDeliveryMethods() {
        return restTemplate.exchange(URI.create(
                        baseURL + "/api/notification/deliveryMethods"),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<NotificationDeliveryMethod>>() {
                }).getBody();
    }

    /**
 * POST create or update user notification settings. Calls `/api/notification/settings/user`.
 */

    public UserNotificationSettings saveUserNotificationSettings(UserNotificationSettings userNotificationSettings) {
        return restTemplate.postForEntity(baseURL + "/api/notification/settings/user", userNotificationSettings, UserNotificationSettings.class).getBody();
    }

    /**
 * GET user notification settings. Calls `/api/notification/settings/user`.
 */

    public Optional<UserNotificationSettings> getUserNotificationSettings() {
        try {
            ResponseEntity<UserNotificationSettings> userNotificationSettings = restTemplate.getForEntity(baseURL + "/api/notification/settings/user", UserNotificationSettings.class);
            return Optional.ofNullable(userNotificationSettings.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * POST create or update notification target. Calls `/api/notification/target`.
 */

    public NotificationTarget saveNotificationTarget(NotificationTarget notificationTarget) {
        return restTemplate.postForEntity(baseURL + "/api/notification/target", notificationTarget, NotificationTarget.class).getBody();
    }

    /**
 * POST create or update notification template. Calls `/api/notification/template`.
 */

    public NotificationTemplate saveNotificationTemplate(NotificationTemplate notificationTemplate) {
        return restTemplate.postForEntity(baseURL + "/api/notification/template", notificationTemplate, NotificationTemplate.class).getBody();
    }

    /**
 * POST create or update ai model. Calls `/api/ai/model`.
 */

    public AiModel saveAiModel(AiModel aiModel) {
        return restTemplate.postForEntity(baseURL + "/api/ai/model", aiModel, AiModel.class).getBody();
    }

    /**
 * GET ai model. Calls `/api/ai/model/{aiModelId}`.
 */

    public Optional<AiModel> getAiModel(AiModelId aiModelId) {
        try {
            ResponseEntity<AiModel> response = restTemplate.getForEntity(
                    baseURL + "/api/ai/model/{aiModelId}", AiModel.class, aiModelId.getId());
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw exception;
            }
        }
    }

    /**
 * DELETE ai model. Calls `/api/ai/model/{aiModelId}`.
 */

    public void deleteAiModel(AiModelId aiModelId) {
        restTemplate.delete(baseURL + "/api/ai/model/{aiModelId}", aiModelId.getId());
    }


    private String getTimeUrlParams(TimePageLink pageLink) {
        String urlParams = getUrlParams(pageLink);
        if (pageLink.getStartTime() != null) {
            urlParams += "&startTime={startTime}";
        }
        if (pageLink.getEndTime() != null) {
            urlParams += "&endTime={endTime}";
        }
        return urlParams;
    }

    private String getUrlParams(PageLink pageLink) {
        String urlParams = "pageSize={pageSize}&page={page}";
        if (!isEmpty(pageLink.getTextSearch())) {
            urlParams += "&textSearch={textSearch}";
        }
        if (pageLink.getSortOrder() != null) {
            urlParams += "&sortProperty={sortProperty}&sortOrder={sortOrder}";
        }
        return urlParams;
    }

    private String getWidgetTypeInfoPageRequestUrlParams(Boolean tenantOnly, Boolean fullSearch,
                                                         DeprecatedFilter deprecatedFilter,
                                                         List<String> widgetTypeList) {
        String urlParams = getTenantOnlyAndFullSearchUrlParams(tenantOnly, fullSearch);
        if (deprecatedFilter != null) {
            urlParams += "&deprecatedFilter={deprecatedFilter}";
        }
        if (!CollectionUtils.isEmpty(widgetTypeList)) {
            urlParams += "&widgetTypeList={widgetTypeList}";
        }
        return urlParams;
    }

    private String getTenantOnlyAndFullSearchUrlParams(Boolean tenantOnly, Boolean fullSearch) {
        String urlParams = "";
        if (tenantOnly != null) {
            urlParams = "&tenantOnly={tenantOnly}";
        }
        if (fullSearch != null) {
            urlParams += "&fullSearch={fullSearch}";
        }
        return urlParams;
    }

    private void addTimePageLinkToParam(Map<String, String> params, TimePageLink pageLink) {
        this.addPageLinkToParam(params, pageLink);
        if (pageLink.getStartTime() != null) {
            params.put("startTime", String.valueOf(pageLink.getStartTime()));
        }
        if (pageLink.getEndTime() != null) {
            params.put("endTime", String.valueOf(pageLink.getEndTime()));
        }
    }

    private void addPageLinkToParam(Map<String, String> params, PageLink pageLink) {
        params.put("pageSize", String.valueOf(pageLink.getPageSize()));
        params.put("page", String.valueOf(pageLink.getPage()));
        if (!isEmpty(pageLink.getTextSearch())) {
            params.put("textSearch", pageLink.getTextSearch());
        }
        if (pageLink.getSortOrder() != null) {
            params.put("sortProperty", pageLink.getSortOrder().getProperty());
            params.put("sortOrder", pageLink.getSortOrder().getDirection().name());
        }
    }

    private void addWidgetInfoFiltersToParams(Boolean tenantOnly, Boolean fullSearch, DeprecatedFilter deprecatedFilter,
                                              List<String> widgetTypeList, Map<String, String> params) {
        addTenantOnlyAndFullSearchToParams(tenantOnly, fullSearch, params);
        if (deprecatedFilter != null) {
            params.put("deprecatedFilter", deprecatedFilter.name());
        }
        if (!CollectionUtils.isEmpty(widgetTypeList)) {
            params.put("widgetTypeList", listToString(widgetTypeList));
        }
    }

    private void addTenantOnlyAndFullSearchToParams(Boolean tenantOnly, Boolean fullSearch, Map<String, String> params) {
        if (tenantOnly != null) {
            params.put("tenantOnly", tenantOnly.toString());
        }
        if (fullSearch != null) {
            params.put("fullSearch", fullSearch.toString());
        }
    }

    private String listToString(List<String> list) {
        return String.join(",", list);
    }

    private String listIdsToString(List<? extends EntityId> list) {
        return listToString(list.stream().map(id -> id.getId().toString()).collect(Collectors.toList()));
    }

    private String listEnumToString(List<? extends Enum> list) {
        return listToString(list.stream().map(Enum::name).collect(Collectors.toList()));
    }

    private HttpEntity<MultiValueMap<String, Object>> createMultipartRequest(String fileName, byte[] fileData, String fileContentType, Map<String, Object> otherParts) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=file; filename=" + fileName);
        if (fileContentType != null) {
            fileMap.add(HttpHeaders.CONTENT_TYPE, fileContentType);
        }
        HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(new ByteArrayResource(fileData), fileMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.setAll(otherParts);
        body.add("file", fileEntity);
        return new HttpEntity<>(body, headers);
    }

    @Override
    /**
 * Shuts down the internal executor service.
 */
    public void close() {
        if (executor.isInitialized()) {
            getExecutor().shutdown();
        }
    }

    @SneakyThrows
    private ExecutorService getExecutor() {
        return executor.get();
    }

}
