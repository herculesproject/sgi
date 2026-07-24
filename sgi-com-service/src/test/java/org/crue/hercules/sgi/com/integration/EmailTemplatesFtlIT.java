package org.crue.hercules.sgi.com.integration;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.com.model.ContentTpl;
import org.crue.hercules.sgi.com.model.EmailTpl;
import org.crue.hercules.sgi.com.model.SubjectTpl;
import org.crue.hercules.sgi.com.repository.EmailTplRepository;
import org.crue.hercules.sgi.framework.i18n.I18nConfig;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StringUtils;

import freemarker.template.AttemptExceptionReporter;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Tests sobre las plantillas FreeMarker de los comunicados, tal y como quedan
 * cargadas en base de datos por el Liquibase real (la última versión de cada
 * CLOB).
 * <p>
 * A diferencia del resto de tests de integración (que usan
 * {@code spring.liquibase.contexts=test} y por tanto omiten los changesets con
 * {@code context="!test"} que cargan los CLOB), este test ejecuta el changelog
 * con un contexto distinto de {@code test}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    // Por defecto (CI): H2 en memoria + Liquibase real. Configurable con variables
    // de entorno SGI_TPL_* para validar, en solo lectura, contra otra BD (Oracle o
    // SQLServer).
    "spring.liquibase.contexts=prod",
    "spring.liquibase.enabled=${SGI_TPL_RUN_MIGRATIONS:true}",
    "preliquibase.enabled=${SGI_TPL_RUN_MIGRATIONS:true}",
    "spring.datasource.url=${SGI_TPL_DB_URL:jdbc:h2:mem:realtpl;NON_KEYWORDS=VALUE;DB_CLOSE_DELAY=-1}",
    "spring.datasource.username=${SGI_TPL_DB_USERNAME:com}",
    "spring.datasource.password=${SGI_TPL_DB_PASSWORD:com}",
    "spring.datasource.driver-class-name=${SGI_TPL_DB_DRIVER:org.h2.Driver}",
    "spring.jpa.properties.hibernate.dialect=${SGI_TPL_DB_DIALECT:org.hibernate.dialect.H2Dialect}",
    "spring.jpa.properties.hibernate.default_schema=${SGI_TPL_DB_SCHEMA:test}",
    "sgi.rest.api.cnf-url=${SGI_TPL_CNF_URL:}",
})
class EmailTemplatesFtlIT extends BaseIT {

  /** Captura los idiomas con macro definida: {@code <#macro renderEs>} -> es. */
  private static final Pattern RENDER_MACRO = Pattern.compile("<#macro\\s+render(\\w+)\\s*>");

  @Autowired
  private EmailTplRepository emailTplRepository;

  @Autowired
  private Configuration freemarkerCfg;

  @BeforeEach
  void configureFreemarkerLikeProduction() {
    // El render usa el bean Configuration de producción pero sin pasar por
    // processTemplate(); se replica su reporter de <#attempt> para que las
    // excepciones recuperadas (p. ej. el fallback de sgi.getFieldValue) se
    // registren como WARN y no como ERROR.
    freemarkerCfg.setAttemptExceptionReporter(AttemptExceptionReporter.LOG_WARN_REPORTER);
  }

  @Test
  void allEmailTemplates_parseWithoutErrors() {
    // given: todas las plantillas cargadas por el Liquibase real
    List<EmailTpl> emailTpls = emailTplRepository.findAll();
    Assertions.assertThat(emailTpls)
        .as("Se esperan comunicados cargados por el Liquibase real")
        .isNotEmpty();

    // when: se parsea cada parte de cada comunicado
    List<String> errors = new ArrayList<>();
    for (EmailTpl emailTpl : emailTpls) {
      partsOf(emailTpl).forEach((part, tpl) -> {
        try (StringReader reader = new StringReader(tpl)) {
          new Template(emailTpl.getName() + "::" + part, reader, freemarkerCfg);
        } catch (Exception e) {
          errors.add(formatError(emailTpl.getName(), part, e));
        }
      });
    }

    // then: ninguna plantilla tiene errores de sintaxis
    Assertions.assertThat(errors)
        .as("Plantillas FreeMarker con errores de sintaxis:%n%s",
            String.join(System.lineSeparator(), errors))
        .isEmpty();
  }

