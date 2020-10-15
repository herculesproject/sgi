package org.crue.hercules.sgi.eti.repository;

import java.util.Arrays;
import java.util.Optional;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ComiteFormulario;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

  @Test
  public void findByComiteIdComiteActivoTrueAndFormularioActivoTrue_ReturnsData() throws Exception {

    // given: Datos existentes para el comité formulario

    Comite comite = entityManager.persistFlushFind(generarMockComite());
    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());

    List<ComiteFormulario> response = new LinkedList<ComiteFormulario>();
    response.add(entityManager.persist(generarMockComiteFormulario(comite, formulario)));
    response.add(entityManager.persist(generarMockComiteFormulario(comite, formulario)));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<ComiteFormulario> pageResponse = new PageImpl<>(response.subList(1, 1), pageable, response.size());

    // when: Se buscan los datos
    Page<ComiteFormulario> result = repository
        .findByComiteIdAndComiteActivoTrueAndFormularioActivoTrueAndFormularioIdIn(comite.getId(), Arrays.asList(1L),
            pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getNumber()).isEqualTo(pageResponse.getNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageResponse.getSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(pageResponse.getTotalElements());
    Assertions.assertThat(result.getContent()).isEqualTo(pageResponse.getContent());

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