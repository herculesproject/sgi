import { KeyValue } from '@angular/common';
import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { Language } from '@core/i18n/language';
import { ConfigModule, ConfigType, IConfigOptions } from '@core/models/cnf/config-options';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { Observable, of } from 'rxjs';
import { map, share } from 'rxjs/operators';

export enum ConfigGlobal {
  ENTIDAD_IMPLANTACION = 'entidad-implantacion',
  ID_ENTIDAD_SGEMP = 'id-entidad-sgemp',
  REP_COMMON_HEADER_LOGO = 'rep-common-header-logo',
  WEB_HEADER_LOGO_1 = 'web-header-logo-1',
  WEB_HEADER_LOGO_1_2X = 'web-header-logo-1-2x',
  WEB_HEADER_LOGO_1_3X = 'web-header-logo-1-3x',
  WEB_HEADER_LOGO_2 = 'web-header-logo-2',
  WEB_HEADER_LOGO_2_2X = 'web-header-logo-2-2x',
  WEB_HEADER_LOGO_2_3X = 'web-header-logo-2-3x',
  WEB_HEADER_LOGO_3 = 'web-header-logo-3',
  WEB_HEADER_LOGO_3_2X = 'web-header-logo-3-2x',
  WEB_HEADER_LOGO_3_3X = 'web-header-logo-3-3x',
  WEB_I18N_ES = 'web-i18n-es',
  WEB_I18N_EU = 'web-i18n-eu',
  WEB_I18N_EN = 'web-i18n-en',
  WEB_NUM_LOGOS_HEADER = 'web-numero-logos-header',
  EXP_MAX_NUM_REGISTROS_EXCEL = 'exp-max-num-registros-excel',
  SGP_ALTA = 'sgp-alta',
  SGP_MODIFICACION = 'sgp-modificacion',
  SGEMP_ALTA = 'sgemp-alta',
  SGEMP_MODIFICACION = 'sgemp-modificacion',
  I18N_ENABLED_LANGUAGES = 'i18n-enabled-languages',
  I18N_LANGUAGES_PRIORITY = 'i18n-languages-priority',
  REP_FONT_ES = 'rep-font-es',
  REP_FONT_EU = 'rep-font-eu',
  REP_FONT_EN = 'rep-font-en',
  // TITLES
  TITLE_INTEGRACION_SISTEMAS_CORPORATIVOS = 'title-integracion-sistemas-corporativos'
}

@Component({
  selector: 'sgi-config-global',
  templateUrl: './config-global.component.html',
  styleUrls: ['./config-global.component.scss']
})
export class ConfigGlobalComponent extends AbstractMenuContentComponent {

