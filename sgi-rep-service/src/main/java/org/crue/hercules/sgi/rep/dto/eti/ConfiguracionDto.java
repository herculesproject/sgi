package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConfiguracionDto extends BaseRestDto {

  private Integer mesesArchivadaInactivo;
  private Integer diasArchivadaPendienteCorrecciones;
  private Integer diasLimiteEvaluador;
  private Integer mesesAvisoProyectoCEEA;
  private Integer mesesAvisoProyectoCEI;
  private Integer mesesAvisoProyectoCBE;

}