package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ModeloTipoFaseDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloTipoFaseDocumento}.
 */
@Repository
public interface ModeloTipoFaseDocumentoRepository
    extends JpaRepository<ModeloTipoFaseDocumento, Long>, JpaSpecificationExecutor<ModeloTipoFaseDocumento> {

}