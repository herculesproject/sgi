package org.crue.hercules.sgi.csp.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaHitoRepository
    extends JpaRepository<ConvocatoriaHito, Long>, JpaSpecificationExecutor<ConvocatoriaHito> {

  /**
   * Busca un {@link ConvocatoriaHito} por su {@link Convocatoria} y url.
   * 
   * @param fecha      fecha de la {@link ConvocatoriaHito}
   * @param tipoHitoId Id de la {@link TipoHito}
   * @return una {@link ConvocatoriaHito}
   */
  Optional<ConvocatoriaHito> findByFechaAndTipoHitoId(LocalDate fecha, Long tipoHitoId);

}
