package org.crue.hercules.sgi.auth.broker.saml.mappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.auth.broker.saml.mappers.rules.RuleEngine;
import org.crue.hercules.sgi.auth.broker.saml.mappers.rules.RuleSet;
import org.jboss.logging.Logger;
import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.saml.SAMLEndpoint;
import org.keycloak.broker.saml.SAMLIdentityProviderFactory;
import org.keycloak.dom.saml.v2.assertion.AssertionType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType;
import org.keycloak.dom.saml.v2.assertion.AttributeType;
import org.keycloak.models.GroupModel;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.IdentityProviderSyncMode;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderConfigProperty;

/**
 * Mapper de Identity Provider SAML que asigna grupos basándose en un ruleset
 * JSON declarativo.
 */
public class JsonRuleGroupMapper extends AbstractIdentityProviderMapper {

  public static final String PROVIDER_ID = "saml-json-rule-group-idp-mapper";

  public static final String CONFIG_RULES_JSON = "rules.json";
  public static final String CONFIG_CLEAR_BEFORE_RUN = "clear.before.run";

  private static final Logger LOGGER = Logger.getLogger(JsonRuleGroupMapper.class);

  private static final Set<IdentityProviderSyncMode> IDENTITY_PROVIDER_SYNC_MODES = new HashSet<>(
      Arrays.asList(IdentityProviderSyncMode.values()));

  public static final String[] COMPATIBLE_PROVIDERS = {
      SAMLIdentityProviderFactory.PROVIDER_ID
  };

  private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

  static {
    ProviderConfigProperty clearBefore = new ProviderConfigProperty();
    clearBefore.setName(CONFIG_CLEAR_BEFORE_RUN);
    clearBefore.setLabel("Clear groups before resolve");
    clearBefore.setHelpText("""
        If enabled the user leaves every existing group before the ruleset is applied.
        Use this when the IdP is the source of truth for group memberships.
        """);
    clearBefore.setType(ProviderConfigProperty.BOOLEAN_TYPE);
    CONFIG_PROPERTIES.add(clearBefore);

    ProviderConfigProperty rulesJson = new ProviderConfigProperty();
    rulesJson.setName(CONFIG_RULES_JSON);
    rulesJson.setLabel("Rules (JSON)");
    rulesJson.setHelpText(
        "JSON ruleset. See https://github.com/herculesproject/sgi/blob/main/docs/keycloak-saml-mappers.md for the schema.");
    rulesJson.setType(ProviderConfigProperty.SCRIPT_TYPE);
    CONFIG_PROPERTIES.add(rulesJson);
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
    return "Group Importer";
  }

  @Override
  public String getDisplayType() {
    return "JSON Rules Group Importer";
  }

  @Override
  public String getHelpText() {
    return "Grants Keycloak group memberships based on a declarative JSON ruleset evaluated against the SAML attribute set.";
  }

  @Override
  public void importNewUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    Set<String> grants = resolve(mapperModel, context);
    for (String groupPath : grants) {
      joinGroup(session, realm, user, groupPath);
    }
  }

  @Override
  public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user,
      IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    boolean clearBeforeRun = Boolean.parseBoolean(mapperModel.getConfig().get(CONFIG_CLEAR_BEFORE_RUN));
    Set<String> grants = resolve(mapperModel, context);
    if (clearBeforeRun) {
      List<GroupModel> existing = user.getGroupsStream().collect(Collectors.toList());
      for (GroupModel group : existing) {
        user.leaveGroup(group);
      }
    }

    for (String groupPath : grants) {
      joinGroup(session, realm, user, groupPath);
    }
  }

  Set<String> resolve(IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    String rulesText = mapperModel.getConfig().get(CONFIG_RULES_JSON);
    if (rulesText == null || rulesText.trim().isEmpty()) {
      LOGGER.warn("No ruleset configured for mapper " + mapperModel.getName() + "; granting nothing");
      return Collections.emptySet();
    }

    RuleSet ruleSet;
    try {
      ruleSet = RuleSet.parse(rulesText);
    } catch (IOException | RuntimeException e) {
      LOGGER.error("Invalid ruleset for mapper " + mapperModel.getName() + "; granting nothing", e);
      return Collections.emptySet();
    }

    Map<String, List<Object>> attributes = extractAttributes(context);
    return RuleEngine.apply(ruleSet, attributes);
  }

  static Map<String, List<Object>> extractAttributes(BrokeredIdentityContext context) {
    Map<String, List<Object>> attributes = new HashMap<>();
    AssertionType assertion = (AssertionType) context.getContextData().get(SAMLEndpoint.SAML_ASSERTION);
    if (assertion == null) {
      return attributes;
    }

    Set<AttributeStatementType> statements = assertion.getAttributeStatements();
    if (statements == null) {
      return attributes;
    }

    for (AttributeStatementType statement : statements) {
      for (AttributeStatementType.ASTChoiceType choice : statement.getAttributes()) {
        AttributeType attr = choice.getAttribute();
        List<Object> values = attr.getAttributeValue();
        if (values == null) {
          continue;
        }

        String name = attr.getName();
        if (name != null) {
          attributes.computeIfAbsent(name, k -> new ArrayList<>()).addAll(values);
        }

        String friendlyName = attr.getFriendlyName();
        if (friendlyName != null && !friendlyName.equals(name)) {
          attributes.computeIfAbsent(friendlyName, k -> new ArrayList<>()).addAll(values);
        }
      }
    }

    return attributes;
  }

  private void joinGroup(KeycloakSession session, RealmModel realm, UserModel user, String groupPath) {
    GroupModel group = KeycloakModelUtils.findGroupByPath(session, realm, groupPath);
    if (group == null) {
      LOGGER.warn("Group not found: " + groupPath);
      return;
    }

    user.joinGroup(group);
  }

}
