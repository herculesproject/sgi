package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoDocumento}.
 */

@Repository
public interface TipoDocumentoRepository
    extends JpaRepository<TipoDocumento, Long>, JpaSpecificationExecutor<TipoDocumento> {

}