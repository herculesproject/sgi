import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Persona } from '@core/models/sgp/persona';

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
    this.logger.debug(PersonaFisicaService.name, `getInformacionBasica(${personaRef})`, '-', 'START');
    return this.http.get<Persona>(`${this.endpointUrl}/persona/${personaRef}`).pipe(
      tap(() => this.logger.debug(PersonaFisicaService.name,
        this.logger.debug(PersonaFisicaService.name, `getInformacionBasica(${personaRef})`, '-', 'END')))
    );
  }

  /**
   * Devuelve todas las personas con su información básica
   * @param options sgiRestFindOptions.
   */
  findAllPersonas(options?: SgiRestFindOptions): Observable<SgiRestListResult<Persona>> {
    this.logger.debug(PersonaFisicaService.name,
      `findAllPersonas(filter:${options.filters},page:${options.page}),sort:${options.sort}`, '-', 'START');
    return this.find<Persona, Persona>(`${this.endpointUrl}/persona`, options).pipe(
      tap(() => this.logger.debug(PersonaFisicaService.name,
        `findAllPersonas(filter:${options.filters},page:${options.page}),sort:${options.sort}`, '-', 'END'))
    );
  }

  /**
   * Devuelve las personas de los personaRefs.
   *
   * @param personaRefs lista de personaRefs.
   * @param options opciones de busqueda.
   * @return las personas de los personaRefs
   */
  findByPersonasRefs(personaRefs: string[], options?: SgiRestFindOptions):
    Observable<SgiRestListResult<Persona>> {
    const refsPersonaString = personaRefs.join('|');
    this.logger.debug(PersonaFisicaService.name, `findAllPersonasFisicaByPersonaRefs(${refsPersonaString})`, '-', 'START');
    return this.find<Persona, Persona>(`${this.endpointUrl}/persona/bypersonarefs/${refsPersonaString}`, options).pipe(
      tap(() => this.logger.debug(PersonaFisicaService.name, `findAllPersonasFisicaByPersonaRefs(${refsPersonaString})`, '-', 'END'))
    );
  }
}
