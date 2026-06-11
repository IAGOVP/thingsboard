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
package org.thingsboard.server.dao.service.validator;

import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.domain.Domain;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
/**
 * Validates domain entities before persistence.
 *
 * <p>Enforces constraints, uniqueness, and referential integrity at the DAO layer.
 */


@Component
public class DomainDataValidator extends AbstractHasOtaPackageValidator<Domain> {

    
    /**
     * Validates data impl.
     *
     * @param tenantId tenant that owns the entity or operation
     * @param domain domain ({@link Domain})
     * @return nothing
     * @throws Exception if an unexpected error occurs during processing
     */


    @Override
    protected void validateDataImpl(TenantId tenantId, Domain domain) {
        if (!isValidDomain(domain.getName())) {
            throw new IncorrectParameterException("Domain name " + domain.getName() + " is invalid");
        }
    }
}
