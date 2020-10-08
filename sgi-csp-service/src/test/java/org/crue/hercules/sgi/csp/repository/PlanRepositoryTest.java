package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Plan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * PlanRepositoryTest
 */
@DataJpaTest
public class PlanRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PlanRepository repository;

  @Test
  public void findByNombre_ReturnsPlan() throws Exception {

    // given: 2 Plan de los que 1 coincide con el nombre buscado
    Plan plan1 = new Plan(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(plan1);

    Plan plan2 = new Plan(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(plan2);

    String nombreBuscado = "nombre-1";

    // when: se busca el Plan nombre
    Plan planEncontrado = repository.findByNombre(nombreBuscado).get();

    // then: Se recupera el Plan con el nombre buscado
    Assertions.assertThat(planEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(planEncontrado.getNombre()).as("getNombre").isEqualTo(plan1.getNombre());
    Assertions.assertThat(planEncontrado.getDescripcion()).as("getDescripcion").isEqualTo(plan1.getDescripcion());
    Assertions.assertThat(planEncontrado.getActivo()).as("getActivo").isEqualTo(plan1.getActivo());
  }

  @Test
  public void findByNombreNoExiste_ReturnsNull() throws Exception {

    // given: 2 Plan que no coinciden con el nombre buscado
    Plan plan1 = new Plan(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(plan1);

    Plan plan2 = new Plan(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(plan2);

    String nombreBuscado = "nombre-noexiste";

    // when: se busca el Plan por nombre
    Optional<Plan> planEncontrado = repository.findByNombre(nombreBuscado);

    // then: No hay ningun Plan con el nombre buscado
    Assertions.assertThat(planEncontrado).isEqualTo(Optional.empty());
  }

}