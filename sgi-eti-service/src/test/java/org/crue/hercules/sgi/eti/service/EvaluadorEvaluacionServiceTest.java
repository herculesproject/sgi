package org.crue.hercules.sgi.eti.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EvaluadorEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.EvaluadorEvaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.EvaluadorEvaluacionRepository;
import org.crue.hercules.sgi.eti.service.impl.EvaluadorEvaluacionServiceImpl;
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
 * EvaluadorEvaluacionServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class EvaluadorEvaluacionServiceTest {

  @Mock
  private EvaluadorEvaluacionRepository evaluadorEvaluacionRepository;

  private EvaluadorEvaluacionService evaluadorEvaluacionService;

  @BeforeEach
  public void setUp() throws Exception {
    evaluadorEvaluacionService = new EvaluadorEvaluacionServiceImpl(evaluadorEvaluacionRepository);
  }

  @Test
  public void find_WithId_ReturnsEvaluadorEvaluacion() {
    BDDMockito.given(evaluadorEvaluacionRepository.findById(1L))
        .willReturn(Optional.of(generarMockEvaluadorEvaluacion(1L)));

    EvaluadorEvaluacion evaluadorEvaluacion = evaluadorEvaluacionService.findById(1L);

    Assertions.assertThat(evaluadorEvaluacion.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluadorEvaluacion.getEvaluador().getResumen()).isEqualTo("Evaluador");

  }

  @Test
  public void find_NotFound_ThrowsEvaluadorEvaluacionNotFoundException() throws Exception {
    BDDMockito.given(evaluadorEvaluacionRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> evaluadorEvaluacionService.findById(1L))
        .isInstanceOf(EvaluadorEvaluacionNotFoundException.class);
  }

  @Test
  public void create_ReturnsEvaluadorEvaluacion() {
    // given: Un nuevo EvaluadorEvaluacion
    EvaluadorEvaluacion evaluadorEvaluacionNew = generarMockEvaluadorEvaluacion(null);

    EvaluadorEvaluacion evaluadorEvaluacion = generarMockEvaluadorEvaluacion(1L);

    BDDMockito.given(evaluadorEvaluacionRepository.save(evaluadorEvaluacionNew)).willReturn(evaluadorEvaluacion);

    // when: Creamos el EvaluadorEvaluacion
    EvaluadorEvaluacion evaluadorEvaluacionCreado = evaluadorEvaluacionService.create(evaluadorEvaluacionNew);

    // then: El EvaluadorEvaluacion se crea correctamente
    Assertions.assertThat(evaluadorEvaluacionCreado).isNotNull();
    Assertions.assertThat(evaluadorEvaluacionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluadorEvaluacionCreado.getEvaluador().getResumen()).isEqualTo("Evaluador");
    Assertions.assertThat(evaluadorEvaluacionCreado.getEvaluacion().getMemoria().getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(evaluadorEvaluacionCreado.getEvaluacion().getDictamen().getNombre()).isEqualTo("Dictamen1");
    Assertions.assertThat(evaluadorEvaluacionCreado.getEvaluacion().getConvocatoriaReunion().getCodigo())
        .isEqualTo("CR-1");
  }

  @Test
  public void create_EvaluadorEvaluacionWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo EvaluadorEvaluacion que ya tiene id
    EvaluadorEvaluacion evaluadorEvaluacionNew = generarMockEvaluadorEvaluacion(1L);
    // when: Creamos el EvaluadorEvaluacion
    // then: Lanza una excepcion porque el EvaluadorEvaluacion ya tiene id
    Assertions.assertThatThrownBy(() -> evaluadorEvaluacionService.create(evaluadorEvaluacionNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsEvaluadorEvaluacion() {
    // given: Un nuevo EvaluadorEvaluacion con el servicio actualizado
    EvaluadorEvaluacion evaluadorEvaluacionServicioActualizado = generarMockEvaluadorEvaluacion(1L);
    evaluadorEvaluacionServicioActualizado.getEvaluador().setResumen("Evaluador actualizado");
    evaluadorEvaluacionServicioActualizado.getEvaluacion().getMemoria().setTitulo("Memoria actualizada");
    evaluadorEvaluacionServicioActualizado.getEvaluacion().getDictamen().setNombre("Dictamen actualizado");
    evaluadorEvaluacionServicioActualizado.getEvaluacion().getConvocatoriaReunion().setCodigo("CR actualizado");

    EvaluadorEvaluacion evaluadorEvaluacion = generarMockEvaluadorEvaluacion(1L);

    BDDMockito.given(evaluadorEvaluacionRepository.findById(1L)).willReturn(Optional.of(evaluadorEvaluacion));
    BDDMockito.given(evaluadorEvaluacionRepository.save(evaluadorEvaluacion))
        .willReturn(evaluadorEvaluacionServicioActualizado);

    // when: Actualizamos el EvaluadorEvaluacion
    EvaluadorEvaluacion evaluadorEvaluacionActualizado = evaluadorEvaluacionService.update(evaluadorEvaluacion);

    // then: El EvaluadorEvaluacion se actualiza correctamente.
    Assertions.assertThat(evaluadorEvaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluadorEvaluacionActualizado.getEvaluador().getResumen())
        .isEqualTo("Evaluador actualizado");
    Assertions.assertThat(evaluadorEvaluacionActualizado.getEvaluacion().getMemoria().getTitulo())
        .isEqualTo("Memoria actualizada");
    Assertions.assertThat(evaluadorEvaluacionActualizado.getEvaluacion().getDictamen().getNombre())
        .isEqualTo("Dictamen actualizado");
    Assertions.assertThat(evaluadorEvaluacionActualizado.getEvaluacion().getConvocatoriaReunion().getCodigo())
        .isEqualTo("CR actualizado");

  }

  @Test
  public void update_ThrowsEvaluadorEvaluacionNotFoundException() {
    // given: Un nuevo EvaluadorEvaluacion a actualizar
    EvaluadorEvaluacion evaluadorEvaluacion = generarMockEvaluadorEvaluacion(1L);

    // then: Lanza una excepcion porque el EvaluadorEvaluacion no existe
    Assertions.assertThatThrownBy(() -> evaluadorEvaluacionService.update(evaluadorEvaluacion))
        .isInstanceOf(EvaluadorEvaluacionNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un EvaluadorEvaluacion que venga sin id
    EvaluadorEvaluacion evaluadorEvaluacion = generarMockEvaluadorEvaluacion(null);

    Assertions.assertThatThrownBy(
        // when: update EvaluadorEvaluacion
        () -> evaluadorEvaluacionService.update(evaluadorEvaluacion))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> evaluadorEvaluacionService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsEvaluadorEvaluacionNotFoundException() {
    // given: Id no existe
    BDDMockito.given(evaluadorEvaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> evaluadorEvaluacionService.delete(1L))
        // then: Lanza EvaluadorEvaluacionNotFoundException
        .isInstanceOf(EvaluadorEvaluacionNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesEvaluadorEvaluacion() {
    // given: Id existente
    BDDMockito.given(evaluadorEvaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(evaluadorEvaluacionRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> evaluadorEvaluacionService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullEvaluadorEvaluacionList() {
    // given: One hundred EvaluadorEvaluacion
    List<EvaluadorEvaluacion> evaluadorEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadorEvaluaciones.add(generarMockEvaluadorEvaluacion(Long.valueOf(i)));
    }

    BDDMockito.given(evaluadorEvaluacionRepository.findAll(ArgumentMatchers.<Specification<EvaluadorEvaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluadorEvaluaciones));

    // when: find unlimited
    Page<EvaluadorEvaluacion> page = evaluadorEvaluacionService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred EvaluadorEvaluaciones
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred EvaluadorEvaluaciones
    List<EvaluadorEvaluacion> evaluadorEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadorEvaluaciones.add(generarMockEvaluadorEvaluacion(Long.valueOf(i)));
    }

    BDDMockito.given(evaluadorEvaluacionRepository.findAll(ArgumentMatchers.<Specification<EvaluadorEvaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<EvaluadorEvaluacion>>() {
          @Override
          public Page<EvaluadorEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<EvaluadorEvaluacion> content = evaluadorEvaluaciones.subList(fromIndex, toIndex);
            Page<EvaluadorEvaluacion> page = new PageImpl<>(content, pageable, evaluadorEvaluaciones.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<EvaluadorEvaluacion> page = evaluadorEvaluacionService.findAll(null, paging);

    // then: A Page with ten EvaluadorEvaluaciones are returned containing
    // id='31' to '40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      EvaluadorEvaluacion EvaluadorEvaluacion = page.getContent().get(i);
      Assertions.assertThat(EvaluadorEvaluacion.getId()).isEqualTo(j);
    }
  }

  /**
   * Función que devuelve un objeto EvaluadorEvaluacion
   * 
   * @param id id del EvaluadorEvaluacion
   * @return el objeto EvaluadorEvaluacion
   */

  public EvaluadorEvaluacion generarMockEvaluadorEvaluacion(Long id) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(1L);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(LocalDate.now());
    evaluador.setFechaBaja(LocalDate.now());
    evaluador.setResumen("Evaluador");
    evaluador.setUsuarioRef("user-001");
    evaluador.setActivo(Boolean.TRUE);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre("Dictamen" + id);
    dictamen.setActivo(Boolean.TRUE);

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
    peticionEvaluacion.setFuenteFinanciacion("Fuente financiación");
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

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(id, "numRef-001", peticionEvaluacion, comite, "Memoria" + id, "user-00" + id,
        tipoMemoria, LocalDate.now(), Boolean.FALSE, LocalDate.now(), 3,
        new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE));

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(LocalDateTime.now());
    convocatoriaReunion.setFechaLimite(LocalDate.now());
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setCodigo("CR-" + id);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(LocalDate.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(LocalDate.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setVersion(2);
    evaluacion.setActivo(Boolean.TRUE);

    EvaluadorEvaluacion evaluadorEvaluacion = new EvaluadorEvaluacion(id, evaluador, evaluacion);

    return evaluadorEvaluacion;
  }
}