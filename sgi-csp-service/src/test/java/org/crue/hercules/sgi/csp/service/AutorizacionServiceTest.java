package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.exceptions.AlreadyInEstadoAutorizacionException;
import org.crue.hercules.sgi.csp.exceptions.AutorizacionNotUpdatableException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessAutorizacionException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.AutorizacionDatosConvocatoria;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.repository.AutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.EstadoAutorizacionRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiRepService;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;

class AutorizacionServiceTest extends BaseServiceTest {

  @Mock
  private AutorizacionRepository autorizacionRepository;
  @Mock
  private EstadoAutorizacionRepository estadoAutorizacionRepository;
  @Mock
  private SgiApiRepService reportService;
  @Mock
  private SgdocService sgdocService;
  @Mock
  private SgiConfigProperties sgiConfigProperties;
  @Mock
  private AutorizacionComService autorizacionComService;

  private AutorizacionService autorizacionService;

  @BeforeEach
  void setup() {
    this.autorizacionService = new AutorizacionService(
        autorizacionRepository,
        estadoAutorizacionRepository,
        reportService,
        sgdocService,
        sgiConfigProperties,
        autorizacionComService);
  }

  @Test
  void create_WithAutorizacionIdNotNull_ThrowsIllegalArgumentException() {
    Autorizacion autorizacion = this.buildMockAutorizacion(1L, null);

    Assertions.assertThatThrownBy(() -> this.autorizacionService.create(autorizacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_WithAutorizacionIdNull_ThrowsIllegalArgumentException() {
    Autorizacion autorizacion = this.buildMockAutorizacion(null, null);

    Assertions.assertThatThrownBy(() -> this.autorizacionService.update(autorizacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_WithAutorizacionIdNull_ThrowsIllegalArgumentException() {
    Assertions.assertThatThrownBy(() -> this.autorizacionService.delete(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @WithMockUser(authorities = { "CSP-AUT-E" })
  @Test
  void cambiarEstado_WithSameEstado_ThrowsAlreadyInEstadoAutorizacionException() {
    Long autorizacionId = 1L;
    Long newEstadoId = 1L;
    EstadoAutorizacion newEstado = this.buildMockEstadoAutorizacion(newEstadoId, autorizacionId,
        EstadoAutorizacion.Estado.AUTORIZADA);
    Autorizacion autorizacion = this.buildMockAutorizacion(autorizacionId, newEstado);

    BDDMockito.given(this.autorizacionRepository.findById(anyLong())).willReturn(Optional.of(autorizacion));

    Assertions.assertThatThrownBy(() -> this.autorizacionService.cambiarEstado(autorizacionId, newEstado))
        .isInstanceOf(AlreadyInEstadoAutorizacionException.class);
  }

  @Test
  void presentable_WithAutorizacionIdNull_ThrowsIllegalArgumentException() {
    Assertions.assertThatThrownBy(() -> this.autorizacionService.presentable(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @WithMockUser(username = "user")
  @Test
  void create_WithRefs_ClearsDatos() {
    // given: una autorización con convocatoria, entidad y responsable y con sus
    // datos manuales
    Autorizacion autorizacion = buildMockAutorizacionWithDatos(null, null);
    autorizacion.setConvocatoriaId(1L);
    autorizacion.setEntidadRef("entidad-001");
    autorizacion.setResponsableRef("responsable-001");
    mockSave();

    // when: se crea la autorización
    Autorizacion created = autorizacionService.create(autorizacion);

    // then: se descartan los datos manuales de los valores con referencia
    Assertions.assertThat(created.getDatosConvocatoria()).isEmpty();
    Assertions.assertThat(created.getDatosEntidad()).isNull();
    Assertions.assertThat(created.getDatosResponsable()).isNull();
    Assertions.assertThat(created.getConvocatoriaId()).isEqualTo(1L);
    Assertions.assertThat(created.getEntidadRef()).isEqualTo("entidad-001");
    Assertions.assertThat(created.getResponsableRef()).isEqualTo("responsable-001");
    Assertions.assertThat(created.getSolicitanteRef()).isEqualTo("user");
  }

  @WithMockUser(username = "user")
  @Test
  void create_WithoutRefs_KeepsDatos() {
    // given: una autorización sin convocatoria, entidad ni responsable y con sus
    // datos manuales
    Autorizacion autorizacion = buildMockAutorizacionWithDatos(null, null);
    mockSave();

    // when: se crea la autorización
    Autorizacion created = autorizacionService.create(autorizacion);

    // then: se conservan los datos manuales
    Assertions.assertThat(created.getDatosConvocatoria()).hasSize(1);
    Assertions.assertThat(created.getDatosConvocatoria().iterator().next().getValue())
        .isEqualTo("datos-convocatoria");
    Assertions.assertThat(created.getDatosEntidad()).isEqualTo("datos-entidad");
    Assertions.assertThat(created.getDatosResponsable()).isEqualTo("datos-responsable");
  }

  @WithMockUser(username = "user", authorities = { "CSP-AUT-INV-ER" })
  @Test
  void update_WithRefs_ClearsDatos() {
    // given: una autorización en borrador del usuario y unos datos actualizados
    // con convocatoria, entidad y responsable y con sus datos manuales
    Autorizacion autorizacionBd = buildMockAutorizacionWithDatos(1L,
        buildMockEstadoAutorizacion(1L, 1L, EstadoAutorizacion.Estado.BORRADOR));
    Autorizacion autorizacionActualizar = buildMockAutorizacionWithDatos(1L, null);
    autorizacionActualizar.setConvocatoriaId(1L);
    autorizacionActualizar.setEntidadRef("entidad-001");
    autorizacionActualizar.setResponsableRef("responsable-001");
    BDDMockito.given(autorizacionRepository.findById(1L)).willReturn(Optional.of(autorizacionBd));
    mockSaveAutorizacion();

    // when: se actualiza la autorización
    Autorizacion updated = autorizacionService.update(autorizacionActualizar);

    // then: se descartan los datos manuales de los valores con referencia
    Assertions.assertThat(updated.getDatosConvocatoria()).isEmpty();
    Assertions.assertThat(updated.getDatosEntidad()).isNull();
    Assertions.assertThat(updated.getDatosResponsable()).isNull();
    Assertions.assertThat(updated.getConvocatoriaId()).isEqualTo(1L);
    Assertions.assertThat(updated.getEntidadRef()).isEqualTo("entidad-001");
    Assertions.assertThat(updated.getResponsableRef()).isEqualTo("responsable-001");
  }

  @WithMockUser(username = "user", authorities = { "CSP-AUT-INV-ER" })
  @Test
  void update_WithoutRefs_KeepsDatos() {
    // given: una autorización en borrador del usuario y unos datos actualizados
    // sin convocatoria, con entidad y responsable vacíos y con sus datos manuales
    Autorizacion autorizacionBd = buildMockAutorizacionWithDatos(1L,
        buildMockEstadoAutorizacion(1L, 1L, EstadoAutorizacion.Estado.BORRADOR));
    Autorizacion autorizacionActualizar = buildMockAutorizacionWithDatos(1L, null);
    autorizacionActualizar.setEntidadRef("");
    autorizacionActualizar.setResponsableRef("");
    BDDMockito.given(autorizacionRepository.findById(1L)).willReturn(Optional.of(autorizacionBd));
    mockSaveAutorizacion();

    // when: se actualiza la autorización
    Autorizacion updated = autorizacionService.update(autorizacionActualizar);

    // then: se conservan los datos manuales
    Assertions.assertThat(updated.getDatosConvocatoria()).hasSize(1);
    Assertions.assertThat(updated.getDatosConvocatoria().iterator().next().getValue())
        .isEqualTo("datos-convocatoria");
    Assertions.assertThat(updated.getDatosEntidad()).isEqualTo("datos-entidad");
    Assertions.assertThat(updated.getDatosResponsable()).isEqualTo("datos-responsable");
  }

  @WithMockUser(username = "gestor", authorities = { "CSP-AUT-E" })
  @Test
  void update_GestorPresentada_ReturnsAutorizacion() {
    // given: una autorización presentada de otro usuario y un gestor
    Autorizacion autorizacionBd = buildMockAutorizacionWithDatos(1L,
        buildMockEstadoAutorizacion(1L, 1L, EstadoAutorizacion.Estado.PRESENTADA));
    Autorizacion autorizacionActualizar = buildMockAutorizacionWithDatos(1L, null);
    BDDMockito.given(autorizacionRepository.findById(1L)).willReturn(Optional.of(autorizacionBd));
    mockSaveAutorizacion();

    // when: se actualiza la autorización
    Autorizacion updated = autorizacionService.update(autorizacionActualizar);

    // then: se guarda la autorización
    Assertions.assertThat(updated).isNotNull();
    Mockito.verify(autorizacionRepository).save(autorizacionBd);
  }

  @WithMockUser(username = "gestor", authorities = { "CSP-AUT-E" })
  @Test
  void update_GestorAutorizada_ThrowsAutorizacionNotUpdatableException() {
    // given: una autorización autorizada y un gestor
    Autorizacion autorizacionBd = buildMockAutorizacionWithDatos(1L,
        buildMockEstadoAutorizacion(1L, 1L, EstadoAutorizacion.Estado.AUTORIZADA));
    Autorizacion autorizacionActualizar = buildMockAutorizacionWithDatos(1L, null);
    BDDMockito.given(autorizacionRepository.findById(1L)).willReturn(Optional.of(autorizacionBd));

    Assertions.assertThatThrownBy(
        // when: se actualiza la autorización
        () -> autorizacionService.update(autorizacionActualizar))
        // then: se rechaza por el estado y no se guarda
        .isInstanceOf(AutorizacionNotUpdatableException.class);
    Mockito.verify(autorizacionRepository, Mockito.never()).save(ArgumentMatchers.<Autorizacion>any());
  }

  @WithMockUser(username = "user", authorities = { "CSP-AUT-INV-ER" })
  @Test
  void update_InvestigadorPresentada_ThrowsAutorizacionNotUpdatableException() {
    // given: una autorización presentada del usuario investigador
    Autorizacion autorizacionBd = buildMockAutorizacionWithDatos(1L,
        buildMockEstadoAutorizacion(1L, 1L, EstadoAutorizacion.Estado.PRESENTADA));
    Autorizacion autorizacionActualizar = buildMockAutorizacionWithDatos(1L, null);
    BDDMockito.given(autorizacionRepository.findById(1L)).willReturn(Optional.of(autorizacionBd));

    Assertions.assertThatThrownBy(
        // when: se actualiza la autorización
        () -> autorizacionService.update(autorizacionActualizar))
        // then: se rechaza por el estado y no se guarda
        .isInstanceOf(AutorizacionNotUpdatableException.class);
    Mockito.verify(autorizacionRepository, Mockito.never()).save(ArgumentMatchers.<Autorizacion>any());
  }

  @WithMockUser(username = "user", authorities = { "CSP-AUT-E", "CSP-AUT-INV-ER" })
  @Test
  void update_InvestigadorAndGestorPresentada_ReturnsAutorizacion() {
    // given: una autorización presentada del usuario, que es gestor e
    // investigador a la vez, y unos datos actualizados con responsable
    Autorizacion autorizacionBd = buildMockAutorizacionWithDatos(1L,
        buildMockEstadoAutorizacion(1L, 1L, EstadoAutorizacion.Estado.PRESENTADA));
    Autorizacion autorizacionActualizar = buildMockAutorizacionWithDatos(1L, null);
    autorizacionActualizar.setResponsableRef("responsable-001");
    BDDMockito.given(autorizacionRepository.findById(1L)).willReturn(Optional.of(autorizacionBd));
    mockSaveAutorizacion();

    // when: se actualiza la autorización
    Autorizacion updated = autorizacionService.update(autorizacionActualizar);

    // then: se aplica la regla del gestor y se guardan los cambios
    Mockito.verify(autorizacionRepository).save(autorizacionBd);
    Assertions.assertThat(updated.getResponsableRef()).isEqualTo("responsable-001");
    Assertions.assertThat(updated.getDatosResponsable()).isNull();
  }

  @WithMockUser(username = "otro", authorities = { "CSP-AUT-INV-ER" })
  @Test
  void update_InvestigadorNotSolicitante_ThrowsUserNotAuthorizedToAccessAutorizacionException() {
    // given: una autorización en borrador de otro usuario
    Autorizacion autorizacionBd = buildMockAutorizacionWithDatos(1L,
        buildMockEstadoAutorizacion(1L, 1L, EstadoAutorizacion.Estado.BORRADOR));
    Autorizacion autorizacionActualizar = buildMockAutorizacionWithDatos(1L, null);
    BDDMockito.given(autorizacionRepository.findById(1L)).willReturn(Optional.of(autorizacionBd));

    Assertions.assertThatThrownBy(
        // when: se actualiza la autorización
        () -> autorizacionService.update(autorizacionActualizar))
        // then: se rechaza por permiso y no se guarda
        .isInstanceOf(UserNotAuthorizedToAccessAutorizacionException.class);
    Mockito.verify(autorizacionRepository, Mockito.never()).save(ArgumentMatchers.<Autorizacion>any());
  }

  private void mockSave() {
    mockSaveAutorizacion();
    BDDMockito.given(estadoAutorizacionRepository.save(ArgumentMatchers.<EstadoAutorizacion>any()))
        .willAnswer(invocation -> {
          EstadoAutorizacion estadoAutorizacion = invocation.getArgument(0, EstadoAutorizacion.class);
          estadoAutorizacion.setId(1L);
          return estadoAutorizacion;
        });
  }

  private void mockSaveAutorizacion() {
    BDDMockito.given(autorizacionRepository.save(ArgumentMatchers.<Autorizacion>any()))
        .willAnswer(invocation -> {
          Autorizacion autorizacion = invocation.getArgument(0, Autorizacion.class);
          if (autorizacion.getId() == null) {
            autorizacion.setId(1L);
          }
          return autorizacion;
        });
  }

  private Autorizacion buildMockAutorizacionWithDatos(Long id, EstadoAutorizacion estado) {
    Set<AutorizacionDatosConvocatoria> datosConvocatoria = new HashSet<>();
    datosConvocatoria.add(AutorizacionDatosConvocatoria.builder()
        .lang(Language.ES)
        .value("datos-convocatoria")
        .build());
    return Autorizacion.builder()
        .id(id)
        .estado(estado)
        .solicitanteRef("user")
        .datosConvocatoria(datosConvocatoria)
        .datosEntidad("datos-entidad")
        .datosResponsable("datos-responsable")
        .build();
  }

  private EstadoAutorizacion buildMockEstadoAutorizacion(Long id, Long autorizacionId,
      EstadoAutorizacion.Estado estado) {
    return EstadoAutorizacion.builder()
        .id(id)
        .autorizacionId(autorizacionId)
        .estado(estado)
        .build();
  }

  private Autorizacion buildMockAutorizacion(Long id, EstadoAutorizacion estado) {
    return Autorizacion.builder()
        .id(id)
        .estado(estado)
        .build();
  }

}