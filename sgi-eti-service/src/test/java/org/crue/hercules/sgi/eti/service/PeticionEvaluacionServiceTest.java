package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.sgo.UnidadVinculacionOutput;
import org.crue.hercules.sgi.eti.dto.sgp.VinculacionOutput;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.rep.MicroserviceCallException;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionDisMetodologico;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionObjetivos;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionResumen;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionTitulo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionUnidadVinculacionRepository;
import org.crue.hercules.sgi.eti.service.impl.PeticionEvaluacionServiceImpl;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiSgoService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * PeticionEvaluacionServiceTest
 */
class PeticionEvaluacionServiceTest extends BaseServiceTest {

  @Mock
  private PeticionEvaluacionRepository peticionEvaluacionRepository;

  @Mock
  private MemoriaService memoriaService;

  @Mock
  private ConfiguracionRepository configuracionRepository;

  @Mock
  private PeticionEvaluacionUnidadVinculacionRepository peticionEvaluacionUnidadVinculacionRepository;

  @Mock
  private SgiApiSgpService sgiApiSgpService;

  @Mock
  private SgiApiSgoService sgiApiSgoService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  private PeticionEvaluacionService peticionEvaluacionService;

  @BeforeEach
  void setUp() {
    peticionEvaluacionService = new PeticionEvaluacionServiceImpl(sgiConfigProperties, peticionEvaluacionRepository,
        memoriaService, configuracionRepository, peticionEvaluacionUnidadVinculacionRepository, sgiApiSgpService,
        sgiApiSgoService);
  }

