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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityIdFactory;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntityRelationInfo;
import org.thingsboard.server.common.data.relation.EntityRelationsQuery;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.config.annotations.ApiOperation;
import org.thingsboard.server.dao.service.ConstraintValidator;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.entity.relation.TbEntityRelationService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.thingsboard.server.controller.ControllerConstants.ENTITY_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.ENTITY_TYPE_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.RELATION_INFO_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.RELATION_TYPE_GROUP_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.RELATION_TYPE_PARAM_DESCRIPTION;

/**
 * REST API for creating, deleting, and querying directed relations between platform entities.
 *
 * <p>Base path: {@code /api}.
 *
 * <p>Authorization: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, or {@code CUSTOMER_USER} with entity-level checks.
 *
 * <p>Uses {@link org.thingsboard.server.service.entitiy.entity.relation.TbEntityRelationService} and inherited {@code relationService}.
 */


@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
public class EntityRelationController extends BaseController {

    private final TbEntityRelationService tbEntityRelationService;

    public static final String TO_TYPE = "toType";
    public static final String FROM_ID = "fromId";
    public static final String FROM_TYPE = "fromType";
    public static final String RELATION_TYPE = "relationType";
    public static final String TO_ID = "toId";

    private static final String SECURITY_CHECKS_ENTITIES_DESCRIPTION = "\n\nIf the user has the authority of 'System Administrator', the server checks that 'from' and 'to' entities are owned by the sysadmin. " +
            "If the user has the authority of 'Tenant Administrator', the server checks that 'from' and 'to' entities are owned by the same tenant. " +
            "If the user has the authority of 'Customer User', the server checks that the 'from' and 'to' entities are assigned to the same customer.";

    private static final String SECURITY_CHECKS_ENTITY_DESCRIPTION = "\n\nIf the user has the authority of 'System Administrator', the server checks that the entity is owned by the sysadmin. " +
            "If the user has the authority of 'Tenant Administrator', the server checks that the entity is owned by the same tenant. " +
            "If the user has the authority of 'Customer User', the server checks that the entity is assigned to the same customer.";

    /**
     * POST {@code /api/relation} — Legacy create/update relation endpoint (hidden, void response).
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param relation JSON relation body
     * @throws ThingsboardException if entities are inaccessible
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @PostMapping(value = "/relation")
    public void saveRelationV1(@Parameter(description = "A JSON value representing the relation.", required = true)
                             @RequestBody EntityRelation relation) throws ThingsboardException {
        doSave(relation);
    }

    /**
     * POST {@code /api/v2/relation} — Create or update a relation between two entities.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param relation JSON relation body
     * @return the saved {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if endpoint entities fail access checks
     */
    @ApiOperation(value = "Create Relation (saveRelation)",
            notes = "Creates or updates a relation between two entities in the platform. " +
                    "Relations unique key is a combination of from/to entity id and relation type group and relation type. " +
                    SECURITY_CHECKS_ENTITIES_DESCRIPTION)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @PostMapping(value = "/v2/relation")
    public EntityRelation saveRelation(@Parameter(description = "A JSON value representing the relation.", required = true)
                                                @RequestBody EntityRelation relation) throws ThingsboardException {
        return doSave(relation);
    }

    private EntityRelation doSave(EntityRelation relation) throws ThingsboardException {
        if (relation.getTypeGroup() == null) {
            relation.setTypeGroup(RelationTypeGroup.COMMON);
        }
        ConstraintValidator.validateFields(relation);
        checkCanCreateRelation(relation.getFrom());
        checkCanCreateRelation(relation.getTo());
        return tbEntityRelationService.save(getTenantId(), getCurrentUser().getCustomerId(), relation, getCurrentUser());
    }

