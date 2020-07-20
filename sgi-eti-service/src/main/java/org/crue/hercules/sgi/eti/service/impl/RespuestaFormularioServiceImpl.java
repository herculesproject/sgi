package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.RespuestaFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.RespuestaFormulario;
import org.crue.hercules.sgi.eti.repository.RespuestaFormularioRepository;
import org.crue.hercules.sgi.eti.service.RespuestaFormularioService;
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
 * Service Implementation para la gestión de {@link RespuestaFormulario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RespuestaFormularioServiceImpl implements RespuestaFormularioService {
  private final RespuestaFormularioRepository respuestaFormularioRepository;

  public RespuestaFormularioServiceImpl(RespuestaFormularioRepository respuestaFormularioRepository) {
    this.respuestaFormularioRepository = respuestaFormularioRepository;
  }

  /**
   * Guarda la entidad {@link RespuestaFormulario}.
   *
   * @param respuestaFormulario la entidad {@link RespuestaFormulario} a guardar.
   * @return la entidad {@link RespuestaFormulario} persistida.
   */
  @Transactional
  public RespuestaFormulario create(RespuestaFormulario respuestaFormulario) {
    log.debug("Petición a create RespuestaFormulario : {} - start", respuestaFormulario);
    Assert.isNull(respuestaFormulario.getId(),
        "RespuestaFormulario id tiene que ser null para crear un nuevo RespuestaFormulario");

    return respuestaFormularioRepository.save(respuestaFormulario);
  }

  /**
   * Obtiene todas las entidades {@link RespuestaFormulario} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link RespuestaFormulario} paginadas y
   *         filtradas.
   */
  public Page<RespuestaFormulario> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Specification<RespuestaFormulario> spec = new QuerySpecification<RespuestaFormulario>(query);

    Page<RespuestaFormulario> returnValue = respuestaFormularioRepository.findAll(spec, paging);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link RespuestaFormulario} por id.
   *
   * @param id el id de la entidad {@link RespuestaFormulario}.
   * @return la entidad {@link RespuestaFormulario}.
   * @throws RespuestaFormularioNotFoundException Si no existe ningún
   *                                              {@link RespuestaFormulario} con
   *                                              ese id.
   */
  public RespuestaFormulario findById(final Long id) throws RespuestaFormularioNotFoundException {
    log.debug("Petición a get RespuestaFormulario : {}  - start", id);
    final RespuestaFormulario respuestaFormulario = respuestaFormularioRepository.findById(id)
        .orElseThrow(() -> new RespuestaFormularioNotFoundException(id));
    log.debug("Petición a get RespuestaFormulario : {}  - end", id);
    return respuestaFormulario;

  }

  /**
   * Elimina una entidad {@link RespuestaFormulario} por id.
   *
   * @param id el id de la entidad {@link RespuestaFormulario}.
   */
  @Transactional
  public void delete(Long id) throws RespuestaFormularioNotFoundException {
    log.debug("Petición a delete RespuestaFormulario : {}  - start", id);
    Assert.notNull(id, "El id de RespuestaFormulario no puede ser null.");
    if (!respuestaFormularioRepository.existsById(id)) {
      throw new RespuestaFormularioNotFoundException(id);
    }
    respuestaFormularioRepository.deleteById(id);
    log.debug("Petición a delete RespuestaFormulario : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link RespuestaFormulario}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de RespuestaFormulario: {} - start");
    respuestaFormularioRepository.deleteAll();
    log.debug("Petición a deleteAll de RespuestaFormulario: {} - end");

  }

  /**
   * Actualiza los datos del {@link RespuestaFormulario}.
   * 
   * @param respuestaFormularioActualizar {@link RespuestaFormulario} con los
   *                                      datos actualizados.
   * @return El {@link RespuestaFormulario} actualizado.
   * @throws RespuestaFormularioNotFoundException Si no existe ningún
   *                                              {@link RespuestaFormulario} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si el
   *                                              {@link RespuestaFormulario} no
   *                                              tiene id.
   */

  @Transactional
  public RespuestaFormulario update(final RespuestaFormulario respuestaFormularioActualizar) {
    log.debug("update(RespuestaFormulario RespuestaFormularioActualizar) - start");

    Assert.notNull(respuestaFormularioActualizar.getId(),
        "RespuestaFormulario id no puede ser null para actualizar un RespuestaFormulario");

    return respuestaFormularioRepository.findById(respuestaFormularioActualizar.getId()).map(respuestaFormulario -> {
      respuestaFormulario.setFormularioMemoria(respuestaFormularioActualizar.getFormularioMemoria());
      respuestaFormulario.setComponenteFormulario(respuestaFormularioActualizar.getComponenteFormulario());
      respuestaFormulario.setValor(respuestaFormularioActualizar.getValor());

      RespuestaFormulario returnValue = respuestaFormularioRepository.save(respuestaFormulario);
      log.debug("update(RespuestaFormulario RespuestaFormularioActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new RespuestaFormularioNotFoundException(respuestaFormularioActualizar.getId()));
  }

}
