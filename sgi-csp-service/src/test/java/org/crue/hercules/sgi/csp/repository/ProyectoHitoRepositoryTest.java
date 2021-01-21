package org.crue.hercules.sgi.csp.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProyectoHitoRepositoryTest
 */
@DataJpaTest

public class ProyectoHitoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoHitoRepository repository;

  @Test
  public void findByProyectoIdAndFechaAndTipoHitoId_ReturnsProyectoHito() throws Exception {

    // given: Proyecto, tipoHito y fecha no encuentra coincidencias
    ProyectoHito proyectoHito1 = generarMockProyectoHito("-001");
    ProyectoHito proyectoHito2 = generarMockProyectoHito("-002");

    Long idProyectoBusqueda = proyectoHito1.getProyecto().getId();
    LocalDate fechaBusqueda = proyectoHito2.getFecha();
    Long idTipoHitoBusqueda = proyectoHito1.getTipoHito().getId();

    // when: se busca el ProyectoHito
    Optional<ProyectoHito> proyectoHitoEncontrado = repository.findByProyectoIdAndFechaAndTipoHitoId(idProyectoBusqueda,
        fechaBusqueda, idTipoHitoBusqueda);

    // then: Recupera el ProyectoHito buscado
    Assertions.assertThat(proyectoHitoEncontrado.isPresent()).as("isPresent()").isTrue();
    Assertions.assertThat(proyectoHitoEncontrado.get().getId()).as("getId()").isEqualTo(proyectoHito1.getId());
    Assertions.assertThat(proyectoHitoEncontrado.get().getProyecto().getId()).as("getProyecto().getId()")
        .isEqualTo(idProyectoBusqueda);
    Assertions.assertThat(proyectoHitoEncontrado.get().getFecha()).as("getFecha()").isEqualTo(fechaBusqueda);
    Assertions.assertThat(proyectoHitoEncontrado.get().getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(idTipoHitoBusqueda);

  }

  @Test
  public void findByProyectoIdAndFechaAndTipoHitoId_ReturnsEmpty() throws Exception {

    // given: Proyecto, tipoHito y fecha no encuentra coincidencias
    ProyectoHito proyectoHito1 = generarMockProyectoHito("-001");
    ProyectoHito proyectoHito2 = generarMockProyectoHito("-002");

    Long idProyectoBusqueda = proyectoHito1.getProyecto().getId();
    LocalDate fechaBusqueda = proyectoHito1.getFecha();
    Long idTipoHitoBusqueda = proyectoHito2.getTipoHito().getId();

    // when: se busca el ProyectoHito
    Optional<ProyectoHito> proyectoHitoEncontrado = repository.findByProyectoIdAndFechaAndTipoHitoId(idProyectoBusqueda,
        fechaBusqueda, idTipoHitoBusqueda);

    // then: No se recupera el ProyectoHito buscado
    Assertions.assertThat(proyectoHitoEncontrado).isEqualTo(Optional.empty());
  }

  /**
   * Función que genera ProyectoHito
   * 
   * @param suffix
   * @return el objeto ProyectoHito
   */
  private ProyectoHito generarMockProyectoHito(String suffix) {

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
    entityManager.persistAndFlush(proyecto);

    TipoHito tipoHito = TipoHito.builder()//
        .nombre("tipoHito" + suffix)//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(tipoHito);

    ModeloTipoHito modeloTipoHito = ModeloTipoHito.builder()//
        .modeloEjecucion(modeloEjecucion)//
        .tipoHito(tipoHito)//
        .convocatoria(Boolean.TRUE)//
        .proyecto(Boolean.TRUE)//
        .solicitud(Boolean.TRUE)//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(modeloTipoHito);

    ProyectoHito proyectoHito = ProyectoHito.builder()//
        .tipoHito(tipoHito)//
        .proyecto(proyecto)//
        .fecha(LocalDate.of(2020, 10, 01))//
        .comentario("comentarioProyectoHito-" + suffix)//
        .generaAviso(Boolean.TRUE)//
        .build();
    return entityManager.persistAndFlush(proyectoHito);
  }

}
