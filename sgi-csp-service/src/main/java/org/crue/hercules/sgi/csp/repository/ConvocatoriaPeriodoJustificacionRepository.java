package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.enums.TipoJustificacionEnum;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaPeriodoJustificacionRepository extends
    JpaRepository<ConvocatoriaPeriodoJustificacion, Long>, JpaSpecificationExecutor<ConvocatoriaPeriodoJustificacion> {

  /**
   * Busca los {@link ConvocatoriaPeriodoJustificacion} de una
   * {@link Convocatoria}.
   * 
   * @param convocatoriaId Id de la {@link Convocatoria}
   * @return lista de {@link ConvocatoriaPeriodoJustificacion}
   */
  List<ConvocatoriaPeriodoJustificacion> findAllByConvocatoriaId(Long convocatoriaId);

  /**
   * Busca los {@link ConvocatoriaPeriodoJustificacion} de una
   * {@link Convocatoria} con numPeriodo posterior al inidicado.
   * 
   * @param convocatoriaId Id de la {@link Convocatoria}
   * @param numPeriodo     numPeriodo del {@link ConvocatoriaPeriodoJustificacion}
   * @return lista de {@link ConvocatoriaPeriodoJustificacion}
   */
  List<ConvocatoriaPeriodoJustificacion> findAllByConvocatoriaIdAndNumPeriodoGreaterThan(Long convocatoriaId,
      Integer numPeriodo);

  /**
   * Busca el {@link ConvocatoriaPeriodoJustificacion} de una {@link Convocatoria}
   * con el mayor numPeriodo.
   * 
   * @param convocatoriaId Id de la {@link Convocatoria}
   * @return el {@link ConvocatoriaPeriodoJustificacion} de una
   *         {@link Convocatoria} con el mayor numPeriodo.
   */
  Optional<ConvocatoriaPeriodoJustificacion> findFirstByConvocatoriaIdOrderByNumPeriodoDesc(Long convocatoriaId);

  /**
   * Busca los {@link ConvocatoriaPeriodoJustificacion} en la {@link Convocatoria}
   * de un {@link TipoJustificacionEnum} concreto.
   * 
   * @param convocatoriaId    Id de la {@link Convocatoria}
   * @param tipoJustificacion {@link TipoJustificacionEnum}
   * @return lista de {@link ConvocatoriaPeriodoJustificacion}.
   */
  List<ConvocatoriaPeriodoJustificacion> findAllByConvocatoriaIdAndTipoJustificacion(Long convocatoriaId,
      TipoJustificacionEnum tipoJustificacion);

}
