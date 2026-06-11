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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * ssl credentials contract.
 */
public interface SslCredentials {

    void init(boolean trustsOnly) throws IOException, GeneralSecurityException;

    /**
     * Reload.
     *
     * @param trustsOnly trusts only
     * @return nothing
     * @throws IOException if ioexception is thrown during processing
     * @throws GeneralSecurityException if general security exception is thrown during processing
     */
    void reload(boolean trustsOnly) throws IOException, GeneralSecurityException;

    /**
     * Returns key store.
     *
     * @return {@link KeyStore}
     * @throws Exception on processing failure
     */
    KeyStore getKeyStore();

    /**
     * Returns key password.
     *
     * @return {@link String}
     * @throws Exception on processing failure
     */
    String getKeyPassword();

    /**
     * Returns key alias.
     *
     * @return {@link String}
     * @throws Exception on processing failure
     */
    String getKeyAlias();

    /**
     * Returns private key.
     *
     * @return {@link PrivateKey}
     * @throws Exception on processing failure
     */
    PrivateKey getPrivateKey();

    /**
     * Returns public key.
     *
     * @return {@link PublicKey}
     * @throws Exception on processing failure
     */
    PublicKey getPublicKey();

    /**
     * Returns certificate chain.
     *
     * @return the X509Certificate[] value
     * @throws Exception on processing failure
     */
    X509Certificate[] getCertificateChain();

    /**
     * Returns trusted certificates.
     *
     * @return the X509Certificate[] value
     * @throws Exception on processing failure
     */
    X509Certificate[] getTrustedCertificates();

    /**
     * Creates trust manager factory.
     *
     * @return {@link TrustManagerFactory}
     * @throws NoSuchAlgorithmException if no such algorithm exception is thrown during processing
     * @throws KeyStoreException if key store exception is thrown during processing
     */
    TrustManagerFactory createTrustManagerFactory() throws NoSuchAlgorithmException, KeyStoreException;

    /**
     * Creates key manager factory.
     *
     * @return {@link KeyManagerFactory}
     * @throws NoSuchAlgorithmException if no such algorithm exception is thrown during processing
     * @throws UnrecoverableKeyException if unrecoverable key exception is thrown during processing
     * @throws KeyStoreException if key store exception is thrown during processing
     */
    KeyManagerFactory createKeyManagerFactory() throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException;

    /**
     * Returns value from subject name by key.
     *
     * @param subjectName subject name ({@link String})
     * @param key key ({@link String})
     * @return {@link String}
     * @throws Exception on processing failure
     */
    String getValueFromSubjectNameByKey(String subjectName, String key);

    /**
     * Returns certificate file paths.
     *
     * @return {@link List}
     * @throws Exception on processing failure
     */
    List<Path> getCertificateFilePaths();

}
