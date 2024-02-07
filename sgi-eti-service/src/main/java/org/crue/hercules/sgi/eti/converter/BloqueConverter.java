package org.crue.hercules.sgi.eti.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.dto.BloqueOutput;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BloqueConverter {

  private final ModelMapper modelMapper;

  public BloqueOutput convert(Bloque entity, String lang) {
    BloqueOutput bloque = modelMapper.map(entity, BloqueOutput.class);
    return bloque;
  }

  public Page<BloqueOutput> convertPage(Page<Bloque> page, String lang) {
    return page.map(item -> convert(item, lang));
  }

  public List<BloqueOutput> convertList(List<Bloque> list, String lang) {
    return list.stream().map(item -> convert(item, lang)).collect(Collectors.toList());

  }

}