    /**
     * DELETE {@code /api/relation} — Legacy delete relation endpoint (hidden, query params).
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strFromId from-entity id
     * @param strFromType from-entity type
     * @param strRelationType relation type name
     * @param strRelationTypeGroup optional relation type group
     * @param strToId to-entity id
     * @param strToType to-entity type
     * @throws ThingsboardException if relation or entities are invalid
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @DeleteMapping(value = "/relation")
    public void deleteRelationV1(@Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @RequestParam(FROM_ID) String strFromId,
                               @Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(FROM_TYPE) String strFromType,
                               @Parameter(description = RELATION_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(RELATION_TYPE) String strRelationType,
                               @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION) @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup,
                               @Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @RequestParam(TO_ID) String strToId,
                               @Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(TO_TYPE) String strToType) throws ThingsboardException {
        doDelete(strFromId, strFromType, strRelationType, strRelationTypeGroup, strToId, strToType);
    }

    /**
     * DELETE {@code /api/v2/relation} — Delete a relation identified by from/to entities and type.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strFromId from-entity id
     * @param strFromType from-entity type
     * @param strRelationType relation type name
     * @param strRelationTypeGroup optional relation type group
     * @param strToId to-entity id
     * @param strToType to-entity type
     * @return the deleted {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if relation does not exist
     */
    @ApiOperation(value = "Delete Relation (deleteRelation)",
            notes = "Deletes a relation between two entities in the platform. " + SECURITY_CHECKS_ENTITIES_DESCRIPTION)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @DeleteMapping(value = "/v2/relation")
    public EntityRelation deleteRelation(@Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @RequestParam(FROM_ID) String strFromId,
                                         @Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(FROM_TYPE) String strFromType,
                                         @Parameter(description = RELATION_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(RELATION_TYPE) String strRelationType,
                                         @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION) @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup,
                                         @Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @RequestParam(TO_ID) String strToId,
                                         @Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(TO_TYPE) String strToType) throws ThingsboardException {
        return doDelete(strFromId, strFromType, strRelationType, strRelationTypeGroup, strToId, strToType);
    }

    private EntityRelation doDelete(String strFromId, String strFromType, String strRelationType, String strRelationTypeGroup, String strToId, String strToType) throws ThingsboardException {
        checkParameter(FROM_ID, strFromId);
        checkParameter(FROM_TYPE, strFromType);
        checkParameter(TO_ID, strToId);
        checkParameter(TO_TYPE, strToType);
        EntityId fromId = EntityIdFactory.getByTypeAndId(strFromType, strFromId);
        EntityId toId = EntityIdFactory.getByTypeAndId(strToType, strToId);
        checkCanCreateRelation(fromId);
        checkCanCreateRelation(toId);

        RelationTypeGroup relationTypeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
        EntityRelation relation = new EntityRelation(fromId, toId, strRelationType, relationTypeGroup);
        return tbEntityRelationService.delete(getTenantId(), getCurrentUser().getCustomerId(), relation, getCurrentUser());
    }

    /**
     * DELETE {@code /api/relations} — Delete all COMMON-group relations for an entity (both directions).
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strId entity id
     * @param strType entity type
     * @throws ThingsboardException if entity write access is denied
     */
    @ApiOperation(value = "Delete common relations (deleteRelations)",
            notes = "Deletes all the relations ('from' and 'to' direction) for the specified entity and relation type group: 'COMMON'. " +
                    SECURITY_CHECKS_ENTITY_DESCRIPTION)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','TENANT_ADMIN', 'CUSTOMER_USER')")
    @DeleteMapping(value = "/relations")
    public void deleteRelations(@Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @RequestParam("entityId") String strId,
                                @Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam("entityType") String strType) throws ThingsboardException {
        checkParameter("entityId", strId);
        checkParameter("entityType", strType);
        EntityId entityId = EntityIdFactory.getByTypeAndId(strType, strId);
        checkEntityId(entityId, Operation.WRITE);
        tbEntityRelationService.deleteCommonRelations(getTenantId(), getCurrentUser().getCustomerId(), entityId, getCurrentUser());
    }

    /**
     * GET {@code /api/relation} — Fetch a single relation between two entities.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strFromId from-entity id
     * @param strFromType from-entity type
     * @param strRelationType relation type
     * @param strRelationTypeGroup optional relation type group
     * @param strToId to-entity id
     * @param strToType to-entity type
     * @return the {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if relation is not found
     */
    @ApiOperation(value = "Get Relation (getRelation)",
            notes = "Returns relation object between two specified entities if present. Otherwise throws exception. " + SECURITY_CHECKS_ENTITIES_DESCRIPTION)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relation")
    public EntityRelation getRelation(@Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @RequestParam(FROM_ID) String strFromId,
                                      @Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(FROM_TYPE) String strFromType,
                                      @Parameter(description = RELATION_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(RELATION_TYPE) String strRelationType,
                                      @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION) @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup,
                                      @Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @RequestParam(TO_ID) String strToId,
                                      @Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(TO_TYPE) String strToType) throws ThingsboardException {
        checkParameter(FROM_ID, strFromId);
        checkParameter(FROM_TYPE, strFromType);
        checkParameter(TO_ID, strToId);
        checkParameter(TO_TYPE, strToType);
        EntityId fromId = EntityIdFactory.getByTypeAndId(strFromType, strFromId);
        EntityId toId = EntityIdFactory.getByTypeAndId(strToType, strToId);
        checkEntityId(fromId, Operation.READ);
        checkEntityId(toId, Operation.READ);
        RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
        return checkNotNull(relationService.getRelation(getTenantId(), fromId, toId, strRelationType, typeGroup));
    }

