package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link AnualidadGasto}.
 */
@Repository
public interface AnualidadGastoRepository
    extends JpaRepository<AnualidadGasto, Long>, JpaSpecificationExecutor<AnualidadGasto> {

  /**
   * Elimina los {@link AnualidadGasto} por el identificador de
   * {@link ProyectoAnualidad}.
   * 
   * @param id identificador de {@link ProyectoAnualidad}.
   */
  void deleteByProyectoAnualidadId(Long id);

  boolean existsByProyectoPartidaIdAndProyectoAnualidadEnviadoSgeIsTrue(Long proyectoPartidaId);

  Optional<List<AnualidadGasto>> findByProyectoPartidaId(Long proyectoPartidaId);
}
