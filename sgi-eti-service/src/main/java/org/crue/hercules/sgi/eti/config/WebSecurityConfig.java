package org.crue.hercules.sgi.eti.config;

import org.crue.hercules.sgi.framework.keycloak.representations.adpaters.config.SgiKeycloakSpringBootProperties;
import org.crue.hercules.sgi.framework.web.config.SgiWebSecurityConfig;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("!SECURITY_MOCK") // If we don't use the SECURITY_MOCK profile, we use this bean!
@KeycloakConfiguration
@EnableConfigurationProperties(SgiKeycloakSpringBootProperties.class)
public class WebSecurityConfig extends SgiWebSecurityConfig {
}
