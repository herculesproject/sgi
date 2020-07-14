package org.crue.hercules.sgi.eti.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.InformeFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.InformeFormulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.InformeFormularioRepository;
import org.crue.hercules.sgi.eti.service.impl.InformeFormularioServiceImpl;
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
 * InformeFormularioServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class InformeFormularioServiceTest {

  @Mock
  private InformeFormularioRepository informeFormularioRepository;

  private InformeFormularioService informeFormularioService;

  @BeforeEach
  public void setUp() throws Exception {
    informeFormularioService = new InformeFormularioServiceImpl(informeFormularioRepository);
  }

  @Test
  public void find_WithId_ReturnsInformeFormulario() {
    BDDMockito.given(informeFormularioRepository.findById(1L))
        .willReturn(Optional.of(generarMockInformeFormulario(1L, "DocumentoFormulario1")));

    InformeFormulario informeFormulario = informeFormularioService.findById(1L);

    Assertions.assertThat(informeFormulario.getId()).isEqualTo(1L);

    Assertions.assertThat(informeFormulario.getDocumentoRef()).isEqualTo("DocumentoFormulario1");

  }

  @Test
  public void find_NotFound_ThrowsInformeFormularioNotFoundException() throws Exception {
    BDDMockito.given(informeFormularioRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> informeFormularioService.findById(1L))
        .isInstanceOf(InformeFormularioNotFoundException.class);
  }

  @Test
  public void create_ReturnsInformeFormulario() {
    // given: Un nuevo InformeFormulario
    InformeFormulario informeFormularioNew = generarMockInformeFormulario(null, "DocumentoFormularioNew");

    InformeFormulario informeFormulario = generarMockInformeFormulario(1L, "DocumentoFormularioNew");

    BDDMockito.given(informeFormularioRepository.save(informeFormularioNew)).willReturn(informeFormulario);

    // when: Creamos el InformeFormulario
    InformeFormulario informeFormularioCreado = informeFormularioService.create(informeFormularioNew);

    // then: El InformeFormulario se crea correctamente
    Assertions.assertThat(informeFormularioCreado).isNotNull();
    Assertions.assertThat(informeFormularioCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(informeFormularioCreado.getDocumentoRef()).isEqualTo("DocumentoFormularioNew");
  }

  @Test
  public void create_InformeFormularioWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo InformeFromulario que ya tiene id
    InformeFormulario informeFormularioNew = generarMockInformeFormulario(1L, "DocumentoFormularioNew");
    // when: Creamos el informeFormulario
    // then: Lanza una excepcion porque el informeFormulario ya tiene id
    Assertions.assertThatThrownBy(() -> informeFormularioService.create(informeFormularioNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsInformeFormulario() {
    // given: Un nuevo informeFormulario con el servicio actualizado
    InformeFormulario informeFormularioServicioActualizado = generarMockInformeFormulario(1L,
        "DocumentoFormulario1 actualizado");

    InformeFormulario informeFormulario = generarMockInformeFormulario(1L, "DocumentoFormulario1");

    BDDMockito.given(informeFormularioRepository.findById(1L)).willReturn(Optional.of(informeFormulario));
    BDDMockito.given(informeFormularioRepository.save(informeFormulario))
        .willReturn(informeFormularioServicioActualizado);

    // when: Actualizamos el informeFormulario
    InformeFormulario informeFormularioActualizado = informeFormularioService.update(informeFormulario);

    // then: El informeFormulario se actualiza correctamente.
    Assertions.assertThat(informeFormularioActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(informeFormularioActualizado.getDocumentoRef()).isEqualTo("DocumentoFormulario1 actualizado");

  }

  @Test
  public void update_ThrowsInformeFormularioNotFoundException() {
    // given: Un nuevo informeFormulario a actualizar
    InformeFormulario informeFormulario = generarMockInformeFormulario(1L, "DocumentoFormulario");

    // then: Lanza una excepcion porque el informeFormulario no existe
    Assertions.assertThatThrownBy(() -> informeFormularioService.update(informeFormulario))
        .isInstanceOf(InformeFormularioNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un InformeFormulario que venga sin id
    InformeFormulario informeFormulario = generarMockInformeFormulario(null, "DocumentoFormulario");

    Assertions.assertThatThrownBy(
        // when: update InformeFormulario
        () -> informeFormularioService.update(informeFormulario))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> informeFormularioService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsInformeFormularioNotFoundException() {
    // given: Id no existe
    BDDMockito.given(informeFormularioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> informeFormularioService.delete(1L))
        // then: Lanza InformeFormularioNotFoundException
        .isInstanceOf(InformeFormularioNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesInformeFormulario() {
    // given: Id existente
    BDDMockito.given(informeFormularioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(informeFormularioRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> informeFormularioService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullInformeFormularioList() {
    // given: One hundred InformeFormulario
    List<InformeFormulario> informeFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      informeFormularios
          .add(generarMockInformeFormulario(Long.valueOf(i), "DocumentoFormulario" + String.format("%03d", i)));
    }

    BDDMockito.given(informeFormularioRepository.findAll(ArgumentMatchers.<Specification<InformeFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(informeFormularios));

    // when: find unlimited
    Page<InformeFormulario> page = informeFormularioService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred InformeFormularios
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred InformeFormularios
    List<InformeFormulario> informeFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      informeFormularios
          .add(generarMockInformeFormulario(Long.valueOf(i), "DocumentoFormulario" + String.format("%03d", i)));
    }

    BDDMockito.given(informeFormularioRepository.findAll(ArgumentMatchers.<Specification<InformeFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<InformeFormulario>>() {
          @Override
          public Page<InformeFormulario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<InformeFormulario> content = informeFormularios.subList(fromIndex, toIndex);
            Page<InformeFormulario> page = new PageImpl<>(content, pageable, informeFormularios.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<InformeFormulario> page = informeFormularioService.findAll(null, paging);

    // then: A Page with ten InformeFormularios are returned containing
    // descripcion='InformeFormulario031' to 'InformeFormulario040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      InformeFormulario informeFormulario = page.getContent().get(i);
      Assertions.assertThat(informeFormulario.getDocumentoRef())
          .isEqualTo("DocumentoFormulario" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto InformeFormulario
   * 
   * @param id           id del InformeFormulario
   * @param documentoRef la referencia del documento
   * @return el objeto InformeFormulario
   */

  public InformeFormulario generarMockInformeFormulario(Long id, String documentoRef) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(LocalDate.now());
    peticionEvaluacion.setFechaInicio(LocalDate.now());
    peticionEvaluacion.setFuenteFinanciacionRef("Referencia fuente financiacion");
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setOtroValorSocial("Otro valor social1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setUsuarioRef("user-001");
    peticionEvaluacion.setValorSocial(3);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(id);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria1", "user-001", tipoMemoria,
        LocalDate.now(), Boolean.FALSE, LocalDate.now(), 3);

    InformeFormulario informeFormulario = new InformeFormulario();
    informeFormulario.setId(id);
    informeFormulario.setDocumentoRef(documentoRef);
    informeFormulario.setMemoria(memoria);
    informeFormulario.setVersion(3);

    return informeFormulario;
  }
}