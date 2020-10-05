package org.crue.hercules.sgi.eti.repository.custom;

import java.util.Optional;

import org.crue.hercules.sgi.eti.dto.ConvocatoriaReunionDatosGenerales;
import org.springframework.stereotype.Component;

/**
 * CustomConvocatoriaReunionRepository
 */
@Component
public interface CustomConvocatoriaReunionRepository {

  /**
   * Obteniene la entidad {@link ConvocatoriaReunionDatosGenerales} que contiene
   * la convocatoria con el identificador proporcionado y un campo que nos indica
   * el número de evaluaciones activas que no son revisión mínima.
   *
   * @param idConvocatoria id de la convocatoria.
   * 
   * @return la {@link ConvocatoriaReunionDatosGenerales}
   */
  Optional<ConvocatoriaReunionDatosGenerales> findByIdWithDatosGenerales(Long idConvocatoria);

}
