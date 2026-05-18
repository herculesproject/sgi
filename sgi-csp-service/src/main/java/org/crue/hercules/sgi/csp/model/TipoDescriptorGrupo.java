package org.crue.hercules.sgi.csp.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.csp.model.BaseActivableEntity.OnActivar;
import org.crue.hercules.sgi.csp.validation.UniqueNombreTipoDescriptorGrupoActivo;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * TipoDescriptorGrupo
 */
@Entity
@Table(name = "tipo_descriptor_grupo")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@UniqueNombreTipoDescriptorGrupoActivo(groups = { BaseEntity.Update.class, BaseEntity.Create.class, OnActivar.class })
@ActivableIsActivo(entityClass = TipoDescriptorGrupo.class, groups = { BaseEntity.Update.class })
public class TipoDescriptorGrupo extends BaseActivableEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_descriptor_grupo_seq")
  @SequenceGenerator(name = "tipo_descriptor_grupo_seq", sequenceName = "tipo_descriptor_grupo_seq", allocationSize = 1)
  private Long id;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_descriptor_grupo_nombre", joinColumns = @JoinColumn(name = "tipo_descriptor_grupo_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<TipoDescriptorGrupoNombre> nombre = new HashSet<>();

}
