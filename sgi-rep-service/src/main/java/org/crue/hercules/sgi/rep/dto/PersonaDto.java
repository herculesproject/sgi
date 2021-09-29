package org.crue.hercules.sgi.rep.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaDto {
  private Long id;
  private String nombre;
  private String numeroDocumento;
  private String apellidos;
  private DatosContactoDto datosContacto;
  // private SexoDto sexo;
  // private VinculacionDto vinculacion;
  // private DatosAcademicosDto datosAcademicos;
  // private EmpresaDto entidad;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SexoDto {
    private Long id;
    private String nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class VinculacionDto {
    private String id;
    private CategoriaProfesionalDto categoriaProfesional;
    private String fechaObtencionCategoria;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CategoriaProfesionalDto {
    private String id;
    private String nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DatosAcademicosDto {
    private Long id;
    private NivelAcademicoDto nivelAcademico;
    private String fechaObtencion;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class EmpresaDto {
    private String id;
    private TipoIdentificadorDto tipoIdentificador;
    private String numeroIdentificacion;
    private String razonSocial;
    private String datosEconomicos;
    private String padreId;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class NivelAcademicoDto {
    private String id;
    private String nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoIdentificadorDto {
    private String id;
    private String nombre;
  }

}