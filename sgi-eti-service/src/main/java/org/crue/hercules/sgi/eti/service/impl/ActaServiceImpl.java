package org.crue.hercules.sgi.eti.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.eti.exceptions.ActaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.repository.ActaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoActaRepository;
import org.crue.hercules.sgi.eti.repository.TipoEstadoActaRepository;
import org.crue.hercules.sgi.eti.service.ActaService;
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
 * Service Implementation para la gestión de {@link Acta}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ActaServiceImpl implements ActaService {

  private final ActaRepository actaRepository;
  private final EstadoActaRepository estadoActaRepository;
  TipoEstadoActaRepository tipoEstadoActaRepository;

  public ActaServiceImpl(ActaRepository actaRepository, EstadoActaRepository estadoActaRepository,
      TipoEstadoActaRepository tipoEstadoActaRepository) {
    this.actaRepository = actaRepository;
    this.estadoActaRepository = estadoActaRepository;
    this.tipoEstadoActaRepository = tipoEstadoActaRepository;
  }

  /**
   * Guarda la entidad {@link Acta}.
   * 
   * Se insertará de forma automática un registro para el estado inicial del tipo
   * "En elaboración" para el acta
   *
   * @param acta la entidad {@link Acta} a guardar.
   * @return la entidad {@link Acta} persistida.
   */
  @Transactional
  public Acta create(Acta acta) {
    log.debug("Acta create (Acta acta) - start");

    Assert.isNull(acta.getId(), "Acta id tiene que ser null para crear un nuevo acta");

    Optional<TipoEstadoActa> tipoEstadoActa = tipoEstadoActaRepository.findById(1L);
    Assert.isTrue(tipoEstadoActa.isPresent(), "No se puede establecer el TipoEstadoActa inicial (1: 'En elaboración')");

    acta.setEstadoActual(tipoEstadoActa.get());
    Acta returnValue = actaRepository.save(acta);

    EstadoActa estadoActa = estadoActaRepository
        .save(new EstadoActa(null, returnValue, tipoEstadoActa.get(), LocalDateTime.now()));
    Assert.notNull(estadoActa, "No se ha podido crear el EstadoActa inicial");

    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Acta} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Acta} paginadas y filtradas.
   */
  public Page<Acta> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Specification<Acta> spec = new QuerySpecification<Acta>(query);

    Page<Acta> returnValue = actaRepository.findAll(spec, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   * @return la entidad {@link Acta}.
   * @throws ActaNotFoundException Si no existe ningún {@link Acta} con ese id.
   */
  public Acta findById(final Long id) throws TareaNotFoundException {
    log.debug("Acta findById (Acta acta)  - start", id);
    final Acta acta = actaRepository.findById(id).orElseThrow(() -> new ActaNotFoundException(id));
    log.debug("Acta findById (Acta acta)  - end", id);
    return acta;

  }

  /**
   * Elimina una entidad {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   * @throws ActaNotFoundException Si no existe ningún {@link Acta} con ese id.
   */
  @Transactional
  public void delete(Long id) throws TareaNotFoundException {
    log.debug("Petición a delete Acta : {}  - start", id);
    Assert.notNull(id, "El id de Acta no puede ser null.");
    if (!actaRepository.existsById(id)) {
      throw new ActaNotFoundException(id);
    }
    actaRepository.deleteById(id);
    log.debug("Petición a delete Acta : {}  - end", id);
  }

  /**
   * Actualiza los datos del {@link Acta}.
   * 
   * @param actaActualizar {@link Acta} con los datos actualizados.
   * @return El {@link Acta} actualizado.
   * @throws ActaNotFoundException    Si no existe ningún {@link Acta} con ese id.
   * @throws IllegalArgumentException Si el {@link Acta} no tiene id.
   */
  @Transactional
  public Acta update(final Acta actaActualizar) {
    log.debug("update(Acta actaActualizar) - start");

    Assert.notNull(actaActualizar.getId(), "Acta id no puede ser null para actualizar un acta");

    return actaRepository.findById(actaActualizar.getId()).map(acta -> {
      acta.setConvocatoriaReunion(actaActualizar.getConvocatoriaReunion());
      acta.setHoraInicio(actaActualizar.getHoraInicio());
      acta.setMinutoInicio(actaActualizar.getMinutoInicio());
      acta.setHoraFin(actaActualizar.getHoraFin());
      acta.setMinutoFin(actaActualizar.getMinutoFin());
      acta.setResumen(actaActualizar.getResumen());
      acta.setNumero(actaActualizar.getNumero());
      acta.setEstadoActual(actaActualizar.getEstadoActual());
      acta.setInactiva(actaActualizar.getInactiva());
      acta.setActivo(actaActualizar.getActivo());

      Acta returnValue = actaRepository.save(acta);
      log.debug("update(Acta actaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ActaNotFoundException(actaActualizar.getId()));
  }

}
