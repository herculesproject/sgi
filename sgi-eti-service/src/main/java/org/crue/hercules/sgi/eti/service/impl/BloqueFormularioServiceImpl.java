package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.BloqueFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.eti.repository.BloqueFormularioRepository;
import org.crue.hercules.sgi.eti.repository.specification.BloqueFormularioSpecifications;
import org.crue.hercules.sgi.eti.service.BloqueFormularioService;
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
 * Service Implementation para la gestión de {@link BloqueFormulario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class BloqueFormularioServiceImpl implements BloqueFormularioService {
  private final BloqueFormularioRepository bloqueFormularioRepository;

  public BloqueFormularioServiceImpl(BloqueFormularioRepository bloqueFormularioRepository) {
    this.bloqueFormularioRepository = bloqueFormularioRepository;
  }

  /**
   * Guarda la entidad {@link BloqueFormulario}.
   *
   * @param bloqueFormulario la entidad {@link BloqueFormulario} a guardar.
   * @return la entidad {@link BloqueFormulario} persistida.
   */
  @Transactional
  public BloqueFormulario create(BloqueFormulario bloqueFormulario) {
    log.debug("Petición a create BloqueFormulario : {} - start", bloqueFormulario);
    Assert.isNull(bloqueFormulario.getId(),
        "BloqueFormulario id tiene que ser null para crear un nuevo bloqueFormulario");

    return bloqueFormularioRepository.save(bloqueFormulario);
  }

  /**
   * Obtiene todas las entidades {@link BloqueFormulario} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link BloqueFormulario} paginadas y
   *         filtradas.
   */
  public Page<BloqueFormulario> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Specification<BloqueFormulario> specByQuery = new QuerySpecification<BloqueFormulario>(query);
    Specification<BloqueFormulario> specActivos = BloqueFormularioSpecifications.activos();

    Specification<BloqueFormulario> specs = Specification.where(specActivos).and(specByQuery);

    Page<BloqueFormulario> returnValue = bloqueFormularioRepository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link BloqueFormulario} por id.
   *
   * @param id el id de la entidad {@link BloqueFormulario}.
   * @return la entidad {@link BloqueFormulario}.
   * @throws BloqueFormularioNotFoundException Si no existe ningún
   *                                           {@link BloqueFormulario} con ese
   *                                           id.
   */
  public BloqueFormulario findById(final Long id) throws BloqueFormularioNotFoundException {
    log.debug("Petición a get BloqueFormulario : {}  - start", id);
    final BloqueFormulario BloqueFormulario = bloqueFormularioRepository.findById(id)
        .orElseThrow(() -> new BloqueFormularioNotFoundException(id));
    log.debug("Petición a get BloqueFormulario : {}  - end", id);
    return BloqueFormulario;

  }

  /**
   * Elimina una entidad {@link BloqueFormulario} por id.
   *
   * @param id el id de la entidad {@link BloqueFormulario}.
   */
  @Transactional
  public void delete(Long id) throws BloqueFormularioNotFoundException {
    log.debug("Petición a delete BloqueFormulario : {}  - start", id);
    Assert.notNull(id, "El id de BloqueFormulario no puede ser null.");
    if (!bloqueFormularioRepository.existsById(id)) {
      throw new BloqueFormularioNotFoundException(id);
    }
    bloqueFormularioRepository.deleteById(id);
    log.debug("Petición a delete BloqueFormulario : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link BloqueFormulario}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de BloqueFormulario: {} - start");
    bloqueFormularioRepository.deleteAll();
    log.debug("Petición a deleteAll de BloqueFormulario: {} - end");

  }

  /**
   * Actualiza los datos del {@link BloqueFormulario}.
   * 
   * @param bloqueFormularioActualizar {@link BloqueFormulario} con los datos
   *                                   actualizados.
   * @return El {@link BloqueFormulario} actualizado.
   * @throws BloqueFormularioNotFoundException Si no existe ningún
   *                                           {@link BloqueFormulario} con ese
   *                                           id.
   * @throws IllegalArgumentException          Si el {@link BloqueFormulario} no
   *                                           tiene id.
   */

  @Transactional
  public BloqueFormulario update(final BloqueFormulario bloqueFormularioActualizar) {
    log.debug("update(BloqueFormulario bloqueFormularioActualizar) - start");

    Assert.notNull(bloqueFormularioActualizar.getId(),
        "BloqueFormulario id no puede ser null para actualizar un bloqueFormulario");

    return bloqueFormularioRepository.findById(bloqueFormularioActualizar.getId()).map(bloqueFormulario -> {
      bloqueFormulario.setNombre(bloqueFormularioActualizar.getNombre());
      bloqueFormulario.setOrden(bloqueFormularioActualizar.getOrden());
      bloqueFormulario.setFormulario(bloqueFormularioActualizar.getFormulario());
      bloqueFormulario.setActivo(bloqueFormularioActualizar.getActivo());

      BloqueFormulario returnValue = bloqueFormularioRepository.save(bloqueFormulario);
      log.debug("update(BloqueFormulario bloqueFormularioActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new BloqueFormularioNotFoundException(bloqueFormularioActualizar.getId()));
  }

}
