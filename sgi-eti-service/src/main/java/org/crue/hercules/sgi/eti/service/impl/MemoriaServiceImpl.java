package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.service.MemoriaService;
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
 * Service Implementation para la gestión de {@link Memoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class MemoriaServiceImpl implements MemoriaService {
  private final MemoriaRepository memoriaRepository;

  public MemoriaServiceImpl(MemoriaRepository memoriaRepository) {
    this.memoriaRepository = memoriaRepository;
  }

  /**
   * Guarda la entidad {@link Memoria}.
   *
   * @param memoria la entidad {@link Memoria} a guardar.
   * @return la entidad {@link Memoria} persistida.
   */
  @Transactional
  public Memoria create(Memoria memoria) {
    log.debug("Petición a create Memoria : {} - start", memoria);
    Assert.isNull(memoria.getId(), "Memoria id tiene que ser null para crear un nueva memoria");

    return memoriaRepository.save(memoria);
  }

  /**
   * Obtiene todas las entidades {@link Memoria} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Memoria} paginadas y filtradas.
   */
  public Page<Memoria> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Specification<Memoria> spec = new QuerySpecification<Memoria>(query);

    Page<Memoria> returnValue = memoriaRepository.findAll(spec, paging);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   * @return la entidad {@link Memoria}.
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   */
  public Memoria findById(final Long id) throws MemoriaNotFoundException {
    log.debug("Petición a get Memoria : {}  - start", id);
    final Memoria Memoria = memoriaRepository.findById(id).orElseThrow(() -> new MemoriaNotFoundException(id));
    log.debug("Petición a get Memoria : {}  - end", id);
    return Memoria;

  }

  /**
   * Elimina una entidad {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   */
  @Transactional
  public void delete(Long id) throws MemoriaNotFoundException {
    log.debug("Petición a delete Memoria : {}  - start", id);
    Assert.notNull(id, "El id de Memoria no puede ser null.");
    if (!memoriaRepository.existsById(id)) {
      throw new MemoriaNotFoundException(id);
    }
    memoriaRepository.deleteById(id);
    log.debug("Petición a delete Memoria : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Memoria}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Memoria: {} - start");
    memoriaRepository.deleteAll();
    log.debug("Petición a deleteAll de Memoria: {} - end");

  }

  /**
   * Actualiza los datos del {@link Memoria}.
   * 
   * @param memoriaActualizar {@link Memoria} con los datos actualizados.
   * @return El {@link Memoria} actualizado.
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   * @throws IllegalArgumentException Si el {@link Memoria} no tiene id.
   */

  @Transactional
  public Memoria update(final Memoria memoriaActualizar) {
    log.debug("update(Memoria MemoriaActualizar) - start");

    Assert.notNull(memoriaActualizar.getId(), "Memoria id no puede ser null para actualizar un tipo memoria");

    return memoriaRepository.findById(memoriaActualizar.getId()).map(memoria -> {
      memoria.setNumReferencia(memoriaActualizar.getNumReferencia());
      memoria.setPeticionEvaluacion(memoriaActualizar.getPeticionEvaluacion());
      memoria.setComite((memoriaActualizar.getComite()));
      memoria.setTitulo(memoriaActualizar.getTitulo());
      memoria.setUsuarioRef(memoriaActualizar.getUsuarioRef());
      memoria.setFechaEstado(memoriaActualizar.getFechaEstado());
      memoria.setTipoMemoria(memoriaActualizar.getTipoMemoria());
      memoria.setFechaEnvioSecretaria(memoriaActualizar.getFechaEnvioSecretaria());
      memoria.setRequiereRetrospectiva(memoriaActualizar.getRequiereRetrospectiva());
      memoria.setFechaRetrospectiva(memoriaActualizar.getFechaRetrospectiva());
      memoria.setVersion(memoriaActualizar.getVersion());

      Memoria returnValue = memoriaRepository.save(memoria);
      log.debug("update(Memoria memoriaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(memoriaActualizar.getId()));
  }

}