  @Test
  @DisplayName("CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO")
  void notificarFacturaNotFirstOrInProrrogaAndIsNotLastNoRequisito_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "tituloProyecto": [{"lang": "es", "value": "Proyecto de prueba"}],
          "codigosSge": ["00001", "00002"],
          "numPrevision": 2,
          "entidadesFinanciadoras": ["Entidad Financiadora 1", "Entidad Financiadora 2"],
          "apellidosDestinatario": "Apellido1 Apellido2",
          "enlaceAplicacion": "https://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication(
        "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST")
  void notificarFacturaNotFirstOrInProrrogaAndIsLast_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "tituloProyecto": [{"lang": "es", "value": "Proyecto de prueba"}],
          "codigosSge": ["00001", "00002"],
          "numPrevision": 2,
          "entidadesFinanciadoras": ["Entidad Financiadora 1", "Entidad Financiadora 2"],
          "tipoFacturacion": [{"lang": "es", "value": "Sin Requisitos"}],
          "apellidosDestinatario": "Apellido1 Apellido2",
          "enlaceAplicacion": "https://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication(
        "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST")
  void calendarioFacturacionNotificarFacturaFirstNoProrrogaNoLast_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "entidadesFinanciadoras": ["nombre entidad 1", "nombre entidad 2"],
          "tituloProyecto": [{"lang": "es", "value": "Proyecto de prueba"}],
          "codigosSge": ["00001", "000002"],
          "numPrevision": 2,
          "enlaceAplicacion": "http://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication(
        "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO")
  void calendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsLastNoRequisito_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "entidadesFinanciadoras": ["nombre entidad 1", "nombre entidad 2"],
          "tituloProyecto": [{"lang": "es", "value": "Proyecto de prueba"}],
          "codigosSge": ["00001", "000002"],
          "numPrevision": 2,
          "enlaceAplicacion": "http://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication(
        "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST")
  void calendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsNotLast_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "entidadesFinanciadoras": ["nombre entidad 1", "nombre entidad 2"],
          "tituloProyecto": [{"lang": "es", "value": "Proyecto de prueba"}],
          "codigosSge": ["00001", "000002"],
          "numPrevision": 2,
          "tipoFacturacion": [{"lang": "es", "value": "Sin Requisitos"}],
          "enlaceAplicacion": "http://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication(
        "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA")
  void calendarioFacturacionNotificarFacturaUnicaNotInProrroga_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "entidadesFinanciadoras": ["nombre entidad 1", "nombre entidad 2"],
          "tituloProyecto": [{"lang": "es", "value": "Proyecto de prueba"}],
          "codigosSge": ["00001", "000002"],
          "numPrevision": 2,
          "tipoFacturacion": [{"lang": "es", "value": "Sin Requisitos"}],
          "enlaceAplicacion": "http://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication(
        "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO")
  void calendarioFacturacionNotificarFacturaUnicaNotInProrrogaNoRequisito_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "entidadesFinanciadoras": ["nombre entidad 1", "nombre entidad 2"],
          "tituloProyecto": [{"lang": "es", "value": "Proyecto de prueba"}],
          "codigosSge": ["00001", "000002"],
          "numPrevision": 2,
          "enlaceAplicacion": "http://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication(
        "CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA")
  void calendarioFacturacionValidarIpRechazada_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
          "tituloProyecto": [{"lang": "es", "value": "Proyecto de prueba"}],
          "codigosSge": ["00001", "000002"],
          "numPrevision": 2,
          "motivoRechazo": [{"lang": "es", "value": "Motivo rechazo"}]
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA")
  void calendarioFacturacionValidarIpValidada_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
          "tituloProyecto": [{"lang": "es", "value": "Proyecto de prueba"}],
          "codigosSge": ["00001", "000002"],
          "numPrevision": 2
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH")
  void cambioEstadoRechazadaSolTipoRrhh_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fechaEstado": "2022-01-31T23:59:59Z",
          "codigoInternoSolicitud": "SOL-001",
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}]
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH")
  void cambioEstadoSolicitadaSolTipoRrhh_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fechaEstado": "2022-01-31T23:59:59Z",
          "nombreApellidosSolicitante": "Nombre Apellidos Solicitante",
          "codigoInternoSolicitud": "SOL-001",
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}],
          "fechaProvisionalConvocatoria": "2022-02-15T23:59:59Z",
          "enlaceAplicacionMenuValidacionTutor": "https://sgi.hercules.org/validacion"
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH")
  void cambioEstadoValidadaSolTipoRrhh_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fechaEstado": "2022-01-31T23:59:59Z",
          "codigoInternoSolicitud": "SOL-001",
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}]
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_SOL_CAMB_EST_ALEGACIONES")
  void solCambEstAlegaciones_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}],
          "fechaPublicacionConvocatoria": "2022-01-15T23:59:59Z",
          "codigoInternoSolicitud": "SOL-001",
          "fechaProvisionalConvocatoria": "2022-02-15T23:59:59Z",
          "nombreApellidosSolicitante": "Nombre Apellidos Solicitante"
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_SOL_CAMB_EST_ALEGACIONES");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_SOL_CAMB_EST_CONC")
  void solCambEstConc_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fechaConcesionConvocatoria": "2022-03-31T23:59:59Z",
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}],
          "enlaces": [
            {
              "tipoEnlace": [{"lang": "es", "value": "Resolucion"}],
              "descripcion": [{"lang": "es", "value": "Descripcion del enlace"}],
              "url": "https://sgi.hercules.org/enlace"
            }
          ]
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_SOL_CAMB_EST_CONC");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_SOL_CAMB_EST_CONC_PROV")
  void solCambEstConcProv_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fechaProvisionalConvocatoria": "2022-02-15T23:59:59Z",
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}],
          "enlaces": [
            {
              "tipoEnlace": [{"lang": "es", "value": "Resolucion"}],
              "descripcion": [{"lang": "es", "value": "Descripcion del enlace"}],
              "url": "https://sgi.hercules.org/enlace"
            }
          ]
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_SOL_CAMB_EST_CONC_PROV");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_SOL_CAMB_EST_DEN")
  void solCambEstDen_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fechaConcesionConvocatoria": "2022-03-31T23:59:59Z",
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}],
          "enlaces": [
            {
              "tipoEnlace": [{"lang": "es", "value": "Resolucion"}],
              "descripcion": [{"lang": "es", "value": "Descripcion del enlace"}],
              "url": "https://sgi.hercules.org/enlace"
            }
          ]
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_SOL_CAMB_EST_DEN");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_SOL_CAMB_EST_DEN_PROV")
  void solCambEstDenProv_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fechaProvisionalConvocatoria": "2022-02-15T23:59:59Z",
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}],
          "enlaces": [
            {
              "tipoEnlace": [{"lang": "es", "value": "Resolucion"}],
              "descripcion": [{"lang": "es", "value": "Descripcion del enlace"}],
              "url": "https://sgi.hercules.org/enlace"
            }
          ]
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_SOL_CAMB_EST_DEN_PROV");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_SOL_CAMB_EST_EXCL_DEF")
  void solCambEstExclDef_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fechaConcesionConvocatoria": "2022-03-31T23:59:59Z",
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}],
          "enlaces": [
            {
              "tipoEnlace": [{"lang": "es", "value": "Resolucion"}],
              "descripcion": [{"lang": "es", "value": "Descripcion del enlace"}],
              "url": "https://sgi.hercules.org/enlace"
            }
          ]
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_SOL_CAMB_EST_EXCL_DEF");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_SOL_CAMB_EST_EXCL_PROV")
  void solCambEstExclProv_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fechaProvisionalConvocatoria": "2022-02-15T23:59:59Z",
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}],
          "enlaces": [
            {
              "tipoEnlace": [{"lang": "es", "value": "Resolucion"}],
              "descripcion": [{"lang": "es", "value": "Descripcion del enlace"}],
              "url": "https://sgi.hercules.org/enlace"
            }
          ]
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_SOL_CAMB_EST_EXCL_PROV");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_SOL_CAMB_EST_SOLICITADA")
  void solCambEstSolicitada_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}],
          "fechaPublicacionConvocatoria": "2022-01-15T23:59:59Z",
          "nombreApellidosSolicitante": "Nombre Apellidos Solicitante",
          "fechaCambioEstadoSolicitud": "2022-01-31T23:59:59Z"
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_SOL_CAMB_EST_SOLICITADA");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_SOL_USUARIO_EXTERNO")
  void solUsuarioExterno_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "tituloConvocatoria": [{"lang": "es", "value": "Convocatoria de prueba"}],
          "enlaceAplicacion": "https://sgi.hercules.org/solicitud",
          "uuid": "550e8400-e29b-41d4-a716-446655440000"
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_SOL_USUARIO_EXTERNO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO")
  void addModificarCertificadoAutorizacionParticipacionProyectoExterno_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "enlaceAplicacion": "https://sgi.hercules.org/csp",
          "tituloProyectoExt": [{"lang": "es", "value": "Proyecto externo de prueba"}]
        }
        """;
    EmailTpl communication = givenExistingCommunication(
        "CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO")
  void cambioEstadoAutorizacionParticipacionProyectoExterno_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fechaEstadoSolicitudPext": "2022-01-31T23:59:59Z",
          "tituloPext": [{"lang": "es", "value": "Proyecto externo de prueba"}],
          "estadoSolicitudPext": [{"lang": "es", "value": "Estado de la solicitud de prueba"}]
        }
        """;
    EmailTpl communication = givenExistingCommunication(
        "CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO")
  void modificacionAutorizacionParticipacionProyectoExterno_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fecha": "2022-01-31T23:59:59Z",
          "tituloProyecto": [{"lang": "es", "value": "Proyecto de prueba"}],
          "nombreSolicitante": "Nombre Apellido Solicitante",
          "enlaceAplicacion": "https://sgi.hercules.org/csp"
        }
        """;
    EmailTpl communication = givenExistingCommunication(
        "CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO")
  void recepcionNotificacionCvnProyectoExterno_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "fechaCreacion": "2022-01-31T23:59:59Z",
          "tituloProyecto": [{"lang": "es", "value": "Proyecto de prueba"}],
          "nombreApellidosCreador": "Nombre Apellido Creador"
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_CONVOCATORIA_FASE")
  void convocatoriaFase_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    Map<String, Object> params = Map.of(
        "CSP_CONV_FASE_TITULO", i18nJson("Convocatoria de prueba"),
        "CSP_CONV_TIPO_FASE", i18nJson("Tipo de fase de prueba"),
        "CSP_CONV_FASE_FECHA_INICIO", "2022-01-31T23:59:59Z",
        "CSP_CONV_FASE_FECHA_FIN", "2022-02-28T23:59:59Z",
        "CSP_CONV_FASE_OBSERVACIONES", i18nJson("Observaciones de prueba"));
    EmailTpl communication = givenExistingCommunication("CSP_COM_CONVOCATORIA_FASE");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, params);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_PROYECTO_FASE")
  void proyectoFase_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    Map<String, Object> params = Map.of(
        "CSP_PRO_FASE_TITULO_CONVOCATORIA", i18nJson("Convocatoria de prueba"),
        "CSP_PRO_TIPO_FASE", i18nJson("Tipo de fase de prueba"),
        "CSP_PRO_FASE_TITULO_PROYECTO", i18nJson("Proyecto de prueba"),
        "CSP_PRO_FASE_FECHA_INICIO", "2022-01-31T23:59:59Z",
        "CSP_PRO_FASE_FECHA_FIN", "2022-02-28T23:59:59Z",
        "CSP_PRO_FASE_OBSERVACIONES", i18nJson("Observaciones de prueba"));
    EmailTpl communication = givenExistingCommunication("CSP_COM_PROYECTO_FASE");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, params);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_SOLICITUD_PETICION_EVALUACION")
  void solicitudPeticionEvaluacion_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    Map<String, Object> params = Map.of(
        "CSP_SOLICITUD_CODIGO", "SOL-2022-001",
        "ETI_PETICION_EVALUACION_CODIGO", "PEV-2022-001");
    EmailTpl communication = givenExistingCommunication("CSP_COM_SOLICITUD_PETICION_EVALUACION");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, params);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_INICIO_PRESENTACION_GASTO")
  void inicioPresentacionGasto_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "proyectos": [
            {
              "titulo": [{"lang": "es", "value": "Proyecto de prueba 1"}],
              "fechaInicio": "2022-01-01T00:00:00Z",
              "fechaFin": "2022-01-31T23:59:59Z"
            },
            {
              "titulo": [{"lang": "es", "value": "Proyecto de prueba 2"}],
              "fechaInicio": "2022-02-01T00:00:00Z",
              "fechaFin": "2022-02-28T23:59:59Z"
            }
          ],
          "enlaceAplicacion": "https://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_INICIO_PRESENTACION_GASTO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO")
  void inicioPeriodoJustificacionSocio_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "titulo": [{"lang": "es", "value": "Proyecto de prueba"}],
          "nombreEntidad": "Entidad Colaboradora",
          "fechaInicio": "2022-01-01T00:00:00Z",
          "numPeriodo": 2
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO")
  void finPeriodoJustificacionSocio_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "titulo": [{"lang": "es", "value": "Proyecto de prueba"}],
          "nombreEntidad": "Entidad Colaboradora",
          "fechaFin": "2022-01-31T23:59:59Z",
          "numPeriodo": 2,
          "enlaceAplicacion": "https://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO")
  void inicioPresentacionSeguimientoCientifico_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "enlaceAplicacion": "https://sgi.hercules.org",
          "proyectos": [
            {
              "titulo": [{"lang": "es", "value": "Proyecto de prueba 1"}],
              "fechaInicio": "2022-01-01T00:00:00Z",
              "fechaFin": "2022-01-31T23:59:59Z"
            },
            {
              "titulo": [{"lang": "es", "value": "Proyecto de prueba 2"}],
              "fechaInicio": "2022-02-01T00:00:00Z",
              "fechaFin": "2022-02-28T23:59:59Z"
            }
          ]
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP")
  void inicioPresentacionSeguimientoCientificoIp_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "titulo": [{"lang": "es", "value": "Proyecto de prueba"}],
          "fechaInicio": "2022-01-01T00:00:00Z",
          "numPeriodo": 2,
          "enlaceAplicacion": "https://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP")
  void finPresentacionSeguimientoCientificoIp_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "titulo": [{"lang": "es", "value": "Proyecto de prueba"}],
          "fechaFin": "2022-01-31T23:59:59Z",
          "numPeriodo": 2,
          "enlaceAplicacion": "https://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO")
  void vencimientoPeriodoPagoSocio_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "titulo": [{"lang": "es", "value": "Proyecto de prueba"}],
          "fechaPrevistaPago": "2022-01-01T00:00:00Z",
          "nombreEntidadColaboradora": "Entidad Colaboradora"
        }
        """;
    EmailTpl communication = givenExistingCommunication("CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_CONVOCATORIA_HITO_EMAIL")
  void convocatoriaHito_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    Map<String, Object> params = Map.of(
        "CSP_HITO_TIPO", i18nJson("Tipo de hito de prueba"),
        "CSP_CONVOCATORIA_TITULO", i18nJson("Convocatoria de prueba"),
        "CSP_HITO_FECHA", "2022-01-31T23:59:59Z",
        "CSP_HITO_OBSERVACIONES", i18nJson("Observaciones de prueba"));
    EmailTpl communication = givenExistingCommunication("CSP_CONVOCATORIA_HITO_EMAIL");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, params);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_PROYECTO_HITO")
  void proyectoHito_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    Map<String, Object> params = Map.of(
        "CSP_HITO_FECHA", "2022-01-31T23:59:59Z",
        "CSP_HITO_TIPO", i18nJson("Tipo de hito de prueba"),
        "CSP_PROYECTO_TITULO", i18nJson("Proyecto de prueba"),
        "CSP_CONVOCATORIA_TITULO", i18nJson("Convocatoria de prueba"),
        "CSP_HITO_OBSERVACIONES", i18nJson("Observaciones de prueba"));
    EmailTpl communication = givenExistingCommunication("CSP_PROYECTO_HITO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, params);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("CSP_SOLICITUD_HITO")
  void solicitudHito_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    Map<String, Object> params = Map.of(
        "CSP_HITO_FECHA", "2022-01-31T23:59:59Z",
        "CSP_HITO_TIPO", i18nJson("Tipo de hito de prueba"),
        "CSP_SOLICITUD_TITULO", i18nJson("Solicitud de prueba"),
        "CSP_CONVOCATORIA_TITULO", i18nJson("Convocatoria de prueba"),
        "CSP_HITO_OBSERVACIONES", i18nJson("Observaciones de prueba"));
    EmailTpl communication = givenExistingCommunication("CSP_SOLICITUD_HITO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, params);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_ACTA_SIN_REV_MINIMA")
  void etiComActaSinRevMinima_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "comiteCodigo": "CEEA",
          "enlaceAplicacion": "https://sgi.hercules.org",
          "tipoActividad": [{"lang": "es", "value": "Tipo de actividad de prueba"}],
          "tituloSolicitudEvaluacion": [{"lang": "es", "value": "Solicitud de evaluación de prueba"}],
          "referenciaMemoria": "M10/2022/001"
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_ACTA_SIN_REV_MINIMA");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_ASIGNACION_EVALUACION")
  void etiComAsignacionEvaluacion_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "referenciaMemoria": "M10/2022/001",
          "fechaConvocatoriaReunion": "2022-01-31T23:59:59Z",
          "fechaEvaluacionAnterior": "2021-12-15T10:30:00Z",
          "tituloSolicitudEvaluacion": [{"lang": "es", "value": "Solicitud de evaluación de prueba"}],
          "nombreApellidosEvaluador1": "Nombre Apellido1 Apellido2",
          "nombreApellidosEvaluador2": "Nombre2 Apellido1 Apellido2",
          "enlaceAplicacion": "https://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_ASIGNACION_EVALUACION");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_CONVOCATORIA_REUNION")
  void etiComConvocatoriaReunion_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    Map<String, Object> params = Map.of(
        "ETI_COMITE", "CEEA",
        "ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION", "2022-01-31T23:59:59Z",
        "ETI_CONVOCATORIA_REUNION_HORA_INICIO", "10",
        "ETI_CONVOCATORIA_REUNION_MINUTO_INICIO", "30",
        "ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA", "11",
        "ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA", "00",
        "ETI_CONVOCATORIA_REUNION_TIPO_CONVOCATORIA", i18nJson("Ordinaria"),
        "ETI_CONVOCATORIA_REUNION_VIDEOCONFERENCIA", Boolean.FALSE,
        "ETI_CONVOCATORIA_REUNION_LUGAR", i18nJson("Sala de reuniones"),
        "ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA", i18nJson("Orden del día de prueba"));
    EmailTpl communication = givenExistingCommunication("ETI_COM_CONVOCATORIA_REUNION");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, params);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_DICT_EVA_REV_MINIMA")
  void etiComDictEvaRevMinima_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "comiteCodigo": "CEEA",
          "enlaceAplicacion": "https://sgi.hercules.org",
          "tipoActividad": [{"lang": "es", "value": "Tipo de actividad de prueba"}],
          "tituloSolicitudEvaluacion": [{"lang": "es", "value": "Solicitud de evaluación de prueba"}],
          "referenciaMemoria": "M10/2022/001"
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_DICT_EVA_REV_MINIMA");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_DICT_EVA_SEG_REV_MINIMA")
  void etiComDictEvaSegRevMinima_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "comiteCodigo": "CEEA",
          "enlaceAplicacion": "https://sgi.hercules.org",
          "tipoActividad": [{"lang": "es", "value": "Tipo de actividad de prueba"}],
          "tituloSolicitudEvaluacion": [{"lang": "es", "value": "Solicitud de evaluación de prueba"}],
          "referenciaMemoria": "M10/2022/001"
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_DICT_EVA_SEG_REV_MINIMA");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_DICT_MEM_REV_MINIMA_ARCH")
  void etiComDictMemRevMinimaArch_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "comiteCodigo": "CEEA",
          "tipoActividad": [{"lang": "es", "value": "Tipo de actividad de prueba"}],
          "tituloSolicitudEvaluacion": [{"lang": "es", "value": "Solicitud de evaluación de prueba"}],
          "referenciaMemoria": "M10/2022/001"
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_DICT_MEM_REV_MINIMA_ARCH");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_EVA_MODIFICADA")
  void etiComEvaModificada_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "nombreInvestigacion": [{"lang": "es", "value": "Investigación de prueba"}],
          "tituloSolicitudEvaluacion": [{"lang": "es", "value": "Solicitud de evaluación de prueba"}],
          "referenciaMemoria": "M10/2022/001"
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_EVA_MODIFICADA");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_INF_RETRO_PENDIENTE")
  void etiComInfRetroPendiente_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "comiteCodigo": "CEEA",
          "enlaceAplicacion": "https://sgi.hercules.org",
          "tipoActividad": [{"lang": "es", "value": "Tipo de actividad de prueba"}],
          "tituloSolicitudEvaluacion": [{"lang": "es", "value": "Solicitud de evaluación de prueba"}],
          "referenciaMemoria": "M10/2022/001"
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_INF_RETRO_PENDIENTE");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_INF_SEG_ANU")
  void etiComInfSegAnu_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "nombreInvestigacion": [{"lang": "es", "value": "Investigación de prueba"}],
          "enlaceAplicacion": "https://sgi.hercules.org",
          "tipoActividad": [{"lang": "es", "value": "Tipo de actividad de prueba"}],
          "tituloSolicitudEvaluacion": [{"lang": "es", "value": "Solicitud de evaluación de prueba"}],
          "referenciaMemoria": "M10/2022/001"
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_INF_SEG_ANU");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_INF_SEG_FIN")
  void etiComInfSegFin_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "nombreInvestigacion": [{"lang": "es", "value": "Investigación de prueba"}],
          "enlaceAplicacion": "https://sgi.hercules.org",
          "tipoActividad": [{"lang": "es", "value": "Tipo de actividad de prueba"}],
          "tituloSolicitudEvaluacion": [{"lang": "es", "value": "Solicitud de evaluación de prueba"}],
          "referenciaMemoria": "M10/2022/001"
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_INF_SEG_FIN");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_MEM_ARCHIVADA_AUT")
  void etiComMemArchivadaAut_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "comiteCodigo": "CEEA",
          "tipoActividad": [{"lang": "es", "value": "Tipo de actividad de prueba"}],
          "tituloSolicitudEvaluacion": [{"lang": "es", "value": "Solicitud de evaluación de prueba"}],
          "referenciaMemoria": "M10/2022/001"
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_MEM_ARCHIVADA_AUT");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_MEM_INDICAR_SUBSANACION")
  void etiComMemIndicarSubsanacion_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "comiteCodigo": "CEEA",
          "enlaceAplicacion": "https://sgi.hercules.org",
          "tipoActividad": [{"lang": "es", "value": "Tipo de actividad de prueba"}],
          "tituloSolicitudEvaluacion": [{"lang": "es", "value": "Solicitud de evaluación de prueba"}],
          "referenciaMemoria": "M10/2022/001",
          "comentarioEstado": [{"lang": "es", "value": "Comentario de estado de prueba"}]
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_MEM_INDICAR_SUBSANACION");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("ETI_COM_REVISION_ACTA")
  void etiComRevisionActa_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "nombreComite": "CEEA",
          "fechaEvaluacion": "2022-01-31T23:59:59Z",
          "enlaceAplicacion": "https://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication("ETI_COM_REVISION_ACTA");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION")
  void piiComAvisoFinPlazoPresentacionFasesNacionalesRegionalesSolicitudProteccion_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "monthsBeforeFechaFinPrioridad": 6,
          "fechaFinPrioridad": "2022-01-01T00:00:00Z",
          "solicitudTitle": [{"lang": "es", "value": "Solicitud de prueba"}]
        }
        """;
    EmailTpl communication = givenExistingCommunication(
        "PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("PII_COM_FECHA_LIMITE_PROCEDIMIENTO")
  void piiComFechaLimiteProcedimiento_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "tipoProcedimiento": [{"lang": "es", "value": "Tipo de procedimiento de prueba"}],
          "accionATomar": [{"lang": "es", "value": "Acción a tomar de prueba"}],
          "fechaLimite": "2022-01-31T23:59:59Z"
        }
        """;
    EmailTpl communication = givenExistingCommunication("PII_COM_FECHA_LIMITE_PROCEDIMIENTO");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION")
  void piiComMesesHastaFinPrioridadSolicitudProteccion_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "monthsBeforeFechaFinPrioridad": 6,
          "fechaFinPrioridad": "2022-01-01T00:00:00Z",
          "solicitudTitle": [{"lang": "es", "value": "Solicitud de prueba"}]
        }
        """;
    EmailTpl communication = givenExistingCommunication("PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("PRC_COM_PROCESO_BAREMACION_ERROR")
  void prcComProcesoBaremacionError_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "anio": "2022",
          "error": "Error de prueba"
        }
        """;
    EmailTpl communication = givenExistingCommunication("PRC_COM_PROCESO_BAREMACION_ERROR");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("PRC_COM_PROCESO_BAREMACION_FIN")
  void prcComProcesoBaremacionFin_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "anio": "2022",
          "enlaceAplicacion": "https://sgi.hercules.org"
        }
        """;
    EmailTpl communication = givenExistingCommunication("PRC_COM_PROCESO_BAREMACION_FIN");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("PRC_COM_VALIDAR_ITEM")
  void prcComValidarItem_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    String data = """
        {
          "nombreEpigrafe": [{"lang": "es", "value": "Epígrafe de prueba"}],
          "tituloItem": "Ítem de prueba",
          "fechaItem": "2022-01-31T23:59:59Z"
        }
        """;
    EmailTpl communication = givenExistingCommunication("PRC_COM_VALIDAR_ITEM");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, data);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("GENERIC_EMAIL")
  void genericEmail_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    Map<String, Object> params = Map.of(
        "GENERIC_SUBJECT", "Asunto de prueba",
        "GENERIC_CONTENT_HTML", "<p>Contenido de prueba</p>",
        "GENERIC_CONTENT_TEXT", "Contenido de prueba");
    EmailTpl communication = givenExistingCommunication("GENERIC_EMAIL");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, params);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  @Test
  @DisplayName("GENERIC_EMAIL_TEXT")
  void genericEmailText_rendersWithoutErrors() {
    // given: los datos que recibe el comunicado
    Map<String, Object> params = Map.of(
        "GENERIC_SUBJECT", "Asunto de prueba",
        "GENERIC_CONTENT_TEXT", "Contenido de prueba");
    EmailTpl communication = givenExistingCommunication("GENERIC_EMAIL_TEXT");

    // when: se renderiza el comunicado con esos datos
    List<String> problems = whenRendered(communication, params);

    // then: no se produce ningún error de render
    thenRenderedWithoutErrors(communication.getName(), problems);
  }

  /**
   * Recupera el comunicado por su nombre y verifica que existe (lo carga el
   * Liquibase real).
   *
   * @param communicationName nombre del comunicado ({@code email_tpl.name})
   * @return el comunicado encontrado
   */
  private EmailTpl givenExistingCommunication(String communicationName) {
    return emailTplRepository.findByName(communicationName)
        .orElseThrow(() -> new AssertionError(String.format(
            "No existe el comunicado '%s' en la BD",
            communicationName)));
  }

  /**
   * Construye el JSON de un campo i18n (en español), tal como lo recibe la
   * plantilla en un parámetro directo.
   *
   * @param value el texto del campo en español
   * @return el JSON del campo i18n, p. ej.
   *         {@code [{"lang": "es", "value": "..."}]}
   */
  private String i18nJson(String value) {
    return """
        [{"lang": "es", "value": "%s"}]""".formatted(value);
  }

  /**
   * Renderiza el comunicado cuando su contenido hace
   * {@code <#assign data = <NAME>_DATA?eval />}: el JSON se asigna al parámetro
   * {@code <communicationName>_DATA}.
   *
   * @param communication el comunicado a renderizar
   * @param dataJson      el JSON con los datos que recibe el comunicado
   * @return los problemas detectados (vacío si renderiza correctamente)
   */
  private List<String> whenRendered(EmailTpl communication, String dataJson) {
    Map<String, Object> params = new HashMap<>();
    params.put(communication.getName() + "_DATA", dataJson);
    return whenRendered(communication, params);
  }

  /**
   * Renderiza todas las partes (subject / content text / content html) del
   * comunicado con los parámetros indicados. Se usa esta sobrecarga cuando el
   * comunicado recibe parámetros directos (no un único JSON {@code _DATA}), como
   * los genéricos.
   *
   * @param communication el comunicado a renderizar
   * @param params        los parámetros que recibe el comunicado
   * @return los problemas detectados: una entrada por cada parte que falla al
   *         renderizar o que queda vacía (vacío si todo renderiza correctamente)
   */
  private List<String> whenRendered(EmailTpl communication, Map<String, Object> params) {
    List<String> problems = new ArrayList<>();

    partsOf(communication).forEach((part, tpl) -> {
      try {
        Template template = new Template(communication.getName() + "::" + part, new StringReader(tpl), freemarkerCfg);
        StringWriter out = new StringWriter();
        Map<String, Object> model = new HashMap<>(params);
        model.put("languagePriorities", languagePrioritiesFor(tpl));
        template.process(model, out);
        if (!StringUtils.hasText(out.toString())) {
          problems.add(String.format("- %s::%s -> el render ha quedado vacío", communication.getName(), part));
        }

      } catch (Exception e) {
        problems.add(formatError(communication.getName(), part, e));
      }
    });

    return problems;
  }

  /**
   * Verifica que el render del comunicado no ha producido ningún error.
   *
   * @param communicationName nombre del comunicado (para el mensaje de error)
   * @param problems          los errores devueltos por {@code whenRendered}
   */
  private void thenRenderedWithoutErrors(String communicationName, List<String> problems) {
    Assertions.assertThat(problems)
        .as("El comunicado '%s' falla al renderizar con los datos definidos:%n%s",
            communicationName, String.join(System.lineSeparator(), problems))
        .isEmpty();
  }

  /**
   * Devuelve las partes no vacías del comunicado (subject / content text /
   * content html), en ese orden.
   *
   * @param emailTpl el comunicado
   * @return un mapa {@code parte -> plantilla} con las partes presentes
   */
  private Map<String, String> partsOf(EmailTpl emailTpl) {
    Map<String, String> parts = new LinkedHashMap<>();
    SubjectTpl subject = emailTpl.getSubjectTpl();
    ContentTpl content = emailTpl.getContentTpl();

    if (subject != null && StringUtils.hasText(subject.getTpl())) {
      parts.put("SUBJECT", subject.getTpl());
    }

    if (content != null && StringUtils.hasText(content.getTplText())) {
      parts.put("CONTENT_text", content.getTplText());
    }

    if (content != null && StringUtils.hasText(content.getTplHtml())) {
      parts.put("CONTENT_html", content.getTplHtml());
    }

    return parts;
  }

  /**
   * Idiomas a renderizar. Si el servicio de configuración (CNF) está disponible y
   * devuelve prioridades de idioma, se usan esas (el comportamiento real de
   * producción, con los idiomas que la implantación tiene habilitados). Si no
   * (CNF sin configurar, p. ej. en CI), se usan los idiomas para los que la
   * plantilla define un macro {@code render<Lang>}; {@code ["es"]} si no define
   * ninguno.
   *
   * @param tpl el contenido de la plantilla
   * @return la lista de códigos de idioma a renderizar
   */
  private List<String> languagePrioritiesFor(String tpl) {
    List<String> configured = I18nConfig.get().getLanguagePriorities().stream()
        .map(Language::getCode)
        .collect(Collectors.toList());
    if (!configured.isEmpty()) {
      return configured;
    }

    List<String> defined = new ArrayList<>();
    Matcher m = RENDER_MACRO.matcher(tpl);
    while (m.find()) {
      defined.add(m.group(1).toLowerCase(Locale.ROOT));
    }

    return defined.isEmpty() ? List.of("es") : defined;
  }

  /**
   * Da formato a un error de una parte del comunicado para el informe.
   *
   * @param name nombre del comunicado
   * @param part parte afectada (subject / content text / content html)
   * @param e    la excepción producida al parsear o renderizar
   * @return la línea de error formateada
   */
  private String formatError(String name, String part, Exception e) {
    String message = StringUtils.hasText(e.getMessage()) ? e.getMessage().strip() : e.toString();
    return String.format("- %s::%s -> %s", name, part, message.replaceAll("\\s+", " "));
  }

}
