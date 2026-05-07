package org.crue.hercules.sgi.csp.converter;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.TipoGrupoInput;
import org.crue.hercules.sgi.csp.dto.TipoGrupoOutput;
import org.crue.hercules.sgi.csp.model.TipoGrupo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TipoGrupoConverter {

  private final ModelMapper modelMapper;

  public TipoGrupo convert(TipoGrupoInput input) {
    return convert(null, input);
  }

  public TipoGrupo convert(Long id, TipoGrupoInput input) {
    TipoGrupo grupo = modelMapper.map(input, TipoGrupo.class);
    grupo.setId(id);
    return grupo;
  }

  public TipoGrupoOutput convert(TipoGrupo entity) {
    return modelMapper.map(entity, TipoGrupoOutput.class);
  }

  public Page<TipoGrupoOutput> convert(Page<TipoGrupo> page) {
    return page.map(this::convert);
  }

  public List<TipoGrupoOutput> convert(List<TipoGrupo> list) {
    return list.stream().map(this::convert).toList();
  }

}
