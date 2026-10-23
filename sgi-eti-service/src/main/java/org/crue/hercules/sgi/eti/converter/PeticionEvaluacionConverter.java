package org.crue.hercules.sgi.eti.converter;

import org.crue.hercules.sgi.eti.dto.EstadoFinanciacionDto;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionSolicitudCspInput;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.EstadoFinanciacion;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Converter de los DTOs de {@link PeticionEvaluacion}.
 */
@Component
@RequiredArgsConstructor
public class PeticionEvaluacionConverter {

  private final ModelMapper modelMapper;

  /**
   * Convierte un {@link PeticionEvaluacionSolicitudCspInput} en una
   * {@link PeticionEvaluacion}.
   *
   * @param input el DTO de entrada.
   * @return la entidad equivalente con el id a {@code null}.
   */
  public PeticionEvaluacion convert(PeticionEvaluacionSolicitudCspInput input) {
    PeticionEvaluacion entity = modelMapper.map(input, PeticionEvaluacion.class);
    entity.setId(null);
    return entity;
  }

  /**
   * Convierte un {@link EstadoFinanciacionDto} en el {@link EstadoFinanciacion}
   * equivalente.
   *
   * @param estadoFinanciacion el estado de la financiación del DTO.
   * @return el estado de la financiación de la entidad.
   */
  public EstadoFinanciacion convert(EstadoFinanciacionDto estadoFinanciacion) {
    return EstadoFinanciacion.valueOf(estadoFinanciacion.name());
  }

}
