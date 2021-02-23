package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConvocatoriaEntidadFinanciadora}.
 */
@Repository
public interface ConvocatoriaEntidadFinanciadoraRepository extends JpaRepository<ConvocatoriaEntidadFinanciadora, Long>,
    JpaSpecificationExecutor<ConvocatoriaEntidadFinanciadora> {

  /**
   * Obtiene las Entidades Financiadoras asociadas a una convocatoria
   * 
   * @param convocatoriaId Identificador de la convocatoria
   * @return Listado de convocatorias
   */
  List<ConvocatoriaEntidadFinanciadora> findAllByConvocatoriaId(Long convocatoriaId);

  /**
   * Recupera todos las {@link ConvocatoriaEntidadFinanciadora} asociados a una
   * {@link Convocatoria}.
   * 
   * @param convocatoriaId Identificador de
   *                       {@link ConvocatoriaEntidadFinanciadora}
   * @return listado de {@link ConvocatoriaEntidadFinanciadora}
   */
  List<ConvocatoriaEntidadFinanciadora> findByConvocatoriaId(Long convocatoriaId);

}
