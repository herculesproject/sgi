package org.crue.hercules.sgi.auth.broker.saml.mappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.saml.SAMLEndpoint;
import org.keycloak.broker.saml.SAMLIdentityProviderFactory;
import org.keycloak.dom.saml.v2.assertion.AssertionType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.IdentityProviderSyncMode;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.ScriptModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.scripting.EvaluatableScriptAdapter;
import org.keycloak.scripting.ScriptingProvider;

public class ScriptToAttributeMapper extends AbstractIdentityProviderMapper {

  public static final String PROVIDER_ID = "saml-script-attribute-idp-mapper";
  public static final String SOURCE_ATTRIBUTE = "source.attribute";
  public static final String USER_ATTRIBUTE = "target.attribute";
  public static final String SCRIPT = "script";

  private static final Logger LOGGER = Logger.getLogger(ScriptToAttributeMapper.class);

  private static final Set<IdentityProviderSyncMode> IDENTITY_PROVIDER_SYNC_MODES = new HashSet<>(
      Arrays.asList(IdentityProviderSyncMode.values()));

  protected static final String[] COMPATIBLE_PROVIDERS = {
      SAMLIdentityProviderFactory.PROVIDER_ID
  };

  private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

  static {
    ProviderConfigProperty sourceAttribute = new ProviderConfigProperty();
    sourceAttribute.setName(SOURCE_ATTRIBUTE);
    sourceAttribute.setLabel("Attribute Name");
    sourceAttribute.setHelpText("Name of attribute received from SAML that contains the value to be mapped");
    sourceAttribute.setType(ProviderConfigProperty.STRING_TYPE);
    configProperties.add(sourceAttribute);

    ProviderConfigProperty userAttribute = new ProviderConfigProperty();
    userAttribute.setName(USER_ATTRIBUTE);
    userAttribute.setLabel("User Attribute Name");
    userAttribute.setHelpText("User attribute name to store saml attribute");
    userAttribute.setType(ProviderConfigProperty.STRING_TYPE);
    configProperties.add(userAttribute);

    ProviderConfigProperty scriptProperty = new ProviderConfigProperty();
    scriptProperty.setName(SCRIPT);
    scriptProperty.setLabel("Script");
    scriptProperty.setHelpText(
        "Script to compute the user attribute. \n" + //
            " Available variables: \n" + //
            " 'sourceAttributeValues' - String with source attribute.\n" + //
            "To use: the last statement is the value returned to Java.\n"//
    );
    scriptProperty.setType(ProviderConfigProperty.SCRIPT_TYPE);
    configProperties.add(scriptProperty);
  }

  @Override
  public boolean supportsSyncMode(IdentityProviderSyncMode syncMode) {
    return IDENTITY_PROVIDER_SYNC_MODES.contains(syncMode);
  }

  @Override
  public List<org.keycloak.provider.ProviderConfigProperty> getConfigProperties() {
    return configProperties;
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
    return "Script to Attribute";
  }

  @Override
  public String getHelpText() {
    return "Import the result of proccessing the declared saml attribute if it exists in assertion into the specified user property or attribute.";
  }

  @Override
  public void importNewUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    mapAttribute(session, realm, user, mapperModel, context);
  }

  @Override
  public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    mapAttribute(session, realm, user, mapperModel, context);
  }

  private void mapAttribute(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {

    String sourceAttributeName = mapperModel.getConfig().get(SOURCE_ATTRIBUTE);
    String userAttributeName = mapperModel.getConfig().get(USER_ATTRIBUTE);
    String scriptSource = mapperModel.getConfig().get(SCRIPT);
    if (sourceAttributeName == null || sourceAttributeName.isEmpty()
        || userAttributeName == null || userAttributeName.isEmpty()
        || scriptSource == null || scriptSource.isEmpty()) {
      return;
    }

    AssertionType assertion = (AssertionType) context.getContextData().get(SAMLEndpoint.SAML_ASSERTION);
    Set<AttributeStatementType> attributeAssertions = assertion.getAttributeStatements();
    if (attributeAssertions == null) {
      return;
    }

    ScriptingProvider scripting = session.getProvider(ScriptingProvider.class);
    ScriptModel scriptModel = scripting.createScript(realm.getId(), ScriptModel.TEXT_JAVASCRIPT,
        "saml-script-to-attr" + mapperModel.getName(), scriptSource, null);
    EvaluatableScriptAdapter script = scripting.prepareEvaluatableScript(scriptModel);

    List<Object> attrValues = attributeAssertions.stream()
        .flatMap(statements -> statements.getAttributes().stream())
        .filter(astChoiceType -> astChoiceType.getAttribute().getName().equals(sourceAttributeName))
        .map(astChoiceType -> astChoiceType.getAttribute().getAttributeValue()).findFirst().orElse(null);

    try {
      Object attributeValue = script.eval(bindings -> bindings.put("sourceAttributeValues", attrValues));

      setUserAttribute(user, userAttributeName, attributeValue);
    } catch (Exception ex) {
      LOGGER.error("Error during execution of Mapper script", ex);
    }
  }

  private void setUserAttribute(UserModel user, String attrName, Object attrValue) {
    if (attrValue.getClass().isArray()) {
      attrValue = Arrays.asList((Object[]) attrValue);
    }
    if (attrValue instanceof List) {
      user.setAttribute(attrName, ((List<?>) attrValue).stream().map(Object::toString).collect(Collectors.toList()));
    } else {
      user.setSingleAttribute(attrName, attrValue.toString());
    }
  }

}