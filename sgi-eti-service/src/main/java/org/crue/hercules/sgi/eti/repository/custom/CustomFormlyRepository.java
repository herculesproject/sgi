package org.crue.hercules.sgi.eti.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.FormlyOutput;
import org.crue.hercules.sgi.eti.model.Formly;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Formly}.
 */
@Component
public interface CustomFormlyRepository {

  /**
   * Devuelve una lista paginada de {@link Formly} para una determinada
   * {@link Formly}
   * 
   * @param idFormly Id de {@link Formly}.
   * @return lista de {@link Formly}
   */
  List<FormlyOutput> findByFormlyId(Long idFormly);

  /**
   * Devuelve una lista paginada de {@link Formly}
   * 
   * @param nombre nombre de {@link Formly}.
   * @param lang   language code
   * @return lista de {@link Formly}
   */
  FormlyOutput findByNombreOrderByVersionDesc(String nombre, String lang);

}
