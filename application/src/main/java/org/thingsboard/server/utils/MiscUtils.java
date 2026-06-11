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
package org.thingsboard.server.utils;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.Charset;

/**
 * Miscellaneous server-side helpers for configuration messages, hash functions,
 * and HTTP request URL construction behind reverse proxies.
 *
 * @author Andrew Shvayka
 */
public class MiscUtils {

    /** UTF-8 charset constant used across server string operations. */
    public static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * Builds a standard message for a missing required configuration property.
     *
     * @param propertyName name of the property that must be set
     * @return formatted error message string
     */
    public static String missingProperty(String propertyName) {
        return "The " + propertyName + " property need to be set!";
    }

    /**
     * Returns a Guava {@link HashFunction} instance by symbolic name.
     *
     * @param name hash algorithm name: {@code murmur3_32}, {@code murmur3_128}, {@code crc32}, or {@code md5}
     * @return corresponding {@link HashFunction}
     * @throws IllegalArgumentException if the name does not match a supported algorithm
     */
    @SuppressWarnings("deprecation")
    public static HashFunction forName(String name) {
        switch (name) {
            case "murmur3_32":
                return Hashing.murmur3_32();
            case "murmur3_128":
                return Hashing.murmur3_128();
            case "crc32":
                return Hashing.crc32();
            case "md5":
                return Hashing.md5();
            default:
                throw new IllegalArgumentException("Can't find hash function with name " + name);
        }
    }

    /**
     * Constructs the base URL ({@code scheme://host:port}) for the incoming request,
     * honoring {@code x-forwarded-proto} and {@code x-forwarded-port} headers when present.
     *
     * @param request current HTTP servlet request
     * @return formatted base URL string
     */
    public static String constructBaseUrl(HttpServletRequest request) {
        return String.format("%s://%s:%d",
                getScheme(request),
                getDomainName(request),
                getPort(request));
    }

    /**
     * Resolves the request scheme, preferring the {@code x-forwarded-proto} header when set.
     *
     * @param request current HTTP servlet request
     * @return {@code http} or {@code https} scheme string
     */
    public static String getScheme(HttpServletRequest request){
        String scheme = request.getScheme();
        String forwardedProto = request.getHeader("x-forwarded-proto");
        if (forwardedProto != null) {
            scheme = forwardedProto;
        }
        return scheme;
    }

    /**
     * Returns the server host name from the servlet request.
     *
     * @param request current HTTP servlet request
     * @return domain or host name (without port)
     */
    public static String getDomainName(HttpServletRequest request){
        return request.getServerName();
    }

    /**
     * Returns the domain name with a non-default port appended when required.
     *
     * <p>Omits the port for standard HTTP (80) and HTTPS (443) combinations.
     *
     * @param request current HTTP servlet request
     * @return host name optionally suffixed with {@code :port}
     */
    public static String getDomainNameAndPort(HttpServletRequest request){
        String domainName = getDomainName(request);
        String scheme = getScheme(request);
        int port = MiscUtils.getPort(request);
        if (needsPort(scheme, port)) {
            domainName += ":" + port;
        }
        return domainName;
    }

    private static boolean needsPort(String scheme, int port) {
        boolean isHttpDefault = "http".equals(scheme.toLowerCase()) && port == 80;
        boolean isHttpsDefault = "https".equals(scheme.toLowerCase()) && port == 443;
        return !isHttpDefault && !isHttpsDefault;
    }

    /**
     * Resolves the effective server port, honoring {@code x-forwarded-port} and
     * {@code x-forwarded-proto} headers when present.
     *
     * @param request current HTTP servlet request
     * @return resolved port number
     */
    public static int getPort(HttpServletRequest request){
        String forwardedProto = request.getHeader("x-forwarded-proto");

        int serverPort = request.getServerPort();
        if (request.getHeader("x-forwarded-port") != null) {
            try {
                serverPort = request.getIntHeader("x-forwarded-port");
            } catch (NumberFormatException e) {
            }
        } else if (forwardedProto != null) {
            switch (forwardedProto) {
                case "http":
                    serverPort = 80;
                    break;
                case "https":
                    serverPort = 443;
                    break;
            }
        }
        return serverPort;
    }
}
