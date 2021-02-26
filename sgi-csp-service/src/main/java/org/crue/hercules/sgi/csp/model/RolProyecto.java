package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rol_proyecto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolProyecto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /**
   * Equipos.
   *
   */
  public enum Equipo {

    /** Equipo de investigación */
    INVESTIGACION,
    /** Equipo de trabajo */
    TRABAJO;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rol_proyecto_seq")
  @SequenceGenerator(name = "rol_proyecto_seq", sequenceName = "rol_proyecto_seq", allocationSize = 1)
  private Long id;

  /** Abreviatura */
  @Column(name = "abreviatura", length = 5, nullable = false)
  @NotBlank
  @Size(max = 5)
  private String abreviatura;

  /** Nombre */
  @Column(name = "nombre", length = 50, nullable = false)
  @NotBlank
  @Size(max = 50)
  private String nombre;

  /** Descripción */
  @Column(name = "descripcion", length = 250, nullable = true)
  @Size(max = 250)
  private String descripcion;

  /** Rol principal */
  @Column(name = "rol_principal", columnDefinition = "boolean default false", nullable = true)
  private Boolean rolPrincipal;

  /** Responsable económico */
  @Column(name = "responsable_economico", columnDefinition = "boolean default false", nullable = true)
  private Boolean responsableEconomico;

  /** Tipo Formulario Solicitud */
  @Column(name = "equipo", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Equipo equipo;

  /** Colectivo */
  @Column(name = "colectivo_ref", nullable = true)
  private String colectivoRef;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}