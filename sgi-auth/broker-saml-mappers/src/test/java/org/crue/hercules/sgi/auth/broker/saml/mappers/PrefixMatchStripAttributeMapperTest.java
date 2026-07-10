package org.crue.hercules.sgi.auth.broker.saml.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.UserModel;
import org.mockito.ArgumentMatchers;

/**
 * Tests unitarios de {@link PrefixMatchStripAttributeMapper}.
 */
class PrefixMatchStripAttributeMapperTest {

  private final PrefixMatchStripAttributeMapper mapper = new PrefixMatchStripAttributeMapper();

  @Test
  void extract_ReturnsValueOfFirstMatchWithoutPrefix() {
    // given: un atributo SAML con un valor que empieza por el prefijo buscado
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("schacPersonalUniqueCode", null,
            "urn:schac:noPrefix:value", "urn:mace:sgi:userRefId:12345"));

    // when: se extrae el valor quitando el prefijo
    String result = PrefixMatchStripAttributeMapper.extract(ctx,
        "schacPersonalUniqueCode", "urn:mace:sgi:userRefId:");

    // then: se obtiene el resto del primer valor que casa con el prefijo
    assertEquals("12345", result);
  }

  @Test
  void extract_WithFriendlyName_ReturnsValue() {
    // given: el atributo se identifica por su FriendlyName, no por su Name
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("urn:oid:1.3.6.1.4.1.25178.1.2.14", "schacPersonalUniqueCode",
            "prefix:99"));

    // when: se extrae usando el FriendlyName como nombre de origen
    String result = PrefixMatchStripAttributeMapper.extract(ctx, "schacPersonalUniqueCode", "prefix:");

    // then: se resuelve igualmente
    assertEquals("99", result);
  }

  @Test
  void extract_WithoutMatchingPrefix_ReturnsNull() {
    // given: ningún valor del atributo empieza por el prefijo
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("attr", null, "other:1", "other:2"));

    // when: se intenta extraer
    String result = PrefixMatchStripAttributeMapper.extract(ctx, "attr", "prefix:");

    // then: no hay nada que devolver
    assertNull(result);
  }

  @Test
  void extract_WithoutAssertion_ReturnsNull() {
    // given: un contexto sin aserción SAML
    BrokeredIdentityContext ctx = SamlAssertions.emptyContext();

    // when: se intenta extraer
    String result = PrefixMatchStripAttributeMapper.extract(ctx, "attr", "prefix:");

    // then: no hay nada que devolver
    assertNull(result);
  }

  @Test
  void apply_SetsCustomUserAttribute() {
    // given: un usuario y una configuración con atributo destino no estándar
    UserModel user = mock(UserModel.class);
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("schacCode", null, "urn:sgi:ref:abc"));
    IdentityProviderMapperModel model = model(Map.of(
        PrefixMatchStripAttributeMapper.CONFIG_SOURCE_ATTRIBUTE, "schacCode",
        PrefixMatchStripAttributeMapper.CONFIG_PREFIX, "urn:sgi:ref:",
        PrefixMatchStripAttributeMapper.CONFIG_TARGET_ATTRIBUTE, "userRefId"));

    // when: se aplica el mapper
    mapper.apply(user, model, ctx);

    // then: el valor sin prefijo se guarda como atributo de usuario
    verify(user).setAttribute("userRefId", List.of("abc"));
  }

  @Test
  void apply_WithEmailTarget_SetsUserProperty() {
    // given: el atributo destino es una propiedad estándar (email)
    UserModel user = mock(UserModel.class);
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("mail", null, "p:john@example.org"));
    IdentityProviderMapperModel model = model(Map.of(
        PrefixMatchStripAttributeMapper.CONFIG_SOURCE_ATTRIBUTE, "mail",
        PrefixMatchStripAttributeMapper.CONFIG_PREFIX, "p:",
        PrefixMatchStripAttributeMapper.CONFIG_TARGET_ATTRIBUTE, UserModel.EMAIL));

    // when: se aplica el mapper
    mapper.apply(user, model, ctx);

    // then: se usa el setter de la propiedad, no setAttribute
    verify(user).setEmail("john@example.org");
  }

  @Test
  void apply_WithMissingConfig_DoesNothing() {
    // given: una configuración incompleta (sin prefijo ni destino)
    UserModel user = mock(UserModel.class);
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("mail", null, "p:x"));
    IdentityProviderMapperModel model = model(Map.of(
        PrefixMatchStripAttributeMapper.CONFIG_SOURCE_ATTRIBUTE, "mail"));

    // when: se aplica el mapper
    mapper.apply(user, model, ctx);

    // then: no se toca el usuario
    verifyNoInteractions(user);
  }

  @Test
  void apply_WithoutMatchingValue_DoesNotWrite() {
    // given: ningún valor del atributo casa con el prefijo
    UserModel user = mock(UserModel.class);
    BrokeredIdentityContext ctx = SamlAssertions.context(
        SamlAssertions.attribute("mail", null, "other:x"));
    IdentityProviderMapperModel model = model(Map.of(
        PrefixMatchStripAttributeMapper.CONFIG_SOURCE_ATTRIBUTE, "mail",
        PrefixMatchStripAttributeMapper.CONFIG_PREFIX, "p:",
        PrefixMatchStripAttributeMapper.CONFIG_TARGET_ATTRIBUTE, "userRefId"));

    // when: se aplica el mapper
    mapper.apply(user, model, ctx);

    // then: no se escribe ningún atributo
    verify(user, never()).setAttribute(ArgumentMatchers.anyString(), ArgumentMatchers.anyList());
  }

  private static IdentityProviderMapperModel model(Map<String, String> config) {
    IdentityProviderMapperModel m = new IdentityProviderMapperModel();
    m.setName("test-mapper");
    m.setConfig(new HashMap<>(config));
    return m;
  }

}
