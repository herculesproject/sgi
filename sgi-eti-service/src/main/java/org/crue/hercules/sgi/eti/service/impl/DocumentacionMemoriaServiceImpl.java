package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.DocumentacionMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
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
  private final DocumentacionMemoriaRepository DocumentacionMemoriaRepository;

  public DocumentacionMemoriaServiceImpl(DocumentacionMemoriaRepository DocumentacionMemoriaRepository) {
    this.DocumentacionMemoriaRepository = DocumentacionMemoriaRepository;
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

    return DocumentacionMemoriaRepository.save(DocumentacionMemoria);
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

    Page<DocumentacionMemoria> returnValue = DocumentacionMemoriaRepository.findAll(spec, paging);
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
    final DocumentacionMemoria DocumentacionMemoria = DocumentacionMemoriaRepository.findById(id)
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
    if (!DocumentacionMemoriaRepository.existsById(id)) {
      throw new DocumentacionMemoriaNotFoundException(id);
    }
    DocumentacionMemoriaRepository.deleteById(id);
    log.debug("Petición a delete DocumentacionMemoria : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link DocumentacionMemoria}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de DocumentacionMemoria: {} - start");
    DocumentacionMemoriaRepository.deleteAll();
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

    return DocumentacionMemoriaRepository.findById(documentacionMemoriaActualizar.getId()).map(documentacionMemoria -> {
      documentacionMemoria.setDocumentoRef(documentacionMemoriaActualizar.getDocumentoRef());
      documentacionMemoria.setMemoria(documentacionMemoriaActualizar.getMemoria());
      documentacionMemoria.setTipoDocumento(documentacionMemoriaActualizar.getTipoDocumento());

      DocumentacionMemoria returnValue = DocumentacionMemoriaRepository.save(documentacionMemoria);
      log.debug("update(DocumentacionMemoria DocumentacionMemoriaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new DocumentacionMemoriaNotFoundException(documentacionMemoriaActualizar.getId()));
  }

}
