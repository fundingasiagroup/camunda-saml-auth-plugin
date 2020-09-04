package com.fundingsocieties.camunda.config;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
import org.camunda.bpm.engine.rest.util.EngineUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * SAML2 Authentication Provider for usage with JumpCloud IDP. You can customize this as per your IDP
 */
public class JumpCloudAuthenticationProvider extends ContainerBasedAuthenticationProvider {

    @Override
    public AuthenticationResult extractAuthenticatedUser(HttpServletRequest request, ProcessEngine engine) {
        Authentication auth = null;

        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            auth = context.getAuthentication();
        }

        if (auth instanceof Saml2Authentication) {
            Saml2Authentication authentication = (Saml2Authentication) SecurityContextHolder.getContext().getAuthentication();
            return contextAuthentication(authentication);
        } else {
            return AuthenticationResult.unsuccessful();
        }

    }

    private AuthenticationResult contextAuthentication(Saml2Authentication authentication) {
        String userId = authentication.getName();

        if (StringUtils.isEmpty(userId)) {
            return AuthenticationResult.unsuccessful();
        }

        // Authentication successful
        AuthenticationResult authenticationResult = new AuthenticationResult(userId, true);
        authenticationResult.setGroups(getUserGroups(userId, EngineUtil.lookupProcessEngine("default")));

        return authenticationResult;
    }

    private List<String> getUserGroups(String userId, ProcessEngine engine) {
        List<String> groupIds = new ArrayList<>();
        engine.getIdentityService().createGroupQuery().groupMember(userId).list()
                .forEach(g -> groupIds.add(g.getId()));
        return groupIds;
    }

}
