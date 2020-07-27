package org.crue.hercules.sgi.eti.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.service.impl.EvaluacionServiceImpl;
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
 * EvaluacionServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class EvaluacionServiceTest {

  @Mock
  private EvaluacionRepository evaluacionRepository;

  private EvaluacionService evaluacionService;

  @BeforeEach
  public void setUp() throws Exception {
    evaluacionService = new EvaluacionServiceImpl(evaluacionRepository);
  }

  @Test
  public void find_WithId_ReturnsEvaluacion() {
    BDDMockito.given(evaluacionRepository.findById(1L)).willReturn(Optional.of(generarMockEvaluacion(1L, null)));

    Evaluacion evaluacion = evaluacionService.findById(1L);

    Assertions.assertThat(evaluacion.getId()).isEqualTo(1L);

    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen1");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getCodigo()).isEqualTo("CR-1");
    Assertions.assertThat(evaluacion.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Test
  public void find_NotFound_ThrowsEvaluacionNotFoundException() throws Exception {
    BDDMockito.given(evaluacionRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> evaluacionService.findById(1L)).isInstanceOf(EvaluacionNotFoundException.class);
  }

  @Test
  public void create_ReturnsEvaluacion() {
    // given: Una nueva Evaluacion
    Evaluacion evaluacionNew = generarMockEvaluacion(null, " New");

    Evaluacion evaluacion = generarMockEvaluacion(1L, " New");

    BDDMockito.given(evaluacionRepository.save(evaluacionNew)).willReturn(evaluacion);

    // when: Creamos la Evaluacion
    Evaluacion evaluacionCreado = evaluacionService.create(evaluacionNew);

    // then: La Evaluacion se crea correctamente
    Assertions.assertThat(evaluacionCreado).isNotNull();
    Assertions.assertThat(evaluacionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria New");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen New");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getCodigo()).isEqualTo("CR- New");
  }

  @Test
  public void create_EvaluacionWithId_ThrowsIllegalArgumentException() {
    // given: Una nueva evaluacion que ya tiene id
    Evaluacion evaluacionNew = generarMockEvaluacion(1L, " New");
    // when: Creamos la evaluacion
    // then: Lanza una excepcion porque la evaluacion ya tiene id
    Assertions.assertThatThrownBy(() -> evaluacionService.create(evaluacionNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsEvaluacion() {
    // given: Una nueva evaluacion con el servicio actualizado
    Evaluacion evaluacionServicioActualizado = generarMockEvaluacion(1L, " actualizado");

    Evaluacion evaluacion = generarMockEvaluacion(1L, null);

    BDDMockito.given(evaluacionRepository.findById(1L)).willReturn(Optional.of(evaluacion));
    BDDMockito.given(evaluacionRepository.save(evaluacion)).willReturn(evaluacionServicioActualizado);

    // when: Actualizamos la evaluacion
    Evaluacion evaluacionActualizado = evaluacionService.update(evaluacion);

    // then: La evaluacion se actualiza correctamente.
    Assertions.assertThat(evaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacionActualizado.getMemoria().getTitulo()).isEqualTo("Memoria actualizado");
    Assertions.assertThat(evaluacionActualizado.getDictamen().getNombre()).isEqualTo("Dictamen actualizado");
    Assertions.assertThat(evaluacionActualizado.getConvocatoriaReunion().getCodigo()).isEqualTo("CR- actualizado");

  }

  @Test
  public void update_ThrowsEvaluacionNotFoundException() {
    // given: Una nueva evaluacion a actualizar
    Evaluacion evaluacion = generarMockEvaluacion(1L, null);

    // then: Lanza una excepcion porque la evaluacion no existe
    Assertions.assertThatThrownBy(() -> evaluacionService.update(evaluacion))
        .isInstanceOf(EvaluacionNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Una Evaluacion que venga sin id
    Evaluacion evaluacion = generarMockEvaluacion(null, "1");

    Assertions.assertThatThrownBy(
        // when: update Evaluacion
        () -> evaluacionService.update(evaluacion))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> evaluacionService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsEvaluacionNotFoundException() {
    // given: Id no existe
    BDDMockito.given(evaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> evaluacionService.delete(1L))
        // then: Lanza EvaluacionNotFoundException
        .isInstanceOf(EvaluacionNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesEvaluacion() {
    // given: Id existente
    BDDMockito.given(evaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(evaluacionRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> evaluacionService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullEvaluacionList() {
    // given: One hundred Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito.given(evaluacionRepository.findAll(ArgumentMatchers.<Specification<Evaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    Page<Evaluacion> page = evaluacionService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred Evaluaciones
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Evaluaciones
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito.given(evaluacionRepository.findAll(ArgumentMatchers.<Specification<Evaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<Evaluacion>>() {
          @Override
          public Page<Evaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Evaluacion> content = evaluaciones.subList(fromIndex, toIndex);
            Page<Evaluacion> page = new PageImpl<>(content, pageable, evaluaciones.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Evaluacion> page = evaluacionService.findAll(null, paging);

    // then: A Page with ten Evaluaciones are returned containing
    // resumen='Evaluacion031' to 'Evaluacion040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Evaluacion evaluacion = page.getContent().get(i);
      Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getConvocatoriaReunion().getCodigo())
          .isEqualTo("CR-" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param id     id del Evaluacion
   * @param sufijo el sufijo para título y nombre
   * @return el objeto Evaluacion
   */

  public Evaluacion generarMockEvaluacion(Long id, String sufijo) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre("Dictamen" + sufijoStr);
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

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(id, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
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
    convocatoriaReunion.setCodigo("CR-" + sufijoStr);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(LocalDate.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(LocalDate.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setVersion(2);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

}