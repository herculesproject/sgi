package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class ListadoAreaTematicaRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ListadoAreaTematicaRepository repository;

  @Test
  public void findByNombre_ReturnsListadoAreaTematica() throws Exception {
    // given: data ListadoAreaTematica with nombre to find
    ListadoAreaTematica listadoAreaTematica = generarMockListadoAreaTematica(1L, Boolean.TRUE);
    entityManager.persistAndFlush(listadoAreaTematica);
    entityManager.persistAndFlush(generarMockListadoAreaTematica(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockListadoAreaTematica(3L, Boolean.TRUE));

    // when: find given nombre
    ListadoAreaTematica listadoAreaTematicaFound = repository.findByNombre(listadoAreaTematica.getNombre()).get();

    // then: ListadoAreaTematica with given name is found
    Assertions.assertThat(listadoAreaTematicaFound).isNotNull();
    Assertions.assertThat(listadoAreaTematicaFound.getId()).isEqualTo(listadoAreaTematica.getId());
    Assertions.assertThat(listadoAreaTematicaFound.getNombre()).isEqualTo(listadoAreaTematica.getNombre());
    Assertions.assertThat(listadoAreaTematicaFound.getDescripcion()).isEqualTo(listadoAreaTematica.getDescripcion());
    Assertions.assertThat(listadoAreaTematicaFound.getActivo()).isEqualTo(listadoAreaTematica.getActivo());
  }

  @Test
  public void findByNombre_ReturnsNull() throws Exception {
    // given: data ListadoAreaTematica with nombre to find
    ListadoAreaTematica listadoAreaTematica = generarMockListadoAreaTematica(1L, Boolean.TRUE);
    entityManager.persistAndFlush(generarMockListadoAreaTematica(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockListadoAreaTematica(3L, Boolean.TRUE));

    // when: find given nombre
    Optional<ListadoAreaTematica> listadoAreaTematicaFound = repository.findByNombre(listadoAreaTematica.getNombre());

    // then: ListadoAreaTematica with given name is not found
    Assertions.assertThat(listadoAreaTematicaFound).isEqualTo(Optional.empty());
  }

  /**
   * Función que devuelve un objeto ListadoAreaTematica
   * 
   * @param id
   * @param activo
   * @return ListadoAreaTematica
   */
  private ListadoAreaTematica generarMockListadoAreaTematica(Long id, Boolean activo) {
    return ListadoAreaTematica.builder().nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo).build();
  }
}
