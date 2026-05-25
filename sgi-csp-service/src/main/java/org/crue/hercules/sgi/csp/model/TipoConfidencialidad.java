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
import org.crue.hercules.sgi.csp.validation.UniqueNombreTipoConfidencialidadActivo;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * TipoConfidencialidad
 */
@Entity
@Table(name = "tipo_confidencialidad")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@UniqueNombreTipoConfidencialidadActivo(groups = { BaseEntity.Update.class, BaseEntity.Create.class, OnActivar.class })
@ActivableIsActivo(entityClass = TipoConfidencialidad.class, groups = { BaseEntity.Update.class })
public class TipoConfidencialidad extends BaseActivableEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_confidencialidad_seq")
  @SequenceGenerator(name = "tipo_confidencialidad_seq", sequenceName = "tipo_confidencialidad_seq", allocationSize = 1)
  private Long id;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_confidencialidad_nombre", joinColumns = @JoinColumn(name = "tipo_confidencialidad_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<TipoConfidencialidadNombre> nombre = new HashSet<>();

}
