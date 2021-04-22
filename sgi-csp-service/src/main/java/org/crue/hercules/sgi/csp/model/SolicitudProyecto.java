package org.crue.hercules.sgi.csp.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitud_proyecto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyecto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  /** Titulo */
  @Column(name = "titulo", length = 250, nullable = false)
  @Size(max = 250)
  @NotNull
  private String titulo;

  /** Acrónimo */
  @Column(name = "acronimo", length = 50, nullable = true)
  @Size(max = 50)
  private String acronimo;

  /** Código externo */
  @Column(name = "codigo_externo", length = 250, nullable = true)
  @Size(max = 50)
  private String codExterno;

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
  @JoinColumn(name = "area_tematica_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTO_AREATEMATICA"))
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

  // Relation mappings for JPA metamodel generation only
  @OneToOne
  @JoinColumn(name = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTO_SOLICITUD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Solicitud solicitud = null;

  @OneToMany(mappedBy = "solicitudProyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudProyectoEquipo> equipo = null;

  @OneToMany(mappedBy = "solicitudProyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudProyectoEntidadFinanciadoraAjena> entidadesFinanciadorasAjenas = null;

  @OneToMany(mappedBy = "solicitudProyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudProyectoPresupuesto> presupuesto = null;
}
