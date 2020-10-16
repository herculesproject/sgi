package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class ComiteRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ComiteRepository repository;

  @Test
  public void findByIdAndActivoTrue_ReturnsData() throws Exception {

    // given: Datos existentes para el comité activo

    Comite comite = entityManager.persistFlushFind(generarMockComite());

    // when: Se buscan los datos
    Optional<Comite> result = repository.findByIdAndActivoTrue(comite.getId());

    // then: Se recuperan los datos correctamente
    Assertions.assertThat(result.get()).isNotNull();

  }

  /**
   * Función que devuelve un objeto Comite
   * 
   * @return el objeto Comite
   */
  private Comite generarMockComite() {
    return new Comite(null, "Comite1", Boolean.TRUE);
  }

}