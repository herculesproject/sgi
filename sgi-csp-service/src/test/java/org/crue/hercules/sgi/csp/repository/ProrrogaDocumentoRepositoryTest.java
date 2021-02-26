package org.crue.hercules.sgi.csp.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProrrogaDocumentoRepositoryTest
 */
@DataJpaTest
public class ProrrogaDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProrrogaDocumentoRepository repository;

  @Test
  public void deleteByProyectoProrrogaId_ReturnsListProrrogaDocumento() throws Exception {

    // given: registros ProyectoProrroga con documentos asociados
    Proyecto proyecto = generarMockProyecto("-001");
    TipoDocumento tipoDocumento1 = generarMockTipoDocumento("-001");
    TipoDocumento tipoDocumento2 = generarMockTipoDocumento("-002");
    ProyectoProrroga proyectoProrroga1 = generarMockProyectoProrroga("-001", proyecto, LocalDate.of(2020, 01, 01));
    ProrrogaDocumento prorrogaDocumento1 = generarMockProrrogaDocumento("-001", proyectoProrroga1, tipoDocumento1);
    ProrrogaDocumento prorrogaDocumento2 = generarMockProrrogaDocumento("-002", proyectoProrroga1, tipoDocumento2);
    ProyectoProrroga proyectoProrroga2 = generarMockProyectoProrroga("-002", proyecto, LocalDate.of(2020, 02, 01));
    ProrrogaDocumento prorrogaDocumento3 = generarMockProrrogaDocumento("-003", proyectoProrroga2, tipoDocumento1);
    ProrrogaDocumento prorrogaDocumento4 = generarMockProrrogaDocumento("-004", proyectoProrroga2, tipoDocumento2);

    Long idProyectoProrroga = proyectoProrroga2.getId();

    // when: Se eliminan los proyectos de la prorroga2
    List<ProrrogaDocumento> result = repository.deleteByProyectoProrrogaId(idProyectoProrroga);

    // then: retorna la lista de documentos eliminados
    Assertions.assertThat(result.size()).isEqualTo(2);
    Assertions.assertThat(result.contains(prorrogaDocumento1)).isFalse();
    Assertions.assertThat(result.contains(prorrogaDocumento2)).isFalse();
    Assertions.assertThat(result.contains(prorrogaDocumento3)).isTrue();
    Assertions.assertThat(result.contains(prorrogaDocumento4)).isTrue();

  }

  @Test
  public void deleteByProyectoProrrogaId_ReturnsEmptyList() throws Exception {

    Proyecto proyecto = generarMockProyecto("-001");
    TipoDocumento tipoDocumento1 = generarMockTipoDocumento("-001");
    TipoDocumento tipoDocumento2 = generarMockTipoDocumento("-002");
    ProyectoProrroga proyectoProrroga1 = generarMockProyectoProrroga("-001", proyecto, LocalDate.of(2020, 01, 01));
    generarMockProrrogaDocumento("-001", proyectoProrroga1, tipoDocumento1);
    generarMockProrrogaDocumento("-002", proyectoProrroga1, tipoDocumento2);
    ProyectoProrroga proyectoProrroga2 = generarMockProyectoProrroga("-002", proyecto, LocalDate.of(2020, 02, 01));

    Long idProyectoProrroga = proyectoProrroga2.getId();

    // when: Se eliminan los proyectos de la prorroga2
    List<ProrrogaDocumento> result = repository.deleteByProyectoProrrogaId(idProyectoProrroga);

    // then: retorna la lista de documentos eliminados
    Assertions.assertThat(result.isEmpty()).isTrue();
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
        .titulo("titulo" + suffix)//
        .unidadGestionRef("OPE")//
        .modeloEjecucion(modeloEjecucion)//
        .finalidad(tipoFinalidad)//
        .ambitoGeografico(tipoAmbitoGeografico)//
        .fechaInicio(LocalDate.of(2020, 01, 01))//
        .fechaFin(LocalDate.of(2020, 12, 31))//
        .paquetesTrabajo(Boolean.TRUE)//
        .activo(Boolean.TRUE)//
        .build();

    return entityManager.persistAndFlush(proyecto);

  }

  /**
   * Función que genera ProyectoProrroga
   * 
   * @param suffix
   * @param fechaConcesion fecha concesión
   * @return el objeto ProyectoProrroga
   */
  private ProyectoProrroga generarMockProyectoProrroga(String suffix, Proyecto proyecto, LocalDate fechaConcesion) {

    ProyectoProrroga proyectoProrroga = ProyectoProrroga.builder()//
        .proyecto(proyecto)//
        .numProrroga(1)//
        .fechaConcesion(fechaConcesion)//
        .tipo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE)//
        .fechaFin(LocalDate.of(2020, 12, 31))//
        .importe(BigDecimal.valueOf(123.45))//
        .observaciones("observaciones-proyecto-prorroga" + suffix)//
        .build();
    return entityManager.persistAndFlush(proyectoProrroga);
  }

  /**
   * Función que devuelve un objeto ProrrogaDocumento
   * 
   * @param suffix
   * @param proyectoProrroga ProyectoProrroga
   * @param tipoDocumento    TipoDocumento
   * @return el objeto ProrrogaDocumento
   */
  private ProrrogaDocumento generarMockProrrogaDocumento(String suffix, ProyectoProrroga proyectoProrroga,
      TipoDocumento tipoDocumento) {

    ProrrogaDocumento prorrogaDocumento = ProrrogaDocumento.builder()//
        .proyectoProrroga(proyectoProrroga)//
        .nombre("prorroga-documento" + suffix)//
        .documentoRef("documentoRef" + suffix)//
        .tipoDocumento(tipoDocumento)//
        .comentario("comentario-prorroga-documento" + suffix)//
        .visible(Boolean.TRUE)//
        .build();

    return entityManager.persistAndFlush(prorrogaDocumento);
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param suffix
   * @return el objeto ModeloTipoFase
   */
  private TipoDocumento generarMockTipoDocumento(String suffix) {

    TipoDocumento tipoDocumento = TipoDocumento.builder()//
        .nombre("tipo-documento-" + suffix)//
        .descripcion("descripcion-tipo-documento-" + suffix)//
        .activo(Boolean.TRUE)//
        .build();

    return entityManager.persistAndFlush(tipoDocumento);
  }
}
