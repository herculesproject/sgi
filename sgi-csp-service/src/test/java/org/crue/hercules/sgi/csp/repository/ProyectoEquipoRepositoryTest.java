package org.crue.hercules.sgi.csp.repository;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProyectoEquipoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoEquipoRepository repository;

  @Test
  public void findAllByProyectoSocioId_ReturnsProyectoSocioEquipo() throws Exception {

    // given: 2 ProyectoEquipo de los que 1 coincide con el
    // idProyecto
    // buscado

    ModeloEjecucion modeloEjecucion1 = entityManager
        .persistAndFlush(new ModeloEjecucion(null, "nombre-1", "descripcion-1", true));

    Proyecto proyecto1 = entityManager.persistAndFlush(Proyecto.builder()//
        .titulo("proyecto 1").acronimo("PR1").fechaInicio(LocalDate.of(2020, 11, 20))
        .fechaFin(LocalDate.of(2021, 11, 20)).unidadGestionRef("OPE").modeloEjecucion(modeloEjecucion1)
        .activo(Boolean.TRUE).build());

    Proyecto proyecto2 = entityManager.persistAndFlush(Proyecto.builder()//
        .titulo("proyecto 2").acronimo("PR2").fechaInicio(LocalDate.of(2020, 11, 20))
        .fechaFin(LocalDate.of(2021, 11, 20)).unidadGestionRef("OPE").modeloEjecucion(modeloEjecucion1)
        .activo(Boolean.TRUE).build());

    RolProyecto rolProyecto = entityManager.persistAndFlush(RolProyecto.builder()//
        .abreviatura("001")//
        .nombre("nombre-001")//
        .descripcion("descripcion-001")//
        .rolPrincipal(Boolean.FALSE)//
        .equipo(RolProyecto.Equipo.INVESTIGACION).activo(Boolean.TRUE)//
        .build());

    ProyectoEquipo proyectoEquipo1 = entityManager.persistAndFlush(
        ProyectoEquipo.builder().proyecto(proyecto1).rolProyecto(rolProyecto).fechaInicio(LocalDate.now())
            .fechaFin(LocalDate.now()).personaRef("001").horasDedicacion(new Double(2)).build());

    entityManager.persistAndFlush(
        ProyectoEquipo.builder().proyecto(proyecto2).rolProyecto(rolProyecto).fechaInicio(LocalDate.now())
            .fechaFin(LocalDate.now()).personaRef("002").horasDedicacion(new Double(44)).build());

    entityManager.persistAndFlush(
        ProyectoEquipo.builder().proyecto(proyecto2).rolProyecto(rolProyecto).fechaInicio(LocalDate.now())
            .fechaFin(LocalDate.now()).personaRef("002").horasDedicacion(new Double(56)).build());

    // when: se buscan los ProyectoEquipo por proyecto id
    List<ProyectoEquipo> ProyectoEquipoEncontrados = repository.findAllByProyectoId(proyecto1.getId());

    // then: Se recupera el ProyectoEquipo con el id socio
    // buscado
    Assertions.assertThat(ProyectoEquipoEncontrados.get(0).getId()).as("getId").isNotNull();
    Assertions.assertThat(ProyectoEquipoEncontrados.get(0).getPersonaRef()).as("getPersonaRef")
        .isEqualTo(proyectoEquipo1.getPersonaRef());

  }

}
