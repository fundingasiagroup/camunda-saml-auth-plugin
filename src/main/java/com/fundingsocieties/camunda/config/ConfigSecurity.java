package com.fundingsocieties.camunda.config;

import org.camunda.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml2.credentials.Saml2X509Credential;
import org.springframework.security.saml2.credentials.Saml2X509Credential.Saml2X509CredentialType;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;

import javax.servlet.Filter;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;

@EnableWebSecurity
public class ConfigSecurity extends WebSecurityConfigurerAdapter {

    @Value("sso-endpoint")
    private String webSsoEndpoint;

    @Bean
    public RelyingPartyRegistration jumpCloudRegistration() throws CertificateException, IOException {

        final String idpEntityId = "camunda";
        final String registrationId = "camunda";
        final String localEntityIdTemplate = "{baseUrl}/saml2/service-provider-metadata/{registrationId}";
        final String acsUrlTemplate = "{baseUrl}/login/saml2/sso/{registrationId}";
        ClassLoader classLoader = getClass().getClassLoader();
        final InputStream is = classLoader.getResourceAsStream("saml-cert.pem");
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final X509Certificate cert = (X509Certificate) cf.generateCertificate(is);

        final Saml2X509Credential credential = new Saml2X509Credential(cert,
                Saml2X509CredentialType.VERIFICATION, Saml2X509CredentialType.ENCRYPTION);

        return RelyingPartyRegistration.withRegistrationId(registrationId)
                .providerDetails(config -> config.entityId(idpEntityId))
                .providerDetails(config -> config.webSsoUrl(webSsoEndpoint))
                .providerDetails(config -> config.signAuthNRequest(false))
                .credentials(c -> c.add(credential))
                .localEntityIdTemplate(localEntityIdTemplate)
                .assertionConsumerServiceUrlTemplate(acsUrlTemplate)
                .build();
    }

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository(
            final RelyingPartyRegistration jumpCloudRegistration) {

        return new InMemoryRelyingPartyRegistrationRepository(jumpCloudRegistration);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http
                .csrf().ignoringAntMatchers("/camunda/api/**")
                .and()
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/camunda/app/**")
                .authenticated()
                .anyRequest()
                .permitAll()
                .and()
                .saml2Login().defaultSuccessUrl("/camunda/app/welcome/default/#!/welcome");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean containerBasedAuthenticationFilter(){

        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new ContainerBasedAuthenticationFilter());

        filterRegistration.setInitParameters(Collections.singletonMap("authentication-provider", "com.fundingsocieties.workflow.config.JumpCloudAuthenticationProvider"));
        filterRegistration.setOrder(101); // make sure the filter is registered after the Spring Security Filter Chain
        filterRegistration.addUrlPatterns("/*");
        return filterRegistration;
    }

    @Bean
    public FilterRegistrationBean processEngineAuthenticationFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setName("camunda-kong-auth");
        registration.addUrlPatterns("/engine-rest/*");
        registration.addInitParameter("authentication-provider", "com.fundingsocieties.workflow.config.ProcessEngineAuthenticationFilterKong");
        registration.setOrder(101);
        registration.setFilter(getProcessEngineAuthenticationFilter());
        return registration;
    }

    @Bean
    public Filter getProcessEngineAuthenticationFilter() {
        return new ProcessEngineAuthenticationFilter();
    }


}
