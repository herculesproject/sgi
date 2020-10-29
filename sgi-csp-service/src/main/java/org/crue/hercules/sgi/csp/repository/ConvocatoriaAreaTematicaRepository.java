package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaAreaTematicaRepository
    extends JpaRepository<ConvocatoriaAreaTematica, Long>, JpaSpecificationExecutor<ConvocatoriaAreaTematica> {

  /**
   * Busca un {@link ConvocatoriaAreaTematica} por su {@link Convocatoria} y
   * {@link AreaTematica}.
   * 
   * @param convocatoriaId Id de la {@link Convocatoria}
   * @param areaTematicaId Id de la {@link AreaTematica}
   * @return una {@link ConvocatoriaAreaTematica}
   */
  Optional<ConvocatoriaAreaTematica> findByConvocatoriaIdAndAreaTematicaId(Long convocatoriaId, Long areaTematicaId);
}
