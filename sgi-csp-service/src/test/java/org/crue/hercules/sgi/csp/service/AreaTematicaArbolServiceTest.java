package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ListadoAreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.AreaTematicaArbolNotFoundException;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.repository.ListadoAreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.AreaTematicaArbolRepository;
import org.crue.hercules.sgi.csp.service.impl.AreaTematicaArbolServiceImpl;
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

/**
 * AreaTematicaArbolServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class AreaTematicaArbolServiceTest {

  @Mock
  private ListadoAreaTematicaRepository listadoAreaTematicaRepository;

  @Mock
  private AreaTematicaArbolRepository repository;

  private AreaTematicaArbolService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new AreaTematicaArbolServiceImpl(repository, listadoAreaTematicaRepository);
  }

  @Test
  public void create_ReturnsAreaTematicaArbol() {
    // given: Un nuevo AreaTematicaArbol
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(null, "nombre-1", 1L, null);

    BDDMockito.given(repository.findByNombreAndListadoAreaTematicaId(areaTematicaArbol.getNombre(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(listadoAreaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockListadoAreaTematica(1L)));

    BDDMockito.given(repository.save(areaTematicaArbol)).will((InvocationOnMock invocation) -> {
      AreaTematicaArbol areaTematicaArbolCreado = invocation.getArgument(0);
      areaTematicaArbolCreado.setId(1L);
      return areaTematicaArbolCreado;
    });

    // when: Creamos el AreaTematicaArbol
    AreaTematicaArbol areaTematicaArbolCreado = service.create(areaTematicaArbol);

    // then: El AreaTematicaArbol se crea correctamente
    Assertions.assertThat(areaTematicaArbolCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaArbolCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(areaTematicaArbolCreado.getNombre()).as("getNombre()")
        .isEqualTo(areaTematicaArbol.getNombre());
    Assertions.assertThat(areaTematicaArbolCreado.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(areaTematicaArbol.getAbreviatura());
    Assertions.assertThat(areaTematicaArbolCreado.getActivo()).as("getActivo()")
        .isEqualTo(areaTematicaArbol.getActivo());
  }

  @Test
  public void create_WithPadre_ReturnsAreaTematicaArbol() {
    // given: Un nuevo AreaTematicaArbol
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(null, "nombre-1", 1L, 1L);

    BDDMockito.given(repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbol.getAbreviatura(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findByNombreAndListadoAreaTematicaId(areaTematicaArbol.getNombre(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(listadoAreaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockListadoAreaTematica(1L)));

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockAreaTematicaArbol(1L)));

    BDDMockito.given(repository.save(areaTematicaArbol)).will((InvocationOnMock invocation) -> {
      AreaTematicaArbol areaTematicaArbolCreado = invocation.getArgument(0);
      areaTematicaArbolCreado.setId(1L);
      return areaTematicaArbolCreado;
    });

    // when: Creamos el AreaTematicaArbol
    AreaTematicaArbol areaTematicaArbolCreado = service.create(areaTematicaArbol);

    // then: El AreaTematicaArbol se crea correctamente
    Assertions.assertThat(areaTematicaArbolCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaArbolCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(areaTematicaArbolCreado.getNombre()).as("getNombre()")
        .isEqualTo(areaTematicaArbol.getNombre());
    Assertions.assertThat(areaTematicaArbolCreado.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(areaTematicaArbol.getAbreviatura());
    Assertions.assertThat(areaTematicaArbolCreado.getActivo()).as("getActivo()")
        .isEqualTo(areaTematicaArbol.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo AreaTematicaArbol que ya tiene id
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(1L);

    // when: Creamos el AreaTematicaArbol
    // then: Lanza una excepcion porque el AreaTematicaArbol ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaArbol)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("AreaTematicaArbol id tiene que ser null para crear un nuevo AreaTematicaArbol");
  }

  @Test
  public void create_WithoutListadoAreaTematicaId_ThrowsIllegalArgumentException() {
    // given: Un nuevo AreaTematicaArbol
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(null, "nombreRepetido", 1L, null);
    areaTematicaArbol.getListadoAreaTematica().setId(null);

    // when: Creamos el AreaTematicaArbol
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaArbol)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id ListadoAreaTematica no puede ser null para crear un AreaTematicaArbol");
  }

  @Test
  public void create_WithDuplicatedAbreviatura_ThrowsIllegalArgumentException() {
    // given: Un nuevo AreaTematicaArbol con un nombre que ya existe
    AreaTematicaArbol areaTematicaArbolNew = generarMockAreaTematicaArbol(null, "nombre-2", 1L, null);
    areaTematicaArbolNew.setAbreviatura("A-1");

    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(1L, "nombre-1", 1L, null);

    BDDMockito.given(repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbolNew.getAbreviatura(),
        areaTematicaArbolNew.getListadoAreaTematica().getId())).willReturn(Optional.of(areaTematicaArbol));

    // when: Creamos el AreaTematicaArbol
    // then: Lanza una excepcion porque hay otro AreaTematicaArbol con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaArbolNew))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un AreaTematicaArbol con la abreviatura "
            + areaTematicaArbolNew.getAbreviatura() + " en el ListadoAreaTematica");
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo AreaTematicaArbol con un nombre que ya existe
    AreaTematicaArbol areaTematicaArbolNew = generarMockAreaTematicaArbol(null, "nombreRepetido", 1L, null);
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(1L, "nombreRepetido", 1L, null);

    BDDMockito.given(repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbolNew.getAbreviatura(),
        areaTematicaArbolNew.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findByNombreAndListadoAreaTematicaId(areaTematicaArbolNew.getNombre(),
        areaTematicaArbolNew.getListadoAreaTematica().getId())).willReturn(Optional.of(areaTematicaArbol));

    // when: Creamos el AreaTematicaArbol
    // then: Lanza una excepcion porque hay otro AreaTematicaArbol con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaArbolNew))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un AreaTematicaArbol con el nombre "
            + areaTematicaArbolNew.getNombre() + " en el ListadoAreaTematica");
  }

  @Test
  public void create_WithNoExistingListadoAreaTematica_ThrowsListadoAreaTematicaNotFoundException() {
    // given: Un nuevo AreaTematicaArbol con un listadoAreaTematica que no existe
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(null, "nombreRepetido", 1L, null);

    BDDMockito.given(repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbol.getAbreviatura(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findByNombreAndListadoAreaTematicaId(areaTematicaArbol.getNombre(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(listadoAreaTematicaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el AreaTematicaArbol
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaArbol))
        .isInstanceOf(ListadoAreaTematicaNotFoundException.class);
  }

  @Test
  public void create_WithNoExistingPadre_ThrowsAreaTematicaArbolNotFoundException() {
    // given: Un nuevo AreaTematicaArbol con un padre que no existe
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(null, "nombreRepetido", 1L, 1L);

    BDDMockito.given(repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbol.getAbreviatura(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findByNombreAndListadoAreaTematicaId(areaTematicaArbol.getNombre(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(listadoAreaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockListadoAreaTematica(1L)));

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el AreaTematicaArbol
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaArbol))
        .isInstanceOf(AreaTematicaArbolNotFoundException.class);
  }

  @Test
  public void update_ReturnsAreaTematicaArbol() {
    // given: Un nuevo AreaTematicaArbol con el nombre actualizado
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(1L);
    AreaTematicaArbol areaTematicaArbolNombreActualizado = generarMockAreaTematicaArbol(1L, "NombreActualizado", 1L,
        null);

    BDDMockito.given(repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbol.getAbreviatura(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findByNombreAndListadoAreaTematicaId(areaTematicaArbolNombreActualizado.getNombre(),
        areaTematicaArbolNombreActualizado.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(areaTematicaArbol));
    BDDMockito.given(repository.save(ArgumentMatchers.<AreaTematicaArbol>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el AreaTematicaArbol
    AreaTematicaArbol areaTematicaArbolActualizado = service.update(areaTematicaArbolNombreActualizado);

    // then: El AreaTematicaArbol se actualiza correctamente.
    Assertions.assertThat(areaTematicaArbolActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaArbolActualizado.getId()).as("getId()").isEqualTo(areaTematicaArbol.getId());
    Assertions.assertThat(areaTematicaArbolActualizado.getNombre()).as("getNombre()")
        .isEqualTo(areaTematicaArbolNombreActualizado.getNombre());
    Assertions.assertThat(areaTematicaArbolActualizado.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(areaTematicaArbol.getAbreviatura());
    Assertions.assertThat(areaTematicaArbolActualizado.getActivo()).as("getActivo()")
        .isEqualTo(areaTematicaArbol.getActivo());
  }

  @Test
  public void update_WithPadre_ReturnsAreaTematicaArbol() {
    // given: Un nuevo AreaTematicaArbol con el nombre actualizado
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(2L);
    AreaTematicaArbol areaTematicaArbolNombreActualizado = generarMockAreaTematicaArbol(2L, "NombreActualizado", 1L,
        1L);

    BDDMockito.given(repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbol.getAbreviatura(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findByNombreAndListadoAreaTematicaId(areaTematicaArbolNombreActualizado.getNombre(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(generarMockAreaTematicaArbol(1L)));
    BDDMockito.given(repository.findById(2L)).willReturn(Optional.of(areaTematicaArbol));

    BDDMockito.given(repository.save(ArgumentMatchers.<AreaTematicaArbol>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el AreaTematicaArbol
    AreaTematicaArbol areaTematicaArbolActualizado = service.update(areaTematicaArbolNombreActualizado);

    // then: El AreaTematicaArbol se actualiza correctamente.
    Assertions.assertThat(areaTematicaArbolActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaArbolActualizado.getId()).as("getId()").isEqualTo(areaTematicaArbol.getId());
    Assertions.assertThat(areaTematicaArbolActualizado.getNombre()).as("getNombre()")
        .isEqualTo(areaTematicaArbolNombreActualizado.getNombre());
    Assertions.assertThat(areaTematicaArbolActualizado.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(areaTematicaArbol.getAbreviatura());
    Assertions.assertThat(areaTematicaArbolActualizado.getActivo()).as("getActivo()")
        .isEqualTo(areaTematicaArbol.getActivo());
  }

  @Test
  public void update_ActivoToFalse_ReturnsAreaTematicaArbol() {
    // given: Un nuevo AreaTematicaArbol con activo actualizado a false
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(2L);
    AreaTematicaArbol areaTematicaArbolHijo = generarMockAreaTematicaArbol(3L, "hijo", 1L, 2L);
    AreaTematicaArbol areaTematicaArbolNieto = generarMockAreaTematicaArbol(4L, "nieto", 1L, 3L);
    AreaTematicaArbol areaTematicaArbolActivoActualizado = generarMockAreaTematicaArbol(2L, "NombreActualizado", 1L,
        1L);
    areaTematicaArbolActivoActualizado.setActivo(false);

    BDDMockito.given(repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbol.getAbreviatura(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findByNombreAndListadoAreaTematicaId(areaTematicaArbolActivoActualizado.getNombre(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(generarMockAreaTematicaArbol(1L)));
    BDDMockito.given(repository.findById(2L)).willReturn(Optional.of(areaTematicaArbol));

    BDDMockito.given(repository.findByPadreIdIn(Arrays.asList(2L))).willReturn(Arrays.asList(areaTematicaArbolHijo));
    BDDMockito.given(repository.findByPadreIdIn(Arrays.asList(3L))).willReturn(Arrays.asList(areaTematicaArbolNieto));
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<AreaTematicaArbol>anyList()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    BDDMockito.given(repository.save(ArgumentMatchers.<AreaTematicaArbol>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el AreaTematicaArbol
    AreaTematicaArbol areaTematicaArbolActualizado = service.update(areaTematicaArbolActivoActualizado);

    // then: El AreaTematicaArbol se actualiza correctamente y sus hijos se
    // desactivan.
    Assertions.assertThat(areaTematicaArbolActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaArbolActualizado.getId()).as("getId()").isEqualTo(areaTematicaArbol.getId());
    Assertions.assertThat(areaTematicaArbolActualizado.getNombre()).as("getNombre()")
        .isEqualTo(areaTematicaArbol.getNombre());
    Assertions.assertThat(areaTematicaArbolActualizado.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(areaTematicaArbol.getAbreviatura());
    Assertions.assertThat(areaTematicaArbolActualizado.getActivo()).as("getActivo()")
        .isEqualTo(areaTematicaArbolActivoActualizado.getActivo());

    Mockito.verify(repository, Mockito.times(2)).saveAll(ArgumentMatchers.<AreaTematicaArbol>anyList());
  }

  @Test
  public void update_WithIdNotExist_ThrowsAreaTematicaArbolNotFoundException() {
    // given: Un AreaTematicaArbol actualizado con un id que no existe
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(1L, "AreaTematicaArbol", 1L, null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Actualizamos el AreaTematicaArbol
    // then: Lanza una excepcion porque el AreaTematicaArbol no existe
    Assertions.assertThatThrownBy(() -> service.update(areaTematicaArbol))
        .isInstanceOf(AreaTematicaArbolNotFoundException.class);
  }

  @Test
  public void update_WithNoExistingPadre_ThrowsAreaTematicaArbolNotFoundException() {
    // given: Un AreaTematicaArbol actualizado con un padre que no existe
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(2L, "nombreRepetido", 1L, 1L);

    BDDMockito.given(repository.findById(1L)).willReturn(Optional.empty());

    // when: Actualizamos el AreaTematicaArbol
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(areaTematicaArbol))
        .isInstanceOf(AreaTematicaArbolNotFoundException.class);
  }

  @Test
  public void update_WithDuplicatedAbreviatura_ThrowsIllegalArgumentException() {
    // given: Un AreaTematicaArbol actualizado con una abreviatura que ya existe
    AreaTematicaArbol areaTematicaArbolActualizado = generarMockAreaTematicaArbol(1L, "nombreRepetido", 1L, null);
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(2L, "nombreRepetido", 1L, null);
    areaTematicaArbol.setAbreviatura("A-1");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(areaTematicaArbolActualizado));

    BDDMockito.given(repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbolActualizado.getAbreviatura(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.of(areaTematicaArbol));

    // when: Actualizamos el AreaTematicaArbol
    // then: Lanza una excepcion porque hay otro AreaTematicaArbol con esa
    // abreviatura
    Assertions.assertThatThrownBy(() -> service.update(areaTematicaArbolActualizado))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un AreaTematicaArbol con la abreviatura "
            + areaTematicaArbolActualizado.getAbreviatura() + " en el ListadoAreaTematica");
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un AreaTematicaArbol actualizado con un nombre que ya existe
    AreaTematicaArbol areaTematicaArbolActualizado = generarMockAreaTematicaArbol(1L, "nombreRepetido", 1L, null);
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(2L, "nombreRepetido", 1L, null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(areaTematicaArbolActualizado));

    BDDMockito.given(repository.findByAbreviaturaAndListadoAreaTematicaId(areaTematicaArbolActualizado.getAbreviatura(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.empty());

    BDDMockito.given(repository.findByNombreAndListadoAreaTematicaId(areaTematicaArbolActualizado.getNombre(),
        areaTematicaArbol.getListadoAreaTematica().getId())).willReturn(Optional.of(areaTematicaArbol));

    // when: Actualizamos el AreaTematicaArbol
    // then: Lanza una excepcion porque hay otro AreaTematicaArbol con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(areaTematicaArbolActualizado))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un AreaTematicaArbol con el nombre "
            + areaTematicaArbolActualizado.getNombre() + " en el ListadoAreaTematica");
  }

  @Test
  public void disable_ReturnsAreaTematicaArbol() {
    // given: Un nuevo AreaTematicaArbol activo
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(1L);
    AreaTematicaArbol areaTematicaArbolHijo = generarMockAreaTematicaArbol(2L, "hijo", 1L, 1L);
    AreaTematicaArbol areaTematicaArbolNieto = generarMockAreaTematicaArbol(3L, "nieto", 1L, 2L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(areaTematicaArbol));
    BDDMockito.given(repository.findByPadreIdIn(Arrays.asList(1L))).willReturn(Arrays.asList(areaTematicaArbolHijo));
    BDDMockito.given(repository.findByPadreIdIn(Arrays.asList(2L))).willReturn(Arrays.asList(areaTematicaArbolNieto));
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<AreaTematicaArbol>anyList()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    BDDMockito.given(repository.save(ArgumentMatchers.<AreaTematicaArbol>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el AreaTematicaArbol y todos sus hijos en cascada
    AreaTematicaArbol areaTematicaArbolActualizado = service.disable(areaTematicaArbol.getId());

    // then: El AreaTematicaArbol y todos sus hijos se desactivan correctamente
    Assertions.assertThat(areaTematicaArbolActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaArbolActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(areaTematicaArbolActualizado.getNombre()).as("getNombre()")
        .isEqualTo(areaTematicaArbol.getNombre());
    Assertions.assertThat(areaTematicaArbolActualizado.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(areaTematicaArbol.getAbreviatura());
    Assertions.assertThat(areaTematicaArbolActualizado.getActivo()).as("getActivo()").isEqualTo(false);

    Mockito.verify(repository, Mockito.times(2)).saveAll(ArgumentMatchers.<AreaTematicaArbol>anyList());

  }

  @Test
  public void disable_WithIdNotExist_ThrowsAreaTematicaArbolNotFoundException() {
    // given: Un id de un AreaTematicaArbol que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el AreaTematicaArbol
    // then: Lanza una excepcion porque el AreaTematicaArbol no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste))
        .isInstanceOf(AreaTematicaArbolNotFoundException.class);
  }

  @Test
  public void findById_ReturnsAreaTematicaArbol() {
    // given: Un AreaTematicaArbol con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockAreaTematicaArbol(idBuscado)));

    // when: Buscamos el AreaTematicaArbol por su id
    AreaTematicaArbol areaTematicaArbol = service.findById(idBuscado);

    // then: el AreaTematicaArbol
    Assertions.assertThat(areaTematicaArbol).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaArbol.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(areaTematicaArbol.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(areaTematicaArbol.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsAreaTematicaArbolNotFoundException() throws Exception {
    // given: Ningun AreaTematicaArbol con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el AreaTematicaArbol por su id
    // then: lanza un AreaTematicaArbolNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(AreaTematicaArbolNotFoundException.class);
  }

  @Test
  public void findAllByListadoAreaTematica_ReturnsPage() {
    // given: Una lista con 37 AreaTematicaArbol para el ListadoAreaTematica
    Long idListadoAreaTematica = 1L;
    List<AreaTematicaArbol> areaTematicaArboles = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      areaTematicaArboles.add(generarMockAreaTematicaArbol(i));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<AreaTematicaArbol>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<AreaTematicaArbol>>() {
          @Override
          public Page<AreaTematicaArbol> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > areaTematicaArboles.size() ? areaTematicaArboles.size() : toIndex;
            List<AreaTematicaArbol> content = areaTematicaArboles.subList(fromIndex, toIndex);
            Page<AreaTematicaArbol> page = new PageImpl<>(content, pageable, areaTematicaArboles.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<AreaTematicaArbol> page = service.findAllByListadoAreaTematica(idListadoAreaTematica, null, paging);

    // then: Devuelve la pagina 3 con los TipoEnlace del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      AreaTematicaArbol areaTematicaArbol = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(areaTematicaArbol.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  /**
   * Función que devuelve un objeto ListadoAreaTematica
   * 
   * @param id id del ListadoAreaTematica
   * @return el objeto ListadoAreaTematica
   */
  private ListadoAreaTematica generarMockListadoAreaTematica(Long id) {
    return generarMockListadoAreaTematica(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto ListadoAreaTematica
   * 
   * @param id     id del ListadoAreaTematica
   * @param nombre nombre del ListadoAreaTematica
   * @return el objeto ListadoAreaTematica
   */
  private ListadoAreaTematica generarMockListadoAreaTematica(Long id, String nombre) {
    ListadoAreaTematica listadoAreaTematica = new ListadoAreaTematica();
    listadoAreaTematica.setId(id);
    listadoAreaTematica.setNombre(nombre);
    listadoAreaTematica.setDescripcion("descripcion-" + id);
    listadoAreaTematica.setActivo(true);

    return listadoAreaTematica;
  }

  /**
   * Función que devuelve un objeto AreaTematicaArbol
   * 
   * @param id id del AreaTematicaArbol
   * @return el objeto AreaTematicaArbol
   */
  private AreaTematicaArbol generarMockAreaTematicaArbol(Long id) {
    return generarMockAreaTematicaArbol(id, "nombre-" + id, id, null);
  }

  /**
   * Función que devuelve un objeto AreaTematicaArbol
   * 
   * @param id                       id del AreaTematicaArbol
   * @param nombre                   nombre del AreaTematicaArbol
   * @param idListadoAreaTematica    id del ListadoAreaTematica
   * @param idAreaTematicaArbolPadre id del AreaTematicaArbol padre
   * @return el objeto AreaTematicaArbol
   */
  private AreaTematicaArbol generarMockAreaTematicaArbol(Long id, String nombre, Long idListadoAreaTematica,
      Long idAreaTematicaArbolPadre) {
    AreaTematicaArbol areaTematicaArbol = new AreaTematicaArbol();
    areaTematicaArbol.setId(id);
    areaTematicaArbol.setNombre(nombre);
    areaTematicaArbol.setAbreviatura("A-" + (id == null ? 0 : id));
    areaTematicaArbol.setListadoAreaTematica(generarMockListadoAreaTematica(idListadoAreaTematica));
    if (idAreaTematicaArbolPadre != null) {
      areaTematicaArbol.setPadre(generarMockAreaTematicaArbol(idAreaTematicaArbolPadre));
    }
    areaTematicaArbol.setActivo(true);

    return areaTematicaArbol;
  }

}
