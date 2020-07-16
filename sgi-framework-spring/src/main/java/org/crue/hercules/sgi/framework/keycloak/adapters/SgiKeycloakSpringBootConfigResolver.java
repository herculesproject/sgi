package org.crue.hercules.sgi.framework.keycloak.adapters;

import org.crue.hercules.sgi.framework.keycloak.representations.adpaters.config.SgiKeycloakSpringBootProperties;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.OIDCHttpFacade;

public class SgiKeycloakSpringBootConfigResolver implements KeycloakConfigResolver {
  private final KeycloakDeployment keycloakDeployment;

  public SgiKeycloakSpringBootConfigResolver(SgiKeycloakSpringBootProperties properties) {
    keycloakDeployment = KeycloakDeploymentBuilder.build(properties);
  }

  @Override
  public KeycloakDeployment resolve(OIDCHttpFacade.Request facade) {
    return keycloakDeployment;
  }
}
