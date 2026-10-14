package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Relación entre una {@link PeticionEvaluacion} y una unidad de vinculación del
 * SGO. La unidad concreta se identifica por la referencia externa
 * {@code unidadVinculacionRef}.
 */
@Entity
@Table(name = PeticionEvaluacionUnidadVinculacion.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(name = "UK_PETEVALUNIDADVINCULACION_PETEVALID_UNIDADVINCULACIONREF", columnNames = {
        "peticion_evaluacion_id", "unidad_vinculacion_ref" })
})
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeticionEvaluacionUnidadVinculacion extends BaseEntity {

  protected static final String TABLE_NAME = "peticion_evaluacion_unidad_vinculacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Longitud máxima de la referencia a la unidad de vinculación */
  public static final int UNIDAD_VINCULACION_REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = PeticionEvaluacionUnidadVinculacion.SEQUENCE_NAME)
  @SequenceGenerator(name = PeticionEvaluacionUnidadVinculacion.SEQUENCE_NAME, sequenceName = PeticionEvaluacionUnidadVinculacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /**
   * Identificador de la {@link PeticionEvaluacion} a la que pertenece la unidad
   * de vinculación.
   */
  @Column(name = "peticion_evaluacion_id", nullable = false)
  @NotNull
  private Long peticionEvaluacionId;

  /** Referencia a la unidad en el SGO. */
  @Column(name = "unidad_vinculacion_ref", length = UNIDAD_VINCULACION_REF_LENGTH, nullable = false)
  @NotNull
  @Size(max = UNIDAD_VINCULACION_REF_LENGTH)
  private String unidadVinculacionRef;

  // Relation mappings for JPA metamodel generation only
  /** Petición de evaluación (solo para JPA metamodel) */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "peticion_evaluacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PETICIONEVALUACIONUNIDADVINCULACION_PETICIONEVALUACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @SuppressWarnings("java:S1170")
  private final PeticionEvaluacion peticionEvaluacion = null;

}
