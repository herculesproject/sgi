package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoEstadoConvocatoriaEnum;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * RequisitoIPRepositoryTest
 */
@DataJpaTest
public class RequisitoIPRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private RequisitoIPRepository repository;

  @Test
  public void findByConvocatoriaId_ReturnsRequisitoIP() throws Exception {

    // given: 2 RequisitoIP de los que 1 coincide con el idConvocatoria buscado
    RequisitoIP requisitoIP1 = generarMockRequisitoIP(1L);
    entityManager.persistAndFlush(requisitoIP1.getConvocatoria());
    entityManager.persistAndFlush(requisitoIP1);

    entityManager.persistAndFlush(requisitoIP1.getConvocatoria());
    RequisitoIP requisitoIP2 = generarMockRequisitoIP(2L);
    entityManager.persistAndFlush(requisitoIP2.getConvocatoria());
    entityManager.persistAndFlush(requisitoIP2);

    Long convocatoriaIdBuscada = requisitoIP1.getConvocatoria().getId();

    // when: se busca el RequisitoIP por el idConvocatoria
    RequisitoIP requisitoIPEncontrado = repository.findByConvocatoriaId(convocatoriaIdBuscada).get();

    // then: Se recupera el RequisitoIP con el idConvocatoria buscado
    Assertions.assertThat(requisitoIPEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(requisitoIPEncontrado.getModalidadContratoRef()).as("getModalidadContratoRef")
        .isEqualTo(requisitoIP1.getModalidadContratoRef());
    Assertions.assertThat(requisitoIPEncontrado.getNivelAcademicoRef()).as("getNivelAcademicoRef")
        .isEqualTo(requisitoIP1.getNivelAcademicoRef());
  }

  @Test
  public void findByConvocatoriaNoExiste_ReturnsNull() throws Exception {

    // given: 2 RequisitoIP que no coinciden con el idConvocatoria buscado
    RequisitoIP requisitoIP1 = generarMockRequisitoIP(1L);
    entityManager.persistAndFlush(requisitoIP1.getConvocatoria());
    entityManager.persistAndFlush(requisitoIP1);

    RequisitoIP requisitoIP2 = generarMockRequisitoIP(2L);

    Long convocatoriaIdBuscada = requisitoIP2.getConvocatoria().getId();

    // when: se busca el RequisitoIP por idConvocatoria que no existe
    Optional<RequisitoIP> requisitoIPEncontrado = repository.findByConvocatoriaId(convocatoriaIdBuscada);

    // then: Se recupera el RequisitoIP con el idConvocatoria buscado
    Assertions.assertThat(requisitoIPEncontrado).isEqualTo(Optional.empty());
  }

  /**
   * Función que devuelve un objeto RequisitoIP
   * 
   * @param id identificador
   * @return el objeto RequisitoIP
   */
  private RequisitoIP generarMockRequisitoIP(Long id) {
    RequisitoIP requisitoIP = new RequisitoIP();
    requisitoIP.setConvocatoria(Convocatoria.builder().activo(Boolean.TRUE).codigo("codigo-00" + id)
        .estadoActual(TipoEstadoConvocatoriaEnum.REGISTRADA).colaborativos(Boolean.FALSE).build());
    requisitoIP.setSexo("Hombre");
    requisitoIP.setModalidadContratoRef("modalidad-00" + (id != null ? id : 1L));
    requisitoIP.setNivelAcademicoRef("nivel-00" + (id != null ? id : 1L));
    return requisitoIP;
  }

}
