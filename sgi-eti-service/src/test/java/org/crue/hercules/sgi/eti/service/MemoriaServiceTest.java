package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.service.impl.MemoriaServiceImpl;
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
 * MemoriaServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class MemoriaServiceTest {

  @Mock
  private MemoriaRepository memoriaRepository;

  private MemoriaService memoriaService;

  @BeforeEach
  public void setUp() throws Exception {
    memoriaService = new MemoriaServiceImpl(memoriaRepository);
  }

  @Test
  public void find_WithId_ReturnsMemoria() {

    BDDMockito.given(memoriaRepository.findById(1L))
        .willReturn(Optional.of(generarMockMemoria(1L, "numRef-5598", "Memoria1", 1)));

    Memoria memoria = memoriaService.findById(1L);

    Assertions.assertThat(memoria.getId()).isEqualTo(1L);
    Assertions.assertThat(memoria.getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(memoria.getVersion()).isEqualTo(1);
    Assertions.assertThat(memoria.getNumReferencia()).isEqualTo("numRef-5598");

  }

  @Test
  public void find_NotFound_ThrowsMemoriaNotFoundException() throws Exception {
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.findById(1L)).isInstanceOf(MemoriaNotFoundException.class);
  }

  @Test
  public void create_ReturnsMemoria() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "numRef-5598", "MemoriaNew", 1);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "MemoriaNew", 1);

    BDDMockito.given(memoriaRepository.save(memoriaNew)).willReturn(memoria);

    // when: Creamos la memoria
    Memoria memoriaCreado = memoriaService.create(memoriaNew);

    // then: La memoria se crea correctamente
    Assertions.assertThat(memoriaCreado).isNotNull();
    Assertions.assertThat(memoriaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaCreado.getTitulo()).isEqualTo("MemoriaNew");
    Assertions.assertThat(memoriaCreado.getNumReferencia()).isEqualTo("numRef-5598");
  }

  @Test
  public void create_MemoriaWithId_ThrowsIllegalArgumentException() {
    // given: Una nueva Memoria que ya tiene id
    Memoria memoriaNew = generarMockMemoria(1L, "numRef-5598", "MemoriaNew", 1);
    // when: Creamos la Memoria
    // then: Lanza una excepcion porque la Memoria ya tiene id
    Assertions.assertThatThrownBy(() -> memoriaService.create(memoriaNew)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsMemoria() {
    // given: Una nueva Memoria con el servicio actualizado
    Memoria memoriaServicioActualizado = generarMockMemoria(1L, "numRef-99", "Memoria 1 actualizada", 1);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1);

    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));
    BDDMockito.given(memoriaRepository.save(memoria)).willReturn(memoriaServicioActualizado);

    // when: Actualizamos la Memoria
    Memoria memoriaActualizado = memoriaService.update(memoria);

    // then: La Memoria se actualiza correctamente.
    Assertions.assertThat(memoriaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaActualizado.getTitulo()).isEqualTo("Memoria 1 actualizada");
    Assertions.assertThat(memoriaActualizado.getNumReferencia()).isEqualTo("numRef-99");

  }

  @Test
  public void update_ThrowsMemoriaNotFoundException() {
    // given: Una nueva Memoria a actualizar
    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1);

    // then: Lanza una excepcion porque la Memoria no existe
    Assertions.assertThatThrownBy(() -> memoriaService.update(memoria)).isInstanceOf(MemoriaNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Una Memoria que venga sin id
    Memoria memoria = generarMockMemoria(null, "numRef-5598", "Memoria1", 1);

    Assertions.assertThatThrownBy(
        // when: update Memoria
        () -> memoriaService.update(memoria))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> memoriaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsMemoriaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(memoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> memoriaService.delete(1L))
        // then: Lanza MemoriaNotFoundException
        .isInstanceOf(MemoriaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesMemoria() {
    // given: Id existente
    BDDMockito.given(memoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(memoriaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> memoriaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullMemoriaList() {
    // given: One hundred Memoria
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-5" + String.format("%03d", i),
          "Memoria" + String.format("%03d", i), 1));
    }

    BDDMockito
        .given(
            memoriaRepository.findAll(ArgumentMatchers.<Specification<Memoria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(memorias));

    // when: find unlimited
    Page<Memoria> page = memoriaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred Memorias
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Memorias
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-5" + String.format("%03d", i),
          "Memoria" + String.format("%03d", i), 1));
    }

    BDDMockito
        .given(
            memoriaRepository.findAll(ArgumentMatchers.<Specification<Memoria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Memoria>>() {
          @Override
          public Page<Memoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Memoria> content = memorias.subList(fromIndex, toIndex);
            Page<Memoria> page = new PageImpl<>(content, pageable, memorias.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Memoria> page = memoriaService.findAll(null, paging);

    // then: A Page with ten Memorias are returned containing
    // titulo='Memoria031' to 'Memoria040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Memoria memoria = page.getContent().get(i);
      Assertions.assertThat(memoria.getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(memoria.getNumReferencia()).isEqualTo("numRef-5" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto Memoria.
   * 
   * @param id            id del memoria.
   * @param numReferencia número de la referencia de la memoria.
   * @param titulo        titulo de la memoria.
   * @param version       version de la memoria.
   * @return el objeto Memoria
   */

  private Memoria generarMockMemoria(Long id, String numReferencia, String titulo, Integer version) {

    return new Memoria(id, numReferencia, generarMockPeticionEvaluacion(id, titulo + " PeticionEvaluacion" + id),
        generarMockComite(id, "comite" + id, true), titulo, "user-00" + id,
        generarMockTipoMemoria(1L, "TipoMemoria1", true),
        generarMockTipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), LocalDate.now(), Boolean.TRUE,
        generarMockRetrospectiva(1L), version, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico" + id);
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(LocalDate.now());
    peticionEvaluacion.setFechaInicio(LocalDate.now());
    peticionEvaluacion.setFuenteFinanciacion("Fuente financiación" + id);
    peticionEvaluacion.setObjetivos("Objetivos" + id);
    peticionEvaluacion.setOtroValorSocial("Otro valor social" + id);
    peticionEvaluacion.setResumen("Resumen" + id);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(titulo);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(3);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Función que devuelve un objeto comité.
   * 
   * @param id     identificador del comité.
   * @param comite comité.
   * @param activo indicador de activo.
   */
  private Comite generarMockComite(Long id, String comite, Boolean activo) {
    return new Comite(id, comite, activo);

  }

  /**
   * Función que devuelve un objeto tipo memoria.
   * 
   * @param id     identificador del tipo memoria.
   * @param nombre nobmre.
   * @param activo indicador de activo.
   */
  private TipoMemoria generarMockTipoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoMemoria(id, nombre, activo);

  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria.
   * 
   * @param id     identificador del TipoEstadoMemoria.
   * @param nombre nombre.
   * @param activo indicador de activo.
   */
  private TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoEstadoMemoria(id, nombre, activo);

  }

  /**
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @return Retrospectiva
   */
  private Retrospectiva generarMockRetrospectiva(Long id) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(generarMockDataEstadoRetrospectiva((id % 2 == 0) ? 2L : 1L));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()));

    return data;
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva generarMockDataEstadoRetrospectiva(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("NombreEstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }

}