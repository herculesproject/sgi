package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.enums.Language;
import org.crue.hercules.sgi.eti.model.ActaDocumento;
import org.crue.hercules.sgi.eti.model.ActaDocumentoKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ActaDocumento}.
 */
@Repository
public interface ActaDocumentoRepository
    extends JpaRepository<ActaDocumento, ActaDocumentoKey>, JpaSpecificationExecutor<ActaDocumento> {

  ActaDocumento findByActaIdAndLang(Long idActa, Language language);

  boolean existsByActaIdAndLang(Long idActa, Language language);

}