package org.crue.hercules.sgi.usr.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.usr.exceptions.UnidadNotFoundException;
import org.crue.hercules.sgi.usr.model.Unidad;
import org.crue.hercules.sgi.usr.repository.UnidadRepository;
import org.crue.hercules.sgi.usr.service.impl.UnidadServiceImpl;
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

/**
 * UnidadServiceTest
 */
@ExtendWith(MockitoExtension.class)

public class UnidadServiceTest {

  @Mock
  private UnidadRepository repository;

  private UnidadService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new UnidadServiceImpl(repository);
  }

  @Test
  public void create_ReturnsUnidad() {
    // given: Un nuevo Unidad
    Unidad unidad = generarMockUnidad(null);

    BDDMockito.given(repository.findByNombre(unidad.getNombre())).willReturn(Optional.empty());
    BDDMockito.given(repository.findByAcronimo(unidad.getAcronimo())).willReturn(Optional.empty());

    BDDMockito.given(repository.save(unidad)).will((InvocationOnMock invocation) -> {
      Unidad unidadCreado = invocation.getArgument(0);
      unidadCreado.setId(1L);
      return unidadCreado;
    });

    // when: Creamos el Unidad
    Unidad unidadCreado = service.create(unidad);

    // then: El Unidad se crea correctamente
    Assertions.assertThat(unidadCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(unidadCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(unidadCreado.getNombre()).as("getNombre").isEqualTo(unidad.getNombre());
    Assertions.assertThat(unidadCreado.getActivo()).as("getActivo").isEqualTo(unidad.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo Unidad que ya tiene id
    Unidad unidad = generarMockUnidad(1L);

    // when: Creamos el Unidad
    // then: Lanza una excepcion porque el Unidad ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(unidad)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unidad id tiene que ser null para crear un nuevo Unidad");
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo Unidad con un nombre que ya existe
    Unidad unidadNew = generarMockUnidad(null, "nombreRepetido");
    Unidad unidad = generarMockUnidad(1L, "nombreRepetido");

    BDDMockito.given(repository.findByNombre(unidadNew.getNombre())).willReturn(Optional.of(unidad));

    // when: Creamos el Unidad
    // then: Lanza una excepcion porque hay otro Unidad con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(unidadNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una Unidad con el nombre " + unidadNew.getNombre());
  }

  @Test
  public void create_WithDuplicatedAcronimo_ThrowsIllegalArgumentException() {
    // given: Un nuevo Unidad con un acronimo que ya existe
    Unidad unidadNew = generarMockUnidad(null, "nombre");
    unidadNew.setAcronimo("acronimoRepetido");
    Unidad unidad = generarMockUnidad(1L, "nombre");
    unidad.setAcronimo("acronimoRepetido");

    BDDMockito.given(repository.findByNombre(unidadNew.getNombre())).willReturn(Optional.empty());
    BDDMockito.given(repository.findByAcronimo(unidadNew.getAcronimo())).willReturn(Optional.of(unidad));

    // when: Creamos el Unidad
    // then: Lanza una excepcion porque hay otro Unidad con ese acronimo
    Assertions.assertThatThrownBy(() -> service.create(unidadNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una Unidad con el acrónimo " + unidadNew.getAcronimo());
  }

  @Test
  public void update_ReturnsUnidad() {
    // given: Un nuevo Unidad con el nombre actualizado
    Unidad unidad = generarMockUnidad(1L);
    Unidad unidadNombreActualizado = generarMockUnidad(1L, "NombreActualizado");

    BDDMockito.given(repository.findByNombre(unidadNombreActualizado.getNombre())).willReturn(Optional.empty());
    BDDMockito.given(repository.findByAcronimo(unidadNombreActualizado.getAcronimo())).willReturn(Optional.empty());

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(unidad));
    BDDMockito.given(repository.save(ArgumentMatchers.<Unidad>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Unidad
    Unidad unidadActualizado = service.update(unidadNombreActualizado);

    // then: El Unidad se actualiza correctamente.
    Assertions.assertThat(unidadActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(unidadActualizado.getId()).as("getId()").isEqualTo(unidad.getId());
    Assertions.assertThat(unidadActualizado.getNombre()).as("getNombre()")
        .isEqualTo(unidadNombreActualizado.getNombre());
    Assertions.assertThat(unidadActualizado.getActivo()).as("getActivo()").isEqualTo(unidad.getActivo());
  }

  @Test
  public void update_WithIdNotExist_ThrowsUnidadNotFoundException() {
    // given: Un Unidad actualizado con un id que no existe
    Unidad unidad = generarMockUnidad(1L, "Unidad");

    BDDMockito.given(repository.findByNombre(unidad.getNombre())).willReturn(Optional.empty());

    // when: Actualizamos el Unidad
    // then: Lanza una excepcion porque el Unidad no existe
    Assertions.assertThatThrownBy(() -> service.update(unidad)).isInstanceOf(UnidadNotFoundException.class);
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un Unidad actualizado con un nombre que ya existe
    Unidad unidadActualizado = generarMockUnidad(1L, "nombreRepetido");
    Unidad unidad = generarMockUnidad(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombre(unidadActualizado.getNombre())).willReturn(Optional.of(unidad));

    // when: Actualizamos el Unidad
    // then: Lanza una excepcion porque hay otro Unidad con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(unidadActualizado)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una Unidad con el nombre " + unidadActualizado.getNombre());
  }

  @Test
  public void update_WithDuplicatedAcronimo_ThrowsIllegalArgumentException() {
    // given: Un Unidad actualizado con un acronimo que ya existe
    Unidad unidadActualizado = generarMockUnidad(1L, "nombre");
    unidadActualizado.setAcronimo("acronimoRepetido");
    Unidad unidad = generarMockUnidad(2L, "nombre");
    unidad.setAcronimo("acronimoRepetido");

    BDDMockito.given(repository.findByAcronimo(unidadActualizado.getAcronimo())).willReturn(Optional.of(unidad));

    // when: Actualizamos el Unidad
    // then: Lanza una excepcion porque hay otro Unidad con ese acronimo
    Assertions.assertThatThrownBy(() -> service.update(unidadActualizado)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una Unidad con el acrónimo " + unidadActualizado.getAcronimo());
  }

  @Test
  public void disable_ReturnsUnidad() {
    // given: Un nuevo Unidad activo
    Unidad unidad = generarMockUnidad(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(unidad));
    BDDMockito.given(repository.save(ArgumentMatchers.<Unidad>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el Unidad
    Unidad unidadActualizado = service.disable(unidad.getId());

    // then: El Unidad se desactiva correctamente.
    Assertions.assertThat(unidadActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(unidadActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(unidadActualizado.getNombre()).as("getNombre()").isEqualTo(unidad.getNombre());
    Assertions.assertThat(unidadActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithIdNotExist_ThrowsUnidadNotFoundException() {
    // given: Un id de un Unidad que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el Unidad
    // then: Lanza una excepcion porque el Unidad no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(UnidadNotFoundException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 Unidad
    List<Unidad> unidades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      unidades.add(generarMockUnidad(i, "Unidad" + String.format("%03d", i)));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Unidad>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Unidad>>() {
          @Override
          public Page<Unidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > unidades.size() ? unidades.size() : toIndex;
            List<Unidad> content = unidades.subList(fromIndex, toIndex);
            Page<Unidad> page = new PageImpl<>(content, pageable, unidades.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Unidad> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los Unidad del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Unidad unidad = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(unidad.getNombre()).isEqualTo("Unidad" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 Unidad
    List<Unidad> unidades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      unidades.add(generarMockUnidad(i, "Unidad" + String.format("%03d", i)));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Unidad>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Unidad>>() {
          @Override
          public Page<Unidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > unidades.size() ? unidades.size() : toIndex;
            List<Unidad> content = unidades.subList(fromIndex, toIndex);
            Page<Unidad> page = new PageImpl<>(content, pageable, unidades.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Unidad> page = service.findAllTodos(null, paging);

    // then: Devuelve la pagina 3 con los Unidad del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Unidad unidad = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(unidad.getNombre()).isEqualTo("Unidad" + String.format("%03d", i));
    }
  }

  @Test
  public void findById_ReturnsUnidad() {
    // given: Un Unidad con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockUnidad(idBuscado)));

    // when: Buscamos el Unidad por su id
    Unidad unidad = service.findById(idBuscado);

    // then: el Unidad
    Assertions.assertThat(unidad).as("isNotNull()").isNotNull();
    Assertions.assertThat(unidad.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(unidad.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(unidad.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsUnidadNotFoundException() throws Exception {
    // given: Ningun Unidad con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el Unidad por su id
    // then: lanza un UnidadNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(UnidadNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto Unidad
   * 
   * @param id id del Unidad
   * @return el objeto Unidad
   */
  private Unidad generarMockUnidad(Long id) {
    return generarMockUnidad(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto Unidad
   * 
   * @param id     id del Unidad
   * @param nombre nombre del Unidad
   * @return el objeto Unidad
   */
  private Unidad generarMockUnidad(Long id, String nombre) {
    Unidad unidad = new Unidad();
    unidad.setId(id);
    unidad.setNombre(nombre);
    unidad.setAcronimo("acronimo-" + id);
    unidad.setDescripcion("descripcion-" + id);
    unidad.setActivo(true);

    return unidad;
  }

}
