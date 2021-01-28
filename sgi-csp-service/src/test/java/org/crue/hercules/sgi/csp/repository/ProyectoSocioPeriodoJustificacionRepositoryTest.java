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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProyectoSocioPeriodoJustificacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoSocioPeriodoJustificacionRepository repository;

  @Test
  public void findAllByProyectoSocioId_ReturnsProyectoSocioPeriodoJustificacion() throws Exception {

    // given: 1 ProyectoSocioPeriodoJustificacion para el ProyectoSocioId buscado
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(modeloEjecucion);

    RolSocio rolSocio = RolSocio.builder()//
        .abreviatura("001")//
        .nombre("nombre-001")//
        .descripcion("descripcion-001")//
        .coordinador(Boolean.FALSE)//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(rolSocio);

    Proyecto proyecto = Proyecto.builder().titulo("proyecto").fechaInicio(LocalDate.of(2020, 9, 18))
        .fechaFin(LocalDate.of(2022, 10, 11)).unidadGestionRef("OPE").modeloEjecucion(modeloEjecucion)
        .activo(Boolean.TRUE).build();

    entityManager.persistAndFlush(proyecto);

    ProyectoSocio proyectoSocio1 = ProyectoSocio.builder()//
        .proyecto(proyecto)//
        .empresaRef("codigo-1")//
        .rolSocio(rolSocio).build();
    entityManager.persistAndFlush(proyectoSocio1);

    ProyectoSocio proyectoSocio2 = ProyectoSocio.builder()//
        .proyecto(proyecto)//
        .empresaRef("codigo-1")//
        .rolSocio(rolSocio).build();
    entityManager.persistAndFlush(proyectoSocio2);

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion1 = new ProyectoSocioPeriodoJustificacion(null,
        proyectoSocio1, 1, LocalDate.of(2020, 10, 10), LocalDate.of(2020, 11, 20),
        LocalDateTime.of(2020, 10, 10, 0, 0, 0), LocalDateTime.of(2020, 11, 20, 0, 0, 0), "observaciones-1",
        Boolean.TRUE, LocalDate.of(2020, 11, 20));
    entityManager.persistAndFlush(proyectoSocioPeriodoJustificacion1);
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion2 = new ProyectoSocioPeriodoJustificacion(null,
        proyectoSocio2, 1, LocalDate.of(2020, 10, 10), LocalDate.of(2020, 11, 20),
        LocalDateTime.of(2020, 10, 10, 0, 0, 0), LocalDateTime.of(2020, 11, 20, 0, 0, 0), "observaciones-1",
        Boolean.TRUE, LocalDate.of(2020, 11, 20));
    entityManager.persistAndFlush(proyectoSocioPeriodoJustificacion2);

    Long proyectoSocioIdBuscado = proyectoSocio1.getId();

    // when: se buscan los ProyectoSocioPeriodoJustificacion por ProyectoSocioId
    List<ProyectoSocioPeriodoJustificacion> dataFound = repository.findAllByProyectoSocioId(proyectoSocioIdBuscado);

    // then: Se recuperan los ProyectoSocioPeriodoJustificacion con el
    // ProyectoSocioId
    // buscado
    Assertions.assertThat(dataFound.size()).isEqualTo(1);
    Assertions.assertThat(dataFound.get(0).getId()).isEqualTo(proyectoSocioPeriodoJustificacion1.getId());
    Assertions.assertThat(dataFound.get(0).getProyectoSocio().getId())
        .isEqualTo(proyectoSocioPeriodoJustificacion1.getProyectoSocio().getId());
    Assertions.assertThat(dataFound.get(0).getObservaciones())
        .isEqualTo(proyectoSocioPeriodoJustificacion1.getObservaciones());
  }

}
