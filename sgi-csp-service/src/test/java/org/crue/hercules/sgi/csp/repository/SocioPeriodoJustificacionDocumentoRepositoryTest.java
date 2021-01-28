package org.crue.hercules.sgi.csp.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.SocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class SocioPeriodoJustificacionDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private SocioPeriodoJustificacionDocumentoRepository repository;

  @Test
  public void findAllByProyectoSocioPeriodoJustificacionId_ReturnsSocioPeriodoJustificacionDocuemnto()
      throws Exception {

    // given: 1 SocioPeriodoJustificacionDocumento para el
    // ProyectoSocioPeriodoJustificacionId buscado
    ModeloEjecucion modeloEjecucion = entityManager
        .persistAndFlush(new ModeloEjecucion(null, "nombre-1", "descripcion-1", true));

    RolSocio rolSocio = entityManager.persistAndFlush(RolSocio.builder()//
        .abreviatura("001")//
        .nombre("nombre-001")//
        .descripcion("descripcion-001")//
        .coordinador(Boolean.FALSE)//
        .activo(Boolean.TRUE)//
        .build());

    Proyecto proyecto = entityManager.persistAndFlush(Proyecto.builder().titulo("proyecto")
        .fechaInicio(LocalDate.of(2020, 9, 18)).fechaFin(LocalDate.of(2022, 10, 11)).unidadGestionRef("OPE")
        .modeloEjecucion(modeloEjecucion).activo(Boolean.TRUE).build());

    ProyectoSocio proyectoSocio1 = entityManager.persistAndFlush(ProyectoSocio.builder()//
        .proyecto(proyecto)//
        .empresaRef("codigo-1")//
        .rolSocio(rolSocio).build());

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion1 = entityManager
        .persistAndFlush(new ProyectoSocioPeriodoJustificacion(null, proyectoSocio1, 1, LocalDate.of(2020, 10, 10),
            LocalDate.of(2020, 11, 20), LocalDateTime.of(2020, 10, 10, 0, 0, 0),
            LocalDateTime.of(2020, 11, 20, 0, 0, 0), "observaciones-1", Boolean.TRUE, LocalDate.of(2020, 11, 20)));
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion2 = entityManager
        .persistAndFlush(new ProyectoSocioPeriodoJustificacion(null, proyectoSocio1, 1, LocalDate.of(2020, 10, 10),
            LocalDate.of(2020, 11, 20), LocalDateTime.of(2020, 10, 10, 0, 0, 0),
            LocalDateTime.of(2020, 11, 20, 0, 0, 0), "observaciones-2", Boolean.TRUE, LocalDate.of(2020, 11, 20)));

    TipoDocumento tipoDocumento = entityManager
        .persistAndFlush(TipoDocumento.builder().nombre("tipo1").activo(Boolean.TRUE).build());

    SocioPeriodoJustificacionDocumento socioPeriodoJustificacionDocumento1 = entityManager
        .persistAndFlush(SocioPeriodoJustificacionDocumento.builder().nombre("doc1").documentoRef("001")
            .proyectoSocioPeriodoJustificacion(proyectoSocioPeriodoJustificacion1).tipoDocumento(tipoDocumento)
            .visible(Boolean.TRUE).build());

    entityManager.persistAndFlush(SocioPeriodoJustificacionDocumento.builder().nombre("doc2").documentoRef("002")
        .proyectoSocioPeriodoJustificacion(proyectoSocioPeriodoJustificacion2).tipoDocumento(tipoDocumento)
        .visible(Boolean.TRUE).build());

    Long proyectoSocioPeriodoJustificacionIdBuscado = proyectoSocioPeriodoJustificacion1.getId();

    // when: se buscan los SocioPeriodoJustificacionDocumento por
    // ProyectoSocioPeriodoJustificacionId
    List<SocioPeriodoJustificacionDocumento> dataFound = repository
        .findAllByProyectoSocioPeriodoJustificacionId(proyectoSocioPeriodoJustificacionIdBuscado);

    // then: Se recuperan los ProyectoSocioPeriodoJustificacion con el
    // ProyectoSocioId
    // buscado
    Assertions.assertThat(dataFound.size()).isEqualTo(1);
    Assertions.assertThat(dataFound.get(0).getId()).isEqualTo(socioPeriodoJustificacionDocumento1.getId());
    Assertions.assertThat(dataFound.get(0).getProyectoSocioPeriodoJustificacion().getId())
        .isEqualTo(socioPeriodoJustificacionDocumento1.getProyectoSocioPeriodoJustificacion().getId());
    Assertions.assertThat(dataFound.get(0).getNombre()).isEqualTo(socioPeriodoJustificacionDocumento1.getNombre());
  }

}