    /**
     * GET {@code /api/relations?fromId=&fromType=} — Hidden legacy endpoint: outbound relations from entity.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strFromId from-entity id query param
     * @param strFromType from-entity type query param
     * @param strRelationTypeGroup optional relation type group
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if entity read access is denied
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations", params = {FROM_ID, FROM_TYPE})
    public List<EntityRelation> findByFrom(@RequestParam(FROM_ID) String strFromId,
                                           @RequestParam(FROM_TYPE) String strFromType,
                                           @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException {
        checkParameter(FROM_ID, strFromId);
        checkParameter(FROM_TYPE, strFromType);
        EntityId entityId = EntityIdFactory.getByTypeAndId(strFromType, strFromId);
        checkEntityId(entityId, Operation.READ);
        RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
        return checkNotNull(filterRelationsByReadPermission(relationService.findByFrom(getTenantId(), entityId, typeGroup)));
    }

    /**
     * GET {@code /api/relations/from/{fromType}/{fromId}} — List outbound relations from an entity.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strFromType from-entity type path variable
     * @param strFromId from-entity id path variable
     * @param strRelationTypeGroup optional relation type group query param
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if entity read access is denied
     */
    @ApiOperation(value = "Get List of Relations (findEntityRelationsByFrom)",
            notes = "Returns list of relation objects for the specified entity by the 'from' direction. " +
                    SECURITY_CHECKS_ENTITY_DESCRIPTION)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations/from/{fromType}/{fromId}")
    public List<EntityRelation> findEntityRelationsByFrom(@Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @PathVariable(FROM_TYPE) String strFromType,
                                                          @Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @PathVariable(FROM_ID) String strFromId,
                                                          @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION)
                                                          @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException {
        return findByFrom(strFromId, strFromType, strRelationTypeGroup);
    }

    /**
     * GET {@code /api/relations/info?fromId=&fromType=} — Hidden legacy endpoint: outbound relation infos.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strFromId from-entity id
     * @param strFromType from-entity type
     * @param strRelationTypeGroup optional relation type group
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelationInfo}
     * @throws ThingsboardException if entity read access is denied
     * @throws java.util.concurrent.ExecutionException if async enrichment fails
     * @throws InterruptedException if interrupted
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations/info", params = {FROM_ID, FROM_TYPE})
    public List<EntityRelationInfo> findInfoByFrom(@Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @RequestParam(FROM_ID) String strFromId,
                                                   @Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(FROM_TYPE) String strFromType,
                                                   @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION)
                                                   @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter(FROM_ID, strFromId);
        checkParameter(FROM_TYPE, strFromType);
        EntityId entityId = EntityIdFactory.getByTypeAndId(strFromType, strFromId);
        checkEntityId(entityId, Operation.READ);
        RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
        return checkNotNull(filterRelationsByReadPermission(relationService.findInfoByFrom(getTenantId(), entityId, typeGroup).get()));
    }

    /**
     * GET {@code /api/relations/info/from/{fromType}/{fromId}} — List outbound relation info objects.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strFromType from-entity type
     * @param strFromId from-entity id
     * @param strRelationTypeGroup optional relation type group
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelationInfo}
     * @throws ThingsboardException if entity read access is denied
     * @throws java.util.concurrent.ExecutionException if async enrichment fails
     * @throws InterruptedException if interrupted
     */
    @ApiOperation(value = "Get List of Relation Infos (findEntityRelationInfosByFrom)",
            notes = "Returns list of relation info objects for the specified entity by the 'from' direction. " +
                    SECURITY_CHECKS_ENTITY_DESCRIPTION + " " + RELATION_INFO_DESCRIPTION)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations/info/from/{fromType}/{fromId}")
    public List<EntityRelationInfo> findEntityRelationInfosByFrom(@Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @PathVariable(FROM_TYPE) String strFromType,
                                                                  @Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @PathVariable(FROM_ID) String strFromId,
                                                                  @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION)
                                                                  @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException, ExecutionException, InterruptedException {
        return findInfoByFrom(strFromId, strFromType, strRelationTypeGroup);
    }

