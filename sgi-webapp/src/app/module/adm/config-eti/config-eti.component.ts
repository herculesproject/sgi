import { KeyValue } from '@angular/common';
import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { ConfigModule, ConfigType, IConfigOptions } from '@core/models/cnf/config-options';
import { FieldVisibility, FIELD_VISIBILITY_MAP } from '@core/models/eti/configuracion';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { UnidadVinculacionService } from '@core/services/sgo/unidad-vinculacion.service';
import { Observable, of } from 'rxjs';
import { map, share } from 'rxjs/operators';

export enum ConfigEti {
  ETI_BLOCKCHAIN_ENABLE = 'eti-blockchain-enable',
  ETI_PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED = 'peticionEvaluacionUnidadesVinculacionEnabled',
  ETI_PETICION_EVALUACION_INICIALIZAR_UNIDAD_VINCULACION_ENABLED = 'peticionEvaluacionInicializarUnidadVinculacionEnabled',
  ETI_TIPO_UNIDAD_VINCULACION_DEPARTAMENTO_REF = 'tipoUnidadVinculacionDepartamentoRef',
  ETI_PETICION_EVALUACION_INTRODUCCION_VISIBILITY = 'peticionEvaluacionIntroduccionVisibility',
  // Textos de ayuda de la solicitud de evaluación
  ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO_ENABLED = 'peticionEvaluacionAyudaFechaInicioEnabled',
  ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO = 'peticionEvaluacionAyudaFechaInicio',
  ETI_PETICION_EVALUACION_AYUDA_RESUMEN_ENABLED = 'peticionEvaluacionAyudaResumenEnabled',
  ETI_PETICION_EVALUACION_AYUDA_RESUMEN = 'peticionEvaluacionAyudaResumen',
  ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO_ENABLED = 'peticionEvaluacionAyudaDisenioMetodologicoEnabled',
  ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO = 'peticionEvaluacionAyudaDisenioMetodologico',
  ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION_ENABLED = 'peticionEvaluacionAyudaIntroduccionEnabled',
  ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION = 'peticionEvaluacionAyudaIntroduccion',
  ETI_PETICION_EVALUACION_AYUDA_TAREAS_ENABLED = 'peticionEvaluacionAyudaTareasEnabled',
  ETI_PETICION_EVALUACION_AYUDA_TAREAS = 'peticionEvaluacionAyudaTareas',
  ETI_REP_ACTA_DOCX = 'rep-eti-acta-docx',
  ETI_REP_EVALUACION_FAVORABLE_MEMORIA_MODIFICACION_DOCX = 'rep-eti-evaluacion-favorable-memoria-modificacion-docx',
  ETI_REP_EVALUACION_FAVORABLE_MEMORIA_NUEVA_DOCX = 'rep-eti-evaluacion-favorable-memoria-nueva-docx',
  ETI_REP_EVALUACION_FAVORABLE_MEMORIA_RATIFICACION_DOCX = 'rep-eti-evaluacion-favorable-memoria-ratificacion-docx',
  ETI_REP_EVALUACION_DOCX = 'rep-eti-evaluacion-docx',
  ETI_REP_EVALUACION_RETROSPECTIVA_DOCX = 'rep-eti-evaluacion-retrospectiva-docx',
  ETI_REP_FICHA_EVALUADOR_DOCX = 'rep-eti-ficha-evaluador-docx',
  ETI_REP_FORMULARIO_M10 = 'M10',
  ETI_REP_FORMULARIO_M20 = 'M20',
  ETI_REP_FORMULARIO_M30 = 'M30',
  ETI_REP_FORMULARIO_SA = 'SA',
  ETI_REP_FORMULARIO_SF = 'SF',
  ETI_REP_FORMULARIO_R = 'R',
  // Límites exportación excel
  ETI_EXP_MAX_NUM_REGISTROS_EXCEL_ACTA_LISTADO = 'eti-exp-max-num-registros-excel-acta-listado',
  ETI_EXP_MAX_NUM_REGISTROS_EXCEL_CONVOCATORIA_REUNION_LISTADO = 'eti-exp-max-num-registros-excel-convocatoria-reunion-listado',
  ETI_EXP_MAX_NUM_REGISTROS_EXCEL_EVALUACION_LISTADO = 'eti-exp-max-num-registros-excel-evaluacion-listado',
  ETI_EXP_MAX_NUM_REGISTROS_EXCEL_EVALUADOR_LISTADO = 'eti-exp-max-num-registros-excel-evaluador-listado',
  ETI_EXP_MAX_NUM_REGISTROS_EXCEL_MEMORIA_LISTADO = 'eti-exp-max-num-registros-excel-memoria-listado',
  ETI_EXP_MAX_NUM_REGISTROS_EXCEL_PETICION_EVALUACION_LISTADO = 'eti-exp-max-num-registros-excel-peticion-evaluacion-listado',
  ETI_EXP_MAX_NUM_REGISTROS_EXCEL_SEGUIMIENTO_LISTADO = 'eti-exp-max-num-registros-excel-seguimiento-listado',
  // Títulos
  TITLE_AYUDAS_PETICION_EVALUACION = 'title-ayudas-peticion-evaluacion',
  TITLE_EXPORTACIÓN = 'title-exportacion',
}

