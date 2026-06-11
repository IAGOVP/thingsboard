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
package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.targets.NotificationRecipient;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Schema
@EqualsAndHashCode(callSuper = true)
/**
 * Platform user account with role-based access within a tenant or customer scope.
 *
 * <p>Stores email, name, {@link org.thingsboard.server.common.data.security.Authority} (SYS_ADMIN,
 * TENANT_ADMIN, CUSTOMER_USER), and tenant/customer association. Credentials and activation state
 * live in separate {@code UserCredentials} entity. Used for REST authentication, notifications,
 * and alarm assignee tracking. API: {@code /api/user}.
 */
public class User extends BaseDataWithAdditionalInfo<UserId> implements HasName, HasTenantId, HasCustomerId, NotificationRecipient, HasVersion {

    private static final long serialVersionUID = 8250339805336035966L;

    private TenantId tenantId;
    private CustomerId customerId;
    private String email;
    private Authority authority;
    @NoXss
    @Length(fieldName = "first name")
    private String firstName;
    @NoXss
    @Length(fieldName = "last name")
    private String lastName;
    @NoXss
    private String phone;

    @Getter @Setter
    private Long version;

    public User() {
        super();
    }

    public User(UserId id) {
        super(id);
    }

    public User(User user) {
        super(user);
        this.tenantId = user.getTenantId();
        this.customerId = user.getCustomerId();
        this.email = user.getEmail();
        this.authority = user.getAuthority();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
        this.version = user.getVersion();
    }
    /**
     * Returns id.
     *
     * @return {@link UserId}
     */


    @Schema(description = "JSON object with the User Id. " +
            "Specify this field to update the device. " +
            "Referencing non-existing User Id will cause error. " +
            "Omit this field to create new customer.")
    @Override
    public UserId getId() {
        return super.getId();
    }
    /**
     * Returns created time.
     *
     * @return the long result
     */

    @Schema(description = "Timestamp of the user creation, in milliseconds", example = "1609459200000", accessMode = Schema.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }
    /**
     * Returns tenant id.
     *
     * @return {@link TenantId}
     */

    @Schema(description = "JSON object with the Tenant Id.")
    public TenantId getTenantId() {
        return tenantId;
    }
    /**
     * Set tenant id.
     *
     * @param tenantId tenant that owns the entity or operation
     */

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }
    /**
     * Returns customer id.
     *
     * @return {@link CustomerId}
     */

    @Schema(description = "JSON object with the Customer Id.")
    public CustomerId getCustomerId() {
        return customerId;
    }
    /**
     * Set customer id.
     *
     * @param customerId customer id ({@link CustomerId})
     */

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }
    /**
     * Returns email.
     *
     * @return {@link String}
     */

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Email of the user", example = "user@example.com")
    public String getEmail() {
        return email;
    }
    /**
     * Set email.
     *
     * @param email email ({@link String})
     */

    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Returns name.
     *
     * @return {@link String}
     */

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Duplicates the email of the user, readonly", example = "user@example.com")
    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return email;
    }
    /**
     * Returns authority.
     *
     * @return {@link Authority}
     */

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Authority", example = "SYS_ADMIN, TENANT_ADMIN or CUSTOMER_USER")
    public Authority getAuthority() {
        return authority;
    }
    /**
     * Set authority.
     *
     * @param authority authority ({@link Authority})
     */

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
    /**
     * Returns first name.
     *
     * @return {@link String}
     */

    @Schema(description = "First name of the user", example = "John")
    public String getFirstName() {
        return firstName;
    }
    /**
     * Set first name.
     *
     * @param firstName first name ({@link String})
     */

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * Returns last name.
     *
     * @return {@link String}
     */

    @Schema(description = "Last name of the user", example = "Doe")
    public String getLastName() {
        return lastName;
    }
    /**
     * Set last name.
     *
     * @param lastName last name ({@link String})
     */

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * Returns phone.
     *
     * @return {@link String}
     */

    @Schema(description = "Phone number of the user", example = "38012345123")
    public String getPhone() {
        return phone;
    }
    /**
     * Set phone.
     *
     * @param phone phone ({@link String})
     */

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Schema(description = "Additional parameters of the user. " +
            "May include: 'defaultDashboardId' (string, UUID of the default dashboard), " +
            "'defaultDashboardFullscreen' (boolean), " +
            "'homeDashboardId' (string, UUID of the home dashboard), " +
            "'homeDashboardHideToolbar' (boolean), " +
            "'lang' (string, user locale, e.g. 'en_US'), " +
            "'authProviderName' (string, name of the authentication provider).",
            implementation = com.fasterxml.jackson.databind.JsonNode.class,
            example = "{\"defaultDashboardId\":\"784f394c-42b6-435a-983c-b7beff2784f9\",\"defaultDashboardFullscreen\":false," +
                    "\"homeDashboardId\":\"784f394c-42b6-435a-983c-b7beff2784f9\",\"homeDashboardHideToolbar\":true,\"lang\":\"en_US\"}")
    /**
     * Returns additional info.
     *
     * @return {@link JsonNode}
     */
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }
    /**
     * Returns title.
     *
     * @return {@link String}
     */

    @JsonIgnore
    public String getTitle() {
        return getTitle(email, firstName, lastName);
    }
    /**
     * Returns title.
     *
     * @param email email ({@link String})
     * @param firstName first name ({@link String})
     * @param lastName last name ({@link String})
     * @return {@link String}
     */

    public static String getTitle(String email, String firstName, String lastName) {
        String title = "";
        if (isNotEmpty(firstName)) {
            title += firstName;
        }
        if (isNotEmpty(lastName)) {
            if (!title.isEmpty()) {
                title += " ";
            }
            title += lastName;
        }
        if (title.isEmpty()) {
            title = email;
        }
        return title;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [tenantId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", email=");
        builder.append(email);
        builder.append(", authority=");
        builder.append(authority);
        builder.append(", firstName=");
        builder.append(firstName);
        builder.append(", lastName=");
        builder.append(lastName);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }
    /**
     * Is system admin.
     *
     * @return the boolean result
     */

    @JsonIgnore
    public boolean isSystemAdmin() {
        return tenantId == null || EntityId.NULL_UUID.equals(tenantId.getId());
    }
    /**
     * Is tenant admin.
     *
     * @return the boolean result
     */

    @JsonIgnore
    public boolean isTenantAdmin() {
        return !isSystemAdmin() && (customerId == null || EntityId.NULL_UUID.equals(customerId.getId()));
    }
    /**
     * Is customer user.
     *
     * @return the boolean result
     */

    @JsonIgnore
    public boolean isCustomerUser() {
        return !isSystemAdmin() && !isTenantAdmin();
    }

}
