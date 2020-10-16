package org.crue.hercules.sgi.eti.repository;

import java.util.Arrays;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ComiteFormulario;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class ComiteFormularioRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ComiteFormularioRepository repository;

  @Test
  public void findByComiteIdAndComiteActivoTrueAndFormularioIdInAndFormularioActivoTrue_ReturnsData() throws Exception {

    // given: Datos existentes para el comité formulario

    Comite comite = entityManager.persistFlushFind(generarMockComite());
    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    entityManager.persistAndFlush(generarMockComiteFormulario(comite, formulario));

    // when: Se buscan los datos
    Optional<ComiteFormulario> result = repository
        .findByComiteIdAndComiteActivoTrueAndFormularioIdInAndFormularioActivoTrue(comite.getId(), Arrays.asList(1L));

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

  /**
   * Función que devuelve un objeto Formulario
   * 
   * @return el objeto Formulario
   */
  private Formulario generarMockFormulario() {
    return new Formulario(1L, "M10", "Formulario M10", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto ComiteFormulario
   * 
   * @return el objeto ComiteFormulario
   */
  private ComiteFormulario generarMockComiteFormulario(Comite comite, Formulario formulario) {
    return new ComiteFormulario(null, comite, formulario);
  }

}