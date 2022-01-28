package org.crue.hercules.sgi.sgdoc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.sgdoc.model.Archivo;
import org.crue.hercules.sgi.sgdoc.model.Documento;

/**
 * ArchivoNotFoundException
 */
public class ArchivoNotFoundException extends SgdocNotFoundBySearchEntityException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ArchivoNotFoundException(String documentoId) {
    super(ProblemMessage.builder().key(SgdocNotFoundBySearchEntityException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(Archivo.class))
        .parameter("searchEntity", ApplicationContextSupport.getMessage(Documento.class))
        .parameter("id", documentoId).build());
  }

}
