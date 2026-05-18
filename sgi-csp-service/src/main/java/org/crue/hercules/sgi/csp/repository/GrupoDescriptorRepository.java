package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GrupoDescriptor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * GrupoDescriptorRepository
 */
@Repository
public interface GrupoDescriptorRepository
    extends JpaRepository<GrupoDescriptor, Long>, JpaSpecificationExecutor<GrupoDescriptor> {
}
