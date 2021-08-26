import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IColumna } from '@core/models/sge/columna';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { environment } from '@env';
import { RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

enum Estado {
  PENDIENTE = 'PENDIENTE',
  VALIDADO = 'VALIDADO'
}

@Injectable({
  providedIn: 'root'
})
export class GastoService extends SgiRestBaseService {
  private static readonly MAPPING = '/gastos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${GastoService.MAPPING}`,
      http
    );
  }

  getColumnas(reducida = false): Observable<IColumna[]> {
    const filter = new RSQLSgiRestFilter('reducida', SgiRestFilterOperator.EQUALS, `${reducida}`);
    const options: SgiRestFindOptions = {
      filter
    };
    return this.find<IColumna, IColumna>(
      `${this.endpointUrl}/columnas`,
      options
    ).pipe(
      map(response => response.items)
    );
  }

  getGastosBloqueados(
    ids: string[],
    fechaDesde: string,
    fechaHasta: string,
    reducida = false
  ): Observable<IDatoEconomico[]> {
    if (!ids || ids.length === 0) {
      return of([]);
    }
    return this.getGastos(ids, null, fechaDesde, fechaHasta, Estado.PENDIENTE, reducida);
  }

  getGastosPendientes(
    proyectoEconomicoId: string,
    fechaDesde: string,
    fechaHasta: string,
    reducida = false
  ): Observable<IDatoEconomico[]> {
    return this.getGastos(null, proyectoEconomicoId, fechaDesde, fechaHasta, Estado.PENDIENTE, reducida);
  }

  getGastosValidados(
    proyectoEconomicoId: string,
    fechaDesde: string,
    fechaHasta: string,
    reducida = false
  ): Observable<IDatoEconomico[]> {
    return this.getGastos(null, proyectoEconomicoId, fechaDesde, fechaHasta, Estado.VALIDADO, reducida);
  }

  private getGastos(
    ids: string[],
    proyectoEconomicoId: string,
    fechaDesde: string,
    fechaHasta: string,
    estado: Estado,
    reducida = false
  ): Observable<IDatoEconomico[]> {
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoEconomicoId)
      .and('estado', SgiRestFilterOperator.EQUALS, estado)
      .and('reducida', SgiRestFilterOperator.EQUALS, `${reducida}`)
      .and('fecha', SgiRestFilterOperator.GREATHER_OR_EQUAL, fechaDesde)
      .and('fecha', SgiRestFilterOperator.LOWER_OR_EQUAL, fechaHasta);

    if (ids) {
      if (ids.length === 0) {
        return of([]);
      }
      filter.and('id', SgiRestFilterOperator.IN, ids);
    }

    const options: SgiRestFindOptions = {
      filter
    };
    return this.find<IDatoEconomico, IDatoEconomico>(
      `${this.endpointUrl}`,
      options
    ).pipe(
      map(response => response.items)
    );
  }


}
