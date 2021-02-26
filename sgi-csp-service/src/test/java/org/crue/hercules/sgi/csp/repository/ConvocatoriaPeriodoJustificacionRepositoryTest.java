package org.crue.hercules.sgi.csp.repository;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ConvocatoriaPeriodoJustificacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaPeriodoJustificacionRepository repository;

  @Test
  public void findAllByConvocatoriaId_ReturnsConvocatoriaPeriodoJustificacion() throws Exception {

    // given: 2 ConvocatoriaPeriodoJustificacion para el ConvocatoriaId buscado
    Convocatoria convocatoria1 = Convocatoria.builder()//
        .estado(Convocatoria.Estado.BORRADOR)//
        .codigo("codigo-1")//
        .unidadGestionRef("OPE")//
        .anio(2020)//
        .titulo("titulo")//
        .activo(Boolean.TRUE)//
        .build();
    ;
    entityManager.persistAndFlush(convocatoria1);
    Convocatoria convocatoria2 = Convocatoria.builder()//
        .estado(Convocatoria.Estado.BORRADOR)//
        .codigo("codigo-2")//
        .unidadGestionRef("OPE")//
        .anio(2020)//
        .titulo("titulo")//
        .activo(Boolean.TRUE)//
        .build();
    ;
    entityManager.persistAndFlush(convocatoria2);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion1 = new ConvocatoriaPeriodoJustificacion(null,
        convocatoria1, 1, 1, 2, LocalDate.of(2020, 10, 10), LocalDate.of(2020, 11, 20), "observaciones-1",
        ConvocatoriaPeriodoJustificacion.Tipo.FINAL);
    entityManager.persistAndFlush(convocatoriaPeriodoJustificacion1);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion2 = new ConvocatoriaPeriodoJustificacion(null,
        convocatoria1, 2, 3, 5, LocalDate.of(2020, 12, 10), LocalDate.of(2021, 11, 20), "observaciones-2",
        ConvocatoriaPeriodoJustificacion.Tipo.PERIODICO);
    entityManager.persistAndFlush(convocatoriaPeriodoJustificacion2);

    Long convocatoriaIdBuscado = convocatoria1.getId();

    // when: se buscan los ConvocatoriaPeriodoJustificacion por ConvocatoriaId
    List<ConvocatoriaPeriodoJustificacion> dataFound = repository.findAllByConvocatoriaId(convocatoriaIdBuscado);

    // then: Se recuperan los ConvocatoriaPeriodoJustificacion con el ConvocatoriaId
    // buscado
    Assertions.assertThat(dataFound.size()).isEqualTo(2);
    Assertions.assertThat(dataFound.get(0).getId()).isEqualTo(convocatoriaPeriodoJustificacion1.getId());
    Assertions.assertThat(dataFound.get(0).getConvocatoria().getId())
        .isEqualTo(convocatoriaPeriodoJustificacion1.getConvocatoria().getId());
    Assertions.assertThat(dataFound.get(0).getObservaciones())
        .isEqualTo(convocatoriaPeriodoJustificacion1.getObservaciones());
  }

  @Test
  public void findFirstByConvocatoriaIdOrderByNumPeriodoDesc_ReturnsConvocatoriaPeriodoJustificacion()
      throws Exception {

    // given: 2 ConvocatoriaPeriodoJustificacion de una Convocatoria
    Convocatoria convocatoria1 = Convocatoria.builder()//
        .estado(Convocatoria.Estado.BORRADOR)//
        .codigo("codigo-1")//
        .unidadGestionRef("OPE")//
        .anio(2020)//
        .titulo("titulo")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(convocatoria1);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion1 = new ConvocatoriaPeriodoJustificacion(null,
        convocatoria1, 2, 7, 9, LocalDate.of(2020, 12, 10), LocalDate.of(2021, 11, 20), "observaciones-1",
        ConvocatoriaPeriodoJustificacion.Tipo.PERIODICO);
    entityManager.persistAndFlush(convocatoriaPeriodoJustificacion1);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion2 = new ConvocatoriaPeriodoJustificacion(null,
        convocatoria1, 1, 3, 5, LocalDate.of(2020, 10, 10), LocalDate.of(2020, 11, 20), "observaciones-2",
        ConvocatoriaPeriodoJustificacion.Tipo.PERIODICO);
    entityManager.persistAndFlush(convocatoriaPeriodoJustificacion2);

    Long convocatoriaIdBuscado = convocatoria1.getId();

    // when: se busca el ConvocatoriaPeriodoJustificacion con el ultimo numero
    // periodo
    ConvocatoriaPeriodoJustificacion dataFound = repository
        .findFirstByConvocatoriaIdOrderByNumPeriodoDesc(convocatoriaIdBuscado).get();

    // then: Se recupera el ConvocatoriaPeriodoJustificacion con el ultimo numero
    // periodo
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).as("getId()").isEqualTo(convocatoriaPeriodoJustificacion1.getId());
    Assertions.assertThat(dataFound.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaPeriodoJustificacion1.getConvocatoria().getId());
    Assertions.assertThat(dataFound.getMesFinal()).as("getObservaciones()")
        .isEqualTo(convocatoriaPeriodoJustificacion1.getMesFinal());
    Assertions.assertThat(dataFound.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoriaPeriodoJustificacion1.getObservaciones());
  }

}
