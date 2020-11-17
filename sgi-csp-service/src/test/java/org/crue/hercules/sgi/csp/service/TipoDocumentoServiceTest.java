package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoDocumentoServiceImpl;
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
 * TipoDocumentoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class TipoDocumentoServiceTest extends BaseServiceTest {

  @Mock
  private TipoDocumentoRepository tipoDocumentoRepository;

  private TipoDocumentoService tipoDocumentoService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoDocumentoService = new TipoDocumentoServiceImpl(tipoDocumentoRepository);
  }

  @Test
  public void create_ReturnsTipoDocumento() {
    // given: Un nuevo TipoDocumento
    TipoDocumento tipoDocumento = generarMockTipoDocumento(null);

    BDDMockito.given(tipoDocumentoRepository.findByNombre(tipoDocumento.getNombre())).willReturn(Optional.empty());

    BDDMockito.given(tipoDocumentoRepository.save(tipoDocumento)).will((InvocationOnMock invocation) -> {
      TipoDocumento tipoDocumentoCreado = invocation.getArgument(0);
      tipoDocumentoCreado.setId(1L);
      return tipoDocumentoCreado;
    });

    // when: Creamos el TipoDocumento
    TipoDocumento tipoDocumentoCreado = tipoDocumentoService.create(tipoDocumento);

    // then: El TipoDocumento se crea correctamente
    Assertions.assertThat(tipoDocumentoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoDocumentoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoDocumentoCreado.getNombre()).as("getNombre").isEqualTo(tipoDocumento.getNombre());
    Assertions.assertThat(tipoDocumentoCreado.getActivo()).as("getActivo").isEqualTo(tipoDocumento.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoDocumento que ya tiene id
    TipoDocumento tipoDocumentoNew = generarMockTipoDocumento(1L);

    // when: Creamos el TipoDocumento
    // then: Lanza una excepcion porque el TipoDocumento ya tiene id
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.create(tipoDocumentoNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoDocumento con un nombre que ya existe
    TipoDocumento tipoDocumentoNew = generarMockTipoDocumento(null, "nombreRepetido");
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L, "nombreRepetido");

    BDDMockito.given(tipoDocumentoRepository.findByNombre(tipoDocumentoNew.getNombre()))
        .willReturn(Optional.of(tipoDocumento));

    // when: Creamos el TipoDocumento
    // then: Lanza una excepcion porque hay otro TipoDocumento con ese nombre
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.create(tipoDocumentoNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsTipoDocumento() {
    // given: Un nuevo TipoDocumento con el nombre actualizado
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    TipoDocumento tipoDocumentoNombreActualizado = generarMockTipoDocumento(1L, "NombreActualizado");

    BDDMockito.given(tipoDocumentoRepository.findByNombre(tipoDocumentoNombreActualizado.getNombre()))
        .willReturn(Optional.of(tipoDocumento));

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoDocumento));
    BDDMockito.given(tipoDocumentoRepository.save(ArgumentMatchers.<TipoDocumento>any()))
        .willReturn(tipoDocumentoNombreActualizado);

    // when: Actualizamos el TipoDocumento
    TipoDocumento tipoDocumentoActualizado = tipoDocumentoService.update(tipoDocumentoNombreActualizado);

    // then: El TipoDocumento se actualiza correctamente.
    Assertions.assertThat(tipoDocumentoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoDocumentoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoDocumentoActualizado.getNombre()).as("getNombre()")
        .isEqualTo(tipoDocumentoNombreActualizado.getNombre());

  }

  @Test
  public void update_WithIdNotExist_ThrowsTipoDocumentoNotFoundException() {
    // given: Un TipoDocumento a actualizar con un id que no existe
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L, "TipoDocumento");

    // when: Actualizamos el TipoDocumento
    // then: Lanza una excepcion porque el TipoDocumento no existe
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.update(tipoDocumento))
        .isInstanceOf(TipoDocumentoNotFoundException.class);
  }

  @Test
  public void disable_ReturnsTipoDocumento() {
    // given: Un nuevo TipoDocumento activo
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoDocumento));
    BDDMockito.given(tipoDocumentoRepository.save(ArgumentMatchers.<TipoDocumento>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el TipoDocumento
    TipoDocumento tipoDocumentoActualizado = tipoDocumentoService.disable(tipoDocumento.getId());

    // then: El TipoDocumento se desactiva correctamente.
    Assertions.assertThat(tipoDocumentoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoDocumentoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoDocumentoActualizado.getNombre()).as("getNombre()").isEqualTo(tipoDocumento.getNombre());
    Assertions.assertThat(tipoDocumentoActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoDocumento.getDescripcion());
    Assertions.assertThat(tipoDocumentoActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithIdNotExist_ThrowsTipoDocumentoNotFoundException() {
    // given: Un id de un TipoDocumento que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el TipoDocumento
    // then: Lanza una excepcion porque el TipoDocumento no existe
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.disable(idNoExiste))
        .isInstanceOf(TipoDocumentoNotFoundException.class);
  }

  @Test
  public void update_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoDocumento con un nombre que ya existe
    TipoDocumento tipoDocumentoUpdated = generarMockTipoDocumento(1L, "nombreRepetido");
    TipoDocumento tipoDocumento = generarMockTipoDocumento(2L, "nombreRepetido");

    BDDMockito.given(tipoDocumentoRepository.findByNombre(tipoDocumentoUpdated.getNombre()))
        .willReturn(Optional.of(tipoDocumento));

    // when: Actualizamos el TipoDocumento
    // then: Lanza una excepcion porque ya existe otro TipoDocumento con ese nombre
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.update(tipoDocumentoUpdated))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 TipoDocumento
    List<TipoDocumento> tiposDocumento = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposDocumento.add(generarMockTipoDocumento(i, "TipoDocumento" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoDocumentoRepository.findAll(ArgumentMatchers.<Specification<TipoDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposDocumento.size() ? tiposDocumento.size() : toIndex;
            List<TipoDocumento> content = tiposDocumento.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tiposDocumento.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoDocumento> page = tipoDocumentoService.findAll(null, paging);

    // then: Devuelve la pagina 3 con los TipoDocumento del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      TipoDocumento tipoDocumento = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 TipoDocumento
    List<TipoDocumento> tiposDocumento = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposDocumento.add(generarMockTipoDocumento(i, "TipoDocumento" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoDocumentoRepository.findAll(ArgumentMatchers.<Specification<TipoDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposDocumento.size() ? tiposDocumento.size() : toIndex;
            List<TipoDocumento> content = tiposDocumento.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tiposDocumento.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoDocumento> page = tipoDocumentoService.findAllTodos(null, paging);

    // then: Devuelve la pagina 3 con los TipoDocumento del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      TipoDocumento tipoDocumento = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento" + String.format("%03d", i));
    }
  }

  @Test
  public void findById_ReturnsTipoDocumento() {
    // given: Un TipoDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(tipoDocumentoRepository.findById(idBuscado))
        .willReturn(Optional.of(generarMockTipoDocumento(idBuscado)));

    // when: Buscamos el TipoDocumento por su id
    TipoDocumento tipoDocumento = tipoDocumentoService.findById(idBuscado);

    // then: el TipoDocumento
    Assertions.assertThat(tipoDocumento).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoDocumento.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(tipoDocumento.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(tipoDocumento.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(tipoDocumento.getActivo()).as("getActivo()").isEqualTo(true);

  }

  @Test
  public void findById_WithIdNotExist_ThrowsTipoDocumentoNotFoundException() throws Exception {
    // given: Ningun TipoDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(tipoDocumentoRepository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el TipoDocumento por su id
    // then: lanza un TipoDocumentoNotFoundException
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.findById(idBuscado))
        .isInstanceOf(TipoDocumentoNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id) {
    return generarMockTipoDocumento(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id, String nombre) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setDescripcion("descripcion-" + id);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

}