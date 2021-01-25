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
import javax.validation.constraints.Max;
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
@Table(name = "solicitud_proyecto_entidad_financiadora_ajena", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "solicitud_proyecto_datos_id",
        "entidad_ref" }, name = "UK_SOLICITUDPROYECTOENTIDADFINANCIADORAAJENA_SOLICITUDPROYECTODATOS_ENTIDAD") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyectoEntidadFinanciadoraAjena extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_entidad_financiadora_ajena_seq")
  @SequenceGenerator(name = "solicitud_proyecto_entidad_financiadora_ajena_seq", sequenceName = "solicitud_proyecto_entidad_financiadora_ajena_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria */
  @ManyToOne
  @JoinColumn(name = "solicitud_proyecto_datos_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOENTIDADFINANCIADORAAJENA_SOLICITUDPROYECTODATOS"))
  @NotNull
  private SolicitudProyectoDatos solicitudProyectoDatos;

  /** Entidad Financiadora */
  @Column(name = "entidad_ref", length = 50, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String entidadRef;

  /** FuenteFinanciacion */
  @ManyToOne
  @JoinColumn(name = "fuente_financiacion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOENTIDADFINANCIADORAAJENA_FUENTEFINANCIACION"))
  private FuenteFinanciacion fuenteFinanciacion;

  /** TipoFinanciacion */
  @ManyToOne
  @JoinColumn(name = "tipo_financiacion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOENTIDADFINANCIADORAAJENA_TIPOFINANCIACION"))
  private TipoFinanciacion tipoFinanciacion;

  /** PorcentajeFinanciacion */
  @Column(name = "porcentaje_financiacion", nullable = true)
  @Min(0)
  @Max(100)
  private Integer porcentajeFinanciacion;

}
