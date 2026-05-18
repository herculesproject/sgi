package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoDescriptor;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;

/**
 * GrupoDescriptorNotFoundException
 */
@SuppressWarnings("java:S110")
public class GrupoDescriptorNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public GrupoDescriptorNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", getEntityName(GrupoDescriptor.class))
        .parameter("id", id).build());
  }

}
