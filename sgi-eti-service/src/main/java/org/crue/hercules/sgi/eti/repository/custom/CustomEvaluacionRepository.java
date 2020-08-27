package org.crue.hercules.sgi.eti.repository.custom;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface CustomEvaluacionRepository {

  /**
   * Obtener todas las entidades {@link EvaluacionWithNumComentario} paginadas
   * asociadas a una memoria y anteriores a la evaluación recibida.
   *
   * @param idMemoria    id de la memoria.
   * @param idEvaluacion id de la evaluación.
   * @param pageable     la información de la paginación.
   * 
   * @return la lista de entidades {@link EvaluacionWithNumComentario} paginadas
   *         y/o filtradas.
   */
  Page<EvaluacionWithNumComentario> findEvaluacionesAnterioresByMemoria(Long idMemoria, Long idEvaluacion,
      Pageable pageable);

}
