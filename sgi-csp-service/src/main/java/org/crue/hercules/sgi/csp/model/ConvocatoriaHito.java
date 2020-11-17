package org.crue.hercules.sgi.csp.model;

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
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "convocatoria_hito")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ConvocatoriaHito extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_hito_seq")
  @SequenceGenerator(name = "convocatoria_hito_seq", sequenceName = "convocatoria_hito_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria */
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAHITO_CONVOCATORIA"))
  @NotNull
  private Convocatoria convocatoria;

  /** Tipo hito. */
  @ManyToOne
  @JoinColumn(name = "tipo_hito_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAHITO_TIPOHITO"))
  @NotNull
  private TipoHito tipoHito;

  /** Fecha. */
  @Column(name = "fecha", nullable = false)
  @NotNull
  private LocalDate fecha;

  /** Comentario. */
  @Column(name = "comentario", length = 2000)
  private String comentario;

  /** Genera Aviso */
  @Column(name = "genera_aviso", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean generaAviso;

}
