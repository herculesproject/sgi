package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;

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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solicitud_proyecto_presupuesto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyectoPresupuesto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_presupuesto_seq")
  @SequenceGenerator(name = "solicitud_proyecto_presupuesto_seq", sequenceName = "solicitud_proyecto_presupuesto_seq", allocationSize = 1)
  private Long id;

  /** Solicitud proyecto datos */
  @ManyToOne
  @JoinColumn(name = "solicitud_proyecto_datos_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUD_PROYECTO_PRESUPUESTO_SOLICITUD_PROYECTO_DATOS"))
  @NotNull
  private SolicitudProyectoDatos solicitudProyectoDatos;

  /** Concepto gasto */
  @ManyToOne
  @JoinColumn(name = "concepto_gasto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUD_PROYECTO_PRESUPUESTO_CONCEPTO_GASTO"))
  @NotNull
  private ConceptoGasto conceptoGasto;

  /** EntidadRef */
  @Column(name = "entidad_ref", length = 50, nullable = true)
  @Size(max = 50)
  private String entidadRef;

  /** Anualidad */
  @Column(name = "anualidad", nullable = true)
  @Min(1)
  private Integer anualidad;

  /** Importe Solicitado */
  @Column(name = "importe_solicitado", nullable = true)
  private BigDecimal importeSolicitado;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000, nullable = true)
  @Size(max = 2000)
  private String observaciones;

  /** Financiacion ajena */
  @Column(name = "financiacion_ajena", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean financiacionAjena;

}
