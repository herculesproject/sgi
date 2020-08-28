import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Persona } from '@core/models/sgp/persona';
import { environment } from '@env';
import { SgiRestService, SgiRestFindOptions } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PersonaFisicaService extends SgiRestService<string, Persona>{
  private static readonly MAPPING = '/personas/fisica';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(PersonaFisicaService.name, logger,
      `${environment.serviceServers.sgp}${PersonaFisicaService.MAPPING}`, http);
  }

  /**
   * Devuelve la persona con su información básica
   * @param personaRef referencia de la persona.
   */
  getInformacionBasica(personaRef: string): Observable<Persona> {
    return this.http.get<Persona>(`${this.endpointUrl}/persona/${personaRef}`);
  }

  /**
   * Devuelve todas las personas con su información básica
   * @param options sgiRestFindOptions.
   */
  findAllPersonas(options?: SgiRestFindOptions) {
    this.logger.debug(PersonaFisicaService.name,
      `findAllPersonas(filter:${options.filters},page:${options.page}),sort:${options.sort}`, '-', 'START');
    return this.find<Persona, Persona>(`${this.endpointUrl}/persona/`, options).pipe(
      tap(() => this.logger.debug(PersonaFisicaService.name,
        `findAllPersonas(filter:${options.filters},page:${options.page}),sort:${options.sort}`, '-', 'END'))
    );
  }
}
