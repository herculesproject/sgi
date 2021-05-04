package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.repository.custom.CustomActaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Acta}.
 */
@Repository
public interface ActaRepository
    extends JpaRepository<Acta, Long>, JpaSpecificationExecutor<Acta>, CustomActaRepository {

  /**
   * Comprueba si la entidad {@link Acta} esta asociada a una
   * {@link ConvocatoriaReunion}
   *
   * @param convocatoriaReunionId Id de {@link ConvocatoriaReunion}.
   * @return si hay acta asociado a la convocatoria de reunión
   */
  boolean existsByConvocatoriaReunionId(Long convocatoriaReunionId);
}