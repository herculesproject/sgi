package org.crue.hercules.sgi.csp.model;

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
 * Relación entre un {@link Proyecto} y una unidad de vinculación del SGO.
 * La unidad concreta se identifica por la referencia externa {@code unidadRef}.
 */
@Entity
@Table(name = ProyectoUnidadVinculacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "proyecto")
public class ProyectoUnidadVinculacion extends BaseEntity {

  protected static final String TABLE_NAME = "proyecto_unidad_vinculacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Longitud máxima de la referencia a la unidad de vinculación. */
  public static final int UNIDAD_VINCULACION_REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ProyectoUnidadVinculacion.SEQUENCE_NAME)
  @SequenceGenerator(name = ProyectoUnidadVinculacion.SEQUENCE_NAME, sequenceName = ProyectoUnidadVinculacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /**
   * Identificador del {@link Proyecto} al que pertenece la unidad de vinculación.
   */
  @Column(name = "proyecto_id", nullable = false)
  private Long proyectoId;

  /** Referencia a la unidad en el SGO. */
  @Column(name = "unidad_vinculacion_ref", length = UNIDAD_VINCULACION_REF_LENGTH, nullable = false)
  private String unidadVinculacionRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOUNIDADVINCULACION_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @SuppressWarnings("java:S1170")
  private final Proyecto proyecto = null;

}
