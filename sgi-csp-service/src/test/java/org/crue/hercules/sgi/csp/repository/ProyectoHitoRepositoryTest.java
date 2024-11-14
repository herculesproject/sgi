package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeograficoNombre;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidadNombre;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoHitoNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProyectoHitoRepositoryTest
 */
@DataJpaTest
class ProyectoHitoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoHitoRepository repository;

  @Test
  void findByProyectoIdAndFechaAndTipoHitoId_ReturnsProyectoHito() throws Exception {

    // given: Proyecto, tipoHito y fecha no encuentra coincidencias
    ProyectoHito proyectoHito1 = generarMockProyectoHito("-001");
    ProyectoHito proyectoHito2 = generarMockProyectoHito("-002");

    Long idProyectoBusqueda = proyectoHito1.getProyectoId();
    Instant fechaBusqueda = proyectoHito2.getFecha();
    Long idTipoHitoBusqueda = proyectoHito1.getTipoHito().getId();

    // when: se busca el ProyectoHito
    Optional<ProyectoHito> proyectoHitoEncontrado = repository.findByProyectoIdAndFechaAndTipoHitoId(
        idProyectoBusqueda,
        fechaBusqueda, idTipoHitoBusqueda);

    // then: Recupera el ProyectoHito buscado
    Assertions.assertThat(proyectoHitoEncontrado.isPresent()).as("isPresent()").isTrue();
    Assertions.assertThat(proyectoHitoEncontrado.get().getId()).as("getId()").isEqualTo(proyectoHito1.getId());
    Assertions.assertThat(proyectoHitoEncontrado.get().getProyectoId()).as("getProyecto().getId()")
        .isEqualTo(idProyectoBusqueda);
    Assertions.assertThat(proyectoHitoEncontrado.get().getFecha()).as("getFecha()").isEqualTo(fechaBusqueda);
    Assertions.assertThat(proyectoHitoEncontrado.get().getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(idTipoHitoBusqueda);

  }

  @Test
  void findByProyectoIdAndFechaAndTipoHitoId_ReturnsEmpty() throws Exception {

    // given: Proyecto, tipoHito y fecha no encuentra coincidencias
    ProyectoHito proyectoHito1 = generarMockProyectoHito("-001");
    ProyectoHito proyectoHito2 = generarMockProyectoHito("-002");

    Long idProyectoBusqueda = proyectoHito1.getProyectoId();
    Instant fechaBusqueda = proyectoHito1.getFecha();
    Long idTipoHitoBusqueda = proyectoHito2.getTipoHito().getId();

    // when: se busca el ProyectoHito
    Optional<ProyectoHito> proyectoHitoEncontrado = repository.findByProyectoIdAndFechaAndTipoHitoId(
        idProyectoBusqueda,
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
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion" + suffix));

    // @formatter:off
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre(nombreModeloEjecucion)
        .activo(Boolean.TRUE)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    Set<TipoFinalidadNombre> nombreTipoFinalidad = new HashSet<>();
    nombreTipoFinalidad.add(new TipoFinalidadNombre(Language.ES, "nombreTipoFinalidad" + suffix));
    
    TipoFinalidad tipoFinalidad = TipoFinalidad.builder()
        .nombre(nombreTipoFinalidad)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoFinalidad);
    
    Set<TipoAmbitoGeograficoNombre> tipoAmbitoGeograficoNombre = new HashSet<>();
    tipoAmbitoGeograficoNombre.add(new TipoAmbitoGeograficoNombre(Language.ES, "nombreTipoAmbitoGeografico" + suffix));

    TipoAmbitoGeografico tipoAmbitoGeografico = TipoAmbitoGeografico.builder()
        .nombre(tipoAmbitoGeograficoNombre)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoAmbitoGeografico);

    ModeloUnidad modeloUnidad = ModeloUnidad.builder()
        .modeloEjecucion(modeloEjecucion)
        .unidadGestionRef("2")
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(modeloUnidad);

    Proyecto proyecto = Proyecto.builder()
        .acronimo("PR" + suffix)
        .codigoExterno("COD" + suffix)
        .titulo("titulo-" + suffix)
        .unidadGestionRef("2")
        .modeloEjecucion(modeloEjecucion)
        .finalidad(tipoFinalidad)
        .ambitoGeografico(tipoAmbitoGeografico)
        .fechaInicio(Instant.parse("2020-01-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-12-31T23:59:59Z"))
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto);

    Set<TipoHitoNombre> nombreTipoHito = new HashSet<>();
    nombreTipoHito.add(new TipoHitoNombre(Language.ES, "tipoHito" + suffix));

    TipoHito tipoHito = TipoHito.builder()
        .nombre(nombreTipoHito)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoHito);

    ModeloTipoHito modeloTipoHito = ModeloTipoHito.builder()
        .modeloEjecucion(modeloEjecucion)
        .tipoHito(tipoHito)
        .convocatoria(Boolean.TRUE)
        .proyecto(Boolean.TRUE)
        .solicitud(Boolean.TRUE)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(modeloTipoHito);

    ProyectoHito proyectoHito = ProyectoHito.builder()
        .tipoHito(tipoHito)
        .proyectoId(proyecto.getId())
        .fecha(Instant.parse("2020-10-01T00:00:00Z"))
        .comentario("comentarioProyectoHito-" + suffix)
        .build();
    // @formatter:on
    return entityManager.persistAndFlush(proyectoHito);
  }

}
