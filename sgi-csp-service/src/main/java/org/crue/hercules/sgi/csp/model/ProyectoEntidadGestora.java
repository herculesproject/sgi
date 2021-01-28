package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proyecto_entidad_gestora", uniqueConstraints = { @UniqueConstraint(columnNames = { "proyecto_id",
    "entidad_ref" }, name = "UK_PROYECTOENTIDADGESTORA_PROYECTO_ENTIDAD") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoEntidadGestora extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_entidad_gestora_seq")
  @SequenceGenerator(name = "proyecto_entidad_gestora_seq", sequenceName = "proyecto_entidad_gestora_seq", allocationSize = 1)
  private Long id;

  /** Proyecto */
  @ManyToOne
  @JoinColumn(name = "proyecto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOENTIDADGESTORA_PROYECTO"))
  @NotNull
  private Proyecto proyecto;

  /** Entidad */
  @Column(name = "entidad_ref", nullable = false)
  @NotNull
  private String entidadRef;

}