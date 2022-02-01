package org.crue.hercules.sgi.prc.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiFullOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AcreditacionInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AutorInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.IndiceImpactoInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.ProyectoInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaResumen;
import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utility class for {@link ProduccionCientifica} and related entities and
 * conversion
 * from/to Input/Output DTOs.
 */
@Component
public class ProduccionCientificaConverter {
  private ModelMapper modelMapper;

  /**
   * Constructor
   * 
   * @param modelMapper the model mapper used for the conversion
   */
  @Autowired
  private ProduccionCientificaConverter(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  public ProduccionCientificaApiOutput convertProduccionCientificaEstadoResumen(
      ProduccionCientificaResumen produccionCientificaResumen) {
    return modelMapper.map(produccionCientificaResumen,
        ProduccionCientificaApiOutput.class);
  }

  public List<ProduccionCientificaApiOutput> convertProduccionCientificaEstadoResumen(
      List<ProduccionCientificaResumen> list) {
    return list.stream()
        .map(produccionCientifica -> convertProduccionCientificaEstadoResumen(produccionCientifica))
        .collect(Collectors.toList());
  }

  public ProduccionCientificaApiFullOutput convert(ProduccionCientifica produccionCientifica) {
    return modelMapper.map(produccionCientifica, ProduccionCientificaApiFullOutput.class);
  }

  public AutorInput convertAutor(Autor autor) {
    return modelMapper.map(autor, AutorInput.class);
  }

  public AcreditacionInput convertAcreditacion(Acreditacion acreditacion) {
    return modelMapper.map(acreditacion, AcreditacionInput.class);
  }

  public ProyectoInput convertProyecto(Proyecto proyecto) {
    return modelMapper.map(proyecto, ProyectoInput.class);
  }

  public IndiceImpactoInput convertIndiceImpacto(IndiceImpacto indiceImpacto) {
    return modelMapper.map(indiceImpacto, IndiceImpactoInput.class);
  }

}
