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
import javax.persistence.UniqueConstraint;

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
 * Relación entre un {@link Grupo} y una unidad de vinculación del SGO.
 * La unidad concreta se identifica por la referencia externa {@code unidadRef}.
 */
@Entity
@Table(name = GrupoUnidadVinculacion.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(name = "UK_GRUPOUNIDADVINCULACION_GRUPOID_UNIDADVINCULACIONREF", columnNames = { "grupo_id",
        "unidad_vinculacion_ref" })
})
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "grupo")
public class GrupoUnidadVinculacion extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_unidad_vinculacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int UNIDAD_VINCULACION_REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoUnidadVinculacion.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoUnidadVinculacion.SEQUENCE_NAME, sequenceName = GrupoUnidadVinculacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /**
   * Identificador del {@link Grupo} al que pertenece la unidad de vinculación.
   */
  @Column(name = "grupo_id", nullable = false)
  private Long grupoId;

  /** Referencia a la unidad en el SGO. */
  @Column(name = "unidad_vinculacion_ref", length = UNIDAD_VINCULACION_REF_LENGTH, nullable = false)
  private String unidadVinculacionRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPOUNIDAD_GRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @SuppressWarnings("java:S1170")
  private final Grupo grupo = null;

}
