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
@Table(name = "proyecto_fase")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoFase extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_fase_seq")
  @SequenceGenerator(name = "proyecto_fase_seq", sequenceName = "proyecto_fase_seq", allocationSize = 1)
  private Long id;

  /** Proyecto */
  @ManyToOne
  @JoinColumn(name = "proyecto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOFASE_PROYECTO"))
  @NotNull
  private Proyecto proyecto;

  /** Tipo Fase */
  @ManyToOne
  @JoinColumn(name = "tipo_fase_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOFASE_TIPOFASE"))
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

  /** Genera aviso */
  @Column(name = "genera_aviso")
  private Boolean generaAviso;

}