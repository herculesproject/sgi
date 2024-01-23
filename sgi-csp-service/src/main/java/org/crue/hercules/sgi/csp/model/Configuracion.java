package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;

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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Configuracion
 */

@Entity
@Table(name = "configuracion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Configuracion extends BaseEntity {

  public enum ValidacionClasificacionGastos {
    VALIDACION,
    CLASIFICACION,
    ELEGIBILIDAD;
  }

  public enum Param {
    /**
     * Formato codigo partida presupuestaria
     * <code>formatoPartidaPresupuestaria</code>
     */
    FORMATO_PARTIDA_PRESUPUESTARIA("formatoPartidaPresupuestaria"),
    /**
     * Plantilla formato codigo partida presupuestaria
     * <code>plantillaFormatoPartidaPresupuestaria</code>
     */
    FORMATO_PARTIDA_PRESUPUESTARIA_PLANTILLA("plantillaFormatoPartidaPresupuestaria"),
    /** Validacion gastos <code>validacionClasificacionGastos</code> */
    VALIDACION_CLASIFICACION_GASTOS("validacionClasificacionGastos"),
    /**
     * Formato identificador justificacion
     * <code>formatoIdentificadorJustificacion</code>
     */
    FORMATO_IDENTIFICADOR_JUSTIFICACION("formatoIdentificadorJustificacion"),
    /**
     * Plantilla formato identificador justificacion
     * <code>plantillaFormatoIdentificadorJustificacion</code>
     */
    FORMATO_IDENTIFICADOR_JUSTIFICACION_PLANTILLA("plantillaFormatoIdentificadorJustificacion"),
    /** Dedicacion minima grupo <code>dedicacionMinimaGrupo</code> */
    DEDICACION_MINIMA_GRUPO("dedicacionMinimaGrupo"),
    /** Formato codigo interno proyecto <code>formatoCodigoInternoProyecto</code> */
    FORMATO_CODIGO_INTERNO_PROYECTO("formatoCodigoInternoProyecto"),
    /**
     * Plantilla formato codigo interno proyecto
     * <code>plantillaFormatoCodigoInternoProyecto</code>
     */
    FORMATO_CODIGO_INTERNO_PROYECTO_PLANTILLA("plantillaFormatoCodigoInternoProyecto"),
    /**
     * Habilitar Ejecución económica de Grupos de investigación
     * <code>ejecucionEconomicaGruposEnabled</code>
     */
    EJECUCION_ECONOMICA_GRUPOS_ENABLED("ejecucionEconomicaGruposEnabled");

    private final String key;

    private Param(String key) {
      this.key = key;
    }

    public String getKey() {
      return this.key;
    }

    public static Param fromKey(String key) {
      for (Param param : Param.values()) {
        if (param.key.equals(key)) {
          return param;
        }
      }
      return null;
    }
  }

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuracion_seq")
  @SequenceGenerator(name = "configuracion_seq", sequenceName = "configuracion_seq", allocationSize = 1)
  private Long id;

  /** Formato codigo partida presupuestaria. */
  @Column(name = "formato_partida_presupuestaria", nullable = false, unique = true)
  @NotNull
  private String formatoPartidaPresupuestaria;

  /** Formato codigo partida presupuestaria. */
  @Column(name = "plantilla_formato_partida_presupuestaria", nullable = false, unique = true)
  @NotNull
  private String plantillaFormatoPartidaPresupuestaria;

  /** Validacion/Clasificacion gastos */
  @Column(name = "validacion_clasificacion_gastos", nullable = false, unique = true)
  @Enumerated(EnumType.STRING)
  private ValidacionClasificacionGastos validacionClasificacionGastos;

  /** Formato identificador justificacion. */
  @Column(name = "formato_identificador_justificacion", nullable = true, unique = true)
  private String formatoIdentificadorJustificacion;

  /** Plantilla formato identificador justificacion. */
  @Column(name = "plantilla_formato_identificador_justificacion", nullable = true, unique = true)
  private String plantillaFormatoIdentificadorJustificacion;

  /** Dedicacion minima grupo. */
  @Column(name = "dedicacion_minima_grupo", nullable = true, unique = true)
  private BigDecimal dedicacionMinimaGrupo;

  /** Formato codigo interno proyecto. */
  @Column(name = "formato_codigo_interno_proyecto", nullable = true, unique = true)
  private String formatoCodigoInternoProyecto;

  /** Plantilla formato codigo interno proyecto. */
  @Column(name = "plantilla_formato_codigo_interno_proyecto", nullable = true, unique = true)
  private String plantillaFormatoCodigoInternoProyecto;

  /** Habilitar Ejecución económica de Grupos de investigación */
  @Column(name = "gin_ejecucion_economica", columnDefinition = "boolean default true", nullable = true, unique = true)
  private Boolean ejecucionEconomicaGruposEnabled;

  public Object getParamValue(Param key) {
    switch (key) {
      case DEDICACION_MINIMA_GRUPO:
        return this.getDedicacionMinimaGrupo();
      case FORMATO_CODIGO_INTERNO_PROYECTO:
        return this.getFormatoCodigoInternoProyecto();
      case FORMATO_CODIGO_INTERNO_PROYECTO_PLANTILLA:
        return this.getPlantillaFormatoCodigoInternoProyecto();
      case FORMATO_IDENTIFICADOR_JUSTIFICACION:
        return this.getFormatoIdentificadorJustificacion();
      case FORMATO_IDENTIFICADOR_JUSTIFICACION_PLANTILLA:
        return this.getPlantillaFormatoIdentificadorJustificacion();
      case FORMATO_PARTIDA_PRESUPUESTARIA:
        return this.getFormatoPartidaPresupuestaria();
      case FORMATO_PARTIDA_PRESUPUESTARIA_PLANTILLA:
        return this.getPlantillaFormatoPartidaPresupuestaria();
      case VALIDACION_CLASIFICACION_GASTOS:
        return this.getValidacionClasificacionGastos();
      case EJECUCION_ECONOMICA_GRUPOS_ENABLED:
        return this.getEjecucionEconomicaGruposEnabled();
      default:
        return null;
    }
  }

  public void updateParamValue(Param key, String newValue) {
    switch (key) {
      case DEDICACION_MINIMA_GRUPO:
        this.setDedicacionMinimaGrupo(new BigDecimal(newValue));
        break;
      case FORMATO_CODIGO_INTERNO_PROYECTO:
        this.setFormatoCodigoInternoProyecto(newValue);
        break;
      case FORMATO_CODIGO_INTERNO_PROYECTO_PLANTILLA:
        this.setPlantillaFormatoCodigoInternoProyecto(newValue);
        break;
      case FORMATO_IDENTIFICADOR_JUSTIFICACION:
        this.setFormatoIdentificadorJustificacion(newValue);
        break;
      case FORMATO_IDENTIFICADOR_JUSTIFICACION_PLANTILLA:
        this.setPlantillaFormatoIdentificadorJustificacion(newValue);
        break;
      case FORMATO_PARTIDA_PRESUPUESTARIA:
        this.setFormatoPartidaPresupuestaria(newValue);
        break;
      case FORMATO_PARTIDA_PRESUPUESTARIA_PLANTILLA:
        this.setPlantillaFormatoPartidaPresupuestaria(newValue);
        break;
      case VALIDACION_CLASIFICACION_GASTOS:
        this.setValidacionClasificacionGastos(ValidacionClasificacionGastos.valueOf(newValue));
        break;
      case EJECUCION_ECONOMICA_GRUPOS_ENABLED:
        this.setEjecucionEconomicaGruposEnabled(new Boolean(newValue));
        break;
    }
  }
}