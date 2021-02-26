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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_solicitud")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoSolicitud extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /**
   * Enumerado tipo estado de las solicitudes.
   *
   */
  public enum Estado {
    /** Borrador */
    BORRADOR,
    /** Presentada */
    PRESENTADA,
    /** Admitida provisional */
    ADMITIDA_PROVISIONAL,
    /** Excluida provisional */
    EXCLUIDA_PROVISIONAL,
    /** Alegada admisión */
    ALEGADA_ADMISION,
    /** Excluida */
    EXCLUIDA,
    /** Admitida definitiva */
    ADMITIDA_DEFINITIVA,
    /** Concedida provisional */
    CONCECIDA_PROVISIONAL,
    /** Denegada provisional */
    DENEGADA_PROVISIONAL,
    /** Alegada admisión */
    ALEGADA_CONCESION,
    /** Desistida */
    DESISTIDA,
    /** Concedida */
    CONCECIDA,
    /** Denegada */
    DENEGADA;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_solicitud_seq")
  @SequenceGenerator(name = "estado_solicitud_seq", sequenceName = "estado_solicitud_seq", allocationSize = 1)
  private Long id;

  /** Solicitud */
  @Column(name = "id_solicitud", nullable = false)
  @NotNull
  private Long idSolicitud;

  /** Tipo estado solicitud */
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