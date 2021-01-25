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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solicitud_proyecto_periodo_justificacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyectoPeriodoJustificacion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_periodo_justificacion_seq")
  @SequenceGenerator(name = "solicitud_proyecto_periodo_justificacion_seq", sequenceName = "solicitud_proyecto_periodo_justificacion_seq", allocationSize = 1)
  private Long id;

  /** Solicitud */
  @ManyToOne
  @JoinColumn(name = "solicitud_proyecto_socio_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION_SOLICITUD_PROYECTO_SOCIO"))
  @NotNull
  private SolicitudProyectoSocio solicitudProyectoSocio;

  /** Número de periodo */
  @Column(name = "num_periodo", nullable = false)
  @NotNull
  private Integer numPeriodo;

  /** Mes Inicio */
  @Column(name = "mes_inicial", nullable = false)
  @Min(1)
  @NotNull
  private Integer mesInicial;

  /** Mes Fin */
  @Column(name = "mes_final", nullable = false)
  @Min(1)
  @NotNull
  private Integer mesFinal;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio", nullable = true)
  private LocalDate fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin", nullable = true)
  private LocalDate fechaFin;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000, nullable = true)
  @Size(max = 2000)
  private String observaciones;

}
