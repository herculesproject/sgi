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
 * Relación entre un {@link SolicitudProyecto} y una unidad de vinculación del
 * SGO.
 * La unidad concreta se identifica por la referencia externa
 * {@code unidadVinculacionRef}.
 */
@Entity
@Table(name = SolicitudProyectoUnidadVinculacion.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(name = "UK_SOLPROYUNIDADVINCULACION_SOLID_UNIDADVINCULACIONREF", columnNames = {
        "solicitud_proyecto_id", "unidad_vinculacion_ref" })
})
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "solicitudProyecto")
public class SolicitudProyectoUnidadVinculacion extends BaseEntity {

  protected static final String TABLE_NAME = "solicitud_proyecto_unidad_vinculacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int UNIDAD_VINCULACION_REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SolicitudProyectoUnidadVinculacion.SEQUENCE_NAME)
  @SequenceGenerator(name = SolicitudProyectoUnidadVinculacion.SEQUENCE_NAME, sequenceName = SolicitudProyectoUnidadVinculacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /**
   * Identificador del {@link SolicitudProyecto} al que pertenece la unidad de
   * vinculación.
   */
  @Column(name = "solicitud_proyecto_id", nullable = false)
  private Long solicitudProyectoId;

  /** Referencia a la unidad en el SGO. */
  @Column(name = "unidad_vinculacion_ref", length = UNIDAD_VINCULACION_REF_LENGTH, nullable = false)
  private String unidadVinculacionRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "solicitud_proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOUNIDADVINCULACION_SOLICITUDPROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @SuppressWarnings("java:S1170")
  private final SolicitudProyecto solicitudProyecto = null;

}
