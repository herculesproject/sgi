package org.crue.hercules.sgi.eti.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.dto.ApartadoOutput;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApartadoConverter {

  private final ModelMapper modelMapper;

  public ApartadoOutput convert(Apartado entity, String lang) {
    ApartadoOutput apartado = modelMapper.map(entity, ApartadoOutput.class);
    return apartado;
  }

  public Page<ApartadoOutput> convertPage(Page<Apartado> page, String lang) {
    return page.map(item -> convert(item, lang));
  }

  public List<ApartadoOutput> convertList(List<Apartado> list, String lang) {
    return list.stream().map(item -> convert(item, lang)).collect(Collectors.toList());

  }

}
