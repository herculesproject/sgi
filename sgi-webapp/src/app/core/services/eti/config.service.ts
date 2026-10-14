import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConfigValue } from '@core/models/cnf/config-value';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@herculesproject/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ConfigEti } from 'src/app/module/adm/config-eti/config-eti.component';
import { IConfigValueResponse } from '../cnf/config-value-response';
import { CONFIG_VALUE_RESPONSE_CONVERTER } from '../cnf/config-value-response.converter';
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

  constructor(protected http: HttpClient) {
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

  isPeticionEvaluacionUnidadesVinculacionEnabled(): Observable<boolean> {
    return this.getValueAsBooleanByConfigKey(ConfigEti.ETI_PETICION_EVALUACION_UNIDADES_VINCULACION_ENABLED);
  }

  private getValueAsBooleanByConfigKey(configKey: ConfigEti): Observable<boolean> {
    return this.findById(configKey).pipe(
      map(configValue => configValue?.value === 'true')
    );
  }

}
