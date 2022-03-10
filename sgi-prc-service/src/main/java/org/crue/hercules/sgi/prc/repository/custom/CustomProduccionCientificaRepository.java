package org.crue.hercules.sgi.prc.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaResumen;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link ProduccionCientifica}.
 */
@Component
public interface CustomProduccionCientificaRepository {

  /**
   * Elimina el estado de {@link ProduccionCientifica} cuyo id coincide con el
   * indicado.
   * 
   * @param produccionCientificaId el identificador de la
   *                               {@link ProduccionCientifica}
   * @return el número de registros eliminados
   */
  @Modifying
  int updateEstadoNull(long produccionCientificaId);

  /**
   * Devuelve el identificador CVN y el estado (Validado O Rechazado) de aquellos
   * items almacenados en producción científica que han cambiado al estado
   * Validado o Rechazado en una fecha igual o superior a la fecha de estado
   * pasada por parámetro y el epigrafeCVN sea uno de los que llevan por el CVN
   * (en la tabla ConfiguracionBaremo el tipo de Fuente es CVN o CVN_OTRO_SISTEMA)
   * 
   * @param specification filtro fechaEstado
   * @return lista de {@link ProduccionCientificaResumen}
   */
  List<ProduccionCientificaResumen> findByEstadoValidadoOrRechazadoByFechaModificacion(
      Specification<ProduccionCientifica> specification);

  /**
   * Devuelve una lista de ids de {@link ProduccionCientifica} de un
   * {@link EpigrafeCVN} que cumplan las condiciones de baremación.
   * 
   * @param baremacionInput fechaInicio Fecha inicio de baremación en formato UTC,
   *                        fechaFin Fecha fin de baremación en formato UTC,
   *                        epigrafeCVN {@link EpigrafeCVN} a filtrar,
   *                        codigoCVN {@link CodigoCVN} a filtrar
   * @return lista de ids de {@link ProduccionCientifica}
   */
  List<Long> findAllBaremacion(BaremacionInput baremacionInput);
}