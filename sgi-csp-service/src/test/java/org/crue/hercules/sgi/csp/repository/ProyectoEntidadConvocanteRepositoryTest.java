package org.crue.hercules.sgi.csp.repository;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProyectoEntidadConvocanteRepositoryTest extends BaseRepositoryTest {
  @Autowired
  private ProyectoEntidadConvocanteRepository repository;

  @Test
  public void existsByProyectoIdAndEntidadRef_returnsTrue() throws Exception {
    // given: 2 ProyectoEntidadConvocante (only 1 matches find criteria)
    Long proyectoId = 1L;
    String matchingEntidadRef = "Entidad1";
    String notMatchingEntidadRef = "Entidad2";
    ProyectoEntidadConvocante proyectoEntidadConvocante1 = ProyectoEntidadConvocante.builder().proyectoId(proyectoId)
        .entidadRef(matchingEntidadRef).build();
    entityManager.persistFlushFind(proyectoEntidadConvocante1);
    ProyectoEntidadConvocante proyectoEntidadConvocante2 = ProyectoEntidadConvocante.builder().proyectoId(proyectoId)
        .entidadRef(notMatchingEntidadRef).build();
    entityManager.persistFlushFind(proyectoEntidadConvocante2);

    // when: check existence by existing ProyectoId and EntidadRef
    Boolean response = repository.existsByProyectoIdAndEntidadRef(proyectoId, matchingEntidadRef);

    // then: the matching ProyectoEntidadConvocante is returned
    Assertions.assertThat(response).isTrue();
  }

  @Test
  public void existsProyectoIdAndEntidadRef_NotExists_returnsFalse() throws Exception {
    // given: 2 ProyectoEntidadConvocante (no one matches find criteria)
    Long proyectoId = 1L;
    String entidadRef = "Entidad";
    String notMatchingEntidadRef1 = "Entidad1";
    String notMatchingEntidadRef2 = "Entidad2";
    ProyectoEntidadConvocante proyectoEntidadConvocante1 = ProyectoEntidadConvocante.builder().proyectoId(proyectoId)
        .entidadRef(notMatchingEntidadRef1).build();
    entityManager.persistFlushFind(proyectoEntidadConvocante1);
    ProyectoEntidadConvocante proyectoEntidadConvocante2 = ProyectoEntidadConvocante.builder().proyectoId(proyectoId)
        .entidadRef(notMatchingEntidadRef2).build();
    entityManager.persistFlushFind(proyectoEntidadConvocante2);

    // when: check existence by no existing ProyectoId and EntidadRef
    boolean response = repository.existsByProyectoIdAndEntidadRef(proyectoId, entidadRef);

    // then: the no ProyectoEntidadConvocante is returned
    Assertions.assertThat(response).isFalse();
  }
}