    /**
     * GET {@code /api/relations?fromId=&fromType=&relationType=} — Hidden legacy endpoint: outbound relations by type.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strFromId from-entity id
     * @param strFromType from-entity type
     * @param strRelationType relation type filter
     * @param strRelationTypeGroup optional relation type group
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if entity read access is denied
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations", params = {FROM_ID, FROM_TYPE, RELATION_TYPE})
    public List<EntityRelation> findByFrom(@RequestParam(FROM_ID) String strFromId,
                                           @RequestParam(FROM_TYPE) String strFromType,
                                           @RequestParam(RELATION_TYPE) String strRelationType,
                                           @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException {
        checkParameter(FROM_ID, strFromId);
        checkParameter(FROM_TYPE, strFromType);
        checkParameter(RELATION_TYPE, strRelationType);
        EntityId entityId = EntityIdFactory.getByTypeAndId(strFromType, strFromId);
        checkEntityId(entityId, Operation.READ);
        RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
        return checkNotNull(filterRelationsByReadPermission(relationService.findByFromAndType(getTenantId(), entityId, strRelationType, typeGroup)));
    }

    /**
     * GET {@code /api/relations/from/{fromType}/{fromId}/{relationType}} — Outbound relations filtered by type.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strFromType from-entity type
     * @param strFromId from-entity id
     * @param strRelationType relation type path variable
     * @param strRelationTypeGroup optional relation type group
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if entity read access is denied
     */
    @ApiOperation(value = "Get List of Relations (findEntityRelationsByFromAndRelationType)",
            notes = "Returns list of relation objects for the specified entity by the 'from' direction and relation type. " +
                    SECURITY_CHECKS_ENTITY_DESCRIPTION)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations/from/{fromType}/{fromId}/{relationType}")
    public List<EntityRelation> findEntityRelationsByFromAndRelationType(@Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @PathVariable(FROM_TYPE) String strFromType,
                                                                         @Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @PathVariable(FROM_ID) String strFromId,
                                                                         @Parameter(description = RELATION_TYPE_PARAM_DESCRIPTION, required = true) @PathVariable(RELATION_TYPE) String strRelationType,
                                                                         @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION)
                                                                         @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException {
        return findByFrom(strFromId, strFromType, strRelationType, strRelationTypeGroup);
    }

    /**
     * GET {@code /api/relations?toId=&toType=} — Hidden legacy endpoint: inbound relations to entity.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strToId to-entity id
     * @param strToType to-entity type
     * @param strRelationTypeGroup optional relation type group
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if entity read access is denied
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations", params = {TO_ID, TO_TYPE})
    public List<EntityRelation> findByTo(@Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @RequestParam(TO_ID) String strToId,
                                         @Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(TO_TYPE) String strToType,
                                         @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION)
                                         @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException {
        checkParameter(TO_ID, strToId);
        checkParameter(TO_TYPE, strToType);
        EntityId entityId = EntityIdFactory.getByTypeAndId(strToType, strToId);
        checkEntityId(entityId, Operation.READ);
        RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
        return checkNotNull(filterRelationsByReadPermission(relationService.findByTo(getTenantId(), entityId, typeGroup)));
    }

    /**
     * GET {@code /api/relations/to/{toType}/{toId}} — List inbound relations to an entity.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strToType to-entity type
     * @param strToId to-entity id
     * @param strRelationTypeGroup optional relation type group
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if entity read access is denied
     */
    @ApiOperation(value = "Get List of Relations (findEntityRelationsByTo)",
            notes = "Returns list of relation objects for the specified entity by the 'to' direction. " +
                    SECURITY_CHECKS_ENTITY_DESCRIPTION)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations/to/{toType}/{toId}")
    public List<EntityRelation> findEntityRelationsByTo(@Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @PathVariable(TO_TYPE) String strToType,
                                                        @Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @PathVariable(TO_ID) String strToId,
                                                        @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION)
                                                        @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException {
        return findByTo(strToId, strToType, strRelationTypeGroup);
    }

