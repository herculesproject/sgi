package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.BaremacionInput.BaremoInput;
import org.crue.hercules.sgi.prc.dto.csp.InvencionDto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.repository.AliasEnumeradoRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceExperimentalidadRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.MapeoTiposRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiPiiService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.modelmapper.ModelMapper;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de invenciones
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionInvencionService extends BaremacionCommonService {
  public static final String PREFIX_INVENCIONES = "INV_";

  private final PuntuacionGrupoRepository puntuacionGrupoRepository;
  private final PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository;
  private final MapeoTiposRepository mapeoTiposRepository;
  private final SgiApiPiiService sgiApiPiiService;

  public BaremacionInvencionService(
      AliasEnumeradoRepository aliasEnumeradoRepository,
      ProduccionCientificaRepository produccionCientificaRepository,
      PuntuacionBaremoItemRepository puntuacionBaremoItemRepository,
      PuntuacionItemInvestigadorRepository puntuacionItemInvestigadorRepository,
      IndiceExperimentalidadRepository indiceExperimentalidadRepository,
      BaremoRepository baremoRepository,
      AutorRepository autorRepository,
      AutorGrupoRepository autorGrupoRepository,
      CampoProduccionCientificaRepository campoProduccionCientificaRepository,
      ValorCampoRepository valorCampoRepository,
      IndiceImpactoRepository indiceImpactoRepository,
      ProduccionCientificaBuilderService produccionCientificaBuilderService,
      SgiApiSgpService sgiApiSgpService,
      SgiApiCspService sgiApiCspService,
      ConvocatoriaBaremacionLogService convocatoriaBaremacionLogService,
      ModelMapper modelMapper,
      SgiConfigProperties sgiConfigProperties,
      PuntuacionGrupoRepository puntuacionGrupoRepository,
      PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository,
      MapeoTiposRepository mapeoTiposRepository,
      SgiApiPiiService sgiApiPiiService) {
    super(aliasEnumeradoRepository,
        produccionCientificaRepository,
        puntuacionBaremoItemRepository,
        puntuacionItemInvestigadorRepository,
        indiceExperimentalidadRepository,
        baremoRepository,
        autorRepository, autorGrupoRepository,
        campoProduccionCientificaRepository,
        valorCampoRepository,
        indiceImpactoRepository,
        produccionCientificaBuilderService,
        sgiApiSgpService,
        sgiApiCspService,
        convocatoriaBaremacionLogService,
        modelMapper,
        sgiConfigProperties);

    this.puntuacionGrupoRepository = puntuacionGrupoRepository;
    this.puntuacionGrupoInvestigadorRepository = puntuacionGrupoInvestigadorRepository;
    this.mapeoTiposRepository = mapeoTiposRepository;
    this.sgiApiPiiService = sgiApiPiiService;

    loadPredicates();
  }

  protected TipoPuntuacion getTipoPuntuacion() {
    return TipoPuntuacion.INVENCIONES;
  }

  protected void loadPredicates() {

    // INVENCION
    // getHmTipoBaremoPredicates().put(TipoBaremo.INVENCION,
    // getPredicateIsInvencionNotEmpty());

  }

  protected BigDecimal evaluateBaremoModulador(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoModulador(baremacionInput) - start");

    log.debug("evaluateBaremoModulador(baremacionInput) - end");
    return new BigDecimal("1.00");
  }

  protected BigDecimal evaluateBaremoExtra(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoExtra(baremacionInput) - start");

    log.debug("evaluateBaremoExtra(baremacionInput) - end");
    return BigDecimal.ZERO;
  }

  /* -------------------- predicates -------------------- */

  private LongPredicate getPredicateIsInvencionNotEmpty() {
    return produccionCientificaId -> isValorCampoNotEmpty(produccionCientificaId, CodigoCVN.E060_030_070_010);
  }

  @Override
  protected List<Pair<BigDecimal, Baremo>> calculatePuntosBaremosPrincipales(BaremacionInput baremacionInput,
      List<Baremo> baremosPrincipales) {
    return baremosPrincipales.stream().map(
        baremo -> {
          baremacionInput.setBaremo(getModelMapper().map(baremo, BaremoInput.class));
          return Pair.of(this.evaluateBaremoPrincipal(baremacionInput), baremo);
        })
        .filter(pairPuntosBaremo -> pairPuntosBaremo.getFirst().compareTo(BigDecimal.ZERO) > 0)
        .collect(Collectors.toList());
  }

  /* -------------------- Copy invenciones -------------------- */

  public void copyInvenciones(Integer anioInicio, Integer anioFin) {
    log.debug("copyInvenciones(anioInicio, anioFin) - start");

    // TODO borrar las invenciones anteriores o actualizarlas

    IntStream.range(anioInicio, anioFin).forEach(this::saveInvenciones);

    log.debug("copyInvenciones(anioInicio, anioFin) - end");
  }

  private void saveInvenciones(Integer anio) {
    sgiApiPiiService.findInvencionesProduccionCientifica(anio).stream().forEach(invencion -> {

      Long invencionId = invencion.getId();
      String produccionCientificaRef = PREFIX_INVENCIONES + invencionId;

      ProduccionCientifica produccionCientifica = ProduccionCientifica.builder()
          .epigrafeCVN(EpigrafeCVN.E050_030_010_000)
          .produccionCientificaRef(produccionCientificaRef)
          .build();

      Long produccionCientificaId = getProduccionCientificaBuilderService().addProduccionCientifaAndEstado(
          produccionCientifica,
          TipoEstadoProduccion.VALIDADO);

      addCampoInvencionTitulo(invencion, produccionCientificaId);
      addCampoInvencionPorcentajeTitularidad(invencion, produccionCientificaId);
      addCampoInvencionTipoProteccion(invencion, produccionCientificaId);
      addCampoInvencionSolicitudProteccion(invencion, produccionCientificaId);
      addCampoInvencionViaProteccionEspania(invencion, produccionCientificaId);
      addCampoInvencionViaProteccionEuropea(invencion, produccionCientificaId);
      addCampoInvencionCuantiaLicencias(invencion, produccionCientificaId);

      sgiApiPiiService.findInvencionInventorByInvencionIdAndAnio(invencionId, anio).forEach(
          personaRef -> getProduccionCientificaBuilderService().addAutorByPersonaRefAndIp(produccionCientificaId,
              personaRef, Boolean.FALSE));
    });
  }

  private void addCampoInvencionTipoProteccion(InvencionDto invencion, Long produccionCientificaId) {
    // TODO
  }

  private void addCampoInvencionViaProteccionEuropea(InvencionDto invencion, Long produccionCientificaId) {
    // TODO
  }

  private void addCampoInvencionCuantiaLicencias(InvencionDto invencion, Long produccionCientificaId) {
    // TODO
  }

  private void addCampoInvencionViaProteccionEspania(InvencionDto invencion, Long produccionCientificaId) {
    // TODO
  }

  private void addCampoInvencionSolicitudProteccion(InvencionDto invencion, Long produccionCientificaId) {
    // TODO
  }

  private void addCampoInvencionPorcentajeTitularidad(InvencionDto invencion, Long produccionCientificaId) {
    // TODO
  }

  private void addCampoInvencionTitulo(InvencionDto invencion, Long produccionCientificaId) {
    getProduccionCientificaBuilderService()
        .addCampoProduccionCientificaAndValor(produccionCientificaId, CodigoCVN.E050_030_010_020,
            invencion.getTitulo());
  }

}
