package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.ComentarioNotFoundException;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.service.ComentarioService;
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
 * Service Implementation para la gestión de {@link Comentario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ComentarioServiceImpl implements ComentarioService {
  private final ComentarioRepository comentarioRepository;

  public ComentarioServiceImpl(ComentarioRepository comentarioRepository) {
    this.comentarioRepository = comentarioRepository;
  }

  /**
   * Guarda la entidad {@link Comentario}.
   *
   * @param comentario la entidad {@link Comentario} a guardar.
   * @return la entidad {@link Comentario} persistida.
   */
  @Transactional
  public Comentario create(Comentario comentario) {
    log.debug("Petición a create Comentario : {} - start", comentario);
    Assert.isNull(comentario.getId(), "Comentario id tiene que ser null para crear un nuevo comentario");

    return comentarioRepository.save(comentario);
  }

  /**
   * Obtiene todas las entidades {@link Comentario} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Comentario} paginadas y filtradas.
   */
  public Page<Comentario> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAllComentario(List<QueryCriteria> query, Pageable paging) - start");
    Specification<Comentario> spec = new QuerySpecification<Comentario>(query);

    Page<Comentario> returnValue = comentarioRepository.findAll(spec, paging);
    log.debug("findAllComentario(List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Comentario} por id.
   *
   * @param id el id de la entidad {@link Comentario}.
   * @return la entidad {@link Comentario}.
   * @throws ComentarioNotFoundException Si no existe ningún {@link Comentario}
   *                                     con ese id.
   */
  public Comentario findById(final Long id) throws ComentarioNotFoundException {
    log.debug("Petición a get Comentario : {}  - start", id);
    final Comentario comentario = comentarioRepository.findById(id)
        .orElseThrow(() -> new ComentarioNotFoundException(id));
    log.debug("Petición a get Comentario : {}  - end", id);
    return comentario;

  }

  /**
   * Elimina una entidad {@link Comentario} por id.
   *
   * @param id el id de la entidad {@link Comentario}.
   */
  @Transactional
  public void delete(Long id) throws ComentarioNotFoundException {
    log.debug("Petición a delete Comentario : {}  - start", id);
    Assert.notNull(id, "El id de Comentario no puede ser null.");
    if (!comentarioRepository.existsById(id)) {
      throw new ComentarioNotFoundException(id);
    }
    comentarioRepository.deleteById(id);
    log.debug("Petición a delete Comentario : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Comentario}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Comentario: {} - start");
    comentarioRepository.deleteAll();
    log.debug("Petición a deleteAll de Comentario: {} - end");

  }

  /**
   * Actualiza los datos del {@link Comentario}.
   * 
   * @param comentarioActualizar {@link Comentario} con los datos actualizados.
   * @return El {@link Comentario} actualizado.
   * @throws ComentarioNotFoundException Si no existe ningún {@link Comentario}
   *                                     con ese id.
   * @throws IllegalArgumentException    Si el {@link Comentario} no tiene id.
   */
  @Transactional
  public Comentario update(final Comentario comentarioActualizar) {
    log.debug("update(Comentario comentarioActualizar) - start");

    Assert.notNull(comentarioActualizar.getId(), "Comentario id no puede ser null para actualizar un comentario");

    return comentarioRepository.findById(comentarioActualizar.getId()).map(comentario -> {
      comentario.setApartadoFormulario(comentarioActualizar.getApartadoFormulario());
      comentario.setEvaluacion(comentarioActualizar.getEvaluacion());
      comentario.setTipoComentario(comentarioActualizar.getTipoComentario());
      comentario.setTexto(comentarioActualizar.getTexto());

      Comentario returnValue = comentarioRepository.save(comentario);
      log.debug("update(Comentario comentarioActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ComentarioNotFoundException(comentarioActualizar.getId()));
  }

}
