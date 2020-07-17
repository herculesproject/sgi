package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * EvaluadorEvaluacion
 */

@Entity
@Table(name = "evaluador_evaluacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class EvaluadorEvaluacion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evaluador_evaluacion_seq")
  @SequenceGenerator(name = "evaluador_evaluacion_seq", sequenceName = "evaluador_evaluacion_seq", allocationSize = 1)
  private Long id;

  /** Evaluador */
  @ManyToOne
  @JoinColumn(name = "evaluador_id", nullable = true)
  private Evaluador evaluador;

  /** Evaluación */
  @ManyToOne
  @JoinColumn(name = "evaluacion_id", nullable = true)
  private Evaluacion evaluacion;

}