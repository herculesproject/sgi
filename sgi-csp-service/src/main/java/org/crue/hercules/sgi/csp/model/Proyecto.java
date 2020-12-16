package org.crue.hercules.sgi.csp.model;

import java.time.LocalDate;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.converter.ClasificacionCVNConverter;
import org.crue.hercules.sgi.csp.converter.TipoHojaFirmaConverter;
import org.crue.hercules.sgi.csp.converter.TipoHorasAnualesConverter;
import org.crue.hercules.sgi.csp.converter.TipoPlantillaJustificacionConverter;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVNEnum;
import org.crue.hercules.sgi.csp.enums.TipoHojaFirmaEnum;
import org.crue.hercules.sgi.csp.enums.TipoHorasAnualesEnum;
import org.crue.hercules.sgi.csp.enums.TipoPlantillaJustificacionEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proyecto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proyecto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_seq")
  @SequenceGenerator(name = "proyecto_seq", sequenceName = "proyecto_seq", allocationSize = 1)
  private Long id;

  /** Estado proyecto */
  @ManyToOne
  @JoinColumn(name = "estado_proyecto_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTO_ESTADO_PROYECTO"))
  private EstadoProyecto estado;

  /** Titulo */
  @Column(name = "titulo", length = 250, nullable = false)
  @Size(max = 250)
  private String titulo;

  /** Acrónimo */
  @Column(name = "acronimo", length = 50, nullable = true)
  @Size(max = 50)
  private String acronimo;

  /** Codigo externo */
  @Column(name = "codigo_externo", length = 50, nullable = true)
  @Size(max = 50)
  private String codigoExterno;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio", nullable = false)
  private LocalDate fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin", nullable = false)
  private LocalDate fechaFin;

  /** Unidad gestion ref */
  @Column(name = "unidad_gestion_ref", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String unidadGestionRef;

  /** Modelo ejecución. */
  @ManyToOne
  @JoinColumn(name = "modelo_ejecucion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTO_MODELOEJECUCION"))
  @NotNull
  private ModeloEjecucion modeloEjecucion;

  /** Tipo Finalidad */
  @ManyToOne
  @JoinColumn(name = "tipo_finalidad_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTO_FINALIDAD"))
  private TipoFinalidad finalidad;

  /** Convocatoria */
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTO_CONVOCATORIA"))
  private Convocatoria convocatoria;

  /** Codigo externo */
  @Column(name = "convocatoria_externa", length = 50, nullable = true)
  @Size(max = 50)
  private String convocatoriaExterna;

  /** Solicitud */
  @ManyToOne
  @JoinColumn(name = "solicitud_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTO_SOLICITUD"))
  private Solicitud solicitud;

  /** Ambito Geografico */
  @ManyToOne
  @JoinColumn(name = "tipo_ambito_geografico_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTO_AMBITOGEOGRAFICO"))
  private TipoAmbitoGeografico ambitoGeografico;

  // /** Plantilla justificación */
  @Column(name = "plantilla_justificacion", length = 10, nullable = true)
  @Convert(converter = TipoPlantillaJustificacionConverter.class)
  private TipoPlantillaJustificacionEnum plantillaJustificacion;

  /** Confidencial */
  @Column(name = "confidencial", nullable = false)
  private Boolean confidencial;

  /** Clasificacion CVN */
  @Column(name = "clasificacion_cvn", length = 50, nullable = true)
  @Convert(converter = ClasificacionCVNConverter.class)
  private ClasificacionCVNEnum clasificacionCVN;

  /** Colaborativo */
  @Column(name = "colaborativo", nullable = true)
  private Boolean colaborativo;

  /** Coordinador Externo */
  @Column(name = "coordinador_externo", nullable = true)
  private Boolean coordinadorExterno;

  /** Universidad Subcontratada */
  @Column(name = "uni_subcontratada", nullable = true)
  private Boolean uniSubcontratada;

  /** TimeSheet */
  @Column(name = "timesheet", nullable = true)
  private Boolean timesheet;

  /** Hoja firma */
  @Column(name = "plantilla_hoja_firma", length = 50, nullable = true)
  @Convert(converter = TipoHojaFirmaConverter.class)
  private TipoHojaFirmaEnum plantillaHojaFirma;

  /** Paquetes de trabajo */
  @Column(name = "paquetes_trabajo", nullable = true)
  private Boolean paquetesTrabajo;

  /** Coste hora */
  @Column(name = "coste_hora", nullable = true)
  private Boolean costeHora;

  /** Tipo horas anuales */
  @Column(name = "tipo_horas_anuales", length = 50, nullable = true)
  @Convert(converter = TipoHorasAnualesConverter.class)
  private TipoHorasAnualesEnum tipoHorasAnuales;

  /** Contratos */
  @Column(name = "contratos", nullable = true)
  private Boolean contratos;

  /** Facturación */
  @Column(name = "facturacion", nullable = true)
  private Boolean facturacion;

  /** Iva */
  @Column(name = "iva", nullable = true)
  private Boolean iva;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000, nullable = true)
  @Size(max = 2000)
  private String observaciones;

  /** Finalista */
  @Column(name = "finalista", nullable = true)
  private Boolean finalista;

  /** Limitativo */
  @Column(name = "limitativo", nullable = true)
  private Boolean limitativo;

  /** Anualidades */
  @Column(name = "anualidades", nullable = true)
  private Boolean anualidades;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}