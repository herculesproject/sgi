package org.crue.hercules.sgi.csp.repository;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class SolicitudProyectoEquipoSocioRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private SolicitudProyectoEquipoSocioRepository repository;

  @Test
  public void findAllBySolicitudProyectoSocioId_ReturnsConvocatoriaPeriodoJustificacion() throws Exception {

    // given: 2 SolicitudProyectoEquipoSocio para el SolicitudProyectoSocio buscado

    Solicitud solicitud1 = entityManager.persistAndFlush(Solicitud.builder()//
        .creadorRef("user-001").solicitanteRef("user-002").unidadGestionRef("OTRI")
        .formularioSolicitud(FormularioSolicitud.AYUDAS_GRUPOS).activo(Boolean.TRUE).build());
    SolicitudProyectoDatos solicitudProyectoDatos = entityManager
        .persistAndFlush(new SolicitudProyectoDatos(null, solicitud1, "solicitud1", null, null, Boolean.TRUE,
            Boolean.TRUE, Boolean.TRUE, null, null, null, null, null, Boolean.FALSE, Boolean.TRUE));

    RolSocio rolSocio = RolSocio.builder().abreviatura("001").nombre("Lider").descripcion("Lider")
        .coordinador(Boolean.FALSE).activo(Boolean.TRUE).build();
    entityManager.persistAndFlush(rolSocio);

    RolProyecto rolProyecto = RolProyecto.builder().abreviatura("001").nombre("Rol1").descripcion("Rol1")
        .rolPrincipal(Boolean.FALSE).responsableEconomico(Boolean.FALSE).equipo(RolProyecto.Equipo.INVESTIGACION)
        .colectivoRef("PDI").activo(Boolean.TRUE).build();
    entityManager.persistAndFlush(rolProyecto);

    SolicitudProyectoSocio solicitudProyectoSocio1 = entityManager.persistAndFlush(
        new SolicitudProyectoSocio(null, solicitudProyectoDatos, rolSocio, "001", 1, 3, 3, new BigDecimal(468)));

    SolicitudProyectoSocio solicitudProyectoSocio2 = entityManager.persistAndFlush(
        new SolicitudProyectoSocio(null, solicitudProyectoDatos, rolSocio, "002", 1, 3, 3, new BigDecimal(468)));

    SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocio1 = entityManager.persistAndFlush(
        new SolicitudProyectoEquipoSocio(null, solicitudProyectoSocio1, "user-001", rolProyecto, 1, 3));

    SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocio2 = entityManager.persistAndFlush(
        new SolicitudProyectoEquipoSocio(null, solicitudProyectoSocio1, "user-001", rolProyecto, 5, 6));

    entityManager.persistAndFlush(
        new SolicitudProyectoEquipoSocio(null, solicitudProyectoSocio2, "user-001", rolProyecto, 1, 3));

    Long proyectoSocioIdBuscado = solicitudProyectoSocio1.getId();

    // when: se buscan los SolicitudProyectoEquipoSocio por SolicitudProyectoSocioId
    List<SolicitudProyectoEquipoSocio> dataFound = repository.findAllBySolicitudProyectoSocioId(proyectoSocioIdBuscado);

    // then: Se recuperan los SolicitudProyectoEquipoSocio con el
    // SolicitudProyectoSocioId
    // buscado
    Assertions.assertThat(dataFound.size()).isEqualTo(2);
    Assertions.assertThat(dataFound.get(0).getSolicitudProyectoSocio().getId())
        .isEqualTo(solicitudProyectoEquipoSocio1.getSolicitudProyectoSocio().getId());
    Assertions.assertThat(dataFound.get(1).getSolicitudProyectoSocio().getId())
        .isEqualTo(solicitudProyectoEquipoSocio2.getSolicitudProyectoSocio().getId());
  }

}
