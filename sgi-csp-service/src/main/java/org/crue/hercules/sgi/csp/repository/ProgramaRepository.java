package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.model.Programa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Programa}.
 */
@Repository
public interface ProgramaRepository extends JpaRepository<Programa, Long>, JpaSpecificationExecutor<Programa> {

  /**
   * Busca un {@link Programa} por su nombre en un {@link Plan}.
   * 
   * @param nombre Nombre del {@link Programa}.
   * @param idPlan Id del {@link Plan}.
   * @return un {@link Programa} si tiene el nombre buscado.
   */
  Optional<Programa> findByNombreAndPlanId(String nombre, Long idPlan);

  /**
   * Recupera los {@link Programa} que tienen como padre alguno de los
   * {@link Programa} de la lista de ids.
   * 
   * @param ids Ids {@link Programa}.
   * @return lista de {@link Programa} que tienen como padre alguno de los
   *         {@link Programa} de la lista de ids.
   */
  List<Programa> findByPadreIdIn(List<Long> ids);

}