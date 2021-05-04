package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.RolProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.RolProyectoSpecifications;
import org.crue.hercules.sgi.csp.service.RolProyectoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link RolProyecto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RolProyectoServiceImpl implements RolProyectoService {

  private final RolProyectoRepository repository;

  public RolProyectoServiceImpl(RolProyectoRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link RolProyecto}.
   * 
   * @param rolProyecto la entidad {@link RolProyecto} a guardar.
   * @return RolProyecto la entidad {@link RolProyecto} persistida.
   */
  @Override
  @Transactional
  public RolProyecto create(RolProyecto rolProyecto) {
    log.debug("create(RolProyecto rolProyecto) - start");

    Assert.isNull(rolProyecto.getId(), "Id tiene que ser null para crear la RolProyecto");

    validarDatosRolProyecto(rolProyecto, null);
    rolProyecto.setActivo(Boolean.TRUE);
    RolProyecto returnValue = repository.save(rolProyecto);

    log.debug("create(RolProyecto rolProyecto) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link RolProyecto}.
   * 
   * @param rolProyecto rolProyectoActualizar {@link RolProyecto} con los datos
   *                    actualizados.
   * @return {@link RolProyecto} actualizado.
   */
  @Override
  @Transactional
  public RolProyecto update(RolProyecto rolProyecto) {
    log.debug("update(RolProyecto rolProyecto) - start");

    Assert.notNull(rolProyecto.getId(), "Id no puede ser null para actualizar RolProyecto");

    return repository.findById(rolProyecto.getId()).map((rolProyectoExistente) -> {

      validarDatosRolProyecto(rolProyecto, rolProyectoExistente);
      rolProyectoExistente.setAbreviatura(rolProyecto.getAbreviatura());
      rolProyectoExistente.setNombre(rolProyecto.getNombre());
      rolProyectoExistente.setDescripcion(rolProyecto.getDescripcion());
      rolProyectoExistente.setRolPrincipal(rolProyecto.getRolPrincipal());
      rolProyectoExistente.setResponsableEconomico(rolProyecto.getResponsableEconomico());
      rolProyectoExistente.setEquipo(rolProyecto.getEquipo());

      RolProyecto returnValue = repository.save(rolProyectoExistente);

      log.debug("update(RolProyecto rolProyecto) - end");
      return returnValue;
    }).orElseThrow(() -> new RolProyectoNotFoundException(rolProyecto.getId()));
  }

  /**
   * Reactiva el {@link RolProyecto}.
   *
   * @param id Id del {@link RolProyecto}.
   * @return la entidad {@link RolProyecto} persistida.
   */
  @Override
  @Transactional
  public RolProyecto enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "RolProyecto id no puede ser null para reactivar un RolProyecto");

    return repository.findById(id).map(rolProyecto -> {
      if (rolProyecto.getActivo()) {
        return rolProyecto;
      }

      Assert.isTrue(!(repository.findByAbreviaturaAndActivoIsTrue(rolProyecto.getAbreviatura()).isPresent()),
          "Ya existe un RolProyecto activo con la abreviatura '" + rolProyecto.getAbreviatura() + "'");
      Assert.isTrue(!(repository.findByNombreAndActivoIsTrue(rolProyecto.getNombre()).isPresent()),
          "Ya existe un RolProyecto activo con el nombre '" + rolProyecto.getNombre() + "'");

      rolProyecto.setActivo(Boolean.TRUE);
      RolProyecto returnValue = repository.save(rolProyecto);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new RolProyectoNotFoundException(id));
  }

  /**
   * Desactiva el {@link RolProyecto}.
   *
   * @param id Id del {@link RolProyecto}.
   * @return la entidad {@link RolProyecto} persistida.
   */
  @Override
  @Transactional
  public RolProyecto disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "RolProyecto id no puede ser null para desactivar un RolProyecto");

    return repository.findById(id).map(rolProyecto -> {
      if (!rolProyecto.getActivo()) {
        return rolProyecto;
      }
      rolProyecto.setActivo(Boolean.FALSE);
      RolProyecto returnValue = repository.save(rolProyecto);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new RolProyectoNotFoundException(id));
  }

  /**
   * Comprueba la existencia del {@link RolProyecto} por id.
   *
   * @param id el id de la entidad {@link RolProyecto}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene una entidad {@link RolProyecto} por id.
   * 
   * @param id Identificador de la entidad {@link RolProyecto}.
   * @return RolProyecto la entidad {@link RolProyecto}.
   */
  @Override
  public RolProyecto findById(Long id) {
    log.debug("findById(Long id) - start");
    final RolProyecto returnValue = repository.findById(id).orElseThrow(() -> new RolProyectoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link RolProyecto} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolProyecto} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<RolProyecto> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<RolProyecto> specs = RolProyectoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<RolProyecto> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link RolProyecto} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolProyecto} paginadas y filtradas.
   */
  @Override
  public Page<RolProyecto> findAllTodos(String query, Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Specification<RolProyecto> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<RolProyecto> returnValue = repository.findAll(specs, paging);
    log.debug("findAllTodos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Comprueba y valida los datos de rolProyecto.
   * 
   * @param datosRolProyecto
   * @param datosOriginales
   */
  private void validarDatosRolProyecto(RolProyecto datosRolProyecto, RolProyecto datosOriginales) {
    log.debug("validarDatosRolProyecto(RolProyecto datosRolProyecto, RolProyecto datosOriginales) - start");

    // Campos obligatorios
    Assert.isTrue(StringUtils.isNotBlank(datosRolProyecto.getAbreviatura()),
        "Abreviatura es un campo obligatorio para RolProyecto");
    Assert.isTrue(StringUtils.isNotBlank(datosRolProyecto.getNombre()),
        "Nombre es un campo obligatorio para RolProyecto");
    Assert.notNull(datosRolProyecto.getEquipo(), "Equipo es un campo obligatorio para RolProyecto");

    // Unicidad abreviatura entre los activos
    repository.findByAbreviaturaAndActivoIsTrue(datosRolProyecto.getAbreviatura()).ifPresent((rolProyecto) -> {
      Assert.isTrue(datosRolProyecto.getId() == rolProyecto.getId(),
          "Ya existe un RolProyecto activo con la abreviatura '" + rolProyecto.getAbreviatura() + "'");
    });

    // Unicidad nombre entre los activos
    repository.findByNombreAndActivoIsTrue(datosRolProyecto.getNombre()).ifPresent((rolProyecto) -> {
      Assert.isTrue(datosRolProyecto.getId() == rolProyecto.getId(),
          "Ya existe un RolProyecto activo con el nombre '" + rolProyecto.getNombre() + "'");
    });

    log.debug("validarDatosRolProyecto(RolProyecto datosRolProyecto, RolProyecto datosOriginales) - end");
  }

}
