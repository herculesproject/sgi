package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ProduccionCientificaApiOutput implements Serializable {
  private String produccionCientificaRef;
  private String epigrafe;
  private TipoEstadoProduccion estado;

  @JsonIgnore
  private EpigrafeCVN epigrafeCVN;

  public void setEpigrafe(String epigrafe) {
    this.epigrafe = epigrafe;
    if (StringUtils.hasText(epigrafe)) {
      this.epigrafeCVN = EpigrafeCVN.getByInternValue(epigrafe);
    }
  }

  public void setEpigrafeCVN(EpigrafeCVN epigrafeCVN) {
    if (null != epigrafeCVN) {
      this.epigrafe = epigrafeCVN.getInternValue();
    }
  }

}