    /**
     * GET {@code /api/relations/info?toId=&toType=} — Hidden legacy endpoint: inbound relation infos.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strToId to-entity id
     * @param strToType to-entity type
     * @param strRelationTypeGroup optional relation type group
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelationInfo}
     * @throws ThingsboardException if entity read access is denied
     * @throws java.util.concurrent.ExecutionException if async enrichment fails
     * @throws InterruptedException if interrupted
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations/info", params = {TO_ID, TO_TYPE})
    public List<EntityRelationInfo> findInfoByTo(@Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @RequestParam(TO_ID) String strToId,
                                                 @Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(TO_TYPE) String strToType,
                                                 @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION)
                                                 @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException, ExecutionException, InterruptedException {
        checkParameter(TO_ID, strToId);
        checkParameter(TO_TYPE, strToType);
        EntityId entityId = EntityIdFactory.getByTypeAndId(strToType, strToId);
        checkEntityId(entityId, Operation.READ);
        RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
        return checkNotNull(filterRelationsByReadPermission(relationService.findInfoByTo(getTenantId(), entityId, typeGroup).get()));
    }

    /**
     * GET {@code /api/relations/info/to/{toType}/{toId}} — List inbound relation info objects.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strToType to-entity type
     * @param strToId to-entity id
     * @param strRelationTypeGroup optional relation type group
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelationInfo}
     * @throws ThingsboardException if entity read access is denied
     * @throws java.util.concurrent.ExecutionException if async enrichment fails
     * @throws InterruptedException if interrupted
     */
    @ApiOperation(value = "Get List of Relation Infos (findEntityRelationInfosByTo)",
            notes = "Returns list of relation info objects for the specified entity by the 'to' direction. " +
                    SECURITY_CHECKS_ENTITY_DESCRIPTION + " " + RELATION_INFO_DESCRIPTION)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations/info/to/{toType}/{toId}")
    public List<EntityRelationInfo> findEntityRelationInfosByTo(@Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @PathVariable(TO_TYPE) String strToType,
                                                                @Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @PathVariable(TO_ID) String strToId,
                                                                @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION)
                                                                @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException, ExecutionException, InterruptedException {
        return findInfoByTo(strToId, strToType, strRelationTypeGroup);
    }

    /**
     * GET {@code /api/relations?toId=&toType=&relationType=} — Hidden legacy endpoint: inbound relations by type.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strToId to-entity id
     * @param strToType to-entity type
     * @param strRelationType relation type filter
     * @param strRelationTypeGroup optional relation type group
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if entity read access is denied
     */
    @Hidden
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations", params = {TO_ID, TO_TYPE, RELATION_TYPE})
    public List<EntityRelation> findByTo(@Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @RequestParam(TO_ID) String strToId,
                                         @Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(TO_TYPE) String strToType,
                                         @Parameter(description = RELATION_TYPE_PARAM_DESCRIPTION, required = true) @RequestParam(RELATION_TYPE) String strRelationType,
                                         @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION)
                                         @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException {
        checkParameter(TO_ID, strToId);
        checkParameter(TO_TYPE, strToType);
        checkParameter(RELATION_TYPE, strRelationType);
        EntityId entityId = EntityIdFactory.getByTypeAndId(strToType, strToId);
        checkEntityId(entityId, Operation.READ);
        RelationTypeGroup typeGroup = parseRelationTypeGroup(strRelationTypeGroup, RelationTypeGroup.COMMON);
        return checkNotNull(filterRelationsByReadPermission(relationService.findByToAndType(getTenantId(), entityId, strRelationType, typeGroup)));
    }

    /**
     * GET {@code /api/relations/to/{toType}/{toId}/{relationType}} — Inbound relations filtered by type.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param strToType to-entity type
     * @param strToId to-entity id
     * @param strRelationType relation type
     * @param strRelationTypeGroup optional relation type group
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if entity read access is denied
     */
    @ApiOperation(value = "Get List of Relations (findEntityRelationsByToAndRelationType)",
            notes = "Returns list of relation objects for the specified entity by the 'to' direction and relation type. " +
                    SECURITY_CHECKS_ENTITY_DESCRIPTION)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/relations/to/{toType}/{toId}/{relationType}")
    public List<EntityRelation> findEntityRelationsByToAndRelationType(@Parameter(description = ENTITY_TYPE_PARAM_DESCRIPTION, required = true) @PathVariable(TO_TYPE) String strToType,
                                                                       @Parameter(description = ENTITY_ID_PARAM_DESCRIPTION, required = true) @PathVariable(TO_ID) String strToId,
                                                                       @Parameter(description = RELATION_TYPE_PARAM_DESCRIPTION, required = true) @PathVariable(RELATION_TYPE) String strRelationType,
                                                                       @Parameter(description = RELATION_TYPE_GROUP_PARAM_DESCRIPTION)
                                                                       @RequestParam(value = "relationTypeGroup", required = false) String strRelationTypeGroup) throws ThingsboardException {
        return findByTo(strToId, strToType, strRelationType, strRelationTypeGroup);
    }

