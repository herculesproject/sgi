package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.InformeFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.InformeFormulario;
import org.crue.hercules.sgi.eti.repository.InformeFormularioRepository;
import org.crue.hercules.sgi.eti.service.InformeFormularioService;
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
 * Service Implementation para la gestión de {@link InformeFormulario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class InformeFormularioServiceImpl implements InformeFormularioService {
  private final InformeFormularioRepository informeFormularioRepository;

  public InformeFormularioServiceImpl(InformeFormularioRepository informeFormularioRepository) {
    this.informeFormularioRepository = informeFormularioRepository;
  }

  /**
   * Guarda la entidad {@link InformeFormulario}.
   *
   * @param informeFormulario la entidad {@link InformeFormulario} a guardar.
   * @return la entidad {@link InformeFormulario} persistida.
   */
  @Transactional
  public InformeFormulario create(InformeFormulario informeFormulario) {
    log.debug("Petición a create InformeFormulario : {} - start", informeFormulario);
    Assert.isNull(informeFormulario.getId(),
        "InformeFormulario id tiene que ser null para crear un nuevo informeFormulario");

    return informeFormularioRepository.save(informeFormulario);
  }

  /**
   * Obtiene todas las entidades {@link InformeFormulario} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link InformeFormulario} paginadas y
   *         filtradas.
   */
  @Override
  public Page<InformeFormulario> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Specification<InformeFormulario> spec = new QuerySpecification<InformeFormulario>(query);

    Page<InformeFormulario> returnValue = informeFormularioRepository.findAll(spec, paging);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link InformeFormulario} por id.
   *
   * @param id el id de la entidad {@link InformeFormulario}.
   * @return la entidad {@link InformeFormulario}.
   * @throws InformeFormularioNotFoundException Si no existe ningún
   *                                            {@link InformeFormulario} con ese
   *                                            id.
   */
  public InformeFormulario findById(final Long id) throws InformeFormularioNotFoundException {
    log.debug("Petición a get InformeFormulario : {}  - start", id);
    final InformeFormulario InformeFormulario = informeFormularioRepository.findById(id)
        .orElseThrow(() -> new InformeFormularioNotFoundException(id));
    log.debug("Petición a get InformeFormulario : {}  - end", id);
    return InformeFormulario;

  }

  /**
   * Elimina una entidad {@link InformeFormulario} por id.
   *
   * @param id el id de la entidad {@link InformeFormulario}.
   */
  @Transactional
  public void delete(Long id) throws InformeFormularioNotFoundException {
    log.debug("Petición a delete InformeFormulario : {}  - start", id);
    Assert.notNull(id, "El id de InformeFormulario no puede ser null.");
    if (!informeFormularioRepository.existsById(id)) {
      throw new InformeFormularioNotFoundException(id);
    }
    informeFormularioRepository.deleteById(id);
    log.debug("Petición a delete InformeFormulario : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link InformeFormulario}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de InformeFormulario: {} - start");
    informeFormularioRepository.deleteAll();
    log.debug("Petición a deleteAll de InformeFormulario: {} - end");

  }

  /**
   * Actualiza los datos del {@link InformeFormulario}.
   * 
   * @param informeFormularioActualizar {@link InformeFormulario} con los datos
   *                                    actualizados.
   * @return El {@link InformeFormulario} actualizado.
   * @throws InformeFormularioNotFoundException Si no existe ningún
   *                                            {@link InformeFormulario} con ese
   *                                            id.
   * @throws IllegalArgumentException           Si el {@link InformeFormulario} no
   *                                            tiene id.
   */

  @Transactional
  public InformeFormulario update(final InformeFormulario informeFormularioActualizar) {
    log.debug("update(InformeFormulario informeFormularioActualizar) - start");

    Assert.notNull(informeFormularioActualizar.getId(),
        "InformeFormulario id no puede ser null para actualizar un informeFormulario");

    return informeFormularioRepository.findById(informeFormularioActualizar.getId()).map(informeFormulario -> {
      informeFormulario.setDocumentoRef(informeFormularioActualizar.getDocumentoRef());
      informeFormulario.setFormularioMemoria(informeFormularioActualizar.getFormularioMemoria());
      informeFormulario.setVersion(informeFormularioActualizar.getVersion());

      InformeFormulario returnValue = informeFormularioRepository.save(informeFormulario);
      log.debug("update(InformeFormulario informeFormularioActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new InformeFormularioNotFoundException(informeFormularioActualizar.getId()));
  }

  /**
   * Devuelve un listado paginado de {@link InformeFormulario} filtrado por la
   * {@link Memoria}
   * 
   * @param id       identificador de la {@link Memoria}
   * @param pageable paginación
   * @return el listado paginado de {@link InformeFormulario}
   */
  @Override
  public void deleteInformeMemoria(Long idMemoria) {
    InformeFormulario informe = informeFormularioRepository
        .findFirstByFormularioMemoriaMemoriaIdOrderByVersionDesc(idMemoria);

    if (informe != null) {
      informeFormularioRepository.delete(informe);
    }

  }

  /**
   * Devuelve un listado paginado de {@link InformeFormulario} filtrado por la
   * {@link Memoria}
   * 
   * @param id       identificador de la {@link Memoria}
   * @param pageable paginación
   * @return el listado paginado de {@link InformeFormulario}
   */
  @Override
  public Page<InformeFormulario> findByMemoria(Long id, Pageable pageable) {
    Assert.notNull(id, "Memoria id no puede ser null para actualizar un informeFormulario");
    Page<InformeFormulario> returnValue = informeFormularioRepository.findByMemoria(id, pageable);
    return returnValue;
  }

}
