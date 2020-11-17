package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVNEnum;
import org.crue.hercules.sgi.csp.enums.TipoDestinatarioEnum;
import org.crue.hercules.sgi.csp.enums.TipoEstadoConvocatoriaEnum;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ConvocatoriaEntidadGestoraRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaEntidadGestoraRepository repository;

  @Test
  public void findByConvocatoriaIdIdAndEntidadRef_ReturnsConvocatoriaEntidadGestora() throws Exception {
    // given: data ConvocatoriaEntidadGestora to find by Convocatoria and
    // EntidadRef
    ConvocatoriaEntidadGestora convocatoriaEntidadGestora1 = generarConvocatoriaEntidadGestora("-001");
    generarConvocatoriaEntidadGestora("-002");

    Long convocatoriaIdBuscado = convocatoriaEntidadGestora1.getConvocatoria().getId();
    String entidadRefBuscado = convocatoriaEntidadGestora1.getEntidadRef();

    // when: find by by Convocatoria and EntidadRef
    ConvocatoriaEntidadGestora dataFound = repository
        .findByConvocatoriaIdAndEntidadRef(convocatoriaIdBuscado, entidadRefBuscado).get();

    // then: ConvocatoriaEntidadGestora is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(convocatoriaEntidadGestora1.getId());
    Assertions.assertThat(dataFound.getConvocatoria().getId())
        .isEqualTo(convocatoriaEntidadGestora1.getConvocatoria().getId());
    Assertions.assertThat(dataFound.getEntidadRef()).isEqualTo(convocatoriaEntidadGestora1.getEntidadRef());
  }

  @Test
  public void findByModeloEjecucionIdAndTipoFinalidadId_ReturnsNull() throws Exception {
    // given: data ConvocatoriaEntidadGestora to find by Convocatoria and EntidadRef
    ConvocatoriaEntidadGestora convocatoriaEntidadGestora1 = generarConvocatoriaEntidadGestora("-001");
    ConvocatoriaEntidadGestora convocatoriaEntidadGestora2 = generarConvocatoriaEntidadGestora("-002");

    Long convocatoriaIdBuscado = convocatoriaEntidadGestora1.getConvocatoria().getId();
    String entidadRefBuscado = convocatoriaEntidadGestora2.getEntidadRef();

    // when: find by by Convocatoria and EntidadRef
    Optional<ConvocatoriaEntidadGestora> dataFound = repository.findByConvocatoriaIdAndEntidadRef(convocatoriaIdBuscado,
        entidadRefBuscado);

    // then: ConvocatoriaEntidadGestora is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }

  /**
   * Función que genera ConvocatoriaEntidadGestora
   * 
   * @param suffix
   * @return el objeto ConvocatoriaEntidadGestora
   */
  private ConvocatoriaEntidadGestora generarConvocatoriaEntidadGestora(String suffix) {

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

    ConvocatoriaEntidadGestora convocatoriaEntidadGestora = ConvocatoriaEntidadGestora.builder()//
        .convocatoria(convocatoria)//
        .entidadRef("entidad" + suffix)//
        .build();
    return entityManager.persistAndFlush(convocatoriaEntidadGestora);
  }
}
