package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaHitoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaHitoServiceImpl;
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
 * ConvocatoriaHitoServiceTest
 */
@ExtendWith(MockitoExtension.class)

public class ConvocatoriaHitoServiceTest {

  @Mock
  private ConvocatoriaHitoRepository repository;

  @Mock
  private ConvocatoriaRepository convocatoriaRepository;

  @Mock
  private TipoHitoRepository tipoHitoRepository;

  private ConvocatoriaHitoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaHitoServiceImpl(repository, convocatoriaRepository, tipoHitoRepository);
  }

  @Test
  public void create_ReturnsConvocatoriaHito() {
    // given: Un nuevo ConvocatoriaHito
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getConvocatoria()));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getTipoHito()));

    BDDMockito.given(repository.save(convocatoriaHito)).will((InvocationOnMock invocation) -> {
      ConvocatoriaHito convocatoriaHitoCreado = invocation.getArgument(0);
      convocatoriaHitoCreado.setId(1L);
      return convocatoriaHitoCreado;
    });

    // when: Creamos el ConvocatoriaHito
    ConvocatoriaHito convocatoriaHitoCreado = service.create(convocatoriaHito);

    // then: El ConvocatoriaHito se crea correctamente
    Assertions.assertThat(convocatoriaHitoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaHitoCreado.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaHito.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaHitoCreado.getFecha()).as("getFechaInicio()")
        .isEqualTo(convocatoriaHito.getFecha());
    Assertions.assertThat(convocatoriaHitoCreado.getComentario()).as("getComentario()")
        .isEqualTo(convocatoriaHito.getComentario());
    Assertions.assertThat(convocatoriaHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(convocatoriaHito.getTipoHito().getId());
    Assertions.assertThat(convocatoriaHitoCreado.getGeneraAviso()).as("getGeneraAviso()")
        .isEqualTo(convocatoriaHito.getGeneraAviso());
  }

  public void create_WithFechaAnterior_SaveGeneraAvisoFalse() {
    // given: Un nuevo ConvocatoriaHito con fecha pasada
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);
    convocatoriaHito.setFecha(LocalDate.now().minusDays(2));
    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getConvocatoria()));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getTipoHito()));

    BDDMockito.given(repository.save(convocatoriaHito)).will((InvocationOnMock invocation) -> {
      ConvocatoriaHito convocatoriaHitoCreado = invocation.getArgument(0);
      convocatoriaHitoCreado.setId(1L);
      return convocatoriaHitoCreado;
    });

    // when: Creamos el ConvocatoriaHito
    ConvocatoriaHito convocatoriaHitoCreado = service.create(convocatoriaHito);

    // then: El ConvocatoriaHito se crea correctamente con GenerarAviso como FALSE
    Assertions.assertThat(convocatoriaHitoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaHitoCreado.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaHito.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaHitoCreado.getFecha()).as("getFechaInicio()")
        .isEqualTo(convocatoriaHito.getFecha());
    Assertions.assertThat(convocatoriaHitoCreado.getComentario()).as("getComentario()")
        .isEqualTo(convocatoriaHito.getComentario());
    Assertions.assertThat(convocatoriaHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(convocatoriaHito.getTipoHito().getId());
    Assertions.assertThat(convocatoriaHitoCreado.getGeneraAviso()).as("getGeneraAviso()")
        .isEqualTo(convocatoriaHito.getGeneraAviso());
    Assertions.assertThat(convocatoriaHitoCreado.getGeneraAviso()).as("getGeneraAviso()").isEqualTo(Boolean.FALSE);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaHito que ya tiene id
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);

    // when: Creamos el ConvocatoriaHito
    // then: Lanza una excepcion porque el ConvocatoriaHito ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaHito)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaHito id tiene que ser null para crear un nuevo ConvocatoriaHito");
  }

  @Test
  public void create_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaHito without ConvocatoriaId
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);
    convocatoriaHito.getConvocatoria().setId(null);
    convocatoriaHito.setTipoHito(TipoHito.builder().build());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaHito
        () -> service.create(convocatoriaHito))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaHito");
  }

  @Test
  public void create_WithFechaYTipoHitoDuplicado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaHito without fecha
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);
    convocatoriaHito.getConvocatoria().setId(1L);
    convocatoriaHito.setTipoHito(TipoHito.builder().id(1L).build());

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getConvocatoria()));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getTipoHito()));
    BDDMockito.given(repository.findByFechaAndTipoHitoId(ArgumentMatchers.any(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaHito
        () -> service.create(convocatoriaHito))
        // then: throw exception as fecha is null
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un Hito con el mismo tipo en esa fecha");
  }

  @Test
  public void create_WithNoExistingConvocatoria_ThrowsConvocatoriaNotFoundException() {
    // given: a ConvocatoriaHito with non existing Convocatoria
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);
    convocatoriaHito.getTipoHito().setId(1L);
    convocatoriaHito.getTipoHito().setActivo(true);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaHito
        () -> service.create(convocatoriaHito))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void create_WithNoExistingTipoHito_404() {
    // given: a ConvocatoriaEnlace with non existing TipoHito
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getConvocatoria()));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEnlace
        () -> service.create(convocatoriaHito))
        // then: throw exception as TipoHito is not found
        .isInstanceOf(TipoHitoNotFoundException.class);
  }

  @Test
  public void create_WithTipoHitoActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaHito with non existing TipoHito
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(null);
    convocatoriaHito.getTipoHito().setActivo(false);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getConvocatoria()));

    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getTipoHito()));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaHito
        () -> service.create(convocatoriaHito))
        // then: throw exception as TipoHito is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoHito debe estar activo");
  }

  @Test
  public void update_ReturnsConvocatoriaHito() {
    // given: Un nuevo ConvocatoriaHito con el tipoHito actualizado
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);
    ConvocatoriaHito convocatoriaHitoTipoHitoActualizado = generarMockConvocatoriaHito(1L);
    convocatoriaHitoTipoHitoActualizado.setTipoHito(new TipoHito(2L, "tipoHito2", "descripcion2", Boolean.TRUE));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaHito));

    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getTipoHito()));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaHito
    ConvocatoriaHito convocatoriaHitoActualizado = service.update(convocatoriaHitoTipoHitoActualizado);

    // then: El ConvocatoriaHito se actualiza correctamente.
    Assertions.assertThat(convocatoriaHitoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaHitoActualizado.getId()).as("getId()").isEqualTo(convocatoriaHito.getId());
    Assertions.assertThat(convocatoriaHitoActualizado.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaHito.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaHitoActualizado.getComentario()).as("getComentario()")
        .isEqualTo(convocatoriaHito.getComentario());
    Assertions.assertThat(convocatoriaHitoActualizado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(convocatoriaHito.getTipoHito().getId());
    Assertions.assertThat(convocatoriaHitoActualizado.getGeneraAviso()).as("getGeneraAviso()")
        .isEqualTo(convocatoriaHito.getGeneraAviso());
  }

  @Test
  public void update_WithFechaAnterior_SaveGeneraAvisoFalse() {
    // given: Un nuevo ConvocatoriaHito con el la fecha anterior
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);
    ConvocatoriaHito convocatoriaHitoFechaActualizado = generarMockConvocatoriaHito(1L);
    convocatoriaHitoFechaActualizado.setFecha(LocalDate.now().minusDays(2));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaHito));

    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito.getTipoHito()));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaHito
    ConvocatoriaHito convocatoriaHitoActualizado = service.update(convocatoriaHitoFechaActualizado);

    // then: El ConvocatoriaHito se actualiza correctamente.
    Assertions.assertThat(convocatoriaHitoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaHitoActualizado.getId()).as("getId()").isEqualTo(convocatoriaHito.getId());
    Assertions.assertThat(convocatoriaHitoActualizado.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaHito.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaHitoActualizado.getComentario()).as("getComentario()")
        .isEqualTo(convocatoriaHito.getComentario());
    Assertions.assertThat(convocatoriaHitoActualizado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(convocatoriaHito.getTipoHito().getId());
    Assertions.assertThat(convocatoriaHitoActualizado.getGeneraAviso()).as("getGeneraAviso()")
        .isEqualTo(convocatoriaHito.getGeneraAviso());
    Assertions.assertThat(convocatoriaHitoActualizado.getGeneraAviso()).as("getGeneraAviso()").isEqualTo(Boolean.FALSE);
  }

  @Test
  public void update_WithFechaYTipoHitoDuplicado_ThrowsIllegalArgumentException() {
    // given: Un ConvocatoriaHito a actualizar sin fecha
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);
    ConvocatoriaHito convocatoriaHitoActualizado = generarMockConvocatoriaHito(2L);
    convocatoriaHitoActualizado.getTipoHito().setId(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHitoActualizado));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHitoActualizado.getTipoHito()));

    BDDMockito.given(repository.findByFechaAndTipoHitoId(ArgumentMatchers.any(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHito));

    // when: Actualizamos el ConvocatoriaHito
    // then: Lanza una excepcion porque la fecha no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaHitoActualizado))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un Hito con el mismo tipo en esa fecha");
  }

  @Test
  public void update_WithIdNotExist_ThrowsConvocatoriaHitoNotFoundException() {
    // given: Un ConvocatoriaHito a actualizar con un id que no existe
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ConvocatoriaHito
    // then: Lanza una excepcion porque el ConvocatoriaHito no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaHito))
        .isInstanceOf(ConvocatoriaHitoNotFoundException.class);
  }

  @Test
  public void update_WithNoExistingTipoHito_404() {
    // given: a ConvocatoriaHito with non existing TipoHito
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaHito));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaHito
        () -> service.update(convocatoriaHito))
        // then: throw exception as TipoHito is not found
        .isInstanceOf(TipoHitoNotFoundException.class);
  }

  @Test
  public void update_WithTipoHitoActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaHito with TipoHitoActivo False
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);

    ConvocatoriaHito convocatoriaHitoActualizado = generarMockConvocatoriaHito(1L);
    convocatoriaHitoActualizado.getTipoHito().setId(2L);
    convocatoriaHitoActualizado.getTipoHito().setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaHito));

    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaHitoActualizado.getTipoHito()));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaHito
        () -> service.update(convocatoriaHitoActualizado))
        // then: throw exception as Programa is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoHito debe estar activo");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ConvocatoriaHito
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
        .isInstanceOf(ConvocatoriaHitoNotFoundException.class);
  }

  @Test
  public void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaHito para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaHito> convocatoriasEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesConvocantes.add(generarMockConvocatoriaHito(i));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaHito>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasEntidadesConvocantes.size() ? convocatoriasEntidadesConvocantes.size()
              : toIndex;
          List<ConvocatoriaHito> content = convocatoriasEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ConvocatoriaHito> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaHito> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaHito del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaHito convocatoriaHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(convocatoriaHito.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  @Test
  public void findById_ReturnsConvocatoriaHito() {
    // given: Un ConvocatoriaHito con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockConvocatoriaHito(idBuscado)));

    // when: Buscamos el ConvocatoriaHito por su id
    ConvocatoriaHito convocatoriaHito = service.findById(idBuscado);

    // then: el ConvocatoriaHito
    Assertions.assertThat(convocatoriaHito).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaHito.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaHitoNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaHito con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaHito por su id
    // then: lanza un ConvocatoriaHitoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaHitoNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaHito
   * 
   * @param id id del ConvocatoriaHito
   * @return el objeto ConvocatoriaHito
   */
  private ConvocatoriaHito generarMockConvocatoriaHito(Long id) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id == null ? 1 : id);
    tipoHito.setActivo(true);

    ConvocatoriaHito convocatoriaHito = new ConvocatoriaHito();
    convocatoriaHito.setId(id);
    convocatoriaHito.setConvocatoria(convocatoria);
    convocatoriaHito.setFecha(LocalDate.of(2020, 10, 19));
    convocatoriaHito.setComentario("comentario" + id);
    convocatoriaHito.setGeneraAviso(true);
    convocatoriaHito.setTipoHito(tipoHito);

    return convocatoriaHito;
  }
}
