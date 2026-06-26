import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DATO_ECONOMICO_DETALLE_CONVERTER } from '@core/converters/sge/dato-economico-detalle.converter';
import { DATO_ECONOMICO_CONVERTER } from '@core/converters/sge/dato-economico.converter';
import { IDatoEconomicoBackend } from '@core/models/sge/backend/dato-economico-backend';
import { IDatoEconomicoDetalleBackend } from '@core/models/sge/backend/dato-economico-detalle-backend';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { EjecucionEconomicaService, TipoOperacion } from '@core/services/sge/ejecucion-economica.service';
import { environment } from '@env';
import {
  RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSort
} from '@herculesproject/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

/**
 * Variante del servicio {@link EjecucionEconomicaService} para el perfil de investigador
 * que usa CSP como proxy para validar el acceso al proyecto económico.
 */
@Injectable({
  providedIn: 'root'
})
export class EjecucionEconomicaCspService extends EjecucionEconomicaService {
  private static readonly MAPPING_CSP = '/ejecucion-economica-investigador';

  proyectoSgeRef: string;

  constructor(protected http: HttpClient) {
    super(http);
    // Sobreescribe la URL base heredada (apuntaba a serviceServers.sge) por el proxy de CSP.
    (this as unknown as { endpointUrl: string }).endpointUrl =
      `${environment.serviceServers.csp}${EjecucionEconomicaCspService.MAPPING_CSP}`;
  }

  protected getDatosEconomicos(
    sort: SgiRestSort,
    proyectoEconomicoId: string,
    tipoOperacion: TipoOperacion,
    anualidades: string[] = [],
    reducido = false,
    fechaPagoRange?: { desde: string, hasta: string },
    fechaDevengoRange?: { desde: string, hasta: string },
    fechaContabilizacionRange?: { desde: string, hasta: string }
  ): Observable<IDatoEconomico[]> {
    this.proyectoSgeRef = proyectoEconomicoId;

    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoEconomicoId)
      .and('tipoOperacion', SgiRestFilterOperator.EQUALS, tipoOperacion)
      .and('reducida', SgiRestFilterOperator.EQUALS, `${reducido}`);
    if (anualidades.length) {
      filter.and('anualidad', SgiRestFilterOperator.IN, anualidades);
    }
    if (fechaPagoRange?.desde && fechaPagoRange?.hasta) {
      filter.and('fechaPago', SgiRestFilterOperator.GREATHER_OR_EQUAL, fechaPagoRange.desde)
        .and('fechaPago', SgiRestFilterOperator.LOWER_OR_EQUAL, fechaPagoRange.hasta);
    }
    if (fechaDevengoRange?.desde && fechaDevengoRange?.hasta) {
      filter.and('fechaDevengo', SgiRestFilterOperator.GREATHER_OR_EQUAL, fechaDevengoRange.desde)
        .and('fechaDevengo', SgiRestFilterOperator.LOWER_OR_EQUAL, fechaDevengoRange.hasta);
    }
    if (fechaContabilizacionRange?.desde && fechaContabilizacionRange?.hasta) {
      filter.and('fechaContabilizacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, fechaContabilizacionRange.desde)
        .and('fechaContabilizacion', SgiRestFilterOperator.LOWER_OR_EQUAL, fechaContabilizacionRange.hasta);
    }
    const options: SgiRestFindOptions = {
      filter,
      sort
    };

    return this.find<IDatoEconomicoBackend, IDatoEconomico>(
      `${this.endpointUrl}?proyectoSgeRef=${encodeURIComponent(proyectoEconomicoId)}`,
      options,
      DATO_ECONOMICO_CONVERTER
    ).pipe(
      map(response => response.items)
    );
  }

  protected getDatoEconomicoDetalle(id: string, tipoOperacion: TipoOperacion): Observable<IDatoEconomicoDetalle> {
    return this.http.get<IDatoEconomicoDetalleBackend>(
      `${this.endpointUrl}/${id}`,
      { params: { tipoOperacion, proyectoSgeRef: this.proyectoSgeRef } }
    ).pipe(
      map(response => DATO_ECONOMICO_DETALLE_CONVERTER.toTarget(response))
    );
  }

}
