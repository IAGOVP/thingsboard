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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.DeviceProfileInfo;
import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.config.annotations.ApiOperation;
import org.thingsboard.server.dao.resource.ImageService;
import org.thingsboard.server.dao.timeseries.TimeseriesService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.device.profile.TbDeviceProfileService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.thingsboard.server.controller.ControllerConstants.DEVICE_PROFILE_DATA;
import static org.thingsboard.server.controller.ControllerConstants.DEVICE_PROFILE_ID;
import static org.thingsboard.server.controller.ControllerConstants.DEVICE_PROFILE_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.DEVICE_PROFILE_INFO_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.DEVICE_PROFILE_TEXT_SEARCH_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.INLINE_IMAGES;
import static org.thingsboard.server.controller.ControllerConstants.INLINE_IMAGES_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.NEW_LINE;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_DATA_PARAMETERS;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_NUMBER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_SIZE_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_PROPERTY_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_AUTHORITY_PARAGRAPH;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH;
import static org.thingsboard.server.controller.ControllerConstants.UUID_WIKI_LINK;

/**
 * REST API for device profile CRUD, default profile management, and telemetry/attribute key discovery.
 *
 * <p>Base path: {@code /api}.
 *
 * <p>Authorization: {@code TENANT_ADMIN} for management; {@code TENANT_ADMIN} or {@code CUSTOMER_USER} for read-only info endpoints.
 *
 * <p>Uses {@link org.thingsboard.server.service.entitiy.device.profile.TbDeviceProfileService},
 * {@link org.thingsboard.server.dao.resource.ImageService}, and {@link org.thingsboard.server.dao.timeseries.TimeseriesService}.
 */
