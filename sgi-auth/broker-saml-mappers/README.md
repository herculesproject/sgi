# SGI Broker SAML Mappers

Mappers de **SAML Identity Provider** propios de Keycloak que usa el SGI para traducir los atributos de un IdP institucional a grupos y atributos de usuario de Keycloak. 
Se empaquetan dentro de la imagen de `sgi-auth`.


## Build y test

```bash
mvn -pl broker-saml-mappers -am test   # requiere JDK 21
```

El JAR se copia en la imagen de Keycloak bajo `/opt/keycloak/providers/` y se registra vía `META-INF/services/org.keycloak.broker.provider.IdentityProviderMapper`.


## Documentación de los mappers
- [`docs/keycloak-saml-mappers.md`](../../docs/keycloak-saml-mappers.md)