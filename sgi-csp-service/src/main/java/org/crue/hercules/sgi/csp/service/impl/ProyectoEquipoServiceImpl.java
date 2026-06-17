package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.MissingInvestigadorPrincipalInProyectoEquipoException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto.Estado;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEquipoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoEquipoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ProyectoEquipo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoEquipoServiceImpl implements ProyectoEquipoService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_ACTION = "action";
  private static final String MSG_FIELD_ACTION_MODIFICAR = "action.modificar";
  private static final String MSG_PROBLEM_ACCION_DENEGADA = "org.springframework.util.Assert.accion.denegada.message";
  private static final String MSG_MODEL_PROYECTO = "org.crue.hercules.sgi.csp.model.Proyecto.message";
  private static final String MSG_MODEL_PROYECTO_EQUIPO = "org.crue.hercules.sgi.csp.model.ProyectoEquipo.message";
  private static final String MSG_FECHAS_PROYECTO_EQUIPO = "proyectoEquipo.fechas";
  private static final String MSG_PROYECTO_EQUIPO_FECHA_FIN_ANTERIOR_A_FECHA_INICIO = "proyectoEquipo.fechaFinAnteriorAFechaInicio";
  private static final String MSG_PROYECTO_EQUIPO_OVERLOAP = "proyectoEquipo.overloap";

  private final ProyectoEquipoRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final SolicitudRepository solicitudRepository;
  private final ProyectoHelper proyectoHelper;
  private final SgiConfigProperties sgiConfigProperties;

  public ProyectoEquipoServiceImpl(ProyectoEquipoRepository repository, ProyectoRepository proyectoRepository,
      SolicitudRepository solicitudRepository, ProyectoHelper proyectoHelper, SgiConfigProperties sgiConfigProperties) {
    this.repository = repository;
    this.proyectoRepository = proyectoRepository;
    this.solicitudRepository = solicitudRepository;
    this.proyectoHelper = proyectoHelper;
    this.sgiConfigProperties = sgiConfigProperties;
  }

  /**
   * Actualiza el listado de {@link ProyectoEquipo} de la {@link Proyecto} con el
   * listado proyectoEquipos añadiendo, editando o eliminando los elementos segun
   * proceda.
   *
   * @param proyectoId      Id de la {@link Proyecto}.
   * @param proyectoEquipos lista con los nuevos {@link ProyectoEquipo} a guardar.
   * @return la entidad {@link ProyectoEquipo} persistida.
   */
  @Override
  @Transactional
  public List<ProyectoEquipo> update(Long proyectoId, List<ProyectoEquipo> proyectoEquipos) {
    log.debug("update(Long proyectoId, List<ProyectoEquipo> proyectoEquipos) - start");

    Proyecto proyecto = proyectoRepository.findById(proyectoId)
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoId));

    List<ProyectoEquipo> miembrosEquipoBD = repository.findAllByProyectoId(proyectoId);

    // Miembros del equipo eliminados
    List<ProyectoEquipo> proyectoEquiposEliminar = miembrosEquipoBD.stream()
        .filter(bd -> proyectoEquipos.stream()
            .map(ProyectoEquipo::getId)
            .noneMatch(id -> Objects.equals(id, bd.getId())))
        .toList();

    if (!proyectoEquiposEliminar.isEmpty()) {
      repository.deleteAll(proyectoEquiposEliminar);
    }

    List<ProyectoEquipo> miembrosEquipoSorted = sortMiembrosEquipo(proyectoEquipos);

    List<ProyectoEquipo> proyectoEquipoToSave = new ArrayList<>();
    List<ProyectoEquipo> returnValue = new ArrayList<>();

    ProyectoEquipo proyectoEquipoAnterior = null;
    for (ProyectoEquipo miembroEquipo : miembrosEquipoSorted) {

      ProyectoEquipo miembroEquipoBD = miembroEquipo.getId() != null
          ? miembrosEquipoBD.stream()
              .filter(p -> p.getId().equals(miembroEquipo.getId())).findFirst()
              .orElseThrow(() -> new ProyectoEquipoNotFoundException(miembroEquipo.getId()))
          : null;

      validateMiembroEquipo(miembroEquipo, miembroEquipoBD, proyectoEquipoAnterior, proyecto);

      miembroEquipo.setProyectoId(proyectoId);

      if (miembroEquipoBD != null) {
        if (hasEditableFieldsChanged(miembroEquipoBD, miembroEquipo)) {
          miembroEquipoBD.setPersonaRef(miembroEquipo.getPersonaRef());
          miembroEquipoBD.setRolProyecto(miembroEquipo.getRolProyecto());
          miembroEquipoBD.setFechaInicio(miembroEquipo.getFechaInicio());
          miembroEquipoBD.setFechaFin(miembroEquipo.getFechaFin());
          proyectoEquipoToSave.add(miembroEquipoBD);
        }

        returnValue.add(miembroEquipoBD);
      } else {
        proyectoEquipoToSave.add(miembroEquipo);
        returnValue.add(miembroEquipo);
      }

      proyectoEquipoAnterior = miembroEquipo;

    }

    validateInvestigadorPrincipal(proyecto, miembrosEquipoSorted);

    if (!proyectoEquipoToSave.isEmpty()) {
      repository.saveAll(proyectoEquipoToSave);
    }

    log.debug("updateProyectoEquiposConvocatoria(Long proyectoId, List<ProyectoEquipo> proyectoEquipos) - end");

    return returnValue;
  }

  /**
   * Ordena los {@link ProyectoEquipo} para que la validación de solapamientos
   * pueda hacerse comparando elementos consecutivos.
   *
   * Los miembros sin {@code fechaInicio} se colocan primero, ordenados por
   * {@code personaRef}. El resto se ordenan por {@code fechaInicio} y, en caso
   * de empate, por {@code personaRef}.
   *
   * @param miembros lista de miembros a ordenar.
   * @return nueva lista ordenada.
   */
  private List<ProyectoEquipo> sortMiembrosEquipo(List<ProyectoEquipo> miembros) {
    List<ProyectoEquipo> sinFecha = miembros.stream()
        .filter(m -> m.getFechaInicio() == null)
        .sorted(Comparator.comparing(ProyectoEquipo::getPersonaRef))
        .toList();

    List<ProyectoEquipo> conFecha = miembros.stream()
        .filter(m -> m.getFechaInicio() != null)
        .sorted(Comparator.comparing(ProyectoEquipo::getFechaInicio)
            .thenComparing(ProyectoEquipo::getPersonaRef))
        .toList();

    List<ProyectoEquipo> resultado = new ArrayList<>(sinFecha);
    resultado.addAll(conFecha);

    return resultado;
  }

  /**
   * Verifica que el solicitante del proyecto figure en el equipo cuando el
   * {@link Proyecto} ha sido concedido a partir de una {@link Solicitud} de tipo
   * {@link FormularioSolicitud#PROYECTO}.
   *
   * @param proyecto proyecto a comprobar.
   * @param miembros lista de miembros del equipo tras la actualización.
   * @throws MissingInvestigadorPrincipalInProyectoEquipoException si el
   *                                                               solicitante no
   *                                                               está en el
   *                                                               equipo.
   */
  private void validateInvestigadorPrincipal(Proyecto proyecto, List<ProyectoEquipo> miembros) {
    if (proyecto.getSolicitudId() == null || !proyecto.getEstado().getEstado().equals(Estado.CONCEDIDO)) {
      return;
    }

    Solicitud solicitud = solicitudRepository.findById(proyecto.getSolicitudId())
        .orElseThrow(() -> new SolicitudNotFoundException(proyecto.getSolicitudId()));

    if (solicitud.getFormularioSolicitud().equals(FormularioSolicitud.PROYECTO)
        && miembros.stream().map(ProyectoEquipo::getPersonaRef)
            .noneMatch(ref -> ref.equals(solicitud.getSolicitanteRef()))) {
      throw new MissingInvestigadorPrincipalInProyectoEquipoException();
    }
  }

  /**
   * Valida un {@link ProyectoEquipo}.
   *
   * <ul>
   * <li>Si el miembro ya existe en base de datos, verifica que el
   * {@link Proyecto} al que pertenece no haya cambiado.</li>
   * <li>Verifica que {@code fechaInicio} sea anterior a {@code fechaFin}.</li>
   * <li>Verifica que las fechas del miembro estén dentro del rango del
   * {@link Proyecto}.</li>
   * <li>Verifica que no haya solapamiento de fechas con el miembro anterior de
   * la misma persona (la lista de entrada debe estar ordenada por
   * {@code personaRef} y {@code fechaInicio}).</li>
   * </ul>
   *
   * @param miembroEquipo         datos de entrada del miembro a validar.
   * @param miembroEquipoBD       entidad recuperada de base de datos,
   *                              o {@code null} si el miembro es nuevo.
   * @param miembroEquipoAnterior miembro procesado inmediatamente antes en la
   *                              lista ordenada, o {@code null} si es el primero.
   * @param proyecto              proyecto al que pertenece el equipo.
   */
  private void validateMiembroEquipo(ProyectoEquipo miembroEquipo, ProyectoEquipo miembroEquipoBD,
      ProyectoEquipo miembroEquipoAnterior, Proyecto proyecto) {

    if (miembroEquipoBD != null) {
      Assert.isTrue(miembroEquipoBD.getProyectoId().equals(miembroEquipo.getProyectoId()),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_ACCION_DENEGADA)
              .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO))
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_EQUIPO))
              .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_MODIFICAR))
              .build());
    }

    if (miembroEquipo.getFechaInicio() != null && miembroEquipo.getFechaFin() != null) {
      Assert.isTrue(miembroEquipo.getFechaInicio().isBefore(miembroEquipo.getFechaFin()),
          ApplicationContextSupport.getMessage(MSG_PROYECTO_EQUIPO_FECHA_FIN_ANTERIOR_A_FECHA_INICIO));
    }

    if (miembroEquipo.getFechaInicio() != null && proyecto.getFechaInicio() != null) {
      Assert.isTrue(
          (miembroEquipo.getFechaInicio().isAfter(proyecto.getFechaInicio())
              || miembroEquipo.getFechaInicio().equals(proyecto.getFechaInicio())),
          ApplicationContextSupport.getMessage(MSG_FECHAS_PROYECTO_EQUIPO));
    }

    Instant proyectoFechaFin = proyecto.getFechaFinDefinitiva() != null ? proyecto.getFechaFinDefinitiva()
        : proyecto.getFechaFin();
    if (miembroEquipo.getFechaFin() != null && proyectoFechaFin != null) {
      Assert.isTrue(
          (miembroEquipo.getFechaFin().isBefore(proyectoFechaFin)
              || miembroEquipo.getFechaFin().equals(proyectoFechaFin)),
          ApplicationContextSupport.getMessage(MSG_FECHAS_PROYECTO_EQUIPO));
    }

    if (miembroEquipoAnterior != null
        && miembroEquipoAnterior.getPersonaRef().equals(miembroEquipo.getPersonaRef())) {
      Assert.isTrue(miembroEquipoAnterior.getFechaFin() != null
          && miembroEquipoAnterior.getFechaFin().isBefore(miembroEquipo.getFechaInicio()),
          ApplicationContextSupport.getMessage(MSG_PROYECTO_EQUIPO_OVERLOAP));
    }
  }

  /**
   * Indica si dos {@link ProyectoEquipo} difieren en alguno de sus campos
   * editables.
   *
   * @param miembroA primer miembro a comparar.
   * @param miembroB segundo miembro a comparar.
   * @return {@code true} si al menos un campo es distinto.
   */
  private boolean hasEditableFieldsChanged(ProyectoEquipo miembroA, ProyectoEquipo miembroB) {
    Long rolMiembroA = miembroA.getRolProyecto() == null ? null : miembroA.getRolProyecto().getId();
    Long rolMiembroB = miembroB.getRolProyecto() == null ? null : miembroB.getRolProyecto().getId();
    return !Objects.equals(miembroA.getPersonaRef(), miembroB.getPersonaRef())
        || !Objects.equals(rolMiembroA, rolMiembroB)
        || !Objects.equals(miembroA.getFechaInicio(), miembroB.getFechaInicio())
        || !Objects.equals(miembroA.getFechaFin(), miembroB.getFechaFin());
  }

  /**
   * Obtiene una entidad {@link ProyectoEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoEquipo}.
   * @return ProyectoEquipo la entidad {@link ProyectoEquipo}.
   */
  @Override
  public ProyectoEquipo findById(Long id) {
    log.debug("findById(Long id) - start");
    final ProyectoEquipo returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoEquipoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ProyectoEquipo} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoEquipo} de la {@link Proyecto}
   *         paginadas.
   */
  public Page<ProyectoEquipo> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");

    Specification<ProyectoEquipo> specs = ProyectoEquipoSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoEquipo> returnValue = repository.findAll(specs, pageable);

    proyectoHelper.checkCanAccessProyecto(proyectoId);

    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Devuelve un listado de {@link ProyectoEquipo} asociados a un {@link Proyecto}
   * y una fecha de fin
   * 
   * @param proyectoId Identificador de {@link Proyecto}.
   * @param fechaFin   Fecha de fin del miembro de equipo
   * @return listado de {@link ProyectoEquipo}.
   */
  @Override
  public List<ProyectoEquipo> findAllByProyectoIdAndFechaFin(Long proyectoId, Instant fechaFin) {
    log.debug("findAllByProyectoIdAndFechaFin(Long proyectoId, Instant fechaFin) - start");
    List<ProyectoEquipo> returnValue = repository.findAllByProyectoIdAndFechaFin(proyectoId, fechaFin);
    log.debug("findAllByProyectoIdAndFechaFin(Long proyectoId, Instant fechaFin) - end");
    return returnValue;
  }

  /**
   * Devuelve un listado de {@link ProyectoEquipo} asociados a un {@link Proyecto}
   * y una fecha de fin mayor a la indicada
   * 
   * @param proyectoId Identificador de {@link Proyecto}.
   * @param fechaFin   Fecha de fin del miembro de equipo
   * @return listado de {@link ProyectoEquipo}.
   */
  @Override
  public List<ProyectoEquipo> findAllByProyectoIdAndFechaFinGreaterThan(Long proyectoId, Instant fechaFin) {
    log.debug("findAllByProyectoIdAndFechaFinGreaterThan(Long proyectoId, Instant fechaFin) - start");
    List<ProyectoEquipo> returnValue = repository.findAllByProyectoIdAndFechaFinGreaterThan(proyectoId, fechaFin);
    log.debug("findAllByProyectoIdAndFechaFinGreaterThan(Long proyectoId, Instant fechaFin) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ProyectoEquipo} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @return la lista de entidades {@link ProyectoEquipo} del {@link Proyecto}
   * 
   */
  @Override
  public List<ProyectoEquipo> findAllByProyectoId(Long proyectoId) {
    log.debug("findAllByProyectoId(Long proyectoId) - start");
    List<ProyectoEquipo> returnValue = repository.findAllByProyectoId(proyectoId);
    log.debug("findAllByProyectoId(Long proyectoId) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista filtrada de investigadores principales del
   * {@link Proyecto}.
   *
   * Son investiador principales los {@link ProyectoEquipo} que tienen el
   * {@link RolProyecto} con el flag {@link RolProyecto#rolPrincipal} a
   * <code>true</code>.
   * 
   * @param proyectoId Identificador del {@link Proyecto}.
   * @return la lista investigadores principales del {@link Proyecto}.
   */
  @Override
  public List<ProyectoEquipo> findInvestigadoresPrincipales(Long proyectoId) {
    log.debug("findInvestigadoresPrincipales(Long proyectoId) - start");

    AssertHelper.idNotNull(proyectoId, Proyecto.class);
    Specification<ProyectoEquipo> specs = ProyectoEquipoSpecifications.byProyectoId(proyectoId)
        .and(ProyectoEquipoSpecifications.byRolPrincipal(true));

    List<ProyectoEquipo> returnValue = repository.findAll(specs);

    log.debug("findInvestigadoresPrincipales(Long proyectoId) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista filtrada de investigadores principales del
   * {@link Proyecto} en el momento actual.
   *
   * Son investiador principales los {@link ProyectoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag {@link RolProyecto#rolPrincipal} a
   * <code>true</code>.
   * 
   * @param proyectoId Identificador del {@link Proyecto}.
   * @return la lista de los investigadores principales del
   *         {@link Proyecto} en el momento actual.
   */
  @Override
  public List<ProyectoEquipo> findInvestigadoresPrincipalesActuales(Long proyectoId) {
    log.debug("findInvestigadoresPrincipalesActuales(Long proyectoId) - start");

    AssertHelper.idNotNull(proyectoId, Proyecto.class);
    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();
    List<ProyectoEquipo> returnValue = repository.findInvestigadoresPrincipales(proyectoId, fechaActual);

    log.debug("findInvestigadoresPrincipalesActuales(Long proyectoId) - end");
    return returnValue;
  }

  /**
   * Comprueba si alguno de los {@link ProyectoEquipo} del {@link Proyecto}
   * tienen fechas
   * 
   * @param proyectoId el id del {@link Proyecto}.
   * @return true si existen y false en caso contrario.
   */
  @Override
  public boolean proyectoHasProyectoEquipoWithDates(Long proyectoId) {
    log.debug("proyectoHasProyectoEquipoWithDates({})  - start", proyectoId);

    Specification<ProyectoEquipo> specs = ProyectoEquipoSpecifications.byProyectoId(proyectoId)
        .and(ProyectoEquipoSpecifications.withFechaInicioOrFechaFin());

    boolean hasProyectoEquipoWithDates = repository.count(specs) > 0;
    log.debug("proyectoHasProyectoEquipoWithDates({})  - end", proyectoId);
    return hasProyectoEquipoWithDates;
  }

}
