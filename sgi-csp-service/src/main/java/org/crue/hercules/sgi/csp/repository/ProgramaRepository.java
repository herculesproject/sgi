package org.crue.hercules.sgi.csp.repository;

import java.util.List;

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
   * Recupera los {@link Programa} activos que tienen como padre alguno de los
   * {@link Programa} de la lista de ids.
   * 
   * @param ids Ids {@link Programa}.
   * @return lista de {@link Programa} que tienen como padre alguno de los
   *         {@link Programa} de la lista de ids.
   */
  List<Programa> findByPadreIdInAndActivoIsTrue(List<Long> ids);

}