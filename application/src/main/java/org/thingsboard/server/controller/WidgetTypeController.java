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

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.widget.DeprecatedFilter;
import org.thingsboard.server.common.data.widget.WidgetType;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetTypeFilter;
import org.thingsboard.server.common.data.widget.WidgetTypeInfo;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.config.annotations.ApiOperation;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.widgets.type.TbWidgetTypeService;
import org.thingsboard.server.service.resource.TbResourceService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.thingsboard.server.controller.ControllerConstants.AVAILABLE_FOR_ANY_AUTHORIZED_USER;
import static org.thingsboard.server.controller.ControllerConstants.INCLUDE_RESOURCES;
import static org.thingsboard.server.controller.ControllerConstants.INCLUDE_RESOURCES_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_DATA_PARAMETERS;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_NUMBER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_SIZE_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_PROPERTY_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH;
import static org.thingsboard.server.controller.ControllerConstants.UUID_WIKI_LINK;
import static org.thingsboard.server.controller.ControllerConstants.WIDGET_TYPE_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.WIDGET_TYPE_TEXT_SEARCH_DESCRIPTION;

/**
 * REST API for widget type definitions used by dashboards.
 *
 * <p>Base path: {@code /api/widgetType} and related list endpoints. Widget types belong to
 * system or tenant scope; FQN is unique within that scope.
 *
 * @see WidgetsBundleController
 * @see org.thingsboard.server.service.entitiy.widgets.type.TbWidgetTypeService
 */
@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
public class WidgetTypeController extends AutoCommitController {

    private final TbWidgetTypeService tbWidgetTypeService;
    private final TbResourceService tbResourceService;

    private static final String WIDGET_TYPE_DESCRIPTION = "Widget Type represents the template for widget creation. Widget Type and Widget are similar to class and object in OOP theory.";
    private static final String WIDGET_TYPE_DETAILS_DESCRIPTION = "Widget Type Details extend Widget Type and add image and description properties. " +
            "Those properties are useful to edit the Widget Type but they are not required for Dashboard rendering. ";
    private static final String WIDGET_TYPE_INFO_DESCRIPTION = "Widget Type Info is a lightweight object that represents Widget Type but does not contain the heavyweight widget descriptor JSON";
    private static final String TENANT_ONLY_PARAM_DESCRIPTION = "Optional boolean parameter indicating whether only tenant widget types should be returned";
    private static final String FULL_SEARCH_PARAM_DESCRIPTION = "Optional boolean parameter indicating whether search widgets by description not only by name";
    private static final String SCADA_FIRST_PARAM_DESCRIPTION = "Optional boolean parameter indicating whether to fetch SCADA symbol widgets first";
    private static final String DEPRECATED_FILTER_PARAM_DESCRIPTION = "Optional string parameter indicating whether to include deprecated widgets";
    private static final String UPDATE_EXISTING_BY_FQN_PARAM_DESCRIPTION = "Optional boolean parameter indicating whether to update existing widget type by FQN if present instead of creating new one";
    private static final String WIDGET_TYPE_ARRAY_DESCRIPTION = "A list of string values separated by comma ',' representing one of the widget type value";

