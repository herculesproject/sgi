package org.crue.hercules.sgi.eti.model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
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
public class Configuracion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Longitud máxima de la referencia al tipo de unidad de vinculación */
  public static final int TIPO_UNIDAD_VINCULACION_DEPARTAMENTO_REF_LENGTH = 50;

  /** Valor de un parámetro de tipo lista sin elementos */
  public static final String EMPTY_LIST_VALUE = "[]";

  /**
   * Parámetros de configuración consultables y modificables de forma individual
   * por su clave.
   *
   * Un parámetro puede depender de otro: solo puede habilitarse si el parámetro
   * del que depende está habilitado, y se deshabilita automáticamente cuando se
   * deshabilita ese.
   *
   * Un parámetro puede ser legible desde el perfil de investigación: lo pueden
   * consultar los usuarios que solo tienen autoridades de investigador.
   */
  public enum Param {
    /**
     * Habilitar el apartado de Unidades de vinculación en las solicitudes de
     * evaluación <code>peticionEvaluacionUnidadesVinculacionEnabled</code>
     */
    PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED("peticionEvaluacionUnidadesVinculacionEnabled"),
    /**
     * Inicializar las unidades de vinculación de la solicitud de evaluación con
     * el departamento del solicitante
     * <code>peticionEvaluacionInicializarUnidadVinculacionEnabled</code>
     */
    PETICION_EVALUACION_INICIALIZAR_UNIDAD_VINCULACION_ENABLED("peticionEvaluacionInicializarUnidadVinculacionEnabled",
        PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED),
    /**
     * Tipo de unidad de vinculación (unidad raíz del SGO) que se corresponde con
     * los departamentos <code>tipoUnidadVinculacionDepartamentoRef</code>
     */
    TIPO_UNIDAD_VINCULACION_DEPARTAMENTO_REF("tipoUnidadVinculacionDepartamentoRef",
        PETICION_EVALUACION_INICIALIZAR_UNIDAD_VINCULACION_ENABLED),
    /**
     * Habilitar ayuda para el campo Fecha inicio de las solicitudes de
     * evaluación <code>peticionEvaluacionAyudaFechaInicioEnabled</code>
     */
    PETICION_EVALUACION_AYUDA_FECHA_INICIO_ENABLED("peticionEvaluacionAyudaFechaInicioEnabled", null, true),
    /**
     * Texto de ayuda a mostrar sobre el campo Fecha inicio de las
     * solicitudes de evaluación <code>peticionEvaluacionAyudaFechaInicio</code>
     */
    PETICION_EVALUACION_AYUDA_FECHA_INICIO("peticionEvaluacionAyudaFechaInicio",
        PETICION_EVALUACION_AYUDA_FECHA_INICIO_ENABLED, true),
    /**
     * Habilitar ayuda para el campo Resumen de las solicitudes de evaluación
     * <code>peticionEvaluacionAyudaResumenEnabled</code>
     */
    PETICION_EVALUACION_AYUDA_RESUMEN_ENABLED("peticionEvaluacionAyudaResumenEnabled", null, true),
    /**
     * Texto de ayuda a mostrar sobre el campo Resumen de las solicitudes de
     * evaluación <code>peticionEvaluacionAyudaResumen</code>
     */
    PETICION_EVALUACION_AYUDA_RESUMEN("peticionEvaluacionAyudaResumen", PETICION_EVALUACION_AYUDA_RESUMEN_ENABLED,
        true),
    /**
     * Habilitar ayuda para el campo Diseño metodológico de las solicitudes de
     * evaluación <code>peticionEvaluacionAyudaDisenioMetodologicoEnabled</code>
     */
    PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO_ENABLED("peticionEvaluacionAyudaDisenioMetodologicoEnabled", null,
        true),
    /**
     * Texto de ayuda a mostrar sobre el campo Diseño metodológico de las
     * solicitudes de evaluación
     * <code>peticionEvaluacionAyudaDisenioMetodologico</code>
     */
    PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO("peticionEvaluacionAyudaDisenioMetodologico",
        PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO_ENABLED, true),
    /**
     * Habilitar ayuda en Asignación de tareas de las solicitudes de evaluación
     * <code>peticionEvaluacionAyudaTareasEnabled</code>
     */
    PETICION_EVALUACION_AYUDA_TAREAS_ENABLED("peticionEvaluacionAyudaTareasEnabled", null, true),
    /**
     * Texto de ayuda a mostrar en Asignación de tareas de las solicitudes de
     * evaluación <code>peticionEvaluacionAyudaTareas</code>
     */
    PETICION_EVALUACION_AYUDA_TAREAS("peticionEvaluacionAyudaTareas", PETICION_EVALUACION_AYUDA_TAREAS_ENABLED, true);

    private final String key;
    private final Param dependsOn;
    private final boolean readableByInvestigador;

    private Param(String key) {
      this(key, null, false);
    }

    private Param(String key, Param dependsOn) {
      this(key, dependsOn, false);
    }

    private Param(String key, Param dependsOn, boolean readableByInvestigador) {
      this.key = key;
      this.dependsOn = dependsOn;
      this.readableByInvestigador = readableByInvestigador;
    }

    /**
     * Clave del parámetro.
     *
     * @return la clave del parámetro.
     */
    public String getKey() {
      return this.key;
    }

    /**
     * Parámetro del que depende este parámetro.
     *
     * @return el {@link Param} del que depende o {@code null} si no depende de
     *         ninguno.
     */
    public Param getDependsOn() {
      return this.dependsOn;
    }

    /**
     * Indica si el parámetro es legible desde el perfil de investigación.
     *
     * @return {@code true} si el parámetro lo pueden consultar los usuarios con
     *         autoridades de investigador.
     */
    public boolean isReadableByInvestigador() {
      return this.readableByInvestigador;
    }

    /**
     * Parámetros que dependen directamente de este parámetro.
     *
     * @return la lista de {@link Param} que dependen de este parámetro.
     */
    public List<Param> getDependents() {
      return Arrays.stream(Param.values())
          .filter(param -> param.dependsOn == this)
          .toList();
    }

    /**
     * Devuelve el {@link Param} con la clave indicada.
     *
     * @param key clave del parámetro.
     * @return el {@link Param} con la clave indicada o {@code null} si no existe.
     */
    public static Param fromKey(String key) {
      for (Param param : Param.values()) {
        if (param.key.equals(key)) {
          return param;
        }
      }

      return null;
    }
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuracion_seq")
  @SequenceGenerator(name = "configuracion_seq", sequenceName = "configuracion_seq", allocationSize = 1)
  private Long id;

  /** diasArchivadaInactivo. */
  @Column(name = "dias_archivada_inactivo", nullable = false, unique = true)
  @NotNull
  private Integer diasArchivadaInactivo;

  /** mesesArchivadaPendienteCorrecciones. */
  @Column(name = "meses_archivada_pendiente_correcciones", nullable = false, unique = true)
  @NotNull
  private Integer mesesArchivadaPendienteCorrecciones;

  /** diasLimiteEvaluador. */
  @Column(name = "dias_limite_evaluador", nullable = false, unique = true)
  @NotNull
  private Integer diasLimiteEvaluador;

  /** diasAvisoRetrospectiva. */
  @Column(name = "dias_aviso_retrospectiva", nullable = false, unique = true)
  @NotNull
  private Integer diasAvisoRetrospectiva;

  /** Duración en años de los proyectos de evaluación */
  @Column(name = "duracion_proyecto_evaluacion", nullable = false, unique = true)
  @NotNull
  private Integer duracionProyectoEvaluacion;

  /** Habilitar el apartado de Unidades de vinculación en las solicitudes */
  @Column(name = "peticion_evaluacion_unidades_vinculacion_enabled", columnDefinition = "boolean default false", nullable = false)
  private Boolean peticionEvaluacionUnidadesVinculacionEnabled;

  /**
   * Inicializar las unidades de vinculación de la solicitud con el departamento
   * del solicitante
   */
  @Column(name = "peticion_evaluacion_inicializar_unidad_vinculacion_enabled", columnDefinition = "boolean default false", nullable = false)
  private Boolean peticionEvaluacionInicializarUnidadVinculacionEnabled;

  /**
   * Referencia al tipo de unidad de vinculación del SGO que actúa como
   * departamento
   */
  @Column(name = "tipo_unidad_vinculacion_departamento_ref", length = TIPO_UNIDAD_VINCULACION_DEPARTAMENTO_REF_LENGTH)
  @Size(max = TIPO_UNIDAD_VINCULACION_DEPARTAMENTO_REF_LENGTH)
  private String tipoUnidadVinculacionDepartamentoRef;

  /** Habilitar ayuda para el campo Fecha inicio prevista de las solicitudes */
  @Column(name = "peticion_evaluacion_ayuda_fecha_inicio_enabled", columnDefinition = "boolean default false", nullable = false)
  private Boolean peticionEvaluacionAyudaFechaInicioEnabled;

  /** Texto de ayuda multi-idioma del campo Fecha inicio prevista (JSON) */
  @Column(name = "peticion_evaluacion_ayuda_fecha_inicio", columnDefinition = "clob")
  private String peticionEvaluacionAyudaFechaInicio;

  /** Habilitar ayuda para el campo Resumen de las solicitudes */
  @Column(name = "peticion_evaluacion_ayuda_resumen_enabled", columnDefinition = "boolean default false", nullable = false)
  private Boolean peticionEvaluacionAyudaResumenEnabled;

  /** Texto de ayuda multi-idioma del campo Resumen (JSON) */
  @Column(name = "peticion_evaluacion_ayuda_resumen", columnDefinition = "clob")
  private String peticionEvaluacionAyudaResumen;

  /** Habilitar ayuda para el campo Diseño metodológico de las solicitudes */
  @Column(name = "peticion_evaluacion_ayuda_disenio_metodologico_enabled", columnDefinition = "boolean default false", nullable = false)
  private Boolean peticionEvaluacionAyudaDisenioMetodologicoEnabled;

  /** Texto de ayuda multi-idioma del campo Diseño metodológico (JSON) */
  @Column(name = "peticion_evaluacion_ayuda_disenio_metodologico", columnDefinition = "clob")
  private String peticionEvaluacionAyudaDisenioMetodologico;

  /** Habilitar ayuda en Asignación de tareas de las solicitudes */
  @Column(name = "peticion_evaluacion_ayuda_tareas_enabled", columnDefinition = "boolean default false", nullable = false)
  private Boolean peticionEvaluacionAyudaTareasEnabled;

  /** Texto de ayuda multi-idioma de Asignación de tareas (JSON) */
  @Column(name = "peticion_evaluacion_ayuda_tareas", columnDefinition = "clob")
  private String peticionEvaluacionAyudaTareas;

  /**
   * Devuelve el valor del parámetro indicado.
   *
   * @param key el {@link Param}.
   * @return el valor del parámetro.
   */
  public Object getParamValue(Param key) {
    switch (key) {
      case PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED:
        return this.getPeticionEvaluacionUnidadesVinculacionEnabled();
      case PETICION_EVALUACION_INICIALIZAR_UNIDAD_VINCULACION_ENABLED:
        return this.getPeticionEvaluacionInicializarUnidadVinculacionEnabled();
      case TIPO_UNIDAD_VINCULACION_DEPARTAMENTO_REF:
        return this.getTipoUnidadVinculacionDepartamentoRef();
      case PETICION_EVALUACION_AYUDA_FECHA_INICIO_ENABLED:
        return this.getPeticionEvaluacionAyudaFechaInicioEnabled();
      case PETICION_EVALUACION_AYUDA_FECHA_INICIO:
        return this.getPeticionEvaluacionAyudaFechaInicio();
      case PETICION_EVALUACION_AYUDA_RESUMEN_ENABLED:
        return this.getPeticionEvaluacionAyudaResumenEnabled();
      case PETICION_EVALUACION_AYUDA_RESUMEN:
        return this.getPeticionEvaluacionAyudaResumen();
      case PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO_ENABLED:
        return this.getPeticionEvaluacionAyudaDisenioMetodologicoEnabled();
      case PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO:
        return this.getPeticionEvaluacionAyudaDisenioMetodologico();
      case PETICION_EVALUACION_AYUDA_TAREAS_ENABLED:
        return this.getPeticionEvaluacionAyudaTareasEnabled();
      case PETICION_EVALUACION_AYUDA_TAREAS:
        return this.getPeticionEvaluacionAyudaTareas();
      default:
        return null;
    }
  }

  /**
   * Indica si el parámetro indicado está habilitado: si es un indicador, que esté
   * activo; en otro caso, que tenga valor.
   *
   * @param key el {@link Param}.
   * @return {@code true} si el parámetro está habilitado.
   */
  public boolean isParamEnabled(Param key) {
    Object value = getParamValue(key);
    if (value instanceof Boolean) {
      return Boolean.TRUE.equals(value);
    }

    return value != null;
  }

  /**
   * Actualiza el valor del parámetro indicado. Si el parámetro queda
   * deshabilitado, se deshabilitan también los parámetros que dependen de él.
   *
   * @param key      el {@link Param}.
   * @param newValue el nuevo valor.
   */
  public void updateParamValue(Param key, String newValue) {
    switch (key) {
      case PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED:
        this.setPeticionEvaluacionUnidadesVinculacionEnabled(Boolean.valueOf(newValue));
        break;
      case PETICION_EVALUACION_INICIALIZAR_UNIDAD_VINCULACION_ENABLED:
        this.setPeticionEvaluacionInicializarUnidadVinculacionEnabled(Boolean.valueOf(newValue));
        break;
      case TIPO_UNIDAD_VINCULACION_DEPARTAMENTO_REF:
        this.setTipoUnidadVinculacionDepartamentoRef(StringUtils.isBlank(newValue) ? null : newValue);
        break;
      case PETICION_EVALUACION_AYUDA_FECHA_INICIO_ENABLED:
        this.setPeticionEvaluacionAyudaFechaInicioEnabled(Boolean.valueOf(newValue));
        break;
      case PETICION_EVALUACION_AYUDA_FECHA_INICIO:
        this.setPeticionEvaluacionAyudaFechaInicio(emptyListIfBlank(newValue));
        break;
      case PETICION_EVALUACION_AYUDA_RESUMEN_ENABLED:
        this.setPeticionEvaluacionAyudaResumenEnabled(Boolean.valueOf(newValue));
        break;
      case PETICION_EVALUACION_AYUDA_RESUMEN:
        this.setPeticionEvaluacionAyudaResumen(emptyListIfBlank(newValue));
        break;
      case PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO_ENABLED:
        this.setPeticionEvaluacionAyudaDisenioMetodologicoEnabled(Boolean.valueOf(newValue));
        break;
      case PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO:
        this.setPeticionEvaluacionAyudaDisenioMetodologico(emptyListIfBlank(newValue));
        break;
      case PETICION_EVALUACION_AYUDA_TAREAS_ENABLED:
        this.setPeticionEvaluacionAyudaTareasEnabled(Boolean.valueOf(newValue));
        break;
      case PETICION_EVALUACION_AYUDA_TAREAS:
        this.setPeticionEvaluacionAyudaTareas(emptyListIfBlank(newValue));
        break;
      default:
        break;
    }

    if (!isParamEnabled(key)) {
      key.getDependents().forEach(dependent -> updateParamValue(dependent, null));
    }
  }

  /**
   * Devuelve el valor de un parámetro de tipo lista, o una lista vacía si no
   * tiene valor.
   *
   * @param value el valor recibido.
   * @return el valor recibido o {@link #EMPTY_LIST_VALUE} si es nulo o está en
   *         blanco.
   */
  private static String emptyListIfBlank(String value) {
    return StringUtils.isBlank(value) ? EMPTY_LIST_VALUE : value;
  }

}