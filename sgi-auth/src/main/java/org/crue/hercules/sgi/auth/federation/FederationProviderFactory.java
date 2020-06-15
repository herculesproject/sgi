/**
 *
 */
package org.crue.hercules.sgi.auth.federation;

import java.util.List;

import javax.naming.InitialContext;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

/**
 * @author Treelogic
 */
public class FederationProviderFactory implements UserStorageProviderFactory<FederationProvider> {
  private static final Logger LOGGER = Logger.getLogger(FederationProviderFactory.class);

  public static final String PROVIDER_NAME = "sgi";

  @Override
  public FederationProvider create(KeycloakSession session, ComponentModel model) {
    try {
      InitialContext ctx = new InitialContext();
      FederationProvider provider = (FederationProvider) ctx
          .lookup("java:global/sgi-auth/" + FederationProvider.class.getSimpleName());
      provider.setModel(model);
      provider.setSession(session);
      return provider;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getId() {
    return PROVIDER_NAME;
  }

  protected static final List<ProviderConfigProperty> configProperties;

  static {
    configProperties = getConfigProps();
  }

  private static List<ProviderConfigProperty> getConfigProps() {
    return ProviderConfigurationBuilder.create().property().name("jdbc-connection").label("jdbc-connection")
        .helpText("jdbc-connection.tooltip").type(ProviderConfigProperty.STRING_TYPE).add().build();
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return configProperties;
  }
}
