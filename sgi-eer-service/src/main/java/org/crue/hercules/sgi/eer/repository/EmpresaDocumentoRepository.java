package org.crue.hercules.sgi.eer.repository;

import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmpresaDocumentoRepository
    extends JpaRepository<EmpresaDocumento, Long>, JpaSpecificationExecutor<EmpresaDocumento> {

}
