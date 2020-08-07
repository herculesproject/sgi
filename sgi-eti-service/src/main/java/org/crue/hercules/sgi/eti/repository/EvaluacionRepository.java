package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Evaluacion}.
 */
@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long>, JpaSpecificationExecutor<Evaluacion> {

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} activas para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  Page<Evaluacion> findAllByActivoTrueAndConvocatoriaReunionId(Long id, Pageable pageable);

}