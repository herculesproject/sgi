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
@Table(name = "convocatoria_documento")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaDocumento extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_documento_seq")
  @SequenceGenerator(name = "convocatoria_documento_seq", sequenceName = "convocatoria_documento_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria */
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIADOCUMENTO_CONVOCATORIA"))
  @NotNull
  private Convocatoria convocatoria;

  /** Tipo fase. */
  @ManyToOne
  @JoinColumn(name = "tipo_fase_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIADOCUMENTO_TIPOFASE"))
  private TipoFase tipoFase;

  /** Tipo documento. */
  @ManyToOne
  @JoinColumn(name = "tipo_documento_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIADOCUMENTO_TIPODOCUMENTO"))
  private TipoDocumento tipoDocumento;

  /** Nombre */
  @Column(name = "nombre", length = 50, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String nombre;

  /** Público */
  @Column(name = "publico", columnDefinition = "boolean default true", nullable = false)
  @NotNull
  private Boolean publico;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000, nullable = true)
  @Size(max = 2000)
  private String observaciones;

  /** Referencia documento */
  @Column(name = "documento_ref", length = 250, nullable = false)
  @NotNull
  private String documentoRef;

}