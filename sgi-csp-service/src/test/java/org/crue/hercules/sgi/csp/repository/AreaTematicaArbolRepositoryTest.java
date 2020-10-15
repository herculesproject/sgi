package org.crue.hercules.sgi.csp.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * AreaTematicaArbolRepositoryTest
 */
@DataJpaTest
public class AreaTematicaArbolRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private AreaTematicaArbolRepository repository;

  @Test
  public void findByAbreviaturaAndListadoAreaTematicaId_ReturnsAreaTematicaArbol() throws Exception {

    // given: 2 AreaTematicaArbol de los que 1 coincide con la abreviatura buscada
    ListadoAreaTematica listadoAreaTematica = new ListadoAreaTematica(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(listadoAreaTematica);

    AreaTematicaArbol areaTematicaArbol1 = new AreaTematicaArbol(null, "A-1", "nombre-1", listadoAreaTematica, null,
        true);
    entityManager.persistAndFlush(areaTematicaArbol1);

    AreaTematicaArbol areaTematicaArbol2 = new AreaTematicaArbol(null, "A-2", "nombre-2", listadoAreaTematica,
        areaTematicaArbol1, true);
    entityManager.persistAndFlush(areaTematicaArbol2);

    String abreviaturaBuscada = "A-1";

    // when: se busca el AreaTematicaArbol por abreviatura
    AreaTematicaArbol areaTematicaArbolEncontrado = repository
        .findByAbreviaturaAndListadoAreaTematicaId(abreviaturaBuscada, listadoAreaTematica.getId()).get();

    // then: Se recupera el AreaTematicaArbol con la abreviatura buscada
    Assertions.assertThat(areaTematicaArbolEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(areaTematicaArbolEncontrado.getAbreviatura()).as("getAbreviatura")
        .isEqualTo(areaTematicaArbol1.getAbreviatura());
    Assertions.assertThat(areaTematicaArbolEncontrado.getNombre()).as("getNombre")
        .isEqualTo(areaTematicaArbol1.getNombre());
    Assertions.assertThat(areaTematicaArbolEncontrado.getActivo()).as("getActivo")
        .isEqualTo(areaTematicaArbol1.getActivo());
  }

  @Test
  public void findByAbreviaturaAndListadoAreaTematicaIdNoExiste_ReturnsNull() throws Exception {

    // given: 2 AreaTematicaArbol que no coinciden con la abreviatura buscada
    ListadoAreaTematica listadoAreaTematica = new ListadoAreaTematica(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(listadoAreaTematica);

    AreaTematicaArbol areaTematicaArbol1 = new AreaTematicaArbol(null, "A-1", "nombre-1", listadoAreaTematica, null,
        true);
    entityManager.persistAndFlush(areaTematicaArbol1);

    AreaTematicaArbol areaTematicaArbol2 = new AreaTematicaArbol(null, "A-2", "nombre-2", listadoAreaTematica,
        areaTematicaArbol1, true);
    entityManager.persistAndFlush(areaTematicaArbol2);

    String abreviaturaBuscada = "A-0";

    // when: se busca el AreaTematicaArbol por abreviatura
    Optional<AreaTematicaArbol> areaTematicaArbolEncontrado = repository
        .findByNombreAndListadoAreaTematicaId(abreviaturaBuscada, listadoAreaTematica.getId());

    // then: No hay ningun AreaTematicaArbol con la abreviatura buscada
    Assertions.assertThat(areaTematicaArbolEncontrado).isEqualTo(Optional.empty());
  }

  @Test
  public void findByNombreAndListadoAreaTematicaId_ReturnsAreaTematicaArbol() throws Exception {

    // given: 2 AreaTematicaArbol de los que 1 coincide con el nombre buscado
    ListadoAreaTematica listadoAreaTematica = new ListadoAreaTematica(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(listadoAreaTematica);

    AreaTematicaArbol areaTematicaArbol1 = new AreaTematicaArbol(null, "A-1", "nombre-1", listadoAreaTematica, null,
        true);
    entityManager.persistAndFlush(areaTematicaArbol1);

    AreaTematicaArbol areaTematicaArbol2 = new AreaTematicaArbol(null, "A-2", "nombre-2", listadoAreaTematica,
        areaTematicaArbol1, true);
    entityManager.persistAndFlush(areaTematicaArbol2);

    String nombreBuscado = "nombre-1";

    // when: se busca el AreaTematicaArbol por nombre
    AreaTematicaArbol areaTematicaArbolEncontrado = repository
        .findByNombreAndListadoAreaTematicaId(nombreBuscado, listadoAreaTematica.getId()).get();

    // then: Se recupera el AreaTematicaArbol con el nombre buscado
    Assertions.assertThat(areaTematicaArbolEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(areaTematicaArbolEncontrado.getAbreviatura()).as("getAbreviatura")
        .isEqualTo(areaTematicaArbol1.getAbreviatura());
    Assertions.assertThat(areaTematicaArbolEncontrado.getNombre()).as("getNombre")
        .isEqualTo(areaTematicaArbol1.getNombre());
    Assertions.assertThat(areaTematicaArbolEncontrado.getActivo()).as("getActivo")
        .isEqualTo(areaTematicaArbol1.getActivo());
  }

  @Test
  public void findByNombreAndListadoAreaTematicaIdNoExiste_ReturnsNull() throws Exception {

    // given: 2 AreaTematicaArbol que no coinciden con el nombre buscado
    ListadoAreaTematica listadoAreaTematica = new ListadoAreaTematica(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(listadoAreaTematica);

    AreaTematicaArbol areaTematicaArbol1 = new AreaTematicaArbol(null, "A-1", "nombre-1", listadoAreaTematica, null,
        true);
    entityManager.persistAndFlush(areaTematicaArbol1);

    AreaTematicaArbol areaTematicaArbol2 = new AreaTematicaArbol(null, "A-2", "nombre-2", listadoAreaTematica,
        areaTematicaArbol1, true);
    entityManager.persistAndFlush(areaTematicaArbol2);

    String nombreBuscado = "nombre-noexiste";

    // when: se busca el AreaTematicaArbol por nombre
    Optional<AreaTematicaArbol> areaTematicaArbolEncontrado = repository
        .findByNombreAndListadoAreaTematicaId(nombreBuscado, listadoAreaTematica.getId());

    // then: No hay ningun AreaTematicaArbol con el nombre buscado
    Assertions.assertThat(areaTematicaArbolEncontrado).isEqualTo(Optional.empty());
  }

  @Test
  public void findByPadreIdIn_ReturnsAreaTematicaArbol() throws Exception {
    // given: 2 AreaTematicaArbol de los que 1 coincide con el id padre buscado
    ListadoAreaTematica listadoAreaTematica = new ListadoAreaTematica(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(listadoAreaTematica);

    AreaTematicaArbol areaTematicaArbol1 = new AreaTematicaArbol(null, "A-1", "nombre-1", listadoAreaTematica, null,
        true);
    entityManager.persistAndFlush(areaTematicaArbol1);

    AreaTematicaArbol areaTematicaArbol2 = new AreaTematicaArbol(null, "A-2", "nombre-2", listadoAreaTematica,
        areaTematicaArbol1, true);
    entityManager.persistAndFlush(areaTematicaArbol2);

    List<Long> idsPadreBuscados = Arrays.asList(areaTematicaArbol1.getId());

    // when: se busca el AreaTematicaArbol el id padre
    List<AreaTematicaArbol> areaTematicaArbolEncontrados = repository.findByPadreIdIn(idsPadreBuscados);

    // then: Se recupera el AreaTematicaArbol con el id padre buscado
    Assertions.assertThat(areaTematicaArbolEncontrados.size()).as("size()").isEqualTo(1);
    AreaTematicaArbol areaTematicaArbolEncontrado = areaTematicaArbolEncontrados.get(0);
    Assertions.assertThat(areaTematicaArbolEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(areaTematicaArbolEncontrado.getNombre()).as("getNombre")
        .isEqualTo(areaTematicaArbol2.getNombre());
    Assertions.assertThat(areaTematicaArbolEncontrado.getAbreviatura()).as("getAbreviatura")
        .isEqualTo(areaTematicaArbol2.getAbreviatura());
    Assertions.assertThat(areaTematicaArbolEncontrado.getActivo()).as("getActivo")
        .isEqualTo(areaTematicaArbol2.getActivo());
  }

  @Test
  public void findByPadreIdIn_IdNoExiste_ReturnsEmptyList() throws Exception {
    // given: 2 AreaTematicaArbol que no coinciden con el id padre buscado
    ListadoAreaTematica listadoAreaTematica = new ListadoAreaTematica(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(listadoAreaTematica);

    AreaTematicaArbol areaTematicaArbol1 = new AreaTematicaArbol(null, "A-1", "nombre-1", listadoAreaTematica, null,
        true);
    entityManager.persistAndFlush(areaTematicaArbol1);

    AreaTematicaArbol areaTematicaArbol2 = new AreaTematicaArbol(null, "A-2", "nombre-2", listadoAreaTematica,
        areaTematicaArbol1, true);
    entityManager.persistAndFlush(areaTematicaArbol2);

    List<Long> idsPadreBuscados = Arrays.asList(areaTematicaArbol2.getId());

    // when: se busca el AreaTematicaArbol el id padre
    List<AreaTematicaArbol> areaTematicaArbolEncontrados = repository.findByPadreIdIn(idsPadreBuscados);

    // then: No hay ningun AreaTematicaArbol con el id padre buscado
    Assertions.assertThat(areaTematicaArbolEncontrados.size()).as("size()").isEqualTo(0);

  }

}