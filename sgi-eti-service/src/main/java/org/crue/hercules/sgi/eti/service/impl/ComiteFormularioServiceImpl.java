package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.ComiteFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ComiteFormulario;
import org.crue.hercules.sgi.eti.repository.ComiteFormularioRepository;
import org.crue.hercules.sgi.eti.service.ComiteFormularioService;
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
 * Service Implementation para la gestión de {@link ComiteFormulario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ComiteFormularioServiceImpl implements ComiteFormularioService {

  private final ComiteFormularioRepository comiteFormularioRepository;

  public ComiteFormularioServiceImpl(ComiteFormularioRepository comiteFormularioRepository) {
    this.comiteFormularioRepository = comiteFormularioRepository;
  }

  /**
   * Guarda la entidad {@link ComiteFormulario}.
   *
   * @param comiteFormulario la entidad {@link ComiteFormulario} a guardar.
   * @return la entidad {@link ComiteFormulario} persistida.
   */
  @Transactional
  public ComiteFormulario create(ComiteFormulario comiteFormulario) {
    log.debug("Petición a create ComiteFormulario : {} - start", comiteFormulario);
    Assert.isNull(comiteFormulario.getId(), "ComiteFormulario id tiene que ser null para crear un nuevo comité");

    return comiteFormularioRepository.save(comiteFormulario);
  }

  /**
   * Obtiene todas las entidades {@link ComiteFormulario} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link ComiteFormulario} paginadas y
   *         filtradas.
   */
  public Page<ComiteFormulario> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAllComiteFormulario(List<QueryCriteria> query,Pageable paging) - start");
    Specification<ComiteFormulario> spec = new QuerySpecification<>(query);

    Page<ComiteFormulario> returnValue = comiteFormularioRepository.findAll(spec, paging);
    log.debug("findAllComiteFormulario(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Optiene una entidad {@link ComiteFormulario} por id.
   *
   * @param id el id de la entidad {@link ComiteFormulario}.
   * @return la entidad {@link ComiteFormulario}.
   * @throws ComiteFormularioNotFoundException excepción.
   */
  public ComiteFormulario findById(final Long id) throws ComiteFormularioNotFoundException {
    log.debug("Petición a get ComiteFormulario : {}  - start", id);
    final ComiteFormulario comiteFormulario = comiteFormularioRepository.findById(id)
        .orElseThrow(() -> new ComiteFormularioNotFoundException(id));
    log.debug("Petición a get ComiteFormulario : {}  - end", id);
    return comiteFormulario;

  }

  /**
   * Elimina una entidad {@link ComiteFormulario} por id.
   *
   * @param id el id de la entidad {@link ComiteFormulario}.
   */
  @Transactional
  public void deleteById(Long id) throws ComiteFormularioNotFoundException {
    log.debug("Petición a delete ComiteFormulario : {}  - start", id);
    Assert.notNull(id, "El id de ComiteFormulario no puede ser null.");
    if (!comiteFormularioRepository.existsById(id)) {
      throw new ComiteFormularioNotFoundException(id);
    }
    comiteFormularioRepository.deleteById(id);
    log.debug("Petición a delete ComiteFormulario : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link ComiteFormulario}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de ComiteFormulario: {} - start");
    comiteFormularioRepository.deleteAll();
    log.debug("Petición a deleteAll de ComiteFormulario: {} - end");

  }

  /**
   * Actualiza los datos del {@link ComiteFormulario}.
   * 
   * @param comiteFormularioActualizar {@link ComiteFormulario} con los datos
   *                                   actualizados.
   * @return El {@link ComiteFormulario} actualizado.
   * @throws ComiteFormularioNotFoundException Si no existe ningún
   *                                           {@link ComiteFormulario} con ese
   *                                           id.
   * @throws IllegalArgumentException          Si el {@link ComiteFormulario} no
   *                                           tiene id.
   */

  @Transactional
  public ComiteFormulario update(final ComiteFormulario comiteFormularioActualizar) {
    log.debug("update(ComiteFormulario ComiteFormularioActualizar) - start");

    Assert.notNull(comiteFormularioActualizar.getId(),
        "ComiteFormulario id no puede ser null para actualizar un ComiteFormulario");

    return comiteFormularioRepository.findById(comiteFormularioActualizar.getId()).map(comiteFormulario -> {
      comiteFormulario.setComite(comiteFormularioActualizar.getComite());
      comiteFormulario.setFormulario(comiteFormularioActualizar.getFormulario());

      ComiteFormulario returnValue = comiteFormularioRepository.save(comiteFormulario);
      log.debug("update(ComiteFormulario ComiteFormularioActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ComiteFormularioNotFoundException(comiteFormularioActualizar.getId()));
  }

}