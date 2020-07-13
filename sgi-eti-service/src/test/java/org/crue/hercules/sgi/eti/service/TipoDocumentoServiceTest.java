package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoDocumentoServiceImpl;
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
public class TipoDocumentoServiceTest {

  @Mock
  private TipoDocumentoRepository tipoDocumentoRepository;

  private TipoDocumentoService tipoDocumentoService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoDocumentoService = new TipoDocumentoServiceImpl(tipoDocumentoRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoDocumento() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    BDDMockito.given(tipoDocumentoRepository.findById(1L))
        .willReturn(Optional.of(generarMockTipoDocumento(1L, "TipoDocumento1", comite)));

    TipoDocumento tipoDocumento = tipoDocumentoService.findById(1L);

    Assertions.assertThat(tipoDocumento.getId()).isEqualTo(1L);

    Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento1");

  }

  @Test
  public void find_NotFound_ThrowsTipoDocumentoNotFoundException() throws Exception {
    BDDMockito.given(tipoDocumentoRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoDocumentoService.findById(1L))
        .isInstanceOf(TipoDocumentoNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoDocumento() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    // given: Un nuevo TipoDocumento
    TipoDocumento tipoDocumentoNew = generarMockTipoDocumento(null, "TipoDocumentoNew", comite);

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L, "TipoDocumentoNew", comite);

    BDDMockito.given(tipoDocumentoRepository.save(tipoDocumentoNew)).willReturn(tipoDocumento);

    // when: Creamos el tipo Documento
    TipoDocumento tipoDocumentoCreado = tipoDocumentoService.create(tipoDocumentoNew);

    // then: El tipo Documento se crea correctamente
    Assertions.assertThat(tipoDocumentoCreado).isNotNull();
    Assertions.assertThat(tipoDocumentoCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoDocumentoCreado.getNombre()).isEqualTo("TipoDocumentoNew");
    Assertions.assertThat(tipoDocumentoCreado.getComite()).isEqualTo(comite);
  }

  @Test
  public void create_TipoDocumentoWithId_ThrowsIllegalArgumentException() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    // given: Un nuevo tipo de Documento que ya tiene id
    TipoDocumento tipoDocumentoNew = generarMockTipoDocumento(1L, "TipoDocumentoNew", comite);

    // when: Creamos el tipo de Documento
    // then: Lanza una excepcion porque el tipo Documento ya tiene id
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.create(tipoDocumentoNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsTipoDocumento() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    // given: Un nuevo tipo Documento con el servicio actualizado
    TipoDocumento tipoDocumentoServicioActualizado = generarMockTipoDocumento(1L, "TipoDocumento1 actualizada", comite);

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L, "TipoDocumento1", comite);

    BDDMockito.given(tipoDocumentoRepository.findById(1L)).willReturn(Optional.of(tipoDocumento));
    BDDMockito.given(tipoDocumentoRepository.save(tipoDocumento)).willReturn(tipoDocumentoServicioActualizado);

    // when: Actualizamos el tipo Documento
    TipoDocumento tipoDocumentoActualizado = tipoDocumentoService.update(tipoDocumento);

    // then: El tipo Documento se actualiza correctamente.
    Assertions.assertThat(tipoDocumentoActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoDocumentoActualizado.getNombre()).isEqualTo("TipoDocumento1 actualizada");
    Assertions.assertThat(tipoDocumentoActualizado.getComite()).isEqualTo(comite);

  }

  @Test
  public void update_ThrowsTipoDocumentoNotFoundException() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    // given: Un nuevo tipo Documento a actualizar
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L, "TipoDocumento", comite);

    // then: Lanza una excepcion porque el tipo Documento no existe
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.update(tipoDocumento))
        .isInstanceOf(TipoDocumentoNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    // given: Un TipoDocumento que venga sin id
    TipoDocumento tipoDocumento = generarMockTipoDocumento(null, "TipoDocumento", comite);

    Assertions.assertThatThrownBy(
        // when: update TipoDocumento
        () -> tipoDocumentoService.update(tipoDocumento))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> tipoDocumentoService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsTipoDocumentoNotFoundException() {
    // given: Id no existe
    BDDMockito.given(tipoDocumentoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> tipoDocumentoService.delete(1L))
        // then: Lanza TipoDocumentoNotFoundException
        .isInstanceOf(TipoDocumentoNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesTipoDocumento() {
    // given: Id existente
    BDDMockito.given(tipoDocumentoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(tipoDocumentoRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tipoDocumentoService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoDocumentoList() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    // given: One hundred TipoDocumento
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoDocumentos.add(generarMockTipoDocumento(Long.valueOf(i), "TipoDocumento" + String.format("%03d", i), comite));
    }

    BDDMockito.given(tipoDocumentoRepository.findAll(ArgumentMatchers.<Specification<TipoDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(tipoDocumentos));

    // when: find unlimited
    Page<TipoDocumento> page = tipoDocumentoService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoDocumentos
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    // given: One hundred TipoDocumentos
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoDocumentos.add(generarMockTipoDocumento(Long.valueOf(i), "TipoDocumento" + String.format("%03d", i), comite));
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
            List<TipoDocumento> content = tipoDocumentos.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tipoDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoDocumento> page = tipoDocumentoService.findAll(null, paging);

    // then: A Page with ten TipoDocumentos are returned containing
    // nombre='TipoDocumento031' to 'TipoDocumento040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoDocumento tipoDocumento = page.getContent().get(i);
      Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id     id del TipoDocumento
   * @param nombre nombre del TipoDocumento
   * @return el objeto TipoDocumento
   */

  public TipoDocumento generarMockTipoDocumento(Long id, String nombre, Comite comite) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setComite(comite);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }
}