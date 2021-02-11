package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProyectoDocumento}.
 */
@Repository
public interface ProyectoDocumentoRepository
    extends JpaRepository<ProyectoDocumento, Long>, JpaSpecificationExecutor<ProyectoDocumento> {

  /**
   * Busca tofod lod {@link ProyectoDocumento} por su {@link Proyecto}
   * 
   * @param proyectoId Id de la {@link Proyecto}
   * @return listado de {@link ProyectoDocumento}
   */
  List<ProyectoDocumento> findAllByProyectoId(Long proyectoId);

}
