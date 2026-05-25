package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoConfidencialidad;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * TipoConfidencialidadRepository
 */
@Repository
public interface TipoConfidencialidadRepository
    extends JpaRepository<TipoConfidencialidad, Long>, JpaSpecificationExecutor<TipoConfidencialidad> {

  /**
   * Busca un {@link TipoConfidencialidad} activo por idioma y valor de nombre.
   *
   * @param lang  idioma del nombre.
   * @param value valor del nombre.
   * @return el {@link TipoConfidencialidad} encontrado, si existe.
   */
  Optional<TipoConfidencialidad> findByNombreLangAndNombreValueAndActivoIsTrue(Language lang, String value);

}
