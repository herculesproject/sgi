package org.crue.hercules.sgi.eti.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ComiteFormulario;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Spring Data JPA repository para {@link ComiteFormulario}.
 */

@Repository
public interface ComiteFormularioRepository
    extends JpaRepository<ComiteFormulario, Long>, JpaSpecificationExecutor<ComiteFormulario> {

  /**
   * Recupera el comite formulario con comite activo e identificador recibido por
   * parámetro y formulario activo cuyo identificador se encuentre entre los
   * recibidos por parámetro.
   * 
   * @param idComite      Identificador {@link Comite}
   * @param idsFormulario Identificadores {@link Formulario}
   * @return {@link ComiteFormulario}
   */
  Optional<ComiteFormulario> findByComiteIdAndComiteActivoTrueAndFormularioIdInAndFormularioActivoTrue(Long idComite,
      List<Long> idsFormulario);

  /**
   * Recupera los comité formulario del comité recibido y que tanto este como sus
   * formularios estén activos.
   * 
   * @param idComite      Identificador {@link Comite}m
   * @param idsFormulario Identificadores de {@link Formulario}.
   * @param pageable      Datos paginación.
   * @return {@link ComiteFormulario}
   */
  Page<ComiteFormulario> findByComiteIdAndComiteActivoTrueAndFormularioActivoTrueAndFormularioIdIn(Long idComite,
      List<Long> idsFormulario, Pageable pageable);

}