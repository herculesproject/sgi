package org.crue.hercules.sgi.csp.converter;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.TipoConfidencialidadInput;
import org.crue.hercules.sgi.csp.dto.TipoConfidencialidadOutput;
import org.crue.hercules.sgi.csp.model.TipoConfidencialidad;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * TipoConfidencialidadConverter
 */
@Component
@RequiredArgsConstructor
public class TipoConfidencialidadConverter {

  private final ModelMapper modelMapper;

  public TipoConfidencialidad convert(TipoConfidencialidadInput input) {
    return convert(null, input);
  }

  public TipoConfidencialidad convert(Long id, TipoConfidencialidadInput input) {
    TipoConfidencialidad entity = modelMapper.map(input, TipoConfidencialidad.class);
    entity.setId(id);
    return entity;
  }

  public TipoConfidencialidadOutput convert(TipoConfidencialidad entity) {
    return modelMapper.map(entity, TipoConfidencialidadOutput.class);
  }

  public Page<TipoConfidencialidadOutput> convert(Page<TipoConfidencialidad> page) {
    return page.map(this::convert);
  }

  public List<TipoConfidencialidadOutput> convert(List<TipoConfidencialidad> list) {
    return list.stream().map(this::convert).toList();
  }

}
