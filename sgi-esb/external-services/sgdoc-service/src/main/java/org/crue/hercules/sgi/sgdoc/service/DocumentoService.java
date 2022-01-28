package org.crue.hercules.sgi.sgdoc.service;

import java.util.List;
import java.util.UUID;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.sgdoc.exceptions.ArchivoNotFoundException;
import org.crue.hercules.sgi.sgdoc.exceptions.DocumentoNotFoundException;
import org.crue.hercules.sgi.sgdoc.model.Archivo;
import org.crue.hercules.sgi.sgdoc.model.Documento;
import org.crue.hercules.sgi.sgdoc.repository.ArchivoRepository;
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

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_DOCUMENTO_REF = "documentoRef";

  /** Documento repository */
  private final DocumentoRepository repository;
  /** Archivo repository */
  private final ArchivoRepository archivoRepository;

  public DocumentoService(DocumentoRepository repository, ArchivoRepository archivoRepository) {
    this.repository = repository;
    this.archivoRepository = archivoRepository;
  }

  /**
   * Guarda la entidad {@link Documento}.
   *
   * @param documento la entidad {@link Documento} a guardar.
   * @param archivo   el {@link Archivo} del {@link Documento}.
   * @return la entidad {@link Documento} persistida.
   */
  @Transactional
  public Documento create(Documento documento, Archivo archivo) {
    log.debug("create(Documento documento) - start");

    Assert.isNull(documento.getDocumentoRef(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_DOCUMENTO_REF))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Documento.class))
            .build());

    documento.setDocumentoRef(UUID.randomUUID().toString());

    Documento returnValue = repository.save(documento);

    archivo.setDocumento(returnValue);
    archivoRepository.save(archivo);

    log.debug("create(Documento documento) - end");

    return returnValue;
  }

  /**
   * Elimina el {@link Documento}.
   *
   * @param id identificador del {@link Documento} a guardar.
   */
  @Transactional
  public void delete(String id) {
    log.debug("delete(String id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_DOCUMENTO_REF))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Documento.class))
            .build());

    if (!repository.existsById(id)) {
      throw new DocumentoNotFoundException(id);
    }

    archivoRepository.deleteByDocumentoDocumentoRef(id);
    repository.deleteById(id);
    log.debug("delete(String id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link Documento}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Documento} paginadas y filtradas.
   */
  public Page<Documento> findAll(String query, Pageable paging) {
    Specification<Documento> spec = SgiRSQLJPASupport.toSpecification(query);

    Page<Documento> returnValue = repository.findAll(spec, paging);
    return returnValue;
  }

  /**
   * Devuelve el {@link Documento} con el id indicado.
   * 
   * @param id Identificador de {@link Documento}.
   * @return {@link Documento} correspondiente al id
   */
  public Documento findById(String id) {
    log.debug("findById(String id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_DOCUMENTO_REF))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Documento.class))
            .build());

    Documento returnValue = repository.findById(id).orElseThrow(() -> new DocumentoNotFoundException(id));
    log.debug("findById(String id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link Documento} que tienen alguno
   * de los ids de la lista.
   * 
   * @param ids    identificadores de {@link Documento}.
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Documento} paginadas y filtradas.
   */
  public Page<Documento> findByDocumentoIds(List<String> ids, String query, Pageable paging) {
    Specification<Documento> specByQuery = SgiRSQLJPASupport.toSpecification(query);
    Specification<Documento> specByDocumentoRefs = DocumentoSpecifications.byDocumentoRefs(ids);

    Specification<Documento> specs = Specification.where(specByDocumentoRefs).and(specByQuery);

    Page<Documento> returnValue = repository.findAll(specs, paging);
    return returnValue;
  }

  /**
   * Devuelve el {@link Archivo} del {@link Documento} con el id indicado.
   * 
   * @param documentoId Identificador de {@link Documento}.
   * @return {@link Archivo} correspondiente al id del {@link Documento}.
   */
  public Archivo findArchivoByDocumentoId(String documentoId) {
    log.debug("findArchivoByDocumentoId(String documentoId) - start");
    Archivo returnValue = archivoRepository.findByDocumentoDocumentoRef(documentoId)
        .orElseThrow(() -> new ArchivoNotFoundException(documentoId));
    log.debug("findArchivoByDocumentoId(String documentoId) - end");
    return returnValue;
  }

}