package org.crue.hercules.sgi.csp.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * GrupoDescriptor
 */
@Entity
@Table(name = GrupoDescriptor.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoDescriptor extends BaseEntity {

  protected static final String TABLE_NAME = "grupo_descriptor";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GrupoDescriptor.SEQUENCE_NAME)
  @SequenceGenerator(name = GrupoDescriptor.SEQUENCE_NAME, sequenceName = GrupoDescriptor.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "grupo_id", nullable = false)
  @NotNull
  private Long grupoId;

  @Column(name = "tipo_descriptor_grupo_id", nullable = false)
  @NotNull
  private Long tipoDescriptorGrupoId;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "grupo_descriptor_texto", joinColumns = @JoinColumn(name = "grupo_descriptor_id"))
  @Valid
  @Builder.Default
  private Set<GrupoDescriptorTexto> texto = new HashSet<>();

  // Relation mappings for JPA metamodel generation only
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPODESCRIPTOR_GRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @SuppressWarnings("java:S1170")
  private final Grupo grupo = null;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tipo_descriptor_grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPODESCRIPTOR_TIPODESCRIPTORGRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @SuppressWarnings("java:S1170")
  private final TipoDescriptorGrupo tipoDescriptorGrupo = null;

}
