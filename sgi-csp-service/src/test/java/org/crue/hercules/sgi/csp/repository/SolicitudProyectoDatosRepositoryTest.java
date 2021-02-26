package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * SolicitudProyectoDatosRepositoryTest
 */
@DataJpaTest
public class SolicitudProyectoDatosRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private SolicitudProyectoDatosRepository repository;

  @Test
  public void findBySolicitudId_ReturnsSolicitudProyectoDatos() throws Exception {

    // given: 2 SolicitudProyectoDatos de los que 1 coincide con el idSolicitud
    // buscado

    Solicitud solicitud1 = entityManager.persistAndFlush(Solicitud.builder()//
        .creadorRef("user-001").solicitanteRef("user-002").unidadGestionRef("OTRI")
        .formularioSolicitud(FormularioSolicitud.AYUDAS_GRUPOS).activo(Boolean.TRUE).build());
    SolicitudProyectoDatos solicitudProyectoDatos1 = entityManager
        .persistAndFlush(new SolicitudProyectoDatos(null, solicitud1, "solicitud1", null, null, Boolean.TRUE,
            Boolean.TRUE, Boolean.TRUE, null, null, null, null, null, Boolean.FALSE, Boolean.TRUE));

    Solicitud solicitud2 = entityManager.persistAndFlush(Solicitud.builder()//
        .creadorRef("user-001").solicitanteRef("user-002").unidadGestionRef("OTRI")
        .formularioSolicitud(FormularioSolicitud.AYUDAS_GRUPOS).activo(Boolean.TRUE).build());
    entityManager.persistAndFlush(new SolicitudProyectoDatos(null, solicitud2, "solicitud2", null, null, Boolean.TRUE,
        Boolean.TRUE, Boolean.TRUE, null, null, null, null, null, Boolean.FALSE, Boolean.TRUE));

    Long convocatoriaIdBuscada = solicitud1.getId();

    // when: se busca el SolicitudProyectoDatos por idSolicitud
    SolicitudProyectoDatos solicitudProyectoDatosEncontrado = repository.findBySolicitudId(convocatoriaIdBuscada).get();

    // then: Se recupera el SolicitudProyectoDatos con el idSolicitud buscado
    Assertions.assertThat(solicitudProyectoDatosEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(solicitudProyectoDatosEncontrado.getTitulo()).as("getTitulo")
        .isEqualTo(solicitudProyectoDatos1.getTitulo());

  }

  @Test
  public void findBySolicitudNoExiste_ReturnsNull() throws Exception {

    // given: 2 SolicitudProyectoDatos de los que ninguno coincide con el
    // idSolicitud
    // buscado
    Solicitud solicitud1 = entityManager.persistAndFlush(Solicitud.builder()//
        .creadorRef("user-001").solicitanteRef("user-002").unidadGestionRef("OTRI")
        .formularioSolicitud(FormularioSolicitud.AYUDAS_GRUPOS).activo(Boolean.TRUE).build());
    entityManager.persistAndFlush(new SolicitudProyectoDatos(null, solicitud1, "solicitud1", null, null, Boolean.TRUE,
        Boolean.TRUE, Boolean.TRUE, null, null, null, null, null, Boolean.FALSE, Boolean.TRUE));
    Solicitud solicitud2 = entityManager.persistAndFlush(Solicitud.builder()//
        .creadorRef("user-001").solicitanteRef("user-002").unidadGestionRef("OTRI")
        .formularioSolicitud(FormularioSolicitud.AYUDAS_GRUPOS).activo(Boolean.TRUE).build());
    entityManager.persistAndFlush(new SolicitudProyectoDatos(null, solicitud2, "solicitud2", null, null, Boolean.TRUE,
        Boolean.TRUE, Boolean.TRUE, null, null, null, null, null, Boolean.FALSE, Boolean.TRUE));

    Long solicitudIdBuscada = 99999L;

    // when: se busca el SolicitudProyectoDatos por solicitudId
    Optional<SolicitudProyectoDatos> solicitudProyectoDatosEncontrado = repository
        .findBySolicitudId(solicitudIdBuscada);

    // then: Se recupera el SolicitudProyectoDatos con el solicitudId buscado
    Assertions.assertThat(solicitudProyectoDatosEncontrado).isEqualTo(Optional.empty());
  }

}