@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class DeviceProfileController extends BaseController {

    private final TbDeviceProfileService tbDeviceProfileService;
    private final ImageService imageService;

    @Autowired
    private TimeseriesService timeseriesService;

    /**
     * GET {@code /api/deviceProfile/{deviceProfileId}} — Fetch a device profile by id.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param strDeviceProfileId device profile UUID string
     * @param inlineImages       when {@code true}, embed image resources inline in the response
     * @return the {@link org.thingsboard.server.common.data.DeviceProfile}
     * @throws ThingsboardException if the profile does not exist or access is denied
     */
    @ApiOperation(value = "Get Device Profile (getDeviceProfileById)",
            notes = "Fetch the Device Profile object based on the provided Device Profile Id. " +
                    "The server checks that the device profile is owned by the same tenant. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/deviceProfile/{deviceProfileId}", method = RequestMethod.GET)
    @ResponseBody
    public DeviceProfile getDeviceProfileById(
            @Parameter(description = DEVICE_PROFILE_ID_PARAM_DESCRIPTION)
            @PathVariable(DEVICE_PROFILE_ID) String strDeviceProfileId,
            @Parameter(description = INLINE_IMAGES_DESCRIPTION)
            @RequestParam(value = INLINE_IMAGES, required = false) boolean inlineImages) throws ThingsboardException {
        checkParameter(DEVICE_PROFILE_ID, strDeviceProfileId);
        DeviceProfileId deviceProfileId = new DeviceProfileId(toUUID(strDeviceProfileId));
        var result = checkDeviceProfileId(deviceProfileId, Operation.READ);
        if (inlineImages) {
            result = imageService.inlineImage(result);
        }
        return result;
    }

    /**
     * GET {@code /api/deviceProfileInfo/{deviceProfileId}} — Fetch lightweight device profile info by id.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     *
     * @param strDeviceProfileId device profile UUID string
     * @return {@link org.thingsboard.server.common.data.DeviceProfileInfo}
     * @throws ThingsboardException if the profile does not exist
     */
    @ApiOperation(value = "Get Device Profile Info (getDeviceProfileInfoById)",
            notes = "Fetch the Device Profile Info object based on the provided Device Profile Id. "
                    + DEVICE_PROFILE_INFO_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/deviceProfileInfo/{deviceProfileId}", method = RequestMethod.GET)
    @ResponseBody
    public DeviceProfileInfo getDeviceProfileInfoById(
            @Parameter(description = DEVICE_PROFILE_ID_PARAM_DESCRIPTION)
            @PathVariable(DEVICE_PROFILE_ID) String strDeviceProfileId) throws ThingsboardException {
        checkParameter(DEVICE_PROFILE_ID, strDeviceProfileId);
        DeviceProfileId deviceProfileId = new DeviceProfileId(toUUID(strDeviceProfileId));
        return new DeviceProfileInfo(checkDeviceProfileId(deviceProfileId, Operation.READ));
    }

    /**
     * GET {@code /api/deviceProfileInfo/default} — Return the tenant's default device profile info.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     *
     * @return default {@link org.thingsboard.server.common.data.DeviceProfileInfo}
     * @throws ThingsboardException if no default profile is configured
     */
    @ApiOperation(value = "Get Default Device Profile (getDefaultDeviceProfileInfo)",
            notes = "Fetch the Default Device Profile Info object. " +
                    DEVICE_PROFILE_INFO_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/deviceProfileInfo/default", method = RequestMethod.GET)
    @ResponseBody
    public DeviceProfileInfo getDefaultDeviceProfileInfo() throws ThingsboardException {
        return checkNotNull(deviceProfileService.findDefaultDeviceProfileInfo(getTenantId()));
    }

    /**
     * GET {@code /api/deviceProfile/devices/keys/timeseries} — List unique timeseries keys used by devices in a profile.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param deviceProfileIdStr optional profile UUID; when omitted, keys from all profiles are returned
     * @return distinct timeseries key names for UI autocomplete
     * @throws ThingsboardException if the profile id is invalid
     */
    @ApiOperation(value = "Get time series keys (getDeviceProfileTimeseriesKeys)",
            notes = "Get a set of unique time series keys used by devices that belong to specified profile. " +
                    "If profile is not set returns a list of unique keys among all profiles. " +
                    "The call is used for auto-complete in the UI forms. " +
                    "The implementation limits the number of devices that participate in search to 100 as a trade of between accurate results and time-consuming queries. " +
                    TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/deviceProfile/devices/keys/timeseries", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getDeviceProfileTimeseriesKeys(
            @Parameter(description = DEVICE_PROFILE_ID_PARAM_DESCRIPTION)
            @RequestParam(name = DEVICE_PROFILE_ID, required = false) String deviceProfileIdStr) throws ThingsboardException {
        DeviceProfileId deviceProfileId;
        if (StringUtils.isNotEmpty(deviceProfileIdStr)) {
            deviceProfileId = new DeviceProfileId(UUID.fromString(deviceProfileIdStr));
            checkDeviceProfileId(deviceProfileId, Operation.READ);
        } else {
            deviceProfileId = null;
        }

        return timeseriesService.findAllKeysByDeviceProfileId(getTenantId(), deviceProfileId);
    }

    /**
     * GET {@code /api/deviceProfile/devices/keys/attributes} — List unique attribute keys used by devices in a profile.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param deviceProfileIdStr optional profile UUID; when omitted, keys from all profiles are returned
     * @return distinct attribute key names for UI autocomplete
     * @throws ThingsboardException if the profile id is invalid
     */
    @ApiOperation(value = "Get attribute keys (getAttributesKeys)",
            notes = "Get a set of unique attribute keys used by devices that belong to specified profile. " +
                    "If profile is not set returns a list of unique keys among all profiles. " +
                    "The call is used for auto-complete in the UI forms. " +
                    "The implementation limits the number of devices that participate in search to 100 as a trade of between accurate results and time-consuming queries. " +
                    TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/deviceProfile/devices/keys/attributes", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAttributesKeys(
            @Parameter(description = DEVICE_PROFILE_ID_PARAM_DESCRIPTION)
            @RequestParam(name = DEVICE_PROFILE_ID, required = false) String deviceProfileIdStr) throws ThingsboardException {
        DeviceProfileId deviceProfileId;
        if (StringUtils.isNotEmpty(deviceProfileIdStr)) {
            deviceProfileId = new DeviceProfileId(UUID.fromString(deviceProfileIdStr));
            checkDeviceProfileId(deviceProfileId, Operation.READ);
        } else {
            deviceProfileId = null;
        }

        return attributesService.findAllKeysByDeviceProfileId(getTenantId(), deviceProfileId);
    }

    /**
     * POST {@code /api/deviceProfile} — Create or update a device profile.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param deviceProfile JSON body with profile configuration
     * @return the saved {@link org.thingsboard.server.common.data.DeviceProfile}
     * @throws Exception if validation fails or a referenced profile id does not exist
     */
    @ApiOperation(value = "Create Or Update Device Profile (saveDeviceProfile)",
            notes = "Create or update the Device Profile. When creating device profile, platform generates device profile id as " + UUID_WIKI_LINK +
                    "The newly created device profile id will be present in the response. " +
                    "Specify existing device profile id to update the device profile. " +
                    "Referencing non-existing device profile Id will cause 'Not Found' error. " + NEW_LINE +
                    "Device profile name is unique in the scope of tenant. Only one 'default' device profile may exist in scope of tenant." + DEVICE_PROFILE_DATA +
                    "Remove 'id', 'tenantId' from the request body example (below) to create new Device Profile entity. " +
                    TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/deviceProfile", method = RequestMethod.POST)
    @ResponseBody
    public DeviceProfile saveDeviceProfile(
            @Parameter(description = "A JSON value representing the device profile.")
            @RequestBody DeviceProfile deviceProfile) throws Exception {
        deviceProfile.setTenantId(getTenantId());
        checkEntity(deviceProfile.getId(), deviceProfile, Resource.DEVICE_PROFILE);
        return tbDeviceProfileService.save(deviceProfile, getCurrentUser());
    }

    /**
     * DELETE {@code /api/deviceProfile/{deviceProfileId}} — Delete a device profile by id.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param strDeviceProfileId device profile UUID string
     * @throws ThingsboardException if the profile is referenced by devices or does not exist
     */
    @ApiOperation(value = "Delete device profile (deleteDeviceProfile)",
            notes = "Deletes the device profile. Referencing non-existing device profile Id will cause an error. " +
                    "Can't delete the device profile if it is referenced by existing devices." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/deviceProfile/{deviceProfileId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteDeviceProfile(
            @Parameter(description = DEVICE_PROFILE_ID_PARAM_DESCRIPTION)
            @PathVariable(DEVICE_PROFILE_ID) String strDeviceProfileId) throws ThingsboardException {
        checkParameter(DEVICE_PROFILE_ID, strDeviceProfileId);
        DeviceProfileId deviceProfileId = new DeviceProfileId(toUUID(strDeviceProfileId));
        DeviceProfile deviceProfile = checkDeviceProfileId(deviceProfileId, Operation.DELETE);
        tbDeviceProfileService.delete(deviceProfile, getCurrentUser());
    }

    /**
     * POST {@code /api/deviceProfile/{deviceProfileId}/default} — Mark a profile as the tenant default.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param strDeviceProfileId device profile UUID string
     * @return the updated default {@link org.thingsboard.server.common.data.DeviceProfile}
     * @throws ThingsboardException if the profile does not exist
     */
    @ApiOperation(value = "Make Device Profile Default (setDefaultDeviceProfile)",
            notes = "Marks device profile as default within a tenant scope." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/deviceProfile/{deviceProfileId}/default", method = RequestMethod.POST)
    @ResponseBody
    public DeviceProfile setDefaultDeviceProfile(
            @Parameter(description = DEVICE_PROFILE_ID_PARAM_DESCRIPTION)
            @PathVariable(DEVICE_PROFILE_ID) String strDeviceProfileId) throws ThingsboardException {
        checkParameter(DEVICE_PROFILE_ID, strDeviceProfileId);
        DeviceProfileId deviceProfileId = new DeviceProfileId(toUUID(strDeviceProfileId));
        DeviceProfile deviceProfile = checkDeviceProfileId(deviceProfileId, Operation.WRITE);
        DeviceProfile previousDefaultDeviceProfile = deviceProfileService.findDefaultDeviceProfile(getTenantId());
        return tbDeviceProfileService.setDefaultDeviceProfile(deviceProfile, previousDefaultDeviceProfile, getCurrentUser());
    }

    /**
     * GET {@code /api/deviceProfiles} — List device profiles owned by the tenant.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param pageSize     items per page
     * @param page         zero-based page index
     * @param textSearch   optional text filter
     * @param sortProperty optional sort field
     * @param sortOrder    optional sort direction
     * @return a page of {@link org.thingsboard.server.common.data.DeviceProfile}
     * @throws ThingsboardException if access is denied
     */
    @ApiOperation(value = "Get Device Profiles (getDeviceProfiles)",
            notes = "Returns a page of devices profile objects owned by tenant. " +
                    PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/deviceProfiles")
    public PageData<DeviceProfile> getDeviceProfiles(
            @Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @Parameter(description = DEVICE_PROFILE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "name", "type", "transportType", "description", "isDefault"}))
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC", "DESC"}))
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return checkNotNull(deviceProfileService.findDeviceProfiles(getTenantId(), pageLink));
    }

    /**
     * GET {@code /api/deviceProfileInfos} — List device profile info objects, optionally filtered by transport type.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     *
     * @param pageSize     items per page
     * @param page         zero-based page index
     * @param textSearch   optional text filter
     * @param sortProperty optional sort field
     * @param sortOrder    optional sort direction
     * @param transportType optional transport filter ({@code MQTT}, {@code COAP}, etc.)
     * @return a page of {@link org.thingsboard.server.common.data.DeviceProfileInfo}
     * @throws ThingsboardException if access is denied
     */
    @ApiOperation(value = "Get Device Profiles for transport type (getDeviceProfileInfos)",
            notes = "Returns a page of devices profile info objects owned by tenant. " +
                    PAGE_DATA_PARAMETERS + DEVICE_PROFILE_INFO_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping(value = "/deviceProfileInfos")
    public PageData<DeviceProfileInfo> getDeviceProfileInfos(
            @Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @Parameter(description = DEVICE_PROFILE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "name", "type", "transportType", "description", "isDefault"}))
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC", "DESC"}))
            @RequestParam(required = false) String sortOrder,
            @Parameter(description = "Type of the transport", schema = @Schema(allowableValues = {"DEFAULT", "MQTT", "COAP", "LWM2M", "SNMP"}))
            @RequestParam(required = false) String transportType) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return checkNotNull(deviceProfileService.findDeviceProfileInfos(getTenantId(), pageLink, transportType));
    }

    /**
     * GET {@code /api/deviceProfile/names} — Return unique device profile names for the tenant.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}, {@code CUSTOMER_USER}.
     *
     * @param activeOnly when {@code true}, return only names referenced by existing devices
     * @return list of {@link org.thingsboard.server.common.data.EntityInfo} name entries
     * @throws ThingsboardException if access is denied
     */
    @ApiOperation(value = "Get Device Profile names (getDeviceProfileNames)",
            notes = "Returns a set of unique device profile names owned by the tenant."
                    + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/deviceProfile/names", method = RequestMethod.GET)
    @ResponseBody
    public List<EntityInfo> getDeviceProfileNames(
            @Parameter(description = "Flag indicating whether to retrieve exclusively the names of device profiles that are referenced by tenant's devices.")
            @RequestParam(value = "activeOnly", required = false, defaultValue = "false") boolean activeOnly) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        return checkNotNull(deviceProfileService.findDeviceProfileNamesByTenantId(tenantId, activeOnly));
    }

    /**
     * GET {@code /api/deviceProfileInfos?deviceProfileIds=} — Internal helper to fetch profiles by id set (hidden API).
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param deviceProfileUUIDs set of device profile UUIDs
     * @return list of {@link org.thingsboard.server.common.data.DeviceProfileInfo}
     * @throws ThingsboardException if any profile is not owned by the tenant
     */
    @Hidden
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/deviceProfileInfos", params = {"deviceProfileIds"})
    public List<DeviceProfileInfo> getDeviceProfileInfosByIdsV1(@RequestParam("deviceProfileIds") Set<UUID> deviceProfileUUIDs) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        List<DeviceProfileId> deviceProfileIds = new ArrayList<>();
        for (UUID deviceProfileUUID : deviceProfileUUIDs) {
            deviceProfileIds.add(new DeviceProfileId(deviceProfileUUID));
        }
        return deviceProfileService.findDeviceProfilesByIds(tenantId, deviceProfileIds);
    }

    /**
     * GET {@code /api/deviceProfileInfos/list} — Fetch multiple device profile info objects by id list.
     *
     * <p>Requires {@code @PreAuthorize}: {@code TENANT_ADMIN}.
     *
     * @param deviceProfileUUIDs comma-separated device profile UUIDs
     * @return list of {@link org.thingsboard.server.common.data.DeviceProfileInfo}
     * @throws ThingsboardException if any profile is not owned by the tenant
     */
    @ApiOperation(value = "Get Device Profile Infos By Ids (getDeviceProfileInfosByIds)",
            notes = "Requested device profiles must be owned by tenant which is performing the request. " +
                    NEW_LINE)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping(value = "/deviceProfileInfos/list")
    public List<DeviceProfileInfo> getDeviceProfileInfosByIds(
            @Parameter(description = "A list of device profile ids, separated by comma ','",  array = @ArraySchema(schema = @Schema(type = "string")), required = true)
            @RequestParam("deviceProfileIds") Set<UUID> deviceProfileUUIDs) throws ThingsboardException {
        return getDeviceProfileInfosByIdsV1(deviceProfileUUIDs);
    }

}
