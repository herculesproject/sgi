package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.GrupoRelacionInstitucionalInput;
import org.crue.hercules.sgi.csp.dto.GrupoRelacionInstitucionalOutput;
import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoRelacionInstitucionalConverter {

  private final ModelMapper modelMapper;

  public GrupoRelacionInstitucional convert(GrupoRelacionInstitucionalInput input) {
    return convert(null, input);
  }

  public GrupoRelacionInstitucional convert(Long id, GrupoRelacionInstitucionalInput input) {
    GrupoRelacionInstitucional entity = modelMapper.map(input, GrupoRelacionInstitucional.class);
    entity.setId(id);
    return entity;
  }

  public GrupoRelacionInstitucionalOutput convert(GrupoRelacionInstitucional entity) {
    return modelMapper.map(entity, GrupoRelacionInstitucionalOutput.class);
  }

  public Page<GrupoRelacionInstitucionalOutput> convert(Page<GrupoRelacionInstitucional> page) {
    return page.map(this::convert);
  }

}
