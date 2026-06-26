package org.crue.hercules.sgi.csp.util;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utilidades de logging para el servicio CSP.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SgiLogUtils {

  /**
   * Formatea un {@link Pageable} para su inclusión en mensajes de log.
   *
   * @param pageable el objeto de paginación.
   * @return {@code "unpaged"} o {@code "page=N,size=M,sort=X"}.
   */
  public static String pageable(Pageable pageable) {
    if (pageable == null || !pageable.isPaged()) {
      return "unpaged";
    }

    String sort = pageable.getSort().isSorted()
        ? pageable.getSort().toString().replace(": ", "-").replace(", ", "|")
        : "unsorted";
    return "page=%d|size=%d|sort=%s".formatted(pageable.getPageNumber(), pageable.getPageSize(), sort);
  }

  /**
   * Formatea un {@link Page} de respuesta para su inclusión en mensajes de log.
   *
   * @param page el resultado paginado.
   * @return {@code "empty"} o {@code "elements=N,page=P/T"}.
   */
  public static String page(Page<?> page) {
    if (page == null || page.isEmpty()) {
      return "empty";
    }

    return "elements=%d|page=%d/%d".formatted(
        page.getNumberOfElements(),
        page.getNumber() + 1,
        page.getTotalPages());
  }

  /**
   * Formatea una {@link Collection} de respuesta para su inclusión en mensajes
   * de log.
   *
   * @param collection la colección de resultados.
   * @return {@code "empty"} o {@code "elements=N"}.
   */
  public static String collection(Collection<?> collection) {
    if (collection == null || collection.isEmpty()) {
      return "empty";
    }

    return "elements=%d".formatted(collection.size());
  }

}
