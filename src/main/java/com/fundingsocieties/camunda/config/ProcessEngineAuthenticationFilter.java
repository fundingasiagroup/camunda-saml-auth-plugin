package com.fundingsocieties.camunda.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.identity.Group;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessEngineAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();

        if (engine == null) {
            engine = ProcessEngines.getDefaultProcessEngine(false);
        }

        if (engine == null) {
            resp.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            String errMessage = "Default Process engine not available";
            ObjectMapper objectMapper = new ObjectMapper();
            resp.setContentType(MediaType.APPLICATION_JSON);
            objectMapper.writer().writeValue(resp.getWriter(), errMessage);
            resp.getWriter().flush();
            return;
        }
        try {
            if (req.getHeader("username") != null) {
                String userId = req.getHeader("username");
                setAuthenticatedUser(engine, userId, getGroupIdsForUser(engine,userId), new ArrayList<>());
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                resp.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
            }
        } finally {
            clearAuthentication(engine);
        }
    }

    protected void setAuthenticatedUser(ProcessEngine engine, String userId, List<String> groupIds, List<String> tenantIds) {
        engine.getIdentityService().setAuthentication(userId, groupIds, tenantIds);
    }
    private List<String> getGroupIdsForUser(ProcessEngine engine, String userId){
        List<Group> groups = engine.getIdentityService().createGroupQuery()
                .groupMember(userId)
                .list();
        return groups.stream().map(Group::getId).collect(Collectors.toList());
    }
    protected void clearAuthentication(ProcessEngine engine) {
        engine.getIdentityService().clearAuthentication();
    }
}

