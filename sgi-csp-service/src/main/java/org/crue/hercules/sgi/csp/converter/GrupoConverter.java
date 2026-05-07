package org.crue.hercules.sgi.csp.converter;

import javax.annotation.PostConstruct;

import org.crue.hercules.sgi.csp.dto.GrupoDto;
import org.crue.hercules.sgi.csp.dto.GrupoInput;
import org.crue.hercules.sgi.csp.dto.GrupoOutput;
import org.crue.hercules.sgi.csp.dto.GrupoResumenOutput;
import org.crue.hercules.sgi.csp.dto.TipoGrupoOutput;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoTipo;
import org.crue.hercules.sgi.csp.model.TipoGrupo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoConverter {

  private final ModelMapper modelMapper;

  @PostConstruct
  public void mapperConfig() {
    modelMapper.emptyTypeMap(GrupoInput.class, Grupo.class)
        .addMappings(mapper -> mapper.<Boolean>map(GrupoInput::getEspecialInvestigacion,
            (dest, value) -> dest.getEspecialInvestigacion().setEspecialInvestigacion(value)))
        .addMappings(mapper -> mapper.skip(Grupo::setTipo))
        .implicitMappings()
        .setPostConverter(ctx -> {
          GrupoInput src = ctx.getSource();
          Grupo dest = ctx.getDestination();

          if (src.getTipoGrupoId() != null) {
            dest.setTipo(GrupoTipo.builder()
                .tipoGrupo(TipoGrupo.builder().id(src.getTipoGrupoId()).build())
                .build());
          }

          return dest;
        });

    modelMapper.emptyTypeMap(Grupo.class, GrupoOutput.class)
        .addMappings(mapper -> mapper.<Boolean>map(src -> src.getEspecialInvestigacion().getEspecialInvestigacion(),
            GrupoOutput::setEspecialInvestigacion))
        .addMappings(mapper -> mapper.skip(GrupoOutput::setTipoGrupo))
        .implicitMappings()
        .setPostConverter(ctx -> {
          Grupo src = ctx.getSource();
          GrupoOutput dest = ctx.getDestination();

          if (src.getTipo() != null && src.getTipo().getTipoGrupo() != null) {
            dest.setTipoGrupo(modelMapper.map(src.getTipo().getTipoGrupo(), TipoGrupoOutput.class));
          }

          return dest;
        });
  }

  public Grupo convert(GrupoInput input) {
    return convert(null, input);
  }

  public Grupo convert(Long id, GrupoInput input) {
    Grupo grupo = modelMapper.map(input, Grupo.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoOutput convert(Grupo entity) {
    return modelMapper.map(entity, GrupoOutput.class);
  }

  public Page<GrupoOutput> convert(Page<Grupo> page) {
    return page.map(this::convert);
  }

  public GrupoResumenOutput convertToGrupoResumenOutput(Grupo grupo) {
    return modelMapper.map(grupo, GrupoResumenOutput.class);
  }

  public GrupoDto convertToDto(Grupo entity) {
    return modelMapper.map(entity, GrupoDto.class);
  }

}
