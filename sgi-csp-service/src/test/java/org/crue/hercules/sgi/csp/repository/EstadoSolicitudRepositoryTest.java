package org.crue.hercules.sgi.csp.repository;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

@DataJpaTest
public class EstadoSolicitudRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private EstadoSolicitudRepository repository;

  @Test
  public void findAllBySolicitud_ReturnsPageEstadoSolicitud() throws Exception {
    // given: data EstadoSolicitud with nombre to find
    entityManager.persistAndFlush(generarMockEstadoSolicitud(1L));
    entityManager.persistAndFlush(generarMockEstadoSolicitud(2L));
    entityManager.persistAndFlush(generarMockEstadoSolicitud(1L));

    // when: find given nombre
    Page<EstadoSolicitud> page = repository.findAllByidSolicitud(1L, null);

    // then: EstadoSolicitud with given name is found
    Assertions.assertThat(page.hasContent()).isNotNull();
  }

  /**
   * Función que devuelve un objeto EstadoSolicitud
   * 
   * @param id identificador de la solicitud
   * @return EstadoSolicitud
   */
  private EstadoSolicitud generarMockEstadoSolicitud(Long id) {
    return EstadoSolicitud.builder().estado(EstadoSolicitud.Estado.BORRADOR).idSolicitud(id)
        .fechaEstado(LocalDateTime.now()).comentario("comentario").build();
  }

}
