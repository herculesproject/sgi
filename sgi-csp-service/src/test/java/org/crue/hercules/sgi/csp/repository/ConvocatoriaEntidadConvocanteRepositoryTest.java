package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ConvocatoriaEntidadConvocanteRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaEntidadConvocanteRepository repository;

  @Test
  public void findByConvocatoriaIdIdAndEntidadRef_ReturnsConvocatoriaEntidadConvocante() throws Exception {

    // given: 2 ConvocatoriaEntidadConvocante de los que 1 coincide con el
    // ConvocatoriaId y EntidadRef buscado
    Convocatoria convocatoria1 = Convocatoria.builder()//
        .estado(Convocatoria.Estado.BORRADOR)//
        .codigo("codigo-1")//
        .unidadGestionRef("OPE")//
        .anio(2020)//
        .titulo("titulo")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(convocatoria1);
    Convocatoria convocatoria2 = Convocatoria.builder()//
        .estado(Convocatoria.Estado.BORRADOR)//
        .codigo("codigo-2")//
        .unidadGestionRef("OPE")//
        .anio(2020)//
        .titulo("titulo")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(convocatoria2);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante1 = new ConvocatoriaEntidadConvocante(null,
        convocatoria1, "entidadRef-1", null);
    entityManager.persistAndFlush(convocatoriaEntidadConvocante1);
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante2 = new ConvocatoriaEntidadConvocante(null,
        convocatoria2, "entidadRef-2", null);
    entityManager.persistAndFlush(convocatoriaEntidadConvocante2);

    Long convocatoriaIdBuscado = convocatoria1.getId();
    String entidadRefBuscado = "entidadRef-1";

    // when: se busca el ConvocatoriaEntidadConvocante por ConvocatoriaId y
    // EntidadRef
    ConvocatoriaEntidadConvocante dataFound = repository
        .findByConvocatoriaIdAndEntidadRef(convocatoriaIdBuscado, entidadRefBuscado).get();

    // then: Se recupera el ConvocatoriaEntidadConvocante con el ConvocatoriaId y
    // EntidadRef buscado
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(convocatoriaEntidadConvocante1.getId());
    Assertions.assertThat(dataFound.getConvocatoria().getId())
        .isEqualTo(convocatoriaEntidadConvocante1.getConvocatoria().getId());
    Assertions.assertThat(dataFound.getEntidadRef()).isEqualTo(convocatoriaEntidadConvocante1.getEntidadRef());
  }

  @Test
  public void findByConvocatoriaIdIdAndEntidadRef_ReturnsNull() throws Exception {
    // given: 2 ConvocatoriaEntidadConvocante que no coincide con el ConvocatoriaId
    // y EntidadRef buscado
    Convocatoria convocatoria1 = Convocatoria.builder()//
        .estado(Convocatoria.Estado.BORRADOR)//
        .codigo("codigo-1")//
        .unidadGestionRef("OPE")//
        .anio(2020)//
        .titulo("titulo")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(convocatoria1);
    Convocatoria convocatoria2 = Convocatoria.builder()//
        .estado(Convocatoria.Estado.BORRADOR)//
        .codigo("codigo-2")//
        .unidadGestionRef("OPE")//
        .anio(2020)//
        .titulo("titulo")//
        .activo(Boolean.TRUE)//
        .build();
    entityManager.persistAndFlush(convocatoria2);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante1 = new ConvocatoriaEntidadConvocante(null,
        convocatoria1, "entidadRef-1", null);
    entityManager.persistAndFlush(convocatoriaEntidadConvocante1);
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante2 = new ConvocatoriaEntidadConvocante(null,
        convocatoria2, "entidadRef-2", null);
    entityManager.persistAndFlush(convocatoriaEntidadConvocante2);

    Long convocatoriaIdBuscado = convocatoria1.getId();
    String entidadRefBuscado = "entidadRef-2";

    // when: se busca el ConvocatoriaEntidadConvocante por ConvocatoriaId y
    // EntidadRef
    Optional<ConvocatoriaEntidadConvocante> dataFound = repository
        .findByConvocatoriaIdAndEntidadRef(convocatoriaIdBuscado, entidadRefBuscado);

    // then: No hay ningun ConvocatoriaEntidadConvocante con el ConvocatoriaId y
    // EntidadRef buscado
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }
}
