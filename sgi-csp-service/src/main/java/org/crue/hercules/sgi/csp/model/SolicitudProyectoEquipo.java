package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
@Table(name = "solicitud_proyecto_equipo")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyectoEquipo extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_equipo_seq")
  @SequenceGenerator(name = "solicitud_proyecto_equipo_seq", sequenceName = "solicitud_proyecto_equipo_seq", allocationSize = 1)
  private Long id;

  /** Solicitud proyecto datos */
  @OneToOne
  @JoinColumn(name = "solicitud_proyecto_datos_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUD_PROYECTO_EQUIPO_PROYECTO_DATOS"))
  @NotNull
  private SolicitudProyectoDatos solicitudProyectoDatos;

  /** Persona ref */
  @Column(name = "persona_ref", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String personaRef;

  /** Rol Proyecto */
  @ManyToOne
  @JoinColumn(name = "rol_proyecto_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUD_PROYECTO_EQUIPO_ROL_PROYECTO"))
  @NotNull
  private RolProyecto rolProyecto;

  /** Mes de inicio */
  @Column(name = "mes_inicio", nullable = true)
  private Integer mesInicio;

  /** Mes de fin */
  @Column(name = "mes_fin", nullable = true)
  private Integer mesFin;

}
