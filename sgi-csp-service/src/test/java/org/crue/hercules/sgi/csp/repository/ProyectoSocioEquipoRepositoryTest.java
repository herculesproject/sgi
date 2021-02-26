package org.crue.hercules.sgi.csp.repository;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProyectoSocioEquipoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoSocioEquipoRepository repository;

  @Test
  public void findAllByProyectoSocioId_ReturnsProyectoSocioEquipo() throws Exception {

    // given: 2 ProyectoSocioEquipo de los que 1 coincide con el
    // idProyectoSocio
    // buscado

    ModeloEjecucion modeloEjecucion1 = entityManager
        .persistAndFlush(new ModeloEjecucion(null, "nombre-1", "descripcion-1", true));

    Proyecto proyecto1 = entityManager.persistAndFlush(Proyecto.builder()//
        .titulo("proyecto 1").acronimo("PR1").fechaInicio(LocalDate.of(2020, 11, 20))
        .fechaFin(LocalDate.of(2021, 11, 20)).unidadGestionRef("OPE").modeloEjecucion(modeloEjecucion1)
        .activo(Boolean.TRUE).build());

    RolSocio rolSocio = entityManager.persistAndFlush(RolSocio.builder()//
        .abreviatura("001")//
        .nombre("nombre-001")//
        .descripcion("descripcion-001")//
        .coordinador(Boolean.FALSE)//
        .activo(Boolean.TRUE)//
        .build());

    RolProyecto rolProyecto = entityManager.persistAndFlush(RolProyecto.builder()//
        .abreviatura("001")//
        .nombre("nombre-001")//
        .descripcion("descripcion-001")//
        .rolPrincipal(Boolean.FALSE)//
        .equipo(RolProyecto.Equipo.INVESTIGACION).activo(Boolean.TRUE)//
        .build());

    ProyectoSocio proyectoSocio1 = entityManager.persistAndFlush(ProyectoSocio.builder()//
        .proyecto(proyecto1).empresaRef("empresa-0041").rolSocio(rolSocio).build());

    ProyectoSocio proyectoSocio2 = entityManager.persistAndFlush(ProyectoSocio.builder()//
        .proyecto(proyecto1).empresaRef("empresa-0025").rolSocio(rolSocio).build());

    ProyectoSocioEquipo proyectoSocioEquipo1 = entityManager.persistAndFlush(
        new ProyectoSocioEquipo(null, proyectoSocio1, rolProyecto, "001", LocalDate.of(2021, 4, 10), null));

    entityManager.persistAndFlush(
        new ProyectoSocioEquipo(null, proyectoSocio2, rolProyecto, "003", LocalDate.of(2021, 4, 10), null));
    entityManager.persistAndFlush(
        new ProyectoSocioEquipo(null, proyectoSocio2, rolProyecto, "004", LocalDate.of(2021, 4, 10), null));

    Long proyectoSocioId = proyectoSocio1.getId();

    // when: se buscan los ProyectoSocioEquipo por proyecto socio id
    List<ProyectoSocioEquipo> proyectoSocioEquipoEncontrados = repository.findAllByProyectoSocioId(proyectoSocioId);

    // then: Se recupera el ProyectoSocioEquipo con el id proyecto socio
    // buscado
    Assertions.assertThat(proyectoSocioEquipoEncontrados.get(0).getId()).as("getId").isNotNull();
    Assertions.assertThat(proyectoSocioEquipoEncontrados.get(0).getPersonaRef()).as("getPersonaRef")
        .isEqualTo(proyectoSocioEquipo1.getPersonaRef());

  }

}
