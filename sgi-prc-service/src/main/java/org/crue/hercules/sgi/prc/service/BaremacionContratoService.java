package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.LongPredicate;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.repository.AliasEnumeradoRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ConfiguracionCampoRepository;
import org.crue.hercules.sgi.prc.repository.IndiceExperimentalidadRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de contratos
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionContratoService extends BaremacionCommonService {
  public static final EpigrafeCVN EPIGRAFE_CVN_CONTRATO = EpigrafeCVN.E050_020_020_000;
  public static final BigDecimal CUANTIA_30000 = new BigDecimal("30000.00");
  public static final BigDecimal CUANTIA_60000 = new BigDecimal("60000.00");
  public static final BigDecimal PUNTOS_CUANTIA_LESS_30000 = new BigDecimal("2.00");
  public static final BigDecimal PUNTOS_CUANTIA_BETWEEN_30000_60000 = new BigDecimal("4.00");
  public static final BigDecimal PUNTOS_CUANTIA_GREATHER_60000 = new BigDecimal("6.00");

  private final PuntuacionGrupoRepository puntuacionGrupoRepository;
  private final PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository;
  private final ConfiguracionCampoRepository configuracionCampoRepository;

  public BaremacionContratoService(
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
      ConfiguracionCampoRepository configuracionCampoRepository) {
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
    this.configuracionCampoRepository = configuracionCampoRepository;

    loadPredicates();
  }

  protected TipoPuntuacion getTipoPuntuacion() {
    return TipoPuntuacion.CONTRATOS;
  }

  protected void loadPredicates() {

    // CONTRATOS
    getHmTipoBaremoPredicates().put(TipoBaremo.CONTRATO_CUANTIA, getPredicateIsCuantiaNotEmpty());
  }

  @Override
  protected BigDecimal evaluateBaremoPrincipal(BaremacionInput baremacionInput) {
    log.debug("calcularBaremoPrincipalPublicacion(produccionCientificaId, baremo) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    if (evaluateProduccionCientificaByTipoBaremo(baremacionInput, tipoBaremo)) {

      puntos = evaluateCuantia(baremacionInput.getProduccionCientificaId());

      String optionalMessage = String.format("BAREMACION CONTRATO CUANTIA PRINCIPAL [%s] %s", tipoBaremo.name(),
          null != puntos ? puntos.toString() : "");
      traceLog(baremacionInput, optionalMessage);
    }

    log.debug("calcularBaremoPrincipalPublicacion(produccionCientificaId, baremo) - end");
    return puntos;
  }

  private BigDecimal evaluateCuantia(Long produccionCientificaId) {
    BigDecimal valorCampo = new BigDecimal(
        findValoresByCampoProduccionCientificaId(CodigoCVN.E050_020_020_200, produccionCientificaId)
            .get(0).getValor());

    if (valorCampo.compareTo(CUANTIA_30000) < 0) {
      return PUNTOS_CUANTIA_LESS_30000;
    }

    if (valorCampo.compareTo(CUANTIA_30000) >= 0 && valorCampo.compareTo(CUANTIA_60000) < 0) {
      return PUNTOS_CUANTIA_BETWEEN_30000_60000;
    }

    if (valorCampo.compareTo(CUANTIA_60000) >= 0) {
      return PUNTOS_CUANTIA_GREATHER_60000;
    }

    return null;
  }

  @Override
  protected void evaluatePuntuacionItemInvestigador(BaremacionInput baremacionInput, BigDecimal puntosInvestigador,
      Autor autor) {
    log.debug("evaluatePuntuacioItemInvestigador(baremacionInput, puntosInvestigador, autor) - start");

    PuntuacionItemInvestigador puntuacionItemInvestigador = PuntuacionItemInvestigador.builder()
        .anio(baremacionInput.getAnio())
        .personaRef(autor.getPersonaRef())
        .tipoPuntuacion(getTipoPuntuacion())
        .produccionCientificaId(baremacionInput.getProduccionCientificaId())
        .puntos(puntosInvestigador)
        .build();

    String optionalMessage = String.format("PuntuacioItemInvestigador CONTRATO Autor[%s] [%s] %s",
        autor.getPersonaRef(), puntosInvestigador.toString(),
        puntuacionItemInvestigador.toString());
    traceLog(baremacionInput, optionalMessage);

    getPuntuacionItemInvestigadorRepository().save(puntuacionItemInvestigador);
    log.debug("evaluatePuntuacioItemInvestigador(produccionCientificaId, puntosInvestigador, autor) - end");
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

  @Override
  protected List<Long> getProduccionCientificaIdsByEpigrafeCVNAndAnio(BaremacionInput baremacionInput) {
    List<Long> result = new ArrayList<>();
    Optional<ConfiguracionCampo> optFechaInicio = configuracionCampoRepository
        .findByEpigrafeCVNAndFechaReferenciaInicioIsTrue(EPIGRAFE_CVN_CONTRATO);
    Optional<ConfiguracionCampo> optFechaFin = configuracionCampoRepository
        .findByEpigrafeCVNAndFechaReferenciaFinIsTrue(EPIGRAFE_CVN_CONTRATO);

    if (optFechaInicio.isPresent() && optFechaFin.isPresent()) {
      CodigoCVN codigoCVNFechaInicio = optFechaInicio.get().getCodigoCVN();
      CodigoCVN codigoCVNFechaFin = optFechaFin.get().getCodigoCVN();
      result = getProduccionCientificaRepository().findAllBaremacionByFechaInicioAndFechaFin(baremacionInput,
          codigoCVNFechaInicio, codigoCVNFechaFin);
    }
    return result;
  }

  @Override
  protected void evaluateAutores(BaremacionInput baremacionInput, BigDecimal puntos, Long newProduccionCientificaId) {
    log.debug("evaluateAutores(baremacionInput, puntos, newProduccionCientificaId) - start");

    List<Autor> autoresBaremables = findAutoresBaremablesByAnio(newProduccionCientificaId, baremacionInput.getAnio());

    evaluateAutoresBaremables(baremacionInput, puntos, newProduccionCientificaId, autoresBaremables);
    log.debug("evaluateAutores(baremacionInput, puntos, newProduccionCientificaId) - end");
  }

  /* -------------------- predicates -------------------- */

  private LongPredicate getPredicateIsCuantiaNotEmpty() {
    return produccionCientificaId -> isValorCampoNotEmpty(produccionCientificaId, CodigoCVN.E050_020_020_200);
  }

}
