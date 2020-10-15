package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link AreaTematicaArbol}.
 */
@Repository
public interface AreaTematicaArbolRepository
    extends JpaRepository<AreaTematicaArbol, Long>, JpaSpecificationExecutor<AreaTematicaArbol> {

  /**
   * Busca un {@link AreaTematicaArbol} por su abreviatura en un
   * {@link ListadoAreaTematica}.
   * 
   * @param abreviatura           Abreviatura del {@link AreaTematicaArbol}.
   * @param idListadoAreaTematica Id del {@link ListadoAreaTematica}.
   * @return un {@link AreaTematicaArbol} si tiene la abreviatura buscado.
   */
  Optional<AreaTematicaArbol> findByAbreviaturaAndListadoAreaTematicaId(String abreviatura, Long idListadoAreaTematica);

  /**
   * 
   * Busca un {@link AreaTematicaArbol} por su nombre en un
   * {@link ListadoAreaTematica}.
   * 
   * @param nombre
   * @param idListadoAreaTematica Id del {@link ListadoAreaTematica}.
   * @return un {@link AreaTematicaArbol} si tiene el nombre buscado.
   */
  Optional<AreaTematicaArbol> findByNombreAndListadoAreaTematicaId(String nombre, Long idListadoAreaTematica);

  /**
   * Recupera los {@link AreaTematicaArbol} que tienen como padre alguno de los
   * {@link AreaTematicaArbol} de la lista de ids.
   * 
   * @param ids Ids {@link AreaTematicaArbol}.
   * @return lista de {@link AreaTematicaArbol} que tienen como padre alguno de
   *         los {@link AreaTematicaArbol} de la lista de ids.
   */
  List<AreaTematicaArbol> findByPadreIdIn(List<Long> ids);

}