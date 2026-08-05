import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IFacturaEmitida } from '@core/models/sge/factura-emitida';
import { IFacturaEmitidaDetalle } from '@core/models/sge/factura-emitida-detalle';
import { CalendarioFacturacionService } from '@core/services/sge/calendario-facturacion.service';
import { IFacturaEmitidaResponse } from '@core/services/sge/factura-emitida/factura-emitida-response';
import { FACTURA_EMITIDA_RESPONSE_CONVERTER } from '@core/services/sge/factura-emitida/factura-emitida-response.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { environment } from '@env';
import {
  RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection
} from '@herculesproject/framework/http';
import { DateTime } from 'luxon';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

/**
 * Variante del servicio {@link CalendarioFacturacionService} para el perfil de investigador
 * que usa CSP como proxy para validar el acceso al proyecto económico.
 */
@Injectable({
  providedIn: 'root'
})
export class CalendarioFacturacionCspService extends CalendarioFacturacionService {
  private static readonly MAPPING_CSP = '/ejecucion-economica-investigador/facturas-emitidas';

  private proyectoSgeRef: string;

  constructor(protected http: HttpClient) {
    super(http);
    // Sobreescribe la URL base heredada (apuntaba a serviceServers.sge) por el proxy de CSP.
    (this as unknown as { endpointUrl: string }).endpointUrl =
      `${environment.serviceServers.csp}${CalendarioFacturacionCspService.MAPPING_CSP}`;
  }

  getFacturasEmitidas(
    proyectoEconomicoId: string,
    fechaFacturaRange?: { desde: DateTime, hasta: DateTime },
  ): Observable<IFacturaEmitida[]> {
    this.proyectoSgeRef = proyectoEconomicoId;

    const sort = new RSQLSgiRestSort('anualidad', SgiRestSortDirection.DESC);
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoEconomicoId);

    if (fechaFacturaRange?.desde) {
      filter.and('fechaFactura', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(fechaFacturaRange.desde, true));
    }

    if (fechaFacturaRange?.hasta) {
      filter.and('fechaFactura', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(fechaFacturaRange.hasta, true));
    }

    const options: SgiRestFindOptions = {
      filter,
      sort
    };

    return this.find<IFacturaEmitidaResponse, IFacturaEmitida>(
      `${this.endpointUrl}?proyectoSgeRef=${encodeURIComponent(proyectoEconomicoId)}`,
      options,
      FACTURA_EMITIDA_RESPONSE_CONVERTER
    ).pipe(
      map(response => response.items)
    );
  }

  getFacturaEmitidaDetalle(id: string): Observable<IFacturaEmitidaDetalle> {
    return this.http.get<IFacturaEmitidaDetalle>(
      `${this.endpointUrl}/${id}`,
      { params: { proyectoSgeRef: this.proyectoSgeRef } }
    );
  }

}
