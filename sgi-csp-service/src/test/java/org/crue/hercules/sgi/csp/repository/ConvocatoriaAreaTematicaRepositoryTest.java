package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVNEnum;
import org.crue.hercules.sgi.csp.enums.TipoDestinatarioEnum;
import org.crue.hercules.sgi.csp.enums.TipoEstadoConvocatoriaEnum;
import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class ConvocatoriaAreaTematicaRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ConvocatoriaAreaTematicaRepository repository;

  @Test
  public void findByConvocatoriaIdAndAreaTematicaArbolId_ReturnsConvocatoriaAreaTematica() throws Exception {

    // given: data ConvocatoriaAreaTematica to find by Convocatoria and
    // AreaTematicaArbolId
    ConvocatoriaAreaTematica convocatoriaAreaTematica1 = generarConvocatoriaAreaTematica("-001");
    generarConvocatoriaAreaTematica("-002");

    Long convocatoriaIdBuscado = convocatoriaAreaTematica1.getConvocatoria().getId();
    Long areaTematicaArbolIdBuscado = convocatoriaAreaTematica1.getAreaTematicaArbol().getId();

    // when: find by Convocatoria and AreaTematicaArbolId
    ConvocatoriaAreaTematica dataFound = repository
        .findByConvocatoriaIdAndAreaTematicaArbolId(convocatoriaIdBuscado, areaTematicaArbolIdBuscado).get();

    // then: ConvocatoriaAreaTematica is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(convocatoriaAreaTematica1.getId());
    Assertions.assertThat(dataFound.getConvocatoria().getId())
        .isEqualTo(convocatoriaAreaTematica1.getConvocatoria().getId());
    Assertions.assertThat(dataFound.getAreaTematicaArbol().getId())
        .isEqualTo(convocatoriaAreaTematica1.getAreaTematicaArbol().getId());
  }

  @Test
  public void findByConvocatoriaIdAndAreaTematicaArbolId_ReturnsNull() throws Exception {
    // given: data ConvocatoriaAreaTematica to find by Convocatoria and
    // AreaTematicaArbolId

    ConvocatoriaAreaTematica convocatoriaAreaTematica1 = generarConvocatoriaAreaTematica("-001");
    ConvocatoriaAreaTematica convocatoriaAreaTematica2 = generarConvocatoriaAreaTematica("-002");

    Long convocatoriaIdBuscado = convocatoriaAreaTematica1.getConvocatoria().getId();
    Long areaTematicaArbolIdBuscado = convocatoriaAreaTematica2.getAreaTematicaArbol().getId();

    // when: find by by Convocatoria and AreaTematicaArbolId
    Optional<ConvocatoriaAreaTematica> dataFound = repository
        .findByConvocatoriaIdAndAreaTematicaArbolId(convocatoriaIdBuscado, areaTematicaArbolIdBuscado);

    // then: ConvocatoriaAreaTematica is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }

  /**
   * Función que genera ConvocatoriaEntidadGestora
   * 
   * @param suffix
   * @return el objeto ConvocatoriaEntidadGestora
   */
  private ConvocatoriaAreaTematica generarConvocatoriaAreaTematica(String suffix) {

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

    ModeloTipoFinalidad modeloTipoFinalidad = ModeloTipoFinalidad.builder()//
        .modeloEjecucion(modeloEjecucion)//
        .tipoFinalidad(tipoFinalidad)//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(modeloTipoFinalidad);

    TipoRegimenConcurrencia tipoRegimenConcurrencia = TipoRegimenConcurrencia.builder()//
        .nombre("nombreTipoRegimenConcurrencia" + suffix)//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(tipoRegimenConcurrencia);

    TipoAmbitoGeografico tipoAmbitoGeografico = TipoAmbitoGeografico.builder()//
        .nombre("nombreTipoAmbitoGeografico" + suffix)//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(tipoAmbitoGeografico);

    Convocatoria convocatoria = Convocatoria.builder()//
        .unidadGestionRef("unidad" + suffix)//
        .modeloEjecucion(modeloEjecucion)//
        .codigo("codigo" + suffix)//
        .anio(2020)//
        .titulo("titulo" + suffix)//
        .objeto("objeto" + suffix)//
        .observaciones("observaciones" + suffix)//
        .finalidad(modeloTipoFinalidad.getTipoFinalidad())//
        .regimenConcurrencia(tipoRegimenConcurrencia)//
        .destinatarios(TipoDestinatarioEnum.INDIVIDUAL)//
        .colaborativos(Boolean.TRUE)//
        .estadoActual(TipoEstadoConvocatoriaEnum.REGISTRADA)//
        .duracion(12)//
        .ambitoGeografico(tipoAmbitoGeografico)//
        .clasificacionCVN(ClasificacionCVNEnum.AYUDAS).activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(convocatoria);

    ListadoAreaTematica listadoAreaTematica = ListadoAreaTematica.builder()//
        .nombre("nombreListadoAreaTematica" + suffix)//
        .descripcion("descripcionListadoAreaTematica" + suffix)//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(listadoAreaTematica);

    AreaTematicaArbol areaTematicaArbol = AreaTematicaArbol.builder()//
        .abreviatura(suffix)//
        .nombre("nombreAreaTematicaArbol" + suffix).activo(Boolean.TRUE)//
        .padre(null)//
        .listadoAreaTematica(listadoAreaTematica)//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(areaTematicaArbol);

    ConvocatoriaAreaTematica convocatoriaAreaTematica = ConvocatoriaAreaTematica.builder()//
        .convocatoria(convocatoria)//
        .areaTematicaArbol(areaTematicaArbol)//
        .observaciones("observaciones" + suffix)//
        .build();
    return entityManager.persistAndFlush(convocatoriaAreaTematica);
  }
}
