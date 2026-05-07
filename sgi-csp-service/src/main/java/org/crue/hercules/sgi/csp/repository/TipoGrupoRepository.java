package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoGrupo;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoGrupoRepository
    extends JpaRepository<TipoGrupo, Long>, JpaSpecificationExecutor<TipoGrupo> {

  /**
   * Obtiene la entidad {@link TipoGrupo} activo con el nombre indicado y el
   * language
   *
   * @param lang   el language sobre el que buscar
   * @param nombre el nombre de {@link TipoGrupo}.
   * @return el {@link TipoGrupo} activo con el nombre indicado
   */
  Optional<TipoGrupo> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String nombre);

}