  private readonly _CONFIG_MAP: Map<ConfigGlobal, IConfigOptions> = new Map([
    [ConfigGlobal.ENTIDAD_IMPLANTACION, { type: ConfigType.TEXT, label: marker(`adm.config.global.ENTIDAD_IMPLANTACION`), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.ID_ENTIDAD_SGEMP, { type: ConfigType.TEXT, label: marker(`adm.config.global.ID_ENTIDAD_SGEMP`), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_NUM_LOGOS_HEADER, { type: ConfigType.SELECT, label: marker(`adm.config.global.WEB_NUM_LOGOS_HEADER`), options: of([{ key: '1', value: '1' }, { key: '2', value: '2' }, { key: '3', value: '3' }]), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.I18N_ENABLED_LANGUAGES, { type: ConfigType.SELECT_MULTIPLE, label: marker(`adm.config.global.WEB_LANGUAGES_HEADER`), options: of(Language.values().map(v => { return { key: v.code, value: v.translateKey } })), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.EXP_MAX_NUM_REGISTROS_EXCEL, { type: ConfigType.TEXT, label: marker(`adm.config.global.EXP_MAX_NUM_REGISTROS_EXCEL`), required: false, info: marker(`adm.config.global.EXP_MAX_NUM_REGISTROS_EXCEL_INFO`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_NUM_LOGOS_HEADER, { type: ConfigType.SELECT, label: marker(`adm.config.global.WEB_NUM_LOGOS_HEADER`), options: of([{ key: '1', value: '1' }, { key: '2', value: '2' }, { key: '3', value: '3' }]), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_1, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_1`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_1_2X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_1_2X`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_1_3X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_1_3X`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_2, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_2`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_2_2X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_2_2X`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_2_3X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_2_3X`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_3, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_3`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_3_2X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_3_2X`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_3_3X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_3_3X`), module: ConfigModule.CNF }],
    [ConfigGlobal.REP_COMMON_HEADER_LOGO, { type: ConfigType.FILE, label: marker(`adm.config.global.REP_COMMON_HEADER_LOGO`), module: ConfigModule.CNF }],
    [ConfigGlobal.REP_FONT_ES, { type: ConfigType.FILE, label: marker(`adm.config.global.REP_FONT_ES`), module: ConfigModule.CNF }],
    [ConfigGlobal.REP_FONT_EU, { type: ConfigType.FILE, label: marker(`adm.config.global.REP_FONT_EU`), module: ConfigModule.CNF }],
    [ConfigGlobal.REP_FONT_EN, { type: ConfigType.FILE, label: marker(`adm.config.global.REP_FONT_EN`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_I18N_ES, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_I18N_ES`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_I18N_EU, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_I18N_EU`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_I18N_EN, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_I18N_EN`), module: ConfigModule.CNF }],
    [ConfigGlobal.TITLE_INTEGRACION_SISTEMAS_CORPORATIVOS, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.integracion-sistemas-corporativos`), module: ConfigModule.NONE }],
    [ConfigGlobal.SGP_ALTA, { type: ConfigType.SELECT, label: marker(`adm.config.global.SGP_ALTA`), options: this.getBooleanValues(), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.SGP_MODIFICACION, { type: ConfigType.SELECT, label: marker(`adm.config.global.SGP_MODIFICACION`), options: this.getBooleanValues(), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.SGEMP_ALTA, { type: ConfigType.SELECT, label: marker(`adm.config.global.SGEMP_ALTA`), options: this.getBooleanValues(), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.SGEMP_MODIFICACION, { type: ConfigType.SELECT, label: marker(`adm.config.global.SGEMP_MODIFICACION`), options: this.getBooleanValues(), required: true, module: ConfigModule.CNF }]
  ]);

  private readonly WEB_HEADER_LOGO_2_KEYS: ConfigGlobal[] = [
    ConfigGlobal.WEB_HEADER_LOGO_2,
    ConfigGlobal.WEB_HEADER_LOGO_2_2X,
    ConfigGlobal.WEB_HEADER_LOGO_2_3X
  ];

  private readonly WEB_HEADER_LOGO_3_KEYS: ConfigGlobal[] = [
    ConfigGlobal.WEB_HEADER_LOGO_3,
    ConfigGlobal.WEB_HEADER_LOGO_3_2X,
    ConfigGlobal.WEB_HEADER_LOGO_3_3X
  ];

  get ConfigType() {
    return ConfigType;
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
  originalOrder = (a: KeyValue<ConfigGlobal, IConfigOptions>, b: KeyValue<ConfigGlobal, IConfigOptions>): number => {
    return 0;
  }

  constructor(
    readonly unidadGestionService: UnidadGestionService
  ) {
    super();
    this._unidadesGestion$ = unidadGestionService.findAll().pipe(
      map(result => result.items),
      share()
    );

  }

  handleErrors(error: Error): void {
    if (error) {
      this.processError(error);
    } else {
      this.clearProblems();
    }
  }

  selectChange(key: string, value: string) {
    if (key === ConfigGlobal.WEB_NUM_LOGOS_HEADER) {
      const numLogos = Number(value);
      this.setConfigOptionsDisabledValue(this.WEB_HEADER_LOGO_2_KEYS, numLogos < 2);
      this.setConfigOptionsDisabledValue(this.WEB_HEADER_LOGO_3_KEYS, numLogos < 3);
    }
  }

  private setConfigOptionsDisabledValue(configKeys: ConfigGlobal[], disabled: boolean): void {
    configKeys.forEach(configKey => this.CONFIG_MAP.get(configKey).disabled = disabled);
  }

  private getBooleanValues(): Observable<KeyValue<string, string>[]> {
    return of([{ key: 'true', value: marker('label.si') }, { key: 'false', value: marker('label.no') }]);
  }

  protected setupI18N(): void {
    // Este componente no tiene textos que requieran resolución i18n en tiempo de ejecución.
  }

}
