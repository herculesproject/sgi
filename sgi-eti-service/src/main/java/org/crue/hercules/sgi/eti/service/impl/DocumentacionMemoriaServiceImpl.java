package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.DocumentacionMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.service.DocumentacionMemoriaService;
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
 * Service Implementation para la gestión de {@link DocumentacionMemoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class DocumentacionMemoriaServiceImpl implements DocumentacionMemoriaService {
  private final DocumentacionMemoriaRepository documentacionMemoriaRepository;

  public DocumentacionMemoriaServiceImpl(DocumentacionMemoriaRepository DocumentacionMemoriaRepository) {
    this.documentacionMemoriaRepository = DocumentacionMemoriaRepository;
  }

  /**
   * Guarda la entidad {@link DocumentacionMemoria}.
   *
   * @param DocumentacionMemoria la entidad {@link DocumentacionMemoria} a
   *                             guardar.
   * @return la entidad {@link DocumentacionMemoria} persistida.
   */
  @Transactional
  public DocumentacionMemoria create(DocumentacionMemoria DocumentacionMemoria) {
    log.debug("Petición a create DocumentacionMemoria : {} - start", DocumentacionMemoria);
    Assert.isNull(DocumentacionMemoria.getId(),
        "DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");

    return documentacionMemoriaRepository.save(DocumentacionMemoria);
  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} paginadas y
   * filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link DocumentacionMemoria} paginadas y
   *         filtradas.
   */
  public Page<DocumentacionMemoria> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAllDocumentacionMemoria(List<QueryCriteria> query,Pageable paging) - start");
    Specification<DocumentacionMemoria> spec = new QuerySpecification<DocumentacionMemoria>(query);

    Page<DocumentacionMemoria> returnValue = documentacionMemoriaRepository.findAll(spec, paging);
    log.debug("findAllDocumentacionMemoria(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link DocumentacionMemoria} por id.
   *
   * @param id el id de la entidad {@link DocumentacionMemoria}.
   * @return la entidad {@link DocumentacionMemoria}.
   * @throws DocumentacionMemoriaNotFoundException Si no existe ningún
   *                                               {@link DocumentacionMemoria}
   *                                               con ese id.
   */
  public DocumentacionMemoria findById(final Long id) throws DocumentacionMemoriaNotFoundException {
    log.debug("Petición a get DocumentacionMemoria : {}  - start", id);
    final DocumentacionMemoria DocumentacionMemoria = documentacionMemoriaRepository.findById(id)
        .orElseThrow(() -> new DocumentacionMemoriaNotFoundException(id));
    log.debug("Petición a get DocumentacionMemoria : {}  - end", id);
    return DocumentacionMemoria;

  }

  /**
   * Elimina una entidad {@link DocumentacionMemoria} por id.
   *
   * @param id el id de la entidad {@link DocumentacionMemoria}.
   */
  @Transactional
  public void delete(Long id) throws DocumentacionMemoriaNotFoundException {
    log.debug("Petición a delete DocumentacionMemoria : {}  - start", id);
    Assert.notNull(id, "El id de DocumentacionMemoria no puede ser null.");
    if (!documentacionMemoriaRepository.existsById(id)) {
      throw new DocumentacionMemoriaNotFoundException(id);
    }
    documentacionMemoriaRepository.deleteById(id);
    log.debug("Petición a delete DocumentacionMemoria : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link DocumentacionMemoria}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de DocumentacionMemoria: {} - start");
    documentacionMemoriaRepository.deleteAll();
    log.debug("Petición a deleteAll de DocumentacionMemoria: {} - end");

  }

  /**
   * Actualiza los datos del {@link DocumentacionMemoria}.
   * 
   * @param documentacionMemoriaActualizar {@link DocumentacionMemoria} con los
   *                                       datos actualizados.
   * @return El {@link DocumentacionMemoria} actualizado.
   * @throws DocumentacionMemoriaNotFoundException Si no existe ningún
   *                                               {@link DocumentacionMemoria}
   *                                               con ese id.
   * @throws IllegalArgumentException              Si el
   *                                               {@link DocumentacionMemoria} no
   *                                               tiene id.
   */

  @Transactional
  public DocumentacionMemoria update(final DocumentacionMemoria documentacionMemoriaActualizar) {
    log.debug("update(DocumentacionMemoria DocumentacionMemoriaActualizar) - start");

    Assert.notNull(documentacionMemoriaActualizar.getId(),
        "DocumentacionMemoria id no puede ser null para actualizar una documentacion memoria");

    return documentacionMemoriaRepository.findById(documentacionMemoriaActualizar.getId()).map(documentacionMemoria -> {
      documentacionMemoria.setDocumentoRef(documentacionMemoriaActualizar.getDocumentoRef());
      documentacionMemoria.setMemoria(documentacionMemoriaActualizar.getMemoria());
      documentacionMemoria.setTipoDocumento(documentacionMemoriaActualizar.getTipoDocumento());
      documentacionMemoria.setAportado(documentacionMemoriaActualizar.getAportado());

      DocumentacionMemoria returnValue = documentacionMemoriaRepository.save(documentacionMemoria);
      log.debug("update(DocumentacionMemoria DocumentacionMemoriaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new DocumentacionMemoriaNotFoundException(documentacionMemoriaActualizar.getId()));
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} para una determinada
   * {@link Memoria}.
   *
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findByMemoriaId(Long id, Pageable pageable) {
    log.debug("findByMemoriaId(Long id, Pageable pageable) - start");
    Assert.isTrue(id != null, "El id de la memoria no puede ser nulo para mostrar su documentacion");
    Page<DocumentacionMemoria> returnValue = documentacionMemoriaRepository.findByMemoriaId(id, pageable);
    log.debug("findByMemoriaId(Long id, Pageable pageable) - end");
    return returnValue;
  }
}
