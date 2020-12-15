package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoAmbitoGeograficoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoAmbitoGeograficoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * TipoAmbitoGeograficoServiceTest
 */
public class TipoAmbitoGeograficoServiceTest extends BaseServiceTest {

  @Mock
  private TipoAmbitoGeograficoRepository repository;

  private TipoAmbitoGeograficoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new TipoAmbitoGeograficoServiceImpl(repository);
  }

  @Test
  public void create_ReturnsTipoAmbitoGeografico() {
    // given: Un nuevo TipoAmbitoGeografico
    TipoAmbitoGeografico tipoAmbitoGeografico = generarMockTipoAmbitoGeografico(null);

    BDDMockito.given(repository.findByNombre(tipoAmbitoGeografico.getNombre())).willReturn(Optional.empty());

    BDDMockito.given(repository.save(tipoAmbitoGeografico)).will((InvocationOnMock invocation) -> {
      TipoAmbitoGeografico tipoAmbitoGeograficoCreado = invocation.getArgument(0);
      tipoAmbitoGeograficoCreado.setId(1L);
      return tipoAmbitoGeograficoCreado;
    });

    // when: Creamos el TipoAmbitoGeografico
    TipoAmbitoGeografico tipoAmbitoGeograficoCreado = service.create(tipoAmbitoGeografico);

    // then: El TipoAmbitoGeografico se crea correctamente
    Assertions.assertThat(tipoAmbitoGeograficoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoAmbitoGeograficoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoAmbitoGeograficoCreado.getNombre()).as("getNombre")
        .isEqualTo(tipoAmbitoGeografico.getNombre());
    Assertions.assertThat(tipoAmbitoGeograficoCreado.getActivo()).as("getActivo")
        .isEqualTo(tipoAmbitoGeografico.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoAmbitoGeografico que ya tiene id
    TipoAmbitoGeografico tipoAmbitoGeografico = generarMockTipoAmbitoGeografico(1L);

    // when: Creamos el TipoAmbitoGeografico
    // then: Lanza una excepcion porque el TipoAmbitoGeografico ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(tipoAmbitoGeografico))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoAmbitoGeografico id tiene que ser null para crear un nuevo TipoAmbitoGeografico");
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoAmbitoGeografico con un nombre que ya existe
    TipoAmbitoGeografico tipoAmbitoGeograficoNew = generarMockTipoAmbitoGeografico(null, "nombreRepetido");
    TipoAmbitoGeografico tipoAmbitoGeografico = generarMockTipoAmbitoGeografico(1L, "nombreRepetido");

    BDDMockito.given(repository.findByNombre(tipoAmbitoGeograficoNew.getNombre()))
        .willReturn(Optional.of(tipoAmbitoGeografico));

    // when: Creamos el TipoAmbitoGeografico
    // then: Lanza una excepcion porque hay otro TipoAmbitoGeografico con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(tipoAmbitoGeograficoNew))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un TipoAmbitoGeografico con el nombre " + tipoAmbitoGeograficoNew.getNombre());
  }

  @Test
  public void update_ReturnsTipoAmbitoGeografico() {
    // given: Un nuevo TipoAmbitoGeografico con el nombre actualizado
    TipoAmbitoGeografico tipoAmbitoGeografico = generarMockTipoAmbitoGeografico(1L);
    TipoAmbitoGeografico tipoAmbitoGeograficoNombreActualizado = generarMockTipoAmbitoGeografico(1L,
        "NombreActualizado");

    BDDMockito.given(repository.findByNombre(tipoAmbitoGeograficoNombreActualizado.getNombre()))
        .willReturn(Optional.empty());

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoAmbitoGeografico));
    BDDMockito.given(repository.save(ArgumentMatchers.<TipoAmbitoGeografico>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el TipoAmbitoGeografico
    TipoAmbitoGeografico tipoAmbitoGeograficoActualizado = service.update(tipoAmbitoGeograficoNombreActualizado);

    // then: El TipoAmbitoGeografico se actualiza correctamente.
    Assertions.assertThat(tipoAmbitoGeograficoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoAmbitoGeograficoActualizado.getId()).as("getId()")
        .isEqualTo(tipoAmbitoGeografico.getId());
    Assertions.assertThat(tipoAmbitoGeograficoActualizado.getNombre()).as("getNombre()")
        .isEqualTo(tipoAmbitoGeograficoNombreActualizado.getNombre());
    Assertions.assertThat(tipoAmbitoGeograficoActualizado.getActivo()).as("getActivo()")
        .isEqualTo(tipoAmbitoGeografico.getActivo());
  }

  @Test
  public void update_WithIdNotExist_ThrowsTipoAmbitoGeograficoNotFoundException() {
    // given: Un TipoAmbitoGeografico a actualizar con un id que no existe
    TipoAmbitoGeografico tipoAmbitoGeografico = generarMockTipoAmbitoGeografico(1L, "TipoAmbitoGeografico");

    // when: Actualizamos el TipoAmbitoGeografico
    // then: Lanza una excepcion porque el TipoAmbitoGeografico no existe
    Assertions.assertThatThrownBy(() -> service.update(tipoAmbitoGeografico))
        .isInstanceOf(TipoAmbitoGeograficoNotFoundException.class);
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoAmbitoGeografico con un nombre que ya existe
    TipoAmbitoGeografico tipoAmbitoGeograficoActualizado = generarMockTipoAmbitoGeografico(1L, "nombreRepetido");
    TipoAmbitoGeografico tipoAmbitoGeografico = generarMockTipoAmbitoGeografico(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombre(tipoAmbitoGeograficoActualizado.getNombre()))
        .willReturn(Optional.of(tipoAmbitoGeografico));

    // when: Creamos el TipoAmbitoGeografico
    // then: Lanza una excepcion porque hay otro TipoAmbitoGeografico con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(tipoAmbitoGeograficoActualizado))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un TipoAmbitoGeografico con el nombre " + tipoAmbitoGeograficoActualizado.getNombre());
  }

  @Test
  public void disable_ReturnsTipoAmbitoGeografico() {
    // given: Un nuevo TipoAmbitoGeografico activo
    TipoAmbitoGeografico tipoAmbitoGeografico = generarMockTipoAmbitoGeografico(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoAmbitoGeografico));
    BDDMockito.given(repository.save(ArgumentMatchers.<TipoAmbitoGeografico>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el TipoAmbitoGeografico
    TipoAmbitoGeografico tipoAmbitoGeograficoActualizado = service.disable(tipoAmbitoGeografico.getId());

    // then: El TipoAmbitoGeografico se desactiva correctamente.
    Assertions.assertThat(tipoAmbitoGeograficoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoAmbitoGeograficoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoAmbitoGeograficoActualizado.getNombre()).as("getNombre()")
        .isEqualTo(tipoAmbitoGeografico.getNombre());
    Assertions.assertThat(tipoAmbitoGeograficoActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithIdNotExist_ThrowsTipoAmbitoGeograficoNotFoundException() {
    // given: Un id de un TipoAmbitoGeografico que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el TipoAmbitoGeografico
    // then: Lanza una excepcion porque el TipoAmbitoGeografico no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste))
        .isInstanceOf(TipoAmbitoGeograficoNotFoundException.class);
  }

  @Test
  public void update_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoAmbitoGeografico con un nombre que ya existe
    TipoAmbitoGeografico tipoAmbitoGeograficoUpdated = generarMockTipoAmbitoGeografico(1L, "nombreRepetido");
    TipoAmbitoGeografico tipoAmbitoGeografico = generarMockTipoAmbitoGeografico(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombre(tipoAmbitoGeograficoUpdated.getNombre()))
        .willReturn(Optional.of(tipoAmbitoGeografico));

    // when: Actualizamos el TipoAmbitoGeografico
    // then: Lanza una excepcion porque ya existe otro TipoAmbitoGeografico con ese
    // nombre
    Assertions.assertThatThrownBy(() -> service.update(tipoAmbitoGeograficoUpdated))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 TipoAmbitoGeografico
    List<TipoAmbitoGeografico> tipoAmbitoGeograficos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tipoAmbitoGeograficos.add(generarMockTipoAmbitoGeografico(i, "TipoAmbitoGeografico" + String.format("%03d", i)));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<TipoAmbitoGeografico>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoAmbitoGeografico>>() {
          @Override
          public Page<TipoAmbitoGeografico> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tipoAmbitoGeograficos.size() ? tipoAmbitoGeograficos.size() : toIndex;
            List<TipoAmbitoGeografico> content = tipoAmbitoGeograficos.subList(fromIndex, toIndex);
            Page<TipoAmbitoGeografico> page = new PageImpl<>(content, pageable, tipoAmbitoGeograficos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoAmbitoGeografico> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los TipoAmbitoGeografico del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      TipoAmbitoGeografico tipoAmbitoGeografico = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(tipoAmbitoGeografico.getNombre())
          .isEqualTo("TipoAmbitoGeografico" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 TipoAmbitoGeografico
    List<TipoAmbitoGeografico> tipoAmbitoGeograficos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tipoAmbitoGeograficos.add(generarMockTipoAmbitoGeografico(i, "TipoAmbitoGeografico" + String.format("%03d", i)));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<TipoAmbitoGeografico>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoAmbitoGeografico>>() {
          @Override
          public Page<TipoAmbitoGeografico> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tipoAmbitoGeograficos.size() ? tipoAmbitoGeograficos.size() : toIndex;
            List<TipoAmbitoGeografico> content = tipoAmbitoGeograficos.subList(fromIndex, toIndex);
            Page<TipoAmbitoGeografico> page = new PageImpl<>(content, pageable, tipoAmbitoGeograficos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoAmbitoGeografico> page = service.findAllTodos(null, paging);

    // then: Devuelve la pagina 3 con los TipoAmbitoGeografico del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      TipoAmbitoGeografico tipoAmbitoGeografico = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(tipoAmbitoGeografico.getNombre())
          .isEqualTo("TipoAmbitoGeografico" + String.format("%03d", i));
    }
  }

  @Test
  public void findById_ReturnsTipoAmbitoGeografico() {
    // given: Un TipoAmbitoGeografico con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockTipoAmbitoGeografico(idBuscado)));

    // when: Buscamos el TipoAmbitoGeografico por su id
    TipoAmbitoGeografico tipoAmbitoGeografico = service.findById(idBuscado);

    // then: el TipoAmbitoGeografico
    Assertions.assertThat(tipoAmbitoGeografico).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoAmbitoGeografico.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(tipoAmbitoGeografico.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(tipoAmbitoGeografico.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsTipoAmbitoGeograficoNotFoundException() throws Exception {
    // given: Ningun TipoAmbitoGeografico con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el TipoAmbitoGeografico por su id
    // then: lanza un TipoAmbitoGeograficoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(TipoAmbitoGeograficoNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto TipoAmbitoGeografico
   * 
   * @param id id del TipoAmbitoGeografico
   * @return el objeto TipoAmbitoGeografico
   */
  private TipoAmbitoGeografico generarMockTipoAmbitoGeografico(Long id) {
    return generarMockTipoAmbitoGeografico(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoAmbitoGeografico
   * 
   * @param id     id del TipoAmbitoGeografico
   * @param nombre nombre del TipoAmbitoGeografico
   * @return el objeto TipoAmbitoGeografico
   */
  private TipoAmbitoGeografico generarMockTipoAmbitoGeografico(Long id, String nombre) {
    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(id);
    tipoAmbitoGeografico.setNombre(nombre);
    tipoAmbitoGeografico.setActivo(true);

    return tipoAmbitoGeografico;
  }

}
