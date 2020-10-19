package org.crue.hercules.sgdoc.service;

import java.util.List;
import java.util.UUID;

import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgdoc.model.DocumentoEntity;
import org.crue.hercules.sgdoc.repository.DocumentoRepository;
import org.crue.hercules.sgdoc.repository.specification.DocumentoSpecifications;
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
   * Guarda la entidad {@link DocumentoEntity}.
   *
   * @param documento la entidad {@link DocumentoEntity} a guardar.
   * @return la entidad {@link DocumentoEntity} persistida.
   */
  @Transactional
  public DocumentoEntity create(DocumentoEntity documento) {
    log.debug("create(Documento documento) - start");
    Assert.isNull(documento.getDocumentoRef(), "DocumentoRef tiene que ser null para crear una nueva documento");

    documento.setDocumentoRef(UUID.randomUUID().toString());

    DocumentoEntity returnValue = repository.save(documento);

    log.debug("create(Documento documento) - end");

    return returnValue;
  }

  /**
   * Actualiza los datos de la {@link DocumentoEntity}.
   *
   * @param documentoActualizar la entidad {@link DocumentoEntity} con los
   *                                datos actualizados.
   * @return la entidad {@link DocumentoEntity} persistida.
   * @throws NotFoundException        Si no existe ningún
   *                                  {@link DocumentoEntity} con ese
   *                                  documentoRef.
   * @throws IllegalArgumentException Si el {@link DocumentoEntity} no tiene
   *                                  documentoRef.
   */
  @Transactional
  public DocumentoEntity update(DocumentoEntity documentoActualizar) {
    log.debug("update(Documento documentoActualizar) - start");
    Assert.notNull(documentoActualizar.getDocumentoRef(),
        "DocumentoRef no puede ser null para actualizar una documento");

    return repository.findById(documentoActualizar.getDocumentoRef()).map(documento -> {
      documento.setNombre(documentoActualizar.getNombre());
      documento.setVersion(documentoActualizar.getVersion());
      documento.setArchivo(documentoActualizar.getArchivo());
      documento.setTipo(documentoActualizar.getTipo());
      documento.setAutorRef(documentoActualizar.getAutorRef());

      DocumentoEntity returnValue = repository.save(documento);
      log.debug("update(Documento documentoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new NotFoundException(documentoActualizar.getDocumentoRef()));
  }

  public Page<DocumentoEntity> findAll(List<QueryCriteria> query, Pageable paging) {
    Specification<DocumentoEntity> spec = new QuerySpecification<DocumentoEntity>(query);

    Page<DocumentoEntity> returnValue = repository.findAll(spec, paging);
    return returnValue;
  }

  public DocumentoEntity findByRef(String documentoRef) {
    Assert.notNull(documentoRef, "Documento ref can't be null to find a documento");
    DocumentoEntity documento = repository.findById(documentoRef)
        .orElseThrow(() -> new NotFoundException("No documento with provided ref " + documentoRef));
    return documento;
  }

  public Page<DocumentoEntity> findByDocumentoRefs(List<String> documentoRefs, List<QueryCriteria> query,
      Pageable paging) {
    Specification<DocumentoEntity> specByQuery = new QuerySpecification<DocumentoEntity>(query);
    Specification<DocumentoEntity> specByDocumentoRefs = DocumentoSpecifications.byDocumentoRefs(documentoRefs);

    Specification<DocumentoEntity> specs = Specification.where(specByDocumentoRefs).and(specByQuery);

    Page<DocumentoEntity> returnValue = repository.findAll(specs, paging);
    return returnValue;
  }

}