@Component({
  selector: 'sgi-config-eti',
  templateUrl: './config-eti.component.html',
  styleUrls: ['./config-eti.component.scss']
})
export class ConfigEtiComponent extends AbstractMenuContentComponent {

  private readonly _CONFIG_MAP: Map<ConfigEti, IConfigOptions> = new Map([
    [ConfigEti.ETI_BLOCKCHAIN_ENABLE, { type: ConfigType.SELECT, label: marker(`adm.config.eti.ETI_BLOCKCHAIN_ENABLE`), options: this.getBooleanValues(), required: true, module: ConfigModule.CNF }],
    [ConfigEti.ETI_PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED.description`) }],
    [ConfigEti.ETI_PETICION_EVALUACION_INICIALIZAR_UNIDAD_VINCULACION_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_INICIALIZAR_UNIDAD_VINCULACION_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_INICIALIZAR_UNIDAD_VINCULACION_ENABLED.description`), disabled: true }],
    [ConfigEti.ETI_TIPO_UNIDAD_VINCULACION_DEPARTAMENTO_REF, { type: ConfigType.SELECT, label: marker(`adm.config.eti.ETI_TIPO_UNIDAD_VINCULACION_DEPARTAMENTO_REF`), options: this.getTiposUnidadVinculacion(), required: true, module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_TIPO_UNIDAD_VINCULACION_DEPARTAMENTO_REF.description`), disabled: true }],
    [ConfigEti.ETI_PETICION_EVALUACION_INTRODUCCION_VISIBILITY, { type: ConfigType.SELECT, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_INTRODUCCION_VISIBILITY`), options: this.getFieldVisibilityValues(), required: true, module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_INTRODUCCION_VISIBILITY.description`) }],
    [ConfigEti.ETI_REP_ACTA_DOCX, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_ACTA_DOCX`), module: ConfigModule.CNF }],
    [ConfigEti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_NUEVA_DOCX, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_NUEVA_DOCX`), module: ConfigModule.CNF }],
    [ConfigEti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_RATIFICACION_DOCX, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_RATIFICACION_DOCX`), module: ConfigModule.CNF }],
    [ConfigEti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_MODIFICACION_DOCX, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_MODIFICACION_DOCX`), module: ConfigModule.CNF }],
    [ConfigEti.ETI_REP_EVALUACION_RETROSPECTIVA_DOCX, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_EVALUACION_RETROSPECTIVA_DOCX`), module: ConfigModule.CNF }],
    [ConfigEti.ETI_REP_EVALUACION_DOCX, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_EVALUACION_DOCX`), module: ConfigModule.CNF }],
    [ConfigEti.ETI_REP_FICHA_EVALUADOR_DOCX, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_FICHA_EVALUADOR_DOCX`), module: ConfigModule.CNF }],
    [ConfigEti.ETI_REP_FORMULARIO_M10, { type: ConfigType.SELECT_FILE, label: marker(`adm.config.eti.ETI_REP_FORMULARIO_M10`), description: marker(`adm.config.eti.ETI_REP_FORMULARIO_M10.file`), module: ConfigModule.ETI }],
    [ConfigEti.ETI_REP_FORMULARIO_M20, { type: ConfigType.SELECT_FILE, label: marker(`adm.config.eti.ETI_REP_FORMULARIO_M20`), description: marker(`adm.config.eti.ETI_REP_FORMULARIO_M20.file`), module: ConfigModule.ETI }],
    [ConfigEti.ETI_REP_FORMULARIO_M30, { type: ConfigType.SELECT_FILE, label: marker(`adm.config.eti.ETI_REP_FORMULARIO_M30`), description: marker(`adm.config.eti.ETI_REP_FORMULARIO_M30.file`), module: ConfigModule.ETI }],
    [ConfigEti.ETI_REP_FORMULARIO_SA, { type: ConfigType.SELECT_FILE, label: marker(`adm.config.eti.ETI_REP_FORMULARIO_SA`), description: marker(`adm.config.eti.ETI_REP_FORMULARIO_SA.file`), module: ConfigModule.ETI }],
    [ConfigEti.ETI_REP_FORMULARIO_SF, { type: ConfigType.SELECT_FILE, label: marker(`adm.config.eti.ETI_REP_FORMULARIO_SF`), description: marker(`adm.config.eti.ETI_REP_FORMULARIO_SF.file`), module: ConfigModule.ETI }],
    [ConfigEti.ETI_REP_FORMULARIO_R, { type: ConfigType.SELECT_FILE, label: marker(`adm.config.eti.ETI_REP_FORMULARIO_R`), description: marker(`adm.config.eti.ETI_REP_FORMULARIO_R.file`), module: ConfigModule.ETI }],
    // Textos de ayuda de la solicitud de evaluación
    [ConfigEti.TITLE_AYUDAS_PETICION_EVALUACION, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.ayudas-peticion-evaluacion`), module: ConfigModule.NONE }],
    [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO_ENABLED.description`) }],
    [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO, { type: ConfigType.I18N_HTML, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO`), module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO.description`), disabled: true }],
    [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_RESUMEN_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_RESUMEN_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_RESUMEN_ENABLED.description`) }],
    [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_RESUMEN, { type: ConfigType.I18N_HTML, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_RESUMEN`), module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_RESUMEN.description`), disabled: true }],
    [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO_ENABLED.description`) }],
    [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO, { type: ConfigType.I18N_HTML, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO`), module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO.description`), disabled: true }],
    [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION_ENABLED.description`), disabled: true }],
    [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION, { type: ConfigType.I18N_HTML, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION`), module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION.description`), disabled: true }],
    [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_TAREAS_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_TAREAS_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_TAREAS_ENABLED.description`) }],
    [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_TAREAS, { type: ConfigType.I18N_HTML, label: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_TAREAS`), module: ConfigModule.ETI, description: marker(`adm.config.eti.ETI_PETICION_EVALUACION_AYUDA_TAREAS.description`), disabled: true }],
    // Límite exportación excel
    [ConfigEti.TITLE_EXPORTACIÓN, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.exportacion`), module: ConfigModule.NONE }],
    [ConfigEti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_PETICION_EVALUACION_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.eti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_PETICION_EVALUACION_LISTADO`), required: false, module: ConfigModule.CNF }],
    [ConfigEti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_MEMORIA_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.eti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_MEMORIA_LISTADO`), required: false, module: ConfigModule.CNF }],
    [ConfigEti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_CONVOCATORIA_REUNION_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.eti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_CONVOCATORIA_REUNION_LISTADO`), required: false, module: ConfigModule.CNF }],
    [ConfigEti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_EVALUACION_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.eti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_EVALUACION_LISTADO`), required: false, module: ConfigModule.CNF }],
    [ConfigEti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_SEGUIMIENTO_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.eti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_SEGUIMIENTO_LISTADO`), required: false, module: ConfigModule.CNF }],
    [ConfigEti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_ACTA_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.eti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_ACTA_LISTADO`), required: false, module: ConfigModule.CNF }],
    [ConfigEti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_EVALUADOR_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.eti.ETI_EXP_MAX_NUM_REGISTROS_EXCEL_EVALUADOR_LISTADO`), required: false, module: ConfigModule.CNF }],
  ]);

  /**
   *  Parámetros que quedan deshabilitados mientras el parámetro del que dependen no esté habilitado
   */
  private readonly DEPENDENT_CONFIGS: Map<ConfigEti, ConfigEti[]> = new Map([
    [
      ConfigEti.ETI_PETICION_EVALUACION_INICIALIZAR_UNIDAD_VINCULACION_ENABLED,
      [ConfigEti.ETI_TIPO_UNIDAD_VINCULACION_DEPARTAMENTO_REF]
    ],
    [
      ConfigEti.ETI_PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED,
      [ConfigEti.ETI_PETICION_EVALUACION_INICIALIZAR_UNIDAD_VINCULACION_ENABLED]
    ],
    [
      ConfigEti.ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO_ENABLED,
      [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO]
    ],
    [
      ConfigEti.ETI_PETICION_EVALUACION_AYUDA_RESUMEN_ENABLED,
      [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_RESUMEN]
    ],
    [
      ConfigEti.ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO_ENABLED,
      [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO]
    ],
    [
      ConfigEti.ETI_PETICION_EVALUACION_INTRODUCCION_VISIBILITY,
      [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION_ENABLED]
    ],
    [
      ConfigEti.ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION_ENABLED,
      [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION]
    ],
    [
      ConfigEti.ETI_PETICION_EVALUACION_AYUDA_TAREAS_ENABLED,
      [ConfigEti.ETI_PETICION_EVALUACION_AYUDA_TAREAS]
    ],
  ]);

  get ConfigType() {
    return ConfigType;
  }

  get ConfigModule(): typeof ConfigModule {
    return ConfigModule;
  }

  get CONFIG_MAP() {
    return this._CONFIG_MAP;
  }

  get unidadesGestion$() {
    return this._unidadesGestion$;
  }
  // tslint:disable-next-line: variable-name
  private readonly _unidadesGestion$: Observable<IUnidadGestion[]>;

  // Preserve original property order
  originalOrder = (a: KeyValue<ConfigEti, IConfigOptions>, b: KeyValue<ConfigEti, IConfigOptions>): number => {
    return 0;
  }

  constructor(
    readonly unidadGestionService: UnidadGestionService,
    private readonly unidadVinculacionService: UnidadVinculacionService
  ) {
    super();
    this._unidadesGestion$ = unidadGestionService.findAll().pipe(
      map(result => result.items),
      share()
    );

  }

  handleErrors(error: Error): void {
    if (!!error) {
      this.processError(error);
    } else {
      this.clearProblems();
    }
  }

  /**
   * Habilita o deshabilita los parámetros que dependen del parámetro indicado según su valor persistido. Los
   * parámetros deshabilitados no se muestran: el back los reinicia al deshabilitar el parámetro del que dependen y,
   * al volver a mostrarse, se recargan con su valor real.
   *
   * @param key clave del parámetro de configuración.
   * @param value valor persistido del parámetro.
   */
  onConfigValueChange(key: ConfigEti, value: string): void {
    this.DEPENDENT_CONFIGS.get(key)?.forEach(dependent => this.setConfigDisabled(dependent, !this.isConfigEnabled(key, value)));
  }

  /**
   * Indica si el parámetro está habilitado según su valor: la visibilidad de un campo, si el campo está visible; el
   * resto, si está activo.
   *
   * @param key clave del parámetro de configuración.
   * @param value valor persistido del parámetro.
   * @returns true si el parámetro está habilitado.
   */
  private isConfigEnabled(key: ConfigEti, value: string): boolean {
    if (key === ConfigEti.ETI_PETICION_EVALUACION_INTRODUCCION_VISIBILITY) {
      return value === FieldVisibility.OPTIONAL || value === FieldVisibility.REQUIRED;
    }

    return value === 'true';
  }

  /**
   * Deshabilita o habilita el parámetro indicado. Al deshabilitarlo se deshabilitan también sus dependientes; al
   * habilitarlo, estos se habilitan cuando el parámetro cargue y emita su valor.
   *
   * @param key clave del parámetro de configuración.
   * @param disabled true para deshabilitarlo.
   */
  private setConfigDisabled(key: ConfigEti, disabled: boolean): void {
    this.CONFIG_MAP.get(key).disabled = disabled;
    if (disabled) {
      this.DEPENDENT_CONFIGS.get(key)?.forEach(dependent => this.setConfigDisabled(dependent, true));
    }
  }

  private getTiposUnidadVinculacion(): Observable<KeyValue<string, string>[]> {
    return this.unidadVinculacionService.findAllPadres().pipe(
      map(response => response.items.map(tipoUnidad => ({ key: tipoUnidad.id, value: tipoUnidad.nombre }))),
      share()
    );
  }

  private getKeyValueListFromMap(enumLabelMap: Map<string, string>): Observable<KeyValue<string, string>[]> {
    const keyValueList = Array.from(enumLabelMap.entries(), ([key, value]) => ({ key, value }));
    return of(keyValueList);
  }

  private getFieldVisibilityValues(): Observable<KeyValue<string, string>[]> {
    return this.getKeyValueListFromMap(FIELD_VISIBILITY_MAP);
  }

  private getBooleanValues(): Observable<KeyValue<string, string>[]> {
    return of([{ key: 'true', value: marker('label.si') }, { key: 'false', value: marker('label.no') }]);
  }

  protected setupI18N(): void {
    // Este componente no tiene textos que requieran resolución i18n en tiempo de ejecución.
  }

}
