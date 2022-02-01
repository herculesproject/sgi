package org.crue.hercules.sgi.prc.repository;

import java.util.Optional;

import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.custom.CustomProduccionCientificaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProduccionCientifica}.
 */

@Repository
public interface ProduccionCientificaRepository
    extends JpaRepository<ProduccionCientifica, Long>, JpaSpecificationExecutor<ProduccionCientifica>,
    CustomProduccionCientificaRepository {

  /**
   * Obtiene la entidad {@link ProduccionCientifica} con el
   * produccionCientificaRef indicado
   *
   * @param produccionCientificaRef el produccionCientificaRef de
   *                                {@link ProduccionCientifica}.
   * @return el {@link ProduccionCientifica} con el produccionCientificaRef
   *         indicado
   */
  Optional<ProduccionCientifica> findByProduccionCientificaRef(String produccionCientificaRef);
}
