package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * GrupoRelacionInstitucionalRepository
 */
@Repository
public interface GrupoRelacionInstitucionalRepository
    extends JpaRepository<GrupoRelacionInstitucional, Long>, JpaSpecificationExecutor<GrupoRelacionInstitucional> {

  /**
   * Recupera la primera {@link GrupoRelacionInstitucional} del {@link Grupo}
   * indicado cuya {@code entidadRef} coincida con la indicada.
   *
   * @param grupoId    identificador del {@link Grupo}.
   * @param entidadRef referencia a entidad del SGE a buscar.
   * @return la primera {@link GrupoRelacionInstitucional} coincidente, si
   *         existe.
   */
  Optional<GrupoRelacionInstitucional> findFirstByGrupoIdAndEntidadRef(Long grupoId, String entidadRef);

  /**
   * Recupera la primera {@link GrupoRelacionInstitucional} del {@link Grupo}
   * indicado cuyo nombre libre de {@code institucion} coincida con el indicado.
   *
   * @param grupoId     identificador del {@link Grupo}.
   * @param institucion nombre libre de la institución a buscar.
   * @return la primera {@link GrupoRelacionInstitucional} coincidente, si
   *         existe.
   */
  Optional<GrupoRelacionInstitucional> findFirstByGrupoIdAndInstitucion(Long grupoId, String institucion);
}
