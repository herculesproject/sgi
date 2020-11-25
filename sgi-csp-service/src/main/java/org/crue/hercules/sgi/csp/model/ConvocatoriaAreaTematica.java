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
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "convocatoria_area_tematica", uniqueConstraints = { @UniqueConstraint(columnNames = { "convocatoria_id",
    "area_tematica_id" }, name = "UK_CONVOCATORIAAREATEMATICA_CONVOCATORIA_AREATEMATICA") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaAreaTematica extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_area_tematica_seq")
  @SequenceGenerator(name = "convocatoria_area_tematica_seq", sequenceName = "convocatoria_area_tematica_seq", allocationSize = 1)
  private Long id;

  /** AreaTematica */
  @ManyToOne
  @JoinColumn(name = "area_tematica_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAAREATEMATICA_AREATEMATICA"))
  @NotNull
  private AreaTematica areaTematica;

  /** Convocatoria */
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAAREATEMATICA_CONVOCATORIA"))
  @NotNull
  private Convocatoria convocatoria;

  /** Obervaciones */
  @Column(name = "observaciones", nullable = true)
  @Size(max = 2000)
  private String observaciones;

}