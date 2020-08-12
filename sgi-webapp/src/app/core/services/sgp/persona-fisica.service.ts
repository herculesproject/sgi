import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Persona } from '@core/models/sgp/persona';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PersonaFisicaService extends SgiRestService<string, Persona>{
  private static readonly MAPPING = '/personas/fisica';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(PersonaFisicaService.name, logger,
      `${environment.serviceServers.sgp}${PersonaFisicaService.MAPPING}`, http);
  }

  getInformacionBasica(personaRef: string): Observable<Persona> {
    return this.http.get<Persona>(`${this.endpointUrl}/persona/${personaRef}`);
  }
}
