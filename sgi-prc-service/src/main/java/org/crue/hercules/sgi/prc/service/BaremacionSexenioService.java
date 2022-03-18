package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.BaremacionInput.BaremoInput;
import org.crue.hercules.sgi.prc.dto.csp.GrupoEquipoDto;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoPuntos;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.repository.AliasEnumeradoRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceExperimentalidadRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.repository.specification.PuntuacionItemInvestigadorSpecifications;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de sexenios
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionSexenioService extends BaremacionCommonService {
  public static final String PREFIX_SEXENIOS = "SEX_";

  public static final EpigrafeCVN EPIGRAFE_CVN_SEXENIO = EpigrafeCVN.E060_030_070_000;

  private final PuntuacionGrupoRepository puntuacionGrupoRepository;
  private final PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository;

  public BaremacionSexenioService(
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
      PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository) {
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
    loadPredicates();
  }

  protected TipoPuntuacion getTipoPuntuacion() {
    return TipoPuntuacion.SEXENIO;
  }

  protected void loadPredicates() {

    // SEXENIO
    getHmTipoBaremoPredicates().put(TipoBaremo.SEXENIO, getPredicateIsSexenioNotEmpty());

  }

  /* -------------------- predicates -------------------- */

  private LongPredicate getPredicateIsSexenioNotEmpty() {
    return produccionCientificaId -> isValorCampoNotEmpty(produccionCientificaId, CodigoCVN.E060_030_070_010);
  }

  @Override
  public void evaluateProduccionCientificaByTypeAndAnio(BaremacionInput baremacionInput) {
    log.debug("evaluateProduccionCientificaByTypeAndAnio(baremacionInput) - start");

    setAnio(baremacionInput.getAnio());

    getProduccionCientificaRepository().findByEpigrafeCVNAndConvocatoriaBaremacionId(baremacionInput.getEpigrafeCVN(),
        baremacionInput.getConvocatoriaBaremacionId()).stream().forEach(produccionCientifica -> {
          Long produccionCientificaId = produccionCientifica.getId();
          baremacionInput.setProduccionCientificaId(produccionCientificaId);

          Pair<BigDecimal, Long> pairEvaluateItem = evaluateItemProduccionCientifica(baremacionInput);
          BigDecimal puntos = pairEvaluateItem.getFirst();
          if (puntos.compareTo(BigDecimal.ZERO) > 0) {

            String optionalMessage = String.format("BAREMACION PRINCIPAL SEXENIO %s", puntos.toString());
            traceLog(baremacionInput, optionalMessage);

            List<Autor> autoresBaremables = getAutorRepository()
                .findAllByProduccionCientificaIdAndPersonaRefIsNotNull(produccionCientificaId);

            if (!autoresBaremables.isEmpty()) {
              int numAutores = autoresBaremables.size();
              BigDecimal puntosInvestigador = puntos.divide(new BigDecimal(numAutores), 2, RoundingMode.HALF_UP);

              optionalMessage = String.format("AUTORES BAREMABLES SEXENIOS %d PUNTOS TOTALES=%s --> %s",
                  numAutores, puntos.toString(), puntosInvestigador.toString());
              traceLog(baremacionInput, optionalMessage);

              autoresBaremables.stream()
                  .filter(autor -> isAutorGrupoValidado(autor.getId(), produccionCientificaId))
                  .forEach(autor -> evaluatePuntuacionItemInvestigador(baremacionInput, puntosInvestigador, autor));
            }
          }
        });

    log.debug("evaluateProduccionCientificaByTypeAndAnio(baremacionInput) - end");

  }

  @Override
  protected Pair<BigDecimal, Long> evaluateItemProduccionCientifica(BaremacionInput baremacionInput) {
    log.debug("evaluateItemProduccionCientifica(baremacionInput) - start");
    BigDecimal puntos = null;

    List<Baremo> baremos = findBaremosByBaremacionInput(baremacionInput);

    // PRINCIPAL
    Pair<BigDecimal, Long> pairEvaluateItemPrincipal = evaluateItemProduccionCientificaSexenio(
        baremacionInput, baremos);
    puntos = pairEvaluateItemPrincipal.getFirst();
    Long newProduccionCientificaId = pairEvaluateItemPrincipal.getSecond();

    log.debug("evaluateItemProduccionCientifica(baremacionInput) - end");
    return Pair.of(puntos, newProduccionCientificaId);
  }

  protected Pair<BigDecimal, Long> evaluateItemProduccionCientificaSexenio(
      BaremacionInput baremacionInput, List<Baremo> baremos) {
    log.debug("evaluateItemProduccionCientificaByTipoBaremoPrincipal(baremacionInput, baremos) - start");

    Pair<BigDecimal, Long> pairEvaluateItemPrincipal = Pair.of(BigDecimal.ZERO, -1L);
    List<Baremo> baremosPrincipales = baremos.stream()
        .filter(baremo -> baremo.getConfiguracionBaremo().getTipoPuntos().equals(TipoPuntos.PRINCIPAL))
        .sorted(getBaremoPrioridadComparator())
        .collect(Collectors.toList());

    Optional<Pair<BigDecimal, Baremo>> puntosBaremoPrincipal = baremosPrincipales.stream().map(
        baremo -> {
          baremacionInput.setBaremo(getModelMapper().map(baremo, BaremoInput.class));
          return Pair.of(this.evaluateBaremoPrincipal(baremacionInput), baremo);
        })
        .filter(pairPuntosBaremo -> pairPuntosBaremo.getFirst().compareTo(BigDecimal.ZERO) > 0)
        .findFirst();

    if (puntosBaremoPrincipal.isPresent()) {
      BigDecimal puntos = puntosBaremoPrincipal.get().getFirst();
      Baremo baremoPrincipal = puntosBaremoPrincipal.get().getSecond();

      String optionalMessage = String.format("PuntuacionBaremoItem PRINCIPAL SEXENIO Baremo[%d] PRC[%d] Puntos[%s]",
          baremoPrincipal.getId(), baremacionInput.getProduccionCientificaId(), puntos.toString());
      traceLog(baremacionInput, optionalMessage);

      savePuntuacionBaremoItem(baremoPrincipal.getId(), baremacionInput.getProduccionCientificaId(), puntos,
          baremacionInput.getAnio()).getId();

      pairEvaluateItemPrincipal = Pair.of(puntos, baremacionInput.getProduccionCientificaId());
    }
    log.debug("evaluateItemProduccionCientificaByTipoBaremoPrincipal(baremacionInput, baremos) - end");
    return pairEvaluateItemPrincipal;
  }

  @Override
  protected BigDecimal evaluateBaremoPrincipal(BaremacionInput baremacionInput) {
    log.debug("calcularBaremoPrincipalPublicacion(produccionCientificaId, baremo) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    if (evaluateProduccionCientificaByTipoBaremo(baremacionInput, tipoBaremo)) {
      puntos = baremacionInput.getBaremo().getPuntos();
      if (tipoBaremo.equals(TipoBaremo.SEXENIO)) {
        BigDecimal numeroSexenios = findValoresByCampoProduccionCientificaId(CodigoCVN.E060_030_070_010,
            baremacionInput.getProduccionCientificaId()).stream()
            .map(valorCampo -> new BigDecimal(valorCampo.getValor()))
            .reduce(new BigDecimal(0), BigDecimal::add);

        puntos = puntos.multiply(numeroSexenios);
      }

      String optionalMessage = String.format("BAREMACION PRINCIPAL [%s] %s", tipoBaremo.name(), puntos.toString());
      traceLog(baremacionInput, optionalMessage);
    }

    log.debug("calcularBaremoPrincipalPublicacion(produccionCientificaId, baremo) - end");
    return puntos;
  }

  @Override
  protected void evaluatePuntuacionItemInvestigador(BaremacionInput baremacionInput, BigDecimal puntosInvestigador,
      Autor autor) {
    log.debug("evaluatePuntuacioItemInvestigador(baremacionInput, puntosInvestigador, anio) - start");

    PuntuacionItemInvestigador puntuacionItemInvestigador = PuntuacionItemInvestigador.builder()
        .anio(getAnio())
        .personaRef(autor.getPersonaRef())
        .tipoPuntuacion(getTipoPuntuacion())
        .produccionCientificaId(baremacionInput.getProduccionCientificaId())
        .puntos(puntosInvestigador)
        .build();

    String optionalMessage = String.format("PuntuacionItemInvestigador SEXENIO %s",
        puntuacionItemInvestigador.toString());
    traceLog(baremacionInput, optionalMessage);

    getPuntuacionItemInvestigadorRepository().save(puntuacionItemInvestigador);

    log.debug("evaluatePuntuacioItemInvestigador(produccionCientificaId, puntosInvestigador, anio) - end");

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

  public void evaluateSexenio(Integer anioInicio, Integer anioFin, Long convocatoriaBaremacionId) {

    copySexenios(anioInicio, anioFin, convocatoriaBaremacionId);

    // Se utilizar anioFin por que es necesario para la tabla
    BaremacionInput baremacionInput = ProduccionCientificaFieldFormatUtil.createBaremacionInput(anioFin - 1,
        convocatoriaBaremacionId, getSgiConfigProperties().getTimeZone());
    baremacionInput.setEpigrafeCVN(EPIGRAFE_CVN_SEXENIO);
    evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

    IntStream.range(anioInicio, anioFin)
        .forEach(anio -> evaluatePuntuacionGrupoInvestigadorSexenio(convocatoriaBaremacionId, anio));
  }

  // TODO remove duplicated call
  private PuntuacionGrupo getOrInitPuntuacionGrupo(Long grupoRef, Long convocatoriaBaremacionId) {
    PuntuacionGrupo puntuacionGrupo = puntuacionGrupoRepository
        .findByConvocatoriaBaremacionIdAndGrupoRef(convocatoriaBaremacionId, grupoRef)
        .orElse(null);
    if (null == puntuacionGrupo) {
      puntuacionGrupo = PuntuacionGrupo.builder()
          .convocatoriaBaremacionId(convocatoriaBaremacionId)
          .puntosCostesIndirectos(BigDecimal.ZERO)
          .puntosSexenios(BigDecimal.ZERO)
          .puntosProduccion(BigDecimal.ZERO)
          .grupoRef(grupoRef)
          .build();
      puntuacionGrupo = puntuacionGrupoRepository.save(puntuacionGrupo);
    }

    return puntuacionGrupo;
  }

  // TODO remove duplicated call
  private PuntuacionGrupoInvestigador initPuntuacionGrupoInvestigador(
      PuntuacionItemInvestigador puntuacionItemInvestigador, BigDecimal participacion, Long puntuacionGrupoId) {
    BigDecimal puntosItemInvestigadorPorcentaje = puntuacionItemInvestigador.getPuntos()
        .multiply(participacion.divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP));
    return PuntuacionGrupoInvestigador.builder()
        .puntuacionItemInvestigadorId(puntuacionItemInvestigador.getId())
        .puntuacionGrupoId(puntuacionGrupoId)
        .puntos(puntosItemInvestigadorPorcentaje)
        .build();
  }

  private void evaluatePuntuacionGrupoInvestigadorSexenio(Long convocatoriaBaremacionId, Integer anio) {
    log.debug("evaluatePuntuacionGrupoInvestigadorSexenio(convocatoriaBaremacionId) - start");

    getSgiApiCspService().findAllGruposByAnio(anio).stream().forEach(grupo -> {

      Long grupoRef = grupo.getId();
      PuntuacionGrupo puntuacionGrupoSearch = getOrInitPuntuacionGrupo(grupoRef, convocatoriaBaremacionId);

      Stream.of(puntuacionGrupoSearch).forEach(puntuacionGrupo -> {

        getSgiApiCspService().findAllGruposEquipoByGrupoIdAndAnio(grupoRef, anio).stream().forEach(grupoEquipo -> {

          Specification<PuntuacionItemInvestigador> specs = PuntuacionItemInvestigadorSpecifications
              .byPersonaRef(grupoEquipo.getPersonaRef())
              .and(PuntuacionItemInvestigadorSpecifications.byTipoPuntuacion(TipoPuntuacion.SEXENIO))
              .and(PuntuacionItemInvestigadorSpecifications.byConvocatoriaBaremacionId(convocatoriaBaremacionId));

          getPuntuacionItemInvestigadorRepository().findAll(specs).stream().forEach(puntuacionItemInvestigador -> {
            PuntuacionGrupoInvestigador puntuacionGrupoInvestigador = initPuntuacionGrupoInvestigador(
                puntuacionItemInvestigador, grupoEquipo.getParticipacion(), puntuacionGrupoSearch.getId());
            BigDecimal puntosItemInvestigadorPorcentaje = puntuacionGrupoInvestigador.getPuntos();

            String messageTracing = String.format(
                "SEXENIO Autor[%s] Participacion[%s] PuntuacionGrupoInvestigador[%s] ",
                grupoEquipo.getPersonaRef(), grupoEquipo.getParticipacion().toString(),
                puntuacionGrupoInvestigador.toString());
            getConvocatoriaBaremacionLogService().save(convocatoriaBaremacionId, messageTracing);

            // Solo tenemos en cuenta la última participación en el equipo ya que no sabemos
            // el anio de participación por sexenio
            Optional<PuntuacionGrupoInvestigador> optPGIUpdate = puntuacionGrupoInvestigadorRepository
                .findAllByPuntuacionGrupoIdAndPuntuacionItemInvestigadorId(
                    puntuacionGrupoSearch.getId(), puntuacionItemInvestigador.getId());
            if (optPGIUpdate.isPresent()) {
              PuntuacionGrupoInvestigador puntuacionGrupoInvestigadorUpdate = optPGIUpdate.get();

              puntuacionGrupo.setPuntosSexenios(
                  puntuacionGrupo.getPuntosSexenios().subtract(puntuacionGrupoInvestigadorUpdate.getPuntos()));

              puntuacionGrupoInvestigadorUpdate.setPuntos(puntosItemInvestigadorPorcentaje);
              puntuacionGrupoInvestigadorRepository.save(puntuacionGrupoInvestigadorUpdate);

              puntuacionGrupo.setPuntosSexenios(
                  puntuacionGrupo.getPuntosSexenios().add(puntosItemInvestigadorPorcentaje));

            } else {
              puntuacionGrupoInvestigadorRepository.save(puntuacionGrupoInvestigador);

              puntuacionGrupo.setPuntosSexenios(
                  puntuacionGrupo.getPuntosSexenios().add(puntosItemInvestigadorPorcentaje));
            }
          });
        });

        String messageTracing = String.format("SEXENIO PuntuacionGrupo[%s] ", puntuacionGrupo.toString());
        getConvocatoriaBaremacionLogService().save(convocatoriaBaremacionId, messageTracing);

        puntuacionGrupoRepository.save(puntuacionGrupo);
      });
    });
    log.debug("evaluatePuntuacionGrupoInvestigadorSexenio(convocatoriaBaremacionId) - end");
  }

  private void copySexenios(Integer anioInicio, Integer anioFin, Long convocatoriaBaremacionId) {
    log.debug("copySexenios(anioInicio, anioFin, convocatoriaBaremacionId) - start");

    List<String> personasEquipo = new ArrayList<>();
    IntStream.range(anioInicio, anioFin)
        .forEach(anio -> getSgiApiCspService().findAllGruposByAnio(anio).stream()
            .forEach(grupo -> personasEquipo.addAll(
                getSgiApiCspService().findAllGruposEquipoByGrupoIdAndAnio(grupo.getId(), anio).stream()
                    .map(GrupoEquipoDto::getPersonaRef).collect(Collectors.toList()))));

    personasEquipo.stream().distinct().forEach(personaRef -> saveSexenio(personaRef, convocatoriaBaremacionId));

    log.debug("copySexenios(anioInicio, anioFin, convocatoriaBaremacionId) - end");
  }

  private void saveSexenio(String personaRef, Long convocatoriaBaremacionId) {

    final CodigoCVN codigoCVN = CodigoCVN.E060_030_070_010;

    try {
      Integer numeroSexenios = getSgiApiSgpService().findSexeniosByPersonaId(personaRef)
          .map(sexenio -> Integer.valueOf(sexenio.getNumero())).orElse(0);

      if (numeroSexenios.compareTo(0) > 0) {

        String produccionCientificaRef = PREFIX_SEXENIOS + personaRef;

        ProduccionCientifica produccionCientifica = ProduccionCientifica.builder()
            .epigrafeCVN(EPIGRAFE_CVN_SEXENIO)
            .produccionCientificaRef(produccionCientificaRef)
            .convocatoriaBaremacionId(convocatoriaBaremacionId)
            .build();

        Long produccionCientificaId = getProduccionCientificaBuilderService().addProduccionCientifaAndEstado(
            produccionCientifica,
            TipoEstadoProduccion.VALIDADO);

        String valor = ProduccionCientificaFieldFormatUtil.formatNumber(numeroSexenios.toString());
        getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(produccionCientificaId, codigoCVN,
            valor);

        getProduccionCientificaBuilderService().addAutorByPersonaRefAndIp(produccionCientificaId, personaRef,
            Boolean.FALSE);
      }
    } catch (Exception e) {
      log.debug(e.getMessage());
    }
  }

}
