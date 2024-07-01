package org.crue.hercules.sgi.eti.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.FormlyOutput;
import org.crue.hercules.sgi.eti.model.Formly;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Formly}.
 */
@Component
public interface CustomFormlyRepository {

  /**
   * Devuelve una lista de {@link FormlyOutput} para un determinado id de
   * {@link Formly}
   * 
   * @param idFormly Id de {@link Formly}.
   * @return lista de {@link FormlyOutput}
   */
  List<FormlyOutput> findByFormlyId(Long idFormly);

  /**
   * Devuelve el {@link FormlyOutput} con él mayor número de versión a partir del
   * nombre e idioma especificado
   * 
   * @param nombre nombre de {@link Formly}.
   * @param lang   El {@link Language} sobre el que buscar.
   * @return lista de {@link FormlyOutput}
   */
  FormlyOutput findByNombreOrderByVersionDesc(String nombre, Language lang);

}
