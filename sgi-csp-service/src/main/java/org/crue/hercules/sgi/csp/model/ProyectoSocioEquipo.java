package org.crue.hercules.sgi.csp.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proyecto_socio_equipo")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoSocioEquipo extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_socio_equipo_seq")
  @SequenceGenerator(name = "proyecto_socio_equipo_seq", sequenceName = "proyecto_socio_seq", allocationSize = 1)
  private Long id;

  /** Rol socio. */
  @ManyToOne
  @JoinColumn(name = "proyecto_socio_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTO_SOCIO_EQUIPO_PROYECTO_SOCIO"))
  @NotNull
  private ProyectoSocio proyectoSocio;

  /** Rol proyecto. */
  @ManyToOne
  @JoinColumn(name = "rol_proyecto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTO_SOCIO_EQUIPO_ROL_PROYECTO"))
  @NotNull
  private RolProyecto rolProyecto;

  /** Persona ref. */
  @Column(name = "persona_ref", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String personaRef;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio", nullable = true)
  private LocalDate fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin", nullable = true)
  private LocalDate fechaFin;

}