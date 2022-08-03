import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IColumna } from '@core/models/sge/columna';
import { IGastoJustificado } from '@core/models/sge/gasto-justificado';
import { IGastoJustificadoDetalle } from '@core/models/sge/gasto-justificado-detalle';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IGastoJustificadoDetalleResponse } from './gasto-justificado-detalle-response';
import { GASTO_JUSTIFICADO_DETALLE_RESPONSE_CONVERTER } from './gasto-justificado-detalle-response.converter';
import { IGastoJustificadoResponse } from './gasto-justificado-response';
import { GASTO_JUSTIFICADO_RESPONSE_CONVERTER } from './gasto-justificado-response.converter';

// tslint:disable-next-line: variable-name
const _SeguimientoJustificacionServiceMixinBase:
  FindByIdCtor<string, IGastoJustificadoDetalle, IGastoJustificadoDetalleResponse> &
  FindAllCtor<IGastoJustificado, IGastoJustificadoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      GASTO_JUSTIFICADO_DETALLE_RESPONSE_CONVERTER
    ),
    GASTO_JUSTIFICADO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SeguimientoJustificacionService extends _SeguimientoJustificacionServiceMixinBase {

  private static readonly MAPPING = '/seguimiento-justificacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${SeguimientoJustificacionService.MAPPING}`,
      http
    );
  }

  /**
   * Obtiene la metainformación sobre las columnas dinámicas del GastoJustificado.
   *
   * @returns Lista de las columnas
   */
  getColumnas(): Observable<IColumna[]> {
    return this.find<IColumna, IColumna>(
      `${this.endpointUrl}/columnas`).pipe(
        map(response => response.items)
      );
  }
}
