package org.crue.hercules.sgi.eti.exceptions;

/**
 * EvaluadorEvaluacionNotFoundException
 */
public class EvaluadorEvaluacionNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EvaluadorEvaluacionNotFoundException(Long evaluadorEvaluacionId) {
    super("EvaluadorEvaluacion " + evaluadorEvaluacionId + " does not exist.");
  }

}