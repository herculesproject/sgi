package org.crue.hercules.sgi.eti.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Memoria
 */

@Entity
@Table(name = "memoria")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Memoria extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memoria_seq")
  @SequenceGenerator(name = "memoria_seq", sequenceName = "memoria_seq", allocationSize = 1)
  private Long id;

  /** Referencia memoria */
  @Column(name = "num_referencia", length = 250, nullable = false)
  @NotNull
  private String numReferencia;

  /** Petición evaluación */
  @ManyToOne
  @JoinColumn(name = "peticion_evaluacion_id", nullable = false)
  @NotNull
  private PeticionEvaluacion peticionEvaluacion;

  /** Comité */
  @ManyToOne
  @JoinColumn(name = "comite_id", nullable = false)
  @NotNull
  private Comite comite;

  /** Título */
  @Column(name = "titulo", length = 250, nullable = false)
  @NotNull
  private String titulo;

  /** Referencia usuario */
  @Column(name = "usuario_ref", length = 250, nullable = false)
  @NotNull
  private String usuarioRef;

  /** Tipo Memoria */
  @ManyToOne
  @JoinColumn(name = "tipo_memoria_id", nullable = false)
  @NotNull
  private TipoMemoria tipoMemoria;

  /** Fecha envio secretaria. */
  @Column(name = "fecha_envio_secretaria")
  private LocalDate fechaEnvioSecretaria;

  /** Indicador require retrospectiva */
  @Column(name = "requiere_retrospectiva", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean requiereRetrospectiva;

  /** Fecha retrospectiva. */
  @Column(name = "fecha_retrospectiva")
  private LocalDate fechaRetrospectiva;

  /** Version */
  @Column(name = "version", nullable = false)
  @NotNull
  private Integer version;

  /** Estado Memoria Actual */
  @OneToOne
  @JoinColumn(name = "estado_actual_id", nullable = false)
  @NotNull
  private TipoEstadoMemoria estadoActual;

}