package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ListadoAreaTematicaRepository
    extends JpaRepository<ListadoAreaTematica, Long>, JpaSpecificationExecutor<ListadoAreaTematica> {

  /**
   * Obtiene la entidad {@link ListadoAreaTematica} con el nombre indicado
   *
   * @param nombre el nombre de {@link ListadoAreaTematica}.
   * @return el {@link ListadoAreaTematica} con el nombre indicado
   */
  Optional<ListadoAreaTematica> findByNombre(String nombre);
}
