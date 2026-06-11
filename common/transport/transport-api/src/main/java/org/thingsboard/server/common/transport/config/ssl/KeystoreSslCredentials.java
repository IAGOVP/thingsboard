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
package org.thingsboard.server.common.transport.config.ssl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.ResourceUtils;
import org.thingsboard.server.common.data.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Collections;
import java.util.List;

/**
 * Keystore ssl credentials.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KeystoreSslCredentials extends AbstractSslCredentials {

    private String type;
    private String storeFile;
    private String storePassword;
    private String keyPassword;
    private String keyAlias;
    /**
     * Can use.
     *
     * @return the boolean result
     * @throws Exception on processing failure
     */

    @Override
    protected boolean canUse() {
        return ResourceUtils.resourceExists(this, this.storeFile);
    }
    /**
     * Loads key store.
     *
     * @param trustsOnly trusts only
     * @param keyPasswordArray key password array
     * @return {@link KeyStore}
     * @throws IOException if ioexception is thrown during processing
     * @throws GeneralSecurityException if general security exception is thrown during processing
     */

    @Override
    protected KeyStore loadKeyStore(boolean trustsOnly, char[] keyPasswordArray) throws IOException, GeneralSecurityException {
        String keyStoreType = StringUtils.isEmpty(this.type) ? KeyStore.getDefaultType() : this.type;
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        try (InputStream tsFileInputStream = ResourceUtils.getInputStream(this, this.storeFile)) {
            keyStore.load(tsFileInputStream, StringUtils.isEmpty(this.storePassword) ? new char[0] : this.storePassword.toCharArray());
        }
        return keyStore;
    }
    /**
     * Updates key alias.
     *
     * @param keyAlias key alias ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */

    @Override
    protected void updateKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }
    /**
     * Returns certificate file paths.
     *
     * @return {@link List}
     * @throws Exception on processing failure
     */

    @Override
    public List<Path> getCertificateFilePaths() {
        if (!StringUtils.isEmpty(storeFile) && !storeFile.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
            // Include the path even if the file doesn't exist yet — the watcher uses mtime=0 / checksum="" as
            // baseline, so a late-appearing file (e.g., mounted after boot) will be detected and trigger a reload.
            return Collections.singletonList(Path.of(storeFile).toAbsolutePath());
        }
        return Collections.emptyList();
    }

}
