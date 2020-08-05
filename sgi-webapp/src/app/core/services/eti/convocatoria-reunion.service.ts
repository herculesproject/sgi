import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ConvocatoriaReunionService extends SgiRestService<number, ConvocatoriaReunion> {
  private static readonly MAPPING = '/convocatoriareuniones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ConvocatoriaReunionService.name,
      logger,
      `${environment.serviceServers.eti}${ConvocatoriaReunionService.MAPPING}`,
      http
    );
  }

  create(element: ConvocatoriaReunion): Observable<ConvocatoriaReunion> {
    return super.create(element).pipe(
      map(convocatoriaReunion => new ConvocatoriaReunion(convocatoriaReunion))
    );
  }

  update(id: number, element: ConvocatoriaReunion): Observable<ConvocatoriaReunion> {
    return super.update(id, element).pipe(
      map(convocatoriaReunion => new ConvocatoriaReunion(convocatoriaReunion))
    );
  }

  findById(id: number): Observable<ConvocatoriaReunion> {
    return super.findById(id).pipe(
      map(convocatoriaReunion => new ConvocatoriaReunion(convocatoriaReunion))
    );
  }

  findAll(options?: SgiRestFindOptions): Observable<SgiRestListResult<ConvocatoriaReunion>> {
    return super.findAll(options).pipe(
      map((listResult: SgiRestListResult<ConvocatoriaReunion>) => {
        listResult.items = listResult.items.map(convocatoriaReunion => new ConvocatoriaReunion(convocatoriaReunion));
        return listResult;
      })
    );
  }
}
