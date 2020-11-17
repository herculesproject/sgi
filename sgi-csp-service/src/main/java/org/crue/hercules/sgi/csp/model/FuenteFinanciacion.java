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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fuente_financiacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuenteFinanciacion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuente_financiacion_seq")
  @SequenceGenerator(name = "fuente_financiacion_seq", sequenceName = "fuente_financiacion_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @Column(name = "nombre", length = 50, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String nombre;

  /** Descripcion */
  @Column(name = "descripcion", length = 250, nullable = true)
  @Size(max = 250)
  private String descripcion;

  /** Fondo estructural */
  @Column(name = "fondo_estructural", nullable = false)
  @NotNull
  private Boolean fondoEstructural;

  /** Tipo ambito geografico. */
  @ManyToOne
  @JoinColumn(name = "tipo_ambito_geografico_id", nullable = false, foreignKey = @ForeignKey(name = "FK_FUENTEFINANCIACION_TIPOAMBITOGEOGRAFICO"))
  @NotNull
  private TipoAmbitoGeografico tipoAmbitoGeografico;

  /** Tipo origen fuente financiacion. */
  @ManyToOne
  @JoinColumn(name = "tipo_origen_fuente_financiacion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_FUENTEFINANCIACION_TIPOORIGENFUENTEFINANCIACION"))
  @NotNull
  private TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull(groups = { Update.class })
  private Boolean activo;

}