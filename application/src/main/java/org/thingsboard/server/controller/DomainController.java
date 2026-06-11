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

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.domain.Domain;
import org.thingsboard.server.common.data.domain.DomainInfo;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DomainId;
import org.thingsboard.server.common.data.id.OAuth2ClientId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.config.annotations.ApiOperation;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.domain.TbDomainService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.controller.ControllerConstants.PAGE_NUMBER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_SIZE_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_PROPERTY_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SYSTEM_AUTHORITY_PARAGRAPH;
import static org.thingsboard.server.controller.ControllerConstants.UUID_WIKI_LINK;

/**
 * REST API for platform-wide domain configuration used in white-labeling and OAuth2 client routing.
 *
 * <p>Base path: {@code /api}.
 *
 * <p>Authorization: {@code SYS_ADMIN} only.
 *
 * <p>Delegates create/update/delete operations to {@link org.thingsboard.server.service.entitiy.domain.TbDomainService}
 * and reads domain data via inherited {@code domainService} from {@link BaseController}.
 */
@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class DomainController extends BaseController {

    private final TbDomainService tbDomainService;

    /**
     * POST {@code /api/domain} — Create or update a domain and optionally associate OAuth2 clients.
     *
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}.
     *
     * @param domain        JSON body with domain configuration
     * @param ids           optional OAuth2 client registration UUIDs to link to the domain
     * @return the saved {@link Domain}
     * @throws Exception if validation fails or the referenced domain does not exist
     */
    @ApiOperation(value = "Save or Update Domain (saveDomain)",
            notes = "Create or update the Domain. When creating domain, platform generates Domain Id as " + UUID_WIKI_LINK +
                    "The newly created Domain Id will be present in the response. " +
                    "Specify existing Domain Id to update the domain. " +
                    "Referencing non-existing Domain Id will cause 'Not Found' error." +
                    "\n\nDomain name is unique for entire platform setup.\n\n" + SYSTEM_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @PostMapping(value = "/domain")
    public Domain saveDomain(
            @Parameter(description = "A JSON value representing the Domain.", required = true)
            @RequestBody @Valid Domain domain,
            @Parameter(description = "A list of oauth2 client registration ids, separated by comma ','", array = @ArraySchema(schema = @Schema(type = "string")))
            @RequestParam(name = "oauth2ClientIds", required = false) UUID[] ids) throws Exception {
        domain.setTenantId(getTenantId());
        checkEntity(domain.getId(), domain, Resource.DOMAIN);
        return tbDomainService.save(domain, getOAuth2ClientIds(ids), getCurrentUser());
    }

    /**
     * PUT {@code /api/domain/{id}/oauth2Clients} — Replace OAuth2 clients linked to a domain.
     *
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}.
     *
     * @param id        domain UUID
     * @param clientIds array of OAuth2 client UUIDs to assign
     * @throws ThingsboardException if the domain does not exist or the caller lacks permission
     */
    @ApiOperation(value = "Update oauth2 clients (updateDomainOauth2Clients)",
            notes = "Update oauth2 clients for the specified domain. ")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @PutMapping(value = "/domain/{id}/oauth2Clients")
    public void updateDomainOauth2Clients(@PathVariable UUID id,
                                          @RequestBody UUID[] clientIds) throws ThingsboardException {
        DomainId domainId = new DomainId(id);
        Domain domain = checkDomainId(domainId, Operation.WRITE);
        List<OAuth2ClientId> oAuth2ClientIds = getOAuth2ClientIds(clientIds);
        tbDomainService.updateOauth2Clients(domain, oAuth2ClientIds, getCurrentUser());
    }

    /**
     * GET {@code /api/domain/infos} — List domain info objects with pagination and text search.
     *
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}.
     *
     * @param pageSize     number of items per page
     * @param page         zero-based page index
     * @param textSearch   optional case-insensitive substring filter on domain name
     * @param sortProperty optional property to sort by
     * @param sortOrder    optional sort direction ({@code ASC} or {@code DESC})
     * @return a page of {@link DomainInfo} records
     * @throws ThingsboardException if the caller lacks domain read permission
     */
    @ApiOperation(value = "Get Domain infos (getDomainInfos)", notes = SYSTEM_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @GetMapping(value = "/domain/infos")
    public PageData<DomainInfo> getDomainInfos(@Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
                                               @RequestParam int pageSize,
                                               @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
                                               @RequestParam int page,
                                               @Parameter(description = "Case-insensitive 'substring' filter based on domain's name")
                                               @RequestParam(required = false) String textSearch,
                                               @Parameter(description = SORT_PROPERTY_DESCRIPTION)
                                               @RequestParam(required = false) String sortProperty,
                                               @Parameter(description = SORT_ORDER_DESCRIPTION)
                                               @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        accessControlService.checkPermission(getCurrentUser(), Resource.DOMAIN, Operation.READ);
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return domainService.findDomainInfosByTenantId(getTenantId(), pageLink);
    }

    /**
     * GET {@code /api/domain/info/{id}} — Fetch a single domain info object by id.
     *
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}.
     *
     * @param id domain UUID
     * @return the {@link DomainInfo} for the given id
     * @throws ThingsboardException if the domain does not exist or access is denied
     */
    @ApiOperation(value = "Get Domain info by Id (getDomainInfoById)", notes = SYSTEM_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @GetMapping(value = "/domain/info/{id}")
    public DomainInfo getDomainInfoById(@PathVariable UUID id) throws ThingsboardException {
        DomainId domainId = new DomainId(id);
        return checkEntityId(domainId, domainService::findDomainInfoById, Operation.READ);
    }

    /**
     * DELETE {@code /api/domain/{id}} — Delete a domain by id.
     *
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}.
     *
     * @param id domain UUID to delete
     * @throws Exception if the domain does not exist or deletion fails
     */
    @ApiOperation(value = "Delete Domain by ID (deleteDomain)",
            notes = "Deletes Domain by ID. Referencing non-existing domain Id will cause an error." + SYSTEM_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @DeleteMapping(value = "/domain/{id}")
    public void deleteDomain(@PathVariable UUID id) throws Exception {
        DomainId domainId = new DomainId(id);
        Domain domain = checkDomainId(domainId, Operation.DELETE);
        tbDomainService.delete(domain, getCurrentUser());
    }

}
