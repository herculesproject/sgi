package org.crue.hercules.sgi.eti.converter;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionUnidadVinculacionInput;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionUnidadVinculacionOutput;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Converter entre {@link PeticionEvaluacionUnidadVinculacion} y sus DTOs
 * {@link PeticionEvaluacionUnidadVinculacionInput} /
 * {@link PeticionEvaluacionUnidadVinculacionOutput}.
 */
@Component
@RequiredArgsConstructor
public class PeticionEvaluacionUnidadVinculacionConverter {

  private final ModelMapper modelMapper;

  /**
   * Convierte un {@link PeticionEvaluacionUnidadVinculacionInput} en una
   * {@link PeticionEvaluacionUnidadVinculacion}.
   *
   * @param input el DTO de entrada.
   * @return la entidad equivalente con el id a {@code null}.
   */
  public PeticionEvaluacionUnidadVinculacion convert(PeticionEvaluacionUnidadVinculacionInput input) {
    PeticionEvaluacionUnidadVinculacion entity = modelMapper.map(input, PeticionEvaluacionUnidadVinculacion.class);
    entity.setId(null);
    return entity;
  }

  /**
   * Convierte una lista de {@link PeticionEvaluacionUnidadVinculacionInput} en
   * una lista de {@link PeticionEvaluacionUnidadVinculacion} nuevas.
   *
   * @param inputList lista de DTOs de entrada.
   * @return la lista de entidades equivalente.
   */
  public List<PeticionEvaluacionUnidadVinculacion> convertInputs(
      List<PeticionEvaluacionUnidadVinculacionInput> inputList) {
    return inputList.stream().map(this::convert).toList();
  }

  /**
   * Convierte una {@link PeticionEvaluacionUnidadVinculacion} en un
   * {@link PeticionEvaluacionUnidadVinculacionOutput}.
   *
   * @param entity la entidad a convertir.
   * @return el DTO de salida equivalente.
   */
  public PeticionEvaluacionUnidadVinculacionOutput convert(PeticionEvaluacionUnidadVinculacion entity) {
    return modelMapper.map(entity, PeticionEvaluacionUnidadVinculacionOutput.class);
  }

  /**
   * Convierte una página de {@link PeticionEvaluacionUnidadVinculacion} en una
   * página de {@link PeticionEvaluacionUnidadVinculacionOutput}.
   *
   * @param page la página de entidades.
   * @return la página equivalente de DTOs de salida.
   */
  public Page<PeticionEvaluacionUnidadVinculacionOutput> convert(Page<PeticionEvaluacionUnidadVinculacion> page) {
    return page.map(this::convert);
  }

  /**
   * Convierte una lista de {@link PeticionEvaluacionUnidadVinculacion} en una
   * lista de {@link PeticionEvaluacionUnidadVinculacionOutput}.
   *
   * @param entityList lista de entidades.
   * @return la lista equivalente de DTOs de salida.
   */
  public List<PeticionEvaluacionUnidadVinculacionOutput> convert(List<PeticionEvaluacionUnidadVinculacion> entityList) {
    return entityList.stream().map(this::convert).toList();
  }

}
