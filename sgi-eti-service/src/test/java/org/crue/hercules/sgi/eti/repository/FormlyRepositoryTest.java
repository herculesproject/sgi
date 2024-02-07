package org.crue.hercules.sgi.eti.repository;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.FormlyOutput;
import org.crue.hercules.sgi.eti.enums.Language;
import org.crue.hercules.sgi.eti.model.Formly;
import org.crue.hercules.sgi.eti.model.FormlyNombre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class FormlyRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private FormlyRepository repository;

  @Test
  public void findFirstByNombreOrderByVersionDesc_ReturnsLatestWithGivenName() throws Exception {
    // given: two Formly, with three versions each
    final int N_VERS = 3;
    for (int j = 1; j <= N_VERS; j++) {
      Formly formly = entityManager
          .persistAndFlush(Formly.builder().version(j).build());
      formly = entityManager.persistAndFlush(formly);

      FormlyNombre formlyNombre = entityManager
          .persistAndFlush(
              FormlyNombre.builder().nombre("FRM" + j).formlyId(formly.getId()).esquema("{}")
                  .lang(Language.fromCode("es")).build());
      formlyNombre = entityManager.persistAndFlush(formlyNombre);
    }

    // when: find latest formly version named 'FRM1'
    FormlyOutput dataFound = repository.findByNombreOrderByVersionDesc("FRM3", "es");

    // then: latest formly version is returned
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getVersion()).isEqualTo(N_VERS);
  }
}
