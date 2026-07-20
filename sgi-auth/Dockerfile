# Stage 1: build de una imagen Keycloak optimizada con los providers del SGI
FROM quay.io/keycloak/keycloak:26.6.4 AS builder

# Configuración persistida durante el build de Quarkus.
# KC_DB se configura en runtime porque depende de cada implantación.
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=false
ENV KC_HTTP_RELATIVE_PATH=/auth

# Driver JDBC de Oracle ( Keycloak no lo redistribuye).
ADD --chown=1000:0 \
  https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc11/21.9.0.0/ojdbc11-21.9.0.0.jar \
  /opt/keycloak/providers/ojdbc11.jar

# Providers del SGI.
COPY broker-saml-mappers/target/broker-saml-mappers*.jar /opt/keycloak/providers/

# Desactiva los mappers SAML legacy basados en JavaScript.
RUN /opt/keycloak/bin/kc.sh build \
  --spi-identity-provider-mapper--saml-script-group-idp-mapper--enabled=false \
  --spi-identity-provider-mapper--saml-script-attribute-idp-mapper--enabled=false


# Stage 2: imagen de runtime
FROM quay.io/keycloak/keycloak:26.6.4

COPY --from=builder /opt/keycloak/ /opt/keycloak/

# Deben mantenerse también en runtime porque Keycloak puede reconstruir
# la distribución de Quarkus al arrancar si la configuración difiere.
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=false
ENV KC_HTTP_RELATIVE_PATH=/auth
ENV KC_HTTP_MANAGEMENT_RELATIVE_PATH=/

# Los ficheros del realm de ejemplo se incluyen en la imagen, pero no se importan
# automáticamente; la importación depende del modo de arranque.
COPY realm /opt/keycloak/data/import/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]

CMD ["start"]