    /**
     * POST {@code /api/relations} — Find relations using {@link org.thingsboard.server.common.data.relation.EntityRelationsQuery}.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param query relations query JSON
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelation}
     * @throws ThingsboardException if query root entity access is denied
     * @throws java.util.concurrent.ExecutionException if async search fails
     * @throws InterruptedException if interrupted
     */
    @ApiOperation(value = "Find related entities (findEntityRelationsByQuery)",
            notes = "Returns all entities that are related to the specific entity. " +
                    "The entity id, relation type, entity types, depth of the search, and other query parameters defined using complex 'EntityRelationsQuery' object. " +
                    "See 'Model' tab of the Parameters for more info.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @PostMapping("/relations")
    public List<EntityRelation> findEntityRelationsByQuery(@Parameter(description = "A JSON value representing the entity relations query object.", required = true)
                                                           @RequestBody EntityRelationsQuery query) throws ThingsboardException, ExecutionException, InterruptedException {
        checkNotNull(query.getParameters());
        checkNotNull(query.getFilters());
        checkEntityId(query.getParameters().getEntityId(), Operation.READ);
        return checkNotNull(filterRelationsByReadPermission(relationService.findByQuery(getTenantId(), query).get()));
    }

    /**
     * POST {@code /api/relations/info} — Find relation infos using {@link org.thingsboard.server.common.data.relation.EntityRelationsQuery}.
     * <p>Requires {@code @PreAuthorize}: {@code SYS_ADMIN}, {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     * @param query relations query JSON
     * @return list of {@link org.thingsboard.server.common.data.relation.EntityRelationInfo}
     * @throws ThingsboardException if query root entity access is denied
     * @throws java.util.concurrent.ExecutionException if async search fails
     * @throws InterruptedException if interrupted
     */
    @ApiOperation(value = "Find related entity infos (findEntityRelationInfosByQuery)",
            notes = "Returns all entity infos that are related to the specific entity. " +
                    "The entity id, relation type, entity types, depth of the search, and other query parameters defined using complex 'EntityRelationsQuery' object. " +
                    "See 'Model' tab of the Parameters for more info. " + RELATION_INFO_DESCRIPTION)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @PostMapping("/relations/info")
    public List<EntityRelationInfo> findEntityRelationInfosByQuery(@Parameter(description = "A JSON value representing the entity relations query object.", required = true)
                                                                   @RequestBody EntityRelationsQuery query) throws ThingsboardException, ExecutionException, InterruptedException {
        checkNotNull(query.getParameters());
        checkNotNull(query.getFilters());
        checkEntityId(query.getParameters().getEntityId(), Operation.READ);
        return checkNotNull(filterRelationsByReadPermission(relationService.findInfoByQuery(getTenantId(), query).get()));
    }

    private void checkCanCreateRelation(EntityId entityId) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        var isTenantAdminAndRelateToSelf = currentUser.isTenantAdmin() && currentUser.getTenantId().equals(entityId);
        if (!isTenantAdminAndRelateToSelf) {
            checkEntityId(entityId, Operation.WRITE);
        }
    }

    private <T extends EntityRelation> List<T> filterRelationsByReadPermission(List<T> relationsByQuery) {
        return relationsByQuery.stream().filter(relationByQuery -> {
            try {
                checkEntityId(relationByQuery.getTo(), Operation.READ);
            } catch (ThingsboardException e) {
                return false;
            }
            try {
                checkEntityId(relationByQuery.getFrom(), Operation.READ);
            } catch (ThingsboardException e) {
                return false;
            }
            return true;
        }).toList();
    }

    private static RelationTypeGroup parseRelationTypeGroup(String strRelationTypeGroup, RelationTypeGroup defaultValue) {
        if (StringUtils.isBlank(strRelationTypeGroup)) {
            return defaultValue;
        }
        try {
            return RelationTypeGroup.valueOf(strRelationTypeGroup);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

}
