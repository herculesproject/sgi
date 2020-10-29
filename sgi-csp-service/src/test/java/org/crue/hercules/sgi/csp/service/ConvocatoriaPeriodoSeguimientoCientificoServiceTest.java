package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPeriodoSeguimientoCientificoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaPeriodoSeguimientoCientificoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class ConvocatoriaPeriodoSeguimientoCientificoServiceTest {

  @Mock
  private ConvocatoriaPeriodoSeguimientoCientificoRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;

  private ConvocatoriaPeriodoSeguimientoCientificoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaPeriodoSeguimientoCientificoServiceImpl(repository, convocatoriaRepository);
  }

  @Test
  public void create_ReturnsConvocatoriaPeriodoSeguimientoCientifico() {
    // given: Existing Periodos from MesInicial 3 to MesFinal 12
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 2, j = 4; i <= 6; i++, j += 2) {
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico//
          .builder()//
          .id(Long.valueOf(i - 1))//
          .convocatoria(convocatoria)//
          .numPeriodo(i - 1)//
          .mesInicial((i * 2) - 1)//
          .mesFinal(j * 1)//
          .build());
    }
    // given: new ConvocatoriaPeriodoSeguimientoCientifico
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico//
        .builder()//
        .convocatoria(convocatoria)//
        .mesInicial(1)//
        .mesFinal(2)//
        .fechaInicioPresentacion(LocalDate.of(2020, 1, 1))//
        .fechaFinPresentacion(LocalDate.of(2020, 2, 1))//
        .observaciones("observaciones")//
        .build();

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(listaConvocatoriaPeriodoSeguimientoCientifico);

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList()))
        .will((InvocationOnMock invocation) -> {
          convocatoriaPeriodoSeguimientoCientifico.setId(1L);
          convocatoriaPeriodoSeguimientoCientifico.setNumPeriodo(1);
          return invocation.getArgument(0);
        });

    // when: create ConvocatoriaPeriodoSeguimientoCientifico
    ConvocatoriaPeriodoSeguimientoCientifico created = service.create(convocatoriaPeriodoSeguimientoCientifico);

    // then: new ConvocatoriaPeriodoSeguimientoCientifico is created
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getNumPeriodo()).isEqualTo(1);
    Assertions.assertThat(created.getMesInicial()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(created.getMesFinal()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(created.getFechaInicioPresentacion())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaInicioPresentacion());
    Assertions.assertThat(created.getFechaFinPresentacion())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion());
    Assertions.assertThat(created.getObservaciones())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getObservaciones());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with id filled
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).build();

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.create(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear ConvocatoriaPeriodoSeguimientoCientifico");
  }

  @Test
  public void create_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico without convocatoria
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().build();

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.create(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as Convocatoria is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Convocatoria no puede ser null en ConvocatoriaPeriodoSeguimientoCientifico");
  }

  @Test
  public void create_WithMesFinalLowerThanMesInicial_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with MesInicial >= MesFinal
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().convocatoria(convocatoria).mesInicial(2).mesFinal(1).build();

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.create(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as mesInicial is >= mesFinal
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El mes inicial debe ser anterior al mes final");
  }

  @Test
  public void create_WithFechaFinBeforeFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with FechaInicio >=
    // FechaFin
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico//
        .builder()//
        .convocatoria(convocatoria)//
        .mesInicial(1)//
        .mesFinal(2)//
        .fechaInicioPresentacion(LocalDate.of(2020, 2, 1))//
        .fechaFinPresentacion(LocalDate.of(2020, 1, 1))//
        .build();

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.create(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as FechaInicio is >= FechaFin
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de inicio debe ser anterior a la fecha de fin");
  }

  @Test
  public void create_WithNoExistingConvocatoria_ThrowsNotFoundException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with no existing
    // Convocatoria
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().convocatoria(convocatoria).mesInicial(1).mesFinal(2).build();

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.create(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void create_WithMesFinalGreaterThanDuracionConvocatoria_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with MesFinal >
    // duracion Convocatoria
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(12).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().convocatoria(convocatoria).mesInicial(1).mesFinal(24).build();

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.create(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as Periodo > duracion Convocatoria
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La duración en meses de la convocatoria es inferior al periodo");
  }

  @Test
  public void create_WithoutDuracionConvocatoria_NotThrowAnyException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with MesFinal >
    // duracion Convocatoria
    Convocatoria convocatoria = Convocatoria.builder().id(1L).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().convocatoria(convocatoria).mesInicial(1).mesFinal(24).build();

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList()))
        .will((InvocationOnMock invocation) -> {
          convocatoriaPeriodoSeguimientoCientifico.setId(1L);
          convocatoriaPeriodoSeguimientoCientifico.setNumPeriodo(1);
          return invocation.getArgument(0);
        });

    Assertions.assertThatCode(
        // when: create ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.create(convocatoriaPeriodoSeguimientoCientifico))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void create_OverlapsExisting_Case1_ThrowsIllegalArgumentException() {
    // given: Existing Periodos from MesInicial 3 to MesFinal 12
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 2, j = 4; i <= 6; i++, j += 2) {
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico//
          .builder()//
          .id(Long.valueOf(i - 1))//
          .convocatoria(convocatoria)//
          .numPeriodo(i - 1)//
          .mesInicial((i * 2) - 1)//
          .mesFinal(j * 1)//
          .build());
    }
    // given: a ConvocatoriaPeriodoSeguimientoCientifico (MesInicial 1 a MesFinal 3)
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().convocatoria(convocatoria).mesInicial(1).mesFinal(3).build();

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(listaConvocatoriaPeriodoSeguimientoCientifico);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.create(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as convocatoria is not found
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void create_OverlapsExisting_Case2_ThrowsIllegalArgumentException() {
    // given: Existing Periodos from MesInicial 3 to MesFinal 12
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 2, j = 4; i <= 6; i++, j += 2) {
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico//
          .builder()//
          .id(Long.valueOf(i - 1))//
          .convocatoria(convocatoria)//
          .numPeriodo(i - 1)//
          .mesInicial((i * 2) - 1)//
          .mesFinal(j * 1)//
          .build());
    }
    // given: a ConvocatoriaPeriodoSeguimientoCientifico (MesInicial 5 a MesFinal 6)
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().convocatoria(convocatoria).mesInicial(5).mesFinal(6).build();

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(listaConvocatoriaPeriodoSeguimientoCientifico);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.create(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as convocatoria is not found
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void create_OverlapsExisting_Case3_ThrowsIllegalArgumentException() {
    // given: Existing Periodos from MesInicial 3 to MesFinal 12
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 2, j = 4; i <= 6; i++, j += 2) {
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico//
          .builder()//
          .id(Long.valueOf(i - 1))//
          .convocatoria(convocatoria)//
          .numPeriodo(i - 1)//
          .mesInicial((i * 2) - 1)//
          .mesFinal(j * 1)//
          .build());
    }
    // given:ConvocatoriaPeriodoSeguimientoCientifico (MesInicial 12 a MesFinal 13)
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().convocatoria(convocatoria).mesInicial(12).mesFinal(13).build();

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(listaConvocatoriaPeriodoSeguimientoCientifico);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.create(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as convocatoria is not found
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void create_OverlapsExisting_Case4_ThrowsIllegalArgumentException() {
    // given: Existing Periodos from MesInicial 3 to MesFinal 12
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 2, j = 4; i <= 6; i++, j += 2) {
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico//
          .builder()//
          .id(Long.valueOf(i - 1))//
          .convocatoria(convocatoria)//
          .numPeriodo(i - 1)//
          .mesInicial((i * 2) - 1)//
          .mesFinal(j * 1)//
          .build());
    }
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().convocatoria(convocatoria).mesInicial(1).mesFinal(13).build();

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(listaConvocatoriaPeriodoSeguimientoCientifico);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.create(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as convocatoria is not found
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void update_WithExistingId_ReturnsConvocatoriaPeriodoSeguimientoCientifico() {
    // given: Existing Periodos from MesInicial 3 to MesFinal 12
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 2, j = 4; i <= 6; i++, j += 2) {
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico//
          .builder()//
          .id(Long.valueOf(i - 1))//
          .convocatoria(convocatoria)//
          .numPeriodo(i - 1)//
          .mesInicial((i * 2) - 1)//
          .mesFinal(j * 1)//
          .build());
    }
    // given: updated ConvocatoriaPeriodoSeguimientoCientifico with id=1 (MesInicial
    // 14 a
    // MesFinal 15)
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder()//
        .id(1L)//
        .convocatoria(convocatoria)//
        .mesInicial(14)//
        .mesFinal(15)//
        .fechaInicioPresentacion(LocalDate.of(2020, 1, 1))//
        .fechaFinPresentacion(LocalDate.of(2020, 2, 1))//
        .observaciones("observaciones")//
        .build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(listaConvocatoriaPeriodoSeguimientoCientifico);

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList()))
        .will((InvocationOnMock invocation) -> {
          convocatoriaPeriodoSeguimientoCientifico.setNumPeriodo(5);
          return invocation.getArgument(0);
        });

    // when: update ConvocatoriaPeriodoSeguimientoCientifico
    ConvocatoriaPeriodoSeguimientoCientifico updated = service.update(convocatoriaPeriodoSeguimientoCientifico);

    // then: ConvocatoriaPeriodoSeguimientoCientifico is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(convocatoria.getId());
    Assertions.assertThat(updated.getNumPeriodo()).isEqualTo(5);
    Assertions.assertThat(updated.getMesInicial()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(updated.getMesFinal()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(updated.getFechaInicioPresentacion())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaInicioPresentacion());
    Assertions.assertThat(updated.getFechaFinPresentacion())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion());
    Assertions.assertThat(updated.getObservaciones())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getObservaciones());
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico without id filled
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().build();

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Id no puede ser null para actualizar ConvocatoriaPeriodoSeguimientoCientifico");
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaPeriodoSeguimientoCientificoNotFoundException.class);
  }

  @Test
  public void update_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico without convocatoria
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as Convocatoria is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Convocatoria no puede ser null en ConvocatoriaPeriodoSeguimientoCientifico");
  }

  @Test
  public void update_WithMesFinalLowerThanMesInicial_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with MesInicial >= MesFinal
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).convocatoria(convocatoria).mesInicial(2).mesFinal(1).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as mesInicial is >= mesFinal
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El mes inicial debe ser anterior al mes final");
  }

  @Test
  public void update_WithFechaFinBeforeFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with FechaInicio >=
    // FechaFin
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico//
        .builder()//
        .id(1L)//
        .convocatoria(convocatoria)//
        .mesInicial(1)//
        .mesFinal(2)//
        .fechaInicioPresentacion(LocalDate.of(2020, 2, 1))//
        .fechaFinPresentacion(LocalDate.of(2020, 1, 1))//
        .build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as FechaInicio is >= FechaFin
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de inicio debe ser anterior a la fecha de fin");
  }

  @Test
  public void update_WithNoExistingConvocatoria_ThrowsNotFoundException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with no existing
    // Convocatoria
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).convocatoria(convocatoria).mesInicial(1).mesFinal(2).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void update_WithMesFinalGreaterThanDuracionConvocatoria_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with MesFinal >
    // duracion Convocatoria
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(12).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).convocatoria(convocatoria).mesInicial(1).mesFinal(24).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as Periodo > duracion Convocatoria
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La duración en meses de la convocatoria es inferior al periodo");
  }

  @Test
  public void update_WithoutDuracionConvocatoria_NotThrowAnyException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with MesFinal >
    // duracion Convocatoria
    Convocatoria convocatoria = Convocatoria.builder().id(1L).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).convocatoria(convocatoria).mesInicial(1).mesFinal(24).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList()))
        .will((InvocationOnMock invocation) -> {
          convocatoriaPeriodoSeguimientoCientifico.setId(1L);
          convocatoriaPeriodoSeguimientoCientifico.setNumPeriodo(1);
          return invocation.getArgument(0);
        });

    Assertions.assertThatCode(
        // when: update ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as Periodo > duracion Convocatoria
        .doesNotThrowAnyException();
  }

  @Test
  public void update_OverlapsExisting_Case1_ThrowsIllegalArgumentException() {
    // given: Existing Periodos from MesInicial 3 to MesFinal 12
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 2, j = 4; i <= 6; i++, j += 2) {
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico//
          .builder()//
          .id(Long.valueOf(i - 1))//
          .convocatoria(convocatoria)//
          .numPeriodo(i - 1)//
          .mesInicial((i * 2) - 1)//
          .mesFinal(j * 1)//
          .build());
    }
    // given: updated ConvocatoriaPeriodoSeguimientoCientifico (MesInicial 1 a
    // MesFinal 5)
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).convocatoria(convocatoria).mesInicial(1).mesFinal(5).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(listaConvocatoriaPeriodoSeguimientoCientifico);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as convocatoria is not found
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void update_OverlapsExisting_Case2_ThrowsIllegalArgumentException() {
    // given: Existing Periodos from MesInicial 3 to MesFinal 12
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 2, j = 4; i <= 6; i++, j += 2) {
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico//
          .builder()//
          .id(Long.valueOf(i - 1))//
          .convocatoria(convocatoria)//
          .numPeriodo(i - 1)//
          .mesInicial((i * 2) - 1)//
          .mesFinal(j * 1)//
          .build());
    }
    // given: a ConvocatoriaPeriodoSeguimientoCientifico (MesInicial 5 a MesFinal 6)
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).convocatoria(convocatoria).mesInicial(5).mesFinal(6).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(listaConvocatoriaPeriodoSeguimientoCientifico);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as convocatoria is not found
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void update_OverlapsExisting_Case3_ThrowsIllegalArgumentException() {
    // given: Existing Periodos from MesInicial 3 to MesFinal 12
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 2, j = 4; i <= 6; i++, j += 2) {
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico//
          .builder()//
          .id(Long.valueOf(i - 1))//
          .convocatoria(convocatoria)//
          .numPeriodo(i - 1)//
          .mesInicial((i * 2) - 1)//
          .mesFinal(j * 1)//
          .build());
    }
    // given:ConvocatoriaPeriodoSeguimientoCientifico (MesInicial 12 a MesFinal 13)
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).convocatoria(convocatoria).mesInicial(12).mesFinal(13).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(listaConvocatoriaPeriodoSeguimientoCientifico);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as convocatoria is not found
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void update_OverlapsExisting_Case4_ThrowsIllegalArgumentException() {
    // given: Existing Periodos from MesInicial 3 to MesFinal 12
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    for (int i = 2, j = 4; i <= 6; i++, j += 2) {
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico//
          .builder()//
          .id(Long.valueOf(i - 1))//
          .convocatoria(convocatoria)//
          .numPeriodo(i - 1)//
          .mesInicial((i * 2) - 1)//
          .mesFinal(j * 1)//
          .build());
    }
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).convocatoria(convocatoria).mesInicial(1).mesFinal(13).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(listaConvocatoriaPeriodoSeguimientoCientifico);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.update(convocatoriaPeriodoSeguimientoCientifico))
        // then: throw exception as convocatoria is not found
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void delete_WithExistingId_NotThrowAnyException() {
    // given: existing ConvocatoriaPeriodoSeguimientoCientifico
    Convocatoria convocatoria = Convocatoria.builder().id(1L).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).convocatoria(convocatoria).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(convocatoriaPeriodoSeguimientoCientifico.getId()))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithoutId_ThrowsNotFoundException() throws Exception {
    // given: no id
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().build();

    Assertions.assertThatThrownBy(
        // when: update non existing ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.delete(convocatoriaPeriodoSeguimientoCientifico.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(IllegalArgumentException.class).hasMessageContaining(
            "ConvocatoriaPeriodoSeguimientoCientifico id no puede ser null para eliminar un ConvocatoriaPeriodoSeguimientoCientifico");
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder().id(1L).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing ConvocatoriaPeriodoSeguimientoCientifico
        () -> service.delete(convocatoriaPeriodoSeguimientoCientifico.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaPeriodoSeguimientoCientificoNotFoundException.class);
  }

  @Test
  public void findById_WithExistingId_ReturnsConvocatoriaPeriodoSeguimientoCientifico() throws Exception {
    // given: existing ConvocatoriaPeriodoSeguimientoCientifico
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder()//
        .id(1L)//
        .convocatoria(convocatoria)//
        .mesInicial(1)//
        .mesFinal(2)//
        .fechaInicioPresentacion(LocalDate.of(2020, 2, 1))//
        .fechaFinPresentacion(LocalDate.of(2020, 1, 1))//
        .build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    // when: find by id ConvocatoriaPeriodoSeguimientoCientifico
    ConvocatoriaPeriodoSeguimientoCientifico data = service.findById(convocatoriaPeriodoSeguimientoCientifico.getId());

    // then: returns ConvocatoriaPeriodoSeguimientoCientifico
    Assertions.assertThat(data).isNotNull();
    Assertions.assertThat(data.getId()).isNotNull();
    Assertions.assertThat(data.getId()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getId());
    Assertions.assertThat(data.getConvocatoria().getId())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getConvocatoria().getId());
    Assertions.assertThat(data.getNumPeriodo()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getNumPeriodo());
    Assertions.assertThat(data.getMesInicial()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(data.getMesFinal()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(data.getFechaInicioPresentacion())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaInicioPresentacion());
    Assertions.assertThat(data.getFechaFinPresentacion())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion());
    Assertions.assertThat(data.getObservaciones())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getObservaciones());

  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaPeriodoSeguimientoCientificoNotFoundException.class);
  }

  @Test
  public void findAllByConvocatoria_WithPaging_ReturnsPage() {
    // given: One hundred ConvocatoriaPeriodoSeguimientoCientifico
    Long convocatoriaId = 1L;
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();
    Convocatoria convocatoria = Convocatoria.builder().id(Long.valueOf(1L)).build();
    for (int i = 1, j = 2; i <= 100; i++, j += 2) {
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico//
          .builder()//
          .id(Long.valueOf(i))//
          .convocatoria(convocatoria)//
          .numPeriodo(i - 1)//
          .mesInicial((i * 2) - 1)//
          .mesFinal(j * 1)//
          .observaciones("observaciones-" + i)//
          .build());
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaPeriodoSeguimientoCientifico>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ConvocatoriaPeriodoSeguimientoCientifico>>() {
          @Override
          public Page<ConvocatoriaPeriodoSeguimientoCientifico> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ConvocatoriaPeriodoSeguimientoCientifico> content = listaConvocatoriaPeriodoSeguimientoCientifico
                .subList(fromIndex, toIndex);
            Page<ConvocatoriaPeriodoSeguimientoCientifico> page = new PageImpl<>(content, pageable,
                listaConvocatoriaPeriodoSeguimientoCientifico.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaPeriodoSeguimientoCientifico> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: A Page with ten ConvocatoriaPeriodoSeguimientoCientifico are returned
    // containing
    // obsrvaciones='observaciones-31' to
    // 'observaciones-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 31; i < 10; i++) {
      ConvocatoriaPeriodoSeguimientoCientifico item = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(item.getId()).isEqualTo(Long.valueOf(i));
    }
  }
}
