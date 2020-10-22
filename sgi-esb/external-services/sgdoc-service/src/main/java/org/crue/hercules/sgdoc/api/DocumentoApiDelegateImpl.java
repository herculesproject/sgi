package org.crue.hercules.sgdoc.api;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgdoc.mapper.DocumentoMapper;
import org.crue.hercules.sgdoc.model.DocumentoEntity;
import org.crue.hercules.sgdoc.openapi.api.DocumentosApiDelegate;
import org.crue.hercules.sgdoc.openapi.model.Documento;
import org.crue.hercules.sgdoc.service.DocumentoService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentoApiDelegateImpl implements DocumentosApiDelegate {

  /** Documento service */
  private final DocumentoService documentoService;

  /** Documento mapper */
  private final DocumentoMapper documentoMapper;

  /** Api utils */
  private final ApiUtils apiUtils;

  public DocumentoApiDelegateImpl(DocumentoMapper documentoMapper, DocumentoService documentoService,
      ApiUtils apiUtils) {

    this.documentoMapper = documentoMapper;
    this.documentoService = documentoService;

    this.apiUtils = apiUtils;
  }

  @Override
  public ResponseEntity<Documento> newDocumento(MultipartFile archivo) {
    log.debug("newDocumento({}) - start", archivo);
    DocumentoEntity documentoEntity = new DocumentoEntity();
    documentoEntity.setNombre(archivo.getOriginalFilename());
    documentoEntity.setFechaCreacion(LocalDateTime.now());
    documentoEntity.setAutorRef("anonymous");
    try {
      documentoEntity.setArchivo(archivo.getBytes());
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    documentoEntity.setVersion(1);
    String[] contentType = (archivo.getContentType().split(";"));
    documentoEntity.setTipo(contentType[0]);
    DocumentoEntity documentoEntityCreada = documentoService.create(documentoEntity);
    Documento documentoCreado = documentoMapper.documentoEntityToDocumento(documentoEntityCreada);

    log.debug("newDocumento({}) - end", archivo);

    return new ResponseEntity<>(documentoCreado, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<Documento> findDocumentoByDocumentoRef(String documentoRef) {
    log.debug("findDocumentoByDocumentoRef({}) - start", documentoRef);
    DocumentoEntity documentoEntity = documentoService.findByRef(documentoRef);
    Documento documento = documentoMapper.documentoEntityToDocumento(documentoEntity);
    log.debug("findDocumentoByDocumentoRef({}) - end", documentoRef);
    return new ResponseEntity<>(documento, HttpStatus.OK);

  }

  @Override
  public ResponseEntity<Resource> findDocumentoArchivoByDocumentoRef(String documentoRef) {
    log.debug("findDocumentoArchivoByDocumentoRef({}) - start", documentoRef);

    DocumentoEntity documentoEntity = documentoService.findByRef(documentoRef);
    log.debug("findDocumentoArchivoByDocumentoRef({}) - end", documentoRef);
    ByteArrayResource archivo = new ByteArrayResource(documentoEntity.getArchivo());

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", documentoEntity.getTipo());
    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<List<Documento>> findAllDocumentosByDocumentoRefs(String documentoRefs, Integer xPage,
      Integer xPageSize, String q, String s) {
    log.debug("findAllDocumentosByDocumentoRefs({}, {}, {}, {}) - start", xPage, xPageSize, q, s);

    List<QueryCriteria> query = apiUtils.getQueryCriteria(q);
    Pageable paging = apiUtils.getPageable(xPage, xPageSize, s);

    List<String> documentoRefsList = Arrays.asList(documentoRefs.split("\\|"));

    Page<DocumentoEntity> page = documentoService.findByDocumentoRefs(documentoRefsList, query, paging);

    List<Documento> documentos = page.getContent().stream()
        .map(documentoEntity -> documentoMapper.documentoEntityToDocumento(documentoEntity))
        .collect(Collectors.toList());

    HttpHeaders headers = apiUtils.getPaginationHeaders(page);

    log.debug("findAllDocumentosByDocumentoRefs({}, {}, {}, {}) - end", xPage, xPageSize, q, s);

    if (documentos.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(documentos, headers, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<List<Documento>> findAllDocumentos(Integer xPage, Integer xPageSize, String q, String s) {
    log.debug("findAllDocumentos({}, {}, {}, {}) - start", xPage, xPageSize, q, s);

    List<QueryCriteria> query = apiUtils.getQueryCriteria(q);
    Pageable paging = apiUtils.getPageable(xPage, xPageSize, s);

    Page<DocumentoEntity> page = documentoService.findAll(query, paging);

    List<Documento> documentos = page.getContent().stream()
        .map(documentoEntity -> documentoMapper.documentoEntityToDocumento(documentoEntity))
        .collect(Collectors.toList());

    HttpHeaders headers = apiUtils.getPaginationHeaders(page);

    log.debug("findAllDocumentos({}, {}, {}, {}) - end", xPage, xPageSize, q, s);

    if (documentos.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(documentos, headers, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> deleteDocumentoByDocumentoRef(String documentoRef) {
    log.debug("deleteDocumentoByDocumentoRef({}) - end", documentoRef);
    documentoService.delete(documentoRef);
    log.debug("deleteDocumentoByDocumentoRef({}) - end", documentoRef);
    return null;
  }

}
