package org.crue.hercules.sgi.csp.dto.com;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CspComCalendarioFacturacionNotificarData implements Serializable {
  private Collection<? extends I18nFieldValue> tituloProyecto;
  private List<String> codigosSge;
  private Integer numPrevision;
  private List<String> entidadesFinanciadoras;
  private Collection<? extends I18nFieldValue> tipoFacturacion;
  private String apellidosDestinatario;
  private boolean prorroga;
}
