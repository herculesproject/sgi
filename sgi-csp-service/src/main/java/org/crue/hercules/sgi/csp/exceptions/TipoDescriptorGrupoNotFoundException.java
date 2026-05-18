package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;

/**
 * TipoDescriptorGrupoNotFoundException
 */
@SuppressWarnings("java:S110")
public class TipoDescriptorGrupoNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public TipoDescriptorGrupoNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", getEntityName(TipoDescriptorGrupo.class))
        .parameter("id", id).build());
  }

}
