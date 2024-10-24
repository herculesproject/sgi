package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFaseNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ModeloTipoFaseRepositoryTest
 */
@DataJpaTest

class ModeloTipoFaseRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloTipoFaseRepository repository;

  @Test
  void findByModeloEjecucionIdAndTipoFaseId_ReturnsModeloTipoFase() throws Exception {

    // given: 2 ModeloTipoFase de los que 1 coincide con los ids de
    // ModeloEjecucion y TipoFase
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true, false, false,
        false);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true, false, false,
        false);
    entityManager.persistAndFlush(modeloEjecucion2);

    Set<TipoFaseNombre> nombreTipoFase1 = new HashSet<>();
    nombreTipoFase1.add(new TipoFaseNombre(Language.ES, "nombre-1"));

    TipoFase tipoFase1 = new TipoFase(null, nombreTipoFase1, "descripcion-1", true);
    entityManager.persistAndFlush(tipoFase1);

    Set<TipoFaseNombre> nombreTipoFase2 = new HashSet<>();
    nombreTipoFase2.add(new TipoFaseNombre(Language.ES, "nombre-2"));

    TipoFase tipoFase2 = new TipoFase(null, nombreTipoFase2, "descripcion-2", true);
    entityManager.persistAndFlush(tipoFase2);

    ModeloTipoFase modeloTipoFase1 = new ModeloTipoFase(null, tipoFase1, modeloEjecucion1, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase1);

    ModeloTipoFase modeloTipoFase2 = new ModeloTipoFase(null, tipoFase2, modeloEjecucion2, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase2);

    Long idModeloEjecucionBuscado = modeloEjecucion1.getId();
    Long idTipoFaseBuscado = tipoFase1.getId();

    // when: se busca el ModeloTipoFase
    ModeloTipoFase modeloTipoFaseEncontrado = repository
        .findByModeloEjecucionIdAndTipoFaseId(idModeloEjecucionBuscado, idTipoFaseBuscado).get();

    // then: Se recupera el ModeloTipoEnlace buscado
    Assertions.assertThat(modeloTipoFaseEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(modeloTipoFaseEncontrado.getModeloEjecucion().getNombre())
        .as("getModeloEjecucion().getNombre()").isEqualTo(modeloEjecucion1.getNombre());
    Assertions.assertThat(modeloTipoFaseEncontrado.getTipoFase().getNombre()).as("getTipoEnlace().getNombre()")
        .isEqualTo(tipoFase1.getNombre());
  }

  @Test
  void findByModeloEjecucionIdAndTipoEnlaceId_NoExiste_ReturnsNull() throws Exception {

    // given: 2 ModeloEjecucion que no coinciden con los ids de
    // ModeloEjecucion y TipoEnlace
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true, false, false,
        false);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true, false, false,
        false);
    entityManager.persistAndFlush(modeloEjecucion2);

    Set<TipoFaseNombre> nombreTipoFase1 = new HashSet<>();
    nombreTipoFase1.add(new TipoFaseNombre(Language.ES, "nombre-1"));

    TipoFase tipoFase1 = new TipoFase(null, nombreTipoFase1, "descripcion-1", true);
    entityManager.persistAndFlush(tipoFase1);

    Set<TipoFaseNombre> nombreTipoFase2 = new HashSet<>();
    nombreTipoFase2.add(new TipoFaseNombre(Language.ES, "nombre-2"));

    TipoFase tipoFase2 = new TipoFase(null, nombreTipoFase2, "descripcion-2", true);
    entityManager.persistAndFlush(tipoFase2);

    ModeloTipoFase modeloTipoFase1 = new ModeloTipoFase(null, tipoFase1, modeloEjecucion1, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase1);

    ModeloTipoFase modeloTipoFase2 = new ModeloTipoFase(null, tipoFase2, modeloEjecucion2, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase2);

    Long idModeloEjecucionBuscado = modeloEjecucion2.getId();
    Long idTipoFaseBuscado = tipoFase1.getId();

    // when: se busca el ModeloTipoFase
    Optional<ModeloTipoFase> modeloTipoFaseEncontrado = repository
        .findByModeloEjecucionIdAndTipoFaseId(idModeloEjecucionBuscado, idTipoFaseBuscado);

    // then: No se recupera el ModeloTipoFase buscado
    Assertions.assertThat(modeloTipoFaseEncontrado).isEqualTo(Optional.empty());
  }

}
