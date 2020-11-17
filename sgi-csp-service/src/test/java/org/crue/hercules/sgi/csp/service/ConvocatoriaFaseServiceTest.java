package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVNEnum;
import org.crue.hercules.sgi.csp.enums.TipoDestinatarioEnum;
import org.crue.hercules.sgi.csp.enums.TipoEstadoConvocatoriaEnum;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaFaseRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaFaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ConvocatoriaFaseServiceTest
 */
@ExtendWith(MockitoExtension.class)

public class ConvocatoriaFaseServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaFaseRepository repository;

  @Mock
  private ConvocatoriaRepository convocatoriaRepository;

  @Mock
  private ModeloTipoFaseRepository modeloTipoFaseRepository;

  private ConvocatoriaFaseService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaFaseServiceImpl(repository, convocatoriaRepository, modeloTipoFaseRepository);
  }

  @Test
  public void create_ReturnsConvocatoriaFase() {
    // given: Un nuevo ConvocatoriaFase
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaFase> page = new PageImpl<>(new ArrayList<ConvocatoriaFase>(), pageable, 0);
          return page;

        });
    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getConvocatoria()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, convocatoriaFase, Boolean.TRUE)));

    BDDMockito.given(repository.save(convocatoriaFase)).will((InvocationOnMock invocation) -> {
      ConvocatoriaFase convocatoriaFaseCreado = invocation.getArgument(0);
      convocatoriaFaseCreado.setId(1L);
      return convocatoriaFaseCreado;
    });

    // when: Creamos el ConvocatoriaFase
    ConvocatoriaFase convocatoriaFaseCreado = service.create(convocatoriaFase);

    // then: El ConvocatoriaFase se crea correctamente
    Assertions.assertThat(convocatoriaFaseCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaFaseCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaFaseCreado.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaFase.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaFaseCreado.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(convocatoriaFase.getFechaInicio());
    Assertions.assertThat(convocatoriaFaseCreado.getFechaFin()).as("getFechaFin()")
        .isEqualTo(convocatoriaFase.getFechaFin());
    Assertions.assertThat(convocatoriaFaseCreado.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(convocatoriaFase.getTipoFase().getId());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaFase que ya tiene id
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);

    // when: Creamos el ConvocatoriaFase
    // then: Lanza una excepcion porque el ConvocatoriaFase ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaFase)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear ConvocatoriaFase");
  }

  @Test
  public void create_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase without ConvocatoriaId
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);
    convocatoriaFase.getConvocatoria().setId(null);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaFase");
  }

  @Test
  public void create_WithNoExistingConvocatoria_ThrowsConvocatoriaNotFoundException() {
    // given: a ConvocatoriaFase with non existing Convocatoria
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void create_WithoutTipoFaseId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase without tipoFaseId
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);
    convocatoriaFase.getTipoFase().setId(null);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as tipoFaseId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Fase no puede ser null para crear ConvocatoriaFase");
  }

  @Test
  public void create_WithoutModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase con TipoFase no asignado al Modelo de Ejecucion de la
    // convocatoria
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getConvocatoria()));
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "TipoFase '%s' no disponible para el ModeloEjecucion '%s'", convocatoriaFase.getTipoFase().getNombre(),
            convocatoriaFase.getConvocatoria().getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithDisabledModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase con la asignación de TipoFase al Modelo de Ejecucion
    // de la convocatoria inactiva
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getConvocatoria()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, convocatoriaFase, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            convocatoriaFase.getTipoFase().getNombre(),
            convocatoriaFase.getConvocatoria().getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithDisabledTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase TipoFase disabled
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);
    convocatoriaFase.getTipoFase().setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getConvocatoria()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, convocatoriaFase, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", convocatoriaFase.getTipoFase().getNombre());
  }

  @Test
  public void create_WithRangoFechasSopalado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase with fechas solapadas con una convocatoria
    // existente
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getConvocatoria()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, convocatoriaFase, Boolean.TRUE)));

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaFase> page = new PageImpl<>(Arrays.asList(generarMockConvocatoriaFase(null)), pageable, 0);
          return page;

        });

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as TipoFase is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe una convocatoria en ese rango de fechas");
  }

  @Test
  public void update_ReturnsConvocatoriaFase() {
    // given: Un nuevo ConvocatoriaFase con el nombre actualizado
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));

    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, convocatoriaFaseActualizado, Boolean.TRUE)));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaFase>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaFase> page = new PageImpl<>(new ArrayList<ConvocatoriaFase>(), pageable, 0);
          return page;

        });

    // when: Actualizamos el ConvocatoriaFase
    ConvocatoriaFase updated = service.update(convocatoriaFaseActualizado);

    // then: El ConvocatoriaFase se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(convocatoriaFase.getId());
    Assertions.assertThat(updated.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaFase.getConvocatoria().getId());
    Assertions.assertThat(updated.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(convocatoriaFaseActualizado.getFechaInicio());
    Assertions.assertThat(updated.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(convocatoriaFaseActualizado.getTipoFase().getId());
  }

  @Test
  public void update_WithIdNotExist_ThrowsConvocatoriaFaseNotFoundException() {
    // given: Un ConvocatoriaFase a actualizar con un id que no existe
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ConvocatoriaFase
    // then: Lanza una excepcion porque el ConvocatoriaFase no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaFase))
        .isInstanceOf(ConvocatoriaFaseNotFoundException.class);
  }

  @Test
  public void update_WithoutModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase con TipoFase no asignado al Modelo de Ejecucion de la
    // convocatoria
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFaseActualizado))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'",
            convocatoriaFaseActualizado.getTipoFase().getNombre(),
            convocatoriaFaseActualizado.getConvocatoria().getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithDisabledModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase con la asignación de TipoFase al Modelo de Ejecucion
    // de la convocatoria inactiva
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));

    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(2L, convocatoriaFaseActualizado, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFaseActualizado))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            convocatoriaFaseActualizado.getTipoFase().getNombre(),
            convocatoriaFaseActualizado.getConvocatoria().getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithDisabledTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase TipoFase disabled
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.FALSE));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));

    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(2L, convocatoriaFaseActualizado, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFaseActualizado))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", convocatoriaFaseActualizado.getTipoFase().getNombre());
  }

  @Test
  public void update_WithRangoFechasSopalado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase with fechas solapadas con una convocatoria
    // existente
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));

    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(2L, convocatoriaFaseActualizado, Boolean.TRUE)));

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaFase> page = new PageImpl<>(Arrays.asList(generarMockConvocatoriaFase(1L)), pageable, 0);
          return page;

        });

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFaseActualizado))
        // then: throw exception as Programa is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe una convocatoria en ese rango de fechas");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ConvocatoriaFase
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaFaseNotFoundException.class);
  }

  @Test
  public void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaFase para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaFase> convocatoriasEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesConvocantes.add(generarMockConvocatoriaFase(Long.valueOf(i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasEntidadesConvocantes.size() ? convocatoriasEntidadesConvocantes.size()
              : toIndex;
          List<ConvocatoriaFase> content = convocatoriasEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ConvocatoriaFase> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaFase> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaFase del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaFase convocatoriaFase = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(convocatoriaFase.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  @Test
  public void findById_ReturnsConvocatoriaFase() {
    // given: Un ConvocatoriaFase con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockConvocatoriaFase(idBuscado)));

    // when: Buscamos el ConvocatoriaFase por su id
    ConvocatoriaFase convocatoriaFase = service.findById(idBuscado);

    // then: el ConvocatoriaFase
    Assertions.assertThat(convocatoriaFase).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaFase.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaFaseNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaFase con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaFase por su id
    // then: lanza un ConvocatoriaFaseNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaFaseNotFoundException.class);
  }

  /**
   * Función que genera Convocatoria
   * 
   * @param convocatoriaId
   * @param unidadGestionId
   * @param modeloEjecucionId
   * @param modeloTipoFinalidadId
   * @param tipoRegimenConcurrenciaId
   * @param tipoAmbitoGeogragicoId
   * @param activo
   * @return la convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long convocatoriaId, Long unidadGestionId, Long modeloEjecucionId,
      Long modeloTipoFinalidadId, Long tipoRegimenConcurrenciaId, Long tipoAmbitoGeogragicoId, Boolean activo) {

    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()//
            .id(modeloEjecucionId)//
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .nombre("nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId))//
            .activo(Boolean.TRUE)//
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .modeloEjecucion(modeloEjecucion)//
            .tipoFinalidad(tipoFinalidad)//
            .activo(Boolean.TRUE)//
            .build();

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()//
            .id(tipoRegimenConcurrenciaId)//
            .nombre("nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()//
            .id(tipoAmbitoGeogragicoId)//
            .nombre("nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId))//
            .activo(Boolean.TRUE)//
            .build();

    Convocatoria convocatoria = Convocatoria.builder()//
        .id(convocatoriaId)//
        .unidadGestionRef((unidadGestionId == null) ? null : "unidad-" + String.format("%03d", unidadGestionId))//
        .modeloEjecucion(modeloEjecucion)//
        .codigo("codigo-" + String.format("%03d", convocatoriaId))//
        .anio(2020)//
        .titulo("titulo-" + String.format("%03d", convocatoriaId))//
        .objeto("objeto-" + String.format("%03d", convocatoriaId))//
        .observaciones("observaciones-" + String.format("%03d", convocatoriaId))//
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())//
        .regimenConcurrencia(tipoRegimenConcurrencia)//
        .destinatarios(TipoDestinatarioEnum.INDIVIDUAL)//
        .colaborativos(Boolean.TRUE)//
        .estadoActual(TipoEstadoConvocatoriaEnum.REGISTRADA)//
        .duracion(12)//
        .ambitoGeografico(tipoAmbitoGeografico)//
        .clasificacionCVN(ClasificacionCVNEnum.AYUDAS)//
        .activo(activo)//
        .build();

    return convocatoria;
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id     id del TipoFase
   * @param activo
   * @return el objeto TipoFase
   */
  private TipoFase generarMockTipoFase(Long id, Boolean activo) {

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre("nombre-" + id);
    tipoFase.setDescripcion("descripcion-" + id);
    tipoFase.setActivo(activo);

    return tipoFase;
  }

  /**
   * Función que genera ModeloTipoFase a partir de un objeto ConvocatoriaFase
   * 
   * @param id
   * @param convocatoriaFase
   * @param activo
   * @return
   */
  private ModeloTipoFase generarMockModeloTipoFase(Long id, ConvocatoriaFase convocatoriaFase, Boolean activo) {

    return ModeloTipoFase.builder()//
        .id(id)//
        .modeloEjecucion(convocatoriaFase.getConvocatoria().getModeloEjecucion())//
        .tipoFase(convocatoriaFase.getTipoFase())//
        .activo(activo)//
        .build();
  }

  /**
   * Función que devuelve un objeto ConvocatoriaFase
   * 
   * @param id id del ConvocatoriaFase
   * @return el objeto ConvocatoriaFase
   */
  private ConvocatoriaFase generarMockConvocatoriaFase(Long id) {

    return ConvocatoriaFase.builder()//
        .id(id)//
        .convocatoria(generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE))//
        .fechaInicio(LocalDate.of(2020, 10, 19))//
        .fechaFin(LocalDate.of(2020, 10, 28))//
        .tipoFase(generarMockTipoFase(1L, Boolean.TRUE))//
        .observaciones("observaciones" + id)//
        .build();
  }

}
