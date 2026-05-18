package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoJustificacion;
import org.crue.hercules.sgi.csp.enums.TipoPartida;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.AreaTematicaDescripcion;
import org.crue.hercules.sgi.csp.model.AreaTematicaNombre;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEcObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartidaDescripcion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacionObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientificoObservaciones;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoOtrosRequisitos;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.model.RequisitoIPOtrosRequisitos;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaAreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadGestoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPartidaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoCategoriaProfesionalRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoNivelAcademicoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPCategoriaProfesionalRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPNivelAcademicoRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPRepository;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;

class ConvocatoriaClonerServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaEntidadGestoraRepository convocatoriaEntidadGestoraRepository;
  @Mock
  private ConvocatoriaAreaTematicaRepository convocatoriaAreaTematicaRepository;
  @Mock
  private ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository;
  @Mock
  private ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository;
  @Mock
  private ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository;
  @Mock
  private ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository;
  @Mock
  private RequisitoIPRepository requisitoIPRepository;
  @Mock
  private RequisitoIPNivelAcademicoRepository requisitoIPNivelAcademicoRepository;
  @Mock
  private RequisitoIPCategoriaProfesionalRepository requisitoIPCategoriaProfesionalRepository;
  @Mock
  private RequisitoEquipoRepository requisitoEquipoRepository;
  @Mock
  private RequisitoEquipoNivelAcademicoRepository requisitoEquipoNivelAcademicoRepository;
  @Mock
  private RequisitoEquipoCategoriaProfesionalRepository requisitoEquipoCategoriaProfesionalRepository;
  @Mock
  private ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository;
  @Mock
  private ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;
  @Mock
  private ConvocatoriaPartidaRepository convocatoriaPartidaRepository;

  private ConvocatoriaClonerService service;

  @BeforeEach
  void setUp() {
    service = new ConvocatoriaClonerService(
        convocatoriaEntidadGestoraRepository,
        convocatoriaAreaTematicaRepository,
        convocatoriaEntidadConvocanteRepository,
        convocatoriaEntidadFinanciadoraRepository,
        convocatoriaPeriodoJustificacionRepository,
        convocatoriaPeriodoSeguimientoCientificoRepository,
        requisitoIPRepository,
        requisitoIPNivelAcademicoRepository,
        requisitoIPCategoriaProfesionalRepository,
        requisitoEquipoRepository,
        requisitoEquipoNivelAcademicoRepository,
        requisitoEquipoCategoriaProfesionalRepository,
        convocatoriaConceptoGastoRepository,
        convocatoriaConceptoGastoCodigoEcRepository,
        convocatoriaPartidaRepository);
  }

  @Test
  void cloneConvocatoriaAreasTematicas_ShouldCloneConvocatoriaAreaTematica() {
    // given: una ConvocatoriaAreaTematica asociada a la convocatoria a clonar
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;

    ConvocatoriaAreaTematica area = buildMockConvocatoriaAreaTematica(convocatoriaToCloneId);

    BDDMockito.given(convocatoriaAreaTematicaRepository.findByConvocatoriaId(anyLong()))
        .willReturn(Arrays.asList(area));
    BDDMockito.given(convocatoriaAreaTematicaRepository.save(ArgumentMatchers.<ConvocatoriaAreaTematica>any()))
        .willReturn(area);

    Set<ConvocatoriaObservaciones> convocatoriaObservaciones = new HashSet<>();
    convocatoriaObservaciones.add(new ConvocatoriaObservaciones(Language.ES, "testing clone"));

    ArgumentCaptor<ConvocatoriaAreaTematica> captor = ArgumentCaptor.forClass(ConvocatoriaAreaTematica.class);

    // when: se llama a cloneConvocatoriaAreasTematicas
    service.cloneConvocatoriaAreasTematicas(convocatoriaToCloneId,
        buildMockConvocatoria(convocatoriaClonedId, convocatoriaObservaciones));

    // then: se persiste una nueva ConvocatoriaAreaTematica vinculada a la
    // convocatoria clonada con la misma areaTematica y observaciones
    verify(convocatoriaAreaTematicaRepository, times(1)).save(captor.capture());
    ConvocatoriaAreaTematica saved = captor.getValue();
    Assertions.assertThat(saved.getConvocatoriaId()).isEqualTo(convocatoriaClonedId);
    Assertions.assertThat(saved.getAreaTematica()).isEqualTo(area.getAreaTematica());
    Assertions.assertThat(saved.getObservaciones()).isEqualTo(area.getObservaciones());
  }

  @Test
  void cloneConvocatoriasEntidadesConvocantes_ShouldCloneConvocatoriaEntidadConvocanteList() {
    // given: una ConvocatoriaEntidadConvocante asociada a la convocatoria a clonar
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;

    List<ConvocatoriaEntidadConvocante> entidades = Arrays.asList(buildMockConvocatoriaEntidadConvocante(1L,
        convocatoriaClonedId));

    BDDMockito.given(convocatoriaEntidadConvocanteRepository.findByConvocatoriaId(anyLong()))
        .willReturn(entidades);
    BDDMockito.given(convocatoriaEntidadConvocanteRepository
        .save(ArgumentMatchers.<ConvocatoriaEntidadConvocante>any())).willReturn(entidades.get(0));

    ArgumentCaptor<ConvocatoriaEntidadConvocante> captor = ArgumentCaptor.forClass(ConvocatoriaEntidadConvocante.class);

    // when: se llama a cloneConvocatoriasEntidadesConvocantes
    service.cloneConvocatoriasEntidadesConvocantes(convocatoriaToCloneId, convocatoriaClonedId);

    // then: se persiste una nueva ConvocatoriaEntidadConvocante vinculada a la
    // convocatoria clonada con la misma entidadRef y programa
    verify(convocatoriaEntidadConvocanteRepository, times(1)).save(captor.capture());
    ConvocatoriaEntidadConvocante saved = captor.getValue();
    Assertions.assertThat(saved.getConvocatoriaId()).isEqualTo(convocatoriaClonedId);
    Assertions.assertThat(saved.getEntidadRef()).isEqualTo(entidades.get(0).getEntidadRef());
    Assertions.assertThat(saved.getPrograma()).isEqualTo(entidades.get(0).getPrograma());
  }

  @Test
  void cloneConvocatoriasEntidadesFinanciadoras_ShouldCloneConvocatoriaEntidadFinanciadoraList() {
    // given: una ConvocatoriaEntidadFinanciadora asociada a la convocatoria a
    // clonar
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;

    List<ConvocatoriaEntidadFinanciadora> entidades = Arrays
        .asList(buildMockConvocatoriaEntidadFinanciadora(convocatoriaClonedId, "entidad-ref-01"));

    BDDMockito.given(convocatoriaEntidadFinanciadoraRepository.findByConvocatoriaId(convocatoriaToCloneId))
        .willReturn(entidades);
    BDDMockito
        .given(convocatoriaEntidadFinanciadoraRepository.save(ArgumentMatchers.<ConvocatoriaEntidadFinanciadora>any()))
        .willReturn(entidades.get(0));

    ArgumentCaptor<ConvocatoriaEntidadFinanciadora> captor = ArgumentCaptor
        .forClass(ConvocatoriaEntidadFinanciadora.class);

    // when: se llama a cloneConvocatoriasEntidadesFinanciadoras
    service.cloneConvocatoriasEntidadesFinanciadoras(convocatoriaToCloneId, convocatoriaClonedId);

    // then: se persiste una nueva ConvocatoriaEntidadFinanciadora vinculada a la
    // convocatoria clonada con los mismos datos de financiación
    verify(convocatoriaEntidadFinanciadoraRepository, times(1)).save(captor.capture());
    ConvocatoriaEntidadFinanciadora saved = captor.getValue();
    Assertions.assertThat(saved.getConvocatoriaId()).isEqualTo(convocatoriaClonedId);
    Assertions.assertThat(saved.getEntidadRef()).isEqualTo(entidades.get(0).getEntidadRef());
    Assertions.assertThat(saved.getFuenteFinanciacion()).isEqualTo(entidades.get(0).getFuenteFinanciacion());
    Assertions.assertThat(saved.getTipoFinanciacion()).isEqualTo(entidades.get(0).getTipoFinanciacion());
    Assertions.assertThat(saved.getPorcentajeFinanciacion()).isEqualTo(entidades.get(0).getPorcentajeFinanciacion());
    Assertions.assertThat(saved.getImporteFinanciacion()).isEqualTo(entidades.get(0).getImporteFinanciacion());
  }

  @Test
  void clonePeriodosJustificacion_ShouldCloneConvocatoriaPeriodoJustificacionList() {
    // given: un ConvocatoriaPeriodoJustificacion asociado a la convocatoria a
    // clonar
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;

    List<ConvocatoriaPeriodoJustificacion> justificaciones = Arrays
        .asList(buildMockConvocatoriaPeriodoJustificacion(convocatoriaClonedId));

    BDDMockito.given(convocatoriaPeriodoJustificacionRepository.findAllByConvocatoriaId(anyLong()))
        .willReturn(justificaciones);
    BDDMockito
        .given(
            convocatoriaPeriodoJustificacionRepository.save(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>any()))
        .willReturn(justificaciones.get(0));

    ArgumentCaptor<ConvocatoriaPeriodoJustificacion> captor = ArgumentCaptor
        .forClass(ConvocatoriaPeriodoJustificacion.class);

    // when: se llama a clonePeriodosJustificacion
    service.clonePeriodosJustificacion(convocatoriaToCloneId, convocatoriaClonedId);

    // then: se persiste un nuevo ConvocatoriaPeriodoJustificacion vinculado a la
    // convocatoria clonada con los mismos datos del período y una copia nueva de
    // observaciones
    verify(convocatoriaPeriodoJustificacionRepository, times(1)).save(captor.capture());
    ConvocatoriaPeriodoJustificacion saved = captor.getValue();
    Assertions.assertThat(saved.getConvocatoriaId()).isEqualTo(convocatoriaClonedId);
    Assertions.assertThat(saved.getMesInicial()).isEqualTo(justificaciones.get(0).getMesInicial());
    Assertions.assertThat(saved.getMesFinal()).isEqualTo(justificaciones.get(0).getMesFinal());
    Assertions.assertThat(saved.getNumPeriodo()).isEqualTo(justificaciones.get(0).getNumPeriodo());
    Assertions.assertThat(saved.getTipo()).isEqualTo(justificaciones.get(0).getTipo());
    Assertions.assertThat(saved.getObservaciones())
        .isNotSameAs(justificaciones.get(0).getObservaciones())
        .containsExactlyInAnyOrderElementsOf(justificaciones.get(0).getObservaciones());
  }

  @Test
  void cloneConvocatoriaPeriodosSeguimientoCientifico_ShouldCloneConvocatoriaPeriodoSeguimientoCientificoList() {
    // given: un ConvocatoriaPeriodoSeguimientoCientifico asociado a la convocatoria
    // a clonar
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;

    List<ConvocatoriaPeriodoSeguimientoCientifico> periodos = Arrays
        .asList(buildMockConvocatoriaPeriodoSeguimientoCientifico(convocatoriaClonedId));

    BDDMockito.given(convocatoriaPeriodoSeguimientoCientificoRepository
        .findAllByConvocatoriaIdOrderByMesInicial(convocatoriaToCloneId)).willReturn(periodos);
    BDDMockito.given(convocatoriaPeriodoSeguimientoCientificoRepository
        .save(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>any()))
        .willReturn(periodos.get(0));

    ArgumentCaptor<ConvocatoriaPeriodoSeguimientoCientifico> captor = ArgumentCaptor
        .forClass(ConvocatoriaPeriodoSeguimientoCientifico.class);

    // when: se llama a cloneConvocatoriaPeriodosSeguimientoCientifico
    service.cloneConvocatoriaPeriodosSeguimientoCientifico(convocatoriaToCloneId, convocatoriaClonedId);

    // then: se persiste un nuevo ConvocatoriaPeriodoSeguimientoCientifico vinculado
    // a la convocatoria clonada con los mismos datos del período y una copia nueva
    // de observaciones
    verify(convocatoriaPeriodoSeguimientoCientificoRepository, times(1)).save(captor.capture());
    ConvocatoriaPeriodoSeguimientoCientifico saved = captor.getValue();
    Assertions.assertThat(saved.getConvocatoriaId()).isEqualTo(convocatoriaClonedId);
    Assertions.assertThat(saved.getMesInicial()).isEqualTo(periodos.get(0).getMesInicial());
    Assertions.assertThat(saved.getMesFinal()).isEqualTo(periodos.get(0).getMesFinal());
    Assertions.assertThat(saved.getNumPeriodo()).isEqualTo(periodos.get(0).getNumPeriodo());
    Assertions.assertThat(saved.getTipoSeguimiento()).isEqualTo(periodos.get(0).getTipoSeguimiento());
    Assertions.assertThat(saved.getObservaciones())
        .isNotSameAs(periodos.get(0).getObservaciones())
        .containsExactlyInAnyOrderElementsOf(periodos.get(0).getObservaciones());
  }

  @Test
  void cloneRequisitoIP_ShouldCloneRequisitoIP() {
    // given: un RequisitoIP con un NivelAcademico y una CategoriaProfesional
    // asociado a la convocatoria a clonar
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;

    RequisitoIP requisitoIp = buildMockRequisitoIP(convocatoriaClonedId);

    List<RequisitoIPNivelAcademico> niveles = Arrays.asList(buildMockRequisitoIPNivelAcademico());
    List<RequisitoIPCategoriaProfesional> categorias = Arrays.asList(buildMockRequisitoIPCategoriaProfesional());

    BDDMockito.given(this.requisitoIPRepository.findById(convocatoriaToCloneId)).willReturn(Optional.of(requisitoIp));
    BDDMockito.given(this.requisitoIPRepository.save(ArgumentMatchers.<RequisitoIP>any())).willReturn(requisitoIp);

    BDDMockito.given(this.requisitoIPNivelAcademicoRepository.findByRequisitoIPId(anyLong())).willReturn(niveles);
    BDDMockito.given(this.requisitoIPNivelAcademicoRepository.save(ArgumentMatchers.<RequisitoIPNivelAcademico>any()))
        .willReturn(niveles.get(0));

    BDDMockito.given(this.requisitoIPCategoriaProfesionalRepository.findByRequisitoIPId(anyLong()))
        .willReturn(categorias);
    BDDMockito.given(
        this.requisitoIPCategoriaProfesionalRepository.save(ArgumentMatchers.<RequisitoIPCategoriaProfesional>any()))
        .willReturn(categorias.get(0));

    // when: se llama a cloneRequisitoIP
    service.cloneRequisitoIP(convocatoriaToCloneId, convocatoriaClonedId);

    // then: se persisten un nuevo RequisitoIP, NivelAcademico y
    // CategoriaProfesional cada uno una vez
    verify(this.requisitoIPRepository, times(1)).save(ArgumentMatchers.<RequisitoIP>any());
    verify(this.requisitoIPNivelAcademicoRepository, times(1)).save(ArgumentMatchers.<RequisitoIPNivelAcademico>any());
    verify(this.requisitoIPCategoriaProfesionalRepository, times(1))
        .save(ArgumentMatchers.<RequisitoIPCategoriaProfesional>any());
  }

  @Test
  void cloneRequisitosEquipo_ShouldCloneRequisitoEquipo() {
    // given: un RequisitoEquipo asociado a la convocatoria a clonar
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;
    RequisitoEquipo requisito = buildMockRequisitoEquipo(convocatoriaClonedId);

    BDDMockito.given(this.requisitoEquipoRepository.findByConvocatoriaId(convocatoriaToCloneId))
        .willReturn(Optional.of(requisito));
    BDDMockito.given(this.requisitoEquipoRepository.save(ArgumentMatchers.<RequisitoEquipo>any()))
        .willReturn(requisito);

    // when: se llama a cloneRequisitosEquipo
    service.cloneRequisitosEquipo(convocatoriaToCloneId, convocatoriaClonedId);

    // then: se persiste un nuevo RequisitoEquipo una vez
    verify(this.requisitoEquipoRepository, times(1)).save(ArgumentMatchers.<RequisitoEquipo>any());
  }

  @Test
  void cloneConvocatoriaConceptosGastosAndConvocatoriaConceptoCodigosEc_ShouldCloneConvocatoriaConceptosGastosAndConvocatoriaConceptoCodigosEc() {
    // given: un ConvocatoriaConceptoGasto con un ConvocatoriaConceptoGastoCodigoEc
    // asociado a la convocatoria a clonar
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;
    List<ConvocatoriaConceptoGasto> gastos = Arrays.asList(buildMockConvocatoriaConceptoGasto(convocatoriaClonedId));
    List<ConvocatoriaConceptoGastoCodigoEc> gastosCodEc = Arrays
        .asList(buildMockConvocatoriaConceptoGastoCodigoEc(gastos.get(0)));

    BDDMockito.given(this.convocatoriaConceptoGastoRepository.findByConvocatoriaId(anyLong())).willReturn(gastos);
    BDDMockito.given(this.convocatoriaConceptoGastoRepository.save(ArgumentMatchers.<ConvocatoriaConceptoGasto>any()))
        .willReturn(gastos.get(0));

    BDDMockito.given(this.convocatoriaConceptoGastoCodigoEcRepository
        .findAllByConvocatoriaConceptoGastoId(anyLong())).willReturn(gastosCodEc);
    BDDMockito.given(this.convocatoriaConceptoGastoCodigoEcRepository
        .save(ArgumentMatchers.<ConvocatoriaConceptoGastoCodigoEc>any())).willReturn(gastosCodEc.get(0));

    ArgumentCaptor<ConvocatoriaConceptoGasto> gastoCaptor = ArgumentCaptor.forClass(ConvocatoriaConceptoGasto.class);
    ArgumentCaptor<ConvocatoriaConceptoGastoCodigoEc> codEcCaptor = ArgumentCaptor
        .forClass(ConvocatoriaConceptoGastoCodigoEc.class);

    // when: se llama a
    // cloneConvocatoriaConceptosGastosAndConvocatoriaConceptoCodigosEc
    service.cloneConvocatoriaConceptosGastosAndConvocatoriaConceptoCodigosEc(convocatoriaToCloneId,
        convocatoriaClonedId);

    // then: se persisten el nuevo ConvocatoriaConceptoGasto y su CodigoEc con el id
    // de la convocatoria clonada y copias nuevas de observaciones
    verify(this.convocatoriaConceptoGastoRepository, times(1)).save(gastoCaptor.capture());
    ConvocatoriaConceptoGasto savedGasto = gastoCaptor.getValue();
    Assertions.assertThat(savedGasto.getConvocatoriaId()).isEqualTo(convocatoriaClonedId);
    Assertions.assertThat(savedGasto.getMesInicial()).isEqualTo(gastos.get(0).getMesInicial());
    Assertions.assertThat(savedGasto.getMesFinal()).isEqualTo(gastos.get(0).getMesFinal());
    Assertions.assertThat(savedGasto.getObservaciones())
        .isNotSameAs(gastos.get(0).getObservaciones())
        .containsExactlyInAnyOrderElementsOf(gastos.get(0).getObservaciones());

    verify(this.convocatoriaConceptoGastoCodigoEcRepository, times(1)).save(codEcCaptor.capture());
    ConvocatoriaConceptoGastoCodigoEc savedCodEc = codEcCaptor.getValue();
    Assertions.assertThat(savedCodEc.getConvocatoriaConceptoGastoId()).isEqualTo(gastos.get(0).getId());
    Assertions.assertThat(savedCodEc.getCodigoEconomicoRef()).isEqualTo(gastosCodEc.get(0).getCodigoEconomicoRef());
    Assertions.assertThat(savedCodEc.getObservaciones())
        .isNotSameAs(gastosCodEc.get(0).getObservaciones())
        .containsExactlyInAnyOrderElementsOf(gastosCodEc.get(0).getObservaciones());
  }

  @Test
  void clonePartidasPresupuestarias_ShouldCloneConvocatoriaPartida() {
    // given: una ConvocatoriaPartida asociada a la convocatoria a clonar
    Long convocatoriaToCloneId = 1L;
    Long convocatoriaClonedId = 2L;
    List<ConvocatoriaPartida> partidas = Arrays.asList(buildMockConvocatoriaPartida(convocatoriaClonedId));

    BDDMockito.given(this.convocatoriaPartidaRepository.findByConvocatoriaId(anyLong())).willReturn(partidas);
    BDDMockito.given(this.convocatoriaPartidaRepository.save(ArgumentMatchers.<ConvocatoriaPartida>any()))
        .willReturn(partidas.get(0));

    ArgumentCaptor<ConvocatoriaPartida> captor = ArgumentCaptor.forClass(ConvocatoriaPartida.class);

    // when: se llama a clonePartidasPresupuestarias
    service.clonePartidasPresupuestarias(convocatoriaToCloneId, convocatoriaClonedId);

    // then: se persiste una nueva ConvocatoriaPartida vinculada a la convocatoria
    // clonada con el mismo codigo y tipoPartida y una copia nueva de descripcion
    verify(this.convocatoriaPartidaRepository, times(1)).save(captor.capture());
    ConvocatoriaPartida saved = captor.getValue();
    Assertions.assertThat(saved.getConvocatoriaId()).isEqualTo(convocatoriaClonedId);
    Assertions.assertThat(saved.getCodigo()).isEqualTo(partidas.get(0).getCodigo());
    Assertions.assertThat(saved.getTipoPartida()).isEqualTo(partidas.get(0).getTipoPartida());
    Assertions.assertThat(saved.getDescripcion())
        .isNotSameAs(partidas.get(0).getDescripcion())
        .containsExactlyInAnyOrderElementsOf(partidas.get(0).getDescripcion());
  }

  private ConvocatoriaAreaTematica buildMockConvocatoriaAreaTematica(Long convocatoriaId) {
    Set<AreaTematicaNombre> nombre = new HashSet<>();
    nombre.add(new AreaTematicaNombre(Language.ES, "area-01-test"));

    Set<AreaTematicaDescripcion> descripcion = new HashSet<>();
    descripcion.add(new AreaTematicaDescripcion(Language.ES, "Testing"));

    return ConvocatoriaAreaTematica.builder()
        .areaTematica(AreaTematica.builder()
            .activo(Boolean.TRUE)
            .descripcion(descripcion)
            .nombre(nombre)
            .id(1L)
            .build())
        .convocatoriaId(convocatoriaId)
        .observaciones("Observaciones convocatoria area test")
        .build();
  }

  private Convocatoria buildMockConvocatoria(Long id, Set<ConvocatoriaObservaciones> observaciones) {
    return Convocatoria.builder()
        .id(id)
        .observaciones(observaciones)
        .build();
  }

  private ConvocatoriaEntidadConvocante buildMockConvocatoriaEntidadConvocante(Long id, Long convocatoriaId) {
    return ConvocatoriaEntidadConvocante.builder()
        .convocatoriaId(convocatoriaId)
        .entidadRef("entidad01")
        .id(id)
        .programa(Programa.builder().build())
        .build();
  }

  private ConvocatoriaEntidadFinanciadora buildMockConvocatoriaEntidadFinanciadora(Long convocatoriaClonedId,
      String entidadRef) {
    return ConvocatoriaEntidadFinanciadora.builder()
        .convocatoriaId(convocatoriaClonedId)
        .entidadRef(entidadRef)
        .fuenteFinanciacion(FuenteFinanciacion.builder().build())
        .tipoFinanciacion(TipoFinanciacion.builder().build())
        .porcentajeFinanciacion(new BigDecimal(100))
        .importeFinanciacion(new BigDecimal(12000))
        .build();
  }

  private ConvocatoriaPeriodoJustificacion buildMockConvocatoriaPeriodoJustificacion(Long convocatoriaClonedId) {
    Set<ConvocatoriaPeriodoJustificacionObservaciones> obsConvocatoriaPeriodoJustificacion = new HashSet<>();
    obsConvocatoriaPeriodoJustificacion
        .add(new ConvocatoriaPeriodoJustificacionObservaciones(Language.ES, "testing"));

    return ConvocatoriaPeriodoJustificacion.builder()
        .convocatoriaId(convocatoriaClonedId)
        .fechaFinPresentacion(Instant.now().plusSeconds(35000000))
        .numPeriodo(1)
        .fechaInicioPresentacion(Instant.now())
        .mesFinal(3)
        .mesInicial(1)
        .observaciones(obsConvocatoriaPeriodoJustificacion)
        .tipo(TipoJustificacion.PERIODICO).build();
  }

  private ConvocatoriaPeriodoSeguimientoCientifico buildMockConvocatoriaPeriodoSeguimientoCientifico(
      Long convocatoriaClonedId) {
    Set<ConvocatoriaPeriodoSeguimientoCientificoObservaciones> observaciones = new HashSet<>();
    observaciones.add(new ConvocatoriaPeriodoSeguimientoCientificoObservaciones(Language.ES, "testing"));

    return ConvocatoriaPeriodoSeguimientoCientifico.builder()
        .convocatoriaId(convocatoriaClonedId)
        .fechaInicioPresentacion(Instant.now())
        .fechaFinPresentacion(Instant.now().plusSeconds(3600000))
        .mesInicial(1)
        .mesFinal(11)
        .numPeriodo(1)
        .tipoSeguimiento(TipoSeguimiento.PERIODICO)
        .observaciones(observaciones)
        .build();
  }

  private RequisitoIP buildMockRequisitoIP(Long convocatoriaClonedId) {
    Set<RequisitoIPOtrosRequisitos> otrosRequisitos = new HashSet<>();
    otrosRequisitos.add(new RequisitoIPOtrosRequisitos(Language.ES, "TESTING"));

    return RequisitoIP.builder()
        .id(convocatoriaClonedId)
        .numMaximoIP(10)
        .edadMaxima(55)
        .sexoRef("h")
        .vinculacionUniversidad(Boolean.FALSE)
        .numMinimoCompetitivos(1)
        .numMinimoNoCompetitivos(2)
        .numMaximoCompetitivosActivos(9)
        .numMaximoNoCompetitivosActivos(8)
        .otrosRequisitos(otrosRequisitos).build();
  }

  private RequisitoIPNivelAcademico buildMockRequisitoIPNivelAcademico() {
    return RequisitoIPNivelAcademico.builder()
        .requisitoIPId(1L)
        .nivelAcademicoRef("GRADO")
        .build();
  }

  private RequisitoIPCategoriaProfesional buildMockRequisitoIPCategoriaProfesional() {
    return RequisitoIPCategoriaProfesional.builder()
        .requisitoIPId(1L)
        .categoriaProfesionalRef("GRADO")
        .build();
  }

  private RequisitoEquipo buildMockRequisitoEquipo(Long convocatoriaClonedId) {
    Set<RequisitoEquipoOtrosRequisitos> otrosRequisitos = new HashSet<>();
    otrosRequisitos.add(new RequisitoEquipoOtrosRequisitos(Language.ES, "NO DEFINIDOS"));

    return RequisitoEquipo.builder()
        .edadMaxima(55)
        .sexoRef("M")
        .ratioSexo(2)
        .vinculacionUniversidad(Boolean.FALSE)
        .numMinimoCompetitivos(2)
        .numMinimoNoCompetitivos(2)
        .numMaximoCompetitivosActivos(10)
        .numMaximoNoCompetitivosActivos(3)
        .otrosRequisitos(otrosRequisitos)
        .id(convocatoriaClonedId)
        .build();
  }

  private ConvocatoriaConceptoGasto buildMockConvocatoriaConceptoGasto(Long convocatoriaClonedId) {
    Set<ConvocatoriaConceptoGastoObservaciones> observacionesConvocatoriaConceptoGasto = new HashSet<>();
    observacionesConvocatoriaConceptoGasto
        .add(new ConvocatoriaConceptoGastoObservaciones(Language.ES, "testing"));

    return ConvocatoriaConceptoGasto.builder()
        .convocatoriaId(convocatoriaClonedId)
        .id(1L)
        .mesInicial(1)
        .mesFinal(11)
        .importeMaximo(new BigDecimal(11000000).doubleValue())
        .observaciones(observacionesConvocatoriaConceptoGasto)
        .permitido(Boolean.TRUE)
        .conceptoGasto(ConceptoGasto.builder().build())
        .build();
  }

  private ConvocatoriaConceptoGastoCodigoEc buildMockConvocatoriaConceptoGastoCodigoEc(
      ConvocatoriaConceptoGasto clonedConvocatoriaConceptoGasto) {
    Set<ConvocatoriaConceptoGastoCodigoEcObservaciones> observacionesConvocatoriaConceptoGastoCodigoEc = new HashSet<>();
    observacionesConvocatoriaConceptoGastoCodigoEc
        .add(new ConvocatoriaConceptoGastoCodigoEcObservaciones(Language.ES, "Testing"));

    return ConvocatoriaConceptoGastoCodigoEc.builder()
        .convocatoriaConceptoGastoId(clonedConvocatoriaConceptoGasto.getId())
        .codigoEconomicoRef("AA.AAAA.BBBB.AAAA")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.now().plusSeconds(3600000))
        .observaciones(observacionesConvocatoriaConceptoGastoCodigoEc)
        .id(0L)
        .build();
  }

  private ConvocatoriaPartida buildMockConvocatoriaPartida(Long convocatoriaClonedId) {
    Set<ConvocatoriaPartidaDescripcion> descripcionConvocatoriaPartida = new HashSet<>();
    descripcionConvocatoriaPartida.add(new ConvocatoriaPartidaDescripcion(Language.ES, "TESTING"));

    return ConvocatoriaPartida.builder()
        .convocatoriaId(convocatoriaClonedId)
        .codigo("COD-001")
        .descripcion(descripcionConvocatoriaPartida)
        .tipoPartida(TipoPartida.GASTO)
        .id(1L)
        .build();
  }
}