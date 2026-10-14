package org.crue.hercules.sgi.eti.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.ConfigParamOutput;
import org.crue.hercules.sgi.eti.exceptions.ConfiguracionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.ConfiguracionParamDependencyException;
import org.crue.hercules.sgi.eti.exceptions.UnknownConfiguracionParamException;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.eti.service.impl.ConfiguracionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;

/**
 * ConfiguracionServiceTest
 */
public class ConfiguracionServiceTest extends BaseServiceTest {

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