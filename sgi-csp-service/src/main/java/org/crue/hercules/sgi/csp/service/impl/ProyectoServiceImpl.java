package org.crue.hercules.sgi.csp.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.crue.hercules.sgi.csp.enums.TipoEstadoProyectoEnum;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio implementación para la gestión de {@link Proyecto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoServiceImpl implements ProyectoService {

  private final ProyectoRepository repository;

  public ProyectoServiceImpl(ProyectoRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link Proyecto}.
   * 
   * @param proyecto          la entidad {@link Proyecto} a guardar.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return proyecto la entidad {@link Proyecto} persistida.
   */
  @Override
  @Transactional
  public Proyecto create(Proyecto proyecto, List<String> unidadGestionRefs) {
    log.debug("create(Proyecto proyecto, List<String> unidadGestionRefs) - start");
    // TODO implementar lógica creación

    // Crea el estado inicial de la solicitud
    EstadoProyecto estadoProyecto = addEstadoProyecto(proyecto, TipoEstadoProyectoEnum.BORRADOR, null);

    // Actualiza la el estado actual del proyecto con el nuevo estado
    proyecto.setEstado(estadoProyecto);
    log.debug("create(Proyecto proyecto, List<String> unidadGestionRefs) - end");
    return proyecto;
  }

  /**
   * Actualiza los datos del {@link Proyecto}.
   * 
   * @param proyecto                proyectoActualizar {@link Proyecto} con los
   *                                datos actualizados.
   * @param unidadGestionRefs       lista de referencias de las unidades de
   *                                gestion permitidas para el usuario.
   * @param isAdministradorOrGestor Indicador de si el usuario que realiza la
   *                                acutalización es administrador o gestor.
   * @return {@link Proyecto} actualizado.
   */
  @Override
  @Transactional
  public Proyecto update(Proyecto proyecto, List<String> unidadGestionRefs, Boolean isAdministradorOrGestor) {
    log.debug("update(Proyecto proyecto, List<String> unidadGestionRefs, Boolean isAdministradorOrGestor) - start");
    // TODO implementar lógica actualización
    log.debug("update(Proyecto proyecto, List<String> unidadGestionRefs, Boolean isAdministradorOrGestor) - end");
    return proyecto;
  }

  /**
   * Reactiva el {@link Proyecto}.
   *
   * @param id                Id del {@link Proyecto}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return la entidad {@link Proyecto} persistida.
   */
  @Override
  @Transactional
  public Proyecto enable(Long id, List<String> unidadGestionRefs) {
    log.debug("enable(Long id, List<String> unidadGestionRefs) - start");

    Assert.notNull(id, "Proyecto id no puede ser null para reactivar un Proyecto");

    return repository.findById(id).map(proyecto -> {

      Assert.isTrue(
          unidadGestionRefs.stream()
              .anyMatch(unidadGestionRef -> unidadGestionRef.equals(proyecto.getUnidadGestionRef())),
          "El proyecto pertenece a una Unidad de Gestión no gestionable por el usuario");

      if (proyecto.getActivo()) {
        // Si esta activo no se hace nada
        return proyecto;
      }

      proyecto.setActivo(true);

      Proyecto returnValue = repository.save(proyecto);
      log.debug("enable(Long id, List<String> unidadGestionRefs) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoNotFoundException(id));
  }

  /**
   * Desactiva el {@link Proyecto}.
   *
   * @param id                Id del {@link Proyecto}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return la entidad {@link Proyecto} persistida.
   */
  @Override
  @Transactional
  public Proyecto disable(Long id, List<String> unidadGestionRefs) {
    log.debug("disable(Long id, List<String> unidadGestionRefs) - start");

    Assert.notNull(id, "Proyecto id no puede ser null para desactivar un Proyecto");

    return repository.findById(id).map(proyecto -> {
      Assert.isTrue(
          unidadGestionRefs.stream()
              .anyMatch(unidadGestionRef -> unidadGestionRef.equals(proyecto.getUnidadGestionRef())),
          "El proyecto pertenece a una Unidad de Gestión no gestionable por el usuario");

      if (!proyecto.getActivo()) {
        // Si no esta activo no se hace nada
        return proyecto;
      }

      proyecto.setActivo(false);

      Proyecto returnValue = repository.save(proyecto);
      log.debug("disable(Long id, List<String> unidadGestionRefs) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoNotFoundException(id));
  }

  /**
   * Obtiene una entidad {@link Proyecto} por id.
   * 
   * @param id                Identificador de la entidad {@link Proyecto}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return Proyecto la entidad {@link Proyecto}.
   */
  @Override
  public Proyecto findById(Long id, List<String> unidadGestionRefs) {
    log.debug("findById(Long id, List<String> unidadGestionRefs) - start");
    final Proyecto returnValue = repository.findById(id).orElseThrow(() -> new ProyectoNotFoundException(id));

    Assert.isTrue(
        unidadGestionRefs.stream()
            .anyMatch(unidadGestionRef -> unidadGestionRef.equals(returnValue.getUnidadGestionRef())),
        "El proyecto pertenece a una Unidad de Gestión no gestionable por el usuario");

    log.debug("findById(Long id, List<String> unidadGestionRefs) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Proyecto} activas paginadas y filtradas.
   *
   * @param query             información del filtro.
   * @param paging            información de paginación.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<Proyecto> findAllRestringidos(List<QueryCriteria> query, Pageable paging,
      List<String> unidadGestionRefs) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs) - start");
    if (query == null) {
      query = new ArrayList<>();
    }

    Specification<Proyecto> specByQuery = new QuerySpecification<Proyecto>(query);
    Specification<Proyecto> specActivos = ProyectoSpecifications.activos();
    Specification<Proyecto> specByUnidadGestionRefIn = ProyectoSpecifications.unidadGestionRefIn(unidadGestionRefs);
    Specification<Proyecto> specs = Specification.where(specActivos).and(specByUnidadGestionRefIn).and(specByQuery);

    Page<Proyecto> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Proyecto} paginadas y filtradas.
   *
   * @param query             información del filtro.
   * @param paging            información de paginación.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion.
   * @return el listado de entidades {@link Proyecto} paginadas y filtradas.
   */
  @Override
  public Page<Proyecto> findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging,
      List<String> unidadGestionRefs) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs) - start");
    if (query == null) {
      query = new ArrayList<>();
    }

    Specification<Proyecto> specByQuery = new QuerySpecification<Proyecto>(query);
    Specification<Proyecto> specByUnidadGestionRefIn = ProyectoSpecifications.unidadGestionRefIn(unidadGestionRefs);
    Specification<Proyecto> specs = Specification.where(specByUnidadGestionRefIn).and(specByQuery);
    // TODO implementar buscador avanzado

    Page<Proyecto> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs) - end");
    return returnValue;
  }

  /**
   * Añade el nuevo {@link EstadoProyecto} y actualiza la {@link Proyecto} con
   * dicho estado.
   * 
   * @param proyecto           la {@link Proyecto} para la que se añade el nuevo
   *                           estado.
   * @param tipoEstadoProyecto El nuevo {@link TipoEstadoProyectoEnum} de la
   *                           {@link Proyecto}.
   * @return la {@link Proyecto} con el estado actualizado.
   */
  private EstadoProyecto addEstadoProyecto(Proyecto proyecto, TipoEstadoProyectoEnum tipoEstadoProyecto,
      String comentario) {
    log.debug(
        "addEstadoProyecto(Proyecto proyecto, TipoEstadoProyectoEnum tipoEstadoProyecto, String comentario) - start");

    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setEstado(tipoEstadoProyecto);
    estadoProyecto.setIdProyecto(proyecto.getId());
    estadoProyecto.setComentario(comentario);
    estadoProyecto.setFechaEstado(LocalDateTime.now(ZoneId.of("Europe/Madrid")));

    // TODO implementar repository
    // EstadoProyecto returnValue = estadoProyectoRepository.save(estadoProyecto);

    log.debug(
        "addEstadoProyecto(Proyecto proyecto, TipoEstadoProyectoEnum tipoEstadoProyecto, String comentario) - end");
    return null;
  }

}
