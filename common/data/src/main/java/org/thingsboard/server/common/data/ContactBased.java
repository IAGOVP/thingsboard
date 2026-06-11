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

import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.UUIDBased;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

@EqualsAndHashCode(callSuper = true)
/**
 * Contact based.
 */
public abstract class ContactBased<I extends UUIDBased> extends BaseDataWithAdditionalInfo<I> implements HasEmail {

    private static final long serialVersionUID = 5047448057830660988L;

    @Length(fieldName = "country")
    @NoXss
    protected String country;
    @Length(fieldName = "state")
    @NoXss
    protected String state;
    @Length(fieldName = "city")
    @NoXss
    protected String city;
    @NoXss
    protected String address;
    @NoXss
    protected String address2;
    @Length(fieldName = "zip or postal code")
    @NoXss
    protected String zip;
    @Length(fieldName = "phone")
    @NoXss
    protected String phone;
    @Length(fieldName = "email")
    @NoXss
    protected String email;

    public ContactBased() {
        super();
    }

    public ContactBased(I id) {
        super(id);
    }

    public ContactBased(ContactBased<I> contact) {
        super(contact);
        this.country = contact.getCountry();
        this.state = contact.getState();
        this.city = contact.getCity();
        this.address = contact.getAddress();
        this.address2 = contact.getAddress2();
        this.zip = contact.getZip();
        this.phone = contact.getPhone();
        this.email = contact.getEmail();
    }
    /**
     * Returns country.
     *
     * @return {@link String}
     */

    public String getCountry() {
        return country;
    }
    /**
     * Set country.
     *
     * @param country country ({@link String})
     */

    public void setCountry(String country) {
        this.country = country;
    }
    /**
     * Returns state.
     *
     * @return {@link String}
     */

    public String getState() {
        return state;
    }
    /**
     * Set state.
     *
     * @param state state ({@link String})
     */

    public void setState(String state) {
        this.state = state;
    }
    /**
     * Returns city.
     *
     * @return {@link String}
     */

    public String getCity() {
        return city;
    }
    /**
     * Set city.
     *
     * @param city city ({@link String})
     */

    public void setCity(String city) {
        this.city = city;
    }
    /**
     * Returns address.
     *
     * @return {@link String}
     */

    public String getAddress() {
        return address;
    }
    /**
     * Set address.
     *
     * @param address address ({@link String})
     */

    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * Returns address2.
     *
     * @return {@link String}
     */

    public String getAddress2() {
        return address2;
    }
    /**
     * Set address2.
     *
     * @param address2 address2 ({@link String})
     */

    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    /**
     * Returns zip.
     *
     * @return {@link String}
     */

    public String getZip() {
        return zip;
    }
    /**
     * Set zip.
     *
     * @param zip zip ({@link String})
     */

    public void setZip(String zip) {
        this.zip = zip;
    }
    /**
     * Returns phone.
     *
     * @return {@link String}
     */

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
    /**
     * Returns email.
     *
     * @return {@link String}
     */

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

}
