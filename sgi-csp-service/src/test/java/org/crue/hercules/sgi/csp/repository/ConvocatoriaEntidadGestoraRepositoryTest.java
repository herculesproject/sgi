package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class ConvocatoriaEntidadGestoraRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ConvocatoriaEntidadGestoraRepository repository;

  @Test
  public void findByConvocatoriaIdIdAndEntidadRef_ReturnsConvocatoriaEntidadGestora() throws Exception {

    // given: data ConvocatoriaEntidadGestora to find by Convocatoria and EntidadRef
    Convocatoria convocatoria1 = new Convocatoria(null, "codigo-1", Boolean.TRUE);
    entityManager.persistAndFlush(convocatoria1);
    Convocatoria convocatoria2 = new Convocatoria(null, "codigo-2", Boolean.TRUE);
    entityManager.persistAndFlush(convocatoria2);

    ConvocatoriaEntidadGestora convocatoriaEntidadGestora1 = new ConvocatoriaEntidadGestora(null, convocatoria1,
        "entidadRef-1");
    entityManager.persistAndFlush(convocatoriaEntidadGestora1);
    ConvocatoriaEntidadGestora convocatoriaEntidadGestora2 = new ConvocatoriaEntidadGestora(null, convocatoria2,
        "entidadRef-2");
    entityManager.persistAndFlush(convocatoriaEntidadGestora2);

    Long convocatoriaIdBuscado = convocatoria1.getId();
    String entidadRefBuscado = "entidadRef-1";

    // when: find by by Convocatoria and EntidadRef
    ConvocatoriaEntidadGestora dataFound = repository
        .findByConvocatoriaIdAndEntidadRef(convocatoriaIdBuscado, entidadRefBuscado).get();

    // then: ConvocatoriaEntidadGestora is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(convocatoriaEntidadGestora1.getId());
    Assertions.assertThat(dataFound.getConvocatoria().getId())
        .isEqualTo(convocatoriaEntidadGestora1.getConvocatoria().getId());
    Assertions.assertThat(dataFound.getEntidadRef()).isEqualTo(convocatoriaEntidadGestora1.getEntidadRef());
  }

  @Test
  public void findByModeloEjecucionIdAndTipoFinalidadId_ReturnsNull() throws Exception {
    // given: data ConvocatoriaEntidadGestora to find by Convocatoria and EntidadRef
    Convocatoria convocatoria1 = new Convocatoria(null, "codigo-1", Boolean.TRUE);
    entityManager.persistAndFlush(convocatoria1);
    Convocatoria convocatoria2 = new Convocatoria(null, "codigo-2", Boolean.TRUE);
    entityManager.persistAndFlush(convocatoria2);

    ConvocatoriaEntidadGestora convocatoriaEntidadGestora1 = new ConvocatoriaEntidadGestora(null, convocatoria1,
        "entidadRef-1");
    entityManager.persistAndFlush(convocatoriaEntidadGestora1);
    ConvocatoriaEntidadGestora convocatoriaEntidadGestora2 = new ConvocatoriaEntidadGestora(null, convocatoria2,
        "entidadRef-2");
    entityManager.persistAndFlush(convocatoriaEntidadGestora2);

    Long convocatoriaIdBuscado = convocatoria1.getId();
    String entidadRefBuscado = "entidadRef-2";

    // when: find by by Convocatoria and EntidadRef
    Optional<ConvocatoriaEntidadGestora> dataFound = repository.findByConvocatoriaIdAndEntidadRef(convocatoriaIdBuscado,
        entidadRefBuscado);

    // then: ConvocatoriaEntidadGestora is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }
}
