package org.crue.hercules.sgi.csp.converter;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.SolicitudProyectoUnidadVinculacionInput;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoUnidadVinculacionOutput;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoUnidadVinculacion;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Converter entre {@link SolicitudProyectoUnidadVinculacion} y sus DTOs
 * {@link SolicitudProyectoUnidadVinculacionInput} /
 * {@link SolicitudProyectoUnidadVinculacionOutput}.
 */
@Component
@RequiredArgsConstructor
public class SolicitudProyectoUnidadVinculacionConverter {

  private final ModelMapper modelMapper;

  /**
   * Convierte un {@link SolicitudProyectoUnidadVinculacionInput} en un
   * {@link SolicitudProyectoUnidadVinculacion}.
   *
   * @param input el DTO de entrada.
   * @return la entidad equivalente con el id a {@code null}.
   */
  public SolicitudProyectoUnidadVinculacion convert(SolicitudProyectoUnidadVinculacionInput input) {
    SolicitudProyectoUnidadVinculacion entity = modelMapper.map(input, SolicitudProyectoUnidadVinculacion.class);
    entity.setId(null);
    return entity;
  }

  /**
   * Convierte una lista de {@link SolicitudProyectoUnidadVinculacionInput} en una
   * lista de {@link SolicitudProyectoUnidadVinculacion} nuevos.
   *
   * @param inputList lista de DTOs de entrada.
   * @return la lista de entidades equivalente.
   */
  public List<SolicitudProyectoUnidadVinculacion> convertSolicitudProyectoUnidadesInput(
      List<SolicitudProyectoUnidadVinculacionInput> inputList) {
    return inputList.stream().map(this::convert).toList();
  }

  /**
   * Convierte una página de {@link SolicitudProyectoUnidadVinculacion} en una
   * página de {@link SolicitudProyectoUnidadVinculacionOutput}.
   *
   * @param page la página de entidades.
   * @return la página equivalente de DTOs de salida.
   */
  public Page<SolicitudProyectoUnidadVinculacionOutput> convert(Page<SolicitudProyectoUnidadVinculacion> page) {
    return page.map(this::convert);
  }

  /**
   * Convierte una lista de {@link SolicitudProyectoUnidadVinculacion} en una
   * lista de {@link SolicitudProyectoUnidadVinculacionOutput}.
   *
   * @param entityList lista de entidades.
   * @return la lista equivalente de DTOs de salida.
   */
  public List<SolicitudProyectoUnidadVinculacionOutput> convertSolicitudProyectoUnidades(
      List<SolicitudProyectoUnidadVinculacion> entityList) {
    return entityList.stream().map(this::convert).toList();
  }

  /**
   * Convierte un {@link SolicitudProyectoUnidadVinculacion} en un
   * {@link SolicitudProyectoUnidadVinculacionOutput}.
   *
   * @param entity la entidad a convertir.
   * @return el DTO de salida equivalente.
   */
  public SolicitudProyectoUnidadVinculacionOutput convert(SolicitudProyectoUnidadVinculacion entity) {
    return modelMapper.map(entity, SolicitudProyectoUnidadVinculacionOutput.class);
  }

}
