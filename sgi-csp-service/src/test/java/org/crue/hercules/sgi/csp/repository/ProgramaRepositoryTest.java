package org.crue.hercules.sgi.csp.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.model.Programa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * ProgramaRepositoryTest
 */
@DataJpaTest
public class ProgramaRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ProgramaRepository repository;

  @Test
  public void findByNombre_ReturnsPrograma() throws Exception {

    // given: 2 Programa de los que 1 coincide con el nombre buscado
    Plan plan = new Plan(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(plan);

    Programa programa1 = new Programa(null, "nombre-1", "descripcion-1", plan, null, true);
    entityManager.persistAndFlush(programa1);

    Programa programa2 = new Programa(null, "nombre-2", "descripcion-2", plan, programa1, true);
    entityManager.persistAndFlush(programa2);

    String nombreBuscado = "nombre-1";

    // when: se busca el Programa nombre
    Programa programaEncontrado = repository.findByNombre(nombreBuscado).get();

    // then: Se recupera el Programa con el nombre buscado
    Assertions.assertThat(programaEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(programaEncontrado.getNombre()).as("getNombre").isEqualTo(programa1.getNombre());
    Assertions.assertThat(programaEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(programa1.getDescripcion());
    Assertions.assertThat(programaEncontrado.getActivo()).as("getActivo").isEqualTo(programa1.getActivo());
  }

  @Test
  public void findByNombreNoExiste_ReturnsNull() throws Exception {

    // given: 2 Programa que no coinciden con el nombre buscado
    Plan plan = new Plan(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(plan);

    Programa programa1 = new Programa(null, "nombre-1", "descripcion-1", plan, null, true);
    entityManager.persistAndFlush(programa1);

    Programa programa2 = new Programa(null, "nombre-2", "descripcion-2", plan, programa1, true);
    entityManager.persistAndFlush(programa2);

    String nombreBuscado = "nombre-noexiste";

    // when: se busca el Programa por nombre
    Optional<Programa> programaEncontrado = repository.findByNombre(nombreBuscado);

    // then: No hay ningun Programa con el nombre buscado
    Assertions.assertThat(programaEncontrado).isEqualTo(Optional.empty());
  }

  @Test
  public void findByPadreIdIn_ReturnsPrograma() throws Exception {
    // given: 2 Programa de los que 1 coincide con el id padre buscado
    Plan plan = new Plan(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(plan);

    Programa programa1 = new Programa(null, "nombre-1", "descripcion-1", plan, null, true);
    entityManager.persistAndFlush(programa1);

    Programa programa2 = new Programa(null, "nombre-2", "descripcion-2", plan, programa1, true);
    entityManager.persistAndFlush(programa2);

    List<Long> idsPadreBuscados = Arrays.asList(programa1.getId());

    // when: se busca el Programa el id padre
    List<Programa> programaEncontrados = repository.findByPadreIdIn(idsPadreBuscados);

    // then: Se recupera el Programa con el id padre buscado
    Assertions.assertThat(programaEncontrados.size()).as("size()").isEqualTo(1);
    Programa programaEncontrado = programaEncontrados.get(0);
    Assertions.assertThat(programaEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(programaEncontrado.getNombre()).as("getNombre").isEqualTo(programa2.getNombre());
    Assertions.assertThat(programaEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(programa2.getDescripcion());
    Assertions.assertThat(programaEncontrado.getActivo()).as("getActivo").isEqualTo(programa2.getActivo());
  }

  @Test
  public void findByPadreIdIn_IdNoExiste_ReturnsEmptyList() throws Exception {
    // given: 2 Programa que no coinciden con el id padre buscado
    Plan plan = new Plan(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(plan);

    Programa programa1 = new Programa(null, "nombre-1", "descripcion-1", plan, null, true);
    entityManager.persistAndFlush(programa1);

    Programa programa2 = new Programa(null, "nombre-2", "descripcion-2", plan, programa1, true);
    entityManager.persistAndFlush(programa2);

    List<Long> idsPadreBuscados = Arrays.asList(programa2.getId());

    // when: se busca el Programa el id padre
    List<Programa> programaEncontrados = repository.findByPadreIdIn(idsPadreBuscados);

    // then: No hay ningun Programa con el id padre buscado
    Assertions.assertThat(programaEncontrados.size()).as("size()").isEqualTo(0);

  }

}