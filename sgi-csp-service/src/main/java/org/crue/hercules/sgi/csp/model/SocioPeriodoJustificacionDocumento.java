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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "socio_periodo_justificacion_documento")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocioPeriodoJustificacionDocumento extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "socio_periodo_justificacion_documento_seq")
  @SequenceGenerator(name = "socio_periodo_justificacion_documento_seq", sequenceName = "socio_periodo_justificacion_documento_seq", allocationSize = 1)
  private Long id;

  /** Proyecto socio. */
  @ManyToOne
  @JoinColumn(name = "proyecto_socio_periodo_justificacion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PERIODO_JUSTIFICACION_DOCUMENTO_SOCIO_PERIODO_JUSTIFICACION"))
  @NotNull
  private ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion;

  /** Nombre. */
  @Column(name = "nombre", length = 250, nullable = false)
  @Size(max = 250)
  @NotNull
  private String nombre;

  /** Referemcoa documento. */
  @Column(name = "documento_ref", length = 250, nullable = false)
  @Size(max = 250)
  @NotNull
  private String documentoRef;

  /** Tipo Documento */
  @ManyToOne
  @JoinColumn(name = "tipo_documento_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PERIODO_JUSTIFICACION_DOCUMENTO_TIPODOCUMENTO"))
  private TipoDocumento tipoDocumento;

  /** Comentario. */
  @Column(name = "comentario", length = 2000, nullable = true)
  @Size(max = 2000)
  private String comentario;

  /** Visible */
  @Column(name = "visible", nullable = false)
  @NotNull
  private Boolean visible;

}