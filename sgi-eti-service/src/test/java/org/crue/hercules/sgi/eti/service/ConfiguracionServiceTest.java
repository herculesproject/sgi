package org.crue.hercules.sgi.eti.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.ConfigParamOutput;
import org.crue.hercules.sgi.eti.exceptions.ConfiguracionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.ConfiguracionParamDependencyException;
import org.crue.hercules.sgi.eti.exceptions.ConfiguracionParamInvalidValueException;
import org.crue.hercules.sgi.eti.exceptions.UnknownConfiguracionParamException;
import org.crue.hercules.sgi.eti.exceptions.UserNotAuthorizedToReadConfiguracionParamException;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.model.Configuracion.FieldVisibility;
import org.crue.hercules.sgi.eti.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.eti.service.impl.ConfiguracionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * ConfiguracionServiceTest
 */
public class ConfiguracionServiceTest extends BaseServiceTest {

  private static final String TEXTO_AYUDA = "[{\"lang\":\"es\",\"value\":\"<p>Ayuda</p>\"}]";

  @Mock
  private ConfiguracionRepository configuracionRepository;

  private ConfiguracionService configuracionService;

  @BeforeEach
  void setUp() {
    configuracionService = new ConfiguracionServiceImpl(configuracionRepository);
  }

  @Test
  void finConfiguracion_ReturnsConfiguracion() {
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc())
        .willReturn(Optional.of(generarMockConfiguracion()));

    Configuracion configuracion = configuracionService.findConfiguracion();

