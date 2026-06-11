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
package org.thingsboard.server.transport.lwm2m.server.store;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.leshan.core.SecurityMode;
import org.eclipse.leshan.core.peer.OscoreIdentity;
import org.eclipse.leshan.server.security.NonUniqueSecurityInfoException;
import org.eclipse.leshan.server.security.SecurityInfo;
import org.thingsboard.server.transport.lwm2m.secure.LwM2mCredentialsSecurityInfoValidator;
import org.thingsboard.server.transport.lwm2m.secure.TbLwM2MSecurityInfo;
import org.thingsboard.server.transport.lwm2m.server.client.LwM2MAuthException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.thingsboard.server.transport.lwm2m.server.uplink.LwM2mTypeServer.CLIENT;

/**
 * Tb lw m2m security store.
 */
@Slf4j
public class TbLwM2mSecurityStore implements TbMainSecurityStore {

    private final TbEditableSecurityStore securityStore;
    private final LwM2mCredentialsSecurityInfoValidator validator;
    private final ConcurrentMap<String, Set<String>> endpointRegistrations = new ConcurrentHashMap<>();

    public TbLwM2mSecurityStore(TbEditableSecurityStore securityStore, LwM2mCredentialsSecurityInfoValidator validator) {
        this.securityStore = securityStore;
        this.validator = validator;
    }
    /**
     * Returns tb lw m2msecurity info by endpoint.
     *
     * @param endpoint endpoint ({@link String})
     * @return {@link TbLwM2MSecurityInfo}
     * @throws Exception on processing failure
     */

    @Override
    public TbLwM2MSecurityInfo getTbLwM2MSecurityInfoByEndpoint(String endpoint) {
        return securityStore.getTbLwM2MSecurityInfoByEndpoint(endpoint);
    }

    
    /**
     * Returns by endpoint.
     *
     * @param endpoint endpoint ({@link String})
     * @return {@link SecurityInfo}
     * @throws Exception on processing failure
     */

    @Override
    public SecurityInfo getByEndpoint(String endpoint) {
        SecurityInfo securityInfo = securityStore.getByEndpoint(endpoint);
        if (securityInfo == null) {
            securityInfo = fetchAndPutSecurityInfo(endpoint);
        } else if (securityInfo.usePSK() && securityInfo.getEndpoint().equals(SecurityMode.NO_SEC.toString())
                && securityInfo.getPskIdentity().equals(SecurityMode.NO_SEC.toString())
                && Arrays.equals(SecurityMode.NO_SEC.toString().getBytes(), securityInfo.getPreSharedKey())) {
            return null;
        }
        return securityInfo;
    }
    /**
     * Returns by identity.
     *
     * @param pskIdentity psk identity ({@link String})
     * @return {@link SecurityInfo}
     * @throws Exception on processing failure
     */

    @Override
    public SecurityInfo getByIdentity(String pskIdentity) {
        SecurityInfo securityInfo = securityStore.getByIdentity(pskIdentity);
        if (securityInfo == null) {
            try {
                securityInfo = fetchAndPutSecurityInfo(pskIdentity);
            } catch (LwM2MAuthException e) {
                log.trace("Registration failed: No pre-shared key found for [identity: {}]", pskIdentity);
                return null;
            }
        }
        return securityInfo;
    }
    /**
     * Returns by oscore identity.
     *
     * @param oscoreIdentity oscore identity ({@link OscoreIdentity})
     * @return {@link SecurityInfo}
     * @throws Exception on processing failure
     */

    @Override
    public SecurityInfo getByOscoreIdentity(OscoreIdentity oscoreIdentity) {
        return null;
    }
    /**
     * Fetches and put security info.
     *
     * @param credentialsId credentials id ({@link String})
     * @return {@link SecurityInfo}
     * @throws Exception on processing failure
     */
    public SecurityInfo fetchAndPutSecurityInfo(String credentialsId) {
        TbLwM2MSecurityInfo securityInfo = validator.getEndpointSecurityInfoByCredentialsId(credentialsId, CLIENT);
        doPut(securityInfo);
        return securityInfo != null ? securityInfo.getSecurityInfo() : null;
    }

    private void doPut(TbLwM2MSecurityInfo securityInfo) {
        if (securityInfo != null) {
            try {
                securityStore.put(securityInfo);
            } catch (NonUniqueSecurityInfoException e) {
                log.trace("Failed to add security info: {}", securityInfo, e);
            }
        }
    }
    /**
     * Put x509.
     *
     * @param securityInfo security info ({@link TbLwM2MSecurityInfo})
     * @return nothing
     * @throws NonUniqueSecurityInfoException if non unique security info exception is thrown during processing
     */

    @Override
    public void putX509(TbLwM2MSecurityInfo securityInfo) throws NonUniqueSecurityInfoException {
        securityStore.put(securityInfo);
    }
    /**
     * Register x509.
     *
     * @param endpoint endpoint ({@link String})
     * @param registrationId registration id ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void registerX509(String endpoint, String registrationId) {
        endpointRegistrations.computeIfAbsent(endpoint, ep -> new HashSet<>()).add(registrationId);
    }
    /**
     * Removes the requested data.
     *
     * @param endpoint endpoint ({@link String})
     * @param registrationId registration id ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    public void remove(String endpoint, String registrationId) {
        Set<String> epRegistrationIds = endpointRegistrations.get(endpoint);
        boolean shouldRemove;
        if (epRegistrationIds == null) {
            shouldRemove = true;
        } else {
            epRegistrationIds.remove(registrationId);
            shouldRemove = epRegistrationIds.isEmpty();
        }
        if (shouldRemove) {
            securityStore.remove(endpoint);
        }
    }
}
