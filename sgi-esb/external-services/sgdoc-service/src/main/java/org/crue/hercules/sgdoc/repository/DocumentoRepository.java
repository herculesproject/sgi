package org.crue.hercules.sgdoc.repository;

import org.crue.hercules.sgdoc.model.DocumentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentoRepository
    extends JpaRepository<DocumentoEntity, String>, JpaSpecificationExecutor<DocumentoEntity> {
}
