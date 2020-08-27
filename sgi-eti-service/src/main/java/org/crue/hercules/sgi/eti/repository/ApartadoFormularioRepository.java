package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.ApartadoFormulario;
import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ApartadoFormulario}.
 */

@Repository
public interface ApartadoFormularioRepository
    extends JpaRepository<ApartadoFormulario, Long>, JpaSpecificationExecutor<ApartadoFormulario> {

  /**
   * Obtiene las entidades {@link ApartadoFormulario} filtradas y paginadas según
   * por el id de su {@link BloqueFormulario}.
   *
   * @param id       id del {@link BloqueFormulario}.
   * @param pageable pageable
   * @return el listado de entidades {@link ApartadoFormulario} paginadas y
   *         filtradas.
   */
  Page<ApartadoFormulario> findByBloqueFormularioId(Long id, Pageable pageable);

  /**
   * Obtiene las entidades {@link ApartadoFormulario} filtradas y paginadas según
   * por el id de su padre {@link ApartadoFormulario}.
   *
   * @param id       id del {@link ApartadoFormulario}.
   * @param pageable pageable
   * @return el listado de entidades {@link ApartadoFormulario} paginadas y
   *         filtradas.
   */
  Page<ApartadoFormulario> findByApartadoFormularioPadreId(Long id, Pageable pageable);

}