package org.crue.hercules.sgi.auth.broker.saml.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.models.GroupModel;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;

/**
 * Tests unitarios de {@link JsonRuleGroupMapper}.
 */
class JsonRuleGroupMapperTest {

  private final JsonRuleGroupMapper mapper = new JsonRuleGroupMapper();

  private static final String RULES = """
      {
        "rules": [
          {
            "name": "admin",
            "grant": [
              "/GRP-A"
            ],
            "when": {
              "attribute": "memberOf",
              "anyOf": [
                "x"
              ]
            }
          }
        ]
      }
      """;

  @Test
  void extractAttributes_ReturnsAttributesIndexedByNameAndFriendlyName() {
    // given: dos statements SAML del mismo atributo, con Name y FriendlyName
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("urn:oid:memberOf", "memberOf", "a", "b"),
        SamlAssertions.attribute("urn:oid:memberOf", "memberOf", "c"));

    // when: se extraen los atributos
    Map<String, List<Object>> attrs = JsonRuleGroupMapper.extractAttributes(ctx);

    // then: quedan indexados por ambas claves y con los valores fusionados
    assertEquals(List.of("a", "b", "c"), attrs.get("urn:oid:memberOf"));
    assertEquals(List.of("a", "b", "c"), attrs.get("memberOf"));
  }

  @Test
  void extractAttributes_WithoutAssertion_ReturnsEmpty() {
    // given: un contexto sin aserción SAML
    BrokeredIdentityContext ctx = SamlAssertions.emptyContext();

    // when: se extraen los atributos
    Map<String, List<Object>> attrs = JsonRuleGroupMapper.extractAttributes(ctx);

    // then: el mapa está vacío
    assertTrue(attrs.isEmpty());
  }

  @Test
  void resolve_WithInlineRules_ReturnsGrants() {
    // given: la configuración lleva las reglas inline y el atributo casa
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("memberOf", null, "x"));
    IdentityProviderMapperModel model = model(Map.of(JsonRuleGroupMapper.CONFIG_RULES_JSON, RULES));

    // when: se resuelven los grupos
    Set<String> grants = mapper.resolve(model, ctx);

    // then: se concede el grupo de la regla
    assertEquals(Set.of("/GRP-A"), grants);
  }

  @Test
  void resolve_WithoutRuleset_ReturnsEmpty() {
    // given: una configuración sin reglas
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("memberOf", null, "x"));

    // when: se resuelven los grupos
    Set<String> grants = mapper.resolve(model(Map.of()), ctx);

    // then: no se concede nada
    assertTrue(grants.isEmpty());
  }

  @Test
  void resolve_WithInvalidRuleset_ReturnsEmpty() {
    // given: un ruleset con JSON malformado
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("memberOf", null, "x"));
    IdentityProviderMapperModel model = model(
        Map.of(JsonRuleGroupMapper.CONFIG_RULES_JSON, "{ not valid"));

    // when: se resuelven los grupos
    Set<String> grants = mapper.resolve(model, ctx);

    // then: se degrada a no conceder nada (no rompe el login)
    assertTrue(grants.isEmpty());
  }

  @Test
  void importNewUser_JoinsGrantedGroup() {
    // given: la regla concede /GRP-A y el grupo existe en el realm
    KeycloakSession session = mock(KeycloakSession.class);
    RealmModel realm = mock(RealmModel.class);
    UserModel user = mock(UserModel.class);
    GroupModel group = mock(GroupModel.class);
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("memberOf", null, "x"));
    IdentityProviderMapperModel model = model(Map.of(JsonRuleGroupMapper.CONFIG_RULES_JSON, RULES));

    try (MockedStatic<KeycloakModelUtils> ms = mockStatic(KeycloakModelUtils.class)) {
      ms.when(() -> KeycloakModelUtils.findGroupByPath(session, realm, "/GRP-A")).thenReturn(group);

      // when: se importa el usuario nuevo
      mapper.importNewUser(session, realm, user, model, ctx);

      // then: el usuario se une al grupo
      verify(user).joinGroup(group);
    }
  }

  @Test
  void importNewUser_WithMissingGroup_DoesNotJoin() {
    // given: la regla concede /GRP-A pero el grupo no existe en el realm
    KeycloakSession session = mock(KeycloakSession.class);
    RealmModel realm = mock(RealmModel.class);
    UserModel user = mock(UserModel.class);
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("memberOf", null, "x"));
    IdentityProviderMapperModel model = model(Map.of(JsonRuleGroupMapper.CONFIG_RULES_JSON, RULES));

    try (MockedStatic<KeycloakModelUtils> ms = mockStatic(KeycloakModelUtils.class)) {
      ms.when(() -> KeycloakModelUtils.findGroupByPath(session, realm, "/GRP-A")).thenReturn(null);

      // when: se importa el usuario nuevo
      mapper.importNewUser(session, realm, user, model, ctx);

      // then: no se intenta unir a ningún grupo
      verify(user, never()).joinGroup(ArgumentMatchers.any());
    }
  }

  @Test
  void updateBrokeredUser_WithClearBeforeRun_LeavesThenJoins() {
    // given: clear.before.run activo, un grupo existente y una regla que concede
    // otro
    KeycloakSession session = mock(KeycloakSession.class);
    RealmModel realm = mock(RealmModel.class);
    UserModel user = mock(UserModel.class);
    GroupModel existing = mock(GroupModel.class);
    GroupModel granted = mock(GroupModel.class);
    when(user.getGroupsStream()).thenReturn(Stream.of(existing));
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("memberOf", null, "x"));
    Map<String, String> config = new HashMap<>();
    config.put(JsonRuleGroupMapper.CONFIG_RULES_JSON, RULES);
    config.put(JsonRuleGroupMapper.CONFIG_CLEAR_BEFORE_RUN, "true");

    try (MockedStatic<KeycloakModelUtils> ms = mockStatic(KeycloakModelUtils.class)) {
      ms.when(() -> KeycloakModelUtils.findGroupByPath(eq(session), eq(realm), eq("/GRP-A")))
          .thenReturn(granted);

      // when: se actualiza el usuario
      mapper.updateBrokeredUser(session, realm, user, model(config), ctx);

      // then: primero abandona el grupo previo y luego se une al concedido
      verify(user).leaveGroup(existing);
      verify(user).joinGroup(granted);
    }
  }

  private static IdentityProviderMapperModel model(Map<String, String> config) {
    IdentityProviderMapperModel m = new IdentityProviderMapperModel();
    m.setName("test-mapper");
    m.setConfig(new HashMap<>(config));
    return m;
  }
}
