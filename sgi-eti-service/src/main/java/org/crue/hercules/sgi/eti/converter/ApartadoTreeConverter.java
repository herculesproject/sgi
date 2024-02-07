package org.crue.hercules.sgi.eti.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.dto.ApartadoOutput;
import org.crue.hercules.sgi.eti.dto.ApartadoTreeOutput;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApartadoTreeConverter {

  private final ModelMapper modelMapper;

  public ApartadoTreeOutput convert(ApartadoOutput entity) {
    return modelMapper.map(entity, ApartadoTreeOutput.class);
  }

  public Page<ApartadoTreeOutput> convertPage(Page<ApartadoOutput> page) {
    return page.map(this::convert);
  }

  public List<ApartadoTreeOutput> convertList(List<ApartadoOutput> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());

  }

}
