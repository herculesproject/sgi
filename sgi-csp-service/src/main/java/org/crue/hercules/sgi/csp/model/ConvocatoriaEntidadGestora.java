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
@Table(name = "convocatoria_entidad_gestora", uniqueConstraints = { @UniqueConstraint(columnNames = { "convocatoria_id",
    "entidad_ref" }, name = "UK_CONVOCATORIAENTIDADGESTORA_CONVOCATORIA_ENTIDAD") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaEntidadGestora extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_entidad_gestora_seq")
  @SequenceGenerator(name = "convocatoria_entidad_gestora_seq", sequenceName = "convocatoria_entidad_gestora_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria */
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAENTIDADGESTORA_CONVOCATORIA"))
  @NotNull
  private Convocatoria convocatoria;

  /** Entidad */
  @Column(name = "entidad_ref", nullable = false)
  @NotNull
  private String entidadRef;

}