  @Test
  void find_WithId_ReturnsPeticionEvaluacion() {
    BDDMockito.given(peticionEvaluacionRepository.findById(1L))
        .willReturn(Optional.of(generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1")));

    PeticionEvaluacion peticionEvaluacion = peticionEvaluacionService.findById(1L);

    Assertions.assertThat(peticionEvaluacion.getId()).isEqualTo(1L);

    Assertions.assertThat(I18nHelper.getValueForLanguage(peticionEvaluacion.getTitulo(), Language.ES))
        .isEqualTo("PeticionEvaluacion1");

  }

  @Test
  void find_NotFound_ThrowsPeticionEvaluacionNotFoundException() {
    BDDMockito.given(peticionEvaluacionRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> peticionEvaluacionService.findById(1L))
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);
  }

  @Test
  void create_ReturnsPeticionEvaluacion() {
    // given: Un nuevo PeticionEvaluacion
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacionNew");

    BDDMockito.given(peticionEvaluacionRepository.save(peticionEvaluacionNew)).willReturn(peticionEvaluacion);

    // when: Creamos el PeticionEvaluacion
    PeticionEvaluacion peticionEvaluacionCreado = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: El PeticionEvaluacion se crea correctamente
    Assertions.assertThat(peticionEvaluacionCreado).isNotNull();
    Assertions.assertThat(peticionEvaluacionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(peticionEvaluacionCreado.getTitulo(), Language.ES))
        .isEqualTo("PeticionEvaluacionNew");
  }

  @Test
  void create_WithInicializacionDesactivada_DoesNotCallEsb() {
    // given: configuración con el apartado activo y la inicialización desactivada
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");
    mockConfiguracion(Boolean.FALSE, "TIPO-DEP");
    mockSavePeticionEvaluacion();

    // when: se crea la petición
    PeticionEvaluacion created = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: no se consulta el ESB ni se crea la unidad de vinculación
    Assertions.assertThat(created.getId()).isEqualTo(1L);
    BDDMockito.then(sgiApiSgpService).shouldHaveNoInteractions();
    BDDMockito.then(peticionEvaluacionUnidadVinculacionRepository).shouldHaveNoInteractions();
  }

  @Test
  void create_WithTipoUnidadNulo_DoesNotCallEsb() {
    // given: la inicialización activa pero sin tipo de unidad configurado
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");
    mockConfiguracion(Boolean.TRUE, null);
    mockSavePeticionEvaluacion();

    // when: se crea la petición
    PeticionEvaluacion created = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: no se consulta el ESB ni se crea la unidad de vinculación
    Assertions.assertThat(created.getId()).isEqualTo(1L);
    BDDMockito.then(sgiApiSgpService).shouldHaveNoInteractions();
    BDDMockito.then(peticionEvaluacionUnidadVinculacionRepository).shouldHaveNoInteractions();
  }

  @Test
  void create_WithSolicitanteSinVinculacion_DoesNotCreateUnidadVinculacion() {
    // given: inicialización activa y el solicitante sin vinculación en el SGP
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");
    mockConfiguracion(Boolean.TRUE, "TIPO-DEP");
    mockSavePeticionEvaluacion();
    BDDMockito.given(sgiApiSgpService.findVinculacionByPersonaId("user-00null")).willReturn(Optional.empty());

    // when: se crea la petición
    PeticionEvaluacion created = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: no se consulta el SGO ni se crea la unidad de vinculación
    Assertions.assertThat(created.getId()).isEqualTo(1L);
    BDDMockito.then(sgiApiSgoService).shouldHaveNoInteractions();
    BDDMockito.then(peticionEvaluacionUnidadVinculacionRepository).shouldHaveNoInteractions();
  }

  @Test
  void create_WithVinculacionSinDepartamento_DoesNotCreateUnidadVinculacion() {
    // given: inicialización activa y la vinculación del solicitante sin
    // departamento
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");
    mockConfiguracion(Boolean.TRUE, "TIPO-DEP");
    mockSavePeticionEvaluacion();
    BDDMockito.given(sgiApiSgpService.findVinculacionByPersonaId("user-00null"))
        .willReturn(Optional.of(VinculacionOutput.builder().id("V1").build()));

    // when: se crea la petición
    PeticionEvaluacion created = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: no se consulta el SGO ni se crea la unidad de vinculación
    Assertions.assertThat(created.getId()).isEqualTo(1L);
    BDDMockito.then(sgiApiSgoService).shouldHaveNoInteractions();
    BDDMockito.then(peticionEvaluacionUnidadVinculacionRepository).shouldHaveNoInteractions();
  }

  @Test
  void create_WithUnidadNoEncontrada_DoesNotCreateUnidadVinculacion() {
    // given: inicialización activa, el solicitante con departamento y sin unidad en
    // el SGO
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");
    mockConfiguracion(Boolean.TRUE, "TIPO-DEP");
    mockSavePeticionEvaluacion();
    mockVinculacionDepartamento("DEP-1");
    BDDMockito.given(sgiApiSgoService.findUnidadVinculacionById("DEP-1")).willReturn(Optional.empty());

    // when: se crea la petición
    PeticionEvaluacion created = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: no se crea la unidad de vinculación
    Assertions.assertThat(created.getId()).isEqualTo(1L);
    BDDMockito.then(peticionEvaluacionUnidadVinculacionRepository).shouldHaveNoInteractions();
  }

  @Test
  void create_WithEsbError_CreatesPeticionWithoutUnidadVinculacion() {
    // given: inicialización activa y el SGP falla
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");
    mockConfiguracion(Boolean.TRUE, "TIPO-DEP");
    mockSavePeticionEvaluacion();
    BDDMockito.given(sgiApiSgpService.findVinculacionByPersonaId("user-00null"))
        .willThrow(new MicroserviceCallException());

    // when: se crea la petición
    PeticionEvaluacion created = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: la petición se crea sin unidad de vinculación y no se propaga el error
    Assertions.assertThat(created.getId()).isEqualTo(1L);
    BDDMockito.then(sgiApiSgoService).shouldHaveNoInteractions();
    BDDMockito.then(peticionEvaluacionUnidadVinculacionRepository).shouldHaveNoInteractions();
  }

  @Test
  void create_WithDepartamentoEncontrado_CreatesUnidadVinculacion() {
    // given: inicialización activa, el solicitante con departamento y la unidad en
    // el SGO
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");
    mockConfiguracion(Boolean.TRUE, "TIPO-DEP");
    mockSavePeticionEvaluacion();
    mockVinculacionDepartamento("DEP-1");
    mockUnidadVinculacion("DEP-1", "TIPO-DEP", Boolean.TRUE);
    mockSaveUnidadVinculacion();

    // when: se crea la petición
    PeticionEvaluacion created = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: se crea la unidad de vinculación del departamento para la petición
    Assertions.assertThat(created.getId()).isEqualTo(1L);
    BDDMockito.then(peticionEvaluacionUnidadVinculacionRepository).should()
        .save(ArgumentMatchers.<PeticionEvaluacionUnidadVinculacion>argThat(
            unidad -> unidad.getPeticionEvaluacionId().equals(1L)
                && unidad.getUnidadVinculacionRef().equals("DEP-1")));
    // and: al ser hija directa del tipo solo se consulta la propia unidad
    BDDMockito.then(sgiApiSgoService).should().findUnidadVinculacionById("DEP-1");
    BDDMockito.then(sgiApiSgoService).shouldHaveNoMoreInteractions();
  }

  @Test
  void create_WithDepartamentoEnNivelInferiorDelTipo_CreatesUnidadVinculacion() {
    // given: inicialización activa y la unidad del departamento cuelga del tipo a
    // través de otra unidad
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");
    mockConfiguracion(Boolean.TRUE, "TIPO-DEP");
    mockSavePeticionEvaluacion();
    mockVinculacionDepartamento("DEP-1");
    mockUnidadVinculacion("DEP-1", "FAC-1", Boolean.TRUE);
    mockUnidadVinculacion("FAC-1", "TIPO-DEP", Boolean.TRUE);
    mockSaveUnidadVinculacion();

    // when: se crea la petición
    PeticionEvaluacion created = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: se crea la unidad de vinculación del departamento para la petición
    Assertions.assertThat(created.getId()).isEqualTo(1L);
    BDDMockito.then(peticionEvaluacionUnidadVinculacionRepository).should()
        .save(ArgumentMatchers.<PeticionEvaluacionUnidadVinculacion>argThat(
            unidad -> unidad.getUnidadVinculacionRef().equals("DEP-1")));
  }

  @Test
  void create_WithDepartamentoDeOtroTipo_DoesNotCreateUnidadVinculacion() {
    // given: inicialización activa y la unidad con el id del departamento pertenece
    // a otro tipo
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");
    mockConfiguracion(Boolean.TRUE, "TIPO-DEP");
    mockSavePeticionEvaluacion();
    mockVinculacionDepartamento("DEP-1");
    mockUnidadVinculacion("DEP-1", "OTRO-TIPO", Boolean.TRUE);
    mockUnidadVinculacion("OTRO-TIPO", null, Boolean.TRUE);

    // when: se crea la petición
    PeticionEvaluacion created = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: no se crea la unidad de vinculación
    Assertions.assertThat(created.getId()).isEqualTo(1L);
    BDDMockito.then(peticionEvaluacionUnidadVinculacionRepository).shouldHaveNoInteractions();
  }

  @Test
  void create_WithDepartamentoInactivo_DoesNotCreateUnidadVinculacion() {
    // given: inicialización activa y la unidad del departamento está inactiva
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(null, "PeticionEvaluacionNew");
    mockConfiguracion(Boolean.TRUE, "TIPO-DEP");
    mockSavePeticionEvaluacion();
    mockVinculacionDepartamento("DEP-1");
    mockUnidadVinculacion("DEP-1", "TIPO-DEP", Boolean.FALSE);

    // when: se crea la petición
    PeticionEvaluacion created = peticionEvaluacionService.create(peticionEvaluacionNew);

    // then: no se crea la unidad de vinculación
    Assertions.assertThat(created.getId()).isEqualTo(1L);
    BDDMockito.then(peticionEvaluacionUnidadVinculacionRepository).shouldHaveNoInteractions();
  }

  @Test
  void create_PeticionEvaluacionWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo tipo de actividad que ya tiene id
    PeticionEvaluacion peticionEvaluacionNew = generarMockPeticionEvaluacion(1L, "PeticionEvaluacionNew");
    // when: Creamos el peticionEvaluacion
    // then: Lanza una excepcion porque el peticionEvaluacion ya tiene id
    Assertions.assertThatThrownBy(() -> peticionEvaluacionService.create(peticionEvaluacionNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_ReturnsPeticionEvaluacion() {
    // given: Un nuevo peticionEvaluacion con el servicio actualizado
    PeticionEvaluacion peticionEvaluacionServicioActualizado = generarMockPeticionEvaluacion(1L,
        "PeticionEvaluacion1 actualizada");

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1");

    BDDMockito.given(peticionEvaluacionRepository.findById(1L)).willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(peticionEvaluacionRepository.save(peticionEvaluacion))
        .willReturn(peticionEvaluacionServicioActualizado);

    // when: Actualizamos el peticionEvaluacion
    PeticionEvaluacion peticionEvaluacionActualizado = peticionEvaluacionService.update(peticionEvaluacion);

    // then: El peticionEvaluacion se actualiza correctamente.
    Assertions.assertThat(peticionEvaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(peticionEvaluacionActualizado.getTitulo(), Language.ES))
        .isEqualTo("PeticionEvaluacion1 actualizada");

  }

  @Test
  void update_ThrowsPeticionEvaluacionNotFoundException() {
    // given: Un nuevo peticionEvaluacion a actualizar
    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacion");

    // then: Lanza una excepcion porque el peticionEvaluacion no existe
    Assertions.assertThatThrownBy(() -> peticionEvaluacionService.update(peticionEvaluacion))
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);

  }

  @Test
  void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un PeticionEvaluacion que venga sin id
    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(null, "PeticionEvaluacion");

    Assertions.assertThatThrownBy(
        // when: update PeticionEvaluacion
        () -> peticionEvaluacionService.update(peticionEvaluacion))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> peticionEvaluacionService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_NonExistingId_ThrowsPeticionEvaluacionNotFoundException() {
    // given: Id no existe
    BDDMockito.given(peticionEvaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> peticionEvaluacionService.delete(1L))
        // then: Lanza PeticionEvaluacionNotFoundException
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);
  }

  @Test
  void delete_WithExistingId_DeletesPeticionEvaluacion() {
    PeticionEvaluacion peticionEvaluacionServicioActualizado = generarMockPeticionEvaluacion(1L,
        "PeticionEvaluacion1 actualizada");
    List<Memoria> memorias = new ArrayList<>();
    // given: Id existente
    BDDMockito.given(peticionEvaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.given(memoriaService.findAllByPeticionEvaluacionIdAndEstadoActualId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(memorias);
    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1");

    BDDMockito.given(peticionEvaluacionRepository.findById(1L)).willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(peticionEvaluacionRepository.save(peticionEvaluacion))
        .willReturn(peticionEvaluacionServicioActualizado);

    Assertions.assertThatCode(
        // when: Delete all
        () -> peticionEvaluacionService.delete(
            1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  void deleteAll_DeleteAllPeticionEvaluacion() {
    // given: el repositorio borra todas las peticiones de evaluación
    BDDMockito.doNothing().when(peticionEvaluacionRepository).deleteAll();

    // when: se borran todas las peticiones de evaluación
    peticionEvaluacionService.deleteAll();

    // then: se delega el borrado en el repositorio
    BDDMockito.verify(peticionEvaluacionRepository).deleteAll();
  }

  @Test
  void findAll_Unlimited_ReturnsFullPeticionEvaluacionList() {
    // given: One hundred PeticionEvaluacion
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito.given(peticionEvaluacionRepository.findAll(ArgumentMatchers.<Specification<PeticionEvaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(peticionEvaluaciones));

    // when: find unlimited
    Page<PeticionEvaluacion> page = peticionEvaluacionService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred PeticionEvaluaciones
    Assertions.assertThat(page.getContent()).hasSize(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  void findAll_WithPaging_ReturnsPage() {
    // given: One hundred PeticionEvaluaciones
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito.given(peticionEvaluacionRepository.findAll(ArgumentMatchers.<Specification<PeticionEvaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<PeticionEvaluacion>>() {
          @Override
          public Page<PeticionEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<PeticionEvaluacion> content = peticionEvaluaciones.subList(fromIndex, toIndex);
            return new PageImpl<>(content, pageable, peticionEvaluaciones.size());
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<PeticionEvaluacion> page = peticionEvaluacionService.findAll(null, paging);

    // then: A Page with ten PeticionEvaluaciones are returned containing
    // descripcion='PeticionEvaluacion031' to 'PeticionEvaluacion040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      PeticionEvaluacion peticionEvaluacion = page.getContent().get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(peticionEvaluacion.getTitulo(), Language.ES))
          .isEqualTo("PeticionEvaluacion" + String.format("%03d", j));
    }
  }

  @Test
  void findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria_Unlimited_ReturnsFullMemoriaPeticionEvaluacionList() {

    List<PeticionEvaluacionWithIsEliminable> peticiones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticiones.add(generarMockPeticionEvaluacionWithIsEliminable(Long.valueOf(i),
          "PeticionEvaluacion" + String.format("%03d", i)));
    }
    BDDMockito.given(peticionEvaluacionRepository.findAllPeticionEvaluacionMemoria(
        ArgumentMatchers.<Specification<Memoria>>any(), ArgumentMatchers.<Specification<PeticionEvaluacion>>any(),
        ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<String>any())).willReturn(new PageImpl<>(peticiones));

    // when: find unlimited peticiones evaluación
    Page<PeticionEvaluacionWithIsEliminable> page = peticionEvaluacionService
        .findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(null, Pageable.unpaged(),
            "user-001");

    Assertions.assertThat(page.getContent()).hasSize(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  void findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria_WithPaging_ReturnsPage() {

    List<PeticionEvaluacionWithIsEliminable> peticiones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticiones.add(generarMockPeticionEvaluacionWithIsEliminable(Long.valueOf(i),
          "PeticionEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito
        .given(peticionEvaluacionRepository.findAllPeticionEvaluacionMemoria(
            ArgumentMatchers.<Specification<Memoria>>any(), ArgumentMatchers.<Specification<PeticionEvaluacion>>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<String>any()))
        .willAnswer(new Answer<Page<PeticionEvaluacionWithIsEliminable>>() {
          @Override
          public Page<PeticionEvaluacionWithIsEliminable> answer(InvocationOnMock invocation) throws Throwable {
            List<PeticionEvaluacionWithIsEliminable> content = peticiones.subList(30, 40);
            return new PageImpl<>(content, PageRequest.of(3, 10), peticiones.size());
          }
        });

    // when: Get page=3 with pagesize=10 asignables by convocatoria
    Pageable paging = PageRequest.of(3, 10);
    Page<PeticionEvaluacionWithIsEliminable> page = peticionEvaluacionService
        .findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(null, paging, "user-001");

    // then: A Page with ten Memorias are returned containing
    // num referencia='NumRef-031' to 'NumRef-040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      PeticionEvaluacionWithIsEliminable peticion = page.getContent().get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(peticion.getTitulo(), Language.ES))
          .isEqualTo("PeticionEvaluacion" + String.format("%03d", j));
    }
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

    Set<PeticionEvaluacionTitulo> tit = new HashSet<>();
    tit.add(new PeticionEvaluacionTitulo(Language.ES, titulo));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen" + id));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos" + id));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "DiseñoMetodologico" + id));
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico(disMetodologico);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos(objetivos);
    peticionEvaluacion.setResumen(resumen);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(tit);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  private PeticionEvaluacionWithIsEliminable generarMockPeticionEvaluacionWithIsEliminable(Long id, String titulo) {
    return new PeticionEvaluacionWithIsEliminable(generarMockPeticionEvaluacion(id, titulo), true);
  }

  private void mockConfiguracion(Boolean inicializacionEnabled, String tipoRef) {
    Configuracion configuracion = new Configuracion();
    configuracion.setId(1L);
    configuracion.setPeticionEvaluacionUnidadesVinculacionEnabled(Boolean.TRUE);
    configuracion.setPeticionEvaluacionInicializarUnidadVinculacionEnabled(inicializacionEnabled);
    configuracion.setTipoUnidadVinculacionDepartamentoRef(tipoRef);
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc()).willReturn(Optional.of(configuracion));
  }

  private void mockSavePeticionEvaluacion() {
    BDDMockito.given(peticionEvaluacionRepository.save(ArgumentMatchers.<PeticionEvaluacion>any()))
        .willAnswer(invocation -> {
          PeticionEvaluacion entity = invocation.getArgument(0, PeticionEvaluacion.class);
          entity.setId(1L);
          return entity;
        });
  }

  private void mockVinculacionDepartamento(String departamentoId) {
    BDDMockito.given(sgiApiSgpService.findVinculacionByPersonaId("user-00null"))
        .willReturn(Optional.of(VinculacionOutput.builder().id("V1")
            .departamento(VinculacionOutput.DepartamentoOutput.builder().id(departamentoId).nombre("Departamento")
                .build())
            .build()));
  }

  private void mockUnidadVinculacion(String id, String predecesorId, Boolean activo) {
    BDDMockito.given(sgiApiSgoService.findUnidadVinculacionById(id))
        .willReturn(Optional.of(UnidadVinculacionOutput.builder().id(id).predecesorId(predecesorId)
            .activo(activo).build()));
  }

  private void mockSaveUnidadVinculacion() {
    BDDMockito.given(peticionEvaluacionUnidadVinculacionRepository
        .save(ArgumentMatchers.<PeticionEvaluacionUnidadVinculacion>any()))
        .willAnswer(invocation -> {
          PeticionEvaluacionUnidadVinculacion entity = invocation.getArgument(0,
              PeticionEvaluacionUnidadVinculacion.class);
          entity.setId(10L);
          return entity;
        });
  }

}