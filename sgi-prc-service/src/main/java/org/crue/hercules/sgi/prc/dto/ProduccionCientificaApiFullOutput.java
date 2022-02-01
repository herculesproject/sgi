package org.crue.hercules.sgi.prc.dto;

import java.util.List;

import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AcreditacionInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AutorInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.CampoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.IndiceImpactoInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.ProyectoInput;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ProduccionCientificaApiFullOutput extends ProduccionCientificaApiOutput {

  private List<CampoProduccionCientificaInput> campos;
  private List<AutorInput> autores;
  private List<IndiceImpactoInput> indicesImpacto;
  private List<AcreditacionInput> acreditaciones;
  private List<ProyectoInput> proyectos;

}
