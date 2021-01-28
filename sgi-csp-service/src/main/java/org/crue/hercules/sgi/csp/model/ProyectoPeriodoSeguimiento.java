package org.crue.hercules.sgi.csp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proyecto_periodo_seguimiento")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoPeriodoSeguimiento extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_periodo_seguimiento_seq")
  @SequenceGenerator(name = "proyecto_periodo_seguimiento_seq", sequenceName = "proyecto_periodo_seguimiento_seq", allocationSize = 1)
  private Long id;

  /** Proyecto */
  @ManyToOne
  @JoinColumn(name = "proyecto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOPERIODOSEGUIMIENTO_PROYECTO"))
  @NotNull
  private Proyecto proyecto;

  /** Fecha inicio. */
  @Column(name = "fecha_inicio", nullable = false)
  @NotNull
  private LocalDate fechaInicio;

  /** Fecha fin. */
  @Column(name = "fecha_fin", nullable = false)
  @NotNull
  private LocalDate fechaFin;

  /** Número periodo. */
  @Column(name = "num_periodo", nullable = false)
  @Min(1)
  @NotNull
  private Integer numPeriodo;

  /** Fecha inicio presentación. */
  @Column(name = "fecha_inicio_presentacion", nullable = true)
  private LocalDateTime fechaInicioPresentacion;

  /** Fecha fin. */
  @Column(name = "fecha_fin_presentacion", nullable = true)
  private LocalDateTime fechaFinPresentacion;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000, nullable = true)
  private String observaciones;

}