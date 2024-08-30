package org.crue.hercules.sgi.eti.dto;

import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.eti.model.Memoria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoriaInput {
  @NotNull
  Long comiteId;

  @NotNull
  Long peticionEvaluacionId;

  @NotNull
  Memoria.Tipo tipo;

  String titulo;

  String responsableRef;

}
