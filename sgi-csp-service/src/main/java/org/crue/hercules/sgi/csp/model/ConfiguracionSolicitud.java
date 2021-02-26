package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuracion_solicitud", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "convocatoria_id" }, name = "UK_CONFIGURACIONSOLICITUD_CONVOCATORIA") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionSolicitud extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuracion_solicitud_seq")
  @SequenceGenerator(name = "configuracion_solicitud_seq", sequenceName = "configuracion_solicitud_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria */
  @OneToOne
  @JoinColumn(name = "convocatoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONFIGURACIONSOLICITUD_CONVOCATORIA"))
  @NotNull
  private Convocatoria convocatoria;

  /** Tramitacion SGI */
  @Column(name = "tramitacion_sgi", columnDefinition = "boolean default false", nullable = true)
  private Boolean tramitacionSGI;

  /** Convocatoria Fase */
  @ManyToOne
  @JoinColumn(name = "convocatoria_fase_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONFIGURACIONSOLICITUD_CONVOCATORIAFASE"))
  private ConvocatoriaFase fasePresentacionSolicitudes;

  /** Importe Máximo Solicitud */
  @Column(name = "importe_maximo_solicitud", nullable = true)
  private BigDecimal importeMaximoSolicitud;

  /** Tipo Formulario Solicitud */
  @Column(name = "formulario_solicitud", length = 50, nullable = true)
  @Enumerated(EnumType.STRING)
  private FormularioSolicitud formularioSolicitud;

}