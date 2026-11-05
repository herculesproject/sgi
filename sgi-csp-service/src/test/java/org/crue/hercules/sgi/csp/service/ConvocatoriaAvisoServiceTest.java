package org.crue.hercules.sgi.csp.service;

import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;

import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFaseAviso;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHitoAviso;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaFaseRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaHitoRepository;
import org.crue.hercules.sgi.csp.util.AvisoTaskHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.jpa.domain.Specification;

/**
 * ConvocatoriaAvisoServiceTest
 */
class ConvocatoriaAvisoServiceTest extends BaseServiceTest {

  private static final Long CONVOCATORIA_ID = 1L;

  @Mock
  private ConvocatoriaFaseRepository convocatoriaFaseRepository;
  @Mock
  private ConvocatoriaHitoRepository convocatoriaHitoRepository;
  @Mock
  private AvisoTaskHelper avisoTaskHelper;

  private ConvocatoriaAvisoService service;

  @BeforeEach
  void setUp() {
    service = new ConvocatoriaAvisoService(convocatoriaFaseRepository, convocatoriaHitoRepository, avisoTaskHelper);
  }

  @Test
  void disablePendingAvisos_DisablesTasksOfFasesAndHitosAvisos() {
    // given: una fase con dos avisos, una fase sin avisos, un hito con aviso y un
    // hito sin aviso
    givenFasesAndHitosWithAvisos();

    // when: se desactivan los avisos pendientes de la convocatoria
    service.disablePendingAvisos(CONVOCATORIA_ID);

    // then: se desactivan las tareas de todos los avisos de fases e hitos
    verify(avisoTaskHelper).disablePendingTasks(Arrays.asList("11", "12", "21"));
  }

  @Test
  void enablePendingAvisos_EnablesTasksOfFasesAndHitosAvisos() {
    // given: una fase con dos avisos, una fase sin avisos, un hito con aviso y un
    // hito sin aviso
    givenFasesAndHitosWithAvisos();

    // when: se activan los avisos pendientes de la convocatoria
    service.enablePendingAvisos(CONVOCATORIA_ID);

    // then: se activan las tareas de todos los avisos de fases e hitos
    verify(avisoTaskHelper).enablePendingTasks(Arrays.asList("11", "12", "21"));
  }

  @Test
  void disablePendingAvisos_WithoutFasesAndHitos_PassesEmptyList() {
    // given: una convocatoria sin fases ni hitos
    BDDMockito.given(convocatoriaFaseRepository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any()))
        .willReturn(Collections.emptyList());
    BDDMockito.given(convocatoriaHitoRepository.findAll(ArgumentMatchers.<Specification<ConvocatoriaHito>>any()))
        .willReturn(Collections.emptyList());

    // when: se desactivan los avisos pendientes de la convocatoria
    service.disablePendingAvisos(CONVOCATORIA_ID);

    // then: no hay tareas que desactivar
    verify(avisoTaskHelper).disablePendingTasks(Collections.emptyList());
  }

  private void givenFasesAndHitosWithAvisos() {
    ConvocatoriaFase faseWithAvisos = ConvocatoriaFase.builder()
        .id(1L)
        .convocatoriaId(CONVOCATORIA_ID)
        .convocatoriaFaseAviso1(ConvocatoriaFaseAviso.builder().tareaProgramadaRef("11").build())
        .convocatoriaFaseAviso2(ConvocatoriaFaseAviso.builder().tareaProgramadaRef("12").build())
        .build();
    ConvocatoriaFase faseWithoutAvisos = ConvocatoriaFase.builder()
        .id(2L)
        .convocatoriaId(CONVOCATORIA_ID)
        .build();
    ConvocatoriaHito hitoWithAviso = ConvocatoriaHito.builder()
        .id(1L)
        .convocatoriaId(CONVOCATORIA_ID)
        .convocatoriaHitoAviso(ConvocatoriaHitoAviso.builder().tareaProgramadaRef("21").build())
        .build();
    ConvocatoriaHito hitoWithoutAviso = ConvocatoriaHito.builder()
        .id(2L)
        .convocatoriaId(CONVOCATORIA_ID)
        .build();

    BDDMockito.given(convocatoriaFaseRepository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any()))
        .willReturn(Arrays.asList(faseWithAvisos, faseWithoutAvisos));
    BDDMockito.given(convocatoriaHitoRepository.findAll(ArgumentMatchers.<Specification<ConvocatoriaHito>>any()))
        .willReturn(Arrays.asList(hitoWithAviso, hitoWithoutAviso));
  }

}
