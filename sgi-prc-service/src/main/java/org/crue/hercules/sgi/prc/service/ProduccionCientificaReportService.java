package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.dto.DetalleGrupoInvestigacionOutput;
import org.crue.hercules.sgi.prc.dto.DetalleGrupoInvestigacionOutput.ResumenCosteIndirectoOutput;
import org.crue.hercules.sgi.prc.dto.DetalleGrupoInvestigacionOutput.ResumenInvestigadorOutput;
import org.crue.hercules.sgi.prc.dto.DetalleGrupoInvestigacionOutput.ResumenProduccionCientificaOutput;
import org.crue.hercules.sgi.prc.dto.DetalleGrupoInvestigacionOutput.ResumenSexenioOutput;
import org.crue.hercules.sgi.prc.dto.DetalleGrupoInvestigacionOutput.ResumenTotalOutput;
import org.crue.hercules.sgi.prc.dto.DetalleProduccionInvestigadorOutput;
import org.crue.hercules.sgi.prc.dto.DetalleProduccionInvestigadorOutput.TipoProduccionInvestigadorOutput;
import org.crue.hercules.sgi.prc.dto.ResumenPuntuacionGrupoAnioOutput;
import org.crue.hercules.sgi.prc.dto.ResumenPuntuacionGrupoAnioOutput.ResumenPuntuacionGrupo;
import org.crue.hercules.sgi.prc.dto.csp.GrupoDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.repository.specification.BaremoSpecifications;
import org.crue.hercules.sgi.prc.repository.specification.ProduccionCientificaSpecifications;
import org.crue.hercules.sgi.prc.repository.specification.PuntuacionGrupoInvestigadorSpecifications;
import org.crue.hercules.sgi.prc.repository.specification.PuntuacionGrupoSpecifications;
import org.crue.hercules.sgi.prc.repository.specification.PuntuacionItemInvestigadorSpecifications;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Report service ProduccionCientifica
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ProduccionCientificaReportService {

  private static final String TITLE = ".title";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String MESSAGE_KEY_ID = "id";

  private final ProduccionCientificaRepository produccionCientificaRepository;
  private final PuntuacionGrupoRepository puntuacionGrupoRepository;
  private final PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository;
  private final PuntuacionItemInvestigadorRepository puntuacionItemInvestigadorRepository;
  private final CampoProduccionCientificaRepository campoProduccionCientificaRepository;
  private final ValorCampoRepository valorCampoRepository;
  private final ConvocatoriaBaremacionRepository convocatoriaBaremacionRepository;
  private final BaremoRepository baremoRepository;
  private final SgiApiCspService sgiApiCspService;
  private final SgiApiSgpService sgiApiSgpService;

  /**
   * Obtiene los datos del informe Resumen puntuación grupos con las puntuaciones
   * globales de cada uno de los grupos de investigación que a fecha 31 de
   * diciembre del año pasado como parámetro estén en estado activo y tengan el
   * campo "especial investigación" a valor "No".
   *
   * @param anio Año de la convocatoria
   * @return datos del informe
   */
  public ResumenPuntuacionGrupoAnioOutput getDataReportResumenPuntuacionGrupos(Integer anio) {

    log.debug("getDataReportResumenPuntuacionGrupos({}) - start", anio);

    try {
      List<ResumenPuntuacionGrupo> puntuacionesGrupos = sgiApiCspService.findAllGruposByAnio(anio - 1).stream().map(
          grupo -> {
            String personaResponsable = sgiApiCspService
                .findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(grupo.getId()).stream()
                .collect(Collectors.joining(", "));

            Specification<PuntuacionGrupo> specs = PuntuacionGrupoSpecifications
                .byGrupoRef(grupo.getId())
                .and(PuntuacionGrupoSpecifications.byConvocatoriaBaremacionAnio(anio));

            PuntuacionGrupo puntuacionGrupo = puntuacionGrupoRepository.findAll(specs).get(0);

            return ResumenPuntuacionGrupo.builder()
                .grupo(grupo.getNombre())
                .personaResponsable(personaResponsable)
                .puntosSexenios(puntuacionGrupo.getPuntosSexenios())
                .puntosCostesIndirectos(puntuacionGrupo.getPuntosCostesIndirectos())
                .puntosProduccion(puntuacionGrupo.getPuntosProduccion())
                .build();
          }).collect(Collectors.toList());

      return ResumenPuntuacionGrupoAnioOutput.builder().puntuacionesGrupos(puntuacionesGrupos).anio(anio).build();

    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }
  }

  /**
   * Obtiene los datos del informe Detalle producción investigador con el
   * detalle de los puntos obtenidos en cada item de producción científica para un
   * investigador concreto. En los puntos de producción científica se debe de
   * detallar los puntos de cada uno de los tipos y los items en concreto que han
   * dado lugar a la puntuación obtenida.
   *
   * @param anio       Año de la convocatoria
   * @param personaRef Id del investigador
   * @return datos del informe
   */
  public DetalleProduccionInvestigadorOutput getDataDetalleProduccionInvestigador(Integer anio, String personaRef) {

    log.debug("getDataDetalleProduccionInvestigador({},{}) - start", anio, personaRef);

    try {

      String investigador = sgiApiSgpService.findPersonaById(personaRef)
          .map(persona -> persona.getNombre() + " " + persona.getApellidos())
          .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
              .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(PersonaDto.class))
              .parameter(MESSAGE_KEY_ID, personaRef).build()));

      List<TipoProduccionInvestigadorOutput> tipos = new ArrayList<>();

      // add puntuacion produccion
      Specification<PuntuacionItemInvestigador> additionalSpec = PuntuacionItemInvestigadorSpecifications
          .byTipoPuntuacionNotIn(Arrays.asList(TipoPuntuacion.COSTE_INDIRECTO, TipoPuntuacion.SEXENIO));
      TipoProduccionInvestigadorOutput tipoProduccion = getPuntosInvestigadorByTipoPuntuacion(anio, personaRef,
          additionalSpec, "produccion", null);

      // add puntuacion libros
      additionalSpec = PuntuacionItemInvestigadorSpecifications.byTipoPuntuacion(TipoPuntuacion.LIBROS);
      tipoProduccion.getProduccionesCientificas()
          .add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "libros",
              CodigoCVN.E060_010_010_030));

      // add puntuacion articulos
      additionalSpec = PuntuacionItemInvestigadorSpecifications.byTipoPuntuacion(TipoPuntuacion.ARTICULOS);
      tipoProduccion.getProduccionesCientificas()
          .add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "articulos",
              CodigoCVN.E060_010_010_030));

      // add puntuacion congresos
      additionalSpec = PuntuacionItemInvestigadorSpecifications.byTipoPuntuacion(TipoPuntuacion.CONGRESOS);
      tipoProduccion.getProduccionesCientificas()
          .add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "congresos",
              CodigoCVN.E060_010_020_030));

      // add puntuacion tesis
      additionalSpec = PuntuacionItemInvestigadorSpecifications.byTipoPuntuacion(TipoPuntuacion.DIRECCION_TESIS);
      tipoProduccion.getProduccionesCientificas()
          .add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "tesis",
              CodigoCVN.E030_040_000_030));

      // add puntuacion obrasArtisticas
      additionalSpec = PuntuacionItemInvestigadorSpecifications.byTipoPuntuacion(TipoPuntuacion.OBRAS_ARTISTICAS);
      tipoProduccion.getProduccionesCientificas()
          .add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "obrasArtisticas",
              CodigoCVN.E050_020_030_010));

      // add puntuacion comites
      additionalSpec = PuntuacionItemInvestigadorSpecifications.byTipoPuntuacion(TipoPuntuacion.COMITES_EDITORIALES);
      tipoProduccion.getProduccionesCientificas()
          .add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "comitesEditoriales",
              CodigoCVN.E060_030_030_010));

      // add puntuacion organizacionActividades
      additionalSpec = PuntuacionItemInvestigadorSpecifications
          .byTipoPuntuacion(TipoPuntuacion.ORGANIZACION_ACTIVIDADES);
      tipoProduccion.getProduccionesCientificas()
          .add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "organizacionActividades",
              CodigoCVN.E060_020_030_010));

      // add puntuacion proyectos
      additionalSpec = PuntuacionItemInvestigadorSpecifications
          .byTipoPuntuacion(TipoPuntuacion.PROYECTOS_INVESTIGACION);
      tipoProduccion.getProduccionesCientificas()
          .add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "proyectos",
              CodigoCVN.E050_020_010_010));

      // add puntuacion contratos
      additionalSpec = PuntuacionItemInvestigadorSpecifications.byTipoPuntuacion(TipoPuntuacion.CONTRATOS);
      tipoProduccion.getProduccionesCientificas()
          .add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "contratos",
              CodigoCVN.E050_020_020_010));

      // add puntuacion invenciones
      additionalSpec = PuntuacionItemInvestigadorSpecifications.byTipoPuntuacion(TipoPuntuacion.INVENCIONES);
      tipoProduccion.getProduccionesCientificas()
          .add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "invenciones",
              CodigoCVN.E050_030_010_020));

      // add produccion
      tipos.add(tipoProduccion);

      // add puntuacion costes indirectos
      additionalSpec = PuntuacionItemInvestigadorSpecifications.byTipoPuntuacion(TipoPuntuacion.COSTE_INDIRECTO);
      tipos.add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "costesIndirectos", null));

      // add puntuacion sexenios
      additionalSpec = PuntuacionItemInvestigadorSpecifications.byTipoPuntuacion(TipoPuntuacion.SEXENIO);
      tipos.add(getPuntosInvestigadorByTipoPuntuacion(anio, personaRef, additionalSpec, "sexenios", null));

      return DetalleProduccionInvestigadorOutput.builder()
          .anio(anio)
          .investigador(investigador)
          .tipos(tipos)
          .build();
    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }
  }

  private TipoProduccionInvestigadorOutput getPuntosInvestigadorByTipoPuntuacion(Integer anio, String personaRef,
      Specification<PuntuacionItemInvestigador> additionalSpec, String prefixTitle, CodigoCVN codigoCVNTitulo) {

    Specification<PuntuacionItemInvestigador> specs = PuntuacionItemInvestigadorSpecifications
        .byPersonaRef(personaRef)
        .and(PuntuacionItemInvestigadorSpecifications.byConvocatoriaBaremacionAnio(anio))
        .and(additionalSpec);

    List<PuntuacionItemInvestigador> puntuaciones = puntuacionItemInvestigadorRepository.findAll(specs);

    BigDecimal puntos = puntuaciones.stream()
        .map(PuntuacionItemInvestigador::getPuntos)
        .reduce(new BigDecimal("0.00"), BigDecimal::add);

    List<TipoProduccionInvestigadorOutput> prcs = new ArrayList<>();

    if (null != codigoCVNTitulo) {
      prcs = puntuaciones.stream().map(puntuacionItemInvestigador -> TipoProduccionInvestigadorOutput.builder()
          .titulo(getTituloProduccionCientifica(
              puntuacionItemInvestigador.getProduccionCientificaId(), codigoCVNTitulo))
          .puntos(puntuacionItemInvestigador.getPuntos())
          .produccionesCientificas(new ArrayList<>())
          .build())
          .collect(Collectors.toList());
    }

    return TipoProduccionInvestigadorOutput.builder()
        .titulo(ApplicationContextSupport.getMessage(prefixTitle + TITLE).toUpperCase())
        .puntos(puntos)
        .produccionesCientificas(prcs)
        .build();
  }

  private String getTituloProduccionCientifica(Long produccionCientificaId, CodigoCVN codigoCVNTitulo) {
    return campoProduccionCientificaRepository
        .findByCodigoCVNAndProduccionCientificaId(codigoCVNTitulo, produccionCientificaId)
        .map(campo -> valorCampoRepository.findAllByCampoProduccionCientificaId(campo.getId()).get(0).getValor())
        .orElse(null);
  }

  /**
   * Obtiene los datos del informe Detalle grupo con el detalle del
   * reparto de la baremación de una convocatoria de un grupo de investigación. Se
   * muestra el listado de investigadores que forman parte del grupo y los puntos
   * Sexenios, de Costes indirectos y de cada baremo de producción (libros,
   * artículos, trabajos presentados en congresos, dirección de tesis, obras
   * artísticas, comités editoriales, organización de actividades I+D+i, proyectos
   * de investigación, contratos e invenciones).
   *
   * @param anio    Año de la convocatoria
   * @param grupoId Id del grupo
   * @return datos del informe
   */
  public DetalleGrupoInvestigacionOutput getDataReportDetalleGrupo(Integer anio, Long grupoId) {

    log.debug("getDataReportDetalleGrupo({},{}) - start", anio, grupoId);

    try {
      String grupo = sgiApiCspService.findGrupoById(grupoId)
          .map(GrupoDto::getNombre)
          .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
              .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(GrupoDto.class))
              .parameter(MESSAGE_KEY_ID, grupoId).build()));

      Specification<PuntuacionGrupo> puntuacionGrupoSpecs = PuntuacionGrupoSpecifications
          .byGrupoRef(grupoId)
          .and(PuntuacionGrupoSpecifications.byConvocatoriaBaremacionAnio(anio));

      PuntuacionGrupo puntuacionGrupo = puntuacionGrupoRepository.findAll(puntuacionGrupoSpecs).get(0);

      return convocatoriaBaremacionRepository
          .findById(puntuacionGrupo.getConvocatoriaBaremacionId()).map(convocatoriaBaremacion -> {
            List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigadores = puntuacionGrupoInvestigadorRepository
                .findAll(PuntuacionGrupoInvestigadorSpecifications.byPuntuacionGrupoId(puntuacionGrupo.getId()));

            List<ResumenInvestigadorOutput> investigadores = getInvestigadores(puntuacionesGrupoInvestigadores);

            ResumenSexenioOutput sexenios = getSexenios(puntuacionesGrupoInvestigadores, convocatoriaBaremacion);

            List<ResumenProduccionCientificaOutput> produccionesCientificas = getProduccionesCientificas(
                puntuacionesGrupoInvestigadores, convocatoriaBaremacion);

            ResumenCosteIndirectoOutput costesIndirectos = getCostesIndirectos(puntuacionGrupo,
                puntuacionesGrupoInvestigadores, convocatoriaBaremacion);

            BigDecimal totalProduccion = puntuacionGrupo.getPuntosProduccion().multiply(convocatoriaBaremacion
                .getPuntoProduccion());
            List<ResumenTotalOutput> totales = getTotales(totalProduccion, sexenios.getImporte(),
                costesIndirectos.getImporte());

            return DetalleGrupoInvestigacionOutput.builder()
                .grupo(grupo)
                .anio(anio)
                .investigadores(investigadores)
                .precioPuntoProduccion(convocatoriaBaremacion.getPuntoProduccion())
                .precioPuntoCostesIndirectos(convocatoriaBaremacion.getPuntoCostesIndirectos())
                .precioPuntoSexenio(convocatoriaBaremacion.getPuntoSexenio())
                .sexenios(sexenios)
                .produccionesCientificas(produccionesCientificas)
                .costesIndirectos(costesIndirectos)
                .totales(totales)
                .build();
          }).orElse(null);
    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }
  }

  private List<ResumenTotalOutput> getTotales(BigDecimal totalProduccion, BigDecimal totalSexenios,
      BigDecimal totalCostesIndirectos) {
    List<ResumenTotalOutput> totales = new ArrayList<>();
    totales.add(ResumenTotalOutput.builder()
        .tipo(ApplicationContextSupport.getMessage("informeDetalleGrupo.totalProduccion"))
        .importe(totalProduccion)
        .build());

    totales.add(ResumenTotalOutput.builder()
        .tipo(ApplicationContextSupport.getMessage("informeDetalleGrupo.totalSexenios"))
        .importe(totalSexenios)
        .build());

    totales.add(ResumenTotalOutput.builder()
        .tipo(ApplicationContextSupport.getMessage("informeDetalleGrupo.totalCostesIndirectos"))
        .importe(totalCostesIndirectos)
        .build());

    totales.add(ResumenTotalOutput.builder()
        .tipo(ApplicationContextSupport.getMessage("informeDetalleGrupo.totalCostesIndirectos"))
        .importe(totalCostesIndirectos)
        .build());

    BigDecimal importeTotal = totalProduccion.add(totalSexenios).add(totalCostesIndirectos);

    totales.add(ResumenTotalOutput.builder()
        .tipo(ApplicationContextSupport.getMessage("informeDetalleGrupo.total"))
        .importe(importeTotal)
        .build());

    return totales;
  }

  private List<ResumenProduccionCientificaOutput> getProduccionesCientificas(
      List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigadores,
      ConvocatoriaBaremacion convocatoriaBaremacion) {

    List<ResumenProduccionCientificaOutput> produccionesCientificas = new ArrayList<>();

    produccionesCientificas.add(getResumenProduccionCientificaLibros(puntuacionesGrupoInvestigadores,
        "libros", TipoPuntuacion.LIBROS, convocatoriaBaremacion));
    produccionesCientificas.add(getResumenProduccionCientificaArticulos(puntuacionesGrupoInvestigadores,
        "articulos", TipoPuntuacion.ARTICULOS, convocatoriaBaremacion));
    produccionesCientificas.add(getResumenProduccionCientifica(puntuacionesGrupoInvestigadores,
        "congresos", TipoPuntuacion.CONGRESOS, convocatoriaBaremacion, EpigrafeCVN.E060_010_020_000));
    produccionesCientificas.add(getResumenProduccionCientifica(puntuacionesGrupoInvestigadores,
        "tesis", TipoPuntuacion.DIRECCION_TESIS, convocatoriaBaremacion, EpigrafeCVN.E030_040_000_000));
    produccionesCientificas.add(getResumenProduccionCientifica(puntuacionesGrupoInvestigadores,
        "obrasArtisticas", TipoPuntuacion.OBRAS_ARTISTICAS, convocatoriaBaremacion, EpigrafeCVN.E050_020_030_000));
    produccionesCientificas.add(getResumenProduccionCientifica(puntuacionesGrupoInvestigadores,
        "comitesEditoriales", TipoPuntuacion.DIRECCION_TESIS, convocatoriaBaremacion, EpigrafeCVN.E060_030_030_000));
    produccionesCientificas.add(getResumenProduccionCientifica(puntuacionesGrupoInvestigadores,
        "organizacionActividades", TipoPuntuacion.ORGANIZACION_ACTIVIDADES, convocatoriaBaremacion,
        EpigrafeCVN.E060_020_030_000));
    produccionesCientificas.add(getResumenProduccionCientifica(puntuacionesGrupoInvestigadores,
        "proyectos", TipoPuntuacion.PROYECTOS_INVESTIGACION, convocatoriaBaremacion, EpigrafeCVN.E050_020_010_000));
    produccionesCientificas.add(getResumenProduccionCientifica(puntuacionesGrupoInvestigadores,
        "contratos", TipoPuntuacion.CONTRATOS, convocatoriaBaremacion, EpigrafeCVN.E050_020_020_000));
    produccionesCientificas.add(getResumenProduccionCientifica(puntuacionesGrupoInvestigadores,
        "invenciones", TipoPuntuacion.INVENCIONES, convocatoriaBaremacion, EpigrafeCVN.E050_030_010_000));

    return produccionesCientificas;
  }

  private ResumenSexenioOutput getSexenios(
      List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigadores,
      ConvocatoriaBaremacion convocatoriaBaremacion) {

    Specification<Baremo> specs = BaremoSpecifications
        .byConvocatoriaBaremacionId(convocatoriaBaremacion.getId())
        .and(BaremoSpecifications.byConfiguracionBaremoTipoBaremo(TipoBaremo.SEXENIO))
        .and(BaremoSpecifications.byConfiguracionBaremoActivoIsTrue());

    List<Baremo> baremos = baremoRepository.findAll(specs);

    BigDecimal puntos = new BigDecimal("0.00");
    Integer numeroPRCs = 0;
    if (!CollectionUtils.isEmpty(baremos)) {
      puntos = getPuntosByTipoPuntuacion(puntuacionesGrupoInvestigadores, TipoPuntuacion.SEXENIO);
      numeroPRCs = puntos.divide(baremos.get(0).getPuntos(), 1, RoundingMode.HALF_UP).intValue();
    }

    return ResumenSexenioOutput.builder()
        .numero(numeroPRCs)
        .puntos(puntos)
        .importe(puntos.multiply(convocatoriaBaremacion.getPuntoSexenio()))
        .build();
  }

  private ResumenCosteIndirectoOutput getCostesIndirectos(
      PuntuacionGrupo puntuacionGrupo,
      List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigadores,
      ConvocatoriaBaremacion convocatoriaBaremacion) {

    Specification<ProduccionCientifica> specs = ProduccionCientificaSpecifications
        .byEpigrafeCVNIn(Arrays.asList(EpigrafeCVN.E050_020_010_000, EpigrafeCVN.E050_020_020_000))
        .and(ProduccionCientificaSpecifications.byConvocatoriaBaremacionId(convocatoriaBaremacion.getId()));

    Integer numeroPRCs = produccionCientificaRepository
        .findAll(specs).stream()
        .filter(produccionCientifica -> hasPuntuacion(puntuacionesGrupoInvestigadores, produccionCientifica.getId()))
        .mapToInt(e -> 1).sum();

    BigDecimal puntos = puntuacionGrupo.getPuntosCostesIndirectos();

    return ResumenCosteIndirectoOutput.builder()
        .numero(numeroPRCs)
        .puntos(puntos)
        .importe(puntos.multiply(convocatoriaBaremacion.getPuntoCostesIndirectos()))
        .build();
  }

  private ResumenProduccionCientificaOutput getResumenProduccionCientificaLibros(
      List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigadores, String prefixTitle,
      TipoPuntuacion tipoPuntuacion,
      ConvocatoriaBaremacion convocatoriaBaremacion) {

    Integer numeroPRCs = produccionCientificaRepository
        .findByEpigrafeCVNAndConvocatoriaBaremacionId(EpigrafeCVN.E060_010_010_000, convocatoriaBaremacion.getId())
        .stream()
        .filter(this::hasCampoAndValorTipoLibro).mapToInt(e -> 1).sum();

    BigDecimal puntos = getPuntosByTipoPuntuacion(puntuacionesGrupoInvestigadores, tipoPuntuacion);

    return ResumenProduccionCientificaOutput.builder()
        .tipo(ApplicationContextSupport.getMessage(prefixTitle + TITLE).toUpperCase())
        .numero(numeroPRCs)
        .puntos(puntos)
        .importe(puntos.multiply(convocatoriaBaremacion.getPuntoProduccion()))
        .build();
  }

  private ResumenProduccionCientificaOutput getResumenProduccionCientificaArticulos(
      List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigadores, String prefixTitle,
      TipoPuntuacion tipoPuntuacion,
      ConvocatoriaBaremacion convocatoriaBaremacion) {

    Integer numeroPRCs = produccionCientificaRepository
        .findByEpigrafeCVNAndConvocatoriaBaremacionId(EpigrafeCVN.E060_010_010_000, convocatoriaBaremacion.getId())
        .stream()
        .filter(produccionCientifica -> hasPuntuacion(puntuacionesGrupoInvestigadores, produccionCientifica.getId()))
        .filter(this::hasCampoAndValorTipoArticulo)
        .mapToInt(e -> 1).sum();

    BigDecimal puntos = getPuntosByTipoPuntuacion(puntuacionesGrupoInvestigadores, tipoPuntuacion);

    return ResumenProduccionCientificaOutput.builder()
        .tipo(ApplicationContextSupport.getMessage(prefixTitle + TITLE).toUpperCase())
        .numero(numeroPRCs)
        .puntos(puntos)
        .importe(puntos.multiply(convocatoriaBaremacion.getPuntoProduccion()))
        .build();
  }

  private boolean hasPuntuacion(List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigadores,
      Long produccionCientificaId) {
    return puntuacionesGrupoInvestigadores.stream()
        .map(PuntuacionGrupoInvestigador::getPuntuacionItemInvestigador)
        .anyMatch(puntuacionItemInvestigador -> puntuacionItemInvestigador.getProduccionCientificaId()
            .compareTo(produccionCientificaId) == 0);
  }

  private boolean hasCampoAndValorTipoLibro(ProduccionCientifica produccionCientifica) {
    return campoProduccionCientificaRepository
        .findByCodigoCVNAndProduccionCientificaId(CodigoCVN.E060_010_010_010, produccionCientifica.getId())
        .map(this::hasValorCampoTipoLibro)
        .orElse(false);
  }

  private boolean hasValorCampoTipoLibro(CampoProduccionCientifica campo) {
    return valorCampoRepository.findAllByCampoProduccionCientificaId(campo.getId()).stream().anyMatch(this::isLibro);
  }

  private boolean isLibro(ValorCampo valorCampo) {
    return valorCampo.getValor().equals("032") || valorCampo.getValor().equals("004")
        || valorCampo.getValor().equals("208") || valorCampo.getValor().equals("COMENTARIO_SISTEMATICO_NORMAS");
  }

  private boolean hasCampoAndValorTipoArticulo(ProduccionCientifica produccionCientifica) {
    return campoProduccionCientificaRepository
        .findByCodigoCVNAndProduccionCientificaId(CodigoCVN.E060_010_010_010, produccionCientifica.getId())
        .map(this::hasValorCampoTipoArticulo)
        .orElse(false);
  }

  private boolean hasValorCampoTipoArticulo(CampoProduccionCientifica campo) {
    return valorCampoRepository.findAllByCampoProduccionCientificaId(campo.getId()).stream().allMatch(this::isArticulo);
  }

  private boolean isArticulo(ValorCampo valorCampo) {
    return valorCampo.getValor().equals("020");
  }

  private ResumenProduccionCientificaOutput getResumenProduccionCientifica(
      List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigadores, String prefixTitle,
      TipoPuntuacion tipoPuntuacion,
      ConvocatoriaBaremacion convocatoriaBaremacion, EpigrafeCVN epigrafeCVN) {

    Integer numeroPRCs = produccionCientificaRepository
        .findByEpigrafeCVNAndConvocatoriaBaremacionId(epigrafeCVN, convocatoriaBaremacion.getId()).stream()
        .filter(produccionCientifica -> hasPuntuacion(puntuacionesGrupoInvestigadores, produccionCientifica.getId()))
        .mapToInt(e -> 1).sum();

    BigDecimal puntos = getPuntosByTipoPuntuacion(puntuacionesGrupoInvestigadores, tipoPuntuacion);

    return ResumenProduccionCientificaOutput.builder()
        .tipo(ApplicationContextSupport.getMessage(prefixTitle + TITLE).toUpperCase())
        .numero(numeroPRCs)
        .puntos(puntos)
        .importe(puntos.multiply(convocatoriaBaremacion.getPuntoProduccion()))
        .build();
  }

  private BigDecimal getPuntosByTipoPuntuacion(List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigadores,
      TipoPuntuacion tipoPuntuacion) {
    return puntuacionesGrupoInvestigadores.stream()
        .filter(puntuacionGrupoInvestigador -> puntuacionGrupoInvestigador.getPuntuacionItemInvestigador()
            .getTipoPuntuacion().equals(tipoPuntuacion))
        .map(PuntuacionGrupoInvestigador::getPuntos)
        .reduce(new BigDecimal("0.00"), BigDecimal::add);
  }

  private List<ResumenInvestigadorOutput> getInvestigadores(
      List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigadores) {

    List<String> investigadoresIds = puntuacionesGrupoInvestigadores.stream()
        .map(PuntuacionGrupoInvestigador::getPuntuacionItemInvestigador)
        .map(PuntuacionItemInvestigador::getPersonaRef)
        .distinct()
        .collect(Collectors.toList());

    return investigadoresIds.stream().map(investigadorId -> {
      String investigador = sgiApiSgpService.findPersonaById(investigadorId)
          .map(persona -> persona.getNombre() + " " + persona.getApellidos())
          .orElse("");

      BigDecimal puntosProduccion = puntuacionesGrupoInvestigadores.stream()
          .filter(puntuacionGrupoInvestigador -> isPersonaRefAndTipoPuntuacionProduccion(
              puntuacionGrupoInvestigador.getPuntuacionItemInvestigador(), investigadorId))
          .map(PuntuacionGrupoInvestigador::getPuntos)
          .reduce(new BigDecimal("0.00"), BigDecimal::add);

      BigDecimal puntosCostesIndirectos = puntuacionesGrupoInvestigadores.stream()
          .filter(puntuacionGrupoInvestigador -> isPersonaRefAndTipoPuntuacion(
              puntuacionGrupoInvestigador.getPuntuacionItemInvestigador(),
              TipoPuntuacion.COSTE_INDIRECTO, investigadorId))
          .map(PuntuacionGrupoInvestigador::getPuntos)
          .reduce(new BigDecimal("0.00"), BigDecimal::add);

      return ResumenInvestigadorOutput.builder()
          .investigador(investigador)
          .puntosProduccion(puntosProduccion)
          .puntosCostesIndirectos(puntosCostesIndirectos)
          .build();

    }).collect(Collectors.toList());
  }

  private boolean isPersonaRefAndTipoPuntuacionProduccion(PuntuacionItemInvestigador puntuacion,
      String investigadorId) {
    return puntuacion.getPersonaRef().equals(investigadorId) &&
        !puntuacion.getTipoPuntuacion().equals(TipoPuntuacion.COSTE_INDIRECTO) &&
        !puntuacion.getTipoPuntuacion().equals(TipoPuntuacion.SEXENIO);
  }

  private boolean isPersonaRefAndTipoPuntuacion(PuntuacionItemInvestigador puntuacion, TipoPuntuacion tipoPuntuacion,
      String investigadorId) {
    return puntuacion.getPersonaRef().equals(investigadorId) && puntuacion.getTipoPuntuacion().equals(tipoPuntuacion);
  }

}
