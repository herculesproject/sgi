package org.crue.hercules.sgi.auth.broker.saml.mappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;
import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.saml.SAMLEndpoint;
import org.keycloak.broker.saml.SAMLIdentityProviderFactory;
import org.keycloak.dom.saml.v2.assertion.AssertionType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType;
import org.keycloak.dom.saml.v2.assertion.AttributeType;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.IdentityProviderSyncMode;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;

/**
 * Mapper de Identity Provider SAML que selecciona el valor de un atributo SAML
 * que empieza por un prefijo dado, le quita ese prefijo y guarda el resto en un
 * atributo de usuario.
 * <p>
 * Comportamiento: entre los valores del atributo origen configurado, se toma el
 * primer valor que empieza por el prefijo, se le elimina dicho prefijo y se
 * almacena como el atributo de usuario destino. El prefijo actúa como
 * <strong>selector</strong>: los valores que no empiezan por él se ignoran, y
 * si ninguno coincide no se escribe nada (el atributo se deja como estaba). Si
 * el nombre del atributo destino es uno de {@code email}, {@code firstName} o
 * {@code lastName}, se establece en su lugar la propiedad correspondiente de
 * {@link UserModel}.
 */
public class PrefixMatchStripAttributeMapper extends AbstractIdentityProviderMapper {

  public static final String PROVIDER_ID = "saml-prefix-match-strip-attribute-idp-mapper";

  public static final String CONFIG_SOURCE_ATTRIBUTE = "source.attribute";
  public static final String CONFIG_PREFIX = "prefix";
  public static final String CONFIG_TARGET_ATTRIBUTE = "target.attribute";

  private static final Logger LOGGER = Logger.getLogger(PrefixMatchStripAttributeMapper.class);

  private static final Set<IdentityProviderSyncMode> IDENTITY_PROVIDER_SYNC_MODES = new HashSet<>(
      Arrays.asList(IdentityProviderSyncMode.values()));

  public static final String[] COMPATIBLE_PROVIDERS = {
      SAMLIdentityProviderFactory.PROVIDER_ID
  };

  private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

  static {
    ProviderConfigProperty source = new ProviderConfigProperty();
    source.setName(CONFIG_SOURCE_ATTRIBUTE);
    source.setLabel("SAML attribute name");
    source.setHelpText("Name of the SAML attribute (or its FriendlyName) whose values are scanned.");
    source.setType(ProviderConfigProperty.STRING_TYPE);
    CONFIG_PROPERTIES.add(source);

    ProviderConfigProperty prefix = new ProviderConfigProperty();
    prefix.setName(CONFIG_PREFIX);
    prefix.setLabel("Prefix to match and strip");
    prefix.setHelpText(
        "The first attribute value that starts with this prefix is selected; the prefix is then "
            + "stripped from it. Values that do not start with the prefix are ignored.");
    prefix.setType(ProviderConfigProperty.STRING_TYPE);
    CONFIG_PROPERTIES.add(prefix);

    ProviderConfigProperty target = new ProviderConfigProperty();
    target.setName(CONFIG_TARGET_ATTRIBUTE);
    target.setLabel("User attribute name");
    target.setHelpText(
        "User attribute to store the stripped value. Use 'email', 'firstName' or 'lastName' "
            + "to map to the predefined user properties.");
    target.setType(ProviderConfigProperty.STRING_TYPE);
    CONFIG_PROPERTIES.add(target);
  }

  @Override
  public boolean supportsSyncMode(IdentityProviderSyncMode syncMode) {
    return IDENTITY_PROVIDER_SYNC_MODES.contains(syncMode);
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return CONFIG_PROPERTIES;
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }

  @Override
  public String[] getCompatibleProviders() {
    return COMPATIBLE_PROVIDERS;
  }

  @Override
  public String getDisplayCategory() {
    return "Attribute Importer";
  }

  @Override
  public String getDisplayType() {
    return "Prefix Match & Strip Attribute Importer";
  }

  @Override
  public String getHelpText() {
    return "Selects the first SAML attribute value that starts with the given prefix, strips the "
        + "prefix and stores the rest in the user attribute. Values without the prefix are ignored.";
  }

  @Override
  public void importNewUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    apply(user, mapperModel, context);
  }

  @Override
  public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    apply(user, mapperModel, context);
  }

  void apply(UserModel user, IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    String sourceAttribute = mapperModel.getConfig().get(CONFIG_SOURCE_ATTRIBUTE);
    String prefix = mapperModel.getConfig().get(CONFIG_PREFIX);
    String targetAttribute = mapperModel.getConfig().get(CONFIG_TARGET_ATTRIBUTE);

    if (isBlank(sourceAttribute) || prefix == null || isBlank(targetAttribute)) {
      LOGGER.warn("PrefixMatchStripAttributeMapper " + mapperModel.getName()
          + " is missing required configuration; skipping");
      return;
    }

    String stripped = extract(context, sourceAttribute, prefix);
    if (stripped == null) {
      return;
    }
    setUserAttribute(user, targetAttribute, stripped);
  }

  static String extract(BrokeredIdentityContext context, String sourceAttribute, String prefix) {
    AssertionType assertion = (AssertionType) context.getContextData().get(SAMLEndpoint.SAML_ASSERTION);
    if (assertion == null) {
      return null;
    }
    Set<AttributeStatementType> statements = assertion.getAttributeStatements();
    if (statements == null) {
      return null;
    }
    for (AttributeStatementType statement : statements) {
      for (AttributeStatementType.ASTChoiceType choice : statement.getAttributes()) {
        AttributeType attr = choice.getAttribute();
        if (!sourceAttribute.equals(attr.getName())
            && !sourceAttribute.equals(attr.getFriendlyName())) {
          continue;
        }
        List<Object> values = attr.getAttributeValue();
        if (values == null) {
          continue;
        }
        for (Object value : values) {
          if (value == null) {
            continue;
          }
          String s = value.toString();
          if (s.startsWith(prefix)) {
            return s.substring(prefix.length());
          }
        }
      }
    }
    return null;
  }

  private static void setUserAttribute(UserModel user, String attrName, String value) {
    if (UserModel.FIRST_NAME.equals(attrName)) {
      user.setFirstName(value);
    } else if (UserModel.LAST_NAME.equals(attrName)) {
      user.setLastName(value);
    } else if (UserModel.EMAIL.equals(attrName)) {
      user.setEmail(value);
    } else {
      user.setAttribute(attrName, Collections.singletonList(value));
    }
  }

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }
}
