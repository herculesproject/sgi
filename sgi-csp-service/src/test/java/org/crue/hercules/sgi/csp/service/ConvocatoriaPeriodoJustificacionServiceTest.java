package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoJustificacionEnum;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaPeriodoJustificacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ConvocatoriaPeriodoJustificacionServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ConvocatoriaPeriodoJustificacionServiceTest {

  @Mock
  private ConvocatoriaPeriodoJustificacionRepository repository;

  @Mock
  private ConvocatoriaRepository convocatoriaRepository;

  private ConvocatoriaPeriodoJustificacionService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaPeriodoJustificacionServiceImpl(repository, convocatoriaRepository);
  }

  @Test
  public void create_ReturnsConvocatoriaPeriodoJustificacion() {
    // given: Un nuevo ConvocatoriaPeriodoJustificacion
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionAnterior = generarMockConvocatoriaPeriodoJustificacion(
        1L);
    convocatoriaPeriodoJustificacionAnterior.setMesInicial(1);
    convocatoriaPeriodoJustificacionAnterior.setMesFinal(2);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion.getConvocatoria()));

    BDDMockito.given(repository.findAllByConvocatoriaIdAndTipoJustificacion(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<TipoJustificacionEnum>any())).willReturn(new ArrayList<>());

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaPeriodoJustificacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaPeriodoJustificacion> page = new PageImpl<>(
              new ArrayList<ConvocatoriaPeriodoJustificacion>(), pageable, 0);
          return page;
        });

    List<ConvocatoriaPeriodoJustificacion> periodosExistentes = new ArrayList<>();
    periodosExistentes.add(convocatoriaPeriodoJustificacionAnterior);
    BDDMockito.given(repository.findAllByConvocatoriaId(ArgumentMatchers.anyLong())).willReturn(periodosExistentes);

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ConvocatoriaPeriodoJustificacion> convocatoriaPeriodoJustificaciones = invocation.getArgument(0);
          List<ConvocatoriaPeriodoJustificacion> convocatoriaPeriodoJustificacionesResponse = new ArrayList<>();
          convocatoriaPeriodoJustificaciones.forEach(periodoJustificacion -> {
            ConvocatoriaPeriodoJustificacion periodoJustificacionResponse = new ConvocatoriaPeriodoJustificacion();
            BeanUtils.copyProperties(periodoJustificacion, periodoJustificacionResponse);
            if (periodoJustificacionResponse.getId() == null) {
              periodoJustificacionResponse.setId(2L);
            }
            convocatoriaPeriodoJustificacionesResponse.add(periodoJustificacionResponse);
          });

          return convocatoriaPeriodoJustificacionesResponse;
        });

    // when: Creamos el ConvocatoriaPeriodoJustificacion
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionCreado = service
        .create(convocatoriaPeriodoJustificacion);

    // then: El ConvocatoriaPeriodoJustificacion se crea correctamente
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getConvocatoria().getId())
        .as("getConvocatoria().getId()").isEqualTo(convocatoriaPeriodoJustificacion.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getMesInicial()).as("getMesInicial()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getMesFinal()).as("getMesFinal()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getFechaInicioPresentacion())
        .as("getFechaInicioPresentacion()").isEqualTo(convocatoriaPeriodoJustificacion.getFechaInicioPresentacion());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getFechaFinPresentacion())
        .as("getFechaFinPresentacion()").isEqualTo(convocatoriaPeriodoJustificacion.getFechaFinPresentacion());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(convocatoriaPeriodoJustificacionAnterior.getNumPeriodo() + 1);
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getObservaciones());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getTipoJustificacion()).as("getTipoJustificacion()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getTipoJustificacion());
  }

  @Test
  public void create_WithPeriodoDisordered_ReturnsConvocatoriaPeriodoJustificacion() {
    // given: Un nuevo ConvocatoriaPeriodoJustificacion
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionAnterior = generarMockConvocatoriaPeriodoJustificacion(
        1L);
    convocatoriaPeriodoJustificacionAnterior.setMesInicial(100);
    convocatoriaPeriodoJustificacionAnterior.setMesFinal(200);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion.getConvocatoria()));

    BDDMockito.given(repository.findAllByConvocatoriaIdAndTipoJustificacion(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<TipoJustificacionEnum>any())).willReturn(new ArrayList<>());

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaPeriodoJustificacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaPeriodoJustificacion> page = new PageImpl<>(
              new ArrayList<ConvocatoriaPeriodoJustificacion>(), pageable, 0);
          return page;
        });

    List<ConvocatoriaPeriodoJustificacion> periodosExistentes = new ArrayList<>();
    periodosExistentes.add(convocatoriaPeriodoJustificacionAnterior);
    BDDMockito.given(repository.findAllByConvocatoriaId(ArgumentMatchers.anyLong())).willReturn(periodosExistentes);

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ConvocatoriaPeriodoJustificacion> convocatoriaPeriodoJustificaciones = invocation.getArgument(0);
          List<ConvocatoriaPeriodoJustificacion> convocatoriaPeriodoJustificacionesResponse = new ArrayList<>();
          convocatoriaPeriodoJustificaciones.forEach(periodoJustificacion -> {
            ConvocatoriaPeriodoJustificacion periodoJustificacionResponse = new ConvocatoriaPeriodoJustificacion();
            BeanUtils.copyProperties(periodoJustificacion, periodoJustificacionResponse);
            if (periodoJustificacionResponse.getId() == null) {
              periodoJustificacionResponse.setId(2L);
            }
            convocatoriaPeriodoJustificacionesResponse.add(periodoJustificacionResponse);
          });

          return convocatoriaPeriodoJustificacionesResponse;
        });

    // when: Creamos el ConvocatoriaPeriodoJustificacion
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionCreado = service
        .create(convocatoriaPeriodoJustificacion);

    // then: El ConvocatoriaPeriodoJustificacion se crea correctamente
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getConvocatoria().getId())
        .as("getConvocatoria().getId()").isEqualTo(convocatoriaPeriodoJustificacion.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getMesInicial()).as("getMesInicial()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getMesFinal()).as("getMesFinal()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getFechaInicioPresentacion())
        .as("getFechaInicioPresentacion()").isEqualTo(convocatoriaPeriodoJustificacion.getFechaInicioPresentacion());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getFechaFinPresentacion())
        .as("getFechaFinPresentacion()").isEqualTo(convocatoriaPeriodoJustificacion.getFechaFinPresentacion());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(convocatoriaPeriodoJustificacionAnterior.getNumPeriodo() - 1);
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getObservaciones());
    Assertions.assertThat(convocatoriaPeriodoJustificacionCreado.getTipoJustificacion()).as("getTipoJustificacion()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getTipoJustificacion());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaPeriodoJustificacion que ya tiene id
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);

    // when: Creamos el ConvocatoriaPeriodoJustificacion
    // then: Lanza una excepcion porque el ConvocatoriaPeriodoJustificacion ya tiene
    // id
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaPeriodoJustificacion))
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "ConvocatoriaPeriodoJustificacion id tiene que ser null para crear un nuevo ConvocatoriaPeriodoJustificacion");
  }

  @Test
  public void create_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion without ConvocatoriaId
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);
    convocatoriaPeriodoJustificacion.getConvocatoria().setId(null);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoJustificacion
        () -> service.create(convocatoriaPeriodoJustificacion))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaPeriodoJustificacion");
  }

  @Test
  public void create_WithMesFinalLowerThanMesInicial_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with mesFinal lower than mesInicial
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);
    convocatoriaPeriodoJustificacion.setMesInicial(convocatoriaPeriodoJustificacion.getMesFinal() + 1);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoJustificacion
        () -> service.create(convocatoriaPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El mes final tiene que ser posterior al mes inicial");
  }

  @Test
  public void create_WithFechaFinBeforeFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with FechaFinPresentacion before
    // FechaInicioPresentacion
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);
    convocatoriaPeriodoJustificacion
        .setFechaInicioPresentacion(convocatoriaPeriodoJustificacion.getFechaFinPresentacion().plusDays(1));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoJustificacion
        () -> service.create(convocatoriaPeriodoJustificacion))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin tiene que ser posterior a la fecha de inicio");
  }

  @Test
  public void create_WithNoExistingConvocatoria_ThrowsConvocatoriaNotFoundException() {
    // given: a ConvocatoriaEntidadGestora with non existing Convocatoria
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadGestora
        () -> service.create(convocatoriaPeriodoJustificacion))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void create_WithMesFinalGreaterThanDuracionConvocatoria_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with mesFinal greater than duracion
    // convocatoria
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);
    convocatoriaPeriodoJustificacion.getConvocatoria().setDuracion(convocatoriaPeriodoJustificacion.getMesFinal() - 1);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion.getConvocatoria()));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoJustificacion
        () -> service.create(convocatoriaPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El mes final no puede ser superior a la duración en meses indicada en la Convocatoria");
  }

  @Test
  public void create_WithConvocatoriaPeriodoJustificacionFinal_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with
    // a ConvocatoriaPeriodoJustificacion Final
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);
    convocatoriaPeriodoJustificacion.setTipoJustificacion(TipoJustificacionEnum.FINAL);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionFinal = generarMockConvocatoriaPeriodoJustificacion(
        null);
    convocatoriaPeriodoJustificacionFinal.setTipoJustificacion(TipoJustificacionEnum.FINAL);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion.getConvocatoria()));

    BDDMockito
        .given(repository.findAllByConvocatoriaIdAndTipoJustificacion(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<TipoJustificacionEnum>any()))
        .willReturn(Arrays.asList(convocatoriaPeriodoJustificacionFinal));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoJustificacion
        () -> service.create(convocatoriaPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La convocatoria ya tiene un ConvocatoriaPeriodoJustificacion de tipo final");
  }

  @Test
  public void create_WithConvocatoriaPeriodoJustificacionPeriodoAfterFinal_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with
    // a ConvocatoriaPeriodoJustificacion Final
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);
    convocatoriaPeriodoJustificacion.setTipoJustificacion(TipoJustificacionEnum.PERIODICA);
    convocatoriaPeriodoJustificacion.setMesInicial(6);
    convocatoriaPeriodoJustificacion.setMesFinal(8);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionFinal = generarMockConvocatoriaPeriodoJustificacion(
        null);
    convocatoriaPeriodoJustificacionFinal.setTipoJustificacion(TipoJustificacionEnum.FINAL);
    convocatoriaPeriodoJustificacionFinal.setMesInicial(3);
    convocatoriaPeriodoJustificacionFinal.setMesFinal(5);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion.getConvocatoria()));

    BDDMockito
        .given(repository.findAllByConvocatoriaIdAndTipoJustificacion(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<TipoJustificacionEnum>any()))
        .willReturn(Arrays.asList(convocatoriaPeriodoJustificacionFinal));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoJustificacion
        () -> service.create(convocatoriaPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El ConvocatoriaPeriodoJustificacion de tipo final tiene que ser el último");
  }

  @Test
  public void create_WithPeriodoSolapado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with mesFinal greater than duracion
    // convocatoria
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionPrevio = generarMockConvocatoriaPeriodoJustificacion(
        1L);
    convocatoriaPeriodoJustificacion.setMesInicial(convocatoriaPeriodoJustificacionPrevio.getMesFinal() - 1);
    convocatoriaPeriodoJustificacion.setMesFinal(convocatoriaPeriodoJustificacion.getMesInicial() + 1);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion.getConvocatoria()));

    BDDMockito.given(repository.findAllByConvocatoriaIdAndTipoJustificacion(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<TipoJustificacionEnum>any())).willReturn(new ArrayList<>());

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaPeriodoJustificacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaPeriodoJustificacion> page = new PageImpl<>(
              Arrays.asList(convocatoriaPeriodoJustificacionPrevio), pageable, 0);
          return page;
        });

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaPeriodoJustificacion
        () -> service.create(convocatoriaPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void update_ReturnsConvocatoriaPeriodoJustificacion() {
    // given: Un nuevo ConvocatoriaPeriodoJustificacion con las observaciones y los
    // meses actualizados para que sea el primero
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(2L);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionObservacionesActualizado = generarMockConvocatoriaPeriodoJustificacion(
        2L);
    convocatoriaPeriodoJustificacionObservacionesActualizado.setObservaciones("observaciones-actualizadas");
    convocatoriaPeriodoJustificacionObservacionesActualizado.setMesInicial(30);
    convocatoriaPeriodoJustificacionObservacionesActualizado.setMesFinal(40);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionAnterior = generarMockConvocatoriaPeriodoJustificacion(
        1L);
    convocatoriaPeriodoJustificacionAnterior.setMesInicial(50);
    convocatoriaPeriodoJustificacionAnterior.setMesFinal(60);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionSiguiente = generarMockConvocatoriaPeriodoJustificacion(
        3L);
    convocatoriaPeriodoJustificacionSiguiente.setMesInicial(100);
    convocatoriaPeriodoJustificacionSiguiente.setMesFinal(200);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion.getConvocatoria()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaPeriodoJustificacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaPeriodoJustificacion> page = new PageImpl<>(
              new ArrayList<ConvocatoriaPeriodoJustificacion>(), pageable, 0);
          return page;
        });

    BDDMockito.given(repository.findAllByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoJustificacionAnterior,
            convocatoriaPeriodoJustificacionObservacionesActualizado, convocatoriaPeriodoJustificacionSiguiente));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaPeriodoJustificacion
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionActualizado = service
        .update(convocatoriaPeriodoJustificacionObservacionesActualizado);

    // then: El ConvocatoriaPeriodoJustificacion se actualiza correctamente.
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getId()).as("getId()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getId());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getConvocatoria().getId())
        .as("getConvocatoria().getId()").isEqualTo(convocatoriaPeriodoJustificacion.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getMesInicial()).as("getMesInicial()")
        .isEqualTo(convocatoriaPeriodoJustificacionObservacionesActualizado.getMesInicial());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getMesFinal()).as("getMesFinal()")
        .isEqualTo(convocatoriaPeriodoJustificacionObservacionesActualizado.getMesFinal());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getFechaInicioPresentacion())
        .as("getFechaInicioPresentacion()").isEqualTo(convocatoriaPeriodoJustificacion.getFechaInicioPresentacion());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getFechaFinPresentacion())
        .as("getFechaFinPresentacion()").isEqualTo(convocatoriaPeriodoJustificacion.getFechaFinPresentacion());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(1);
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoriaPeriodoJustificacionObservacionesActualizado.getObservaciones());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getTipoJustificacion())
        .as("getTipoJustificacion()").isEqualTo(convocatoriaPeriodoJustificacion.getTipoJustificacion());

    Mockito.verify(repository, Mockito.times(1)).saveAll(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>anyList());
  }

  @Test
  public void update_WithoutConvocatoriaPeriodoJustificacionId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion without ConvocatoriaId
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoJustificacion
        () -> service.update(convocatoriaPeriodoJustificacion))
        // then: throw exception as ConvocatoriaPeriodoJustificacionId is null
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "ConvocatoriaPeriodoJustificacion id no puede ser null para actualizar un ConvocatoriaPeriodoJustificacion");
  }

  @Test
  public void update_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion without ConvocatoriaId
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);
    convocatoriaPeriodoJustificacion.getConvocatoria().setId(null);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoJustificacion
        () -> service.update(convocatoriaPeriodoJustificacion))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaPeriodoJustificacion");
  }

  @Test
  public void update_WithMesFinalLowerThanMesInicial_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with mesFinal lower than mesInicial
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);
    convocatoriaPeriodoJustificacion.setMesInicial(convocatoriaPeriodoJustificacion.getMesFinal() + 1);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoJustificacion
        () -> service.update(convocatoriaPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El mes final tiene que ser posterior al mes inicial");
  }

  @Test
  public void update_WithFechaFinBeforeFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with FechaFinPresentacion before
    // FechaInicioPresentacion
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);
    convocatoriaPeriodoJustificacion
        .setFechaInicioPresentacion(convocatoriaPeriodoJustificacion.getFechaFinPresentacion().plusDays(1));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoJustificacion
        () -> service.update(convocatoriaPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin tiene que ser posterior a la fecha de inicio");
  }

  @Test
  public void update_WithIdNotExist_ThrowsConvocatoriaPeriodoJustificacionNotFoundException() {
    // given: Un ConvocatoriaPeriodoJustificacion a actualizar con un id que no
    // existe
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion.getConvocatoria()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ConvocatoriaPeriodoJustificacion
    // then: Lanza una excepcion porque el ConvocatoriaPeriodoJustificacion no
    // existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaPeriodoJustificacion))
        .isInstanceOf(ConvocatoriaPeriodoJustificacionNotFoundException.class);
  }

  @Test
  public void update_WithNoExistingConvocatoria_ThrowsConvocatoriaNotFoundException() {
    // given: a ConvocatoriaEntidadGestora with non existing Convocatoria
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadGestora
        () -> service.update(convocatoriaPeriodoJustificacion))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void update_WithMesFinalGreaterThanDuracionConvocatoria_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with mesFinal greater than duracion
    // convocatoria
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);
    convocatoriaPeriodoJustificacion.getConvocatoria().setDuracion(convocatoriaPeriodoJustificacion.getMesFinal() - 1);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion.getConvocatoria()));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoJustificacion
        () -> service.update(convocatoriaPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El mes final no puede ser superior a la duración en meses indicada en la Convocatoria");
  }

  @Test
  public void update_WithMesSolapado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with mesFinal greater than duracion
    // convocatoria
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionActualizado = generarMockConvocatoriaPeriodoJustificacion(
        1L);
    convocatoriaPeriodoJustificacionActualizado.setMesInicial(13);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion.getConvocatoria()));

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaPeriodoJustificacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaPeriodoJustificacion> page = new PageImpl<>(
              Arrays.asList(generarMockConvocatoriaPeriodoJustificacion(2L)), pageable, 0);
          return page;
        });

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoJustificacion
        () -> service.update(convocatoriaPeriodoJustificacionActualizado))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void update_With_MesesAfterConvocatoriaPeriodoJustificacionFinal_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with mesInicial greater than
    // ConvocatoriaPeriodoJustificacion FINAL
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(2L);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionActualizado = generarMockConvocatoriaPeriodoJustificacion(
        2L);
    convocatoriaPeriodoJustificacionActualizado.setObservaciones("observaciones-actualizadas");
    convocatoriaPeriodoJustificacionActualizado.setMesInicial(130);
    convocatoriaPeriodoJustificacionActualizado.setMesFinal(140);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionAnterior = generarMockConvocatoriaPeriodoJustificacion(
        1L);
    convocatoriaPeriodoJustificacionAnterior.setMesInicial(50);
    convocatoriaPeriodoJustificacionAnterior.setMesFinal(60);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionSiguiente = generarMockConvocatoriaPeriodoJustificacion(
        3L);
    convocatoriaPeriodoJustificacionSiguiente.setMesInicial(100);
    convocatoriaPeriodoJustificacionSiguiente.setMesFinal(200);
    convocatoriaPeriodoJustificacionSiguiente.setTipoJustificacion(TipoJustificacionEnum.FINAL);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion.getConvocatoria()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaPeriodoJustificacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaPeriodoJustificacion> page = new PageImpl<>(
              new ArrayList<ConvocatoriaPeriodoJustificacion>(), pageable, 0);
          return page;
        });

    BDDMockito.given(repository.findAllByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoJustificacionAnterior, convocatoriaPeriodoJustificacionActualizado,
            convocatoriaPeriodoJustificacionSiguiente));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaPeriodoJustificacion
        () -> service.update(convocatoriaPeriodoJustificacionActualizado))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El ConvocatoriaPeriodoJustificacion de tipo final tiene que ser el último");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ConvocatoriaPeriodoJustificacion
    Long id = 1L;
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion1 = generarMockConvocatoriaPeriodoJustificacion(
        1L);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion2 = generarMockConvocatoriaPeriodoJustificacion(
        2L);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion3 = generarMockConvocatoriaPeriodoJustificacion(
        3L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoJustificacion1));
    BDDMockito
        .given(repository.findAllByConvocatoriaIdAndNumPeriodoGreaterThan(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyInt()))
        .willReturn(Arrays.asList(convocatoriaPeriodoJustificacion2, convocatoriaPeriodoJustificacion3));

    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();

    Mockito.verify(repository, Mockito.times(1)).deleteById(ArgumentMatchers.anyLong());
    Mockito.verify(repository, Mockito.times(1)).saveAll(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>anyList());
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaPeriodoJustificacionNotFoundException.class);
  }

  @Test
  public void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaEntidadGestora para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaPeriodoJustificacion> convocatoriasEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesConvocantes.add(generarMockConvocatoriaPeriodoJustificacion(i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaPeriodoJustificacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasEntidadesConvocantes.size() ? convocatoriasEntidadesConvocantes.size()
              : toIndex;
          List<ConvocatoriaPeriodoJustificacion> content = convocatoriasEntidadesConvocantes.subList(fromIndex,
              toIndex);
          Page<ConvocatoriaPeriodoJustificacion> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaPeriodoJustificacion> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaPeriodoJustificacion del 31 al
    // 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(convocatoriaPeriodoJustificacion.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(convocatoriaPeriodoJustificacion.getObservaciones()).isEqualTo("observaciones-" + i);
    }
  }

  @Test
  public void findById_ReturnsConvocatoriaPeriodoJustificacion() {
    // given: Un ConvocatoriaPeriodoJustificacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockConvocatoriaPeriodoJustificacion(idBuscado)));

    // when: Buscamos el ConvocatoriaPeriodoJustificacion por su id
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = service.findById(idBuscado);

    // then: el ConvocatoriaPeriodoJustificacion
    Assertions.assertThat(convocatoriaPeriodoJustificacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getMesInicial()).as("getMesInicial()").isEqualTo(10);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getMesFinal()).as("getMesFinal()").isEqualTo(20);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getFechaInicioPresentacion())
        .as("getFechaInicioPresentacion()").isEqualTo(LocalDate.of(2020, 10, 10));
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getFechaFinPresentacion()).as("getFechaFinPresentacion()")
        .isEqualTo(LocalDate.of(2020, 11, 20));
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones-1");
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getTipoJustificacion()).as("getTipoJustificacion()")
        .isEqualTo(TipoJustificacionEnum.PERIODICA);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaPeriodoJustificacionNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaPeriodoJustificacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaPeriodoJustificacion por su id
    // then: lanza un ConvocatoriaPeriodoJustificacionNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaPeriodoJustificacionNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoJustificacion
   * 
   * @param id id del ConvocatoriaPeriodoJustificacion
   * @return el objeto ConvocatoriaPeriodoJustificacion
   */
  private ConvocatoriaPeriodoJustificacion generarMockConvocatoriaPeriodoJustificacion(Long id) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = new ConvocatoriaPeriodoJustificacion();
    convocatoriaPeriodoJustificacion.setId(id);
    convocatoriaPeriodoJustificacion.setConvocatoria(convocatoria);
    convocatoriaPeriodoJustificacion.setNumPeriodo(id == null ? 1 : id.intValue());
    convocatoriaPeriodoJustificacion.setMesInicial(10);
    convocatoriaPeriodoJustificacion.setMesFinal(20);
    convocatoriaPeriodoJustificacion.setFechaInicioPresentacion(LocalDate.of(2020, 10, 10));
    convocatoriaPeriodoJustificacion.setFechaFinPresentacion(LocalDate.of(2020, 11, 20));
    convocatoriaPeriodoJustificacion.setObservaciones("observaciones-" + id);
    convocatoriaPeriodoJustificacion.setTipoJustificacion(TipoJustificacionEnum.PERIODICA);

    return convocatoriaPeriodoJustificacion;
  }

}
