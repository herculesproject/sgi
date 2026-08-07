package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoDocumentoNombre;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoDocumentoSpecifications;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProyectoDocumentoRepositoryTest
 */
@DataJpaTest
class ProyectoDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoDocumentoRepository repository;

  @Test
  void findAll_WithOnlyVisiblesSpecification_ReturnsOnlyVisibleProyectoDocumento() {
    // given: un Proyecto con dos ProyectoDocumento visibles y uno no visible
    Proyecto proyecto = generarMockProyecto();

    ProyectoDocumento documentoVisible1 = generarMockProyectoDocumento("-001", proyecto.getId(), Boolean.TRUE);
    ProyectoDocumento documentoVisible2 = generarMockProyectoDocumento("-002", proyecto.getId(), Boolean.TRUE);
    ProyectoDocumento documentoNoVisible = generarMockProyectoDocumento("-003", proyecto.getId(), Boolean.FALSE);

    Specification<ProyectoDocumento> specs = ProyectoDocumentoSpecifications.byProyectoId(proyecto.getId())
        .and(ProyectoDocumentoSpecifications.onlyVisibles());

    // when: se buscan los ProyectoDocumento visibles del Proyecto
    List<ProyectoDocumento> result = repository.findAll(specs);

    // then: solo se devuelven los documentos visibles
    Assertions.assertThat(result)
        .hasSize(2)
        .extracting(ProyectoDocumento::getId)
        .containsExactlyInAnyOrder(documentoVisible1.getId(), documentoVisible2.getId())
        .doesNotContain(documentoNoVisible.getId());
  }

  @Test
  void findAll_WithoutOnlyVisiblesSpecification_ReturnsAllProyectoDocumento() {
    // given: un Proyecto con un ProyectoDocumento visible y otro no visible
    Proyecto proyecto = generarMockProyecto();

    ProyectoDocumento documentoVisible = generarMockProyectoDocumento("-001", proyecto.getId(), Boolean.TRUE);
    ProyectoDocumento documentoNoVisible = generarMockProyectoDocumento("-002", proyecto.getId(), Boolean.FALSE);

    Specification<ProyectoDocumento> specs = ProyectoDocumentoSpecifications.byProyectoId(proyecto.getId());

    // when: se buscan todos los ProyectoDocumento del Proyecto, sin filtrar por
    // visible
    List<ProyectoDocumento> result = repository.findAll(specs);

    // then: se devuelven todos los documentos, visibles y no visibles
    Assertions.assertThat(result)
        .hasSize(2)
        .extracting(ProyectoDocumento::getId)
        .containsExactlyInAnyOrder(documentoVisible.getId(), documentoNoVisible.getId());
  }

  /**
   * Función que devuelve un objeto Proyecto
   *
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto() {
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

    return entityManager.persistAndFlush(proyecto);
  }

  /**
   * Función que devuelve un objeto ProyectoDocumento
   *
   * @param suffix     sufijo para los campos únicos
   * @param proyectoId id del {@link Proyecto}
   * @param visible    flag visible del documento
   * @return el objeto ProyectoDocumento
   */
  private ProyectoDocumento generarMockProyectoDocumento(String suffix, Long proyectoId, Boolean visible) {
    Set<ProyectoDocumentoNombre> nombreProyectoDocumento = new HashSet<>();
    nombreProyectoDocumento.add(new ProyectoDocumentoNombre(Language.ES, "proyecto-documento" + suffix));

    ProyectoDocumento proyectoDocumento = ProyectoDocumento.builder()
        .proyectoId(proyectoId)
        .nombre(nombreProyectoDocumento)
        .documentoRef("documentoRef" + suffix)
        .visible(visible)
        .build();

    return entityManager.persistAndFlush(proyectoDocumento);
  }

}
