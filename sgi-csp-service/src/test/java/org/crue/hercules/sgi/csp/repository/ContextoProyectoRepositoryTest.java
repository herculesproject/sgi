package org.crue.hercules.sgi.csp.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ContextoProyectoRepositoryTest
 */
@DataJpaTest
public class ContextoProyectoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ContextoProyectoRepository repository;

  @Test
  public void findByProyectoId_ReturnsContextoProyecto() throws Exception {

    // given: 2 ContextoProyecto de los que 1 coincide con el idProyecto buscado
    ContextoProyecto contextoProyecto1 = generarMockContextoProyecto(1L);
    entityManager.persistAndFlush(contextoProyecto1.getProyecto());
    entityManager.persistAndFlush(contextoProyecto1);

    entityManager.persistAndFlush(contextoProyecto1.getProyecto());
    ContextoProyecto contextoProyecto2 = generarMockContextoProyecto(2L);
    entityManager.persistAndFlush(contextoProyecto2.getProyecto());
    entityManager.persistAndFlush(contextoProyecto2);

    Long convocatoriaIdBuscada = contextoProyecto1.getProyecto().getId();

    // when: se busca el ContextoProyecto por el idProyecto
    ContextoProyecto contextoProyectoEncontrado = repository.findByProyectoId(convocatoriaIdBuscada).get();

    // then: Se recupera el ContextoProyecto con el idProyecto buscado
    Assertions.assertThat(contextoProyectoEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(contextoProyectoEncontrado.getIntereses()).as("getIntereses")
        .isEqualTo(contextoProyecto1.getIntereses());
    Assertions.assertThat(contextoProyectoEncontrado.getObjetivos()).as("getObjetivos")
        .isEqualTo(contextoProyecto1.getObjetivos());
  }

  @Test
  public void findByProyectoNoExiste_ReturnsNull() throws Exception {

    // given: 2 ContextoProyecto que no coinciden con el idProyecto buscado
    ContextoProyecto contextoProyecto1 = generarMockContextoProyecto(1L);
    entityManager.persistAndFlush(contextoProyecto1.getProyecto());
    entityManager.persistAndFlush(contextoProyecto1);

    ContextoProyecto contextoProyecto2 = generarMockContextoProyecto(2L);

    Long convocatoriaIdBuscada = contextoProyecto2.getProyecto().getId();

    // when: se busca el ContextoProyecto por idProyecto que no existe
    Optional<ContextoProyecto> contextoProyectoEncontrado = repository.findByProyectoId(convocatoriaIdBuscada);

    // then: Se recupera el ContextoProyecto con el idProyecto buscado
    Assertions.assertThat(contextoProyectoEncontrado).isEqualTo(Optional.empty());
  }

  /**
   * Función que devuelve un objeto ContextoProyecto
   * 
   * @param id identificador
   * @return el objeto ContextoProyecto
   */
  private ContextoProyecto generarMockContextoProyecto(Long id) {

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()//
        .nombre("nombreModeloEjecucion")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    Proyecto proyecto = Proyecto.builder()//
        .unidadGestionRef("OPE").modeloEjecucion(modeloEjecucion)//
        .titulo("PRO" + (id != null ? id : ""))//
        .fechaInicio(LocalDate.now())//
        .fechaFin(LocalDate.now()).activo(Boolean.TRUE)//
        .build();

    ContextoProyecto contextoProyecto = new ContextoProyecto();
    contextoProyecto.setProyecto(proyecto);
    contextoProyecto.setIntereses("intereses");
    contextoProyecto.setObjetivos("objetivos");
    contextoProyecto.setPropiedadResultados(ContextoProyecto.PropiedadResultados.COMPARTIDA);
    return contextoProyecto;
  }

}
