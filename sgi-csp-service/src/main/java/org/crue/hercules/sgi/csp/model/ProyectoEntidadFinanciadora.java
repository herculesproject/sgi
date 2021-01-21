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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proyecto_entidad_financiadora", uniqueConstraints = { @UniqueConstraint(columnNames = { "proyecto_id",
    "entidad_ref", "ajena" }, name = "UK_PROYECTOENTIDADFINANCIADORA_PROYECTO_ENTIDAD_AJENA") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoEntidadFinanciadora extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_entidad_financiadora_seq")
  @SequenceGenerator(name = "proyecto_entidad_financiadora_seq", sequenceName = "proyecto_entidad_financiadora_seq", allocationSize = 1)
  private Long id;

  /** Proyecto */
  @Column(name = "proyecto_id", nullable = false)
  @JoinColumn(name = "proyecto_id", foreignKey = @ForeignKey(name = "FK_PROYECTOENTIDADFINANCIADORA_PROYECTO"))
  @NotNull
  private Long proyectoId;

  /** Entidad Financiadora */
  @Column(name = "entidad_ref", length = 50, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String entidadRef;

  /** FuenteFinanciacion */
  @ManyToOne
  @JoinColumn(name = "fuente_financiacion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTOENTIDADFINANCIADORA_FUENTEFINANCIACION"))
  private FuenteFinanciacion fuenteFinanciacion;

  /** TipoFinanciacion */
  @ManyToOne
  @JoinColumn(name = "tipo_financiacion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTOENTIDADFINANCIADORA_TIPOFINANCIACION"))
  private TipoFinanciacion tipoFinanciacion;

  /** PorcentajeFinanciacion */
  @Column(name = "porcentaje_financiacion", nullable = true)
  @Min(0)
  private Integer porcentajeFinanciacion;

  /** Ajena */
  @Column(name = "ajena", nullable = false)
  private Boolean ajena;
}
