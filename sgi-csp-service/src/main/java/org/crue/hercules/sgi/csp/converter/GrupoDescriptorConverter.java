package org.crue.hercules.sgi.csp.converter;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.GrupoDescriptorInput;
import org.crue.hercules.sgi.csp.dto.GrupoDescriptorOutput;
import org.crue.hercules.sgi.csp.model.GrupoDescriptor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * GrupoDescriptorConverter
 */
@Component
@RequiredArgsConstructor
public class GrupoDescriptorConverter {

  private final ModelMapper modelMapper;

  public GrupoDescriptor convert(GrupoDescriptorInput input) {
    return convert(input.getId(), input);
  }

  public GrupoDescriptor convert(Long id, GrupoDescriptorInput input) {
    GrupoDescriptor entity = modelMapper.map(input, GrupoDescriptor.class);
    entity.setId(id);
    return entity;
  }

  public GrupoDescriptorOutput convert(GrupoDescriptor entity) {
    return modelMapper.map(entity, GrupoDescriptorOutput.class);
  }

  public Page<GrupoDescriptorOutput> convert(Page<GrupoDescriptor> page) {
    return page.map(this::convert);
  }

  public List<GrupoDescriptorOutput> convert(List<GrupoDescriptor> list) {
    return list.stream().map(this::convert).toList();
  }

}
