package org.crue.hercules.sgi.auth.broker.saml.mappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

public class AttributeTemplateMapper extends AbstractIdentityProviderMapper {

  protected static final String[] COMPATIBLE_PROVIDERS = { SAMLIdentityProviderFactory.PROVIDER_ID };

  public static final String TEMPLATE = "template";
  public static final String TARGET = "target.attribute";

  private static final Map<String, UnaryOperator<String>> TRANSFORMERS = new HashMap<>();

  private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();
  private static final Set<IdentityProviderSyncMode> IDENTITY_PROVIDER_SYNC_MODES = new HashSet<>(
      Arrays.asList(IdentityProviderSyncMode.values()));

  static {
    ProviderConfigProperty property;
    property = new ProviderConfigProperty();
    property.setName(TEMPLATE);
    property.setLabel("Template");
    property.setHelpText(
        "Template to use to format the user attribute to import.  Substitutions are enclosed in ${} and reference SAML attribute by the attribute name or friendly name. \n"
            + "The substitution can be converted to upper or lower case by appending |uppercase or |lowercase to the substituted value, e.g. '${NAMEID | lowercase}");
    property.setType(ProviderConfigProperty.STRING_TYPE);
    property.setDefaultValue("${NAMEID}");
    configProperties.add(property);

    property = new ProviderConfigProperty();
    property.setName(TARGET);
    property.setLabel("User Attribute Name");
    property.setHelpText(
        "User attribute name to store template result. Use email, lastName, and firstName to map to those predefined user properties.");
    property.setType(ProviderConfigProperty.STRING_TYPE);
    configProperties.add(property);

    TRANSFORMERS.put("uppercase", String::toUpperCase);
    TRANSFORMERS.put("lowercase", String::toLowerCase);
  }

  public static final String PROVIDER_ID = "saml-attribute-template-idp-mapper";

  @Override
  public boolean supportsSyncMode(IdentityProviderSyncMode syncMode) {
    return IDENTITY_PROVIDER_SYNC_MODES.contains(syncMode);
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
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
    return "Attribute Template Importer";
  }

  @Override
  public String getHelpText() {
    return "Format the attribute to import.";
  }

  @Override
  public void importNewUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    mapAttribute(user, mapperModel, context);
  }

  @Override
  public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    mapAttribute(user, mapperModel, context);
  }

  private static final Pattern SUBSTITUTION = Pattern.compile("\\$\\{([^}]+?)(?:\\s*\\|\\s*(\\S+)\\s*)?\\}");

  private void mapAttribute(UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    String userAttributeName = mapperModel.getConfig().get(TARGET);
    AssertionType assertion = (AssertionType) context.getContextData().get(SAMLEndpoint.SAML_ASSERTION);
    String template = mapperModel.getConfig().get(TEMPLATE);
    Matcher m = SUBSTITUTION.matcher(template);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      String variable = m.group(1);
      UnaryOperator<String> transformer = Optional.ofNullable(m.group(2)).map(TRANSFORMERS::get)
          .orElse(UnaryOperator.identity());

      String value = "";
      for (AttributeStatementType statement : assertion.getAttributeStatements()) {
        for (AttributeStatementType.ASTChoiceType choice : statement.getAttributes()) {
          AttributeType attr = choice.getAttribute();
          if (variable.equals(attr.getName()) || variable.equals(attr.getFriendlyName())) {
            List<Object> attributeValue = attr.getAttributeValue();
            if (attributeValue != null && !attributeValue.isEmpty()) {
              value = attributeValue.get(0).toString();
            }
            break;
          }
        }
      }
      m.appendReplacement(sb, transformer.apply(value));
    }
    m.appendTail(sb);
    setUserAttribute(user, userAttributeName, sb.toString());
  }

  private void setUserAttribute(UserModel user, String attrName, Object attrValue) {
    if (attrValue.getClass().isArray()) {
      attrValue = Arrays.asList((Object[]) attrValue);
    }
    if (attrValue instanceof List) {
      user.setAttribute(attrName, ((List<?>) attrValue).stream().map(Object::toString).collect(Collectors.toList()));
    } else {
      if(attrName.equals(UserModel.FIRST_NAME)) {
        user.setFirstName(attrValue.toString());
      }
      else if (attrName.equals(UserModel.LAST_NAME)) {
        user.setLastName(attrValue.toString());
      }
      else if (attrName.equals(UserModel.EMAIL)) {
        user.setEmail(attrValue.toString());
      }
      else {
        user.setSingleAttribute(attrName, attrValue.toString());
      }
    }
  }

}