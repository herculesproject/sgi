package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class ConvocatoriaPeriodoSeguimientoCientificoServiceTest extends BaseServiceTest {

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
  public void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_ReturnsConvocatoriaPeriodoSeguimientoCientificoList() {
    // given: una lista con uno de los ConvocatoriaPeriodoSeguimientoCientifico
    // actualizado, otro nuevo y sin el otros existente
    Long convocatoriaId = 1L;

    List<ConvocatoriaPeriodoSeguimientoCientifico> peridosJustificiacionExistentes = new ArrayList<>();
    peridosJustificiacionExistentes.add(generarMockConvocatoriaPeriodoSeguimientoCientifico(2L, 5, 10, 1L));
    peridosJustificiacionExistentes.add(generarMockConvocatoriaPeriodoSeguimientoCientifico(4L, 11, 15, 1L));
    peridosJustificiacionExistentes.add(generarMockConvocatoriaPeriodoSeguimientoCientifico(5L, 20, 25, 1L));

    ConvocatoriaPeriodoSeguimientoCientifico newConvocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        null, 1, 10, 1L);
    ConvocatoriaPeriodoSeguimientoCientifico updatedConvocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        4L, 11, 19, 1L);

    List<ConvocatoriaPeriodoSeguimientoCientifico> peridosJustificiacionActualizar = new ArrayList<>();
    peridosJustificiacionActualizar.add(newConvocatoriaPeriodoSeguimientoCientifico);
    peridosJustificiacionActualizar.add(updatedConvocatoriaPeriodoSeguimientoCientifico);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(peridosJustificiacionExistentes);

    BDDMockito.doNothing().when(repository)
        .deleteAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList());

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ConvocatoriaPeriodoSeguimientoCientifico> periodoSeguimientoCientificos = invocation.getArgument(0);
          return periodoSeguimientoCientificos.stream().map(periodoSeguimientoCientifico -> {
            if (periodoSeguimientoCientifico.getId() == null) {
              periodoSeguimientoCientifico.setId(6L);
            }
            periodoSeguimientoCientifico.getConvocatoria().setId(convocatoriaId);
            return periodoSeguimientoCientifico;
          }).collect(Collectors.toList());
        });

    // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
    List<ConvocatoriaPeriodoSeguimientoCientifico> periodosSeguimientoCientificoActualizados = service
        .updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId, peridosJustificiacionActualizar);

    // then: Se crea el nuevo ConvocatoriaPeriodoSeguimientoCientifico, se actualiza
    // el existe y se elimina el otro
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getConvocatoria().getId())
        .as("get(0).getConvocatoria().getId()").isEqualTo(convocatoriaId);
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getMesInicial()).as("get(0).getMesInicial()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getMesFinal()).as("get(0).getMesFinal()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getFechaInicioPresentacion())
        .as("get(0).getFechaInicioPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getFechaInicioPresentacion());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getFechaFinPresentacion())
        .as("get(0).getFechaFinPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()")
        .isEqualTo(1);
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getObservaciones())
        .as("get(0).getObservaciones()").isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getObservaciones());

    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getId()).as("get(1).getId()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getId());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getConvocatoria().getId())
        .as("get(1).getConvocatoria().getId()").isEqualTo(convocatoriaId);
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getMesInicial()).as("get(1).getMesInicial()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getMesFinal()).as("get(1).getMesFinal()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getFechaInicioPresentacion())
        .as("get(1).getFechaInicioPresentacion()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getFechaInicioPresentacion());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getFechaFinPresentacion())
        .as("get(1).getFechaFinPresentacion()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()")
        .isEqualTo(2);
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getObservaciones())
        .as("get(1).getObservaciones()").isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getObservaciones());

    Mockito.verify(repository, Mockito.times(1))
        .deleteAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList());
    Mockito.verify(repository, Mockito.times(1))
        .saveAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList());
  }

  @Test
  public void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithNoExistingConvocatoria_ThrowsConvocatoriaNotFoundException() {
    // given: a ConvocatoriaEntidadGestora with non existing Convocatoria
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadGestora
        () -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico)))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithIdNotExist_ThrowsConvocatoriaPeriodoSeguimientoCientificoNotFoundException() {
    // given: Un ConvocatoriaPeriodoSeguimientoCientifico a actualizar con un id que
    // no existe
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(new ArrayList<>());

    // when:updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
    // then: Lanza una excepcion porque el ConvocatoriaPeriodoSeguimientoCientifico
    // no existe
    Assertions
        .assertThatThrownBy(() -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico)))
        .isInstanceOf(ConvocatoriaPeriodoSeguimientoCientificoNotFoundException.class);
  }

  @Test
  public void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithMesFinalLowerThanMesInicial_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with mesFinal lower than
    // mesInicial
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L);
    convocatoriaPeriodoSeguimientoCientifico.setMesInicial(convocatoriaPeriodoSeguimientoCientifico.getMesFinal() + 1);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoSeguimientoCientifico));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
        () -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El mes final tiene que ser posterior al mes inicial");
  }

  @Test
  public void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithFechaFinBeforeFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with FechaFinPresentacion
    // before FechaInicioPresentacion
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L);
    convocatoriaPeriodoSeguimientoCientifico
        .setFechaInicioPresentacion(convocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion().plusDays(1));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoSeguimientoCientifico));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
        () -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin tiene que ser posterior a la fecha de inicio");
  }

  @Test
  public void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithMesFinalGreaterThanDuracionConvocatoria_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with mesFinal greater than
    // duracion convocatoria
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L);

    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    convocatoria.setDuracion(convocatoriaPeriodoSeguimientoCientifico.getMesFinal() - 1);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoSeguimientoCientifico));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
        () -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El mes final no puede ser superior a la duración en meses indicada en la Convocatoria");
  }

  @Test
  public void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithMesSolapado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with mesFinal greater than
    // duracion convocatoria
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico1 = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L, 1, 10, 1L);
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico2 = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        2L, 8, 15, 1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong())).willReturn(
        Arrays.asList(convocatoriaPeriodoSeguimientoCientifico1, convocatoriaPeriodoSeguimientoCientifico2));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
        () -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico1, convocatoriaPeriodoSeguimientoCientifico2)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
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

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoSeguimientoCientifico
   * 
   * @param id id del ConvocatoriaPeriodoSeguimientoCientifico
   * @return el objeto ConvocatoriaPeriodoSeguimientoCientifico
   */
  private ConvocatoriaPeriodoSeguimientoCientifico generarMockConvocatoriaPeriodoSeguimientoCientifico(Long id) {
    return generarMockConvocatoriaPeriodoSeguimientoCientifico(id, 1, 2, id);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoSeguimientoCientifico
   * 
   * @param id             id del ConvocatoriaPeriodoSeguimientoCientifico
   * @param mesInicial     Mes inicial
   * @param mesFinal       Mes final
   * @param tipo           Tipo SeguimientoCientifico
   * @param convocatoriaId Id Convocatoria
   * @return el objeto ConvocatoriaPeriodoSeguimientoCientifico
   */
  private ConvocatoriaPeriodoSeguimientoCientifico generarMockConvocatoriaPeriodoSeguimientoCientifico(Long id,
      Integer mesInicial, Integer mesFinal, Long convocatoriaId) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(convocatoriaId == null ? 1 : convocatoriaId);

    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = new ConvocatoriaPeriodoSeguimientoCientifico();
    convocatoriaPeriodoSeguimientoCientifico.setId(id);
    convocatoriaPeriodoSeguimientoCientifico.setConvocatoria(convocatoria);
    convocatoriaPeriodoSeguimientoCientifico.setNumPeriodo(1);
    convocatoriaPeriodoSeguimientoCientifico.setMesInicial(mesInicial);
    convocatoriaPeriodoSeguimientoCientifico.setMesFinal(mesFinal);
    convocatoriaPeriodoSeguimientoCientifico.setFechaInicioPresentacion(LocalDate.of(2020, 10, 10));
    convocatoriaPeriodoSeguimientoCientifico.setFechaFinPresentacion(LocalDate.of(2020, 11, 20));
    convocatoriaPeriodoSeguimientoCientifico.setObservaciones("observaciones-" + id);

    return convocatoriaPeriodoSeguimientoCientifico;
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
