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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.BaseEntity.Create;
import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.validation.GrupoRelacionInstitucionalEntidadRefOrInstitucion;
import org.crue.hercules.sgi.csp.validation.UniqueGrupoRelacionInstitucional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * GrupoRelacionInstitucional
 */
@Entity
@Table(name = GrupoRelacionInstitucional.TABLE_NAME)
@GrupoRelacionInstitucionalEntidadRefOrInstitucion(groups = { Create.class, Update.class })
@UniqueGrupoRelacionInstitucional(groups = { Create.class, Update.class })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoRelacionInstitucional extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_relacion_institucional";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int ENTIDAD_REF_LENGTH = 255;
  public static final int INSTITUCION_LENGTH = 1000;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoRelacionInstitucional.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoRelacionInstitucional.SEQUENCE_NAME, sequenceName = GrupoRelacionInstitucional.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Grupo */
  @Column(name = "grupo_id", nullable = false)
  @NotNull
  private Long grupoId;

  /** Referencia a la entidad en el sistema corporativo de empresas (SGE). */
  @Column(name = "entidad_ref")
  @Size(max = ENTIDAD_REF_LENGTH)
  private String entidadRef;

  /** Nombre libre de la institución, cuando no se referencia una entidad SGE. */
  @Column(name = "institucion")
  @Size(max = INSTITUCION_LENGTH)
  private String institucion;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPORELACIONINSTITUCIONAL_GRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @SuppressWarnings("java:S1170")
  private final Grupo grupo = null;

}
