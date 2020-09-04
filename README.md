# Camunda authentication using SAML and custom Identity Service




This project contains template to implement SAML login for your users using any identity provider (e.g. JumpCloud).
It also implements a custom Process Engine Plugin to replace Camunda's default identity service with your own identity service as a store for users, groups and tenants.

  

### Why should you use this template?

Camunda's documentation for using SAML is quite limited to using Keycloak as an identity provider and often developers will try to customize their code to implement SAML authentication with their own choice of Identity Provider. This repository contains complete implementation of SAML using any Identity provider along with custom identity store.

### SAML Related Files

To configure your SAML config you need to modify the follwing files as per your own requirments:
 - ConfigSecurity.java 
 - JumpCloudAuthenticationProvider.java
 - application.yml
 - saml-cert.pem

The REST-APIs authentication can be modified (if required) in the ProcessEngineAuthenticationFilter.java file.

### Custom Process Engine Plugin for replacing default identity service

A custom process engine plugin is defined and registered in the CustomIdentityProviderPlugin.java file
This plugin registers the CustomIdentityProvider which implements the ReadOnlyIdentityProvider interface definded by Camunda to provide custom queries.

We use an external REST based service to provide the users, group info. You can implement it in any way you like by modifying the files.



