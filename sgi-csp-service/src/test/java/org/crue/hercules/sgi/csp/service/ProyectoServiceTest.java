package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaAreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadGestoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.EstadoProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloUnidadRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudModalidadRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoDatosRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEntidadFinanciadoraAjenaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoServiceImpl;
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
import org.springframework.security.test.context.support.WithMockUser;

/**
 * ProyectoServiceTest
 */
public class ProyectoServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoRepository repository;
  @Mock
  private EstadoProyectoRepository estadoProyectoRepository;
  @Mock
  private ModeloUnidadRepository modeloUnidadRepository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository;
  @Mock
  private ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService;
  @Mock
  private ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository;
  @Mock
  private ProyectoEntidadConvocanteService proyectoEntidadConvocanteService;
  @Mock
  private ConvocatoriaAreaTematicaRepository convocatoriaAreaTematicaRepository;
  @Mock
  private ContextoProyectoService contextoProyectoService;
  @Mock
  private ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository;
  @Mock
  private ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService;
  @Mock
  private ConvocatoriaEntidadGestoraRepository convocatoriaEntidadGestoraRepository;
  @Mock
  private ProyectoEntidadGestoraService proyectoEntidadGestoraService;
  @Mock
  private SolicitudRepository solicitudRepository;
  @Mock
  private SolicitudProyectoDatosRepository solicitudProyectoDatosRepository;
  @Mock
  private SolicitudModalidadRepository solicitudModalidadRepository;
  @Mock
  private SolicitudProyectoEquipoRepository solicitudEquipoRepository;
  @Mock
  private ProyectoEquipoService proyectoEquipoService;
  @Mock
  private SolicitudProyectoSocioRepository solicitudSocioRepository;
  @Mock
  private ProyectoSocioService proyectoSocioService;
  @Mock
  private SolicitudProyectoEquipoSocioRepository solicitudEquipoSocioRepository;
  @Mock
  private ProyectoSocioEquipoService proyectoEquipoSocioService;
  @Mock
  private SolicitudProyectoPeriodoPagoRepository solicitudPeriodoPagoRepository;
  @Mock
  private ProyectoSocioPeriodoPagoService proyectoSocioPeriodoPagoService;
  @Mock
  private SolicitudProyectoPeriodoJustificacionRepository solicitudPeriodoJustificacionRepository;
  @Mock
  private ProyectoSocioPeriodoJustificacionService proyectoSocioPeriodoJustificacionService;
  @Mock
  private ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository;
  @Mock
  private SolicitudProyectoEntidadFinanciadoraAjenaRepository solicitudProyectoEntidadFinanciadoraAjenaRepository;
  @Mock
  ProgramaRepository programaRepository;

  private ProyectoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoServiceImpl(repository, estadoProyectoRepository, modeloUnidadRepository,
        convocatoriaRepository, convocatoriaEntidadFinanciadoraRepository, proyectoEntidadFinanciadoraService,
        convocatoriaEntidadConvocanteRepository, proyectoEntidadConvocanteService, convocatoriaEntidadGestoraRepository,
        proyectoEntidadGestoraService, convocatoriaAreaTematicaRepository, contextoProyectoService,
        convocatoriaPeriodoSeguimientoCientificoRepository, proyectoPeriodoSeguimientoService, solicitudRepository,
        solicitudProyectoDatosRepository, solicitudModalidadRepository, solicitudEquipoRepository,
        proyectoEquipoService, solicitudSocioRepository, proyectoSocioService, solicitudEquipoSocioRepository,
        proyectoEquipoSocioService, solicitudPeriodoPagoRepository, proyectoSocioPeriodoPagoService,
        solicitudPeriodoJustificacionRepository, proyectoSocioPeriodoJustificacionService,
        convocatoriaConceptoGastoRepository, solicitudProyectoEntidadFinanciadoraAjenaRepository, programaRepository);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void create_ReturnsProyecto() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any())).will((InvocationOnMock invocation) -> {
      Proyecto proyectoCreado = invocation.getArgument(0);
      if (proyectoCreado.getId() == null) {
        proyectoCreado.setId(1L);
      }

      return proyectoCreado;
    });

    BDDMockito.given(estadoProyectoRepository.save(ArgumentMatchers.<EstadoProyecto>any()))
        .will((InvocationOnMock invocation) -> {
          EstadoProyecto estadoProyectoCreado = invocation.getArgument(0);
          estadoProyectoCreado.setId(1L);
          return estadoProyectoCreado;
        });

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    // when: Creamos el Proyecto
    Proyecto proyectoCreado = service.create(proyecto);

    // then: El Proyecto se crea correctamente
    Assertions.assertThat(proyectoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());
    Assertions.assertThat(proyectoCreado.getActivo()).as("getActivo").isEqualTo(proyecto.getActivo());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void createWithConvocatoria_ReturnsProyecto() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setConvocatoria(Convocatoria.builder().id(1L).build());

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any())).will((InvocationOnMock invocation) -> {
      Proyecto proyectoCreado = invocation.getArgument(0);
      if (proyectoCreado.getId() == null) {
        proyectoCreado.setId(1L);
      }

      return proyectoCreado;
    });

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE)));

    BDDMockito.given(estadoProyectoRepository.save(ArgumentMatchers.<EstadoProyecto>any()))
        .will((InvocationOnMock invocation) -> {
          EstadoProyecto estadoProyectoCreado = invocation.getArgument(0);
          estadoProyectoCreado.setId(1L);
          return estadoProyectoCreado;
        });

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    // when: Creamos el Proyecto
    Proyecto proyectoCreado = service.create(proyecto);

    // then: El Proyecto se crea correctamente
    Assertions.assertThat(proyectoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());
    Assertions.assertThat(proyectoCreado.getActivo()).as("getActivo").isEqualTo(proyecto.getActivo());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void createWithConvocatoriaAndConvocatoriaAreaTematica_ReturnsProyecto() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setConvocatoria(Convocatoria.builder().id(1L).build());

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any())).will((InvocationOnMock invocation) -> {
      Proyecto proyectoCreado = invocation.getArgument(0);
      if (proyectoCreado.getId() == null) {
        proyectoCreado.setId(1L);
      }

      return proyectoCreado;
    });

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE)));

    ConvocatoriaAreaTematica convocatoriaAreaTematica = generarMockConvocatoriaAreaTematica(
        generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE));

    BDDMockito.given(convocatoriaAreaTematicaRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaAreaTematica));

    BDDMockito.given(estadoProyectoRepository.save(ArgumentMatchers.<EstadoProyecto>any()))
        .will((InvocationOnMock invocation) -> {
          EstadoProyecto estadoProyectoCreado = invocation.getArgument(0);
          estadoProyectoCreado.setId(1L);
          return estadoProyectoCreado;
        });

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    // when: Creamos el Proyecto
    Proyecto proyectoCreado = service.create(proyecto);

    // then: El Proyecto se crea correctamente
    Assertions.assertThat(proyectoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());
    Assertions.assertThat(proyectoCreado.getActivo()).as("getActivo").isEqualTo(proyecto.getActivo());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void create_WithConvocatoriaNotExists_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setConvocatoria(Convocatoria.builder().id(1L).build());

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La convocatoria con id '" + proyecto.getConvocatoria().getId() + "' no existe");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto que ya tiene id
    Proyecto proyecto = generarMockProyecto(1L);

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Proyecto id tiene que ser null para crear un Proyecto");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_UGI" })
  public void create_WithoutUnidadGestion_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setUnidadGestionRef(null);

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La Unidad de Gestión no es gestionable por el usuario");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void create_WithConvocatoriaAndConvocatoriaExterna_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setConvocatoria(Convocatoria.builder().id(1L).build());
    proyecto.setConvocatoriaExterna("conv-001");

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La convocatoria no puede ser externa ya que el proyecto tiene una convocatoria asignada");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void create_WithoutModeloUnidad_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.empty());

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion '" + proyecto.getModeloEjecucion().getNombre()
            + "' no disponible para la UnidadGestion " + proyecto.getUnidadGestionRef());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void create_WithCosteHoraAndWithoutTimesheetTrue_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setTimesheet(false);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El proyecto requiere timesheet");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void create_WithCosteHoraAndWithoutTipoHorasAnuales_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(null);
    proyecto.setTipoHorasAnuales(null);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El campo tipoHorasAnuales debe ser obligatorio para el proyecto");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void update_ReturnsProyecto() {
    // given: Un nuevo Proyecto con las observaciones actualizadas
    Proyecto proyecto = generarMockProyecto(1L);
    Proyecto proyectoObservacionesActualizadas = generarMockProyecto(1L);
    proyectoObservacionesActualizadas.setObservaciones("observaciones actualizadas");

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Proyecto
    Proyecto proyectoActualizada = service.update(proyectoObservacionesActualizadas);

    // then: El Proyecto se actualiza correctamente.
    Assertions.assertThat(proyectoActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoActualizada.getId()).as("getId()").isEqualTo(proyecto.getId());
    Assertions.assertThat(proyectoActualizada.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoActualizada.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoActualizada.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());
    Assertions.assertThat(proyectoActualizada.getActivo()).as("getActivo").isEqualTo(proyecto.getActivo());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void updateWithConvocatoria_ReturnsProyecto() {
    // given: Un nuevo Proyecto con las observaciones actualizadas
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setConvocatoria(generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE));
    Proyecto proyectoObservacionesActualizadas = generarMockProyecto(1L);
    proyectoObservacionesActualizadas.setConvocatoria(generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE));
    proyectoObservacionesActualizadas.setObservaciones("observaciones actualizadas");

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE)));

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Proyecto
    Proyecto proyectoActualizada = service.update(proyectoObservacionesActualizadas);

    // then: El Proyecto se actualiza correctamente.
    Assertions.assertThat(proyectoActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoActualizada.getId()).as("getId()").isEqualTo(proyecto.getId());
    Assertions.assertThat(proyectoActualizada.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(proyectoActualizada.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyecto.getObservaciones());
    Assertions.assertThat(proyectoActualizada.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(proyecto.getUnidadGestionRef());
    Assertions.assertThat(proyectoActualizada.getActivo()).as("getActivo").isEqualTo(proyecto.getActivo());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void update_WithNotCorrectEstado_ThrowsIllegalArgumentException() {
    // given: Actualizar un proyecto con estado Finalizado
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.getEstado().setId(4L);
    proyecto.getEstado().setEstado(EstadoProyecto.Estado.FINALIZADO);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));
    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(new ModeloUnidad()));

    // when: Actualizamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El proyecto no está en un estado en el que puede ser actualizado");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void update_WithConvocatoriaNotExists_ThrowsIllegalArgumentException() {
    // given: Actualizar proyecto
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setConvocatoria(Convocatoria.builder().id(1L).build());

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Actualizamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La convocatoria con id '" + proyecto.getConvocatoria().getId() + "' no existe");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_UGI" })
  public void update_WithoutUnidadGestion_ThrowsIllegalArgumentException() {
    // given: Actualizar Proyecto
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));
    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(new ModeloUnidad()));

    // when: Actualizamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El proyecto no pertenece a una Unidad de Gestión gestionable por el usuario");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void update_WithConvocatoriaAndConvocatoriaExterna_ThrowsIllegalArgumentException() {
    // given: Actualizar Proyecto
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setConvocatoria(Convocatoria.builder().id(1L).build());
    proyecto.setConvocatoriaExterna("conv-001");

    // when: Actualizamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La convocatoria no puede ser externa ya que el proyecto tiene una convocatoria asignada");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void update_WithoutModeloUnidad_ThrowsIllegalArgumentException() {
    // given: Actualizar Proyecto
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.empty());

    // when: Actualizamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion '" + proyecto.getModeloEjecucion().getNombre()
            + "' no disponible para la UnidadGestion " + proyecto.getUnidadGestionRef());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void update_WithDistinctConvocatoria_ThrowsIllegalArgumentException() {
    // given: Actualizar Proyecto
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setConvocatoria(generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE));
    Proyecto proyectoConvocatoriaUpdate = generarMockProyecto(1L);
    proyecto.setConvocatoria(generarMockConvocatoria(2L, 2L, 2L, 2L, 2L, 2L, Boolean.TRUE));

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    // then: Lanza una excepcion porque no se puede modificar la convocatoria del
    // proyecto
    Assertions.assertThatThrownBy(() -> service.update(proyectoConvocatoriaUpdate))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Existen campos del proyecto modificados que no se pueden modificar");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void update_WithCosteHoraAndWithoutTimesheetTrue_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setTimesheet(false);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El proyecto requiere timesheet");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void update_WithCosteHoraAndWithoutTipoHorasAnuales_ThrowsIllegalArgumentException() {
    // given: Un nuevo Proyecto
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setTipoHorasAnuales(null);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(1L);
    modeloUnidad.setModeloEjecucion(proyecto.getModeloEjecucion());
    modeloUnidad.setUnidadGestionRef(proyecto.getUnidadGestionRef());
    modeloUnidad.setActivo(true);

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidad));

    // when: Creamos el Proyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(proyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El campo tipoHorasAnuales debe ser obligatorio para el proyecto");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void enable_ReturnsProyecto() {
    // given: Un nuevo Proyecto inactivo
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Activamos el Proyecto
    Proyecto programaActualizado = service.enable(proyecto.getId());

    // then: El Proyecto se activa correctamente.
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(proyecto.getId());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void enable_WithIdNotExist_ThrowsProyectoNotFoundException() {
    // given: Un id de un Proyecto que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el Proyecto
    // then: Lanza una excepcion porque el Proyecto no existe
    Assertions.assertThatThrownBy(() -> service.enable(idNoExiste)).isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void disable_ReturnsProyecto() {
    // given: Un Proyecto activo
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el Proyecto
    Proyecto proyectoActualizada = service.disable(proyecto.getId());

    // then: El Proyecto se desactivan correctamente
    Assertions.assertThat(proyectoActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoActualizada.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoActualizada.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void disable_WithIdNotExist_ThrowsProyectoNotFoundException() {
    // given: Un id de un Proyecto que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el Proyecto
    // then: Lanza una excepcion porque el Proyecto no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void getModeloEjecucion_WithExistingId_ReturnsModeloEjecucion() throws Exception {
    // given: existing Proyecto id
    Proyecto proyectoExistente = generarMockProyecto(1L);
    proyectoExistente.getModeloEjecucion().setId(99L);

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.given(repository.getModeloEjecucion(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoExistente.getModeloEjecucion()));

    // when: getModeloEjecucion by id Proyecto
    ModeloEjecucion modeloEjecucion = service.getModeloEjecucion(proyectoExistente.getId());

    // then: returns ModeloEjecucion
    Assertions.assertThat(modeloEjecucion).isNotNull();
    Assertions.assertThat(modeloEjecucion.getId()).as("getId()").isEqualTo(99L);

  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void getModeloEjecucion_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing Proyecto id
    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: getModeloEjecucion by id Proyecto
        () -> service.getModeloEjecucion(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void existsById_WithExistingId_ReturnsTRUE() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isTrue();
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void existsById_WithNoExistingId_ReturnsFALSE() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void findById_ReturnsProyecto() {
    // given: Un Proyecto con el id buscado
    Long idBuscado = 1L;
    Proyecto proyectoBuscada = generarMockProyecto(idBuscado);
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(proyectoBuscada));

    // when: Buscamos el Proyecto por su id
    Proyecto proyecto = service.findById(idBuscado);

    // then: el Proyecto
    Assertions.assertThat(proyecto).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(proyecto.getEstado().getId()).as("getEstado().getId()").isEqualTo(1);
    Assertions.assertThat(proyecto.getObservaciones()).as("getObservaciones()").isEqualTo("observaciones-001");
    Assertions.assertThat(proyecto.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("OPE");
    Assertions.assertThat(proyecto.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void findById_WithIdNotExist_ThrowsProyectoNotFoundException() throws Exception {
    // given: Ningun Proyecto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el Proyecto por su id
    // then: lanza un ProyectoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 Proyecto
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Proyecto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
            List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
            Page<Proyecto> page = new PageImpl<>(content, pageable, proyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Proyecto> page = service.findAllRestringidos(null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Proyecto proyecto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_OPE" })
  public void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 Proyecto
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Proyecto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
            List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
            Page<Proyecto> page = new PageImpl<>(content, pageable, proyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Proyecto> page = service.findAllTodosRestringidos(null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Proyecto proyecto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  /**
   * Función que devuelve un objeto Proyecto
   * 
   * @param id id del Proyecto
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto(Long id) {
    EstadoProyecto estadoProyecto = generarMockEstadoProyecto(1L);

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(1L);

    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    Proyecto proyecto = new Proyecto();
    proyecto.setId(id);
    proyecto.setTitulo("PRO" + (id != null ? id : 1));
    proyecto.setCodigoExterno("cod-externo-" + (id != null ? String.format("%03d", id) : "001"));
    proyecto.setObservaciones("observaciones-" + String.format("%03d", id));
    proyecto.setUnidadGestionRef("OPE");
    proyecto.setFechaInicio(LocalDate.now());
    proyecto.setFechaFin(LocalDate.now().plusMonths(1));
    proyecto.setModeloEjecucion(modeloEjecucion);
    proyecto.setFinalidad(tipoFinalidad);
    proyecto.setAmbitoGeografico(tipoAmbitoGeografico);
    proyecto.setConfidencial(Boolean.FALSE);
    proyecto.setCosteHora(true);
    proyecto.setTimesheet(true);
    proyecto.setTipoHorasAnuales(Proyecto.TipoHorasAnuales.REAL);
    proyecto.setActivo(true);

    if (id != null) {
      proyecto.setEstado(estadoProyecto);
    }

    return proyecto;
  }

  /**
   * Función que devuelve un objeto EstadoProyecto
   * 
   * @param id id del EstadoProyecto
   * @return el objeto EstadoProyecto
   */
  private EstadoProyecto generarMockEstadoProyecto(Long id) {
    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(id);
    estadoProyecto.setComentario("Estado-" + id);
    estadoProyecto.setEstado(EstadoProyecto.Estado.BORRADOR);
    estadoProyecto.setFechaEstado(LocalDateTime.now());
    estadoProyecto.setIdProyecto(1L);

    return estadoProyecto;
  }

  /**
   * Función que genera Convocatoria
   * 
   * @param convocatoriaId
   * @param unidadGestionId
   * @param modeloEjecucionId
   * @param modeloTipoFinalidadId
   * @param tipoRegimenConcurrenciaId
   * @param tipoAmbitoGeogragicoId
   * @param activo
   * @return la convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long convocatoriaId, Long unidadGestionId, Long modeloEjecucionId,
      Long modeloTipoFinalidadId, Long tipoRegimenConcurrenciaId, Long tipoAmbitoGeogragicoId, Boolean activo) {

    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()//
            .id(modeloEjecucionId)//
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .nombre("nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId))//
            .activo(Boolean.TRUE)//
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .modeloEjecucion(modeloEjecucion)//
            .tipoFinalidad(tipoFinalidad)//
            .activo(Boolean.TRUE)//
            .build();

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()//
            .id(tipoRegimenConcurrenciaId)//
            .nombre("nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()//
            .id(tipoAmbitoGeogragicoId)//
            .nombre("nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId))//
            .activo(Boolean.TRUE)//
            .build();

    Convocatoria convocatoria = Convocatoria.builder()//
        .id(convocatoriaId)//
        .unidadGestionRef((unidadGestionId == null) ? null : "unidad-" + String.format("%03d", unidadGestionId))//
        .modeloEjecucion(modeloEjecucion)//
        .codigo("codigo-" + String.format("%03d", convocatoriaId))//
        .anio(2020)//
        .titulo("titulo-" + String.format("%03d", convocatoriaId))//
        .objeto("objeto-" + String.format("%03d", convocatoriaId))//
        .observaciones("observaciones-" + String.format("%03d", convocatoriaId))//
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())//
        .regimenConcurrencia(tipoRegimenConcurrencia)//
        .destinatarios(Convocatoria.Destinatarios.INDIVIDUAL)//
        .colaborativos(Boolean.TRUE)//
        .estado(Convocatoria.Estado.REGISTRADA)//
        .duracion(12)//
        .ambitoGeografico(tipoAmbitoGeografico)//
        .clasificacionCVN(ClasificacionCVN.AYUDAS)//
        .activo(activo)//
        .build();

    return convocatoria;
  }

  /**
   * Función que devuelve un objeto ConvocatoriaAreaTematica
   * 
   * @param convocatoria la Convocatoria
   * @return el objeto ConvocatoriaAreaTematica
   */
  private ConvocatoriaAreaTematica generarMockConvocatoriaAreaTematica(Convocatoria convocatoria) {
    AreaTematica areaTematica = new AreaTematica();
    areaTematica.setId(1L);
    areaTematica.setNombre("AreaTematica");
    areaTematica.setDescripcion("descripcion");
    areaTematica.setActivo(true);
    ConvocatoriaAreaTematica convocatoriaAreaTematica = new ConvocatoriaAreaTematica();
    convocatoriaAreaTematica.setConvocatoria(convocatoria);
    convocatoriaAreaTematica.setAreaTematica(areaTematica);

    return convocatoriaAreaTematica;
  }

}
