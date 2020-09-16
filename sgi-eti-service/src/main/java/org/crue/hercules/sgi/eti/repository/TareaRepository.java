package org.crue.hercules.sgi.eti.repository;

import java.util.List;

import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Tarea}.
 */
@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long>, JpaSpecificationExecutor<Tarea> {

  /**
   * Obtener todas las entidades {@link Tarea} para una determinada
   * {@link PeticionEvaluacion} que no estan asociadas a {@link Memoria} en
   * ninguno de los estados de la lista.
   *
   * @param idPeticionEvaluacion Id de {@link PeticionEvaluacion}.
   * @param idsTipoEstadoMemoria Ids de {@link TipoEstadoMemoria}.
   * @return la lista de entidades {@link Tarea}.
   */
  List<Tarea> findAllByEquipoTrabajoPeticionEvaluacionIdAndMemoriaEstadoActualIdNotIn(Long idPeticionEvaluacion,
      List<Long> idsTipoEstadoMemoria);

  /**
   * Elimina las {@link Tarea} para una determinado {@link EquipoTrabajo}
   * 
   * @param idEquipoTrabajo
   */
  void deleteByEquipoTrabajoId(Long idEquipoTrabajo);

}