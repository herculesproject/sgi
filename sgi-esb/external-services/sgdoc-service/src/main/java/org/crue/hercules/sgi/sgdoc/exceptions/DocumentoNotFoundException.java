package org.crue.hercules.sgi.sgdoc.exceptions;

/**
 * DocumentoNotFoundException
 */
public class DocumentoNotFoundException extends SgdocNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public DocumentoNotFoundException(String documentoId) {
    super("Documento " + documentoId + " does not exist.");
  }

}
