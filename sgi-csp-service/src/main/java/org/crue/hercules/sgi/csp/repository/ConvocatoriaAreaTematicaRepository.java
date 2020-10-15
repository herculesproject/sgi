package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaAreaTematicaRepository
    extends JpaRepository<ConvocatoriaAreaTematica, Long>, JpaSpecificationExecutor<ConvocatoriaAreaTematica> {

  /**
   * Busca un {@link ConvocatoriaAreaTematica} por su {@link Convocatoria} y
   * {@link AreaTematicaArbol}.
   * 
   * @param convocatoriaId      Id de la {@link Convocatoria}
   * @param areaTematicaArbolId Id de la {@link AreaTematicaArbol}
   * @return una {@link ConvocatoriaAreaTematica}
   */
  Optional<ConvocatoriaAreaTematica> findByConvocatoriaIdAndAreaTematicaArbolId(Long convocatoriaId,
      Long areaTematicaArbolId);
}
