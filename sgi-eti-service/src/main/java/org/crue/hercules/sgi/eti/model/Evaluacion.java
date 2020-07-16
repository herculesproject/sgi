package org.crue.hercules.sgi.eti.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Evaluacion
 */

@Entity
@Table(name = "evaluacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Evaluacion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evaluacion_seq")
  @SequenceGenerator(name = "evaluacion_seq", sequenceName = "evaluacion_seq", allocationSize = 1)
  private Long id;

  /** Memoria */
  @ManyToOne
  @JoinColumn(name = "memoria_id", nullable = false)
  private Memoria memoria;

  /** Convocatoria reunión */
  @ManyToOne
  @JoinColumn(name = "convocatoria_reunion_id", nullable = false)
  private ConvocatoriaReunion convocatoriaReunion;

  /** Dictamen */
  @ManyToOne
  @JoinColumn(name = "dictamen_id", nullable = false)
  private Dictamen dictamen;

  /** Fecha Dictamen */
  @Column(name = "fecha_dictamen")
  private LocalDate fechaDictamen;

  /** Version */
  @Column(name = "version", nullable = false)
  @NotNull
  @Size(max = 8)
  private Integer version;

  /** Es revisión mínima */
  @Column(name = "es_rev_minima", columnDefinition = "boolean default false", nullable = false)
  private Boolean esRevMinima;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}