package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.converter.ClasificacionCVNConverter;
import org.crue.hercules.sgi.csp.converter.TipoDestinatarioConverter;
import org.crue.hercules.sgi.csp.converter.TipoEstadoConvocatoriaConverter;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVNEnum;
import org.crue.hercules.sgi.csp.enums.TipoDestinatarioEnum;
import org.crue.hercules.sgi.csp.enums.TipoEstadoConvocatoriaEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "convocatoria")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Convocatoria extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_seq")
  @SequenceGenerator(name = "convocatoria_seq", sequenceName = "convocatoria_seq", allocationSize = 1)
  private Long id;

  /** Unidad Gestion */
  @Column(name = "unidad_gestion_ref", nullable = true)
  private String unidadGestionRef;

  /** Modelo Ejecucion */
  @ManyToOne
  @JoinColumn(name = "modelo_ejecucion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIA_MODELOEJECUCION"))
  private ModeloEjecucion modeloEjecucion;

  /** Codigo */
  @Column(name = "codigo", length = 50, nullable = true)
  @Size(max = 50)
  private String codigo;

  /** Anio */
  @Column(name = "anio", nullable = true)
  @Min(1000)
  @Max(9999)
  @Digits(fraction = 0, integer = 4)
  private Integer anio;

  /** Titulo */
  @Column(name = "titulo", length = 250, nullable = true)
  @Size(max = 250)
  private String titulo;

  /** Objeto */
  @Column(name = "objeto", length = 2000, nullable = true)
  @Size(max = 2000)
  private String objeto;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000, nullable = true)
  @Size(max = 2000)
  private String observaciones;

  /** Tipo Finalidad */
  @ManyToOne
  @JoinColumn(name = "tipo_finalidad_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIA_FINALIDAD"))
  private TipoFinalidad finalidad;

  /** Regimen Concurrencia */
  @ManyToOne
  @JoinColumn(name = "tipo_regimen_concurrencia_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIA_REGIMENCONCURRENCIA"))
  private TipoRegimenConcurrencia regimenConcurrencia;

  /** Destinatarios */
  @Column(name = "destinatarios", length = 50, nullable = true)
  @Convert(converter = TipoDestinatarioConverter.class)
  private TipoDestinatarioEnum destinatarios;

  /** Colaborativos */
  @Column(name = "colaborativos", nullable = true)
  private Boolean colaborativos;

  /** Estado Actual */
  @Column(name = "estado_actual", length = 50, nullable = false)
  @Convert(converter = TipoEstadoConvocatoriaConverter.class)
  private TipoEstadoConvocatoriaEnum estadoActual;

  /** Duracion */
  @Column(name = "duracion", nullable = true)
  @Min(1)
  @Max(9999)
  @Digits(fraction = 0, integer = 4)
  private Integer duracion;

  /** Ambito Geografico */
  @ManyToOne
  @JoinColumn(name = "tipo_ambito_geografico_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIA_AMBITOGEOGRAFICO"))
  private TipoAmbitoGeografico ambitoGeografico;

  /** Clasificacion CVN */
  @Column(name = "clasificacion_cvn", length = 50, nullable = true)
  @Convert(converter = ClasificacionCVNConverter.class)
  private ClasificacionCVNEnum clasificacionCVN;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}