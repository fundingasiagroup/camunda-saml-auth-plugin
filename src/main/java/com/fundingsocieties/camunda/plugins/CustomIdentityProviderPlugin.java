package com.fundingsocieties.camunda.plugins;

import com.fundingsocieties.camunda.idservice.CustomIdentityProviderFactory;
import com.fundingsocieties.camunda.services.GroupService;
import com.fundingsocieties.camunda.services.UserService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This is the custom process engine plugin to replace Camunda's default identity service
 */
@Component
public class CustomIdentityProviderPlugin implements ProcessEnginePlugin {

    private final UserService userService;

    private final GroupService groupService;

    @Autowired
    public CustomIdentityProviderPlugin(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        CustomIdentityProviderFactory identityProviderFactory = new CustomIdentityProviderFactory(userService, groupService);
        processEngineConfiguration.setIdentityProviderSessionFactory(identityProviderFactory);
    }

    @Override
    public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

    }

    @Override
    public void postProcessEngineBuild(ProcessEngine processEngine) {

    }
}
