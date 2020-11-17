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
@Table(name = "convocatoria_periodo_seguimiento_cientifico")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaPeriodoSeguimientoCientifico extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_periodo_seguimiento_cientifico_seq")
  @SequenceGenerator(name = "convocatoria_periodo_seguimiento_cientifico_seq", sequenceName = "convocatoria_periodo_seguimiento_cientifico_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria */
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAPERIODOSEGUIMIENTOCIENTIFICO_CONVOCATORIA"))
  @NotNull
  private Convocatoria convocatoria;

  /** Numero Periodo */
  @Column(name = "num_periodo", nullable = false)
  @Min(1)
  private Integer numPeriodo;

  /** Mes Inicial */
  @Column(name = "mes_inicial", nullable = false)
  @NotNull
  @Min(1)
  private Integer mesInicial;

  /** Mes Final */
  @Column(name = "mes_final", nullable = false)
  @NotNull
  @Min(2)
  private Integer mesFinal;

  /** Fecha Inicio Presentacion */
  @Column(name = "fecha_inicio_presentacion", nullable = true)
  private LocalDate fechaInicioPresentacion;

  /** Fecha Fin Presentacion */
  @Column(name = "fecha_fin_presentacion", nullable = true)
  private LocalDate fechaFinPresentacion;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000, nullable = true)
  @Size(max = 2000)
  private String observaciones;

}