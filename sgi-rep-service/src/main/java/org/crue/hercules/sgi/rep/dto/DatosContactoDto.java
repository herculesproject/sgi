package org.crue.hercules.sgi.rep.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosContactoDto {
  private PaisDto paisContacto;
  private ComunidadAutonomaDto comAutonomaContacto;
  private ProvinciaDto provinciaContacto;
  private String ciudadContacto;
  private String codigoPostalContacto;
  private String direccionContacto;
  private List<String> emails;
  private List<String> telefonos;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class PaisDto {
    private String id;
    private String nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ComunidadAutonomaDto {
    private String id;
    private String nombre;
    private Long paisId;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ProvinciaDto {
    private String id;
    private String nombre;
    private Long comunidadAutonomaId;
  }
}