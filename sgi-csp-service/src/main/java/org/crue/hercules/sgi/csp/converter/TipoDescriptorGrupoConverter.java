package org.crue.hercules.sgi.csp.converter;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.TipoDescriptorGrupoInput;
import org.crue.hercules.sgi.csp.dto.TipoDescriptorGrupoOutput;
import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * TipoDescriptorGrupoConverter
 */
@Component
@RequiredArgsConstructor
public class TipoDescriptorGrupoConverter {

  private final ModelMapper modelMapper;

  public TipoDescriptorGrupo convert(TipoDescriptorGrupoInput input) {
    return convert(null, input);
  }

  public TipoDescriptorGrupo convert(Long id, TipoDescriptorGrupoInput input) {
    TipoDescriptorGrupo entity = modelMapper.map(input, TipoDescriptorGrupo.class);
    entity.setId(id);
    return entity;
  }

  public TipoDescriptorGrupoOutput convert(TipoDescriptorGrupo entity) {
    return modelMapper.map(entity, TipoDescriptorGrupoOutput.class);
  }

  public Page<TipoDescriptorGrupoOutput> convert(Page<TipoDescriptorGrupo> page) {
    return page.map(this::convert);
  }

  public List<TipoDescriptorGrupoOutput> convert(List<TipoDescriptorGrupo> list) {
    return list.stream().map(this::convert).toList();
  }

}
