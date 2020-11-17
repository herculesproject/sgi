package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ConvocatoriaPeriodoJustificacionServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ConvocatoriaPeriodoJustificacionServiceTest extends BaseServiceTest {

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
  public void updateConvocatoriaPeriodoJustificacionesConvocatoria_ReturnsConvocatoriaPeriodoJustificacionList() {
    // given: una lista con uno de los ConvocatoriaPeriodoJustificacion actualizado,
    // otro nuevo y sin el otros existente
    Long convocatoriaId = 1L;

    List<ConvocatoriaPeriodoJustificacion> peridosJustificiacionExistentes = new ArrayList<>();
    peridosJustificiacionExistentes
        .add(generarMockConvocatoriaPeriodoJustificacion(2L, 5, 10, TipoJustificacionEnum.PERIODICA, 1L));
    peridosJustificiacionExistentes
        .add(generarMockConvocatoriaPeriodoJustificacion(4L, 11, 15, TipoJustificacionEnum.PERIODICA, 1L));
    peridosJustificiacionExistentes
        .add(generarMockConvocatoriaPeriodoJustificacion(5L, 20, 25, TipoJustificacionEnum.FINAL, 1L));

    ConvocatoriaPeriodoJustificacion newConvocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null, 1, 10, TipoJustificacionEnum.PERIODICA, 1L);
    ConvocatoriaPeriodoJustificacion updatedConvocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        4L, 11, 19, TipoJustificacionEnum.FINAL, 1L);

    List<ConvocatoriaPeriodoJustificacion> peridosJustificiacionActualizar = new ArrayList<>();
    peridosJustificiacionActualizar.add(newConvocatoriaPeriodoJustificacion);
    peridosJustificiacionActualizar.add(updatedConvocatoriaPeriodoJustificacion);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(peridosJustificiacionExistentes);

    BDDMockito.doNothing().when(repository).deleteAll(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>anyList());

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ConvocatoriaPeriodoJustificacion> periodoJustificaciones = invocation.getArgument(0);
          return periodoJustificaciones.stream().map(periodoJustificacion -> {
            if (periodoJustificacion.getId() == null) {
              periodoJustificacion.setId(6L);
            }
            periodoJustificacion.getConvocatoria().setId(convocatoriaId);
            return periodoJustificacion;
          }).collect(Collectors.toList());
        });

    // when: updateConvocatoriaPeriodoJustificacionesConvocatoria
    List<ConvocatoriaPeriodoJustificacion> periodosJustificacionActualizados = service
        .updateConvocatoriaPeriodoJustificacionesConvocatoria(convocatoriaId, peridosJustificiacionActualizar);

    // then: Se crea el nuevo ConvocatoriaPeriodoJustificacion, se actualiza el
    // existe y se elimina el otro
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getConvocatoria().getId())
        .as("get(0).getConvocatoria().getId()").isEqualTo(convocatoriaId);
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getMesInicial()).as("get(0).getMesInicial()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getMesFinal()).as("get(0).getMesFinal()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getFechaInicioPresentacion())
        .as("get(0).getFechaInicioPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getFechaInicioPresentacion());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getFechaFinPresentacion())
        .as("get(0).getFechaFinPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getFechaFinPresentacion());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()")
        .isEqualTo(1);
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getObservaciones());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getTipoJustificacion())
        .as("get(0).getTipoJustificacion()").isEqualTo(newConvocatoriaPeriodoJustificacion.getTipoJustificacion());

    Assertions.assertThat(periodosJustificacionActualizados.get(1).getId()).as("get(1).getId()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getId());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getConvocatoria().getId())
        .as("get(1).getConvocatoria().getId()").isEqualTo(convocatoriaId);
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getMesInicial()).as("get(1).getMesInicial()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getMesFinal()).as("get(1).getMesFinal()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getFechaInicioPresentacion())
        .as("get(1).getFechaInicioPresentacion()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getFechaInicioPresentacion());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getFechaFinPresentacion())
        .as("get(1).getFechaFinPresentacion()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getFechaFinPresentacion());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()")
        .isEqualTo(2);
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getObservaciones()).as("get(1).getObservaciones()")
        .isEqualTo(updatedConvocatoriaPeriodoJustificacion.getObservaciones());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getTipoJustificacion())
        .as("get(1).getTipoJustificacion()").isEqualTo(updatedConvocatoriaPeriodoJustificacion.getTipoJustificacion());

    Mockito.verify(repository, Mockito.times(1))
        .deleteAll(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>anyList());
    Mockito.verify(repository, Mockito.times(1)).saveAll(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>anyList());
  }

  @Test
  public void updateConvocatoriaPeriodoJustificacionesConvocatoria_WithNoExistingConvocatoria_ThrowsConvocatoriaNotFoundException() {
    // given: a ConvocatoriaEntidadGestora with non existing Convocatoria
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadGestora
        () -> service.updateConvocatoriaPeriodoJustificacionesConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoJustificacion)))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void updateConvocatoriaPeriodoJustificacionesConvocatoria_WithIdNotExist_ThrowsConvocatoriaPeriodoJustificacionNotFoundException() {
    // given: Un ConvocatoriaPeriodoJustificacion a actualizar con un id que no
    // existe
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaId(ArgumentMatchers.anyLong())).willReturn(new ArrayList<>());

    // when:updateConvocatoriaPeriodoJustificacionesConvocatoria
    // then: Lanza una excepcion porque el ConvocatoriaPeriodoJustificacion no
    // existe
    Assertions
        .assertThatThrownBy(() -> service.updateConvocatoriaPeriodoJustificacionesConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoJustificacion)))
        .isInstanceOf(ConvocatoriaPeriodoJustificacionNotFoundException.class);
  }

  @Test
  public void updateConvocatoriaPeriodoJustificacionesConvocatoria_WithMesFinalLowerThanMesInicial_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with mesFinal lower than mesInicial
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);
    convocatoriaPeriodoJustificacion.setMesInicial(convocatoriaPeriodoJustificacion.getMesFinal() + 1);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoJustificacionesConvocatoria
        () -> service.updateConvocatoriaPeriodoJustificacionesConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoJustificacion)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El mes final tiene que ser posterior al mes inicial");
  }

  @Test
  public void updateConvocatoriaPeriodoJustificacionesConvocatoria_WithFechaFinBeforeFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with FechaFinPresentacion before
    // FechaInicioPresentacion
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);
    convocatoriaPeriodoJustificacion
        .setFechaInicioPresentacion(convocatoriaPeriodoJustificacion.getFechaFinPresentacion().plusDays(1));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoJustificacionesConvocatoria
        () -> service.updateConvocatoriaPeriodoJustificacionesConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoJustificacion)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin tiene que ser posterior a la fecha de inicio");
  }

  @Test
  public void updateConvocatoriaPeriodoJustificacionesConvocatoria_WithMesFinalGreaterThanDuracionConvocatoria_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with mesFinal greater than duracion
    // convocatoria
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);

    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    convocatoria.setDuracion(convocatoriaPeriodoJustificacion.getMesFinal() - 1);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoJustificacionesConvocatoria
        () -> service.updateConvocatoriaPeriodoJustificacionesConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoJustificacion)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El mes final no puede ser superior a la duración en meses indicada en la Convocatoria");
  }

  @Test
  public void updateConvocatoriaPeriodoJustificacionesConvocatoria_WithMesSolapado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with mesFinal greater than duracion
    // convocatoria
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion1 = generarMockConvocatoriaPeriodoJustificacion(1L,
        1, 10, TipoJustificacionEnum.PERIODICA, 1L);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion2 = generarMockConvocatoriaPeriodoJustificacion(2L,
        8, 15, TipoJustificacionEnum.PERIODICA, 1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoJustificacion1, convocatoriaPeriodoJustificacion2));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoJustificacionesConvocatori
        () -> service.updateConvocatoriaPeriodoJustificacionesConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoJustificacion1, convocatoriaPeriodoJustificacion2)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void updateConvocatoriaPeriodoJustificacionesConvocatori_WithMesesAfterConvocatoriaPeriodoJustificacionFinal_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoJustificacion with mesInicial greater than
    // ConvocatoriaPeriodoJustificacion FINAL
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion1 = generarMockConvocatoriaPeriodoJustificacion(1L,
        1, 5, TipoJustificacionEnum.FINAL, 1L);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion2 = generarMockConvocatoriaPeriodoJustificacion(2L,
        8, 15, TipoJustificacionEnum.PERIODICA, 1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoJustificacion1, convocatoriaPeriodoJustificacion2));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoJustificacionesConvocatoria
        () -> service.updateConvocatoriaPeriodoJustificacionesConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoJustificacion1, convocatoriaPeriodoJustificacion2)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El ConvocatoriaPeriodoJustificacion de tipo final tiene que ser el último");
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
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getMesInicial()).as("getMesInicial()").isEqualTo(1);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getMesFinal()).as("getMesFinal()").isEqualTo(2);
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
    return generarMockConvocatoriaPeriodoJustificacion(id, 1, 2, TipoJustificacionEnum.PERIODICA, id);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoJustificacion
   * 
   * @param id             id del ConvocatoriaPeriodoJustificacion
   * @param mesInicial     Mes inicial
   * @param mesFinal       Mes final
   * @param tipo           Tipo justificacion
   * @param convocatoriaId Id Convocatoria
   * @return el objeto ConvocatoriaPeriodoJustificacion
   */
  private ConvocatoriaPeriodoJustificacion generarMockConvocatoriaPeriodoJustificacion(Long id, Integer mesInicial,
      Integer mesFinal, TipoJustificacionEnum tipo, Long convocatoriaId) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(convocatoriaId == null ? 1 : convocatoriaId);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = new ConvocatoriaPeriodoJustificacion();
    convocatoriaPeriodoJustificacion.setId(id);
    convocatoriaPeriodoJustificacion.setConvocatoria(convocatoria);
    convocatoriaPeriodoJustificacion.setNumPeriodo(1);
    convocatoriaPeriodoJustificacion.setMesInicial(mesInicial);
    convocatoriaPeriodoJustificacion.setMesFinal(mesFinal);
    convocatoriaPeriodoJustificacion.setFechaInicioPresentacion(LocalDate.of(2020, 10, 10));
    convocatoriaPeriodoJustificacion.setFechaFinPresentacion(LocalDate.of(2020, 11, 20));
    convocatoriaPeriodoJustificacion.setObservaciones("observaciones-" + id);
    convocatoriaPeriodoJustificacion.setTipoJustificacion(tipo);

    return convocatoriaPeriodoJustificacion;
  }

  /**
   * Función que devuelve un objeto Convocatoria
   * 
   * @param id id del Convocatoria
   * @return el objeto Convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long id) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    return convocatoria;
  }

}
