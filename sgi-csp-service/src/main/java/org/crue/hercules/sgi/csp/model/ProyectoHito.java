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
@Table(name = "proyecto_hito")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoHito extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_hito_seq")
  @SequenceGenerator(name = "proyecto_hito_seq", sequenceName = "proyecto_hito_seq", allocationSize = 1)
  private Long id;

  /** Proyecto */
  @ManyToOne
  @JoinColumn(name = "proyecto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOHITO_PROYECTO"))
  @NotNull
  private Proyecto proyecto;

  /** Tipo hito. */
  @ManyToOne
  @JoinColumn(name = "tipo_hito_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOHITO_TIPOHITO"))
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
  @Column(name = "genera_aviso", nullable = false)
  @NotNull
  private Boolean generaAviso;

}