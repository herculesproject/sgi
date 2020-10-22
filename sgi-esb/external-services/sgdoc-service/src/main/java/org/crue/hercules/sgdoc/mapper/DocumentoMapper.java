package org.crue.hercules.sgdoc.mapper;

import org.crue.hercules.sgdoc.model.DocumentoEntity;
import org.crue.hercules.sgdoc.openapi.model.Documento;
import org.mapstruct.Mapper;

/**
 * Mapper entre la entidad {@link DocumentoEntity} y el objeto del api
 * {@link Documento}.
 */
@Mapper(componentModel = "spring")
public interface DocumentoMapper {

  /**
   * Transforma un {@link DocumentoEntity} en un {@link Documento}.
   * 
   * @param documentoEntity un {@link DocumentoEntity}.
   * @return el Documento correspondiente.
   */
  Documento documentoEntityToDocumento(DocumentoEntity documentoEntity);

  /**
   * Transforma un {@link Documento} en un {@link DocumentoEntity}.
   * 
   * @param Documento un {@link Documento}.
   * @return el DocumentoEntity correspondiente.
   */
  DocumentoEntity documentoToDocumentoEntity(Documento documento);

}