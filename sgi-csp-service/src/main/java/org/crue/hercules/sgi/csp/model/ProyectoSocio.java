package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "proyecto_socio")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoSocio extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_socio_seq")
  @SequenceGenerator(name = "proyecto_socio_seq", sequenceName = "proyecto_socio_seq", allocationSize = 1)
  private Long id;

  /** Proyecto. */
  @ManyToOne
  @JoinColumn(name = "proyecto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTO_SOCIO_PROYECTO"))
  @NotNull
  private Proyecto proyecto;

  /** Empresa ref. */
  @Column(name = "empresa_ref", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String empresaRef;

  /** Rol socio. */
  @ManyToOne
  @JoinColumn(name = "rol_socio_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTO_SOCIO_ROL_SOCIO"))
  @NotNull
  private RolSocio rolSocio;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio", nullable = true)
  private LocalDate fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin", nullable = true)
  private LocalDate fechaFin;

  /** Numero investigadores. */
  @Column(name = "num_investigadores", nullable = true)
  @Min(0)
  private Integer numInvestigadores;

  /** Importe concedido. */
  @Column(name = "importe_concedido", nullable = true)
  private BigDecimal importeConcedido;

}