package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Comite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Comite}.
 */

@Repository
public interface ComiteRepository extends JpaRepository<Comite, Long>, JpaSpecificationExecutor<Comite> {

  /**
   * Obtiene una lista paginada {@link Comite} a partir de su nombre.
   *
   * @param comite el nombre de la entidad {@link Comite}.
   * @param paging pageable
   * @return el listado paginado {@link Comite}.
   */
  Page<Comite> findByComiteContaining(String comite, Pageable paging);

}