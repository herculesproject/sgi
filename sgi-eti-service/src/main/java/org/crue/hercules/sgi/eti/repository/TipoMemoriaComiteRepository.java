package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.TipoMemoriaComite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoMemoriaComite}.
 */

@Repository
public interface TipoMemoriaComiteRepository
    extends JpaRepository<TipoMemoriaComite, Long>, JpaSpecificationExecutor<TipoMemoriaComite> {

}