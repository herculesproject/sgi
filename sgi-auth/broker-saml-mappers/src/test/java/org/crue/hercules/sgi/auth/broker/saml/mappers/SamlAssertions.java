package org.crue.hercules.sgi.auth.broker.saml.mappers;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.saml.SAMLEndpoint;
import org.keycloak.dom.saml.v2.assertion.AssertionType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType;
import org.keycloak.dom.saml.v2.assertion.AttributeType;
import org.keycloak.models.IdentityProviderModel;

/**
 * Utilidad de test que construye instancias de {@link BrokeredIdentityContext}
 * con una{@link AssertionType} real.
 */
final class SamlAssertions {

  private SamlAssertions() {
  }

  /**
   * Construye un {@link AttributeType} con el nombre, friendlyName opcional y
   * valores dados.
   */
  static AttributeType attribute(String name, String friendlyName, Object... values) {
    AttributeType attr = new AttributeType(name);
    if (friendlyName != null) {
      attr.setFriendlyName(friendlyName);
    }

    for (Object value : values) {
      attr.addAttributeValue(value);
    }

    return attr;
  }

  /**
   * Envuelve los atributos en un assertion de un solo statement dentro de un
   * contexto de broker.
   */
  static BrokeredIdentityContext context(AttributeType... attributes) {
    AttributeStatementType statement = new AttributeStatementType();
    for (AttributeType attr : attributes) {
      statement.addAttribute(new AttributeStatementType.ASTChoiceType(attr));
    }

    AssertionType assertion = new AssertionType("test-assertion", now());
    assertion.addStatement(statement);

    BrokeredIdentityContext context = emptyContext();
    context.getContextData().put(SAMLEndpoint.SAML_ASSERTION, assertion);
    return context;
  }

  /**
   * Contexto de broker sin assertion SAML (la config del IdP no puede ser null y
   * debe estar habilitada).
   */
  static BrokeredIdentityContext emptyContext() {
    IdentityProviderModel idp = new IdentityProviderModel();
    idp.setEnabled(true);
    return new BrokeredIdentityContext("test-user", idp);
  }

  private static javax.xml.datatype.XMLGregorianCalendar now() {
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar();
    } catch (DatatypeConfigurationException e) {
      throw new IllegalStateException(e);
    }
  }
}
