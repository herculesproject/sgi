import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IColumna } from '@core/models/sge/columna';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { environment } from '@env';
import { RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

enum TipoOperacion {
  EPA = 'EPA',
  EPG = 'EPG',
  EPI = 'EPI',
  FJF = 'FJF',
  FJV = 'FJV',
  FJP = 'FJP',
  DOG = 'DOG',
  DOI = 'DOI',
  DOM = 'DOM'
}

@Injectable({
  providedIn: 'root'
})
export class EjecucionEconomicaService extends SgiRestBaseService {
  private static readonly MAPPING = '/ejecucion-economica';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${EjecucionEconomicaService.MAPPING}`,
      http
    );
  }

  private getColumnas(proyectoEconomicoId: string, tipoOperacion: TipoOperacion): Observable<IColumna[]> {
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoEconomicoId)
      .and('tipoOperacion', SgiRestFilterOperator.EQUALS, tipoOperacion);
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

  getColumnasEstadoActual(proyectoEconomicoId: string): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.EPA);
  }

  getColumnasGastos(proyectoEconomicoId: string): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.EPG);
  }

  getColumnasIngresos(proyectoEconomicoId: string): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.EPI);
  }

  private getDatosEconomicos(
    proyectoEconomicoId: string, tipoOperacion: TipoOperacion, anualidades: string[] = []
  ): Observable<IDatoEconomico[]> {
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoEconomicoId)
      .and('tipoOperacion', SgiRestFilterOperator.EQUALS, tipoOperacion);
    if (anualidades.length) {
      filter.and('anualidad', SgiRestFilterOperator.IN, anualidades);
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

  getEstadoActual(proyectoEconomicoId: string, anualidades: string[] = []): Observable<IDatoEconomico[]> {
    return this.getDatosEconomicos(proyectoEconomicoId, TipoOperacion.EPA, anualidades);
  }

  getGastos(proyectoEconomicoId: string, anualidades: string[] = []): Observable<IDatoEconomico[]> {
    return this.getDatosEconomicos(proyectoEconomicoId, TipoOperacion.EPG, anualidades);
  }

  getIngresos(proyectoEconomicoId: string, anualidades: string[] = []): Observable<IDatoEconomico[]> {
    return this.getDatosEconomicos(proyectoEconomicoId, TipoOperacion.EPI, anualidades);
  }

}
