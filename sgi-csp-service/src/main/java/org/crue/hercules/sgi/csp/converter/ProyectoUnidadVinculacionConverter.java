package org.crue.hercules.sgi.csp.converter;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.ProyectoUnidadVinculacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoUnidadVinculacionOutput;
import org.crue.hercules.sgi.csp.model.ProyectoUnidadVinculacion;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Converter entre {@link ProyectoUnidadVinculacion} y sus DTOs
 * {@link ProyectoUnidadVinculacionInput} /
 * {@link ProyectoUnidadVinculacionOutput}.
 */
@Component
@RequiredArgsConstructor
public class ProyectoUnidadVinculacionConverter {

  private final ModelMapper modelMapper;

  /**
   * Convierte un {@link ProyectoUnidadVinculacionInput} en un
   * {@link ProyectoUnidadVinculacion}.
   *
   * @param input el DTO de entrada.
   * @return la entidad equivalente con el id a {@code null}.
   */
  public ProyectoUnidadVinculacion convert(ProyectoUnidadVinculacionInput input) {
    ProyectoUnidadVinculacion entity = modelMapper.map(input, ProyectoUnidadVinculacion.class);
    entity.setId(null);
    return entity;
  }

  /**
   * Convierte una lista de {@link ProyectoUnidadVinculacionInput} en una lista de
   * {@link ProyectoUnidadVinculacion} nuevos.
   *
   * @param inputList lista de DTOs de entrada.
   * @return la lista de entidades equivalente.
   */
  public List<ProyectoUnidadVinculacion> convertProyectoUnidadesInput(List<ProyectoUnidadVinculacionInput> inputList) {
    return inputList.stream().map(this::convert).toList();
  }

  /**
   * Convierte una página de {@link ProyectoUnidadVinculacion} en una página de
   * {@link ProyectoUnidadVinculacionOutput}.
   *
   * @param page la página de entidades.
   * @return la página equivalente de DTOs de salida.
   */
  public Page<ProyectoUnidadVinculacionOutput> convert(Page<ProyectoUnidadVinculacion> page) {
    return page.map(this::convert);
  }

  /**
   * Convierte una lista de {@link ProyectoUnidadVinculacion} en una lista de
   * {@link ProyectoUnidadVinculacionOutput}.
   *
   * @param entityList lista de entidades.
   * @return la lista equivalente de DTOs de salida.
   */
  public List<ProyectoUnidadVinculacionOutput> convertProyectoUnidades(List<ProyectoUnidadVinculacion> entityList) {
    return entityList.stream().map(this::convert).toList();
  }

  /**
   * Convierte un {@link ProyectoUnidadVinculacion} en un
   * {@link ProyectoUnidadVinculacionOutput}.
   *
   * @param entity la entidad a convertir.
   * @return el DTO de salida equivalente.
   */
  public ProyectoUnidadVinculacionOutput convert(ProyectoUnidadVinculacion entity) {
    return modelMapper.map(entity, ProyectoUnidadVinculacionOutput.class);
  }

}
