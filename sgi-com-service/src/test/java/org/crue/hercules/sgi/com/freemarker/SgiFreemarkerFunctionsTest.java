package org.crue.hercules.sgi.com.freemarker;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Tests unitarios de las funciones de {@code sgi.ftl}.
 */
class SgiFreemarkerFunctionsTest {

  private Configuration cfg;

  @BeforeEach
  void setUp() throws Exception {
    cfg = new Configuration(Configuration.VERSION_2_3_0);
    cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/org/crue/hercules/sgi/com/freemarker/");
    cfg.addAutoImport("sgi", "sgi.ftl");
    cfg.setSharedVariable("languagePriorities", List.of("es", "en", "eu", "ca"));
    cfg.setLocale(Locale.of("es"));
  }

  @Test
  void getFieldValue_i18nField_returnsValueForCurrentLanguage() {
    // given: un campo i18n con valor en varios idiomas y el idioma actual "es"
    Object i18nField = List.of(
        Map.of("lang", "es", "value", "Hola"),
        Map.of("lang", "en", "value", "Hello"));

    // when: se resuelve el valor del campo
    String result = render("${sgi.getFieldValue(field)}", Map.of("field", i18nField));

    // then: devuelve el valor del idioma actual (es)
    Assertions.assertThat(result).isEqualTo("Hola");
  }

  @Test
  void getFieldValue_currentLanguageMissing_fallsBackToLanguagePriorities() {
    // given: un campo i18n sin el idioma actual (ca), con "es" disponible
    Object i18nField = List.of(Map.of("lang", "es", "value", "Hola"));

    // when: se renderiza forzando el idioma "ca"
    String result = render("<#setting locale=\"ca\">${sgi.getFieldValue(field)}", Map.of("field", i18nField));

    // then: cae al primer idioma disponible según languagePriorities
    Assertions.assertThat(result).isEqualTo("Hola");
  }

  @Test
  void getFieldValue_plainString_returnsItUnchanged() {
    // given: un texto plano (no i18n)
    // when: se resuelve el campo
    String result = render("${sgi.getFieldValue(field)}", Map.of("field", "Sin Requisitos"));

    // then: lo devuelve tal cual (fallback tolerante por compatibilidad)
    Assertions.assertThat(result).isEqualTo("Sin Requisitos");
  }

  @Test
  void formatDate_validIsoInstant_isFormatted() {
    // given: una fecha ISO válida
    // when: se formatea la fecha
    String result = render("${sgi.formatDate(fecha, \"SHORT\")}", Map.of("fecha", "2022-01-31T23:59:59Z"));

    // then: produce una fecha formateada no vacía
    Assertions.assertThat(result).isNotBlank();
  }

  @Test
  void formatDate_garbageString_fails() {
    // given: un texto que no es una fecha
    // when / then: falla al intentar interpretarlo como fecha
    Assertions.assertThatThrownBy(
        () -> render("${sgi.formatDate(fecha, \"SHORT\")}", Map.of("fecha", "no soy una fecha")))
        .isInstanceOf(Exception.class);
  }

  private String render(String templateBody, Map<String, Object> model) {
    try {
      Template template = new Template("test", new StringReader(templateBody), cfg);
      StringWriter out = new StringWriter();
      template.process(model, out);
      return out.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
