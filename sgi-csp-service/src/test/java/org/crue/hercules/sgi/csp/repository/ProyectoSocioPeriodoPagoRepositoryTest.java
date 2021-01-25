package org.crue.hercules.sgi.csp.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * ProyectoSocioPeriodoPagoRepositoryTest
 */
@DataJpaTest
public class ProyectoSocioPeriodoPagoRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ProyectoSocioPeriodoPagoRepository repository;

  @Test
  public void findAllByProyectoSocioId_ReturnsProyectoSocioPeriodoPago() throws Exception {

    // given: 2 ProyectoSocioPeriodoPago de los que 1 coincide con el
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

    ProyectoSocio proyectoSocio1 = entityManager.persistAndFlush(ProyectoSocio.builder()//
        .proyecto(proyecto1).empresaRef("empresa-0041").rolSocio(rolSocio).build());

    ProyectoSocio proyectoSocio2 = entityManager.persistAndFlush(ProyectoSocio.builder()//
        .proyecto(proyecto1).empresaRef("empresa-0025").rolSocio(rolSocio).build());

    ProyectoSocioPeriodoPago proyectoSocioPeriodoPago1 = entityManager.persistAndFlush(
        new ProyectoSocioPeriodoPago(null, proyectoSocio1, 1, new BigDecimal(3500), LocalDate.of(2021, 4, 10), null));

    entityManager.persistAndFlush(
        new ProyectoSocioPeriodoPago(null, proyectoSocio2, 1, new BigDecimal(2750), LocalDate.of(2021, 1, 10), null));

    entityManager.persistAndFlush(
        new ProyectoSocioPeriodoPago(null, proyectoSocio2, 1, new BigDecimal(1500), LocalDate.of(2021, 2, 10), null));

    Long proyectoSocioId = proyectoSocio1.getId();

    // when: se buscan los ProyectoSocioPeriodoPago por proyecto socio id
    List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagoEncontrados = repository
        .findAllByProyectoSocioId(proyectoSocioId);

    // then: Se recupera el ProyectoSocioPeriodoPago con el id proyecto socio
    // buscado
    Assertions.assertThat(proyectoSocioPeriodoPagoEncontrados.get(0).getId()).as("getId").isNotNull();
    Assertions.assertThat(proyectoSocioPeriodoPagoEncontrados.get(0).getImporte()).as("getImporte")
        .isEqualTo(proyectoSocioPeriodoPago1.getImporte());

  }

}