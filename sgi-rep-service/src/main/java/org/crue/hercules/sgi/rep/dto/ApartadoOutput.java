package org.crue.hercules.sgi.rep.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApartadoOutput {
  private Long id;
  private String nombre;
  private String titulo;
  private Integer orden;
  private String esquema;

  private RespuestaOutput respuesta;

  private List<ElementOutput> elementos;

  private List<ApartadoOutput> apartadosHijos;
}
