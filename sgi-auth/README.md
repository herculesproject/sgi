![](./doc/images/logos_feder.png)

# HÉRCULES - SGI 
El objetivo del Proyecto SGI dentro del Proyecto HÉRCULES es crear un Prototipo de un Sistema de Gestión de Investigación (SGI) basado en datos abiertos semánticos que ofrezca una visión global de los datos de investigación del Sistema Universitario Español (SUE) para mejorar la gestión, el análisis y las posibles sinergias entre universidades y el gran público desarrollando e incorporando  soluciones que superen las actualmente disponibles en el mercado.

# HÉRCULES SGI - Auth

Servicio de autenticación del SGI basado en **Keycloak 26.7.0**.

## Build

```bash
mvn -pl broker-saml-mappers -am package   # JDK 21; genera el JAR de los mappers SPI
docker build -t sgi-auth .                # imagen Keycloak optimizada (kc.sh build)
```

## Variables de entorno

Las siguientes variables se configuran durante el despliegue. La columna *Por defecto* indica los valores fijados en la imagen.

| Variable | Por defecto | Descripción |
|---|---|---|
| `KC_BOOTSTRAP_ADMIN_USERNAME` / `KC_BOOTSTRAP_ADMIN_PASSWORD` | — | Usuario administrador inicial (solo se crea sobre una BD vacía) |
| `KC_DB_SCHEMA` | — | Esquema de la BD |
| `KC_DB_URL_PROPERTIES` | — | Propiedades adicionales del driver JDBC (opcional; p.ej. `?oracle.net.CONNECT_TIMEOUT=61000`) |
| `KC_DB_URL` | — | URL JDBC de conexión a la BD |
| `KC_DB_USERNAME` / `KC_DB_PASSWORD` | — | Credenciales de la BD |
| `KC_DB` | — | Motor de BD: `oracle` \| `mssql` \| `postgres` |
| `KC_HEALTH_ENABLED` | `true` | Expone los endpoints de health (puerto de management) |
| `KC_HOSTNAME` | — | Hostname público de Keycloak. Si se omite, se infiere de las cabeceras `X-Forwarded-*` |
| `KC_HTTP_ENABLED` | — | Poner a `true` para servir HTTP tras un proxy que termina TLS |
| `KC_HTTP_MANAGEMENT_RELATIVE_PATH` | `/` | Prefijo del interfaz de management; se mantiene en la raíz, desacoplado de `KC_HTTP_RELATIVE_PATH` |
| `KC_HTTP_RELATIVE_PATH` | `/auth` | Prefijo público de la consola y los endpoints |
| `KC_METRICS_ENABLED` | `false` | Expone el endpoint de métricas (puerto de management) |
| `KC_PROXY_HEADERS` | — | Cabeceras de proxy a confiar: `xforwarded` \| `forwarded` |
| `KC_SPI_IDENTITY_PROVIDER_MAPPER__SAML_SCRIPT_*_IDP_MAPPER__ENABLED` | `false` | Reactiva los mappers SAML JavaScript (ver [`docs/keycloak-saml-mappers.md`](../docs/keycloak-saml-mappers.md)) |

## Endpoints expuestos

La imagen de Keycloak expone dos interfaces HTTP:

| Puerto | Descripción |
|--------:|-------------|
| **8080** | Interfaz principal de Keycloak. Expone la consola de administración y los endpoints OIDC/SAML bajo `KC_HTTP_RELATIVE_PATH` (por defecto, `/auth`). |
| **9000** | Interfaz de management (`KC_HTTP_MANAGEMENT_RELATIVE_PATH=/`). Expone los endpoints de health y, si `KC_METRICS_ENABLED=true`, también `/metrics`. Está destinada a las probes de Kubernetes y a la monitorización, y normalmente no se publica mediante el ingress. |

## Documentación relacionada

- Actualización a KC26: [notas de la versión](../docs/upgrade/1.3.0.md).
- Mappers SAML: [`docs/keycloak-saml-mappers.md`](../docs/keycloak-saml-mappers.md).
- Rotación de secretos/keys: [`docs/keycloak-secrets-rotation.md`](../docs/keycloak-secrets-rotation.md).
