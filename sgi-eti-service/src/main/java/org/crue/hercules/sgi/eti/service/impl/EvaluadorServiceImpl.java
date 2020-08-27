package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.EvaluadorNotFoundException;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.repository.EvaluadorRepository;
import org.crue.hercules.sgi.eti.service.EvaluadorService;
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
 * Service Implementation para la gestión de {@link Evaluador}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EvaluadorServiceImpl implements EvaluadorService {
  private final EvaluadorRepository evaluadorRepository;

  public EvaluadorServiceImpl(EvaluadorRepository evaluadorRepository) {
    this.evaluadorRepository = evaluadorRepository;
  }

  /**
   * Guarda la entidad {@link Evaluador}.
   *
   * @param evaluador la entidad {@link Evaluador} a guardar.
   * @return la entidad {@link Evaluador} persistida.
   */
  @Transactional
  public Evaluador create(Evaluador evaluador) {
    log.debug("Petición a create Evaluador : {} - start", evaluador);
    Assert.isNull(evaluador.getId(), "Evaluador id tiene que ser null para crear un nuevo evaluador");

    return evaluadorRepository.save(evaluador);
  }

  /**
   * Obtiene todas las entidades {@link Evaluador} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Evaluador} paginadas y filtradas.
   */
  public Page<Evaluador> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Specification<Evaluador> spec = new QuerySpecification<Evaluador>(query);

    Page<Evaluador> returnValue = evaluadorRepository.findAll(spec, paging);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Evaluador} por id.
   *
   * @param id el id de la entidad {@link Evaluador}.
   * @return la entidad {@link Evaluador}.
   * @throws EvaluadorNotFoundException Si no existe ningún {@link Evaluador} con
   *                                    ese id.
   */
  public Evaluador findById(final Long id) throws EvaluadorNotFoundException {
    log.debug("Petición a get Evaluador : {}  - start", id);
    final Evaluador Evaluador = evaluadorRepository.findById(id).orElseThrow(() -> new EvaluadorNotFoundException(id));
    log.debug("Petición a get Evaluador : {}  - end", id);
    return Evaluador;

  }

  /**
   * Elimina una entidad {@link Evaluador} por id.
   *
   * @param id el id de la entidad {@link Evaluador}.
   */
  @Transactional
  public void delete(Long id) throws EvaluadorNotFoundException {
    log.debug("Petición a delete Evaluador : {}  - start", id);
    Assert.notNull(id, "El id de Evaluador no puede ser null.");
    if (!evaluadorRepository.existsById(id)) {
      throw new EvaluadorNotFoundException(id);
    }
    evaluadorRepository.deleteById(id);
    log.debug("Petición a delete Evaluador : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Evaluador}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Evaluador: {} - start");
    evaluadorRepository.deleteAll();
    log.debug("Petición a deleteAll de Evaluador: {} - end");

  }

  /**
   * Actualiza los datos del {@link Evaluador}.
   * 
   * @param evaluadorActualizar {@link Evaluador} con los datos actualizados.
   * @return El {@link Evaluador} actualizado.
   * @throws EvaluadorNotFoundException Si no existe ningún {@link Evaluador} con
   *                                    ese id.
   * @throws IllegalArgumentException   Si el {@link Evaluador} no tiene id.
   */

  @Transactional
  public Evaluador update(final Evaluador evaluadorActualizar) {
    log.debug("update(Evaluador evaluadorActualizar) - start");

    Assert.notNull(evaluadorActualizar.getId(), "Evaluador id no puede ser null para actualizar un evaluador");

    return evaluadorRepository.findById(evaluadorActualizar.getId()).map(evaluador -> {
      evaluador.setCargoComite(evaluadorActualizar.getCargoComite());
      evaluador.setComite(evaluadorActualizar.getComite());
      evaluador.setFechaAlta(evaluadorActualizar.getFechaAlta());
      evaluador.setFechaBaja(evaluadorActualizar.getFechaBaja());
      evaluador.setResumen(evaluadorActualizar.getResumen());
      evaluador.setPersonaRef(evaluadorActualizar.getPersonaRef());
      evaluador.setActivo(evaluadorActualizar.getActivo());

      Evaluador returnValue = evaluadorRepository.save(evaluador);
      log.debug("update(Evaluador evaluadorActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EvaluadorNotFoundException(evaluadorActualizar.getId()));
  }

}
