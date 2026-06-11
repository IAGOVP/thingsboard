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
package org.thingsboard.server.service.security.auth.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.service.security.exception.AuthMethodNotSupportedException;
import org.thingsboard.server.service.security.model.UserPrincipal;

import java.io.IOException;
/**
 * Servlet filter that handles username/password REST login login/token requests.
 */

@Slf4j
public class RestPublicLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;


    public RestPublicLoginProcessingFilter(String defaultProcessUrl, AuthenticationSuccessHandler successHandler,
                                     AuthenticationFailureHandler failureHandler) {
        super(defaultProcessUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }
    /**
     * Attempt authentication.
     *
     * @param request request (HttpServletRequest)
     * @param response response (HttpServletResponse)
     * @return {@link Authentication} result
     * @throws AuthenticationException if the operation fails
     * @throws IOException if the operation fails
     * @throws ServletException if the operation fails
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            if(log.isDebugEnabled()) {
                log.debug("Authentication method not supported. Request method: " + request.getMethod());
            }
            throw new AuthMethodNotSupportedException("Authentication method not supported");
        }

        PublicLoginRequest loginRequest;
        try {
            loginRequest = JacksonUtil.fromReader(request.getReader(), PublicLoginRequest.class);
        } catch (Exception e) {
            throw new AuthenticationServiceException("Invalid public login request payload");
        }

        if (StringUtils.isBlank(loginRequest.getPublicId())) {
            throw new AuthenticationServiceException("Public Id is not provided");
        }

        UserPrincipal principal = new UserPrincipal(UserPrincipal.Type.PUBLIC_ID, loginRequest.getPublicId());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, "");

        return this.getAuthenticationManager().authenticate(token);
    }

    /**
     * Successful authentication.
     *
     * @param request request (HttpServletRequest)
     * @param response response (HttpServletResponse)
     * @param chain chain (FilterChain)
     * @param authResult auth result (Authentication)
     * @throws IOException if the operation fails
     * @throws ServletException if the operation fails
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    /**
     * Unsuccessful authentication.
     *
     * @param request request (HttpServletRequest)
     * @param response response (HttpServletResponse)
     * @param failed failed (AuthenticationException)
     * @throws IOException if the operation fails
     * @throws ServletException if the operation fails
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
