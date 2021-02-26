package org.crue.hercules.sgi.csp.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_proyecto", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "estado", "id_proyecto" }, name = "UK_ESTADOPROYECTO_ESTADO_PROYECTO") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoProyecto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /**
   * Estados del proyecto
   */
  public enum Estado {

    /** Borrador */
    BORRADOR,
    /** Provisional */
    PROVISIONAL,
    /** Abierto */
    ABIERTO,
    /** Finalizado */
    FINALIZADO,
    /** Cancelado */
    CANCELADO;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_proyecto_seq")
  @SequenceGenerator(name = "estado_proyecto_seq", sequenceName = "estado_proyecto_seq", allocationSize = 1)
  private Long id;

  /** Proyecto */
  @Column(name = "id_proyecto", nullable = false)
  @NotNull
  private Long idProyecto;

  /** Tipo estado proyecto */
  @Column(name = "estado", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Estado estado;

  /** Fecha. */
  @Column(name = "fecha_estado", nullable = false)
  @NotNull
  private LocalDateTime fechaEstado;

  /** Comentario */
  @Column(name = "comentario", length = 2000, nullable = true)
  @Size(max = 2000)
  private String comentario;

}