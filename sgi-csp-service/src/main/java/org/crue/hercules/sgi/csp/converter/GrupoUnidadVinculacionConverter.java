package org.crue.hercules.sgi.csp.converter;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.GrupoUnidadVinculacionInput;
import org.crue.hercules.sgi.csp.dto.GrupoUnidadVinculacionOutput;
import org.crue.hercules.sgi.csp.model.GrupoUnidadVinculacion;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Converter entre {@link GrupoUnidadVinculacion} y sus DTOs
 * {@link GrupoUnidadVinculacionInput} / {@link GrupoUnidadVinculacionOutput}.
 */
@Component
@RequiredArgsConstructor
public class GrupoUnidadVinculacionConverter {

  private final ModelMapper modelMapper;

  /**
   * Convierte un {@link GrupoUnidadVinculacionInput} en un
   * {@link GrupoUnidadVinculacion}.
   *
   * @param input el DTO de entrada.
   * @return la entidad equivalente con el id a {@code null}.
   */
  public GrupoUnidadVinculacion convert(GrupoUnidadVinculacionInput input) {
    GrupoUnidadVinculacion entity = modelMapper.map(input, GrupoUnidadVinculacion.class);
    entity.setId(null);
    return entity;
  }

  /**
   * Convierte una lista de {@link GrupoUnidadVinculacionInput} en una lista de
   * {@link GrupoUnidadVinculacion} nuevos.
   *
   * @param inputList lista de DTOs de entrada.
   * @return la lista de entidades equivalente.
   */
  public List<GrupoUnidadVinculacion> convertGrupoUnidadesInput(List<GrupoUnidadVinculacionInput> inputList) {
    return inputList.stream().map(this::convert).toList();
  }

  /**
   * Convierte una página de {@link GrupoUnidadVinculacion} en una página de
   * {@link GrupoUnidadVinculacionOutput}.
   *
   * @param page la página de entidades.
   * @return la página equivalente de DTOs de salida.
   */
  public Page<GrupoUnidadVinculacionOutput> convert(Page<GrupoUnidadVinculacion> page) {
    return page.map(this::convert);
  }

  /**
   * Convierte una lista de {@link GrupoUnidadVinculacion} en una lista de
   * {@link GrupoUnidadVinculacionOutput}.
   *
   * @param entityList lista de entidades.
   * @return la lista equivalente de DTOs de salida.
   */
  public List<GrupoUnidadVinculacionOutput> convertGrupoUnidades(List<GrupoUnidadVinculacion> entityList) {
    return entityList.stream().map(this::convert).toList();
  }

  /**
   * Convierte un {@link GrupoUnidadVinculacion} en un
   * {@link GrupoUnidadVinculacionOutput}.
   *
   * @param entity la entidad a convertir.
   * @return el DTO de salida equivalente.
   */
  public GrupoUnidadVinculacionOutput convert(GrupoUnidadVinculacion entity) {
    return modelMapper.map(entity, GrupoUnidadVinculacionOutput.class);
  }

}
