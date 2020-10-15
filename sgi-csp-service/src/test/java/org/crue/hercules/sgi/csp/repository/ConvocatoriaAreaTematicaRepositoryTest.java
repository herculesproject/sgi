package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class ConvocatoriaAreaTematicaRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ConvocatoriaAreaTematicaRepository repository;

  @Test
  public void findByConvocatoriaIdAndAreaTematicaArbolId_ReturnsConvocatoriaAreaTematica() throws Exception {

    // given: data ConvocatoriaAreaTematica to find by Convocatoria and
    // AreaTematicaArbolId
    Convocatoria convocatoria1 = new Convocatoria(null, "codigo-1", Boolean.TRUE);
    entityManager.persistAndFlush(convocatoria1);
    Convocatoria convocatoria2 = new Convocatoria(null, "codigo-2", Boolean.TRUE);
    entityManager.persistAndFlush(convocatoria2);

    ListadoAreaTematica listadoAreaTematica1 = new ListadoAreaTematica(null, "nombre-1", "descripcion-1", Boolean.TRUE);
    entityManager.persistAndFlush(listadoAreaTematica1);
    ListadoAreaTematica listadoAreaTematica2 = new ListadoAreaTematica(null, "nombre-2", "descripcion-2", Boolean.TRUE);
    entityManager.persistAndFlush(listadoAreaTematica2);

    AreaTematicaArbol areaTematicaArbol1 = new AreaTematicaArbol(null, "abr-1", "nombre-1", listadoAreaTematica1, null,
        Boolean.TRUE);
    entityManager.persistAndFlush(areaTematicaArbol1);
    AreaTematicaArbol areaTematicaArbol2 = new AreaTematicaArbol(null, "abr-2", "nombre-2", listadoAreaTematica2, null,
        Boolean.TRUE);
    entityManager.persistAndFlush(areaTematicaArbol2);

    ConvocatoriaAreaTematica convocatoriaAreaTematica1 = new ConvocatoriaAreaTematica(null, areaTematicaArbol1,
        convocatoria1, "observaciones-1");
    entityManager.persistAndFlush(convocatoriaAreaTematica1);
    ConvocatoriaAreaTematica convocatoriaAreaTematica2 = new ConvocatoriaAreaTematica(null, areaTematicaArbol2,
        convocatoria2, "observaciones-2");
    entityManager.persistAndFlush(convocatoriaAreaTematica2);

    Long convocatoriaIdBuscado = convocatoria1.getId();
    Long areaTematicaArbolIdBuscado = areaTematicaArbol1.getId();

    // when: find by Convocatoria and AreaTematicaArbolId
    ConvocatoriaAreaTematica dataFound = repository
        .findByConvocatoriaIdAndAreaTematicaArbolId(convocatoriaIdBuscado, areaTematicaArbolIdBuscado).get();

    // then: ConvocatoriaAreaTematica is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(convocatoriaAreaTematica1.getId());
    Assertions.assertThat(dataFound.getConvocatoria().getId())
        .isEqualTo(convocatoriaAreaTematica1.getConvocatoria().getId());
    Assertions.assertThat(dataFound.getAreaTematicaArbol().getId())
        .isEqualTo(convocatoriaAreaTematica1.getAreaTematicaArbol().getId());
  }

  @Test
  public void findByConvocatoriaIdAndAreaTematicaArbolId_ReturnsNull() throws Exception {
    // given: data ConvocatoriaAreaTematica to find by Convocatoria and
    // AreaTematicaArbolId
    Convocatoria convocatoria1 = new Convocatoria(null, "codigo-1", Boolean.TRUE);
    entityManager.persistAndFlush(convocatoria1);
    Convocatoria convocatoria2 = new Convocatoria(null, "codigo-2", Boolean.TRUE);
    entityManager.persistAndFlush(convocatoria2);

    ListadoAreaTematica listadoAreaTematica1 = new ListadoAreaTematica(null, "nombre-1", "descripcion-1", Boolean.TRUE);
    entityManager.persistAndFlush(listadoAreaTematica1);
    ListadoAreaTematica listadoAreaTematica2 = new ListadoAreaTematica(null, "nombre-2", "descripcion-2", Boolean.TRUE);
    entityManager.persistAndFlush(listadoAreaTematica2);

    AreaTematicaArbol areaTematicaArbol1 = new AreaTematicaArbol(null, "abr-1", "nombre-1", listadoAreaTematica1, null,
        Boolean.TRUE);
    entityManager.persistAndFlush(areaTematicaArbol1);
    AreaTematicaArbol areaTematicaArbol2 = new AreaTematicaArbol(null, "abr-2", "nombre-2", listadoAreaTematica2, null,
        Boolean.TRUE);
    entityManager.persistAndFlush(areaTematicaArbol2);

    ConvocatoriaAreaTematica convocatoriaAreaTematica1 = new ConvocatoriaAreaTematica(null, areaTematicaArbol1,
        convocatoria1, "observaciones-1");
    entityManager.persistAndFlush(convocatoriaAreaTematica1);
    ConvocatoriaAreaTematica convocatoriaAreaTematica2 = new ConvocatoriaAreaTematica(null, areaTematicaArbol2,
        convocatoria2, "observaciones-2");
    entityManager.persistAndFlush(convocatoriaAreaTematica2);

    Long convocatoriaIdBuscado = convocatoria1.getId();
    Long areaTematicaArbolIdBuscado = areaTematicaArbol2.getId();

    // when: find by by Convocatoria and AreaTematicaArbolId
    Optional<ConvocatoriaAreaTematica> dataFound = repository
        .findByConvocatoriaIdAndAreaTematicaArbolId(convocatoriaIdBuscado, areaTematicaArbolIdBuscado);

    // then: ConvocatoriaAreaTematica is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }
}
