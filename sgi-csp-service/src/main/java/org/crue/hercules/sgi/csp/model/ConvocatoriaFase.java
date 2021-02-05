package org.crue.hercules.sgi.csp.model;

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
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "convocatoria_fase")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ConvocatoriaFase extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_fase_seq")
  @SequenceGenerator(name = "convocatoria_fase_seq", sequenceName = "convocatoria_fase_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria */
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAFASE_CONVOCATORIA"))
  @NotNull
  private Convocatoria convocatoria;

  /** Tipo Fase */
  @ManyToOne
  @JoinColumn(name = "tipo_fase_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAFASE_TIPOFASE"))
  @NotNull
  private TipoFase tipoFase;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio", nullable = false)
  @NotNull
  private LocalDateTime fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin", nullable = false)
  @NotNull
  private LocalDateTime fechaFin;

  /** Observaciones. */
  @Column(name = "observaciones", length = 2000)
  private String observaciones;

}