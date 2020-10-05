package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.DocumentacionMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.eti.service.impl.DocumentacionMemoriaServiceImpl;
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
 * DocumentacionMemoriaServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class DocumentacionMemoriaServiceTest {

  @Mock
  private DocumentacionMemoriaRepository documentacionMemoriaRepository;

  @Mock
  private MemoriaRepository memoriaRepository;

  @Mock
  private TipoDocumentoRepository tipoDocumentoRepository;

  private DocumentacionMemoriaService documentacionMemoriaService;

  @Mock
  private TipoDocumentoService tipoDocumentoService;

  @BeforeEach
  public void setUp() throws Exception {
    documentacionMemoriaService = new DocumentacionMemoriaServiceImpl(documentacionMemoriaRepository, memoriaRepository,
        tipoDocumentoRepository);
  }

  @Test
  public void find_WithId_ReturnsDocumentacionMemoria() {

    Memoria memoria = generarMockMemoria(1L, "Memoria1");
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    BDDMockito.given(documentacionMemoriaRepository.findById(1L))
        .willReturn(Optional.of(generarMockDocumentacionMemoria(1L, memoria, tipoDocumento)));

    DocumentacionMemoria documentacionMemoria = documentacionMemoriaService.findById(1L);

    Assertions.assertThat(documentacionMemoria.getId()).isEqualTo(1L);
    Assertions.assertThat(documentacionMemoria.getMemoria().getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(documentacionMemoria.getTipoDocumento().getNombre()).isEqualTo("TipoDocumento1");
    Assertions.assertThat(documentacionMemoria.getDocumentoRef()).isEqualTo("doc-001");

  }

  @Test
  public void find_NotFound_ThrowsDocumentacionMemoriaNotFoundException() throws Exception {
    BDDMockito.given(documentacionMemoriaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.findById(1L))
        .isInstanceOf(DocumentacionMemoriaNotFoundException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> documentacionMemoriaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsDocumentacionMemoriaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(documentacionMemoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> documentacionMemoriaService.delete(1L))
        // then: Lanza DocumentacionMemoriaNotFoundException
        .isInstanceOf(DocumentacionMemoriaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesDocumentacionMemoria() {
    // given: Id existente
    BDDMockito.given(documentacionMemoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(documentacionMemoriaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> documentacionMemoriaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullDocumentacionMemoriaList() {

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    // given: One hundred DocumentacionMemoria
    List<DocumentacionMemoria> documentacionMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      documentacionMemorias.add(generarMockDocumentacionMemoria(Long.valueOf(i),
          generarMockMemoria(Long.valueOf(i), "Memoria" + String.format("%03d", i)), tipoDocumento));
    }

    BDDMockito.given(documentacionMemoriaRepository.findAll(ArgumentMatchers.<Specification<DocumentacionMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(documentacionMemorias));

    // when: find unlimited
    Page<DocumentacionMemoria> page = documentacionMemoriaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred DocumentacionMemorias
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    // given: One hundred DocumentacionMemorias
    List<DocumentacionMemoria> documentacionMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      documentacionMemorias.add(generarMockDocumentacionMemoria(Long.valueOf(i),
          generarMockMemoria(Long.valueOf(i), "Memoria" + String.format("%03d", i)), tipoDocumento));
    }

    BDDMockito.given(documentacionMemoriaRepository.findAll(ArgumentMatchers.<Specification<DocumentacionMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<DocumentacionMemoria>>() {
          @Override
          public Page<DocumentacionMemoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<DocumentacionMemoria> content = documentacionMemorias.subList(fromIndex, toIndex);
            Page<DocumentacionMemoria> page = new PageImpl<>(content, pageable, documentacionMemorias.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<DocumentacionMemoria> page = documentacionMemoriaService.findAll(null, paging);

    // then: A Page with ten DocumentacionMemorias are returned containing
    // memoria.titulo='Memoria031' to 'Memoria040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      DocumentacionMemoria documentacionMemoria = page.getContent().get(i);
      Assertions.assertThat(documentacionMemoria.getMemoria().getTitulo())
          .isEqualTo("Memoria" + String.format("%03d", j));
    }
  }

  @Test
  public void findAllByMemoriaIdNull() {
    // given: EL id de la memoria sea null
    Long memoriaId = null;
    try {
      // when: se listar sus evaluaciones
      documentacionMemoriaService.findByMemoriaId(memoriaId, Pageable.unpaged());
      Assertions.fail("El id de la memoria no puede ser nulo para mostrar su documentación");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la memoria no puede ser nulo para mostrar su documentación");
    }
  }

  @Test
  public void findAllByMemoriaIdValid() {
    // given: EL id de la memoria es valido
    Long memoriaId = 12L;
    Memoria memoria = generarMockMemoria(memoriaId, "Titulo");
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> response = new LinkedList<DocumentacionMemoria>();
    response.add(generarMockDocumentacionMemoria(Long.valueOf(1), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(3), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(5), memoria, tipoDocumento));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<DocumentacionMemoria> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito
        .given(
            documentacionMemoriaService.findByMemoriaId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<DocumentacionMemoria> result = documentacionMemoriaService.findByMemoriaId(memoriaId, pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findDocumentacionFormularioMemoriaIdValid() {
    // given: EL id de la memoria es valido
    Long memoriaId = 12L;
    Memoria memoria = generarMockMemoria(memoriaId, "Titulo");

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(memoria));

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> response = new LinkedList<DocumentacionMemoria>();
    response.add(generarMockDocumentacionMemoria(Long.valueOf(1), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(3), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(5), memoria, tipoDocumento));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<DocumentacionMemoria> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(documentacionMemoriaRepository.findAll(ArgumentMatchers.<Specification<DocumentacionMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<DocumentacionMemoria> result = documentacionMemoriaService.findDocumentacionFormularioMemoria(memoriaId,
        pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findDocumentacionFormularioMemoriaIdNotValid() {
    // given: EL id de la memoria sea null
    Long memoriaId = null;
    try {
      // when: se listar sus evaluaciones
      documentacionMemoriaService.findDocumentacionFormularioMemoria(memoriaId, Pageable.unpaged());
      Assertions.fail("El id de la memoria no puede ser nulo para mostrar su documentación");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la memoria no puede ser nulo para mostrar su documentación");
    }
  }

  @Test
  public void findDocumentacionSeguimientoAnualIdValid() {
    // given: EL id de la memoria es valido
    Long memoriaId = 12L;
    Memoria memoria = generarMockMemoria(memoriaId, "Titulo");

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(memoria));

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> response = new LinkedList<DocumentacionMemoria>();
    response.add(generarMockDocumentacionMemoria(Long.valueOf(1), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(3), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(5), memoria, tipoDocumento));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<DocumentacionMemoria> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(documentacionMemoriaRepository.findAll(ArgumentMatchers.<Specification<DocumentacionMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<DocumentacionMemoria> result = documentacionMemoriaService.findDocumentacionSeguimientoAnual(memoriaId,
        pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findDocumentacionSeguimientoAnualIdNotValid() {
    // given: EL id de la memoria sea null
    Long memoriaId = null;
    try {
      // when: se listar sus evaluaciones
      documentacionMemoriaService.findDocumentacionSeguimientoAnual(memoriaId, Pageable.unpaged());
      Assertions.fail("El id de la memoria no puede ser nulo para mostrar su documentación");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la memoria no puede ser nulo para mostrar su documentación");
    }
  }

  @Test
  public void findDocumentacionSeguimientoFinalIdValid() {
    // given: EL id de la memoria es valido
    Long memoriaId = 12L;
    Memoria memoria = generarMockMemoria(memoriaId, "Titulo");

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(memoria));

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> response = new LinkedList<DocumentacionMemoria>();
    response.add(generarMockDocumentacionMemoria(Long.valueOf(1), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(3), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(5), memoria, tipoDocumento));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<DocumentacionMemoria> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(documentacionMemoriaRepository.findAll(ArgumentMatchers.<Specification<DocumentacionMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<DocumentacionMemoria> result = documentacionMemoriaService.findDocumentacionSeguimientoFinal(memoriaId,
        pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findDocumentacionSeguimientoFinallIdNotValid() {
    // given: EL id de la memoria sea null
    Long memoriaId = null;
    try {
      // when: se listar sus evaluaciones
      documentacionMemoriaService.findDocumentacionSeguimientoFinal(memoriaId, Pageable.unpaged());
      Assertions.fail("El id de la memoria no puede ser nulo para mostrar su documentación");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la memoria no puede ser nulo para mostrar su documentación");
    }
  }

  @Test
  public void findDocumentacionRetrospectivaIdValid() {
    // given: EL id de la memoria es valido
    Long memoriaId = 12L;
    Memoria memoria = generarMockMemoria(memoriaId, "Titulo");

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(memoria));

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> response = new LinkedList<DocumentacionMemoria>();
    response.add(generarMockDocumentacionMemoria(Long.valueOf(1), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(3), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(5), memoria, tipoDocumento));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<DocumentacionMemoria> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(documentacionMemoriaRepository.findAll(ArgumentMatchers.<Specification<DocumentacionMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<DocumentacionMemoria> result = documentacionMemoriaService.findDocumentacionRetrospectiva(memoriaId, pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findDocumentacionRetrospectivaIdNotValid() {
    // given: EL id de la memoria sea null
    Long memoriaId = null;
    try {
      // when: se listar sus evaluaciones
      documentacionMemoriaService.findDocumentacionRetrospectiva(memoriaId, Pageable.unpaged());
      Assertions.fail("El id de la memoria no puede ser nulo para mostrar su documentación");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la memoria no puede ser nulo para mostrar su documentación");
    }
  }

  /**
   * Función que devuelve un objeto DocumentacionMemoria
   * 
   * @param id            id de DocumentacionMemoria
   * @param memoria       la Memoria de DocumentacionMemoria
   * @param tipoDocumento el TipoDocumento de DocumentacionMemoria
   * @return el objeto DocumentacionMemoria
   */

  public DocumentacionMemoria generarMockDocumentacionMemoria(Long id, Memoria memoria, TipoDocumento tipoDocumento) {

    DocumentacionMemoria documentacionMemoria = new DocumentacionMemoria();
    documentacionMemoria.setId(id);
    documentacionMemoria.setMemoria(memoria);
    documentacionMemoria.setTipoDocumento(tipoDocumento);
    documentacionMemoria.setDocumentoRef("doc-00" + id);
    documentacionMemoria.setAportado(Boolean.TRUE);

    return documentacionMemoria;
  }

  /**
   * Función que devuelve un objeto Memoria
   * 
   * @param id     id del Memoria
   * @param titulo título de la memoria
   * @return el objeto Memoria
   */

  public Memoria generarMockMemoria(Long id, String titulo) {

    Memoria memoria = new Memoria();
    memoria.setId(id);
    memoria.setTitulo(titulo);

    return memoria;
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */

  public TipoDocumento generarMockTipoDocumento(Long id) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre("TipoDocumento" + id);

    return tipoDocumento;
  }

}