    Assertions.assertThat(configuracion.getId()).isEqualTo(1L);
    Assertions.assertThat(configuracion.getDiasLimiteEvaluador()).isEqualTo(3);
  }

  @Test
  void update_ReturnsConfiguracion() {
    // given: Una nueva configuracion con el servicio actualizado
    Configuracion configuracionServicioActualizado = generarMockConfiguracionActualizada();

    Configuracion configuracion = generarMockConfiguracion();

    BDDMockito.given(configuracionRepository.findById(1L)).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(configuracion)).willReturn(configuracionServicioActualizado);

    // when: Actualizamos la configuracion
    Configuracion configuracionActualizado = configuracionService.update(configuracion);

    // then: La configuracion se actualiza correctamente.
    Assertions.assertThat(configuracionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(configuracionActualizado.getDiasLimiteEvaluador()).isEqualTo(10);
  }

  @Test
  void update_WithDatosPantallaConfiguracion_UpdatesAllFields() {
    // given: una configuración persistida y los nuevos valores de la pantalla de
    // configuración de ética
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setDiasAvisoRetrospectiva(1);
    configuracion.setDuracionProyectoEvaluacion(1);
    Configuracion configuracionActualizar = new Configuracion();
    configuracionActualizar.setId(1L);
    configuracionActualizar.setMesesArchivadaPendienteCorrecciones(30);
    configuracionActualizar.setDiasLimiteEvaluador(10);
    configuracionActualizar.setDiasArchivadaInactivo(5);
    configuracionActualizar.setDiasAvisoRetrospectiva(7);
    configuracionActualizar.setDuracionProyectoEvaluacion(4);
    BDDMockito.given(configuracionRepository.findById(1L)).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));

    // when: se actualiza la configuración
    Configuracion updated = configuracionService.update(configuracionActualizar);

    // then: se actualizan todos los campos de la pantalla
    Assertions.assertThat(updated.getMesesArchivadaPendienteCorrecciones()).isEqualTo(30);
    Assertions.assertThat(updated.getDiasLimiteEvaluador()).isEqualTo(10);
    Assertions.assertThat(updated.getDiasArchivadaInactivo()).isEqualTo(5);
    Assertions.assertThat(updated.getDiasAvisoRetrospectiva()).isEqualTo(7);
    Assertions.assertThat(updated.getDuracionProyectoEvaluacion()).isEqualTo(4);
  }

  @Test
  void update_ThrowsConfiguracionNotFoundException() {
    // given: Una nueva configuracion a actualizar
    Configuracion configuracion = generarMockConfiguracion();

    // then: Lanza una excepcion porque la configuracion no existe
    Assertions.assertThatThrownBy(() -> configuracionService.update(configuracion))
        .isInstanceOf(ConfiguracionNotFoundException.class);

  }

  @Test
  void update_DoesNotModifyParamsUnidadesVinculacion() {
    // given: una configuración persistida con los parámetros de unidades informados
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionUnidadesVinculacionEnabled(Boolean.TRUE);
    configuracion.setPeticionEvaluacionInicializarUnidadVinculacionEnabled(Boolean.TRUE);
    configuracion.setTipoUnidadVinculacionDepartamentoRef("TIPO-DEP");
    Configuracion configuracionActualizar = generarMockConfiguracionActualizada();
    BDDMockito.given(configuracionRepository.findById(1L)).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));

    // when: se actualiza la configuración con los datos generales
    Configuracion updated = configuracionService.update(configuracionActualizar);

    // then: los parámetros de unidades de vinculación no cambian
    Assertions.assertThat(updated.getDiasLimiteEvaluador()).isEqualTo(10);
    Assertions.assertThat(updated.getPeticionEvaluacionUnidadesVinculacionEnabled()).isTrue();
    Assertions.assertThat(updated.getPeticionEvaluacionInicializarUnidadVinculacionEnabled()).isTrue();
    Assertions.assertThat(updated.getTipoUnidadVinculacionDepartamentoRef()).isEqualTo("TIPO-DEP");
  }

  @ParameterizedTest
  @CsvSource({ "peticionEvaluacionUnidadesVinculacionEnabled,true",
      "peticionEvaluacionInicializarUnidadVinculacionEnabled,false",
      "tipoUnidadVinculacionDepartamentoRef,TIPO-DEP" })
  @WithMockUser(authorities = "ETI-MEM-V")
  void get_WithParamKey_ReturnsConfigParamOutput(String name, String value) {
    // given: una configuración con los parámetros de unidades informados
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionUnidadesVinculacionEnabled(Boolean.TRUE);
    configuracion.setPeticionEvaluacionInicializarUnidadVinculacionEnabled(Boolean.FALSE);
    configuracion.setTipoUnidadVinculacionDepartamentoRef("TIPO-DEP");
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));

    // when: se consulta el parámetro por su clave
    ConfigParamOutput output = configuracionService.get(name);

    // then: se devuelve la clave y su valor como texto
    Assertions.assertThat(output.getName()).isEqualTo(name);
    Assertions.assertThat(output.getValue()).isEqualTo(value);
  }

  @Test
  @WithMockUser(authorities = "ETI-MEM-V")
  void get_WithParamNull_ReturnsEmptyValue() {
    // given: una configuración sin tipo de unidad configurado
    Configuracion configuracion = generarMockConfiguracion();
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));

    // when: se consulta el tipo de unidad
    ConfigParamOutput output = configuracionService.get("tipoUnidadVinculacionDepartamentoRef");

    // then: el valor es vacío
    Assertions.assertThat(output.getValue()).isEmpty();
  }

  @Test
  void get_WithUnknownKey_ThrowsUnknownConfiguracionParamException() {
    // given: una clave que no existe
    String name = "unknown";

    Assertions.assertThatThrownBy(
        // when: se consulta la clave
        () -> configuracionService.get(name))
        // then: se lanza la excepción de parámetro de configuración desconocido
        .isInstanceOf(UnknownConfiguracionParamException.class);
  }

  @ParameterizedTest
  @CsvSource({ "peticionEvaluacionUnidadesVinculacionEnabled,true,true,false,false",
      "peticionEvaluacionUnidadesVinculacionEnabled,false,false,true,false",
      "peticionEvaluacionInicializarUnidadVinculacionEnabled,true,true,true,false",
      "peticionEvaluacionInicializarUnidadVinculacionEnabled,false,false,true,true",
      "tipoUnidadVinculacionDepartamentoRef,TIPO-DEP,TIPO-DEP,true,true",
      "tipoUnidadVinculacionDepartamentoRef,' ','',true,true" })
  void updateValue_WithParamKey_ReturnsUpdatedValue(String name, String value, String expected,
      Boolean apartadoEnabled, Boolean inicializacionEnabled) {
    // given: una configuración existente con un valor distinto del nuevo y los
    // parámetros de los que depende habilitados
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionUnidadesVinculacionEnabled(apartadoEnabled);
    configuracion.setPeticionEvaluacionInicializarUnidadVinculacionEnabled(inicializacionEnabled);
    configuracion.setTipoUnidadVinculacionDepartamentoRef(Boolean.TRUE.equals(inicializacionEnabled) ? "OTRO" : null);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));

    // when: se actualiza el parámetro por su clave
    ConfigParamOutput output = configuracionService.updateValue(name, value);

    // then: se devuelve el valor actualizado
    Assertions.assertThat(output.getName()).isEqualTo(name);
    Assertions.assertThat(output.getValue()).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({ "peticionEvaluacionInicializarUnidadVinculacionEnabled,false",
      "tipoUnidadVinculacionDepartamentoRef,''" })
  void updateValue_DisablingWithDependencyDisabled_ReturnsUpdatedValue(String name, String value) {
    // given: una configuración con todos los parámetros deshabilitados
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionUnidadesVinculacionEnabled(Boolean.FALSE);
    configuracion.setPeticionEvaluacionInicializarUnidadVinculacionEnabled(Boolean.FALSE);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));

    // when: se deshabilita un parámetro cuyo parámetro del que depende está
    // deshabilitado
    ConfigParamOutput output = configuracionService.updateValue(name, value);

    // then: se guarda sin error
    Assertions.assertThat(output.getName()).isEqualTo(name);
    BDDMockito.then(configuracionRepository).should().save(configuracion);
  }

  @Test
  void updateValue_WithUnknownKey_ThrowsUnknownConfiguracionParamException() {
    // given: una clave que no existe
    String name = "unknown";

    Assertions.assertThatThrownBy(
        // when: se actualiza la clave
        () -> configuracionService.updateValue(name, "true"))
        // then: se lanza la excepción de parámetro de configuración desconocido
        .isInstanceOf(UnknownConfiguracionParamException.class);
  }

  @Test
  void updateValue_WithApartadoDisabled_DisablesDependentParams() {
    // given: una configuración con el apartado, la inicialización y el tipo
    // habilitados
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionUnidadesVinculacionEnabled(Boolean.TRUE);
    configuracion.setPeticionEvaluacionInicializarUnidadVinculacionEnabled(Boolean.TRUE);
    configuracion.setTipoUnidadVinculacionDepartamentoRef("TIPO-DEP");
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));

    // when: se deshabilita el apartado
    configuracionService.updateValue("peticionEvaluacionUnidadesVinculacionEnabled", "false");

    // then: se deshabilitan en cascada la inicialización y el tipo
    Assertions.assertThat(configuracion.getPeticionEvaluacionUnidadesVinculacionEnabled()).isFalse();
    Assertions.assertThat(configuracion.getPeticionEvaluacionInicializarUnidadVinculacionEnabled()).isFalse();
    Assertions.assertThat(configuracion.getTipoUnidadVinculacionDepartamentoRef()).isNull();
  }

  @Test
  void updateValue_WithInicializacionDisabled_DisablesTipoAndKeepsApartado() {
    // given: una configuración con el apartado, la inicialización y el tipo
    // habilitados
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionUnidadesVinculacionEnabled(Boolean.TRUE);
    configuracion.setPeticionEvaluacionInicializarUnidadVinculacionEnabled(Boolean.TRUE);
    configuracion.setTipoUnidadVinculacionDepartamentoRef("TIPO-DEP");
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));

    // when: se deshabilita la inicialización
    configuracionService.updateValue("peticionEvaluacionInicializarUnidadVinculacionEnabled", "false");

    // then: se borra el tipo y el apartado sigue habilitado
    Assertions.assertThat(configuracion.getPeticionEvaluacionUnidadesVinculacionEnabled()).isTrue();
    Assertions.assertThat(configuracion.getPeticionEvaluacionInicializarUnidadVinculacionEnabled()).isFalse();
    Assertions.assertThat(configuracion.getTipoUnidadVinculacionDepartamentoRef()).isNull();
  }

  @ParameterizedTest
  @CsvSource({ "peticionEvaluacionInicializarUnidadVinculacionEnabled,true,false,false",
      "tipoUnidadVinculacionDepartamentoRef,TIPO-DEP,true,false" })
  void updateValue_WithDependencyDisabled_ThrowsConfiguracionParamDependencyException(String name, String value,
      Boolean apartadoEnabled, Boolean inicializacionEnabled) {
    // given: una configuración en la que el parámetro del que depende está
    // deshabilitado
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionUnidadesVinculacionEnabled(apartadoEnabled);
    configuracion.setPeticionEvaluacionInicializarUnidadVinculacionEnabled(inicializacionEnabled);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));

    Assertions.assertThatThrownBy(
        // when: se intenta habilitar el parámetro
        () -> configuracionService.updateValue(name, value))
        // then: se lanza la excepción de dependencia deshabilitada y no se guarda
        .isInstanceOf(ConfiguracionParamDependencyException.class);
    BDDMockito.then(configuracionRepository).should(BDDMockito.never()).save(ArgumentMatchers.any());
  }

  @Test
  void updateValue_WithAyudaIntroduccionEnabledAndIntroduccionHidden_ThrowsConfiguracionParamDependencyException() {
    // given: una configuración con el campo Introducción oculto
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionIntroduccionVisibility(FieldVisibility.HIDDEN);
    configuracion.setPeticionEvaluacionAyudaIntroduccionEnabled(Boolean.FALSE);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));

    Assertions.assertThatThrownBy(
        // when: se intenta habilitar la ayuda de la Introducción
        () -> configuracionService.updateValue("peticionEvaluacionAyudaIntroduccionEnabled", "true"))
        // then: se lanza la excepción de dependencia deshabilitada y no se guarda
        .isInstanceOf(ConfiguracionParamDependencyException.class);
    BDDMockito.then(configuracionRepository).should(BDDMockito.never()).save(ArgumentMatchers.any());
  }

  @ParameterizedTest
  @CsvSource({ "peticionEvaluacionAyudaFechaInicioEnabled,true",
      "peticionEvaluacionAyudaFechaInicio,'[{\"lang\":\"es\",\"value\":\"<p>Fecha</p>\"}]'",
      "peticionEvaluacionAyudaResumenEnabled,false",
      "peticionEvaluacionAyudaResumen,'[{\"lang\":\"es\",\"value\":\"<p>Resumen</p>\"}]'",
      "peticionEvaluacionAyudaDisenioMetodologicoEnabled,true",
      "peticionEvaluacionAyudaDisenioMetodologico,'[{\"lang\":\"es\",\"value\":\"<p>Diseño</p>\"}]'",
      "peticionEvaluacionAyudaIntroduccionEnabled,true",
      "peticionEvaluacionAyudaIntroduccion,'[{\"lang\":\"es\",\"value\":\"<p>Introducción</p>\"}]'",
      "peticionEvaluacionAyudaTareasEnabled,false",
      "peticionEvaluacionAyudaTareas,'[{\"lang\":\"es\",\"value\":\"<p>Tareas</p>\"}]'" })
  @WithMockUser(authorities = "ETI-PEV-INV-C")
  void get_WithParamInvestigadorAndInvAuthority_ReturnsConfigParamOutput(String name, String value) {
    // given: una configuración con las ayudas informadas y un usuario con
    // autoridad de investigador
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionAyudaFechaInicioEnabled(Boolean.TRUE);
    configuracion.setPeticionEvaluacionAyudaFechaInicio("[{\"lang\":\"es\",\"value\":\"<p>Fecha</p>\"}]");
    configuracion.setPeticionEvaluacionAyudaResumenEnabled(Boolean.FALSE);
    configuracion.setPeticionEvaluacionAyudaResumen("[{\"lang\":\"es\",\"value\":\"<p>Resumen</p>\"}]");
    configuracion.setPeticionEvaluacionAyudaDisenioMetodologicoEnabled(Boolean.TRUE);
    configuracion
        .setPeticionEvaluacionAyudaDisenioMetodologico("[{\"lang\":\"es\",\"value\":\"<p>Diseño</p>\"}]");
    configuracion.setPeticionEvaluacionAyudaIntroduccionEnabled(Boolean.TRUE);
    configuracion
        .setPeticionEvaluacionAyudaIntroduccion("[{\"lang\":\"es\",\"value\":\"<p>Introducción</p>\"}]");
    configuracion.setPeticionEvaluacionAyudaTareasEnabled(Boolean.FALSE);
    configuracion.setPeticionEvaluacionAyudaTareas("[{\"lang\":\"es\",\"value\":\"<p>Tareas</p>\"}]");
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));

    // when: se consulta el parámetro de ayuda por su clave
    ConfigParamOutput output = configuracionService.get(name);

    // then: se devuelve la clave y su valor como texto
    Assertions.assertThat(output.getName()).isEqualTo(name);
    Assertions.assertThat(output.getValue()).isEqualTo(value);
  }

  @ParameterizedTest
  @CsvSource({ "peticionEvaluacionUnidadesVinculacionEnabled", "peticionEvaluacionInicializarUnidadVinculacionEnabled",
      "tipoUnidadVinculacionDepartamentoRef" })
  @WithMockUser(authorities = { "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  void get_WithParamNoInvestigadorAndInvAuthority_ThrowsUserNotAuthorizedToReadConfiguracionParamException(
      String name) {
    // given: un parámetro no legible desde el perfil de investigación y un
    // usuario con solo autoridades de investigador

    Assertions.assertThatThrownBy(
        // when: se consulta el parámetro
        () -> configuracionService.get(name))
        // then: se lanza la excepción de acceso no autorizado y no se consulta la
        // configuración
        .isInstanceOf(UserNotAuthorizedToReadConfiguracionParamException.class);
    BDDMockito.then(configuracionRepository).should(BDDMockito.never()).findFirstByOrderByIdAsc();
  }

  @Test
  @WithMockUser(authorities = "SCOPE_sgi-cnf")
  void get_WithParamNoInvestigadorAndClientScope_ReturnsConfigParamOutput() {
    // given: un cliente con scope de configuración y un parámetro no legible desde
    // el perfil de investigación
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionUnidadesVinculacionEnabled(Boolean.TRUE);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));

    // when: se consulta el parámetro
    ConfigParamOutput output = configuracionService.get("peticionEvaluacionUnidadesVinculacionEnabled");

    // then: se devuelve el valor del parámetro
    Assertions.assertThat(output.getValue()).isEqualTo("true");
  }

  @Test
  @WithMockUser(authorities = { "ETI-PEV-INV-ER", "ETI-PEV-V" })
  void get_WithParamNoInvestigadorAndInvAndGestionAuthority_ReturnsConfigParamOutput() {
    // given: un usuario con autoridades de investigador y de gestión
    // y un parámetro no legible desde el perfil de investigación
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setTipoUnidadVinculacionDepartamentoRef("TIPO-DEP");
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));

    // when: se consulta el parámetro
    ConfigParamOutput output = configuracionService.get("tipoUnidadVinculacionDepartamentoRef");

    // then: se devuelve el valor del parámetro
    Assertions.assertThat(output.getValue()).isEqualTo("TIPO-DEP");
  }

  @Test
  @WithMockUser(authorities = "ETI-PEV-INV-C")
  void get_WithUnknownKeyAndInvAuthority_ThrowsUnknownConfiguracionParamException() {
    // given: una clave que no existe y un usuario con solo autoridad de
    // investigador
    String name = "unknown";

    Assertions.assertThatThrownBy(
        // when: se consulta la clave
        () -> configuracionService.get(name))
        // then: se lanza la excepción de parámetro desconocido antes que la de acceso
        // y no se llega a consultar la configuración
        .isInstanceOf(UnknownConfiguracionParamException.class);
    BDDMockito.then(configuracionRepository).shouldHaveNoInteractions();
  }

  @ParameterizedTest
  @CsvSource({ "peticionEvaluacionAyudaFechaInicio", "peticionEvaluacionAyudaResumen",
      "peticionEvaluacionAyudaDisenioMetodologico", "peticionEvaluacionAyudaIntroduccion",
      "peticionEvaluacionAyudaTareas" })
  void updateValue_WithAyudaTextoAndFlagDisabled_ThrowsConfiguracionParamDependencyException(String name) {
    // given: una configuración con las ayudas deshabilitadas
    Configuracion configuracion = generarMockConfiguracionAyudas(Boolean.FALSE);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));

    Assertions.assertThatThrownBy(
        // when: se intenta guardar un texto de ayuda
        () -> configuracionService.updateValue(name, TEXTO_AYUDA))
        // then: se lanza la excepción de dependencia deshabilitada y no se guarda
        .isInstanceOf(ConfiguracionParamDependencyException.class);
    BDDMockito.then(configuracionRepository).should(BDDMockito.never()).save(ArgumentMatchers.any());
  }

  @ParameterizedTest
  @CsvSource({ "peticionEvaluacionAyudaFechaInicio,'[]'", "peticionEvaluacionAyudaResumen,''",
      "peticionEvaluacionAyudaDisenioMetodologico,' '", "peticionEvaluacionAyudaIntroduccion,''",
      "peticionEvaluacionAyudaTareas,'[]'" })
  void updateValue_WithAyudaTextoVacioAndFlagDisabled_ThrowsConfiguracionParamDependencyException(String name,
      String value) {
    // given: una configuración con las ayudas deshabilitadas
    Configuracion configuracion = generarMockConfiguracionAyudas(Boolean.FALSE);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));

    Assertions.assertThatThrownBy(
        // when: se intenta guardar un texto de ayuda vacío
        () -> configuracionService.updateValue(name, value))
        // then: se lanza la excepción de dependencia deshabilitada y no se guarda
        .isInstanceOf(ConfiguracionParamDependencyException.class);
    BDDMockito.then(configuracionRepository).should(BDDMockito.never()).save(ArgumentMatchers.any());
  }

  @ParameterizedTest
  @CsvSource({ "peticionEvaluacionAyudaFechaInicio,'[]'", "peticionEvaluacionAyudaResumen,''",
      "peticionEvaluacionAyudaDisenioMetodologico,' '", "peticionEvaluacionAyudaIntroduccion,''",
      "peticionEvaluacionAyudaTareas,'[]'" })
  void updateValue_WithAyudaTextoVacio_ReturnsEmptyI18nValue(String name, String value) {
    // given: una configuración con las ayudas habilitadas
    Configuracion configuracion = generarMockConfiguracionAyudas(Boolean.TRUE);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));

    // when: se guarda un texto de ayuda vacío
    ConfigParamOutput output = configuracionService.updateValue(name, value);

    // then: se guarda sin error como texto multi-idioma vacío
    Assertions.assertThat(output.getName()).isEqualTo(name);
    Assertions.assertThat(output.getValue()).isEqualTo(Configuracion.EMPTY_LIST_VALUE);
  }

  @Test
  void updateValue_WithAyudaTextoNull_ReturnsEmptyI18nValue() {
    // given: una configuración con las ayudas habilitadas
    Configuracion configuracion = generarMockConfiguracionAyudas(Boolean.TRUE);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));

    // when: se guarda un texto de ayuda nulo
    ConfigParamOutput output = configuracionService.updateValue("peticionEvaluacionAyudaResumen", null);

    // then: se guarda como texto multi-idioma vacío
    Assertions.assertThat(output.getValue()).isEqualTo(Configuracion.EMPTY_LIST_VALUE);
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaResumen()).isEqualTo(Configuracion.EMPTY_LIST_VALUE);
  }

  @Test
  void updateValue_WithAyudaFlagDisabled_ClearsTexto() {
    // given: una configuración con las ayudas habilitadas y con texto
    Configuracion configuracion = generarMockConfiguracionAyudas(Boolean.TRUE);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));

    // when: se deshabilitan todas las ayudas
    configuracionService.updateValue("peticionEvaluacionAyudaDisenioMetodologicoEnabled", "false");
    configuracionService.updateValue("peticionEvaluacionAyudaFechaInicioEnabled", "false");
    configuracionService.updateValue("peticionEvaluacionAyudaIntroduccionEnabled", "false");
    configuracionService.updateValue("peticionEvaluacionAyudaResumenEnabled", "false");
    configuracionService.updateValue("peticionEvaluacionAyudaTareasEnabled", "false");

    // then: los flags quedan deshabilitados y los textos vacíos
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaDisenioMetodologicoEnabled()).isFalse();
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaFechaInicioEnabled()).isFalse();
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaIntroduccionEnabled()).isFalse();
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaResumenEnabled()).isFalse();
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaDisenioMetodologico())
        .isEqualTo(Configuracion.EMPTY_LIST_VALUE);
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaFechaInicio())
        .isEqualTo(Configuracion.EMPTY_LIST_VALUE);
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaIntroduccion())
        .isEqualTo(Configuracion.EMPTY_LIST_VALUE);
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaResumen()).isEqualTo(Configuracion.EMPTY_LIST_VALUE);
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaTareas()).isEqualTo(Configuracion.EMPTY_LIST_VALUE);
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaTareasEnabled()).isFalse();
  }

  @ParameterizedTest
  @CsvSource({ "peticionEvaluacionAyudaFechaInicio", "peticionEvaluacionAyudaResumen",
      "peticionEvaluacionAyudaDisenioMetodologico", "peticionEvaluacionAyudaIntroduccion",
      "peticionEvaluacionAyudaTareas" })
  void updateValue_WithAyudaTextoAndFlagEnabled_ReturnsUpdatedValue(String name) {
    // given: una configuración con las ayudas habilitadas
    Configuracion configuracion = generarMockConfiguracionAyudas(Boolean.TRUE);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));
    String texto = "[{\"lang\":\"es\",\"value\":\"<p><strong>Nuevo</strong></p>\"}]";

    // when: se guarda un texto de ayuda
    ConfigParamOutput output = configuracionService.updateValue(name, texto);

    // then: se devuelve el texto tal cual
    Assertions.assertThat(output.getName()).isEqualTo(name);
    Assertions.assertThat(output.getValue()).isEqualTo(texto);
  }

  @Test
  void updateValue_WithAyudaFlagEnabled_KeepsTexto() {
    // given: una configuración con las ayudas habilitadas y con texto
    Configuracion configuracion = generarMockConfiguracionAyudas(Boolean.TRUE);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));

    // when: se vuelve a habilitar la ayuda de Asignación de tareas
    ConfigParamOutput output = configuracionService.updateValue("peticionEvaluacionAyudaTareasEnabled", "true");

    // then: el flag sigue habilitado y el texto se conserva
    Assertions.assertThat(output.getValue()).isEqualTo("true");
    Assertions.assertThat(configuracion.getPeticionEvaluacionAyudaTareas()).isEqualTo(TEXTO_AYUDA);
  }

  @Test
  @WithMockUser(authorities = "ETI-PEV-INV-C")
  void get_WithParamIntroduccionVisibilityAndInvAuthority_ReturnsConfigParamOutput() {
    // given: una configuración con el campo Introducción obligatorio y un usuario
    // con autoridad de investigador
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionIntroduccionVisibility(FieldVisibility.REQUIRED);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));

    // when: se consulta la visibilidad del campo Introducción
    ConfigParamOutput output = configuracionService.get("peticionEvaluacionIntroduccionVisibility");

    // then: se devuelve el nombre de la visibilidad
    Assertions.assertThat(output.getName()).isEqualTo("peticionEvaluacionIntroduccionVisibility");
    Assertions.assertThat(output.getValue()).isEqualTo("REQUIRED");
  }

  @ParameterizedTest
  @EnumSource(FieldVisibility.class)
  void updateValue_WithIntroduccionVisibilityValida_ReturnsUpdatedValue(FieldVisibility visibility) {
    // given: una configuración con el campo Introducción oculto
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionIntroduccionVisibility(FieldVisibility.HIDDEN);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(ArgumentMatchers.<Configuracion>any()))
        .willAnswer(invocation -> invocation.getArgument(0, Configuracion.class));

    // when: se actualiza la visibilidad del campo Introducción
    ConfigParamOutput output = configuracionService.updateValue("peticionEvaluacionIntroduccionVisibility",
        visibility.name());

    // then: se guarda y se devuelve el nombre de la visibilidad
    Assertions.assertThat(output.getName()).isEqualTo("peticionEvaluacionIntroduccionVisibility");
    Assertions.assertThat(output.getValue()).isEqualTo(visibility.name());
    Assertions.assertThat(configuracion.getPeticionEvaluacionIntroduccionVisibility()).isEqualTo(visibility);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = { " ", "FOO", "required" })
  void updateValue_WithIntroduccionVisibilityNoValida_ThrowsConfiguracionParamInvalidValueException(String value) {
    // given: una configuración con el campo Introducción oculto
    Configuracion configuracion = generarMockConfiguracion();
    configuracion.setPeticionEvaluacionIntroduccionVisibility(FieldVisibility.HIDDEN);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));

    Assertions.assertThatThrownBy(
        // when: se intenta guardar una visibilidad desconocida, vacía o nula
        () -> configuracionService.updateValue("peticionEvaluacionIntroduccionVisibility", value))
        // then: se lanza la excepción, la visibilidad no cambia y no se guarda
        .isInstanceOf(ConfiguracionParamInvalidValueException.class);
    Assertions.assertThat(configuracion.getPeticionEvaluacionIntroduccionVisibility())
        .isEqualTo(FieldVisibility.HIDDEN);
    BDDMockito.then(configuracionRepository).should(BDDMockito.never()).save(ArgumentMatchers.any());
  }

  @Test
  void updateValue_WithoutConfiguracion_ThrowsConfiguracionNotFoundException() {
    // given: no existe configuración
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se actualiza un parámetro
        () -> configuracionService.updateValue("peticionEvaluacionUnidadesVinculacionEnabled", "true"))
        // then: se lanza la excepción de configuración no encontrada
        .isInstanceOf(ConfiguracionNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto Configuracion
   * 
   * @return el objeto Configuracion
   */

  public Configuracion generarMockConfiguracion() {

    Configuracion configuracion = new Configuracion();

    configuracion.setId(1L);
    configuracion.setMesesArchivadaPendienteCorrecciones(20);
    configuracion.setDiasLimiteEvaluador(3);
    configuracion.setDiasArchivadaInactivo(2);

    return configuracion;
  }

  /**
   * Devuelve una {@link Configuracion} con todas las ayudas de la solicitud de
   * evaluación en el estado indicado y, si están habilitadas, con texto.
   *
   * @param enabled estado de los flags de las ayudas.
   * @return la {@link Configuracion}.
   */
  private Configuracion generarMockConfiguracionAyudas(Boolean enabled) {
    Configuracion configuracion = generarMockConfiguracion();
    String texto = Boolean.TRUE.equals(enabled) ? TEXTO_AYUDA : Configuracion.EMPTY_LIST_VALUE;
    configuracion.setPeticionEvaluacionAyudaDisenioMetodologico(texto);
    configuracion.setPeticionEvaluacionAyudaDisenioMetodologicoEnabled(enabled);
    configuracion.setPeticionEvaluacionAyudaFechaInicio(texto);
    configuracion.setPeticionEvaluacionAyudaFechaInicioEnabled(enabled);
    configuracion.setPeticionEvaluacionIntroduccionVisibility(
        Boolean.TRUE.equals(enabled) ? FieldVisibility.OPTIONAL : FieldVisibility.HIDDEN);
    configuracion.setPeticionEvaluacionAyudaIntroduccion(texto);
    configuracion.setPeticionEvaluacionAyudaIntroduccionEnabled(enabled);
    configuracion.setPeticionEvaluacionAyudaResumen(texto);
    configuracion.setPeticionEvaluacionAyudaResumenEnabled(enabled);
    configuracion.setPeticionEvaluacionAyudaTareas(texto);
    configuracion.setPeticionEvaluacionAyudaTareasEnabled(enabled);
    return configuracion;
  }

  /**
   * Función que devuelve un objeto Configuracion
   * 
   * @return el objeto Configuracion
   */

  public Configuracion generarMockConfiguracionActualizada() {

    Configuracion configuracion = new Configuracion();

    configuracion.setId(1L);
    configuracion.setMesesArchivadaPendienteCorrecciones(20);
    configuracion.setDiasLimiteEvaluador(10);
    configuracion.setDiasArchivadaInactivo(2);

    return configuracion;
  }

}