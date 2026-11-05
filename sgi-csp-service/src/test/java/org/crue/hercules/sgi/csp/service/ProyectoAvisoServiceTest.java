package org.crue.hercules.sgi.csp.service;

import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;

import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoFaseAviso;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoHitoAviso;
import org.crue.hercules.sgi.csp.repository.ProyectoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoHitoRepository;
import org.crue.hercules.sgi.csp.util.AvisoTaskHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProyectoAvisoServiceTest
 */
class ProyectoAvisoServiceTest extends BaseServiceTest {

  private static final Long PROYECTO_ID = 1L;

  @Mock
  private ProyectoFaseRepository proyectoFaseRepository;
  @Mock
  private ProyectoHitoRepository proyectoHitoRepository;
  @Mock
  private AvisoTaskHelper avisoTaskHelper;

  private ProyectoAvisoService service;

  @BeforeEach
  void setUp() {
    service = new ProyectoAvisoService(proyectoFaseRepository, proyectoHitoRepository, avisoTaskHelper);
  }

  @Test
  void disablePendingAvisos_DisablesTasksOfFasesAndHitosAvisos() {
    // given: una fase con dos avisos, una fase sin avisos, un hito con aviso y un
    // hito sin aviso
    givenFasesAndHitosWithAvisos();

    // when: se desactivan los avisos pendientes del proyecto
    service.disablePendingAvisos(PROYECTO_ID);

    // then: se desactivan las tareas de todos los avisos de fases e hitos
    verify(avisoTaskHelper).disablePendingTasks(Arrays.asList("11", "12", "21"));
  }

  @Test
  void enablePendingAvisos_EnablesTasksOfFasesAndHitosAvisos() {
    // given: una fase con dos avisos, una fase sin avisos, un hito con aviso y un
    // hito sin aviso
    givenFasesAndHitosWithAvisos();

    // when: se activan los avisos pendientes del proyecto
    service.enablePendingAvisos(PROYECTO_ID);

    // then: se activan las tareas de todos los avisos de fases e hitos
    verify(avisoTaskHelper).enablePendingTasks(Arrays.asList("11", "12", "21"));
  }

  @Test
  void disablePendingAvisos_WithoutFasesAndHitos_PassesEmptyList() {
    // given: un proyecto sin fases ni hitos
    BDDMockito.given(proyectoFaseRepository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any()))
        .willReturn(Collections.emptyList());
    BDDMockito.given(proyectoHitoRepository.findAll(ArgumentMatchers.<Specification<ProyectoHito>>any()))
        .willReturn(Collections.emptyList());

    // when: se desactivan los avisos pendientes del proyecto
    service.disablePendingAvisos(PROYECTO_ID);

    // then: no hay tareas que desactivar
    verify(avisoTaskHelper).disablePendingTasks(Collections.emptyList());
  }

  private void givenFasesAndHitosWithAvisos() {
    ProyectoFase faseWithAvisos = ProyectoFase.builder()
        .id(1L)
        .proyectoId(PROYECTO_ID)
        .proyectoFaseAviso1(ProyectoFaseAviso.builder().tareaProgramadaRef("11").build())
        .proyectoFaseAviso2(ProyectoFaseAviso.builder().tareaProgramadaRef("12").build())
        .build();
    ProyectoFase faseWithoutAvisos = ProyectoFase.builder()
        .id(2L)
        .proyectoId(PROYECTO_ID)
        .build();
    ProyectoHito hitoWithAviso = ProyectoHito.builder()
        .id(1L)
        .proyectoId(PROYECTO_ID)
        .proyectoHitoAviso(ProyectoHitoAviso.builder().tareaProgramadaRef("21").build())
        .build();
    ProyectoHito hitoWithoutAviso = ProyectoHito.builder()
        .id(2L)
        .proyectoId(PROYECTO_ID)
        .build();

    BDDMockito.given(proyectoFaseRepository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any()))
        .willReturn(Arrays.asList(faseWithAvisos, faseWithoutAvisos));
    BDDMockito.given(proyectoHitoRepository.findAll(ArgumentMatchers.<Specification<ProyectoHito>>any()))
        .willReturn(Arrays.asList(hitoWithAviso, hitoWithoutAviso));
  }

}
