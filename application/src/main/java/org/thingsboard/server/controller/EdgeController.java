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

import com.google.common.util.concurrent.ListenableFuture;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.rule.engine.flow.TbRuleChainInputNode;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeInfo;
import org.thingsboard.server.common.data.edge.EdgeInstructions;
import org.thingsboard.server.common.data.edge.EdgeSearchQuery;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportRequest;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportResult;
import org.thingsboard.server.common.msg.edge.FromEdgeSyncResponse;
import org.thingsboard.server.config.annotations.ApiOperation;
import org.thingsboard.server.exception.DataValidationException;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.EdgeBulkImportService;
import org.thingsboard.server.service.edge.instructions.EdgeInstallInstructionsService;
import org.thingsboard.server.service.edge.instructions.EdgeUpgradeInstructionsService;
import org.thingsboard.server.service.edge.rpc.EdgeRpcService;
import org.thingsboard.server.service.entitiy.edge.TbEdgeService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.thingsboard.server.controller.ControllerConstants.CUSTOMER_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.EDGE_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.EDGE_INFO_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.EDGE_TEXT_SEARCH_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.EDGE_TYPE_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_DATA_PARAMETERS;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_NUMBER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_SIZE_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.RULE_CHAIN_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_PROPERTY_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_AUTHORITY_PARAGRAPH;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH;
import static org.thingsboard.server.controller.ControllerConstants.UUID_WIKI_LINK;

/**
 * REST API for edge instance lifecycle, customer assignment, synchronization, and installation instructions.
 *
 * <p>Base path: {@code /api}.
 *
 * <p>Authorization: {@code TENANT_ADMIN} for management; {@code TENANT_ADMIN} or {@code CUSTOMER_USER} for reads;
 * all roles may check edge support availability.
 *
 * <p>Uses {@link org.thingsboard.server.service.entitiy.edge.TbEdgeService}, {@link org.thingsboard.server.service.edge.EdgeBulkImportService},
 * and optional {@link org.thingsboard.server.service.edge.rpc.EdgeRpcService} for cloud-edge sync.
 */


