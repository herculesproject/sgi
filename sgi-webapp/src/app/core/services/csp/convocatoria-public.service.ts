import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_ENTIDAD_CONVOCANTE_CONVERTER } from '@core/converters/csp/convocatoria-entidad-convocante.converter';
import { CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER } from '@core/converters/csp/convocatoria-entidad-financiadora.converter';
import { CONVOCATORIA_CONVERTER } from '@core/converters/csp/convocatoria.converter';
import { IConvocatoriaBackend } from '@core/models/csp/backend/convocatoria-backend';
import { IConvocatoriaEntidadConvocanteBackend } from '@core/models/csp/backend/convocatoria-entidad-convocante-backend';
import { IConvocatoriaEntidadFinanciadoraBackend } from '@core/models/csp/backend/convocatoria-entidad-financiadora-backend';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IConvocatoriaFaseResponse } from './convocatoria-fase/convocatoria-fase-response';
import { CONVOCATORIA_FASE_RESPONSE_CONVERTER } from './convocatoria-fase/convocatoria-fase-response.converter';

// tslint:disable-next-line: variable-name
const _ConvocatoriaMixinBase:
  FindByIdCtor<number, IConvocatoria, IConvocatoriaBackend> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    CONVOCATORIA_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaPublicService extends _ConvocatoriaMixinBase {

  private static readonly MAPPING = '/convocatorias';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaPublicService.PUBLIC_PREFIX}${ConvocatoriaPublicService.MAPPING}`,
      http
    );
  }

  /**
   * Comprueba si existe una convocatoria
   *
   * @param id Id de la convocatoria
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Devuelve el listado de convocatorias que puede ver un investigador
   *
   * @param options opciones de búsqueda.
   */
  findAllInvestigador(options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoria>> {
    return this.find<IConvocatoriaBackend, IConvocatoria>(
      `${this.endpointUrl}/investigador`,
      options,
      CONVOCATORIA_CONVERTER
    );
  }

  /**
   * Comprueba si tiene permisos para tramitar la convocatoria
   *
   * @param id Id de la convocatoria
   */
  tramitable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/tramitable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  findAllConvocatoriaEntidadConvocantes(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IConvocatoriaEntidadConvocante>> {
    return this.find<IConvocatoriaEntidadConvocanteBackend, IConvocatoriaEntidadConvocante>(
      `${this.endpointUrl}/${id}/convocatoriaentidadconvocantes`,
      options,
      CONVOCATORIA_ENTIDAD_CONVOCANTE_CONVERTER
    );
  }

  findAllConvocatoriaFases(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaFase>> {
    return this.find<IConvocatoriaFaseResponse, IConvocatoriaFase>(
      `${this.endpointUrl}/${id}/convocatoriafases`,
      options,
      CONVOCATORIA_FASE_RESPONSE_CONVERTER
    );
  }

  findEntidadesFinanciadoras(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaEntidadFinanciadora>> {
    return this.find<IConvocatoriaEntidadFinanciadoraBackend, IConvocatoriaEntidadFinanciadora>(
      `${this.endpointUrl}/${id}/convocatoriaentidadfinanciadoras`,
      options,
      CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER
    );
  }

}
