package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.SolicitudRrhhInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhOutput;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SolicitudRrhhConverter {

  private final ModelMapper modelMapper;

  public SolicitudRrhh convert(SolicitudRrhhInput input) {
    return convert(input.getId(), input);
  }

  public SolicitudRrhh convert(Long id, SolicitudRrhhInput input) {
    SolicitudRrhh entity = modelMapper.map(input, SolicitudRrhh.class);
    entity.setId(id);
    return entity;
  }

  public SolicitudRrhhOutput convert(SolicitudRrhh entity) {
    return modelMapper.map(entity, SolicitudRrhhOutput.class);
  }

}
