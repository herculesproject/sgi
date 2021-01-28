package org.crue.hercules.sgi.csp.repository;

import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProyectoPeriodoSeguimientoDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoPeriodoSeguimientoDocumentoRepository repository;

  @Test
  public void existsByProyectoSeguimiento_ReturnsTrue() throws Exception {

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()//
        .nombre("nombreModeloEjecucion")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimientoDocumento with same ProyectoSeguimientoId
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

    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento//
        .builder()//
        .proyecto(proyecto1)//
        .numPeriodo(1)//
        .fechaInicio(LocalDate.now().plusDays(1))//
        .fechaFin(LocalDate.now().plusMonths(1))//
        .build();

    entityManager.persistAndFlush(proyectoPeriodoSeguimientoCientifico);

    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = ProyectoPeriodoSeguimientoDocumento//
        .builder()//
        .proyectoPeriodoSeguimiento(proyectoPeriodoSeguimientoCientifico)//
        .documentoRef("doc-1")//
        .nombre("nombre-1")//
        .build();

    entityManager.persistAndFlush(proyectoPeriodoSeguimientoDocumento);

    // when: se busca ProyectoPeriodoSeguimientoDocumento por ProyectoSeguimientoId
    // ordenadas por Fecha Inicio
    repository.existsByProyectoPeriodoSeguimientoId(1L);
  }

  @Test
  public void existsByProyectoSeguimiento_ReturnsFalse() throws Exception {

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()//
        .nombre("nombreModeloEjecucion")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimientoDocumento with same ProyectoSeguimientoId
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

      ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = ProyectoPeriodoSeguimientoDocumento//
          .builder()//
          .proyectoPeriodoSeguimiento(proyectoPeriodoSeguimientoCientifico)//
          .documentoRef("doc-" + i)//
          .nombre("nombre-" + i)//
          .build();

      entityManager.persistAndFlush(proyectoPeriodoSeguimientoDocumento);

    }

    // when: se busca ProyectoPeriodoSeguimientoDocumento por ProyectoSeguimientoId
    // ordenadas por Fecha Inicio
    Boolean dataFound = repository.existsByProyectoPeriodoSeguimientoId(111L);

    // then: Se recupera ProyectoPeriodoSeguimientoDocumento con el
    // ProyectoSeguimientoId ordenados por Fecha Inicio
    Assertions.assertThat(dataFound).isFalse();
  }

  @Test
  public void deleteByProyectoSeguimiento_WithExistingId_NoReturnsAnyException() throws Exception {

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()//
        .nombre("nombreModeloEjecucion")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimientoDocumento with same ProyectoSeguimientoId
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

      ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = ProyectoPeriodoSeguimientoDocumento//
          .builder()//
          .proyectoPeriodoSeguimiento(proyectoPeriodoSeguimientoCientifico)//
          .documentoRef("doc-" + i)//
          .nombre("nombre-" + i)//
          .build();

      entityManager.persistAndFlush(proyectoPeriodoSeguimientoDocumento);

    }

    // when: se busca ProyectoPeriodoSeguimientoDocumento por ProyectoSeguimientoId
    // ordenadas por Fecha Inicio
    repository.deleteByProyectoPeriodoSeguimientoId(1L);
  }

}
