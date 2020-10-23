package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaFaseRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.TipoFaseRepository;
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

public class ConvocatoriaFaseServiceTest {

  @Mock
  private ConvocatoriaFaseRepository repository;

  @Mock
  private ConvocatoriaRepository convocatoriaRepository;

  @Mock
  private TipoFaseRepository tipoFaseRepository;

  private ConvocatoriaFaseService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaFaseServiceImpl(repository, convocatoriaRepository, tipoFaseRepository);
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
    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getTipoFase()));

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
    convocatoriaFase.getTipoFase().setActivo(true);

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
    convocatoriaFase.setTipoFase(TipoFase.builder().build());

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
    convocatoriaFase.getTipoFase().setId(1L);
    convocatoriaFase.getTipoFase().setActivo(true);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void create_WithNoExistingTipoFase_404() {
    // given: a ConvocatoriaEnlace with non existing TipoFase
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getConvocatoria()));
    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEnlace
        () -> service.create(convocatoriaFase))
        // then: throw exception as TipoFase is not found
        .isInstanceOf(TipoFaseNotFoundException.class);
  }

  @Test
  public void create_WithTipoFaseActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase with non existing TipoFase
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);
    convocatoriaFase.getTipoFase().setActivo(false);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getConvocatoria()));

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getTipoFase()));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as TipoFase is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoFase debe estar activo");
  }

  @Test
  public void create_WithRangoFechasSopalado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase with fechas solapadas con una convocatoria
    // existente
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getConvocatoria()));

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getTipoFase()));

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaFase> page = new PageImpl<>(Arrays.asList(generarMockConvocatoriaFase(1L)), pageable, 0);
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
    ConvocatoriaFase convocatoriaFaseTipoFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseTipoFaseActualizado.setTipoFase(new TipoFase(1L, "tipoFase1", "descripcion1", Boolean.TRUE));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFase.getTipoFase()));

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
    ConvocatoriaFase convocatoriaFaseActualizado = service.update(convocatoriaFaseTipoFaseActualizado);

    // then: El ConvocatoriaFase se actualiza correctamente.
    Assertions.assertThat(convocatoriaFaseActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaFaseActualizado.getId()).as("getId()").isEqualTo(convocatoriaFase.getId());
    Assertions.assertThat(convocatoriaFaseActualizado.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaFase.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaFaseActualizado.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(convocatoriaFase.getFechaInicio());
    Assertions.assertThat(convocatoriaFaseTipoFaseActualizado.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(convocatoriaFase.getTipoFase().getId());
  }

  @Test
  public void update_WithIdNotExist_ThrowsConvocatoriaFaseNotFoundException() {
    // given: Un ConvocatoriaFase a actualizar con un id que no existe
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaFase.getTipoFase()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ConvocatoriaFase
    // then: Lanza una excepcion porque el ConvocatoriaFase no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaFase))
        .isInstanceOf(ConvocatoriaFaseNotFoundException.class);
  }

  @Test
  public void update_WithNoExistingTipoFase_404() {
    // given: a ConvocatoriaFase with non existing TipoFase
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFase))
        // then: throw exception as TipoFase is not found
        .isInstanceOf(TipoFaseNotFoundException.class);
  }

  @Test
  public void update_WithTipoFaseActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase with TipoFaseActivo False
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);

    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.getTipoFase().setId(2L);
    convocatoriaFaseActualizado.getTipoFase().setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFaseActualizado.getTipoFase()));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFaseActualizado))
        // then: throw exception as Programa is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoFase debe estar activo");
  }

  @Test
  public void update_WithRangoFechasSopalado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase with fechas solapadas con una convocatoria
    // existente
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);

    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaFaseActualizado.getTipoFase()));

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
      convocatoriasEntidadesConvocantes.add(generarMockConvocatoriaFase(i));
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
   * Función que devuelve un objeto ConvocatoriaFase
   * 
   * @param id id del ConvocatoriaFase
   * @return el objeto ConvocatoriaFase
   */
  private ConvocatoriaFase generarMockConvocatoriaFase(Long id) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id == null ? 1 : id);
    tipoFase.setActivo(true);

    ConvocatoriaFase convocatoriaFase = new ConvocatoriaFase();
    convocatoriaFase.setId(id);
    convocatoriaFase.setConvocatoria(convocatoria);
    convocatoriaFase.setFechaInicio(LocalDate.of(2020, 10, 19));
    convocatoriaFase.setFechaFin(LocalDate.of(2020, 10, 28));
    convocatoriaFase.setTipoFase(tipoFase);
    convocatoriaFase.setObservaciones("observaciones" + id);

    return convocatoriaFase;
  }

}
