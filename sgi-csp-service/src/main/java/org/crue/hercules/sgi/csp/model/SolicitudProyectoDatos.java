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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solicitud_proyecto_datos")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyectoDatos extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_datos_seq")
  @SequenceGenerator(name = "solicitud_proyecto_datos_seq", sequenceName = "solicitud_proyecto_datos_seq", allocationSize = 1)
  private Long id;

  /** Solicitud */
  @ManyToOne
  @JoinColumn(name = "solicitud_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUD_PROYECTO_DATOS_SOLICITUD"), unique = true)
  @NotNull
  private Solicitud solicitud;

  /** Titulo */
  @Column(name = "titulo", length = 250, nullable = false)
  @Size(max = 250)
  @NotNull
  private String titulo;

  /** Acrónimo */
  @Column(name = "acronimo", length = 50, nullable = true)
  @Size(max = 50)
  private String acronimo;

  /** Duracion */
  @Column(name = "duracion", nullable = true)
  @Min(1)
  private Integer duracion;

  /** Colaborativo */
  @Column(name = "colaborativo", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean colaborativo;

  /** Coordinador externo */
  @Column(name = "coordinador_externo", nullable = true)
  private Boolean coordinadorExterno;

  /** Universidad Subcontratada */
  @Column(name = "uni_subcontratada", nullable = true)
  private Boolean universidadSubcontratada;

  /** Objetivos */
  @Column(name = "objetivos", length = 2000, nullable = true)
  @Size(max = 2000)
  private String objetivos;

  /** Intereses */
  @Column(name = "intereses", length = 2000, nullable = true)
  @Size(max = 2000)
  private String intereses;

  /** Resultados previstos */
  @Column(name = "resultados_previstos", length = 2000, nullable = true)
  @Size(max = 2000)
  private String resultadosPrevistos;

  /** Área temática */
  @ManyToOne
  @JoinColumn(name = "area_tematica_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUD_PROYECTO_DATOS_AREA_TEMATICA"))
  private AreaTematica areaTematica;

  /** CheckListRef */
  @Column(name = "check_list_ref", length = 2000, nullable = true)
  @Size(max = 2000)
  private String checkListRef;

  /** Envío Ética */
  @Column(name = "envio_etica", nullable = true)
  private Boolean envioEtica;

  /** Presupuesto por entidades */
  @Column(name = "presupuesto_por_entidades", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean presupuestoPorEntidades;

}
