package org.crue.hercules.sgi.csp.service;

import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;

import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudHitoAviso;
import org.crue.hercules.sgi.csp.repository.SolicitudHitoRepository;
import org.crue.hercules.sgi.csp.util.AvisoTaskHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.jpa.domain.Specification;

/**
 * SolicitudAvisoServiceTest
 */
class SolicitudAvisoServiceTest extends BaseServiceTest {

  private static final Long SOLICITUD_ID = 1L;

  @Mock
  private SolicitudHitoRepository solicitudHitoRepository;
  @Mock
  private AvisoTaskHelper avisoTaskHelper;

  private SolicitudAvisoService service;

  @BeforeEach
  void setUp() {
    service = new SolicitudAvisoService(solicitudHitoRepository, avisoTaskHelper);
  }

  @Test
  void disablePendingAvisos_DisablesTasksOfHitosAvisos() {
    // given: dos hitos con aviso y un hito sin aviso
    givenHitosWithAvisos();

    // when: se desactivan los avisos pendientes de la solicitud
    service.disablePendingAvisos(SOLICITUD_ID);

    // then: se desactivan las tareas de todos los avisos de los hitos
    verify(avisoTaskHelper).disablePendingTasks(Arrays.asList("11", "12"));
  }

  @Test
  void enablePendingAvisos_EnablesTasksOfHitosAvisos() {
    // given: dos hitos con aviso y un hito sin aviso
    givenHitosWithAvisos();

    // when: se activan los avisos pendientes de la solicitud
    service.enablePendingAvisos(SOLICITUD_ID);

    // then: se activan las tareas de todos los avisos de los hitos
    verify(avisoTaskHelper).enablePendingTasks(Arrays.asList("11", "12"));
  }

  @Test
  void disablePendingAvisos_WithoutHitos_PassesEmptyList() {
    // given: una solicitud sin hitos
    BDDMockito.given(solicitudHitoRepository.findAll(ArgumentMatchers.<Specification<SolicitudHito>>any()))
        .willReturn(Collections.emptyList());

    // when: se desactivan los avisos pendientes de la solicitud
    service.disablePendingAvisos(SOLICITUD_ID);

    // then: no hay tareas que desactivar
    verify(avisoTaskHelper).disablePendingTasks(Collections.emptyList());
  }

  private void givenHitosWithAvisos() {
    SolicitudHito hitoWithAviso1 = SolicitudHito.builder()
        .id(1L)
        .solicitudId(SOLICITUD_ID)
        .solicitudHitoAviso(SolicitudHitoAviso.builder().tareaProgramadaRef("11").build())
        .build();
    SolicitudHito hitoWithAviso2 = SolicitudHito.builder()
        .id(2L)
        .solicitudId(SOLICITUD_ID)
        .solicitudHitoAviso(SolicitudHitoAviso.builder().tareaProgramadaRef("12").build())
        .build();
    SolicitudHito hitoWithoutAviso = SolicitudHito.builder()
        .id(3L)
        .solicitudId(SOLICITUD_ID)
        .build();

    BDDMockito.given(solicitudHitoRepository.findAll(ArgumentMatchers.<Specification<SolicitudHito>>any()))
        .willReturn(Arrays.asList(hitoWithAviso1, hitoWithAviso2, hitoWithoutAviso));
  }

}