    /**
     * Returns full widget type details by id, optionally including exported resources.
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetType/{widgetTypeId}?includeResources=true|false}
     * <p><b>Auth:</b> {@code SYS_ADMIN} or {@code TENANT_ADMIN}
     *
     * @param strWidgetTypeId widget type UUID
     * @param includeResources when {@code true}, embeds related resource payloads
     * @return {@link WidgetTypeDetails}
     * @throws ThingsboardException if not found or access denied
     */
    @ApiOperation(value = "Get Widget Type Details (getWidgetTypeById)",
            notes = "Get the Widget Type Details based on the provided Widget Type Id. " + WIDGET_TYPE_DETAILS_DESCRIPTION + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @GetMapping(value = "/widgetType/{widgetTypeId}")
    public WidgetTypeDetails getWidgetTypeById(@Parameter(description = WIDGET_TYPE_ID_PARAM_DESCRIPTION, required = true)
                                               @PathVariable("widgetTypeId") String strWidgetTypeId,
                                               @Parameter(description = INCLUDE_RESOURCES_DESCRIPTION)
                                               @RequestParam(value = INCLUDE_RESOURCES, required = false) boolean includeResources) throws ThingsboardException {
        checkParameter("widgetTypeId", strWidgetTypeId);
        WidgetTypeId widgetTypeId = new WidgetTypeId(toUUID(strWidgetTypeId));
        WidgetTypeDetails widgetTypeDetails = checkWidgetTypeId(widgetTypeId, Operation.READ);
        if (includeResources) {
            widgetTypeDetails.setResources(tbResourceService.exportResources(widgetTypeDetails, getCurrentUser()));
        }
        return widgetTypeDetails;
    }

    /**
     * Returns lightweight widget type info by id (no descriptor JSON).
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetTypeInfo/{widgetTypeId}}
     * <p><b>Auth:</b> {@code SYS_ADMIN} or {@code TENANT_ADMIN}
     *
     * @param strWidgetTypeId widget type UUID
     * @return {@link WidgetTypeInfo}
     * @throws ThingsboardException if not found or access denied
     */
    @ApiOperation(value = "Get Widget Type Info (getWidgetTypeInfoById)",
            notes = "Get the Widget Type Info based on the provided Widget Type Id. " + WIDGET_TYPE_DETAILS_DESCRIPTION + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @GetMapping(value = "/widgetTypeInfo/{widgetTypeId}")
    public WidgetTypeInfo getWidgetTypeInfoById(
            @Parameter(description = WIDGET_TYPE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable("widgetTypeId") String strWidgetTypeId) throws ThingsboardException {
        checkParameter("widgetTypeId", strWidgetTypeId);
        WidgetTypeId widgetTypeId = new WidgetTypeId(toUUID(strWidgetTypeId));
        return checkWidgetTypeInfoId(widgetTypeId, Operation.READ);
    }

    /**
     * Creates or updates a widget type; optionally upserts by FQN.
     *
     * <p><b>HTTP:</b> {@code POST /api/widgetType?updateExistingByFqn=true|false}
     * <p><b>Auth:</b> {@code SYS_ADMIN} or {@code TENANT_ADMIN}
     *
     * @param widgetTypeDetails widget type JSON body
     * @param updateExistingByFqn when {@code true}, update existing type with same FQN
     * @return saved {@link WidgetTypeDetails}
     * @throws Exception on validation or permission errors
     */
    @ApiOperation(value = "Create Or Update Widget Type (saveWidgetType)",
            notes = "Create or update the Widget Type. " + WIDGET_TYPE_DESCRIPTION + " " +
                    "When creating the Widget Type, platform generates Widget Type Id as " + UUID_WIKI_LINK +
                    "The newly created Widget Type Id will be present in the response. " +
                    "Specify existing Widget Type id to update the Widget Type. " +
                    "Referencing non-existing Widget Type Id will cause 'Not Found' error." +
                    "\n\nWidget Type fqn is unique in the scope of System or Tenant. " +
                    "Special Tenant Id '13814000-1dd2-11b2-8080-808080808080' is automatically used if the create request is sent by user with 'SYS_ADMIN' authority." +
                    "Remove 'id', 'tenantId' rom the request body example (below) to create new Widget Type entity." +
                    SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @PostMapping(value = "/widgetType")
    public WidgetTypeDetails saveWidgetType(
            @Parameter(description = "A JSON value representing the Widget Type Details.", required = true)
            @RequestBody WidgetTypeDetails widgetTypeDetails,
            @Parameter(description = UPDATE_EXISTING_BY_FQN_PARAM_DESCRIPTION)
            @RequestParam(required = false) Boolean updateExistingByFqn) throws Exception {
        var currentUser = getCurrentUser();
        if (Authority.SYS_ADMIN.equals(currentUser.getAuthority())) {
            widgetTypeDetails.setTenantId(TenantId.SYS_TENANT_ID);
        } else {
            widgetTypeDetails.setTenantId(currentUser.getTenantId());
        }

        checkEntity(widgetTypeDetails.getId(), widgetTypeDetails, Resource.WIDGET_TYPE);
        return tbWidgetTypeService.save(widgetTypeDetails, updateExistingByFqn != null && updateExistingByFqn, currentUser);
    }

    /**
     * Deletes a widget type by id.
     *
     * <p><b>HTTP:</b> {@code DELETE /api/widgetType/{widgetTypeId}}
     * <p><b>Auth:</b> {@code SYS_ADMIN} or {@code TENANT_ADMIN}
     *
     * @param strWidgetTypeId widget type UUID
     * @throws Exception if not found or access denied
     */
    @ApiOperation(value = "Delete widget type (deleteWidgetType)",
            notes = "Deletes the  Widget Type. Referencing non-existing Widget Type Id will cause an error." + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @DeleteMapping(value = "/widgetType/{widgetTypeId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteWidgetType(
            @Parameter(description = WIDGET_TYPE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable("widgetTypeId") String strWidgetTypeId) throws Exception {
        checkParameter("widgetTypeId", strWidgetTypeId);
        WidgetTypeId widgetTypeId = new WidgetTypeId(toUUID(strWidgetTypeId));
        WidgetTypeDetails wtd = checkWidgetTypeId(widgetTypeId, Operation.DELETE);
        tbWidgetTypeService.delete(wtd, getCurrentUser());
    }

    /**
     * Returns a paginated list of widget types available to the caller.
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetTypes?pageSize=&amp;page=&amp;...}
     * <p><b>Auth:</b> {@code SYS_ADMIN}, {@code TENANT_ADMIN}, or {@code CUSTOMER_USER}
     *
     * @param pageSize page size
     * @param page zero-based page index
     * @param textSearch optional name/description filter
     * @param sortProperty sort field
     * @param sortOrder {@code ASC} or {@code DESC}
     * @param tenantOnly when {@code true}, tenant-owned types only
     * @param fullSearch search description as well as name
     * @param deprecatedFilter {@code ALL}, {@code ACTUAL}, or {@code DEPRECATED}
     * @param widgetTypeList optional widget category filters
     * @param scadaFirst prioritize SCADA symbol widgets
     * @return page of {@link WidgetTypeInfo}
     * @throws ThingsboardException on authorization failure
     */
    @ApiOperation(value = "Get Widget Types (getWidgetTypes)",
            notes = "Returns a page of Widget Type objects available for current user. " + WIDGET_TYPE_DESCRIPTION + " " +
                    PAGE_DATA_PARAMETERS + AVAILABLE_FOR_ANY_AUTHORIZED_USER)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/widgetTypes", params = {"pageSize", "page"})
    public PageData<WidgetTypeInfo> getWidgetTypes(
            @Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @Parameter(description = WIDGET_TYPE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "name", "deprecated", "tenantId"}))
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC, DESC"}))
            @RequestParam(required = false) String sortOrder,
            @Parameter(description = TENANT_ONLY_PARAM_DESCRIPTION)
            @RequestParam(required = false) Boolean tenantOnly,
            @Parameter(description = FULL_SEARCH_PARAM_DESCRIPTION)
            @RequestParam(required = false) Boolean fullSearch,
            @Parameter(description = DEPRECATED_FILTER_PARAM_DESCRIPTION, schema = @Schema(allowableValues = {"ALL", "ACTUAL", "DEPRECATED"}))
            @RequestParam(required = false) String deprecatedFilter,
            @Parameter(description = WIDGET_TYPE_ARRAY_DESCRIPTION, array = @ArraySchema(schema = @Schema(type = "string", allowableValues = {"timeseries", "latest", "control", "alarm", "static"})))
            @RequestParam(required = false) String[] widgetTypeList,
            @Parameter(description = SCADA_FIRST_PARAM_DESCRIPTION)
            @RequestParam(required = false) Boolean scadaFirst) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        List<String> widgetTypes = widgetTypeList != null ? Arrays.asList(widgetTypeList) : Collections.emptyList();
        DeprecatedFilter widgetTypeDeprecatedFilter = StringUtils.isNotEmpty(deprecatedFilter) ? DeprecatedFilter.valueOf(deprecatedFilter) : DeprecatedFilter.ALL;
        WidgetTypeFilter widgetTypeFilter = WidgetTypeFilter.builder()
                .tenantId(getTenantId())
                .widgetTypes(widgetTypes)
                .deprecatedFilter(widgetTypeDeprecatedFilter)
                .fullSearch(fullSearch != null && fullSearch)
                .scadaFirst(scadaFirst != null && scadaFirst)
                .build();
        if (Authority.SYS_ADMIN.equals(getCurrentUser().getAuthority())) {
            return checkNotNull(widgetTypeService.findSystemWidgetTypesByPageLink(widgetTypeFilter, pageLink));
        } else {
            if (tenantOnly != null && tenantOnly) {
                return checkNotNull(widgetTypeService.findTenantWidgetTypesByTenantIdAndPageLink(widgetTypeFilter, pageLink));
            } else {
                return checkNotNull(widgetTypeService.findAllTenantWidgetTypesByTenantIdAndPageLink(widgetTypeFilter, pageLink));
            }
        }
    }

    /**
     * Returns widget types for a bundle identified by alias (deprecated).
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetTypes?isSystem=&amp;bundleAlias=}
     * <p><b>Auth:</b> {@code SYS_ADMIN} or {@code TENANT_ADMIN}
     *
     * @param isSystem {@code true} for system bundle scope
     * @param bundleAlias widgets bundle alias
     * @return list of {@link WidgetType}
     * @throws ThingsboardException if bundle not found
     * @deprecated use {@link #getBundleWidgetTypes(String)}
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @GetMapping(value = "/widgetTypes", params = {"isSystem", "bundleAlias"})
    @Deprecated
    public List<WidgetType> getBundleWidgetTypesByBundleAlias(
            @Parameter(description = "System or Tenant", required = true)
            @RequestParam boolean isSystem,
            @Parameter(description = "Widget Bundle alias", required = true)
            @RequestParam String bundleAlias) throws ThingsboardException {
        TenantId tenantId;
        if (isSystem) {
            tenantId = TenantId.SYS_TENANT_ID;
        } else {
            tenantId = getCurrentUser().getTenantId();
        }
        WidgetsBundle widgetsBundle = checkNotNull(widgetsBundleService.findWidgetsBundleByTenantIdAndAlias(tenantId, bundleAlias));
        return checkNotNull(widgetTypeService.findWidgetTypesByWidgetsBundleId(getTenantId(), widgetsBundle.getId()));
    }

    /**
     * Returns widget types for a bundle by id (legacy query-param API).
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetTypes?widgetsBundleId=}
     * <p><b>Auth:</b> {@code SYS_ADMIN}, {@code TENANT_ADMIN}, or {@code CUSTOMER_USER}
     *
     * @param strWidgetsBundleId widgets bundle UUID
     * @return list of {@link WidgetType}
     * @throws ThingsboardException if bundle not found
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/widgetTypes", params = {"widgetsBundleId"})
    public List<WidgetType> getBundleWidgetTypesV1(
            @Parameter(description = "Widget Bundle Id", required = true)
            @RequestParam("widgetsBundleId") String strWidgetsBundleId) throws ThingsboardException {
        WidgetsBundleId widgetsBundleId = new WidgetsBundleId(toUUID(strWidgetsBundleId));
        return checkNotNull(widgetTypeService.findWidgetTypesByWidgetsBundleId(getTenantId(), widgetsBundleId));
    }

    /**
     * Returns all widget types belonging to a widgets bundle.
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetsBundle/{widgetsBundleId}/widgetTypes}
     * <p><b>Auth:</b> {@code SYS_ADMIN}, {@code TENANT_ADMIN}, or {@code CUSTOMER_USER}
     *
     * @param strWidgetsBundleId widgets bundle UUID
     * @return list of {@link WidgetType}
     * @throws ThingsboardException if bundle not found
     */
    @ApiOperation(value = "Get all Widget types for specified Bundle (getBundleWidgetTypes)",
            notes = "Returns an array of Widget Type objects that belong to specified Widget Bundle." + WIDGET_TYPE_DESCRIPTION + " " + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/widgetsBundle/{widgetsBundleId}/widgetTypes")
    public List<WidgetType> getBundleWidgetTypes(
            @Parameter(description = "Widget Bundle Id", required = true)
            @PathVariable("widgetsBundleId") String strWidgetsBundleId) throws ThingsboardException {
        return getBundleWidgetTypesV1(strWidgetsBundleId);
    }

    /**
     * Returns widget type details for a bundle by alias (deprecated).
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetTypesDetails?isSystem=&amp;bundleAlias=}
     * <p><b>Auth:</b> {@code SYS_ADMIN} or {@code TENANT_ADMIN}
     *
     * @param isSystem {@code true} for system bundle scope
     * @param bundleAlias widgets bundle alias
     * @return list of {@link WidgetTypeDetails}
     * @throws ThingsboardException if bundle not found
     * @deprecated use {@link #getBundleWidgetTypesDetails(String, boolean)}
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @GetMapping(value = "/widgetTypesDetails", params = {"isSystem", "bundleAlias"})
    @Deprecated
    public List<WidgetTypeDetails> getBundleWidgetTypesDetailsByBundleAlias(
            @Parameter(description = "System or Tenant", required = true)
            @RequestParam boolean isSystem,
            @Parameter(description = "Widget Bundle alias", required = true)
            @RequestParam String bundleAlias) throws ThingsboardException {
        TenantId tenantId;
        if (isSystem) {
            tenantId = TenantId.SYS_TENANT_ID;
        } else {
            tenantId = getCurrentUser().getTenantId();
        }
        WidgetsBundle widgetsBundle = checkNotNull(widgetsBundleService.findWidgetsBundleByTenantIdAndAlias(tenantId, bundleAlias));
        return checkNotNull(widgetTypeService.findWidgetTypesDetailsByWidgetsBundleId(getTenantId(), widgetsBundle.getId()));
    }

    /**
     * Returns widget type details for a bundle by id.
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetTypesDetails?widgetsBundleId=&amp;includeResources=}
     * <p><b>Auth:</b> {@code SYS_ADMIN}, {@code TENANT_ADMIN}, or {@code CUSTOMER_USER}
     *
     * @param strWidgetsBundleId widgets bundle UUID
     * @param includeResources when {@code true}, embeds related resource payloads
     * @return list of {@link WidgetTypeDetails}
     * @throws ThingsboardException if bundle not found
     */
    @ApiOperation(value = "Get all Widget types details for specified Bundle (getBundleWidgetTypesDetails)",
            notes = "Returns an array of Widget Type Details objects that belong to specified Widget Bundle." + WIDGET_TYPE_DETAILS_DESCRIPTION + " " + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/widgetTypesDetails", params = {"widgetsBundleId"})
    public List<WidgetTypeDetails> getBundleWidgetTypesDetails(
            @Parameter(description = "Widget Bundle Id", required = true)
            @RequestParam("widgetsBundleId") String strWidgetsBundleId,
            @Parameter(description = INCLUDE_RESOURCES_DESCRIPTION)
            @RequestParam(value = INCLUDE_RESOURCES, required = false) boolean includeResources
    ) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        WidgetsBundleId widgetsBundleId = new WidgetsBundleId(toUUID(strWidgetsBundleId));
        List<WidgetTypeDetails> result = checkNotNull(widgetTypeService.findWidgetTypesDetailsByWidgetsBundleId(getTenantId(), widgetsBundleId));
        if (includeResources) {
            for (WidgetTypeDetails widgetTypeDetails : result) {
                widgetTypeDetails.setResources(tbResourceService.exportResources(widgetTypeDetails, user));
            }
        }
        return result;
    }

    /**
     * Returns FQNs of widget types in a widgets bundle.
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetTypeFqns?widgetsBundleId=}
     * <p><b>Auth:</b> {@code SYS_ADMIN} or {@code TENANT_ADMIN}
     *
     * @param strWidgetsBundleId widgets bundle UUID
     * @return ordered list of widget type FQN strings
     * @throws ThingsboardException if bundle not found
     */
    @ApiOperation(value = "Get all Widget type fqns for specified Bundle (getBundleWidgetTypeFqns)",
            notes = "Returns an array of Widget Type fqns that belong to specified Widget Bundle." + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @GetMapping(value = "/widgetTypeFqns")
    public List<String> getBundleWidgetTypeFqns(
            @Parameter(description = "Widget Bundle Id", required = true)
            @RequestParam("widgetsBundleId") String strWidgetsBundleId) throws ThingsboardException {
        WidgetsBundleId widgetsBundleId = new WidgetsBundleId(toUUID(strWidgetsBundleId));
        return checkNotNull(widgetTypeService.findWidgetFqnsByWidgetsBundleId(getTenantId(), widgetsBundleId));
    }

    /**
     * Returns widget type info for a bundle by alias (deprecated).
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetTypesInfos?isSystem=&amp;bundleAlias=}
     * <p><b>Auth:</b> {@code SYS_ADMIN}, {@code TENANT_ADMIN}, or {@code CUSTOMER_USER}
     *
     * @param isSystem {@code true} for system bundle scope
     * @param bundleAlias widgets bundle alias
     * @return list of {@link WidgetTypeInfo}
     * @throws ThingsboardException if bundle not found
     * @deprecated use {@link #getBundleWidgetTypesInfos(String, int, int, String, String, String, Boolean, String, String[])}
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/widgetTypesInfos", params = {"isSystem", "bundleAlias"})
    @Deprecated
    public List<WidgetTypeInfo> getBundleWidgetTypesInfosByBundleAlias(
            @Parameter(description = "System or Tenant", required = true)
            @RequestParam boolean isSystem,
            @Parameter(description = "Widget Bundle alias", required = true)
            @RequestParam String bundleAlias) throws ThingsboardException {
        TenantId tenantId;
        if (isSystem) {
            tenantId = TenantId.SYS_TENANT_ID;
        } else {
            tenantId = getCurrentUser().getTenantId();
        }
        WidgetsBundle widgetsBundle = checkNotNull(widgetsBundleService.findWidgetsBundleByTenantIdAndAlias(tenantId, bundleAlias));
        return checkNotNull(widgetTypeService.findWidgetTypesInfosByWidgetsBundleId(getTenantId(), widgetsBundle.getId(), false, DeprecatedFilter.ALL,
                null, new PageLink(1024))).getData();
    }

    /**
     * Returns a paginated list of widget type info objects for a widgets bundle.
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetTypesInfos?widgetsBundleId=&amp;pageSize=&amp;page=&amp;...}
     * <p><b>Auth:</b> {@code SYS_ADMIN}, {@code TENANT_ADMIN}, or {@code CUSTOMER_USER}
     *
     * @param strWidgetsBundleId widgets bundle UUID
     * @param pageSize page size
     * @param page zero-based page index
     * @param textSearch optional filter
     * @param sortProperty sort field
     * @param sortOrder {@code ASC} or {@code DESC}
     * @param fullSearch search description as well as name
     * @param deprecatedFilter {@code ALL}, {@code ACTUAL}, or {@code DEPRECATED}
     * @param widgetTypeList optional category filters
     * @return page of {@link WidgetTypeInfo}
     * @throws ThingsboardException if bundle not found
     */
    @ApiOperation(value = "Get Widget Type Info objects (getBundleWidgetTypesInfos)",
            notes = "Get the Widget Type Info objects based on the provided parameters. " + WIDGET_TYPE_INFO_DESCRIPTION + AVAILABLE_FOR_ANY_AUTHORIZED_USER)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/widgetTypesInfos", params = {"widgetsBundleId", "pageSize", "page"})
    public PageData<WidgetTypeInfo> getBundleWidgetTypesInfos(
            @Parameter(description = "Widget Bundle Id", required = true)
            @RequestParam("widgetsBundleId") String strWidgetsBundleId,
            @Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @Parameter(description = WIDGET_TYPE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "name", "deprecated", "tenantId"}))
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC", "DESC"}))
            @RequestParam(required = false) String sortOrder,
            @Parameter(description = FULL_SEARCH_PARAM_DESCRIPTION)
            @RequestParam(required = false) Boolean fullSearch,
            @Parameter(description = DEPRECATED_FILTER_PARAM_DESCRIPTION, schema = @Schema(allowableValues = {"ALL", "ACTUAL", "DEPRECATED"}))
            @RequestParam(required = false) String deprecatedFilter,
            @Parameter(description = WIDGET_TYPE_ARRAY_DESCRIPTION, array = @ArraySchema(schema = @Schema(allowableValues = {"timeseries", "latest", "control", "alarm", "static"})))
            @RequestParam(required = false) String[] widgetTypeList) throws ThingsboardException {
        WidgetsBundleId widgetsBundleId = new WidgetsBundleId(toUUID(strWidgetsBundleId));
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        List<String> widgetTypes = widgetTypeList != null ? Arrays.asList(widgetTypeList) : Collections.emptyList();
        DeprecatedFilter widgetTypeDeprecatedFilter = StringUtils.isNotEmpty(deprecatedFilter) ? DeprecatedFilter.valueOf(deprecatedFilter) : DeprecatedFilter.ALL;
        return checkNotNull(widgetTypeService.findWidgetTypesInfosByWidgetsBundleId(getTenantId(), widgetsBundleId, fullSearch != null && fullSearch,
                widgetTypeDeprecatedFilter, widgetTypes, pageLink));
    }

    /**
     * Returns a widget type by bundle and type alias (deprecated).
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetType?isSystem=&amp;bundleAlias=&amp;alias=}
     * <p><b>Auth:</b> {@code SYS_ADMIN}, {@code TENANT_ADMIN}, or {@code CUSTOMER_USER}
     *
     * @param isSystem {@code true} for system scope
     * @param bundleAlias bundle alias segment
     * @param alias widget type alias within bundle
     * @return {@link WidgetType}
     * @throws ThingsboardException if not found or access denied
     * @deprecated use {@link #getWidgetType(String)} with FQN
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/widgetType", params = {"isSystem", "bundleAlias", "alias"})
    @Deprecated
    public WidgetType getWidgetTypeByBundleAliasAndTypeAlias(
            @Parameter(description = "System or Tenant", required = true)
            @RequestParam boolean isSystem,
            @Parameter(description = "Widget Bundle alias", required = true)
            @RequestParam String bundleAlias,
            @Parameter(description = "Widget Type alias", required = true)
            @RequestParam String alias) throws ThingsboardException {
        TenantId tenantId;
        if (isSystem) {
            tenantId = TenantId.fromUUID(ModelConstants.NULL_UUID);
        } else {
            tenantId = getCurrentUser().getTenantId();
        }
        WidgetType widgetType = widgetTypeService.findWidgetTypeByTenantIdAndFqn(tenantId, bundleAlias + "." + alias);
        checkNotNull(widgetType);
        accessControlService.checkPermission(getCurrentUser(), Resource.WIDGET_TYPE, Operation.READ, widgetType.getId(), widgetType);
        return widgetType;
    }

    /**
     * Returns a widget type by fully qualified name ({@code system.*} or {@code tenant.*}).
     *
     * <p><b>HTTP:</b> {@code GET /api/widgetType?fqn=}
     * <p><b>Auth:</b> {@code SYS_ADMIN}, {@code TENANT_ADMIN}, or {@code CUSTOMER_USER}
     *
     * @param fqn widget type FQN with scope prefix
     * @return {@link WidgetType}
     * @throws ThingsboardException if FQN is invalid, not found, or access denied
     */
    @ApiOperation(value = "Get Widget Type (getWidgetType)",
            notes = "Get the Widget Type by FQN. " + WIDGET_TYPE_DESCRIPTION + AVAILABLE_FOR_ANY_AUTHORIZED_USER, hidden = true)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/widgetType", params = {"fqn"})
    public WidgetType getWidgetType(
            @Parameter(description = "Widget Type fqn", required = true)
            @RequestParam String fqn) throws ThingsboardException {
        String[] parts = fqn.split("\\.");
        String scopeQualifier = parts.length > 0 ? parts[0] : null;
        if (parts.length < 2 || (!scopeQualifier.equals("system") && !scopeQualifier.equals("tenant"))) {
            throw new ThingsboardException("Invalid fqn!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
        TenantId tenantId;
        if ("system".equals(scopeQualifier)) {
            tenantId = TenantId.fromUUID(ModelConstants.NULL_UUID);
        } else {
            tenantId = getCurrentUser().getTenantId();
        }
        String typeFqn = fqn.substring(scopeQualifier.length() + 1);
        WidgetType widgetType = widgetTypeService.findWidgetTypeByTenantIdAndFqn(tenantId, typeFqn);
        checkNotNull(widgetType);

        accessControlService.checkPermission(getCurrentUser(), Resource.WIDGET_TYPE, Operation.READ, widgetType.getId(), widgetType);
        return widgetType;
    }

}
