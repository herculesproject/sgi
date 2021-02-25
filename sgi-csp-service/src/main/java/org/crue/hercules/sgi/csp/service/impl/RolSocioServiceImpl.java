package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.RolSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.repository.RolSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.RolSocioSpecifications;
import org.crue.hercules.sgi.csp.service.RolSocioService;
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
 * Service Implementation para gestion {@link RolSocio}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RolSocioServiceImpl implements RolSocioService {

  private final RolSocioRepository repository;

  public RolSocioServiceImpl(RolSocioRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link RolSocio}.
   * 
   * @param rolSocio la entidad {@link RolSocio} a guardar.
   * @return RolSocio la entidad {@link RolSocio} persistida.
   */
  @Override
  @Transactional
  public RolSocio create(RolSocio rolSocio) {
    log.debug("create(RolSocio rolSocio) - start");

    Assert.isNull(rolSocio.getId(), "Id tiene que ser null para crear la RolSocio");

    validarDatosRolSocio(rolSocio, null);
    rolSocio.setActivo(Boolean.TRUE);
    RolSocio returnValue = repository.save(rolSocio);

    log.debug("create(RolSocio rolSocio) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link RolSocio}.
   * 
   * @param rolSocio rolSocioActualizar {@link RolSocio} con los datos
   *                 actualizados.
   * @return {@link RolSocio} actualizado.
   */
  @Override
  @Transactional
  public RolSocio update(RolSocio rolSocio) {
    log.debug("update(RolSocio rolSocio) - start");

    Assert.notNull(rolSocio.getId(), "Id no puede ser null para actualizar RolSocio");

    return repository.findById(rolSocio.getId()).map((rolSocioExistente) -> {

      validarDatosRolSocio(rolSocio, rolSocioExistente);
      rolSocioExistente.setAbreviatura(rolSocio.getAbreviatura());
      rolSocioExistente.setNombre(rolSocio.getNombre());
      rolSocioExistente.setDescripcion(rolSocio.getDescripcion());
      rolSocioExistente.setCoordinador(rolSocio.getCoordinador());

      RolSocio returnValue = repository.save(rolSocioExistente);

      log.debug("update(RolSocio rolSocio) - end");
      return returnValue;
    }).orElseThrow(() -> new RolSocioNotFoundException(rolSocio.getId()));
  }

  /**
   * Reactiva el {@link RolSocio}.
   *
   * @param id Id del {@link RolSocio}.
   * @return la entidad {@link RolSocio} persistida.
   */
  @Override
  @Transactional
  public RolSocio enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "RolSocio id no puede ser null para reactivar un RolSocio");

    return repository.findById(id).map(rolSocio -> {
      if (rolSocio.getActivo()) {
        return rolSocio;
      }

      Assert.isTrue(!(repository.findByAbreviaturaAndActivoIsTrue(rolSocio.getAbreviatura()).isPresent()),
          "Ya existe un RolSocio activo con la abreviatura '" + rolSocio.getAbreviatura() + "'");
      Assert.isTrue(!(repository.findByNombreAndActivoIsTrue(rolSocio.getNombre()).isPresent()),
          "Ya existe un RolSocio activo con el nombre '" + rolSocio.getNombre() + "'");

      rolSocio.setActivo(Boolean.TRUE);
      RolSocio returnValue = repository.save(rolSocio);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new RolSocioNotFoundException(id));
  }

  /**
   * Desactiva el {@link RolSocio}.
   *
   * @param id Id del {@link RolSocio}.
   * @return la entidad {@link RolSocio} persistida.
   */
  @Override
  @Transactional
  public RolSocio disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "RolSocio id no puede ser null para desactivar un RolSocio");

    return repository.findById(id).map(rolSocio -> {
      if (!rolSocio.getActivo()) {
        return rolSocio;
      }
      rolSocio.setActivo(Boolean.FALSE);
      RolSocio returnValue = repository.save(rolSocio);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new RolSocioNotFoundException(id));
  }

  /**
   * Comprueba la existencia del {@link RolSocio} por id.
   *
   * @param id el id de la entidad {@link RolSocio}.
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
   * Obtiene una entidad {@link RolSocio} por id.
   * 
   * @param id Identificador de la entidad {@link RolSocio}.
   * @return RolSocio la entidad {@link RolSocio}.
   */
  @Override
  public RolSocio findById(Long id) {
    log.debug("findById(Long id) - start");
    final RolSocio returnValue = repository.findById(id).orElseThrow(() -> new RolSocioNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link RolSocio} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolSocio} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<RolSocio> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<RolSocio> specs = RolSocioSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));
    Page<RolSocio> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link RolSocio} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolSocio} paginadas y filtradas.
   */
  @Override
  public Page<RolSocio> findAllTodos(String query, Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Specification<RolSocio> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<RolSocio> returnValue = repository.findAll(specs, paging);
    log.debug("findAllTodos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Comprueba y valida los datos de rolSocio.
   * 
   * @param datosRolSocio
   * @param datosOriginales
   */
  private void validarDatosRolSocio(RolSocio datosRolSocio, RolSocio datosOriginales) {
    log.debug("validarDatosRolSocio(RolSocio datosRolSocio, RolSocio datosOriginales) - start");

    // Campos obligatorios
    Assert.isTrue(StringUtils.isNotBlank(datosRolSocio.getAbreviatura()),
        "Abreviatura es un campo obligatorio para RolSocio");
    Assert.isTrue(StringUtils.isNotBlank(datosRolSocio.getNombre()), "Nombre es un campo obligatorio para RolSocio");

    // Unicidad abreviatura entre los activos
    repository.findByAbreviaturaAndActivoIsTrue(datosRolSocio.getAbreviatura()).ifPresent((rolSocio) -> {
      Assert.isTrue(datosRolSocio.getId() == rolSocio.getId(),
          "Ya existe un RolSocio activo con la abreviatura '" + rolSocio.getAbreviatura() + "'");
    });

    // Unicidad nombre entre los activos
    repository.findByNombreAndActivoIsTrue(datosRolSocio.getNombre()).ifPresent((rolSocio) -> {
      Assert.isTrue(datosRolSocio.getId() == rolSocio.getId(),
          "Ya existe un RolSocio activo con el nombre '" + rolSocio.getNombre() + "'");
    });

    log.debug("validarDatosRolSocio(RolSocio datosRolSocio, RolSocio datosOriginales) - end");
  }

}
