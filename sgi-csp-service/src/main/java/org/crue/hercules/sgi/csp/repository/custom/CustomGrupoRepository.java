package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.dto.GrupoDto;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Grupo}.
 */
@Component
public interface CustomGrupoRepository {

  /**
   * Devuelve si grupoRef pertenece a un grupo de investigación con el campo
   * "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param grupoRef        grupoRef
   * @param fechaBaremacion fecha de baremación
   * @return true/false
   */
  Boolean isGrupoBaremable(Long grupoRef, Instant fechaBaremacion);

  /**
   * Devuelve una lista de {@link GrupoDto} pertenecientes a un determinado
   * grupo y que estén a 31 de diciembre del año de baremación
   *
   * @param fechaBaremacion fecha de baremación
   * 
   * @return Lista de {@link GrupoDto}
   */
  List<GrupoDto> findAllByAnio(Instant fechaBaremacion);

}
