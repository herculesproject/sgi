package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * TipoDescriptorGrupoRepository
 */
@Repository
public interface TipoDescriptorGrupoRepository
    extends JpaRepository<TipoDescriptorGrupo, Long>, JpaSpecificationExecutor<TipoDescriptorGrupo> {

  /**
   * Busca un {@link TipoDescriptorGrupo} activo por idioma y valor de nombre.
   *
   * @param lang  idioma del nombre.
   * @param value valor del nombre.
   * @return el {@link TipoDescriptorGrupo} encontrado, si existe.
   */
  Optional<TipoDescriptorGrupo> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String value);

}
