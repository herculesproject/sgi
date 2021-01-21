package org.crue.hercules.sgi.csp.repository;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProyectoRepositoryTest
 */
@DataJpaTest
public class ProyectoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoRepository repository;

  @Test
  public void existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual_ReturnsTRUE() throws Exception {
    // given: un proyecto comprencido entre las fechas 2020-01-01 y 2020-12/31
    Proyecto proyecto1 = generarMockProyecto("-001");

    Long idProyectoBusqueda = proyecto1.getId();
    LocalDate fechaIniBusqueda = proyecto1.getFechaInicio().plusDays(1);
    LocalDate fechaFinBusqueda = proyecto1.getFechaFin().minusDays(1);

    // when: comprueba si rango de fechas indicadas están dentro del proyecto
    boolean isDentroProyecto = repository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
        idProyectoBusqueda, fechaIniBusqueda, fechaFinBusqueda);

    // then: Confirma que las fechas están dentro del proyecto
    Assertions.assertThat(isDentroProyecto).isTrue();

  }

  @Test
  public void existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual_ReturnsFALSE() throws Exception {
    // given: un proyecto comprencido entre las fechas 2020-01-01 y 2020-12/31
    Proyecto proyecto1 = generarMockProyecto("-001");

    Long idProyectoBusqueda = proyecto1.getId();

    LocalDate fechaIniBusquedaCaso1 = proyecto1.getFechaInicio().minusDays(10);
    LocalDate fechaFinBusquedaCaso1 = proyecto1.getFechaInicio().minusDays(9);
    // when: comprueba si rango de fechas indicadas están dentro del proyecto
    boolean isDentroProyectoCaso1 = repository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
        idProyectoBusqueda, fechaIniBusquedaCaso1, fechaFinBusquedaCaso1);
    // then: Confirma que las fechas están fuera del proyecto
    Assertions.assertThat(isDentroProyectoCaso1).isFalse();

    LocalDate fechaIniBusquedaCaso2 = proyecto1.getFechaInicio().minusDays(1);
    LocalDate fechaFinBusquedaCaso2 = proyecto1.getFechaFin().minusDays(1);
    // when: comprueba si rango de fechas indicadas están dentro del proyecto
    boolean isDentroProyectoCaso2 = repository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
        idProyectoBusqueda, fechaIniBusquedaCaso2, fechaFinBusquedaCaso2);
    // then: Confirma que las fechas están fuera del proyecto
    Assertions.assertThat(isDentroProyectoCaso2).isFalse();

    LocalDate fechaIniBusquedaCaso3 = proyecto1.getFechaInicio();
    LocalDate fechaFinBusquedaCaso3 = proyecto1.getFechaFin().plusDays(1);
    // when: comprueba si rango de fechas indicadas están dentro del proyecto
    boolean isDentroProyectoCaso3 = repository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
        idProyectoBusqueda, fechaIniBusquedaCaso3, fechaFinBusquedaCaso3);
    // then: Confirma que las fechas están fuera del proyecto
    Assertions.assertThat(isDentroProyectoCaso3).isFalse();

    LocalDate fechaIniBusquedaCaso4 = proyecto1.getFechaFin().plusDays(9);
    LocalDate fechaFinBusquedaCaso4 = proyecto1.getFechaFin().plusDays(10);
    // when: comprueba si rango de fechas indicadas están dentro del proyecto
    boolean isDentroProyectoCaso4 = repository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
        idProyectoBusqueda, fechaIniBusquedaCaso4, fechaFinBusquedaCaso4);
    // then: Confirma que las fechas están fuera del proyecto
    Assertions.assertThat(isDentroProyectoCaso4).isFalse();
  }

  /**
   * Función que genera Proyecto
   * 
   * @param suffix
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto(String suffix) {

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()//
        .nombre("nombreModeloEjecucion" + suffix)//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    TipoFinalidad tipoFinalidad = TipoFinalidad.builder()//
        .nombre("nombreTipoFinalidad" + suffix)//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(tipoFinalidad);

    TipoAmbitoGeografico tipoAmbitoGeografico = TipoAmbitoGeografico.builder()//
        .nombre("nombreTipoAmbitoGeografico" + suffix)//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(tipoAmbitoGeografico);

    ModeloUnidad modeloUnidad = ModeloUnidad.builder()//
        .modeloEjecucion(modeloEjecucion)//
        .unidadGestionRef("OPE")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(modeloUnidad);

    Proyecto proyecto = Proyecto.builder()//
        .acronimo("PR" + suffix)//
        .codigoExterno("COD" + suffix)//
        .titulo("titulo-" + suffix)//
        .unidadGestionRef("OPE")//
        .modeloEjecucion(modeloEjecucion)//
        .finalidad(tipoFinalidad)//
        .ambitoGeografico(tipoAmbitoGeografico)//
        .fechaInicio(LocalDate.of(2020, 01, 01))//
        .fechaFin(LocalDate.of(2020, 12, 31))//
        .activo(Boolean.TRUE)//
        .build();
    return entityManager.persistAndFlush(proyecto);
  }

}