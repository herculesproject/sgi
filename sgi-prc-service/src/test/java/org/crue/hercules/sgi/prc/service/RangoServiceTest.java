package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.RangoGapBetweenRangesRangeException;
import org.crue.hercules.sgi.prc.exceptions.RangoMultiplesNullDesdeException;
import org.crue.hercules.sgi.prc.exceptions.RangoMultiplesNullHastaException;
import org.crue.hercules.sgi.prc.exceptions.RangoMultiplesTemporalidadFinalException;
import org.crue.hercules.sgi.prc.exceptions.RangoMultiplesTemporalidadInicialException;
import org.crue.hercules.sgi.prc.exceptions.RangoOverlapRangeException;
import org.crue.hercules.sgi.prc.exceptions.RangoPuntosNullOrLessThanZeroException;
import org.crue.hercules.sgi.prc.model.Rango;
import org.crue.hercules.sgi.prc.model.Rango.TipoRango;
import org.crue.hercules.sgi.prc.model.Rango.TipoTemporalidad;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.crue.hercules.sgi.prc.repository.RangoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

/**
 * RangoServiceTest
 */
@Import({ RangoService.class, ApplicationContextSupport.class })
public class RangoServiceTest extends BaseServiceTest {

  @MockBean
  private RangoRepository repository;

  @MockBean
  private ConvocatoriaBaremacionRepository convocatoriaBaremacionRepository;

  @Autowired
  private RangoService rangoService;

  @Test
  public void validateRangosEmpty() {
    List<Rango> rangos = new ArrayList<>();

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void validateRangosNull() {
    List<Rango> rangos = null;
    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void validateRangosPuntosNull() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void validateRangosPuntosZero() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(BigDecimal.ZERO)
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .puntos(BigDecimal.ZERO)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(RangoPuntosNullOrLessThanZeroException.class);
  }

  @Test
  public void validateRangosPuntosNegative() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(-331))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .puntos(BigDecimal.ZERO)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(RangoPuntosNullOrLessThanZeroException.class);
  }

  @Test
  public void validateRangosConvocatoriaBaremacionIdNull() {
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(ConstraintViolationException.class);

  }

  @Test
  public void validateRangosTwoTemporalidadInicial() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(RangoMultiplesTemporalidadInicialException.class);
  }

  @Test
  public void validateRangosTwoTemporalidadFinal() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(RangoMultiplesTemporalidadFinalException.class);
  }

  @Test
  public void validateRangosTwoNullDesde() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(null)
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(null)
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(RangoMultiplesNullDesdeException.class);
  }

  @Test
  public void validateRangosTwoNullHasta() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(null)
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(null)
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(RangoMultiplesNullHastaException.class);
  }

  @Test
  public void validateRangosDesdeEqualsHasta() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(RangoOverlapRangeException.class);
  }

  @Test
  public void validateRangosOverlapped() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(9))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(10))
        .hasta(new BigDecimal(19))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INTERMEDIO)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(20))
        .hasta(new BigDecimal(19))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(RangoOverlapRangeException.class);
  }

  @Test
  public void validateRangosOverlapped2() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(null)
        .hasta(new BigDecimal(9))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(8))
        .hasta(new BigDecimal(19))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INTERMEDIO)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(20))
        .hasta(new BigDecimal(19))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(RangoOverlapRangeException.class);
  }

  @Test
  public void validateRangosDesdeGapHasta() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(9))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(11))
        .hasta(new BigDecimal(19))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    Assertions.assertThatThrownBy(() -> rangoService.validateRangos(rangos))
        .isInstanceOf(RangoGapBetweenRangesRangeException.class);
  }

  @Test
  public void validateRangosOk() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(9))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(10))
        .hasta(new BigDecimal(19))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    rangoService.validateRangos(rangos);
    Assertions.assertThat(rangos).isEqualTo(rangos);
  }

  @Test
  public void validateRangosOk2() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(9))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(10))
        .hasta(new BigDecimal(19))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INTERMEDIO)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(20))
        .hasta(new BigDecimal(29))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    rangoService.validateRangos(rangos);
    Assertions.assertThat(rangos).isEqualTo(rangos);
  }

  @Test
  public void validateRangosOk3() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(null)
        .hasta(new BigDecimal(9))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(10))
        .hasta(new BigDecimal(19))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INTERMEDIO)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(20))
        .hasta(new BigDecimal(29))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    rangoService.validateRangos(rangos);
    Assertions.assertThat(rangos).isEqualTo(rangos);
  }

  @Test
  public void validateRangosOk4() {
    Long convocatoriaBaremacionId = 1L;
    List<Rango> rangos = new ArrayList<>();
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(null)
        .hasta(new BigDecimal(9))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(10))
        .hasta(new BigDecimal(19))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INTERMEDIO)
        .build());
    rangos.add(Rango.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(20))
        .hasta(null)
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    rangoService.validateRangos(rangos);
    Assertions.assertThat(rangos).isEqualTo(rangos);
  }

}
