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
package org.thingsboard.server.common.data.device.credentials.lwm2m;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;

@Getter
@Setter
/**
 * Abstract lw m2mbootstrap client credential with keys.
 */
public abstract class AbstractLwM2MBootstrapClientCredentialWithKeys implements LwM2MBootstrapClientCredential {

    private String clientPublicKeyOrId;
    private String clientSecretKey;
    /**
     * Returns decoded client public key or id.
     *
     * @return the byte[] value
     */

    @JsonIgnore
    public byte[] getDecodedClientPublicKeyOrId() {
        return getDecoded(clientPublicKeyOrId);
    }
    /**
     * Returns decoded client secret key.
     *
     * @return the byte[] value
     */

    @JsonIgnore
    public byte[] getDecodedClientSecretKey() {
        return getDecoded(clientSecretKey);
    }

    @SneakyThrows
    private static byte[] getDecoded(String key) {
        return Base64.decodeBase64(key.getBytes());
    }
}
