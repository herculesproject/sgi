package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class ConfiguracionSolicitudRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ConfiguracionSolicitudRepository repository;

  @Test
  public void findByConvocatoriaId_ReturnsConfiguracionSolicitud() throws Exception {
    // given: data ConfiguracionSolicitud to find by Convocatoria
    Convocatoria convocatoria1 = entityManager.persistAndFlush(Convocatoria.builder()//
        .estado(Convocatoria.Estado.BORRADOR)//
        .codigo("codigo-1")//
        .unidadGestionRef("OPE")//
        .anio(2020)//
        .titulo("titulo")//
        .activo(Boolean.TRUE)//
        .build());
    ConfiguracionSolicitud configuracionSolicitud1 = entityManager
        .persistAndFlush(ConfiguracionSolicitud.builder().convocatoria(convocatoria1).build());

    Convocatoria convocatoria2 = entityManager.persistAndFlush(Convocatoria.builder()//
        .estado(Convocatoria.Estado.BORRADOR)//
        .codigo("codigo-2")//
        .unidadGestionRef("OPE")//
        .anio(2020)//
        .titulo("titulo")//
        .activo(Boolean.TRUE)//
        .build());
    entityManager.persistAndFlush(ConfiguracionSolicitud.builder().convocatoria(convocatoria2).build());

    Long convocatoriaIdBuscado = configuracionSolicitud1.getConvocatoria().getId();

    // when: find by by Convocatoria
    ConfiguracionSolicitud dataFound = repository.findByConvocatoriaId(convocatoriaIdBuscado).get();

    // then: ConfiguracionSolicitud is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(configuracionSolicitud1.getId());
    Assertions.assertThat(dataFound.getConvocatoria().getId())
        .isEqualTo(configuracionSolicitud1.getConvocatoria().getId());
  }

  @Test
  public void findByModeloEjecucionId_ReturnsNull() throws Exception {
    // given: data ConfiguracionSolicitud to find by Convocatoria
    Convocatoria convocatoria1 = entityManager.persistAndFlush(Convocatoria.builder()//
        .estado(Convocatoria.Estado.BORRADOR)//
        .codigo("codigo-1")//
        .unidadGestionRef("OPE")//
        .anio(2020)//
        .titulo("titulo")//
        .activo(Boolean.TRUE)//
        .build());
    entityManager.persistAndFlush(ConfiguracionSolicitud.builder().convocatoria(convocatoria1).build());

    Convocatoria convocatoria2 = entityManager.persistAndFlush(Convocatoria.builder()//
        .estado(Convocatoria.Estado.BORRADOR)//
        .codigo("codigo-2")//
        .unidadGestionRef("OPE")//
        .anio(2020)//
        .titulo("titulo")//
        .activo(Boolean.TRUE)//
        .build());
    ConfiguracionSolicitud configuracionSolicitud2 = ConfiguracionSolicitud.builder().convocatoria(convocatoria2)
        .build();

    Long convocatoriaIdBuscado = configuracionSolicitud2.getConvocatoria().getId();

    // when: find by by Convocatoria
    Optional<ConfiguracionSolicitud> dataFound = repository.findByConvocatoriaId(convocatoriaIdBuscado);

    // then: ConfiguracionSolicitud is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }
}
