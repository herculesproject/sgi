package org.crue.hercules.sgi.sgdoc.service;

import java.util.List;
import java.util.UUID;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.sgdoc.exceptions.DocumentoNotFoundException;
import org.crue.hercules.sgi.sgdoc.model.Documento;
import org.crue.hercules.sgi.sgdoc.repository.DocumentoRepository;
import org.crue.hercules.sgi.sgdoc.repository.specification.DocumentoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class DocumentoService {

  /** Documento repository */
  private final DocumentoRepository repository;

  public DocumentoService(DocumentoRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link Documento}.
   *
   * @param documento la entidad {@link Documento} a guardar.
   * @return la entidad {@link Documento} persistida.
   */
  @Transactional
  public Documento create(Documento documento) {
    log.debug("create(Documento documento) - start");
    Assert.isNull(documento.getDocumentoRef(), "DocumentoRef tiene que ser null para crear una nueva documento");

    documento.setDocumentoRef(UUID.randomUUID().toString());

    Documento returnValue = repository.save(documento);

    log.debug("create(Documento documento) - end");

    return returnValue;
  }

  /**
   * Actualiza los datos de la {@link Documento}.
   *
   * @param documentoActualizar la entidad {@link Documento} con los datos
   *                            actualizados.
   * @return la entidad {@link Documento} persistida.
   * @throws NotFoundException        Si no existe ningún {@link Documento} con
   *                                  ese documentoRef.
   * @throws IllegalArgumentException Si el {@link Documento} no tiene
   *                                  documentoRef.
   */
  @Transactional
  public Documento update(Documento documentoActualizar) {
    log.debug("update(Documento documentoActualizar) - start");
    Assert.notNull(documentoActualizar.getDocumentoRef(),
        "DocumentoRef no puede ser null para actualizar una documento");

    return repository.findById(documentoActualizar.getDocumentoRef()).map(documento -> {
      documento.setNombre(documentoActualizar.getNombre());
      documento.setVersion(documentoActualizar.getVersion());
      documento.setArchivo(documentoActualizar.getArchivo());
      documento.setTipo(documentoActualizar.getTipo());
      documento.setAutorRef(documentoActualizar.getAutorRef());

      Documento returnValue = repository.save(documento);
      log.debug("update(Documento documentoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new NotFoundException(documentoActualizar.getDocumentoRef()));
  }

  /**
   * Elimina el {@link Documento}.
   *
   * @param id identificador del {@link Documento} a guardar.
   */
  @Transactional
  public void delete(String id) {
    log.debug("delete(String id) - start");

    Assert.notNull(id, "id no puede ser null para eliminar un Documento");

    if (!repository.existsById(id)) {
      throw new NotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(String id) - end");
  }

  public Page<Documento> findAll(String query, Pageable paging) {
    Specification<Documento> spec = SgiRSQLJPASupport.toSpecification(query);

    Page<Documento> returnValue = repository.findAll(spec, paging);
    return returnValue;
  }

  public Documento findById(String id) {
    log.debug("findById(String id) - start");
    Documento returnValue = repository.findById(id).orElseThrow(() -> new DocumentoNotFoundException(id));
    log.debug("findById(String id) - end");
    return returnValue;
  }

  public Page<Documento> findByDocumentoIds(List<String> ids, String query, Pageable paging) {
    Specification<Documento> specByQuery = SgiRSQLJPASupport.toSpecification(query);
    Specification<Documento> specByDocumentoRefs = DocumentoSpecifications.byDocumentoRefs(ids);

    Specification<Documento> specs = Specification.where(specByDocumentoRefs).and(specByQuery);

    Page<Documento> returnValue = repository.findAll(specs, paging);
    return returnValue;
  }

}