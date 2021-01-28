package org.crue.hercules.sgi.csp.repository;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProyectoPeriodoSeguimientoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoPeriodoSeguimientoRepository repository;

  @Test
  public void findAllByProyectoIdOrderByFechaInicial_ReturnsProyectoPeriodoSeguimientoList() throws Exception {

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()//
        .nombre("nombreModeloEjecucion")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimiento with same ProyectoId
    Proyecto proyecto1 = Proyecto.builder()//
        .unidadGestionRef("OPE").modeloEjecucion(modeloEjecucion)//
        .titulo("PRO1")//
        .fechaInicio(LocalDate.now())//
        .fechaFin(LocalDate.now().plusMonths(3)).activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(proyecto1);

    Proyecto proyecto2 = Proyecto.builder()//
        .unidadGestionRef("OPE").modeloEjecucion(modeloEjecucion)//
        .titulo("PRO2")//
        .fechaInicio(LocalDate.now())//
        .fechaFin(LocalDate.now().plusMonths(3)).activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(proyecto2);

    for (int i = 11; i > 1; i--) {
      ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento//
          .builder()//
          .proyecto((i % 2 == 0) ? proyecto2 : proyecto1)//
          .numPeriodo(i / 2)//
          .fechaInicio(LocalDate.now().plusDays(i - 1))//
          .fechaFin(LocalDate.now().plusMonths(i))//
          .build();

      entityManager.persistAndFlush(proyectoPeriodoSeguimientoCientifico);
    }
    Long proyectoIdBuscado = proyecto1.getId();

    // when: se busca ProyectoPeriodoSeguimiento por ProyectoId
    // ordenadas por Fecha Inicio
    List<ProyectoPeriodoSeguimiento> dataFound = repository.findAllByProyectoIdOrderByFechaInicio(proyectoIdBuscado);

    // then: Se recupera ProyectoPeriodoSeguimiento con el
    // ProyectoId ordenados por Fecha Inicio
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound).size().isEqualTo(5);

    for (int i = 0; i < dataFound.size(); i++) {
      Assertions.assertThat(dataFound.get(i).getNumPeriodo()).as("getNumPeriodo()").isEqualTo(i + 1);
    }
  }

  @Test
  public void findAllByProyectoIdOrderByFechaInicio_ReturnsNull() throws Exception {
    // given: 10 ProyectoPeriodoSeguimiento
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()//
        .nombre("nombreModeloEjecucion")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimiento with same ProyectoId
    Proyecto proyecto1 = Proyecto.builder()//
        .unidadGestionRef("OPE").modeloEjecucion(modeloEjecucion)//
        .titulo("PRO1")//
        .fechaInicio(LocalDate.now())//
        .fechaFin(LocalDate.now().plusMonths(3)).activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(proyecto1);

    Proyecto proyecto2 = Proyecto.builder()//
        .unidadGestionRef("OPE").modeloEjecucion(modeloEjecucion)//
        .titulo("PRO2")//
        .fechaInicio(LocalDate.now())//
        .fechaFin(LocalDate.now().plusMonths(3)).activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(proyecto2);

    for (int i = 11; i > 1; i--) {
      ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento//
          .builder()//
          .proyecto((i % 2 == 0) ? proyecto2 : proyecto1)//
          .numPeriodo(i / 2)//
          .fechaInicio(LocalDate.now())//
          .fechaFin(LocalDate.now().plusMonths(1))//
          .observaciones("obs-" + i)//
          .build();

      if (i % 2 == 0) {
        entityManager.persistAndFlush(proyectoPeriodoSeguimientoCientifico);
      }
    }
    Long proyectoIdBuscado = proyecto1.getId();

    // when: se busca ProyectoPeriodoSeguimiento por ProyectoId
    // ordenadas por Fecha Inicio
    List<ProyectoPeriodoSeguimiento> dataFound = repository.findAllByProyectoIdOrderByFechaInicio(proyectoIdBuscado);

    // then: No encuentra ProyectoPeriodoSeguimiento para
    // ProyectoId
    Assertions.assertThat(dataFound).isEmpty();
  }

}