@RestController
@TbCoreComponent
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class EdgeController extends BaseController {

    private final EdgeBulkImportService edgeBulkImportService;
    private final TbEdgeService tbEdgeService;
    private final Optional<EdgeRpcService> edgeRpcServiceOpt;
    private final Optional<EdgeInstallInstructionsService> edgeInstallServiceOpt;
    private final Optional<EdgeUpgradeInstructionsService> edgeUpgradeServiceOpt;

    public static final String EDGE_ID = "edgeId";
    public static final String EDGE_SECURITY_CHECK = "If the user has the authority of 'Tenant Administrator', the server checks that the edge is owned by the same tenant. " +
            "If the user has the authority of 'Customer User', the server checks that the edge is assigned to the same customer.";

    /**
     * GET {@code /api/edges/enabled} — Check whether edge support is enabled on this server.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @return {@code true} if edges are enabled
     */
    @ApiOperation(value = "Is edges support enabled (isEdgesSupportEnabled)",
            notes = "Returns 'true' if edges support enabled on server, 'false' - otherwise.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/edges/enabled")
    public boolean isEdgesSupportEnabled() {
        return edgesEnabled;
    }

    /**
     * GET {@code /api/edge/{edgeId}} — Fetch an edge by id.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strEdgeId edge UUID string
     * @return the {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if the edge does not exist or access is denied
     */
    @ApiOperation(value = "Get Edge (getEdgeById)",
            notes = "Get the Edge object based on the provided Edge Id. " + EDGE_SECURITY_CHECK + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/edge/{edgeId}")
    public Edge getEdgeById(@Parameter(description = EDGE_ID_PARAM_DESCRIPTION, required = true)
                            @PathVariable(EDGE_ID) String strEdgeId) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        return checkEdgeId(edgeId, Operation.READ);
    }

    /**
     * GET {@code /api/edge/info/{edgeId}} — Fetch edge info (includes customer title) by id.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strEdgeId edge UUID string
     * @return the {@link org.thingsboard.server.common.data.edge.EdgeInfo}
     * @throws ThingsboardException if the edge does not exist
     */
    @ApiOperation(value = "Get Edge Info (getEdgeInfoById)",
            notes = "Get the Edge Info object based on the provided Edge Id. " + EDGE_SECURITY_CHECK + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/edge/info/{edgeId}")
    public EdgeInfo getEdgeInfoById(@Parameter(description = EDGE_ID_PARAM_DESCRIPTION, required = true)
                                    @PathVariable(EDGE_ID) String strEdgeId) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        return checkEdgeInfoId(edgeId, Operation.READ);
    }

    /**
     * POST {@code /api/edge} — Create or update an edge instance.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param edge JSON body with edge configuration
     * @return the saved {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws Exception if validation fails or edge template rule chain is missing on create
     */
    @ApiOperation(value = "Create Or Update Edge (saveEdge)",
            notes = "Create or update the Edge. When creating edge, platform generates Edge Id as " + UUID_WIKI_LINK +
                    "The newly created edge id will be present in the response. " +
                    "Specify existing Edge id to update the edge. " +
                    "Referencing non-existing Edge Id will cause 'Not Found' error." +
                    "\n\nEdge name is unique in the scope of tenant. Use unique identifiers like MAC or IMEI for the edge names and non-unique 'label' field for user-friendly visualization purposes." +
                    "Remove 'id', 'tenantId' and optionally 'customerId' from the request body example (below) to create new Edge entity. " +
                    TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping(value = "/edge")
    public Edge saveEdge(@Parameter(description = "A JSON value representing the edge.", required = true)
                         @RequestBody Edge edge) throws Exception {
        TenantId tenantId = getTenantId();
        edge.setTenantId(tenantId);
        boolean created = edge.getId() == null;

        RuleChain edgeTemplateRootRuleChain = null;
        if (created) {
            edgeTemplateRootRuleChain = ruleChainService.getEdgeTemplateRootRuleChain(tenantId);
            if (edgeTemplateRootRuleChain == null) {
                throw new DataValidationException("Root edge rule chain is not available!");
            }
        }

        Operation operation = created ? Operation.CREATE : Operation.WRITE;

        accessControlService.checkPermission(getCurrentUser(), Resource.EDGE, operation, edge.getId(), edge);

        return tbEdgeService.save(edge, edgeTemplateRootRuleChain, getCurrentUser());
    }

    /**
     * DELETE {@code /api/edge/{edgeId}} — Delete an edge by id.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param strEdgeId edge UUID string
     * @throws ThingsboardException if the edge does not exist
     */
    @ApiOperation(value = "Delete edge (deleteEdge)",
            notes = "Deletes the edge. Referencing non-existing edge Id will cause an error." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @DeleteMapping(value = "/edge/{edgeId}")
    public void deleteEdge(@Parameter(description = EDGE_ID_PARAM_DESCRIPTION, required = true)
                           @PathVariable(EDGE_ID) String strEdgeId) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        Edge edge = checkEdgeId(edgeId, Operation.DELETE);
        tbEdgeService.delete(edge, getCurrentUser());
    }

    /**
     * GET {@code /api/edges} — List edges owned by the tenant (paginated).
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param pageSize items per page
     * @param page zero-based page index
     * @param textSearch optional text filter
     * @param sortProperty optional sort field
     * @param sortOrder optional sort direction
     * @return a page of {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if access is denied
     */
    @ApiOperation(value = "Get Tenant Edges (getEdges)",
            notes = "Returns a page of edges owned by tenant. " +
                    PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/edges", params = {"pageSize", "page"})
    public PageData<Edge> getEdges(@Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
                                   @RequestParam int pageSize,
                                   @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
                                   @RequestParam int page,
                                   @Parameter(description = EDGE_TEXT_SEARCH_DESCRIPTION)
                                   @RequestParam(required = false) String textSearch,
                                   @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "name", "type", "label", "customerTitle"}))
                                   @RequestParam(required = false) String sortProperty,
                                   @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC", "DESC"}))
                                   @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        TenantId tenantId = getCurrentUser().getTenantId();
        return checkNotNull(edgeService.findEdgesByTenantId(tenantId, pageLink));
    }

    /**
     * POST {@code /api/customer/{customerId}/edge/{edgeId}} — Assign an edge to a customer.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param strCustomerId customer UUID string
     * @param strEdgeId edge UUID string
     * @return the updated {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if customer or edge does not exist
     */
    @ApiOperation(value = "Assign edge to customer (assignEdgeToCustomer)",
            notes = "Creates assignment of the edge to customer. Customer will be able to query edge afterwards." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping(value = "/customer/{customerId}/edge/{edgeId}")
    public Edge assignEdgeToCustomer(@Parameter(description = CUSTOMER_ID_PARAM_DESCRIPTION, required = true)
                                     @PathVariable("customerId") String strCustomerId,
                                     @Parameter(description = EDGE_ID_PARAM_DESCRIPTION, required = true)
                                     @PathVariable(EDGE_ID) String strEdgeId) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        checkParameter(EDGE_ID, strEdgeId);
        CustomerId customerId = new CustomerId(toUUID(strCustomerId));
        Customer customer = checkCustomerId(customerId, Operation.READ);
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        checkEdgeId(edgeId, Operation.ASSIGN_TO_CUSTOMER);
        return tbEdgeService.assignEdgeToCustomer(getTenantId(), edgeId, customer, getCurrentUser());
    }

    /**
     * DELETE {@code /api/customer/edge/{edgeId}} — Unassign an edge from its customer.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param strEdgeId edge UUID string
     * @return the updated {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if the edge is not assigned to a customer
     */
    @ApiOperation(value = "Unassign edge from customer (unassignEdgeFromCustomer)",
            notes = "Clears assignment of the edge to customer. Customer will not be able to query edge afterwards." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @DeleteMapping(value = "/customer/edge/{edgeId}")
    public Edge unassignEdgeFromCustomer(@Parameter(description = EDGE_ID_PARAM_DESCRIPTION, required = true)
                                         @PathVariable(EDGE_ID) String strEdgeId) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        Edge edge = checkEdgeId(edgeId, Operation.UNASSIGN_FROM_CUSTOMER);
        if (edge.getCustomerId() == null || edge.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
            throw new IncorrectParameterException("Edge isn't assigned to any customer!");
        }
        Customer customer = checkCustomerId(edge.getCustomerId(), Operation.READ);

        return tbEdgeService.unassignEdgeFromCustomer(edge, customer, getCurrentUser());
    }

    /**
     * POST {@code /api/customer/public/edge/{edgeId}} — Assign an edge to the public customer.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param strEdgeId edge UUID string
     * @return the updated {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if the edge does not exist
     */
    @ApiOperation(value = "Make edge publicly available (assignEdgeToPublicCustomer)",
            notes = "Edge will be available for non-authorized (not logged-in) users. " +
                    "This is useful to create dashboards that you plan to share/embed on a publicly available website. " +
                    "However, users that are logged-in and belong to different tenant will not be able to access the edge." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping(value = "/customer/public/edge/{edgeId}")
    public Edge assignEdgeToPublicCustomer(@Parameter(description = EDGE_ID_PARAM_DESCRIPTION, required = true)
                                           @PathVariable(EDGE_ID) String strEdgeId) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        checkEdgeId(edgeId, Operation.ASSIGN_TO_CUSTOMER);
        return tbEdgeService.assignEdgeToPublicCustomer(getTenantId(), edgeId, getCurrentUser());
    }

    /**
     * GET {@code /api/tenant/edges} — List tenant edges with optional type filter.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param pageSize items per page
     * @param page zero-based page index
     * @param type optional edge type filter
     * @param textSearch optional text filter
     * @param sortProperty optional sort field
     * @param sortOrder optional sort direction
     * @return a page of {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if access is denied
     */
    @ApiOperation(value = "Get Tenant Edges (getTenantEdges)",
            notes = "Returns a page of edges owned by tenant. " +
                    PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/tenant/edges", params = {"pageSize", "page"})
    public PageData<Edge> getTenantEdges(
            @Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @Parameter(description = EDGE_TYPE_DESCRIPTION)
            @RequestParam(required = false) String type,
            @Parameter(description = EDGE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "name", "type", "label", "customerTitle"}))
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC", "DESC"}))
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        if (type != null && !type.trim().isEmpty()) {
            return checkNotNull(edgeService.findEdgesByTenantIdAndType(tenantId, type, pageLink));
        } else {
            return checkNotNull(edgeService.findEdgesByTenantId(tenantId, pageLink));
        }
    }

    /**
     * GET {@code /api/tenant/edgeInfos} — List tenant edge info objects with optional type filter.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param pageSize items per page
     * @param page zero-based page index
     * @param type optional edge type filter
     * @param textSearch optional text filter
     * @param sortProperty optional sort field
     * @param sortOrder optional sort direction
     * @return a page of {@link org.thingsboard.server.common.data.edge.EdgeInfo}
     * @throws ThingsboardException if access is denied
     */
    @ApiOperation(value = "Get Tenant Edge Infos (getTenantEdgeInfos)",
            notes = "Returns a page of edges info objects owned by tenant. " +
                    PAGE_DATA_PARAMETERS + EDGE_INFO_DESCRIPTION + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/tenant/edgeInfos")
    public PageData<EdgeInfo> getTenantEdgeInfos(
            @Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @Parameter(description = EDGE_TYPE_DESCRIPTION)
            @RequestParam(required = false) String type,
            @Parameter(description = EDGE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "name", "type", "label", "customerTitle"}))
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC", "DESC"}))
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        if (type != null && !type.trim().isEmpty()) {
            return checkNotNull(edgeService.findEdgeInfosByTenantIdAndType(tenantId, type, pageLink));
        } else {
            return checkNotNull(edgeService.findEdgeInfosByTenantId(tenantId, pageLink));
        }
    }

    /**
     * GET {@code /api/tenant/edges?edgeName=} — Internal helper to fetch edge by name (hidden API).
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param edgeName unique edge name within the tenant
     * @return the {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if not found
     */
    @Hidden
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/tenant/edges", params = {"edgeName"})
    public Edge getTenantEdge(@RequestParam String edgeName) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        return checkNotNull(edgeService.findEdgeByTenantIdAndName(tenantId, edgeName));
    }

    /**
     * GET {@code /api/tenant/edge?edgeName=} — Fetch a tenant edge by unique name.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param edgeName unique edge name
     * @return the {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if not found
     */
    @ApiOperation(value = "Get Tenant Edge by name (getTenantEdgeByName)",
            notes = "Requested edge must be owned by tenant or customer that the user belongs to. " +
                    "Edge name is an unique property of edge. So it can be used to identify the edge." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/tenant/edge")
    public Edge getTenantEdgeByName(@Parameter(description = "Unique name of the edge", required = true)
                                    @RequestParam String edgeName) throws ThingsboardException {
        return getTenantEdge(edgeName);
    }

    /**
     * POST {@code /api/edge/{edgeId}/{ruleChainId}/root} — Set the root rule chain for an edge.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param strEdgeId edge UUID string
     * @param strRuleChainId rule chain UUID string
     * @return the updated {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws Exception if edge or rule chain validation fails
     */
    @ApiOperation(value = "Set root rule chain for provided edge (setEdgeRootRuleChain)",
            notes = "Change root rule chain of the edge to the new provided rule chain. \n" +
                    "This operation will send a notification to update root rule chain on remote edge service." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @PostMapping(value = "/edge/{edgeId}/{ruleChainId}/root")
    public Edge setEdgeRootRuleChain(@Parameter(description = EDGE_ID_PARAM_DESCRIPTION, required = true)
                                     @PathVariable(EDGE_ID) String strEdgeId,
                                     @Parameter(description = RULE_CHAIN_ID_PARAM_DESCRIPTION, required = true)
                                     @PathVariable("ruleChainId") String strRuleChainId) throws Exception {
        checkParameter(EDGE_ID, strEdgeId);
        checkParameter("ruleChainId", strRuleChainId);
        RuleChainId ruleChainId = new RuleChainId(toUUID(strRuleChainId));
        checkRuleChain(ruleChainId, Operation.WRITE);
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        Edge edge = checkEdgeId(edgeId, Operation.WRITE);
        accessControlService.checkPermission(getCurrentUser(), Resource.EDGE, Operation.WRITE, edge.getId(), edge);
        return tbEdgeService.setEdgeRootRuleChain(edge, ruleChainId, getCurrentUser());
    }

    /**
     * GET {@code /api/customer/{customerId}/edges} — List edges assigned to a customer.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strCustomerId customer UUID string
     * @param pageSize items per page
     * @param page zero-based page index
     * @param type optional edge type filter
     * @param textSearch optional text filter
     * @param sortProperty optional sort field
     * @param sortOrder optional sort direction
     * @return a page of {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if customer access is denied
     */
    @ApiOperation(value = "Get Customer Edges (getCustomerEdges)",
            notes = "Returns a page of edges objects assigned to customer. " +
                    PAGE_DATA_PARAMETERS + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/customer/{customerId}/edges")
    public PageData<Edge> getCustomerEdges(
            @Parameter(description = CUSTOMER_ID_PARAM_DESCRIPTION)
            @PathVariable("customerId") String strCustomerId,
            @Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @Parameter(description = EDGE_TYPE_DESCRIPTION)
            @RequestParam(required = false) String type,
            @Parameter(description = EDGE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "name", "type", "label", "customerTitle"}))
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC", "DESC"}))
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        CustomerId customerId = new CustomerId(toUUID(strCustomerId));
        checkCustomerId(customerId, Operation.READ);
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        PageData<Edge> result;
        if (type != null && !type.trim().isEmpty()) {
            result = edgeService.findEdgesByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink);
        } else {
            result = edgeService.findEdgesByTenantIdAndCustomerId(tenantId, customerId, pageLink);
        }
        return checkNotNull(result);
    }

    /**
     * GET {@code /api/customer/{customerId}/edgeInfos} — List edge info objects for a customer.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strCustomerId customer UUID string
     * @param pageSize items per page
     * @param page zero-based page index
     * @param type optional edge type filter
     * @param textSearch optional text filter
     * @param sortProperty optional sort field
     * @param sortOrder optional sort direction
     * @return a page of {@link org.thingsboard.server.common.data.edge.EdgeInfo}
     * @throws ThingsboardException if customer access is denied
     */
    @ApiOperation(value = "Get Customer Edge Infos (getCustomerEdgeInfos)",
            notes = "Returns a page of edges info objects assigned to customer. " +
                    PAGE_DATA_PARAMETERS + EDGE_INFO_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/customer/{customerId}/edgeInfos")
    public PageData<EdgeInfo> getCustomerEdgeInfos(
            @Parameter(description = CUSTOMER_ID_PARAM_DESCRIPTION)
            @PathVariable("customerId") String strCustomerId,
            @Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @Parameter(description = EDGE_TYPE_DESCRIPTION)
            @RequestParam(required = false) String type,
            @Parameter(description = EDGE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "name", "type", "label", "customerTitle"}))
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC", "DESC"}))
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        CustomerId customerId = new CustomerId(toUUID(strCustomerId));
        checkCustomerId(customerId, Operation.READ);
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        PageData<EdgeInfo> result;
        if (type != null && !type.trim().isEmpty()) {
            result = edgeService.findEdgeInfosByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink);
        } else {
            result = edgeService.findEdgeInfosByTenantIdAndCustomerId(tenantId, customerId, pageLink);
        }
        return checkNotNull(result);
    }

    /**
     * GET {@code /api/edges?edgeIds=} — Internal helper to fetch edges by id list (hidden API).
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strEdgeIds array of edge UUID strings
     * @return list of {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if any edge is inaccessible
     * @throws java.util.concurrent.ExecutionException if async lookup fails
     * @throws InterruptedException if the async lookup is interrupted
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/edges", params = {"edgeIds"})
    public List<Edge> getEdgesByIds(
            @RequestParam("edgeIds") String[] strEdgeIds) throws ThingsboardException, ExecutionException, InterruptedException {
        checkArrayParameter("edgeIds", strEdgeIds);
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        CustomerId customerId = user.getCustomerId();
        List<EdgeId> edgeIds = new ArrayList<>();
        for (String strEdgeId : strEdgeIds) {
            edgeIds.add(new EdgeId(toUUID(strEdgeId)));
        }
        ListenableFuture<List<Edge>> edgesFuture;
        if (customerId == null || customerId.isNullUid()) {
            edgesFuture = edgeService.findEdgesByTenantIdAndIdsAsync(tenantId, edgeIds);
        } else {
            edgesFuture = edgeService.findEdgesByTenantIdCustomerIdAndIdsAsync(tenantId, customerId, edgeIds);
        }
        List<Edge> edges = edgesFuture.get();
        return checkNotNull(edges);
    }

    /**
     * GET {@code /api/edges/list} — Fetch multiple edges by id list.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strEdgeIds array of edge UUID strings
     * @return list of {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if any edge is inaccessible
     * @throws java.util.concurrent.ExecutionException if async lookup fails
     * @throws InterruptedException if the async lookup is interrupted
     */
    @ApiOperation(value = "Get Edges By Ids (getEdgeList)",
            notes = "Requested edges must be owned by tenant or assigned to customer which user is performing the request." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/edges/list")
    public List<Edge> getEdgeList(
            @Parameter(description = "A list of edges ids, separated by comma ','", array = @ArraySchema(schema = @Schema(type = "string")), required = true)
            @RequestParam("edgeIds") String[] strEdgeIds) throws ThingsboardException, ExecutionException, InterruptedException {
        return getEdgesByIds(strEdgeIds);
    }

    /**
     * POST {@code /api/edges} — Find edges related to an entity via {@link org.thingsboard.server.common.data.edge.EdgeSearchQuery}.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param query search query with entity id, relation type, and depth
     * @return list of matching {@link org.thingsboard.server.common.data.edge.Edge}
     * @throws ThingsboardException if the query is invalid
     * @throws java.util.concurrent.ExecutionException if async search fails
     * @throws InterruptedException if the search is interrupted
     */
    @ApiOperation(value = "Find related edges (findEdgesByQuery)",
            notes = "Returns all edges that are related to the specific entity. " +
                    "The entity id, relation type, edge types, depth of the search, and other query parameters defined using complex 'EdgeSearchQuery' object. " +
                    "See 'Model' tab of the Parameters for more info." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @PostMapping(value = "/edges")
    public List<Edge> findEdgesByQuery(@RequestBody EdgeSearchQuery query) throws ThingsboardException, ExecutionException, InterruptedException {
        checkNotNull(query);
        checkNotNull(query.getParameters());
        checkNotNull(query.getEdgeTypes());
        checkEntityId(query.getParameters().getEntityId(), Operation.READ);
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        List<Edge> edges = checkNotNull(edgeService.findEdgesByQuery(tenantId, query).get());
        edges = edges.stream().filter(edge -> {
            try {
                accessControlService.checkPermission(user, Resource.EDGE, Operation.READ, edge.getId(), edge);
                return true;
            } catch (ThingsboardException e) {
                return false;
            }
        }).collect(Collectors.toList());
        return edges;
    }

    /**
     * GET {@code /api/edge/types} — Return distinct edge types for the tenant.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @return list of {@link org.thingsboard.server.common.data.EntitySubtype}
     * @throws ThingsboardException if access is denied
     * @throws java.util.concurrent.ExecutionException if async lookup fails
     * @throws InterruptedException if the lookup is interrupted
     */
    @ApiOperation(value = "Get Edge Types (getEdgeTypes)",
            notes = "Returns a set of unique edge types based on edges that are either owned by the tenant or assigned to the customer which user is performing the request."
                    + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/edge/types")
    public List<EntitySubtype> getEdgeTypes() throws ThingsboardException, ExecutionException, InterruptedException {
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        ListenableFuture<List<EntitySubtype>> edgeTypes = edgeService.findEdgeTypesByTenantId(tenantId);
        return checkNotNull(edgeTypes.get());
    }

    /**
     * POST {@code /api/edge/sync/{edgeId}} — Start cloud-to-edge entity synchronization.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param strEdgeId edge UUID string
     * @return deferred HTTP response completed when sync finishes
     * @throws ThingsboardException if edges are disabled or edge id is invalid
     */
    @ApiOperation(value = "Sync edge (syncEdge)",
            notes = "Starts synchronization process between edge and cloud. \n" +
                    "All entities that are assigned to particular edge are going to be send to remote edge service." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping(value = "/edge/sync/{edgeId}")
    public DeferredResult<ResponseEntity> syncEdge(@Parameter(description = EDGE_ID_PARAM_DESCRIPTION, required = true)
                                                   @PathVariable("edgeId") String strEdgeId) throws ThingsboardException {
        checkParameter("edgeId", strEdgeId);
        final DeferredResult<ResponseEntity> response = new DeferredResult<>();
        if (isEdgesEnabled() && edgeRpcServiceOpt.isPresent()) {
            EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
            edgeId = checkNotNull(edgeId);
            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();
            edgeRpcServiceOpt.get().processSyncRequest(tenantId, edgeId, fromEdgeSyncResponse -> reply(response, fromEdgeSyncResponse));
        } else {
            throw new ThingsboardException("Edges support disabled", ThingsboardErrorCode.GENERAL);
        }
        return response;
    }

    private void reply(DeferredResult<ResponseEntity> response, FromEdgeSyncResponse fromEdgeSyncResponse) {
        if (fromEdgeSyncResponse.isSuccess()) {
            response.setResult(new ResponseEntity<>(HttpStatus.OK));
        } else {
            response.setErrorResult(new ThingsboardException(fromEdgeSyncResponse.getError(), ThingsboardErrorCode.GENERAL));
        }
    }

    /**
     * GET {@code /api/edge/missingToRelatedRuleChains/{edgeId}} — Find rule chain ids referenced but not assigned to edge.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param strEdgeId edge UUID string
     * @return JSON string with missing rule chain ids
     * @throws ThingsboardException if the edge does not exist
     */
    @ApiOperation(value = "Find missing rule chains (findMissingToRelatedRuleChains)",
            notes = "Returns list of rule chains ids that are not assigned to particular edge, but these rule chains are present in the already assigned rule chains to edge." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/edge/missingToRelatedRuleChains/{edgeId}")
    public String findMissingToRelatedRuleChains(@Parameter(description = EDGE_ID_PARAM_DESCRIPTION, required = true)
                                                 @PathVariable("edgeId") String strEdgeId) throws ThingsboardException {
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        edgeId = checkNotNull(edgeId);
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        return edgeService.findMissingToRelatedRuleChains(tenantId, edgeId, TbRuleChainInputNode.class.getName());
    }

    /**
     * POST {@code /api/edge/bulk_import} — Bulk import edges from CSV.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param request bulk import request with CSV payload
     * @return {@link org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportResult}
     * @throws Exception if import validation fails or edge template rule chain is missing
     */
    @ApiOperation(value = "Import the bulk of edges (processEdgesBulkImport)",
            notes = "There's an ability to import the bulk of edges using the only .csv file." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @PostMapping("/edge/bulk_import")
    public BulkImportResult<Edge> processEdgesBulkImport(@RequestBody BulkImportRequest request) throws Exception {
        SecurityUser user = getCurrentUser();
        RuleChain edgeTemplateRootRuleChain = ruleChainService.getEdgeTemplateRootRuleChain(user.getTenantId());
        if (edgeTemplateRootRuleChain == null) {
            throw new DataValidationException("Root edge rule chain is not available!");
        }

        return edgeBulkImportService.processBulkImport(request, user);
    }

    /**
     * GET {@code /api/edge/instructions/install/{edgeId}/{method}} — Return edge installation instructions.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param strEdgeId edge UUID string
     * @param installationMethod install method ({@code docker}, {@code ubuntu}, or {@code centos})
     * @param request HTTP request used to build instruction URLs
     * @return {@link org.thingsboard.server.common.data.edge.EdgeInstructions}
     * @throws ThingsboardException if edges are disabled or edge does not exist
     */
    @ApiOperation(value = "Get Edge Install Instructions (getEdgeInstallInstructions)",
            notes = "Get an install instructions for provided edge id." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/edge/instructions/install/{edgeId}/{method}")
    public EdgeInstructions getEdgeInstallInstructions(
            @Parameter(description = EDGE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable("edgeId") String strEdgeId,
            @Parameter(description = "Installation method ('docker', 'ubuntu' or 'centos')", schema = @Schema(allowableValues = {"docker", "ubuntu", "centos"}))
            @PathVariable("method") String installationMethod,
            HttpServletRequest request) throws ThingsboardException {
        if (isEdgesEnabled() && edgeInstallServiceOpt.isPresent()) {
            EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
            edgeId = checkNotNull(edgeId);
            Edge edge = checkEdgeId(edgeId, Operation.READ);
            return checkNotNull(edgeInstallServiceOpt.get().getInstallInstructions(edge, installationMethod, request));
        } else {
            throw new ThingsboardException("Edges support disabled", ThingsboardErrorCode.GENERAL);
        }
    }

    /**
     * GET {@code /api/edge/instructions/upgrade/{edgeVersion}/{method}} — Return edge upgrade instructions.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param edgeVersion target edge version
     * @param method upgrade method ({@code docker}, {@code ubuntu}, or {@code centos})
     * @return {@link org.thingsboard.server.common.data.edge.EdgeInstructions}
     * @throws Exception if edges are disabled
     */
    @ApiOperation(value = "Get Edge Upgrade Instructions (getEdgeUpgradeInstructions)",
            notes = "Get an upgrade instructions for provided edge version." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/edge/instructions/upgrade/{edgeVersion}/{method}")
    public EdgeInstructions getEdgeUpgradeInstructions(
            @Parameter(description = "Edge version", required = true)
            @PathVariable("edgeVersion") String edgeVersion,
            @Parameter(description = "Upgrade method ('docker', 'ubuntu' or 'centos')", schema = @Schema(allowableValues = {"docker", "ubuntu", "centos"}))
            @PathVariable("method") String method) throws Exception {
        if (isEdgesEnabled() && edgeUpgradeServiceOpt.isPresent()) {
            return checkNotNull(edgeUpgradeServiceOpt.get().getUpgradeInstructions(edgeVersion, method));
        } else {
            throw new ThingsboardException("Edges support disabled", ThingsboardErrorCode.GENERAL);
        }
    }

    /**
     * GET {@code /api/edge/{edgeId}/upgrade/available} — Check whether upgrade is available for a connected edge.
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     * @param strEdgeId edge UUID string
     * @return {@code true} if upgrade is available
     * @throws Exception if edges are disabled or edge does not exist
     */
    @ApiOperation(value = "Is edge upgrade enabled (isEdgeUpgradeAvailable)",
            notes = "Returns 'true' if upgrade available for connected edge, 'false' - otherwise.")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/edge/{edgeId}/upgrade/available")
    public boolean isEdgeUpgradeAvailable(
            @Parameter(description = EDGE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable("edgeId") String strEdgeId) throws Exception {
        if (isEdgesEnabled() && edgeUpgradeServiceOpt.isPresent()) {
            EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
            edgeId = checkNotNull(edgeId);
            Edge edge = checkEdgeId(edgeId, Operation.READ);
            return edgeUpgradeServiceOpt.get().isUpgradeAvailable(edge.getTenantId(), edge.getId());
        } else {
            throw new ThingsboardException("Edges support disabled", ThingsboardErrorCode.GENERAL);
        }
    }

}
