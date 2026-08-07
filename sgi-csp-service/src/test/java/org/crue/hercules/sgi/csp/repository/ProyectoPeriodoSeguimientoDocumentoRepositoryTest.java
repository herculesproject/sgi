package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumentoNombre;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoPeriodoSeguimientoDocumentoSpecifications;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

@DataJpaTest
class ProyectoPeriodoSeguimientoDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoPeriodoSeguimientoDocumentoRepository repository;

  @Test
  void existsByProyectoSeguimiento_ReturnsTrue() {
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion"));

    // @formatter:off
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre(nombreModeloEjecucion)
        .activo(Boolean.TRUE)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimientoDocumento with same ProyectoSeguimientoId
    Set<ProyectoTitulo> tituloProyecto1 = new HashSet<>();
    tituloProyecto1.add(new ProyectoTitulo(Language.ES, "PRO1"));

    Proyecto proyecto1 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto1)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3))))
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto1);

        Set<ProyectoTitulo> tituloProyecto2 = new HashSet<>();
    tituloProyecto2.add(new ProyectoTitulo(Language.ES, "PRO2"));

    Proyecto proyecto2 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto2)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto2);

    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento.builder()
        .proyectoId(proyecto1.getId())
        .numPeriodo(1)
        .tipoSeguimiento(TipoSeguimiento.FINAL)
        .fechaInicio(Instant.now().plus(Period.ofDays(1)))
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(1))))
        .build();

    entityManager.persistAndFlush(proyectoPeriodoSeguimientoCientifico);

    Set<ProyectoPeriodoSeguimientoDocumentoNombre> nombreDocumento = new HashSet<>();
    nombreDocumento.add(new ProyectoPeriodoSeguimientoDocumentoNombre(Language.ES,"nombre-1"));

    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = ProyectoPeriodoSeguimientoDocumento
        .builder()
        .proyectoPeriodoSeguimientoId(proyectoPeriodoSeguimientoCientifico.getId())
        .documentoRef("doc-1")
        .nombre(nombreDocumento)
        .build();

    entityManager.persistAndFlush(proyectoPeriodoSeguimientoDocumento);
    // @formatter:on

    // when: se busca ProyectoPeriodoSeguimientoDocumento por ProyectoSeguimientoId
    boolean exists = repository.existsByProyectoPeriodoSeguimientoId(proyectoPeriodoSeguimientoCientifico.getId());
    Assertions.assertThat(exists).isTrue();
  }

  @Test
  void existsByProyectoSeguimiento_ReturnsFalse() {
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion"));

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre(nombreModeloEjecucion)
        .activo(Boolean.TRUE)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimientoDocumento with same ProyectoSeguimientoId
    Set<ProyectoTitulo> tituloProyecto1 = new HashSet<>();
    tituloProyecto1.add(new ProyectoTitulo(Language.ES, "PRO1"));

    Proyecto proyecto1 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto1)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto1);

    Set<ProyectoTitulo> tituloProyecto2 = new HashSet<>();
    tituloProyecto2.add(new ProyectoTitulo(Language.ES, "PRO2"));

    Proyecto proyecto2 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto2)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto2);

    for (int i = 11; i > 1; i--) {
      ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento
          .builder()
          .proyectoId((i % 2 == 0) ? proyecto2.getId() : proyecto1.getId())
          .numPeriodo(i / 2)
          .tipoSeguimiento(TipoSeguimiento.FINAL)
          .fechaInicio(Instant.now().plus(Period.ofDays(i - 1)))
          .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(i))))
          .build();

      entityManager.persistAndFlush(proyectoPeriodoSeguimientoCientifico);

      Set<ProyectoPeriodoSeguimientoDocumentoNombre> nombreDocumento = new HashSet<>();
      nombreDocumento.add(new ProyectoPeriodoSeguimientoDocumentoNombre(Language.ES, "nombre-" + i));

      ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = ProyectoPeriodoSeguimientoDocumento
          .builder()
          .proyectoPeriodoSeguimientoId(proyectoPeriodoSeguimientoCientifico.getId())
          .documentoRef("doc-" + i)
          .nombre(nombreDocumento)
          .build();

      entityManager.persistAndFlush(proyectoPeriodoSeguimientoDocumento);
    }

    // when: se busca ProyectoPeriodoSeguimientoDocumento por ProyectoSeguimientoId
    Boolean dataFound = repository.existsByProyectoPeriodoSeguimientoId(111L);

    // then: Se recupera ProyectoPeriodoSeguimientoDocumento con el
    // ProyectoSeguimientoId ordenados por Fecha Inicio
    Assertions.assertThat(dataFound).isFalse();
  }

  @Test
  void deleteByProyectoSeguimiento_WithExistingId_NoReturnsAnyException() {
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion"));

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre(nombreModeloEjecucion)
        .activo(Boolean.TRUE)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimientoDocumento with same ProyectoSeguimientoId
    Set<ProyectoTitulo> tituloProyecto1 = new HashSet<>();
    tituloProyecto1.add(new ProyectoTitulo(Language.ES, "PRO1"));

    Proyecto proyecto1 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto1)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto1);

    Set<ProyectoTitulo> tituloProyecto2 = new HashSet<>();
    tituloProyecto2.add(new ProyectoTitulo(Language.ES, "PRO2"));

    Proyecto proyecto2 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto2)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto2);

    for (int i = 11; i > 1; i--) {
      ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento
          .builder()
          .proyectoId((i % 2 == 0) ? proyecto2.getId() : proyecto1.getId())
          .numPeriodo(i / 2)
          .tipoSeguimiento(TipoSeguimiento.FINAL)
          .fechaInicio(Instant.now().plus(Period.ofDays(i - 1)))
          .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(i))))
          .build();

      entityManager.persistAndFlush(proyectoPeriodoSeguimientoCientifico);

      Set<ProyectoPeriodoSeguimientoDocumentoNombre> nombreDocumento = new HashSet<>();
      nombreDocumento.add(new ProyectoPeriodoSeguimientoDocumentoNombre(Language.ES, "nombre-" + i));

      ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = ProyectoPeriodoSeguimientoDocumento
          .builder()
          .proyectoPeriodoSeguimientoId(proyectoPeriodoSeguimientoCientifico.getId())
          .documentoRef("doc-" + i)
          .nombre(nombreDocumento)
          .build();

      entityManager.persistAndFlush(proyectoPeriodoSeguimientoDocumento);
    }

    // when: se busca ProyectoPeriodoSeguimientoDocumento por ProyectoSeguimientoId
    repository.deleteByProyectoPeriodoSeguimientoId(1L);
    boolean exists = repository.existsByProyectoPeriodoSeguimientoId(1L);
    Assertions.assertThat(exists).isFalse();
  }

  @Test
  void findAll_WithOnlyVisiblesSpecification_ReturnsVisiblesAndNullDocumentos() {
    // given: un periodo de seguimiento con documentos visible=true, visible=false y
    // visible=null
    ProyectoPeriodoSeguimiento periodo = generarMockProyectoPeriodoSeguimiento();

    ProyectoPeriodoSeguimientoDocumento documentoVisible = generarMockProyectoPeriodoSeguimientoDocumento("-001",
        periodo.getId(), Boolean.TRUE);
    ProyectoPeriodoSeguimientoDocumento documentoNoVisible = generarMockProyectoPeriodoSeguimientoDocumento("-002",
        periodo.getId(), Boolean.FALSE);
    ProyectoPeriodoSeguimientoDocumento documentoSinValor = generarMockProyectoPeriodoSeguimientoDocumento("-003",
        periodo.getId(), null);

    Specification<ProyectoPeriodoSeguimientoDocumento> specs = ProyectoPeriodoSeguimientoDocumentoSpecifications
        .byProyectoPeriodoSeguimientoId(periodo.getId())
        .and(ProyectoPeriodoSeguimientoDocumentoSpecifications.onlyVisibles());

    // when: se buscan los documentos visibles del periodo
    List<ProyectoPeriodoSeguimientoDocumento> result = repository.findAll(specs);

    // then: se devuelven los documentos visibles
    Assertions.assertThat(result)
        .hasSize(1)
        .extracting(ProyectoPeriodoSeguimientoDocumento::getId)
        .containsExactlyInAnyOrder(documentoVisible.getId())
        .doesNotContain(documentoNoVisible.getId(), documentoSinValor.getId());
  }

  /**
   * Función que devuelve un objeto ProyectoPeriodoSeguimiento persistido, junto
   * con su Proyecto
   *
   * @return el objeto ProyectoPeriodoSeguimiento
   */
  private ProyectoPeriodoSeguimiento generarMockProyectoPeriodoSeguimiento() {
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion"));

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre(nombreModeloEjecucion)
        .activo(Boolean.TRUE)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    Set<ProyectoTitulo> tituloProyecto = new HashSet<>();
    tituloProyecto.add(new ProyectoTitulo(Language.ES, "titulo-001"));

    Proyecto proyecto = Proyecto.builder()
        .unidadGestionRef("2")
        .modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3))))
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto);

    ProyectoPeriodoSeguimiento periodo = ProyectoPeriodoSeguimiento.builder()
        .proyectoId(proyecto.getId())
        .numPeriodo(1)
        .tipoSeguimiento(TipoSeguimiento.FINAL)
        .fechaInicio(Instant.now().plus(Period.ofDays(1)))
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(1))))
        .build();

    return entityManager.persistAndFlush(periodo);
  }

  /**
   * Función que devuelve un objeto ProyectoPeriodoSeguimientoDocumento
   *
   * @param suffix                       sufijo para los campos únicos
   * @param proyectoPeriodoSeguimientoId id del {@link ProyectoPeriodoSeguimiento}
   * @param visible                      flag visible del documento (puede ser
   *                                     {@code null})
   * @return el objeto ProyectoPeriodoSeguimientoDocumento
   */
  private ProyectoPeriodoSeguimientoDocumento generarMockProyectoPeriodoSeguimientoDocumento(String suffix,
      Long proyectoPeriodoSeguimientoId, Boolean visible) {
    Set<ProyectoPeriodoSeguimientoDocumentoNombre> nombreDocumento = new HashSet<>();
    nombreDocumento.add(new ProyectoPeriodoSeguimientoDocumentoNombre(Language.ES, "nombre" + suffix));

    ProyectoPeriodoSeguimientoDocumento documento = ProyectoPeriodoSeguimientoDocumento.builder()
        .proyectoPeriodoSeguimientoId(proyectoPeriodoSeguimientoId)
        .nombre(nombreDocumento)
        .documentoRef("documentoRef" + suffix)
        .visible(visible)
        .build();

    return entityManager.persistAndFlush(documento);
  }

}
