package org.crue.hercules.sgdoc.mapper.impl;

import java.time.LocalDateTime;

import org.crue.hercules.sgdoc.mapper.DocumentoMapper;
import org.crue.hercules.sgdoc.model.DocumentoEntity;
import org.crue.hercules.sgdoc.openapi.model.Documento;
import org.crue.hercules.sgdoc.openapi.model.DocumentoListado;


/**
 * Mapper entre la entidad {@link DocumentoEntity} y el objeto del api
 * {@link Documento}.
 */
public class DocumentoMapperImpl implements DocumentoMapper {
     private final DocumentoMapper documentoMapper;
     
     public DocumentoMapperImpl(DocumentoMapper documentoMapper) {
         this.documentoMapper = documentoMapper;
     }

  /**
   * Transforma un {@link Documento} en un {@link DocumentoEntity}.
   * 
   * @param Documento un {@link Documento}.
   * @return el DocumentoEntity correspondiente.
   */
  @Override
  public DocumentoEntity documentoToDocumentoEntity(Documento documento){
    DocumentoEntity documentoEntity = new DocumentoEntity();
    documentoEntity.setDocumentoRef(documento.getDocumentoRef());
    documentoEntity.setNombre(documento.getNombre());
    documentoEntity.setVersion(documento.getVersion());
    LocalDateTime fechaCreacion = LocalDateTime.parse(documento.getFechaCreacion());
    documentoEntity.setFechaCreacion(fechaCreacion);
    documentoEntity.setTipo(documento.getTipo());
    documentoEntity.setAutorRef(documento.getAutorRef());
    documentoEntity.setArchivo(documento.getArchivo());

    return documentoEntity;
  }

    /**
   * Transforma un {@link DocumentoEntity} en un {@link DocumentoListado}.
   * 
   * @param documentoEntity un {@link DocumentoEntity}.
   * @return el DocumentoListado correspondiente.
   */
  @Override
  public DocumentoListado documentoEntityToDocumentoListado(DocumentoEntity documentoEntity){
    return this.documentoMapper.documentoEntityToDocumentoListado(documentoEntity);

  }

    /**
   * Transforma un {@link DocumentoEntity} en un {@link Documento}.
   * 
   * @param documentoEntity un {@link DocumentoEntity}.
   * @return el Documento correspondiente.
   */
    @Override
  public Documento documentoEntityToDocumento(DocumentoEntity documentoEntity){
    Documento documento = new Documento();
    documento.setDocumentoRef(documentoEntity.getDocumentoRef());
    documento.setNombre(documentoEntity.getNombre());
    documento.setVersion(documentoEntity.getVersion());
    documento.setFechaCreacion(documentoEntity.getFechaCreacion().toString());
    documento.setTipo(documentoEntity.getTipo());
    documento.setAutorRef(documentoEntity.getAutorRef());
    documento.setArchivo(documentoEntity.getArchivo());

    return documento;


  }
}
