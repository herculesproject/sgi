package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.ConfiguracionNotFoundException;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.eti.service.ConfiguracionService;
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
 * Service Implementation para la gestión de {@link Configuracion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConfiguracionServiceImpl implements ConfiguracionService {
  private final ConfiguracionRepository configuracionRepository;

  public ConfiguracionServiceImpl(ConfiguracionRepository configuracionRepository) {
    this.configuracionRepository = configuracionRepository;
  }

  /**
   * Guarda la entidad {@link Configuracion}.
   *
   * @param configuracion la entidad {@link Configuracion} a guardar.
   * @return la entidad {@link Configuracion} persistida.
   */
  @Transactional
  public Configuracion create(Configuracion configuracion) {
    log.debug("Petición a create Configuracion : {} - start", configuracion);
    Assert.isNull(configuracion.getId(), "Configuracion id tiene que ser null para crear una nueva configuracion");

    return configuracionRepository.save(configuracion);
  }

  /**
   * Obtiene todas las entidades {@link Configuracion} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Configuracion} paginadas y filtradas.
   */
  public Page<Configuracion> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Specification<Configuracion> spec = new QuerySpecification<Configuracion>(query);

    Page<Configuracion> returnValue = configuracionRepository.findAll(spec, paging);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Configuracion} por id.
   *
   * @param id el id de la entidad {@link Configuracion}.
   * @return la entidad {@link Configuracion}.
   * @throws ConfiguracionNotFoundException Si no existe ningún
   *                                        {@link Configuracion} con ese id.
   */
  public Configuracion findById(final Long id) throws ConfiguracionNotFoundException {
    log.debug("Petición a get Configuracion : {}  - start", id);
    final Configuracion Configuracion = configuracionRepository.findById(id)
        .orElseThrow(() -> new ConfiguracionNotFoundException(id));
    log.debug("Petición a get Configuracion : {}  - end", id);
    return Configuracion;

  }

  /**
   * Elimina una entidad {@link Configuracion} por id.
   *
   * @param id el id de la entidad {@link Configuracion}.
   */
  @Transactional
  public void delete(Long id) throws ConfiguracionNotFoundException {
    log.debug("Petición a delete Configuracion : {}  - start", id);
    Assert.notNull(id, "El id de Configuracion no puede ser null.");
    if (!configuracionRepository.existsById(id)) {
      throw new ConfiguracionNotFoundException(id);
    }
    configuracionRepository.deleteById(id);
    log.debug("Petición a delete Configuracion : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Configuracion}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Configuracion: {} - start");
    configuracionRepository.deleteAll();
    log.debug("Petición a deleteAll de Configuracion: {} - end");

  }

  /**
   * Actualiza los datos de la entidad {@link Configuracion}.
   * 
   * @param configuracionActualizar {@link Configuracion} con los datos
   *                                actualizados.
   * @return La {@link Configuracion} actualizada.
   * @throws ConfiguracionNotFoundException Si no existe ningún
   *                                        {@link Configuracion} con ese id.
   * @throws IllegalArgumentException       Si la {@link Configuracion} no tiene
   *                                        id.
   */

  @Transactional
  public Configuracion update(final Configuracion configuracionActualizar) {
    log.debug("update(Configuracion configuracionActualizar) - start");

    Assert.notNull(configuracionActualizar.getId(),
        "Configuracion id no puede ser null para actualizar una configuracion");

    return configuracionRepository.findById(configuracionActualizar.getId()).map(configuracion -> {
      configuracion.setClave(configuracionActualizar.getClave());
      configuracion.setDescripcion(configuracionActualizar.getDescripcion());
      configuracion.setValor(configuracionActualizar.getValor());

      Configuracion returnValue = configuracionRepository.save(configuracion);
      log.debug("update(Configuracion configuracionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConfiguracionNotFoundException(configuracionActualizar.getId()));
  }

}
