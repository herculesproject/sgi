package org.crue.hercules.sgi.eti.repository.custom;

import org.crue.hercules.sgi.eti.dto.BloqueOutput;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Bloque}.
 */
@Component
public interface CustomBloqueRepository {

  BloqueOutput findByBloqueIdAndLanguage(Long idBloque, String lang);

  /**
   * Devuelve una lista paginada de {@link Bloque} para un determinado formulario
   * e idioma
   * 
   * @param idFormulario Id de {@link Formulario}.
   * @param lang         code language
   * @param pageable     datos de paginación
   * @return lista de tareas con la informacion de si son eliminables.
   */
  Page<BloqueOutput> findByFormularioIdAndLanguage(Long idFormulario, String lang, Pageable pageable);

  /**
   * /**
   * Devuelve el {@link Bloque} general para un determinado idioma
   * 
   * @param lang code language
   * @return el bloque general
   */
  BloqueOutput getBloqueComentarioGenerales(String lang);

}
