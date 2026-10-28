import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IConfigValue } from '@core/models/cnf/config-value';
import { II18nConfigValue } from '@core/models/cnf/i18n-config-value';
import { FieldVisibility } from '@core/models/eti/configuracion';
import { IPeticionEvaluacionAyudas } from '@core/models/eti/peticion-evaluacion-ayudas';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@herculesproject/framework/http';
import { forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ConfigEti } from 'src/app/module/adm/config-eti/config-eti.component';
import { IConfigValueResponse } from '../cnf/config-value-response';
import { CONFIG_VALUE_RESPONSE_CONVERTER } from '../cnf/config-value-response.converter';
import { I18N_CONFIG_VALUE_REQUEST_CONVERTER } from '../cnf/i18n-config-value-request.converter';
import { I18N_CONFIG_VALUE_RESPONSE_CONVERTER } from '../cnf/i18n-config-value-response.converter';
import { TimeZoneConfigService } from '../timezone.service';

// tslint:disable-next-line: variable-name
const _ConfigServiceMixinBase:
  FindByIdCtor<string, IConfigValue, IConfigValueResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    CONFIG_VALUE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConfigService extends _ConfigServiceMixinBase implements TimeZoneConfigService {
  private static readonly MAPPING = '/config';

  constructor(
    protected http: HttpClient
  ) {
    super(
      `${environment.serviceServers.eti}${ConfigService.MAPPING}`,
      http
    );
  }

  getTimeZone(): Observable<string> {
    return this.http.get(`${this.endpointUrl}/time-zone`, { responseType: 'text' });
  }

  updateValue(key: string, value: string): Observable<IConfigValue> {
    return this.http.patch<IConfigValueResponse>(
      `${this.endpointUrl}/${key}`,
      value
    ).pipe(
      map((response => CONFIG_VALUE_RESPONSE_CONVERTER.toTarget(response)))
    );
  }

  /**
   * Obtiene el valor multi-idioma de un parámetro de configuración.
   *
   * @param key clave del parámetro de configuración.
   * @returns el valor multi-idioma del parámetro.
   */
  findI18nById(key: string): Observable<II18nConfigValue> {
    return this.http.get<IConfigValueResponse>(
      `${this.endpointUrl}/${key}`
    ).pipe(
      map(response => I18N_CONFIG_VALUE_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Actualiza el valor multi-idioma de un parámetro de configuración.
   *
   * @param key clave del parámetro de configuración.
   * @param value valor multi-idioma del parámetro.
   * @returns el valor multi-idioma persistido.
   */
  updateI18nValue(key: string, value: II18nConfigValue): Observable<II18nConfigValue> {
    return this.http.patch<IConfigValueResponse>(
      `${this.endpointUrl}/${key}`,
      I18N_CONFIG_VALUE_REQUEST_CONVERTER.fromTarget(value).value
    ).pipe(
      map(response => I18N_CONFIG_VALUE_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  isPeticionEvaluacionUnidadesVinculacionEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigEti.ETI_PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED);
  }

  /**
   * Obtiene los textos de ayuda de la solicitud de evaluación. Una ayuda que no esté habilitada o que no tenga texto en
   * ningún idioma se devuelve a null.
   *
   * @returns los textos de ayuda de la solicitud de evaluación.
   */
  getPeticionEvaluacionAyudas(): Observable<IPeticionEvaluacionAyudas> {
    return forkJoin({
      disenioMetodologico: this.getAyuda(
        ConfigEti.ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO_ENABLED,
        ConfigEti.ETI_PETICION_EVALUACION_AYUDA_DISENIO_METODOLOGICO
      ),
      fechaInicio: this.getAyuda(
        ConfigEti.ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO_ENABLED,
        ConfigEti.ETI_PETICION_EVALUACION_AYUDA_FECHA_INICIO
      ),
      introduccion: this.getAyuda(
        ConfigEti.ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION_ENABLED,
        ConfigEti.ETI_PETICION_EVALUACION_AYUDA_INTRODUCCION
      ),
      resumen: this.getAyuda(
        ConfigEti.ETI_PETICION_EVALUACION_AYUDA_RESUMEN_ENABLED,
        ConfigEti.ETI_PETICION_EVALUACION_AYUDA_RESUMEN
      ),
      tareas: this.getAyuda(
        ConfigEti.ETI_PETICION_EVALUACION_AYUDA_TAREAS_ENABLED,
        ConfigEti.ETI_PETICION_EVALUACION_AYUDA_TAREAS
      )
    });
  }

  /**
   * Obtiene la visibilidad del campo Introducción de la solicitud de evaluación. Si el valor no es una visibilidad
   * conocida se devuelve {@link FieldVisibility.HIDDEN}.
   *
   * @returns la visibilidad del campo Introducción.
   */
  getPeticionEvaluacionIntroduccionVisibility(): Observable<FieldVisibility> {
    return this.findById(ConfigEti.ETI_PETICION_EVALUACION_INTRODUCCION_VISIBILITY).pipe(
      map(configValue =>
        Object.values(FieldVisibility).find(visibility => visibility === configValue?.value) ?? FieldVisibility.HIDDEN)
    );
  }

  private getValueAsBooleanByConfigKey(configKey: ConfigEti): Observable<boolean> {
    return this.findById(configKey).pipe(
      map(configValue => configValue?.value === 'true')
    );
  }

  /**
   * Obtiene el texto de una ayuda si está habilitada, sin los idiomas que no tienen texto.
   * Así, si el idioma activo no tiene texto se muestra el del idioma priorizado y, si ninguno
   * lo tiene, no se muestra la ayuda.
   *
   * @param enabledKey clave del parámetro que habilita la ayuda.
   * @param textKey clave del parámetro con el texto de la ayuda.
   * @returns el texto de la ayuda o null si no se muestra.
   */
  private getAyuda(enabledKey: ConfigEti, textKey: ConfigEti): Observable<I18nFieldValue[]> {
    return forkJoin({
      enabled: this.getValueAsBooleanByConfigKey(enabledKey),
      text: this.findI18nById(textKey).pipe(
        map(configValue => configValue?.value ?? [])
      )
    }).pipe(
      map(({ enabled, text }) => {
        const values = text.filter(field => !!field?.value?.trim());
        return enabled && values.length ? values : null;
      })
    );
  }

}
